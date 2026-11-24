# H2 — Ölçülmüş resmî duyuru okuyucusu

Durum: H2 okuyucu PR'ı; H1 schemaVersion=1 alanlarına JSON üretir.  
Kaynak ölçümü: [K1-B #45429536647](https://github.com/aries919-bot/Jarvis/actions/runs/45429536647).  
K1-C kaynak sınıflandırma raporu: [#45430917966](https://github.com/aries919-bot/Jarvis/actions/runs/45430917966).

## Kaynak kapsamı

- YHGM 2.764 sürekli işçi kura duyurusu: robots izinli, HTTP 200 ölçülmüş. Kura/sonuçtır; yeni iş sayılmaz.
- BOTAŞ duyuru arşivi ve iki resmî detay sayfası: arşiv hostu robots izinli, HTTP 200 ölçülmüş. Detay ve PDF istekleri kendi robots yol kararıyla yapılır; yalnız allowlist içindeki BOTAŞ hostuna izin verilir.
- Kariyer Kapısı/e-Devlet, İŞKUR ayrıntılı ilan sayfaları, Şişecam 403, TLS ve timeout kapalı listedeki adresler okuyucu kapsamı dışındadır.

## Kart alanları ve tarih kuralları

H1 şeması: institution, title, category, contentType, officialSourceUrl, howToApply, conditions, deadline, publishedAt, collectedAt, firstSeenAt, freshnessStatus, tags, sourceStatus.

- YHGM: publishedAt 12.11.2025; deadline 13.10.2025; contentType=lottery; Süresi geçti.
- BOTAŞ 223 kişilik personel duyurusu: publishedAt 05.11.2025; deadline 14.11.2025; contentType=job; Süresi geçti.
- BOTAŞ müfettiş yardımcılığı sonuç kaydı: publishedAt 21.01.2026; deadline=null; contentType=result; Süresi geçti.
- Başlıktaki sınav/kura tarihi publishedAt sayılmaz. Tarih alanı yoksa null; PDF dosya adı ve çekim saati yayın tarihi değildir.
- collectedAt kaynak başarıyla okunduğu anda üretilir. firstSeenAt önceki feed dosyasındaki aynı id'den korunur; yoksa ilk okunduğu an yazılır.
- Her kaynak ayrı JSON dosyasıdır. Bir kaynak hata verirse yalnız o kaynağın son iyi JSON'u stale olur; diğer kaynak etkilenmez.
- GitHub Actions branch-scoped cache, bu dalın son başarılı JSON'larını ve firstSeenAt değerlerini sonraki koşuya taşır. Cache bulunamazsa ilk görülme zamanı yeni başlangıç olarak yazılır; cache 7 günlük kullanım politikası nedeniyle kalıcı üretim deposu sayılmaz. Kalıcı firstSeenAt ve last-good saklaması C1-1 Worker veri deposunda tamamlanacaktır.

## Güvenlik ve hata davranışı

- Her hedef URL'den önce robots.txt kontrolü; robots UNKNOWN/Disallow ise hedefe istek yok.
- TLS varsayılan Python SSLContext ile doğrulanır; doğrulama kapatılmaz. UA AriesKaynakTara/1.0. HTTPS GET dışında yöntem yok.
- 403/500, timeout, ağ kesintisi, boş yanıt veya bozuk PDF son iyi veriyi ezmez.
- BOTAŞ PDF yalnız aynı resmî host ve robots izniyle indirilir.
- Actions artefaktı 14 gün saklanır. PR doğrulaması persistent firstSeenAt/last-good deposunun üretimde kalıcılaştırıldığını tek başına kanıtlamaz; bu kalıcılık adımı C1-1/kalıcı veri deposu entegrasyonunda ayrıca yapılacaktır.


## İlk canlı çalıştırma — 24.11.2026

- PR testi: [#45437948449](https://github.com/aries919-bot/Jarvis/actions/runs/45437948449) — **19/19 test SUCCESS**.
- Canlı okuyucu: [#45437928827](https://github.com/aries919-bot/Jarvis/actions/runs/45437928827) — workflow SUCCESS; `collectedAt=2026-11-24T16:30:37Z`.
- Kaynak sonuçları yeniden sayıldı: **2 kaynak, 1 fresh / 1 stale, 1 kart**.
  - `yhgm-kura`: fresh, 1 kart. `publishedAt=2025-11-12`, `deadline=2025-10-13`, `contentType=lottery`, `freshnessStatus=Süresi geçti`.
  - `botas-duyurular`: stale, 0 yeni kart. `/Icerik/223-kisilik-personel-alim-duyu/1203` hedefi timeout oldu; robots izni sonrası yalnız bir kontrollü tekrar yapıldı ve o da timeout verdi. Başarısız/boş cevap son iyi veriyi ezmedi.
- JSON artefaktı: [#14942950569](https://github.com/aries919-bot/Jarvis/actions/runs/45437928827/artifacts/14942950569). İçerik açılıp doğrulandı: YHGM JSON'unda tarih/lottery alanları doğru; BOTAŞ JSON'unda `sourceStatus=stale`, `lastError=TIMEOUT_RETRY_FAILED`.
- Bu çıktı iki kaynağın da canlı veri verdiğini kanıtlamaz. BOTAŞ detay sayfası stale kaldığı için H2 **KISMİ**; BOTAŞ duyurusu güncel ilan sayısına eklenmez.
- 1 fresh haber kartı da yeni kamu işi değildir. Kamu ilanı hâlâ **1 kaynak / 43 doğrulanmış ilan** (e-Şube); **10 kaynak / 100 ilan** hedefi karşılanmadı.
- Branch-scoped Actions cache başarılı kaynakların JSON/firstSeenAt değerlerini sonraki çalıştırmaya taşır; bu geçici çözümdür. Kalıcı saklama C1-1 Worker veri deposunda tamamlanır.



## Genişletilmiş okuyucu — 24.11.2026 20:00 UTC

H2 kaynak listesi K1-B geniş ölçümünde 28 URL içinden yalnız izinli ve erişilebilir resmî kaynaklara göre genişletildi. Kaynak sayısı 6 kurum:
- YHGM kura kaydı
- BOTAŞ duyuruları (bu denemede stale)
- Adli Tıp Kurumu 2026-2 personel alımı (HTML + aynı host Ek-1 PDF)
- Anadolu Üniversitesi 4/B duyurusu (HTML; PDF uç noktası K1-B'de ağ hatası olduğundan ek PDF kazınmaz)
- Düzce Üniversitesi 23.11.2026 öğretim elemanı PDF'si
- Tarsus Üniversitesi öğretim elemanı duyurusu (HTML + K1-B'de ayrı ölçülmüş aynı-host PDF)

H2 okuyucu için yeni genel parser:
- Türkçe ay adları ile tarih ve başvuru aralığı algılar.
- Yayın tarihi yalnız açık meta/etiket veya kaynağa özel sabitlenmiş başlık alanından; başlık sınav/kura tarihi ve dosya adı yayın tarihi sayılmaz.
- Başvuru aralığındaki bitiş tarihi deadline olur; collection timestamp asla bunun yerine geçmez.
- Başvuru açılış zamanı ilan metninde ayrı veriliyorsa `applicationOpensAt` alanında tutulur; açılış gelmeden `Doğrulandı` etiketi verilmez.
- PDF ekleri robots/TLS kontrolünden geçmeden alınmaz. Bir HTML sayfasında bağlantı bulunması beklenir; sayfa linki boşluk/URL-encoding farkı gösterse de önceden K1-B ile ölçülmüş aynı-host PDF URL'si kullanılabilir. Başka host reddedilir.
- Kaynak bazında stale ve Actions cache last-good davranışı korunur.

İlk geniş live run [#45475126894](https://github.com/aries919-bot/Jarvis/actions/runs/45475126894): 6 kaynak, 4 fresh /2 stale, 4 kart (YHGM 1, ATK 1, Anadolu 1, Düzce 1; BOTAŞ timeout, Tarsus PDF URL mismatch). Tarsus bağlantı düzeltmesinden sonra live run [#45476181267](https://github.com/aries919-bot/Jarvis/actions/runs/45476181267): **6 kaynak, 5 fresh /1 stale, 5 kart** (YHGM 1, ATK 1, Anadolu 1, Düzce 1, Tarsus 1; BOTAŞ timeout). Aynı koşuda 30 tests / 30 PASS.
- Tarsus PDF link düzeltmesi: K1-B ile önceden ölçülmüş percent-encoded aynı-host URL'si çağrılır; kaynak HTML'inden bulunan href'in decode edilmiş path'iyle eşleştirilir. Bu, robots izinlerini atlamaz.
- YHGM kartı kura (lottery) ve geçmiş tarihlidir; güncel iş ilanına eklenmez.
- ATK kartı 181 pozisyonu temsil eden tek duyuru kartıdır; 181 ayrı haber sayılmaz. `applicationOpensAt=2026-11-30T09:00:00+03:00`, deadline 2026-12-06; 24 Kasım ölçümünde henüz açılmadığı için `Doğrulanamadı` olması gerekir.
- Anadolu kartı tek personel duyurusudur, 30 Kasım deadline; KPSS(B) puan sıralaması. İlan PDF uç noktası erişilemediği için kart yalnız HTML'den kanıtlanabilen genel şartları içerir.
- Düzce kartı 5 öğretim elemanı kadrolu bir duyuru; 7 Aralık deadline.
- Tarsus kartı 3 öğretim görevlisi kadrolu bir duyuru; 7 Aralık deadline.
- BOTAŞ detay sayfası timeout + tek retry timeout; stale; son iyi veri korunur.

### Zamanlama ve canlı çalışma sınırı
- H2 workflow `17 */6 * * *` UTC cron kullanır. GitHub Actions schedule yalnız varsayılan `main` dalındaki workflow üzerinden tetiklenir. H2 PR #51/#52 `cline/K1` hedeflidir; **main'e alınana ve kullanıcı main birleştirmesine onay verene kadar otomatik cron çalışmaz**. Push ve workflow_dispatch ile ayrı dallarda test/canlı ölçüm yapılabilir.
- Workflow yalnız JSON kaynak artefaktlarını üretir ve Actions cache'de kaynak bazlı last-good/firstSeenAt korur. Cache ve 14 günlük artifact production database değildir.
- Aries uygulamasındaki Android Haber ekranı bu artefaktı henüz çekmiyor. Worker'da H2 public feed route/publish yok; C1 `/kariyer-feed` production publish de secret çifti olmadığı için SKIPPED. Android uygulama network fetch/refresh bağlı değil. Dolayısıyla **haber okuyucu canlı kaynak çekebiliyor; telefon ekranında canlı haber akışı henüz yok**.
- Üretim canlı akışı için: default branch'e onaylı schedule workflow; kaynak başına last-good ve firstSeenAt kalıcılığı Worker/D1; güvenli public/read authenticated feed; Android haber ekranı JSON refresh; per-source stale; dış site Custom Tabs. Bu iş C1 kabul/threshold öncesinde UI'ya taşınmaz.


### Son doğrulama — 24.11.2026 20:04 UTC

- Son kod HEAD: `3d2012c3d4e3208b40ef2b24e5f70d30912182ac`.
- [Test koşusu #45476832673](https://github.com/aries919-bot/Jarvis/actions/runs/45476832673): **32/32 test PASS**, 0 failure.
- [Canlı okuma koşusu #45476818354](https://github.com/aries919-bot/Jarvis/actions/runs/45476818354): overall SUCCESS; job `138739101315`; live feed step, cache restore/save, JSON artifact upload SUCCESS.
- Ölçüm `collectedAt=2026-11-24T20:04:05Z`; 6 sources, 5 fresh / 1 stale, 5 cards. Artefakt [#14961200293](https://github.com/aries919-bot/Jarvis/actions/runs/45476818354/artifacts/14961200293), 5.283 bayt.
- Artefakt zip'i açılıp altı JSON dosyası tekrar sayıldı:
  - `yhgm-kura`: fresh, 1 lottery card; `publishedAt=2025-11-12`, `deadline=2025-10-13`, Süresi geçti; yeni iş sayılmaz.
  - `atk-personel-2026-2`: fresh, 1 job card; 181 pozisyon; `publishedAt=2025-11-20`; `applicationOpensAt=2026-11-30T09:00:00+03:00`; `deadline=2026-12-06`; 24 Kasım'da henüz açılmadığı için **Doğrulanamadı** (başvuruyu açık göstermiyoruz).
  - `anadolu-sozlesmeli-2026`: fresh, 1 job card; `publishedAt=2026-11-13`; `deadline=2026-11-30`; Doğrulandı. PDF endpoint'i K1-B'de ağ hatası olduğundan sadece HTML sayfasında kanıtlanan genel şartlar taşınır.
  - `duzce-ogretim-elemani-2026`: fresh, 1 job card; `publishedAt=2026-11-23`; `deadline=2026-12-07`; Doğrulandı; 5 kadroluk duyuru bir karttır.
  - `tarsus-ogretim-elemani-2026`: fresh, 1 job card; `publishedAt=2026-11-23`; `deadline=2026-12-07`; Doğrulandı; 3 kadroluk duyuru bir karttır.
  - `botas-duyurular`: stale; detay sayfası timeout + tek retry timeout; son iyi kayıt stale korundu.
- Her kurum duyurusu tek karttır; kontenjan adedi haber/kaynak sayısını şişirmez. 5 kart = 4 iş/personel duyurusu +1 geçmiş kura haberi. Başvurusu açık/doğrulanmış güncel iş sayısı H2'de 3 karttır (Anadolu, Düzce, Tarsus); ATK başvurusu 30 Kasım'da açılacağından henüz açık sayılmaz. Kura kartı yeni iş sayılmaz.
- İlk görülme değerleri JSON artefaktından tekrar kontrol edildi. İlk üç / YHGM için önceki cache'deki `firstSeenAt` değerleri korundu; Tarsus için ilk fresh görülme `2026-11-24T20:00:39Z`.
- H2 workflow schedule `17 */6 * * *` UTC tanımlıdır. Workflow `main` varsayılan dalına alınmadığından schedule **henüz otomatik çalışır durumda değildir**. PR #51/#52 yalnız `cline/K1` dalına gider; `main` birleştirmesi ayrı kullanıcı onayı gerektirir. Manual/push koşuları branch'te canlı çekim yapar.
- Android uygulaması H2 JSON artefaktına/Worker'a bağlı değildir. Mevcut genel Haber yenileme butonu hâlâ Google News RSS başlıklarını elle yeniler; H2 doğrulanmış kartları telefonda henüz gösterilmez.


## Temiz K1 tabanı ve H2 kategori düzeltmesi — 24.11.2026

- Önceki PR #40 `durum` tabanındaydı; `cline/K1` ile karşılaştırma 111 ileri /14 geride ve 13 dosya farkı üretti. Kod bu nedenle doğrudan retarget edilmedi; yalnız 5 H2 dosyası temiz `cline/K1` tabanına taşındı.
- BOTAŞ kategori değeri H1 enum'uyla eşitlendi: `Kamu bağlı şirket`. Kaynak config testi artık tüm kayıtların izinli H1 kategorilerinden biri olduğunu zorunlu kılar.
- Son doğrulanmış H2 canlı okuma [#45476818354](https://github.com/aries919-bot/Jarvis/actions/runs/45476818354): 6 kaynak, 5 fresh /1 stale, 5 kart. 3 açık/doğrulanmış iş kartı Anadolu, Düzce ve Tarsus'tur; ATK başvurusu 30.11.2026 09:00'da açılır, YHGM geçmiş kura, BOTAŞ stale.
- H2 sayımında her duyuru tek karttır; kontenjan adedi kart sayısını büyütmez. Kamu genel sayımı e-Şube 43 + Anadolu 1 + Düzce 1 + Tarsus 1 = 4 kaynak /46 açık kayıt; 10 kaynak /100 doğrulanmış ilan eşiği RED.
- H2 Actions cache'i geçici branch-run last-good/firstSeenAt sağlar; production kalıcı veri deposu değildir. Cron yalnız workflow varsayılan dalına ulaştığında başlar; Android Haber sekmesi H2 JSON'una bağlı değildir.
- Bu PR'ın yeni HEAD canlı ölçümü ve CI sonuçları aşağıya eklenecektir; kaynak ya da test sonuçları önceden varsayılmaz.

## H2 son entegrasyon dilimi — 24.11.2026

- Çekirdek okuyucu, altı kaynak manifesti ve kritik testler PR [#51](https://github.com/aries919-bot/Jarvis/pull/51) ile `cline/K1` dalına alındı (merge SHA `27042ab4d1ba7959c44047ad7acca65caba8e4b3`). Bu PR'ın test kanıtı 120/120; Hakem ONAY.
- Bu son dilim kalan 14 testin tamamını geri getirir (toplam 33 H2 testi), altı saatlik Actions workflow'unu ve H2 raporunu ekler.
- Cron `main` varsayılan dalına birleştirilene kadar otomatik tetiklenmez. H2 PR'ları `cline/K1` hedeflidir; telefona/Android Haber sekmesine veri bağlanmaz.
- Canlı ölçüm sonucu ve exact HEAD CI kanıtları PR kontrolü tamamlandıktan sonra bu bölüme işlenecektir.

## H2 last-good / firstSeen cache düzeltmesi — 25.11.2026

- İlk temiz dal çalıştırmasında (`2026-11-24T21:00:47Z`) Actions cache miss oldu; Tarsus'un önceki verified kartı stale/0 olarak kaldı. Bu geçiş kusurunu çözmek için eski başarılı artefakt [run #45476818354](https://github.com/aries919-bot/Jarvis/actions/runs/45476818354), artifact #14961200293 içindeki beş `fresh` kaynağın JSON'u `career/h2_last_good_seed.json` olarak kaynaklandı. Önceki BOTAŞ kaydı zaten stale/boş olduğundan seed'e alınmadı.
- `scripts/h2_last_good_seed.py` cache'te kaynak yoksa veya stale/boşsa tarihsel fresh beslemeyi geri koyar; geçerli mevcut beslemeyi ezmez, eşleşen ilanlarda yalnız daha eski `firstSeenAt` değerini birleştirir. Başarısız kaynak `stale` olur ve son iyi item listesi korunur.
- Seed kapsamı beş kaynaktır: Anadolu, ATK, Düzce, Tarsus ve YHGM. BOTAŞ için tarihsel son-iyi veri yoktur; timeout halinde stale/0 kalması beklenen ve dürüst davranıştır.
- İlk seed'li canlı okuma [#45488828903](https://github.com/aries919-bot/Jarvis/actions/runs/45488828903): `copied=0, first_seen_merged=4, kept_current=1, skipped=0`; 6 kaynakta 4 fresh /2 stale, 5 kart. Tarsus robots DNS hatasında stale/1 son-iyi kart olarak korundu; BOTAŞ timeout stale/0 kaldı.
- Sonraki canlı okuma [#45488834084](https://github.com/aries919-bot/Jarvis/actions/runs/45488834084): `copied=0, first_seen_merged=0, kept_current=5, skipped=0`; beş geçerli cache kaydı korundu. Artefakt [#14964574716](https://github.com/aries919-bot/Jarvis/actions/runs/45488834084/artifacts/14964574716) indirildi ve altı JSON tek tek sayıldı.
- Son artefakt `collectedAt=2026-11-24T21:14:31Z`: Anadolu fresh/1 Doğrulandı; Düzce fresh/1 Doğrulandı; Tarsus stale/1 (eski `firstSeenAt=2026-11-24T20:00:39Z`, `publishedAt=2026-11-23`, `deadline=2026-12-07`); ATK fresh/1 Doğrulanamadı (başlangıç 30.11.2026); YHGM fresh/1 Süresi geçti; BOTAŞ stale/0.
- Böylece H2 şu an 2 fresh açık/doğrulanmış iş kartı + 1 stale son-iyi açık kart üretir. Tarsus kaydı kullanıcıya `sourceStatus=stale` olarak gösterilmelidir; yeniden robots/network erişimi doğrulanmadan fresh ilan gibi sunulmamalıdır.
- PR #52'nin altı dosyalık H2 entegrasyonu [d99e58c](https://github.com/aries919-bot/Jarvis/commit/d99e58c67b7619ba2eb03f87fe868b961817436b) ile `cline/K1` dalına birleştirildi. Bu PR #53 yalnız bu canlı kanıtı `docs/H2_PLAN.md`'ye ekler.
- Birleşik H2 uygulama kanıtı: [Python 142/142](https://github.com/aries919-bot/Jarvis/actions/runs/45488851410), [H2+seed 41/41](https://github.com/aries919-bot/Jarvis/actions/runs/45488851555), [Hakem ONAY](https://github.com/aries919-bot/Jarvis/actions/runs/45488851398), [K1-B 24/24 ve 28 URL: 10 çalışan /18 engelli /0 ölü](https://github.com/aries919-bot/Jarvis/actions/runs/45488851385). URL sayısı ilan sayısı değildir.
- Kamu sayımı ayrı kalır: 4 kaynak /46 son bilinen açık kayıt (43 e-Şube + Anadolu ve Düzce fresh + Tarsus stale son-iyi). Fresh/doğrulanmış açık H2 iş kaydı 2'dir; stale Tarsus ayrı işaretlenir. 10 kaynak /100 doğrulanmış ilan eşiği RED.
- Actions cache'i dal kapsamlı ve geçicidir; seed ilk geçişte tarihçeyi taşır, sonraki aynı dal koşuları cache'i kullanır. Kalıcı üretim depolaması Worker/D1 entegrasyonu gerektirir. Cron yalnız workflow varsayılan `main` dalına geldiğinde başlar; `cline/K1` push canlı okuma/test içindir. Android Haber ekranına henüz bağ yok.
