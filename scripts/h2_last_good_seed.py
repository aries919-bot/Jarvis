#!/usr/bin/env python3
"""Bootstrap H2 last-good feeds and merge immutable first-seen timestamps."""
from __future__ import annotations

import argparse
import json
import re
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

SOURCE_ID = re.compile(r"^[a-z0-9][a-z0-9-]{0,79}$")


def read_feed(path: Path) -> dict[str, Any] | None:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, ValueError, TypeError):
        return None
    return value if isinstance(value, dict) else None


def timestamp(value: Any) -> datetime | None:
    if not isinstance(value, str) or not value:
        return None
    try:
        parsed = datetime.fromisoformat(value.replace("Z", "+00:00"))
    except ValueError:
        return None
    if parsed.tzinfo is None:
        return None
    return parsed.astimezone(timezone.utc)


def valid_last_good(feed: dict[str, Any], source_id: str) -> bool:
    return (
        feed.get("schemaVersion") == 1
        and feed.get("sourceId") == source_id
        and feed.get("sourceStatus") == "fresh"
        and isinstance(feed.get("items"), list)
        and len(feed["items"]) > 0
    )


def write_feed(path: Path, feed: dict[str, Any]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(feed, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def seed_last_good(seed_path: Path, previous_dir: Path) -> dict[str, int]:
    seed = read_feed(seed_path)
    if not seed or seed.get("schemaVersion") != 1 or not isinstance(seed.get("sources"), list):
        raise ValueError("H2 last-good seed manifest invalid")

    previous_dir.mkdir(parents=True, exist_ok=True)
    counts = {"copied": 0, "first_seen_merged": 0, "kept_current": 0, "skipped": 0}
    for historical in seed["sources"]:
        if not isinstance(historical, dict):
            counts["skipped"] += 1
            continue
        source_id = historical.get("sourceId")
        if not isinstance(source_id, str) or not SOURCE_ID.fullmatch(source_id):
            counts["skipped"] += 1
            continue
        if not valid_last_good(historical, source_id):
            counts["skipped"] += 1
            continue

        target = previous_dir / f"{source_id}.json"
        current = read_feed(target) if target.is_file() else None
        if not current or not valid_last_good(current, source_id):
            write_feed(target, historical)
            counts["copied"] += 1
            continue

        historical_first = {
            item.get("id"): item.get("firstSeenAt")
            for item in historical["items"]
            if isinstance(item, dict) and isinstance(item.get("id"), str)
        }
        changed = False
        for item in current["items"]:
            if not isinstance(item, dict):
                continue
            old_seen = timestamp(historical_first.get(item.get("id")))
            current_seen = timestamp(item.get("firstSeenAt"))
            if old_seen and (current_seen is None or old_seen < current_seen):
                item["firstSeenAt"] = historical_first[item["id"]]
                counts["first_seen_merged"] += 1
                changed = True
        if changed:
            write_feed(target, current)
        else:
            counts["kept_current"] += 1

    return counts


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--seed", type=Path, required=True)
    parser.add_argument("--previous-dir", type=Path, required=True)
    args = parser.parse_args()
    counts = seed_last_good(args.seed, args.previous_dir)
    print("H2 last-good seed: " + json.dumps(counts, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
