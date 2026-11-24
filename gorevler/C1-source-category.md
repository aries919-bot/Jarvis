# C1 kaynak kategori alanı — QA ve düzeltme

## Rapor kaynaklı bulgu
- İlk Hakem raporu: [run #45503911811](https://github.com/aries919-bot/Jarvis/actions/runs/45503911811)
- JSON: [qa-reports/c1/latest.json](https://github.com/aries919-bot/Jarvis/blob/durum/qa-reports/c1/latest.json)
- Exact private SHA: `74816f3954c3fa8164ef0266b848b0b9e9a897d4`
- Raporun private source summary satırlarında `category=null` görünüyordu; kaynak kataloğunda özel kaynaklar `Büyük özel` olarak tanımlıydı. `readSource()` üç dönüş yolunda (robots sonucu, başarı/empty, fetch error) kategoriyi düşürüyordu.

## İlk PR ve neden superseded oldu
- PR #7: https://github.com/aries919-bot/jarvis-cloud/pull/7 — kapatıldı, merge edilmedi.
- #7, PR #6'nın snapshot timestamp düzeltmesini içermeyen eski C1 tabanından açılmıştı.
- QA [#45504531310](https://github.com/aries919-bot/Jarvis/actions/runs/45504531310) bu nedenle kamu sözleşmesinde `source snapshot time not preserved` hatası verdi. Bu eşik gevşetilmedi; rapor doğru şekilde RED kaldı.

## Geçerli stacked düzeltme PR
- PR #8: https://github.com/aries919-bot/jarvis-cloud/pull/8
- Branch: `fix/c1-source-category-after-timestamp` → `fix/c1-source-snapshot-time` (PR #6 tabanı)
- HEAD: `68e858424c3589b7d6cffea7ba26c7941027b017`
- Base SHA: `74816f3954c3fa8164ef0266b848b0b9e9a897d4`
- Compare yeniden sayımı: 2 dosya, +9 / -0; yalnız `src/career-feed.js` ve `tests/career-feed.test.mjs`.
- Üç `readSource()` dönüş yolunda `category` korunuyor; başarı, robots deny/unreachable, HTTP hata ve timeout testlerine kategori regresyon kontrolleri eklendi.
- PR #8 taslak/açık; merge edilmedi.

## Nihai stacked PR ve paralel Hakem sonucu
- Eski tabanlı PR #7 kapatıldı. Geçerli kategori düzeltmesi [PR #8](https://github.com/aries919-bot/jarvis-cloud/pull/8): `fix/c1-source-category-after-timestamp` → PR #6'nın `fix/c1-source-snapshot-time` tabanı; HEAD `68e858424c3589b7d6cffea7ba26c7941027b017`; diff yeniden sayımı 2 dosya / +9 / -0.
- PR #6 timestamp düzeltmesi taban SHA `74816f3954c3fa8164ef0266b848b0b9e9a897d4`; PR #6 açık/taslak. PR #8 de açık/taslak; ikisi de merge edilmedi.
- Son paralel QA [#45504819883](https://github.com/aries919-bot/Jarvis/actions/runs/45504819883), exact SHA #8. Nihai JSON: [qa-reports/c1/latest.json](https://github.com/aries919-bot/Jarvis/blob/durum/qa-reports/c1/latest.json). Karar **RED**.
- Test: `npm run test:c1` 38/38; `node scripts/c1-worker-d1-smoke.mjs` 11/11; 12/12 senaryo kapsamı; kamu tarih/freshness sözleşmesi PASS; kategori hatası listesi boş; security diff 2 dosya /1.638 bayt / yasaklı desen 0.
- Kaynak yeniden sayımı: 16 toplam /10 fresh /6 stale. Özel fresh kaynaklar: Trendyol 35, Midas 16, Commencis 5, Dream Games 17, Aleph 5, Ajax Systems 13, Insider One 22, Binance 0, Picus 1 =114 private aday; 114 deadline unknown ve doğrulanmış açık private ilan 0. Altı Ashby robots HTTP 401 stale; API isteği yok.
- Kamu ayrı: e-Şube 1 kaynak /43 raw unique /42 açık /1 expired; snapshot `2026-11-24T11:57:55Z` (audit anında 10,9 saat); `publishedAt=null`. Doğrulanmış kaynak/ilan toplamı 1/42; 10/100 şartı RED.
- Production authenticated Worker GET BLOCKED: `JARVIS_WORKER_URL`/`JARVIS_WORKER_TOKEN` yok. Production deploy/Cloudflare D1 doğrulanmadı.

## Sert kabul kapıları
- [ ] Exact-head C1 tests green.
- [ ] Local Worker + D1 smoke green.
- [ ] Live source category assertion passes and `categoryErrors=[]`.
- [ ] Per-source status, robots, HTTP, last-good, last-attempt and error diagnostics present.
- [ ] Final Hakem JSON checked from `durum/qa-reports/c1/latest.json`.
- [ ] Public evidence reported separately; public 0 = RED.
- [ ] At least 10 verified sources and 100 verified, non-expired, deadline-confirmed listings. Unknown-deadline candidates never count.
- [ ] Production authenticated Worker GET confirmed separately. Local D1 is not production evidence.
- [ ] No merge without all required CI, Hakem ONAY, narrow diff, independent review, and Cem's explicit merge authorization.

## Değişmeyen kurallar
This repair does not change the 10-source/100-listing threshold, public gate, date rules, robots rules, source access or production deployment.
