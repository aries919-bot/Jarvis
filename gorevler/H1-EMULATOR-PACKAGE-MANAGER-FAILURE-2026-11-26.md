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
