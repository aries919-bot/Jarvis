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
- HEAD: fdfdd539a2e81826e53898aa07004ee21a0bd55b
- PR #69: https://github.com/aries919-bot/Jarvis/pull/69 (aktif)
- Eski PR #68 kapatıldı (unmerged), çünkü `is/c3-ilan-semasi-ai-kaynak-kaniti` hedefinin workflow filtresi CI tetiklemiyordu.
- Durum: açık/taslak; merge yok; kullanıcı/gerçek cihaz doğrulaması yok.

## Diff yeniden sayımı
- Compare exact taban/head: 8 commit; 2 dosya; +741 / -0; 0 behind.
- Yeni production dosyası CareerNewsVerification.kt: 500 ek satır.
- Yeni test dosyası CareerNewsVerificationTest.kt: 241 ek satır; 11 test.
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
