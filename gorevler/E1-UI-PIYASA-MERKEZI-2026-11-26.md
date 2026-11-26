# E1-UI — Piyasa Merkezi dal entegrasyonu (26.11.2026)

## Amaç
Önceki APK'da Piyasa Merkezi bulunmadığı için borsa/altın/döviz kodunu güncel güvenlik tabanına taşımak. Bu görev, kullanıcının açık talebiyle sınırlı E1-UI entegrasyonudur; `main` dalına yazılmaz.

## Dal teşhisi
- `main` gerideydi; Android kariyer uygulamasının güncel hedefi `kariyer-modulu` olarak tutuldu.
- `kariyer-modulu` tabanı PR #73 güvenlik merge'ini içerir: merge SHA `1c4c1c14821d9fa933678ec5c436ee3762058cf2`.
- Eski Piyasa Merkezi kaynağı PR #22 / `is/kariyer-1-21-verified-search`; `kariyer-modulu` tabanından 25 commit geride ve farklı dosyalarda çakışma taşıdığı için doğrudan merge edilmedi.
- `feature/aries-finance-center` ile `main` kıyaslaması 177 ahead / 5 behind ve geniş karışık fark gösteriyor; bu dal topluca birleştirilmedi.
- H1 PR #69 ve #71 bu entegrasyona alınmadı. #71 APK/unit başarılı olsa da emulator smoke başarısız olduğundan H1 kabul kapısı açık.

## Uygulanan entegrasyon
- PR #74: `integration/aries-1.23-market-security-20261126` → `kariyer-modulu`.
- Değişen dosyalar: `app/build.gradle.kts`, `AndroidManifest.xml`, `FinanceActivity.kt`, `FinanceData.kt`, `MainActivity.kt`, `Ai.kt`, `Views.kt`, `FinanceDataTest.kt`.
- Sürüm `1.23`, `versionCode=23`.
- Altın/döviz, Finnhub arama/izleme ve fiyat alarmı ekranı eklendi.
- Eski `finnhub_enc` finans şifreli kaydı ilk okumada ortak `SecureStore` alanına taşınır; yeni anahtarlar da aynı güvenli depoda saklanır.
- `FinanceKeyStore`'un yalnız kendi pref alanını okuma kusuru tespit edilip düzeltildi. `Store.migrateSecrets()`'ın eski `finnhub` kaydını ayrı SecureStore dosyasına taşımasıyla veri kaybı/anahtara erişememe riski vardı.
- İlk CI #768 `Pal.AMBER` eksikliği nedeniyle başarısız oldu. Renk token'ı eklendi; exact-head son kaynak SHA `2e981a5497b38fb13151ea39994e51041acc0dfe`.

## Kanıt ve durum
- PR #74: https://github.com/aries919-bot/Jarvis/pull/74
- İlk CI #768: https://github.com/aries919-bot/Jarvis/actions/runs/45734428315 — FAILURE; `FinanceActivity.kt` içindeki 10 `Pal.AMBER` referansı tema paletinde tanımsızdı.
- Düzeltme sonrası exact-head CI #772: https://github.com/aries919-bot/Jarvis/actions/runs/45735481573 — son kontrolde IN_PROGRESS; birim test aşaması. Bu nedenle APK henüz yok.
- Exact diff yeniden sayımı: 8 dosya / 1.187 ekleme / 14 silme (renk ve secure-store düzeltmeleriyle son diff sayımı GitHub PR metadata'sında ayrıca doğrulanacak).
- Gizli anahtar literal, private-key başlığı, token sabiti, düz metin `finnhub` yazımı ve SSL bypass desen taramaları eklenen satırlarda 0 eşleşme.
- PR #74 normal merge ile tamamlandı; merge SHA `e40e2aa4e9d140c88546785ad5b3b89a99c9b7e4`. `main` dalına yazılmadı.

## Kabul kapıları
1. Exact-head birim testleri SUCCESS.
2. `assembleDebug` SUCCESS.
3. `aries-apk` artefaktı yüklenmiş ve indirilen APK SHA-256 ile doğrulanmış.
4. PR #74 diff'i yalnız yukarıdaki 7 dosya ve görev kapsamı içinde.
5. PR #74 yalnız `kariyer-modulu` dalına merge edilir; `main` değiştirilmez.
6. Merge sonrası `kariyer-modulu` dalında yeni APK workflow'u ve artefaktı ayrıca kontrol edilir.

**KARAR:** PR #74 MERGED; test ve PR artefaktı PASS. Son kullanıcı APK'sı için post-merge #775 artefaktı ayrıca doğrulanacak.

**SONRAKİ ADIM:** #772 sonucunu al; yeşil değilse hata logunun son bölümünü inceleyip tek kök nedeni düzelt.

## Son APK teslim kaydı — 26.11.2026
- PR #74 merge commit: `e40e2aa4e9d140c88546785ad5b3b89a99c9b7e4`.
- PR exact-head `438bae8e5ed5fed0f44970c018b867d12279305d` ve merge commit aynı Git tree SHA'yı taşıyor: `7889fd520f7ea872c3243c0990708ad809451ab9`. Dolayısıyla PR #774 artefaktı, merge edilen kaynak ağacının aynısını derlemiştir.
- PR #774 build/test/artifact upload SUCCESS: https://github.com/aries919-bot/Jarvis/actions/runs/45735775751.
- Teslim APK: `Aries-1.23-Piyasa-Merkezi.apk`, 14,855,695 bayt; SHA-256 `5f420aac52293284e2cfefb26f4e923c5b06087f24de2238e27103f614d04be0`.
- Artefakt ZIP #15078344143, 14,100,789 bayt; SHA-256 `98479f74649ac0cc63d9e8b03ff4fc315a76449e7374315fad60314ef05a1d10`.
- APK DEX içinde `Piyasa Merkezi`, `FinanceActivity` ve `FinanceKeyStore` dizgileri bulundu; uygulama paket formatı doğrulandı.
- Post-merge Actions #775 https://github.com/aries919-bot/Jarvis/actions/runs/45736185331: exact merge SHA üzerinde birim test SUCCESS; APK build adımı son kontrolde IN_PROGRESS, post-merge artifact henüz görünmüyor. Teslim edilen APK, aynı Git tree'den başarılı PR #774 koşusunda üretilmiştir.
**KARAR:** APK teslim edildi; kaynak ağacı merge commit ile birebir aynı. Post-merge #775 artefaktı ayrı son koşu kanıtı olarak bekliyor. Gerçek telefon/live piyasa verisi denemesi yapılmadı.
