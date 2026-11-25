# Android CI hızlandırma — takip işi, güvenlik kapılı

## Mevcut kanıt
- `.github/workflows/android.yml` branch `kariyer-modulu`, blob `3795aede0b90fb9ee447ed9c57e94f468ca3ae9d`.
- Workflow iki Gradle çağrısı yapıyor: `gradle testDebugUnitTest --no-daemon` ve `gradle assembleDebug --no-daemon`.
- `app/build.gradle.kts` blob `e45bfab00b7798dbf239768f7a91691ec2752b91` içinde `TMDB_TOKEN`, `KEYSTORE_PATH`, `KEYSTORE_PASS`, `KEY_ALIAS`, `KEY_PASS` için `System.getenv` kullanımı var.
- Şu an bu sırlar yalnız APK derleme adımının env'inde; test adımına aktarılmıyor.

## Önemli güvenlik notu
İki Gradle task'ını tek komutta birleştirmek ikinci task'a özel sırları aynı Gradle/daemon/test sürecine taşıyabilir. Sırları birim test worker'larına açmadan güvenli yöntem kanıtlanmadan tek çağrıya çevrilmemeli. Süre kazancı uğruna test kodunun erişebileceği ortam genişletilmeyecek.

## Denenecek seçenekler
1. Önce iki workflow adımının gerçek sürelerini CI metadata'sından ölç; tek çağrıdan beklenen kazanç varsayılmasın.
2. Gradle task graph/cache davranışını ve APK imzalama ihtiyacını incele. Debug APK için özel keystore gerçekten zorunlu değilse, imzalama ayrı güvenli aşamaya alınabilir; otomatik imza varsayımı yapılmaz.
3. Birleştirme önerisi varsa PR'da test process env'ine hangi değişkenlerin gittiğini gösteren regresyon denetimi ekle. Test aşamasına TMDB/keystore sırları sızmamalı.
4. A/B CI ölçümü: aynı committe eski ve yeni workflow çalışma süreleri, test sonucu ve artifact SHA karşılaştırması.

## KARAR
**ŞİMDİLİK KOD DEĞİŞİKLİĞİ YOK.** Bu, H1 emulator entegrasyon düzeltmesinden ayrı bir takip PR'ıdır. Güvenlik sınırı korunmadan hız optimizasyonu kabul edilmez.
