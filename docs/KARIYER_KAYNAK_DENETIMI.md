# Aries — İş ve Kariyer Kaynak Denetimi

Tarih: 23.11.2026. Kapsam: 20 kaynaklık envanter, ücretsiz HTML okuyucu ölçümü ve her kaynağın tarayıcı erişim/giriş durumu. Bu PR sunucu JSON toplayıcısını veya Android kariyer feed'ini henüz değiştirmez.

## Koddan çıkan mevcut mimari
- Android CareerSourceCatalog içinde daha önce 107 kayıt (93 resmî, 14 ikincil) vardı; kayıt sayısı çalışan okuyucu sayısı değildir.
- Otomatik tarama WorkManager ile cihazda planlanıyor. Önce Aries kariyer uç noktasına gider; yeterli sonuç yoksa DuckDuckGo/Bing/Brave HTML arama yedeği ve ardından Google grounding kullanır. Bu, sunucu taraflı JSON toplayıcı değildir.
- İlan tıklaması varsayılan tarayıcıya gider. Film/Dizi MediaSourceBrowser, Kariyer modülünde toplu tarayıcı değildir.
- Bu PR; 20 kaynak kaydı, tarayıcı erişim/giriş notu, güvenli public HTML probe, link sınıflandırması ve regresyon testlerini kapsar. JSON yayınlama, PDF okuyucu, dinamik Kariyer Kapısı/İŞKUR entegrasyonu ve Android feed değişikliği C1 kapsamındadır.

## 20 kaynağın tarayıcı ve CI durumu
| # | Kaynak | Tarayıcı kontrolü (23.11.2026) | CI reader durumu |
|---:|---|---|---|
| 1 | Kariyer Kapısı | Açık public kabuk; Aktif İlanlar başlığı var, satırlar metin çıktısında yok; giriş istemedi | Dynamic; probe edilmedi |
| 2 | İŞKUR Açık İş Haritası | “Lütfen bekleyiniz” ara sayfası; login istemedi | Dynamic; probe edilmedi |
| 3 | e-Devlet İş İlanları | Kimlik doğrulaması istiyor; giriş yapılmadı | Manual-only |
| 4 | ilan.gov.tr | Tarayıcı timeout; giriş/CAPTCHA sonucu çıkarılamaz | SSL sertifika doğrulama hatası |
| 5 | Resmî Gazete | Public ana sayfa ve ilan kategorileri açıldı | PDF arşivi; ayrı PDF reader gerekli |
| 6 | ÖSYM Duyurular | Public sayfa açıldı; ilanlar ve sınav süreçleri ayrılmalı | 11 süreç/sonuç bağlantısı; job reader değil |
| 7 | Sayıştay 4/B | Public detay; 16 pozisyon, başvuru 11–25.11.2026 | Çalışan HTML detail |
| 8 | Anadolu Üniversitesi 4/B | Public detay; 1 pozisyon, başvuru 16–30.11.2026 | Çalışan HTML detail |
| 9 | Zafer Kalkınma Ajansı | Public detay; 2 uzman, başvuru 25.11–14.12.2026; CI güvenlik işareti verdi | Otomatik probe durdu; okuyucu sayılmadı |
| 10 | TGA Kariyer | Public detay; 13 pozisyon, başvuru 03.11–04.12.2026 | Çalışan HTML detail |
| 11 | TÜRKAK Resmî Gazete PDF | PDF tarayıcı aracında alınamadı; login durumu bilinmiyor | PDF reader yok |
| 12 | Sahil Güvenlik PDF | PDF isteği timeout; login durumu bilinmiyor | PDF reader yok |
| 13 | Malatya Turgut Özal Üniversitesi PDB | Public indeks açıldı | Çalışan HTML index; 1 iş + 5 süreç bağlantısı |
| 14 | İnönü Üniversitesi PDB | Tarayıcı timeout; login durumu teyitsiz | SSL sertifika doğrulama hatası |
| 15 | Fırat Üniversitesi PDB | Tarayıcı timeout; login durumu teyitsiz | Timeout |
| 16 | Battalgazi Belediyesi | Public ana sayfa açıldı; Duyurular ve İhale İlanları ayrılmış; login istemedi | CI güvenlik metin işareti nedeniyle durdu; reader sayılmadı |
| 17 | TUSAŞ Açık Pozisyonlar | Public liste açıldı; Global Talents programı iş ilanından ayrı | Çalışan HTML index; 3 iş + 2 program |
| 18 | ROKETSAN Açık Pozisyonlar | Public liste açıldı; CHAIN UP programı iş ilanından ayrı | Çalışan HTML index; 19 iş + 1 program |
| 19 | TEI Açık Pozisyonlar/Yetenek Programları | Public liste açıldı; Jet Talent, Back To Core, genel başvuru bağlantıları görüldü | Actions HTTP 403; otomatik probe kapalı |
| 20 | ASELSAN Kariyer | Sayfa açıldı ancak metin çıktısı yalnız görsel; ilan listesi doğrulanmadı | Manual-only |

loginRequired=null, erişim isteği timeout/PDF aracı hatası nedeniyle doğrulanamadı demektir; giriş gerekliliği varsayılmaz. CAPTCHA veya güvenlik kontrolünü aşmak yoktur. Battalgazi public ana sayfası tarayıcıda açılırken probe'un güvenlik metin işaretinde durması olası yanlış pozitif olarak ayrı tutulur.

## Kaynağında doğrulanan ilan örnekleri
- [Sayıştay resmî ilanı](https://www.sayistay.gov.tr/duyuru/818-sozlesmeli-cozumleyici-ve-sozlesmeli-prog): 3 çözümleyici, 9 lisans programcı, 4 ön lisans programcı; başvuru 11–25.11.2026.
- [Anadolu Üniversitesi resmî ilanı](https://anadolu.edu.tr/duyurular/sozlesmeli-personel-alimi-ilani-1794568099): 1 adet 4/B personel; başvuru 16–30.11.2026.
- [TGA resmî kariyer ilanı](https://tga.gov.tr/kariyer): 13 pozisyon; başvuru 03.11–04.12.2026.
- [Zafer Kalkınma Ajansı](https://zafer.gov.tr/duyurular/227): 2 uzman; başvuru 25.11–14.12.2026. 23 Kasım itibarıyla ilan duyurulmuş, başvuru henüz açılmamıştı.
- Şirket listeleri: [TUSAŞ](https://kariyer.tusas.com/acik-pozisyonlarimiz?sirala=DESC), [ROKETSAN](https://kariyer.roketsan.com.tr/jobs), [TEI](https://yetenek.tei.com.tr/jobs). [ASELSAN](https://www.aselsan.com/tr/kariyer/basvuru) sayfasından makinece okunabilir pozisyon listesi doğrulanmadı.

## Son canlı CI ölçümü
- Son kod doğrulama HEAD: c0a542cace109020d271ca38d2cff2099802fec2.
- PR CI [45164530319](https://github.com/aries919-bot/Jarvis/actions/runs/45164530319) ve push CI [45164525918](https://github.com/aries919-bot/Jarvis/actions/runs/45164525918) SUCCESS.
- Artifact: 20 kaynak, 12 probe, 6 çalışan okuyucu (3 detay + 3 indeks), 3 aktif ilan detay kaynağı, 2 program okuyucu, 26 iş bağlantısı ve 3 program bağlantısı.
- 8 kaynak dinamik/oturumlu/PDF/manuel sınırlar nedeniyle probe edilmedi. ÖSYM'nin 11, Özal indeksinin 5 süreç/sonuç bağlantısı iş ilanlarına eklenmedi.
- Zafer ve Battalgazi CI güvenlik işaretinde durdu; TEI Actions runner 403 verdi. Bu sınırlar aşılmadı. Canlı sonuç, sadece CI'nin gerçekten çalıştırdığı erişim ölçümüdür; tarayıcı kontrolü ayrı sütundadır.

## Güvenlik ve kapsam
LinkedIn/Indeed, ücretli servis, otomatik giriş, CAPTCHA/güvenlik kontrolü aşma veya arama motoru HTML kazıma yok. Denetleyici sadece kayıtlı kaynak hostuna HTTPS yönlendirmeyi kabul eder; başka hosta, HTTP'ye veya kimlik bilgili hedefe yönlendirme reddedilir. Sunucu JSON toplayıcı, AI alan çıkarımı, CV eşleştirmesi ve Android feed sonraki ayrı işlerdir.
