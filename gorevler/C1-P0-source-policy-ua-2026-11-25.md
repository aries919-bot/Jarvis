# C1 P0 — izinli kaynak ve User-Agent güvenlik düzeltmesi
Tarih: 25.11.2026
Durum: PR #70 AÇIK / TASLAK / EXACT-HEAD CI DEVAM EDİYOR

## Amaç
NOT_C1 ihlallerini küçük ve ayrı değişikliklerle kaldırmak; mevcut arama zincirini Worker production GET kanıtı gelmeden kapatmamak.

## PR / Dal
- Repo: aries919-bot/Jarvis
- Taban: kariyer-modulu
- Taban SHA: 4b270245019ed322dc2dc1c735c715c0934076a2
- Dal: fix/c1-source-policy-ua-ci-20261125
- HEAD: bf8ca39c1dee78b8436069e5cfabcbbd3afbc146
- PR #70: https://github.com/aries919-bot/Jarvis/pull/70
- Durum: açık/taslak, birleştirilmedi.
- Eski PR #67 yanlış tabana bağlı olduğundan kapatıldı (unmerged); #66 da önceden kapatılmıştı.

## Değişiklikler
1. CareerMonitor.kt: Indeed/LinkedIn kaynak kataloğu girdileri, trusted domain kayıtları, secondary domain listesi ve site arama sorguları kaldırıldı. Genel edu.tr trusted domain listesinden çıkarıldı.
2. Ai.kt: kaynak arama ve Google Haberler RSS isteklerindeki tarayıcı taklidi UA değerleri AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis) olarak değiştirildi; RSS bağlantısına da UA eklendi.
3. CareerForbiddenSourcesPolicyTest.kt: LinkedIn/Indeed alanları trusted/secondary kabul edilmez, kaynak kataloğunda yer almaz; genel edu.tr ve izin listesi dışı üniversite hostları reddedilir; izinli Turgut Özal üniversite hostu kabul edilir.
4. CareerSourceCatalogTest.kt: eski genel edu.tr pozitif beklentisi false olarak düzeltildi.

## Diff / yeniden sayım
- Exact compare: 8 commit / 4 dosya / +46 / -13 / 0 behind.
- CareerMonitor.kt: 2.041 satır; kod taramasında Indeed/LinkedIn URL/domain ve genel trusted edu.tr kaydı yok.
- Ai.kt: 737 satır; Mozilla/5.0 ve Chrome tarayıcı-UA kalıbı yok; kaynak UA sabiti 1 tanım + 4 kullanım (5 toplam referans).
- CareerForbiddenSourcesPolicyTest.kt: 39 satır, 2 @Test; kaynak allowlist + tek User-Agent sabiti.
- CareerSourceCatalogTest.kt: 272 satır, 13 mevcut @Test; tek genel .edu.tr beklentisi yeni kurala göre güncellendi.
- Yasaklı domain isimleri yalnız regresyon testinde, RED beklenen değer olarak bulunur; production source/query/catalog dosyasında bulunmaz.

## CI
- İlk exact-head PR #70 koşusu #45657277644 FAIL: 96 birim testten 1 eski assertion başarısız; eski CareerSourceCatalogTest example.edu.tr adresini trusted bekliyordu.
- Assertion düzeltildikten sonraki önceki HEAD #45657731556 (#686): unit test task, APK build ve artifact upload SUCCESS; artifact 15043108366. Ancak bu koşu User-Agent tek sabite alınmadan önceki SHA'ya aittir.
- Güncel HEAD bf8ca39 için exact-head run #45658875411 (#692): birim test adımı SUCCESS, APK derlemesi IN_PROGRESS; henüz final run sonucu yok. Önceki artifact yeni HEAD için kanıt değildir.

## Sınırlar
- DDG/Bing/Brave HTML fallback kaldırılmadı: Worker production GET authenticated endpoint kanıtı yok; şimdi kapatmak iş aramasını bozabilir.
- C1 global gate: en son ölçülmüş kamu 6 kaynak /47 doğrulanmış açık ilan; 10 kaynak/100 ilan RED; production GET BLOCKED. Bu PR bu sayıları artırmaz.
- Custom Tabs ve haber iddia/kanıt modeli ayrı PR.
- Gerçek telefon testi yapılmadı.

## Güvenlik kararı
- PR #70 şu an KABUL DEĞİL; exact-head CI ve statik inceleme bekliyor.
- Merge/deploy yapılmadı.

## SONRAKİ ADIM
1. PR #70 run #45657731556 sonucunu al; yeşil değilse log kök nedeni düzelt.
2. PR #70 exact-head diff ve security scan tekrar et.
3. Haber çekirdeği PR #69 testlerini tamamla.
4. Worker JSON production GET, kaynak bazlı last-good, cross-feed dedup ve 10/100 gate kanıtları gelmeden Android arama motoru HTML fallback kapatılmaz.

## Güncel exact-head — 25.11.2026 16:54 TRT
- Güncel PR #70 HEAD: `bf8ca39c1dee78b8436069e5cfabcbbd3afbc146`.
- Exact compare: 8 commit /4 dosya /+46/-13 /0 behind.
- Android Actions [#45658875411](https://github.com/aries919-bot/Jarvis/actions/runs/45658875411): son kontrolde PENDING; job/test adımları başlamamış. Önceki #45657277644 eski HEAD'de 96 test/1 hata; #45658345528 önceki HEAD için eski koşudur. Hiçbiri güncel HEAD'e PASS aktarmaz.
- Son kaynak kodu statik sayımı: CareerMonitor 2042 satır; production'da Indeed/LinkedIn URL/domain eşleşmesi 0; genel trusted `edu.tr` kaydı yok; `isUnverifiedUniversityHost(host)` erken ret kontrolü mevcut.
- Ai.kt 737 satır; Mozilla/Chrome browser-UA kalıbı yok; `ARIES_SOURCE_USER_AGENT` bir sabit tanımı + dört istek kullanımıyla yer alıyor.
- Regresyon testleri: `CareerForbiddenSourcesPolicyTest` 2 @Test; `CareerSourceCatalogTest` 13 @Test mevcut, genel edu.tr pozitif beklentisi negatif olarak değiştirildi.
- Sonuç: exact-head CI henüz tamamlanmadı; PR #70 RED/BEKLEME, merge yok.

## 25.11.2026 — exact-head UA sabiti CI sonucu
- Son HEAD: bf8ca39c1dee78b8436069e5cfabcbbd3afbc146.
- Exact compare: 8 commit / 4 dosya / +46 / -13 / 0 behind.
- Android APK run #45658875411 (#692): job SUCCESS; Birim testleri SUCCESS; APK derle SUCCESS; artifact upload SUCCESS.
- Artifact ID 15044212711; 14.061.515 bayt; SHA-256 zip digest 2a8f2183f474b907ee9345266b75c7a6d483c0982939073b2667f6560ce0b9de.
- Bu exact head için CI yeşil; bağımsız statik inceleme/öz-QA ve PR karar kaydı henüz yazılmadı. PR açık/taslak, merge yok.