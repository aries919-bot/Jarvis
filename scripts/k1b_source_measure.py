#!/usr/bin/env python3
"""K1-B public-source access measurement; robots-first and TLS verification enabled."""
from __future__ import annotations

import argparse
import html
import json
import re
import ssl
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from email.message import Message
from pathlib import Path
from typing import Any
from urllib.robotparser import RobotFileParser

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "career" / "k1b_source_candidates.json"
USER_AGENT = "AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)"
TIMEOUT_SECONDS = 15
MAX_BODY_BYTES = 12 * 1024 * 1024
MAX_ROBOTS_BYTES = 512 * 1024
MAX_REDIRECTS = 5

DATE_RE = re.compile(
    r"(?<!\d)(?:"
    r"(?:[0-3]?\d)[./-](?:0?[1-9]|1[0-2])[./-](?:19|20)\d{2}"
    r"|(?:19|20)\d{2}[./-](?:0?[1-9]|1[0-2])[./-](?:0?[1-9]|[12]\d|3[01])"
    r"|(?:0?[1-9]|[12]\d|3[01])\s+"
    r"(?:Ocak|Şubat|Subat|Mart|Nisan|Mayıs|Mayis|Haziran|Temmuz|Ağustos|Agustos|"
    r"Eylül|Eylul|Ekim|Kasım|Kasim|Aralık|Aralik)\s+(?:19|20)\d{2}"
    r")",
    re.IGNORECASE,
)
ANNOUNCEMENT_RE = re.compile(
    r"duyuru|ilan|başvuru|basvuru|personel|işçi|isci|alım|alim|kura|sınav|sinav|"
    r"yerleştirme|yerlestirme|sonuç|sonuc|yayımlandı|yayimlandi|yayın tarihi|"
    r"yayin tarihi|başvurular|basvurular",
    re.IGNORECASE,
)
AUTH_RE = re.compile(
    r"captcha|robot olmadığınızı|robot olmadiginizi|güvenlik doğrulaması|"
    r"guvenlik dogrulamasi|erişim engellendi|access denied|oturum açmanız gerekir|"
    r"oturum acmaniz gerekir|giriş yapmanız gerekir|giris yapmaniz gerekir|"
    r"e-devlet kapısı üzerinden giriş|e-devlet kapisi uzerinden giris",
    re.IGNORECASE,
)
AUTH_PATH_RE = re.compile(r"/(?:login|log-in|signin|sign-in|giris|oturum|captcha)(?:/|$)", re.I)
REDIRECT_CODES = {301, 302, 303, 307, 308}
BLOCK_CODES = {401, 403, 407, 429, 451}
DEAD_CODES = {404, 410}


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def read_manifest(path: Path = MANIFEST) -> list[dict[str, str]]:
    data = json.loads(path.read_text(encoding="utf-8"))
    items = data["sources"]
    urls = [item["url"] for item in items]
    if len(items) != 20:
        raise ValueError(f"Manifestte 20 hedef bekleniyordu, bulunan: {len(items)}")
    if len(urls) != len(set(urls)):
        raise ValueError("Manifestte tekrarlanan URL var; tekrarlar kaynak diye sayılmaz.")
    for item in items:
        parsed = urllib.parse.urlsplit(item["url"])
        if parsed.scheme != "https" or not parsed.hostname:
            raise ValueError(f"HTTPS olmayan/hedefsiz URL reddedildi: {item['url']}")
    return items


def _opener() -> urllib.request.OpenerDirector:
    # Default SSLContext deliberately verifies the certificate chain and hostname.
    context = ssl.create_default_context()
    return urllib.request.build_opener(urllib.request.HTTPSHandler(context=context), NoRedirect())


def _request_once(url: str, limit: int) -> tuple[int | None, Message | None, bytes, str | None]:
    request = urllib.request.Request(
        url,
        headers={
            "User-Agent": USER_AGENT,
            "Accept": "text/html,application/pdf,application/xhtml+xml,*/*;q=0.5",
            "Accept-Language": "tr-TR,tr;q=0.9,en;q=0.5",
            "Connection": "close",
        },
        method="GET",
    )
    try:
        with _opener().open(request, timeout=TIMEOUT_SECONDS) as response:
            body = response.read(limit + 1)
            return response.status, response.headers, body[:limit], ("BODY_LIMIT" if len(body) > limit else None)
    except urllib.error.HTTPError as exc:
        try:
            body = exc.read(min(limit, 64 * 1024))
        except Exception:
            body = b""
        return exc.code, exc.headers, body, None
    except ssl.SSLCertVerificationError as exc:
        return None, None, b"", f"TLS_CERTIFICATE: {exc}"
    except ssl.SSLError as exc:
        return None, None, b"", f"TLS_ERROR: {exc}"
    except TimeoutError as exc:
        return None, None, b"", f"TIMEOUT: {exc}"
    except urllib.error.URLError as exc:
        reason = getattr(exc, "reason", exc)
        if isinstance(reason, ssl.SSLError):
            return None, None, b"", f"TLS_ERROR: {reason}"
        if isinstance(reason, TimeoutError):
            return None, None, b"", f"TIMEOUT: {reason}"
        return None, None, b"", f"NETWORK_ERROR: {reason}"
    except Exception as exc:  # Keep one failing host from hiding the remaining sources.
        return None, None, b"", f"REQUEST_ERROR: {type(exc).__name__}: {exc}"


def _robots_for(url: str) -> dict[str, Any]:
    parsed = urllib.parse.urlsplit(url)
    robots_url = urllib.parse.urlunsplit((parsed.scheme, parsed.netloc, "/robots.txt", "", ""))
    current = robots_url
    body = b""
    status: int | None = None
    error: str | None = None

    for _ in range(MAX_REDIRECTS + 1):
        status, headers, body, error = _request_once(current, MAX_ROBOTS_BYTES)
        if status in REDIRECT_CODES and headers:
            location = headers.get("Location")
            if not location:
                return {"permission": "UNKNOWN", "label": "BİLİNMİYOR", "status": status, "error": "robots redirect konumu yok"}
            next_url = urllib.parse.urljoin(current, location)
            if urllib.parse.urlsplit(next_url).scheme != "https":
                return {"permission": "UNKNOWN", "label": "BİLİNMİYOR", "status": status, "error": "robots HTTPS dışına yönlendi"}
            current = next_url
            continue
        break

    if error:
        return {"permission": "UNKNOWN", "label": "BİLİNMİYOR", "status": None, "error": error}
    if status in (404, 410):
        return {"permission": "ALLOW", "label": f"İZİN VAR (robots HTTP {status})", "status": status, "error": None}
    if status != 200:
        return {"permission": "UNKNOWN", "label": f"BİLİNMİYOR (robots HTTP {status})", "status": status, "error": None}

    message = headers.get_content_charset() if headers else None
    charset = message or "utf-8"
    text = body.decode(charset, errors="replace")
    parser = RobotFileParser()
    parser.set_url(robots_url)
    parser.parse(text.splitlines())
    allowed = parser.can_fetch(USER_AGENT, url)
    return {
        "permission": "ALLOW" if allowed else "DISALLOW",
        "label": f"{'İZİN VAR' if allowed else 'YASAK'} (robots HTTP 200)",
        "status": status,
        "error": None,
    }


def robots_permission_from_text(text: str, url: str) -> bool:
    parser = RobotFileParser()
    parser.set_url(urllib.parse.urljoin(url, "/robots.txt"))
    parser.parse(text.splitlines())
    return parser.can_fetch(USER_AGENT, url)


def _decode_body(body: bytes, headers: Message | None, url: str) -> str:
    is_pdf = (headers.get_content_type() == "application/pdf") if headers else False
    is_pdf = is_pdf or body.startswith(b"%PDF-") or url.lower().endswith(".pdf")
    if is_pdf:
        # pypdf is pinned in the workflow; no OCR or browser rendering is used.
        from pypdf import PdfReader
        from io import BytesIO

        reader = PdfReader(BytesIO(body), strict=False)
        return "\n".join((page.extract_text() or "") for page in reader.pages[:30])
    charset = headers.get_content_charset() if headers else None
    return html.unescape(body.decode(charset or "utf-8", errors="replace"))


def has_dated_announcement(text: str) -> bool:
    plain = re.sub(r"<(script|style)\b[^>]*>.*?</\1>", " ", text, flags=re.I | re.S)
    plain = re.sub(r"<[^>]+>", " ", plain)
    plain = re.sub(r"\s+", " ", plain)
    for match in DATE_RE.finditer(plain):
        window = plain[max(0, match.start() - 180): min(len(plain), match.end() + 180)]
        if ANNOUNCEMENT_RE.search(window):
            return True
    return False


def classify_result(permission: str, status: int | None, body_bytes: int, error: str | None = None,
                    auth_detected: bool = False, body_limited: bool = False) -> str:
    if permission != "ALLOW":
        return "ENGELLİ"
    if auth_detected or body_limited:
        return "ENGELLİ"
    if error:
        if error.startswith("NETWORK_ERROR") and any(word in error.lower() for word in ("name or service not known", "nodename nor servname", "temporary failure in name resolution", "no address associated")):
            return "ÖLÜ"
        return "ENGELLİ"
    if status in BLOCK_CODES or (status is not None and status >= 500):
        return "ENGELLİ"
    if status in DEAD_CODES or status is None or (status is not None and 400 <= status < 500):
        return "ÖLÜ"
    if status is not None and 200 <= status < 300 and body_bytes > 0:
        return "ÇALIŞIYOR"
    return "ENGELLİ"


def measure_one(source: dict[str, str], robots_cache: dict[str, dict[str, Any]] | None = None) -> dict[str, Any]:
    cache = robots_cache if robots_cache is not None else {}
    current = source["url"]
    body = b""
    headers: Message | None = None
    status: int | None = None
    error: str | None = None
    permission = "UNKNOWN"
    robots_label = "BİLİNMİYOR"
    auth_detected = False
    body_limited = False
    final_url = current

    for redirect_count in range(MAX_REDIRECTS + 1):
        parsed = urllib.parse.urlsplit(current)
        if parsed.scheme != "https" or not parsed.hostname:
            error = "HTTPS dışı yönlendirme reddedildi"
            break

        origin = f"{parsed.scheme}://{parsed.netloc}"
        if origin not in cache:
            cache[origin] = _robots_for(current)
        robots = cache[origin]
        permission = robots["permission"]
        robots_label = robots["label"]
        if permission != "ALLOW":
            error = robots.get("error") or "robots izni yok/belirsiz; istek yapılmadı"
            break

        status, headers, body, error = _request_once(current, MAX_BODY_BYTES)
        final_url = current
        if error == "BODY_LIMIT":
            body_limited = True
            error = "Yanıt 12 MiB sınırını aştı"
            break
        if status in REDIRECT_CODES and headers:
            location = headers.get("Location")
            if not location:
                error = "Yönlendirme konumu yok"
                break
            if redirect_count >= MAX_REDIRECTS:
                error = "Yönlendirme sınırı aşıldı"
                break
            next_url = urllib.parse.urljoin(current, location)
            next_parsed = urllib.parse.urlsplit(next_url)
            if next_parsed.scheme != "https" or not next_parsed.hostname:
                error = "HTTPS dışına yönlendirme reddedildi"
                break
            if AUTH_PATH_RE.search(next_parsed.path):
                error = "Giriş/CAPTCHA yoluna yönlendirme; takip edilmedi"
                auth_detected = True
                break
            current = next_url
            continue
        break

    if status is not None and 200 <= status < 300 and body:
        try:
            decoded = _decode_body(body, headers, final_url)
            if AUTH_RE.search(decoded):
                auth_detected = True
            dated = has_dated_announcement(decoded)
        except Exception as exc:
            dated = None
            if error is None:
                error = f"İçerik ayrıştırma hatası: {type(exc).__name__}: {exc}"
    else:
        dated = None

    state = classify_result(permission, status, len(body), error, auth_detected, body_limited)
    return {
        "id": source["id"],
        "category": source["category"],
        "name": source["name"],
        "url": source["url"],
        "finalUrl": final_url,
        "robots": robots_label,
        "robotsPermission": permission,
        "robotsHttp": (cache.get(f"{urllib.parse.urlsplit(final_url).scheme}://{urllib.parse.urlsplit(final_url).netloc}") or {}).get("status"),
        "http": status,
        "bytes": len(body),
        "datedAnnouncement": dated,
        "status": state,
        "error": error,
    }


def fmt_http(row: dict[str, Any]) -> str:
    if row["http"] is not None:
        return str(row["http"])
    error = row.get("error") or "HTTP yok"
    if error.startswith("TLS_"):
        return "TLS HATASI"
    if error.startswith("TIMEOUT"):
        return "TIMEOUT"
    if error.startswith("NETWORK_ERROR"):
        return "AĞ HATASI"
    if "robots" in error.lower():
        return "— (robots nedeniyle istek yok)"
    return "—"


def fmt_dated(value: bool | None) -> str:
    if value is True:
        return "EVET"
    if value is False:
        return "HAYIR"
    return "BİLİNMİYOR"


def make_markdown(rows: list[dict[str, Any]], collected_at: str) -> str:
    counts = {label: sum(1 for row in rows if row["status"] == label) for label in ("ÇALIŞIYOR", "ENGELLİ", "ÖLÜ")}
    lines = [
        "## K1-B — canlı kaynak erişim ölçümü",
        "",
        f"- Ölçüm zamanı (UTC): **{collected_at}**",
        f"- User-Agent: `{USER_AGENT}`",
        "- TLS: varsayılan Python SSL doğrulaması açık; sertifika doğrulama kapatılmadı.",
        "- Robots: her adresin robots.txt dosyası önce okundu; yasak/belirsiz olanlarda sayfa isteği yapılmadı.",
        "- İstek: yalnız GET; giriş, CAPTCHA veya form gönderme denenmedi.",
        f"- Hedef: **{len(rows)} benzersiz URL** (H1 listesi + 2 açık ek adres).",
        f"- Sonuç: **ÇALIŞIYOR {counts['ÇALIŞIYOR']} / ENGELLİ {counts['ENGELLİ']} / ÖLÜ {counts['ÖLÜ']}**",
        "",
        "| Adres | Robots | HTTP | Bayt | Tarihli duyuru var mı? | Durum |",
        "|---|---|---:|---:|---|---|",
    ]
    for row in rows:
        lines.append(
            f"| {row['url']} | {row['robots']} | {fmt_http(row)} | {row['bytes']} | "
            f"{fmt_dated(row['datedAnnouncement'])} | **{row['status']}** |"
        )
    if counts["ÇALIŞIYOR"] == 0:
        lines.extend(["", "> **RED:** ÇALIŞIYOR sınıfında kaynak yok."])
    lines.extend([
        "",
        "### Ölçüm sınırı",
        "Bu ölçüm sayfanın robots/TLS/HTTP erişimini ve gövde içindeki tarihli duyuru işaretini kontrol eder; ilan satırlarını saymaz, başvuru şartlarını doğrulamaz ve canlı ilan okuyucusu kanıtı değildir.",
        "HTTP 404/410 ve boş/yanıtsız sayfalar ÖLÜ; robots engeli, 401/403/429, TLS/timeout, 5xx veya giriş/CAPTCHA gerektiren erişim ENGELLİ sayılmıştır.",
    ])
    return "\n".join(lines) + "\n"


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--summary", default=None, help="GitHub Step Summary dosyası")
    parser.add_argument("--json", default="reports/k1b-source-measure.json", help="JSON rapor yolu")
    args = parser.parse_args()

    sources = read_manifest()
    # The URL list is re-counted before the network loop; duplicate URLs are a hard error.
    if len({item["url"] for item in sources}) != 20:
        raise SystemExit("Beklenen 20 farklı URL doğrulanamadı.")

    collected_at = time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())
    cache: dict[str, dict[str, Any]] = {}
    rows = []
    for index, source in enumerate(sources, start=1):
        row = measure_one(source, cache)
        rows.append(row)
        print(
            f"[{index:02d}/{len(sources)}] {row['url']} | robots={row['robots']} | "
            f"http={fmt_http(row)} | bytes={row['bytes']} | dated={fmt_dated(row['datedAnnouncement'])} | {row['status']}"
        )

    # Independent recount from generated rows.
    if len(rows) != 20 or len({row["url"] for row in rows}) != 20:
        raise SystemExit(f"Ölçüm sayısı beklenenden farklı: {len(rows)} satır.")
    report = {"collectedAt": collected_at, "userAgent": USER_AGENT, "total": len(rows), "sources": rows}
    output = ROOT / args.json
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    markdown = make_markdown(rows, collected_at)
    if args.summary:
        summary = Path(args.summary)
        with summary.open("a", encoding="utf-8") as handle:
            handle.write(markdown)
    print(markdown)
    return 0


if __name__ == "__main__":
    sys.exit(main())
