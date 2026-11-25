# H1 — Android emulator window-focus failure — 2026-11-26

## KARAR
**H1 RED.** Exact-head APK/unit işi başarılı, fakat Android emulator instrumentation 4 testten 3'ünü pencere odağı alınamadığı için çalıştıramadı. UI testlerini atlamak, sayıyı düşürmek veya XML gate'i gevşetmek yok. PR #71 merge/deploy yapılmaz.

## Exact-head CI kanıtı
- PR: https://github.com/aries919-bot/Jarvis/pull/71
- HEAD: e390e05e1485717c3a07ce3af06db6b3840062f3
- APK/unit run #45712459742: **SUCCESS** — https://github.com/aries919-bot/Jarvis/actions/runs/45712459742
- Emulator run #45712459718: **FAILURE** — https://github.com/aries919-bot/Jarvis/actions/runs/45712459718
- Job: 139550204961; step 7 instrumentation FAILURE, step 8 XML gate FAILURE, step 9 artifact upload SUCCESS.
- Artifact #15069268534 — 951,757 bytes; SHA-256 f9d848088c2ad8ca49985c0aeb44279f2e94202a1b1ecb8e9069e09ba97628d7; https://github.com/aries919-bot/Jarvis/actions/runs/45712459718/artifacts/15069268534
- Downloaded artifact ZIP: h1-run-45712459718.zip; XML and per-test logcat independently read from the ZIP.

## XML independent recount
JUnit XML: TEST-emulator-5554 - 14-_app-.xml

| Test | Result | Failure |
|---|---|---|
| evidenceReviewAndCorrectionFlowPersistsPublicationDateAndAuditDetails | FAIL | RootViewWithoutFocusException |
| opensH1ActivityAndPersistsUnknownPublicationDateAsNull | FAIL | RootViewWithoutFocusException |
| sqliteLastGoodCopyRecoversCorruptPrimaryWithoutLosingPreviousRecord | PASS | — |
| displaysArchivedHumanReviewHistoryFromSqlite | FAIL | RootViewWithoutFocusException |
| **Total** | **4 tests /1 pass /3 fail /0 error /0 skipped** | XML gate exit 1 |

The three failures each waited 10 seconds for Espresso's root view window focus. DecorView state in each failure: has-window-focus=false, has-focus=false, is-layout-requested=false. The failure occurred before the corresponding text/check/click assertion could evaluate, so no assertion that the business rule itself is wrong is supported.

Job log output:
- Gradle: BUILD FAILED in 11m 27s; 69 actionable tasks.
- XML verifier: tests=4 failures=3 errors=0 skipped=0; expected 4; process exit 1.
- Artifact upload completed successfully.

## Device diagnostics
- adb devices: emulator-5554 remained device.
- sys.boot_completed=1, boot animation stopped.
- system_server PID at preflight and failure: 552 (no observed restart).
- Package service: found.
- /data/user/0: 7.7G total, 7.0G free (8% used); storage shortage not supported.
- App Activity log records Displayed com.cem.jarvis/.CareerNewsVerificationActivity; later WindowManager transition shows test package InstrumentationActivityInvoker/EmptyActivity brought to front and Jarvis task moved to back immediately before Espresso failure.
- System logs contain slow dispatch and monitor contention during window/activity transitions. This supports focus handoff / emulator scheduling as a plausible cause, not a proven single cause.

## Root-cause classification
- **Observed failure mechanism:** tested Activity root never received window focus at Espresso root-picking time.
- **Most likely area:** ActivityScenario/EmptyActivity focus handoff plus slow emulator system_server/window-manager activity transitions.
- **Not proven:** whether the initiating race is AndroidX lifecycle orchestration, emulator timing, or an interaction in the app's activity startup. Do not state app code is fault-free until a clean test passes.
- Storage exhaustion, Package Manager missing, emulator disconnect, and system_server restart are not supported by this run's diagnostics.

## External technical evidence
Google's AndroidX commit dated 18 September 2026 documents the same failure pattern on low-end/OEM devices: after ActivityScenario transitions that cycle through EmptyActivity, window focus may not be restored immediately without touch input. Their test helper removes the hasWindowFocus() precondition when the subsequent click is executed directly on the UI thread with View.performClick(), and waits by checking view readiness/enabled state. This is analogous evidence, not proof that Aries has the identical root cause:
- https://android.googlesource.com/platform/frameworks/support/+/36c1b8edfc954907a546e09c7f42e20d39ecb083
- Commit message explicitly notes EmptyActivity transition and lost focus: same URL, lines 33–49.

## Next controlled fix and QA
1. Change only the H1 instrumentation harness, not app production logic, first: inspect replacing Espresso's focus-dependent root picking/clicks with a deterministic ActivityScenario onActivity/main-thread view readiness and direct View.performClick() path, following the AndroidX pattern. Preserve all four tests and their assertions.
2. Capture the target Activity lifecycle state, hasWindowFocus, current top-resumed activity, and WindowManager focus immediately before each UI action. Do not treat RESUMED as proof of window focus.
3. Keep an explicit positive focus/UI test where feasible; if direct programmatic view clicks are used, also keep a device test for real tap/focus behavior so the harness fix does not erase user-interaction coverage.
4. No retries of the same failed head as a substitute for a fix. After a narrow test-only change, run exact-head APK/unit and emulator; re-read XML and recount 4 tests /0 failures /0 errors /0 skipped.
5. PR #71 remains draft/open; no merge/deploy until exact-head tests and real-phone verification pass.

## Scope and diff
The current PR #71 contains the H1 change set; this task note adds no application-code changes. No token, secret, profile, CV/KPSS/e-Devlet personal data, robots bypass, or new network access was introduced.
## Sonraki PR HEAD — sistem başlatıcı odak kurtarma denemesi
- PR #71 head artık b51c53a5797c19d4e18ed81b679024fc14fa3723; önceki e390e05e1485717c3a07ce3af06db6b3840062f3 başarısız koşusunun sonucu hâlâ geçerlidir, yeni head henüz PASS değildir.
- Yeni commit scripts/run-h1-emulator-tests.sh içine window/activity/process dumpsys kanıtı ve instrumentation öncesi pencere odağı preflight'ı ekledi; app ve androidTest APK'ları hazırlandıktan sonra Application Not Responding/Application Error penceresi taranıyor, varsa sistem iletişim penceresi kapatılıp HOME gönderiliyor. Eski üç UI testini atlamaz, XML gate'i değiştirmez.
- Eski artifact logcat'inde 22:44:02'de NexusLauncherActivity için Activity top resumed state loss timeout ve Activity pause timeout kayıtları görüldü; bu, launcher başlangıç/odak sorunu hipotezini güçlendirir. Eski artifact'te o ana ait dumpsys window snapshot bulunmadığı için ANR dialog bulunduğu kesin söylenemez.
- Yeni exact-head CI: H1 emulator #45716694004 IN_PROGRESS (job #139564450544; ilk 6 setup/dependency step SUCCESS, step 7 instrumentation/preflight çalışıyor; XML/artifact henüz yok): https://github.com/aries919-bot/Jarvis/actions/runs/45716694004
- Yeni APK/unit #45716694105 IN_PROGRESS (unit test step SUCCESS, APK build çalışıyor): https://github.com/aries919-bot/Jarvis/actions/runs/45716694105
- Yeni run artefact listesi son sorguda boştu; log endpoint henüz BlobNotFound döndürdü. Bu, PASS/FAIL değil; yeni CI'nın gerçek sonucunu bekleyen bir durum.
- Google AndroidX 18.09.2026 EmptyActivity/focus düzeltmesi analog araştırma notu olarak kalır. Yeni PR çözümünün etkisi sadece yeni head XML ve preflight window/activity diagnostics ile belirlenecek.
**Güncel KARAR:** H1 hâlâ RED/beklemede; #45712459718 failure kapandı, yeni b51c53a runları in_progress. Merge/deploy yok.