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
- Değişen dosyalar: `app/build.gradle.kts`, `AndroidManifest.xml`, `FinanceActivity.kt`, `FinanceData.kt`, `MainActivity.kt`, `Views.kt`, `FinanceDataTest.kt`.
- Sürüm `1.23`, `versionCode=23`.
- Altın/döviz, Finnhub arama/izleme ve fiyat alarmı ekranı eklendi.
- Eski `finnhub_enc` finans şifreli kaydı ilk okumada ortak `SecureStore` alanına taşınır; yeni anahtarlar da aynı güvenli depoda saklanır.
- `FinanceKeyStore`'un yalnız kendi pref alanını okuma kusuru tespit edilip düzeltildi. `Store.migrateSecrets()`'ın eski `finnhub` kaydını ayrı SecureStore dosyasına taşımasıyla veri kaybı/anahtara erişememe riski vardı.
- İlk CI #768 `Pal.AMBER` eksikliği nedeniyle başarısız oldu. Renk token'ı eklendi; exact-head son kaynak SHA `2e981a5497b38fb13151ea39994e51041acc0dfe`.

## Kanıt ve durum
- PR #74: https://github.com/aries919-bot/Jarvis/pull/74
- İlk CI #768: https://github.com/aries919-bot/Jarvis/actions/runs/45734428315 — FAILURE; `FinanceActivity.kt` içindeki 10 `Pal.AMBER` referansı tema paletinde tanımsızdı.
- Düzeltme sonrası exact-head CI #772: https://github.com/aries919-bot/Jarvis/actions/runs/45735481573 — son kontrolde IN_PROGRESS; birim test aşaması. Bu nedenle APK henüz yok.
- Exact diff yeniden sayımı: 7 dosya / 1.181 ekleme / 4 silme (renk ve secure-store düzeltmeleriyle son diff sayımı GitHub PR metadata'sında ayrıca doğrulanacak).
- Gizli anahtar literal, private-key başlığı, token sabiti, düz metin `finnhub` yazımı ve SSL bypass desen taramaları eklenen satırlarda 0 eşleşme.
- Merge/deploy yapılmadı; #74 açık.

## Kabul kapıları
1. Exact-head birim testleri SUCCESS.
2. `assembleDebug` SUCCESS.
3. `aries-apk` artefaktı yüklenmiş ve indirilen APK SHA-256 ile doğrulanmış.
4. PR #74 diff'i yalnız yukarıdaki 7 dosya ve görev kapsamı içinde.
5. PR #74 yalnız `kariyer-modulu` dalına merge edilir; `main` değiştirilmez.
6. Merge sonrası `kariyer-modulu` dalında yeni APK workflow'u ve artefaktı ayrıca kontrol edilir.

**KARAR:** DEVAM / MERGE BEKLİYOR.

**SONRAKİ ADIM:** #772 sonucunu al; yeşil değilse hata logunun son bölümünü inceleyip tek kök nedeni düzelt.