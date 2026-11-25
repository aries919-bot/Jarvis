# C1 H2 şehir alanı kök neden denetimi — 25.11.2026

## Exact source evidence
- H2 reader repository: `aries919-bot/Jarvis`.
- Pinned reader commit: `fe37be93202442394504f455b4a1caeb31f298cf`.
- Reader file: `scripts/h2_announcement_reader.py`.
- Config file: `career/h2_sources.json`.
- Config schema: `schemaVersion=1`, `sources=19`.
- Source catalog field recount: `city`/ `location` alanı olmayan kayıt: **19/19**.
- Reader `make_item(...)` signature has no city/location argument; returned JSON object has no `city`/ `location` property. `parse_official_notice()` calls this constructor without location evidence. This is the upstream cause; changing only cloud `src/h2-input.js` cannot recover a location the reader never emits.
- Pinned tree includes `scripts/test_h2_announcement_reader.py`; reader's current deterministic tests should receive location extraction and null fallback cases.

## Consequence
- C1 cloud mapper `src/h2-input.js` currently maps H2 city as null. With no upstream city field, same-institution/position/city/deadline exact dedup cannot run.
- Existing duplicateCount=0 / possibleDuplicateGroupCount=0 output cannot be interpreted as proof that there are no cross-feed duplicates. The exact key lacks location evidence.
- Do not invent city from company name, source hostname, or institution headquarters. Jobs may have position-specific campuses, districts or province spread.
- The public H2 catalogue is 19 configured source records. This is NOT 19 fresh public institutions or 19 open listings; archival records, repeated institutions and upcoming announcements are included. Current public feed remains 12 sources /55 verified open listings.

## Safe change design
1. Add optional `city` or a structured location evidence object to H2 items. Default stays null.
2. Populate only if the official source page/PDF has an explicit work-location field tied to the position row, or the official vacancy page explicitly identifies the duty location. Institution office address is insufficient unless the notice says that is the duty location.
3. Multi-city or per-position tables require either splitting positions into separate records or preserving a location array; don't assign one source-wide city to multiple postings without evidence.
4. Record the exact source fragment/label and official URL that supports the location. Keep original source URL.
5. Add reader tests for: explicit single city; multiple cities; missing field remains null; title or address without job-site label does not infer a city; city differs across position rows.
6. Update Cloud H2 mapper and tests to pass through explicit city only, and make dedupe classification conservative. Missing city -> possible duplicate / not in verified unique card threshold until resolved.
7. Keep per-source stale/last-good behavior unchanged; robots gates and truthful UA remain unchanged.

## Recommended sequencing
- Phase A: audit each source record by official source/PDF and record whether the vacancy itself states a duty city, not just the institution city.
- Phase B: add extraction for only proven sources, separate tests, Actions run, exact-head review.
- Phase C: re-run cross-feed dedup and recount public unique listings. Only then revise the 55/100 counter.
- Keep public and private counts separate; do not infer or synthesize the missing 45.

## KARAR
**RED / implementation blocked pending city evidence.** A source-level defect is proven (19/19 catalog records omit city; upstream reader has no city output). No code changed. No new listings counted. The public threshold remains 12 sources /55 verified listings, 45 short of 100.
