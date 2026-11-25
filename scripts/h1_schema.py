#!/usr/bin/env python3
"""H1 JSON contract validation and source-isolation helpers (stdlib only)."""
from __future__ import annotations
import copy
from datetime import date, datetime, timedelta
from typing import Any

CATEGORIES = {
    "Kamu", "Kura", "KPSS", "KPSS'siz",
    "Büyük özel şirket", "Kamu bağlı büyük şirket",
}
CONTENT_TYPES = {"job", "lottery", "result", "internship", "exam", "other"}
FRESHNESS = {"Doğrulandı", "Süresi geçti", "Doğrulanamadı"}
SOURCE_STATUS = {"fresh", "stale"}
ITEM_REQUIRED = {
    "id", "institution", "title", "category", "contentType",
    "officialSourceUrl", "howToApply", "conditions", "deadline",
    "publishedAt", "collectedAt", "firstSeenAt", "freshnessStatus",
    "tags", "sourceStatus",
}


def _nonempty_string(value: Any) -> bool:
    return isinstance(value, str) and bool(value.strip())


def _parse_datetime(value: Any, field: str, *, require_utc: bool = False) -> datetime:
    if not isinstance(value, str) or not value.strip():
        raise ValueError(f"{field}: ISO-8601 metni gerekli")
    try:
        parsed = datetime.fromisoformat(value.replace("Z", "+00:00"))
    except ValueError as exc:
        raise ValueError(f"{field}: geçersiz ISO-8601 tarih/saat") from exc
    if parsed.tzinfo is None or parsed.utcoffset() is None:
        raise ValueError(f"{field}: saat dilimi zorunlu")
    if require_utc and parsed.utcoffset() != timedelta(0):
        raise ValueError(f"{field}: UTC (+00:00/Z) olmalı")
    return parsed


def _parse_date_or_datetime(value: Any, field: str) -> date:
    if not isinstance(value, str) or not value.strip():
        raise ValueError(f"{field}: ISO-8601 tarih metni gerekli")
    if len(value) == 10:
        try:
            return date.fromisoformat(value)
        except ValueError as exc:
            raise ValueError(f"{field}: geçersiz ISO-8601 tarih") from exc
    return _parse_datetime(value, field).date()


def _https_url(value: Any) -> bool:
    if not _nonempty_string(value):
        return False
    from urllib.parse import urlsplit
    parsed = urlsplit(value)
    return (
        parsed.scheme.lower() == "https"
        and bool(parsed.hostname)
        and parsed.username is None
        and parsed.password is None
    )


def validate_feed(feed: Any, today: date) -> list[str]:
    """Return contract errors without guessing missing dates or application status."""
    errors: list[str] = []
    if not isinstance(feed, dict):
        return ["feed: object olmalı"]
    if feed.get("schemaVersion") != 1:
        errors.append("schemaVersion: 1 olmalı")
    if not _nonempty_string(feed.get("sourceId")):
        errors.append("sourceId: boş olmayan metin olmalı")
    try:
        _parse_datetime(feed.get("collectedAt"), "collectedAt", require_utc=True)
    except ValueError as exc:
        errors.append(str(exc))

    items = feed.get("items")
    if not isinstance(items, list):
        return errors + ["items: dizi olmalı"]

    seen_ids: set[str] = set()
    for index, item in enumerate(items):
        prefix = f"items[{index}]"
        if not isinstance(item, dict):
            errors.append(f"{prefix}: object olmalı")
            continue
        missing = sorted(ITEM_REQUIRED - set(item))
        if missing:
            errors.append(f"{prefix}: zorunlu alan eksik: {', '.join(missing)}")
            continue

        for name in ("id", "institution", "title"):
            if not _nonempty_string(item.get(name)):
                errors.append(f"{prefix}.{name}: boş olmayan metin olmalı")
        item_id = item.get("id")
        if _nonempty_string(item_id):
            if item_id in seen_ids:
                errors.append(f"{prefix}.id: aynı kaynakta yinelenen kimlik")
            seen_ids.add(item_id)

        if item.get("category") not in CATEGORIES:
            errors.append(f"{prefix}.category: izin verilmeyen kategori")
        if item.get("contentType") not in CONTENT_TYPES:
            errors.append(f"{prefix}.contentType: izin verilmeyen içerik türü")
        if item.get("freshnessStatus") not in FRESHNESS:
            errors.append(f"{prefix}.freshnessStatus: izin verilmeyen durum")
        if item.get("sourceStatus") not in SOURCE_STATUS:
            errors.append(f"{prefix}.sourceStatus: fresh/stale olmalı")
        if not _https_url(item.get("officialSourceUrl")):
            errors.append(f"{prefix}.officialSourceUrl: HTTPS resmî URL olmalı")

        apply = item.get("howToApply")
        if not isinstance(apply, dict):
            errors.append(f"{prefix}.howToApply: object olmalı")
        else:
            for name in ("method", "instructions"):
                if not _nonempty_string(apply.get(name)):
                    errors.append(f"{prefix}.howToApply.{name}: boş olmayan metin olmalı")
            if not _https_url(apply.get("url")):
                errors.append(f"{prefix}.howToApply.url: HTTPS URL olmalı")

        conditions = item.get("conditions")
        if not isinstance(conditions, list) or any(not isinstance(x, str) for x in conditions):
            errors.append(f"{prefix}.conditions: metin dizisi olmalı")
        tags = item.get("tags")
        if not isinstance(tags, list) or any(not isinstance(x, str) for x in tags):
            errors.append(f"{prefix}.tags: metin dizisi olmalı")

        deadline_date = None
        published_date = None
        if item.get("deadline") is not None:
            try:
                deadline_date = _parse_date_or_datetime(item["deadline"], f"{prefix}.deadline")
            except ValueError as exc:
                errors.append(str(exc))
        if item.get("publishedAt") is not None:
            try:
                published_date = _parse_date_or_datetime(item["publishedAt"], f"{prefix}.publishedAt")
            except ValueError as exc:
                errors.append(str(exc))

        try:
            collected_at = _parse_datetime(item.get("collectedAt"), f"{prefix}.collectedAt", require_utc=True)
        except ValueError as exc:
            collected_at = None
            errors.append(str(exc))
        try:
            _parse_datetime(item.get("firstSeenAt"), f"{prefix}.firstSeenAt", require_utc=True)
        except ValueError as exc:
            errors.append(str(exc))

        opens_at = None
        if item.get("applicationOpensAt") is not None:
            try:
                opens_at = _parse_datetime(item["applicationOpensAt"], f"{prefix}.applicationOpensAt")
            except ValueError as exc:
                errors.append(str(exc))

        status = item.get("freshnessStatus")
        content_type = item.get("contentType")
        if deadline_date is not None and deadline_date < today and status != "Süresi geçti":
            errors.append(f"{prefix}.freshnessStatus: geçmiş deadline 'Süresi geçti' olmalı")
        if content_type == "job" and status == "Doğrulandı":
            if deadline_date is None:
                errors.append(f"{prefix}.deadline: doğrulanmış iş için son başvuru tarihi gerekli")
            if published_date is None:
                errors.append(f"{prefix}.publishedAt: doğrulanmış iş için yayın tarihi kanıtı gerekli")
            if item.get("sourceStatus") != "fresh":
                errors.append(f"{prefix}.sourceStatus: stale ilan doğrulandı gösterilemez")
            if opens_at is not None and collected_at is not None and collected_at < opens_at:
                errors.append(f"{prefix}.applicationOpensAt: başvuru henüz açılmamış")
        if content_type == "job" and deadline_date is None and status != "Doğrulanamadı":
            errors.append(f"{prefix}.freshnessStatus: deadline yoksa iş doğrulanamaz")

    return errors


def preserve_first_seen(
    previous_items: list[dict[str, Any]],
    new_items: list[dict[str, Any]],
) -> list[dict[str, Any]]:
    """Keep firstSeenAt stable by item id; initialize only from collectedAt."""
    previous = {
        item.get("id"): item.get("firstSeenAt")
        for item in previous_items
        if isinstance(item, dict) and _nonempty_string(item.get("id"))
    }
    result = []
    for item in new_items:
        current = copy.deepcopy(item)
        item_id = current.get("id")
        if item_id in previous and _nonempty_string(previous[item_id]):
            current["firstSeenAt"] = previous[item_id]
        elif not _nonempty_string(current.get("firstSeenAt")):
            current["firstSeenAt"] = current.get("collectedAt")
        result.append(current)
    return result


def merge_source_items(
    previous_by_source: dict[str, list[dict[str, Any]]],
    fresh_by_source: dict[str, list[dict[str, Any]]],
    failed_sources: set[str],
) -> dict[str, list[dict[str, Any]]]:
    """A source failure marks only that source's last-good records stale."""
    merged = {}
    source_ids = set(previous_by_source) | set(fresh_by_source) | set(failed_sources)
    for source_id in source_ids:
        if source_id in failed_sources:
            records = copy.deepcopy(previous_by_source.get(source_id, []))
            for record in records:
                record["sourceStatus"] = "stale"
            merged[source_id] = records
        elif source_id in fresh_by_source:
            records = preserve_first_seen(
                previous_by_source.get(source_id, []),
                fresh_by_source[source_id],
            )
            for record in records:
                record["sourceStatus"] = "fresh"
            merged[source_id] = records
        else:
            merged[source_id] = copy.deepcopy(previous_by_source.get(source_id, []))
    return merged


def count_verified_open_jobs(
    feeds_by_source: dict[str, list[dict[str, Any]]],
    today: date,
) -> int:
    """Count only fresh jobs with publication, deadline and an already-open application."""
    count = 0
    for records in feeds_by_source.values():
        for record in records:
            if (
                record.get("contentType") != "job"
                or record.get("sourceStatus") != "fresh"
                or record.get("freshnessStatus") != "Doğrulandı"
                or record.get("deadline") is None
                or record.get("publishedAt") is None
            ):
                continue
            try:
                deadline = _parse_date_or_datetime(record["deadline"], "deadline")
                _parse_date_or_datetime(record["publishedAt"], "publishedAt")
                if deadline < today:
                    continue
                collected_at = _parse_datetime(record.get("collectedAt"), "collectedAt")
                opens_value = record.get("applicationOpensAt")
                if opens_value is not None:
                    opens_at = _parse_datetime(opens_value, "applicationOpensAt")
                    if collected_at < opens_at:
                        continue
            except (ValueError, TypeError):
                continue
            count += 1
    return count
