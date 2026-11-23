# Aries — Durum Haritası

> Tek gerçek durum dosyası. Sohbet geçmişinden önce bu dosya okunur.
> Son kontrol: 23.11.2026. Repo: `aries919-bot/Jarvis`.
> Bu dal yalnızca proje notları içindir; Android kodu burada geliştirilmez.

## Repo gerçeği
- Varsayılan `main`: `63e877d96cb910e6d98d56834ae55ae8edafab2a`.
- Notlar `durum` dalında; repo erişimi ve yazma izni doğrulandı.
- K1 PR #24 açık/taslak, dal `is/kariyer-kaynak-denetimi-1`, base `main`, HEAD `c0a542cace109020d271ca38d2cff2099802fec2`, mergeable.
- Diğer açık PR'lar: #22/#23/#18/#17/#9/#4/#3/#2/#1; farklı tabanlı eski PR'lar main'e alınmış sayılmaz.
- Son tamamlanmış HEAD baseline: c9a5ce6; PR CI 45164079348 ve push CI 45164072719 SUCCESS. Yeni güvenli-yönlendirme testli HEAD c0a542a için PR CI 45164530319 çalışıyor; test adımı SUCCESS, canlı probe aşaması sürüyor.

## İş listesi
| İş | Durum | Not |
|---|---|---|
| K1 Kaynak tablosu ve okuyucu sağlığı | DEVAM | PR #24 bağımsız hakem incelemesi; kaynak raporu + tam diff/CI kanıtı doğrulanıyor. Yama yazarı hakem onayı veremez. |
| A1 Tema: 2 ekran taslağı | YAPILMADI | Koda geçmeden önce İş & Kariyer + Ana ekran taslağını Cem'e sun; seçim bekle |
| A2 Material 3 Expressive tüm uygulama | YAPILMADI | A1 seçimi sonrası; eski PR #18 ayrı incelenecek |
| B1 Yapay zekâ: streaming, bellek, ortak ses/yazı beyni, net hata | YAPILMADI | Ücretsiz; güvenli onaylı eylemler |
| C1 Sunucu tarafı İş & Kariyer toplayıcı + JSON | YAPILMADI | K1 hakem kararı sonrası; ücretsiz Actions/Worker, arama motoru kazıma yok |
| C2 Ayrı listeler, geçmiş, Haber/Duyuru ayrımı | YAPILMADI | Kamu türleri/özel sektör/staj/kura ayrı |
| C3 İlan şeması, AI alan çıkarımı, kullanıcı tetikli tarayıcı yedeği | YAPILMADI | Kaynak/AI/tarayıcı provenansı; son tarih ve puan çapraz kontrol |
| C4 CV + KPSS tercih modülü | YAPILMADI | Profil ve nitelik kodları telefonda kalır |
| C5 Gelecek alım, takvim ve bildirim | YAPILMADI | Resmî tarih / tahmin ayrı |
| D1 Film & Dizi | YAPILMADI | Yasal kaynak; TMDB/JustWatch atfı; Media3 ve sezon/bölüm |
| E1 Altın/Borsa + Xiaomi | YAPILMADI | PR #22'de kısmi çalışma var; main'e alınmış değil |
| Z1 Gerçek telefon testleri | TELEFONDA DENENECEK | Mikrofon, Xiaomi pil, alarm/bildirim, canlı API, gerçek video |

## K1 kanıt özeti
- Güncel c9a5ce6 artifact: 20 kaynak, 12 probe, 6 çalışan okuyucu (3 detay + 3 indeks), 3 açık ilan detay kaynağı, 2 program okuyucu (3 program bağlantısı).
- Açık detay örnekleri: Sayıştay, Anadolu Üniversitesi, TGA; Zafer Kalkınma Ajansı süreç duyurusu, güvenlik/CAPTCHA işareti nedeniyle otomasyondan hariç.
- PR kapsamı yalnız envanter + sağlık denetimi; sunucu JSON toplayıcı, dinamik Kariyer Kapısı/İŞKUR ve uygulama feed bağlantısı kapsam dışı.
- PR #24 yazarı bu oturumdaki hakemle aynı çalışma hattı; bu hakem kendi yamayı ONAYLAYAMAZ veya merge edemez. Bağımsız insan incelemesi gereklidir.

## Son 10 kayıt
1. K1 için 20 kaynaklık liste ve ücretsiz kaynak sağlık probe'u eklendi.
2. QA Türkçe T.C. normalizasyon hash hatasını buldu; düzeltildi.
3. QA Türkçe ay adı tarih penceresi hatasını buldu; düzeltildi ve regresyon testi eklendi.
4. TGA detay okuyucu eklendi.
5. Rapor 5 okuyucu / 3 aktif detay kaynak olarak ayrıştırıldı.
6. K1 PR #24; HEAD c9a5ce6 CI SUCCESS (20 kaynak, 12 probed, 6 okuyucu); son commit c0a542a'da aynı-host HTTPS yönlendirme kuralı ve ek regresyon testleri, CI sürüyor.
7. Bu turda kod PR'ı merge edilmedi.
8. Mevcut Android kariyer taraması hâlâ Worker endpoint + cihazda arama motoru HTML fallback kullanıyor; C1 bunu kaldıracak.
9. Eski #22/#23/#18/#17 tabanları main ile farklı; otomatik birleştirilmeyecek.
10. K1'de eklenen düzeltme: indeks sayfasının kendisini ilan saymama, kura/sonuç kelime gruplarını ayırma, TUSAŞ/ROKETSAN programlarını ilanlardan ayırma, TEI HTTP 403 kaynağını manual_only tutma ve probe yönlendirmelerini aynı HTTPS host ile sınırlandırma. Yeni QA tamamlanınca K1 hakem kararı; C1 sonraki teknik iş.

**SONRAKİ ADIM:** K1'in tam kod ve canlı QA artefaktını bağımsız gözle incele; kendi yamanı ONAYLAMA.