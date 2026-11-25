# Aries repository work rules

## Single source of truth
- Current task order and acceptance gates: `docs/HARITA.md` and `docs/NOT_C1.md` on branch `durum`.
- Every substantial task gets `gorevler/<id>.md` on `durum`; keep exact branch, commit SHA, CI links, test counts and decision.
- When continuing, choose the first `DEVAM` item, otherwise the first `YAPILMADI` item in HARITA.

## Scope and order
- Current allowed work stream: Haber doğrulama (H1) first, then İş & Kariyer C1.
- Do not change film/player or theme modules in this stream.
- No unrelated refactors while an exact-head CI run is in progress. Structural work is split into small PRs after H1/C1 gates permit it.

## QA roles
Run separate review passes for:
1. Architecture and file boundaries.
2. Data integrity, persistence and dates.
3. Security, permissions and source/robots policy.
4. Exact-head tests, CI and diff scope.

Only call these independent agents when an actual agent execution tool is available. Otherwise label the work as separate self-QA passes; never claim agents or an external reviewer ran when they did not.

## Change / merge gates
- No direct writes to `main`; use a task branch and PR.
- Re-read the exact PR diff and file list before merge.
- Merge only when exact-head CI is green, diff is limited to the task, self-QA/Hakem decision is ONAY, no unresolved blocker remains, and the prior task order permits it.
- No merge based on an older SHA's checks.
- Do not guess publication/deadline dates; preserve nulls when source evidence is absent.
- Respect robots.txt, no login/CAPTCHA bypass, no browser impersonation, no LinkedIn/Indeed, no SSL bypass.
- Never place tokens, CV, KPSS or e-Devlet data in code, logs, prompts or external review payloads.

## Tooling safety
- Do not run the current `denetle.py` until its Groq diff-upload behavior has been removed or explicitly reviewed. It transmits up to 12,000 characters of `.git/fark.diff` after a limited pattern scan.
- Prefer GitHub Actions, local deterministic tests and exact diff inspections. Do not invent CI, test or agent results.
- Keep source files in small, cohesive packages; no bulk move/delete without an inventory and a tested PR.
