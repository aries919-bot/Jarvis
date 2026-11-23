# Aries — Durum Haritası

> Tek gerçek durum dosyası. Sohbet geçmişinden önce bu dosya okunur.
> Son kontrol: 23.11.2026. Repo: `aries919-bot/Jarvis`.
> Bu dal yalnızca proje notları içindir; Android kodu burada geliştirilmez.

## Repo gerçeği
- Varsayılan `main`: `63e877d96cb910e6d98d56834ae55ae8edafab2a`.
- Notlar `durum` dalında; repo okuma/yazma erişimi doğrulandı.
- K1 PR #24 açık/taslak, dal `is/kariyer-kaynak-denetimi-1`, base `main`, HEAD `be740a3d24049fb1145faf14207202c1edc405f2`; PR/push CI SUCCESS.
- A2 PR #25 açık/taslak, dal `is/a2-expressive-theme`, base `main`, HEAD `2f0fbd8efb6c51083d1e0e0e484d867e1d6d0f73`. İlk CI denemesinde `:app:checkDebugAarMetadata` `path is null` NPE oluştu; aynı run'ın ikinci denemesi SUCCESS: test, APK ve artefakt başarılı. Hata tekrar üretilemedi; kök neden belirlenmedi.
- A2 tanılama PR #27 açık/taslak, `fix/a2-gradle-metadata-stacktrace` → A2; HEAD `ce88f0c7985cbf94cbe2fdba64b113ad5dc37455`. Workflow-only fark; PR #45177070960 ve push #45177061495 SUCCESS; bağımsız inceleme bekliyor.
- A2 devam PR #28 açık/taslak, `is/a2-theme-completion` → A2, HEAD `f788b340748918c698ead66bd38081a26931fb70`; 6 dosya, 6 commit. Açık/koyu palet, yerel tema tercihi, gerçek görev/hatırlatma panel sayacı, medya skeleton ve kontrast testleri. PR açıldı; CI koşusu henüz görünmüyor.
- A2 eksik kapsam: İş & Kariyer ekranı, kariyer modülü/main tabanı yokken sahte veriyle eklenmedi. Açık ilan son tarihleri C1/C5 feed bağlanınca gösterilecek.
- B1 PR #26 açık/taslak, `is/b1-ai-memory-foundation` → A2, HEAD `dbab9d9d275cd8058a1d94900806fb04d82da9cf`; PR CI #45175633112 ve push CI #45175625717 SUCCESS. Bağımsız inceleme, canlı API ve telefon doğrulaması bekliyor.
- Diğer açık PR'lar: #22/#23/#18/#17/#9/#4/#3/#2/#1; eski/farklı tabanlı PR'lar main'e alınmış sayılmaz.

## İş listesi
| İş | Durum | Not |
|---|---|---|
| K1 Kaynak tablosu ve okuyucu sağlığı | PR AÇIK | PR #24 HEAD be740a3; 20 kaynak erişim/giriş matrisi, 6 çalışan okuyucu; bağımsız hakem onayı bekliyor. |
| A1 Tema: 2 ekran taslağı | ONAYLANDI | Cem Ana ekran + İş & Kariyer tasarımlarını onayladı. |
| A2 Material 3 Expressive tüm uygulama | DEVAM | PR #25 CI retry yeşil, #27 tanılama yeşil; #28 açık/taslak, CI bekleniyor. #28: açık/koyu mod, panel sayacı, skeleton ve kontrast regresyonları. İş & Kariyer ekranı C1/C2 entegrasyonuyla yapılacak. Bağımsız review yok. |
| B1 Yapay zekâ: streaming, bellek, ortak ses/yazı beyni, net hata | PR AÇIK | PR #26 HEAD dbab9d9; test + APK CI yeşil. Bağımsız insan incelemesi, canlı sağlayıcı ve telefon testi bekliyor. |
| C1 Sunucu taraflı İş & Kariyer toplayıcı + JSON | YAPILMADI | K1 hakem kararı sonrası; ücretsiz Actions/Worker, arama motoru kazıma yok. |
| C2 Ayrı listeler, geçmiş, Haber/Duyuru ayrımı | YAPILMADI | Kamu türleri/özel sektör/staj/kura ayrı. |
| C3 İlan şeması, AI alan çıkarımı, kullanıcı tetikli tarayıcı yedeği | YAPILMADI | Kaynak/AI/tarayıcı provenansı; son tarih ve puan çapraz kontrol. |
| C4 CV + KPSS tercih modülü | YAPILMADI | Profil ve nitelik kodları telefonda kalır. |
| C5 Gelecek alım, takvim ve bildirim | YAPILMADI | Resmî tarih / tahmin ayrı. |
| D1 Film & Dizi | YAPILMADI | Yasal kaynak; TMDB/JustWatch atfı; Media3 ve sezon/bölüm. |
| E1 Altın/Borsa + Xiaomi | YAPILMADI | PR #22'de kısmi çalışma var; main'e alınmış değil. |
| Z1 Gerçek telefon testleri | TELEFONDA DENENECEK | Mikrofon, Xiaomi pil, alarm/bildirim, canlı API, gerçek video ve yeni tema kalıcılığı. |

## K1 kanıt özeti
- Güncel be740a3 artifact: 20 kaynak, 12 probe, 6 çalışan okuyucu (3 detay + 3 indeks), 3 açık ilan detay kaynağı, 2 program okuyucu (3 program bağlantısı).
- Açık detay örnekleri: Sayıştay, Anadolu Üniversitesi, TGA; Zafer Kalkınma Ajansı güvenlik/CAPTCHA nedeniyle otomasyondan hariç.
- PR #24 envanter + sağlık denetimi + tarayıcı/giriş matrisi kapsamı; sunucu JSON toplayıcı ve uygulama feed bağlantısı kapsam dışı.
- PR #24/#25/#26/#27/#28 için bağımsız inceleme gerekir; bu oturumda kendi yazılan yamalar onaylanmaz veya birleştirilmez.

## Son 10 kayıt
1. QA Türkçe T.C. normalizasyon hash hatası bulundu ve düzeltildi.
2. Türkçe ay adı tarih penceresi regresyonu eklendi.
3. TGA detay okuyucusu eklendi; okuyucu/indeks sayıları ayrıştırıldı.
4. K1 PR #24: 20 kaynak matrisi, browser/login durumları ve canlı QA raporu; bağımsız inceleme bekliyor.
5. A1 iki ekran tasarımı Cem tarafından onaylandı.
6. A2 PR #25: ilk CI NPE, aynı run'ın ikinci denemesi test+APK SUCCESS; kök neden belirlenmedi.
7. B1 PR #26 hafıza kontrolleri / yerel not / varsayılan kapalı bulut paylaşımı; CI yeşil.
8. B1 streaming Groq/Mistral/Gemini SSE ve saf Kotlin parser; CI test+APK yeşil.
9. A2 tanılama PR #27 workflow-only, PR/push CI SUCCESS; NPE tekrar üretilemedi.
10. A2 devam PR #28: 6 commit ile light/dark palette, saklanan tercih, dashboard sayaçları, media skeleton, contrast tests; CI bekleniyor.
**SONRAKİ ADIM:** PR #28 CI'ı bul/incele; hata varsa düzelt, başarılıysa PR'ı bağımsız hakem incelemesine bırak.
