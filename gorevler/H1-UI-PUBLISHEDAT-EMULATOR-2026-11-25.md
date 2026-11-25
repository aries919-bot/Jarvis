# H1 — yayın tarihi, düzeltme kanıtları ve Android UI doğrulaması / 25.11.2026

## Görev
PR #71'de insan inceleme geçmişi append-only düzeltmesini tamamla; kullanıcıya kanıtın gerçek yayın tarihini girebilme, düzeltmede eski kanıtı görebilme ve gerçek UI akışını Android instrumentation ile doğrulama imkânı ver.

## Uygulanan değişiklik
- PR #71: https://github.com/aries919-bot/Jarvis/pull/71
- Dal: `feat/h1-news-store-ui-20261125`
- Exact HEAD: `6b392a3aa02d42700743657350d081d7f7ad6aeb`
- `CareerNewsReviewEvent`: eski insan kararı, UTC zamanı, gerekçe ve o an kullanılan evidence ID'leri append-only.
- Yeni evidence eski incelemeyi arşivler; aktif karar resetlenir. Yeniden inceleme önceki kararı arşivler.
- Store geçmiş silme/yeniden yazma girişimlerini reddeder.
- Kanıt formuna isteğe bağlı `publishedAt` UTC alanı eklendi. Boş değer null; geçersiz tarih model validation'dan geçmez.
- Correction geçmişi artık previousEvidence publisher/sourceType/stance/excerpt/publishedAt/checkedAt ve kaynak açma bağlantısını gösterir.
- Android UI testinde iddia ekle → yayın tarihli kanıt ekle → insan incelemesi → yeni kanıt ekleyerek önceki incelemeyi arşivle → iddiayı düzelt → eski kanıt ayrıntısını göster akışı yürütülüyor.

## Sayım / diff
- Değişen dosya: 10.
- Diff: +2.578 / -26.
- Test sayımı (kaynak dosyalarında `@Test` yeniden sayımı): 20 model + 10 SQLite store + 4 Android instrumentation = **34 test**.

## Exact-head CI
- APK + unit: [#45679523180](https://github.com/aries919-bot/Jarvis/actions/runs/45679523180) — son kontrolde IN_PROGRESS.
- Emulator + SQLite: [#45679523114](https://github.com/aries919-bot/Jarvis/actions/runs/45679523114) — son kontrolde IN_PROGRESS.
- Önceki emulator #45675851473 yeni HEAD commit'i nedeniyle CANCELLED (18:52:27Z); PASS değildir.
- Eski exact-head APK/unit #45675851347 SUCCESS'ti ama yeni UI değişikliklerini kapsamaz.

## KARAR
**RED / beklemede.** Güncel HEAD'in hem APK/unit hem emulator/SQLite sonuçları SUCCESS olmadan PR #71 kabul/merge edilmez. Gerçek telefonda test ayrıca gereklidir.

## SONRAKİ ADIM
1. Yalnız #45679523180 ve #45679523114 exact-head sonuçlarını kontrol et.
2. İkisi PASS ise PR #71 diff'ini tekrar sıfırdan incele; gerçek telefon testi için gereken ekran adımlarını kaydet.
3. H1 kabulünden sonra onaylı C1 kaynak politikası PR #70'i sıradaki C1 işi olarak birleştir.
4. C1 PR #11 halen RED: 12 kamu kaynağı /55 doğrulanmış ilan, 45 eksik; Worker production authenticated GET/D1 ve şehir kanıtlı cross-feed dedup yok.

## Ayrı C1 sınırı
C1 10 kaynak /100 doğrulanmış açık ilan ve kamuya özel rapor şartı değişmez. H1 testleri C1 kabulü yerine geçmez.
