# H1 Emülatör Package Manager Arızası ve Alternatif AVD — 26.11.2026

## Kanıt — önceki koşunun ham artifact incelemesi
- Run [#45707502311](https://github.com/aries919-bot/Jarvis/actions/runs/45707502311), exact PR HEAD `4398073b3759109fa27a8172956cbea13f31ce27`.
- Artifact `15066187350`, SHA-256 `4911ad43a0fae8abd1b808fc113eef8062aa12934c62bd179071d0303ce4f4d2`; indirildi ve ZIP içeriği listelendi.
- XML: `tests=0 failures=0 errors=0 skipped=0`. Bu PASS değildir. `system-err`: `AndroidTestApkInstallerPlugin` öncesinde uygulama APK kurulumu başarısız; `package install-commit` sırasında `Failure calling service package: Broken pipe (32)`.
- `h1-emulator.log`: `Task :app:connectedDebugAndroidTest FAILED`; `BUILD FAILED in 3m 20s`. Instrumentation testcase çalışmamış.
- Önceki RootViewWithoutFocusException bulgusu bu run'ın nedeni değildir. Bu run'da odak testi başlamadan Android Package Manager kurulumu çökmüştür. İki ayrı hata sınıfı olarak takip edilir.
- APK build run [#45707502321](https://github.com/aries919-bot/Jarvis/actions/runs/45707502321) SUCCESS; yalnızca derleme/unit kapısını kanıtlar.

## Yapılan alternatif düzeltme
Feature branch `feat/h1-news-store-ui-20261125`:
- `810112c90c487d634163c98d3c38daddb43fc9fa`: emulator runner scriptine boot readiness + Package Manager preflight + başarısızlıkta adb/getprop/service/diskstats/logcat artifact teşhisi eklendi.
- `b6feff46a361ae969d81a176b459c0192d1a9dfc`: emülatör `target: google_apis`, force fresh AVD, `-no-snapshot -wipe-data`, 2 CPU, 2 GB RAM, 8 GB data disk, 900 sn boot timeout; diagnostics artifact listesine eklendi.
- Yerel statik kontrol: `bash -n /tmp/run-h1-emulator-tests.sh` => PASS. Bu yalnız shell syntax kontrolüdür; emulator çalıştırma kanıtı değildir.

## Yeni exact-head CI
- Yeni PR HEAD: `b6feff46a361ae969d81a176b459c0192d1a9dfc`.
- Emulator run [#45711684280](https://github.com/aries919-bot/Jarvis/actions/runs/45711684280): in_progress; checkout ve base/head doğrulaması başarılı; Java/Gradle başarılı; son kontrolde dependencyInsight adımı çalışıyordu.
- APK/unit run [#45711684204](https://github.com/aries919-bot/Jarvis/actions/runs/45711684204): in_progress; son kontrolde unit test adımı çalışıyordu.
- Bu iki run tamamlanmadan H1 sonucu yoktur. Yeni Google APIs koşusu öncekiyle aynı AVD konfigürasyonu değildir; bu kontrollü alternatif denemedir.

## Teknik referans / araştırma
- ReactiveCircus action `target` için `default` ve `google_apis` destekler; ayrıca `ram-size`, `heap-size`, `disk-size`, `force-avd-creation`, `emulator-boot-timeout` seçenekleri belgelenmiştir: https://github.com/ReactiveCircus/android-emulator-runner/blob/main/action.yml
- Issue #425, aynı action'da AVD cache kullanımıyla split APK install hatası yaşayan bir örnektir; kullanıcının mevcut akışı cache kullanmıyor, bu nedenle birebir aynı kök neden olduğu iddia edilmez: https://github.com/ReactiveCircus/android-emulator-runner/issues/425
- Google Issue Tracker'da Ddmlib install sırasında Package Manager / shell command responsiveness arızası örneği vardır: https://issuetracker.google.com/issues/337904209
- Bu kaynaklar hata sınıfının bilinen bir emulator/UTP kurulum problemi olabildiğini destekler; bu projedeki Broken pipe kök nedeninin tek başına kanıtı değildir.

## Kabul kapısı
- XML 4 testcase ve failure/error/skipped=0 olmadan H1 instrumentation PASS yok.
- XML 0 testcase ise install/boot arızasıdır; UI testleri geçmedi veya geçti diye yorumlanmaz.
- XML 4 testcase içinde failure varsa gerçek test hatası olarak ayrı teşhis edilir.
- Aynı AVD ayarıyla tekrarlı rerun yapılmaz. Yeni google_apis AVD de package service hatası verirse sıradaki alternatif ayrı emulator image/host veya fiziksel cihaz doğrulamasıdır; test skip/retry ile yeşil gösterilmez.
- PR #71 açık/taslak kalır; merge/deploy yok.

## KARAR
Bu çalışma önceki 0-test sahte PASS riskini kapatmıştır. Yeni runner/AVD değişikliği test sonucunu henüz kanıtlamamıştır. H1 RED/BLOCKED — yeni exact-head CI sonucu bekleniyor.


## Ek doğrulama — XML kapısının kendisi
- Yerel fixture kontrolünde artifact'ten çıkan `tests=0` XML'i verifier algoritmasına verildi: exit code 1 / RED.
- Sentetik 4 testcase, 0 failure/error/skipped XML'i: PASS. Bu yalnız validator'ın pozitif/negatif mantığını doğrular; gerçek Android UI testlerinin geçtiğini göstermez.
- Yeni exact-head run #45711684280 son API sorgusunda hâlâ instrumentation adımında IN_PROGRESS; #45711684204 APK/unit run da unit test adımında IN_PROGRESS. Sonuç alınmadan kabul kararı verilmez.
- Container ortamında GitHub'a doğrudan `git clone` denemesi DNS nedeniyle başarısız oldu; Cloud PR #12 yerel test komutları bu oturumda çalıştırılamadı. Bu yüzden PR #12 için yeni CI/yerel PASS iddiası yoktur.


## 26.11.2026 — Framework-generation araştırması ve PID-aware düzeltme

### Araştırma bulgusu
- FUTO Notes'taki gerçek CI olay raporu, Android emülatöründe 283 koşunun 33'ünde APK kurulumunda "cmd: Failure calling service package: Broken pipe (32)" ve benzer Package Manager hataları görüldüğünü bildiriyor.
- Rapordaki kök neden: sys.boot_completed=1 olduktan sonra system_server yeniden başlayabiliyor; property eski değerde kaldığı için basit boot ve servis kontrolleri eski framework neslini yanlışlıkla hazır sanabiliyor.
- Oradaki düzeltme, çalışan system_server PID'sinin kendi boot_progress_enable_screen olayını yazdığını doğrulamak, Package Manager durumunu tam eşleşmeyle kontrol etmek ve yeniden başlatma penceresini beklemek. Bu başka bir projedeki kanıtlanmış Android hata sınıfıdır; Aries run'ında aynı kök neden olduğu henüz logla kanıtlanmadı.
- Kaynak: https://gitlab.futo.org/futo-notes/futo-notes/-/blob/papercuts-bridge-guidance-close-round10/scripts/ci-android-sync-leg.test.mjs

### Aries değişikliği
- Commit 29aaa17e532c13a394d1ed7dd7c53bc613d0bec9: runner artık aktif system_server PID'sini ve boot_progress_enable_screen olayındaki PID'yi eşleştiriyor; Package Manager çıktısını tam "Service package: found" olarak doğruluyor; kararlılık penceresinden sonra tekrar kontrol ediyor.
- Commit e390e05e1485717c3a07ce3af06db6b3840062f3: boş paket listesi artık RED; başarılı preflight PID dosyası için dizin oluşturma düzeltildi. Teşhis artifact'i events log, crash buffer, process listesi ve services çıktısını kapsıyor.
- Komut kanıtı: bash -n run-h1-emulator-tests.sh PASS.
- Mock ADB/Gradle entegrasyon kontrolü: kararlı framework + gerçek paket kaydı senaryosu exit 0; Package Manager bulunamadı exit 21; eski system_server PID işaretçisi exit 21; boş paket listesi exit 21. Sonuçlar yerel simülasyondur; Android emülatör PASS değildir.

### Exact-head CI durumu
- PR #71 current HEAD: e390e05e1485717c3a07ce3af06db6b3840062f3; diff 14 dosya / +3.492 / -26.
- Önceki HEAD b6feff4 APK run [#45711684204](https://github.com/aries919-bot/Jarvis/actions/runs/45711684204) SUCCESS oldu; bu önceki HEAD'in unit/APK derleme kanıtıdır, son HEAD'e taşınamaz.
- Aynı eski HEAD emulator run [#45711684280](https://github.com/aries919-bot/Jarvis/actions/runs/45711684280) yeni commit nedeniyle CANCELLED; 4 UI testi çalışmadı.
- Aradaki HEAD 29aaa17 emulator run [#45712393809](https://github.com/aries919-bot/Jarvis/actions/runs/45712393809) da sonraki commit ile CANCELLED; APK run [#45712393814](https://github.com/aries919-bot/Jarvis/actions/runs/45712393814) CANCELLED.
- Current exact-head emulator [#45712459718](https://github.com/aries919-bot/Jarvis/actions/runs/45712459718) ve APK [#45712459742](https://github.com/aries919-bot/Jarvis/actions/runs/45712459742) son kontrolde IN_PROGRESS. Emulator job henüz Set up job aşamasındaydı; APK hattı unit test aşamasındaydı. Bu iki run bitmeden H1 PASS yok.
- Saat düzeltmesi: önceki mesajda 17 saat varsayımı yanlıştı. GitHub UTC kayıtları ilk run'ın yaklaşık 22:15'te başladığını ve yaklaşık 22:20'de tamamlandığını gösteriyor; yaklaşık 5 dakika sürmüştür. 20/35 dakika timeout henüz aşılmamıştı.

### Kabul ve karar
- H1 emulator PASS için son HEAD'de XML tam 4 testcase; failure=0, errors=0, skipped=0; XML gate PASS ve artifact kontrolü gereklidir.
- Mock kontrolleri, unit/APK success veya 0 testcase H1 PASS sayılmaz.
- PR #71 draft/açık; merge/deploy yok.
- C1 bağımsız RED: kamu feed son iyi veri 6 kaynak /47 kart; 6 kaynağın hepsi stale, 10 kaynak/100 doğrulanmış ilan ve production authenticated GET/D1 kanıtı yok.

**KARAR:** H1 yeni PID-aware düzeltme CI bekliyor; C1 RED.

## 26.11.2026 — Exact-head unit/APK sonucu ve yeni test sayımı

- Exact HEAD `e390e05e1485717c3a07ce3af06db6b3840062f3` APK run [#45712459742](https://github.com/aries919-bot/Jarvis/actions/runs/45712459742) **SUCCESS**. Job loglarında `gradle testDebugUnitTest --no-daemon` için `BUILD SUCCESSFUL in 1m 50s`; `gradle assembleDebug --no-daemon` için `BUILD SUCCESSFUL in 1m 10s`; artifact upload SUCCESS.
- APK artifact ID `15068252085`, 14,197,729 byte; ZIP SHA-256 `d3ceae0d1781cfb0f6ae19cdeb587494ea4f04310d2117f3fe23c46aafb43c91`. ZIP'te tek `app-debug.apk`; çıkarılan APK SHA-256 `045bf135f19f4cac514ad48b05b3c33b374c7f0cc805a2debd4d07cdc3a4fb6d`.
- Son HEAD test anotasyonu yeniden sayımı: 12 unit test dosyası /142 `@Test`; H1 model 20, store 10, QA 17 = H1/QA alanında 47 unit test tanımı. Android instrumentation dosyası 4 `@Test`; tüm repo sayımı 142 unit +4 instrument =146 test tanımı. Bu statik anotasyon sayımıdır; log, Gradle unit görevinin SUCCESS olduğunu kanıtlar, ayrı XML test-count kanıtı değildir.
- Exact-head emulator run [#45712459718](https://github.com/aries919-bot/Jarvis/actions/runs/45712459718) son API sorgusunda IN_PROGRESS; checkout/head-base doğrulaması, Java/Gradle ve dependency insight SUCCESS; `Run Android emulator instrumentation tests` IN_PROGRESS; XML gate ve artifact upload henüz başlamadı. Run tamamlanmadan ve XML yeniden sayılmadan H1 PASS yok.
- FUTO'da sonraki Android CI vakası, Package Manager servisinin bulunmasını tek başına yeterli görmüyor; `pm install-create` ile depolama/installer yolunu gerçek kurulumdan önce sınamayı öneriyor: https://gitlab.futo.org/futo-notes/futo-notes/-/blob/android-fs-picker/scripts/ci-android-emulator.sh. Bu, mevcut Aries koşusu StorageManager hatası üretirse uygulanacak alternatif; henüz Aries loguyla bu kök neden doğrulanmadı.
- PR #71 açık/taslak; diff 14 dosya /+3.492 /-26. Merge/deploy yok.

**KARAR:** Unit/APK kapısı PASS (exact HEAD); H1 Android UI kapısı RED/BLOCKED — 4/4 instrumentation XML henüz yok. C1 kamu feed 6 kaynak/47 last-good; 6/6 stale, 10/100 ve production authenticated GET/D1 hâlâ RED.


### Bekleyen bakım bulgusu — workflow path filters
- Workflow `.github/workflows/h1-android-ui-smoke.yml` içindeki pull_request.paths listesi `scripts/run-h1-emulator-tests.sh` ve `scripts/verify_h1_emulator_xml.py` dosyalarını kapsamıyor. Bu iki betikten biri tek başına değişirse PR event'i bu workflow'u tetiklemeyebilir; mevcut run tetiklendi çünkü aynı PR'da workflow dosyası da değişti.
- Mevcut emulator koşusu tamamlanmadan workflow dosyasına dokunulmadı; aksi halde concurrency `cancel-in-progress: true` mevcut kanıt koşusunu iptal eder. Sonuç alındıktan sonraki kontrollü düzeltmede iki script yolu trigger listesine eklenmeli.
- FUTO'nun daha yeni CI olayı ayrıca `pm install-create`/abandon preflight ile PackageInstaller + StorageManager yolunun hazır oluşunu doğruluyor. Bu sadece mevcut Aries artifact'inde aynı storage/installer hatası görülürse uygulanacak; şimdilik tahminle commit yok.


### İkinci araştırma kolu — CI runner takılması
- ReactiveCircus/android-emulator-runner açık issue #478, 2026-06-17: GitHub hosted runner üzerinde emulator çalışırken logların emulator adımında/testler başlamadan kesilmesi ve job'ın hata vermeden beklemesi raporlanmış. Yazar, GitHub runner image rollout ihtimalini tartışmış ve Blacksmith runner ile denediğinde de benzer sonuç aldığını belirtmiş: https://github.com/ReactiveCircus/android-emulator-runner/issues/478
- Bu, Aries için doğrulanmış neden değil; yalnız mevcut in_progress + log paketi henüz indirilemiyor semptomu için ikinci olası sınıf.
- Ayrım ölçütleri: (1) job sonunda gradle connectedDebugAndroidTest/adb install/system_server/StorageManager tanı logu varsa Android framework/installer kolu; (2) emulator action/runner/Gradle step logları test başlangıcından önce kesilmiş ve Android komutu çıktısı yoksa runner/host kolu. Her iki durumda da XML yoksa H1 RED.
- Son kontrol: emulator run #45712459718 exact HEAD e390e05... hâlâ in_progress; artifact yok. Log fetch 404 BlobNotFound, bu çalışırken GitHub tarafından log arşivi sunulmadığı anlamına geliyor; test sonucu değildir.
- Runner kaynaklarına göre StorageManager yarışını ayrıştırmak için gerekirse pm install-create/pm install-abandon preflight kullanılır; yalnız ilgili NPE veya install-session hata kanıtı çıktığında.
