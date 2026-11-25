# H1 — Haber iddia/kanıt/düzeltme çekirdeği
Tarih: 25.11.2026
Durum: PR #69 AÇIK / TASLAK / EXACT-HEAD CI DEVAM EDİYOR

## Amaç
Mevcut Haber arşivi başlık, bağlantı, snippet ve sınıflandırma saklıyor; iddia atomları, destekleyen/çelişen kanıtlar, insan incelemesi ve düzeltme geçmişi için veri sözleşmesi yok. Bu iş, mevcut haber kayıtlarına dokunmadan bu çekirdeği ayrı ekler.

## Dal / PR
- Repo: aries919-bot/Jarvis
- Taban: kariyer-modulu
- Taban SHA: 4b270245019ed322dc2dc1c735c715c0934076a2
- Dal: feat/h1-story-claim-evidence-ci-20261125
- HEAD: 36cba13cb382fb55a8a583c878177429f69c42f6
- PR #69: https://github.com/aries919-bot/Jarvis/pull/69 (aktif)
- Eski PR #68 kapatıldı (unmerged), çünkü `is/c3-ilan-semasi-ai-kaynak-kaniti` hedefinin workflow filtresi CI tetiklemiyordu.
- Durum: açık/taslak; merge yok; kullanıcı/gerçek cihaz doğrulaması yok.

## Diff yeniden sayımı
- Compare exact taban/head: 8 commit; 2 dosya; +741 / -0; 0 behind.
- Yeni production dosyası CareerNewsVerification.kt: 532 ek satır.
- Yeni test dosyası CareerNewsVerificationTest.kt: 274 ek satır; 12 @Test.
- Eski Haber arşivi, SharedPreferences kaydı, CareerActivity UI, C1 feed, Worker ve kaynak okuyucuları bu PR'da değişmedi.

## Model
- Story: storyId (URL+başlık kararlı kimliği), headline, publisher, originalSourceUrl, publishedAt (kaynakta yoksa null), collectedAt ve firstSeenAt ayrı alanlar.
- Claim: tek/atomik ve test edilebilir iddia; tek story altında çoklu claim desteklenir.
- Evidence: yayıncı, HTTPS kaynak URL'si, sourceType (PRIMARY_SOURCE / INDEPENDENT_REPORTING / SECONDARY / UNKNOWN), stance (SUPPORTS / CONTRADICTS / CONTEXT_ONLY), kaynak alıntısı, nullable publishedAt ve checkedAt.
- Review: CONFIRM / REFUTE / INCONCLUSIVE; karar verilmişse reviewedAt zorunlu. İnceleyen kişiye ait hesap/kimlik saklanmaz.
- Correction: claimId, önceki/yeni verdict, UTC düzeltme zamanı, gerekçe ve kaynak URL'si; kayıt append-only.

## Fail-closed karar kuralları
1. Birincil kaynak kanıtı + açık insan CONFIRM + çelişen kanıt olmadan Doğrulandı üretilmez.
2. Birincil kaynak çelişki kanıtı + açık insan REFUTE + destekleyen kanıt olmadan REFUTED üretilmez.
3. Hem destekleyen hem çelişen kanıt varsa insan onayı bulunsa da CONFLICTING_EVIDENCE.
4. Yalnız ikincil/bağlam kanıtı varsa CONTEXT_MISSING; bu içerik tek başına doğrulanmış sayılmaz.
5. Yeni kanıt eklenince önceki insan kararı sıfırlanır.
6. Claim düzeltilince eski kanıt ve inceleme yeniden kullanılamaz; geçmişe önceki/yeni verdict eklenir, yeni kanıt ve yeni inceleme gerekir.
7. publishedAt hiçbir durumda collectedAt veya firstSeenAt ile doldurulmaz. UTC timestamp biçimi ve gerçek takvim aralığı kontrol edilir.
8. Evidence excerpt içinde bilinen e-posta/telefon/IBAN/11 haneli kimlik desenleri bulunursa kayıt reddedilir; bu maskeleme tüm kişisel verileri yakaladığı iddiası değildir.

## Test sayımı — kaynak dosyasındaki @Test anotasyonları tekrar sayıldı: 11
- Birincil kanıt tek başına doğrulama vermez.
- Birincil kanıt + insan teyidi doğrulanmış iddia üretir.
- İkincil kanıt insan onayı ile de yetmez.
- Destekleyen ve çelişen kanıt birlikteyse durum çelişkilidir.
- Çürütme için birincil karşıt kanıt ve insan kararı gerekir.
- Yeni kanıt eski insan incelemesini sıfırlar.
- Düzeltme geçmişi append-only kalır ve yeniden kanıt/inceleme ister.
- JSON round-trip publishedAt=null değerini korur.
- HTTP URL ve bozuk UTC tarihi reddedilir.
- Story ID fragment/son slash farkını normalize eder.
- İmkânsız tarih ve kimlik numarası barındıran excerpt reddedilir.

## CI / güvenlik sınırları
- Önceki PR #68 için pull_request workflow tetiklenmedi; hedef dal filtresi uyuşmazlığı bulundu.
- PR #69 ilk test koşusu #45657242423, yeni HEAD'e göre eski SHA `d09786662a6ca26bd548a0a947282b0e775e5dd5` üzerinde başladı; concurrency tarafından yeni commit gelince iptal edildi. Bu koşu exact-head kanıtı değildir.
- Son HEAD `c3cd6e65f46c35e996fed30d4d45b0352b307d70` için Actions run ve status henüz doğrulanmadı. CI PASS değildir.
- Kaynak URL'leri otomatik açılmaz veya kazınmaz; model yalnız referansları saklar.
- Bu PR haber kartında kullanıcıya gösterilen yeni arayüz sunmaz; henüz UI/SharedPreferences entegrasyonu yok.
- Merge/deploy yapılmadı.

## SONRAKİ ADIM
1. PR #69 exact-head Android unit tests ve APK sonucunu al; run #45657661662 tamamlanmadan PASS deme. Yeni başarısızlık olursa log kök nedenini düzelt.
2. Ayrı PR'da mevcut Haber arşivine geriye dönük veri kaybı olmadan local store bağla; mevcut `career_news_items` kayıtları otomatik VERIFIED yapılmaz.
3. Haber kartına iddia ekle, kaynak kanıtı ekle, karşıt kanıt ekle, insan incelemesi ve düzeltme geçmişi görüntüleme eylemleri ekle.
4. Dış bağlantıları Custom Tabs ile aç; C1 worker JSON ve 10/100 kapıları kapanmadan mobil HTML arama motoru fallback'ini kapatma.
5. H1 UI PR'si için ayrıca Full App QA workflow eklenmesi veya mevcut kariyer-modulu dalında UI test workflow'unun varlığı doğrulanmalı; PR #69 yalnız Android APK workflow'unu tetikler. Telefon testinde eski haber arşivi korunumu, kullanıcı onayı, link geri dönüşü ve offline/empty/error davranışını doğrula.

## 25.11.2026 CI hata analizi / düzeltme
- İlk H1 test koşusunda model Kotlin derlemesi ve test Kotlin derlemesi tamamlandı; test çalıştırmada JSON round-trip çağrıları Android'in local unit test mock JSON sınıflarında RuntimeException verdi. Bu nedenle test scope'a `org.json:json:20240303` eklendi; app runtime'a yeni bağımlılık eklenmedi.
- Aynı eski koşuda düzeltme geçmişi testindeki verdict assertion da başarısızdı; güncel HEAD'de correction kaydı önceki metin/kanıt/review alanlarını saklar ve düzeltilmiş claim'in kanıtını sıfırlar. Bu düzeltme exact-head testle doğrulanacak.
- PR #69 latest HEAD: `c3cd6e65f46c35e996fed30d4d45b0352b307d70`; exact-head CI sonucu henüz bekleniyor.
- Bu PR'da otomatik kaynak fetch yoktur. PRIMARY_SOURCE etiketi, UI katmanında yalnız gerçek resmî/orijinal kaynağı seçme ve insan incelemesiyle sunulmalıdır; bu model kendi başına URL'nin resmîliğini kanıtlamaz.


## Exact-head CI sonucu — 25.11.2026
- PR #69 exact HEAD `c3cd6e65f46c35e996fed30d4d45b0352b307d70`.
- Android APK run [#45658340086](https://github.com/aries919-bot/Jarvis/actions/runs/45658340086): **SUCCESS**.
- Job adımları: JUnit `gradle testDebugUnitTest --no-daemon` SUCCESS; APK `gradle assembleDebug --no-daemon` SUCCESS; artifact upload SUCCESS.
- APK artifact ID `15043163794`, 14,082,661 bytes; SHA-256 `514af7cdba04e162f0bfd065cfad22afa257c5b158b90cc1f6dcf15578bce38f`.
- Yeni modelin 11 @Test anotasyonu ve 38 assert çağrısı; JUnit görevi exact HEAD'de PASS. Eski başarısız/iptal koşular latest-head sonucu değildir.
- Sınır: bu CI yalnız unit test + debug APK derlemesini kanıtlar. Full App QA, Haber UI/store entegrasyonu, gerçek telefon, canlı kaynak doğrulaması ve kullanıcı akışı henüz kanıtlanmadı.
- Karar: model çekirdeği teknik olarak CI PASS; PR açık/taslak. H1 haber ürünü tamamlandı veya production-ready demek değildir.


## 2. H1 yerel arşiv + Haber kartı entegrasyonu — 25.11.2026
- Aktif PR: #69 https://github.com/aries919-bot/Jarvis/pull/69, taban `kariyer-modulu`, dal `feat/h1-story-claim-evidence-ci-20261125`.
- Exact HEAD: `b095528b9bb7974578c564be9e84791fa300ff55`; compare base/head: 29 commit /8 dosya /+1.572 /-35 /0 behind. 8 dosya: app/build.gradle.kts, CareerActivity.kt, CareerExternalLinks.kt, CareerNewsVerification.kt, CareerNewsVerificationStore.kt, CareerNewsVerificationUi.kt, CareerNewsVerificationStoreTest.kt, CareerNewsVerificationTest.kt.
- Test yeniden sayımı: CareerNewsVerificationTest 13 @Test + CareerNewsVerificationStoreTest 5 @Test = **18 yeni test**.
- Haber kartından iddia oluşturma, kanıt ve karşıt kanıt ekleme, insan CONFIRM/REFUTE/INCONCLUSIVE gerekçesi, iddia düzeltme ve düzeltme geçmişi okuma bağlandı. AI tek başına doğrulama üretmez.
- Ayrı H1 SharedPreferences anahtarı kullanılır; mevcut `career_news_items` arşivi silinmez veya üstüne yazılmaz. Arşiv 1.5 MB sınırını aşar ya da bozuk JSON içerirse sessiz temizleme yerine kayıt reddedilir. Önceki iddialar, kanıtlar ve correctionHistory sessizce silinemez.
- `firstSeenAt` tarama cache'inde ilk kez kaydedilir; eski kayıt ilk yeniden görüldüğünde başlatılır. Kaynakta yayın tarihi yoksa `publishedAt=null`; `collectedAt` kaynak tarama zamanı, `firstSeenAt` ilk kayıt zamanı olarak ayrı kalır.
- Birincil kanıt için izinli kurum/işveren alan adı kontrolü eklendi. Genel .edu.tr, LinkedIn/Indeed ve ikincil iş ilanı platformları PRIMARY_SOURCE olamaz.
- Kaynak bağlantıları için AndroidX Custom Tabs eklendi. Browser 1.10.0 denemesi AAR metadata kontrolünde reddedildi (compileSdk 36 + AGP 8.9.1 gerektiriyor); proje altyapısı yükseltilmeden Browser 1.8.0'a sabitlendi. Güncel #45665491590 koşusunda AAR metadata kontrolü ve Android birim testleri SUCCESS; APK derlemesi IN_PROGRESS.
- Android Full App QA workflow dosyası `kariyer-modulu` tabanında bulunmadığından emülatör UI QA koşusu yok; CI yalnız Android APK + unit test workflow'u.
- Önceki H1 PR #68 kapatıldı (unmerged); #69 onun yerini alır.
- Telefon testi yapılmadı; merge/deploy yok.

## 3. PR #71 SQLite tam entegrasyonu — 25.11.2026
- PR #71: https://github.com/aries919-bot/Jarvis/pull/71, branch `feat/h1-news-store-ui-20261125`, base `kariyer-modulu`.
- Güncel HEAD `3a1017528ebb1d87ca8e7f87022ae610ce1bdc6c`; compare base/head 52 commit /8 dosya /+1.979 /-26 /0 behind.
- Yeni test sayımı: model 17 @Test + store 8 @Test =25.
- SQLite `career_news_verification_v1.db`: primary + last-good per story, atomik transaction, corrupt-primary fallback, two-copy corruption blocks writes, capacity/no-eviction, commit failure reporting.
- Append-only store guard: old story URL/firstSeenAt, claim IDs, old evidence, review decision, previous review note and correctionHistory prefix are protected. A claim text/evidence reset requires an actual appended correction record containing prior state.
- Story ID now uses canonical URL (fragment/trailing slash/host/scheme normalization); same URL with changed headline retains same identity.
- News list now uses one `loadMany()` SQLite IN query for up to 100 cards instead of one SQLite query per card. Regression test counts exactly one batch read and verifies last-good fallback.
- Previous run #45663064814 failed because duplicate test function was declared twice. Duplicate removed. Intermediate HEAD `2597f36210d077a19b0e8e4ec100713d6736e141` run #45666479386 SUCCESS.
- Current exact HEAD [#45667520387](https://github.com/aries919-bot/Jarvis/actions/runs/45667520387): started for HEAD 3a10175; final status pending at report time. Do not claim PASS until complete.
- PR #71 includes the tested H1 core and UI integration and is the current SQLite candidate; PR #69 remains separately open/draft, both unmerged. PR #70 C1 source-policy P0 remains open/draft and has exact-head Android PASS (#45658875411).
- No Full App QA workflow / emulator or real-phone test on `kariyer-modulu`; no merge/deploy.

### PR #71 exact-head CI — 25.11.2026
- Exact tested HEAD `3a1017528ebb1d87ca8e7f87022ae610ce1bdc6c`.
- Android APK run [#45667520387](https://github.com/aries919-bot/Jarvis/actions/runs/45667520387): SUCCESS.
- Job: `testDebugUnitTest` SUCCESS; `assembleDebug` SUCCESS; artifact upload SUCCESS.
- Log outputs: `BUILD SUCCESSFUL in 1m 31s` for JUnit and `BUILD SUCCESSFUL in 50s` for APK.
- Artifact `aries-apk`, ID `15049240153`, size 14,176,012 bytes, not expired.
- Exact test annotation recount: 17 model tests +8 store tests =25 new @Test.
- Full App QA/emulator workflow not found on `kariyer-modulu`; no actual phone test. This is not production/phone acceptance.
- PR #71 stays open/draft/unmerged pending referee review and device test. PR #69/70 remain independently open/draft; no merge/deploy.
