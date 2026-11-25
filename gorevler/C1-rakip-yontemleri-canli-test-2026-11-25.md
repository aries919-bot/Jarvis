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


---

## EK DENETİM — C1 çapraz kaynak ve H1 haber doğrulama (25.11.2026)

### Rakip mimarileri: ek bulgular
- ATS adapter katalogu: https://github.com/Mersetti/ats-job-board-apis — 11 ATS'nin public JSON/XML/RSS endpoint'leri ve pagination farkları. Greenhouse/Lever/Ashby/Workable/Recruitee/Personio/SmartRecruiters/Workday/Breezy/Teamtailor/Rippling.
- https://github.com/mherzog4/job-boards — işveren board slug'larını keşfedip public API'leri kullanma yaklaşımı. Global tek iş ilanı endpoint'i yok.
- Adzuna/Jooble API: https://developer.adzuna.com/ ve https://jooble.org/api/about — lisans/kota/ülke koşullarıyla ayrı agregatör adapter'ı; agregatör tek kaynak sayılır.
- Google JobPosting: https://developers.google.com/search/docs/appearance/structured-data/job-posting — datePosted/validThrough/organization/location alan standardı; Indexing API başka şirketlerin ilanlarını çekmez.
- Ground News: https://ground.news/about — aynı olay haberlerini story altında toplar; publisher bias değerlendirmesi tek makalenin doğruluk hükmü değildir.
- GDELT: https://gdeltproject.org/data.html — çok dilli haber keşif sinyali; doğruluk kanıtı değil, 429/rate limit ve boş/HTML yanıt kontrolü gerekir.
- Google Fact Check API: https://developers.google.com/fact-check/tools/api/ — ClaimReview ile önceden kontrol edilmiş iddia araması; API key gerektirir.
- Reuters Fact Check: https://www.reuters.com/fact-check/about/ — iddia/ilk kaynak/lehte-aleyhte kanıt/insan incelemesi/düzeltme geçmişi yaklaşımı.

### Kod denetimi ve bulunan kök neden
Backend `aries919-bot/jarvis-cloud`:
1. `src/career-feed.js` yalnız Lever/Ashby adapter'larını içeriyor; diğer ATS'ler henüz yok.
2. `dedupeJobs()` anahtarı `sourceId + providerId`; aynı ilan başka sourceId'de farklı kimlikle gelirse tekilleştirmiyor.
3. `src/career-feed-store.js` listing id ile birleştiriyordu; H2/e-Şube farklı ID kullanınca çift sayım riski vardı.
4. `src/h2-input.js` H2 ilanlarında `city: null`; şehir kanıtı yokken otomatik birleştirme yanlış ilanı silebilir.
5. `.github/workflows/validate.yml` PR base filtresi `fix/c1-h2-official-feed` dalını kapsamıyordu; C1 fix branch filtresi genişletildi.

### Uygulanan kod — PR #10
https://github.com/aries919-bot/jarvis-cloud/pull/10

- Kesin eşleşme yalnız normalize kurum + pozisyon + şehir + deadline birebir aynıysa.
- Birleşen kartta sourceIds/sourceEvidence ve en eski firstSeenAt korunur.
- Şehir eksikse otomatik birleştirme yok; olası tekrar olarak işaretlenir ve doğrulanmış tekil ilan sayısına girmez.
- Tarih/şehir farklıysa ayrı kart.
- Bir kaynak stale/500 olsa da diğer fresh kaynağın kanıtı korunur.
- 4 regresyon testi: kesin eşleşme, farklı tarih/şehir, şehir eksik, stale kaynak izolasyonu.
- PR açık; merge/deploy yapılmadı. Head commit: `09e375a7f6745b1928e41ca88956efc8ef428224`.

### CI sonucu / kök neden
- Actions run #45649406146: FAILURE.
- Job #139329455436: `steps=[]`; log endpoint 404 BlobNotFound.
- Bu nedenle test adımları başlamadı; **PASS denemez**.
- Önceki C1 denemelerinde de aynı runner_id/boş steps belirtisi görülmüştü. Körlemesine rerun yapılmayacak; Actions runner/plan/izin ayarları veya GitHub Support incelemesi gerekir.

### H1 haber doğrulama uyarlaması — ayrı iş
H1 mevcut kart şeması korunur. Ayrı kanıt katmanı:
- storyId: aynı olayın haberlerini gruplar.
- claimId/claimText: tek sınanabilir iddia.
- originUrl: ilk kaynak; bilinmiyorsa null.
- evidence[]: destekleyen/çelişen kaynak, resmî URL, collectedAt.
- verificationStatus: Doğrulandı / Çelişkili / Bağlam Eksik / Doğrulanamadı.
- reviewedAt/correctionHistory[]: inceleme ve düzeltme geçmişi.

Akış: izinli RSS/API ile haber keşfi → canonical URL/olay kümeleri → sınanabilir iddia → ilk kaynak → lehte/aleyhte kanıt → insan incelemesi → düzeltme geçmişi. Haber kopyaları bağımsız kaynak sayılmaz; haber sayısı doğruluk oyu değildir. GDELT/ClaimReview boş yanıtı veya 429 hatası doğruluk etiketini değiştirmez.

### Güncel sayım sınırı
- Önceki birleşik kamu ölçümü: 6 kaynak / 47 doğrulanmış açık kart; 10/100 eşiği nedeniyle RED.
- H2 ayrı reader: 13 açık doğrulanmış kart.
- e-Şube ayrı snapshot: 42 açık benzersiz kayıt.
- Cross-feed kanıtı olmadan 13+42=55 tekil ilan değildir.
- PR #10 CI geçip canlı ortak artifact oluşmadan yeni birleşik toplam verilmez.

### Sonraki adım
1. Runner kök nedenini çöz; PR #10 exact-head testlerini çalıştır.
2. e-Şube/H2 ortak feed canlı yeniden sayımı: kaynak başına ham/tekil/olası tekrar/açık kart.
3. ATS yeni adapter'larını yalnız ölçülmüş izinli board'larda ekle.
4. H1 story/claim/evidence/correctionHistory için ayrı görev dosyası.
5. Production authenticated GET/D1 kanıtı olmadan deploy/Android entegrasyonu yok.


### Actions runner arızası — kök neden araştırması
- GitHub resmi log belgesi: https://docs.github.com/en/actions/how-tos/monitor-workflows/use-workflow-run-logs — normal bir job'da GitHub'ın eklediği “Set up job” ve “Complete job” adımları da görünür.
- GitHub troubleshooting: https://docs.github.com/en/actions/how-tos/troubleshoot-workflows — branch/path filtreleri workflow'un oluşturulmasını engelleyebilir; PR #10'da branch filtresi zaten düzeltildi ve run oluştu.
- Aynı belirtiyi anlatan topluluk kayıtları: https://github.com/orgs/community/discussions/213925 ve https://github.com/orgs/community/discussions/209028 — steps boş + runner_id 0 + BlobNotFound, job'ın runner'a atanmasından önceki dispatch/provisioning aşamasında kesildiğiyle uyumlu.
- Olası nedenler: private repo Actions dakika/bütçe sınırı, repo/organizasyon Actions politikası veya GitHub runner provisioning/platform sorunu. Bu hesap/repo ayarlarına buradan erişim yok; kesin neden seçilemedi.
- Sonuç: YAML komutları veya test kodu henüz çalışmadığı için PR #10 CI RED/kanıtsızdır. Workflow'u aynı şekilde tekrar çalıştırmak kök nedeni çözmez. Ayarlar/usage ve check-run annotation incelenmeden yeniden deneme yapılmayacak; gerekirse GitHub Support'a run #45649406146 ve job #139329455436 verilmeli.


### PR #10 son statik inceleme düzeltmesi
- Aynı ilan için önceki birleşik kart ile güncel kaynak satırı aynı listing id'yi taşıyorsa byId birleştirmesi kaynak kanıtlarını kaybedebiliyordu. Kaynak başına tek evidence kaydı korunacak, fresh kanıt stale kanıttan öncelikli olacak şekilde düzeltildi.
- Son backend head: `043fcf15c26af05fcc9c79e91fbd7bccd8ce6d3d`.
- Önceki Actions #45649406146, `09e375a7f6745b1928e41ca88956efc8ef428224` head'inde runner'a ulaşmadan başarısız oldu; bu son düzeltmeyi test etmedi. Son head için birleşik status boş; PASS yok.
- PR #10 base branch'i parent PR #9 açık olduğu için hâlâ hareket ediyor; compare sonucu head 8 commit ahead / 5 behind. Parent dal sabitlenmeden rebase/merge yapılmayacak.
