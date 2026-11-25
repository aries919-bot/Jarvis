# CI Alternatif Doğrulama Planı — 26.11.2026

## Amaç
Aynı başarısız CI komutunu tekrarlamayı durdurmak. H1 Android UI ve Cloud Worker C1/QA testleri için birbirinden bağımsız kanıt yolları kurmak. Bu dosya plan/iş kaydıdır; test geçmişini PASS olarak değiştirmez.

## Canlı durum — GitHub API'den yeniden okundu
- Jarvis PR #71 HEAD: `4398073b3759109fa27a8172956cbea13f31ce27`, açık/taslak, merge edilmedi.
- Aynı HEAD APK run [#45707502321](https://github.com/aries919-bot/Jarvis/actions/runs/45707502321): `completed/success`.
- Aynı HEAD H1 emulator run [#45707502311](https://github.com/aries919-bot/Jarvis/actions/runs/45707502311): `in_progress`. Job adımı 7 emulator instrumentation; checkout, PR head/base doğrulaması, Java, Gradle ve AndroidX dependencyInsight adımları başarılı. XML doğrulaması ve artifact henüz bekliyor.
- Jarvis PR #71 diff: 14 dosya / +3.370 / -26 (PR API sayımı).
- Cloud PR #12 HEAD: `1692930b60b952589fdf5d849010a177e288bfb2`, açık/taslak, merge edilmedi.
- Cloud son exact-head run [#45697424481](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45697424481): failure; job `runner_id=0`, `runner_name=""`, `steps=[]`, 2 saniyede tamamlandı. Bu, testlerin çalışıp başarısız olduğunu kanıtlamaz; runner/job-start seviyesinde blokajdır.
- Cloud son 12 kayıtlı workflow çalışmasının tamamı failure. Aynı commit/rerun döngüsü yapılmayacak.

## H1 — farklı doğrulama yolu
1. Devam eden #45707502311 tamamlanmadan yeni emulator commit'i veya rerun yapılmayacak.
2. Sonuçta yalnız GitHub conclusion değil, artifact XML yeniden sayılacak: beklenen 4 testcase; failure/error/skipped = 0. Gradle log exit code ile XML sonucu birbiriyle tutarlı olmalı.
3. 3 UI test yine `RootViewWithoutFocusException` verirse aynı koşullarda rerun yok. İlk iş, hata anındaki `adb shell dumpsys window windows`, `dumpsys activity activities`, `dumpsys input` ve ilgili logcat'i artifact'e alacak teşhis adımı eklemek. Amaç test anında hangi pencerenin focus tuttuğunu kanıtlamak.
4. ActivityScenario ile test edilen uygulama Activity'sinin RESUMED olması tek başına pencere focus kanıtı değildir. Test başlangıcında ekran/uyandırma/keyguard durumu ve foreground Activity ölçülmeli; UI assertion'dan önce odak kontrolü açıkça raporlanmalı.
5. Focus sorunu uygulama kodundaysa gerçek pencere/Activity yaşam döngüsü düzeltilir. Yalnız runner/emulator imajı sorunu kanıtlanırsa API 34 Google APIs standart imajında ikinci bir bağımsız koşu denenir. Test skip, assumption, retry ile yeşile boyama yasak.
6. Android UI doğrulamasından bağımsız olarak model/store testleri ve SQLite bozuk-primary/last-good senaryosu ayrı JVM/unit testlerinde tutulur; bunlar UI testlerinin yerine geçmez.
7. Emulator PASS olsa bile gerçek Android telefon testi ayrı kabul kapısıdır.

## Cloud Worker PR #12 — CI dışı, ama bağımsız kanıt
1. Runner id=0 / steps=[] sonuçları kaynak kod test hatası sayılmaz. Aynı workflow'a commit basıp tetikleme yapılmayacak.
2. Önce GitHub Actions service/runner incident ve repository workflow/job configuration kanıtı araştırılır; bekleme süresi sonrası yalnız tek kontrollü workflow_dispatch veya yeni runner image/queue denemesi yapılır. Tekrarlayan runner tahsis hatasında bu yol bloke olarak işaretlenir.
3. Kod doğrulaması CI'dan bağımsız yerel Node.js yoluna taşınır: sabit fixture'lar + `node --check` + `npm run test:c1`; D1/Workers AI bağımlılıkları adapter/mock sınırında izole edilir. Test çıktısı, Node sürümü, komut, exit code ve test sayısı kaydedilir. Bu yol gerçek Cloudflare production GET/D1 kanıtı değildir.
4. D1 davranışı için local D1/Miniflare entegrasyon testi ayrı; production authenticated GET ve D1 kanıtı ayrı kapıdır. Birbirinin yerine geçmez.
5. PR #12 draft kalır. C1 10 kaynak/100 doğrulanmış kamu ilanı, source-level freshness, cross-feed dedup ve production GET/D1 şartları sağlanmadan merge/deploy yok.

## C1 ilan sayımını ilerletmek için alternatif
- Eski 47 last-good kamu kaydını güncel ilan gibi göstermeden koru.
- 2 kamu +1 özel yeni resmî ilan adayını exact dedup, resmi URL, sourceId, şehir ve deadline ile Worker fixture'ına al; önce fixture/contract test, sonra feed ingest. Kontenjan sayısı ilan kartı sayısı değildir.
- Kaynak başına son iyi veri tutulur; bir kaynağın 403/timeout/robots/boş cevabı diğer kaynakları stale yapmaz.
- İŞKUR ilan sayfası ve Kariyer Kapısı kazınmaz; e-Şube JSON ve resmî başvuru sayfasına yönlendirme kuralı korunur.

## Durdurma koşulları
- Aynı commit üzerinde aynı test, aynı ortam, aynı sonuç: tekrar yok.
- Runner başlamadıysa kod hatası teşhisi yok.
- XML 4/4 PASS olmadan H1 PASS yok.
- CI yoksa yerel test CI PASS diye sunulmaz.
- Public listing 0 ölçüldüyse RED; stale last-good kayıtlar 0 ilan diye silinmez.
- Hiçbir PR otomatik birleştirilmez.

## SONRAKİ ADIM
#45707502311 tamamlandığında XML ve log artifact'ini oku. Sonuç 4/4 değilse hata anı window/focus teşhisini ekle; Cloud PR #12 için yeni commit/rerun yerine Node.js + local D1 bağımsız test yolunu uygula.
