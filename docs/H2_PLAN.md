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

