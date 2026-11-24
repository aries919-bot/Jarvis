# KARIYER KAYNAK DENETİMİ — K1-C

Tarih: 24.11.2026
Kaynak ölçümü: [K1-B Actions #45405558813](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813)
Makine raporu: [artifact #14926070923](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813/artifacts/14926070923)
Ölçüm commit'i: b1e6c1b808ea0901595d80983791ce68cc848d75
User-Agent: AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)

## Özet

- Yeniden sayım: 20 benzersiz URL.
- Sonuç: **8 ÇALIŞIYOR / 12 ENGELLİ / 0 ÖLÜ**.
- Tanım: ÇALIŞIYOR = robots izni + TLS doğrulaması açık + HTTP 2xx. Bu yalnız erişilebilirliği gösterir; gerçek ilan bulunduğu veya içerik okuyucusunun hazır olduğu anlamına gelmez.
- ENGELLİ kaynaklar ölü ilan edilmedi. 403, TLS issuer, robots belirsizliği ve timeout ayrı nedenler olarak tutulur.
- En az bir kamu kaynağı ÇALIŞIYOR: e-Şube.
- Gerçek ve doğrulanmış kamu iş ilanı sağlayıcısı: **1 kaynak (e-Şube) / 43 doğrulanmış kayıt**. 10 kaynak / 100 ilan hedefi sağlanmadı; C1 RED.
- Kariyer Kapısı ve kimlik doğrulama isteyen e-Devlet yüzeyleri kazınmaz; yalnız resmî sayfaya yönlendirme / Custom Tabs. e-Şube dışında bu denetimdeki erişilebilir sayfalar yeni kamu ilanı olarak sayılmaz.
- İlanın son başvuru tarihi geçmişse güncel listede gösterilmez. e-Şube satır tarihi son başvurudur; yayın tarihi olmadığı için publishedAt=null.

## Kaynak bazında yeniden sayım

| # | sourceId | Kategori | URL | Durum | Robots / HTTP / bayt | Yorum |
|---:|---|---|---|---|---|
| 1 | esube-kamu-acik-ilan | Kamu | https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx | ÇALIŞIYOR | İzin var / 200 / 79.115 | K1-E1: 3 sayfa, 43 benzersiz kamu ilanı, 8 kurum. Gerçek kamu ilanı sayımına giren tek kaynak. |
| 2 | ilan-gov-www | Kamu | https://www.ilan.gov.tr/ | ENGELLİ | Robots bilinmiyor / HTTP yok / 0 | TLS_ERROR: CERTIFICATE_VERIFY_FAILED, unable to get local issuer certificate. |
| 3 | resmigazete-ilanlar | Kamu | https://www.resmigazete.gov.tr/ilanlar | ENGELLİ | İzin var / timeout / 0 | Hedef okuma TIMEOUT. Sonraki Actions tekrarları aşağıdaki denetim notunda. |
| 4 | yhgm-kura-2764 | Kura | https://yhgm.saglik.gov.tr/TR-111525/tasra-teskilati-hizmet-birimlerinde-istihdam-edilmek-uzere-2764-surekli-isci-alim-kurasi-hakkinda-duyuru.html | ÇALIŞIYOR | İzin var / 200 / 89.063 | Kura/sonuç haberi; yeni iş ilanı sayılmaz. |
| 5 | tuseb-duyurular | Kura | https://www.tuseb.gov.tr/public/tr/duyurular | ENGELLİ | Robots bilinmiyor / HTTP yok / 0 | TLS issuer doğrulama hatası. |
| 6 | tuseb-insan-kaynaklari | Kura | https://www.tuseb.gov.tr/kurumsal/insan-kaynaklari | ENGELLİ | Robots bilinmiyor / HTTP yok / 0 | TLS issuer doğrulama hatası. |
| 7 | osym-kpss-grup | KPSS | https://osym.gov.tr/SinavGrubu/Menu/338 | ENGELLİ | Robots bilinmiyor / HTTP yok / 0 | robots.txt 512 KiB sınırını aştı; hedef sayfaya istek yapılmadı. |
| 8 | osym-duyurular | KPSS | https://www.osym.gov.tr/Duyurular/Index | ENGELLİ | Robots bilinmiyor / HTTP yok / 0 | robots.txt 512 KiB sınırını aştı; hedef sayfaya istek yapılmadı. |
| 9 | kariyerkapisi-isealim | KPSS | https://kariyerkapisi.gov.tr/isealim | ÇALIŞIYOR | robots HTTP 404 (izinli sayıldı) / 200 / 18.791 | Erişim başarılı, tarihli ilan kanıtı yok; otomatik ilan kaynağı veya ilan sayısı değildir. |
| 10 | msvk-kariyer-kapisi | KPSS'siz adayı | https://www.turkiye.gov.tr/msvk-kariyer-kapisi-kamu-ise-alim | ÇALIŞIYOR | İzin var / 200 / 16.283 | e-Devlet kimlik doğrulaması gerekir; içerik kazınmaz, Custom Tabs. KPSS'siz etiketi kaynakta varsayılmaz. |
| 11 | izmirbb-kariyer-kapisi | KPSS'siz adayı | https://www.turkiye.gov.tr/bb-kariyer-kapisi-kamu-ise-alim | ÇALIŞIYOR | İzin var / 200 / 15.953 | e-Devlet kimlik doğrulaması gerekir; içerik kazınmaz, Custom Tabs. KPSS'siz etiketi kaynakta varsayılmaz. |
| 12 | turkcell-kariyer | Büyük özel | https://www.turkcell.com.tr/hakkimizda/insankaynaklari/kariyer-firsatlari/detay | ÇALIŞIYOR | İzin var / 200 / 132.486 | Erişim başarılı; tarihli ilan sinyali yok. |
| 13 | sisecam-kariyer | Büyük özel | https://www.sisecam.com/tr/kariyer | ENGELLİ | İzin var / 403 / 5.595 | HTTP 403; tekrar denenmeyecek. |
| 14 | sisecam-careers | Büyük özel | https://careers.sisecam.com/?locale=tr_TR | ENGELLİ | İzin var / 403 / 5.623 | HTTP 403; tekrar denenmeyecek. |
| 15 | koc-kariyerim | Büyük özel | https://www.kockariyerim.com/jobAd/list | ÇALIŞIYOR | İzin var / 200 / 707.465 | Erişim başarılı; tarihli ilan sinyali yok. |
| 16 | ptt-duyurular | Kamu bağlı şirket | https://www.ptt.gov.tr/duyurular | ENGELLİ | Robots bilinmiyor / HTTP yok / 0 | TLS issuer doğrulama hatası. |
| 17 | botas-duyurular | Kamu bağlı şirket | https://www.botas.gov.tr/Kategori/duyurular/2 | ÇALIŞIYOR | İzin var / 200 / 55.753 | Erişim başarılı; 2025 personel alımı sonuç duyurusu Süresi geçti. |
| 18 | tpao-personel-pdf | Kamu bağlı şirket | https://www.tpao.gov.tr/file/2512/sinav-ilani-33-personel-alimi-29-12-2025-37736953d2e834d07.pdf | ENGELLİ | Robots bilinmiyor / HTTP yok / 0 | Robots sonucu belirsiz; PDF hedefine GET yapılmadı. Timeout robots kontrolünde oluştu. |
| 19 | ilan-gov-apex | Kamu | https://ilan.gov.tr/ | ENGELLİ | Robots bilinmiyor / HTTP yok / 0 | TLS_ERROR: CERTIFICATE_VERIFY_FAILED, unable to get local issuer certificate. |
| 20 | resmigazete-root | Kamu | https://www.resmigazete.gov.tr/ | ENGELLİ | İzin var / timeout / 0 | Hedef okuma TIMEOUT. Sonraki Actions tekrarları aşağıdaki denetim notunda. |

## TLS hata nedeni — 5 URL

Şu beş hedefte aynı hata görüldü: CERTIFICATE_VERIFY_FAILED: certificate verify failed: unable to get local issuer certificate.

1. https://www.ilan.gov.tr/
2. https://www.tuseb.gov.tr/public/tr/duyurular
3. https://www.tuseb.gov.tr/kurumsal/insan-kaynaklari
4. https://www.ptt.gov.tr/duyurular
5. https://ilan.gov.tr/

Bu çıktı sertifikanın süresinin dolduğunu söylemiyor. Hata, güven zincirinde issuer/CA doğrulamasının tamamlanamadığını gösteriyor. Sunucunun ara sertifikayı eksik sunması ile Actions runner CA deposunun eksikliği bu rapordan ayrıştırılamadı. TLS doğrulaması kapatılmadı; Şişecam 403 ve ÖSYM robots belirsizliği aşılmaya çalışılmadı.

## Yayın tarihi / sınav tarihi ayrımı

- YHGM resmî sayfasında “12 Kasım 2025” ve “12.11.2025 Tarihli Duyuru” açıkça yer alıyor; bu kayıt kura/sonuç haberidir. Aynı metin başvuru dönemini 08.10.2025–13.10.2025 olarak veriyor. Bu nedenle H1 kaydında publishedAt=2025-11-12, deadline=2025-10-13; contentType=lottery ve freshnessStatus=Süresi geçti olmalı. Kayıt yeni iş ilanı sayılmaz. [Resmî sayfa](https://yhgm.saglik.gov.tr/TR-111525/tasra-teskilati-hizmet-birimlerinde-istihdam-edilmek-uzere-2764-surekli-isci-alim-kurasi-hakkinda-duyuru.html)
- BOTAŞ'ın ölçülen duyuru listesinde yer alan sonuç kaydının resmî detay sayfasında ayrı “Tarih: 21/1/2026” alanı görülüyor. Başlıktaki “10 Kasım 2025 / 14 Kasım 2025” yazılı sınav tarihlerini ifade ediyor; publishedAt alanına yazılmamalı. H1 kaydında publishedAt=2026-01-21, deadline=null, contentType=result ve freshnessStatus=Süresi geçti olmalı. Detay sayfası manuel okunarak tarih ayrımı doğrulandı; K1-B otomatik URL listesinde ayrı adres olarak ölçülmedi. [Resmî detay](https://www.botas.gov.tr/Icerik/10-kasim-2025-14-kasim-2025/1233)

## Timeout deneme sınırı — HATA / RED

- Resmî Gazete /ilanlar ve kök sayfa için robots izni bulunan sonraki koşularda şu Actions run'larında HTTP TIMEOUT kaydı oluştu: [#45405355596](https://github.com/aries919-bot/Jarvis/actions/runs/45405355596), [#45405516646](https://github.com/aries919-bot/Jarvis/actions/runs/45405516646), [#45405558813](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813).
- İki Resmî Gazete URL'si için workflow tetiklemeleri hedefe bir kezden fazla tekrar isteği gönderdi; tek retry sınırı aşıldı. Bu operasyonel hata kabul edilmiyor. Artık tekrar istek yapılmayacak.
- TPAO PDF satırında robots sonucu BİLİNMİYOR olduğu için hedef PDF'ye erişilmedi. Robots belirsizliği izin sayılmadı.
- Bu sebeple K1-B'nin ölçüm tablosu kaydedildi; timeout tekrar kuralı tam uyumlu değildir.

## Haber / ilan sınıflandırması

- BOTAŞ'ın 2025 personel alımı sonuç duyurusu: **Süresi geçti**; açık/güncel iş ilanı değildir.
- Sağlık Bakanlığı YHGM 2.764 sürekli işçi kura/sonuç duyurusu: **Haber / Kura**; yeni iş ilanı sayımına girmez.
- K1-E1 e-Şube satırlarında publishedAt=null; satırdaki tarih deadline'dır.
