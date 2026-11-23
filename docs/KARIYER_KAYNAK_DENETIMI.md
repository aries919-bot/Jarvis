# Aries — İş ve Kariyer Kaynak Denetimi

Tarih: 23.11.2026. Kapsam: 20 kaynaklık envanter, ücretsiz ve sınırlı HTML okuyucu sağlık ölçümü. Bu PR henüz sunucu JSON toplayıcısını veya Android kariyer feed'ini değiştirmez.

## Koddan çıkan mevcut mimari
- Android tarafında CareerSourceCatalog içinde daha önce 107 kayıt (93 resmî, 14 ikincil) vardı; kayıt sayısı çalışan okuyucu sayısı değildir.
- Otomatik tarama WorkManager ile cihazda planlanıyor. Önce Aries kariyer uç noktasına gider; yeterli sonuç yoksa DuckDuckGo/Bing/Brave HTML arama geri dönüşü ve ardından Google grounding kullanır. Bu yapı sunucu taraflı JSON toplayıcı değildir.
- İlan tıklaması varsayılan tarayıcıya gider. Film/Dizi MediaSourceBrowser, Kariyer modülünde toplu tarayıcı değildir.
- Bu PR yalnızca kaynak envanteri, public HTML probe, sınıflandırma ve regresyon testlerini kapsar. JSON yayınlama, PDF okuyucu, dinamik Kariyer Kapısı/İŞKUR entegrasyonu ve Android feed değişikliği sonraki C1 kapsamındadır.

## Güncel 20 kaynak
| # | Kaynak | Tür | Son CI gözlemi |
|---:|---|---|---|
| 1 | Kariyer Kapısı | dynamic | JS tabanlı; public makinece okunur API doğrulanmadı, otomatik probe yok |
| 2 | İŞKUR Açık İş Haritası | dynamic | Bekleme/JS ekranı; API doğrulanmadı, otomatik probe yok |
| 3 | e-Devlet İş İlanları | manual_only | Oturum gerekir; otomatik giriş yapılmaz |
| 4 | ilan.gov.tr | html_index | SSL sertifika doğrulama hatası; giriş/CAPTCHA kanıtı değil |
| 5 | Resmî Gazete | pdf_archive | PDF okuyucu bu PR'da yok |
| 6 | ÖSYM Duyurular | html_index | HTTP 200, 11 süreç/sonuç bağlantısı; iş ilanı okuyucusu sayılmadı |
| 7 | Sayıştay 4/B | html_detail | Çalışan detay okuyucu; 16 pozisyon, başvuru 11–25.11.2026 |
| 8 | Anadolu Üniversitesi 4/B | html_detail | Çalışan detay okuyucu; 1 pozisyon, başvuru 16–30.11.2026 |
| 9 | Zafer Kalkınma Ajansı | html_detail | 200 yanıtında güvenlik/CAPTCHA işareti; probe durdu, okuyucu sayılmadı |
| 10 | TGA Kariyer | html_detail | Çalışan detay okuyucu; 13 pozisyon, başvuru 03.11–04.12.2026 |
| 11 | TÜRKAK Resmî Gazete PDF | pdf_detail | Ayrı PDF okuyucu gerekli |
| 12 | Sahil Güvenlik Resmî Gazete PDF | pdf_detail | Ayrı PDF okuyucu gerekli |
| 13 | Malatya Turgut Özal Üniversitesi PDB | html_index | Çalışan indeks; 1 iş adayı ve 5 süreç/sonuç bağlantısı |
| 14 | İnönü Üniversitesi PDB | html_index | SSL sertifika doğrulama hatası |
| 15 | Fırat Üniversitesi PDB | html_index | Timeout |
| 16 | Battalgazi Belediyesi | html_index | Güvenlik/CAPTCHA işareti; aşılmadı |
| 17 | TUSAŞ Açık Pozisyonlar | html_index | Çalışan indeks; 3 iş bağlantısı + 2 yetenek programı |
| 18 | ROKETSAN Açık Pozisyonlar | html_index | Çalışan indeks; 19 iş bağlantısı + 1 program |
| 19 | TEI Açık Pozisyonlar/Yetenek Programları | manual_only | Actions runner HTTP 403; otomatik erişim devre dışı, aşma yok |
| 20 | ASELSAN Kariyer | manual_only | Kariyer bilgisi açık; makinece okunur pozisyon listesi doğrulanmadı |

## Kaynağında doğrulanan ilan örnekleri
- [Sayıştay resmî ilanı](https://www.sayistay.gov.tr/duyuru/818-sozlesmeli-cozumleyici-ve-sozlesmeli-prog): 3 çözümleyici, 9 lisans programcı, 4 ön lisans programcı; başvuru 11–25.11.2026.
- [Anadolu Üniversitesi resmî ilanı](https://anadolu.edu.tr/duyurular/sozlesmeli-personel-alimi-ilani-1794568099): 1 adet 4/B personel; başvuru 16–30.11.2026.
- [TGA resmî kariyer ilanı](https://tga.gov.tr/kariyer): 13 pozisyon; başvuru 03.11–04.12.2026.
- [Zafer Kalkınma Ajansı](https://zafer.gov.tr/duyurular/227): tarayıcı kontrolünde 2 uzman ve 25.11–14.12.2026 dönemi görüldü. 23.11.2026 itibarıyla başvuru henüz açılmamıştı. Runner güvenlik işareti verdiği için otomatik okuyucu değildir.
- Şirket listeleri de tarayıcıda kontrol edildi: [TUSAŞ](https://kariyer.tusas.com/acik-pozisyonlarimiz?sirala=DESC), [ROKETSAN](https://kariyer.roketsan.com.tr/jobs), [TEI](https://yetenek.tei.com.tr/jobs). TEI sayfası manuel tarayıcıda görünse de Actions HTTP 403 verdi; otomasyon devre dışı bırakıldı. [ASELSAN](https://www.aselsan.com/tr/kariyer/basvuru) için açık, makinece okunabilir pozisyon listesi kanıtlanmadı.

## Son canlı CI ölçümü
- Güncel PR HEAD: `c9a5ce6c93ae114bf6671031f489613edcdc5420`.
- PR CI: [45164079348](https://github.com/aries919-bot/Jarvis/actions/runs/45164079348) ve push CI: [45164072719](https://github.com/aries919-bot/Jarvis/actions/runs/45164072719), ikisi de SUCCESS (23.11.2026).
- Güncel artifact (`aries-career-source-status`, run 45164079348): 20 kaynak, 12 probe, 6 çalışan okuyucu (3 detay + 3 indeks), 3 aktif ilan detay kaynağı, 2 program okuyucu, 26 iş bağlantısı ve 3 program bağlantısı.
- 8 kaynak otomatik probe dışında: dinamik/oturumlu/PDF/manuel sınırlar. Bu sayfalar çalışan okuyucu sayısına eklenmez.
- ÖSYM'nin 11 süreç/sonuç bağlantısı iş ilanı olarak sayılmadı. Özal indeksindeki 5 süreç/sonuç bağlantısı da iş sayısına eklenmedi.
- Sonucun yeşil olması canlı okuyucu kalitesini kanıtlayan tek ölçüt değildir; artifact kaynak başına HTTP, sınıflandırma ve hatayı içerir.

## Güvenlik ve kapsam sınırları
LinkedIn/Indeed, ücretli servis, otomatik giriş, CAPTCHA/güvenlik kontrolü aşma veya arama motoru HTML kazıma yok. Dinamik, PDF ve manuel kaynaklar çalışan reader sayılmaz. K1'in bu aşaması envanter ve sağlık denetimidir; JSON toplayıcı ve uygulamaya gerçek ilan akışı C1'de ayrı PR olarak yapılacaktır.
