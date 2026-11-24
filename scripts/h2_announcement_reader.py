#!/usr/bin/env python3
"""H2: robots-first reader for measured YHGM and BOTAŞ official notices."""
from __future__ import annotations

import argparse
import copy
import html
import io
import json
import re
import ssl
import time
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from datetime import date, datetime, timezone
from email.message import Message
from html.parser import HTMLParser
from pathlib import Path
from typing import Any, Callable
from urllib.robotparser import RobotFileParser
from zoneinfo import ZoneInfo

ROOT = Path(__file__).resolve().parents[1]
CONFIG = ROOT / "career" / "h2_sources.json"
USER_AGENT = "AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)"
MAX_HTML_BYTES = 5 * 1024 * 1024
MAX_PDF_BYTES = 20 * 1024 * 1024
MAX_ROBOTS_BYTES = 512 * 1024
TIMEOUT_SECONDS = 15
MAX_REDIRECTS = 3
ALLOWED_HOSTS = {"yhgm.saglik.gov.tr", "www.botas.gov.tr", "botas.gov.tr", "atk.gov.tr", "www.atk.gov.tr", "anadolu.edu.tr", "cdn.duzce.edu.tr", "personel.tarsus.edu.tr"}

NUMERIC_DATE = re.compile(r"(?<!\d)(\d{1,2})[./-](\d{1,2})[./-](\d{4})(?!\d)")
RANGE_DATE = re.compile(
    r"(\d{1,2}[./-]\d{1,2}[./-]\d{4})\s*(?:-|–|—|−)\s*"
    r"(\d{1,2}[./-]\d{1,2}[./-]\d{4})"
)


class SourceError(RuntimeError):
    pass


@dataclass
class FetchResponse:
    status: int | None
    headers: Any
    body: bytes
    error: str | None = None


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


class PageParser(HTMLParser):
    def __init__(self) -> None:
        super().__init__(convert_charrefs=True)
        self.parts: list[str] = []
        self.links: list[tuple[str,str]] = []
        self.meta: dict[str,str] = {}
        self._href: str | None = None
        self._anchor: list[str] = []

    def handle_starttag(self, tag: str, attrs: list[tuple[str,str | None]]) -> None:
        values = {k.lower():v for k,v in attrs if v is not None}
        if tag.lower() == "a":
            self._href = values.get("href")
            self._anchor = []
        if tag.lower() == "meta":
            name = (values.get("property") or values.get("name") or values.get("itemprop") or "").lower()
            if name and values.get("content"):
                self.meta[name] = values["content"]

    def handle_data(self, data: str) -> None:
        clean = " ".join(data.split())
        if clean:
            self.parts.append(clean)
            if self._href is not None:
                self._anchor.append(clean)

    def handle_endtag(self, tag: str) -> None:
        if tag.lower() == "a" and self._href is not None:
            self.links.append((" ".join(self._anchor).strip(), self._href))
            self._href = None
            self._anchor = []

    @property
    def text(self) -> str:
        return " ".join(self.parts)


def utc_now() -> str:
    return datetime.now(timezone.utc).replace(microsecond=0).isoformat().replace("+00:00","Z")


def istanbul_today() -> date:
    return datetime.now(ZoneInfo("Europe/Istanbul")).date()


def request_once(url: str, limit: int) -> FetchResponse:
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
    # Default context verifies certificate chain and hostname. Do not weaken TLS.
    opener = urllib.request.build_opener(
        urllib.request.HTTPSHandler(context=ssl.create_default_context()),
        NoRedirect(),
    )
    try:
        with opener.open(request, timeout=TIMEOUT_SECONDS) as response:
            body = response.read(limit + 1)
            if len(body) > limit:
                return FetchResponse(response.status,response.headers,body[:limit],"BODY_LIMIT")
            return FetchResponse(response.status,response.headers,body)
    except urllib.error.HTTPError as exc:
        try:
            body = exc.read(limit + 1)
        except Exception:
            body = b""
        return FetchResponse(exc.code,exc.headers,body[:limit])
    except urllib.error.URLError as exc:
        reason = getattr(exc,"reason",exc)
        if isinstance(reason,ssl.SSLError):
            return FetchResponse(None,None,b"",f"TLS_ERROR: {reason}")
        if isinstance(reason,TimeoutError):
            return FetchResponse(None,None,b"",f"TIMEOUT: {reason}")
        return FetchResponse(None,None,b"",f"NETWORK_ERROR: {reason}")
    except ssl.SSLError as exc:
        return FetchResponse(None,None,b"",f"TLS_ERROR: {exc}")
    except TimeoutError as exc:
        return FetchResponse(None,None,b"",f"TIMEOUT: {exc}")
    except Exception as exc:
        return FetchResponse(None,None,b"",f"NETWORK_ERROR: {type(exc).__name__}: {exc}")


def origin(url: str) -> str:
    parsed = urllib.parse.urlsplit(url)
    host = (parsed.hostname or "").lower()
    if parsed.scheme != "https" or host not in ALLOWED_HOSTS:
        raise SourceError(f"HTTPS dışı veya allowlist dışı adres: {url}")
    return f"https://{parsed.netloc}"


def same_official_host(left: str, right: str) -> bool:
    a = (urllib.parse.urlsplit(left).hostname or "").lower()
    b = (urllib.parse.urlsplit(right).hostname or "").lower()
    if a == b:
        return True
    return {a, b}.issubset({"www.botas.gov.tr", "botas.gov.tr"}) or {a, b}.issubset({"atk.gov.tr", "www.atk.gov.tr"})


def official_link_target(page: PageParser, base_url: str, expected_url: str) -> str | None:
    """Match a configured attachment to a link on the same official host, allowing URL-encoding variants."""
    expected = urllib.parse.urlsplit(expected_url)
    expected_path = urllib.parse.unquote(expected.path).casefold().rstrip("/")
    expected_query = expected.query
    for candidate in official_link_map(page,base_url):
        parsed = urllib.parse.urlsplit(candidate)
        if not same_official_host(base_url,candidate):
            continue
        candidate_path = urllib.parse.unquote(parsed.path).casefold().rstrip("/")
        if candidate_path == expected_path and parsed.query == expected_query:
            return candidate
    return None


def header(headers: Any, name: str) -> str | None:
    if headers is None or not hasattr(headers,"get"):
        return None
    value = headers.get(name)
    return str(value) if value is not None else None


def fetch_public_url(
    url: str,
    requester: Callable[[str,int],FetchResponse] = request_once,
    robots_cache: dict[str,RobotFileParser] | None = None,
    limit: int = MAX_HTML_BYTES,
) -> tuple[bytes,str,Any]:
    cache = robots_cache if robots_cache is not None else {}
    current = url
    for redirect_number in range(MAX_REDIRECTS + 1):
        host_origin = origin(current)
        if host_origin not in cache:
            robots_url = host_origin + "/robots.txt"
            for robots_redirect in range(MAX_REDIRECTS + 1):
                robots = requester(robots_url,MAX_ROBOTS_BYTES)
                if robots.error:
                    raise SourceError(f"{robots_url}: robots UNKNOWN: {robots.error}")
                if robots.status in {301,302,303,307,308}:
                    location = header(robots.headers,"Location")
                    if not location:
                        raise SourceError(f"robots UNKNOWN: HTTP {robots.status}; Location yok")
                    target = urllib.parse.urljoin(robots_url,location)
                    target_origin = origin(target)
                    if not same_official_host(host_origin,target_origin):
                        raise SourceError("robots yönlendirmesi farklı/izin verilmeyen hosta gidiyor")
                    robots_url = target
                    continue
                if robots.status == 404:
                    parser = RobotFileParser()
                    parser.parse([])
                    break
                if robots.status is not None and 200 <= robots.status < 300 and robots.body:
                    parser = RobotFileParser()
                    parser.parse(robots.body.decode("utf-8","replace").splitlines())
                    break
                raise SourceError(f"robots UNKNOWN: HTTP {robots.status}; hedef istenmedi")
            else:
                raise SourceError("robots yönlendirme sınırı aşıldı; hedef istenmedi")
            cache[host_origin] = parser
        if not cache[host_origin].can_fetch(USER_AGENT,current):
            raise SourceError("robots DISALLOW; hedef sayfaya istek gönderilmedi")
        response = requester(current,limit)
        if response.error and response.error.startswith("TIMEOUT:"):
            first_timeout = response.error
            time.sleep(1)
            response = requester(current,limit)
            if response.error:
                raise SourceError(
                    f"{current}: TIMEOUT_RETRY_FAILED: first={first_timeout}; retry={response.error}"
                )
        if response.error:
            raise SourceError(f"{current}: {response.error}")
        if response.status in {301,302,303,307,308}:
            location = header(response.headers,"Location")
            if not location:
                raise SourceError(f"HTTP {response.status}; Location yok")
            target = urllib.parse.urljoin(current,location)
            target_origin = origin(target)
            if not same_official_host(host_origin,target_origin):
                raise SourceError("Farklı/izin verilmeyen hosta yönlendirme reddedildi")
            if redirect_number >= MAX_REDIRECTS:
                raise SourceError("Yönlendirme sınırı aşıldı")
            current = target
            continue
        if response.status != 200:
            raise SourceError(f"HTTP {response.status}; kaynak okunamadı")
        if not response.body or not response.body.strip():
            raise SourceError("Boş yanıt; son iyi veri korunmalı")
        return response.body,current,response.headers
    raise SourceError("Yönlendirme sınırı aşıldı")


def parse_html(body: bytes, headers: Any) -> PageParser:
    content_type = header(headers,"Content-Type") or ""
    match = re.search(r"charset=([\w-]+)",content_type,re.I)
    charset = match.group(1) if match else "utf-8"
    try:
        source = body.decode(charset,"replace")
    except LookupError:
        source = body.decode("utf-8","replace")
    parser = PageParser()
    parser.feed(source)
    return parser


def official_link_map(page: PageParser, base_url: str) -> dict[str,str]:
    """Return only links that stay on the same official host family."""
    links: dict[str,str] = {}
    for label, href in page.links:
        absolute = urllib.parse.urljoin(base_url,href)
        try:
            if same_official_host(base_url,absolute):
                links[absolute] = " ".join(label.split())
        except SourceError:
            continue
    return links


MONTHS_TR = {
    "ocak":1, "şubat":2, "subat":2, "mart":3, "nisan":4, "mayıs":5, "mayis":5,
    "haziran":6, "temmuz":7, "ağustos":8, "agustos":8, "eylül":9, "eylul":9,
    "ekim":10, "kasım":11, "kasim":11, "aralık":12, "aralik":12,
}
WORD_DATE = re.compile(
    r"(\d{1,2})\s+(Ocak|Şubat|Subat|Mart|Nisan|Mayıs|Mayis|Haziran|Temmuz|Ağustos|Agustos|Eylül|Eylul|Ekim|Kasım|Kasim|Aralık|Aralik)\s+(20\d{2})",
    re.I,
)
WORD_RANGE = re.compile(
    r"(\d{1,2}\s+(?:Ocak|Şubat|Subat|Mart|Nisan|Mayıs|Mayis|Haziran|Temmuz|Ağustos|Agustos|Eylül|Eylul|Ekim|Kasım|Kasim|Aralık|Aralik)\s+20\d{2})"
    r".{0,260}?(\d{1,2}\s+(?:Ocak|Şubat|Subat|Mart|Nisan|Mayıs|Mayis|Haziran|Temmuz|Ağustos|Agustos|Eylül|Eylul|Ekim|Kasım|Kasim|Aralık|Aralik)\s+20\d{2})",
    re.I | re.S,
)


def parse_date(value: str | None) -> str | None:
    if not value:
        return None
    match = NUMERIC_DATE.search(value)
    if match:
        day,month,year = map(int,match.groups())
        try:
            return date(year,month,day).isoformat()
        except ValueError:
            return None
    word = WORD_DATE.search(value)
    if word:
        day,month_name,year = word.groups()
        month = MONTHS_TR.get(month_name.lower())
        try:
            return date(int(year),month,int(day)).isoformat() if month else None
        except ValueError:
            return None
    return None


def explicit_published_at(page_text: str, meta: dict[str,str] | None = None,
                          mode: str | None = None) -> str | None:
    meta = meta or {}
    for key in ("article:published_time","datepublished","publishdate","dc.date","date"):
        if key in meta:
            parsed = parse_date(meta[key])
            if parsed:
                return parsed
    if mode == "leading-date":
        first = next((line.strip() for line in page_text.splitlines() if line.strip()), "")
        parsed = parse_date(first)
        if parsed:
            return parsed
    if mode == "duzce-title":
        match = re.search(r"DÜZCE ÜNİVERSİTESİ\s+(\d{1,2}[./-]\d{1,2}[./-]\d{4})\s+TARİHLİ",page_text,re.I)
        if match:
            return parse_date(match.group(1))
    if mode == "tarsus-gazette":
        match = re.search(r"İlanın Yayımlandığı Resmî Gazete Tarih ve Sayısı\s*:\s*(\d{1,2}[./-]\d{1,2}[./-]\d{4})",page_text,re.I)
        if match:
            return parse_date(match.group(1))
    # A named publication field is stronger evidence than a date in a title.
    labelled = re.search(r"(?:Yayın Tarihi|Yayımlanma Tarihi|Tarih)\s*:\s*(\d{1,2}[./-]\d{1,2}[./-]\d{4})",page_text,re.I)
    if labelled:
        return parse_date(labelled.group(1))
    added = re.search(r"Eklenme Tarihi\s*:\s*(\d{1,2}\s+[A-Za-zÇĞİÖŞÜçğıöşü]+\s+20\d{2})",page_text,re.I)
    if added:
        return parse_date(added.group(1))
    # YHGM explicitly dates the notice; this is not the kura date in the title.
    notice = re.search(r"(\d{1,2}[./-]\d{1,2}[./-]\d{4})\s*Tarihli\s+Duyuru",page_text,re.I)
    if notice:
        return parse_date(notice.group(1))
    return None


def application_deadline(text: str, context: str) -> str | None:
    lowered_context = context.lower()
    # Numeric or Turkish month-name date ranges; never substitute publication/collection dates.
    for match in RANGE_DATE.finditer(text):
        start = max(0,match.start()-260)
        end = min(len(text),match.end()+260)
        if lowered_context in text[start:end].lower():
            return parse_date(match.group(2))
    for match in WORD_RANGE.finditer(text):
        start = max(0,match.start()-260)
        end = min(len(text),match.end()+260)
        if lowered_context in text[start:end].lower():
            return parse_date(match.group(2))
    return None


def status_for(deadline: str | None, published: str | None, content_type: str, today: date) -> str:
    if deadline:
        end = date.fromisoformat(deadline)
        if end < today:
            return "Süresi geçti"
        if published and date.fromisoformat(published) <= today:
            return "Doğrulandı"
        return "Doğrulanamadı"
    if content_type == "result" and published and date.fromisoformat(published) <= today:
        return "Süresi geçti"
    return "Doğrulanamadı"


def make_item(
    *, item_id: str, institution: str, title: str, category: str,
    content_type: str, official_url: str, how_to_apply: dict[str,str],
    conditions: list[str], deadline: str | None, published_at: str | None,
    collected_at: str, first_seen_at: str, today: date, tags: list[str] | None = None,
) -> dict[str,Any]:
    return {
        "id":item_id,
        "institution":institution,
        "title":title,
        "category":category,
        "contentType":content_type,
        "officialSourceUrl":official_url,
        "howToApply":how_to_apply,
        "conditions":conditions,
        "deadline":deadline,
        "publishedAt":published_at,
        "collectedAt":collected_at,
        "firstSeenAt":first_seen_at,
        "freshnessStatus":status_for(deadline,published_at,content_type,today),
        "tags":tags or [],
        "sourceStatus":"fresh",
    }


YHGM_URL = "https://yhgm.saglik.gov.tr/TR-111525/tasra-teskilati-hizmet-birimlerinde-istihdam-edilmek-uzere-2764-surekli-isci-alim-kurasi-hakkinda-duyuru.html"
BOTAS_JOB_URL = "https://www.botas.gov.tr/Icerik/223-kisilik-personel-alim-duyu/1203"
BOTAS_PDF_URL = "https://www.botas.gov.tr/uploads/dosyaYoneticisi/559215-lan-tablo.pdf"
BOTAS_RESULT_URL = "https://www.botas.gov.tr/Icerik/10-kasim-2025-14-kasim-2025/1233"


def parse_yhgm(body: bytes, headers: Any, collected_at: str, first_seen_at: str, today: date) -> dict[str,Any]:
    page = parse_html(body,headers)
    text = html.unescape(page.text)
    if "2.764" not in text or "kura" not in text.lower():
        raise SourceError("YHGM 2.764 kura duyurusu doğrulanamadı")
    deadline = application_deadline(text,"başvurular")
    if not deadline:
        raise SourceError("YHGM başvuru aralığı yok; deadline üretilmedi")
    published = explicit_published_at(text,page.meta)
    return make_item(
        item_id="yhgm-2764-kura-2025",
        institution="T.C. Sağlık Bakanlığı — Yönetim Hizmetleri Genel Müdürlüğü",
        title="Taşra Teşkilatı Hizmet Birimlerinde İstihdam Edilmek Üzere 2.764 Sürekli İşçi Alım Kurası Hakkında Duyuru",
        category="Kura",
        content_type="lottery",
        official_url=YHGM_URL,
        how_to_apply={
            "method":"Başvuru dönemi kapandı; kura sonucu resmî sonuç sayfasından sorgulanır.",
            "url":"https://iscisonuc.saglik.gov.tr/",
            "instructions":"Başvurular 08.10.2025–13.10.2025 arasında İŞKUR üzerinden alınmış; kura 12.11.2025 tarihinde çekilmiştir."
        },
        conditions=[
            "2.764 sürekli işçi alımı için İŞKUR tarafından bildirilen adaylar noter kurasına alınmıştır.",
            "Asıl ve yedek adaylar açık iş sayısı kadar kura ile belirlenmiştir."
        ],
        deadline=deadline,
        published_at=published,
        collected_at=collected_at,
        first_seen_at=first_seen_at,
        today=today,
        tags=["kura","sonuç","arşiv"],
    )


def extract_pdf_text(body: bytes) -> str:
    try:
        from pypdf import PdfReader
    except ImportError as exc:
        raise SourceError("pypdf kurulu değil") from exc
    try:
        reader = PdfReader(io.BytesIO(body),strict=False)
        return "\n".join(page.extract_text() or "" for page in reader.pages[:20])
    except Exception as exc:
        raise SourceError(f"BOTAŞ PDF çözümlenemedi: {type(exc).__name__}") from exc


def parse_botas_job(detail_body: bytes, detail_headers: Any, pdf_text: str,
                    collected_at: str, first_seen_at: str, today: date) -> dict[str,Any]:
    page = parse_html(detail_body,detail_headers)
    text = html.unescape(page.text)
    if "223 kişilik personel alım duyurusu" not in text.lower():
        raise SourceError("BOTAŞ 223 kişilik personel başlığı yok")
    published = explicit_published_at(text,page.meta)
    deadline = application_deadline(pdf_text,"ilanlar")
    if not deadline:
        raise SourceError("BOTAŞ PDF ilan aralığı yok; deadline üretilmedi")
    required = [
        "Pozisyon, öğrenim, mezun olunan bölüm, KPSS puan türü ve kontenjan PDF tablosunda pozisyona göre belirtilmiştir.",
        "Adayların, İŞKUR ilanındaki son başvuru tarihi itibarıyla belirtilen bölümden mezun olması gerekir.",
        "Duyurudaki genel şartta, ilgili ilandaki KPSS puan türünden en az 60 puan istenir.",
        "Nihai listeden mülakata giren adaylarda 70 puanın altı başarısız sayılır."
    ]
    if "35 yaşını" in pdf_text.lower():
        required.append("İlan tarihi itibarıyla 35 yaşını doldurmamış olma şartı bulunur.")
    return make_item(
        item_id="botas-223-personel-alimi-2025",
        institution="Boru Hatları ile Petrol Taşıma A.Ş. (BOTAŞ)",
        title="223 Kişilik Personel Alım Duyurusu",
        category="Kamu bağlı büyük şirket",
        content_type="job",
        official_url=BOTAS_JOB_URL,
        how_to_apply={
            "method":"İŞKUR üzerinden başvuru; başvuru dönemi sona erdi.",
            "url":"https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx",
            "instructions":"Resmî ek PDF'de İŞKUR ilanlarının 10.11.2025–14.11.2025 arasında yayımlanacağı belirtilir. İŞKUR ayrıntılı ilan sayfası bu okuyucuda kazınmaz."
        },
        conditions=required,
        deadline=deadline,
        published_at=published,
        collected_at=collected_at,
        first_seen_at=first_seen_at,
        today=today,
        tags=["KPSS","arşiv"],
    )


def parse_botas_result(body: bytes, headers: Any, collected_at: str,
                       first_seen_at: str, today: date) -> dict[str,Any]:
    page = parse_html(body,headers)
    text = html.unescape(page.text)
    if "müfettiş yardımcılığı" not in text.lower() or "sonuç duyurusu" not in text.lower():
        raise SourceError("BOTAŞ müfettiş yardımcılığı sonuç duyurusu doğrulanamadı")
    published = explicit_published_at(text,page.meta)
    return make_item(
        item_id="botas-mufettis-sonuc-2026",
        institution="Boru Hatları ile Petrol Taşıma A.Ş. (BOTAŞ)",
        title="10 Kasım 2025 / 14 Kasım 2025 Tarihli Müfettiş Yardımcılığı Alımına Yönelik Sonuç Duyurusu",
        category="Kamu bağlı büyük şirket",
        content_type="result",
        official_url=BOTAS_RESULT_URL,
        how_to_apply={"method":"Başvuru yok; sonuç duyurusu.","url":BOTAS_RESULT_URL,"instructions":"Bu kayıt önceki alım sürecinin sonucudur; açık başvuru değildir."},
        conditions=[],
        deadline=None,
        published_at=published,
        collected_at=collected_at,
        first_seen_at=first_seen_at,
        today=today,
        tags=["sonuç","arşiv"],
    )


def parse_official_notice(source: dict[str,Any], page_text: str, attachment_text: str,
                         collected_at: str, first_seen_at: str, today: date) -> dict[str,Any]:
    combined = html.unescape((page_text or "") + "\\n" + (attachment_text or ""))
    for needle in source.get("expectedText", []):
        if str(needle).lower() not in combined.lower():
            raise SourceError(f"Resmî duyuru doğrulanamadı; beklenen metin bulunamadı: {needle}")
    published = explicit_published_at(combined, mode=source.get("publicationMode"))
    if not published:
        raise SourceError("Açık yayın tarihi kanıtı bulunamadı; publishedAt üretilmedi")
    deadline = application_deadline(combined, str(source.get("deadlineContext", "başvuru")))
    if not deadline:
        raise SourceError("Açık başvuru aralığı bulunamadı; deadline üretilmedi")
    how_to_apply = source.get("howToApply")
    if not isinstance(how_to_apply, dict) or not how_to_apply.get("url"):
        raise SourceError("Resmî başvuru yönlendirmesi eksik")
    return make_item(
        item_id=str(source["itemId"]),
        institution=str(source["institution"]),
        title=str(source["title"]),
        category=str(source.get("category", "Kamu")),
        content_type=str(source.get("contentType", "job")),
        official_url=str(source["url"]),
        how_to_apply=how_to_apply,
        conditions=[str(x) for x in source.get("conditions", [])],
        deadline=deadline,
        published_at=published,
        collected_at=collected_at,
        first_seen_at=first_seen_at,
        today=today,
        tags=[str(x) for x in source.get("tags", [])],
    )


def collect_official(source: dict[str,Any], requester: Callable[[str,int],FetchResponse],
                     now: str, previous: dict[str,Any] | None, today: date) -> dict[str,Any]:
    robots_cache: dict[str,RobotFileParser] = {}
    fmt = source.get("format", "html")
    body,_,headers = fetch_public_url(source["url"],requester,robots_cache,
                                       MAX_PDF_BYTES if fmt == "pdf" else MAX_HTML_BYTES)
    attachment_text = ""
    if fmt == "pdf":
        page_text = extract_pdf_text(body)
    elif fmt == "html":
        page = parse_html(body,headers)
        page_text = page.text
        pdf_url = source.get("pdfUrl")
        if pdf_url:
            linked_pdf = official_link_target(page,source["url"],pdf_url)
            if not linked_pdf:
                raise SourceError("Ek PDF aynı resmî HTML sayfasında/hostunda bağlantı olarak bulunamadı")
            pdf_body,_,_ = fetch_public_url(linked_pdf,requester,robots_cache,MAX_PDF_BYTES)
            attachment_text = extract_pdf_text(pdf_body)
    else:
        raise SourceError(f"Desteklenmeyen resmî duyuru biçimi: {fmt}")
    item = parse_official_notice(source,page_text,attachment_text,now,now,today)
    items = [item]
    assign_first_seen(items,previous,now)
    return {"schemaVersion":1,"sourceId":source["id"],"collectedAt":now,
            "sourceStatus":"fresh","items":items}


def load_previous(previous_dir: Path | None, source_id: str) -> dict[str,Any] | None:
    if previous_dir is None:
        return None
    path = previous_dir / f"{source_id}.json"
    if not path.is_file():
        return None
    try:
        feed = json.loads(path.read_text(encoding="utf-8"))
        if feed.get("schemaVersion") == 1 and feed.get("sourceId") == source_id:
            return feed
    except (OSError,ValueError,TypeError):
        return None
    return None


def assign_first_seen(items: list[dict[str,Any]], previous: dict[str,Any] | None, collected_at: str) -> None:
    prior = {item.get("id"):item.get("firstSeenAt") for item in (previous or {}).get("items",[])}
    for item in items:
        item["firstSeenAt"] = prior.get(item["id"]) or collected_at


def stale_feed(source_id: str, attempted_at: str, error: str, previous: dict[str,Any] | None) -> dict[str,Any]:
    if previous:
        kept = copy.deepcopy(previous)
        for item in kept.get("items",[]):
            item["sourceStatus"] = "stale"
        kept["sourceStatus"] = "stale"
        kept["lastAttemptAt"] = attempted_at
        kept["lastError"] = error
        return kept
    return {
        "schemaVersion":1,
        "sourceId":source_id,
        "collectedAt":attempted_at,
        "sourceStatus":"stale",
        "items":[],
        "lastAttemptAt":attempted_at,
        "lastError":error,
    }


def collect_yhgm(source: dict[str,Any], requester: Callable[[str,int],FetchResponse],
                 now: str, previous: dict[str,Any] | None, today: date) -> dict[str,Any]:
    body,_,headers = fetch_public_url(source["url"],requester,{},MAX_HTML_BYTES)
    items = [parse_yhgm(body,headers,now,now,today)]
    assign_first_seen(items,previous,now)
    return {"schemaVersion":1,"sourceId":source["id"],"collectedAt":now,"sourceStatus":"fresh","items":items}


def write_feed(feed: dict[str,Any], out_dir: Path, previous_dir: Path | None = None) -> None:
    source_id = feed["sourceId"]
    payload = json.dumps(feed,ensure_ascii=False,indent=2) + "\n"
    out_dir.mkdir(parents=True,exist_ok=True)
    (out_dir/f"{source_id}.json").write_text(payload,encoding="utf-8")
    if previous_dir is not None and feed.get("sourceStatus") == "fresh":
        previous_dir.mkdir(parents=True,exist_ok=True)
        (previous_dir/f"{source_id}.json").write_text(payload,encoding="utf-8")


def collect_botas(source: dict[str,Any], requester: Callable[[str,int],FetchResponse],
                  now: str, previous: dict[str,Any] | None, today: date) -> dict[str,Any]:
    robots_cache: dict[str,RobotFileParser] = {}
    # Read measured official archive first, then only the two verified detail pages and the linked official PDF.
    listing,_,listing_headers = fetch_public_url(source["listingUrl"],requester,robots_cache,MAX_HTML_BYTES)
    listing_page = parse_html(listing,listing_headers)
    if "Duyurular" not in listing_page.text:
        raise SourceError("BOTAŞ duyuru arşivi doğrulanamadı")
    listed_links = official_link_map(listing_page,source["listingUrl"])
    job_label = listed_links.get(source["jobUrl"],"").lower()
    result_label = listed_links.get(source["resultUrl"],"").lower()
    if "223 kişilik personel alım duyurusu" not in job_label:
        raise SourceError("223 kişilik duyuru arşivde resmî bağlantı olarak bulunamadı")
    if "sonuç duyurusu" not in result_label:
        raise SourceError("Müfettiş yardımcılığı sonuç bağlantısı arşivde bulunamadı")

    job_body,_,job_headers = fetch_public_url(source["jobUrl"],requester,robots_cache,MAX_HTML_BYTES)
    job_page = parse_html(job_body,job_headers)
    pdf_links = official_link_map(job_page,source["jobUrl"])
    if source["jobPdfUrl"] not in pdf_links:
        raise SourceError("BOTAŞ resmî PDF eki detay sayfasında bağlantı olarak bulunamadı")
    pdf_body,_,_ = fetch_public_url(source["jobPdfUrl"],requester,robots_cache,MAX_PDF_BYTES)
    result_body,_,result_headers = fetch_public_url(source["resultUrl"],requester,robots_cache,MAX_HTML_BYTES)
    pdf_text = extract_pdf_text(pdf_body)
    items = [
        parse_botas_job(job_body,job_headers,pdf_text,now,now,today),
        parse_botas_result(result_body,result_headers,now,now,today),
    ]
    assign_first_seen(items,previous,now)
    return {"schemaVersion":1,"sourceId":source["id"],"collectedAt":now,"sourceStatus":"fresh","items":items}


def run_reader(config_path: Path = CONFIG, out_dir: Path = ROOT/"reports"/"h2",
               previous_dir: Path | None = None,
               requester: Callable[[str,int],FetchResponse] = request_once,
               collected_at: str | None = None, today: date | None = None) -> tuple[list[dict[str,Any]],str]:
    config = json.loads(config_path.read_text(encoding="utf-8"))
    sources = config.get("sources",[])
    allowed_readers = {"yhgm", "botas", "official"}
    if not isinstance(sources,list) or len(sources) < 2:
        raise SourceError("H2 en az iki ölçülmüş resmî kaynak bekler")
    source_ids = [source.get("id") for source in sources]
    if any(not isinstance(source_id,str) or not source_id for source_id in source_ids) or len(source_ids) != len(set(source_ids)):
        raise SourceError("H2 kaynak kimlikleri eksik veya tekrarlı")
    if {source.get("reader") for source in sources} - allowed_readers:
        raise SourceError("H2 ölçülmemiş okuyucu türünü reddetti")
    if not {"yhgm","botas"}.issubset({source.get("reader") for source in sources}):
        raise SourceError("H2 ana YHGM ve BOTAŞ kayıtları eksik")
    now = collected_at or utc_now()
    today_value = today or istanbul_today()
    out_dir.mkdir(parents=True,exist_ok=True)
    rows = []
    for source in sources:
        previous = load_previous(previous_dir,source["id"])
        try:
            reader = source["reader"]
            if reader == "yhgm":
                feed = collect_yhgm(source,requester,now,previous,today_value)
            elif reader == "botas":
                feed = collect_botas(source,requester,now,previous,today_value)
            else:
                feed = collect_official(source,requester,now,previous,today_value)
        except SourceError as exc:
            feed = stale_feed(source["id"],now,str(exc),previous)
        write_feed(feed,out_dir,previous_dir)
        rows.append({"sourceId":source["id"],"sourceStatus":feed["sourceStatus"],
                     "items":len(feed.get("items",[])),"error":feed.get("lastError")})
    summary = make_summary(rows,now)
    return rows,summary


def make_summary(rows: list[dict[str,Any]], collected_at: str) -> str:
    fresh = sum(row["sourceStatus"] == "fresh" for row in rows)
    stale = sum(row["sourceStatus"] == "stale" for row in rows)
    total = sum(row["items"] for row in rows)
    lines = [
        "## H2 resmî duyuru okuyucusu",
        "",
        f"- collectedAt (UTC): **{collected_at}**",
        f"- Kaynak: **{len(rows)}** (ölçülmüş resmî kaynaklar)",
        f"- Durum: **{fresh} fresh / {stale} stale**",
        f"- Kart: **{total}**",
        "",
        "| sourceId | sourceStatus | kart | hata |",
        "|---|---|---:|---|",
    ]
    for row in rows:
        lines.append(f"| {row['sourceId']} | {row['sourceStatus']} | {row['items']} | {row.get('error') or ''} |")
    lines.extend([
        "",
        "Kura/sonuç kayıtları yeni iş sayacına girmez. Geçmiş deadline güncel iş listesine girmez.",
        "Robots izin belirsizse hedef sayfaya istek yoktur; TLS varsayılan doğrulaması açıktır.",
        "Bu okuyucu gerçek başvurusu açık kamu ilanı sayısını artırdığını iddia etmez.",
    ])
    return "\n".join(lines)+"\n"


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--config",type=Path,default=CONFIG)
    parser.add_argument("--out-dir",type=Path,default=ROOT/"reports"/"h2")
    parser.add_argument("--previous-dir",type=Path)
    parser.add_argument("--summary",type=Path)
    args = parser.parse_args()
    rows,summary = run_reader(args.config,args.out_dir,args.previous_dir)
    print(summary)
    if args.summary:
        with args.summary.open("a",encoding="utf-8") as handle:
            handle.write(summary)
    # Source outages are reported as per-source stale states, not parser failures.
    # The workflow succeeds when both source records were processed and summarized.
    return 0 if len(rows) >= 2 else 1


if __name__ == "__main__":
    raise SystemExit(main())
