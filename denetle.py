#!/usr/bin/env python3
"""Fail-closed full-diff security review."""
import json
import os
import re
import sys
import urllib.request

MAX_CHARS = 12000
MAX_PARTS = 8

SECRET_PATTERNS = (
    re.compile(r"(?:gsk_|sk-|AKIA|ghp_|AIza)[A-Za-z0-9_-]{16,}", re.I),
    re.compile(r"(?:api[_-]?key|token|secret|password)\\s*[=:]\\s*['\\"]?[A-Za-z0-9_-]{16,}", re.I),
    re.compile(r"-----BEGIN [A-Z0-9 ]*PRIVATE KEY-----", re.I),
    re.compile(
        r"(?im)^\\s*\\+?\\s*(?:export\\s+)?"
        r"[A-Z0-9_.-]*(?:API[_-]?KEY|TOKEN|SECRET|PASSWORD|CREDENTIAL|PRIVATE[_-]?KEY)"
        r"[A-Z0-9_.-]*\\s*=\\s*['\\"]?[^#\\s'\\"]{8,}"
    ),
)


def contains_secret(diff):
    return any(pattern.search(diff) for pattern in SECRET_PATTERNS)


def _split_large_file_section(section, max_chars):
    """Split an oversized file diff only between complete @@ hunks."""
    first_hunk = re.search(r"(?m)^@@", section)
    if first_hunk is None:
        return None
    preamble = section[:first_hunk.start()]
    hunks = re.split(r"(?=^@@)", section[first_hunk.start():], flags=re.M)
    chunks, current = [], preamble
    for hunk in hunks:
        if len(preamble) + len(hunk) > max_chars:
            return None
        if len(current) + len(hunk) > max_chars:
            if current == preamble:
                return None
            chunks.append(current)
            current = preamble
        current += hunk
    if current != preamble:
        chunks.append(current)
    return chunks or None


def split_diff(diff, max_chars=MAX_CHARS, max_parts=MAX_PARTS):
    """Pack file diffs; oversized files are split only at @@ hunk boundaries."""
    sections = re.split(r"(?=^diff --git )", diff, flags=re.M)
    if len(sections) > 1 and sections[0] == "":
        sections = sections[1:]
    if not sections:
        sections = [diff]

    file_chunks = []
    for section in sections:
        if len(section) <= max_chars:
            file_chunks.append(section)
            continue
        split_sections = _split_large_file_section(section, max_chars)
        if split_sections is None:
            return None
        file_chunks.extend(split_sections)

    parts, current = [], ""
    for section in file_chunks:
        if len(section) > max_chars:
            return None
        if current and len(current) + len(section) > max_chars:
            parts.append(current)
            current = ""
        current += section
    if current:
        parts.append(current)
    if not parts or len(parts) > max_parts:
        return None
    return parts


def _model_review(part, part_no, part_count, opener=None):
    api_key = os.environ.get("GROQ_API_KEY")
    if not api_key:
        raise RuntimeError("GROQ_API_KEY tanımlı değil")
    prompt = (
        "Sen kod denetcisisin. Aşağıdaki diff parçasını incele. "
        "Gerçek çalışma hatası, veri kaybı veya güvenlik açığı varsa RED ver; "
        "stil ve küçük öneriler RED sebebi değildir. "
        "Bu parça tam diff'in bir bölümüdür; yalnız bu parçaya dayanarak denetle. "
        "Sorun varsa dosya ve satırı belirt. Son satır yalnızca ONAY veya RED olsun.\\n\\n"
        f"Parça {part_no}/{part_count}:\\n\\n{part}"
    )
    request = urllib.request.Request(
        "https://api.groq.com/openai/v1/chat/completions",
        data=json.dumps({
            "model": "openai/gpt-oss-120b",
            "reasoning_effort": "low",
            "max_tokens": 2000,
            "messages": [{"role": "user", "content": prompt}],
        }).encode(),
        headers={
            "Content-Type": "application/json",
            "Authorization": "Bearer " + api_key,
            "User-Agent": "AriesKaynakTara/1.0",
        },
    )
    response = (opener or urllib.request.urlopen)(request, timeout=60)
    with response:
        payload = json.load(response)
    content = payload["choices"][0]["message"]["content"]
    if not isinstance(content, str) or not content.strip():
        raise ValueError("Model boş cevap verdi")
    return content.strip()


def review_diff(diff, opener=None):
    # Full diff is scanned before any outbound model request.
    if not isinstance(diff, str) or not diff.strip():
        return "RED: diff boş veya okunamadı"
    if contains_secret(diff):
        return "RED: gizli bilgi bulundu; diff gönderilmedi"
    parts = split_diff(diff)
    if parts is None:
        return "RED: diff çok büyük, elle bak"
    results = []
    try:
        for index, part in enumerate(parts, 1):
            answer = _model_review(part, index, len(parts), opener=opener)
            results.append(answer)
            if answer.splitlines()[-1].strip() != "ONAY":
                return "\\n\\n".join(results) + "\\nRED"
    except Exception as exc:
        return "RED: denetim hatası (" + type(exc).__name__ + ")"
    return "\\n\\n".join(results) + "\\nONAY"


def main():
    try:
        with open(".git/fark.diff", encoding="utf-8") as diff_file:
            diff = diff_file.read()
    except Exception:
        print("RED: diff okunamadı")
        return 1
    result = review_diff(diff)
    print(result)
    return 0 if result.splitlines()[-1] == "ONAY" else 1


if __name__ == "__main__":
    sys.exit(main())
