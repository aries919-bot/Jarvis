# ARIES NOTU v3 FINAL — 24 Kasım 2026

> TALIMAT.md'ye ektir. Çelişki halinde bu not geçerlidir. Eski v2 ve v3 yamasının tamamının yerine geçer.
> Kalıcı hedef: `docs/NOT_C1.md`, `durum` dalı.

## 0. Rol ve ilk tur

- Cline KULLANILMIYOR. Kod, test, PR ve denetim işlerini ChatGPT yürütür.
- Cem'in tek işi telefonda elle doğrulama ve açıkça “birleştir” onayı vermektir. Cem'den komut, Termux veya yapıştırma istenmez.
- İlk turda şu yetkiler ayrı ayrı sınanır ve raporlanır: (a) dal açma + dosya yazma, (b) PR açma, (c) Actions sonucunu okuma. Biri başarısızsa durulur; yetki sorunu ve tüm işlerin durum/sonraki adım bloğu verilir.
- Kendi PR'ını sıfırdan ve şüpheci biçimde incele. `main`'e doğrudan yazma. Her iş ayrı dalda yapılır, PR açılır, CI yeşil olana kadar düzeltilir. Merge için Cem'in onayı beklenir.

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

Kategoriler: Tümü, Kamu, Kura, KPSS, KPSS'siz, Büyük özel şirket, Kamu bağlı büyük şirket.

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
- jarvis-cloud PR #5 RED: 15 adayın tamamı özel sektör; CI başarısız; `/kariyer-feed` tanımsız; `firstSeenAt` kalıcı değil.
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
- Cem'in telefon doğrulaması ve “birleştir” onayı beklenmeden PR birleştirilmez.
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


## 8. Hakem düzeltmesi ve K1-F2 yeniden denemesi — 24.11.2026

### PR #33 K1-F2
- Diff yeniden sayımı: 2 dosya, 0 ekleme, 606 silme. Yalnız `.github/workflows/iskur-render.yml` ve `scripts/render_probe.py`.
- Test job SUCCESS: `Ran 53 tests in 0.093s`, `OK` (53/53).
- Başarısız Hakem job yeniden çalıştırıldı: [Actions #45413014756](https://github.com/aries919-bot/Jarvis/actions/runs/45413014756), attempt 2. GitHub API yeniden çalıştırma cevabı `success=true`; tests SUCCESS, hakem `138746764701` FAILURE / RED.
- Actions logunda secret alanları maskeli (`CF_ACCOUNT_ID: ***`, `CF_API_TOKEN: ***`). Hakem silinen eski render_probe.py içindeki Mozilla/Chrome User-Agent satırlarını taradığı için RED verdi. Deterministik kontrolde durduğundan Cloudflare API çağrısına ulaşmadı; secret geçerliliği kanıtlanmış değildir.

### PR #39 HAKEM-DIFF
- PR: [#39](https://github.com/aries919-bot/Jarvis/pull/39), `fix/hakem-added-diff-only` → `cline/K1`.
- Kapsam: `.github/workflows/hakem.yml`, `scripts/hakem_gate.py`, `scripts/test_hakem_gate.py`. Diff'te yalnız eklenen hunk satırları denetlenir; silinen UA yanlış pozitif olmaz. Test logunda pozitif test sayısı, OK ve test job success zorunludur. Cloudflare AI opsiyoneldir; AI yoksa bunun bir insan onayı olmadığı açıkça belirtilir.
- Kod hakem incelemesi: KABUL. Diff'te yasak desen tekrar sayımı 0.
- Actions [#45477635801](https://github.com/aries919-bot/Jarvis/actions/runs/45477635801): Python Script Tests SUCCESS; `Ran 66 tests`, `OK` (66/66).
- Actions [#45477635690](https://github.com/aries919-bot/Jarvis/actions/runs/45477635690): tests SUCCESS, 66/66; hakem job SUCCESS; otomatik diff/AI adımı bootstrap PR #39 için SKIPPED, manual-review Step Summary SUCCESS. Bu bir bağımsız insan PR onayı değildir.
- PR #39 açık, merge yok. Cem'in açık “birleştir” izni bekleniyor. Bu izin verilmeden PR birleştirilmeyecek.
- #39 tabana girdikten sonra PR #33 Hakem job'ı yeniden çalıştırılır. #33 ancak test ve Hakem tümüyle SUCCESS olduktan, sonra Cem'in ayrı birleştirme onayı geldikten sonra birleşebilir.
- Secret sona erme tarihi yaklaşık 24.12.2026 olarak Cem tarafından bildirilmiştir; GitHub tarafında kesin expiry doğrulanmadı. Secret değerleri raporlanmaz.


## 9. Güncel hakem ve K1-F2 sonucu — 24.11.2026 20:26 UTC

### PR #39 — tamamlandı
- Cem'in “benim yerime sen yap” yanıtı, hemen önce #39'un birleştirilmesine ilişkin açık soruya verildiğinden yalnız PR #39 için onay kabul edildi.
- [PR #39](https://github.com/aries919-bot/Jarvis/pull/39) squash ile cline/K1 dalına birleştirildi: 34809ef345d2756734808f238a7beb4012ddd84e. main dalına doğrudan yazılmadı.
- Actions [#45477635801](https://github.com/aries919-bot/Jarvis/actions/runs/45477635801) ve [#45477635690](https://github.com/aries919-bot/Jarvis/actions/runs/45477635690): her iki test işi 66/66 PASS. PR #39 bootstrap olduğu için otomatik AI diff adımı atlandı; manuel review zorunluluğu kayda alındı.

### PR #33 — eski deneme kapatıldı
- Eski PR #33, #39 sonrasında geride kalmış base SHA ve mergeable=false nedeniyle birleştirilmedi.
- Aynı iki dosyanın silinmesi temiz taban üzerinden PR #44'e taşındı; #33 superseded olarak kapatıldı.

### PR #44 — temiz K1-F2 adayı
- PR: [#44](https://github.com/aries919-bot/Jarvis/pull/44), is/K1-F2-clean-20261124 → cline/K1; base commit 34809ef345d2756734808f238a7beb4012ddd84e.
- Diff: yalnız .github/workflows/iskur-render.yml ve scripts/render_probe.py; 2 dosya, 0 ekleme, 606 silme.
- Python Script Tests [#45480461048](https://github.com/aries919-bot/Jarvis/actions/runs/45480461048): Ran 66 tests in 0.092s, OK; summary 66 toplam /66 geçen /0 başarısız.
- PR Hakem [#45480461106](https://github.com/aries919-bot/Jarvis/actions/runs/45480461106): test ve hakem job SUCCESS; Cloudflare AI katmanı çalıştı ve ilk yanıt satırı ONAY. Secret alanları logda maskeli tutuldu; değerleri rapora yazılmadı.
- Kod incelemesi KABUL. PR #44 henüz birleştirilmedi; bu PR için ayrıca açık Cem onayı gerekir.


## 10. K1-F2 merge tamamlandı — 24.11.2026 20:30 UTC
- Cem'in açık “Evet” onayıyla [PR #44](https://github.com/aries919-bot/Jarvis/pull/44) squash merge edildi.
- Merge commit: `7348f47a339fcaebe581111f41bcc9689035234a`; hedef `cline/K1`, `main` değil.
- GitHub metadata: closed, merged=true; 2 dosya, 0 ekleme, 606 silme.
- Ön-merge test [#45480461048](https://github.com/aries919-bot/Jarvis/actions/runs/45480461048): 66/66 PASS. Hakem [#45480461106](https://github.com/aries919-bot/Jarvis/actions/runs/45480461106): SUCCESS, AI ONAY.
- Merge SHA sonrası Actions sorgusu boş döndü; post-merge CI çalıştığı iddia edilmiyor.
