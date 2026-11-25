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
## 26.11.2026 — b51c53a exact-head APK result
- APK/unit run #45716694105 is now **SUCCESS** on exact PR HEAD b51c53a5797c19d4e18ed81b679024fc14fa3723: https://github.com/aries919-bot/Jarvis/actions/runs/45716694105. All unit/build/upload steps passed.
- APK artifact #15069888409 (name aries-apk), archive digest sha256:8f98ae33b42003beb70851e5b596e0b6354f7d24559b9585219e1f0ae46fbb79; downloaded app-debug.apk is 14,960,717 bytes, SHA-256 96ba976ec3f341d51d403633246e96b74c90228de64ac270b9a96058e3d925ab.
- Emulator run #45716694004 remains IN_PROGRESS; job #139564450544, step 7 instrumentation/preflight still running; XML/artifact list empty. No emulator PASS/FAIL yet.
- APK build success is separate from H1 emulator criterion. No merge/deploy until current exact-head emulator XML has 4 tests /0 failures /0 errors /0 skipped and real-device checklist is completed.

## 26.11.2026 — b51c53a exact-head emulator sonucu: instrumentation timeout

### Sonuç
PR #71 HEAD `b51c53a5797c19d4e18ed81b679024fc14fa3723` için:
- APK/unit workflow [#45716694105](https://github.com/aries919-bot/Jarvis/actions/runs/45716694105): **SUCCESS**.
- Emulator workflow [#45716694004](https://github.com/aries919-bot/Jarvis/actions/runs/45716694004): **FAILURE**.
- Job `139564450544`: setup/dependency adımları 1–6 SUCCESS; step 7 `Run Android emulator instrumentation tests` FAILURE; step 8 XML doğrulama FAILURE; step 9 artifact upload SUCCESS.
- Emulator artifact: [#15070859325](https://github.com/aries919-bot/Jarvis/actions/runs/45716694004/artifacts/15070859325), 570,324 bytes, SHA-256 `2f4a7e0b9b500cc6dacaabe3235a61202f9b1c10364d967ff86080936625e94c`.
- APK artifact: [#15069888409](https://github.com/aries919-bot/Jarvis/actions/runs/45716694105), archive digest SHA-256 `8f98ae33b42003beb70851e5b596e0b6354f7d24559b9585219e1f0ae46fbb79`; APK dosyası SHA-256 `96ba976ec3f341d51d403633246e96b74c90228de64ac270b9a96058e3d925ab`.

### Logdan yeniden doğrulanan olay sırası
1. Android boot logu `Boot completed in 997518 ms` kaydı verdi.
2. Grafik/emülatör belirtileri: `Failed to find ColorBuffer: 18`, `Failed to bind to texture: invalid color buffer` ve `Failed to start Emulator console for 5554`.
3. Android framework/system_server ve Package Manager hazır kontrolleri geçti.
4. Window-focus preflight açıkça **PASS** verdi: odakta sistem ANR/Application Error penceresi yoktu.
5. Uygulama ve androidTest APK derlemesi `BUILD SUCCESSFUL` oldu (68 görev, 3m 12s).
6. `gradle connectedDebugAndroidTest` başladı; GitHub Actions adımı 30 dakika zaman aşımıyla sonlandı.
7. `app/build/outputs/androidTest-results/connected` altında instrumentation XML bulunmadı; XML doğrulayıcı exit 2 verdi.

### Hakem sınıflandırması
- Önceki `RootViewWithoutFocusException` teşhisi bu yeni koşunun sonucu değildir; önceki run #45712459718 tarihsel FAILURE olarak kalır.
- Yeni koşuda dört testin geçtiğine veya uygulama iddialarının başarısız olduğuna dair XML kanıtı yoktur. Test sayısı **0 raporlanmış sonuç**; bu, 4 testin sıfırının başarısız olduğu anlamına gelmez.
- Kanıtlanan yeni mekanizma: emülatör/ADB/Gradle instrumentation çalıştırma hattı `connectedDebugAndroidTest` aşamasında zaman aşımına uğradı.
- ColorBuffer ve EmulatorConsole kayıtları emülatör altyapısı şüphesini güçlendirir; tek başına kesin kök neden kanıtı değildir. Pencere odağı preflight PASS olduğundan aynı focus kurtarma değişikliğini tekrar etmek gerekçesizdir.

### Tekrarlı döngüyü önleyen sonraki adım
- Aynı konfigürasyonla körlemesine workflow rerun yapılmayacak.
- Bir sonraki H1-CI işi, uygulama kodunu değiştirmeden, emülatör bağlantısı ve instrumentation başlatma safhasını ayıran tanı kanıtı üretmelidir: `adb devices -l`, `getprop sys.boot_completed`, Package Manager yanıtı, kurulu test APK/runner kaydı, `dumpsys activity instrumentation`, ilgili logcat ve instrumentation komutunun başlangıç/son durumları.
- Tek kontrollü emülatör denemesinde ilk testin gerçekten başlayıp başlamadığı ayrıştırılır; testler atlanmaz, dört testlik XML kapısı gevşetilmez.
- PR #71 draft/open kalır. Bu run için emülatör PASS yok; formal GitHub review yok; merge/deploy yok.


## 26.11.2026 — kontrollü instrumentation başlatma tanısı eklendi
- Hedef: aynı timeout'u körlemesine tekrarlamak yerine, `connectedDebugAndroidTest` sırasında ADB/emülatör durumunun ve test runner'ın gerçekten başlayıp başlamadığının zaman çizelgesini toplamak.
- PR #71 mevcut H1 dalında yalnız iki CI dosyası güncellendi; üretim Kotlin kodu ve dört instrumentation testi değiştirilmedi:
  - `scripts/run-h1-emulator-tests.sh`: instrumentation komutundan önce başlangıç işareti; komut çalışırken yaklaşık 90 saniyede bir `adb devices -l`, `sys.boot_completed`, Package Manager, `pm list instrumentation`, `dumpsys activity instrumentation`, Activity/window state, system_server PID ve app/test APK path snapshots; eşzamanlı canlı logcat; komut sonu işareti ve son durum tanısı.
  - `.github/workflows/h1-android-ui-smoke.yml`: iki tanı betiği path filtresine eklendi; böylece yalnız betik değişikliği de H1 workflow'unu tetikler.
- Değişiklik commitleri: Commitler: script `3039a965bfec674602a08bbeb5956bccfa68a814`; workflow path filtresi `62b57ba38ef37a01ebb0dab18315b79e2d86bf9c`; printf tanı çıktısı düzeltmesi `65095a5420d80b6c8cbf9a48eb36f3a5d803fcf6`. PR head API'den yeniden okundu: `65095a5420d80b6c8cbf9a48eb36f3a5d803fcf6`.
- Exact-head Actions [emulator #45723036152](https://github.com/aries919-bot/Jarvis/actions/runs/45723036152) ve [APK/unit #45723036154](https://github.com/aries919-bot/Jarvis/actions/runs/45723036154) son kontrolde in_progress; sonuç/artefakt henüz yok. Önceki head runları bu yeni HEAD için kanıt değildir.
- Önceki #45716694004 logunda build SUCCESSFUL, window-focus preflight PASS, fakat `connectedDebugAndroidTest` 30 dakika timeout; XML yok. Bu nedenle mevcut test sonuç sayısı 0 raporlanmıştır; dört test PASS/FAIL diye sayılmaz.
- Bu değişiklik bir tanı enstrümantasyonudur; emülatör kaynaklı olduğu kesinleşmiş değildir. Dört test, XML 4/4 kapısı, timeout sınırı ve güvenlik politikası değişmedi.
**KARAR:** H1 RED/BLOCKED; yeni tanı CI'sı tamamlanana ve XML bağımsız yeniden sayılana kadar ONAY/merge/deploy yok.


## 26.11.2026 — CI tanı kapsamı sertleştirildi; GENEL_DUZELTME_PLANI.md eşlemesi
- `docs/GENEL_DUZELTME_PLANI.md` §9 önerileri kontrol edildi: logcat ve ekran görüntüsü artefaktı H1 tanısına uygulanabilir; izin otomasyonu ve HoloView animasyon kapatma bu H1 ekranı için gerekli değil (CareerNewsVerificationActivity özelinde ilgili runtime izni yok; HoloView bu activity akışında kullanılmıyor). Smoke testi yalnız package açılışına indirgenmedi; mevcut dört H1 UI/SQLite testi korunuyor.
- Önceki tanı koşusu #45723036152, yeni script commit'i geldiğinde hâlâ `in_progress` idi; PR concurrency kuralı yeni HEAD'i çalıştırırken önceki run'ı `cancelled` yaptı. Bu normal supersede davranışıdır; süre aşımı veya emülatör kök nedeni kanıtı değildir. Önceki run için tamamlanmış test artefaktı oluşmadı.
- Script commit `9532d04343d1934dc01015f7dc78c7ec1ffc8e68`: ADB tanı komutları 15 saniye ile sınırlandı; başarısızlık anında screenshot `screenshot.png` olarak toplanıyor; logcat de tutuluyor. Watchdog ve logcat ayrı process group ile başlatılıp EXIT'te process group kapatılıyor. Instrumentation Gradle komutu 1500 saniye iç timeout + 30 saniye kill-after ile çalışıyor; workflow step 30 dakika, job 35 dakika sınırı değişmedi. Böylece hata tanısı için kalan zaman korunuyor ve arka plan alt süreçlerinin runner'ı açık tutma riski azaltılıyor.
- Yeni exact-head [H1 run #45723550345](https://github.com/aries919-bot/Jarvis/actions/runs/45723550345) son kontrolde job setup aşamasında; [APK/unit #45723550139](https://github.com/aries919-bot/Jarvis/actions/runs/45723550139) Gradle setup aşamasında. Sonuç/artefakt henüz yok.
- PR #71 HEAD `9532d04343d1934dc01015f7dc78c7ec1ffc8e68`, 14 dosya; PR açık/taslak. Formal review 0. Bu run tamamlanmadan ONAY/merge yok.
**KARAR:** H1 RED/BLOCKED. Sonraki değerlendirme yeni run'ın gerçek step sonuçları, XML ve screenshot/logcat artefaktı üzerinden yapılacak.
