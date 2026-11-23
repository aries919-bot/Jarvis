# Aries — Durum Haritası

> Tek gerçek durum dosyası. Sohbet geçmişinden önce bu dosya okunur.
> Son kontrol: 23.11.2026. Repo: `aries919-bot/Jarvis`.
> Bu dal yalnızca proje notları içindir; Android kodu burada geliştirilmez.

## Repo gerçeği
- Varsayılan `main`: `63e877d96cb910e6d98d56834ae55ae8edafab2a`.
- Notlar `durum` dalında; repo erişimi ve yazma izni doğrulandı.
- K1 PR #24 açık/taslak, dal `is/kariyer-kaynak-denetimi-1`, base `main`, HEAD `be740a3d24049fb1145faf14207202c1edc405f2`, mergeable; PR/push CI 45165045934/45165041955 SUCCESS.
- A2 PR #25 açık/taslak, dal `is/a2-expressive-theme`, base `main`, HEAD `2f0fbd8efb6c51083d1e0e0e484d867e1d6d0f73`, mergeable. Son PR/push CI 45169541324/45169532073 çalışıyor; son tamamlanan denemede yalnız webSearch imza uyuşmazlığı bulundu ve düzeltildi, yeni CI sonucu bekleniyor.
- Diğer açık PR'lar: #22/#23/#18/#17/#9/#4/#3/#2/#1; farklı tabanlı eski PR'lar main'e alınmış sayılmaz.
- Son tamamlanmış HEAD baseline: c9a5ce6; PR CI 45164079348 ve push CI 45164072719 SUCCESS. HEAD c0a542a için PR/push CI 45164530319 ve 45164525918 SUCCESS. HEAD be740a3 için PR CI 45165045934 ve push CI 45165041955 SUCCESS; offline regresyon + 20 kaynak canlı probe tamamlandı.

## İş listesi
| İş | Durum | Not |
|---|---|---|
| K1 Kaynak tablosu ve okuyucu sağlığı | PR AÇIK | PR #24 HEAD be740a3; PR/push CI yeşil, 20 kaynak tarayıcı/giriş durumu kayıtlı. Bağımsız hakem onayı bekleniyor; kendi PR'ımızı onaylamayız. |
| A1 Tema: 2 ekran taslağı | ONAYLANDI | Cem Ana ekran + İş & Kariyer tasarımlarını onayladı. |
| A2 Material 3 Expressive tüm uygulama | PR AÇIK | PR #25; Pal/Tok ortak tasarımı, 48dp kontroller, ana ekran hızlı erişim, Film & Dizi Compose teması, eksik medya destek dosyaları ve build bağımlılıkları. Son CI 45169541324/45169532073 sürüyor; bağımsız hakem gerekli. |
| B1 Yapay zekâ: streaming, bellek, ortak ses/yazı beyni, net hata | DEVAM | Bu tur seçildi. main/Ai.kt inceleniyor; mevcut senkron Net.post ve webSearch fallback bulguları. A2 PR #25 son CI 45169541324 FAILED: :app:checkDebugAarMetadata, path null. |
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
1. K1 için 20 kaynaklık liste ve ücretsiz kaynak sağlık probe'u eklendi.
2. QA Türkçe T.C. normalizasyon hash hatasını buldu; düzeltildi.
3. QA Türkçe ay adı tarih penceresi hatasını buldu; düzeltildi ve regresyon testi eklendi.
4. TGA detay okuyucu eklendi.
5. Rapor 5 okuyucu / 3 aktif detay kaynak olarak ayrıştırıldı.
6. K1 PR #24 HEAD be740a3; 20 kaynağın browserCheck/login durumu kayıtlı, PR/push CI SUCCESS, artifact 20/12/6 (3 detay + 3 indeks). K1 bağımsız hakem bekliyor.
7. Bu turda kod PR'ı merge edilmedi.
8. Mevcut Android kariyer taraması hâlâ Worker endpoint + cihazda arama motoru HTML fallback kullanıyor; C1 bunu kaldıracak.
9. Eski #22/#23/#18/#17 tabanları main ile farklı; otomatik birleştirilmeyecek.
10. K1'de indeks öz-bağlantısı/sonuç yanlış pozitifleri düzeltildi, TUSAŞ/ROKETSAN programları ayrıldı, TEI HTTP 403 manual_only, yönlendirme aynı HTTPS hostla sınırlandı, 20 kaynağın browserCheck durumu test edildi. Sonraki: K1 için bağımsız insan hakem onayı.
11. A1 iki ekran tasarımı Cem tarafından onaylandı. A2 PR #25 main tabanında açıldı; Material 3 tema, ortak Pal/Tok, ana ekran hızlı erişimleri, Compose medya teması, medya modülünün eksik destek dosyaları ve CI test aşaması eklendi. İlk testlerde eksik media support sınıfları ve iki webSearch imza uyuşmazlığı bulundu; destek dosyaları film-dizi-v2 dalından incelenerek eklendi, çağrı imzaları düzeltildi. Son CI bekleniyor. Sonraki: PR #25 CI sonucu + bağımsız hakem.

**SONRAKİ ADIM:** B1 mevcut sohbet çağrı zinciri, bellek saklama ve test altyapısını çıkar; küçük bir streaming/bellek dilimi tasarla.