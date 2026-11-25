# DURUM — Aries

## Son güncelleme: 25 Kasım 2026

## Yetkili takip dosyaları
- İş sırası ve kararlar: `docs/HARITA.md`
- Sert kurallar ve H1/C1 kabul kapıları: `docs/NOT_C1.md`
- QA ekip ve test kanıtları: `gorevler/QA-EKIP-PANOSU-2026-11-25.md`
- H1 emulator hata raporu: `gorevler/H1-EMULATOR-FAIL-ANALYSIS-2026-11-25.md`

## Aktif iş — H1 Haber doğrulama
- **PR #71:** açık/taslak, merge edilmedi. Güncel HEAD `5df909fb1d0893aa0fb308f33b5fb8064aec8811`; 10 dosya / +2.607 / -26 (GitHub diff yeniden sayımı); test dosyalarından 20 model +10 store +4 instrumentation = **34 test**.
- Güncel exact-head APK/unit [#45685959491](https://github.com/aries919-bot/Jarvis/actions/runs/45685959491) **SUCCESS**; unit test ve APK derleme adımları tamamlandı.
- Güncel merge-ref Android emulator [#45685959506](https://github.com/aries919-bot/Jarvis/actions/runs/45685959506) son kontrolde **IN_PROGRESS**; merge-ref doğrulama ve AndroidX dependencyInsight PASS, instrumentation çalışıyor.
- Önceki emulator [#45679523114](https://github.com/aries919-bot/Jarvis/actions/runs/45679523114) **FAILURE**: 4 UI instrumentation testinden 1 PASS / 3 FAIL; üçü RootViewWithoutFocusException. Bu koşu kabul kanıtı değildir.
- Merge-ref workflow gerçek merge commit parent'larını kontrol eder: #70 merge commit `09f98b324a9a1f38dbbc25853ec2eba2fc544db9` + PR #71 HEAD. Eski PR API base.sha snapshot'ı güncel olmadığı için parent doğrulaması kullanılır.
- PR #71 için gerçek Android telefon testi yok; emulator 4/4 PASS ve telefon doğrulaması olmadan merge yok.
- **PR #69:** PR #71 ile 6 dosya ortak ve eski SharedPreferences/UI yaklaşımı taşıdığı için merge dışı bekletiliyor; #71 kabul edilirse superseded olarak kapatılır.
- H1 ilk açılışta SQLite load + koşulsuz save ana UI thread üzerinde; P2 takip işi `gorevler/H1-SQLITE-ASYNC-OPEN-2026-11-25.md`. Henüz kod değişmedi.

## Kaynak politikası
- **PR #70:** MERGED; commit `09f98b324a9a1f38dbbc25853ec2eba2fc544db9`; exact-head [#45677312902](https://github.com/aries919-bot/Jarvis/actions/runs/45677312902) SUCCESS ve scoped öz-QA ONAY.
- **C1 Worker PR #11:** RED. Kamu sayımı **12 kaynak /55 doğrulanmış açık ilan**; 100 hedefine 45 eksik. Worker production authenticated GET/D1 SKIPPED; H2 city null nedeniyle exact cross-feed dedup kanıtlanmıyor. Kamu raporu ayrı kalır.
- Resmî ilan keşif araştırmasında 7 açık duyuru adayı bulundu; pozisyon kontenjanları ilan kartı sayılmadı. 4 yeni kaynak adayı henüz feed'e alınmadı; sayım değişmedi.

## Mimari ve güvenlik
- `CareerActivity.kt` yaklaşık 2.795 satır; `CareerMonitor.kt` yaklaşık 2.049 satır. Küçük class-boundary extraction H1 kapıları sonrası.
- MainActivity ilk açılışta RECORD_AUDIO, CALL_PHONE, READ_CONTACTS, POST_NOTIFICATIONS topluca istiyor; CALL_PHONE ve READ_CONTACTS gerçek özelliklerde kullanılıyor. Just-in-time izin akışı ayrı güvenlik işi.
- `denetle.py` diff'in ilk 12 KB'sini Groq'a gönderdiği için çalıştırılmıyor.
- Android CI Gradle çağrısını birleştirme işi ayrı: test işçilerine TMDB/keystore sırlarını sızdırmama kapısı var.

## Çalışma kuralı
Önce H1 merge-ref emülatör sonucu, ardından gerçek telefon testi. Sonra H1 küçük performans işi ve C1 kamu kaynağı entegrasyonu. C1 kabul eşiği 10 resmî kaynak /100 doğrulanmış açık ilan ve production GET/D1 doğrulamasıdır. Film/tema/Borsa bu iş sırasının dışındadır.
