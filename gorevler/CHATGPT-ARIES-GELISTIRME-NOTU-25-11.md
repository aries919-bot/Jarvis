# ARIES — CHATGPT ANA GELİŞTİRME NOTU
## Rakip ürün araştırması, teknik envanter, eksik analizi ve yol haritası
**Hazırlanma tarihi:** 25 Kasım 2026  
**Amaç:** Bu dosya yeni bir ChatGPT sohbetine verilecek ana çalışma talimatıdır. Kullanıcının paylaştığı önceki Aries notunun 1–9. bölümlerini ve PDF'deki eklenti mimarisi 10–12. bölümlerini birleştirir; GitHub kod incelemesi ve tarihli web kaynaklarıyla günceller.

# NOT BAŞLANGICI

## 0. ChatGPT için başlangıç talimatı

Sen Aries projesinin kıdemli ürün mühendisi, Android/web mimarı, araştırmacısı ve bağımsız hakemisin. Kullanıcı Türkçe konuşur; Türkiye kaynakları ve mevzuatı önceliklidir. Hedef yalnızca çok özellikli bir uygulama yapmak değil; hızlı, güvenilir, şeffaf, mahremiyete saygılı, bakımı yapılabilir ve gerçekten çalışan bir ürün oluşturmaktır.

**İlk işin kod envanteri ve kanıt incelemesidir.** Kodda görmediğini var sayma. Repo ekranında görünmeyen özelliği “çalışıyor” diye yazma. Kodun bulunması, özelliğin kullanıcıya ulaştığı veya doğru çalıştığı anlamına gelmez. Her özellik şu üç seviyeden biriyle işaretlenir:
- Çalışıyor: gerçek kod yolu ve test/çalıştırma kanıtı var.
- Kısmi: kod izi var ama uçtan uca çalışma, veri doğruluğu veya test kanıtı eksik.
- Yok/kanıtsız: kodda görünmüyor veya var olduğu iddiasını destekleyen test/çıktı yok.

Bu belge ürün araştırması açısından İş&Kariyer, Film, Borsa, Asistan çekirdeği ve Aries'in ayrı web/uygulama ürünleri olarak değerlendirilmesini ister. Ancak **mevcut uygulama geliştirme kapsamı, kullanıcının aktif durum notlarında belirtilen İş&Kariyer C1 + Haber doğrulama işiyle sınırlıdır.** Film ve Borsa bu notta araştırma ve gelecek yol haritası olarak ele alınır; C1 kapanmadan ve kullanıcı ayrıca kapsamı açmadan bu modüllerde kod değişikliği yapılmaz. Tema, görsel makyaj veya alakasız işler başlatılmaz.

### Önce okunacak dosyalar
Her yeni “devam et” mesajında:
1. Aries/Jarvis deposunun durum dalındaki docs/HARITA.md ve docs/NOT_C1.md dosyalarını yeniden oku.
2. İlk DEVAM işi neyse oradan devam et. Eski mesajların özetini güncel dosyaların yerine koyma.
3. GitHub dalının gerçek HEAD SHA'sını doğrula. Dosya içeriğinin hangi dala ait olduğunu açıkça yaz.
4. aries919-bot/Jarvis Android uygulaması ile aries919-bot/jarvis-cloud Worker/API deposunu ayrı ayrı incele. Özel repo açılamazsa bunu açıkça söyle; tahmin yürütme.
5. İşin kapsamına göre ayrı web/PWA projesi veya diğer depo varsa onu da arayıp envantere ekle. Bulunmayan repo için “erişim yok / bulunamadı” yaz.

### Kesin çalışma ve onay kuralları
- Kullanıcıdan Termux komutu, token, API anahtarı, CV, KPSS, e-Devlet bilgisi veya başka sır isteme.
- Kullanıcının “birleştir” onayı olmadan PR birleştirme. Kullanıcının açık onayı olmadan deploy, yayın, dış sisteme mesaj, başvuru, emir veya veri silme yapılmaz.
- Her kod değişikliği ayrı dal/PR; diff yalnız ilgili iş kapsamındaki dosyalarda; CI ve hakem incelemesi gerekir.
- “Bitti” demek için gerçek komut/test çıktısı, değişen dosyaların sayısı, kaynak başına ilan sayısı (ilgili işte), CI bağlantısı ve bağımsız yeniden sayım gerekir.
- Bir hata düzeltilmeden başka modüle atlama. Başarısız testi gizleme veya test kapsamını azaltma.
- Kod, test ve çalışma durumunu birbirinden ayır. “Kod var”, “CI geçti”, “üretimde çalışıyor” ve “telefonda doğrulandı” ayrı kanıtlardır.
- Son rapor şu başlıklarla biter: **KARAR, Kanıt, Eksik, Telefonda denenecek, SONRAKİ ADIM.**

## 1. Rakip ürünler neden hızlı ve güvenilir görünüyor?

Hiçbir ürün kusursuz değildir. Hız ve iyi kullanıcı deneyimi genellikle “daha büyük bir model” veya çok sayıda eklentiden değil, aşağıdaki mimari birleşiminden gelir:

### 1.1 Üç kademeli yönlendirme
1. **Yerel/deterministik kademe:** Saat, tarih, hesap makinesi, alarm, hatırlatma, uygulama açma, ses seviyesi, basit arama ve bilinen cihaz komutları modele gitmeden çalışır. Hızlı ve ucuzdur.
2. **Belirli beceri / API kademe:** “İlanları filtrele”, “KAP bildirimlerini getir”, “sezonları listele”, “fonları karşılaştır” gibi istekler sabit ve test edilmiş iş akışlarına yönlenir. Model gerekiyorsa yalnızca gerekli alanları çıkarır; veri kaynağı ve kurallar kodla doğrulanır.
3. **Genel ajan kademe:** Yalnızca çok adımlı, belirsiz ve planlama gerektiren istekler tam LLM/araç döngüsüne girer.

PokeClaw bu üç kademeyi açıkça uyguluyor: basit komutlar sıfır LLM çağrısı, eşleşen beceriler sabit adımlarla, zor görevler ajan döngüsü. Home Assistant da niyet eşleşmesini konuşma ajanından ayırıyor. Aries'te MainActivity yerel komut kontrolü ve Commands protokolü zaten var; ancak resmî bir router/skill kayıt defteri, test edilmiş beceri eşleştirmesi ve görünür görev planı yok.

### 1.2 Araç sözleşmesi ve izin sistemi
Her araç bir kayıt defterinde tanımlanmalı:
- tool adı ve açıklaması;
- JSON girdi/çıktı şeması ve zorunlu alanlar;
- okuduğu veri ve kaynağı;
- yan etkisi var mı;
- hangi izinleri gerektirir;
- hangi durumda kullanıcı onayı ister;
- zaman aşımı, tekrar deneme ve idempotency anahtarı;
- hata kodları ve izleme alanları.

Salt okunur araçlar önce gelir. Mesaj gönderme, e-posta yollama, başvuru yapma, dosya silme, arama/emir verme, hesap/ayar değiştirme daima açık onay ister. Kullanıcı aracı her zaman durdurabilmeli.

### 1.3 Akışlı yanıt
Modelin bütün cevabını bekleyip sonra tek seferde göstermek yerine desteklenen sağlayıcı ve Worker uçları üzerinden akışlı metin gönderilir. UI metni geldikçe gösterir. Sesli kullanımda tamamlanmış cümle parçaları TTS'ye verilir; aynı anda konuşma ve kesme/durdurma mümkün olur. Bu yaklaşım bekleme algısını azaltır. Streaming gerçek sağlayıcı desteğiyle ve iptal sinyaliyle uygulanmalıdır; yalnızca animasyonlu “düşünüyor” ekranı akış değildir.

### 1.4 Önbellek ve kaynak tazeliği
- Sabit katalog/meta verisi uzun; fiyat, ilan, yayın akışı gibi değişken veri kısa TTL ile önbelleğe alınır.
- Aynı isteğin eşzamanlı tekrarları tek uçuşta birleştirilir (request coalescing).
- Kaynak başına son başarılı veri tutulur; tek kaynak bozulursa tüm ürün çökmez.
- Ekranda “son güncelleme”, “kaynak”, “tazelik” ve varsa gecikme görünür.
- Önbellekteki eski veri güncelmiş gibi gösterilmez. Eski değer stale olarak etiketlenir.
- Liste sayfalarında sayfalama, arama ve filtre sunucu tarafında yapılır; bütün veriyi telefona indirme.

### 1.5 Uzmanlaşmış veri katmanı
İyi finans ve iş ürünleri doğrudan LLM'e web sayfası okutmak yerine yapılandırılmış, normalize edilmiş ve denetlenebilir veriyi önce getirir. Model sadece sorguyu anlamlandırır, özetler ve kullanıcıya anlatır. Her sayısal iddia kaynaktaki gerçek değere ve as_of/fetched_at zamanına bağlanır.

### 1.6 Test, iz ve durdurma
Her ajan/araç çağrısı iz kaydına girer: istek kimliği, seçilen model, araç adı, argüman özeti, süre, hata, yeniden deneme, kaynak, token/maliyet (varsa). Sır, tam CV veya özel mesaj içeriği loglanmaz. Ajan döngüsünde görev bütçesi, süre sınırı, tekrar algılama, iptal ve kullanıcıya devretme bulunur. Test seti gerçek kullanıcı senaryolarından türetilir.

## 2. Araştırılan rakipler ve alınabilecek mimari dersler

### 2.1 ChatGPT Apps SDK ve MCP
OpenAI Apps SDK, MCP üzerine kurularak ChatGPT içinde hem araç mantığı hem etkileşimli arayüz tanımlamaya izin verir. Önizleme statüsündedir; uygulama kendi backend'ine bağlanır, veri paylaşımı/izinleri kullanıcıya açık olmalıdır. Aries için ders: modüllerin işlevlerini standart araçlar olarak ayırmak; gerekiyorsa sonuçları küçük etkileşimli UI bileşenleriyle göstermek. Apps SDK, Aries'in Android kodunu kendiliğinden ChatGPT'ye taşımaz; ayrıca geliştirilen MCP sunucusu ve UI gerekir.

MCP 2026-07-28 sürümü durumsuz protokol çekirdeği, çok turlu girdi isteme, HTTP başlıklarıyla yönlendirme, önbelleklenebilir liste yanıtları ve yetkilendirme iyileştirmeleri getirdi. Bu sürümde tools/list, prompts/list, resources/list ve resources/read gibi sonuçlar TTL ve cache scope ipuçları taşıyabilir; istemci/sunucu uyumlu sürüm müzakeresi ve server/discover kavramı bulunur. Eski örneklerdeki el sıkışma ve SSE varsayımlarını yeni protokole aynen kopyalama. MCP SDK sürümü, protokol tarihi ve taşıma biçimi birlikte sabitlenmeli.

Aries için önerilen ilk araçlar salt okunur olsun:
- career.search_verified_jobs
- career.get_job_detail
- news.get_official_notice
- film.search_catalog
- film.get_seasons_episodes
- film.get_watch_providers
- market.get_quote
- market.get_kap_disclosures
- market.get_fund_data
- assistant.search_memory
- assistant.list_reminders

MCP uç noktası ilk sürümde yalnızca kimlik doğrulamalı ve salt okunur olsun. Yazma araçları ayrı onay politikası olmadan açılmasın.

### 2.2 Open WebUI
Open WebUI; yerel Python araçları, MCP, OpenAPI, fonksiyon/pipeline ve beceri yönergeleriyle genişletilebilen bir arayüz sunuyor. Büyük araç listesini her modele yüklemek yerine Tool Search ile gerektiğinde arama fikri önemli. MCP bağlantıları yönetici tarafından tanımlanır; yetki sınırı ve token saklama güvenliği kritiktir. Aries için ders: bütün özellikleri devasa bir sistem mesajına yığmak yerine beceri/araç katalogları ve kısa görev talimatları kullan.

### 2.3 Home Assistant Assist
Ses hattı ayrı parçalar hâlinde tasarlanmış: uyandırma sözcüğü, STT, intent/konuşma ajanı, işleme ve TTS. Yerel niyet tanıma basit komutu LLM olmadan tamamlayabilir. Aries için ders: ses tanıma, niyet, eylem ve TTS ayrı modüller olsun; ses sağlayıcısı değişince tüm asistan yeniden yazılmasın.

### 2.4 Gemini Live
Kamera veya ekran paylaşımı sırasında kullanıcı konuşmaya devam edebiliyor; cihazın ekranda gördüğü şey hakkında bağlamsal yardım veriyor. Aries için fikirler: kullanıcı izniyle belge/ekran açıklama, kamera üzerinden nesne/etiket okuma, açık uygulamaya göre yardım. Ekran paylaşımı/erişilebilirlik çok güçlü mahremiyet iznidir; varsayılan kapalı, oturumluk ve ekranda belirgin durdurma olmalıdır.

### 2.5 Android ajan örnekleri
- **PokeClaw:** Gemma 4 ile yerel cihazda çalışma ve isteğe bağlı bulut modeli; üç kademeli router; MacroDroid/Tasker intent girişi; 8 GB RAM alt sınırı ve CPU'da yavaşlama uyarıları; becerilerle 3–10 LLM turunu atlayabilen sabit iş akışları; token bütçesi ve takılı kalma algısı. Xiaomi 11T üzerinde uygunluğu varsayma; gerçek cihaz testi gerekir.
- **CellClaw:** Kotlin/Jetpack Compose, Hilt, Room, TensorFlow Lite, provider adaptörleri, AgentLoop, HeartbeatManager, AutonomyPolicy, ApprovalQueue, SecureKeyStore, WorkManager ve ayrı tool/skill katmanları. 33 araç iddiası proje sahibinin açıklamasıdır; Aries kodu olarak kabul edilmez.
- **ClawDroid:** Kotlin/Compose arayüzü ve APK içine gömülü Go backend, 40+ araç, MCP/Cron/skills/memory ve kanallar; uygulama ile yerel backend arasında WebSocket. Aries için tek bir devasa Activity yerine sınırları belirlenmiş çekirdek servisleri düşün.
- **ClosePaw:** görünür overlay, her adımı durdurma/devralma, uygulama başına SKILL.md, Accessibility tree, opsiyonel ekran görüntüsü/virtual display, iz kaydı ve değerlendirme (eval) döngüsü. Güçlü erişilebilirlik izni ve ekran verisi riski var. Aries'te erişilebilirlik varsayılan kapalı olmalı; önce Android Intent/standart izinli API'ler.
- **PhoneClaw:** cihazda arka plan servis/voice giriş ve yerel otomasyon fikri; kaynak kodu olmayan veya decompile edilmiş projeleri üretim bağımlılığı yapma.

Bu projeler yeni veya küçük olabilir. Yalnızca mimari desen al; lisans, bakım, güvenlik geçmişi, cihaz uyumluluğu ve son commit tarihini incelemeden kod kopyalama.

## 3. Aries GitHub kod envanteri — doğrulanmış gözlemler

**İncelenen kaynak:** aries919-bot/Jarvis, durum dalı, HEAD d94bda0ca959c9919934c9aaff194bce04c2edc8; ayrıca aries919-bot/jarvis-cloud varsayılan main dalı. Özel Worker'daki açık PR'lar varsayılan main'e dahil değildir.

### 3.1 Android uygulaması
- app/src/main/java/com/cem/jarvis/MainActivity.kt: büyük Activity; ana sayfa, sohbet, ses, görevler, dosyalar, ayarlar, haberler, sohbet geçmişi, TTS, yerel komut yönlendirme. Kod izi güçlü; test/cihaz kanıtı olmadan bütün ekranlar “tam çalışıyor” sayılmaz.
- Ai.kt: Gemini, Groq, Mistral, Aries Bulut sağlayıcı çağrıları; yedek sağlayıcı sırası; Gemini Google Search grounding; Google News RSS başlık okuma; DuckDuckGo HTML arama; Finnhub tekil fiyat fonksiyonu; hata/yeniden deneme. Web arama işlevi 5 sonuca kadar HTML ayrıştırıyor; bu resmi ve kararlı arama API'si değil. Kodda tarayıcı User-Agent taklidi var; kaynak tarama kuralına aykırı, kaldırılmalı.
- Commands.kt: telefon/uygulama/müzik/medya gibi yerel komut eşleştirmeleri; izin gereken telefon/kişiler işlemleri. Henüz formal araç kataloğu ve bütün komutlar için schema/approval kayıt defteri kanıtlanmadı.
- Reminders.kt: hatırlatma kayıtları, alarm zamanlama, bildirim ve açılış sonrası geri yükleme. Kullanıcı tanımlı genel görev/zamanlama sistemiyle aynı şey değil.
- Store.kt: yerel sohbet geçmişi, ayarlar/facts, görevler, izleme listesi, JSON içe/dışa aktarma; hafıza silme ve veri temizleme fonksiyonları mevcut.
- Notif.kt ve Manifest: NotificationListenerService mevcut; Android manifest mikrofon, telefon arama, kişiler, internet, bildirim, açılış ve Bluetooth bağlantı izinlerini listeliyor. Gereksiz izinler azaltılmalı, hassas izinlerin amacı kullanıcıya açıklanmalı.
- Util.kt: matematik ayrıştırıcı ve telefon yardımcıları.
- Views.kt: özel çizim ve ortak UI bileşenleri.
- media/FilmDizi.kt: Compose, Retrofit ve TMDB ile katalog arama, trend/popüler içerik, TV sezonu/bölümü, fragman, watch-provider bölgesi ve geçmiş/izleme ekranları için kod var.
- media/MediaSourceBrowser.kt: büyük WebView tabanlı kaynak keşfi, TVmaze/TMDB aramaları, sezon/bölüm seçimi, sayfa açma ve medya adayı tespiti kodu var. Bu kodun bulunması, kaynakların yasal/izinli olduğunu veya DRM'li içeriğin çalıştığını kanıtlamaz.

### 3.2 Kritik Android derleme farkı
main ve durum dallarındaki app/build.gradle.kts dosyasında görülen içerik, mevcut Film kodunun kullandığı Compose, Retrofit, OkHttp, Coil, lifecycle ve YouTube player bağımlılıklarını tanımlamıyor; Compose/buildConfig yapılandırması da görünmüyor. film-dizi-v2 dalındaki Gradle dosyasında bu bağımlılıklar ve build özellikleri var. Android workflow dosyası .github/workflows/android.yml yalnız main, master, film-dizi-v2 dallarında push tetikliyor; durum listede yok. durum dalında APK derleme/test kanıtı bulunmadan APK çalışıyor denemez. Bu, ilk çözülmesi gereken temel build/reproducibility sorunudur. Derleme komutu gerçekten çalıştırılmadan “kesin derleme hatası” diye de hüküm verilmez; dosya karşılaştırması kanıtı ve test ihtiyacı olarak yazılır.

Kod ağacında app/src/test veya app/src/androidTest test klasörü görünmedi. Repository tree'de Gradle wrapper dosyası da görünmüyor. Bunları “test yok” diye nihai hükme çevirmeden önce repo HEAD ve workflow kontrolüyle doğrula.

### 3.3 Worker / web backend
aries919-bot/jarvis-cloud varsayılan main dalında:
- Cloudflare Worker src/index.js, D1 database DB, Workers AI binding ve zamanlanmış cron tanımı bulunuyor.
- src/arkaplan.js: araştırma/çoklu arama ve öneri-kod üretim iş akışları.
- src/hafiza.js ve src/memory.js: hafıza, ilgili konuşma bulma, otomatik not ve kayan özet fonksiyonları.
- Koddaki route adlarında /chat, /hizli, /film, /kariyer-ara, /memory, /oy_sohbet, /arastir ve diğerleri mevcut. Her route için auth, veri sınırı, rate limit, CORS, log ve idempotency ayrı ayrı denetlenmeli.
- Bu repo bir API/Worker backend'idir; ağaçta bağımsız web frontend/PWA uygulaması görünmüyor. Ayrı web ürünü yapılacaksa yeni UI projesi veya açıkça ayrılmış web uygulama klasörü gerekir.
- D1 hafızada keyword eşleştirme ile ilgili notlar seçiliyor, konuşma özetleniyor ve eski veri temizleniyor. Bu, gelişmiş vektör/semantic memory olduğunun kanıtı değildir.

### 3.4 İş & Kariyer C1 durumu
Son tam kaydedilmiş exact-head denetim: public Hakem Actions #45517626605  
https://github.com/aries919-bot/Jarvis/actions/runs/45517626605

Bu rapora göre:
- 46/46 C1 birim/regresyon testi PASS.
- Yerel Worker + D1 smoke 11/11 PASS; bu üretim testi değildir.
- Hata matrisi 17/17 PASS.
- Tek Actions collector çıktısı: 26 kaynak, 17 fresh / 9 stale; 163 taze aday kayıt, 47 doğrulanmış açık iş, 116 son tarihi bilinmeyen kayıt, 0 süresi geçmiş kayıt.
- Kamu ayrı: 6 kaynak /47 doğrulanmış açık iş (e-Şube 42 + H2 5). H2 kartları Anadolu, Düzce, Tarsus, Marmara ve Zafer kaynaklarından.
- 114 özel ATS adayı deadline unknown; doğrulanmış açık ilan sayılmaz.
- Sabit eşik 10 kaynak/100 doğrulanmış açık ilan; gerçekleşen 6/47: RED.
- Üretim authenticated GET BLOCKED; JARVIS_WORKER_URL ve JARVIS_WORKER_TOKEN yapılandırması kanıtlanmadı; publish/deploy yapılmadı.
- jarvis-cloud PR #9 açık/taslak, HEAD 8a43b99981db601de3442e8394b71d0651d0e34e, 5 dosya, +442/-10, formal GitHub review 0; birleştirilmedi.
Bu sayılar 25.11.2026 tarihli tarihsel Actions sayımıdır. Yeni çalışma yaparken eski sayıyı güncelmiş gibi yazma; yeni run çalıştır ve yeniden say.

## 4. İş & Kariyer araştırması: rakipler ve Aries boşlukları

### 4.1 Türkiye ürünleri ve resmî kaynaklar
- **Kariyer.net Mülakat Provası:** pozisyon/ilan tanımına göre soru üretimi, sesli veya görüntülü yanıt, yapay zekâdan performans geri bildirimi. Resmî sayfasında ücretsiz olduğu belirtiliyor; yardım/SSS kısmında oturum başına 4 soru ve yaklaşık 10 dakika azami süre açıklanıyor. Bu özelliğin Aries'te karşılığı henüz kod/testle kanıtlanmadı.
- **Kariyer.net Maaş:** kendi yardım sayfası maaş verilerinin adayların iş başvurularındaki maaş beklentisi ve anketle paylaşılan maaş bilgisi gibi kaynakları kullandığını açıklıyor. Dolayısıyla rakamları resmî ücret istatistiği gibi göstermemek; örneklem ve veri türünü açıkça belirtmek gerekir. “10.000 pozisyon” ve “Kariyer 360 çoklu ajan” iddiaları bu notta bağımsız resmî kaynakla doğrulanmadığından ürün kararı için kanıt kabul edilmesin.
- **İŞKUR Açık İş Haritası:** il/ilçe/meslek filtresi, harita ve Excel indirme gibi resmi özellikler var. Harita dinamik veridir; ekrandaki toplam ilan sayısını kalıcı sabit sayı gibi not etme.
- **e-Şube:** açık iş arama sayfası. Giriş gerektiren bölümleri kazıma; uygulamada resmî bağlantıya Custom Tabs üzerinden yönlendir.
- **Kariyer Kapısı:** giriş isteyen sayfaları otomatik kazıma; ilan/kamu duyurusu için resmî duyuru bağlantısına geçiş sağla.
- Resmî kaynaklar: e-Şube, ilan.gov.tr, Resmî Gazete ve kurumların resmî duyuru sayfaları. Her hedefin robots.txt/şartları ve teknik erişilebilirliği önce ölçülür.

### 4.2 Global ürünler
- **Teal:** ücretsiz çekirdek Job Tracker, Resume Builder ve tarayıcı eklentisi; farklı iş sitelerindeki ilanı kaydetme; iş tanımıyla CV eşleştirme.
- **Huntr:** Kanban iş başvuru panosu, başvuru aşaması, aktivite zaman çizelgesi, kişi/iletişim takibi, maaş/son tarih/not/doküman, her ilana özel CV ve kapak mektubu, job clipper ve otomatik form doldurma.
- **Simplify:** iş arama planı, ATS eşleşme analizi, role göre CV madde uyarlama, kapak mektubu, ücretsiz Copilot form doldurma, ücretli/limitli Autopilot ile desteklenen sitelere başvuru gönderme. Aries otomatik başvuru/otomatik gönderim yapmayacak; her başvuru kullanıcı onaylı kalacak.
- **Jobscan:** özgeçmiş ile belirli iş tanımını eşleştirme; beceri, eğitim, unvan, kelime ve ATS okunabilirliği kontrolü. Eşleşme puanı iş garantisi veya gerçek ATS kararının aynısı değildir.
- **JobSpy / LinkedIn / Indeed:** rakip örneği olsa da Aries'e eklenmeyecek. Kullanım şartı, erişim engeli ve hukuki riskler nedeniyle yasak kapsamındadır.

### 4.3 Aries için önerilen İş & Kariyer özellikleri
1. İlan şeffaflık kartı: kurum, kategori (Kamu/Kura/KPSS/KPSS'siz/Büyük özel/Kamu bağlı şirket), resmî link, başvuru adımı, şartlar, son başvuru, yayın tarihi, collectedAt, firstSeenAt, tazelik ve doğrulama etiketi.
2. Başvuru panosu: Kaydedildi → Başvuruldu → Görüşme → Teklif → Red/Sessiz. Son tarih ve takip hatırlatması.
3. CV ana sürüm + ilana özel öneri: Her düzenleme ana CV'de gerçek bir kanıta bağlansın. Olmayan deneyim, eğitim veya sertifika uydurulmasın. CV dosyası mümkünse cihazda kalmalı; buluta giden içerik minimum ve açık izinli olmalı.
4. ATS kontrolü: okunabilirlik, bölüm başlıkları, anahtar kelime açığı, tarih tutarlılığı ve Türkçe karakter testi. Tek puan yerine bulgu/kanıt/belirsizlik.
5. Mülakat provası: ilan/pozisyon odaklı soru, sesli yanıt, STAR rubriği (Durum/Görev/Eylem/Sonuç), somut gelişim önerisi ve kullanıcının yerel geçmişi.
6. İlan eşleştirme hunisi: önce deterministik şehir/meslek/deneyim/son tarih filtreleri; sonra embedding benzerliği; en son LLM rubriği. Çıktıda neden eşleşti, eksik şart, belirsizlik ve CV kanıtı göster.
7. Piyasa nabzı: İŞKUR resmî bülteni/haritasından meslek ve bölge eğilimi; veri dönemi ve kaynakla.
8. Riskli ilan işareti: para talebi, yalnız WhatsApp iletişimi, belirsiz şirket, kişisel bilgi isteyen sahte form. Bu özellik kesin suç isnadı değil, dikkat uyarısı üretir.

### 4.4 C1 için değişmez veri ve toplama kuralları
- İş sayılacak kayıtların deadline'ı kaynakta açıkça bulunmalı ve henüz geçmemiş olmalı.
- Alanlar ayrı tutulur: publishedAt (kaynak yayın tarihi yoksa null), deadline, collectedAt, firstSeenAt, varsa applicationOpensAt.
- Çekim tarihi, ilan yayın tarihi değildir. Duyuru başlığındaki sınav tarihi yayın tarihi değildir.
- Deadline geçmiş ilan güncel açık ilan listesine girmez. Gelecekte açılacak başvuru, henüz açık sayılmaz. Kura/sonuç/staj duyurusu açık iş sayılmaz.
- İŞKUR ilan detay sayfaları ve giriş gereken Kariyer Kapısı sayfaları kazınmaz; kullanıcı resmî sayfaya yönlendirilir.
- e-Şube, ilan.gov.tr, Resmî Gazete, kurum duyuruları ve robots izinli ATS kariyer sayfaları ölçülür. İzin belirsiz/401/robots denied ise hedef API çağrısı gönderilmez.
- UA: AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis). Tarayıcı taklidi yok.
- Kaynak bazlı son iyi veri. Bir kaynak düşerse yalnız o kaynak stale olur.
- Kamu ayrı raporlanır; kamu doğrulanmış ilanı 0 ise RED.
- Sabit kabul eşiği 10 kaynak /100 doğrulanmış açık ilan. Altındaysa gerçek sayı yazılır ve UI entegrasyonu başlamaz.
- Hata testleri: gerçek resmî ilan, offline, boş yanıt, 403/500, timeout, robots reddi, stale last-good, kaynak kaybı izolasyonu.
- Ağır toplama Actions'ta; Worker doğrulanmış JSON'u sunar. Android doğrudan büyük web sitelerini taramaz.

## 5. Film araştırması: katalog, takip ve izleme

### 5.1 Rakip ve altyapı
- **TMDB:** film/dizi/oyuncu/kadro/ekip, özet, görsel, fragman ve sezon/bölüm meta verisi. Geliştirici API'si ticari olmayan projelerde atıfla kullanılabilir; gelir amaçlı ticari kullanım için lisans/izin gerekir. TMDB logo/atıf şartları uygulanmalı.
- **TMDB Watch Providers / JustWatch:** ülke bazında abonelik, kiralama ve satın alma sağlayıcılarını verir. JustWatch atfı gereklidir. Provider verisini gerçek oynatma linki sanma: çoğu sonuç “nerede mevcut” bilgisidir, her platforma özel derin bağlantı sağlamaz. Türkiye TR bölgesi seçilmeli; sonuç yoksa uydurulmaz.
- **TVmaze:** dizi, sezon/bölüm ve yayın takvimi için tamamlayıcı kaynak olabilir; lisans/atıf şartı ayrıca doğrulanır.
- **Trakt:** OAuth 2.0; web/mobile için Authorization Code + PKCE, TV/cihaz için Device Code akışı. Scrobble (başla/duraklat/durdur), izleme geçmişi, watchlist, favoriler ve listeler. Token ve refresh-token ömrü/döndürme kuralları güncel API dokümanına göre uygulanır.
- **Letterboxd:** API erişimi talebe bağlı; veri analizi, öneri, LLM/GPT ve kişisel proje kullanımı için şu an erişim vermediğini belirtiyor. Kendi hesabının izinli CSV dışa aktarımı veya profil RSS'si alternatif olabilir; profil verisi kullanıcı onayıyla alınır.
- **Jellyfin Android:** resmî açık kaynak Android istemcisi; Media3'e geçiş, indirme yönetimi, bölüm/başlık atlama ve Android Auto gibi özellikler sunuyor.
- **Wholphin:** Jellyfin Android TV istemcisi; ExoPlayer ve MPV motorlarını seçme, altyazı/ASS, bölüm ve oynatma kontrolleri gibi fikirler. Lisansı GPL-2.0 nedeniyle kod kopyalamadan önce yükümlülükleri değerlendir.

### 5.2 Aries film envanteri ve boşluklar
- FilmDizi.kt içinde TMDB arama, trend/popüler listeler, TV bilgisi, sezon endpoint'i, fragman ve watch-provider veri modelleri bulunuyor.
- MediaSourceBrowser.kt içinde WebView, web arama, TVmaze/TMDB meta verisi, sezon/bölüm seçimi ve medya adayı tespit mantığı bulunuyor. Ancak bu, her kaynağın izinli olduğunu, her URL'nin oynatılabildiğini, DRM'nin desteklendiğini veya uygulamanın hukuka uygun içerik akışı sağladığını kanıtlamaz.
- Güvenli hedef: katalog + fragman + yayın platformu bulunabilirliği + kullanıcının kendi Jellyfin sunucusu gibi meşru medya kaynakları. Telifli akış kazıma, DRM kırma, korumalı yayını dışarı çıkarma yok.
- “İzle” butonu yalnızca yasal ve çalışır bağlantı varsa gösterilir. Kaynak adı, bağlantı türü, ülke, doğrulama ve son kontrol saati kullanıcıya açık olur.
- Kendi oynatıcısı yazılacaksa Android Media3/ExoPlayer ana tercih; codec/altyazı ihtiyacı doğrulanır. DRM'li servislerin resmî uygulamasına yönlendir.
- Fikirler: zevk profili, “bu akşam ne izlesem”, neden önerildi açıklaması, izleme geçmişi, Trakt sync, yaklaşan bölüm takvimi, sezon/bölüm ilerleme çubuğu, spoiler kontrolü, sağlayıcıya göre izleme listesi ve katalog değişiklik bildirimi.

## 6. Borsa araştırması: veri ve analiz

### 6.1 Rakipler ve resmî kaynaklar
- **Fintables:** standardize bilanço/gelir/nakit akış tabloları, oran analizi, Excel dışa aktarımı ve çok kriterli Hisse Radar. Evo adlı araştırma asistanının şirket, hisse, fon, takas/aracı kurum, KAP belgesi ve piyasa verileriyle çalıştığı Fintables'ın 18.06.2026 tarihli kendi tanıtımında açıklanıyor. Bunlar kendi ürün beyanıdır; veri lisansı, ücretsiz/ücretli kapsam ve sınırlar ayrıca doğrulanmalı.
- **KAP:** şirket/fon bildirim sorguları, finansal raporlar, beklenen bildirimler ve bildirim türleri. Ekranında detaylı sorgu ve dönem/şirket/konu filtreleri var. Bu sayfaların bulunması herkese açık ve kararlı bir API lisansı bulunduğu anlamına gelmez. Resmî API/şartlar yoksa web sayfasını izinsiz kazıma.
- **TEFAS:** fonları aynı platformda inceleme/karşılaştırma, dönem getirisi, fon büyüklüğü, yönetim ücreti, risk değeri, portföy dağılımı ve tarihsel veri. Kaynağın güncellenme tarihi ve fonun değerleme tarihi gösterilmeli.
- **TCMB EVDS:** makroekonomik seri/veri servisi. API kullanımında kullanıcı kaydı ve API anahtarı gerekebilir. API anahtarı telefonda veya LLM prompt'unda tutulmaz; Worker secret olarak saklanır.
- **Borsa İstanbul:** gerçek zamanlı, gecikmeli ve gün sonu verilerini lisanslı veri dağıtıcıları üzerinden yayımlar. Aries için fiyat, derinlik veya gerçek zamanlı veri göstermeden önce kullanım ve yeniden dağıtım lisansı doğrulanmalı.
- **borsapy (GitHub: saidsurucu/borsapy):** BIST, döviz, kripto, fon ve ekonomik veri için Python kütüphanesi. README, kişisel/eğitim kullanımını belirtip ticari kullanım için Borsa İstanbul'dan lisans alınması gerektiğini söylüyor. Repo kodu Apache-2.0 görünse bile veri kaynağı koşulu ayrı olabilir; ticari ürünün temel veri sağlayıcısı olarak doğrudan kullanma.
- **TradingAgents (TauricResearch):** piyasa/haber/temel analiz araştırmacıları, boğa/ayı tartışması, trader, risk yöneticisi ve portföy yöneticisi rol dağılımını gösteren LangGraph tabanlı açık kaynak araştırma çerçevesi. Tarihsel veri sızıntısı ve backtest düzeltmeleri changelog'da yer alıyor; bu tip ürünler otomatik olarak kârlı değildir.
- **OpenBB/OpenBQ:** OpenBB GitHub organizasyonu ticari operasyonları sonlandırma kararı bildirdi; 5 Kasım 2026 duyurusuna göre açık kaynak varlıklarının idaresi OpenBQ Foundation'a devrediliyor, Workspace kodu FINOS'a taşınıyor. Eski “OpenBB tüm ürünleri aktif ticari platform olarak geliştiriyor” varsayımını kullanma. Açık kaynak ODP/Workspace mimarisini öğrenme kaynağı olarak incele; sahiplik ve bakım durumunu her sürümde yeniden denetle.

### 6.2 Aries Borsa boşlukları
- Android Ai.kt içinde Finnhub tekil quote fonksiyonu var; veri gelmezse “yok” döndürebiliyor. Kapsamlı BIST izleme/finansal veri modülü değildir.
- Worker tarafında kur bilgisi fonksiyonu ve /film gibi endpoint'ler görünür; ama bu tek başına KAP/TEFAS/EVDS normalize piyasa katmanı değildir.
- Doğrulanmış ve lisanslı BIST quote/ohlcv sağlayıcısı, KAP disclosure adapter, TEFAS fund adapter, EVDS macro adapter, normalize finansal tablo modeli ve tazelik/eksik veri alanları henüz tam kanıtlanmış değil.
- Her piyasa verisi en az şu alanları taşımalı: source, instrument, currency, value, unit, asOf, fetchedAt, delay, licenseClass, confidence, isStale.
- Finansal tabloların dönem, para birimi, konsolidasyon ve muhasebe standardı normalize edilmeden şirketler arası oran karşılaştırması üretilmez.
- Kullanıcıya kaynaklı bilgi, finansal rapor özeti ve varsayım analizi sun; kişiye özel “al/sat/tut” tavsiyesi veya otomatik emir verme. Yatırım danışmanlığı/portföy yöneticiliği gibi düzenlenmiş hizmetlerin sınırları için SPK mevzuatı ve hukukçu görüşü gerekir. “Yatırım tavsiyesi değildir” ibaresi tek başına lisans yükümlülüğünü ortadan kaldırmaz.
- Paper trading dahi gerçek emir değildir; varsayımlar, işlem maliyeti, kayma ve tarihsel veri sızıntısı ayrıca gösterilmeli.

### 6.3 Borsa için güvenli ilk özellikler
1. KAP bildirim akışı ve önem türleri: finansal etki, operasyon, yönetim kararı, risk. Her özet resmî bildirim linkine dayanır.
2. Manuel izleme listesi ve alarm eşikleri; emir iletimi yok.
3. TEFAS fon karşılaştırması: getiri dönemleri, fon türü, risk, ücret ve portföy dağılımı; fiyat tarihi ve karşılaştırma ölçütü görünür.
4. TCMB EVDS makro göstergeleri: politika faizi, enflasyon ve döviz; kaynak/seri kodu ve veri tarihiyle.
5. KAP PDF/Excel belge özeti; modelin özetlediği pasajların belge sayfası ve kaynağı gösterilir.
6. Günlük/haftalık brifing: BIST endeksi, takip edilen hisseler, KAP, fonlar ve ekonomik takvim; her veri tarih/saat ve lisans etiketi taşır.
7. Çok perspektifli rapor ve karar günlüğü ancak veri katmanı sağlamlaştıktan sonra.

## 7. Aries'i ayrı uygulamalar ve web siteleri olarak değerlendirme

Aries tek Android uygulaması olarak kalmak zorunda değil. Ürün mantığı ile arayüzleri ayır:
- **Aries Asistan:** ses, sohbet, cihaz komutları, hafıza, dosyalar, izinler, bildirimler, görev/hatırlatma.
- **Aries İş & Kariyer:** bağımsız iş arama/başvuru panosu, CV, mülakat ve resmî ilan doğrulama.
- **Aries Film:** bağımsız katalog, sezon/bölüm, watchlist, platform bulunabilirliği ve Jellyfin bağlantısı.
- **Aries Borsa:** bağımsız finansal izleme, KAP/fon/makro araştırma ekranları.
- **Aries Web:** telefondaki işlevleri masaüstünde sunan responsive web/PWA paneli. Bu ayrı bir frontend'dir; Worker API tek başına web uygulaması değildir.

Ortak backend katmanı kimlik doğrulama, veri saklama, rate limit, kaynak adaptörleri, bildirim ve model/araç kayıt defterini sağlar. Her modülün kendi veri şeması, testleri ve izinleri olur. Bir modül arızalanınca diğerlerinin sekmesi/verisi karışmaz. Mobil ve web aynı API sözleşmesini kullanır. MCP, araçların farklı istemcilerce kullanılabilmesi için opsiyonel standart katman olur; uygulama API'lerinin yerine geçmez.

Web/PWA için:
- aynı Worker API'lerini kullan, Android koduna bağlı kalma;
- kullanıcı hesabı, oturum ve yetki kapsamını açık tasarla;
- her modülde bağımsız arama, filtre, detay ve geçmiş;
- dış resmî kaynakları yeni sekme/Custom Tabs ile aç;
- web'de erişilebilirlik, klavye, mobil ekran ve yavaş internet senaryolarını test et;
- kişisel veriyi modüller arasında varsayılan paylaşma; kullanıcı ayarı ve açık onay gerekir.

## 8. Bilmediğimiz veya henüz eklenmemiş olabilecek özellikler

Bunlar ürün fikirleridir; kodda var kabul edilmez:
- Skill marketi/yerel beceriler: Markdown yönerge, şema, izin ve test örneği bulunan yeniden kullanılabilir görevler.
- Araç arama: çok fazla aracı prompt'a yüklememek; niyete göre uygun araçları bulmak.
- Kullanıcı tanımlı zamanlanmış görev ve koşullu izleme: “her sabah resmî ilanları kontrol et”, “KAP'ta takip ettiğim şirkete bildirim gelirse haber ver” gibi. Kaynak ve frekans limiti olmalı.
- Sohbet kuyruğu: cevap hazırlanırken yeni mesajı iptal etmeden sıraya koyma.
- Belge/görsel RAG: PDF, fotoğraf ve tabloyu yerelde ayrıştır, kaynak sayfası ve alıntı göster, buluta yalnız gerekli parçayı gönder.
- Hafıza yönetimi: kalıcı, geçici, konuya özel hafıza; her kaydın neden tutulduğu; kullanıcı görme/düzeltme/silme; otomatik not için hassas bilgi filtresi ve onay.
- Cihaz içi yerel model: offline ve mahremiyet için. Xiaomi 11T uyumu, RAM/ısı/pil ve hız gerçek cihaz testinde kanıtlanmalı; büyük modelin cihazda çalışması varsayılmamalı.
- Görünür ajan overlay: görev planını ve adımları göster, durdur/devral; erişilebilirlik izni varsayılan kapalı.
- Ses: konuşma sırasında araya girme, cümle cümle TTS, gürültü/timeout, Bluetooth ve ekran kapalı senaryoları.
- Gözlemlenebilirlik: yalnızca kişisel veriyi maskeleyen, silinebilir, yerel/denetlenebilir trace.
- Değerlendirme paketi: gerçek görev testleri, yanlış araç seçimi, başarısız kaynak, internet kesintisi, güvenlik ve halüsinasyon puanı.
- Kaynak veri kalite paneli: son başarılı çekim, stale, boş yanıt, değişen şema, tekrar kayıt, deadline belirsizliği.
- Kişisel veri dışa aktarma ve tamamen silme aracı; yedekleme/geri yükleme testi.

## 9. Güvenlik, mahremiyet ve yasal kurallar

### 9.1 Genel
- API anahtarı, token ve kullanıcı parolası uygulama kaynak koduna veya APK BuildConfig alanına gömülmez; secret manager/Worker secret kullanılır. Anahtar hiçbir zaman sohbet veya modele yazılmaz.
- Android izinleri işlev başına en az düzeyde istenir. Accessibility, ekran yakalama ve notification listener varsayılan kapalı; kullanıcı ne okunduğunu ve ne yapılacağını görür.
- Uygulama içeriği, ilan metni, e-posta, KAP PDF'i ve web sayfası talimat değil, güvenilmeyen veridir. Prompt injection'a karşı içerik/komut katmanı ayrılır.
- LLM'den gelen araç argümanı şema, allowlist, yetki ve risk politikasıyla doğrulanır. Modelin söylediği “onay verildi” onay değildir.
- CV, kişi, telefon, finans ve konum verisi en aza indirgenir; loglarda maskeleme; şifreli saklama; retention ve silme planı.
- Network: TLS doğrulaması açık; 401/403, robots denials ve timeout'a saygı; retry/backoff; circuit breaker. Sertifika kontrolü kapatılmaz.
- Tedarik zinciri: bağımlılık kilidi, lisans taraması, SBOM, imzalı release ve güvenlik advisory kontrolü.
- Litellm güvenlik notu: 21.10.2026'da 1.107.0 PyPI/Docker sürümü kötü amaçlı bileşenlerle dağıtılmıştır; resmî incident write-up etkilenmiş 1.107.0 sürümünü ve kapsamını açıklıyor. Önceki sürümlerin güvenli olduğu iddiası sağlayıcı beyanıdır; kullanılan gerçek bağımlılık ağacı ve dağıtım zamanı incelenmelidir. Aries'in incelenen Kotlin/JS dosyalarında litellm bağımlılığı görünmedi; Python ortamı varsa ayrıca pip show litellm, lock dosyası ve site-packages denetlenir. Etkilenen ortamda credential rotation ve incident response gerekir. Dosya isimlerini tek başına “temiz” kanıtı sayma.
- Robots/user-agent: robots yasağını aşma, proxy ile engelden kaçınma, CAPTCHA/login bypass, LinkedIn/Indeed scraping veya tarayıcı taklidi yok.
- User-Agent: AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis).

### 9.2 Film
Telifli içerik kaynağı kazıma, DRM aşma, ücretli platformun akış URL'sini çıkarma ve korsan kaynak yönlendirmesi yapılmaz. TMDB/JustWatch/TVmaze/Trakt atıf ve lisans şartları ekranda/uygulama hakkında bölümünde yerine getirilir. Katalog meta verisi ile oynatma hakkı birbirinden ayrıdır.

### 9.3 Borsa
BIST veri lisansı ve yeniden dağıtım hakkı doğrulanmadan gerçek zamanlı/veri ürünü olarak dağıtım yapılmaz. KAP, TEFAS ve EVDS kullanımı her birinin şartlarıyla değerlendirilir. Düzenlenmiş yatırım hizmeti sunulmaz; emir iletimi ve kişisel al-sat direktifi yoktur. Yatırım ürünü kamuya açılacaksa SPK uzmanı/hukukçu değerlendirmesi alınır.

### 9.4 İş & Kariyer
İlan kaynağı ve başvuru adresi resmî olmalı. Otomatik başvuru gönderme, işveren/iş arayan hesabına giriş, e-Devlet/CV/KPSS hassas veri toplama yok. CV düzenlemesi gerçek deneyim ve kanıta bağlı kalır.

## 10. Önerilen teknik mimari

### 10.1 Mobil istemci (Kotlin)
- UI: Compose/Activity ekranları, ViewModel, repository; MainActivity'yi modül ve sorumluluklara böl.
- Router: deterministik intent → skill → agent.
- Voice: STT / VAD / intent / LLM stream / chunk TTS / interrupt ayrı katmanlar.
- Tool registry: şema, permission, approval, timeout, retry, idempotency.
- Local storage: Room/Datastore; key/secret Android Keystore. Kullanıcıya export/delete.
- Media: Media3; playback provider metadata adapter'dan ayrılır.
- Network: ortak OkHttp/Retrofit client, timeout, connection pool, retry policy, cache.

### 10.2 Worker/API
- Her iş alanı modülüne ayrı route/handler ve typed schema.
- D1 veri tabloları sürümlü migrations ile yönetilir.
- Heavy jobs GitHub Actions veya uygun zamanlayıcıda; Worker ham/işlenmiş JSON'u yetkiyle sunar.
- Cache ve source freshness kaynağa göre; son iyi veri ayrı.
- İsteğe bağlı MCP Streamable HTTP endpoint'i; MCP protokolünü ve SDK sürümünü sabitle.
- Auth, CORS, rate-limit, secret management ve route başına authorization testleri.
- Long-running task state gerekiyorsa Durable Objects/queue gibi uygun altyapı değerlendirilir; Workers'ın süre/CPU/request sınırı ölçülmeden ağır işi request içinde çalıştırma.

### 10.3 Web/PWA
- Ayrı web istemcisi, ortak API.
- Modül sınırları ve veri şemaları korunur.
- Her araç ve içerik aynı backend yetki modelinden geçer.
- Web UI, ChatGPT Apps SDK ve Android üç ayrı istemci olabilir; aynı servis sözleşmesini kullanırlar.

## 11. Öncelik sırası

**Aktif geliştirme:** yalnızca kullanıcının yürürlükteki HARITA.md / NOT_C1.md dosyalarında DEVAM işaretli İş&Kariyer C1 + Haber doğrulama görevi. Aşağıdaki diğer alanlar araştırma backlog'udur.

### P0 — temel güvenilirlik
1. durum dalının Gradle bağımlılık/buildFeatures eksikliğini ve branch/workflow farkını düzeltme planı çıkar; önce dosya/diff/test kanıtı, sonra ayrı PR.
2. Gerçek Android CI: durum veya seçilen aktif dal için derleme + lint + unit test + instrumentation smoke; testleri küçültme yok.
3. Browser UA taklidini ve izinsiz HTML aramasını kaldır; resmi, kullanım şartlarına uygun provider adaptörlerine geç.
4. Worker route auth, privacy, CORS, quota, log mask, D1 migrations ve error contract denetimi.
5. C1 current-source threshold ve production authenticated GET kapıları sağlanmadan UI entegrasyonu yok.

### P1 — asistan hız mimarisi (C1 sonrası)
6. Üç kademeli router.
7. Skill registry + JSON schema tool registry.
8. Streaming text + sentence-level TTS + interrupt/cancel.
9. Trace ve tool-turn/token/time bütçesi; stuck detection.
10. User-facing approval queue ve per-app/feature scopes.

### P2 — ortak platform
11. Kullanıcı tanımlı Scheduled tasks ve event monitoring.
12. Hafıza konsolidasyonu + kaynak/kanıt gösterimi + temporary chats.
13. Dosya/görsel RAG; sayfa/alıntı kaynak gösterimi.
14. MCP salt-okunur endpoint, Apps SDK demo ve web/PWA istemcisi.
15. Erişilebilir overlay, yalnızca ayrı onay ve cihaz testiyle.

### P3 — ürün modülleri
16. İş & Kariyer: C1 canlı feed tamamlanması, doğrulanmış kart UI, başvuru panosu, CV kanıt bağlantısı ve mülakat.
17. Film: watchlist/Trakt, JustWatch atıflı TR sağlayıcılar, meşru playback/Jellyfin.
18. Borsa: lisanslı veri, KAP/TEFAS/EVDS adapter ve source/timestamp katmanı. Bu iş ancak açık kullanıcı talimatı ve mevzuat kontrolüyle açılır.

## 12. Her geliştirme adımında zorunlu test seti

Genel:
- başarılı gerçek iş akışı;
- internet yok, yavaş internet, DNS/TLS hatası, timeout;
- HTTP 400/401/403/404/429/500/502/503/504;
- boş/bozuk JSON, şema değişikliği, eksik alan;
- duplicate ve idempotency;
- eski cache ve kaynağa özel stale isolation;
- kullanıcı iptali, uygulama arka plan/ön plan, süreç yeniden başlatma;
- kullanıcı onayı verilmediği hâlde yan etkili işlemin çalışmaması;
- prompt injection içeren dış metin;
- loglarda secret ve hassas veri bulunmaması;
- küçük ekran/Android 13 Xiaomi gerçek cihaz, pil ve bellek.

İş & Kariyer:
- gerçek resmî ilan; son tarih açık; son tarih geçmiş; tarih yok; başvuru gelecekte açılıyor;
- Kura/sonuç/staj iş sayısına girmiyor;
- robots deny/unknown ve 401'de hedef URL'ye ek istek yok;
- kaynak düşerse diğer kaynakların last-good verisi korunuyor.

Film:
- film ve dizi, sezon 0/specials, eksik bölüm, dil/ülke/metadata bulunmuyor;
- TR provider var/yok; JustWatch atfı;
- yalnız resmi/legal link açılıyor; DRM bypass veya stream URL çıkarma yok;
- Jellyfin yok/erişilemiyor, altyazı, codec ve seek hataları.

Borsa:
- stale fiyat, gecikmeli veri, piyasa kapalı, bölünme/temettü, currency/unit, eksik finansal dönem;
- KAP bildirim tarihi/period mismatch;
- TEFAS fiyat tarihi ve fon dağılımı snapshot;
- lisanssız kaynaktan veri dağıtımı kapalı;
- LLM'den gelen uydurma sayısal değer otomatik reddediliyor.

## 13. Değerlendirme metrikleri

Her değişiklikte önce/sonra ölç:
- p50/p95 ilk metin yanıtı gecikmesi ve tamamlama süresi;
- görev başına model çağrısı / tool çağrısı / token;
- doğru intent ve tool seçimi;
- tool success/failure ve yeniden deneme oranı;
- kaynak başına fresh/stale/empty/forbidden;
- deadline doğrulama doğruluğu ve duplicate oranı;
- sesli kullanımda STT gecikmesi, ilk TTS sesi, kesme gecikmesi;
- crash-free session, ANR, bellek ve pil;
- kullanıcı tarafından düzeltilen/geri çevrilen yanıt oranı.

Model benchmark'ı yalnızca genel cevap kalitesiyle ölçme. Aries'in yerel komut ve domain görevlerinden sabit, sürümlü bir eval veri kümesi üret.

## 14. Sonuç raporu standardı

Her işlem şu biçimde raporlanır:

**KARAR:** KABUL / RED / BEKLEME  
**Kanıt:** HEAD SHA, dosya listesi, çalışan komut/test çıktısı, kaynak başına ilan sayısı gerekiyorsa, CI linki, yeniden sayım.  
**Eksik:** test edilmemiş alan, erişilemeyen repo, kaynak izni, secret/config engeli, kullanıcı onayı.  
**Telefonda denenecek:** gerçek adımlar; kullanıcıdan token veya Termux komutu isteme.  
**SONRAKİ ADIM:** tek, net ve mevcut kapsamla uyumlu iş.

# KAYNAKLAR — 25.11.2026 tarihinde kontrol edildi

## Aries iç kaynakları
- Jarvis durum HARITA: https://github.com/aries919-bot/Jarvis/blob/durum/docs/HARITA.md
- Jarvis durum NOT_C1: https://github.com/aries919-bot/Jarvis/blob/durum/docs/NOT_C1.md
- Son kaydedilmiş C1 Hakem raporu: https://github.com/aries919-bot/Jarvis/blob/durum/qa-reports/c1/latest.json
- Exact-head C1 Actions #45517626605: https://github.com/aries919-bot/Jarvis/actions/runs/45517626605
- C1 H2 entegrasyon PR #9: https://github.com/aries919-bot/jarvis-cloud/pull/9

## Asistan / ajan mimarisi / protokol
- OpenAI Apps SDK: https://help.openai.com/en/articles/12515353-building-with-the-apps-sdk
- MCP 2026-07-28 specification release: https://blog.modelcontextprotocol.io/posts/2026-07-28/
- MCP TS SDK migration notes: https://ts.sdk.modelcontextprotocol.io/v2/migration/support-2026-07-28
- Open WebUI features: https://docs.openwebui.com/features/
- Open WebUI MCP: https://docs.openwebui.com/features/extensibility/mcp/
- Open WebUI tool ecosystem: https://docs.openwebui.com/getting-started/essentials/
- Home Assistant Assist pipeline: https://contributors.home-assistant.io/core/internals/assist-pipelines/
- Gemini Live help: https://support.google.com/gemini/answer/15274899
- ChatGPT scheduled tasks: https://help.openai.com/en/articles/10291617-scheduled-tasks
- PokeClaw: https://github.com/agents-io/PokeClaw
- CellClaw: https://github.com/frido22/cellclaw
- ClawDroid: https://github.com/shotechs/clawdroid
- ClosePaw: https://github.com/imoonkey/closepaw

## İş & Kariyer
- Kariyer.net AI Mülakat Provası: https://www.kariyer.net/mulakat-provasi
- Kariyer.net yardım/maaş verisinin nasıl üretildiği: https://www.kariyer.net/yardim/
- İŞKUR Açık İş Haritası: https://acikisharita.iskur.gov.tr/
- Teal: https://join.tealhq.com/
- Teal Job Search Companion: https://help.tealhq.com/en/articles/9524956-teal-job-search-companion-bookmark-jobs
- Huntr tracker: https://huntr.co/product/job-tracker
- Huntr özellikleri: https://huntr.co/
- Simplify: https://simplify.jobs/
- Simplify FAQ: https://simplify.jobs/faq
- Jobscan tutorial: https://www.jobscan.co/jobscan-tutorial

## Film
- TMDB API FAQ / attribution: https://developer.themoviedb.org/docs/faq
- TMDB watch provider endpoint index: https://developer.themoviedb.org/reference
- Trakt Developer API: https://developer.trakt.tv/
- Trakt OAuth / Device Code: https://developer.trakt.tv/docs/authentication-oauth
- Letterboxd API access limitations: https://letterboxd.com/api-beta/access/
- Jellyfin Android: https://github.com/jellyfin/jellyfin-android
- Jellyfin Android releases: https://github.com/jellyfin/jellyfin-android/releases
- Wholphin: https://github.com/bcanfield/wholphin

## Borsa
- Fintables: https://fintables.com/
- Fintables Evo tanıtımı: https://www.youtube.com/watch?v=yIuBFUY-Pvc
- KAP bildirim sorgusu: https://kap.org.tr/tr/bildirim-sorgu
- KAP beklenen bildirim sorgusu: https://kap.org.tr/tr/beklenen-bildirim-sorgu
- TEFAS resmi finansal okuryazarlık tanımı: https://finansalokuryazarlik.gov.tr/i/tefas
- TCMB EVDS Excel/web service kılavuzu: https://evds3.tcmb.gov.tr/igmevdsms-dis/documents/showDocument?docId=3
- Borsa İstanbul veri yayını: https://www.borsaistanbul.com/veriler/veri-yayini
- Borsa İstanbul lisanslı veri dağıtıcı dizini: https://www.borsaistanbul.com/en/data/data-dissemination/data-vendors-directory
- borsapy: https://github.com/saidsurucu/borsapy
- TradingAgents: https://github.com/TauricResearch/TradingAgents
- OpenBB GitHub / wind-down durumu: https://github.com/OpenBB-finance
- OpenBQ geçiş duyurusu: https://openbq.org/news/a-new-home-for-openbb-projects.html
- SPK güncel Yatırım Hizmetleri rehberi: https://spk.gov.tr/data/61e496a11b41c60d1404d6b2/Yat%C4%B1r%C4%B1m%20Hizmet%20ve%20Faaliyetleri%20ile%20Yat%C4%B1r%C4%B1m%20Kurulu%C5%9Flar%C4%B1na%20%C4%B0li%C5%9Fkin%20Rehber%20%28Versiyon%2C%20i-SPK.37.9%29.pdf

## Güvenlik
- LiteLLM 21.10.2026 incident: https://docs.litellm.ai/blog/security-incident-oct-21-2026
- LiteLLM root-cause investigation: https://docs.litellm.ai/blog/security-incident-oct-21-2026-attack-chain
- Microsoft analysis: https://www.microsoft.com/en-us/security/blog/2026/10/23/inside-the-litellm-1-107-0-supply-chain-compromise-credential-theft-through-python-startup-hooks/

# NOT SONU

**ChatGPT'ye uygulama talimatı:** Önce repo envanterini ve aktif C1 görev durumunu kanıtla. Bu nottaki rakip özelliklerini Aries'te zaten varmış gibi gösterme. Güncel fiyat, özellik, lisans, API veya mevzuat bilgisi gerekiyorsa kaynağından tekrar kontrol et. Kaynakların erişilemediği yerde bunu açıkça yaz. Kullanıcıdan “başla” gelmeden yeni modül geliştirmeye geçme; “devam et” denince mevcut HARITA.md ve NOT_C1.md sırasını izle.
