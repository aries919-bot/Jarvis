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
- /hizli kimlik doğrulaması.
- Sağlayıcıların veri saklama politikaları.
- Cloudflare secret ve log ayarları.
