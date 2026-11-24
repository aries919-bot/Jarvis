# C1 kaynak kategori alanı — QA ve düzeltme

## Rapor kaynaklı bulgu
- Hakem raporu: [run #45503911811](https://github.com/aries919-bot/Jarvis/actions/runs/45503911811)
- JSON: [qa-reports/c1/latest.json](https://github.com/aries919-bot/Jarvis/blob/durum/qa-reports/c1/latest.json)
- Exact private SHA: `74816f3954c3fa8164ef0266b848b0b9e9a897d4`
- Raporun tüm private source summary satırlarında `category=null` görünüyordu; kaynak kataloğunda özel kaynaklar `Büyük özel` olarak tanımlıydı. Kaynak özetinin üç dönüş yolunda (başarılı, robots engeli/erişilemiyor, HTTP/timeout hatası) kategori alanı kayboluyordu.

## Ayrı düzeltme PR
- PR #7: https://github.com/aries919-bot/jarvis-cloud/pull/7
- Branch: `fix/c1-source-category` → `is/c1-career-feed`
- HEAD: `0eeefed9534040a46ce7ef83157c0d6872b7f0b0`
- Diff: 2 dosya, +9 / -0 (yeniden sayım compare ile doğrulandı)
- Dosyalar: `src/career-feed.js`, `tests/career-feed.test.mjs`
- Üç dönüş yolunda `category` korunuyor; başarı, robots deny/unreachable, HTTP hata ve timeout testlerine kategori regresyon kontrolleri eklendi.

## Yenilenen paralel QA
- Workflow: [C1 parallel QA and Hakem](https://github.com/aries919-bot/Jarvis/blob/qa/c1-private-runner/.github/workflows/c1-private-backend-qa-public.yml)
- Run: [#45504531310](https://github.com/aries919-bot/Jarvis/actions/runs/45504531310)
- Private SHA: `0eeefed9534040a46ce7ef83157c0d6872b7f0b0`
- Bu koşu kategori sözleşmesini test eder; source JSON'a `category`, `lastAttemptAt`, `lastError` ve `collectedAt` alanlarını da yazar.
- Sonuç: QA ve Hakem job'ları tamamlanınca güncellenecek. Sonuç gelmeden PASS/ONAY varsayılmaz.

## Sert kabul kapıları
- [ ] Exact-head C1 tests green.
- [ ] Local Worker + D1 smoke green.
- [ ] Live source audit categories: public `Kamu`, private `Büyük özel`; category error list empty.
- [ ] Per-source error/status/last-good fields visible in QA JSON.
- [ ] Hakem JSON decision checked from `durum/qa-reports/c1/latest.json`.
- [ ] Public evidence reported separately; public 0 = RED.
- [ ] At least 10 verified sources and 100 verified, non-expired, deadline-confirmed listings. Current previous report was 1/42; do not count unknown-deadline candidates.
- [ ] Production authenticated Worker GET confirmed separately. Local D1 is not production evidence.
- [ ] No merge without all required CI, Hakem ONAY, narrow diff, independent review, and Cem's explicit merge authorization.

## Current rule
This repair does not change the 10-source/100-listing threshold, public gate, date rules, robots rules, source access or production deployment.
