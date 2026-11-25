# H1 — Haber iddia/kanıt/düzeltme çekirdeği
Tarih: 25.11.2026
Durum: PR AÇIK / TASLAK / CI KANITI BEKLİYOR

## Amaç
Mevcut Haber arşivi başlık, bağlantı, snippet ve sınıflandırma saklıyor; iddia atomları, destekleyen/çelişen kanıtlar, insan incelemesi ve düzeltme geçmişi için veri sözleşmesi yok. Bu iş, mevcut haber kayıtlarına dokunmadan bu çekirdeği ayrı ekler.

## Dal / PR
- Repo: aries919-bot/Jarvis
- Taban: is/c3-ilan-semasi-ai-kaynak-kaniti
- Taban SHA: 350f38bc9bd1939a5481689c32889c9b326f1d63
- Dal: feat/h1-story-claim-evidence-20261125
- HEAD: 5f5af96834de429dcb302d939a5fff444a2b01c0
- PR #68: https://github.com/aries919-bot/Jarvis/pull/68
- Durum: açık/taslak; merge yok; kullanıcı/gerçek cihaz doğrulaması yok.

## Diff yeniden sayımı
- Compare exact taban/head: 5 commit; 2 dosya; +664 / -0; 0 behind.
- Yeni production dosyası CareerNewsVerification.kt: 435 ek satır.
- Yeni test dosyası CareerNewsVerificationTest.kt: 229 ek satır; 11 test.
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
- PR #68 exact-head workflow run listesi: 0 kayıt.
- Commit combined status listesi: 0 kayıt.
- CI sonucu PASS değildir; çalıştırılmadı/kanıt yok.
- Kaynak URL'leri otomatik açılmaz veya kazınmaz; model yalnız referansları saklar.
- Bu PR haber kartında kullanıcıya gösterilen yeni arayüz sunmaz; henüz UI/SharedPreferences entegrasyonu yok.
- Merge/deploy yapılmadı.

## SONRAKİ ADIM
1. PR #68 exact-head Android unit tests, APK ve Full App QA kanıtını al; başarısız olursa log kök nedenini düzelt.
2. Ayrı PR'da mevcut Haber arşivine geriye dönük veri kaybı olmadan local store bağla; mevcut `career_news_items` kayıtları otomatik VERIFIED yapılmaz.
3. Haber kartına iddia ekle, kaynak kanıtı ekle, karşıt kanıt ekle, insan incelemesi ve düzeltme geçmişi görüntüleme eylemleri ekle.
4. Dış bağlantıları Custom Tabs ile aç; C1 worker JSON ve 10/100 kapıları kapanmadan mobil HTML arama motoru fallback'ini kapatma.
5. Telefon testinde eski haber arşivi korunumu, kullanıcı onayı, link geri dönüşü ve offline/empty/error davranışını doğrula.