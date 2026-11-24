# KARIYER KAYNAK DENETİMİ — K1-C

Tarih: 24.11.2026  
Son ölçüm: [Actions #45429536647](https://github.com/aries919-bot/Jarvis/actions/runs/45429536647)  
Ölçüm zamanı: 2026-11-24T15:47:22Z  
Ölçüm commit'i: 6309a8208ef814b7137366c172d890c8c2311e45  
User-Agent: AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)

## Özet — son ölçüm yeniden sayımı

- Hedef: **20 benzersiz URL**.
- Mod: **9 aktif ölçüm / 8 geçici kapalı / 3 yalnız yönlendirme**.
- Genel durum: **5 ÇALIŞIYOR / 15 ENGELLİ / 0 ÖLÜ**. ENGELLİ olan 15 kaydın 8'i geçici kapalı, 3'ü politika gereği yönlendirme, 4'ü aktif ölçümde engelli (2 robots UNKNOWN, 2 HTTP 403).
- Kapatılan 8 TLS/timeout adresine ve 3 yönlendirme adresine bu Actions çalışmasında **hiç ağ isteği gönderilmedi**; deneme sayısı 0.
- TLS doğrulaması varsayılan Python SSL context ile açık kaldı. verify=False, -k, sertifika denetimini kapatma veya robots aşma yok.
- Kamu için doğrulanmış gerçek iş ilanı kaynağı: **1 (e-Şube) / 43 doğrulanmış ilan**. 10 kaynak / 100 ilan eşiği sağlanmadı; C1 toplam hedefi **RED**.
- 20 URL'nin erişilebilir olması gerçek ilan sayısı anlamına gelmez. YHGM kura/sonuçtur; BOTAŞ 2025 başvurusu süresi geçmiştir; Turkcell ve Koç ölçümünde tarihli ilan sinyali bulunmamıştır.

## Kaynak bazında sayım — 20 satır

| # | sourceId | Kategori | Son durum | Güncel ölçüm / yorum |
|---:|---|---|---|---|
| 1 | esube-public | Kamu | ÇALIŞIYOR | Robots izinli, HTTP 200. K1-E1: 3 sayfa, 43 benzersiz kamu ilanı, 8 kurum. |
| 2 | ilan-www | Kamu | KAPALI_GEÇİCİ | TLS issuer doğrulaması başarısız; robots bilinmiyor; yeni istek yok. |
| 3 | resmigazete-ilanlar | Kamu | KAPALI_GEÇİCİ | Robots.txt timeout, izin UNKNOWN; hedef sayfaya istek yok. |
| 4 | yhgm-kura | Kura | ÇALIŞIYOR | Robots izinli, HTTP 200. 2.764 sürekli işçi kura/sonuç duyurusu; yeni iş değildir. |
| 5 | tuseb-duyurular | Kura | KAPALI_GEÇİCİ | TLS issuer doğrulaması başarısız; yeni istek yok. |
| 6 | tuseb-ik | Kura | KAPALI_GEÇİCİ | TLS issuer doğrulaması başarısız; yeni istek yok. |
| 7 | osym-kpss | KPSS | ENGELLİ | Robots UNKNOWN; sayfa isteği gönderilmedi. |
| 8 | osym-duyurular | KPSS | ENGELLİ | Robots UNKNOWN; sayfa isteği gönderilmedi. |
| 9 | kariyerkapisi | KPSS | YALNIZ_YÖNLENDİRME | İçerik kazınmaz/yoklanmaz; resmî Custom Tabs. |
| 10 | msvk-kariyerkapisi | KPSS'siz adayı | YALNIZ_YÖNLENDİRME | e-Devlet içeriği kazınmaz/yoklanmaz; resmî Custom Tabs. |
| 11 | izbb-kariyerkapisi | KPSS'siz adayı | YALNIZ_YÖNLENDİRME | e-Devlet içeriği kazınmaz/yoklanmaz; resmî Custom Tabs. |
| 12 | turkcell-kariyer | Büyük özel şirket | ÇALIŞIYOR | Robots izinli, HTTP 200; tarihli ilan sinyali yok; ilan adedi 0 doğrulandı. |
| 13 | sisecam-kariyer | Büyük özel şirket | ENGELLİ | Robots izinli, HTTP 403; engel aşılmadı. |
| 14 | sisecam-jobs | Büyük özel şirket | ENGELLİ | Robots izinli, HTTP 403; engel aşılmadı. |
| 15 | koc-kariyerim | Büyük özel şirket | ÇALIŞIYOR | Robots izinli, HTTP 200; tarihli ilan sinyali yok; ilan adedi 0 doğrulandı. |
| 16 | ptt-duyurular | Kamu bağlı büyük şirket | KAPALI_GEÇİCİ | TLS issuer doğrulaması başarısız; yeni istek yok. |
| 17 | botas-duyurular | Kamu bağlı büyük şirket | ÇALIŞIYOR | Robots izinli, HTTP 200; içerikteki 2025 başvuru süresi geçmiş. |
| 18 | tpao-personel-pdf | Kamu bağlı büyük şirket | KAPALI_GEÇİCİ | Robots.txt timeout, izin UNKNOWN; PDF hedefine istek yok. |
| 19 | ilan-apex-extra | Kamu | KAPALI_GEÇİCİ | TLS issuer doğrulaması başarısız; robots bilinmiyor; yeni istek yok. |
| 20 | resmigazete-root-extra | Kamu | KAPALI_GEÇİCİ | Robots.txt timeout, izin UNKNOWN; hedef sayfaya istek yok. |

## TLS / timeout kapalı listesi

### TLS — 5 URL
1. https://www.ilan.gov.tr/
2. https://www.tuseb.gov.tr/public/tr/duyurular
3. https://www.tuseb.gov.tr/kurumsal/insan-kaynaklari
4. https://www.ptt.gov.tr/duyurular
5. https://ilan.gov.tr/

Hatanın metni CERTIFICATE_VERIFY_FAILED: unable to get local issuer certificate. Bu, sertifikanın süresinin dolduğunu kanıtlamaz; issuer/CA güven zinciri doğrulaması başarısızdır. SSL kontrolünü kapatmak yerine bu 5 adres geçici kapalı tutuldu.

### Timeout — 3 URL
1. https://www.resmigazete.gov.tr/ilanlar
2. https://www.tpao.gov.tr/file/2512/sinav-ilani-33-personel-alimi-29-12-2025-37736953d2e834d07.pdf
3. https://www.resmigazete.gov.tr/

Son ölçümde robots.txt timeout/UNKNOWN olduğundan hedef URL'lere GET gönderilmedi. Tekrar deneme yapılmaması için manifestte TEMPORARILY_CLOSED işaretlendi.

### Yalnız yönlendirme — 3 URL
- https://kariyerkapisi.gov.tr/isealim
- https://www.turkiye.gov.tr/msvk-kariyer-kapisi-kamu-ise-alim
- https://www.turkiye.gov.tr/bb-kariyer-kapisi-kamu-ise-alim

Bu üç adres hiçbir otomatik ölçüm/okuma adımında istenmez. Kullanıcı Android Custom Tabs üzerinden resmî sayfaya gider ve varsa girişini kendisi yapar.

## Tarih ve içerik sınıflandırması — H2 girdileri

- **YHGM 2.764 sürekli işçi alım kurası:** resmî sayfada 12.11.2025 tarihli duyuru ve önceki 11.11.2025 duyurusu yer alır. Başvuru dönemi 08.10.2025–13.10.2025 olarak belirtilmiştir. Kayıt category=Kura, contentType=lottery, publishedAt=2025-11-12, deadline=2025-10-13, freshnessStatus=Süresi geçti; iş ilanı sayacına girmez. [Resmî YHGM sayfası](https://yhgm.saglik.gov.tr/TR-111525/tasra-teskilati-hizmet-birimlerinde-istihdam-edilmek-uzere-2764-surekli-isci-alim-kurasi-hakkinda-duyuru.html)
- **BOTAŞ 223 kişilik personel alımı:** detay sayfasında Tarih: 5/11/2025; ekli resmî PDF, İŞKUR ilanlarının 10.11.2025–14.11.2025 aralığında yayımlanacağını ve pozisyona göre KPSS puan şartı bulunduğunu yazar. category=Kamu bağlı büyük şirket, contentType=job, publishedAt=2025-11-05, deadline=2025-11-14, freshnessStatus=Süresi geçti; güncel işe alım sayılmaz. Başvuru yöntemi geçmişte İŞKUR idi; İŞKUR ayrıntılı ilan sayfası kazınmaz. [Resmî duyuru](https://www.botas.gov.tr/Icerik/223-kisilik-personel-alim-duyu/1203) · [Resmî PDF](https://www.botas.gov.tr/uploads/dosyaYoneticisi/559215-lan-tablo.pdf)
- **BOTAŞ müfettiş yardımcılığı sonuç duyurusu:** detay sayfasında Tarih: 21/1/2026 bulunur. Başlıktaki 10/14 Kasım 2025 tarihleri sınav/alım sürecine ilişkindir, publishedAt yerine kullanılamaz. contentType=result; yeni iş değildir, başvuru açık değildir. [Resmî sonuç duyurusu](https://www.botas.gov.tr/Icerik/10-kasim-2025-14-kasim-2025/1233)
- publishedAt yalnız açık kaynak tarihinden alınır; sayfa çekim zamanı değildir. deadline yalnız başvuru aralığının bitişidir. collectedAt ve firstSeenAt H2 okuyucu çalışınca üretilir; geçmişe dönük uydurulmaz. Kura/sınav tarihleri yayın tarihi sayılmaz.

## Kamu ve genel eşik

- Kamuya ait gerçek, doğrulanmış ve başvurusu halen açık ilan: **1 kaynak / 43 kayıt** (e-Şube).
- H1 ölçümündeki YHGM bir kura/sonuç haberi; BOTAŞ ilanı 2025'te kapanmış; bunlar yeni ve açık kamu iş ilanı sayısına eklenmez.
- Hedef **10 kaynak / 100 doğrulanmış ilan**. Gerçek sayı 1/43; hedef sağlanmadı. C1 toplam sonucu **RED**, arayüz entegrasyonuna geçilmez.

## Ölçüm sınırı ve güvenlik

Bu ölçüm erişim/robots/HTTP seviyesindedir; ilan satır sayısı yalnız e-Şube K1-E1 okuyucusundan doğrulanmıştır. Başka kaynaklar için 200 yanıt tek başına canlı ilan kanıtı değildir. 403 aşılmaz; robots UNKNOWN ise sayfaya istek yok; giriş/CAPTCHA yok; LinkedIn/Indeed yok; browser UA yok; SSL kontrolü kapatılmaz.
