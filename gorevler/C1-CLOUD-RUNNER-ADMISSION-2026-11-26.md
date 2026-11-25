# C1 — GitHub Actions runner admission blocker
Date: 2026-11-26
Scope: Worker/C1 validation and scheduled public feed collection.

## Evidence
- Cloud PR #12 latest recorded run [#45697424481](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45697424481), job 139498357281: conclusion=failure, runner_id=0, runner_name empty, steps empty/null, runtime about 2 seconds. Job log retrieval returns BlobNotFound.
- Workflow `.github/workflows/validate.yml` declares `runs-on: ubuntu-latest`, timeout 15m. Its first step is checkout; no checkout/setup/test steps were recorded.
- Multiple preceding runs in this repository show the same pre-step runner admission fingerprint.
- This is NOT a source parser/unit-test failure. It does not establish the precise GitHub/account cause.

## External research
GitHub issue search found multiple independent repositories with the same fingerprint:
- “Unblock GitHub Actions CI: jobs fail before recording any steps” — ubuntu-latest, runner_id=0, steps empty, BlobNotFound; cause not established.
- “BLOCKER: GitHub-hosted Actions never allocates runner (runner_id=0, even zero-dependency smoke)” — even a zero-dependency echo workflow failed before runner assignment; owner-side Actions policy/billing/runner eligibility checks required.
- “GitHub Actions jobs fail before runner startup (runner_id 0, no steps/logs)” — independent ubuntu-latest smoke job with same symptom.
These reports support the failure class (runner admission/dispatch), not a specific account billing or GitHub outage diagnosis.

## Workflow-specific impact
- PR validation (pull_request) runs syntax/unit tests and local Worker/D1 smoke, but intentionally skips upstream e-Şube clone, live H2 source reads and live Worker audit.
- Scheduled/push runs do the upstream Jarvis/durum clone, H2 read, live source audit and production Worker GET/D1 checks.
- Because jobs fail before steps, no scheduled refresh/publish evidence exists. Do not infer the latest source input has reached production.
- H2 reader separate live run #45646698179 succeeded: 11 institutions /13 open cards /125 positions; e-Şube separate input 42 unique rows. Neither is proof of production Worker integration.
- Worker last-good remains 6 sources /47 cards, 6/6 stale; production authenticated GET/D1 not proven.

## Next action (owner-side, no code retry loop)
1. In GitHub repository Settings → Actions → General, verify Actions are enabled and GitHub-hosted runners are allowed.
2. Check organization-level Actions policy, runner group restrictions/required approvals and private repository hosted-runner eligibility.
3. Check Actions usage/billing/concurrency limits for the repository/organization.
4. Inspect the run-level GitHub error annotation/banner in the web UI; copy the exact message into this task. Connected API cannot retrieve a runner log because no runner/step was allocated.
5. Only after owner-side policy/eligibility is checked, run one zero-dependency ubuntu-latest echo workflow as a controlled admission probe. Do not repeatedly rerun the same source workflow.
6. Once runner assignment is restored, run the scheduled exact-head source audit; require per-source fresh/stale counts, H2/e-Şube cross-feed dedup, authenticated Worker GET/D1 and artifact evidence before C1 PASS.

## Security / no workarounds
- Do not switch to unknown third-party/self-hosted runners, paste secrets, bypass account policy, disable TLS, or scrape login-gated pages.
- No deploy or merge while this blocker remains.

**KARAR:** C1 Cloud Actions is BLOCKED before user code. The exact runner-admission cause is UNKNOWN; code tests are NOT_EXECUTED in these failed runs.