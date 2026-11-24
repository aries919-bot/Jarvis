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

## Güvenlik ve hata davranışı

- Her hedef URL'den önce robots.txt kontrolü; robots UNKNOWN/Disallow ise hedefe istek yok.
- TLS varsayılan Python SSLContext ile doğrulanır; doğrulama kapatılmaz. UA AriesKaynakTara/1.0. HTTPS GET dışında yöntem yok.
- 403/500, timeout, ağ kesintisi, boş yanıt veya bozuk PDF son iyi veriyi ezmez.
- BOTAŞ PDF yalnız aynı resmî host ve robots izniyle indirilir.
- Actions artefaktı 14 gün saklanır. PR doğrulaması persistent firstSeenAt/last-good deposunun üretimde kalıcılaştırıldığını tek başına kanıtlamaz; bu kalıcılık adımı C1-1/kalıcı veri deposu entegrasyonunda ayrıca yapılacaktır.
