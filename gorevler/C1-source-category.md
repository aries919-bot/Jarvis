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

## Güncel paralel QA ve Hakem
- Workflow: [C1 parallel QA and Hakem](https://github.com/aries919-bot/Jarvis/blob/qa/c1-private-runner/.github/workflows/c1-private-backend-qa-public.yml)
- Run: [#45504819883](https://github.com/aries919-bot/Jarvis/actions/runs/45504819883)
- Exact private SHA: `68e858424c3589b7d6cffea7ba26c7941027b017`
- Exact base SHA: `74816f3954c3fa8164ef0266b848b0b9e9a897d4`
- Ara sonuç: preflight, unit tests, public-data contract, failure matrix, security-diff ve live-source-audit PASS; production authenticated GET BLOCKED (URL/token ayarı eksik); local Worker+D1 smoke tamamlanmayı bekliyordu; Hakem kararı henüz bekleniyor.
- Yeni QA JSON'u kaynak başına `category`, `collectedAt`, `lastGoodAt`, `lastAttemptAt`, `lastAttemptStatus` ve `lastError` alanlarını toplar. Kategori kapısı kamu için `Kamu`, private ATS için `Büyük özel` bekler.

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
