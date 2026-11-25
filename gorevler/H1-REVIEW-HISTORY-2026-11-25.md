# H1 — append-only insan inceleme geçmişi / 25.11.2026

## Görev
PR #71 Haber doğrulama çekirdeğinde, önceki insan inceleme kararının yeni kanıt eklenince veya yeniden inceleme yapılınca sessizce kaybolmasını engelle.

## Uygulanan değişiklik
- PR #71 dalı: `feat/h1-news-store-ui-20261125`.
- Exact HEAD: `2520b085bdd3e60a9f97ab42406147b1b57f3b07`.
- `CareerNewsReviewEvent` eklendi: karar, UTC zamanı, gerekçe ve karar anındaki kanıt kimlikleri saklanır.
- `withEvidence()` önceki kararı arşivler, sonra aktif kararı sıfırlar.
- `withHumanReview()` yeni karar yazmadan önce önceki kararı arşivler.
- Store geçmiş listesinin prefix olarak korunmasını şart koşar; reviewHistory silme/değiştirme ve yeni kanıt eklenirken eski kararın aktif kalması reddedilir.
- Activity eski incelemeleri ekranda gösterir.
- 2 model, 2 store ve 1 SQLite Activity instrumentation regresyon testi eklendi.
- Eski kayıt uyumu: `reviewHistory` alanı bulunmayan JSON boş geçmiş olarak okunur.

## CI / kanıt
- Exact-head APK + unit test: [run #45675851347](https://github.com/aries919-bot/Jarvis/actions/runs/45675851347) — SUCCESS; unit tests and APK build passed.
- Exact-head emulator + SQLite: [run #45675851473](https://github.com/aries919-bot/Jarvis/actions/runs/45675851473) — IN_PROGRESS; instrumentation step still running.
- Önceki HEAD emulator run #45673231634 yeni commit sonrası CANCELLED; PASS değildir.
- GitHub job log endpoint'i o anda BlobNotFound döndürdü; bu test sonucu değil, log erişim engelidir.

## Açık kalanlar
- Exact-head iki CI sonucu PASS olmalı.
- UI testleri bütün iddia/kanıt/inceleme/düzeltme düğmelerini uçtan uca tıklamıyor.
- Gerçek telefonda test yapılmadı.
- Kanıt ekleme ekranında kanıtın `publishedAt` tarihi girilemiyor; şimdilik null kalıyor.
- Düzeltme geçmişi önceki kanıtların ayrıntısını değil, yalnız sayısını gösteriyor.
- PR #70 kaynak/UA politikası ayrı; PR #71 tabanı #70'i içermiyor. Kaynak politikası ayrıca denetlenmeli.

## KARAR
**RED / beklemede.** Kod düzeltmesi eklendi; exact-head CI PASS ve telefon testi olmadan PR #71 kabul/merge edilmez.
