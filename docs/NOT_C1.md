# ARIES NOTU v3 FINAL — 24 Kasım 2026

> TALIMAT.md'ye ektir. Çelişki halinde bu not geçerlidir. Eski v2 ve v3 yamasının tamamının yerine geçer.
> Kalıcı hedef: `docs/NOT_C1.md`, `durum` dalı.

## 0. Rol ve ilk tur

- Cline KULLANILMIYOR. Kod, test, PR ve denetim işlerini ChatGPT yürütür.
- Cem'den komut, Termux veya yapıştırma istenmez. Telefon doğrulaması yalnız gerçek cihaz gerektiren adımlarda kalır. Her PR için ayrıca merge onayı istenmez; daha önce verilen koşullu birleştirme yetkisi uygulanır.
- İlk turda şu yetkiler ayrı ayrı sınanır ve raporlanır: (a) dal açma + dosya yazma, (b) PR açma, (c) Actions sonucunu okuma. Biri başarısızsa durulur; yetki sorunu ve tüm işlerin durum/sonraki adım bloğu verilir.
- Kendi PR'ını sıfırdan ve şüpheci biçimde incele. `main`'e doğrudan yazma. Her iş ayrı dalda yapılır, PR açılır, CI yeşil olana kadar düzeltilir. Hakem yoksa/erişilemiyorsa ChatGPT öz-QA yapıp açık karar kaydeder; koşulların tümü geçerse daha önce verilmiş koşullu yetkiyle merge eder.

## 1. Sert kurallar

1. Tek iş alanı İş & Kariyer. Önce Haber doğrulama (H hattı), sonra C1. Tema, film ve bildirim işi yok.
2. “Bitti” kanıtı: çalışan komut çıktısı, kaynak başına ilan sayısı, CI bağlantısı. Sayılar hakem tarafından yeniden sayılır.
3. Kamu ayrı raporlanır; kamu ilanı 0 ise RED. Kamu şartına ek eşik en az 10 kaynak ve 100 doğrulanmış gerçek ilandır. Altındaysa gerçek sayı yazılır ve arayüz işine geçilmez.
4. Tarih uydurulmaz. Alanlar: `publishedAt` (yoksa null), `deadline`, `collectedAt`, `firstSeenAt`. Çekim tarihi yayın tarihi değildir. e-Şube'de `publishedAt=null`, `deadline=son başvuru`. `deadline < bugün` olan ilan güncel listede görünmez. Kurum duyuru başlığındaki tarih çoğunlukla sınav tarihidir; yayın tarihi sayılmaz.
5. İŞKUR ilan sayfaları ve Kariyer Kapısı'nın giriş isteyen bölümleri kazınmaz/yoklanmaz. Uygulama haberi doğrular ve resmî sayfaya Custom Tabs ile yönlendirir; giriş orada yapılır. Girişsiz kaynaklar: e-Şube, ilan.gov.tr, Resmî Gazete, kurum duyuruları ve robots izin veriyorsa büyük şirket kariyer sayfaları.
6. Son iyi veri kaynak bazlıdır. Bir kaynak düşerse yalnız o kaynağın verisi stale olur.
7. Sekmeler yalnız kendi verisini gösterir. Dış site Custom Tabs ile açılır; beyaz ekran olmaz.
8. Her PR'da gerçek ilan, bozuk internet, boş yanıt, HTTP 403/500, timeout, robots engeli ve eski veri testleri bulunur.
9. Yasaklar: robots ihlali, giriş/CAPTCHA aşma, LinkedIn/Indeed, film akışı kazıma, SSL doğrulamasını kapatma, token/CV/KPSS/e-Devlet bilgisi. User-Agent: `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)`; tarayıcı taklidi yok.
10. Ağır toplama GitHub Actions'ta yapılır. Worker (ücretsiz sınırlar: 10 ms CPU, 50 dış istek) JSON'u ayrıştırmadan ham saklar ve `/kariyer-feed` ile sunar. Telefondaki DuckDuckGo/Brave/Bing kazıması kapatılır.
11. Her yanıt şu alanlarla biter: KARAR, Kanıt, Eksik, Telefonda denenecek, SONRAKİ ADIM.

## 2. Haber kartı (H1)

Kategoriler: Tümü, Kamu, Kura, KPSS, KPSS'siz, Büyük özel, Kamu bağlı şirket.

Her kartta: kurum, başlık, kategori, resmî kaynak linki, nasıl başvurulur (nereden), şartlar, `deadline`, `publishedAt` (yoksa null), `collectedAt`, `firstSeenAt`, güncellik etiketi.

Etiketler:
- **Doğrulandı:** tarih var ve başvuru süresi açık.
- **Süresi geçti:** son başvuru tarihi geçmiş.
- **Doğrulanamadı:** tarih yok veya açık süre kanıtlanamıyor.

Kura, sonuç ve staj Haber'de kalır; “yeni iş ilanı” sayılmaz. Tarihi bulunmayan ilan açık kabul edilmez.

## 3. Kanıtlanmış bulgular

- `iskur.gov.tr/ilanlar/*`: boş kabuk; ilan 0, tarih 0, XHR yok. Kaynak değildir.
- e-Şube `https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx`: giriş yok, CAPTCHA yok, robots izinli. Kamu filtresi + Ara postback + `btnNext`. Satırdaki tarih son başvuru tarihidir. 11 haneli ilan no tekilleştirme anahtarıdır. Betik: `scripts/esube_kamu_okuyucu.py`.
- K1-E1: Actions #45388925995 SUCCESS; 3 sayfa, 43 ilan, 8 kurum. `career/esube_kamu_ilanlari.json` durum dalında; 43 benzersiz, 0 tekrar. Cem 24.11.2026'da 3 ilanı elle doğruladı: 00009905681 (Muğla), 00009905686 (İzmir), 00009905689 (İstanbul); MSB Personel GM, son başvuru 27.11.2026. Sayfa altında “Toplam Kayıt: 43”.
- `csgb.gov.tr/duyurular`: ikincil kaynak. “alım/ilan” başlığı süzülür, “kazananlara ilişkin” elenir. URL sonundaki sayı yayın tarihi olarak doğrulanmamıştır.
- `ilan.gov.tr`: sertifika zinciri sorunu var; Actions'ta ölçülür, TLS doğrulaması kapatılmaz.
- C1-1 güncel durum: private `jarvis-cloud` PR #5 HEAD `d78df610` hâlâ açık/taslak ve main hedefli; 13 dosyalı diff 60 KB hakem sınırını aşıyor. Timestamp PR #6 açık/taslak; kategori düzeltmesi PR #8 açık/taslak ve PR #6 tabanında (yalnız 2 dosya +9/-0); önceki yanlış tabanlı PR #7 kapatıldı. Paralel exact-head QA [#45504819883](https://github.com/aries919-bot/Jarvis/actions/runs/45504819883): 38/38 test, 11/11 local D1, 12/12 regresyon senaryosu, kamu tarih sözleşmesi, kaynak kategori ve security diff PASS. Canlı ölçüm: 16 toplam/10 fresh/6 stale; 9 private fresh kaynaktan 114 adayın tümü deadline unknown; 6 Ashby robots HTTP 401 stale, API çağrısı yok. Kamu ayrı: e-Şube 1 kaynak/43 unique/42 açık/1 süresi geçmiş; snapshot 10,9 saat. Doğrulanmış kaynak/ilan sayısı 1/42; 10/100 RED. Production authenticated Worker GET BLOCKED; deploy yok. Hakem raporu `qa-reports/c1/latest.json`, karar RED. Ayrıntı: `gorevler/C1-1.md`, `gorevler/C1-source-category.md`.
- Mevcut `cline/K1` dalındaki eski e-Şube okuyucu/workflow kanıt olarak korunur; bu not kapsamındaki yeni işler ayrı dallarda yapılır. Cline kullanılmaz.

## 4. HARITA

`docs/HARITA.md` içinde şu kimlikler eklenir/güncellenir: K1-E1, K1-F2, H1, K1-B, K1-C, H2, C1-1, C1-2, K1-M, U1. K1-F iptal edilmiştir.

## 5. Kesin görev sırası

“Devam et” denince HARITA'da ilk DEVAM işi seçilir; DEVAM yoksa ilk YAPILMADI işi seçilir. Aşağıdaki sıra ve bu notun kuralları geçerlidir:

1. **K1-E1 — KABUL/RED:** Actions yeşil ve kullanıcı tarafından elle doğrulandı; 3. bölümdeki kanıtla KABUL kararı kaydedilir. Bu kabul, 10 kaynak/100 ilan eşiğinin sağlandığı anlamına gelmez.
2. **K1-F2:** Yalnız `.github/workflows/iskur-render.yml` ve `scripts/render_probe.py` silinir. Başka dosyaya dokunulmaz. Testlerin 46'sı yeşil kalmalıdır.
3. **H1:** `docs/HABER_SEMA.md` oluşturulur; 2. bölümdeki kart şeması ve her kategori için 2–3 resmî duyuru sayfası adayı yazılır.
4. **K1-B:** `.github/workflows/kaynak-yokla.yml` oluşturulur. `workflow_dispatch` ve push tetikleyicisi (yalnız kendi dalı ve kendi dosya yolu) kullanılır. `https://www.ilan.gov.tr` ve `https://www.resmigazete.gov.tr` için robots.txt, ana sayfa ve `/sitemap.xml` HTTP kodu, bayt ve hata bilgisi `curl -sS -w` ile ölçülür. ilan.gov.tr için `openssl s_client -connect www.ilan.gov.tr:443 -servername www.ilan.gov.tr` ile sertifika zinciri ölçülür. `-k` yasaktır. Robots izin vermiyorsa o yola istek gönderilmez. Sonuç `GITHUB_STEP_SUMMARY`'ye yazılır.
5. **K1-C:** `KARIYER_KAYNAK_DENETIMI.md` ve `career/adaylar.txt` yalnız gerçekten ölçülmüş adreslerle hazırlanır. Her kaynak ÇALIŞIYOR / ENGELLİ (nedeniyle) / ÖLÜ olarak sınıflanır. En az bir kamu kaynağı ÇALIŞIYOR olmalıdır.
6. **H2:** Ölçülmüş duyuru sayfalarından H1 şemasına uygun JSON üreten okuyucu geliştirilir; sonuç durum dalına PR ile sunulur.
7. **C1-1:** jarvis-cloud PR #5 düzeltilir: kimlik doğrulamalı `/kariyer-feed`, kalıcı `firstSeenAt`, kaynak bazlı son iyi veri, kamu girdisi olarak durum dalındaki JSON, 1. bölümdeki hata testleri ve `npm run test:c1`.
8. **C1-2:** Canlı rapor üretilir; kaynak başına ilan sayısı ve kamu ayrı gösterilir. Eşik sağlanmıyorsa RED ve gerçek sayı yazılır.
9. **K1-M:** `kamu-ilan-cek.yml`, `kaynak-tarama.yml` ve gerekli betikler temiz bir daldan main'e PR edilir. Film commitleri alınmaz. Cron yalnız main'e birleşince çalışır.
10. **U1:** Uygulamada arama motoru kazıması kapatılır, JSON okunur, Haber sekmesi H1 şemasına bağlanır ve dış bağlantılar Custom Tabs açar. Cem telefonda 3 kartı resmî sayfayla karşılaştırır.

## 6. Bitiş ve raporlama

- Her iş ayrı dal + PR ile yürütülür; `main`'e doğrudan yazılmaz. PR diff'i sıfırdan incelenir; gerekli testler ve Actions yeşil olmadan kabul edilmez.
- Her iş için kanıt: komut/test çıktısı, kaynak başına ilan sayısı (ilgiliyse), CI bağlantısı ve yeniden sayım.
- Kamu ilanı 0 ise RED. 10 kaynak/100 doğrulanmış ilan eşiği sağlanmadıysa gerçek sayılar raporlanır ve arayüze geçilmez.
- Gerçek cihaz gerektiren telefon doğrulaması tamamlanmadan telefon kabulü verilmez. Ancak her PR için ayrıca “birleştir” onayı sorulmaz; exact-head CI, kapsam, öz-QA/Hakem ONAY ve tüm görev kapıları geçince koşullu birleştirme yetkisi kullanılır.
- Sonuç biçimi:
  - KARAR: KABUL/RED
  - Kanıt:
  - Eksik:
  - Telefonda denenecek:
  - SONRAKİ ADIM:


## 7. K1-B doğrulama düzeltmeleri — 24.11.2026
- **Kamu ilan kaynağı:** yalnız e-Şube; 43 benzersiz ilan. 10 kaynak/100 ilan eşiği karşılanmadı.
- **BOTAŞ:** 2025 personel alımına ilişkin sonuç duyurusu; güncel iş ilanı değil, etiket **Süresi geçti**.
- **YHGM:** 2.764 sürekli işçi kura/sonuç duyurusu; güncel iş ilanı değil, Haber sekmesinde **Kura** olarak sınıflanır.
- TLS beş hedefinde hata CERTIFICATE_VERIFY_FAILED: unable to get local issuer certificate; hata süresi dolmuş sertifikayı kanıtlamaz. SSL doğrulaması açık kalır.
- Resmî Gazete'de robots izinli iki timeout hedefi birer kez yeniden denendi; tekrar timeout. TPAO PDF için robots izni belirsiz olduğundan tekrar istek yok.

## 8. Paralel Actions QA ve Hakem — 25.11.2026
- `.github/workflows/c1-private-backend-qa-public.yml` ayrı Actions job'ları çalıştırır: exact-SHA preflight, birim test, local Worker+D1, canlı kaynak audit, kamu tarih sözleşmesi, hata senaryosu matrisi, security/diff ve production GET-only kontrolü. Son Hakem job'ı artifact JSON'larını toplar, sayıları yeniden değerlendirir ve run JSON'unu `qa-reports/c1/latest.json` ile birlikte `durum` dalına kaydeder.
- [Run #45504819883](https://github.com/aries919-bot/Jarvis/actions/runs/45504819883): 38/38 C1 test PASS; local Wrangler+D1 11/11 PASS; 12/12 senaryo kapsamı; kaynak kategori kontrolü ve e-Şube tarih/freshness sözleşmesi PASS; diff/security PASS.
- Hakem **RED**: production authenticated GET BLOCKED ve 10 kaynak/100 doğrulanmış ilan eşiği karşılanmadı (1 kaynak/42 ilan). Eşik, kamu kapısı ve tarih kuralları değiştirilmedi.
- Robots HTTP 401 veren Ashby kaynaklarına API isteği gönderilmez; stale kalırlar. Deadline unknown özel adaylar 100 doğrulanmış ilan sayısına eklenmez.
- Workflow şu an `qa/c1-private-runner` dalında push ve `workflow_dispatch` ile çalışır; varsayılan `main` dalına taşınmadığı için zamanlanmış sürekli çalışma iddiası yoktur.


## 9. Toplu Hakem ve tazelik güncellemesi — 25.11.2026
- Toplu iç denetim dosyası: `gorevler/C1-toplu-hakem-25-11.md`; PR #8 yorum ID `7147163607`. Bu, ChatGPT'nin iç denetimidir; bağımsız Claude/insan hakem incelemesi yapılmış sayılmaz.
- PR #6 ve PR #8 birleşik değişiklik karşılaştırması: 7 commit / 7 dosya. #6 snapshot zamanı ile feed deneme zamanını ayırır; #8 kategori alanını success/robots/error sonuçlarında saklar. Son exact-head CI: 38/38 test, 11/11 local D1, 12/12 hata senaryosu PASS.
- **C1 kararı RED.** Ölçülmüş son sayım 1 kaynak/42 ilan; zorunlu eşik 10 kaynak/100 doğrulanmış, son başvuru tarihi geçmemiş ilan. 114 özel aday deadline unknown olduğu için sayılmaz. Üretim authenticated GET, `JARVIS_WORKER_URL`/`JARVIS_WORKER_TOKEN` ayarları eksik olduğundan BLOCKED.
- e-Şube snapshot zamanı `2026-11-24T11:57:55Z`; 25.11.2026 itibarıyla 12 saatlik tazelik penceresinin dışındadır. Önceki 42 açık ilan artık güncel kanıt olarak sayılamaz; yeni çekim olmadan güncel kamu sayımı 0 / RED.
- H2 workflow `45489853859` attempt 2 ile yeniden çalıştırıldı. Reader job `138848189009`, son kontrolde `Read measured official announcements` adımında in_progress; yeni sayılar henüz kanıtlanmadı.
- PR #5/#6/#8 birleştirilmedi. 10/100 eşiği, kamu sıfır RED şartı, robots ve deadline kuralları değişmedi.
- Sonraki adım: H2 ve e-Şube taze kaynak çekimini bitir; kaynak başına ilan sayısını ve her deadline'ı yeniden doğrula; ardından yeni toplu Hakem çalıştır. Üretim GET doğrulanmadan ONAY yok.

## 10. Güncel H1/H2 + C1 sayımı — 25.11.2026 03:03 TRT

- H1 PR #35 HEAD 9785696ac4788805d23ad327e3690a75a521eb2c; Actions #45514829808 SUCCESS, 12/12 test. Sayaç publishedAt kanıtını, fresh kaynağı, son tarihi ve varsa applicationOpensAt açılışını kontrol eder. PR açık; formal review yok; merge yok.
- H2 PR #57 HEAD ae51188ff2bb7c55ef813be7205825b0c5b05db0; reader #45512710235/artifact 14976107324. 10 kaynak tanımı, 7 fresh/3 stale; 5 fresh kaynaktan 5 doğrulanmış açık iş kartı: Anadolu, Düzce, Marmara, Tarsus, Zafer. ATK henüz açılmadı; YHGM geçmiş kura; Bilecik/BOTAŞ/ÇOMÜ stale.
- e-Şube durum commit c5d1a1d9c105703c3766659e7f9a3be1fbb01b18; snapshot 2026-11-24T23:51:48Z, 42 benzersiz açık kayıt, 0 geçmiş, 42 deadline, publishedAt=null. 12 saat stale sınırı 2026-11-25T11:51:48Z.
- Ayrı feed'lerin manuel birleşik kamu sayımı: 6 kaynak / 47 doğrulanmış açık iş kartı. Bu tek Worker feed'i ve telefon entegrasyonu değildir.
- C1 özel ATS son denetiminde 114 adayın tamamı deadline unknown; altı Ashby robots HTTP 401 stale. Bu kayıtlar 100 doğrulanmış ilana eklenmez.
- Global kabul RED: 10 kaynak / 100 ilan eşiğine karşı fiilî 6 / 47.
- C1 otomatik Hakem latest.json kendi Actions #45504819883 tarihsel görüntüsüdür; eski e-Şube 43/42 sayısını içerir. Yeni manuel ek rapor qa-reports/c1/manual-recount-2026-11-25.json dosyasıdır.
- Production authenticated GET BLOCKED; JARVIS_WORKER_URL / JARVIS_WORKER_TOKEN yapılandırması kanıtlanmadı. PR #5/#6/#8 ve #35/#57 merge edilmedi.

Kurallar değişmedi: tarih uydurulmaz; e-Şube publishedAt=null; deadline yalnız kaynak son başvuru tarihi; robots unknown/401'de hedef API çağrısı yok; deadline unknown kayıtlar doğrulanmış sayılmaz.

## 11. H2'nin C1 Actions feed'ine entegrasyonu — 25.11.2026 03:25 TRT
- jarvis-cloud PR #9: `fix/c1-h2-official-feed` → PR #8 tabanı `fix/c1-source-category-after-timestamp`, açık/taslak; HEAD `8a43b99981db601de3442e8394b71d0651d0e34e`; 5 dosya / +442 / -10 / 22.465 bayt. Ayrıntı: `gorevler/C1-H2-entegrasyon-25-11.md`.
- H2 reader exact source pin `ae51188ff2bb7c55ef813be7205825b0c5b05db0`; Actions'ta pypdf 5.4.0, robots-first H2 reader, last-good cache/firstSeenAt seed çalıştı. H2 parser+seed testi SUCCESS.
- Public-runner exact-head [#45517626605](https://github.com/aries919-bot/Jarvis/actions/runs/45517626605): preflight PASS; 46/46 Node test PASS; 11/11 local Wrangler+D1 PASS; 17/17 regresyon matrisi PASS; e-Şube date contract PASS; 5 dosyalı security/diff PASS (22.465 bayt, 0 forbidden-pattern match, doğru UA).
- Live collector: 26 kaynak /17 fresh /9 stale; özel ATS 114 deadline-unknown; kamu 6 source /47 current verified cards; threshold 10/100 RED. Kaynak başına sayım görev notundadır. H2 beş açık kart + e-Şube 42 tek Actions collector çıktısında birleştirildi.
- e-Şube: snapshot `2026-11-24T23:51:48Z`, 42/42 unique/open, publishedAt=null.
- H2: 10 source /7 fresh/3 stale; açık kartlar Anadolu, Düzce, Tarsus, Marmara, Zafer. ATK henüz açılmadı; YHGM kura. BOTAŞ timeout, ÇOMÜ robots UNKNOWN ve Bilecik robots timeout; hedeflere izin belirsizken ek istek yok.
- Özel kaynak sayıları: Trendyol 35, Midas 16, Commencis 5, Dream Games 17, Aleph 5, Ajax Systems 13, Insider One 22, Binance 0, Picus 1 =114; 114/114 deadline unknown. Altı Ashby robots HTTP 401 stale.
- Hakem [#45517626605](https://github.com/aries919-bot/Jarvis/actions/runs/45517626605) RED; report JSON `qa-reports/c1/latest.json` güncellendi. Public gate 6/47 pozitif ama 10/100 sabit eşiği karşılanmadı.
- Production authenticated GET BLOCKED; Worker URL/token secret çifti yapılandırılmamış. Publish SKIPPED, POST/deploy yok. Worker/Android canlı entegrasyon kanıtı yok.
- PR #9 formal GitHub reviews 0; bot yorumları bağımsız onay sayılmaz; hiçbir PR merge edilmedi.
- Tarih kuralları değişmedi: yayın tarihi yoksa null; collectedAt yayın tarihi değildir; deadline unknown kayıtlar sayılmaz; gelecekteki applicationOpensAt açık ilan değildir; kura/sonuç iş ilanı değildir.

## 12. Hakem yoksa ChatGPT öz-QA — 25.11.2026

1. Bağımsız Hakem/Claude yoksa veya Hakem CI yalnız isteğe bağlı AI erişimi eksik olduğu için çalışmıyorsa ChatGPT exact HEAD üzerinden kendi QA ve hakem kararını verir. Bu karar **öz-QA** olarak yazılır; bağımsız insan/Claude onayıymış gibi gösterilmez.
2. Öz-QA; PR tabanını ve head SHA'yı, diff dosya/adet/ekleme-silme sayılarını, yalnız görev kapsamındaki dosyaları, test komut ve Actions sonuçlarını, robots/TLS/403/500/timeout/boş yanıt/stale senaryolarını, kaynak başına açık ilan ve kurum sayısını, tarih sözleşmesini, güvenlik desenlerini ve kaynak kodunu baştan inceler.
3. Sonuç PR yorumuna ve `gorevler/<id>.md` + HARITA'ya `ChatGPT öz-QA: ONAY/RED` biçiminde yazılır. Dış hakem yok bilgisi açıkça belirtilir.
4. Test/CI başarısızsa, diff kapsam dışıysa, statik incelemede kusur varsa, veri/robots/tarih kanıtı yetersizse veya görevin kendi kabul eşiği geçilmediyse RED verilir ve merge yapılmaz. Öz-QA, C1'in 10 kaynak/100 doğrulanmış ilan, kamu 0=RED ve production GET kapılarını gevşetmez.
5. Öz-QA ONAY yalnız ilgili PR kapsamını kabul eder. H1 şema düzeltmesinin ONAY'ı tüm H1 kaynaklarını; H2 reader ONAY'ı C1 global hedefini; local D1 testi production'ı; Actions live artifact'i Android telefon davranışını kabul etmez.
6. Önceden verilen koşullu merge yetkisi gereği, exact-head testler yeşil, diff yalnız kapsam içi, öz-QA/Hakem ONAY ve görevin diğer tüm zorunlu kabul kapıları sağlanmışsa ayrıca kullanıcı onayı sorulmadan birleştirilir. Aksi durumda PR açık/RED kalır.


## 13. P0 kaynak politikası ve H1 arayüz entegrasyonu — 25.11.2026
- C1 P0 için aktif PR #70: https://github.com/aries919-bot/Jarvis/pull/70. Exact head `bf8ca39c1dee78b8436069e5cfabcbbd3afbc146`; 4 dosya /+46 /-13; Android unit tests ve APK [#45658875411](https://github.com/aries919-bot/Jarvis/actions/runs/45658875411) SUCCESS. Indeed/LinkedIn kaldırıldı; genel .edu.tr trusted değil; UA `AriesKaynakTara/1.0`.
- Haber H1 için aktif PR #69: https://github.com/aries919-bot/Jarvis/pull/69. 8 dosya /+1.572 /-35; 18 yeni test. Local archive, append-only correctionHistory, claim/evidence/review UI ve Custom Tabs bağlandı.
- Browser 1.10.0 mevcut compileSdk34/AGP8.5.2 ile uyumsuz çıktı; Browser 1.8.0'a sabitlendi. Güncel exact-head #45665491590'da unit test task SUCCESS, APK assemble adımı son kontrol sırasında çalışıyordu.
- PR #67/#68 kapatıldı (unmerged) ve #70/#69 ile değiştirildi. Hiçbir PR merge/deploy edilmedi.
- Bu işler C1'in kaynak sayısı kapısını değiştirmez. 10 kaynak/100 doğrulanmış ilan, kamu ayrı raporu, üretim Worker GET ve e-Şube/H2 cross-feed dedup kapıları hâlâ zorunlu. Kaynaklardan herhangi biri sıfırsa/başvuru süresi geçmişse kurallar gevşetilmez.
- Eski Haber arşivi otomatik VERIFIED yapılmaz; source publishedAt yoksa null kalır. Telefon doğrulaması ve Full App QA henüz yok.

## 14. H1 SQLite entegrasyonu ve veri güvenliği — 25.11.2026
- Güncel H1 entegrasyon adayı PR #71: https://github.com/aries919-bot/Jarvis/pull/71; base `kariyer-modulu`, HEAD `3a1017528ebb1d87ca8e7f87022ae610ce1bdc6c`.
- Exact compare: 52 commit /8 dosya /+1.979 /-26 /0 behind. 17 model testi +8 store testi =25 @Test.
- SQLite primary + last-good aynı transaction'da yazılır; primary bozuksa yedek okunur, iki kopya bozuksa üzerine yazılmaz. Kapasite 300 hikâye, hikâye 16 KB, 50 claim, claim başına 25 evidence, 100 correction; limitte eski kayıt atılmaz.
- Aynı URL için başlık güncellense de storyId sabit; önceki evidence/review/correction history silinemez; kart listesi doğrulama durumlarını tek toplu SQLite sorgusuyla getirir.
- PR #71 ilk exact-head run #45663064814 duplicate test tanımı yüzünden RED. Duplicate silindi. Ara HEAD run #45666479386 SUCCESS; yeni testler/store/performans sağlamlaştırması sonrası son HEAD için [#45667520387](https://github.com/aries919-bot/Jarvis/actions/runs/45667520387) başlatıldı; final sonuç bekleniyor.
- PR #69 H1 temel+SharedPreferences/UI uygulaması için exact-head [#45665491590](https://github.com/aries919-bot/Jarvis/actions/runs/45665491590) SUCCESS; PR #71 SQLite/last-good tasarımıyla onun yerine geçecek adaydır. PR #69/#71 henüz merge edilmedi.
- C1 P0 PR #70 exact-head [#45658875411](https://github.com/aries919-bot/Jarvis/actions/runs/45658875411) unit tests + APK SUCCESS. Bu değişikliklerin hiçbirisi C1'in kaynak sayısı veya Worker production GET sonucunu değiştirmez.
- 10 kaynak/100 doğrulanmış ilan, kamu 0=RED, kaynak başına last-good, e-Şube/H2 dedup ve production authenticated GET kapıları aynen zorunludur.

## 15. H1 SQLite entegrasyonu exact-head CI — 25.11.2026
- PR #71 exact HEAD `3a1017528ebb1d87ca8e7f87022ae610ce1bdc6c` Android run [#45667520387](https://github.com/aries919-bot/Jarvis/actions/runs/45667520387) **SUCCESS**.
- `gradle testDebugUnitTest --no-daemon` SUCCESS; `gradle assembleDebug --no-daemon` SUCCESS; APK artifact upload SUCCESS.
- Loglarda test görevi `BUILD SUCCESSFUL in 1m 31s`, APK görevi `BUILD SUCCESSFUL in 50s`. Yeni test count 17 model +8 SQLite store =25 @Test.
- APK artifact ID `15049240153`, size 14,176,012 bytes.
- Full App QA/emulator workflow ve gerçek telefon testi yok. CI PASS tek başına UI/telefon veya production kabulü değildir.
- PR #71 open/draft/unmerged. PR #70 source policy unit + APK run #45658875411 SUCCESS. C1 10 source/100 verified cards, public report, Worker production authenticated GET and cross-feed dedup remain separate/RED/BLOCKED.

## 16. H1 exact-head APK + Android emülatör doğrulaması — 25.11.2026
- PR #71 HEAD `0a376b4c7355a5a6822252da4d9286606ce23bde`; compare `kariyer-modulu`: 57 commit /10 dosya /+2.132 /-26 /0 behind.
- Test sayımı: 17 model +8 store +2 Android instrumentation =27 @Test.
- Exact-head APK/Unit run [#45669708914](https://github.com/aries919-bot/Jarvis/actions/runs/45669708914) SUCCESS; artifact `aries-apk` ID `15049965420`, 14,176,024 bytes.
- Emulator workflow [#45669709252](https://github.com/aries919-bot/Jarvis/actions/runs/45669709252) son kontrolde IN_PROGRESS. Workflow PR head SHA'sini açık checkout eder; PR numarası concurrency key'i; duplicate push event kaldırıldı.
- Emulator testleri yalnız Activity açılışı, iddia/kaynak düğmelerinin görünmesi, nullable publishedAt'in SQLite'a yazılması ve SQLite last-good recovery'yi kontrol eder. Bütün claim/evidence/review etkileşimleri henüz UI test edilmedi.
- C1 source-policy PR #70 ayrı: Indeed/LinkedIn kaldırma, generic .edu.tr deny ve Aries UA düzeltmesi [#45658875411](https://github.com/aries919-bot/Jarvis/actions/runs/45658875411) SUCCESS. #70 #71 tabanına henüz birleşmedi; H1 CI PASS, C1 gate sonucu değildir.
- C1 10 kaynak/100 doğrulanmış ilan, kamu 0=RED, source-specific last-good, e-Şube/H2 cross-feed dedup ve production authenticated Worker GET kapıları etkilenmedi; ayrı RED/BLOCKED kalır. Merge/deploy/telefon doğrulaması yok.

## 17. PR #71 üçüncü hakem denetimi — 25.11.2026
- PR #71 son HEAD `4a9a05bda6a699666769bdcb99d1cabb122991c0`; 10 dosya /+2.132 /-26 /0 behind; son commit yalnız emulator workflow env düzeltmesi. 27 test tanımı (17 model +8 store +2 instrumentation) yeniden sayımı önceki kod head'i için geçerlidir.
- Emulator run [#45669709252](https://github.com/aries919-bot/Jarvis/actions/runs/45669709252) FAIL: testler başlamadan Android Emulator 37.3.10 `-accel off` nedeniyle boot edemedi; log `ANDROID_I_WANT_MY_TCG=yes` öneriyor. Düzeltme commit'i `4a9a05bda6a699666769bdcb99d1cabb122991c0` bu env'i ekledi. Yeni APK run [#45673231753](https://github.com/aries919-bot/Jarvis/actions/runs/45673231753) ve emulator run [#45673231634](https://github.com/aries919-bot/Jarvis/actions/runs/45673231634) IN_PROGRESS; henüz PASS yok.
- Hakem RED bulgusu: review kararının değiştirilmesi ve yeni kanıt eklenince eski review'un geçersizleştirilmesi append-only geçmişe kaydedilmiyor. `preservesHistory` yalnız review null'a çekildiğinde ve kanıt aynı kaldığında engel koyuyor; CONFIRM→REFUTE/INCONCLUSIVE overwrite ve yeni kanıtla reset audit dışı kalıyor. Review geçmişi modeli + test zorunlu.
- Ek takip: evidence formu yayın tarihi girişi sunmuyor (`publishedAt=null` sabit); correction UI eski evidence detaylarını göstermiyor. PR #70 kaynak politikası PR #71'den ayrı ve entegrasyon bağımlılığı.
- C1 global gate değişmedi: kamu ayrı raporu, 10 kaynak/100 doğrulanmış ilan, Worker production authenticated GET ve cross-feed dedup hâlâ ayrı RED/BLOCKED. H1 testleri C1 kaynak eşiği yerine geçmez.
- PR #71 hakem kararı RED; merge/deploy yok. Gerçek telefon testi yok.

## 14. H1 UI kanıt tarihi ve emülatör devamı — 25.11.2026
- H1 PR #71 güncel HEAD: `6b392a3aa02d42700743657350d081d7f7ad6aeb`; 10 dosya / +2.578 / -26.
- Review geçmişi append-only: her önceki kararın UTC zamanı, gerekçesi ve evidence ID listesi korunur. Yeni kanıt kararı arşivleyerek sıfırlar; yeni review eski kararı arşivler; Store geçmiş silme/yeniden yazmayı reddeder.
- Evidence formu isteğe bağlı `publishedAt` UTC alanı içerir; boşsa null, geçersiz tarih reddedilir. Correction geçmişi eski kanıtların publisher/sourceType/stance/excerpt/publishedAt/checkedAt ve kaynak linkini gösterir.
- Android UI instrumentation gerçek akışı yürütür: claim ekle → publishedAt'li evidence → human review → yeni evidence ile review arşivi → correction → previous evidence ayrıntısı.
- Test sayımı yeniden: 20 model +10 store +4 Android instrumentation = **34 test**.
- Exact-head APK/unit [#45679523180](https://github.com/aries919-bot/Jarvis/actions/runs/45679523180) ve emulator/SQLite [#45679523114](https://github.com/aries919-bot/Jarvis/actions/runs/45679523114) son kontrolde IN_PROGRESS.
- Önceki emulator #45675851473 yeni commit nedeniyle CANCELLED; PASS değildir. Önceki APK/unit #45675851347 SUCCESS ama yeni UI akışını kapsamıyor.
- **H1 KARAR: RED / beklemede.** Güncel iki exact-head CI PASS ve gerçek telefon testi olmadan merge yok.
- C1 source policy PR #70 exact-head #45677312902 SUCCESS ve öz-QA ONAY; H1 sırasından sonra merge edilecek.
- C1 H2/e-Şube PR #11 hâlâ RED: 12 kamu kaynağı /55 doğrulanmış açık ilan (100 eşiğine 45 eksik); Worker authenticated production GET/D1 ve şehir kanıtlı cross-feed dedup yok. Kamu ayrı raporu ve 10 kaynak/100 ilan kapısı değişmez.
- Ayrıntılı görev: `gorevler/H1-UI-PUBLISHEDAT-EMULATOR-2026-11-25.md`.

## 15. Aries mimari ve dosya düzeni QA — 25.11.2026
- Envanter: `kariyer-modulu` 79 kayıt/30 Kotlin; H1 #71 HEAD 91 kayıt/36 Kotlin.
- CareerActivity yaklaşık 2.795 satır, CareerMonitor 2.049, MainActivity 1.802, Ai.kt 735. CareerMonitor içinde SourceCatalog, Eligibility, ListingParser, SearchEngine, RecruitmentTracker ve WorkManager scheduler/worker birlikte.
- `denetle.py` diff'in ilk 12 KB'sini Groq'a yollar; bu işte çalıştırılmadı. `AGENTS.md` eski Groq/Claude/Cline iş akışındaydı; `DURUM.md` 31 Ekim tarihliydi. İkisi `durum` dalında güncellendi.
- Media altında beş eski kaynak yedeği bulunuyor; H1/C1 kapsam dışı olduğundan değişiklik yapılmadı. `.gitignore` içindeki `gorevler/` ve `HARITA.md` ignore desenleri ayrıca düzeltilmeli.
- Gerçek ayrı ajan aracı mevcut değil; mimari, güvenlik, veri ve CI olarak dört öz-QA geçişi yapıldı; ajan kullanıldığı iddia edilmedi.
- Refaktör sırası: H1 emulator PASS + telefon; sonra PR #70; sonra CareerMonitor sınıf sınırlarına göre küçük dosya bölmeleri; ardından CareerActivity için repository ve page controller ayrımı.
- H1 exact-head APK/unit #45679523180 SUCCESS; emulator #45679523114 son kontrolde IN_PROGRESS. C1 #11 RED: 12 kamu kaynağı/55 doğrulanmış açık ilan; 45 eksik; Worker production GET/D1 ve city dedup eksik.
- **KARAR: RED / mimari temizlik tamamlanmadı.** Envanter ve iş planı kayda girdi; hiçbir kaynak dosyası topluca taşınmadı veya silinmedi.
- Ayrıntı: `gorevler/ARIES-MIMARI-DAGINIKLIK-2026-11-25.md`.

## 16. Paralel QA iş atamaları — 25.11.2026
- QA-01 H1 model/SQLite: kod incelemesi tamam; 20 model +10 store testi.
- QA-02 H1 Android UI/emülatör: APK/unit PASS; exact-head emülatör IN_PROGRESS; gerçek telefon testi bekliyor.
- QA-03 C1 kaynak/UA: PR #70 exact-head CI PASS; H1 sırası bekleniyor.
- QA-04 C1 canlı kamu/Worker: RED; 12 kamu kaynağı/55 ilan, 45 eksik; H2 city null ve production GET/D1 yok.
- QA-05 mimari: dosya/sınıf envanteri tamam; CareerMonitor extraction küçük PR olarak planlandı.
- QA-06 güvenlik/izinler: `denetle.py` Groq diff upload sebebiyle çalıştırılmıyor; manifest izinlerinin kod kullanım haritası açık.
- Gerçek alt ajan çalıştırma aracı olmadığından roller ayrı QA sorumluluklarıdır; harici ajan kullanıldığı iddia edilmez. Koordinatör bütün çıktıları çapraz kontrol eder.
- Görev panosu: `gorevler/QA-EKIP-PANOSU-2026-11-25.md`.
- Paralel hız kuralı: bağımsız dosya/depodaki QA aynı anda; aynı branch/dosya yazımı sıralı; exact-head CI bitmeden ilgili PR merge edilmez.
- **KARAR: RED / genel kabul yok.** Açık kapılar: emülatör + telefon, izin kullanım haritası, kamu 100 ilan eşiği ve Worker GET/D1.

## 17. Android izin akışı QA bulgusu — 25.11.2026
- MainActivity.onCreate satır 140–145 ilk açılışta RECORD_AUDIO, CALL_PHONE, READ_CONTACTS ve POST_NOTIFICATIONS izinlerini tek seferde istiyor.
- MainActivity kaynak taramasında CALL_PHONE ve READ_CONTACTS yalnız bu izin listesinde; ACTION_CALL, ACTION_DIAL, Phone.CONTENT_URI/Phone.query çağrısı bulunamadı. ContactsContract Phone import'u da kullanılmıyor görünüyor.
- RECORD_AUDIO sesli komut, BLUETOOTH_CONNECT kullanıcı ses çıkışı seçimi, POST_NOTIFICATIONS bildirim akışı için kullanılıyor. Reminders.kt setAlarmClock çağırıyor; exact alarm izin gereksinimi ayrıca doğrulanmalı.
- CALL_PHONE ve READ_CONTACTS gereksiz açılış izinleri adayı. Tam repo araması ve regresyon testi sonrası ayrı PR açılacak; henüz kod değiştirilmedi.
- H1 emülatör #45679523114 `in_progress`, conclusion `null`; timeout dolmadan sonuç varsayılmayacak.

## 18. C1 resmî kamu ilanı keşif dalgası — 25.11.2026
- 7 açık duyuru adayı doğrulandı: Sayıştay (16), Zafer (2), TGA (13), Hazine Kontrolörleri Kurulu (en fazla 10), Anadolu (1), Bartın (1), SSB (26). Bunlar pozisyon kontenjanlarıdır; ilan kartı sayısı değildir.
- Yeni kaynak adayı 4: Sayıştay, HMB, Bartın, SSB. H2'de Zafer/TGA/Anadolu zaten bulunduğundan tekrar kontrolü yapılmadan eklenmez.
- Gelecek: ATK 181 pozisyon 30.11'de, YÖKAK 5 pozisyon 01.12'de açılacak; başvuru açılmadan açık sayımına eklenmez.
- Kamu sayımı değişmedi: 12 kaynak/55 ilan; bu araştırmadan feed'e eklenen 0. C1 hâlâ RED, 45 ilan eksiği var.
- Araştırma belgesi: `gorevler/C1-RESMI-ILAN-ARASTIRMASI-2026-11-25.md`.

## 19. Diff güvenliği ve CI hız QA
- PR #71: 10 dosya/+2.578/-26; secret pattern 0; LinkedIn/Indeed URL 0; browser impersonation UA 0; publishedAt literal yalnız testlerde.
- PR #70: 4 dosya/+75/-20; secret pattern 0; fake browser UA 0; AriesKaynakTara/1.0 kullanılıyor; LinkedIn/Indeed dizeleri deny-policy testlerinde.
- Android build workflow iki ayrı Gradle çağrısı kullanıyor; sonraki workflow değişikliğinde testDebugUnitTest + assembleDebug tek çağrı olarak çalıştırılabilir. H1 emülatör job timeout 35 dk, run #45679523114 step 5 hâlâ IN_PROGRESS ve log endpoint BlobNotFound.
- **KARAR:** Diff güvenlik taraması PASS; CI hız optimizasyonu takip işi, mevcut koşu bitmeden workflow değiştirilmez.

## 20. H1 emülatör uzun koşu — 25.11.2026 19:12Z
- Exact HEAD #45679523114 18:52:22Z'de başladı; 19:12:37Z'de hâlâ IN_PROGRESS. Job timeout 35 dk, teorik sınır 19:27:22Z.
- Log endpoint BlobNotFound; bu test sonucu değildir. APK/unit #45679523180 aynı HEAD SUCCESS.
- **KARAR:** H1 emülatör ve gerçek telefon testi kapanana kadar RED/beklemede.

## 21. C1 kaynak politikası PR #70 merge — 25.11.2026
- PR #70 HEAD `7ab7aec5e5fa34209fec5c56d20a3c468af89a78`; 4 dosya/+75/-20; exact-head Android CI #45677312902 SUCCESS; öz-QA ONAY.
- Merge commit `09f98b324a9a1f38dbbc25853ec2eba2fc544db9`; kariyer-modulu branch head bu commit.
- PR #71 H1 base artık source-policy düzeltmesini içeriyor. Mevcut emulator run exact PR head checkout ediyor; #70 ile gerçek entegrasyonun test edilmesi için run kapandıktan sonra workflow merge-ref checkout'a çevrilecek.
- PR #71 hâlâ merge edilmedi; emülatör + telefon testi zorunlu.
- C1 genel 10/100, public feed ve production GET/D1 kabulü değişmedi; RED.

## 22. H1 workflow QA düzeltmesi
- H1 emulator workflow rapor upload adımı zaten `if: always()`; fail/timeout rapor koruması mevcut.
- H1 emulator workflow checkout exact PR head yapıyor; PR #70 merge sonrası entegrasyon için yeni koşuda `github.sha` merge ref test edilmeli.
- Ana Android APK workflow iki ayrı Gradle çağrısı çalıştırıyor; tek çağrıda birleştirme hız optimizasyonu olarak incelenecek.

## 23. Android izin envanteri — düzeltme
- Ana Kotlin kaynakları tarandı. CALL_PHONE/READ_CONTACTS Commands.kt'de gerçek kullanıma sahip; silinmemeli. Ancak MainActivity.onCreate ilk açılışta RECORD_AUDIO, CALL_PHONE, READ_CONTACTS, POST_NOTIFICATIONS topluca istiyor; just-in-time akışa ayrılmalı.
- Reminders.kt setAlarmClock kullanıyor. Android resmi belgelerine göre exact alarm yetkisi gerekir; USE_EXACT_ALARM otomatik verilir ancak alarm/takvim uygulamaları için sınırlıdır. Jarvis'in ana fonksiyonu alarm değilse policy riski vardır. Kullanıcı onaylı SCHEDULE_EXACT_ALARM veya inexact alarm/WorkManager değerlendirilecek.
- **KARAR:** Kod henüz değiştirilmedi; ayrı güvenlik PR'ı.


## 24. H1 emülatör hata kanıtı + retest — 25.11.2026
- Run #45679523114 FAILURE. Artifact 15055314040 XML: 4 instrumentation testi; 1 PASS, 3 FAIL, 0 ERROR, 0 SKIP. Üç UI testi RootViewWithoutFocusException (window-focus=false) ile kaldı; yalnız SQLite last-good recovery testi geçti.
- Logcat 177+ frame skip, 2.5–3.2s renderer jank ve Activity PAUSED geçişi gösteriyor. onCreate senkron SQLite load + koşulsuz save P2 performans işi ayrı kaydedildi; tek kök neden olduğu kesin değil.
- `Trace.forceEnableAppTracing()` NoSuchMethodError için AndroidX Test issue #2248 ve Trace API dokümanı incelendi; app dependency `androidx.tracing:tracing:1.2.0` eklendi. Bu AndroidX Test monitor 1.7.1 düzeltmesiyle karıştırılmamalı; logdaki hata caught/logged idi. Kaynak: https://github.com/android/android-test/issues/2248 ; https://developer.android.com/reference/androidx/tracing/Trace ; https://developer.android.com/jetpack/androidx/releases/tracing.
- İlk merge-ref run #45685844306 workflow doğrulama adımında eski PR base.sha snapshot nedeniyle durdu; test adımı atlandı. Kontrol gerçek `github.sha` merge commit parent'ları üzerinden düzeltildi.
- Yeni HEAD `5df909fb1d0893aa0fb308f33b5fb8064aec8811`; [merge-ref emulator #45685959506](https://github.com/aries919-bot/Jarvis/actions/runs/45685959506) + [APK/unit #45685959491](https://github.com/aries919-bot/Jarvis/actions/runs/45685959491) IN_PROGRESS. Workflow merge-ref verification PASS; parent1 #70 merge `09f98b324a9a1f38dbbc25853ec2eba2fc544db9`, parent2 PR head.
- Emulator test image AOSP default API34 / Pixel 2 / x86_64; report + logs if: always() ile korunuyor.
- PR #71 merge yok; PR #69 mükerrer iş/SharedPreferences mimarisi nedeniyle merge dışı. PR #71 için yeni emülatör PASS ve gerçek telefon kontrolü gereklidir.
- Kamu C1 12 kaynak /55 doğrulanmış açık ilan; 45 eksik; Worker production GET/D1 ve city dedup yok; **RED**.
- Ayrıntı: `gorevler/H1-EMULATOR-FAIL-ANALYSIS-2026-11-25.md` ve `gorevler/QA-EKIP-PANOSU-2026-11-25.md`.

## 25. H1 yeni HEAD build + emulator durumu — 25.11.2026 19:34Z
- PR #71 HEAD `5df909fb1d0893aa0fb308f33b5fb8064aec8811`; 10 dosya / +2.607 / -26; kaynak test sayımı 20 model +10 store +4 instrumentation =34.
- [APK/unit #45685959491](https://github.com/aries919-bot/Jarvis/actions/runs/45685959491) SUCCESS; birim testleri + APK derleme ve artifact upload tamam.
- [Merge-ref emulator #45685959506](https://github.com/aries919-bot/Jarvis/actions/runs/45685959506): merge parent verify SUCCESS; tracing/debugAndroidTest dependencyInsight SUCCESS; Android instrumentation hâlâ IN_PROGRESS.
- Önceki #45679523114 FAILURE 1/4 PASS,3/4 FAIL; 3 UI test RootViewWithoutFocusException. Yeni koşu eski başarısız sonucu kapatmadan kabul edilmeyecek.
- Diff scan 10 dosya / secret pattern 0 / LinkedIn-Indeed URL 0 / browser UA 0.
- **KARAR:** APK/unit PASS; emulator sonuçlanmadı; gerçek telefon yok; PR #71 merge yok. C1 hâlâ 12 kamu kaynağı/55 ilan, 45 eksik; Worker GET/D1 ve şehirli dedup yok.

## 26. H2 city alanı upstream kanıtı — 25.11.2026
- H2 reader sabit commit `fe37be93202442394504f455b4a1caeb31f298cf`; career/h2_sources.json 19 source record / city-location alanı eksik 19/19.
- scripts/h2_announcement_reader.py make_item() city/location parametresi almıyor ve output JSON'a eklemiyor; parse_official_notice() kaynak kayıtlarından da city çıkarmıyor. Cloud mapper tek başına upstream city field oluşturamaz.
- Şehir yalnız resmi PDF/sayfada pozisyon satırına bağlı duty location olarak kanıtlanırsa yazılmalı. Kurum merkez adresi, kaynak hostname veya başlıktan tahmin yasak. Çok lokasyon varsa ayrı pozisyonlar ya da location array; bulunmazsa null + possible duplicate ve benzersiz ilan sayısının dışında.
- Ayrıntı: `gorevler/C1-H2-CITY-FIELD-AUDIT-2026-11-25.md`; commit `99055f2b5ecee19a2b3936443f8d9fefd375548c`.
- C1 sayımı değişmedi: 12 kamu kaynağı /55 doğrulanmış ilan /45 eksik; Worker production GET/D1 halen SKIPPED. **RED**.

## 27. H1 gerçek emulator başarısızlığı + async açılış / Career QA-RAG — 25.11.2026
- Run #45685959506 GitHub SUCCESS görünse de artifact 15057322360 XML gerçek sonucu **4 instrumentation / 1 PASS / 3 FAIL / 0 ERROR / 0 SKIP**. Üç UI test RootViewWithoutFocusException (has-window-focus=false) ile kaldı; tek geçen test SQLite last-good recovery.
- Eski workflow emulator scripti set -o pipefail kullanıp set -e kullanmadığından Gradle failure kodu action'dan yutuldu. Hatalı PASS kararı geri çekildi. .github/workflows/h1-android-ui-smoke.yml set -euo pipefail oldu; commit 8d560a7d4e18db6fa6fdd1b131b47a831d6fd4bd.
- CareerNewsVerificationActivity.kt açılış SQLite read ve mevcut kaydı koşulsuz yazma UI thread'deydi. Tek FIFO ExecutorService ile asenkron load/persist, sadece ilk kayıtta save, Activity kapanırken kuyruğun sonunda database close eklendi; commit b8a78126cbc6713cb4d42838027a6189274f19e7.
- Yeni PR #71 HEAD b8a78126cbc6713cb4d42838027a6189274f19e7. APK/unit run #45692016951 SUCCESS; birim testleri + APK build + upload success. Emulator run #45692017824 son kontrolde IN_PROGRESS; merge-ref doğrulama + AndroidX dependencyInsight PASS, instrumentation step 7 halen çalışıyor. XML raporu oluşmadığı için 4/4 sonucu yok; gerçek telefon testi de yok. PR #71 merge yok.
- Ayrıntı ve kanıt: docs/HARITA.md bölüm 34; gorevler/H1-EMULATOR-FAIL-ANALYSIS-2026-11-25.md.

### Aries İş & Kariyer Question Answering / RAG
- Cloud PR #12 açık/taslak, son HEAD d49de634b9e07030c0117039b62027b76405b435; 4 dosya / +992 / -20; test dosyasında 24 test tanımı. Test tanımı geçiş kanıtı değildir.
- Kaynaklı QA: provider allowlist + taze/açık/doğrulanmış kayıtlar; Türkçe BM25 + BGE-M3 embedding + RRF; seçilmiş kaynak kayıtlarıyla Gemma 4; son tarih/şart/başvuru yöntemi deterministik; kanıt/konu dışı yoksa abstain; model kapasitesi doluysa BM25 fallback.
- Kamu kaynak sağlayıcıları yalnız iskur-esube-json / h2-official-announcement. Büyük özel Lever/Ashby yalnız jobs.lever.co / jobs.ashbyhq.com resmi board hostları. Stale/expired/unknown/possibleDuplicate kayıtlar cevap havuzuna alınmaz.
- CV/özgeçmiş, kişisel KPSS puanı, kimlik, telefon, e-posta, bearer/API anahtarı AI çağrısından önce bloklanır; user history/profil D1/AI'a gitmez. Soru/token loglanmaz. Haber SQLite ile kariyer feed'i karıştırılmaz.
- Son 7 workflow #45692460706, #45692517637, #45692608933, #45692642608, #45692792848, #45692810534, #45692919011 failure; her job 0 steps ve 2–3 sn sonra kapandı. Log BlobNotFound; gerçek Node syntax/unit test çalışmadı. **PR #12 CI RED / runner başlangıç engeli; merge/deploy yasak.**
- QA notu: gorevler/ARIES-CAREER-QA-RAG-2026-11-25.md, commit 71ddd4a722942113f748c1afc586891ef041ed56.
- Cloudflare model belgeleri ve maliyet: BGE-M3 https://developers.cloudflare.com/workers-ai/models/bge-m3/ ; Gemma 4 https://developers.cloudflare.com/workers-ai/models/gemma-4-26b-a4b-it/ ; rejectIfBusy https://developers.cloudflare.com/workers-ai/features/reject-if-busy/.
- Kamu C1 sayımı değişmedi: **12 kaynak /55 doğrulanmış açık ilan /45 eksik**, Worker production GET/D1 ve city dedup RED.

## 28. Son Career QA/RAG ve H1 exact-head durumu — 25.11.2026 20:20Z
- Cloud PR #12 HEAD 4b010df6fbd30229ec1868cf12e86a03b3eab055; 4 dosya / +1045 / -20; 26 test tanımı. Çalıştırılmış test sayısı **0**.
- Son 7 run #45692642608, #45692792848, #45692810534, #45692919011, #45692938639, #45693799901, #45693834527; hepsinde 1 job / 0 step / failure / 2–3 saniye. En son job 139485957163: 20:19:25Z–20:19:27Z. Logs BlobNotFound, annotation içeriği okunamıyor. **Runner/job startup blocker; kod test failure/pass olarak sınıflandırılamaz.**
- QA source allowlist: public categories iskur-esube-json/H2 official, Büyük özel Lever/Ashby official host; stale, expired, unknown, unresolved duplicate dışarıda. Cevaplar deadline, şart, application, city, positionCount alanlarından deterministik çıkarılır; city/positionCount yoksa tahmin edilmez.
- PII gate: CV/özgeçmiş, kişisel KPSS puanı, national ID, phone, e-mail, bearer/API token bloklanır; off-topic soru abstain; model rejectIfBusy → BM25 fallback. PR taslak, merge/deploy yok.
- H1 current PR #71 HEAD b8a78126cbc6713cb4d42838027a6189274f19e7; APK/unit #45692016951 SUCCESS. Emulator #45692017824 son kontrolde IN_PROGRESS; XML yok. Eski run #45685959506 XML 4/1 pass/3 fail; false green wrapper hatası geri alındı; asenkron SQLite init eklendi. H1 merge yok; phone testi yok.
- Ayrıntılar: docs/HARITA.md bölüm 34–35; gorevler/ARIES-CAREER-QA-RAG-2026-11-25.md; gorevler/QA-EKIP-PANOSU-2026-11-25.md.
- Kamu C1 sayımı 12 kaynak /55 doğrulanmış açık ilan /45 eksik. Worker production GET/D1 ve city dedup RED.

## 29. QA/RAG exact-head son durum — 25.11.2026 20:22Z
- Cloud PR #12 HEAD a80e557d1dc8373446a3a41b15d50f998d5e3e46; diff 4 dosya / +1043 / -20; 26 test tanımı / 0 çalıştırılmış.
- Son yedi CI: #45692792848, #45692810534, #45692919011, #45692938639, #45693799901, #45693834527, #45694369322. Her job 0 steps, 1–3 saniyede failure; son job 139487790097 20:22:41Z–20:22:42Z. Kodun syntax/unit testleri koşmadı.
- Yeni QA alanları: positionCount/city yalnız kayıttan; null ise açıkça belirtilmemiş. Public source allowlist esube/H2; büyük özel Lever/Ashby. PII gate, off-topic abstain, rejectIfBusy + BM25 fallback.
- Testlerdeki Bearer örneği gerçek tokena benzemeyen placeholder'a indirildi; uzun GitHub PAT biçimli fixture kaldırıldı.
- PR #12 draft, merge/deploy yok; runner başlangıç engeli çözülmeden yeniden deneme döngüsü yok.
- H1 #71 HEAD b8a78126cbc6713cb4d42838027a6189274f19e7. APK/unit #45692016951 SUCCESS; emulator #45692017824 son kontrolde IN_PROGRESS, rapor XML yok. 4/4 pass/telefon yok; merge yok.
- Kamu C1 12 kaynak /55 doğrulanmış açık ilan /45 eksik; Worker GET/D1 + city dedup RED.


## 30. H1 Haber QA / mevcut test kapısı — 25.11.2026
- H1 PR #71 yeni HEAD 0eaaa74153dd3eacdd66c701b9727c2b8978983f. Yeni CareerNewsQuestionAnswer.kt cihaz içi deterministic soru-cevap; yalnız mevcut haber SQLite kaydındaki claim/evidence/review/correction ve ayrı yayın/toplama/ilk görülme alanlarından cevap verir. LLM, ağ, Worker, kariyer feed çağrısı yok.
- Activity'ye "Haber hakkında soru sor" düğmesi ve kaynakları açan cevap penceresi eklendi. 17 JUnit testi tanımı var.
- İlk run #45698403489: 142 test /138 pass /4 fail. Hata sınıfları düzeltildi: firstSeen test beklentisi, Türkçe "asıl kaynağı" eşleşmesi, aynı URL altında farklı kanıt kayıtlarının korunması, context-missing açık etiketi. Yeni HEAD 0eaaa741 üzerinde 17 QA testi.
- Güncel exact-head APK/unit #45699134292 ve emulator merge-ref #45699134296 son kontrolde IN_PROGRESS; sonucu gelmeden merge yok. Önceki #45692016951/#45692017824 PASS yalnız b8a7812 HEAD'ini kapsar.
- H1 SQLite onCreate async load + existing record no-op save işi PR #71 içinde bulunuyor; P2 uygulandı. Ayrıntı gorevler/H1-SQLITE-ASYNC-OPEN-2026-11-25.md.
- H1 Haber QA raporu: gorevler/H1-NEWS-QA-2026-11-25.md; güncel harita: docs/HARITA.md bölüm 37.
- Cloud Career QA/RAG PR #12: local pure tests 18/18, route tests 7/7; private repo Actions job 0 step runner-start blocker. CI PASS değildir; PR #12 draft/no merge/no deploy.
- C1 kamu sayımı değişmedi: 12 resmî kaynak /55 doğrulanmış açık ilan /45 eksik. Worker GET/D1 + city dedup RED.


### 26.11.2026 — H1 CI false-green ve yeni C1 resmî kaynakları
- H1 PR #71 son HEAD `8a52e21e3dfb7e5e11d6f5009077f90edce2c09b`: APK run #45699704415 başarılı; emulator run #45699704565 GitHub SUCCESS görünmesine karşın artifact XML yeniden sayımı **4 test /3 failure /0 error /0 skipped**. Hatalar üç UI testte `RootViewWithoutFocusException` (window-focus=false); yalnız SQLite last-good recovery testi geçti.
- Artifact ID `15063616173`, SHA-256 `da33e7d6084c64f6aabcadb488684ad41210be00f389fcfc266a0b1f43d10470`; logda `BUILD FAILED in 6m 26s`.
- False-green kök sebep doğrulandı: workflow içindeki pinli ReactiveCircus runner'ın `parseScript` metodu çok satırlı script'i satırlara bölüyor ve her satırı ayrı `sh -c` ile çalıştırıyor. Bu yüzden `set -euo pipefail` sonraki Gradle|tee satırına taşınmıyor; tee'nin 0 çıkış kodu Gradle failure'ını gizliyor. Kaynakta tek satır `bash scripts/run-h1-emulator-tests.sh` ve bağımsız XML gate gerekli. Üç UI focus hatası da ayrıca çözülmeli; testi atlamak yok.
- PR #71 H1 **ONAY YOK / merge yok**.
- C1 resmî yeni kaynak adayları: SSB 26 proje bazlı personel ilanı (20.11–21.12.2026; bir ilan kartı, 26 değil); Hazine Kontrolörleri en çok 10 stajyer (23.11–04.12.2026; bir ilan kartı, 10 değil); Bartın 20.11.2026 akademik duyurusu (öğretim görevlisi son başvuru 04.12.2026 23:59; teyit edilen bir pozisyon; öğretim üyesi kadrolarının tamamı henüz sayılmadı). Sayıştay son tarihi 25.11.2026 23:59:59 olduğundan artık süresi geçti.
- Yeni kaynak adayları exact dedup, sourceId ve city kontrolü yapılmadan Worker'a veya sayıya eklenmedi. SSB/HMB/Bartın duyuruları Kariyer Kapısı kazınmadan, kurum sayfalarından resmî link ile yönlendirme olarak sunulmalı.
- Güncel C1 envanteri değişmedi: **12 kamu kaynağı /55 doğrulanmış açık ilan /45 ilan eksiği — RED**. C1 Worker'ın exact integrated audit'i ayrı: 26 katalog kaynağı, 17 fresh/9 stale, 47 açık doğrulanmış kart, 114 deadline-unknown özel aday. Bu iki veri katmanı toplanmaz.
- C1 production authenticated GET/D1 kanıtı yok; Worker publish hâlâ blokeli. PR #11 draft; PR #12 draft ve son CI run #45697424481 FAILURE (job steps=null; PASS değil).
- Ayrıntılı görev/kanıt: `gorevler/H1-CI-FALSE-GREEN-2026-11-26.md`.
- CI bağlantıları: https://github.com/aries919-bot/Jarvis/actions/runs/45699704565 ; https://github.com/aries919-bot/Jarvis/actions/runs/45699704415 ; PR https://github.com/aries919-bot/Jarvis/pull/71
**KARAR:** H1 RED (yanlış yeşil CI, 3/4 UI test fail); C1 RED (12/55, production GET/D1 yok); PR #11/#12/#71 merge/deploy yok.


## Kaynak başına yeniden sayım (feed katmanı) — 26.11.2026

Son makine okuyucu raporunun kaynak satırları yeniden toplandı. Bu, 12/55 proje-geneli resmi envanter sayısından ayrı olan Worker'a girmiş mevcut kamu feed'i ölçümüdür:

| Kamu kaynağı | Güncel açık kart | Not |
|---|---:|---|
| İŞKUR e-Şube | 42 | Son snapshot 2026-11-24T23:51:48Z; 3 sayfa; publishedAt=null |
| Anadolu Üniversitesi | 1 | Son başvuru 30.11.2026 |
| Düzce Üniversitesi | 1 | Son başvuru 07.12.2026 |
| Marmara Üniversitesi | 1 | Son başvuru 08.12.2026 |
| Tarsus Üniversitesi | 1 | Son başvuru 07.12.2026 |
| Zafer Kalkınma Ajansı | 1 | Son başvuru 14.12.2026 |
| **Toplam** | **47** | **6 kaynak / 47 açık ve benzersiz kamu kartı** |

Tekrar hesabı: **42+1+1+1+1+1 = 47**.

Açık iş olarak sayılmayan reader satırları:
- Adli Tıp Kurumu: 1 duyuru / 181 kadro; başvuru 30.11.2026 09:00'da açılacağından 26.11.2026 tarihinde açık değil; aktif açık sayımına 0.
- YHGM: 1 kura/sonuç duyurusu; son başvuru tarihi geçmiş, iş ilanı değil; 0.
- Bilecik Üniversitesi, BOTAŞ, ÇOMÜ: stale kaynak; 0 yeni kart değil, ölçüm başarısız/son iyi veri korunmalı. Bunlar 0 ilan diye yorumlanamaz.

Katman ayrımı:
- Proje-geneli manuel/kurum doğrulama envanteri: **12 kaynak /55 açık kart /45 kart eksiği** (kaydedilmiş harita sayımı).
- Worker'ın son makineyle doğrulanmış kamu feed'i: **6 kaynak /47 açık kart /4 kaynak ve 53 kart eksiği**.
- 12/55 ile 6/47 toplanmaz; 6/47, feed'e alınmış ve source-level yeniden sayılmış alt kümedir. 12/55'teki fazladan kayıtların Worker feed'e exact dedup, sourceId ve şehir alanı kanıtı olmadan eklendiği iddia edilmez.
- 114 özel ATS kaydı son başvuru tarihi bilinmediği için Doğrulanamadı; kamu 47'ye eklenmez.
- Hazine, SSB ve Bartın'ın yeni/ek resmî duyuru adayları henüz hiçbir sayaca eklenmedi.

**Eşik:** Worker feed 6/10 kaynak ve 47/100 kart ile RED; geniş resmi envanterde kaynak sayısı 12/10 olsa da ilan sayısı 55/100 ile RED.


## 26.11.2026 — 12 saat freshness yeniden değerlendirmesi ve yeni resmî ilanlar

- **Kayıt zamanı:** 2026-11-25T21:30:32.537Z
- **Önceki makine kanıtı:** C1 live audit run [#45517626605](https://github.com/aries919-bot/Jarvis/actions/runs/45517626605), artifact [14977937193](https://github.com/aries919-bot/Jarvis/actions/runs/45517626605/artifacts/14977937193), JSON `collectedAt=2026-11-25T00:25:02.241Z`. Bu run anında 26 kaynak, 17 fresh /9 stale; 47 doğrulanmış açık kamu kartı; 116 deadline-unknown özel kayıt; eşik NOT_MET idi. Bu, bugünün canlı ölçümü değildir.
- H2 official feed artifact [14976107324](https://github.com/aries919-bot/Jarvis/actions/runs/45512710235/artifacts/14976107324) içindeki 10 JSON dosyasının her birinde `collectedAt=2026-11-24T23:46:14Z`; kaynağa özgü `sourceStatus` alanları dosyadan yeniden okundu. Entegre audit sırasında H2 son iyi zamanları `2026-11-25T00:23:08Z` idi.
- Projedeki freshness sınırı 12 saat olduğundan 26.11.2026 itibarıyla aşağıdaki altı kamu kaynağının tamamının son iyi ölçümü 12 saatten eskidir. Bunlar silinmez; yalnız kendi sourceId'leri stale olur. Diğer kaynakların last-good verisi bu durumdan etkilenmez.

### Kamu — source-by-source son iyi kayıt / bugünkü statü

| Kaynak | Son iyi kayıt sayısı | Son iyi ölçüm | 26.11 statüsü | Not |
|---|---:|---|---|---|
| İŞKUR e-Şube | 42 | 2026-11-24T23:51:48Z | stale | `publishedAt=null`; son başvuru tarihleri kaynak snapshot'ından |
| Anadolu Üniversitesi | 1 | 2026-11-25T00:23:08Z | stale | deadline 30.11.2026 |
| Düzce Üniversitesi | 1 | 2026-11-25T00:23:08Z | stale | deadline 07.12.2026 |
| Marmara Üniversitesi | 1 | 2026-11-25T00:23:08Z | stale | deadline 08.12.2026 |
| Tarsus Üniversitesi | 1 | 2026-11-25T00:23:08Z | stale | deadline 07.12.2026 |
| Zafer Kalkınma Ajansı | 1 | 2026-11-25T00:23:08Z | stale | deadline 14.12.2026 |
| **Toplam last-good** | **47** | — | **6 kaynak stale** | **42+1+1+1+1+1=47** |

Bu nedenle bugünkü doğru gösterim **0 fresh ölçülmüş kamu kaynağı / 0 fresh ilan** değil; **6 kaynak stale / 47 kayıtlı last-good açık ilan** şeklindedir. “0 fresh” sıfır ilan anlamına gelmez. Eski 47 kayıt son iyi veri olarak tutulur, bugünkü canlı ilan etiketiyle gösterilmez. Kamu ilanları 0 diye raporlanmaz.

Önceki proje-geneli manuel envanter **12 kaynak /55 açık kart** olarak kaydedilmişti; ancak bu 12 kaynağın tümü için bugüne ait source-level `lastGoodAt` kanıtı alınmadı. Dolayısıyla 12/55 de bugünün fresh sayımı değildir; son kayıtlı envanterdir. **12/55 ile feed alt kümesi 6/47 toplanmaz.** Her iki eşik değerlendirmesi de ilan sayısı 100'ün altında kaldığı için RED.

### 26 Kasım'da yeniden bulunan resmî ilan adayları

Bunlar resmî belge/sayfada doğrulandı; exact duplicate, sourceId, şehir ve Worker entegrasyonu yapılmadığı için mevcut 12/55 veya 6/47 sayımlarına eklenmedi. Kontenjanı ayrı ilan kartı gibi çoğaltma.

1. **Sahil Güvenlik Komutanlığı — Kamu / KPSS.** 13.11.2026 tarihli Resmî Gazete ilanı; 10 açıktan devlet memuru, tek ilan kartı. Başvuru 16–27.11.2026; son tarih 27.11.2026 (ilan saat belirtmiyor). KPSS 2026: uzman/psikolog/İngilizce öğretmeni/tedarik uzmanı için P3, video montaj teknikeri ve muhasebeci için P93; asgari puan kılavuz tablosunda. Yaş ve 657 şartları var. Başvuru e-Devlet üzerinden: https://www.turkiye.gov.tr/sahil-guvenlik-komutanligi-is-basvurusu ; resmî ilan PDF'si: https://www.resmigazete.gov.tr/eskiler/2026/11/20261113.pdf (s.84). Önceki C1-1 probe satırındaki Sahil Güvenlik 0, tarama tasarım gereği yapılmadığı için 0'dı; “ilan yok” anlamına gelmiyordu. Yeni tek kart adayıdır.
2. **Manisa Celal Bayar Üniversitesi — Kamu / KPSS'siz akademik personel.** 25.11.2026 Resmî Gazete ilanı, tek duyuru kartı; 13 öğretim üyesi +9 öğretim görevlisi/araştırma görevlisi = 22 kadro satırı. Elektronik başvuru sonu 09.12.2026 mesai bitimi; gerekli imzalı dosyaların teslim sonu 16.12.2026 mesai bitimi. Bu iki tarih ayrı süreç adımlarıdır; ilan kartında başvuru sonu 09.12.2026 kalır. ALES/dil/akademik derece ve kadroya özgü koşullar aranır. Resmî ilan PDF: https://www.resmigazete.gov.tr/ilanlar/eskiilanlar/2026/11/20261125-4-10.pdf ; başvuru: https://aed-basvuru.mcbu.edu.tr/ . 22 kadro, 22 ilan kartı değildir.
3. **Çağ Üniversitesi — özel üniversite / KPSS'siz.** 25.11.2026 Resmî Gazete ilanı; Uluslararası Finans ve Bankacılık ile Uluslararası Ticaret ve Lojistik alanlarında 2 araştırma görevlisi kadrosu, tek duyuru kartı. `publishedAt=2026-11-25`; `deadline=2026-12-09`. ALES EA 70, yabancı dil 85 ve ilgili alanda tezli yüksek lisans mezunu/öğrencisi olma şartı. İnternet başvurusu kabul edilmez; şahsen/posta ile Çağ Üniversitesi İnsan Kaynakları Direktörlüğü'ne başvuru. Resmî PDF: https://www.resmigazete.gov.tr/ilanlar/eskiilanlar/2026/11/20261125-4-9.pdf . Kamu sayımına girmez; özel üniversite kaydı ayrı tutulur.

### Resmî Gazete 25.11.2026 taraması — yanlış pozitif
- 25 Kasım 33441 sayısının Çeşitli İlanlar sayfasında CTE Genel Müdürlüğü başlığına rastlandı; bireysel PDF `20261125-4-2.pdf` bir personel ilanı değil, ihale yasaklama kararıdır. İlan sayımına eklenmedi.
- Aynı sayfada listelenen Yapı İşleri, Yerel Yönetimler, MEB, MSB ve Kültür Bakanlığı başlıklarının tüm PDF'leri bu ölçümde tek tek içerik/başvuru şartı düzeyinde doğrulanamadı; sırf sayfa listesinde adları geçtiği için personel alımı kabul edilmez.
- PDF metinleri web arama/okuma katmanından çıkarıldı; screenshot çağrıları cache miss verdi. Bu nedenle görsel PDF incelemesi tamamlanmış sayılmaz; metin kaynağının resmî linki korunur.

### Tarih/veri yazımı
- Sahil Güvenlik `publishedAt=2026-11-13`; MCBÜ `publishedAt=2026-11-25`; Çağ Üniversitesi `publishedAt=2026-11-25`.
- `collectedAt` yalnız gerçek ingest çalışması anında yazılabilir; `firstSeenAt` ilk başarılı entegrasyon zamanı gelmeden doldurulmaz. Bu üç aday için feed ingest henüz yapılmadığından `collectedAt=null`, `firstSeenAt=null` kalmalıdır.
- Tarih kartları: deadline geçen Sayıştay ve benzeri ilanlar güncel listeden çıkar; sınav/ön değerlendirme tarihi deadline alanına yazılmaz. Sahil Güvenlik 27.11.2026 son başvuru tarihidir; resmi metinde saat yok.

**KARAR:** C1 RED. Feed'in son iyi kamu verisi 6 kaynak /47 karttır; 26.11.2026 itibarıyla bu altı kaynağın tümü 12 saat freshness sınırını aştığı için stale durumundadır. Fresh ilan sayısı bu kanıt setinde 0; bu, kamu ilanı olmadığı anlamına gelmez. Yeni 2 kamu +1 özel aday kartı bulundu fakat dedup/Worker ingest yapılmadı. Production authenticated GET/D1 kanıtı ve 10/100 eşiği yok.


## 26.11.2026 — H1 XML gate uygulaması ve kapsam ilerlemesi
- collectedAt: 2026-11-25T21:47:27.094Z
- H1 PR #71 branch'ine üç commit yazıldı: strict Bash runner `173902ab99b0a267877bf79fe826cfe34eb8fac3`; XML gate `621c57640389221950ade7c6be08ef2e9eee4970`; workflow tek satır runner + always XML doğrulaması `4398073b3759109fa27a8172956cbea13f31ce27`.
- PR #71 yeni HEAD `4398073b3759109fa27a8172956cbea13f31ce27`; diff 14 dosya / +3.370 / -26. Dosya listesi API'den yeniden alındı.
- CI tasarımı: Gradle|tee aynı Bash process içinde; XML raporu yoksa/bozuksa, test sayısı 4 değilse veya failure/error/skipped varsa job fail. Artifact upload yine `if: always()`.
- Exact-head Actions sorgusunda `4398073...` için henüz run yok. Bu nedenle commit var ama CI kanıtı yok; önceki 4 test /3 failure XML bulgusu hâlâ geçerli. H1 RED, PR draft ve merge yok.
- İş kaydı: `gorevler/H1-CI-XML-GATE-IMPLEMENTATION-2026-11-26.md`.
- **Kapsam yüzdesi (yalnız H1 + C1; tüm Aries değil): H1 yaklaşık %50, C1 yaklaşık %40, eşit ağırlıklı toplam yaklaşık %45.** Bu planlama tahminidir, ölçülmüş uygulama tamamlama metriği değildir. Tam Aries için tek bir doğrulanmış payda bulunmadığından bütün uygulamaya yüzde verilmedi.
- C1 son kamu feed ölçümü 6 kaynak/47 last-good kart; tümü stale. Fresh ilan sayısı 0 olarak değil, 0 fresh ölçüm /47 last-good kayıt şeklinde sunulmalı. 10/100 ve production GET/D1 hâlâ RED.
**KARAR:** H1 RED; C1 RED; merge/deploy yok. Ayrıntı ve hesaplama: `gorevler/H1-CI-XML-GATE-IMPLEMENTATION-2026-11-26.md`.


## 26.11.2026 — Tekrarlanan CI döngüsünü kırma / yeni kanıt

- Yeni iş kaydı: [gorevler/CI-ALTERNATIF-DOGRULAMA-2026-11-26.md](gorevler/CI-ALTERNATIF-DOGRULAMA-2026-11-26.md). Bu ek, önceki CI planının güncel durumunu geçersiz kılar.
- Jarvis PR #71 HEAD `4398073b3759109fa27a8172956cbea13f31ce27`: aynı HEAD APK CI [#45707502321](https://github.com/aries919-bot/Jarvis/actions/runs/45707502321) SUCCESS. Aynı HEAD emulator CI [#45707502311](https://github.com/aries919-bot/Jarvis/actions/runs/45707502311) şu son kontrolde IN_PROGRESS; checkout, PR head/base kontrolü, Java/Gradle kurulumu ve AndroidX dependencyInsight başarılı; emulator instrumentation adımı çalışıyor. XML gate ve artifact henüz tamamlanmadı. Önceki “exact-head run yok” notu artık güncel değildir.
- PR #71 API diff sayımı: 14 dosya / +3.370 / -26. PR açık/taslak; merge yok. APK SUCCESS, H1 emulator sonucu henüz bilinmiyor; H1 kabulü yok.
- Cloud PR #12 HEAD `1692930b60b952589fdf5d849010a177e288bfb2`; son run [#45697424481](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45697424481) failure. Job API: `runner_id=0`, boş runner adı, `steps=[]`, yaklaşık 2 saniye. Bu testlerin başarısızlığı değil, runner/job-start seviyesinde kanıtsız kapanıştır. Aynı koşulda yeni commit/rerun döngüsü durduruldu.
- Yeni strateji: H1 mevcut emulator run tamamlanana kadar bekle; XML 4/4 ve failure/error/skipped=0 yeniden sayılacak. Tekrar RootViewWithoutFocusException olursa aynı koşuda rerun yok; hata anı window/activity/input dumpsys + logcat artifact'i eklenerek odak kaynağı teşhis edilecek. Cloud PR #12 için CI dışında Node.js unit/contract testleri ve local D1/Miniflare yolu ayrıştırılacak; bunlar production GET/D1 kanıtı yerine sayılmayacak.
- C1 sayımları değişmedi: Worker son iyi kamu verisi 6 kaynak /47 kart; altı kaynak stale. 10/100 ve production GET/D1 RED. Yeni ilan adayları exact dedup/ingest olmadan sayaca eklenmez.

**KARAR:** H1 ve C1 henüz RED/BLOCKED; PR #71 ve #12 taslak/açık, merge/deploy yok. Yeni yaklaşım aynı başarısız CI'ı tekrarlamak değil; runner başlangıç arızasını kod testinden ayırmak ve UI focus için teşhis kanıtı toplamak.


## 26.11.2026 — H1 emülatör package manager kök neden düzeltmesi

- Ham artifact [run #45707502311](https://github.com/aries919-bot/Jarvis/actions/runs/45707502311), artifact 15066187350 indirildi. XML yeniden sayımı: **0 testcase /0 failure /0 error /0 skipped**; `system-err` AndroidTestApkInstallerPlugin öncesinde `package install-commit` sırasında `Broken pipe (32)`. Log: `BUILD FAILED in 3m 20s`. UI testleri bu koşuda hiç başlamadı; bu PASS değil ve önceki RootViewWithoutFocusException'dan ayrı arızadır.
- APK run [#45707502321](https://github.com/aries919-bot/Jarvis/actions/runs/45707502321) SUCCESS; yalnız derleme/unit kanıtı.
- Yeni feature commitleri: `810112c90c487d634163c98d3c38daddb43fc9fa` (boot/package-manager preflight ve hata anı adb/logcat/disk diagnostics); `b6feff46a361ae969d81a176b459c0192d1a9dfc` (Google APIs image, fresh wipe-data AVD, 2GB RAM, 8GB data disk, 900 sn boot timeout, diagnostics artifact).
- Yerel `bash -n` kontrolü PASS; emulator testi değildir.
- Yeni exact-head [emulator run #45711684280](https://github.com/aries919-bot/Jarvis/actions/runs/45711684280) ve [APK run #45711684204](https://github.com/aries919-bot/Jarvis/actions/runs/45711684204) son kontrolde IN_PROGRESS idi. H1 henüz PASS değil.
- Tam kanıt ve sonraki alternatif: [H1-EMULATOR-PACKAGE-MANAGER-FAILURE-2026-11-26.md](gorevler/H1-EMULATOR-PACKAGE-MANAGER-FAILURE-2026-11-26.md).
**KARAR:** H1 RED/BLOCKED; 0 testcase olan XML başarı sayılamaz. Yeni AVD denemesi bekleniyor. PR #71 merge/deploy yok.


## 26.11.2026 — H1 teknik blokaj güncellemesi
- FUTO Notes CI vakası, Android system_server sürecinin sys.boot_completed=1 sonrasında yeniden başlayabildiğini ve boot_progress_enable_screen olayının güncel PID ile eşleştirilmesi gerektiğini gösteriyor: https://gitlab.futo.org/futo-notes/futo-notes/-/blob/papercuts-bridge-guidance-close-round10/scripts/ci-android-sync-leg.test.mjs
- PR #71 runner düzeltmeleri: 29aaa17e532c13a394d1ed7dd7c53bc613d0bec9, fail-closed düzeltmesi e390e05e1485717c3a07ce3af06db6b3840062f3.
- Yerel mock kontrolleri: 1 pozitif exit 0; 3 negatif (Package Manager yok, PID marker eski, paket listesi boş) exit 21. Bash syntax PASS. Bunlar cihaz/emulator testi değildir.
- Exact HEAD e390e05: emulator [#45712459718](https://github.com/aries919-bot/Jarvis/actions/runs/45712459718) ve APK [#45712459742](https://github.com/aries919-bot/Jarvis/actions/runs/45712459742) henüz IN_PROGRESS. H1 XML 4/4 kanıtı yok.
- Önceki HEAD APK run #45711684204 SUCCESS; emulator #45711684280 sonraki commit nedeniyle CANCELLED. Aradaki HEAD 29aaa CI koşuları da yeni commit nedeniyle iptal edildi; bu koşular test sonucu olarak sayılmaz.
- C1: 6 kaynak /47 last-good kamu ilanı; 6 kaynak stale; eşik 10/100 ve production GET/D1 yok.

**KARAR:** H1 RED/BLOCKED — current exact-head CI bekleniyor. C1 RED. Merge/deploy yok.

## 26.11.2026 — H1 teknik blokaj durumu
- PR #71 exact HEAD `e390e05e1485717c3a07ce3af06db6b3840062f3`: APK/unit [#45712459742](https://github.com/aries919-bot/Jarvis/actions/runs/45712459742) SUCCESS. Job logları `testDebugUnitTest` 1m50s ve `assembleDebug` 1m10s BUILD SUCCESSFUL; artifact ID 15068252085, 14,197,729 byte, ZIP SHA-256 `d3ceae0d1781cfb0f6ae19cdeb587494ea4f04310d2117f3fe23c46aafb43c91`.
- Kaynak anotasyon sayımı: 12 unit test file /142 @Test; H1 verification 20 + store 10 + QA 17 =47; instrumentation 4. Unit görevinin PASS olması kanıttır; 142 ayrı test sonucuna ait XML raporu bulunmadığından “142/142” iddiası yok.
- Exact-head emulator [#45712459718](https://github.com/aries919-bot/Jarvis/actions/runs/45712459718) API kontrolünde IN_PROGRESS; H1 instrumentation step IN_PROGRESS, XML gate/artifact pending. 4 testcase /0 failure/error/skipped doğrulanmadan H1 PASS yok.
- İkinci olası Android başlangıç yarışı: Package Manager kayıtlı olsa bile StorageManager kurulum yolu hazır olmayabilir; FUTO CI çözümü `pm install-create`/abandon preflight kullanıyor: https://gitlab.futo.org/futo-notes/futo-notes/-/blob/android-fs-picker/scripts/ci-android-emulator.sh. Aries'te ancak current emulator logu bu hatayı doğrularsa uygulanacak.
- C1 Worker kamu kaydı 6 kaynak/47 last-good, 6 stale; 10/100 ve production GET/D1 RED. Ayrı manuel envanter sayımıyla birleştirilmez.

**KARAR:** H1 APK/unit PASS; H1 instrumentation RED/BLOCKED; C1 RED; merge/deploy yok.


### Bekleyen bakım bulgusu — workflow path filters
- Workflow `.github/workflows/h1-android-ui-smoke.yml` içindeki pull_request.paths listesi `scripts/run-h1-emulator-tests.sh` ve `scripts/verify_h1_emulator_xml.py` dosyalarını kapsamıyor. Bu iki betikten biri tek başına değişirse PR event'i bu workflow'u tetiklemeyebilir; mevcut run tetiklendi çünkü aynı PR'da workflow dosyası da değişti.
- Mevcut emulator koşusu tamamlanmadan workflow dosyasına dokunulmadı; aksi halde concurrency `cancel-in-progress: true` mevcut kanıt koşusunu iptal eder. Sonuç alındıktan sonraki kontrollü düzeltmede iki script yolu trigger listesine eklenmeli.
- FUTO'nun daha yeni CI olayı ayrıca `pm install-create`/abandon preflight ile PackageInstaller + StorageManager yolunun hazır oluşunu doğruluyor. Bu sadece mevcut Aries artifact'inde aynı storage/installer hatası görülürse uygulanacak; şimdilik tahminle commit yok.
