import json, re, time, html, os, datetime as dt, urllib.parse
import urllib.robotparser as rpmod, importlib.util, concurrent.futures as cf
import xml.etree.ElementTree as ET
H = os.path.dirname(os.path.abspath(__file__))
sp = importlib.util.spec_from_file_location("k1", os.path.join(H, "career_source_audit.py"))
k1 = importlib.util.module_from_spec(sp); sp.loader.exec_module(k1)
UA = k1.USER_AGENT


def get(u):
    time.sleep(1.0)
    return k1.fetch(u)


def robots(base):
    r = get(base + "/robots.txt")
    rp = rpmod.RobotFileParser()
    if r["status"] == 404:
        rp.parse([]); rp.modified(); return rp
    if (r["status"] == 200 and r["text"].strip()
            and not re.search(r"<\s*(html|!doctype)", r["text"][:500], re.I)):
        rp.parse(r["text"].splitlines()); rp.modified(); return rp
    return None


def local(t):
    return t.rsplit("}", 1)[-1]


def m_jsonld(url, base, text, rp):
    def walk(o):
        if isinstance(o, dict):
            if "JobPosting" in str(o.get("@type", "")):
                yield o
            for v in o.values():
                yield from walk(v)
        elif isinstance(o, list):
            for v in o:
                yield from walk(v)
    out = []
    for b in re.findall(r"<script[^>]+ld\+json[^>]*>(.*?)</script>", text, re.S | re.I):
        try:
            d = json.loads(b)
        except ValueError:
            continue
        for j in walk(d):
            t = j.get("title") or j.get("name")
            if t:
                out.append({"title": k1.clean_title(t), "url": j.get("url") or url})
    return out


def m_rss(url, base, text, rp):
    c = []
    for tag in re.findall(r"<link[^>]+>", text, re.I):
        if re.search(r"(rss|atom)\+xml", tag, re.I):
            m = re.search(r'href=["\']([^"\']+)', tag)
            if m:
                c.append(urllib.parse.urljoin(url, html.unescape(m.group(1))))
    c += [base + "/rss", base + "/feed", base + "/rss.xml"]
    for u in list(dict.fromkeys(c))[:4]:
        if not rp.can_fetch(UA, u):
            continue
        r = get(u)
        if r["status"] != 200 or not re.search(r"<(rss|feed)\b", r["text"][:3000]):
            continue
        try:
            root = ET.fromstring(r["text"].encode("utf-8"))
        except ET.ParseError:
            continue
        out = []
        for el in root.iter():
            if local(el.tag) in ("item", "entry"):
                t = l = ""
                for ch in el:
                    n = local(ch.tag)
                    if n == "title":
                        t = ch.text or ""
                    if n == "link":
                        l = (ch.text or "").strip() or ch.get("href", "")
                if t and l:
                    out.append({"title": k1.clean_title(t), "url": l})
        if out:
            return out
    return []


def m_sitemap(url, base, text, rp):
    u = base + "/sitemap.xml"
    if not rp.can_fetch(UA, u):
        return []
    out = []
    for loc in re.findall(r"<loc>\s*([^<\s]+)\s*</loc>", get(u)["text"]):
        if loc.endswith(".xml") or not re.search(r"ilan|duyuru|alim|personel|sinav|kadro", loc, re.I):
            continue
        slug = urllib.parse.unquote(loc.rstrip("/").rsplit("/", 1)[-1])
        out.append({"title": re.sub(r"[-_]+", " ", re.sub(r"\.\w+$", "", slug)), "url": loc})
    return out


def m_html(url, base, text, rp):
    host = urllib.parse.urlsplit(base).netloc.replace("www.", "")
    out = []
    for h, t in re.findall(r'<a[^>]+href=["\']([^"\']+)["\'][^>]*>(.*?)</a>', text, re.S | re.I):
        u = urllib.parse.urljoin(url, html.unescape(h))
        if urllib.parse.urlsplit(u).netloc.endswith(host):
            out.append({"title": k1.clean_title(t), "url": u})
    return out


METHODS = [("jsonld", m_jsonld, False), ("rss", m_rss, True),
           ("sitemap", m_sitemap, True), ("html", m_html, True)]


def probe(url, base, rp):
    res = {"url": url, "yontem": None, "ilan": 0, "ornekler": [],
           "denemeler": {}, "neden": ""}
    if rp is None:
        res["neden"] = "robots-belirsiz"; return res
    if not rp.can_fetch(UA, url):
        res["neden"] = "robots-engel"; return res
    pg = get(url)
    if pg["status"] != 200:
        res["neden"] = "erisilemedi" if pg["status"] is None else "http %s" % pg["status"]
        return res
    hits = k1.scan_markers(pg["text"])
    if any(h.startswith(("login", "captcha", "waf")) for h in hits) and len(pg["text"]) < 20000:
        res["neden"] = "giris/engel isareti"; return res
    for name, fn, filt in METHODS:
        items = k1.dedupe_listings(fn(url, base, pg["text"], rp))
        if filt:
            items = [i for i in items if k1.is_real_listing(i["title"])]
        res["denemeler"][name] = len(items)
        if len(items) >= 3:
            res.update(yontem=name, ilan=len(items), ornekler=items[:5])
            return res
    res["neden"] = "hicbir yontem 3 gercek ilan bulamadi"
    return res


def host_job(base, urls):
    rp = robots(base)
    return [probe(u, base, rp) for u in urls]


def main():
    root = os.path.dirname(H)
    cfg = json.load(open(os.path.join(root, "career/sources.json"), encoding="utf-8"))
    urls = [s["url"] for s in cfg["sources"] if not s.get("login_required")]
    ap = os.path.join(root, "career/adaylar.txt")
    if os.path.exists(ap):
        urls += [l.strip() for l in open(ap, encoding="utf-8") if l.strip().startswith("https://")]
    groups = {}
    for u in dict.fromkeys(urls):
        p = urllib.parse.urlsplit(u)
        groups.setdefault("%s://%s" % (p.scheme, p.netloc), []).append(u)
    allres = []
    with cf.ThreadPoolExecutor(6) as ex:
        for f in [ex.submit(host_job, b, us) for b, us in groups.items()]:
            allres += f.result()
    ok = [r for r in allres if r["yontem"]]
    bad = [r for r in allres if not r["yontem"]]
    now = dt.datetime.now(dt.timezone.utc).isoformat(timespec="seconds")
    out = {"checked_at": now, "calisan": ok, "calismayan": bad}
    json.dump(out, open(os.path.join(root, "career/calisan_kaynaklar.json"), "w", encoding="utf-8"),
              ensure_ascii=False, indent=1)
    kp = os.path.join(root, "career/kaynak_kayit.json")
    kayit = json.load(open(kp, encoding="utf-8")) if os.path.exists(kp) else {}
    for r in allres:
        k = kayit.setdefault(r["url"], {"ilk_gorulme": now, "basarili": 0, "basarisiz": 0,
                                        "yontem": None, "son_basarili": None,
                                        "ilan": 0, "neden": ""})
        if r["yontem"]:
            k.update(yontem=r["yontem"], son_basarili=now, ilan=r["ilan"], neden="")
            k["basarili"] += 1
        else:
            k["basarisiz"] += 1
            k["neden"] = r["neden"]
        k["son_kontrol"] = now
    json.dump(kayit, open(kp, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
    for r in ok:
        print("OK   %-7s %3d  %s" % (r["yontem"], r["ilan"], r["url"][:60]))
    for r in bad:
        print("YOK  %-22s %s" % (r["neden"][:22], r["url"][:60]))
    print("CALISAN: %d / %d" % (len(ok), len(allres)))


if __name__ == "__main__":
    main()
