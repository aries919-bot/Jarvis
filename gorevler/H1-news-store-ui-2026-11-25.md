# H1 Haber arşivi — iddia/kanıt ekranı ve yerel depo
Tarih: 25.11.2026
Durum: PR #71 AÇIK / TASLAK / exact-head CI düzeltme sonrası bekleniyor.

## Dal ve PR
- Repo: aries919-bot/Jarvis
- Base: kariyer-modulu (`4b270245019ed322dc2dc1c735c715c0934076a2`)
- Branch: feat/h1-news-store-ui-20261125
- Current HEAD: `b9abd9433badf90d0cda306c8c81daa001b0a383`
- PR #71: https://github.com/aries919-bot/Jarvis/pull/71
- Parent H1 core PR #69: https://github.com/aries919-bot/Jarvis/pull/69 (exact-head unit test + APK PASS, still open/draft).
- PR #71, PR #69 modelini içerir ve UI/store entegrasyonunun superset PR'ıdır. İkisi de merge edilmedi.

## Diff yeniden sayımı
- Compare base/head: 43 commit, 8 dosya, +1811 / -25, 0 behind.
- CareerNewsVerification.kt: 564 ek satır.
- CareerNewsVerificationActivity.kt: 468 ek satır.
- CareerNewsVerificationStore.kt: 133 ek satır.
- CareerActivity.kt: +104/-25; haber kartı, firstSeenAt, Custom Tabs ve duplicate merge.
- Manifest: yalnız private activity kaydı.
- Gradle: test JSON implementasyonu ve Custom Tabs browser dependency.
- Testler: CareerNewsVerificationTest 18 @Test; CareerNewsVerificationStoreTest 6 @Test; toplam 24 @Test.

## İşlev
- Haber kartında iddia/kanıt inceleme ekranı açılır.
- Birden fazla atomik iddia; destekleyen, çelişen, bağlam kanıtı; kaynak türü; kısa alıntı; kontrol tarihi; insan inceleme kararı ve notu.
- Birincil kanıt URL'si resmi/izinli kaynak allowlist'iyle sınırlandırılır. Genel .edu.tr, ikincil ilan panosu ve haber keşif siteleri birincil kaynak kabul edilmez.
- Birincil kaynak kanıtı + insan CONFIRM olmadan VERIFIED yok. Karşıt birincil kanıt + REFUTE olmadan REFUTED yok. Karşıt ve destekleyici kanıt birlikteyse CONFLICTING.
- Düzeltme eski iddia, kanıt ve review kararını saklar; yeni iddia için kanıt ve insan incelemesi yeniden gerekir.
- Claim, evidence excerpt, review note ve correction text içindeki bilinen e-posta/telefon/IBAN/11 haneli kimlik örüntüleri reddedilir. Bu tüm PII'yi tespit ettiği garantisi değildir.
- Ayrı `career_news_verification_v1.db` SQLite deposu; legacy `career_news_items` anahtarına dokunmaz.
- Primary + last-good kayıt; iki kopya bozuksa üzerine yazmaz. 300 kayıt sınırında eskisini silmek yerine yeni yazmayı reddeder.
- Yerel yazma UI thread dışında yapılır.
- Yeni keşifte firstSeenAt yazılır; eski kayıtta bilinmiyorsa null. collectedAt lastSeenAt'ten alınır; publishedAt yalnız kaynak alanı varsa, aksi null.
- CareerActivity içindeki harici kariyer/haber URL'leri Custom Tabs ile açılır; ACTION_VIEW sayımı 0.

## CI / hata düzeltme
- İlk PR #71 run #45661731482 exact HEAD'de compile başarısızdı:
  1. EditText `singleLine` unresolved → `setSingleLine(true)` yapıldı.
  2. Store validation mesajında kaçışlı string interpolation parser hatası → string concatenation yapıldı.
- Önceki HEAD `a985d9600da7237bd45b954ca5e82a9c850f1944` run #45662111632, SQLite/record-limit değişiklikleri gelince eski HEAD oldu.
- Güncel HEAD `b9abd9433badf90d0cda306c8c81daa001b0a383` için Android run #45663064814: queued; exact-head sonuç bekleniyor.
- Başarısız eski run güncel HEAD'e PASS sayılmaz. APK build step ilk koşuda skipped.
- No merge/deploy; gerçek cihaz testi yok.

## Güvenlik / sınırlar
- Web kazıma veya otomatik kaynak fetch'i eklenmedi.
- AI kendi başına doğrulama kararı vermez.
- Eski haber arşivi ve başvurular değişmeden kalır; doğrulama kayıtları ayrı prefs dosyasındadır.
- Haber kaynağı yayın tarihi bilinmiyorsa null kalır; toplanma ve ilk görülme zamanı karıştırılmaz.

## SONRAKİ ADIM
1. PR #71 exact-head test ve APK CI sonucunu bekle; hata varsa yalnız logda görülen kök nedeni düzelt.
2. Testler geçerse diff/PII/Custom Tabs/store recovery için üç göz denetimi yap.
3. Ayrı Android emulator veya telefon smoke testinde eski arşiv korunumu, iddia ekleme, birincil kaynak reddi, karşıt kanıt, insan incelemesi, düzeltme ve offline kayıt kontrol et.
4. PR #69/#71 için merge tercihini hakem belirler; otomatik merge yok.


## SQLite / boyut sınırı ek denetimi
- SharedPreferences yerine platform SQLite kullanıldı; yeni harici DB bağımlılığı yok.
- `verification_kv` tablosu; primary ve last-good kayıtları aynı transaction'da yazar.
- İlk kayıtta da last-good kopya oluşturulur. Her kayıtta 16 KB sınırı, 300 hikâye kapasitesi, 50 claim/hikâye, 25 evidence/claim, 100 correction/hikâye.
- Store unit testleri fake key-value adapter üzerinden transaction başarı/başarısızlık ve recovery davranışını test eder. Android SQLite adapter'ın gerçek cihaz/emülatör smoke testi hâlâ yapılmadı.
