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
- Base: `cline/K1` (`f22ba0b9487d88185f9cd277388403b4f8e3ab4d`); head: `is/C1-h2-public-source-feed-20261125` (`8769ece09b1f85fb0326cb38d582c2fa46e70609`).
- Diff yeniden sayımı: 5 dosya / +967 / -30; yalnız H2 workflow, `career/h2_sources.json`, H2 okuyucu, H2 testleri ve görev notu.
- Exact-head PR Actions: Python script tests #45540323459 SUCCESS; H2 reader tests #45540323403 SUCCESS; K1-B measurement #45540323455 SUCCESS. Görev kaydındaki test sayıları: Python 160/160, H2 59/59. Live push run #45540249638 SUCCESS, artifact [15028921924](https://github.com/aries919-bot/Jarvis/actions/runs/45540249638/artifacts/15028921924). Son PR head'in live run kodundan farkı yalnız görev notuna 10 satır ekleyen doküman commitidir.
- Artefaktı açıp JSON'ları yeniden saydım: 19 kaynak kaydı; 17 fresh / 2 stale; 13 doğrulanmış açık ilan kartı / 11 benzersiz kurum / 125 pozisyon. 125 pozisyon 100 ilan kartı eşiği değildir.
- Kaynak başına doğrulanmış açık kart: Anadolu 1; Atatürk 2; Balıkesir 1; DOKA 1; Düzce 1; Mardin Artuklu 1; Marmara 2; ORAN 1; Tarsus 1; TGA 1; Zafer 1. Yeniden sayım: 13 kart / 11 kurum.
- Stale: BOTAŞ (timeout; kaynak başına last-good izolasyonu) ve Mardin Artuklu öğretim üyesi (açık başvuru aralığı kanıtı yok). YHGM eski kura; ATK başvurusu 30.11.2026 09:00 +03'te açılacağı için henüz açık değil. Munzur'un 2 duyurusu 25.11.2026 mesai bitimi deadline'ı kesin saat içermediğinden Doğrulanamadı; 2 kart sayaç dışı. Yayın tarihi olmayan Balıkesir ve TGA kartlarında `publishedAt=null`.
- **Öz-QA RED bulgusu:** `scripts/h2_announcement_reader.py` içindeki `parse_botas_job()` ve `parse_botas_result()` çıktısı `category="Kamu bağlı büyük şirket"` üretiyor. H1 enum'unda geçerli etiket `Kamu bağlı şirket`. H2 testindeki kategori kontrolü yalnız JSON kaynak tanımını kontrol ediyor; üretilen BOTAŞ kartını denetlemiyor. BOTAŞ son live run'da stale/timeout olduğu için bu hata canlı artefaktta görünmedi; kaynağın sonraki başarılı yanıtında H1 doğrulaması bozulabilir.
- Hakem job #45540323511 Cloudflare AI bootstrap eksikliği nedeniyle başarısız; bu bağımsız insan incelemesi değildir. Formal review sayısı 0.
- **Karar: RED; PR #61 birleştirilmez.** Önce iki BOTAŞ parser kategorisi H1 enum'una eşitlenmeli ve her iki çıktı için regresyon testi eklenmeli; ardından exact-head H2 + Python CI ve live reader yeniden çalışmalı. Ayrıca 13/100 kart eşiği, çapraz e-Şube/H2 tek-feed tekilleştirmesi ve production authenticated GET hâlâ eksik.
- PR #61 H2 sayımı e-Şube ile tek feed değildir. e-Şube snapshot `2026-11-25T13:49:08Z`: 1 kamu kaynağı / 42 ham / 42 benzersiz / 42 açık kart / 0 geçmiş, `publishedAt=null`. Bu sayı H2'nin 13 kartına eklenerek birleşik kabul sayısı diye sunulamaz; cross-feed dedup/Worker kanıtı yok.

## C1 güncel birleşik Hakem kapısı

- [C1 exact-head run #45634071990](https://github.com/aries919-bot/Jarvis/actions/runs/45634071990): 46/46 test, local D1 11/11, regresyon 17/17 PASS; Hakem raporu RED.
- Report [artifact 15030858902](https://github.com/aries919-bot/Jarvis/actions/runs/45634071990/artifacts/15030858902): 26 kaynak tanımı / 17 fresh / 9 stale; kamu 6 kaynak / 47 doğrulanmış açık kart; özel 114 adayın deadline'ı unknown, sayılmaz. 10/100 RED.
- Production authenticated GET [artifact 15032077702](https://github.com/aries919-bot/Jarvis/actions/runs/45634071990/artifacts/15032077702): BLOCKED; `JARVIS_WORKER_URL/JARVIS_WORKER_TOKEN` çifti yapılandırılmamış. Production deploy/POST yok.
- Bu C1 raporu PR #61'in genişletilmiş H2 feed'ini içermiyor. Ayrı feed sonuçlarını toplamak yasak; yeni H2 kaydı entegrasyona alınmadan önce cross-feed tekilleştirme gerekir.

## Sıradaki birleştirme sırası

1. PR #61 BOTAŞ kategori/çıktı test açığını ayrı, dar düzeltmeyle kapat; exact HEAD CI + yeniden canlı ölçüm.
2. H2/e-Şube feed'lerini C1 collector'a bağla; kurum ve ilan kartı başına tekilleştir; source-specific last-good ve `firstSeenAt` korumasını test et.
3. 10 kaynak / 100 doğrulanmış açık ilanı gerçek kaynak sayılarıyla yeniden ölç. Altındaysa RED; arayüz/deploy yok.
4. Production authenticated GET-only ve D1 kalıcılık kanıtını al. Worker secret değerlerini loglama veya sohbete isteme.
5. H1/H2 kart sözleşmesi, kategori, tarih ve başvuru yönlendirmesi bütün kapılardan geçtikten sonra Android/Custom Tabs telefon testi.

## İşlem dışı PR'lar
- PR #58 tek seferlik e-Şube refresh tetik koludur; geçici workflow değişikliği merge edilmez.
- PR #59/#60 robots ve link-audit araçları açık/taslak; bu turda merge kararı verilmedi. Ayrı self-QA ve current-base diff denetimi gerekir.
