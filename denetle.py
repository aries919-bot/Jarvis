#!/usr/bin/env python3
"""Review the complete staged diff with Groq, failing closed on uncertainty."""
from __future__ import annotations

import json
import os
import re
import sys
import urllib.error
import urllib.request
from pathlib import Path

API_URL = "https://api.groq.com/openai/v1/chat/completions"
MODEL = "openai/gpt-oss-120b"
MAX_CHUNK_CHARS = 10_000
OVERLAP_LINES = 16

SECRET_PATTERNS = (
    ("provider credential", re.compile(
        r"(?:gsk_[A-Za-z0-9]{20,}|sk-(?:live|test|proj)-[A-Za-z0-9_-]{16,}|"
        r"sk-[A-Za-z0-9]{32,}|AKIA[0-9A-Z]{16}|gh[pousr]_[A-Za-z0-9]{20,}|"
        r"AIza[0-9A-Za-z_-]{20,})"
    )),
    ("private key", re.compile(r"-----BEGIN [A-Z ]*PRIVATE KEY-----")),
    ("credential assignment", re.compile(
        r"\b(?:api[_-]?key|access[_-]?token|auth[_-]?token|token|secret|password|authorization)"
        r"\b\s*[:=]\s*[\"']?[A-Za-z0-9_./+=-]{16,}", re.IGNORECASE
    )),
    ("bearer credential", re.compile(r"\bBearer\s+[A-Za-z0-9._~+/=-]{16,}", re.IGNORECASE)),
)

REVIEW_RULES = (
    "Sen Android (Kotlin), Python ve Cloudflare Worker projeleri için dikkatli bir kod denetçisisin. "
    "Bu, tam farkın yalnızca bir parçası olabilir; başka dosya ve parçalarda yardımcı kod bulunabileceğini unutma. "
    "Sadece bu parçada açıkça görülen gerçek çalışma hatası, veri kaybı, gizlilik ihlali veya güvenlik açığı varsa RED ver. "
    "Stil, biçim, küçük öneriler veya bu parçada görünmeyen bağlamı eksik sanmak RED sebebi değildir. "
    "RED verirsen önce dosya adı ile fonksiyon/sınıf veya hunk bilgisini ve somut gerekçeyi yaz. "
    "Kesin bir sorun görmüyorsan ONAY ver. Son satır yalnızca ONAY veya RED olsun."
)


def find_secret_hits(diff: str) -> list[tuple[int, str]]:
    """Scan the entire, untruncated diff; return line numbers and pattern labels only."""
    hits: list[tuple[int, str]] = []
    seen: set[tuple[int, str]] = set()
    for label, pattern in SECRET_PATTERNS:
        for match in pattern.finditer(diff):
            line_number = diff.count("\n", 0, match.start()) + 1
            item = (line_number, label)
            if item not in seen:
                seen.add(item)
                hits.append(item)
    return sorted(hits)


def _split_section(section: str, max_chars: int) -> list[str]:
    if len(section) <= max_chars:
        return [section]

    lines = section.splitlines(keepends=True)
    has_git_header = bool(lines and lines[0].startswith("diff --git "))
    header = "".join(lines[:4]) if has_git_header else ""
    body_lines = lines[4:] if has_git_header else lines
    marker = "\n# Devam parçası: aynı dosyanın önceki değişiklik satırları yukarıda olabilir.\n"
    budget = max_chars - len(header) - len(marker)
    if budget < 256:
        raise ValueError("Diff parçası için başlık çok uzun; denetim güvenli biçimde bölünemedi.")

    chunks: list[str] = []
    current: list[str] = []
    current_chars = 0

    def emit() -> None:
        nonlocal current, current_chars
        if not current:
            return
        chunks.append(header + marker + "".join(current))
        overlap = current[-OVERLAP_LINES:]
        current = list(overlap)
        current_chars = sum(len(item) for item in current)

    for line in body_lines:
        if len(line) > budget:
            emit()
            remaining = line
            while len(remaining) > budget:
                fragment, remaining = remaining[:budget], remaining[budget:]
                chunks.append(header + marker + fragment)
            if remaining:
                current = [remaining]
                current_chars = len(remaining)
            continue

        if current and current_chars + len(line) > budget:
            emit()
            if current_chars + len(line) > budget:
                current = []
                current_chars = 0
        current.append(line)
        current_chars += len(line)

    if current:
        chunks.append(header + marker + "".join(current))
    if not chunks:
        chunks.append(section)
    return chunks


def split_diff(diff: str, max_chars: int = MAX_CHUNK_CHARS) -> list[str]:
    """Split by file first, then by lines with overlap so the full diff is reviewed."""
    if max_chars < 1_024:
        raise ValueError("max_chars must be at least 1024")
    if not diff:
        return []
    sections = re.split(r"(?m)(?=^diff --git .+$)", diff)
    chunks: list[str] = []
    for section in sections:
        if section:
            chunks.extend(_split_section(section, max_chars))
    return chunks


def _ask_groq(chunk: str, index: int, total: int, api_key: str) -> str:
    prompt = (
        REVIEW_RULES
        + f"\n\nTam farkın parçası: {index}/{total}. Bu parça yaklaşık {len(chunk)} karakter.\n"
        + "Yalnızca aşağıdaki Git farkını incele:\n\n"
        + chunk
    )
    payload = {
        "model": MODEL,
        "reasoning_effort": "low",
        "max_tokens": 2_000,
        "messages": [{"role": "user", "content": prompt}],
    }
    request = urllib.request.Request(
        API_URL,
        data=json.dumps(payload, ensure_ascii=False).encode("utf-8"),
        headers={
            "Content-Type": "application/json",
            "Authorization": "Bearer " + api_key,
            "User-Agent": "Aries-Diff-Reviewer/2.0",
        },
    )
    with urllib.request.urlopen(request, timeout=90) as response:
        result = json.loads(response.read().decode("utf-8"))
    return result["choices"][0]["message"]["content"]


def _verdict(answer: str) -> str:
    meaningful = [line.strip() for line in answer.splitlines() if line.strip()]
    if not meaningful:
        return "RED"
    final_line = meaningful[-1].strip().strip("*` ").upper()
    return final_line if final_line in {"ONAY", "RED"} else "RED"


def main() -> int:
    diff_path = Path(".git/fark.diff")
    try:
        diff = diff_path.read_text(encoding="utf-8")
    except OSError:
        print(".git/fark.diff okunamadi; denetim yapilamadi.\nRED")
        return 1

    if not diff.strip():
        print("Fark yok; denetim yapilamadi.\nRED")
        return 1

    secret_hits = find_secret_hits(diff)
    if secret_hits:
        print("GIZLI BILGI olabilecek desen bulundu; diff dis servise gonderilmedi:")
        for line_number, label in secret_hits:
            print(f"- diff satiri {line_number}: {label}")
        print("RED")
        return 1

    api_key = os.environ.get("GROQ_API_KEY", "").strip()
    if not api_key:
        print("GROQ_API_KEY tanimli degil; denetim tamamlanamadi.\nRED")
        return 1

    try:
        chunks = split_diff(diff)
    except Exception as exc:
        print(f"Diff parcalanirken hata olustu ({type(exc).__name__}); denetim tamamlanamadi.\nRED")
        return 1

    print(f"Tam fark tarandi: {len(diff)} karakter; Groq incelemesi {len(chunks)} parca.")
    approvals = 0
    for index, chunk in enumerate(chunks, start=1):
        try:
            answer = _ask_groq(chunk, index, len(chunks), api_key)
        except urllib.error.HTTPError as exc:
            print(f"Groq API HTTP hatasi: {exc.code}")
            print("RED")
            return 1
        except Exception as exc:
            print(f"Groq denetimi tamamlanamadi ({type(exc).__name__}).")
            print("RED")
            return 1
        print(f"\n--- Groq denetimi {index}/{len(chunks)} ---")
        print(answer.rstrip())
        if _verdict(answer) != "ONAY":
            print("\nRED")
            return 1
        approvals += 1

    if approvals == len(chunks):
        print("\nONAY")
        return 0
    print("\nRED")
    return 1


if __name__ == "__main__":
    raise SystemExit(main())
