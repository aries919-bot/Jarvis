# Aries çekirdek + İş/Kariyer + Film karşılaştırmalı teknik denetimi

**Tarih:** 25.11.2026  
**Denetim tabanı:** durum dalı; çekirdek için main; Kariyer kodu PR #31 HEAD; H2/C1 PR #61 HEAD; medya için PR #1/#2/#4.  
**Kapsam:** Kod/mimari incelemesi ve kamuya açık rakip ürün, doküman ve GitHub araştırması. Kod değiştirme, merge veya deploy değildir.

> Kapalı rakiplerin gizli altyapılarını bildiğimi iddia etmiyorum. Yalnız resmî doküman, açık kaynak kodu ve Aries deposunda görülen davranışlar karşılaştırıldı. Rakipler de kusursuz değildir.

## 1. Yönetici kararı

Aries'in ana sorunu özellik fikri eksikliği değil; çalışan parçaların ayrı dallarda kalması, veri kaynağı doğruluğu ve çekirdek dosyaların aşırı büyümesidir. Bazı iyi çözümler geliştirilmiş ancak tek, doğrulanmış Android sürümüne henüz taşınmış değil.

Öncelik:
1. Main ve özellik dallarının gerçek durumunu tekleştir; tek release commit üzerinden APK/QA kanıtı üret.
2. C1/Haber hattında e-Şube + H2 verisini tek sözleşmede birleştir; çapraz kaynak tekilleştirmesini ve Worker GET/D1'i kanıtla. Eşik dolmadan arayüz kabulü yok.
3. Kariyer kaynak güvenini düzelt: yasaklı kaynakları kaldır, edu.tr genel güvenini kaldır, HTML arama motoru kazımasını doğrulanmış feed'den ayır.
4. Büyük Activity/Manager dosyalarını ViewModel, Repository, UseCase ve küçük kaynak adaptörlerine ayır.
5. Medyada katalog/yasal izleme bağlantısı ile gerçek oynatma kaynağını ayır. Yalnız izinli/lisanslı sağlayıcılar; Film kodu C1 tamamlanmadan değiştirilmemeli.

## 2. Durumun doğru fotoğrafı

### C1 / H2 — son canlı sayım

PR #61 açık, taslak, merge edilmemiş; hedef dalı cline/K1, HEAD:
f2e21dadc2d19ac144814e4f23fef00cf02c4ad9.

H2 canlı Actions #45536720514, job #138945018271 SUCCESS; artifact #14987278818. Reader logundaki satırlar yeniden sayıldı:

| H2 kaynak / kurum | Durum | Toplam kart | Açık doğrulanmış kart | Pozisyon |
|---|---|---:|---:|---:|
| YHGM | fresh, kura/sonuç | 1 | 0 | 2.764 (iş sayılmaz) |
| BOTAŞ | stale, iki timeout | 0 | 0 | 0 |
| Adli Tıp Kurumu | fresh, başvuru 30.11'de açılacak | 1 | 0 | 181 |
| Anadolu Üniversitesi | fresh | 1 | 1 | 1 |
| Düzce Üniversitesi | fresh | 1 | 1 | 5 |
| Tarsus Üniversitesi | fresh | 1 | 1 | 3 |
| TGA | fresh | 1 | 1 | 13 |
| ORAN | fresh | 1 | 1 | 7 |
| Zafer Kalkınma Ajansı | fresh | 1 | 1 | 2 |
| DOKA | fresh | 1 | 1 | 1 |
| Atatürk Üniversitesi — 20.11 duyurusu | fresh | 1 | 1 | 1 |
| Atatürk Üniversitesi — 16.11 duyurusu | fresh | 1 | 1 | 70 |
| Mardin Artuklu — öğretim üyesi | stale, açık başvuru aralığı yok | 0 | 0 | 0 |
| Mardin Artuklu — öğretim görevlisi | fresh | 1 | 1 | 4 |
| Balıkesir Üniversitesi | fresh | 1 | 1 | 7 |
| **Yeniden sayım** | **15 kaynak kaydı / 13 fresh / 2 stale** | **13** | **11 kart / 10 kurum** | **114 açık pozisyon** |

**114 pozisyon, 114 ilan kartı değildir.** Örneğin 70 kadrolu Atatürk duyurusu tek ilan kartıdır. H2 tek başına 10 kurum kaynağına ulaşmış, fakat C1'in 100 doğrulanmış ilan kartı eşiğinde yalnız 11/100 düzeyindedir.

e-Şube ayrı snapshot'ında 42 açık kayıt bulunuyor; collectedAt=2026-11-24T23:51:48Z, rapordaki tazelik sonu 25.11.2026 11:51:48 UTC+3. H2 + e-Şube ham kart adedi 53 görünse de çapraz-feed dedup ve tek Worker feed kanıtı yok. Bu nedenle 53'ü tek doğrulanmış global toplam diye sunmak yanlış olur. Kamu sayımı ayrı kalır.

Kanıt:
- H2 canlı reader: https://github.com/aries919-bot/Jarvis/actions/runs/45536720514
- H2 test: https://github.com/aries919-bot/Jarvis/actions/runs/45536808921
- Python genel test: https://github.com/aries919-bot/Jarvis/actions/runs/45536310829 — 154/154 PASS
- Deterministik Hakem: https://github.com/aries919-bot/Jarvis/actions/runs/45536808964 — workflow SUCCESS; formal GitHub review 0, bağımsız insan/Claude onayı yok
- Kaynak erişim ölçümü: https://github.com/aries919-bot/Jarvis/actions/runs/45536310861 — 28 URL, 12 ÇALIŞIYOR / 16 ENGELLİ / 0 ÖLÜ. URL sayısı kaynak veya ilan sayısı değildir.

**C1 kararı RED.** Eksik: 89 net doğrulanmış ilan kartı; e-Şube/H2 çapraz dedup; tek JSON feed; production Worker GET/D1/kalıcı firstSeenAt; bağımsız inceleme. PR #61 taslak ve merge edilmemiştir. H2 dalı cline/K1 hedeflidir; varsayılan main ile aynı şey değildir.

### Durum belgesi tutarsızlığı

docs/HARITA.md, daha eski C1 sayımlarını ve eski görev sırasını taşıyor; PR #61 açıklaması ve 25.11 02:59Z Actions ölçümü daha yeni. Bir sonraki durum güncellemesinde eski 6/47 veya 9/107 ifadeleri güncel rakam diye kalmamalı. Güncel kanıt H2 = 10 kurum / 11 kart / 114 pozisyon; e-Şube = ayrı 42 kayıt.

## 3. Çekirdek dosyalar — bulgular

### Main sürüm ve dal sorunu — KRİTİK

- Varsayılan main SHA: 63e877d96cb910e6d98d56834ae55ae8edafab2a (21.11.2026).
- main/app/build.gradle.kts yalnız temel Android/Kotlin yapılandırması içeriyor; versionCode=1, versionName=1.0, compileSdk=34, targetSdk=34; Compose, Retrofit, OkHttp ve Media3 bağımlılıkları/buildFeatures burada görünmüyor.
- Buna karşılık medya kaynak ağacında Compose/Retrofit/Media3 kullanan FilmDizi.kt ve MediaSourceBrowser.kt var. Bu konfigürasyon/kaynak uyumsuzluğu derleme riski doğuruyor. Bu incelemede yerel Gradle komutu çalıştırılmadı; dolayısıyla “main kesin derlenmiyor” değil, “main build konfigürasyonu özellik dallarıyla tutarsız ve exact-main CI ile doğrulanmalı” kararı veriyorum.
- PR #1/#2 film-dizi-v2 tabanında; PR #4 kariyer-modulu tabanında; PR #31 C3 dalına yığılmış; PR #61 cline/K1 hedefli. Yeşil PR testleri tek APK'nın hepsini içerdiğini kanıtlamaz.

Dosyalar:
- https://github.com/aries919-bot/Jarvis/blob/main/app/build.gradle.kts
- https://github.com/aries919-bot/Jarvis/blob/main/app/src/main/AndroidManifest.xml
- https://github.com/aries919-bot/Jarvis/pull/1
- https://github.com/aries919-bot/Jarvis/pull/2
- https://github.com/aries919-bot/Jarvis/pull/4
- https://github.com/aries919-bot/Jarvis/pull/31
- https://github.com/aries919-bot/Jarvis/pull/61

### MainActivity.kt ve Ai.kt — YÜKSEK

İyi:
- Komut önce yerel localCommand / Commands.tryHandle / Reminders.tryHandle yolundan geçiyor. Saat, alarm, hatırlatma gibi bilinen komutlar için LLM şart değil. Bu, Home Assistant'ın yerel intent + LLM ajanı ayrımına yakın ve hızlı bir temel.
- Sağlayıcı yedekleme var; hata türlerine göre kullanıcıya mesaj veriliyor.
- Araştırma cevapları kaynak URL listesi taşıyabiliyor.

Yanlış/eksik:
- Ai.Net.webSearch DuckDuckGo HTML'sini regex ile kazıyor ve Android Chrome 120 benzeri User-Agent gönderiyor. Bu kırılgan; C1 kurallarındaki “tarayıcı taklidi yok / AriesKaynakTara/1.0” kuralıyla uyuşmuyor. Ürün kaynak doğrulaması bunun üzerine kurulmamalı.
- Ai.research arama başlığı/snippet'ini modele veriyor; çoğu akışta asıl kaynak sayfasını çekip kanıtlamıyor. Snippet'ten doğrulanmış haber üretilemez. Haber cevabında kaynak sayfası, yayın tarihi ve kanıt alıntısı gereklidir.
- Ai.openAi isteği max_tokens=600 ile sınırlar; Ai.chat/Gemini yanıtı tamamlandıktan sonra ekrana gelir. Token akışı ve cümle/parça bazlı TTS yok.
- withRetry sabit 1 saniye bekleyen tek tekrar kullanır; Retry-After, üstel gecikme, jitter, sağlayıcı devre kesici ve toplam gecikme bütçesi yok. Sağlayıcılar sıralı denenir; kullanıcı bekleme süresi birikebilir.
- Net.get/post bağlantı/okuma timeout tanımlar ama bağlantıyı her yolda disconnect() ile kapatma, yanıt gövdesi boyut sınırı ve iptal sinyali ortak bir katmanda görünmüyor.
- Büyük MainActivity'de ses/AI/komut/ekran işleri iç içe. Ham thread kullanımı Lifecycle iptali ve tekil ağ hata yönetimini zorlaştırıyor.

Dosyalar:
- https://github.com/aries919-bot/Jarvis/blob/main/app/src/main/java/com/cem/jarvis/MainActivity.kt
- https://github.com/aries919-bot/Jarvis/blob/main/app/src/main/java/com/cem/jarvis/Ai.kt

### Store.kt / Manifest — YÜKSEK

- Store.kt konuşmaları SharedPreferences içinde tek JSON dizesi olarak saklıyor; her mesaj tüm sohbetleri yeniden serileştiriyor. Sohbet başına 200 mesaj sınırı var ama toplam sohbet sayısı sınırlanmıyor. Bozuk JSON okunursa bellek içi sohbet önbelleği temizleniyor. Uzun vadede yavaşlama/veri kaybı riski.
- Anahtarlar da aynı Preferences yapısına yazılıyor; Manifest'te allowBackup=true. Android yedekleme davranışı cihaz/ayar koşullarına bağlı olsa da API anahtarlarının sıradan Preferences içinde tutulması güvenlik açığı alanı oluşturur. Keystore ile şifreli saklama, anahtar göçü ve dışa aktarım/temizleme testleri gerekli.
- Bildirim dinleme servisi hassas bir Android iznidir; kullanıcıya hangi bildirim metninin okunabileceği ve verinin nereye gittiği açıkça anlatılmalı.

Dosyalar:
- https://github.com/aries919-bot/Jarvis/blob/main/app/src/main/java/com/cem/jarvis/Store.kt
- https://github.com/aries919-bot/Jarvis/blob/main/app/src/main/AndroidManifest.xml

### Kariyer — somut ihlaller ve iyi parçalar

**KRİTİK: güvenilir alan adı kuralı çelişkili.** CareerSourceCatalog yorumunda kamu üniversiteleri için dört alan adı allowlist'i var; fakat trustedDomains listesine genel edu.tr eklenmiş. isTrustedUrl alt alan adı eşleştirmesi yaptığı için allowlist dışındaki herhangi bir .edu.tr ana makinesi de trusted olabilir. Bu, kamu/özel üniversite ayrımını bozar. Genel gov.tr ve bel.tr aileleri de tek başına kaynak doğrulama yerine geçmemeli; kurum kimliği ve tam kaynak kaydı olmalı.

**KRİTİK: yasaklı iş platformları sorgu kodunda hâlâ var.** CareerSourceCatalog'da Indeed ve LinkedIn kaynakları/trusted domain listesi bulunuyor; CareerSearchEngine platforms listesinde site:tr.indeed.com ve site:linkedin.com/jobs/view sorguları oluşturuyor. NOT_C1'deki açık yasakla çelişiyor. Sadece “ikincil” diye etiketlemek yasağı karşılamaz; kaynak kataloğundan, sorgulardan, test fixture'larından ve UI yönlendirmesinden çıkarılmalı.

**YÜKSEK: mobil HTML arama hattı hâlâ ana yol.** CareerSearchEngine DDG → Bing → Google News RSS → Brave sıralı fallback kullanıyor. Her sorguda en fazla 4 iş parçacığı var; arama sayfası HTML'si/parçacığı üzerinden aday toplanıyor. Erişilebilir arama sonucu, resmî ve açık iş ilanı değildir. Bu, Actions'taki H2/ATS JSON beslemesine yedek veya keşif sinyali olabilir; ana doğrulanmış feed olmamalı.

**YÜKSEK: haber bağlantısı Custom Tabs garantilemiyor.** CareerActivity Haber kartında Intent.ACTION_VIEW ile genel dış tarayıcıya geçiliyor. NOT_C1 açıkça Custom Tabs istiyor. Uygulamada sekmelerin karışmasını önlemek için her kart kendi tab'inde kalmalı; bağlantı AndroidX Custom Tabs ile açılmalı.

**YÜKSEK: sayfa okuyucu SSRF savunması tam değil.** CareerListingPagePolicy HTTPS, userinfo, port, IP literal ve localhost gibi alanları reddediyor; aynı host üst seviye navigasyon ve subresource kontrolleri de var. Ancak alan adının DNS çözümünün özel IP'ye gitmesi / DNS rebinding riski bütünüyle kanıtlanmış değil. Bu risk HARITA'da da kayıtlı. WebView subresource kontrolü + DNS çözümleme ve bağlantı noktasında tekrar doğrulama testi gerekir.

**ORTA: AI maskelemesi eksik olabilir.** E-posta/telefon/11 haneli numara/IBAN regex ile maskeleniyor; ad, adres, plaka, doğum tarihi gibi her kişisel veri bulunmaz. UI'da maskelemenin garanti olmadığı söylenmesi iyi; gizlilik sınırı teknik olarak çözülmüş sayılmaz.

İyi:
- İş / kura / sınav / sonuç / staj sınıflandırması ayrılmış; deadline bilinmiyorsa açık ilan diye sunulmuyor.
- Arama sıralaması resmî kaynağı ve gelecek deadline'ı öne çıkarıyor; gelecekteki tarih tek başına açık başvuru sayılmıyor.
- Kullanıcı tetiklemeli WebView sayfa okuyucu HTTPS/SSL, HTTP hata, CAPTCHA/login belirteçleri ve aynı-host navigasyon kontrolleri içeriyor; kullanıcı onayı olmadan sayfa metni AI sağlayıcısına gönderilmiyor.
- CareerListingSchema AI alanlarını aynı sayfadan kesintisiz kanıt alıntısıyla karşılaştırıyor; deadline/kontenjan/KPSS puan türü/nitelik kodu için alan-özel kontroller var.
- CareerContentClassifier ve deduplikasyon, kura/sonuçların iş ilanına karışmasını ve bazı tekrarları azaltıyor.
- H2 reader kaynak bazlı fresh/stale ve last-good davranışını uyguluyor; başarısız kaynak başarılı kaynakların verisini silmemeli.

Dosyalar:
- https://github.com/aries919-bot/Jarvis/blob/c6795908ccd753f47b216f34c59bc697df4f4d5d/app/src/main/java/com/cem/jarvis/CareerMonitor.kt
- https://github.com/aries919-bot/Jarvis/blob/c6795908ccd753f47b216f34c59bc697df4f4d5d/app/src/main/java/com/cem/jarvis/CareerActivity.kt
- https://github.com/aries919-bot/Jarvis/blob/c6795908ccd753f47b216f34c59bc697df4f4d5d/app/src/main/java/com/cem/jarvis/CareerListingSchema.kt
- https://github.com/aries919-bot/Jarvis/blob/durum/docs/NOT_C1.md
- https://github.com/aries919-bot/Jarvis/pull/61

### Dosya boyutu / bakım maliyeti — YÜKSEK

PR #31 kod tabanında:
- CareerActivity.kt: 3.374 satır / yaklaşık 180 KB.
- CareerMonitor.kt: 2.234 satır / yaklaşık 126 KB.
- MediaSourceBrowser.kt: yaklaşık 2.060 satır / 88 KB.
- Ana MainActivity.kt: yaklaşık 1.667 satır.

Tek dosyada UI, ağ, parse, politika, depolama ve zamanlama birikmesi; küçük değişikliklerde bile geniş regresyon riski yaratır. Bunları tek seferde yeniden yazmak yerine küçük PR'larla böl: UI/ViewModel, domain/use-case, Repository, kaynak adaptörü, politika ve test katmanı.

## 4. İş & Kariyer rakipleri: nasıl daha hızlı ve tutarlı?

### Simplify

Resmî açıklamasına göre şirket kariyer sayfalarından ilanları indeksliyor; profil ve tercihleriyle eşleştiriyor; tarayıcı Copilot eklentisi desteklenen başvuru formlarını dolduruyor; kullanıcı yanıtları inceliyor ve başvuruyu kendisi gönderiyor. Tracker başvuru geçmişini tutuyor. Arama/eşleşme/tracker ücretsiz; gelişmiş otomasyon/Autopilot ve bazı özellikler ücretli. Bu nedenle Aries'in 0 TL hedefiyle tüm özelliklerini bire bir kopyalamak gerçekçi değil.

Ders: tek bir genel HTML araması yerine şirket kariyer sayfası/ATS adaptörleri; ilanı kaydetme; CV eşleşme gerekçesi; kullanıcı denetimli başvuru paketi; takip panosu. Aries otomatik gönderim yapmamalı.

### Huntr

Chrome Web Store tanımı, web'deki iş fırsatlarını yakalama, başvuru notu/tarih/maaş/konum/şirket bilgisini bir arada tutma ve profil üzerinden desteklenen başvuru formlarını otomatik doldurma özelliklerini açıklıyor. Güçlü tarafı yalnız ilan bulmak değil, başvuru sürecini takip etmek.

Ders: Aries'te durumlar Kaydedildi → Hazırlanıyor → Başvuruldu → Sınav/Mülakat → Sonuç şeklinde, hatırlatıcı ve notlarla tek bir başvuru kaydı üzerinde yürümeli. Form gönderme ve kişisel veri paylaşımı kullanıcı kontrolünde kalmalı.

### ATS API'leri — açık kaynak ve resmî dokümanlarla doğrulanan model

Greenhouse Job Board API, şirketin yayımlanmış pozisyonlarını JSON olarak listeler; content=true tam açıklama, departman ve ofis bilgisi ekler. Başvuru POST'u ayrı kimlik doğrulama ister.

Lever Postings API, şirket board slug'ı üzerinden yayımlanmış ilanları JSON döndürür; global/EU uçları vardır. Herkesi kapsayan global tam metin araması sunmaz; her şirket/board için ayrı çağrılır.

Ashby Public Job Posting API, ilan panosu slug'ı üzerinden yayımlanmış işleri JSON sunar; başlık, konum, ekip, açıklama, publishedAt, çalışma türü ve apply URL alanları vardır. Ücret bilgisi opsiyoneldir ve eksik alanlar olabilir.

Bir GitHub ölçüm deposu 16 şirketin 5.122 ilanını bu 4 ATS'nin açık JSON uçlarından örnekleyip alan kapsamasını raporlamış. Bu, üçüncü taraf ölçümüdür; bütün pazarı temsil ettiği iddia edilmemeli.

**Aries'e uygulanabilir desen:** BoardRegistry (şirket adı + ATS + board slug + resmi kaynak URL) → ATS adapter → ortak Job JSON → fingerprint/dedup → kaynak kanıtı + güncellik → profil eşleşmesi → Worker feed. Bu API'ler per-company olduğundan otomatik keşif ayrıca gerekir; kariyer sayfasından görünen ATS slug'ı belirlenir. Sadece belgelenmiş/izinli public endpoint'ler, robots ve kullanım şartları kontrol edilerek kullanılmalı. Ashby web sayfasında robots 401 ise sayfa engelini aşmak için API'ye farklı yoldan gitmek yok; API için ayrı resmî izin/robots ölçümü gerekir.

Kaynaklar:
- Greenhouse: https://docs.greenhouse.io/job-board
- Lever: https://github.com/Lever/postings-api
- Ashby: https://developers.ashbyhq.com/docs/public-job-posting-api
- Örnek alan ölçümü: https://github.com/GregoryBolshakov/ats-job-boards-reference
- Çoklu ATS API listesi: https://github.com/Mersetti/ats-job-board-apis
- Simplify: https://simplify.jobs/
- Huntr: https://chromewebstore.google.com/detail/huntr-job-search-tracker/mihdfbecejheednfigjpdacgeilhlmnf

## 5. Film & Dizi rakipleri: neleri iyi yapıyorlar?

### Jellyfin

Jellyfin metaveriyi tek kaynağa kilitlemiyor: varsayılan TMDb/OMDb/NFO; eklentilerle TheTVDB, fanart.tv, AniDB ve kendi metadata provider'ını geliştirme seçeneği var. Zamanlanmış sunucu görevleri kütüphane tarama, altyazı indirme, bölüm görseli/anahtar kare çıkarma, eklenti güncelleme ve veritabanı optimizasyonu gibi işleri oynat tuşuna basılmadan yapabiliyor.

Ders: başlık/afiş/sezon/bölüm/benzer içerik detayını önceden alıp önbelleğe koy; ekranda açılış sırasında seri ağ isteği bekletme. Sağlayıcı önceliği ve hata kaynağı görünür olmalı.

### Stremio

Stremio Add-on Protocol JSON/HTTP tabanlıdır. Eklenti manifest yayınlar; catalog, meta, stream, subtitles gibi kaynak uçları ayrı çalışır. Uygulama eklenti kodunu çalıştırmaz; manifest'e göre ilgili HTTP uçlarına çağrı yapar. Bu, veri kaynağı ile mobil arayüzü ayırır. Stremio eklenti ekosistemi her tür içeriğe izin veriyor diye Aries'in lisanssız kaynakları kullanması gerektiği sonucu çıkmaz.

Ders: Aries için MediaProvider adaptör sözleşmesi: catalog/search, metadata/detail, availability, playbackLink, subtitles. Kaynak, hak/izin ve ülke bilgisi kaydedilmeli. Yalnız kamu malı, kendi içeriğimiz veya sağlayıcının açıkça izin verdiği akışlar kullanılmalı.

### TMDb / TVmaze / yasal izleme bilgisi

TMDb TV season API sezonun bölüm listesi, bölüm numarası, yayın tarihi, özet ve still verisini sağlar. Aries'te TMDb/TVmaze entegrasyonu ve bölüm seçimi iyi bir temel. Ancak medya detayındaki watch/providers verisi izleme hizmeti/bağlantısı içindir; doğrudan bir video akışı değildir. Watchmode da ülke bazlı servis bulunabilirliği ve bağlantı sunar, fakat kendi dokümanına göre video barındırmaz/stream etmez; tam kapsamlı kullanım ücretli planlara bağlıdır.

Ders: iki ayrı buton olmalı: “Yasal nerede izlenir?” ve “İzinli kaynağı oynat”. Katalog/availability linkini asla oynatılabilir HLS/MP4 gibi gösterme.

### Oynatma hızı — gerçek teknik faktörler

Media3/ExoPlayer doğru Android oynatıcı temeli. Android belgeleri HttpEngine'i API 34+ için önerilen ağ yığını olarak gösteriyor; Cronet HTTP/2 ve HTTP/3/QUIC desteği sunuyor. ExoPlayer disk cache için tekil SimpleCache ve sınırlı alanı boşaltan LRU evictor öneriyor. Seeking, tamponda veri yoksa yeniden ağdan yükleme ve keyframe'e kadar decode gerektirdiği için yavaş olabilir.

Aries için:
- Tek bir paylaşılan OkHttp/HttpEngine/Cronet istemcisi ve bağlantı havuzu.
- Poster/metadata/sezon bölümü/arama sonucu için süreli cache; metadata açılışta seri istek yerine toplu/append çağrısı.
- Media3 SimpleCache boyut sınırı ve LRU; lisanssız/offline video indirmesi yok.
- Oynatma öncesi yalnızca izinli URL'de küçük MIME/manifest doğrulaması; response body/redirect timeout ve iptal.
- Hata halinde aynı izinli sağlayıcıdaki alternatif kalite/URL ve kullanıcıya anlaşılır hata; rastgele arama sonucu otomatik oynatılabilir kaynak sayılmaz.

Kaynaklar:
- Jellyfin metadata: https://jellyfin.org/docs/general/server/metadata/
- Jellyfin eklentiler: https://jellyfin.org/docs/general/server/plugins/
- Jellyfin zamanlanmış işler: https://github.com/jellyfin/jellyfin.org/blob/master/docs/general/server/tasks.md
- Stremio protocol: https://stremio.github.io/stremio-addon-sdk/protocol.html
- Stremio SDK: https://github.com/Stremio/stremio-addon-sdk
- TMDb sezon endpoint: https://developer.themoviedb.org/reference/tv-season-details
- Watchmode API ve kapsam sınırı: https://api.watchmode.com/docs
- Media3 ağ/cache: https://developer.android.com/media/media3/exoplayer/network-stacks
- Media3 seek davranışı: https://developer.android.com/media/media3/exoplayer/troubleshooting

## 6. Medya kodunda iyi / kötü / henüz entegre değil

İyi:
- TMDb/TVmaze metadata; sezon ve bölüm seçimi; favori/kütüphane; Media3 ExoPlayer.
- HLS, DASH, MP4, WebM, MOV, MKV MIME algılama; kısa response probe; kaynak başarısız olunca alternatif deneme.
- PR #1 ve #2 ayrı medya dallarında test+APK başarıları mevcut: #1 Actions #41964659588, #2 Actions #41982871190.
- PR #4'te AriesMediaPolicy taslağı, aynı-origin dışına yönlendirmede Authorization/Cookie/x-api-key/x-* başlıklarını süzen ve HLS/DASH manifest imzasını kontrol eden test edilebilir politika katmanı ekliyor.

Eksik/risk:
- MediaSourceBrowser.kt yaklaşık 2.060 satır; arama motoru sayfası, WebView, DOM/network gözlemi, header aktarımı, aday puanlama, oynatıcıya devretme ve UI aynı dosyada.
- PR #1/#2/#4 farklı taban dallarında ve hiçbiri ana sürüme birleşmiş değil. PR #4 taslak; HEAD için Actions run bulunamadı. Bu yüzden PR #4 güvenlik düzeltmeleri yayınlanmış özellik sayılmaz.
- DDG/Bing/Google sonuç HTML'sinden kaynak aramak hızlı/garantili ürün mimarisi değildir; DOM değişikliği, bölge, rate limit ve captcha akışı bozar. Kaynağı oynatılabilir hale getirmek ayrıca hukuki izin kanıtı değildir.
- WebView yapılandırmasında JavaScript, DOM storage ve third-party cookies açık; Chrome 120 gibi User-Agent kullanımı var. Bu uyumluluk sağlayabilir ama gizlilik/izolasyon alanını büyütür ve genel projedeki tarayıcı taklidi yasağıyla çelişir.
- Media3'e aktarılan istek başlıkları arasında Cookie/Authorization/Origin/Referer ve X-* bulunabiliyor. PR #4 cross-origin redirect'te bunları süzüyor; bu politika mevcut kullanıcı APK'sına taşınmış kabul edilemez. Header'lar yalnız aynı origin veya sağlayıcının belgelenmiş açık gereği için kullanılmalı.
- En önemlisi, Aries'in “kendi oynatıcısında her web videosu” garantisi yoktur. DRM, giriş, coğrafi kısıt, lisans, dinamik token veya sayfa erişim kontrolü olan içerik açılmayabilir ve aşılmamalıdır.

Dosyalar:
- PR #1 medya: https://github.com/aries919-bot/Jarvis/blob/bbb7bd82cc25790ebff97355f4a1476a7e2ba1da/app/src/main/java/com/cem/jarvis/media/MediaSourceBrowser.kt
- PR #2 medya: https://github.com/aries919-bot/Jarvis/blob/3f357d52da2dd21fb7c4c26287b2f7c21873eb8e/app/src/main/java/com/cem/jarvis/media/MediaSourceBrowser.kt
- PR #4 medya politikası: https://github.com/aries919-bot/Jarvis/blob/7465a28d8509fb66d2254470215fb1d5ba2e7ae5/app/src/main/java/com/cem/jarvis/media/AriesMediaPolicy.kt

## 7. Rakip asistanlardan çekirdek dersi

Home Assistant Assist sesi alt aşamalara ayırıyor: wake word → STT → intent/conversation agent → TTS. Bazı aşamalar cihazda/yerelde, bazıları bulutta çalışabilir. Pipecat token akışını LLM'den TTS'ye parça parça verir; frame/processor yapısı ve kullanıcı konuştuğunda iptal edilebilen işler kesintiyi yönetmeye yarar.

Aries'te iyi temel: yerel komut önce, AI sonra. Eksik: streaming yanıt, TTS cümle parçaları, VAD/konuşma kesme (barge-in), yankı kontrolü, açık iptal ve ölçülen gecikme. Bunu ücretsiz/yerel seçeneklerle kademeli yap; her komutta LLM çağırma.

Kaynaklar:
- Home Assistant pipeline: https://contributors.home-assistant.io/core/internals/assist-pipelines/
- Home Assistant voice architecture: https://contributors.home-assistant.io/architecture/platform/voice/
- Pipecat quickstart: https://github.com/pipecat-ai/docs/blob/main/pipecat/get-started/quickstart.mdx
- Pipecat frame semantics: https://github.com/pipecat-ai/docs/blob/main/api-reference/server/frames/overview.mdx
- Pipecat LLM streaming: https://github.com/pipecat-ai/docs/blob/main/pipecat/learn/llm.mdx

## 8. Aries'i 0 TL ile daha iyi yapma planı

### P0 — güven ve tek sürüm
1. Tek ana entegrasyon dalı / tek release commit kararı. PR'lar doğru tabana hedeflenmeli; Cline/K1, kariyer-modulu, film-dizi-v2 ve main arasındaki farklar compare ile raporlanmalı.
2. Main için gerçek Gradle build + unit tests + APK + imza + API29/33/34/35 QA çalıştırılmalı. Main'in build.gradle dosyası özellik kaynaklarıyla eşleştirilmeli.
3. Indeed/LinkedIn ve Chrome impersonation kalıpları tamamen kaldırılmalı; testte kaynak kodunun tamamı taranmalı.
4. edu.tr genel trusted domain kaldırılmalı; her kurum tam resmi host + kurum türü + owner + ölçüm zamanı ile kayıtlanmalı.
5. Haber/kariyer dış linkleri AndroidX Custom Tabs ile açılmalı; source URL, tarih ve kategori kartta görünmeli.
6. Preferences içindeki sırlar Android Keystore tabanlı encrypted storage'a taşınmalı; sohbet/takip verisi Room/Datastore ve migration testleriyle ayrılmalı.

### P1 — C1 veri hattı
1. H2 15 kaynak kaydını Actions'ta toplamaya devam et; her satırda publishedAt/null, deadline, collectedAt, firstSeenAt, officialSourceUrl ve freshnessStatus koru.
2. e-Şube girişsiz okuyucunun 42 kaydını tazele; H2/e-Şube tekilleştirmesini ilan kimliği + kurum + pozisyon + şehir + deadline fingerprint ile yap. Ham kayıtları kaybetme; hangi kaynakların birleştirildiğini provenance olarak sakla.
3. Worker GET yalnız JSON sunmalı; D1 firstSeenAt kalıcı olmalı; authenticated GET canlı test; boş/403/500/timeout/robots/expired source ve partial-outage matrisi.
4. 10 kurum kaynağı tamam; ilan kartı 11/100. Gerçek ve açık ilan olmadan kontenjanları kart sayısına çevirmek yasak. 89 ek benzersiz doğrulanmış kart gerekir.
5. Başarılı exact-head Actions, kaynak başına sayım ve bağımsız hakem/review olmadan RED kalkmaz.

### P2 — Kariyer deneyimi (C1 kabulünden sonra)
- CareerActivity'yi UI, ViewModel, Repository, use-case ve kart bileşenlerine ayır.
- ATS adaptörleri (Greenhouse/Lever/Ashby/SmartRecruiters/Workable/Recruitee) için public endpoint + robots/terms + board slug doğrulama kaydı yap; unsupported ATS'lerde resmi web bağlantısı.
- Ortak JobListing şeması: id, employer, title, location, employmentType, publishedAt, deadline, applicationOpensAt, sourceUrl, applyUrl, requirements, kpssType, qualificationCodes, quota, sourceStatus, firstSeenAt, collectedAt.
- Dedupe için canonical URL + ATS posting ID + institution ID; fuzzy title/location yalnız aday eşleştirme, otomatik silme değil.
- CV eşleşmesi açıklanabilir olmalı; puan sadece kanıta dayansın; başvuru gönderimi kullanıcı tarafından yapılsın.
- İş/Kura/KPSS/Haber ayrımı filtrelerde aynı enum'u kullansın; tarih ve sınıflandırma testleri.

### P3 — Film araştırmasından sonra, izinli kaynakla
- MediaSourceBrowser'ı MetadataRepository, AvailabilityRepository, MediaProvider, SourceResolver, PlayerController olarak böl.
- TMDb/TVmaze metadata önceden alınsın; bölümler season/{n} ile çekilip cache edilsin.
- Ülkeye göre yasal izleme hizmeti bağlantısı göster; bu URL'yi stream olarak işaretleme.
- Stream adaptörleri sadece açıkça izinli kaynak/sağlayıcı; manifest/MIME/redirect/headers güvenlik testi.
- Media3 single-instance SimpleCache, HttpEngine/Cronet veya OkHttp fallback, LRU cache sınırı, timeout ve cancellation.
- “Otomatik video bulundu” etiketini ancak URL'nin kaynağı ve oynatma hakkı doğrulanıyorsa göster; DRM/login/CAPTCHA/geo-block bypass yok.

### P4 — Asistan
- Yerel intent router + hızlı araçlar (saat, alarm, not, cihaz eylemi); LLM sadece serbest dil ve karmaşık yorum için.
- Streaming API destekleyen sağlayıcıda token → cümle tamponu → TTS akışı; kullanıcı araya girince ağ isteği ve TTS iptal.
- Her provider için timeout bütçesi, Retry-After, exponential backoff+jitter, circuit breaker, bağlantı havuzu ve yanıt bayt sınırı.
- Net yerine tek OkHttp/Retrofit katmanı; structured logs, redacted error, request id.
- Room/DataStore + encrypted key storage, migration, export, corruption recovery.

## 9. Hüküm tablosu

| Alan | Şu anki seviye | İyi | Kötü / ana engel |
|---|---|---|---|
| Repo/release | Dağınık | CI ve bağımsız iş dalları var | Özellikler farklı tabanlarda, tek APK kanıtı yok |
| C1/H2 | Kısmi kanıtlı | 10 kurum / 11 kart; 154/154 genel test | 100 kart yok; e-Şube dedup ve Worker GET yok |
| Haber | Kısmi | kaynak/tarih/kategori ayırımı | Custom Tabs ve tam doğrulanmış kaynak akışı eksik |
| Kariyer | Kısmi | CV eşleşme, açıklanabilir kanıt, tracker | yasaklı kaynak + trusted domain çelişkisi + HTML search |
| Asistan | Temel işlev | yerel komut önce | non-streaming, raw thread/network, monolit |
| Film | araştırma/özellik dalları | TMDb/TVmaze/Media3 ve fallback prototipi | yetkili stream provider yok; PR'lar ana sürüme birleşmemiş |
| Güvenlik | Kısmi | user consent, TLS, bazı URL guard'ları | secrets storage, DNS rebinding, WebView cookie/UA alanları |

## 10. Kaynak sınırı ve güven

- Resmî ürün dokümanları ve resmî GitHub projeleri rakip mimarisi için birincil kaynak olarak kullanıldı.
- ATS saha sayıları üçüncü taraf açık kaynak ölçümünün tarihli snapshot'ıdır; her ATS/şirket için gelecekte aynı alanların dolu olacağı garanti edilmez.
- Simplify/Huntr kapalı ürünlerdir; iç servis altyapıları kamuya açık değil. Yalnız yayınladıkları özellik/iş akışları anlatıldı.
- Film akış kaynağı bulma konusunda arama motoru sonuçları/DOM seçicileri oynatma garantisi değildir. İzin, DRM ve başvuru/abonelik duvarı aşılmayacak.
- Bu denetimde Android cihazda manuel oynatma, gerçek mikrofon arka plan testi veya main branch Gradle build çalıştırılmadı. Bunlar eksik kanıt olarak kalır.

**KARAR:** RED — Aries'in tek ve güncel, uçtan uca doğrulanmış sürüm olduğu kanıtlanmadı. C1 de 10 kurum / 11 ilan kartı ile 10/100 altında.  
**Kanıt:** PR #61 H2 live #45536720514 logu; 154/154 Python tests; H2 53 test; 28 URL erişim ölçümü; main ve feature branch kaynak dosyaları; rakiplerin resmî API/eklenti dokümanları.  
**Eksik:** Tek release dalı; global e-Şube+H2 dedup; production Worker GET/D1; bağımsız formal review; yasaklı kaynak temizliği; main APK CI; gerçek cihaz testi.  
**Telefonda denenecek:** Şu aşamada hayır; önce tek APK'nın derlenmesi ve C1 feed kabulü.  
**SONRAKİ ADIM:** Önce P0 güven/kaynak ihlallerini ayrı küçük PR'lara böl ve C1 feed/dedup + production GET kanıtını tamamla. Film yalnız araştırma olarak tutuldu; kod uygulaması sırası C1 sonrasıdır.
