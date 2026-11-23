#!/usr/bin/env python3
"""Free public source audit; no login, CAPTCHA bypass or search-engine scraping."""
from __future__ import annotations
import argparse, datetime as dt, hashlib, html, json, re, socket, sys, unicodedata
from html.parser import HTMLParser
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.parse import urljoin, urlparse
from urllib.request import Request, urlopen

ROOT=Path(__file__).resolve().parents[1]
REGISTRY=ROOT/"career"/"sources.json"
MAX_BYTES=1_000_000
TIMEOUT=12
UA="AriesCareerSourceAudit/1.0 (+https://github.com/aries919-bot/Jarvis)"
JOB_TERMS=("personel alım","işçi alım","sözleşmeli personel","açık pozisyon","iş ilan","uzman personel","memur alım","personel recruitment")
PROCESS_TERMS=("kura sonucu","asıl yedek","sınav sonucu","mülakat tarihi","başvuru değerlendirme","nihai sonuç")
MONTH_NAMES="Ocak|Şubat|Subat|Mart|Nisan|Mayıs|Mayis|Haziran|Temmuz|Ağustos|Agustos|Eylül|Eylul|Ekim|Kasım|Kasim|Aralık|Aralik"
DATE_ATOM=r"(?:\d{1,2}[./-]\d{1,2}[./-]\d{4}|\d{1,2}\s+(?:"+MONTH_NAMES+r")\s+\d{4})"
DATE_RE=re.compile(DATE_ATOM,re.I)
RANGE_RE=re.compile(r"(?<!\d)("+DATE_ATOM+r")\s*(?:-|–|—|ile|/|\bve\b|tarihinde\s+başlayıp[,]?)\s*("+DATE_ATOM+r")(?!\d)",re.I)
BLOCK=("verify you are human","captcha","robot olmadığınızı","security challenge","automated requests","unusual traffic")
LOGIN=("giriş yap","e-devlet ile giriş","oturum aç")

def norm(s):
    s=html.unescape(s or "").lower().translate(str.maketrans({"ı":"i","ş":"s","ğ":"g","ü":"u","ö":"o","ç":"c"}))
    s=unicodedata.normalize("NFKD",s)
    s="".join(c for c in s if not unicodedata.combining(c))
    return re.sub(r"\s+"," ",re.sub(r"[^a-z0-9]+"," ",s)).strip()

def listing_fingerprint(employer,position,city):
    noise={"t","c","tc","turkiye","cumhuriyeti","anonim","sirketi","as","ltd","sti"}
    parts=[" ".join(t for t in norm(v).split() if t not in noise) for v in (employer,position,city)]
    return hashlib.sha256("|".join(parts).encode()).hexdigest()

class Parser(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True); self.text=[]; self.links=[]; self.anchor=None
    def handle_starttag(self,tag,attrs):
        if tag.lower()=="a":
            href=dict(attrs).get("href") or ""
            if href: self.anchor={"href":href,"text":""}
    def handle_data(self,data):
        v=data.strip()
        if v:
            self.text.append(v)
            if self.anchor is not None: self.anchor["text"]+=" "+v
    def handle_endtag(self,tag):
        if tag.lower()=="a" and self.anchor is not None:
            self.links.append(self.anchor); self.anchor=None

def date_of(s):
    s=s.strip()
    numeric=re.fullmatch(r"(\d{1,2})[./-](\d{1,2})[./-](\d{4})",s)
    if numeric:
        try: return dt.date(int(numeric.group(3)),int(numeric.group(2)),int(numeric.group(1)))
        except (ValueError,TypeError): return None
    named=re.fullmatch(r"(\d{1,2})\s+([A-Za-zÇĞİÖŞÜçğıöşü]+)\s+(\d{4})",s,re.I)
    if not named: return None
    months={"ocak":1,"şubat":2,"subat":2,"mart":3,"nisan":4,"mayıs":5,"mayis":5,"haziran":6,"temmuz":7,"ağustos":8,"agustos":8,"eylül":9,"eylul":9,"ekim":10,"kasım":11,"kasim":11,"aralık":12,"aralik":12}
    month=months.get(named.group(2).lower())
    if month is None: return None
    try: return dt.date(int(named.group(3)),month,int(named.group(1)))
    except (ValueError,TypeError): return None

def application_window(text,today=None):
    today=today or dt.date.today(); m=RANGE_RE.search(text); start=end=None
    if m:
        start=date_of(m.group(1)); end=date_of(m.group(2))
    else:
        m=re.search(r"(?:son\s+başvuru|son\s+basvuru)[^\d]{0,60}("+DATE_ATOM+r")",text,re.I)
        if m: end=date_of(m.group(1))
    if not start and not end: status="unknown"
    elif start and start>today: status="announced_not_open"
    elif end and end<today: status="expired"
    elif end and end>=today: status="open"
    else: status="unknown"
    return {"startDate":start.isoformat() if start else None,"deadline":end.isoformat() if end else None,"status":status}

def fetch(url):
    req=Request(url,headers={"User-Agent":UA,"Accept":"text/html,application/xhtml+xml;q=0.9,*/*;q=0.5","Accept-Language":"tr-TR,tr;q=0.9"},method="GET")
    with urlopen(req,timeout=TIMEOUT) as r:
        raw=r.read(MAX_BYTES+1); ctype=r.headers.get("Content-Type",""); enc=re.search(r"charset=([\w-]+)",ctype,re.I)
        try: body=raw[:MAX_BYTES].decode(enc.group(1) if enc else "utf-8",errors="replace")
        except LookupError: body=raw[:MAX_BYTES].decode("utf-8",errors="replace")
        if len(raw)>MAX_BYTES: body+=" [TRUNCATED]"
        return r.status,r.geturl(),ctype,body

def kind(label,href,candidate_terms=(),programme_terms=()):
    value=norm(label+" "+href)
    if any(norm(x) in value for x in PROCESS_TERMS): return "process_notice"
    if any(norm(x) in value for x in programme_terms): return "programme_candidate"
    if any(norm(x) in value for x in JOB_TERMS) or any(norm(x) in value for x in candidate_terms):
        return "job_candidate"
    return None

def probe(source):
    out={"id":source["id"],"name":source["name"],"url":source["url"],"readerMode":source["readerMode"],"readerOk":False,"candidateCount":0,"programmeCount":0,"processNoticeCount":0,"candidateLinks":[],"applicationWindow":{"startDate":None,"deadline":None,"status":"unknown"},"httpStatus":None,"finalUrl":None,"error":None,"checkedAt":dt.datetime.now(dt.timezone.utc).isoformat()}
    if source["readerMode"] not in ("html_index","html_detail"):
        out.update(status="not_probed_by_design",error="Dynamic/PDF/manual boundary; no automated login or CAPTCHA bypass.")
        return out
    try:
        code,final,ctype,body=fetch(source["probeUrl"]); out["httpStatus"]=code; out["finalUrl"]=final
        if not 200<=code<300: out.update(status="http_error",error=f"HTTP {code}"); return out
        if "pdf" in ctype.lower() or body.lstrip().startswith("%PDF"): out.update(status="pdf_requires_reader",error="Separate bounded PDF reader required."); return out
        p=Parser(); p.feed(body); text=" ".join(p.text); ntext=norm(text)
        if any(norm(x) in ntext for x in BLOCK): out.update(status="blocked_or_captcha",error="Security/CAPTCHA marker; stopped without bypass."); return out
        if source.get("loginSensitive") and any(norm(x) in ntext for x in LOGIN): out.update(status="login_required",error="Login boundary; no automated login."); return out
        markers=all(norm(x) in ntext for x in source.get("expectedMarkers",[]))
        links=[]; seen=set()
        for a in p.links:
            href=urljoin(final,a["href"]); label=re.sub(r"\s+"," ",a["text"]).strip(); k=kind(label,href,source.get("candidateTerms",[]),source.get("programmeTerms",[]))
            u=urlparse(href)
            if k and label and href.startswith("https://") and not u.username and not u.password and href not in seen:
                seen.add(href); links.append({"title":label[:240],"url":href,"kind":k})
        jobs=[x for x in links if x["kind"]=="job_candidate"]
        programmes=[x for x in links if x["kind"]=="programme_candidate"]
        process=[x for x in links if x["kind"]=="process_notice"]
        if source["readerMode"]=="html_detail":
            ok=len(text)>=100 and any(norm(x) in ntext for x in JOB_TERMS) and bool(DATE_RE.search(text)) and markers
            out["readerOk"]=ok; out["candidateCount"]=1 if ok else 0
            out["candidateLinks"]=[{"title":source["name"],"url":final,"kind":"job_detail"}] if ok else []
            out["applicationWindow"]=application_window(text)
        else:
            # A generic 200 page is not a reader: it must expose a direct vacancy
            # or a separately tagged early-career programme link.
            out["readerOk"]=len(text)>=100 and markers and bool(jobs or programmes)
            out["candidateCount"]=len(jobs); out["programmeCount"]=len(programmes); out["processNoticeCount"]=len(process)
            out["candidateLinks"]=(jobs+programmes+process)[:50]
        out["status"]="reader_ok" if out["readerOk"] else "reader_not_confirmed"
        if not out["readerOk"]: out["error"]="Page opened, but no verified direct vacancy link/detail was extracted."
        return out
    except HTTPError as e: out.update(status="http_error",httpStatus=e.code,error=f"HTTP {e.code}")
    except (URLError,socket.timeout,TimeoutError) as e: out.update(status="network_error",error=str(getattr(e,"reason",e))[:240])
    except Exception as e: out.update(status="reader_error",error=f"{type(e).__name__}: {e}"[:240])
    return out

def validate(sources):
    ids=[x["id"] for x in sources]; urls=[x["url"] for x in sources]
    if len(ids)!=len(set(ids)): raise ValueError("Duplicate source IDs")
    if len(urls)!=len(set(urls)): raise ValueError("Duplicate source URLs")
    if not 15<=len(sources)<=20: raise ValueError("First wave must contain 15–20 sources")
    for s in sources:
        if not s["url"].startswith("https://"): raise ValueError("HTTPS required: "+s["id"])
        if any(x in s["url"].lower() for x in ("linkedin.com","indeed.com")): raise ValueError("Do not scrape login-gated job boards")
        if s["readerMode"] in ("html_index","html_detail") and not s.get("probeUrl"): raise ValueError("Missing probeUrl: "+s["id"])

def report(sources,results):
    working=[x for x in results if x["readerOk"] and (x["candidateCount"]+x.get("programmeCount",0))>0]
    detail=[x for x in working if x["readerMode"]=="html_detail"]
    index=[x for x in working if x["readerMode"]=="html_index"]
    open_sources=[x for x in detail if x["applicationWindow"]["status"]=="open"]
    announced=[x for x in detail if x["applicationWindow"]["status"]=="announced_not_open"]
    expired=[x for x in detail if x["applicationWindow"]["status"]=="expired"]
    programme_sources=[x for x in working if x.get("programmeCount",0)>0]
    return {"schemaVersion":1,"generatedAt":dt.datetime.now(dt.timezone.utc).isoformat(),"sourceCount":len(sources),"probedCount":sum(x["status"]!="not_probed_by_design" for x in results),"workingReaderCount":len(working),"workingDetailReaderCount":len(detail),"workingIndexReaderCount":len(index),"activeVacancySourceCount":len(open_sources),"announcedNotOpenSourceCount":len(announced),"expiredDetailSourceCount":len(expired),"programmeReaderCount":len(programme_sources),"vacancyLinkCount":sum(x["candidateCount"] for x in working),"programmeLinkCount":sum(x.get("programmeCount",0) for x in working),"sourcesWithCandidates":len(working),"results":results}

def main():
    ap=argparse.ArgumentParser(); ap.add_argument("--probe",action="store_true"); ap.add_argument("--min-working",type=int,default=0); ap.add_argument("--output",type=Path); args=ap.parse_args()
    sources=json.loads(REGISTRY.read_text(encoding="utf-8"))["sources"]; validate(sources)
    if not args.probe: print(json.dumps({"sourceCount":len(sources)},ensure_ascii=False)); return 0
    results=[probe(s) for s in sources]; data=report(sources,results); text=json.dumps(data,ensure_ascii=False,indent=2)
    if args.output: args.output.parent.mkdir(parents=True,exist_ok=True); args.output.write_text(text+"\n",encoding="utf-8")
    print(text)
    if data["workingReaderCount"]<args.min_working:
        print(f"FAIL: {data['workingReaderCount']} working readers; {args.min_working} required.",file=sys.stderr); return 1
    return 0

if __name__=="__main__": raise SystemExit(main())
