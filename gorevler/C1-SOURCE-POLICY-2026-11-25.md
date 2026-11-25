# C1 — kaynak/UA politikası / PR #70 / 25.11.2026

## Kapsam
PR #70 `fix/c1-source-policy-ua-ci-20261125`, exact HEAD `7ab7aec5e5fa34209fec5c56d20a3c468af89a78`.
Değişen dosyalar 4: `Ai.kt`, `CareerMonitor.kt`, `CareerForbiddenSourcesPolicyTest.kt`, `CareerSourceCatalogTest.kt`. Diff: +70/-19.

## İlk CI ve hakem bulgusu
- Önceki HEAD `bf8ca39c1dee78b8436069e5cfabcbbd3afbc146` için Android APK/unit CI [#45658875411](https://github.com/aries919-bot/Jarvis/actions/runs/45658875411) SUCCESS; logda `testDebugUnitTest`, `BUILD SUCCESSFUL`, APK `assembleDebug` ve artifact upload başarıyla tamamlandı.
- Öz-QA'da sabit `isSecondary` listesinde SecretCV, Yenibiriş ve İşbul'un da kaldırıldığı görüldü. Bu üçü trustedDomains'de kaldığından `isTrustedUrl && !isSecondary` filtresinden geçip yeni ilan bildirimi üretebilirdi.

## Düzeltme
- `isSecondary()` artık sabit domain listesinden değil `sources.filterNot { it.official }` metadata'sından türetiliyor.
- Katalogdaki bütün resmî olmayan kaynaklar discovery-only/secondary kalıyor.
- Test: tüm `official=false` katalog kaynaklarının ikincil kalması ve SecretCV/Yenibiriş/İşbul'un primary alert olmaması.
- LinkedIn/Indeed katalog ve arama sorgularından çıkarıldı; genel `.edu.tr` trust kaldırıldı; izinli üniversite hostları açık allowlist'te.
- Kaynak HTTP isteklerinde `AriesKaynakTara/1.0` kullanılıyor; Mozilla/Chrome taklidi yok.

## Yeni exact-head CI
- Ara HEAD #45676772911 FAILED: 98 testten 2 assertion başarısız. Biri İşbul'un trusted olmasını varsayan yeni test, diğeri Memur365'in secondary olmamasını varsayan eski testti; iki beklenti düzeltildi.
- Güncel exact-head Android APK/unit [#45677312902](https://github.com/aries919-bot/Jarvis/actions/runs/45677312902) — son kontrolde IN_PROGRESS.
- Önceki HEAD run #45658875411 yeni değişiklikleri kapsamıyor; yeni HEAD için PASS yerine geçmez.

## KARAR
**RED / beklemede.** Yeni exact-head CI tamamlanmadan PR #70 ONAY/merge edilmez.
