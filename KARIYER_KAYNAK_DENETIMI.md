# KARIYER KAYNAK DENETİMİ — K1-C

Tarih: 24.11.2026  
Son ölçüm: [K1-B Actions #45482883679](https://github.com/aries919-bot/Jarvis/actions/runs/45482883679)  
Ölçüm zamanı: 2026-11-24T20:38:24Z  
Ölçüm commit'i: ece04d4e672234f15a7d1677828a2831fd0d59bd  
User-Agent: AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)

## Özet — son ölçüm yeniden sayımı

- Hedef: **28 benzersiz URL**.
- Ölçüm modu: **17 aktif ölçüm / 8 geçici kapalı / 3 yalnız yönlendirme**.
- Erişim sonucu: **11 ÇALIŞIYOR / 17 ENGELLİ / 0 ÖLÜ**.
- 17 ENGELLİ kaydın alt ayrımı: 6 aktif ölçümde engelli (4xx/ağ hatası/tarihli duyuru sinyali bulunmaması/robots belirsizliği), 8 geçici kapalı, 3 politika gereği yalnız yönlendirme.
- 8 geçici kapalı ve 3 yalnız yönlendirme hedef adresine bu koşuda GET gönderilmedi.
- Aktif ölçümdeki 17 URL'nin 11'i HTTP 2xx + izinli robots + boş olmayan içerik + sınıflandırıcı şartlarını karşıladı.
- TLS doğrulaması varsayılan Python SSL context ile açık kaldı. verify=False, -k, sertifika denetimini kapatma veya robots aşma yok.
- K1-E1 kamu doğrulaması: **1 kaynak / 43 e-Şube kaydı**. H2 ek okuyucu daha sonra 3 açık kamu duyurusu doğruladı; aşağıdaki son ek bölümünde ayrı gösterilir.
- Bu rapordaki ÇALIŞIYOR yalnız erişim ve tarihli duyuru sinyali sınıflamasıdır; gerçek açık ilan / ilan sayısı kanıtı değildir.
- 10 kaynak / 100 doğrulanmış gerçek ilan eşiği sağlanmadı; bütüncül karar **RED**.

## Kaynak bazında sayım — 28 satır

| # | sourceId | Kategori | Son durum | Güncel ölçüm / yorum |
|---:|---|---|---|---|
| 1 | esube-public | Kamu | ÇALIŞIYOR | Robots HTTP 200, sayfa HTTP 200, 79.088 bayt. K1-E1: 3 sayfa / 43 benzersiz kamu kaydı / 8 kurum. |
| 2 | ilan-www | Kamu | KAPALI_GEÇİCİ | Önceki TLS issuer doğrulaması başarısız; robots izni doğrulanmadı; tekrar istek yok. |
| 3 | resmigazete-ilanlar | Kamu | KAPALI_GEÇİCİ | robots.txt timeout / UNKNOWN; hedef sayfa istenmedi. |
| 4 | yhgm-kura | Kura | ÇALIŞIYOR | Robots HTTP 200, sayfa HTTP 200, 89.063 bayt. 2.764 sürekli işçi kura/sonuç duyurusu; yeni iş değildir. |
| 5 | tuseb-duyurular | Kura | KAPALI_GEÇİCİ | TLS issuer doğrulaması başarısız; tekrar istek yok. |
| 6 | tuseb-ik | Kura | KAPALI_GEÇİCİ | TLS issuer doğrulaması başarısız; tekrar istek yok. |
| 7 | osym-kpss | KPSS | ENGELLİ | robots.txt 512 KiB sınırını aştı; sayfa istenmedi. |
| 8 | osym-duyurular | KPSS | ENGELLİ | robots.txt 512 KiB sınırını aştı; sayfa istenmedi. |
| 9 | kariyerkapisi | KPSS | YALNIZ_YÖNLENDİRME | İçerik kazınmaz/yoklanmaz; kullanıcı Custom Tabs ile resmî giriş sayfasına gider. |
| 10 | msvk-kariyerkapisi | KPSS'siz adayı | YALNIZ_YÖNLENDİRME | e-Devlet içeriği kazınmaz/yoklanmaz; yalnız Custom Tabs yönlendirmesi. |
| 11 | izbb-kariyerkapisi | KPSS'siz adayı | YALNIZ_YÖNLENDİRME | e-Devlet içeriği kazınmaz/yoklanmaz; yalnız Custom Tabs yönlendirmesi. |
| 12 | turkcell-kariyer | Büyük özel şirket | ÇALIŞIYOR | Robots 200, HTTP 200, 132.484 bayt; tarihli duyuru sinyali yok. |
| 13 | sisecam-kariyer | Büyük özel şirket | ENGELLİ | Robots 200, HTTP 403; engel aşılmadı. |
| 14 | sisecam-jobs | Büyük özel şirket | ENGELLİ | Robots 200, HTTP 403; engel aşılmadı. |
| 15 | koc-kariyerim | Büyük özel şirket | ÇALIŞIYOR | Robots 200, HTTP 200, 707.465 bayt; tarihli duyuru sinyali yok. |
| 16 | ptt-duyurular | Kamu bağlı büyük şirket | KAPALI_GEÇİCİ | TLS issuer doğrulaması başarısız; tekrar istek yok. |
| 17 | botas-duyurular | Kamu bağlı büyük şirket | ÇALIŞIYOR | Robots 200, HTTP 200, 55.753 bayt; görülen BOTAŞ 2025 başvurusu süresi geçmiş. |
| 18 | tpao-personel-pdf | Kamu bağlı büyük şirket | KAPALI_GEÇİCİ | robots.txt timeout / UNKNOWN; PDF hedefine istek yok. |
| 19 | ilan-apex-extra | Kamu | KAPALI_GEÇİCİ | Önceki TLS issuer doğrulaması başarısız; tekrar istek yok. |
| 20 | resmigazete-root-extra | Kamu | KAPALI_GEÇİCİ | robots.txt timeout / UNKNOWN; hedef sayfa istenmedi. |
| 21 | atk-duyuru-2026-2 | Kamu | ÇALIŞIYOR | Robots dosyası HTTP 404 (yok), HTTPS HTTP 200, 8.598 bayt; 2026-2 personel duyurusu. |
| 22 | atk-ek1-2026-2 | Kamu | ÇALIŞIYOR | Robots dosyası HTTP 404 (yok), HTTPS HTTP 200, 217.209 bayt; aynı kurumun pozisyon PDF'si. |
| 23 | anadolu-sozlesmeli-2026 | Kamu | ÇALIŞIYOR | Robots 200, HTTP 200, 36.462 bayt; tarihli 4/B personel duyurusu. |
| 24 | anadolu-sozlesmeli-pdf-2026 | Kamu | ENGELLİ | Robots 200; bağlantı sıfırlandı (NETWORK_ERROR); H2 PDF içeriği istemedi, HTML sayfasından genel kanıtı kullandı. |
| 25 | turkak-guvenlik-2026 | Kamu | ENGELLİ | Robots 200, HTTP 200, 12.283 bayt; K1-B sınıflandırıcısında tarihli duyuru sinyali HAYIR, otomatik H2 kaynağına alınmadı. |
| 26 | duzce-ogretim-elemani-2026 | Kamu | ÇALIŞIYOR | Robots dosyası HTTP 404 (yok), PDF HTTP 200, 434.992 bayt; 23.11.2026 tarihli öğretim elemanı duyurusu. |
| 27 | tarsus-ogretim-elemani-2026 | Kamu | ÇALIŞIYOR | Robots 200, HTTP 200, 26.000 bayt; öğretim elemanı duyuru sayfası. |
| 28 | tarsus-ogretim-elemani-pdf-2026 | Kamu | ÇALIŞIYOR | Robots 200, HTTP 200, 771.374 bayt; aynı kurumun resmî PDF eki. |

## Durum toplamları — tekrar sayım

- ÇALIŞIYOR: **11**
- ENGELLİ (aktif ölçümde 6 + geçici kapalı 8 + yalnız yönlendirme 3): **17**
- ÖLÜ: **0**
- Toplam benzersiz URL: **28**
- Aktif ölçüm modu: 17; geçici kapalı: 8; redirect only: 3.
- Aktif ölçümdeki 17 URL içinde 11 erişim/tarih sinyali şartını karşıladı, 6 engelli sınıflandırıldı.

## TLS / robots timeout geçici kapalı listesi

### TLS — 5 URL
1. https://www.ilan.gov.tr/
2. https://www.tuseb.gov.tr/public/tr/duyurular
3. https://www.tuseb.gov.tr/kurumsal/insan-kaynaklari
4. https://www.ptt.gov.tr/duyurular
5. https://ilan.gov.tr/

Hatanın metni CERTIFICATE_VERIFY_FAILED: unable to get local issuer certificate. Bu, sertifikanın süresinin dolduğunu kanıtlamaz; issuer/CA güven zinciri doğrulaması başarısızdır. SSL kontrolünü kapatmak yerine bu 5 adres geçici kapalı tutuldu.

### robots timeout — 3 URL
1. https://www.resmigazete.gov.tr/ilanlar
2. https://www.tpao.gov.tr/file/2512/sinav-ilani-33-personel-alimi-29-12-2025-37736953d2e834d07.pdf
3. https://www.resmigazete.gov.tr/

robots.txt timeout/UNKNOWN olduğundan hedef URL'lere GET gönderilmedi. Tekrar deneme yapılmaması için manifestte TEMPORARILY_CLOSED işaretlendi.

### Yalnız yönlendirme — 3 URL
- https://kariyerkapisi.gov.tr/isealim
- https://www.turkiye.gov.tr/msvk-kariyer-kapisi-kamu-ise-alim
- https://www.turkiye.gov.tr/bb-kariyer-kapisi-kamu-ise-alim

Bu üç adres hiçbir otomatik ölçüm/okuma adımında istenmez. Kullanıcı Android Custom Tabs üzerinden resmî sayfaya gider ve varsa girişini kendisi yapar.

## Tarih ve içerik sınıflandırması — H2 girdileri

- **YHGM 2.764 sürekli işçi alım kurası:** resmî sayfada 12.11.2025 tarihli duyuru ve önceki 11.11.2025 duyurusu yer alır. Başvuru dönemi 08.10.2025–13.10.2025 olarak belirtilmiştir. Kayıt category=Kura, contentType=lottery, publishedAt=2025-11-12, deadline=2025-10-13, freshnessStatus=Süresi geçti; iş ilanı sayacına girmez. [Resmî YHGM sayfası](https://yhgm.saglik.gov.tr/TR-111525/tasra-teskilati-hizmet-birimlerinde-istihdam-edilmek-uzere-2764-surekli-isci-alim-kurasi-hakkinda-duyuru.html)
- **BOTAŞ 223 kişilik personel alımı:** detay sayfasında Tarih: 5/11/2025; ekli resmî PDF, İŞKUR ilanlarının 10.11.2025–14.11.2025 aralığında yayımlanacağını ve pozisyona göre KPSS puan şartı bulunduğunu yazar. category=Kamu bağlı büyük şirket, contentType=job, publishedAt=2025-11-05, deadline=2025-11-14, freshnessStatus=Süresi geçti; güncel işe alım sayılmaz. Başvuru yöntemi geçmişte İŞKUR idi; İŞKUR ayrıntılı ilan sayfası kazınmaz. [Resmî duyuru](https://www.botas.gov.tr/Icerik/223-kisilik-personel-alim-duyu/1203) · [Resmî PDF](https://www.botas.gov.tr/uploads/dosyaYoneticisi/559215-lan-tablo.pdf)
- **BOTAŞ müfettiş yardımcılığı sonuç duyurusu:** detay sayfasında Tarih: 21/1/2026 bulunur. Başlıktaki 10/14 Kasım 2025 tarihleri sınav/alım sürecine ilişkindir, publishedAt yerine kullanılamaz. contentType=result; yeni iş değildir, başvuru açık değildir. [Resmî sonuç duyurusu](https://www.botas.gov.tr/Icerik/10-kasim-2025-14-kasim-2025/1233)
- **Adli Tıp Kurumu 2026-2 personel alımı:** resmî duyuru tarihi 20.11.2026; toplam 181 pozisyon; başvuru 30.11.2026 09:00'da açılır ve 06.12.2026 23:59'da biter. Bu nedenle 24.11.2026 ölçümünde applicationOpensAt gelecekte olduğundan freshnessStatus=Doğrulanamadı; henüz açık başvuru sayılmaz. P94 ortaöğretim tablosunda 26 hizmetli +10 şoför +1 infaz-koruma =37 pozisyon bulunur. Ek-1 PDF kaynağı ayrıca ölçülmüştür.
- **Anadolu Üniversitesi 4/B personel duyurusu:** sayfada Eklenme Tarihi 13.11.2026; 1 sözleşmeli personel; deadline 30.11.2026; KPSS(B) puan sıralaması. PDF URL K1-B'de ağ hatası verdiği için H2 okuyucu PDF içeriğini almamış; şartlar yalnız HTML'deki genel bilgi seviyesinde tutulmuştur.
- **Düzce Üniversitesi öğretim elemanı duyurusu:** PDF başlığı 23.11.2026 tarihli; 5 akademik kadro; deadline 07.12.2026; ALES ve kadroya özgü akademik şartlar.
- **Tarsus Üniversitesi öğretim elemanı duyurusu:** Resmî Gazete tarihi 23.11.2026; 3 öğretim görevlisi kadrosu; deadline 07.12.2026 mesai bitimi; ALES ve kadroya özgü şartlar.
- publishedAt yalnız açık kaynak tarihinden alınır; sayfa çekim zamanı değildir. deadline yalnız başvuru aralığının bitişidir. collectedAt ve firstSeenAt H2 okuyucu çalışınca üretilir; geçmişe dönük uydurulmaz. Kura/sınav tarihleri yayın tarihi sayılmaz.

## Kamu ve genel eşik

- C1 Worker feed'ine şu an bağlı kamu kaynağı: **1 e-Şube / 43 kayıt**.
- Ayrı H2 okuyucusunun canlı kanıtı: 3 güncel başvurusu açık kamu kaynağından 3 doğrulanmış iş/personel duyurusu (Anadolu, Düzce, Tarsus); 1 gelecek başvuru (ATK, henüz açık değil); 1 geçmiş kura (YHGM); BOTAŞ stale.
- Birleşik resmî kamu kanıtı, iki JSON hattı henüz tek feed'e bağlanmamış olarak: **4 aktif kaynak / 46 doğrulanmış açık kart** (e-Şube 43 + 3 H2 kartı). Kontenjan adetleri kart/ilan adedi diye çoğaltılmadı.
- H2 reader toplamı: 6 kaynak, 5 fresh /1 stale, 5 kart; aynı run 32/32 test PASS.
- Özel ATS tarafında 112 Türkiye adayı bulundu ama tümünde deadline unknown; doğrulanmış güncel ilan sayısına girmez.
- Mevcut birleşik kanıt: **4 kaynak / 46 doğrulanmış açık kayıt**. Hedef **10 kaynak / 100 doğrulanmış ilan** sağlanmadı; genel sonuç **RED**.

## Ölçüm kanıtı ve güvenlik

- K1-B run [#45482883679](https://github.com/aries919-bot/Jarvis/actions/runs/45482883679), job `138760408277`, commit `ece04d4e672234f15a7d1677828a2831fd0d59bd`: 28/28 satır loglandı; sonuç 11/17/0; özel testler 16/16; artefakt [#14963011109](https://github.com/aries919-bot/Jarvis/actions/runs/45482883679/artifacts/14963011109).
- K1-C raporu 28 ölçülmüş URL'nin tamamını içerir; ölçümün tam zamanı 19:47:42Z.
- robots unknown/disallow, login/CAPTCHA ve geçici kapalı hedeflere yeni HTTP istek yok; TLS doğrulaması açık.
- ÇALIŞIYOR erişilebilir + tarihli içerik sinyalini gösterir; gerçek ilan sayısı/başvuru süresi H2 ve kaynak detayında ayrıca doğrulanır.