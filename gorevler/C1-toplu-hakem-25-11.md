# C1 toplu hakem özeti — 25.11.2026

## Karar
**RED — birleştirme yok.** Bu rapor ChatGPT'nin iç denetimidir; bağımsız Claude/insan hakem onayı değildir.

## Birleşik PR kapsamı
- Ana C1 PR #5: `is/c1-career-feed` → `main`, açık/taslak, 13 dosya, +2.698/-7. PR'ın kendi geniş diff'i 60 KB hakem sınırını aşıyor.
- PR #6: `fix/c1-source-snapshot-time` → `is/c1-career-feed`, açık/taslak.
- PR #8: `fix/c1-source-category-after-timestamp` → PR #6, açık/taslak.
- Birleşik compare `is/c1-career-feed...fix/c1-source-category-after-timestamp`: 7 commit, 7 dosya. Dosyalar: `.github/workflows/validate.yml`, `src/career-feed-store.js`, `src/career-feed.js`, `src/esube-input.js`, `tests/career-feed-state.test.mjs`, `tests/career-feed.test.mjs`, `tests/esube-input.test.mjs`.
- PR #8'in kendi dar diff'i: 2 dosya, +9/-0 (`src/career-feed.js`, `tests/career-feed.test.mjs`).
- GitHub PR #8 konuşmasına toplu iç denetim notu eklendi: comment ID `7147163607`. PR bağlantısı: https://github.com/aries919-bot/jarvis-cloud/pull/8

## Kod ve CI kanıtı
Son exact-head C1 QA: https://github.com/aries919-bot/Jarvis/actions/runs/45504819883
- `npm run test:c1`: 38/38 PASS.
- `node scripts/c1-worker-d1-smoke.mjs`: 11/11 PASS; yalnız yerel Wrangler + D1, üretim değil.
- Hata/regresyon matrisi: 12/12 kapsam PASS.
- Kamu tarih/freshness sözleşmesi, kategori denetimi ve PR #8 security/diff kapısı PASS.
- PR #6 timestamp düzeltmesi: kaynak snapshot `collectedAt` / `lastGoodAt` korunur; feed deneme zamanı `lastAttemptAt` olarak ayrılır.
- PR #8 kategori düzeltmesi: `category` başarı, robots ve hata dönüşlerinde korunur.

## Kaynak ve ilan yeniden sayımı
Son C1 Hakem JSON: https://github.com/aries919-bot/Jarvis/blob/durum/qa-reports/c1/latest.json
- Toplam kaynak: 16; fresh 10; stale 6.
- Özel kaynaklardan 9 fresh kaynakta aday sayısı: Trendyol 35, Midas 16, Commencis 5, Dream Games 17, Aleph 5, Ajax Systems 13, Insider One 22, Binance 0, Picus Security 1. Yeniden toplama: 35+16+5+17+5+13+22+0+1 = 114.
- Bu 114 özel adayın deadline alanı unknown; doğrulanmış açık özel ilan sayısına katılmadı.
- Altı Ashby kaynağında robots.txt HTTP 401: hedef API isteği gönderilmedi; kaynaklar stale kaldı.
- Kamu ayrı: e-Şube 1 kaynak, 43 ham/benzersiz kayıt, 42 açık ve 1 süresi geçmiş. Snapshot zamanı `2026-11-24T11:57:55Z`; önceki audit sırasında 10,9 saatlikti.
- Önceki Hakem sayımı: 1 doğrulanmış kaynak / 42 açık ilan; eşik 10 kaynak / 100 doğrulanmış ilan. **RED**.

## Güncellik ve üretim engelleri
- Bugün 25.11.2026 olduğundan e-Şube snapshot'ı 12 saat tazelik sınırını aşmıştır; yeni başarılı çekim olmadan güncel kamu kanıtı sayılamaz.
- Üretim authenticated GET, `JARVIS_WORKER_URL` ve `JARVIS_WORKER_TOKEN` ayarları bulunmadığı için BLOCKED. Yerel D1 testi üretim kanıtı değildir. Production deploy yapılmadı.
- H2 resmi duyuru okuyucusu run #45489853859 attempt 2 olarak yeniden başlatıldı. Job ID `138848189009`, `Read measured official announcements` adımında çalışıyor. Bu yenileme tamamlanmadan H2 için yeni kaynak/ilan sayısı ileri sürülmez. Run: https://github.com/aries919-bot/Jarvis/actions/runs/45489853859
- H2 kaynak akışı ayrı; mevcut C1 Hakem raporuna otomatik birleştirildiği kanıtlanmadı. Eski H2 sayıları yeni taze kanıt yerine kullanılamaz.

## Karar kapıları
1. 10 kaynak / 100 deadline-doğrulanmış, süresi geçmemiş ilan: karşılanmadı.
2. Kamu kaynağı ve güncel kamu ilanı: önceki raporda var, ancak kaynak snapshot'ı artık stale.
3. Production authenticated Worker GET: kanıtlanmadı.
4. PR #5'in büyük diff ve ana kabul koşulları: çözülmedi.
5. PR #6 ve #8: açık/taslak; merge edilmedi.

## Sonraki adım
Önce H2 ve e-Şube için taze kaynak çekimini tamamla; kaynak başına ilanları ve her ilan için deadline kanıtını yeniden say. Yalnız sonra yeni C1 Hakem raporu üret. Unknown deadline kayıtları doğrulanmış sayılmaz; robots 401/deny durumunda API çağrısı yapılmaz. Production Worker GET kanıtı olmadan ONAY verilmez.
