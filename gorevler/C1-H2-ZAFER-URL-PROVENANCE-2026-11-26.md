# C1 — H2 Zafer source URL provenance check
Tarih: 26.11.2026
Scope: H2 official public source feed only. No production mutation.

## Finding
The H2 configuration used by Cloud is in Jarvis PR #61 branch, pinned commit `fe37be93202442394504f455b4a1caeb31f298cf`, file `career/h2_sources.json`.
The `zafer-personel-2026-11` entry has:
- `institution`: Zafer Kalkınma Ajansı
- fetch `url`: `https://www.doka.org.tr/announcement-detail.php?id=1222`
- `officialUrl`: `https://www.zafer.gov.tr/duyurular/227`
- `pdfUrl`: DOKA domain asset `announcement_1222`
- `expectedText`: Zafer Kalkınma Ajansı / 2 Uzman / 14.12.2026
- application URL: Zafer official page.
The DOKA source is a separate row, id `doka-personel-2026-11`, fetch URL id=1221.

## Why not change blindly
H2 live reader run [#45646698179](https://github.com/aries919-bot/Jarvis/actions/runs/45646698179) marked Zafer as a verified open card (the 11-source/13-card report includes Zafer). This means the DOKA-hosted URL may be a mirror/cross-post containing the Zafer announcement, while the user-facing `officialUrl` is the correct Zafer page. The hostname mismatch alone does not prove the parser is wrong, but source provenance must be checked before changing it.

## Required next check
1. Inspect H2 artifact source report for `zafer-personel-2026-11`: fetched URL, final officialSourceUrl, parsed institution, publishedAt, deadline, sourceStatus, HTTP status.
2. Compare the DOKA-hosted source content against the official Zafer page/PDF. If the DOKA page is only a mirror, prefer fetching the official Zafer page/PDF if it exposes the same measurable fields and robots policy allows it.
3. Add a regression fixture: Zafer source must not be accepted if institution/title/official URL does not match; it must not silently become DOKA.
4. Keep the public card's official link at `https://www.zafer.gov.tr/duyurular/227`.
5. Re-run H2 60 tests + Python script suite and a fresh live reader artifact; report source counts separately.

## Current measured baseline
- H2 run #45646698179: 19 announcement configs, 17 fresh /2 stale; 11 unique institutions /13 verified open cards /125 positions. Zafer counted as one of the 13 open cards.
- e-Şube: separate 1 source /42 unique open listing rows, collectedAt 2026-11-25T13:49:08Z; 42 all deadline 27.11.2026; 7 employers.
- No cross-feed dedup or production Worker GET/D1 proof; do not combine these into a live feed count.
- Worker last-good public feed remains 6 sources /47 cards, all 6 stale.

**KARAR:** URL provenance mismatch is a review item, not yet a proven wrong-source bug. Do not change the config until artifact/source comparison is completed.
