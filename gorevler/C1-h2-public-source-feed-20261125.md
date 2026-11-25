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
