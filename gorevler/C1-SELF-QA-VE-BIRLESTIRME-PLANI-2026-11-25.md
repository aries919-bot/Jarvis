# Öz-QA ve birleştirme planı — 25.11.2026

## Yetki / kayıt kuralı
Bağımsız Hakem bulunmadığında veya Hakem'in isteğe bağlı AI adımı yetki/secret eksiğiyle çalışmadığında ChatGPT exact-HEAD öz-QA yapar. Bu karar **bağımsız insan/Claude incelemesi değildir**; PR yorumu ve bu dosyada açıkça “ChatGPT öz-QA” olarak yazılır. Öz-QA, test, diff kapsamı, kaynak sayımı, güvenlik ve kendi kod incelemesinin yerini almaz. Hata ya da görev kabul eşiği eksikse RED ve merge yok.

## H1 — PR #63

- PR: https://github.com/aries919-bot/Jarvis/pull/63
- Base: `durum`; head: `fix/H1-exact-categories-null-published-20261125`; exact HEAD `4e26b83c2ad265cf89347615cccd272f7813c0cd`.
- Diff yeniden sayımı: 4 dosya / +43 / -20. Dosyalar yalnız `scripts/h1_schema.py`, `scripts/test_h1_schema.py`, `docs/HABER_SEMA.md`, görev notu.
- Exact-head Actions [#45628989453](https://github.com/aries919-bot/Jarvis/actions/runs/45628989453): SUCCESS, 12/12 test.
- Denetim: enum tam olarak Kamu, Kura, KPSS, KPSS'siz, Büyük özel, Kamu bağlı şirket. Eski uzun kategori takma adları reddediliyor. `publishedAt=null` korunuyor; kaynak yayın tarihi vermediğinde deadline/başvuru açıklığı kanıtlıysa kayıt sayılabiliyor. `collectedAt` ve `firstSeenAt` yayın tarihi olarak kullanılmıyor.
- Eklenen satırlarda token/secret/private key/CV/e-Devlet/TCKN deseni 0. KPSS kelime eşleşmeleri örnek şart metinleridir; kullanıcıya ait puan/veri yok.
- ChatGPT öz-QA: **ONAY**, yalnız H1 şema sözleşmesi kapsamı. Bağımsız dış hakem yok.
- Birleştirme: 25.11.2026; PR #63 `durum` dalına squash edildi. Merge SHA: `4145ce51398c796e61a95305cc6d39d2779dbbec`. PR yorumu: 7166808934.
- Sınır: Bu birleştirme H1'in tüm kaynak/ilan doğrulamasını, C1'in 10/100 eşiğini, Worker/production veya Android entegrasyonunu kabul etmez.

## H2 / C1 — PR #61

- PR: https://github.com/aries919-bot/Jarvis/pull/61
- Base: `cline/K1` (`f22ba0b9487d88185f9cd277388403b4f8e3ab4d`); current head: `is/C1-h2-public-source-feed-20261125` (`fe37be93202442394504f455b4a1caeb31f298cf`). PR #65 BOTAŞ fix'i bu H2 dalına squash merge edildi.
- PR #61 cumulative diff: 5 dosya / +983 / -33; yalnız H2 workflow, `career/h2_sources.json`, H2 okuyucu, H2 testleri ve görev notu.
- Önceki exact-head PR actions: Python 160/160; H2 59/59; live #45540249638 artifact 15028921924 (tarihsel). Güncel, PR #65 düzeltmesi sonrası head için H2 PR tests [#45646718610](https://github.com/aries919-bot/Jarvis/actions/runs/45646718610) 60/60; Python [#45646718628](https://github.com/aries919-bot/Jarvis/actions/runs/45646718628) 161/161; K1-B [#45646718308](https://github.com/aries919-bot/Jarvis/actions/runs/45646718308) 24/24. PR Hakem [#45646718328](https://github.com/aries919-bot/Jarvis/actions/runs/45646718328) RED.
- Post-merge live reader [#45646698179](https://github.com/aries919-bot/Jarvis/actions/runs/45646698179), artifact [15037029949](https://github.com/aries919-bot/Jarvis/actions/runs/45646698179/artifacts/15037029949), exact head `fe37be93202442394504f455b4a1caeb31f298cf`, `collectedAt=2026-11-25T15:34:38Z`. Artifact JSON'larını yeniden saydım: 19 kaynak kaydı; 17 fresh / 2 stale; 13 doğrulanmış açık ilan kartı / 11 benzersiz kurum / 125 pozisyon. 125 pozisyon 100 ilan kartı eşiği değildir.
- Kaynak başına doğrulanmış açık kart: Anadolu 1; Atatürk 2; Balıkesir 1; DOKA 1; Düzce 1; Mardin Artuklu öğretim görevlisi 1; Marmara 2; ORAN 1; Tarsus 1; TGA 1; Zafer 1. Yeniden sayım: 13 kart / 11 kurum. Stale: BOTAŞ timeout, Mardin Artuklu öğretim üyesi deadline kanıtı yok. ATK başvuru açılışı 30.11.2026 09:00 +03; iki Munzur başvurusu 25.11.2026 mesai saati kesin olmadığından Doğrulanamadı; YHGM geçmiş kura.
- Stale: BOTAŞ (timeout; kaynak başına last-good izolasyonu) ve Mardin Artuklu öğretim üyesi (açık başvuru aralığı kanıtı yok). YHGM eski kura; ATK başvurusu 30.11.2026 09:00 +03'te açılacağı için henüz açık değil. Munzur'un 2 duyurusu 25.11.2026 mesai bitimi deadline'ı kesin saat içermediğinden Doğrulanamadı; 2 kart sayaç dışı. Yayın tarihi olmayan Balıkesir ve TGA kartlarında `publishedAt=null`.
- **Öz-QA düzeltmesi (PR #65):** BOTAŞ job/result parser kategorileri `Kamu bağlı şirket` yapıldı; `İlanlar` deadline bağlamı Türkçe büyük İ ile eşleştirildi. H2 60/60, tüm scripts 161/161, K1-B 24/24; Hakem #45646136859 ONAY; squash merge SHA `fe37be93202442394504f455b4a1caeb31f298cf`.
- Hakem job #45540323511 Cloudflare AI bootstrap eksikliği nedeniyle başarısız; bu bağımsız insan incelemesi değildir. Formal review sayısı 0.
- **Karar: RED; PR #61 birleştirilmez.** Önce iki BOTAŞ parser kategorisi H1 enum'una eşitlenmeli ve her iki çıktı için regresyon testi eklenmeli; ardından exact-head H2 + Python CI ve live reader yeniden çalışmalı. Ayrıca 13/100 kart eşiği, çapraz e-Şube/H2 tek-feed tekilleştirmesi ve production authenticated GET hâlâ eksik.
- PR #61 H2 sayımı e-Şube ile tek feed değildir. e-Şube snapshot `2026-11-25T13:49:08Z`: 1 kamu kaynağı / 42 ham / 42 benzersiz / 42 açık kart / 0 geçmiş, `publishedAt=null`. Bu sayı H2'nin 13 kartına eklenerek birleşik kabul sayısı diye sunulamaz; cross-feed dedup/Worker kanıtı yok.

## C1 güncel birleşik Hakem kapısı

- [C1 exact-head run #45634071990](https://github.com/aries919-bot/Jarvis/actions/runs/45634071990): 46/46 test, local D1 11/11, regresyon 17/17 PASS; Hakem raporu RED.
- Report [artifact 15030858902](https://github.com/aries919-bot/Jarvis/actions/runs/45634071990/artifacts/15030858902): 26 kaynak tanımı / 17 fresh / 9 stale; kamu 6 kaynak / 47 doğrulanmış açık kart; özel 114 adayın deadline'ı unknown, sayılmaz. 10/100 RED.
- Production authenticated GET [artifact 15032077702](https://github.com/aries919-bot/Jarvis/actions/runs/45634071990/artifacts/15032077702): BLOCKED; `JARVIS_WORKER_URL/JARVIS_WORKER_TOKEN` çifti yapılandırılmamış. Production deploy/POST yok.
- Bu C1 raporu PR #61'in genişletilmiş H2 feed'ini içermiyor. Ayrı feed sonuçlarını toplamak yasak; yeni H2 kaydı entegrasyona alınmadan önce cross-feed tekilleştirme gerekir.

## Sıradaki birleştirme sırası

1. PR #61'in H2 reader kapsamı, BOTAŞ parser kusuru PR #65 ile kapatıldı; current live H2 sayımı 13 verified card /11 institution. PR #61 global eşik sebebiyle RED kalır.
2. H2/e-Şube feed'lerini C1 collector'a bağla; kurum ve ilan kartı başına tekilleştir; source-specific last-good ve `firstSeenAt` korumasını test et.
3. 10 kaynak / 100 doğrulanmış açık ilanı gerçek kaynak sayılarıyla yeniden ölç. Altındaysa RED; arayüz/deploy yok.
4. Production authenticated GET-only ve D1 kalıcılık kanıtını al. Worker secret değerlerini loglama veya sohbete isteme.
5. H1/H2 kart sözleşmesi, kategori, tarih ve başvuru yönlendirmesi bütün kapılardan geçtikten sonra Android/Custom Tabs telefon testi.

## İşlem dışı PR'lar
- PR #58 tek seferlik e-Şube refresh tetik koludur; geçici workflow değişikliği merge edilmez.
- PR #59/#60 robots ve link-audit araçları açık/taslak; bu turda merge kararı verilmedi. Ayrı self-QA ve current-base diff denetimi gerekir.
## PR #65 BOTAŞ parser kusurunun düzeltilmesi — 25.11.2026

- Ayrı düzeltme PR'ı: https://github.com/aries919-bot/Jarvis/pull/65; base `is/C1-h2-public-source-feed-20261125`; head `3020790f3b450f6e0f0cf04cfb4df598b749a6a7`.
- Diff: 2 dosya / +16 / -3; yalnız `scripts/h2_announcement_reader.py` ve `scripts/test_h2_announcement_reader.py`.
- İlk koşu test kusurunu yakaladı: `application_deadline(pdf_text, "ilanlar")` Türkçe `İlanlar` metnini eşleştirmiyordu. İlk koşu H2 60 testte 1 error; scripts 161 testte 1 error. Context düzeltildi.
- Başarılı exact-head: H2 Reader #45646136860 60/60; Python Script Tests #45646137068 161/161; K1-B #45646137091 24/24; K1-B ölçümü 28 URL /12 ÇALIŞIYOR /16 ENGELLİ /0 ÖLÜ; deterministic Hakem #45646136859 ONAY.
- Güvenlik taraması ek satırlarında token/secret/private key/CV/KPSS kişisel veri/e-Devlet/TCKN eşleşmesi 0.
- Merge: squash, 25.11.2026, SHA `fe37be93202442394504f455b4a1caeb31f298cf`. PR #61 head'ine düzeltme girdi; PR #61 açık/taslak kaldı.
- Post-merge live #45646698179 başarıyla tamamlandı ve yeni source counts yukarıdaki tabloda kayıtlıdır. BOTAŞ parser düzeltmesi testlerde doğrulandı; bu koşuda BOTAŞ kaynağı timeout sebebiyle stale kaldı. PR #61 Hakem [#45646718328](https://github.com/aries919-bot/Jarvis/actions/runs/45646718328) RED.
- PR #61 genel RED; C1 10/100, cross-feed tekilleştirme ve production Worker GET kapıları devam ediyor.