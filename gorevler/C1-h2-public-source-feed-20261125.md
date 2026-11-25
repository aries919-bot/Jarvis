# C1 — H2 resmî kamu kaynaklarını genişletme (25.11.2026)

## Amaç
H2 official reader'a 7 yeni kurumun 9 duyuru kaydını eklemek; her kaynağın robots izni, yayın tarihi (yoksa null), deadline, pozisyon sayısı, ilk görülme ve toplama zamanını ayrı tutmak. İŞKUR ilan sayfası/Kariyer Kapısı kazınmaz; başvuru için resmî kurum sayfasına yönlendirilir.

## Kaynak ön denetim kanıtı
- Robots-only exact path: https://github.com/aries919-bot/Jarvis/actions/runs/45533905500 — job 138935687474 SUCCESS; artifact 14986480255.
- HTML link audit: https://github.com/aries919-bot/Jarvis/actions/runs/45533908007 — job 138935696949 SUCCESS; artifact 14986240689.
- Link audit test: https://github.com/aries919-bot/Jarvis/actions/runs/45533917072 — job 138935726682 SUCCESS.
- UA AriesKaynakTara/1.0; TLS doğrulaması açık; PDF dosyaları link audit aşamasında indirilmedi.
- RFC 9309 §2.3.1.3: robots 404 = NO_ROBOTS_FILE; 200 boş dosyada da yasak kuralı yok. 401/403/5xx/timeout/redirect UNKNOWN/fail-closed.

## Eklenen duyurular
1. TGA: 13 pozisyon; son başvuru 04.12.2026; TGA kariyer sayfası üzerinden çevrimiçi.
2. ORAN: 6 uzman + 1 destek = 7; ilanın kendi tablosunda tarih 19.10.2026; başvuru 09.11–03.12.2026.
3. Zafer: 2 uzman; deadline 14.12.2026; DOKA resmî sayfasındaki PDF kopyası kullanılır, kart bağlantısı Zafer'in kendi sayfasına gider.
4. DOKA: 1 uzman; deadline 06.12.2026.
5. Atatürk Üniversitesi: 20.11 ilanı 1 kadro; 16.11 profesör ilanı 70 kadro (PDF doğrulaması workflow çıktısında ayrıca kontrol edilecek).
6. Mardin Artuklu: 36 öğretim üyesi + 4 öğretim görevlisi = 40.
7. Balıkesir Üniversitesi: 7 öğretim elemanı kadrosu.

Toplam pozisyon adayı 141; 23'ü (TGA 13, ORAN 7, Zafer 2, DOKA 1) HTML/PDF metniyle önceden doğrulandı. Atatürk/Mardin/Balıkesir PDF metinlerinin exact-head reader doğrulaması tamamlanmadan toplam 141 doğrulanmış ilan diye raporlanmaz.

## Kod kapsamı
- career/h2_sources.json: 9 resmî duyuru kaydı, hepsi Kamu kategorisinde.
- scripts/h2_announcement_reader.py: yeni host allowlist'i, Balıkesir resmî host ailesi, boş 200 robots gövdesi, source-specific publishedAt ayrıştırma, null publishedAt opt-in, tekil son başvuru tarihi ayrıştırma, positionCount ve Zafer officialUrl override.
- scripts/test_h2_announcement_reader.py: robots, 403/500/timeout/empty, stale isolation, date parsing, null publishedAt, positionCount ve host-family testleri.
- .github/workflows/h2-announcement-reader.yml: bu görev dalında live reader çalıştırılır.

## Kabul ölçütü
1. Exact-head test workflow SUCCESS.
2. Live reader her kaynak için fresh/stale, ilan adedi, positionCount, publishedAt, deadline, collectedAt ve firstSeenAt üretir.
3. Deadline geçmiş ilan güncel listeden çıkar; gelecekte açılacak başvuru açık ilan sayılmaz.
4. Bir kaynak düşerse yalnız o kaynak stale kalır; diğer kaynakların son iyi verisi korunur.
5. Toplamlar kaynak bazında yeniden sayılır; 10 kaynak / 100 doğrulanmış açık ilan sağlanmadan C1 ONAY yok.

## Karar
**RED / doğrulama bekliyor.** Bu görev kod ve kaynak yapılandırmasıdır; Worker deploy, Android ekranı, branch silme, force-push veya merge yapmaz.

## İlk exact-head live ölçüm (başarılı, düzeltme öncesi)
- Run: https://github.com/aries919-bot/Jarvis/actions/runs/45534654328
- Job: 138938195142 SUCCESS
- Artifact: 14986776132
- collectedAt=2026-11-25T02:40:49Z
- 15 source definition; 9 fresh / 6 stale; 9 kart.
- Güncel ve açık doğrulanmış: **6 kurum / 83 pozisyon**:
  - Atatürk Üniversitesi 71 (70 profesör + 1 öğretim üyesi)
  - Zafer 2
  - DOKA 1
  - Anadolu Üniversitesi 1
  - Düzce Üniversitesi 5
  - Tarsus Üniversitesi 3
- YHGM kura deadline 13.10.2025 geçti; güncel sayıya dahil değil.
- ATK 181 pozisyon başvurusu 30.11.2026'da açılır; henüz güncel açık sayıya dahil değil.
- BOTAŞ kaynağı TIMEOUT_RETRY_FAILED; yalnız BOTAŞ stale kaldı.
- Yeni kaynak düzeltmeleri: TGA expectedText `13 (onüç) adet pozisyon`; ORAN `6 (altı) Uzman` / `1 (bir) Destek Personeli`; Mardin başlık metni eşleşmesi sadeleştirildi; Mardin öğretim görevlisi PDF URL'sindeki fazla boşluk düzeltildi; Balıkesir beklenen metni resmî sayfadaki `Son Başvuru Tarihi` metnine çekildi.
- Bu ölçüm düzeltme öncesidir; yeni exact-head live run bekleniyor. 10 kaynak / 100 pozisyon henüz sağlanmadığından RED.

## İkinci exact-head live ölçüm (TGA/ORAN/Balıkesir düzeltmelerinden önce)
- Run: https://github.com/aries919-bot/Jarvis/actions/runs/45535116804
- Job: 138939715351 SUCCESS; artifact 14986582434; collectedAt=2026-11-25T02:44:48Z.
- 15 kaynak kaydı; 10 fresh / 5 stale; 10 kart.
- Güncel açık doğrulanmış: **7 kurum / 87 pozisyon**: Atatürk 71, Zafer 2, DOKA 1, Mardin Artuklu öğretim görevlisi 4, Anadolu 1, Düzce 5, Tarsus 3.
- TGA deadline metni `Başvurular ... tarihinde başlayıp ... tarihinde sona erecektir` biçiminde; bu cümle biçimi için parser eklendi.
- ORAN tarih metni `03 Aralık 2026`; expectedText düzeltildi.
- Balıkesir'de publishedAt metaverisi bulunmadı; tarih uydurulmayıp `publishedAt:null` kabul edildi; deadline tekil `Son Başvuru Tarihi` etiketinden okunur.
- Mardin öğretim üyesi sayfasında 04.12.2026 yalnız başlık aralığında göründüğünden, sınav tarihi/başvuru deadline karışıklığı riskine karşı sayılmadı. Mardin öğretim görevlisi PDF'si ise açıkça 04.12.2026 son başvuru tarihini ve 4 kadroyu doğruluyor.
- BOTAŞ timeout; Mardin öğretim üyesi deadline kanıtı yok; yalnız ilgili kaynaklar stale/doğrulanamadı kaldı.

## Üçüncü exact-head live ölçüm (ORAN slash-format düzeltmesinden önce)
- Run: https://github.com/aries919-bot/Jarvis/actions/runs/45535597812
- Job: 138941422970 SUCCESS; artifact 14986608285; collectedAt=2026-11-25T02:51:27Z.
- 15 kaynak kaydı; 12 fresh / 3 stale; 12 kart.
- Güncel açık doğrulanmış: **9 kurum / 107 pozisyon**.
- Kaynak başına güncel açık pozisyon: TGA 13; Atatürk 71; Zafer 2; DOKA 1; Mardin Artuklu öğretim görevlisi 4; Balıkesir 7; Anadolu 1; Düzce 5; Tarsus 3. Toplam 107; 9 benzersiz kurum.
- ORAN yalnız expectedText biçimi nedeniyle stale kaldı; resmî sayfada son tarih `03/12/2026` olduğundan beklenen metin bu exact biçime düzeltildi. Böylece ORAN 7 pozisyon ve 1 kurum eklemelidir.
- Mardin Artuklu öğretim üyesi deadline başlık aralığından çıkarılmadı; 36 kadro sayılmıyor. BOTAŞ timeout/stale.
- Atatürk başvuru yöntemi resmî PDF'ye göre elektronik başvuru sistemine düzeltildi; Balıkesir şahsen/posta ve son gün mesai bitimi talimatı netleştirildi.
- Son ORAN düzeltmesi ve talimat güncellemesi sonrası exact-head live run bekleniyor.

## Eşik sayacı düzeltmesi — 25.11.2026
- C1'nin `10 kaynak / 100 doğrulanmış ilan` eşiği **ilan kartı** üzerinden hesaplanır. `positionCount` bir ilandaki kadro sayısını gösterir; tek kartı birden fazla ilana dönüştürmez. Örneğin 70 profesör kadrolu Atatürk duyurusu = 1 doğrulanmış ilan kartı + 70 pozisyon.
- Üçüncü live run #45535597812 artifact 14986608285, collectedAt=2026-11-25T02:49:15Z: 15 duyuru kaydı, 12 fresh / 3 stale. Yeniden sayım: **10 doğrulanmış açık ilan kartı / 9 benzersiz kurum / 107 pozisyon**. 100 ilan eşiği 107 pozisyonla sağlanmış olmaz.
- Kaynak kart sayımı: Anadolu 1; Atatürk 2; Balıkesir 1; DOKA 1; Düzce 1; Mardin Artuklu öğretim görevlisi 1; Tarsus 1; TGA 1; Zafer 1. Toplam 10 kart / 9 kurum. ORAN, Mardin öğretim üyesi ve BOTAŞ stale; ATK future-open; YHGM süresi geçmiş kura.
- e-Şube ayrı feed snapshot: 2026-11-24T23:51:48Z; 42 benzersiz/açık kayıt; 12 saatlik tazelik sınırı 2026-11-25T11:51:48Z. H2 ölçüm zamanında e-Şube snapshot fresh idi. Ham ayrı-feed toplamı 42 + 10 = 52 kart; çapraz-feed eşleştirme ve Worker tek-feed kanıtı henüz olmadığı için bunu tekil birleşik toplam diye sunma.
- PR #61 içinde `verifiedOpenListings` kart sayısını, `verifiedOpenPositions` pozisyon sayısını ayrı sayacak şekilde değiştirildi. Özet açıkça 100 eşiğinin kart sayısına göre hesaplandığını yazar. Testte iki Atatürk kartı = 2 ilan kartı, 71 kadro = 71 pozisyon olarak doğrulanır.
- Son ORAN slash-format düzeltmesi ve sayaç güncellemesinden sonra yeni exact-head live run zorunlu; 10/100 kararı eski 9/107 ölçümünden türetilmez.

## Dördüncü exact-code-head live ölçüm — kart/pozisyon ayrımı sonrası
- Run: https://github.com/aries919-bot/Jarvis/actions/runs/45536720514
- Job: 138945018271 SUCCESS; artifact 14987278818; collectedAt=2026-11-25T02:59:02Z.
- H2 parser/last-good/robots/stale/TLS testleri: `Ran 53 tests in 0.017s / OK`.
- Kaynak kaydı 15; **13 fresh / 2 stale**; toplam kart 13.
- H2 açık kamu: **10 benzersiz kurum / 11 doğrulanmış ilan kartı / 114 pozisyon**.
- İlan kartı kaynak başına yeniden sayım: Anadolu 1; Atatürk 2; Balıkesir 1; DOKA 1; Düzce 1; Mardin Artuklu 1; ORAN 1; Tarsus 1; TGA 1; Zafer 1. Toplam 11 kart / 10 kurum.
- Pozisyon toplamı (ayrı bilgi): Anadolu 1; Atatürk 71; Balıkesir 7; DOKA 1; Düzce 5; Mardin Artuklu 4; ORAN 7; Tarsus 3; TGA 13; Zafer 2 = 114.
- Stale yalnız BOTAŞ (iki timeout) ve Mardin Artuklu öğretim üyesi duyurusu (açık başvuru aralığı kanıtı yok). YHGM eski kura, ATK 181 başvuru açılış zamanı 30.11.2026 olduğundan açık iş sayılmaz.
- C1'nin 100 ilan kartı eşiği sağlanmadı: H2 11/100. `positionCount=114` bu eşiği sağlamaz.
- e-Şube ayrı feed snapshot 42 açık kayıt; collectedAt 2026-11-24T23:51:48Z ve fresh-until 2026-11-25T11:51:48Z. H2 ölçümünde fresh. H2 + e-Şube ham ayrı-feed toplamı 53 karttır; çapraz-feed dedup/tek Worker feed henüz doğrulanmadığından nihai birleşik sayım değildir.
- H2 source freshness ve card/position summary code PR #61 head 9cadd652ea2bb7c12d23aa6887956d0585fa535d; live workflow code head 518a95f27d7b20fbe32c7c5ae72d3bf28f51b85f. PR #61 test suite 154/154; H2 PR checks pass; formal GitHub reviews 0.
- Karar: RED. 10 kaynak eşiği H2 kurum sayısında karşılandı, 100 ayrı doğrulanmış ilan kartı ve Worker/D1/production GET şartları karşılanmadı.

## Beşinci exact-head live ölçüm — Marmara iki yeni duyurusu sonrası
- Run: https://github.com/aries919-bot/Jarvis/actions/runs/45539131064
- Job: 138953507700 SUCCESS; artifact 14987669118; collectedAt=2026-11-25T03:20:36Z.
- 17 source kaydı; 15 fresh / 2 stale; 15 toplam kaynak kartı.
- Güncel açık H2 kamu: 11 benzersiz kurum / 13 doğrulanmış açık ilan kartı / 125 pozisyon.
- Marmara 24.11.2026 duyuruları ayrı iki kart: öğretim elemanı 4 kadro + öğretim üyesi 7 kadro = 2 kart / 11 pozisyon. İkisi de deadline 08.12.2026, başvuru Marmara personel başvuru sistemi üzerinden.
- Kaynak kartları: Anadolu 1; Atatürk 2; Balıkesir 1; DOKA 1; Düzce 1; Marmara 2; Mardin Artuklu öğretim görevlisi 1; ORAN 1; Tarsus 1; TGA 1; Zafer 1 = 13 kart / 11 kurum.
- Pozisyonlar: Anadolu 1; Atatürk 71; Balıkesir 7; DOKA 1; Düzce 5; Marmara 11; Mardin Artuklu 4; ORAN 7; Tarsus 3; TGA 13; Zafer 2 = 125.
- Stale: BOTAŞ (timeout), Mardin Artuklu öğretim üyesi (deadline kanıtı yok). YHGM kura/sonuç; ATK future-open; bunlar açık işe dahil değil.
- e-Şube snapshot 42 kart (collectedAt 2026-11-24T23:51:48Z, fresh-until 2026-11-25T11:51:48Z). H2+e-Şube ham ayrı feed toplamı 55; cross-feed dedup / tek Worker feed kanıtı yok.
- Bu canlı run HEAD a08676419bea194891b87b79cff37cf922d829ec; Marmara iki duyurusu dahildi, Munzur iki kaydı henüz eklenmemişti.
- C1 eşiği kart sayısıyla: H2 13/100 RED. 125 pozisyon kart eşiğinin yerine geçmez.

## Munzur Üniversitesi — iki günlük duyuru adayının sınıflandırılması
- Resmî PDF: https://cdn.munzur.edu.tr/www/media/akademik-personel-ilani.pdf ; exact robots ön denetimi run #45538589906, robots HTTP 404 / NO_ROBOTS_FILE (erişim serbest). Web PDF metni açıldı; screenshot denemesi cache miss verdi.
- Öğretim üyesi bölümü: İlan Tarihi 10.11.2026, Son Başvuru Tarihi 25.11.2026 mesai bitimine kadar; 2026/18–2026/28 satırları, her biri 1 kadro = 11 pozisyon.
- Öğretim görevlisi bölümü: 2026/29 tek kadro; sınav takvimi 10.11.2026–25.11.2026 başvuru, 27.11 ön değerlendirme, 30.11 sınav; deadline 25.11.2026.
- Bu iki ayrı duyuru H2 kaynağı olarak geçici eklenmiştir (source config 19). Ancak deadline mesai bitimine kadar şeklinde, kesin saat yayımlanmadı; date-only otomatik sayaç aynı gün mesai sonrası yanlış açık gösterebileceği için canlı doğrulamada Doğrulanamadı / açık sayaca eklenmez kuralı uygulanmalı. PublishedAt öğretim görevlisi bölümünde açık yayın tarihi kanıtı yoksa null kalır.
- İlan PDF sayımını yeniden say: 11 profesör/doçent/doktor öğretim üyesi pozisyonu + 1 öğretim görevlisi pozisyonu; 2 kart, 12 pozisyon. Son başvuru tarihi bugün olduğundan, exact saat bilinmeden açık kart eşiğine ekleme.
