# JARVIS Gizlilik ve Veri Akışı Analizi
TASLAK: Tam kod incelemesi yapılmadı. Bu rapor yalnızca belirtilen satırlara dayanır ve gizlilik kararı için tek başına yeterli değildir.
**Durum:** Aşama 1 – Yalnızca analiz.  
**Kod değişikliği:** Yapılmadı.  
**Deploy:** Yapılmadı.  
**Kapsam:** Android `Ai.kt`, `MainActivity.kt`, `Store.kt`; Worker `src/index.js`, `src/memory.js`, `wrangler.toml`.

**Güvenlik:** Gizli yapılandırma değerleri, hesap kimlikleri, tokenlar, API anahtarları, veritabanı adı ve veritabanı kimliği bu raporda yer almaz. Kısmi karakterleri veya uzunlukları da paylaşılmaz.

**Kanıt sınırı:** Bu rapor, bu sohbette kullanıcı tarafından aktarılan kod incelemesi bulgularını ve satır referanslarını kaydeder. Asistan kaynak dosyaları bu oturumda bağımsız olarak açıp doğrulamamıştır. Bir satır numarasının bildirilmiş olması, çevresindeki tüm çağrı zincirinin incelendiği anlamına gelmez.

## 1. Kod incelemesinden aktarılan bulgular

Bu bölümdeki maddeler, bu oturumda paylaşılan kod satırı referanslarına ve inceleme sonuçlarına dayanır. Okunmadığı belirtilen dosya bölümleri için kesin hüküm verilmez: `index.js` 1–8, 16–44, 60–80 ve 100–130 arası bölümler ile `MainActivity.kt` dosyasının tamamı. Ayrıca `wrangler.toml` dosyasının tamamı ve Cloudflare hesabı/panel durumu bu rapor kapsamında bağımsız olarak doğrulanmamıştır.

Her madde, kanıtın kapsamını açıkça belirten bir etiket taşır.

### Android tarafı

- **[Kod incelemesinden aktarıldı – bağımsız doğrulama yapılmadı]** Sohbet geçmişinin `Store.kt` içinde `SharedPreferences` (`convs`) ile tutulduğu; `histOn` ayarının bu kaydı kontrol ettiği bildirildi.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `MainActivity.kt:1395` satırında `hist` listesinin mevcut konuşmanın son 20 mesajından oluşturulduğu ve bu satırda `histOn` kontrolü bulunmadığı bildirildi. Bu, çağrı zincirinin başka bir yerinde filtreleme bulunmadığını tek başına kanıtlamaz.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `MainActivity.kt:1400` satırında `Ai.chat(listOf("bulut") + providerOrder(), ...)` çağrısının bulunduğu bildirildi.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `providerOrder()` için `MainActivity.kt:1385` satırında `Ai.ALL` döndürüldüğü bildirildi. Sağlayıcı ayarlarının tüm akışta nasıl kullanıldığı, `MainActivity.kt` dosyasının tamamı incelenmeden kesinleştirilmez.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `Ai.chat` içinde anahtarı boş sağlayıcıların atlandığı ve hata durumunda sonraki sağlayıcıya geçildiği bildirildi. Her sağlayıcıya fiilen hangi içeriğin gönderildiği, istek gövdeleri ayrıca incelenmeden kesinleştirilmez.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `maskele()` fonksiyonunun `Ai.kt:210` civarında bulunduğu ve `hizli()` içinde `Ai.kt:221` satırında çağrıldığı bildirildi. Bunun diğer veri yollarını kapsadığı sonucuna varılmaz.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `MainActivity.kt:1313` civarında `hatırla:` komutunun `facts` alanına yazdığı bildirildi. Tüm kullanıcı onayı ve silme akışı dosyanın tamamı görülmeden kesinleştirilmez.
- **[Kod incelemesinden aktarıldı – bağımsız doğrulama yapılmadı]** `facts` içeriğinin 1500 karakterde kesildiği bildirildi.

### Worker tarafı

- **[Yapılandırma varlığı aktarıldı – kaynak dosya burada bağımsız incelenmedi]** `wrangler.toml` içinde D1 veritabanı bağlantısı mevcut. Veritabanı adı ve kimliği raporlanmaz. Bu yapılandırma tek başına Worker kodunun D1'e veri yazdığını kanıtlamaz.
- **[Yapılandırma varlığı aktarıldı – kaynak dosya burada bağımsız incelenmedi]** `wrangler.toml` içinde cron yapılandırması bulunduğu bildirildi. Zamanlanmış görevin yaptığı işlem, ilgili kaynak kod bütünüyle görülmeden kesinleştirilmez.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `messages` tablosundaki kayıtların `scheduled` işlevinde 30 gün sonra silindiği (`index.js`, satır 9–12) bildirildi. Belirtilen dosya bölümlerinin bir kısmı bu oturumda okunmadığından, bu bulgu aktarılan inceleme sonucudur.
- **[Kod incelemesinden aktarıldı – bağımsız doğrulama yapılmadı]** Her istekte son 12 mesajın modele gönderildiği (`HISTORY_LIMIT`, satır 6) bildirildi.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `hafiza` tablosunun `memory.js` içinde kullanıldığı; `hatırla:` ve `unut:` komutlarının `index.js` satır 85–88 civarında yazma/silme yaptığı bildirildi. Onay akışının bütünü görülmeden kapsamlı bir hüküm verilmez.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `logHata` fonksiyonunun `q` ve `a` alanlarını `maskText` ile `hatalar` tablosuna yazdığı (`index.js`, satır 471) bildirildi. Saklama süresinin kaynak kodda bulunmadığı aktarılmıştır; tüm temizleme yolları incelenmeden süresiz saklandığı kesinleştirilmez.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `tavily_onbellek` kayıtlarının 6 saat önbelleklendiği ve `tavily_sayac` tablosunun günlük sayaç tuttuğu (satır 473–496) bildirildi.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `ask` fonksiyonunun (satır 133–200) şu sırayı kullandığı bildirildi: Workers AI → Groq → Gemini → OpenRouter → Cohere. Anahtarı boş olanın atlandığı ve hata durumunda sonraki sağlayıcının denendiği aktarılmıştır. Tam istek gövdeleri incelenmeden her sağlayıcıya gönderilen verinin aynı olduğu kesinleştirilmez.
- **[Kod incelemesinden aktarıldı – bağımsız doğrulama yapılmadı]** `ask` içinde `maskMessages` uygulandığı bildirildi. Maskelemenin kapsamı ve tüm giriş yollarına uygulanıp uygulanmadığı ayrıca doğrulanmalıdır.
- **[Satır referansı aktarıldı – kapsam sınırlı]** Yönlendirme aşamasında `prev` değerinin maskelenerek modele gönderildiği; üretilen `sq` değerinin Brave, DuckDuckGo ve Tavily aramalarında kullanıldığı (satır 293–294, 300, 303, 487–507) bildirildi. `pickBest` çıktısının sonraki kullanım zinciri tam kod üzerinden doğrulanmadıkça kesin kabul edilmez.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `maskText` fonksiyonunun (satır 441) kart, IBAN, e-posta, telefon, T.C. kimlik numarası ve anahtar benzeri değerleri kapsadığı; ad, adres ve tarihleri kapsamadığı bildirildi.
- **[Satır referansı aktarıldı – kapsam sınırlı]** `sq` değerinin console'a yazıldığı (satır 295) bildirildi.
- **[Kod incelemesinden aktarıldı – grep sonucu bildirildi]** Sağlayıcı yanıtlarının console'a yazılmadığı bildirildi. Bu bulgu yalnızca incelenen loglama noktalarının kapsamı kadardır.
- **[Satır referansları aktarıldı – kapsam sınırlı]** Bazı hata noktalarında `e.message` değerinin console'a yazıldığı bildirildi. Bu mesajların hangi durumlarda kullanıcı girdisi veya hassas bilgi içerebildiği doğrulanmamıştır.

## 2. Doğrulanamayan noktalar

- Cloudflare Workers AI, Groq, Gemini, OpenRouter, Cohere, DuckDuckGo, Brave ve Tavily hizmetlerinin veri saklama, model eğitiminde kullanım ve silme politikaları.
- Cloudflare loglarının saklama süresi ve kimlerin erişebildiği.
- Cloudflare hesabında hangi secret değerlerinin ayarlı olduğu.
- Android (`Ai.kt`) ile Worker arasındaki birleşik veri akışının eksiksiz kapsamı.
- `e.message` içeriğinin hangi durumlarda kullanıcı girdisi veya hassas bilgi içerebildiği.
- Her isteğe fiilen hangi sağlayıcının yanıt verdiği ve sağlayıcı yanıtının `sq`/arama sorgusu oluşturulmasında nasıl kullanıldığı.
- D1 yapılandırmasının mevcut olmasının ötesinde, okunmamış kod bölümlerinde hangi verilerin D1'e yazıldığı ve hangi tabloların hangi yollarla kullanıldığı.
- Cron yapılandırmasının kaynak kodda hangi görevle eşleştiği; ilgili işleyici ve veri işlemleri tam kaynak kod üzerinden ayrıca doğrulanmalıdır.

## 3. Bekleyen kararlar – Henüz uygulanmadı

1. Hata kayıtları için saklama süresi ve otomatik silme.
2. Bulut sağlayıcılarına otomatik geçişte kullanıcı onayı.
3. Maskelemenin ad, adres ve tarih alanlarını kapsaması.
4. Hafıza kayıtlarının kullanıcı kontrolünde olması ve buluta gönderilmeden önce izin alınması.
5. `sq` loglamasının kaldırılması veya yalnızca sayaç ve durum bilgilerinin yazılması.
6. Hata loglarında `e.message` yerine sabit hata kodlarının kullanılması ve bu yaklaşımın yeterliliğinin kod incelemesi ve uygun testlerle değerlendirilmesi.

## 4. Loglama değerlendirmesi

- `sq` değerinin console'a yazıldığı satır 295 için loglama yapıldığı bildirilmiştir.
- Sağlayıcı yanıtlarının console'a yazılmadığı, grep incelemesinin sonucu olarak aktarılmıştır.
- Bazı hata noktalarında `e.message` loglanmaktadır. Bunun her durumda kullanıcı girdisi içerdiği kanıtlanmamıştır; bazı durumlarda içerebilmesi ihtimali de dışlanmamıştır.
- Cloudflare loglarının saklama süresi ve erişim yetkileri kaynak koddan belirlenemez.

**Kodda loglama yapılması, bu logların kimler tarafından görülebildiğini veya ne kadar süre saklandığını tek başına kanıtlamaz.**

## 5. Aşama sınırları

- Bu rapor mevcut davranışı belgelemek ve sonraki teknik değerlendirmeye girdi sağlamak içindir.
- Aşama 2'ye geçmeden önce rapor ve bekleyen kararlar ayrıca değerlendirilip onaylanmalıdır.
- Bu aşamada kod değişikliği, deploy, commit, push, test veya gerçek servis çağrısı yapılmaz.
- Gizli yapılandırma değerleri rapora eklenmez.
- Geçmiş commit içerikleri taranmaz; yalnızca mevcut dosyalar incelenir.

**Son durum:** Aşama 1 analiz raporu. Aşama 2 onayı verilmemiştir.
