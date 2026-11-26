> **Arşiv kaydı — 26.11.2026**
> Bu belge kullanıcı tarafından iletilen teknik tarama notunun korunmuş kopyasıdır. İçindeki bulguların tamamı mevcut kod üzerinde yeniden doğrulanmış kabul edilmez. Bu belge tek başına kod değişikliği, PR, merge veya aktif görev sırası onayı değildir. Aktif çalışma kapsamı İş & Kariyer ve Haber doğrulamasıdır; kapsam dışı maddeler ayrıca yetkilendirilip görev sırasına alınana kadar beklemededir.
>
> Not: İletilen içerik mesajda iki kez yinelendiği için arşivde tek eksiksiz kopya tutulur.

# Jarvis / Aries — Tam Teknik Tarama ve Geliştirme Notları

## 1. Hedef

Bu depo; Kotlin/Android tabanlı bir sesli asistan uygulaması, GitHub Actions APK derlemesi, Cloudflare Worker entegrasyonları ve kişisel otomasyon özelliklerini içermektedir.

## 2. Kritik Hatalar

### 2.1 API anahtarları düz metin saklanıyor

**Dosyalar**
- `Store.kt`
- `MainActivity.kt`
- `AndroidManifest.xml`

**Sorun**
- Cloud ve yapay zekâ anahtarları SharedPreferences içinde saklanıyor.
- Android backup açık olduğu için yedekleme yoluyla dışarı taşınma riski oluşuyor.
- Anahtar değerleri UI alanına alınabiliyor.

**Çözüm**
- AndroidX Security / EncryptedSharedPreferences veya Android Keystore kullan.
- API anahtarlarını mümkünse uygulama dışına taşı.
- `allowBackup=false` yap.
- Tamamen serbest çalışan kullanıcı anahtarları için Keystore içinde şifrele.
- Bulut anahtarı geri dönüşü olmayan şekilde sunucuya gitmeli.

### 2.2 Bildirimleri toplu olarak bellekte tutma

**Dosya**
- `Notif.kt`

**Sorun**
- Tüm uygulamalardan gelen bildirim başlığı ve metni tutuluyor.
- Bildirim verileri şifrelenmiyor.
- Bildirim Listener servisi yayınlanmış durumda.

**Çözüm**
- Uygulama-kendi bildirimlerini filtrele.
- Ekstradan tüm bildirim metinlerini almadan belirli paketleri seç.
- Gizlilik modu ekle.
- Hafızada tutma süresini kısalt.
- Servis dışında gerekmeyen dış erişim seçeneklerini kaldır.
- Bildirim içeriğini kullanıcı tarafından açıkça etkinleştirilen özellik olarak tanımla.

### 2.3 Sohbet geçmişinin sürekli saklanması

**Dosya**
- `Store.kt`

**Sorun**
- Geçmiş varsayılan olarak açık.
- Maksimum 200 mesaj tutuluyor.
- `histOn=false` durumunda yalnızca kaydetme durduruluyor; mevcut bellekteki konuşmalar aktif kalıyor.

**Çözüm**
- `histOn` ilk başlatıldığında ayar yapılabilir.
- Diske kaydetme kapalıysa RAM listesini de temizle.
- Otomatik silme süresi ekle.
- Görünür sohbet dışarı aktarma sonrası uygula.
- İçe/dışa aktarılırken tüm geçmişi dışa aktarmadan ayrık alan oluştur.

### 2.4 Buluta gereksiz hassas veri gönderimi

**Dosyalar**
- `Ai.kt`
- `MainActivity.kt`
- `Store.kt`
- Cloud Worker

**Sorun**
- Sohbetin son 20 mesajı gönderiliyor.
- `facts` ve `hatırla:` verileri buluta gönderiliyor.
- Maskeleme yalnızca bazı iletişim ve IBAN biçimlerini temizliyor.
- `oyVer()` kullanıcı cevabını buluta gönderebiliyor.

**Çözüm**
- `hatırla:` için önceden açık izin dialogu ekle.
- Bulut/anlık kişisel verileri varsayılan olarak kapat.
- Maskelemede en az şunları ekle:
  - TC Kimlik
  - Adres
  - Kart numarası
  - IP adresi
  - Tarih
  - Dosya yolları
  - Özel anahtarlar
  - JWT/token
  - Telefon numarası
- Geri bildirim gönderiminde metin yerine anonim istem puanı gönder.
- Cloud'a gönderilecek verileri tek bir `sanitizeForCloud()` fonksiyonuna bağla.

---

## 3. Yüksek Öncelikli Hatalar

### 3.1 Crash handler işlemi durdurup tekrar gösteriyor

**Dosya**
- `MainActivity.kt:121`

**Sorun**
- Uygulamayı process olarak öldürüyor.
- Crash bilgisini SharedPreferences içine kaydediyor.
- Stack trace kullanıcı mesajlarında ve yedeklerde hassas veri bırakabilir.

**Çözüm**
- Crash raporunu kapalıyken saklama.
- Raporu ilk 1000 karakterle sınırla.
- API anahtarlarını, mesajları ve dosya yollarını maskele.
- Crash gösteriminden önce tüm potansiyel hassas alanları temizle.
- Release crash yerine Crashlytics/Sentry benzeri kontrollü sistem kullan.

### 3.2 Dosya URI izinlerinin kalıcı olarak korunmaması

**Dosya**
- `MainActivity.kt:736-750`

**Sorun**
- URI'nin kalıcı izni alınmaya çalışılıyor fakat kaybolan erişim durumları ele alınmıyor.
- Dosya adı ve URI SharedPreferences'da tutuluyor.
- Açarken sadece boş hata gösteriliyor.

**Çözüm**
- URI açmayı başarısızlık halinde yeniden dosya seçmeye yönlendir.
- `takePersistableUriPermission()` öncesi URI izinlerini kontrol et.
- Dosya kaydı için son değişiklik tarihi ve boyut ekle.
- URI yerine mümkün olduğunda dosya içeriğini uygulama özel depolamasına kopyala.
- İçerik sağlayıcı URI’lerinin export edilmesini engelle.

### 3.3 Hatırlatmaların edge-case hataları

**Dosyalar**
- `Reminders.kt`
- `BootReceiver`
- `ReminderReceiver`

**Sorunlar**
- Bildirimler kapalıyken hatırlatma yalnızca metinle uyarılıyor.
- Geçmiş hatırlatmalar bildirim olarak geri yükleniyor.
- Boot tamamlanmadan exact alarm schedule işe koşulabilir.
- Alarm cancel işlemi hataları yutulmuş.

**Çözüm**
- Exact alarm izni kontrolü ekle.
- Geçmiş hatırlatmalarda kullanıcıya tek özet bildirimi gönder.
- Boot tamamlanmasını bekle.
- AlarmManager exceptionlarını kullanıcıya bildir.
- Hatırlatmayı fire ettikten sonra atomik olarak sil.
- Aynı hatırlatma için duplicate cancellation önle.

### 3.4 Hatırlatma zaman çözücüsü

**Dosya**
- `Reminders.kt`

**Sorunlar**
- Metin parseri karmaşık regexlere bağlı.
- "Saat 24:00", "saat 5 akşam", "haftaya çarşamba" gibi edge-case'ler eksik.
- "Bugün saat 15:00" ve "yarın saat 15:00" karışabilir.
- Relative ve absolute sürelerde bazı metinleri yanlış ayırabilir.

**Çözüm**
- Ayrı `ReminderParser` sınıfına taşı.
- Absolute/relative/day/period aşamalarını netleştir.
- Saat, dakika, gün, ay ve hafta günü için tipleri ayrıştır.
- Geçmiş ve belirsiz saatlerde kullanıcıya onay sorma.
- Unit testleri ekle:
  - yarın
  - yarım saat
  - 15.30
  - saat on
  - hafta içi
  - çeyrek saat
  - geçerli olmayan saatler
  - geçmiş tarih

### 3.5 Bildirim servisinde thread güvenliği

**Dosya**
- `Notif.kt`

**Sorun**
- `MutableList` synchronized olsa da onListenerConnected verisi volatile.
- Bildirim güncellemeleri UI thread dışında çalışıyor.
- Aynı paket/başlık/metin kontrolü sınırlı.

**Çözüm**
- UI snapshot'ı için copy-on-write list kullan.
- Paket adı/başlık/metni tek key olarak hashle.
- Maksimum saklama süresi ekle.
- 30 kayıtlık limit yerine zaman ve boyut bazlı retention ekle.

---

## 4. Orta Öncelikli Hatalar

### 4.1 AI ve web cevaplarında güvenilirlik

**Dosya**
- `Ai.kt`

**Sorunlar**
- Gemini fallback modeli sabit; modeli kullanıcının modeliyle uyumsuz olabilir.
- Gemini response candidates boş olabilir.
- Gemini `groundingChunks` değeri boşsa kaynak boş.
- OpenAI compatible cevap formatı hatalı olabilir.
- `Mistral` için JSON formatı garanti değil.

**Çözüm**
- Tüm model yanıtlarını try/catch JSON validasyonundan geçir.
- `candidates`/`parts` boşsa Friendly response.
- Fallback model seçimini hata durumuna göre yap.
- Timeout/retry değerlerini ayrı konfigürasyona al.
- Retry'ta random jitter ekle.
- `429` için süreyi provider headers'dan okuyarak beklet.

### 4.2 HTTP bağlantısı güvenliği

**Dosya**
- `Ai.kt`

**Sorun**
- HTTP bağlantıların tamamı HTTPS; ancak sertifika pinning yok.
- Redirect kontrolü yapılmıyor.
- Response boyutu sınırı yok.
- Malformed response halinde kullanıcı mesajı belirsiz.

**Çözüm**
- Response size limit ekle.
- Redirect policy ekle.
- Content-Type kontrolü yap.
- User-Agent ayarla.
- Timeouts değerlerini provider'a göre ayır.
- `IOException` ve `ApiError` ayrımı netleştir.

### 4.3 Matematik parserı

**Dosya**
- `Util.kt`

**Sorun**
- Üs alma, yüzde, virgül formatı ve mantıksal bölme desteklenmiyor.
- Çok büyük sayı Overflow'a düşebilir.
- Divide-by-zero ve NaN kontrolü eksik.

**Çözüm**
- Üs, yüzde, `0/0`, açık unicode sembolleri ekle.
- `isFinite` kontrolü yap.
- Bölme sıfırda özel mesaj.
- Decimal yerine BigDecimal benzeri hassas hesaplama kullan.
- Unit testleri ekle.

### 4.4 Rehber telefon arama

**Dosya**
- `Commands.kt`

**Sorunlar**
- Telefon numarası ve isim aramaları regex'e bağlı.
- Rehberde birden fazla kişiyi bulursa ilkini kullanıyor.
- Türkçe case handling Locale'a bağlı.
- ACT_CALL izni olmadan fallback düzgün çalışabilir.

**Çözüm**
- Aynı isim için birden fazla sonuç gösterecek seçim dialogu ekle.
- İzin reddini kullanıcıya açıkça bildir.
- Kullanıcı dışında otomatik aramayı önlemek için `ACTION_DIAL` varsayılan yap.
- Rehber sorgusunda Excel karakterleri ve apostrof ayrıştır.

### 4.5 Takvim komutları

**Dosya**
- `Commands.kt`

**Sorun**
- Belirsiz tarihleri yarın olarak varsaymıyor.
- "Pazartesi" için bugünkü/gelişmiş hesaplama eksik olabilir.
- Sadece 1 saatlik event oluşturuyor.
- Event title metni temizlenmeden kalıyor.

**Çözüm**
- Event için başlangıç/bitiş günü ayrı.
- Varsayılan süreyi kullanıcı ayarı yap.
- Zaman dilimini ve timezone'u açıkça ayarla.
- Title ve location'i güvenli temizle.
- All-day event desteği ekle.

### 4.6 Dosya listeleme

**Dosya**
- `MainActivity.kt:710-798`

**Sorun**
- Kötü JSON ile uygulama çökebilir.
- URI path'inin insanlar için gösterimi `lastPathSegment` ile belirsiz.
- Silme geri alınamıyor.
- Dosya boyutu ve türü gösterilmiyor.

**Çözüm**
- JSON parserı `try/catch` ile Store'a taşı.
- Dosya adı/URI için data class kullan.
- Silmeden önce onay ekle.
- MIME tipi ve boyut ekle.
- Silinen dosyaları geçici boşaltma yerine recycle bin yap.

### 4.7 UI ve dalgalanan durumlar

**Dosyalar**
- `Views.kt`
- `MainActivity.kt`

**Sorun**
- `HoloView` sınıfında her frame CPU kullanılıyor.
- `onDraw()` sırasında alpha/radius sürekli hesaplanıyor.
- `postInvalidateOnAnimation()` sürekli çalıştırılıyor.
- `state 99` özel kullanım durumundan yazılı hiçbir kontrolü yok.

**Çözüm**
- Frame update'i yalnızca visible durumda çalıştır.
- `ViewVisibility` ve Lifecycle ile animasyonu durdur.
- Holo state enum kullan.
- Dot array precompute et.
- `ViewCompat.setAccessibilityLiveRegion` ekle.
- Buttonlar için min touch size ekle.

### 4.8 UI Thread ağ çağrıları

**Sorun**
- Birçok `Thread` ve runnable doğru olsa da UI thread'den çağrılan Network Service kontrolleri olabilir.

**Çözüm**
- `ExecutorService` veya coroutine altyapısı kur.
- Retrofit yerine minimum `OkHttp` ekle.
- Ağ çağrıları Lifecycle/Scope ile iptal edilebilir hale getir.
- Main thread UI update helper'ı ekle.

---

## 5. Düşük Öncelikli Hatalar

### 5.1 Kod organizasyonu

**Sorun**
- `MainActivity.kt` 1400+ satır.
- `Commands.kt`, `Store.kt`, `Ai.kt`, `Views.kt` birbirine sıkı bağlı.
- UI, state, network, analytics tek dosyada.

**Çözüm**
- Uygulama katmanları:
  - `ui`
  - `data`
  - `domain`
  - `network`
  - `alarm`
  - `speech`
  - `media`
- `MainActivity` yalnızca Lifecycle + navigation controller olsun.
- `MainViewModel` ekle.
- Conversation, settings, reminder, speech ve media modüllerini ayır.

### 5.2 Mantıksal değişkenler

**Sorun**
- `PICK_FILE_REQUEST` `val` yerine companion object kullanılabilir.
- `NO_NET` sabit.
- Birden fazla stringly-typed provider "groq", "gemini", "mistral".
- `role="J"` ve "user" gibi roller string.

**Çözüm**
- Enum: `Provider`, `ConversationRole`, `OrbState`, `Page`.
- String keyleri `const val` veya resource kullan.
- UI metinlerini `strings.xml`e taşı.

### 5.3 Renk ve resource bakımı

**Dosyalar**
- `Views.kt`
- `themes.xml`

**Sorun**
- Renklerin bir kısmı `Pal`, bir kısmı doğrudan Hex olarak tutulmuş.
- Icon cache HashMap sınırsız büyüyebilir.

**Çözüm**
- Tüm renkleri `colors.xml` veya tek object'e taşı.
- Icon cache'i resource ID olarak sabitle.
- Material design componentlerini aşamalı olarak entegre et.

### 5.4 Erişilebilirlik

**Sorun**
- Birçok custom view için konuşma yerine görsel bilgi kullanılıyor.
- `TextView` ve custom FrameLayoutlar için contentDescription eksik.
- Renk kontrastı test edilmemiş.
- Touch target minimum 48dp değil.

**Çözüm**
- `contentDescription` ekle.
- 48dp minimum touch target.
- TalkBack testleri.
- Duyuru/uyarı için `LiveRegion`.
- Ekran animasyonu Reduce Motion desteği.

### 5.5 Dildeki sorunlar

**Sorun**
- UI metinleri Kotlin içinde Türkçe string.
- `strings.xml` dışında tutulmuş.

**Çözüm**
- Tüm metni `strings.xml`e taşı.
- İngilizce/İngilizce için localization hazırlığı.
- Plural/datetime resource kullan.

---

## 6. Build ve Gradle Notları

### 6.1 Mevcut build özellikleri

**Dosyalar**
- `build.gradle.kts`
- `app/build.gradle.kts`
- `settings.gradle.kts`
- `gradle.properties`

**Sorunlar**
- AndroidX bağımlılıkları eksik; SDK yüklü olabilir.
- Release buildType tanımlı değil.
- Proguard/R8 ayarı yok.
- Unit test dependency yok.
- Instrumented test dependency yok.
- Kotlin compiler plugin güncel değil.
- Manifest backup açık.
- Debug imzası açık bırakılmış.

**Çözüm**
- Android Gradle Plugin ve Kotlin sürümlerini uyumlu güncelle.
- Debug ve release ayrı buildType.
- R8 + shrinkResources.
- ABI split veya App Bundle ekle.
- `compileSdk=35` veya 36 hedefle.
- Min SDK 24 korunabilir.
- Java 17 korunabilir.

### 6.2 Önerilen dependencies

~~~kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
~~~

### 6.3 Gradle önerileri

- `gradle.properties` içine:
  - `org.gradle.parallel=true`
  - `org.gradle.caching=true`
  - `kotlin.incremental=true`
  - `android.enableJetifier=false`
- `libs.versions.toml` ekle.
- Gradle wrapper ekle.
- Wrapper dağıtım hashini doğrula.
- `dependencies` task'i CI kontrolüne ekle.

---

## 7. GitHub Actions Notları

**Dosya**
- `.github/workflows/android.yml`

**Sorunlar**
- Workflow push sırasında tüm main ve master değişikliklerinde çalışıyor.
- Test aşaması yok.
- Debug keystore için null kontrol yalnızca keystore varlığında.
- Secret'ların echo veya loglanmama kontrolü yok.
- `--stacktrace` debug olmadan.
- Artifact retention süresi ayarlı değil.
- Cancellation hataları doğrulanmıyor.

**Çözüm**
- Aynı commit için concurrency ekle.
- `assembleDebug` öncesi lint/test.
- `gradle wrapper` kullan.
- APK artifact retention 7/14 gün.
- Debug ve release build ayrı job.
- Workflow:
  1. Checkout
  2. Setup JDK
  3. Cache
  4. Gradle validation
  5. Unit test
  6. Android lint
  7. assemble
  8. artifact upload
- `secrets.TMDB_TOKEN` app tarafından kullanılmıyorsa kaldır.
- Workflow permissionları `read-only`.
- PR'lardan gelen fork secret erişimini kaldır.

---

## 8. Test Planı

### Unit test

- `MathParser`
- `ReminderParser`
- `Store`
- `Ai.parseReply`
- `Ai.maskele`
- `cleanSuffixes`
- `waNumber`
- `evalMath`
- Conversation title generation
- JSON import/export
- Faulty JSON handling
- Relative/absolute reminder dates
- Turkish suffix handling
- Provider response parsing
- HTTP error mapping
- Zero divide
- Timezone calculations
- Notification ID generation
- Duplicate reminder scheduling

### Integration test

- AI provider fallback
- Bulut unauthorized
- Offline behavior
- Network retry
- Local reminder scheduling
- Boot reminder restore
- Notification listener
- Bluetooth output switching
- Contacts and phone permission flow
- File URI opening
- Speech recognizer start/stop
- TTS completion
- Audio session playback
- Export/import data
- Clear all data

### UI test

- Yeni sohbet açma
- Mesaj gönderme
- Mikrofon gönderme
- Müzik çalma komutu
- Hatırlatma oluşturma
- Hatırlatmaları listeme
- Hatırlatmaları silme
- Ayarlar toggle
- Dosya ekleme/silme
- Crash ekranı
- Offline durumu
- Keyboard resize
- Side panel açma/kapama
- Back navigation
- Rotation
- Dark/light theme
- TalkBack navigation

### CI test

- Debug APK build
- R8/release build
- APK minSdk check
- Manifest permission check
- Secret scan
- Dependency vulnerability scan
- Gradle dependency verification
- Kotlin lint
- Baseline unit test
- Smoke emulator test
- Artifact integrity
- App startup no crash

---

## 9. Test Hatalarını Ayıklama Notu

Son 20 Actions kayıt içinde UI smoke testlerin bazıları failure gösteriyor. Başarısız jobın tam logu erişilmediği için şimdilik kesin root-cause ayrılamıyor.

Muhtemel nedenler:
- Emulator başlangıcında APK yüklenmemesi
- `UIAUtomator` timeout
- Ana ekrana `HoloView` çizilmesi
- Permission dialogların beklenmemesi
- Exact alarm permissions
- Notification listener ekranı
- APK launching on locked/selected emulator state
- Gradle build cache veya Android SDK mismatch
- APK package launch intent bulunamaması
- 300dp sphere canvası farklı ekranlarda taşması

Öneri:
- Smoke testte tüm permission grant aşamalarını otomatikleştir.
- İzin dialoglarını kapat veya bekle.
- `HoloView` animation disable flag ekle.
- Uygulama başlangıçta crash logları doğrula.
- `adb logcat` ve screenshot artifact ekle.
- İlk smoke testte yalnızca package açılışını denetle.

---

## 10. Güvenlik İyileştirme Sırası

### Faz 1
- Backup kapatma
- Secret storage
- Cloud masking
- Bildirim verisi sıfırlama
- PII export kontrolü
- Crash record maskeleme

### Faz 2
- Cloud opt-in
- History opt-in
- Memory opt-in
- API key UI kontrolü
- Cloudflaresigned request
- Token refresh

### Faz 3
- Encrypted database
- Secure backup
- Data retention
- GDPR-style delete
- Export data
- Audit log

---

## 11. AI ve Cloud Güvenliği

- Worker secretları client’a gönderme.
- Worker’a `JARVIS_TOKEN` dışında signed request ekle.
- Android uygulama ve Worker arasında nonce + timestamp ekle.
- Replay attack koruması ekle.
- Cloud context uzunluğunu sınırla.
- Token sabit tut ama rate limit ekle.
- Worker yanıtında provider `sources` ve `citations` doğrula.
- URL redirect ve SSRF kontrolleri ekle.
- External source URL'leri Android'in `ACTION_VIEW` açarken scheme'i doğrula.
- `javascript:` ve `file:` URLlerini reddet.
- Loglarda search query’sini loglama.
- Model response'u HTML olarak render etme; plain text kullan.
- Prompt injection response'u güvenli biçimde ele al.

---

## 12. Worker / Cloud Eksikleri

- Worker kodu ayrı repoda incelenmeli.
- D1 tablo retention doğrulanmalı.
- Cron job taski test edilmeli.
- `/hizli` Authorization doğrulaması tüm branchlere uygulanmalı.
- `/tg` Telegram webhook secret doğrulaması.
- `/duzelt` GitHub API yetkilendirmesi.
- `/chat` token doğrulaması.
- `/durum` ve `/rapor` PII satır sayısını sınırla.
- Cloud console loglarında kullanıcı verisini yok et.
- Tüm CORS ve method kontrolleri.
- 413, 429, 503 hata mapping.
- Request size limit.
- AI cevaplarını cache etme.
- Worker KV cache key'lerini PII'ya göre temizleme.

---

## 13. PR ve Versiyon Durumu

Açık PR sayısı yoğun. Dallanma çakışmaları oluşabilir.

**Önerilen birleşme sırası**
1. Güvenlik PR'ları
2. Store ve MainActivity refactor
3. Test altyapısı
4. Reminder fixes
5. AI privacy changes
6. UI feature PR'ları
7. Media and career modules
8. Documentation cleanup
9. CI workflow fixes
10. Version bump

**Branch kuralları**
- Ana branch'e merge etmeyin.
- Her PR yalnızca tek concern içersin.
- PR açıklamasına test listesi ekleyin.
- Gerekli build job'unu mandatory status yapın.
- Secret scan'e bypass olmazsa merge etme.
- UI smoke başarısızlığı varsa merge etme.

---

## 14. Kod Standartları

- Ktlint ekle.
- Detekt ekle.
- Android Lint fail-on-error.
- `TODO`/`FIXME` kontrolü.
- Public API Javadocs.
- 500 satır üstü dosyalara refactor task.
- Null ve exception handling standartlaştır.
- `RuntimeException` yerine domain error kullan.
- Thread yerine coroutine/Executor kullan.
- Synchronous SharedPreferences yerine DataStore.
- JSON yerine Kotlinx Serialization.
- ViewBinding/DataBinding.
- Retrofit veya OkHttp.

---

## 15. Açık Test Öncelikleri

1. Hatırlatma parser testleri
2. Store import/export testleri
3. API fallback testleri
4. PII masking testleri
5. Secure storage testleri
6. File URI testleri
7. Speech lifecycle testleri
8. Boot alarm restore testleri
9. Notification service testleri
10. MainActivity smoke testleri
11. Release build testleri
12. APK artifact testleri
13. Accessibility testleri
14. Offline testleri
15. Rotation testleri
16. Low-memory testleri
17. Airplane mode testleri
18. Android 14+ exact alarm testleri
19. Android 13+ notification permission testleri
20. Android 11+ package visibility testleri
