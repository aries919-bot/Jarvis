# C1 günlük tazelik ve yeni resmî duyurular — 25.11.2026

## KARAR
**RED — güncel günlük sayım kanıtlanamadı.** Son birleştirilmiş Actions raporu tarihsel snapshot'tır; bugünün şu anki doğrulanmış ilan sayısı olarak kullanılamaz. C1 kod testleri PASS olsa da kaynak tazeliği, 10 kaynak/100 doğrulanmış açık ilan ve production GET kapıları hâlâ geçilmedi.

## Son Actions ölçümü (tarihsel; güncel sayım değildir)
- En son C1 collector: `collectedAt=2026-11-25T00:25:02.241Z` (03:25 TRT).
- Workflow: [Actions #45517626605](https://github.com/aries919-bot/Jarvis/actions/runs/45517626605).
- Son run toplamı: 26 source definitions; 17 fresh / 9 stale; 163 fresh records; 47 deadline-verified open job cards; 116 unknown-deadline/non-open rows; 0 expired.
- Kamu ayrı: 6 başarılı kaynak / 47 açık kart (e-Şube 42 + H2 5). Bu, yalnız o run'ın snapshot'ıdır.
- e-Şube snapshot: `2026-11-24T23:51:48Z`; 42 raw / 42 unique / 42 open / 0 expired; `publishedAt=null`; 12 saatlik fresh-until `2026-11-25T11:51:48Z` (14:51:48 TRT). Bu sınır geçtikten sonra yeni başarılı çekim yoksa 42 satır güncel sayıma katılmaz.
- H2 son reader snapshot'ı: yaklaşık `2026-11-25T00:23:08Z` (03:23 TRT), 10 H2 kaynak tanımı / 7 fresh / 3 stale / 5 deadline-doğrulanmış açık duyuru. Bu snapshot sonradan yeniden çalıştırılmış değil; anlık güncel toplam gibi gösterilmez.
- Özel ATS: Trendyol 35, Midas 16, Commencis 5, Dream Games 17, Aleph 5, Ajax Systems 13, Insider One 22, Binance 0, Picus Security 1 = 114 aday. 114/114 son tarih unknown; doğrulanmış açık ilan sayısı 0. Bold Games, fal, The Global Talent Co., Sensor Tower, XDS, Cambly robots HTTP 401 stale; endpoint'lerine istek gönderilmedi.
- Diğer H2: YHGM 1 kura / 0 iş; ATK 1 gelecek açılış / 0 şu an açık (30.11.2026 09:00 +03); BOTAŞ stale/timeout; ÇOMÜ robots UNKNOWN, hedef istenmedi; Bilecik robots timeout. Anadolu 1, Düzce öğretim elemanı 1, Tarsus 1, Marmara 1, Zafer 1 açık kart.

## 25.11 resmî sayfa çapraz kontrolü — yeni adaylar, Actions toplamına eklenmedi
| Kurum | Duyuru | Yayın tarihi | Son başvuru | Sonuç |
|---|---|---:|---:|---|
| Düzce Üniversitesi | Öğretim üyesi alımı, 4 kadro | 23.11.2026 (duyuru başlığı) | 07.12.2026 | 1 yeni duyuru adayı; Düzce aynı kurum/kaynak olduğundan yeni kaynak sayılmaz |
| Düzce Üniversitesi | Öğretim elemanı alımı, 5 kadro | 23.11.2026 (duyuru başlığı) | 07.12.2026 | H2 okuyucusunda zaten 1 kart; tekrar eklenmeyecek |
| Bartın Üniversitesi | 1 sözleşmeli personel | 20.11.2026 | 05.12.2026 13:00 | 1 duyuru adayı; robots ölçümü yapılamadığı için reader'a eklenmedi |
| TGA | 2026 personel alım ilanı, sayfada 13 pozisyon listeleniyor | 03.11.2026 | 04.12.2026 | Tek ana duyuru; 13 pozisyon otomatik olarak 13 ilan sayılmaz; kategori ve alt ilan tekilleştirmesi bekliyor |

### Resmî kanıt bağlantıları
- Düzce duyuru listesi: https://personel.duzce.edu.tr/tum-duyurular
- Düzce öğretim üyesi duyurusu: https://personel.duzce.edu.tr/duyuru/01b9/23112026-tarihli-ogretim-uyesi-alimi-lanimiz-yayinlanmistir
- Düzce öğretim üyesi ilan PDF: https://cdn.duzce.edu.tr/File/GetFile/5c44b1ea-f2da-4f94-8f1a-9978d7b86c87
- Düzce öğretim elemanı duyurusu: https://personel.duzce.edu.tr/duyuru/ffc7/23112026-tarihli-ogretim-elemani-alimi-lanimiz-yayinlanmistir
- Düzce öğretim elemanı ilan PDF: https://cdn.duzce.edu.tr/File/GetFile/4455f177-5bf2-414d-a4c6-9de77131bf51
- Bartın Üniversitesi duyurusu: https://muh.bartin.edu.tr/duyurular/sozlesmelipersonelalimilani202604.html
- Bartın ilan PDF: https://cdn.bartin.edu.tr/onceinsan/fa8dc0ccaa1fa07ccf0ef519114d8f0a/ilan-metni.pdf
- TGA kariyer sayfası: https://tga.gov.tr/kariyer

### Kontrol ayrıntıları
- Düzce öğretim üyesi PDF'si 4 kadro içeriyor: İktisat/İktisat Tarihi profesör (1), Gelişim Psikolojisi doktor öğretim üyesi (1), Yönetim ve Organizasyon doktor öğretim üyesi (1), Gelişimsel Pediatri doktor öğretim üyesi (1). Başvuru 23.11–07.12.2026 arasında Düzce Akademik İlan Başvuru Sistemi üzerinden; aday yalnız bir kadroya başvurabilir.
- Düzce öğretim elemanı PDF'si 5 kadro içeriyor: Makine Mühendisliği araştırma görevlisi (1) ve dört öğretim görevlisi (4). ALES tabanı 70; yabancı dil şartı kadroya göre 50 veya muaf. Başvuru 23.11–07.12.2026; yalnız bir kadro.
- Bartın duyurusu 1 sözleşmeli personel. PDF koşulları: ön lisans mezuniyeti, 2026 KPSS P93 en az 60, 01.01.1991 veya sonrası doğum. Başvuru Kariyer Kapısı üzerinden; başvuru sonrası Aday Başvuru Bilgileri belgesi 08.12.2026 17:30'a kadar kuruma elden teslim edilir. Okuyucu Career Gate'e girmez.
- Bartın `muh.bartin.edu.tr/robots.txt` ve `cdn.bartin.edu.tr/robots.txt` kontrolleri bu oturumda erişilemedi; robots izinli kabul edilmedi ve otomatik okuyucuya eklenmedi.
- TGA resmi sayfası 13 pozisyonu listeliyor, başvuru 03.11–04.12.2026 ve çevrim içi başvuru talimatı veriyor. Tek ana duyuru kartı esas alınmalı; 13 pozisyon sayısı kart sayısı değildir. Kamu/Kamu bağlı şirket sınıfı ve ayrı pozisyon sayfalarının tekilleştirilmesi ayrıca teyit edilmeden kamu sayısına eklenmez.
- Düzce öğretim üyesi duyurusu yeni; öğretim elemanı duyurusu H2 snapshot'ında zaten bir kayıt olduğundan ikinci kez sayılmaz. Eski reader yalnız öğretim elemanı bağlantısını içeriyor; öğretim üyesi için ayrı kayıt/reader testi gerekir.

## PR #9 kod ve altyapı durumu
- Private PR: https://github.com/aries919-bot/jarvis-cloud/pull/9 — HEAD `8a43b99981db601de3442e8394b71d0651d0e34e`, base `fix/c1-source-category-after-timestamp`; açık/taslak, formal review 0, merged=false.
- Exact-head CI: [#45517626605](https://github.com/aries919-bot/Jarvis/actions/runs/45517626605): `npm run test:c1` 46/46 PASS; local Wrangler/D1 11/11 PASS; failure matrix 17/17 PASS; security/diff 5 files /22,465 bytes PASS.
- Hakem RED: production GET `JARVIS_WORKER_URL/JARVIS_WORKER_TOKEN` yok diye BLOCKED; yayın SKIPPED. Production POST/deploy yok.
- Workflow `validate.yml` e-Şube için durum dalındaki statik `career/esube_kamu_ilanlari.json` dosyasını klonluyor; `scripts/career-feed-live.mjs` bu JSON'u map edip collector'a ekliyor, e-Şube okuyucusunu canlı çalıştırmıyor. Dolayısıyla schedule tetiklense bile e-Şube snapshot'ı kendiliğinden yenilenmiyor. Schedule `0 6` ve `0 18` UTC; GitHub schedule yalnız varsayılan branch'te çalışır. PR #9 birleşmediği için bu schedule üretimde aktif değil.
- Bu oturumda Actions workflow_dispatch başlatma aracı bulunmadı; #45517626605'in başarılı QA job'ları yeniden çalıştırılarak canlı kaynak verisi yenilenemez. Bu nedenle yeni Actions live collector iddiası yoktur.

## Kaynak/ilan toplamını yeniden sayma yöntemi
- Kaynak sayısı: aynı kurumun iki duyuru sayfasını iki ayrı kurum kaynağı sayma; kaynak endpoint/reader kimliği ayrı göster, kaynak sayısını yapay yükseltme.
- İlan sayısı: resmi alım duyurusu/başvuru ilanı başına 1 kart; kadro/pozisyon sayısı kartı çoğaltmaz. Aynı ilana bağlı PDF ve HTML bir duyurunun kanıtlarıdır.
- Başvuru açılış tarihi gelecekteyse current-open sayılmaz; başvuru son tarihi geçmişse current feed'den çıkarılır. Deadline veya yayın tarihi kanıtı yoksa Doğrulanamadı.
- Kura, sonuç, sınav duyurusu ve süreç adımı yeni iş ilanı değildir.
- Yeni collector her kaynak için publishedAt, deadline, collectedAt, firstSeenAt, source state, raw/unique/verified/expired/unknown counts üretmeli; önceki çalıştırmaya göre new/removed/expired/unchanged delta ayrı hesaplanmalı.

## Eksikler / SONRAKİ ADIM
1. e-Şube okuyucusunu C1 Actions içinde canlı çalıştır; statik branch JSON yalnız seed/last-good olarak kalsın. Gerçek reader komutunu, robots, sayfa sayısı, ilan no unique count ve Actions artifact ile ispatla.
2. Düzce öğretim üyesi duyurusunu H2 okuyucusuna ayrı item olarak ekle; aynı kuruma yeni source id vererek kaynak sayısını şişirme. Mevcut H2 için Düzce sayfa/PDF reader ölçümünü koru.
3. Bartın için robots ölçümü erişilebilir runner'da yapılmadan kaynağı okuyucuya alma; erişim yoksa stale/unmeasured.
4. TGA tek duyuru kartı olarak tut; alt başlıkların bağımsız başvuru/ilan URL'lerini ve kurum kategorisini teyit etmeden çoğaltma.
5. Yeni branch/PR, exact-head test ve fresh audit olmadan bu adaylar C1 toplamına katılmaz. Public verified current total şu an **yeniden ölçülmemiş/kanıtlanmamış**; son tarihsel 6/47 run sayısı current olarak kullanılmaz.
6. Worker production authenticated GET ve durable firstSeenAt kanıtı olmadan C1 kabul/merge ve Android UI yok.

**KARAR: RED — fresh daily recount not proven; 10/100 and production gates unmet.**
