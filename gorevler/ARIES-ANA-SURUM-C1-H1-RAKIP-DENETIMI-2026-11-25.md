# Aries ana sürüm + C1/H1 rakip ve güvenilirlik denetimi — 25.11.2026

## KARAR

**RED / KORUMA MODU.** Bu denetimde uygulama kodu, APK, Worker veya D1 değiştirilmedi. Rakip güncelliği, repo/özellik dalı durumu ve çekirdek kodda güvenilirlik riskleri incelendi. Mevcut çalışan işlevleri bozmamak için öneriler küçük, geri alınabilir işlere ayrıldı.

**Kapsam sınırı:** Bu çalışma Android çekirdeğinin İş & Kariyer/Haber'e etkisi, C1 veri hattı ve haber doğrulaması içindir. Tema, Film/Dizi, Borsa veya başka modüllere müdahale yoktur. “Tüm Aries” ifadesi bu çalışma alanı dışındaki modüllerde kod değiştirme izni değildir.

## 1. Mevcut sürümün gerçek fotoğrafı

### Ana dal ve özellik dalları

- Jarvis main SHA: 63e877d96cb910e6d98d56834ae55ae8edafab2a.
- main ağacında CareerActivity.kt bulunmuyor. Main build.gradle.kts versionCode 1 / versionName 1.0; compileSdk 34, targetSdk 34. Kariyer özellik dalındaki modern bağımlılık ve build ayarlarının tümü main'de yok.
- kariyer-modulu/C2/C3 dallarında CareerActivity, CareerMonitor, CareerListingSchema, haber gezinme ve ilgili testler var. Bir özellik dalı build.gradle.kts versionCode 22 / versionName 1.22 ve WorkManager, WebKit, DataStore, Media3, Compose, Retrofit, OkHttp bağımlılıkları taşıyor.
- C2/C3 kod HEAD c6795908ccd753f47b216f34c59bc697df4f4d5d için Android APK Actions #45249266638 SUCCESS; Full Application QA #45249266761 SUCCESS.
- Full QA'da Android emulator API 29, 33, 34 ve 35 üzerinde UI audit adımları başarıyla tamamlandı. Bu sonuç özellik dalının test edilip APK üretildiğini gösterir; main'e birleştiğini, gerçek telefonda tüm ağ kaynaklarının çalıştığını veya üretim sürümü olduğunu göstermez.
- C2/C3/H2 PR'ları farklı taban dallarına yığılmıştır. Bir PR'ın yeşil CI sonucu, bütün özelliklerin tek APK'da bulunduğunu kanıtlamaz.

**Sonuç:** Önce tek release adayı commit belirlenmeli ve yalnız o SHA için APK + tam QA üretilmeli. Main ile özellik dallarını körlemesine birleştirmek yok.

## 2. Rakipler güncel mi? — 25.11.2026 web kontrolü

### İş & Kariyer

| Rakip / altyapı | Güncel görünen özellik/yöntem | Aries için çıkarım |
|---|---|---|
| Simplify | App Store'da görünen 2.6.0 sürümü yaklaşık 24.11.2026 tarihli; Autopilot, role göre CV uyarlama, form doldurma/gönderme ve canlı başvuru takibi var. | İlan eşleştirme gerekçesi, başvuru panosu ve kullanıcı onaylı başvuru hazırlığı alınabilir. Otomatik gönderim ve otomatik kişisel veri paylaşımı kopyalanmamalı. |
| Kariyer.net | Google Play sayfası 10.11.2026 güncellemesini gösteriyor. App Store geçmişinde iş alarmı, işveren etkinlik takvimi, mülakat hazırlığı ve CV'den başvuru alanı doldurma özellikleri var. | Kayıtlı arama/alarm, sınav-etkinlik takvimi, kullanıcı onaylı başvuru hazırlığı. |
| Ground News | 2026 sonbahar App Store sürüm geçmişi; haber hikâyesinde kaynak sıralama/filtreleme, sahiplik, yerellik, yayın tarihi ve yayıncı düzeyinde factuality filtreleri. | Aynı olayı storyId ile grupla; farklı yayınların başlık ve kaynaklarını yan yana göster. Yayıncı puanı tek haberin doğruluk kararı değildir. |
| ATS public API'leri | 2.11.2026 tarihinde test edildiği belirtilen katalog Greenhouse, Lever, Ashby, SmartRecruiters, Workable, Recruitee, Personio, Workday ve başka sistemleri listeliyor. Her endpoint şirketin board/slug'ı içindir; tek global tüm ilan veritabanı yok. | Lever/Ashby'den sonra ayrı ATS adapter'ları eklenebilir; önce her işveren board'u ayrı ölçülmeli. |
| Google Fact Check Tools | Claim Search API daha önce yayımlanmış ClaimReview kayıtlarını bulur; API key ve şartlar gerekir. Read/Write aracı kendi sitesindeki fact-check işaretlemesi için yetkilendirme ister. | Eski doğrulamayı bulmak için opsiyonel; Google sonucu olmaması iddiayı doğru veya yanlış yapmaz. |
| GDELT | Çok dilli haber keşif ve değişen sayfa sinyalleri sağlar. | Olay/haber adayı keşfi; doğruluk kanıtı değildir. |

Kaynaklar:
- Simplify: https://apps.apple.com/us/app/simplify-ai-job-search/id6781911255
- Kariyer.net Google Play: https://play.google.com/store/apps/details?id=com.kariyer.androidproject
- Kariyer.net App Store: https://apps.apple.com/tr/app/kariyer-net-job-search/id574794931
- Ground News: https://apps.apple.com/us/app/ground-news/id1324203419
- Ground News kaynak filtreleri: https://help.ground.news/en/articles/6049857
- ATS API kataloğu: https://github.com/jobspipe/ats-job-apis
- Ashby public API: https://developers.ashbyhq.com/docs/public-job-posting-api
- Lever API: https://github.com/Lever/postings-api
- Google Fact Check: https://developers.google.com/fact-check/tools/api/
- GDELT: https://gdeltproject.org/data.html

Mağaza sürüm geçmişleri bölgeye göre farklı görünebilir. “En yeni” bilgisi kontrol edilen mağaza sayfasının gösterdiği sürüm/tarihle sınırlıdır; kapalı rakiplerin özel altyapısı hakkında varsayım yapılmamıştır.

## 3. Aries kodunda doğrulanan iyi taraflar

1. Kariyer sınıflandırması iş ilanı, kura/sonuç, öğrenci/staj, kurum duyurusu ve haber ayrımını yapıyor.
2. Deadline bilinmiyorsa açık iş diye kabul etmeme ve yayın tarihi ile son tarama zamanını ayırma yaklaşımı bulunuyor.
3. Kullanıcı tetiklemeli ilan sayfası okuma akışı var. JavaScript yalnız kullanıcının okuma eylemiyle açılıyor; üçüncü taraf çerezleri kapalı; SSL hatası iptal ediliyor; file/content erişimleri ve mixed content kapalı.
4. C3 sayfa okuyucu AI alan çıkarımı öncesi kullanıcı onayı istiyor ve her alan için sayfa metninden kısa kanıt alıntısı talep ediyor.
5. İş takibi, kayıtlı ilan, durum geçmişi, not ve hatırlatma gibi temel işlevler var.
6. Bulut anahtarının güncel saklama yolunda Android Keystore AES-GCM şifrelemesi var; eski düz anahtarı ilk okumada taşıyıp kaldırma kodu mevcut.

Bunlar korunmalı. Büyük yeniden yazım gerekmiyor.

## 4. Kritik kusurlar / riskler

### P0 — yasaklı kariyer kaynakları özellik dalında duruyor

C2/C3 CareerMonitor.kt incelendi:
- Indeed ve LinkedIn source girdileri hâlâ var.
- trustedDomains listesinde indeed.com ve linkedin.com bulunuyor.
- site:tr.indeed.com/jobs ve site:linkedin.com/jobs/view sorguları üretiliyor.
- trustedDomains içinde genel edu.tr de var. Dört kamu üniversitesi için ayrı allowlist tanımlanmasına rağmen genel edu.tr kabulü özel/başka üniversiteleri güvenilir gösterebilir.

Bu, NOT_C1 kurallarıyla çelişiyor. Source kataloğundan, sorgulardan ve test fixture'larından kaldırılmalı; genel edu.tr yerine tam kurum/host allowlist'i kullanılmalı. Bu raporda kod değiştirilmedi.

### P0 — tarayıcı kimliği taklidi

Ai.kt arama çağrılarında Chrome/Android benzeri User-Agent gönderiyor. NOT_C1'nin tarayıcı taklidi yasağıyla çelişiyor. Arama motoru HTML parse hattı doğrulanmış iş/haber feed'inin ana kaynağı olmamalı. İzinli public API/RSS ve resmî sayfa adaptörleri esas olmalı. UA yalnız izinli Aries okuyucusunu tanıtmalı.

### P0 — haber arama, haber doğrulama değil

Ai.newsTitles Google News RSS'den yalnız başlıkları alıyor; link/pubDate/source alanlarını saklamıyor. Ai.research çoğu durumda arama başlığı/snippet'ini modele veriyor. Bu keşif/özet için yardımcıdır; iddia doğruluğu için yeterli kanıt değildir.

H1'e ayrı katman önerisi:
- storyId: aynı olayın haberlerini grupla.
- claimId/claimText: tek, sınanabilir iddia.
- originUrl: iddianın ilk/özgün kaynağı; yoksa null.
- evidence[]: destekleyen/çelişen kanıt, kurum/yayıncı, resmî URL, collectedAt.
- verificationStatus: Doğrulandı / Çelişkili / Bağlam Eksik / Doğrulanamadı.
- reviewedAt ve correctionHistory[]: inceleme ve düzeltme geçmişi.

İnsan incelemesi olmadan tartışmalı iddiaya kesin hüküm yok. Kaynak adedi doğruluk oyu değildir. Google ClaimReview yalnız önceden yayımlanmış fact-check'i bulmak için opsiyonel kaynaktır.

### P1 — dış bağlantı açılışı

Kariyer/haber kartlarında birçok bağlantı Intent.ACTION_VIEW ile açılıyor; NOT_C1 Custom Tabs istiyor. Android dokümanı dış alan adları için Custom Tabs'ın paylaşılan tarayıcı durumu, daha iyi güvenlik ve uygulamaya kolay dönüş sağladığını anlatıyor. Küçük ortak helper ile yalnız kariyer/haber linkleri standardize edilebilir.

### P1 — ağ istekleri ve gecikme

Ai.kt'de:
- Birçok çağrı HttpURLConnection kullanıyor; timeout'lar var ama ortak client, her yolda disconnect, response byte limiti ve iptal tek bir standartta değil.
- withRetry sadece bir kez sabit 1 saniye sonra deniyor; Retry-After, exponential backoff, jitter ve provider circuit breaker görünmüyor.
- Sağlayıcılar sıralı deneniyor; gecikme birikebilir.
- Streaming token → cümle tamponu → TTS akışı görünür değil.

OkHttp ve Retrofit build bağımlılıkları zaten var. Yeni ağ yığını eklemek yerine tek paylaşımlı client/Repository, toplam timeout bütçesi, body byte limiti, cancellation ve Retry-After ile kademeli standardizasyon.

### P1 — depolama

Store.kt tüm sohbetleri convs anahtarında tek JSON dizesi olarak SharedPreferences'a yazar; her mesaj tüm konuşma listesini yeniden serileştirir. Sohbet başına 200 mesaj sınırı var ama toplam konuşma sayısı sınırlandırılmıyor. Bozuk JSON olursa cache temizleniyor. Uzun kullanımda yazma maliyeti ve bozuk veri riski doğurabilir.

Bulut anahtarının Keystore yolu zaten var; korunmalı. PII/CV/KPSS/e-Devlet verisi sunucu profiline taşınmamalı. Room'a geçilecekse önce export/import yedeği ve geri alma testi olmalı.

### P1 — WorkManager kesin saat garantisi vermez

CareerMonitor iki günlük tarama penceresi planlıyor. WorkManager periyodik işin saatini garanti etmez; Android dokümanı 15 dakika minimum periyodik aralık ve esnek yürütme penceresi belirtir. UI “tam 08:30'da tarar” dememeli; son başarılı tarama zamanı ve “sistem uygun olduğunda” bilgisi gösterilmeli.

### P2 — hız iyileştirmesi ölçümden sonra

Baseline Profiles ve Macrobenchmark kritik kullanıcı yolculuklarının açılış/çizim gecikmesini ölçüp iyileştirmek için Android'in önerdiği araçlardır. Aries'te önce cold start, Haber açılışı, kayıtlı ilanlar, manuel yenileme, AI ilk yanıt ve TTS ilk cümle ölçülmeli. Ardından Baseline Profile denenmeli; fiziksel telefon ölçümü emülatörün yerini tutmaz.

## 5. Güvenli ve faydalı özellikler — öncelik sırasıyla

| Öncelik | Özellik | Fayda | Kabul sınırı |
|---|---|---|---|
| 1 | Haber hikâyesi + çok kaynak karşılaştırması | Aynı olayın farklı başlıklarını tek yerde karşılaştırır. | Kopya haberler bağımsız kanıt sayılmaz; source URL'ler saklanır. |
| 2 | İddia doğrulama kartı | Kullanıcı hangi iddianın hangi kanıtla değerlendirildiğini görür. | Lehte/aleyhte kanıt, insan incelemesi ve düzeltme geçmişi. |
| 3 | Kaydedilmiş arama + ilan alarmı | Yeni ilanları tekrar tekrar aramayı azaltır. | Yerel tercihler, son tarama zamanı, yeni ilan farkı; WorkManager gecikmesi açık yazılır. |
| 4 | Başvuru panosu | Kaydedildi → Hazırlanıyor → Başvuruldu → Sınav/Mülakat → Sonuç. | Kullanıcı kontrolü; not, son tarih ve hatırlatma. Otomatik başvuru gönderimi yok. |
| 5 | Uygunluk gerekçesi | Eğitim, şehir, puan türü ve nitelik şartlarının neden eşleştiğini açıklar. | Yerel profil; gerekçeli eşleşme; kabul şansı tahmini yok. |
| 6 | Resmî sınav/başvuru takvimi | Son başvuru ve sınav/kura tarihini ayırır. | Sınav tarihi publishedAt yerine geçmez; resmî link gösterilir. |
| 7 | Kaynak sağlığı kartı | Kaynakların son başarı/hata ve stale durumunu gösterir. | Kaynak bazlı lastGoodAt/lastAttemptStatus; hata veren kaynak diğerini bozmaz. |

## 6. Eklenebilir güvenli kütüphane/altyapı

- AndroidX Browser Custom Tabs: yalnız kariyer/haber dış bağlantılarını açmak için.
- OkHttp + Retrofit: zaten bağımlılık listesinde; tek paylaşımlı ağ katmanında doğru kullan.
- Room: yalnız sohbet geçmişi için kademeli migration; önce yedek ve rollback testi.
- WorkManager: zaten mevcut; unique work, network constraints, exponential backoff ve stale kontrolü.
- Baseline Profiles + Macrobenchmark: yalnız önce gerçek cihaz ölçümü alındıktan sonra.
- ATS adapter registry: Greenhouse/Lever/Ashby/SmartRecruiters/Workable gibi her sağlayıcı ayrı; board ve izin/robots/ToS kontrolü olmadan aktif değil.
- Google Fact Check Claim Search: API key/kota/şartlar onaylanırsa eski fact-check araması; otomatik verdict değil.
- GDELT: haber keşfi; iddia kanıtı değil.

## 7. C1/H2 kabul sınırı

- Son H2 reader: 19 kaynak kaydı / 17 fresh / 2 stale / 13 açık doğrulanmış ilan kartı / 11 kurum / 125 pozisyon. 125 pozisyon, 125 ilan kartı değildir.
- e-Şube ayrı snapshot: 42 benzersiz açık kayıt. H2 + e-Şube çapraz feed dedup kanıtı olmadan 55 tekil ilan diye raporlanamaz.
- C1 10 kaynak / 100 doğrulanmış açık ilan eşiği halen geçilmedi.
- jarvis-cloud PR #10 cross-source dedup açık; exact-head Actions kanıtı yok. Önceki run #45649406146 runner'a atanmadı (steps boş, log 404); körlemesine tekrar denenmeyecek.
- Parent PR #9 açık ve base branch hareket ediyor.
- Production Worker authenticated GET ve D1 firstSeenAt kalıcılığı kanıtlanmadan uygulama feed entegrasyonu kabul edilmez.

## 8. Bozma riskini önleyen uygulama sırası

1. Önce yalnız doküman/test hazırlığı; mevcut davranış ve rollback planı yazılır.
2. P0 kaynak/UA kural ihlalleri için küçük ayrı PR: testler önce, sonra Indeed/LinkedIn ve Chrome UA temizliği; tam host allowlist.
3. H1 story/claim/evidence/correctionHistory veri sözleşmesi ve fixture testleri; mevcut Haber arşivi kaybolmadan geriye uyumlu alanlar.
4. Kariyer/Haber Custom Tabs helper'ı ve navigation testleri.
5. Network standardizasyonu tek endpoint pilotuyla: offline/empty/429/403/500/timeout/cancel.
6. Room migration: önce export, veri karşılaştırması, rollback; eski SharedPreferences verisini doğrulamadan silme.
7. Tek release SHA: unit + APK + emulator UI QA + kaynak testleri; sonra gerçek telefonda kontrollü test.

Her adım ayrı küçük PR, diff kontrolü, test, üç göz inceleme ve geri dönüş planı. Bu denetimde hiçbir kod/Worker/D1 değişmedi; kullanıcı onayı olmadan merge/deploy yok.

## Kanıt bağlantıları

- C2 APK SUCCESS: https://github.com/actions/runs/45249266638
- C2 Full Application QA SUCCESS: https://github.com/actions/runs/45249266761
- C3 consent guard APK: https://github.com/actions/runs/45226233478
- C3 consent guard Full QA: https://github.com/actions/runs/45226233411
- H2 latest successful reader: https://github.com/aries919-bot/Jarvis/actions/runs/45646698179
- C1 cross-source dedup PR: https://github.com/aries919-bot/jarvis-cloud/pull/10
- H2 integration PR: https://github.com/aries919-bot/jarvis-cloud/pull/9

## SONUÇ

**KARAR:** RED — tek bir yayınlanabilir, uçtan uca doğrulanmış Aries sürümü kanıtlanmadı; C1 10/100 altında; H1 iddia doğrulama katmanı tamamlanmadı.

**Kanıt:** main/feature branch karşılaştırması; C2 APK + emulator QA SUCCESS; C1/H2 canlı okuyucu; kaynak kodu incelemesi; güncel rakip mağaza/API/doküman sayfaları.

**Eksik:** yasaklı kaynak/UA temizliği; tek release SHA; e-Şube/H2 dedup ve production GET/D1; H1 story/claim/evidence/correctionHistory; network retry/body limit; güvenli storage migration.

**Telefonda denenecek:** şu aşamada yeni test yok; önce exact release APK ve C1 feed kapıları geçmeli.

**SONRAKİ ADIM:** P0 kaynak/UA kurallarını küçük PR ile düzelt; sonra H1 kanıt sözleşmesi ve C1 cross-feed exact-head CI. Başka modüllere dokunma.
