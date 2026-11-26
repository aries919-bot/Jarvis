# Claude devir notu
Son güncelleme: 27.11.2026

## Çalışma şekli
- Kullanıcı Cem. Türkçe, kısa. Önce incele, uygula. Tasarım kararlarını tek tek sorma.
- GitHub yazma (PR, merge) ChatGPT'de. Claude salt okunur, jarvis-cloud private olduğu için okunamaz.
- Kanıt: "geçti" demeden önce Termux çıktısı. ChatGPT beyanları GitHub'dan doğrulanır.
- Claude diff/çıktıyı okur, hakemlik yapar, ChatGPT'ye komut yazar.

## Repolar
- Jarvis (public, dal kariyer-modulu): Android uygulaması. main eski.
- jarvis-cloud (private, main = 010a518): Cloudflare Worker. Testler: node --test tests/ (20/20).

## Durum
- Worker canlıya alınmadı. /health GIT_SHA'yı döndürmeye hazır.
- jarvis-cloud Actions başarısız (#156-158), sebep doğrulanmadı (billing: ücretsiz plan, borç yok; dakika kotası şüphesi).
- Cloudflare paneli telefonda kullanılamıyor.

## Kalan işler
1. Canlıya alma: önce Actions dakika/kota sebebi, olmazsa Cloudflare Connect GitHub.
2. Uygulamada Hafıza KAPALI/AÇIK testi, diğer API anahtarları.
3. jarvis-cloud PR #13 (deploy.yml izinleri).
4. Ai.kt boş catch blokları.
5. Kariyer HTML arama yedeği C1 ile çelişiyor. Jarvis #69/#71 birlikte alınmasın.
6. Eski dokümanlar (DURUM.md, AGENTS.md, docs/HARITA.md).
7. Açık PR temizliği, körlemesine birleştirme yok.

## Kararlar/risk
- Rate limit her istekte D1'e yazar; D1 hatasında 503. Yetkili için fail-open kararı bekliyor.
- Hakem (denetle.py) gerçek Groq çağrısıyla denenmedi.
