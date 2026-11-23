# Aries — Durum Haritası

> Tek gerçek durum dosyası. Sohbet geçmişinden önce bu dosya okunur.
> Son kontrol: 23.11.2026. Repo: `aries919-bot/Jarvis`.
> Bu dal yalnızca proje notları içindir; Android kodu burada geliştirilmez.

## Repo gerçeği
- Varsayılan `main`: `63e877d96cb910e6d98d56834ae55ae8edafab2a`.
- Notlar `durum` dalında; repo okuma/yazma erişimi doğrulandı.
- K1 PR #24 açık/taslak, dal `is/kariyer-kaynak-denetimi-1`, base `main`, HEAD `be740a3d24049fb1145faf14207202c1edc405f2`; PR/push CI SUCCESS.
- A2 PR #25 açık/taslak, dal `is/a2-expressive-theme`, base `main`, HEAD `2f0fbd8efb6c51083d1e0e0e484d867e1d6d0f73`. İlk PR CI denemesinde `:app:checkDebugAarMetadata` `path is null` NPE hatası oluştu; aynı run attempt 2 (23.11.2026) SUCCESS: birim testleri, `assembleDebug` ve APK artefaktı yükleme başarılı. Hata tekrar üretilemedi; kök neden belirlenmedi.
- A2 tanılama PR #27 açık/taslak, `fix/a2-gradle-metadata-stacktrace` → `is/a2-expressive-theme`, HEAD `ce88f0c7985cbf94cbe2fdba64b113ad5dc37455`. Fark yalnızca `.github/workflows/android.yml` (4 ekleme/4 silme; A2 PR tetikleyicisi + --stacktrace). PR CI #45177070960 ve push CI #45177061495 SUCCESS; test + APK başarılı. PR #27 bağımsız inceleme bekliyor.
- A2 kapsam denetimi: ortak Pal/Tok/UI yardımcıları MainActivity sayfalarına renk ve 48dp hedefleri uyguluyor; Film/Dizi Compose tema sarmalayıcısı yalnızca koyu renk şeması sağlıyor. Eksikler: açık tema/geçişi yok; iskelet yükleme yok (liste yüklemesinde spinner); ana ekranda günün özeti ve yaklaşan ilan son tarihleri yok; İş & Kariyer ekranı main tabanında bulunmadığı için A1 tasarımı henüz uygulanmıyor. Material3 Compose teması yalnız medya ekranında, diğer sayfalar native View hiyerarşisinde.
- B1 PR #26 açık/taslak, `is/b1-ai-memory-foundation` → A2, HEAD `dbab9d9d275cd8058a1d94900806fb04d82da9cf`; PR CI #45175633112 ve push CI #45175625717 SUCCESS. Bağımsız inceleme, canlı API ve telefon doğrulaması bekliyor.
- Diğer açık PR'lar: #22/#23/#18/#17/#9/#4/#3/#2/#1; eski/farklı tabanlı PR'lar main'e alınmış sayılmaz.

## İş listesi
| İş | Durum | Not |
|---|---|---|
| K1 Kaynak tablosu ve okuyucu sağlığı | PR AÇIK | PR #24 HEAD be740a3; 20 kaynak erişim/giriş matrisi, 6 çalışan okuyucu; bağımsız hakem onayı bekliyor. |
| A1 Tema: 2 ekran taslağı | ONAYLANDI | Cem Ana ekran + İş & Kariyer tasarımlarını onayladı. |
| A2 Material 3 Expressive tüm uygulama | DEVAM | PR #25 CI yeniden denemesi yeşil; PR #27 tanılama CI yeşil. Ancak açık/koyu tema, iskelet yükleme, tam panel ana ekran ve İş & Kariyer görünümü henüz tamam değil. PR #25/#27 bağımsız inceleme bekliyor. |
| B1 Yapay zekâ: streaming, bellek, ortak ses/yazı beyni, net hata | PR AÇIK | PR #26 HEAD dbab9d9; test + APK CI yeşil. Bağımsız insan incelemesi, canlı sağlayıcı ve telefon testi bekliyor. |
| C1 Sunucu taraflı İş & Kariyer toplayıcı + JSON | YAPILMADI | K1 hakem kararı sonrası; ücretsiz Actions/Worker, arama motoru kazıma yok. |
| C2 Ayrı listeler, geçmiş, Haber/Duyuru ayrımı | YAPILMADI | Kamu türleri/özel sektör/staj/kura ayrı. |
| C3 İlan şeması, AI alan çıkarımı, kullanıcı tetikli tarayıcı yedeği | YAPILMADI | Kaynak/AI/tarayıcı provenansı; son tarih ve puan çapraz kontrol. |
| C4 CV + KPSS tercih modülü | YAPILMADI | Profil ve nitelik kodları telefonda kalır. |
| C5 Gelecek alım, takvim ve bildirim | YAPILMADI | Resmî tarih / tahmin ayrı. |
| D1 Film & Dizi | YAPILMADI | Yasal kaynak; TMDB/JustWatch atfı; Media3 ve sezon/bölüm. |
| E1 Altın/Borsa + Xiaomi | YAPILMADI | PR #22'de kısmi çalışma var; main'e alınmış değil. |
| Z1 Gerçek telefon testleri | TELEFONDA DENENECEK | Mikrofon, Xiaomi pil, alarm/bildirim, canlı API, gerçek video. |

## K1 kanıt özeti
- Güncel be740a3 artifact: 20 kaynak, 12 probe, 6 çalışan okuyucu (3 detay + 3 indeks), 3 açık ilan detay kaynağı, 2 program okuyucu (3 program bağlantısı).
- Açık detay örnekleri: Sayıştay, Anadolu Üniversitesi, TGA; Zafer Kalkınma Ajansı süreç duyurusu, güvenlik/CAPTCHA işareti nedeniyle otomasyondan hariç.
- PR kapsamı envanter + sağlık denetimi + tarayıcı/giriş matrisi; sunucu JSON toplayıcı, dinamik Kariyer Kapısı/İŞKUR ve uygulama feed bağlantısı kapsam dışı.
- PR #24 yazarı bu oturumdaki hakemle aynı çalışma hattı; kendi yamamızı ONAYLAYAMAYIZ veya merge edemeyiz. Bağımsız insan incelemesi gereklidir.

## Son 10 kayıt
1. K1 için Türkçe T.C. normalizasyon hash hatası bulundu ve düzeltildi.
2. Türkçe ay adı tarih penceresi regresyonu eklendi.
3. TGA detay okuyucusu eklendi; okuyucu/indeks sayıları ayrıştırıldı.
4. K1 PR #24: 20 kaynak matrisi, browser/login durumları ve canlı QA raporu; bağımsız inceleme bekliyor.
5. A1 iki ekran tasarımı Cem tarafından onaylandı.
6. A2 PR #25: ilk CI `:app:checkDebugAarMetadata` NPE ile başarısız, aynı run'ın ikinci denemesi birim test + APK ile SUCCESS; kök neden belirlenmedi.
7. B1 PR #26 A2 üzerine taşındı; bellek kontrolleri, yerel not ve varsayılan kapalı bulut paylaşımı eklendi. HEAD dbab9d9 CI yeşil.
8. B1 streaming: Groq/Mistral/Gemini SSE, kademeli JSON reply, saf Kotlin parser ve hata sınıflandırması eklendi. CI test+APK yeşil.
9. A2 tanılama PR #27: workflow farkı dışında uygulama kodu değişmedi; PR/push CI SUCCESS, NPE tekrar üretilemedi.
10. A2 kapsam denetimi: 48dp ortak hedefler doğrulandı; açık tema, skeleton, kapsamlı dashboard ve İş & Kariyer ekranı açık kaldı. HARITA güncellendi.
**SONRAKİ ADIM:** A2'nin eksik tasarım gereksinimleri için tamamlayıcı bir dal/PR hazırla; değişiklikleri test edip bağımsız inceleme için bırak.
