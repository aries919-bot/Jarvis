# C1 H2 resmî kaynak entegrasyonu — 25.11.2026

## KARAR
**RED — PR açık/taslak; birleştirme ve production deploy yok.** Kod/test kapıları geçti. Global kabul, 10 kaynak /100 deadline-doğrulanmış açık ilan ve production authenticated GET şartları nedeniyle reddedildi.

## PR ve diff
- PR: https://github.com/aries919-bot/jarvis-cloud/pull/9
- Head: `fix/c1-h2-official-feed`, SHA `8a43b99981db601de3442e8394b71d0651d0e34e`
- Base: `fix/c1-source-category-after-timestamp`, SHA `68e858424c3589b7d6cffea7ba26c7941027b017` (PR #8 tabanı)
- PR state: açık/taslak, mergeable true, merged false.
- Diff: 5 dosya / +442 / -10 / 22.465 bayt; izin listesi tam eşleşti; 60.000 bayt altı; added-line forbidden pattern 0; UA exact; diff-check temiz.
- Dosyalar: `.github/workflows/validate.yml`, `scripts/career-feed-live.mjs`, `src/career-feed-store.js`, `src/h2-input.js`, `tests/h2-input.test.mjs`.
- Formal GitHub review: 0. PR konuşmasındaki bot yorumları bağımsız insan/Claude onayı değildir.

## Değişiklik
- H2 okuyucusu measured/pinned commit `ae51188ff2bb7c55ef813be7205825b0c5b05db0` üzerinden Actions'ta çalıştırılır; pypdf 5.4.0 sabitlenmiştir.
- H2 source JSON'ları C1 şemasına eşlenir. publishedAt/deadline/collectedAt/firstSeenAt, resmi kaynak URL, nasıl başvurulur, şartlar, category, applicationOpensAt alanları korunur.
- deadline bilinmiyorsa open sayılmaz; geçmiş deadline job güncel listeden çıkar; applicationOpensAt gelecekteyse ATK açık ilan değildir; Kura/sonuç yeni iş sayılmaz.
- C1 Worker geçmişi yokken H2'nin kaynak bazlı stale last-good kaydı tutulur; Worker geçmişi varsa önceliklidir.
- `Kamu bağlı şirket` public gate altında sayılır; 0 public source/listing RED.
- H2 mapping, future-open, stale first-last-good, Kura hariç tutma ve public-zero RED testleri eklendi.

## Exact-head CI ve komut kanıtı
[Public exact-SHA Hakem Actions #45517626605](https://github.com/aries919-bot/Jarvis/actions/runs/45517626605)
- Preflight: exact private SHA checkout PASS; artifact checkedOutSha `8a43b99981db601de3442e8394b71d0651d0e34e`.
- Komut `npm run test:c1`: **46 test / 46 PASS / 0 FAIL / 0 skipped**.
- Komut `node scripts/c1-worker-d1-smoke.mjs`: **11/11 PASS**, local Wrangler + local D1; production değildir.
- H2 parser + last-good seed: Actions step SUCCESS.
- Failure matrix: **17/17** kapsam PASS.
- e-Şube data contract: PASS, 42 raw/42 unique/42 open/0 expired, snapshot age 0.52 h.
- Security/diff: PASS, five exact files /22.465 bytes, forbidden-pattern findings 0, user-agent exact.
- H2 live reader and C1 collector command: `node scripts/career-feed-live.mjs`; exit 0 / PASS. Feed collectedAt `2026-11-25T00:25:02.241Z`.
- Hakem report: `qa-reports/c1/latest.json`, report collectedAt `2026-11-25T00:25:12.024883+00:00`, decision RED.
- Private repo Actions run #45517394728 runner_id=0/steps=[]: code command never began; this is infrastructure issue. Exact code tests were independently run on public runner above.

## Source-by-source recount

### Private ATS — fresh candidates, all deadlines unknown
| Kaynak | Türkiye aday |
|---|---:|
| Trendyol | 35 |
| Midas | 16 |
| Commencis | 5 |
| Dream Games | 17 |
| Aleph | 5 |
| Ajax Systems | 13 |
| Insider One | 22 |
| Binance | 0 |
| Picus Security | 1 |
| **Toplam** | **114 (114 unknown deadline; 0 verified open)** |

Stale private boards: Bold Games, fal, The Global Talent Co., Sensor Tower, XDS, Cambly — each robots HTTP 401; their API endpoints were not requested and are not considered zero-vacancy sources.

### Kamu/H2 source results
| Source | Fresh/stale | Reader cards | Open verified jobs | Reason |
|---|---|---:|---:|---|
| İŞKUR e-Şube | fresh | 42 | 42 | JSON snapshot 2026-11-24T23:51:48Z; 42 unique/open |
| YHGM | fresh | 1 | 0 | Historical Kura/result; not a job |
| BOTAŞ | stale | 0 | 0 | Two official-page timeouts; last-good stays isolated |
| Adli Tıp Kurumu | fresh | 1 | 0 | Application opens 30.11.2026 09:00 +03; not yet open |
| Anadolu Üniversitesi | fresh | 1 | 1 | deadline-confirmed public vacancy |
| Düzce Üniversitesi | fresh | 1 | 1 | deadline-confirmed public vacancy |
| Tarsus Üniversitesi | fresh | 1 | 1 | deadline-confirmed public vacancy |
| Marmara Üniversitesi | fresh | 1 | 1 | deadline-confirmed public vacancy |
| ÇOMÜ | stale | 0 | 0 | robots UNKNOWN HTTP 200; target page not requested |
| Zafer Kalkınma Ajansı | fresh | 1 | 1 | deadline-confirmed public vacancy |
| Bilecik Şeyh Edebali | stale | 0 | 0 | Resmî Gazete robots timeout; target not requested |

H2 totals: 10 source definitions /7 fresh /3 stale; 7 source cards; 5 open verified jobs. e-Şube + H2 produce one Actions feed with **6 public sources /47 verified open job cards**. Position quotas do not multiply card count.

### C1 live collector totals
- Source definitions: 26 (15 private ATS + e-Şube + 10 H2).
- Fresh/stale: 17/9.
- All fresh records: 163.
- Verified open jobs: **47**.
- Unknown-deadline/non-open records: 116.
- Expired listings: 0.
- Public verified: **6 sources /47 listings**.
- Threshold: required 10 sources /100 verified open jobs; actual **6/47 — RED**.

## Production / last-good boundary
- Production authenticated GET: **BLOCKED**, reason `JARVIS_WORKER_URL/JARVIS_WORKER_TOKEN` pair not configured.
- `PUBLISH status=SKIPPED`; Worker POST and deployment were not attempted. Local D1 PASS is not production proof.
- No proof that the integrated C1 feed is stored in production D1 or fetched from Android. This PR integrates the Actions collector, not an already-live deployed Worker.
- 6 private robots 401 + H2 BOTAŞ timeout/ÇOMÜ robots UNKNOWN/Bilecik robots timeout remain source-specific stale; no robots bypass or API request was performed.

## Eksik / next steps
1. Keep PR #9 open/draft until independent review.
2. Production Worker authenticated GET must be configured through GitHub settings, without exposing secret values; GET-only QA must succeed.
3. Global 10/100 requirement still needs at least 4 more verified open sources and 53 more verified open listings, with deadlines/publication evidence. Current public 6/47 does not pass.
4. Re-run exact-head Hakem after any changes and recount every source.
5. No Android UI integration until threshold and production gates pass. No PR merge without user's explicit "birleştir".
