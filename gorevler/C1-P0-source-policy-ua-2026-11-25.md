# C1-P0 — kaynak politikası ve User-Agent düzeltmesi
Tarih: 25.11.2026
Durum: PR AÇIK / CI BEKLİYOR / KISMİ

## Amaç
Mevcut İş & Kariyer modülünde NOT_C1 ihlali olan LinkedIn/Indeed kaynak güvenini kaldırmak, genel .edu.tr alanını resmî/güvenilir saymamak ve kaynak isteklerinde tarayıcı taklidini kaldırmak. Mevcut C3 kullanıcı onayı korumaları korunarak ayrı, geri alınabilir PR açıldı.

## Dal ve PR
- Repo: aries919-bot/Jarvis
- PR hedef tabanı: is/c3-ilan-semasi-ai-kaynak-kaniti (Actions filtresindeki izinli taban)
- Önceki C3 consent commit'i: 5f6eebb816fadfa8e1c7c51b4ccce1234d1cee03; PR #66 diff'i bu commit'i de içerir
- Dal: fix/c1-source-policy-ua-clean-20261125
- HEAD: dc71e10dac04f17d0608678376d3ad38a3e1edf2
- PR #67: https://github.com/aries919-bot/Jarvis/pull/67 (aktif)
- Eski PR #66 kapatıldı; taban C3 PR #30 commitlerini diff'e karıştırdığı için kapsam temizliği amacıyla superseded.
- PR #67 durumu: open, draft, unmerged; mergeable false (GitHub sonucu).
- Temiz PR #67 doğrudan C3 dalından açıldı; compare yalnız 4 dosya / +32 / -13. PR #66'nın 9 dosyalı parent-diff sorunu giderildi. Son test commit'i katalogda Indeed/LinkedIn URL'lerinin hiç bulunmadığını da doğrular.

## Değişen dosyalar
1. CareerMonitor.kt: Indeed/LinkedIn kaynak kayıtları, trustedDomains kayıtları, secondaryDomains kayıtları ve site sorguları çıkarıldı. Genel edu.tr trusted domain listesinden çıkarıldı.
2. Ai.kt: Chrome/Mozilla mobil tarayıcı taklidi yapan User-Agent değerleri AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis) ile değiştirildi. Google News RSS çağrısına da aynı UA eklendi.
3. CareerSourceCatalogTest.kt: example.edu.tr güven testi artık RED bekliyor.
4. CareerForbiddenSourcesPolicyTest.kt: LinkedIn/Indeed alan adlarının güvenilir veya ikincil kaynak kabul edilmediğini test eder.

## Kanıt ve yeniden sayım
- GitHub compare sonucu: 4 dosya; CareerMonitor 1 ekleme/9 silme; Ai.kt 4 ekleme/3 silme; yeni test 18 ekleme; mevcut test 1 ekleme/1 silme. Toplam 24 ekleme /13 silme.
- CareerMonitor son dosya: 2.142 satır. Kod içi taramada Indeed/LinkedIn alan adı kalmadı. .edu.tr yalnız akademik statüyü sınıflandırmak için kalır; genel trusted domain olarak kullanılmaz.
- Ai.kt son dosya: 736 satır. Kod içi taramada Mozilla/5.0 veya Chrome tarayıcı UA kalmadı; AriesKaynakTara UA 4 noktada kullanılıyor.
- Yeni test dosyası 23 satır, 1 test metodu; LinkedIn/Indeed ile izinli/izinsiz .edu.tr alanlarını doğruluyor. Diff incelemesinde assertTrue import eksiği yakalandı ve CI tamamlanmadan ayrı commit ile düzeltildi.
- CI: HEAD için Actions workflow run listesi boş; birleşik commit status listesi boş. Bu nedenle testler ÇALIŞTIRILMADI / KANIT YOK, PASS denemez.

## Bilinçli kapsam sınırı / eksikler
- Mobil DuckDuckGo/Bing/Brave HTML arama fallback'i bu PR'da kaldırılmadı. Worker production GET henüz doğrulanmadığından bunu şimdi kapatmak aramayı sıfır sonuçlu bırakabilir. NOT_C1'e uygun sunucu JSON geçişi ayrı, zorunlu sonraki iş; bu PR genel uyum kabulü değildir.
- CareerActivity dış bağlantılarında Intent.ACTION_VIEW hâlâ mevcut; Custom Tabs geçişi ayrı iş.
- Haber H1 claim/evidence, birincil kaynak, karşıt kanıt, insan incelemesi ve düzeltme geçmişi bu PR'da değişmedi.
- Gerçek telefon testi yapılmadı.

## Güvenlik kararı
- Bu PR tek başına KABUL DEĞİL; CI ve exact-head inceleme bekliyor.
- PR #66 şu an C3 dalını hedefler ve PR #30'un değişikliklerini de diff içinde taşır. #30 ayrı PR olarak incelenip birleştirilmeden #66 ana dala taşınamaz; aksi hâlde aynı değişiklikler üst üste binebilir.
- Birleştirme veya dağıtım yapılmadı.

## SONRAKİ ADIM
1. PR #66 exact-head Android unit tests + APK + Full App QA Actions sonuçlarını al.
2. Başarısızlık olursa aynı testi körlemesine tekrarlama; log ve kök neden üzerinden düzelt.
3. Sonraki ayrı PR: Custom Tabs + H1 kanıt kartı; ardından Worker production GET kanıtı geldiğinde mobil HTML arama fallback'ini kapatıp JSON feed'e geç.