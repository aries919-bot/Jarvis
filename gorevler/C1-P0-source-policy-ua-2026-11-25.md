# C1-P0 — kaynak politikası ve User-Agent düzeltmesi
Tarih: 25.11.2026
Durum: PR AÇIK / CI BEKLİYOR / KISMİ

## Amaç
Mevcut İş & Kariyer modülünde NOT_C1 ihlali olan LinkedIn/Indeed kaynak güvenini kaldırmak, genel .edu.tr alanını resmî/güvenilir saymamak ve kaynak isteklerinde tarayıcı taklidini kaldırmak. Mevcut C3 kullanıcı onayı korumaları korunarak ayrı, geri alınabilir PR açıldı.

## Dal ve PR
- Repo: aries919-bot/Jarvis
- Taban: fix/c3-ai-consent-and-navigation-guard
- Taban SHA: 5f6eebb816fadfa8e1c7c51b4ccce1234d1cee03
- Dal: fix/c1-source-policy-ua-guard-20261125
- HEAD: 17f3fb796d482ea779c5ee2cf9f099828c7b3d72
- PR #66: https://github.com/aries919-bot/Jarvis/pull/66
- PR durumu: open, draft, unmerged; mergeable false (GitHub sonucu).
- Compare: 4 commit, 4 dosya, +24/-13; base ile 0 geride.

## Değişen dosyalar
1. CareerMonitor.kt: Indeed/LinkedIn kaynak kayıtları, trustedDomains kayıtları, secondaryDomains kayıtları ve site sorguları çıkarıldı. Genel edu.tr trusted domain listesinden çıkarıldı.
2. Ai.kt: Chrome/Mozilla mobil tarayıcı taklidi yapan User-Agent değerleri AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis) ile değiştirildi. Google News RSS çağrısına da aynı UA eklendi.
3. CareerSourceCatalogTest.kt: example.edu.tr güven testi artık RED bekliyor.
4. CareerForbiddenSourcesPolicyTest.kt: LinkedIn/Indeed alan adlarının güvenilir veya ikincil kaynak kabul edilmediğini test eder.

## Kanıt ve yeniden sayım
- GitHub compare sonucu: 4 dosya; CareerMonitor 1 ekleme/9 silme; Ai.kt 4 ekleme/3 silme; yeni test 18 ekleme; mevcut test 1 ekleme/1 silme. Toplam 24 ekleme /13 silme.
- CareerMonitor son dosya: 2.142 satır. Kod içi taramada Indeed/LinkedIn alan adı kalmadı. .edu.tr yalnız akademik statüyü sınıflandırmak için kalır; genel trusted domain olarak kullanılmaz.
- Ai.kt son dosya: 736 satır. Kod içi taramada Mozilla/5.0 veya Chrome tarayıcı UA kalmadı; AriesKaynakTara UA 4 noktada kullanılıyor.
- Yeni test dosyası 19 satır (package/imports dahil), 1 test metodu; üç yasaklı host örneği doğrulanıyor.
- CI: HEAD için Actions workflow run listesi boş; birleşik commit status listesi boş. Bu nedenle testler ÇALIŞTIRILMADI / KANIT YOK, PASS denemez.

## Bilinçli kapsam sınırı / eksikler
- Mobil DuckDuckGo/Bing/Brave HTML arama fallback'i bu PR'da kaldırılmadı. Worker production GET henüz doğrulanmadığından bunu şimdi kapatmak aramayı sıfır sonuçlu bırakabilir. NOT_C1'e uygun sunucu JSON geçişi ayrı, zorunlu sonraki iş; bu PR genel uyum kabulü değildir.
- CareerActivity dış bağlantılarında Intent.ACTION_VIEW hâlâ mevcut; Custom Tabs geçişi ayrı iş.
- Haber H1 claim/evidence, birincil kaynak, karşıt kanıt, insan incelemesi ve düzeltme geçmişi bu PR'da değişmedi.
- Gerçek telefon testi yapılmadı.

## Güvenlik kararı
- Bu PR tek başına KABUL DEĞİL; CI ve exact-head inceleme bekliyor.
- PR #30 C3 consent/navigation guard tabanına bağlıdır. #30 tabanı birleşmeden #66 ana dala taşınamaz.
- Birleştirme veya dağıtım yapılmadı.

## SONRAKİ ADIM
1. PR #66 exact-head Android unit tests + APK + Full App QA Actions sonuçlarını al.
2. Başarısızlık olursa aynı testi körlemesine tekrarlama; log ve kök neden üzerinden düzelt.
3. Sonraki ayrı PR: Custom Tabs + H1 kanıt kartı; ardından Worker production GET kanıtı geldiğinde mobil HTML arama fallback'ini kapatıp JSON feed'e geç.