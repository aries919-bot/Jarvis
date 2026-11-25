# C1 rakip yöntemleri ve canlı doğrulama — 25.11.2026

## KARAR
**RED — araştırma ve mevcut H2 canlı test tamamlandı; C1 kabul değil.** Rakiplerde görülen ölçek yöntemi çoklu kaynağı tek bir kazıyıcıya yüklemek değil; lisanslı API/feed + işveren ATS uçları + resmî kaynak adaptörleri, ortak şema, tekilleştirme ve son kullanma/güncellik kontrolüdür. Aries için güvenli uyarlama bu mimaridir. Bu kayıt yalnız dokümantasyon ve mevcut CI/live-run kanıtıdır; kod, Worker deploy, PR merge veya uygulama değişikliği yapılmadı.

## Rakiplerde gözlenen modeller

| Model | Örnek / kanıt | Aries'e uyarlama | Sınır |
|---|---|---|---|
| İş ilanı API toplayıcısı | Adzuna geliştirici API: https://developer.adzuna.com/ | Lisans/anahtar ve kullanım koşulları onaylanırsa özel sektör için ayrı adapter | Ücret/ülke/kota/yeniden kullanım koşulları doğrulanmadan canlı kaynak sayılmaz |
| Çok kaynaklı API | Jooble API: https://jooble.org/api/about | Ülke/anahtar bazlı ayrı provider; source, company, location, link, updated alanlarını normalize et | API'den gelen kart resmî işveren linkine gidilerek doğrulanmalı; agregatör tek kaynak sayılır |
| İşveren ATS API'leri | Greenhouse, Lever, Ashby vb. katalog: https://github.com/jobspipe/ats-job-apis | Her işverenin herkese açık ve izinli board endpoint'i ayrı kaynak adapter'ı | Bu endpoint'ler evrensel tüm işveren veri tabanı değildir; şirket bazında seçilir, robots/ToS kontrolü gerekir |
| Arama motoru yapılandırılmış veri standardı | Google JobPosting: https://developers.google.com/search/docs/appearance/structured-data/job-posting ; Schema.org: https://schema.org/JobPosting | title, datePosted, validThrough, hiringOrganization, jobLocation, employmentType gibi alanları eşleme | Google Indexing API, Aries'in başkalarının ilanlarını çekme API'si değildir; yalnız sahibinin kendi sayfaları için bildirimdir |
| Agregatör feed / RSS | YubHub ürün sayfası: https://yubhub.com/ (sağlayıcının kendi hacim/yenileme iddiaları bağımsız doğrulanmadı) | Sadece sözleşme, lisans ve gerçek kaynak izlenebilirliği doğrulanırsa aday | Sağlayıcının pazarlama sayısı bağımsız kanıt değildir; 1 agregatör 10 kaynak diye sayılmaz |

## Aries için uygulanacak tasarım

1. **Kaynak kayıt defteri:** sourceId, kurum/işveren, kaynak türü (Kamu/Özel), resmî host, erişim yöntemi, robots/ToS durumu, ölçüm zamanı, son hata ve son başarılı çekim.
2. **Adapter katmanı:** e-Şube (girişsiz kamu görünümü), ölçülmüş resmî kurum duyuruları, lisanslı API'ler ve izinli ATS board endpoint'leri birbirinden bağımsız.
3. **Ortak kart sözleşmesi:** institution, title, category, officialUrl, applyHowTo, conditions, publishedAt (yoksa null), deadline, collectedAt, firstSeenAt, status, sourceId, stable external ID.
4. **Tekilleştirme:** önce kaynak ilan kimliği; yoksa normalize edilmiş resmî URL; son çare kurum + başlık + deadline bileşimi. Pozisyon sayısı ilan kartı sayısını artırmaz.
5. **Doğrulama ve sona erme:** deadline geçmişse aktif listeden çıkar; tarih yoksa doğrulanamadı; stale kaynağın son iyi verisi yalnız o kaynak için korunur.
6. **Sunum:** Actions ağır toplar, Worker ham/normalize edilmiş JSON'u kimlik doğrulamalı GET ile sunar; Kamu ve Özel ayrı sayaç/sekme; resmî başvuru sayfası Custom Tabs.
7. **Kota ve kalite:** her sağlayıcı için ayrı rate limit, cache, backoff, timeout, kaynak başına gözlem; lisans/robots belirsizse istek yok.

## Gerçek canlı test — mevcut H2 okuyucu

- PR #61 exact HEAD: `fe37be93202442394504f455b4a1caeb31f298cf`.
- Live reader Actions: https://github.com/aries919-bot/Jarvis/actions/runs/45646698179
- Reader job: `139319504019`, SUCCESS. Komut: `python -m unittest scripts/test_h2_announcement_reader.py scripts/test_h2_last_good_seed.py -v`; çıktı: **Ran 60 tests — OK**.
- Live reader aynı koşuda: `collectedAt=2026-11-25T15:34:38Z`; 19 kaynak kaydı; 17 fresh / 2 stale; 17 ham kart; 11 kurum; **13 güncel açık doğrulanmış ilan kartı**; 125 pozisyon.
- Artifact: https://github.com/aries919-bot/Jarvis/actions/runs/45646698179/artifacts/15037029949 ; 19 JSON dosyası; SHA-256 `eaf0911ed1d832bc0c3a01a6a65de3e7a10770aff8f69ae35ca0b579e7270ef6`.

### Kaynak başına yeniden sayım

| Kaynak | Durum | Açık doğrulanmış kart |
|---|---:|---:|
| Sağlık Bakanlığı YHGM (kura/sonuç) | fresh | 0 |
| BOTAŞ | stale — iki timeout | 0 |
| Adli Tıp Kurumu | fresh — başvuru henüz açılmamış | 0 |
| Anadolu Üniversitesi | fresh | 1 |
| Düzce Üniversitesi | fresh | 1 |
| Tarsus Üniversitesi | fresh | 1 |
| TGA | fresh | 1 |
| ORAN | fresh | 1 |
| Zafer Kalkınma Ajansı | fresh | 1 |
| DOKA | fresh | 1 |
| Atatürk Üniversitesi (20.11 duyurusu) | fresh | 1 |
| Atatürk Üniversitesi (16.11 duyurusu) | fresh | 1 |
| Mardin Artuklu (öğretim üyesi) | stale — açık başvuru aralığı kanıtı yok | 0 |
| Mardin Artuklu (öğretim elemanı) | fresh | 1 |
| Balıkesir Üniversitesi | fresh | 1 |
| Marmara Üniversitesi (öğretim elemanı) | fresh | 1 |
| Marmara Üniversitesi (öğretim üyesi) | fresh | 1 |
| Munzur Üniversitesi (öğretim üyesi) | fresh — aynı gün deadline saati belirsiz | 0 |
| Munzur Üniversitesi (öğretim görevlisi) | fresh — aynı gün deadline saati belirsiz | 0 |
| **Toplam** | **19 kayıt / 17 fresh / 2 stale** | **13 kart** |

13 kartın dağılımı: Anadolu 1, Atatürk 2, Balıkesir 1, DOKA 1, Düzce 1, Mardin Artuklu 1, Marmara 2, ORAN 1, Tarsus 1, TGA 1, Zafer 1. Yeniden toplam: **13**. Pozisyon toplamı **125**; 100 ilan kartı eşiği yerine kullanılamaz.

### Ayrı kamu kaynağı ve birleştirme sınırı
- e-Şube snapshot: 1 kaynak, 3 sayfa, 42 ham / 42 benzersiz / 0 tekrar / 42 açık; `collectedAt=2026-11-25T13:49:08Z`, `publishedAt=null`.
- H2 ve e-Şube ayrı feed'lerdir. Aralarında aynı ilanı URL/ilan kimliği ile tek feed içinde cross-feed dedup kanıtı yok. Bu yüzden 13 + 42 = 55 yalnız ham ayrı-feed toplamıdır; 55 tekil birleşik ilan diye raporlanamaz.
- Kamu için mevcut kanıt: 20 kaynak kaydı varsayımıyla sayım yapılmıyor; H2'nin 19 duyuru kaydı + e-Şube 1 ayrı kaynak kaydı vardır. Ancak kurum/host/adapter bazlı kaynak tanımı netleştirilmeden kaynak eşiği kabul edilmez. Doğrulanmış kamu kartı 13 H2 + 42 e-Şube ayrı snapshot; tekil birleşik sayı bilinmiyor.
- 100 doğrulanmış ilan kartı eşiği sağlanmadı. Kamu 0 değildir; fakat eşik ve birleşik feed kanıtı eksik olduğundan C1 **RED**.

## Test matrisi — mevcut kanıt / açık kalan
- Gerçek resmî duyuru çekimi: PASS — 19 kayıt; 13 açık doğrulanmış kart.
- 403/500/boş yanıt/timeout: PASS — test `test_403_500_empty_and_timeout_fail_closed`.
- Robots disallow/robots timeout: PASS — hedef URL'ye istek gönderilmemesi testleri.
- Kaynak bazlı last-good izolasyonu: PASS — `test_source_failure_preserves_only_that_source_last_good`; başarılı kaynağın verisi korunur, hata veren kaynak tek başına stale kalır.
- firstSeenAt korunması: PASS — `test_first_seen_is_preserved_for_known_ids` ve seed testleri.
- TLS doğrulaması açık: PASS — `test_tls_context_does_not_disable_verification`.
- Tarih/başvuru penceresi: PASS — başlık sınav tarihi ile yayın tarihi ayrımı, gelecekte açılan başvuru, geçmiş deadline ve aynı gün saati bilinmeyen başvurular testleri.
- Cross-feed duplicate test: **BLOCKED / mevcut kanıt yok**.
- Production Worker authenticated GET + D1 kalıcılık: **BLOCKED**.
- Android Custom Tabs/sekme ayrımı: **bu testin kapsamı dışında; cihaz kanıtı yok**.
- PR #61 Hakem Actions #45646718328: RED; formal bağımsız insan/Claude review 0. H2 reader testinin yeşil olması global C1 kabulü değildir.

## Güvenlik ve kapsam
- İŞKUR ilan detay sayfaları ve Kariyer Kapısı kazınmadı.
- robots belirsiz/engelli hedeflerde istek yapılmadı; login/CAPTCHA aşma yok.
- SSL doğrulaması kapatılmadı; tarayıcı taklidi yok.
- Token, CV, KPSS veya e-Devlet verisi kullanılmadı.
- Bu görevde yalnız bu Markdown dosyası yazılacak; kod/PR/merge/deploy yapılmayacak.

## SONRAKİ ADIM
1. Cross-feed dedup için H2 ve e-Şube kartlarını ortak externalId/canonicalUrl anahtarıyla test eden küçük, bağımsız QA görevi.
2. 10 kaynak / 100 doğrulanmış açık kart hedefi için kamu ve özel kaynakları ayrı ölç; özel ATS/API ancak izin/lisans doğrulanınca.
3. Production GET/D1 erişimi kanıtlanmadan Worker deploy veya uygulama entegrasyonu yok.
