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