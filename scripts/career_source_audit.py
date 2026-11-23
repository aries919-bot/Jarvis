#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""K1 - Kamu kariyer kaynak matrisi canli okuyucu denetimi.

`career/sources.json` icindeki her kaynak gercek sayfa olarak denetlenir:
  * erisim durumu (HTTP kodu / tasima hatasi)
  * giris (login) ve CAPTCHA / WAF engeli tespiti
  * cekim suresi (ms) ve indirilen bayt
  * ilan sayisi (kaynak bazli kural ile) ve en az 3 gercek ilan ornegi
  * hata mesaj ve otomasyon karari (acik + giris gerektirmeyen sayfa mi)

Sadece standart kutuphane kullanir; dis bagimlilik yoktur.
Anahtar/token/credential kullanilmaz, kaynak matrisinde de bulunamaz.

Kullanim:
  python3 scripts/career_source_audit.py --sources career/sources.json \
      --out-dir career-audit-report --min-ok 3
  python3 scripts/career_source_audit.py --sources career/sources.json --offline
"""

from __future__ import annotations

import argparse
import concurrent.futures
import datetime as _dt
import gzip
import html
import io
import json
import os
import re
import socket
import ssl
import sys
import time
import unicodedata
import urllib.error
import urllib.parse
import urllib.request
import zlib

SCHEMA_VERSION = 1
DEFAULT_TIMEOUT = 20.0
MAX_BYTES = 2_500_000
USER_AGENT = (
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) "
    "Chrome/120.0.0.0 Safari/537.36 JarvisK1Audit/1.0"
)

REQUIRED_SOURCE_KEYS = (
    "id",
    "name",
    "owner",
    "url",
    "kind",
    "content_type",
    "expected_min_listings",
    "rule",
    "notes",
)
VALID_KINDS = ("index", "detail", "search", "feed")
VALID_CONTENT_TYPES = ("ilan", "duyuru", "detay")

# --- işaretçi kalıpları -----------------------------------------------------
LOGIN_MARKERS = (
    r"[üu]ye\s*giri[sş]i?",
    r"giri[sş]\s*yap(ın|iniz)?",
    r"oturum aç",
    r"sign\s*in",
    r"log\s*in",
    r"yetkili\s*değilsiniz",
    r"erişim\s*izniniz",
)
CAPTCHA_MARKERS = (
    r"captcha",
    r"recaptcha",
    r"hcaptcha",
    r"turnstile",
    r"cf-challenge",
    r"just a moment",
    r"doğrulamayı",
)
WAF_MARKERS = (
    r"request rejected",
    r"access denied",
    r"forbidden",
    r"engellendi",
    r"istek reddedildi",
    r"you have been blocked",
)
SOFT_404_MARKERS = (
    r"sayfa bulunamad(ı|i)",
    r"sayfa bulunmadı",
    r"404",
    r"bulunamadı",
    r"page not found",
    r"aradığınız sayfa",
)

_SECRET_LIKE = re.compile(
    r"(gsk_|sk-|AKIA|ghp_|xox[baprs]-|AIza[0-9A-Za-z_\-]{10,}|"
    r"(api[_-]?key|token|secret|password|passwd)\s*[=:]\s*['\"]?[A-Za-z0-9_\-]{12,})",
    re.IGNORECASE,
)


# --- normalize / tekilleştirme ----------------------------------------------
_TR_LOWER = str.maketrans(
    {"İ": "i", "I": "ı", "Ş": "ş", "Ğ": "ğ", "Ü": "ü", "Ö": "ö", "Ç": "ç",
     "Â": "â", "Î": "î", "Û": "û"}
)


_TR_FOLD = str.maketrans("ğışçöüâîû", "gisco" + "uaiu")


def normalize_title(text: str) -> str:
    """Türkçe uyumlu, karşılaştırmaya uygun başlık üretir.

    'T.C.' / 'T.C' / 'T C' aynı anahtara inmelidir; noktalama ve tekrar eden
    boşluklar atılır, Türkçe büyük/küçük harf farkları (İ/I) korunarak düşürülür.
    """
    if not text:
        return ""
    text = html.unescape(str(text))
    text = re.sub(r"<[^>]+>", " ", text)
    text = text.replace("\u00a0", " ").replace("\ufeff", " ")
    text = text.translate(_TR_LOWER)
    text = text.lower()
    text = text.translate(_TR_FOLD)
    # noktalama: "T.C." -> "t c" -> sonra tek harf grupları birleşir
    text = re.sub(r"[^0-9a-zçğıöşüâîû]+", " ", text)
    text = " ".join(text.split())
    # tek harfli kurum kısaltma noktalarını sadeleştir: "t c" -> "tc"
    text = re.sub(r"\b([a-z])\s+(?=[a-z]\b)", r"\1", text)
    return text


def normalize_url(url: str) -> str:
    """Aynı ilanın farklı yazımlarını tekilleştirmek için kanonik URL."""
    url = html.unescape((url or "").strip())
    if not url:
        return ""
    if url.startswith("//"):
        url = "https:" + url
    parts = urllib.parse.urlsplit(url)
    netloc = parts.netloc.lower()
    if netloc.startswith("www."):
        netloc = netloc[4:]
    path = re.sub(r"/{2,}", "/", parts.path or "/")
    if path != "/" and path.endswith("/"):
        path = path.rstrip("/")
    query = urllib.parse.urlencode(
        sorted(urllib.parse.parse_qsl(parts.query, keep_blank_values=True))
    )
    return urllib.parse.urlunsplit((parts.scheme.lower() or "https", netloc, path, query, ""))


def dedupe_listings(items):
    """URL + normalize başlık anahtarı ile tekilleştirir, sırayı korur."""
    seen, out = set(), []
    for it in items:
        url = normalize_url(it.get("url", ""))
        title = normalize_title(it.get("title", ""))
        key = url or title
        if not key or key in seen:
            continue
        seen.add(key)
        out.append({"title": clean_title(it.get("title", "")), "url": url})
    return out


def clean_title(text: str) -> str:
    text = html.unescape(str(text or ""))
    text = re.sub(r"<[^>]+>", " ", text)
    text = re.sub(r"\s+", " ", text).strip()
    return text[:220]



# --- canli okuma -----------------------------------------------------------
def _decode(raw, enc, ctype):
    enc = (enc or "").lower()
    try:
        if "gzip" in enc:
            raw = gzip.decompress(raw)
        elif "deflate" in enc:
            raw = zlib.decompress(raw)
    except Exception:
        pass
    raw = raw[:MAX_BYTES]
    m = re.search(r"charset=([\w\-]+)", ctype or "", re.I)
    for cs in ((m.group(1) if m else None), "utf-8", "cp1254"):
        if not cs:
            continue
        try:
            return raw.decode(cs)
        except (UnicodeDecodeError, LookupError):
            continue
    return raw.decode("utf-8", "replace")


def fetch(url, timeout=DEFAULT_TIMEOUT):
    t0 = time.monotonic()
    out = {"status": None, "text": "", "bytes": 0, "error": "", "ms": 0}
    req = urllib.request.Request(url, headers={
        "User-Agent": USER_AGENT,
        "Accept-Encoding": "gzip, deflate",
        "Accept-Language": "tr,en;q=0.5"})
    try:
        with urllib.request.urlopen(req, timeout=timeout) as r:
            raw = r.read(MAX_BYTES)
            out["status"] = r.status
            out["bytes"] = len(raw)
            out["text"] = _decode(raw, r.headers.get("Content-Encoding"),
                                  r.headers.get("Content-Type"))
    except urllib.error.HTTPError as e:
        out["status"] = e.code
        out["error"] = "HTTP %d" % e.code
    except (urllib.error.URLError, socket.timeout, ssl.SSLError, OSError) as e:
        out["error"] = (type(e).__name__ + ": " + str(e))[:140]
    out["ms"] = int((time.monotonic() - t0) * 1000)
    return out


def check_robots(url):
    import urllib.robotparser
    p = urllib.parse.urlsplit(url)
    rurl = "%s://%s/robots.txt" % (p.scheme, p.netloc)
    r = fetch(rurl)
    if r["status"] is None:
        res = "erisilemedi"
    elif r["status"] == 404:
        res = "yok"
    elif (r["status"] != 200 or not r["text"].strip()
          or re.search(r"<\s*(html|!doctype)", r["text"][:500], re.I)):
        res = "gecersiz"
    else:
        rp = urllib.robotparser.RobotFileParser()
        rp.parse(r["text"].splitlines())
        res = "izinli" if rp.can_fetch(USER_AGENT, url) else "reddedildi"
    return {"url": rurl, "result": res, "http_status": r["status"],
            "fetch_ms": r["ms"]}


def scan_markers(text):
    hits = []
    for label, pats in (("login", LOGIN_MARKERS), ("captcha", CAPTCHA_MARKERS),
                        ("waf", WAF_MARKERS), ("soft_404", SOFT_404_MARKERS)):
        for pat in pats:
            if re.search(pat, text, re.I):
                hits.append("%s:%s" % (label, pat))
    return hits


def find_pattern(rule):
    for v in (rule or {}).values():
        if isinstance(v, str):
            try:
                p = re.compile(v, re.IGNORECASE | re.DOTALL)
            except re.error:
                continue
            if "href" in p.groupindex and "title" in p.groupindex:
                return p
    return None


NOISE_TITLE = re.compile(
    r"uyelik|cerez|gizlilik|log in|login|skip to|teskilat|genel mudur|"
    r"e hizmet|iletisim|hakkimizda|kvkk|site haritasi|sikca sorulan|"
    r"ana sayfa|erisilebilirlik|kullanim|main content|jobs opportunities|"
    r"yargi ilan|yonetmelik|kayit ol|giris yap")
JOB_WORDS = re.compile(
    r"alim|personel|sozlesmeli|kadro|isci|memur|sinav|kura|pozisyon|"
    r"basvuru|ilan|vacanc|job|position|recruit|hiring|staj|uzman|ogretim")


def is_real_listing(title):
    t = normalize_title(title)
    if len(t) < 12 or len(t.split()) < 3:
        return False
    if NOISE_TITLE.search(t):
        return False
    return bool(JOB_WORDS.search(t))


def audit_source(src):
    now = _dt.datetime.now(_dt.timezone.utc).isoformat(timespec="seconds")
    rule = src.get("rule") or {}
    rob = check_robots(src["url"])
    res = {"id": src["id"], "url": src["url"], "checked_at": now,
           "robots": rob, "http_status": None, "bytes": 0, "ms": 0,
           "listing_count": 0, "examples": [], "marker_hits": [],
           "error": "", "decision": "", "open": False}
    if rob["result"] not in ("izinli", "yok"):
        res["decision"] = "robots-engel"
        return res
    if src.get("login_required"):
        res["decision"] = "giris-istiyor"
        return res
    time.sleep(1.0)
    page = fetch(src["url"])
    res.update(http_status=page["status"], bytes=page["bytes"],
               ms=page["ms"], error=page["error"])
    if page["status"] != 200:
        res["decision"] = "erisilemedi" if page["status"] is None else "http-hata"
        return res
    res["marker_hits"] = scan_markers(page["text"])
    pat = find_pattern(rule)
    items = []
    if pat:
        for m in pat.finditer(page["text"]):
            href = urllib.parse.urljoin(rule.get("base_url") or src["url"],
                                        html.unescape(m.group("href")))
            items.append({"title": m.group("title"), "url": href})
    items = [i for i in dedupe_listings(items) if i["title"]]
    res["raw_link_count"] = len(items)
    items = [i for i in items if is_real_listing(i["title"])]
    res["listing_count"] = len(items)
    res["examples"] = items[:3]
    need = max(3, int(src.get("expected_min_listings") or 0))
    if len(items) >= need:
        res["decision"] = "ok"
    elif res["marker_hits"]:
        res["decision"] = "engelli"
    else:
        res["decision"] = "eksik-ilan"
    res["open"] = (res["decision"] == "ok"
                   and not rule.get("js_rendered")
                   and not rule.get("requires_post"))
    return res


def main(argv=None):
    ap = argparse.ArgumentParser(description="K1 kaynak denetimi")
    ap.add_argument("--sources", default="career/sources.json")
    ap.add_argument("--out-dir", default="career-audit-report")
    ap.add_argument("--min-ok", type=int, default=3)
    ap.add_argument("--offline", action="store_true")
    a = ap.parse_args(argv)
    with open(a.sources, encoding="utf-8") as fh:
        doc = json.load(fh)
    srcs = doc["sources"]
    eksik = sorted({s.get("id", "?") for s in srcs
                    for k in REQUIRED_SOURCE_KEYS if k not in s})
    if eksik:
        print("HATA: alan eksik:", eksik)
        return 1
    if a.offline:
        print("offline: %d kaynak, sema tamam" % len(srcs))
        return 0
    rows = []
    for s in srcs:
        r = audit_source(s)
        rows.append(r)
        print("%-24s robots=%-11s http=%-4s ilan=%-3s %s" % (
            r["id"][:24], r["robots"]["result"], r["http_status"],
            r["listing_count"], r["decision"]))
    n_open = sum(1 for r in rows if r["open"])
    toplam = sum(r["listing_count"] for r in rows if r["open"])
    os.makedirs(a.out_dir, exist_ok=True)
    rapor = {"schema_version": SCHEMA_VERSION,
             "generated_at": _dt.datetime.now(_dt.timezone.utc).isoformat(timespec="seconds"),
             "min_ok": a.min_ok, "open_sources": n_open,
             "open_listing_total": toplam, "results": rows}
    with open(os.path.join(a.out_dir, "audit_sonuc.json"), "w", encoding="utf-8") as fh:
        json.dump(rapor, fh, ensure_ascii=False, indent=2)
    print("ACIK KAYNAK: %d (alt sinir %d), acik kaynaklardan ilan: %d" % (
        n_open, a.min_ok, toplam))
    return 0 if n_open >= a.min_ok else 1


if __name__ == "__main__":
    sys.exit(main())
