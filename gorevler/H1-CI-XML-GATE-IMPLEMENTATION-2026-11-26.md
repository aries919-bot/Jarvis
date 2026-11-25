# H1 XML gate implementation and scoped progress — 26.11.2026

- collectedAt: 2026-11-25T21:47:27.094Z
- Scope: only H1 Haber doğrulama and C1 İş & Kariyer. Film/theme/stock are outside this report.
- H1 PR: https://github.com/aries919-bot/Jarvis/pull/71
- Current PR head: `4398073b3759109fa27a8172956cbea13f31ce27`
- Branch: `feat/h1-news-store-ui-20261125`
- No merge, no deployment.

## Implemented on PR #71

1. Added `scripts/run-h1-emulator-tests.sh`; strict shell options and Gradle|tee pipeline execute in the same Bash process.
2. Changed runner input to a single shell command: `bash scripts/run-h1-emulator-tests.sh`. This avoids the pinned emulator action splitting the multiline script into separate shells.
3. Added `scripts/verify_h1_emulator_xml.py`; it requires XML output, counts all testcase/failure/error/skipped nodes, expects exactly 4 tests and exits non-zero for missing/malformed XML, any failure/error/skip, or wrong test count.
4. Added an `if: always()` XML validation step before artifact upload, so the test result cannot be hidden by a successful `tee` or a green action wrapper.

### Exact write evidence
- `scripts/run-h1-emulator-tests.sh` commit: `173902ab99b0a267877bf79fe826cfe34eb8fac3`
- `scripts/verify_h1_emulator_xml.py` commit: `621c57640389221950ade7c6be08ef2e9eee4970`
- `.github/workflows/h1-android-ui-smoke.yml` commit: `4398073b3759109fa27a8172956cbea13f31ce27`
- PR API recount: 14 files / +3,370 / -26. Exact changed filenames re-read; the three additions are the workflow and two scripts.
- Latest PR metadata: open, draft, mergeable=true, not merged.
- Exact-head Actions lookup after commit `4398073...`: no run found yet. Therefore the implementation is committed to the PR branch but **not CI-verified**. Do not call the false-green fix accepted until a run for this exact SHA executes.
- Previous exact head `8a52e21...`: APK SUCCESS; emulator GitHub SUCCESS contradicted by XML (4 tests / 3 failures). That prior XML remains a failure and is not superseded by the unrun workflow patch.

## Remaining H1 blockers
- Re-run exact new head and inspect raw XML; require 4/4 pass and CI result consistent with XML.
- Fix three `RootViewWithoutFocusException` failures; no skip/assumption workaround.
- Real Android phone test.
- Only then consider PR #71 review/merge.

## Scoped progress estimate — not whole Aries

A reproducible high-level estimate over the two currently authorized workstreams:

| Workstream | Estimate | Basis |
|---|---:|---|
| H1 Haber doğrulama | 50% | Claim/evidence/review-history model, SQLite store, Activity/UI and deterministic QA code exist; APK build passed on prior exact head. Current emulator XML had 3/4 failures; new CI gate is committed but has no exact-head run; physical phone and merge are outstanding. |
| C1 İş & Kariyer | 40% | Official source policy PR #70 merged; source research and 47-card last-good feed exist. Worker feed coverage remains 6/10 sources and 47/100 cards; the last measurement is stale. City-grounded dedup, production authenticated GET/D1, QA/RAG CI and Android integration remain blocked. |
| **Combined scoped estimate** | **45%** | Simple equal weighting of H1 and C1 (50% + 40%) / 2. This is a planning estimate, not a measured product-completion metric. |

This percentage does not represent the full Aries application. No reliable whole-app denominator is present in the current H1/C1 acceptance docs, so claiming a whole-Aries percentage would be misleading.

## Next actions
1. Obtain an exact-head Actions run for `4398073...`; verify XML gate itself with both a valid 4/4 report and a deliberately failing/missing report.
2. Fix the UI focus failures without skipping tests.
3. Re-run six stale public sources independently; preserve each source's last-good snapshot and refresh only the source that succeeds.
4. Add new official candidates only after sourceId, city and exact-duplicate checks.
5. Solve Worker production GET/D1 and the Cloud PR #12 runner-start issue; no merge/deploy until acceptance gates pass.

**Decision:** H1 RED pending exact-head CI + 4/4 XML + phone; C1 RED pending freshness, 10/100, city dedup and production Worker. Scoped progress estimate: **45%**.
