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
- H2 workflow'a `17 */6 * * *` UTC cron eklendi. GitHub Actions schedule yalnız workflow varsayılan branch'e ulaştığında çalışır. PR #40 şu an `durum` tabanına açık/taslak olduğundan **cron henüz otomatik çalışmıyor**. Manual run ve push ile branch'te canlı ölçüm yapılabilir.
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
- H2 workflow schedule `17 */6 * * *` UTC tanımlı. Ancak PR #40 `durum` tabanına açık/taslak olduğundan schedule **henüz otomatik çalışır durumda değildir**; GitHub schedule yalnız default branch'teki workflow'da tetiklenir. Manual/push koşuları branch'te canlı çekim yapıyor.
- Android uygulaması H2 JSON artefaktına/Worker'a bağlı değildir. Mevcut genel Haber yenileme butonu hâlâ Google News RSS başlıklarını elle yeniler; H2 doğrulanmış kartları telefonda henüz gösterilmez.
