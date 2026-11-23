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
    r"üye girişi",
    r"giriş\s*yap(ın|iniz)?",
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
    for src, dst in _TR_LOWER.items():
        text = text.replace(src, dst)
    text = text.lower()
    # noktalama: "T.C." -> "t c" -> sonra tek harf grupları birleşir
    text = re.sub(r"[^0-9a-zçğıöşüâîû]+", " ", text)
    text = " ".join(text.split())
    # tek harfli kurum kısaltma noktalarını sadeleştir: "t c" -> "tc"
    text = re.sub(r"\b([a-z])\s+(?=[a-z]\b)", r"\1", text)
    return text


def normalize_url(url: str) -> str:
    """Aynı ilanın farklı yazımlarını tekilleştirmek için kanonik URL."""
    url = html.unescape((url or "").strip())
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

