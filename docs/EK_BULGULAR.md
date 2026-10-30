# Ek Bulgular (Kod okumaları)

Kapsam: Bu oturumda okunan satırlar. Tam kod incelemesi değildir.

## Telegram yolları (index.js)
- /tg (POST): Telegram webhook'u X-Telegram-Bot-Api-Secret-Token ile doğrulanıyor. Serbest metin serbest() üzerinden model sağlayıcılarına gidiyor. [Okundu]
- /duzelt: Dosya adı çıkarılıyor, GitHub API ile depodaki kaynak dosyası okunuyor, kod metni ask ile groq, gemini, workers-ai ve openrouter'a gönderiliyor. [Okundu, kapsam sınırlı]
- /durum ve /rapor: JARVIS_TOKEN ile korunuyor. Son hata kayıtlarının ilk 80 karakteri dönüyor. [Okundu]
- /hizli (POST): Authorization kontrolü bu okumalarda görülmedi. [Doğrulanamadı]
- /chat (POST): JARVIS_TOKEN kontrolü var. [Okundu]

## Android giriş akışı (MainActivity.kt)
- Satır 1245-1265: handle() sesli ve yazılı girişin ortak yolu. Mesaj store.addMsg ile kaydediliyor; histOn bu çağrıyı engellemez, yalnızca diske yazmayı kapatır. [Okundu]
- isOnline() ve hasAiKey() doğruysa askAi çağrılır, değilse searchFacts ile yerel arama. [Okundu]
- hasAiKey(), bulut veya Ai.ALL anahtarlarından birinin dolu olmasını ister. [Okundu]

## Açık noktalar
- /hizli (POST): index.js satır 34-35'teki JARVIS_TOKEN kontrolünden sonra çalışıyor. [Okundu, kapsam: grep].
- Sağlayıcıların veri saklama politikaları.
- Cloudflare secret ve log ayarları.
## 8. Cloudflare ve ortam değişkenleri (özet, değer içermez)

- Gözlemlenebilirlik paneli: Günlükler, İzler ve Sorunlar kapalı. İhracat hedefi yok. Canlı tail ve eski kayıtlar ayrıca doğrulanmadı. [Okundu: panel ekranı]
- Worker kodu 18 ortam değişkeni adı kullanıyor (AI, AI_MODEL, COHERE_KEY, COHERE_MODEL, DB, GEMINI_KEY, GEMINI_MODEL, GITHUB_OKUMA, GROQ_KEY, GROQ_MODEL, JARVIS_TOKEN, OPENROUTER_KEY, OPENROUTER_MODEL, TAVILY_KEY, TELEGRAM_CHAT, TELEGRAM_GIZLI, TELEGRAM_TOKEN). Bu adlar kodda `env.` ile geçiyor. [Okundu: grep]
- Cloudflare'de 9 gizli değişken tanımlı. Hangi adın hangi değişkenle eşleştiği, değerler ve tam liste bu raporda yazılmadı; panelde karşılaştırma gerekiyor. [Doğrulanamadı: tam eşleşme]
- Cloudflare veri kullanımı, Workers AI politikası ve Brave Search API gizlilik sayfası ayrıca doğrulanmadı. [Doğrulanamadı]

## 9. Sağlayıcı gizlilik politikaları (sohbette okunan sayfalar)

- Gemini ücretsiz katman: girdi ve çıktılar ürün iyileştirme için kullanılır; insan incelemesi olabilir. Ücretli katmanda kullanılmaz. [Okundu: Ek Hizmet Şartları, 23 Mart 2026]
- Grounding ile Google Arama: girdi ve çıktılar 30 gün saklanır. [Okundu]
- Groq: API istekleri için eğitim kullanımı açıkça yazılmamış. [Okundu, sonuç doğrulanamadı]
- OpenRouter: kendisi eğitimde kullanmaz; altındaki model sağlayıcısının politikası geçerli. "openrouter/free" yönlendirmesi nedeniyle son sağlayıcı doğrulanamadı. [Okundu]
- Cohere: deneme ve araştırma kullanıcılarında girdi/çıktılar araştırma için kullanılabilir; kurumsal kullanıcılarda kullanılmaz. Platform girdileri kurumsal için 30 gün saklanır. [Okundu]
- Tavily: sorgu verisinin bir kısmı hizmet iyileştirmede kullanılabilir; bazı sorgular üçüncü taraf indeks sağlayıcılarına gidebilir. [Okundu]
- Brave: sohbette verilen sayfa Brave tarayıcısı politikası; Search API için geçerli değil. [Doğrulanamadı]
- DuckDuckGo: sohbette verilen metin tanıtım niteliğinde, HTML arama için resmi kaynak değil. [Doğrulanamadı]

## 10. Maskeleme sınırı

- Maskeleme kart, IBAN, e-posta, telefon, TC ve anahtar benzeri değerleri kapsar. Ad, adres ve tarih kapsamaz. [Okundu: maskText, satır 441]
