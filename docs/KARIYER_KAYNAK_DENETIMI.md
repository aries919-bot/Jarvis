# Aries — İş ve Kariyer Kaynak Denetimi

Tarih: 23.11.2026. Kapsam: ilk 20 kaynaklık envanter ve otomatik okuyucu sağlığı; mevcut Kariyer ekranı henüz değiştirilmedi.

## Koddan çıkan bugünkü mimari
- CareerSourceCatalog içinde 107 URL kaydı vardı: 93 official=true, 14 ikincil; 99 host. 107 kayıt, 107 çalışan okuyucu değildir.
- LinkedIn ve Indeed arama sorgularında yer alıyordu; bu denetim dalgasından çıkarıldı.
- Otomatik günlük tarama cihazdaki WorkManager'da 08:30 ve 20:30 için planlanıyor. Önce Ai.kariyerAra uç noktasına istek gidiyor; yeterli sonuç yoksa telefon DuckDuckGo/Bing/Brave HTML araması yapıyor; sonra Google grounding deniyor. Bu, server-side collector değildir.
- Kariyer ilanlarına dokunma Intent.ACTION_VIEW ile varsayılan tarayıcıyı açıyor. Kariyer modülünde toplu otomatik WebView yok; MediaSourceBrowser WebView'i Film/Dizi modülüne ait.

## Kaynak başına ön gözlem
1 Kariyer Kapısı: JS shell; ilan satırlarının public JSON/API erişimi doğrulanmadı.
2 İŞKUR Açık İş Haritası: "Lütfen bekleyiniz" ara sayfası; API doğrulanmadı.
3 e-Devlet: kimlik doğrulama gerektirir; yalnız kullanıcı açar.
4 ilan.gov.tr: elle denetimde timeout; CAPTCHA/giriş kanıtı değildir.
5 Resmî Gazete: PDF arşivi; ayrı, boyut sınırlı PDF reader gerekli.
6 ÖSYM Duyurular: public HTML; KPSS takvim/kılavuz kaynağı, tüm kayıtlar iş değildir.
7 Sayıştay: doğrudan ilan detay HTML'i.
8 Anadolu Üniversitesi: doğrudan ilan detay HTML'i.
9 Zafer Kalkınma Ajansı: doğrudan ilan detay HTML'i; başvuru henüz açılmadı.
10 ÇSGB Duyurular: public HTML, tarihî sınav/sonuç kayıtları da içerir.
11 Millî Saraylar Personel Alım: public HTML, eski kayıtlar da var.
12 TGA Kariyer: doğrudan HTML detay okuyucusu; CI 200 yanıtı, ilan metni ve 03.11–04.12.2026 tarih aralığını doğruladı.
13 TÜRKAK Resmî Gazete PDF: PDF reader bekliyor.
14 Sahil Güvenlik Resmî Gazete PDF: PDF reader bekliyor.
15 Malatya Turgut Özal Üniversitesi PDB: public HTML.
16 İnönü Üniversitesi PDB: elle kontrolde timeout.
17 Fırat Üniversitesi PDB: elle kontrolde timeout.
18 Adıyaman Üniversitesi PDB: public HTML.
19 Battalgazi Belediyesi: Duyurular ve İhale/İlanlar alanları ayrı; ihale iş ilanı değildir.
20 Yeşilyurt Belediyesi: Güncel/Duyuru alanı public.

## Kaynağında manuel doğrulanan 3 ilan
TGA'nın doğrudan kariyer sayfasında 13 pozisyon ve 03.11.2026–04.12.2026 başvuru aralığı da görüntülenebiliyor.
- Sayıştay: 3 çözümleyici, 9 lisans programcı, 4 ön lisans programcı; başvuru 11–25.11.2026, 23:59:59.
- Anadolu Üniversitesi: 1 adet 4/B personel; başvuru 16–30.11.2026.
- Zafer Kalkınma Ajansı: 2 uzman; TR33 bölgesinde görevlendirme; başvuru 25.11–14.12.2026. 23 Kasım itibarıyla duyurulmuş ama henüz açılmamış.

## Sayım ilkesi
HTTP 200 veya tarayıcıda açılması tek başına reader sayılmaz. HTML detail okuyucusu ilan metni + tarih bulmalı. HTML index okuyucusu doğrudan personel/iş bağlantısı çıkarmalı. Kura/sınav sonuçları ayrı process stream olarak sınıflandırılır. PDF, dinamik sayfa ve kimlik doğrulama gereken kaynaklar bu PR'da çalışan reader sayılmaz. Son CI canlı probu (23.11.2026, commit 10d1f31): 20 kayıt, 14 probed, 5 çalışan okuyucu (3 HTML detay + 2 HTML indeks), 3 açık ilan detay kaynağı. ÇSGB ve Özal indeks okuyucuları eski/tarihî kayıtlar da üretebildiği için açık ilan kaynağı sayısına eklenmez. Zafer ilanı tarayıcıda doğrulandı fakat CI güvenlik/CAPTCHA işareti nedeniyle okuyucusu durduruldu; bu nedenle otomatik kaynak sayısına dahil edilmez. Gerçek workingReaderCount her çalıştırmada CI artifact'inden alınır.

## Bu PR'ın kapsamı
20 kaynaklık envanter, ücretsiz Python probe, kurum+pozisyon+il SHA-256 parmak izi, açık/henüz açılmamış/geçmiş tarih ayrımı ve çevrimdışı regresyon testleri. Server-side JSON collector, PDF reader, dinamik Kariyer Kapısı/İŞKUR API ve CV eşleştirmesi sonraki ayrı işlerdir; bu PR bunları tamamlamış gibi göstermez.
