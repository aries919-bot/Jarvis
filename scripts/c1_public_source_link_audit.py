#!/usr/bin/env python3
"""Discover official attachment links only after a positive robots decision.

No PDF is downloaded. No login, CAPTCHA, browser impersonation, or TLS bypass.
"""
from __future__ import annotations

import json
import ssl
import sys
import urllib.error
import urllib.parse
import urllib.request
from datetime import datetime, timezone
from html.parser import HTMLParser
from urllib.robotparser import RobotFileParser

UA = "AriesKaynakTara/1.0"
TIMEOUT_SECONDS = 15
MAX_BYTES = 1024 * 1024
SOURCES = [
    {
        "id": "ataturk",
        "name": "Atatürk Üniversitesi",
        "allowed_link_hosts": ["atauni.edu.tr"],
        "page_urls": [
            "https://atauni.edu.tr/personel-daire-baskanligi/2026/11/20/ataturk-universitesi-ogretim-uyesi-alim-ilani-20-11-2026/",
            "https://atauni.edu.tr/personel-daire-baskanligi/2026/11/16/ataturk-universitesi-ogretim-uyesi-alim-ilani-16-11-2026/",
        ],
    },
    {
        "id": "balikesir",
        "name": "Balıkesir Üniversitesi",
        "allowed_link_hosts": ["personel.balikesir.edu.tr", "baunwebapi.balikesir.edu.tr"],
        "page_urls": [
            "https://personel.balikesir.edu.tr/duyurular/20-11-2026-tarihli-ogretim-elemani-ilanimiz-202611201352",
        ],
    },
    {
        "id": "zafer",
        "name": "Zafer Kalkınma Ajansı",
        "allowed_link_hosts": ["www.zafer.gov.tr", "zafer.gov.tr"],
        "page_urls": ["https://www.zafer.gov.tr/duyurular/227"],
    },
    {
        "id": "doka",
        "name": "Doğu Karadeniz Kalkınma Ajansı",
        "allowed_link_hosts": ["www.doka.org.tr", "doka.org.tr"],
        "page_urls": ["https://www.doka.org.tr/announcement-detail.php?id=1221"],
    },
    {
        "id": "doka_zafer_mirror",
        "name": "DOKA üzerinde yayımlanan Zafer Kalkınma Ajansı ilan kopyası",
        "allowed_link_hosts": ["www.doka.org.tr", "doka.org.tr"],
        "page_urls": ["https://www.doka.org.tr/announcement-detail.php?id=1222"],
    },
    {
        "id": "tga",
        "name": "Türkiye Turizm Tanıtım ve Geliştirme Ajansı",
        "allowed_link_hosts": ["tga.gov.tr", "kariyer.tga.gov.tr"],
        "page_urls": [
            "https://tga.gov.tr/kariyer",
            "https://tga.gov.tr/kariyer/z7zehjtctwbgfp7tygzvfg7gv3",
            "https://tga.gov.tr/kariyer/sfy7j99p8z4yty9hygzlwgfmvq",
            "https://tga.gov.tr/kariyer/f46dc366kvk4jazmygzkmnsry3",
            "https://tga.gov.tr/kariyer/caaxteswzw5mqjebygzzru3mnw",
            "https://kariyer.tga.gov.tr/jobs",
        ],
    },
    {
        "id": "yokak_upcoming",
        "name": "Yükseköğretim Kalite Kurulu",
        "allowed_link_hosts": ["www.yokak.gov.tr", "yokak.gov.tr"],
        "page_urls": [
            "https://www.yokak.gov.tr/2026/11/13/yuksekogretim-kalite-kurulu-uzman-yardimcisi-alim-ilani-3/",
        ],
    },
]


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def request_once(url: str, limit: int = MAX_BYTES) -> dict:
    req = urllib.request.Request(
        url,
        headers={"User-Agent": UA, "Accept": "text/html,application/xhtml+xml,*/*;q=0.5"},
        method="GET",
    )
    opener = urllib.request.build_opener(
        urllib.request.HTTPSHandler(context=ssl.create_default_context()),
        NoRedirect(),
    )
    try:
        with opener.open(req, timeout=TIMEOUT_SECONDS) as response:
            body = response.read(limit + 1)
            if len(body) > limit:
                return {"status": response.status, "body": b"", "error": "BODY_LIMIT"}
            return {"status": response.status, "body": body}
    except urllib.error.HTTPError as exc:
        return {"status": exc.code, "body": b"", "error": f"HTTP_{exc.code}"}
    except urllib.error.URLError as exc:
        reason = getattr(exc, "reason", exc)
        if isinstance(reason, ssl.SSLError):
            return {"status": None, "body": b"", "error": "TLS_ERROR"}
        if isinstance(reason, TimeoutError):
            return {"status": None, "body": b"", "error": "TIMEOUT"}
        return {"status": None, "body": b"", "error": "NETWORK_ERROR"}
    except TimeoutError:
        return {"status": None, "body": b"", "error": "TIMEOUT"}
    except ssl.SSLError:
        return {"status": None, "body": b"", "error": "TLS_ERROR"}
    except Exception as exc:
        return {"status": None, "body": b"", "error": type(exc).__name__}


def robots_decision(page_url: str, requester=request_once) -> dict:
    parsed = urllib.parse.urlsplit(page_url)
    origin = f"{parsed.scheme}://{parsed.netloc}"
    robots_url = origin + "/robots.txt"
    response = requester(robots_url, 256 * 1024)
    result = {"robots_url": robots_url, "robots_status": response.get("status"), "allowed": None}
    if response.get("error") or response.get("status") != 200 or not response.get("body"):
        result["robots_error"] = response.get("error") or f"ROBOTS_HTTP_{response.get('status')}"
        return result
    parser = RobotFileParser()
    parser.set_url(robots_url)
    parser.parse(response["body"].decode("utf-8", "replace").splitlines())
    result["allowed"] = parser.can_fetch(UA, page_url)
    return result


class PageParser(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.parts = []
        self.meta = {}
        self.links = []
        self.title_parts = []
        self.in_title = False
        self.ignored_depth = 0
        self.current_href = None
        self.current_anchor = []

    def handle_starttag(self, tag, attrs):
        values = {key.lower(): value for key, value in attrs if value is not None}
        tag = tag.lower()
        if tag in {"script", "style", "noscript"}:
            self.ignored_depth += 1
            return
        if tag == "title":
            self.in_title = True
        if tag == "meta":
            name = (values.get("property") or values.get("name") or values.get("itemprop") or "").lower()
            if name and values.get("content"):
                self.meta[name] = values["content"]
        if tag == "a" and values.get("href"):
            self.current_href = values["href"]
            self.current_anchor = []
        if tag in {"iframe", "embed"} and values.get("src"):
            self.links.append({"kind": tag, "text": "", "href": values["src"]})

    def handle_data(self, data):
        if self.ignored_depth:
            return
        clean = " ".join(data.split())
        if not clean:
            return
        self.parts.append(clean)
        if self.in_title:
            self.title_parts.append(clean)
        if self.current_href is not None:
            self.current_anchor.append(clean)

    def handle_endtag(self, tag):
        tag = tag.lower()
        if tag in {"script", "style", "noscript"}:
            self.ignored_depth = max(0, self.ignored_depth - 1)
            return
        if tag == "title":
            self.in_title = False
        if tag == "a" and self.current_href is not None:
            self.links.append({"kind": "anchor", "text": " ".join(self.current_anchor), "href": self.current_href})
            self.current_href = None
            self.current_anchor = []


def parse_page(body: bytes, base_url: str, allowed_hosts: list[str]) -> dict:
    parser = PageParser()
    parser.feed(body.decode("utf-8", "replace"))
    links = []
    for item in parser.links:
        target = urllib.parse.urljoin(base_url, item["href"])
        parsed = urllib.parse.urlsplit(target)
        host = (parsed.hostname or "").lower()
        if parsed.scheme != "https" or host not in allowed_hosts:
            continue
        links.append({
            "kind": item["kind"],
            "text": item["text"][:200],
            "url": target,
            "looks_like_pdf": parsed.path.lower().endswith(".pdf"),
        })
    text = " ".join(parser.parts)
    return {
        "title": " ".join(parser.title_parts)[:300],
        "meta": parser.meta,
        "text_excerpt": text[:1800],
        "official_links": links,
    }


def audit_page(page_url: str, allowed_hosts: list[str], requester=request_once) -> dict:
    robots = robots_decision(page_url, requester)
    result = {"page_url": page_url, **robots, "page_requested": False}
    if robots["allowed"] is not True:
        result["page_error"] = "ROBOTS_NOT_CONFIRMED_ALLOW"
        return result
    response = requester(page_url, MAX_BYTES)
    result["page_http_status"] = response.get("status")
    if response.get("error") or response.get("status") != 200 or not response.get("body"):
        result["page_error"] = response.get("error") or f"PAGE_HTTP_{response.get('status')}"
        return result
    result["page_requested"] = True
    result.update(parse_page(response["body"], page_url, allowed_hosts))
    return result


def run(sources=SOURCES, requester=request_once):
    output = []
    for source in sources:
        pages = [audit_page(url, source["allowed_link_hosts"], requester) for url in source["page_urls"]]
        output.append({"source_id": source["id"], "source_name": source["name"], "pages": pages})
    return {
        "collectedAt": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        "userAgent": UA,
        "scope": "robots-first HTML metadata and same-official-host link discovery; no PDF download",
        "sources": output,
    }


def main():
    report = run()
    print(json.dumps(report, ensure_ascii=False, indent=2))
    print("\nSUMMARY")
    for source in report["sources"]:
        for page in source["pages"]:
            allow = "ALLOW" if page["allowed"] is True else "DENY" if page["allowed"] is False else "UNKNOWN"
            print(f'{source["source_id"]}: robots={page["robots_status"]}/{allow}; page_requested={page["page_requested"]}; page_http={page.get("page_http_status")}; links={len(page.get("official_links", []))}')
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
