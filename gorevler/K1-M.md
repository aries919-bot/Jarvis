# K1-M — Zamanlanmış kaynak yenilemesini varsayılan dala taşı

**Durum:** YAPILMADI — görev tanımı  
**Kapsam:** Yalnız İş & Kariyer / Haber kaynak toplama ve doğrulama  
**Hedef PR tabanı:** `main`  
**Görev kaydı:** `durum/gorevler/K1-M.md`

## Amaç

GitHub Actions `schedule` olayları yalnız varsayılan dalda bulunan workflow dosyalarını çalıştırır. Bu repo için varsayılan dal `main`'dir. Mevcut incelemede `main` altında yalnız `.github/workflows/android.yml`; `cline/K1` altında `kamu-ilan-cek.yml` ve `kaynak-tarama.yml`; `durum` altında `h1-schema.yml` görülmüştür. Bu nedenle `cline/K1` içindeki zamanlanmış e-Şube/kaynak işlerinin cron ile çalıştığı varsayılamaz.

Hedef, izinli kaynak yenileme workflow'larının temiz ve dar kapsamlı bir PR ile `main`e alınması; toplanan JSON'un mevcut tasarım gibi `durum` dalına yazılmasıdır. Bu görev veri eşiğini veya C1 kabul kararını değiştirmez.

## Başlangıçta doğrulanmış repo bulguları

- Repo varsayılan dalı: `main`.
- `main/.github/workflows`: yalnız `android.yml`.
- `cline/K1/.github/workflows`: `kamu-ilan-cek.yml`, `kaynak-tarama.yml` ve başka K1 workflow'ları mevcut.
- `durum/.github/workflows`: `android.yml`, `h1-schema.yml`.
- `kamu-ilan-cek.yml`: cron `17 5,14 * * *`; sonuç JSON'unu `durum` dalına yazma akışı var.
- `kaynak-tarama.yml`: cron `30 5,17 * * *`; sonuç JSON'larını `durum` dalına yazma akışı var.
- Bu iki workflow'un schedule tanımı `main`de bulunmadığından düzenli çalışma kanıtı değildir.

## Yapılacaklar

1. İşe başlamadan `docs/HARITA.md`, `docs/NOT_C1.md`, bu görev ve kaynak betiklerinin güncel sürümlerini oku. Sabit H/K1/C1 sırasını değiştirme; K1-M ancak sıradaki iş olduğunda yürütülür.
2. Yalnız ayrı bir çalışma dalı aç. `main`e doğrudan commit/push yapma. PR diff'ine film, tema, Android UI veya ilgisiz eski commitleri alma.
3. `.github/workflows/kamu-ilan-cek.yml`, `.github/workflows/kaynak-tarama.yml` ve yalnız bu workflow'ların çalışması için zorunlu betik/veri/test dosyalarını incele. Gereksiz dosyaları taşıma.
4. İki workflow'u ve gerekli dosyaları `main` hedefli tek, küçük, anlaşılır PR'da sun. Schedule cron ifadelerini gerekçesiz değiştirme. `workflow_dispatch` korunsun. Gerekli push tetikleyicileri yalnız `main` ve ilgili dosya yollarıyla sınırlandırılsın.
5. Workflow çalışma kodunu varsayılan dalın commit'inden almalı; sonuçları mevcut tasarıma uygun biçimde `durum` dalına yazmalı. `main` veri snapshot'larıyla kirletilmemeli.
6. `contents: write` iznini yalnız veri yazan job için ve gereken en dar kapsamda kullan. Repo Actions ayarları veya dal koruması yazmayı engellerse güvenlik ayarlarını genişletme; kanıtla ve BLOCKED raporla.
7. İzinli kaynak ilkesi korunmalı: robots.txt önce kontrol edilir; robots engelinde/UNKNOWN durumunda hedef sayfaya istek atılmaz. Giriş, CAPTCHA veya oturum aşılmaz. İŞKUR ilan sayfaları ve Kariyer Kapısı kazınmaz; yalnız izinli e-Şube ve doğrulanmış resmî kaynaklar kullanılır.
8. User-Agent tam olarak `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)` olmalı; tarayıcı taklidi yapılmamalı. SSL/TLS doğrulaması kapatılmamalı.
9. Veri sözleşmesini koru: `publishedAt` kaynakta açıkça yoksa `null`; `deadline`, `collectedAt` ve `firstSeenAt` ayrı alanlardır. Geçmiş deadline güncel ilan değildir. Bir kaynak başarısız olursa yalnız o kaynağın son iyi verisi stale işaretlenir.
10. Kaynak toplama için `CF_ACCOUNT_ID` veya `CF_API_TOKEN` gereksinimi ekleme. Mevcut Hakem workflow'unda bu değerler yalnız isteğe bağlı Cloudflare AI incelemesinde kullanılmaktadır; deterministic test ve veri toplama bu görevin içinde Cloudflare AI'ye bağlanmamalıdır. Secret değerleri koda, loga veya sohbete yazılmamalıdır.

## Zorunlu testler ve kanıt

- Birim testleri ve workflow YAML doğrulaması.
- Gerçek izinli kaynakta canlı deneme; kaynak başına HTTP/robots sonucu ve ilan sayısı.
- Bozuk internet, boş yanıt, HTTP 403/500, timeout, robots engeli ve stale-cache regresyon testleri.
- `workflow_dispatch` çalıştırması: başarılı job adımları, çıktı özeti ve `durum` dalına yazılan commit SHA.
- PR birleştirildikten sonra cron kanıtı: Actions olay türü `schedule`, çalıştırma ref'i varsayılan dal (`main`) ve tamamlanan job logları. Yalnız manuel dispatch, günlük cron'un çalıştığını kanıtlamaz.
- Kaynak başına sayımlar tekrar hesaplanmalı; kamu ayrı raporlanmalı. Kamu 0 ise RED. 10 kaynak / 100 doğrulanmış, deadline'ı geçmemiş ilan eşiği sağlanmıyorsa gerçek sayı yazılmalı ve RED korunmalı.
- CI bağlantısı ve tam run kimliği rapora eklenmeli.

## Sınırlar

- Bu PR tek başına 10 kaynak / 100 doğrulanmış ilan eşiğini karşılamaz ve C1'i ONAY yapmaz.
- Worker deploy'u, Worker secret'ları, uygulama entegrasyonu, Android dosyaları, `Store.kt`, manifest, alarm, tema ve medya kodu bu görevin dışındadır.
- `CF_ACCOUNT_ID` / `CF_API_TOKEN` eksikliği, yalnız isteğe bağlı AI Hakem katmanının durumudur; günlük kamu kaynak toplamanın başarısızlık nedeni olarak raporlanmamalıdır.
- PR, hakem denetimi ve Cem'in açık birleştirme onayı olmadan merge edilmez.

## Sonuç raporu zorunlu biçimi

- **KARAR:** KABUL / RED
- **Kanıt:** komut çıktısı, PR diff dosya sayısı ve bayt, test sayısı, gerçek kaynak başına ilan sayısı, CI/Actions bağlantıları, `schedule` run ref'i ve commit SHA
- **Eksik:** engeller ve doğrulanamayan iddialar
- **Telefonda denenecek:** yalnız gerçek telefon doğrulaması gerekiyorsa
- **SONRAKİ ADIM:** görev kuyruğundaki bir sonraki iş
