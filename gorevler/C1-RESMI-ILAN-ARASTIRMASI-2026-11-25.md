# C1 kamu ilanı resmi kaynak araştırması — 25.11.2026

## Amaç
C1 kamu tarafındaki 55 doğrulanmış açık ilanı 100'e yaklaştırmak için yeni resmî kaynakları araştırmak. Bu belge kaynak doğrulamasıdır; henüz Worker'a yazılmış/QA sayımına eklenmiş ilan değildir.

## Ölçüm kuralları
- 25.11.2026 itibarıyla başvuru açık olanlar ayrı, gelecekte açılacaklar ayrı tutulur.
- Bir duyurudaki kontenjan sayısı, otomatik olarak o kadar ilan kaydı demek değildir. Her pozisyon/ilan kartı feed'de ayrı ve kanıtlı temsil edilmedikçe toplam ilan sayısına eklenmez.
- Aynı kurum ve aynı ilan H2/e-Şube içinde zaten varsa ikinci kez sayılmaz.
- Başvuru Kariyer Kapısı üzerinden ise uygulama kullanıcıyı resmî kuruma/Kariyer Kapısı'na yönlendirir; Kariyer Kapısı kazınmaz.
- publishedAt sadece kurum sayfasındaki ilan tarihi; yoksa null. deadline resmi duyurudaki son tarih; collectedAt ve firstSeenAt entegrasyon anında üretilir, geçmişe dönük uydurulmaz.

## 25 Kasım itibarıyla açık olan resmî duyurular

| Kurum | Pozisyon / kontenjan | publishedAt | Başvuru dönemi / deadline | Başvuru | Feed durumu |
|---|---:|---|---|---|---|
| Sayıştay Başkanlığı | 3 çözümleyici + 9 programcı lisans + 4 programcı ön lisans = 16 | 2026-11-10 | 2026-11-11–2026-11-25 23:59:59 | e-Devlet/Kariyer Kapısı | Yeni kaynak adayı; deadline bugün, hemen ekleme ve exact duplicate kontrolü |
| Zafer Kalkınma Ajansı | 2 uzman | 2026-11-03 (sayfa tarihi; metin sonunda 04.11.2026 ibaresi de var) | 2026-11-25–2026-12-14 | Kariyer Kapısı | H2'de Zafer zaten var; mevcut kayıtla eşleşmeden ekleme |
| TGA | 13 pozisyon | 2026-11-03 Resmî Gazete | 2026-11-03–2026-12-04 | TGA kariyer sayfası | H2'de TGA zaten var; mevcut kayıtla eşleşmeden ekleme |
| Hazine ve Maliye Bakanlığı / Hazine Kontrolörleri Kurulu | En fazla 10 stajyer kontrolör | 2026-11-13 | 2026-11-23–2026-12-04 | e-Devlet/Kariyer Kapısı | Yeni kaynak adayı |
| Anadolu Üniversitesi | 1 sözleşmeli personel | 2026-11-13 | 2026-11-16–2026-11-30 | e-Devlet/Kariyer Kapısı | H2'de Anadolu zaten var; mevcut kayıtla eşleşmeden ekleme |
| Bartın Üniversitesi | 1 sözleşmeli personel | 2026-11-20 | 2026-11-20–2026-12-05 13:00 | e-Devlet/Kariyer Kapısı; başvuru bilgileri belgesi 08.12.2026 17:30'a kadar elden/vekaletle teslim | Yeni kaynak adayı |
| Cumhurbaşkanlığı Savunma Sanayii Başkanlığı | 26 proje bazlı sözleşmeli personel | 2026-11-12 | 2026-11-20 10:00–2026-12-21 23:59 | e-Devlet/Kariyer Kapısı | Yeni kaynak adayı; 5 yıl deneyim + İngilizce şartı var |

### Kaynak kanıtları
1. Sayıştay — https://sayistay.gov.tr/duyuru/818-sozlesmeli-cozumleyici-ve-sozlesmeli-prog
   - Resmî duyuru 10.11.2026; 16 pozisyon; son başvuru 25.11.2026 23:59:59.
   - Başvuru e-Devlet/Kariyer Kapısı. Kaynakta ilan edilen şartlar: lisans/ön lisans ayrımı, 2026 KPSS P1/P93 en az 60; çözümleyici için ayrıca programlama ve YDS şartları.
2. Zafer Kalkınma Ajansı — https://zafer.gov.tr/duyurular/227
   - Sayfa tarihi 03.11.2026; iki uzman; başvuru 25.11–14.12.2026; TR33 bölgesi Afyonkarahisar/Kütahya/Manisa/Uşak; Kariyer Kapısı.
3. TGA — https://www.resmigazete.gov.tr/ilanlar/eskiilanlar/2026/11/20261103-4-12.pdf
   - Resmî Gazete 03.11.2026; 13 pozisyon; son başvuru 04.12.2026; TGA kariyer sayfası. TGA'da kamu personeli statüsü olmadığı ilan metninde belirtiliyor; kategori sınıflandırması buna göre kontrol edilmeli.
4. Hazine ve Maliye Bakanlığı — https://ms.hmb.gov.tr/uploads/2026/11/Ilan-Metni-Internet-Sitesi-491a08379759c79c.pdf
   - En fazla 10 stajyer Hazine Kontrolörü; başvuru 23.11–04.12.2026; KPSS puan türleri 17/18/22/23/28/47/48'den en az 80; ilgili lisans bölümleri ve yaş şartı.
5. Anadolu Üniversitesi — https://www.anadolu.edu.tr/duyurular/sozlesmeli-personel-alimi-ilani-1794568099
   - Sayfa tarihi 13.11.2026; bir sözleşmeli personel; 16.11–30.11.2026; 4/B ve KPSS B grubu puan sırası; e-Devlet/Kariyer Kapısı.
6. Bartın Üniversitesi — https://dinkulturu.bartin.edu.tr/duyurular/sozlesmelipersonelalimilani202604.html
   - Sayfa tarihi 20.11.2026; bir sözleşmeli personel; 05.12.2026 saat 13:00 son başvuru; Kariyer Kapısı; aday başvuru bilgileri belgesinin 08.12.2026 17:30'a kadar teslimi gerekiyor.
7. Savunma Sanayii Başkanlığı — https://www.ssb.gov.tr/storage/img/proje-bazli-sozlesmeli-personel-alimi-giris-sinavi-ilan-metni.pdf
   - Resmî duyuru 12.11.2026; 26 pozisyon; 20.11.2026 10:00–21.12.2026 23:59; 4 yıllık lisans, son 2 yılda YDS İngilizce 70/eşdeğer, ilgili ihtisas alanında en az 5 yıl tecrübe; Kariyer Kapısı.

## Başvurusu henüz açılmayanlar — açık ilan sayımına eklenmez
| Kurum | Pozisyon | publishedAt | Başvuru dönemi | Durum |
|---|---:|---|---|---|
| Adli Tıp Kurumu | 181 | 2026-11-20 | 2026-11-30 09:00–2026-12-06 23:59 | Gelecek; 30 Kasım'dan önce açık ilan sayılmaz |
| Yükseköğretim Kalite Kurulu | 5 | 2026-11-13 | 2026-12-01–2026-12-14 23:59:59 | Gelecek; 1 Aralık'tan önce açık ilan sayılmaz |

- Adli Tıp resmî duyurusu: https://atk.gov.tr/haber20.11.26.html
- YÖKAK resmî duyurusu: https://www.yokak.gov.tr/2026/11/13/yuksekogretim-kalite-kurulu-uzman-yardimcisi-alim-ilani-3/

## Bilinçli dışlamalar
- DİKA için 14.11.2026 Resmî Gazete metninde başvuru yöntemi ve 4 uzman bilgisi görüldü; fakat bu denetimde son başvuru tarihini güvenilir biçimde teyit edemedim. Açık ilan sayısına eklenmedi.
- SSB duyuru sayfası web aramasında bulundu ancak sayfa açılışı 403 verdi; resmî PDF açıldı. Kaynak kartı için doğrudan PDF kullanılabilir; 403 durumunu kaynak bazlı stale/erişim hatası olarak izlemek gerekir.
- Üçüncü taraf iş ilanı listeleri yalnızca keşif için kullanıldı; doğrulama/ilan sayımında kaynak kabul edilmedi.
- İŞKUR ilan detayları ve Kariyer Kapısı ilan sayfaları kazınmadı.

## Sayım kararı
- Mevcut doğrulanmış kamu sayımı: 12 kaynak /55 ilan. Bu araştırma sonrası henüz sayıya eklenen ilan: 0; çünkü H2/e-Şube ile exact dedup ve Worker feed'e yazma yapılmadı.
- Yeni kaynak adayı: Sayıştay, Hazine ve Maliye Bakanlığı, Bartın Üniversitesi, SSB = 4.
- Mevcut H2 kaynaklarıyla çakışma adayı: Zafer, TGA, Anadolu = 3; duplicate check zorunlu.
- Gelecek başvurular: ATK 181 ve YÖKAK 5; bugünkü açık sayımına dahil değil.
- Pozisyon kontenjanı toplamı ilan kartı sayısı değildir. 16+2+13+10+1+1+26 gibi sayılarla 100 ilan eşiği geçildiği iddia edilemez.
- KARAR: KAYNAK ADAYLARI DOĞRULANDI; FEED SAYIMI DEĞİŞMEDİ / C1 RED.

## Sonraki teknik adım
1. Yeni 4 resmî kaynağı source catalog'a eklemek için ayrı C1 PR görevi aç.
2. Mevcut H2 kaydı olan Zafer/TGA/Anadolu için stable ID + official URL ile exact duplicate kontrolü yap.
3. ATK ve YÖKAK için başvuru açılışında zamanlanmış değil, günlük Actions taramasıyla yeni veri yakala; açılmadan verifiedListingCount artırma.
4. SSB 403'ü yalnız bu kaynağa stale yaz; diğer kaynakların last-good verisini bozma.
5. Worker authenticated GET/POST ve D1 kanıtı olmadan PUBLISH tamamlandı sayılmaz.
