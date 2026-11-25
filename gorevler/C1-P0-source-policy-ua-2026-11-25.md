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
- HEAD: 5d15ef30e016a0105cc8e1b62ed05b0405a48c77
- PR #70: https://github.com/aries919-bot/Jarvis/pull/70
- Durum: açık/taslak, birleştirilmedi.
- Eski PR #67 yanlış tabana bağlı olduğundan kapatıldı (unmerged); #66 da önceden kapatılmıştı.

## Değişiklikler
1. CareerMonitor.kt: Indeed/LinkedIn kaynak kataloğu girdileri, trusted domain kayıtları, secondary domain listesi ve site arama sorguları kaldırıldı. Genel edu.tr trusted domain listesinden çıkarıldı.
2. Ai.kt: kaynak arama ve Google Haberler RSS isteklerindeki tarayıcı taklidi UA değerleri AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis) olarak değiştirildi; RSS bağlantısına da UA eklendi.
3. CareerForbiddenSourcesPolicyTest.kt: LinkedIn/Indeed alanları trusted/secondary kabul edilmez, kaynak kataloğunda yer almaz; genel edu.tr ve izin listesi dışı üniversite hostları reddedilir; izinli Turgut Özal üniversite hostu kabul edilir.
4. CareerSourceCatalogTest.kt: eski genel edu.tr pozitif beklentisi false olarak düzeltildi.

## Diff / yeniden sayım
- Exact compare: 4 commit / 4 dosya / +33 / -13 / 0 behind.
- CareerMonitor.kt: 2.041 satır; kod taramasında Indeed/LinkedIn URL/domain ve genel trusted edu.tr kaydı yok.
- Ai.kt: 736 satır; Mozilla/5.0 ve Chrome tarayıcı-UA kalıbı yok; AriesKaynakTara/1.0 tam 4 noktada.
- CareerForbiddenSourcesPolicyTest.kt: 28 satır, 1 @Test.
- CareerSourceCatalogTest.kt: 272 satır, 13 mevcut @Test; tek genel .edu.tr beklentisi yeni kurala göre güncellendi.
- Yasaklı domain isimleri yalnız regresyon testinde, RED beklenen değer olarak bulunur; production source/query/catalog dosyasında bulunmaz.

## CI
- İlk exact-head PR #70 koşusu #45657277644 FAIL: 96 birim testten 1 eski assertion başarısız; hata eski CareerSourceCatalogTest'in example.edu.tr adresini trusted beklemesiydi.
- Assertion düzeltildi; güncel HEAD 5d15ef30.
- Güncel exact-head run #45657731556, Android APK #686: IN_PROGRESS. Son kontrolde Gradle setup aşamasındaydı; yeni test sonucu yok.
- Eski failed run yeni HEAD için PASS sayılmaz. APK derlemesi/artefaktı henüz yok.

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