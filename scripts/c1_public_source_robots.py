#!/usr/bin/env python3
"""Robots-only probe for candidate official public recruitment sources (revision 2).

This script fetches ONLY each origin's /robots.txt. It never fetches the
announcement page, follows redirects, logs in, or bypasses TLS verification.
"""
from __future__ import annotations

import json
import sys
from datetime import datetime, timezone
from urllib.error import HTTPError, URLError
from urllib.parse import urlsplit
from urllib.request import HTTPRedirectHandler, Request, build_opener

UA = "AriesKaynakTara/1.0"
TIMEOUT_SECONDS = 15
SOURCES = [
    {
        "id": "mardin_artuklu",
        "name": "Mardin Artuklu Üniversitesi",
        "page_urls": [
            "https://www.artuklu.edu.tr/tr/personel-daire-baskanligi/duyurular/20112026-tarihli-ve-33436-sayili-resmi-gazete-ogretim-uyesi-alim-ilani-profdr-docdr-drogruyesi-20112026-04122026",
            "https://www.artuklu.edu.tr/tr/personel-daire-baskanligi/duyurular/20112026-tarihli-ve-33436-sayili-resmi-gazete-ogretim-elemani-alim-ilani-ogrgor-2011202604122026",
        ],
    },
    {
        "id": "ataturk",
        "name": "Atatürk Üniversitesi",
        "page_urls": [
            "https://atauni.edu.tr/personel-daire-baskanligi/2026/11/20/ataturk-universitesi-ogretim-uyesi-alim-ilani-20-11-2026/",
            "https://atauni.edu.tr/personel-daire-baskanligi/2026/11/16/ataturk-universitesi-ogretim-uyesi-alim-ilani-16-11-2026/",
        ],
    },
    {
        "id": "balikesir",
        "name": "Balıkesir Üniversitesi",
        "page_urls": [
            "https://personel.balikesir.edu.tr/duyurular/20-11-2026-tarihli-ogretim-elemani-ilanimiz-202611201352",
            "https://baunwebapi.balikesir.edu.tr/uploads/1795172124974.pdf",
        ],
    },
    {
        "id": "oran",
        "name": "Orta Anadolu Kalkınma Ajansı (ORAN)",
        "page_urls": [
            "https://www.dogaka.gov.tr/duyuru/t-c-orta-anadolu-kalkinma-ajansi-oran-personel-alim-ilani/855",
            "https://www.dogaka.gov.tr/tr/duyurular",
        ],
    },
    {
        "id": "tga",
        "name": "Türkiye Turizm Tanıtım ve Geliştirme Ajansı",
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
        "id": "zafer",
        "name": "Zafer Kalkınma Ajansı",
        "page_urls": [
            "https://www.zafer.gov.tr/duyurular/227",
            "https://doka.org.tr/assets/dosyalar/announcement_1222/files/E2_Ek-2_ZAFERPersonelAl__m__lanMetni_1Sayfa_.pdf",
        ],
    },
    {
        "id": "doka",
        "name": "Doğu Karadeniz Kalkınma Ajansı (DOKA)",
        "page_urls": [
            "https://www.doka.org.tr/announcement-detail.php?id=1221",
            "https://doka.org.tr/assets/dosyalar/announcement_1221/files/DOKA_Personel_ALIMI.pdf",
        ],
    },
    {
        "id": "yokak_upcoming",
        "name": "Yükseköğretim Kalite Kurulu (başvuru 01.12.2026'da açılıyor)",
        "page_urls": [
            "https://www.yokak.gov.tr/2026/11/13/yuksekogretim-kalite-kurulu-uzman-yardimcisi-alim-ilani-3/",
        ],
    },
]


class NoRedirect(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def probe_origin(origin: str) -> dict:
    robots_url = origin.rstrip("/") + "/robots.txt"
    req = Request(robots_url, headers={"User-Agent": UA, "Accept": "text/plain"})
    opener = build_opener(NoRedirect())
    try:
        with opener.open(req, timeout=TIMEOUT_SECONDS) as response:
            status = response.status
            body = response.read(256 * 1024)
            return {"robots_url": robots_url, "http_status": status, "body": body.decode("utf-8", "replace")}
    except HTTPError as exc:
        return {"robots_url": robots_url, "http_status": exc.code, "error": "HTTP_ERROR"}
    except (URLError, TimeoutError, OSError) as exc:
        return {"robots_url": robots_url, "http_status": None, "error": type(exc).__name__}


def main() -> int:
    try:
        from urllib.robotparser import RobotFileParser
    except Exception:
        print("robotparser import failed", file=sys.stderr)
        return 2

    results = []
    for source in SOURCES:
        by_origin = {}
        page_results = []
        for page_url in source["page_urls"]:
            parsed = urlsplit(page_url)
            origin = f"{parsed.scheme}://{parsed.netloc}"
            if origin not in by_origin:
                by_origin[origin] = probe_origin(origin)
            robots = by_origin[origin]
            allowed = None
            if robots.get("http_status") == 200 and robots.get("body"):
                parser = RobotFileParser()
                parser.set_url(robots["robots_url"])
                parser.parse(robots["body"].splitlines())
                allowed = parser.can_fetch(UA, page_url)
            page_results.append({
                "page_url": page_url,
                "robots_url": robots["robots_url"],
                "robots_http_status": robots.get("http_status"),
                "allowed": allowed,
                "error": robots.get("error"),
            })
        results.append({
            "source_id": source["id"],
            "source_name": source["name"],
            "pages": page_results,
            "all_allowed": bool(page_results) and all(p["allowed"] is True for p in page_results),
        })

    output = {
        "collectedAt": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        "userAgent": UA,
        "scope": "robots.txt only; no announcement page requests",
        "sources": results,
    }
    encoded = json.dumps(output, ensure_ascii=False, indent=2)
    print(encoded)
    print("\nSUMMARY")
    for source in results:
        statuses = ",".join(str(p["robots_http_status"]) for p in source["pages"])
        decisions = ",".join("ALLOW" if p["allowed"] is True else "DENY" if p["allowed"] is False else "UNKNOWN" for p in source["pages"])
        print(f'{source["source_id"]}: robots_http={statuses}; page_policy={decisions}')
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
