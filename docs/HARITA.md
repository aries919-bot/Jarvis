# Aries — Durum Haritası

> Tek gerçek durum dosyası. Sohbet geçmişinden önce bu dosya okunur.
> Son kontrol: 23.11.2026. Repo: `aries919-bot/Jarvis`.
> Bu dal yalnızca proje notları içindir; Android kodu burada geliştirilmez.

## Repo gerçeği
- Varsayılan `main`: `63e877d96cb910e6d98d56834ae55ae8edafab2a`.
- Notlar `durum` dalında; repo erişimi ve yazma izni doğrulandı.
- K1 PR #24 açık/taslak, dal `is/kariyer-kaynak-denetimi-1`, base `main`, HEAD `be740a3d24049fb1145faf14207202c1edc405f2`, mergeable; PR/push CI 45165045934/45165041955 SUCCESS.
- A2 PR #25 açık/taslak, dal `is/a2-expressive-theme`, base `main`, HEAD `2f0fbd8efb6c51083d1e0e0e484d867e1d6d0f73`, mergeable. PR CI 45169541324 FAILED: `testDebugUnitTest` aşamasındaki `:app:checkDebugAarMetadata` NPE (`path` null); APK adımı atlandı. Önceki webSearch imza uyuşmazlığı HEAD'de düzeltilmiş; bu ayrı Gradle/AGP hatası stacktrace ile teşhis edilecek.
- Diğer açık PR'lar: #22/#23/#18/#17/#9/#4/#3/#2/#1; farklı tabanlı eski PR'lar main'e alınmış sayılmaz.
- Son tamamlanmış HEAD baseline: c9a5ce6; PR CI 45164079348 ve push CI 45164072719 SUCCESS. HEAD c0a542a için PR/push CI 45164530319 ve 45164525918 SUCCESS. HEAD be740a3 için PR CI 45165045934 ve push CI 45165041955 SUCCESS; offline regresyon + 20 kaynak canlı probe tamamlandı.

## İş listesi
| İş | Durum | Not |
|---|---|---|
| K1 Kaynak tablosu ve okuyucu sağlığı | PR AÇIK | PR #24 HEAD be740a3; PR/push CI yeşil, 20 kaynak tarayıcı/giriş durumu kayıtlı. Bağımsız hakem onayı bekleniyor; kendi PR'ımızı onaylamayız. |
| A1 Tema: 2 ekran taslağı | ONAYLANDI | Cem Ana ekran + İş & Kariyer tasarımlarını onayladı. |
| A2 Material 3 Expressive tüm uygulama | DEVAM | PR #25; HEAD 2f0fbd8. PR CI 45169541324 FAILED; gerçek logda `testDebugUnitTest` içindeki `:app:checkDebugAarMetadata` NPE (`path` null), APK adımı skip. Önceki nottaki yalnız `checkDebugAarMetadata` hatası doğrulandı; stacktrace ile kök neden araştırılıyor. |
| B1 Yapay zekâ: streaming, bellek, ortak ses/yazı beyni, net hata | PR AÇIK | PR #26 `is/b1-ai-memory-foundation` → `is/a2-expressive-theme`, son değişiklik HEAD `dbab9d9`. Önceki HEAD `79b30b6` PR/push CI yeşildi. Ek düzeltme: yarım kalan OpenAI SSE `[DONE]` yoksa hata, Gemini `finishReason` yoksa hata; kesik akış sessizce tamamlanmış sayılmıyor. Kısmi yanıt uyarısının satır sonu düzeltildi. Güncel HEAD `dbab9d9d275cd8058a1d94900806fb04d82da9cf`: PR CI 45175633112 ve push CI 45175625717 SUCCESS; her ikisinde birim test + debug APK başarılı. Ara commit `ad0fb76` sıralı dosya commitlerinden önce tetiklendiği için `geminiFinished` referansı eksik kaldı ve başarısız oldu; güncel HEAD'de parser ve test mevcut. Bağımsız insan incelemesi, canlı sağlayıcı anahtarı ve telefon testi bekliyor. B1 workflow yalnız JVM unit + debug APK çalıştırır; main'de tam uygulama QA raporu workflow/script'i bulunmuyor (PR #17 ayrı dalda). |
| C1 Sunucu tarafı İş & Kariyer toplayıcı + JSON | YAPILMADI | K1 hakem kararı sonrası; ücretsiz Actions/Worker, arama motoru kazıma yok |
| C2 Ayrı listeler, geçmiş, Haber/Duyuru ayrımı | YAPILMADI | Kamu türleri/özel sektör/staj/kura ayrı |
| C3 İlan şeması, AI alan çıkarımı, kullanıcı tetikli tarayıcı yedeği | YAPILMADI | Kaynak/AI/tarayıcı provenansı; son tarih ve puan çapraz kontrol |
| C4 CV + KPSS tercih modülü | YAPILMADI | Profil ve nitelik kodları telefonda kalır |
| C5 Gelecek alım, takvim ve bildirim | YAPILMADI | Resmî tarih / tahmin ayrı |
| D1 Film & Dizi | YAPILMADI | Yasal kaynak; TMDB/JustWatch atfı; Media3 ve sezon/bölüm |
| E1 Altın/Borsa + Xiaomi | YAPILMADI | PR #22'de kısmi çalışma var; main'e alınmış değil |
| Z1 Gerçek telefon testleri | TELEFONDA DENENECEK | Mikrofon, Xiaomi pil, alarm/bildirim, canlı API, gerçek video |

## K1 kanıt özeti
- Güncel be740a3 artifact: 20 kaynak, 12 probe, 6 çalışan okuyucu (3 detay + 3 indeks), 3 açık ilan detay kaynağı, 2 program okuyucu (3 program bağlantısı).
- Açık detay örnekleri: Sayıştay, Anadolu Üniversitesi, TGA; Zafer Kalkınma Ajansı süreç duyurusu, güvenlik/CAPTCHA işareti nedeniyle otomasyondan hariç.
- PR kapsamı yalnız envanter + sağlık denetimi + tarayıcı/giriş matrisi; sunucu JSON toplayıcı, dinamik Kariyer Kapısı/İŞKUR ve uygulama feed bağlantısı kapsam dışı.
- PR #24 yazarı bu oturumdaki hakemle aynı çalışma hattı; bu hakem kendi yamayı ONAYLAYAMAZ veya merge edemez. Bağımsız insan incelemesi gereklidir.

## Son 10 kayıt
1. K1 için 20 resmî/kurumsal kaynak ve ücretsiz kaynak sağlık probe'u eklendi.
2. QA Türkçe T.C. normalizasyon hash hatası bulundu ve düzeltildi.
3. Türkçe ay adı tarih penceresi regresyonu eklendi.
4. TGA detay okuyucusu eklendi; okuyucu/indeks sayıları ayrıştırıldı.
5. K1 PR #24: 20 kaynak matrisi, browser/login durumları ve canlı QA raporu; bağımsız inceleme bekliyor.
6. A1 iki ekran tasarımı Cem tarafından onaylandı.
7. A2 PR #25: push CI yeşil, PR check `:app:checkDebugAarMetadata` hatasıyla başarısız; bağımsız inceleme bekliyor.
8. B1 PR #26 A2 üzerine taşındı; hafıza kontrolleri Ayarlar'da, notlar yerel, bulut paylaşımı varsayılan kapalı, silme onaylı. HEAD 3a832bb CI yeşil.
9. B1 streaming: Groq/Mistral/Gemini SSE, kademeli JSON reply, saf Kotlin parser, SSE metadata ayrıştırma ve hata sınıflandırması eklendi. HEAD 024681a CI test+APK yeşil.
10. B1 HEAD `dbab9d9`: yarım SSE akışı tespiti ve regresyon testi eklendi; PR CI 45175633112 + push CI 45175625717 SUCCESS (unit + APK). PR #26 açık/taslak; bağımsız review, canlı API ve telefon doğrulaması bekliyor.
11. A2 PR #25 logu tekrar incelendi: workflow unit test komutunda `:app:checkDebugAarMetadata` `path` null NPE veriyor; eski `webSearch` imza hatası değil. Teşhis için stacktrace eklenecek.
**SONRAKİ ADIM:** A2 PR #25'te `checkDebugAarMetadata` NPE stacktrace'ini al, kök nedeni düzelt ve PR/push CI'ı yeniden doğrula.