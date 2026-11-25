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

