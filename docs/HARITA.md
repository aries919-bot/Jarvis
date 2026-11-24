# Aries — Durum Haritası

> Tek gerçek durum dosyası. Önce bu dosya ve docs/TALIMAT.md okunur.
> Son kontrol: 24.11.2026. Repo: `aries919-bot/Jarvis`.
> Bu dal yalnızca proje notları içindir; Android kodu burada geliştirilmez.
> **C1 özel ve öncelikli kurallar:** [docs/NOT_C1.md](NOT_C1.md). TALIMAT.md ile çelişirse NOT_C1.md geçerlidir.

## Repo gerçeği
- Varsayılan `main`: `63e877d96cb910e6d98d56834ae55ae8edafab2a` (2026-11-21); bu SHA için PR tetiklemeli workflow run listesi boş.
- Notlar `durum` dalında; GitHub okuma/yazma erişimi doğrulandı.
- `main` üzerinde docs/HARITA.md ve docs/TALIMAT.md yok; doğru kopyalar `durum` dalında.
- `cline/K1` dalı mevcut; Cem Cline'ın K1 üzerinde çalıştığını bildirdi. Dalına/dosyalarına müdahale yok; PR/bitirme kanıtı bekleniyor. K1 sahipliği Cline'da.
- K1 eski PR #24 ChatGPT koludur; Cline K1 tamamlayana kadar #24'e dokunma.
- A2 PR #25 açık/taslak, `is/a2-expressive-theme` → `main`; ikinci CI denemesi test+APK SUCCESS, ilk Gradle metadata NPE'nin kök nedeni bilinmiyor.
- A2 tanılama PR #27 açık/taslak, `fix/a2-gradle-metadata-stacktrace` → A2; workflow-only, PR/push CI SUCCESS.
- A2 devam PR #28 açık/taslak, `is/a2-theme-completion` → A2; önceki 7 commit CI #45180833170/#45180819864 SUCCESS. Yeni commitler `c9c65a0` (Açık/Koyu iki seçenekli ayar kontrolü) ve `850c530` (tüm yüzeylerde kontrast testi). Güncel HEAD CI #45387508292: birim test SUCCESS, APK derlemesi son kontrolde sürüyordu. Bağımsız inceleme/telefon testi bekliyor.
- B1 PR #26 açık/taslak, `is/b1-ai-memory-foundation` → A2; PR #45175633112 ve push #45175625717 SUCCESS. Canlı sağlayıcı ve telefon testi/bağımsız inceleme bekliyor.
- C2 PR #22 açık/taslak, `is/kariyer-1-21-verified-search` → `kariyer-modulu`; APK + Full App QA API29/33/34/35 SUCCESS. Main'e birleşmedi; telefon testi/bağımsız inceleme bekliyor.
- C3 ana PR #29 açık/taslak: `is/c3-ilan-semasi-ai-kaynak-kaniti` → `kariyer-modulu`; #29 için GitHub review kaydı yok. PR #30 açık/taslak ve mergeable: `fix/c3-ai-consent-and-navigation-guard` → PR #29 dalı.
- C3 PR #30 HEAD `5f6eebb816fadfa8e1c7c51b4ccce1234d1cee03`; APK #638 (run 45226233478) SUCCESS; Full App QA #84 (run 45226233411) SUCCESS.
- Full QA #84: statik denetim/Lint/birim test/APK ve Android 29, 33, 34, 35 genel UI audit işlerinin tümü SUCCESS. API35 summary: 20 ekran, 0 hata, 26 uyarı, 1 manuel okunabilirlik incelemesi.
- QA kapsam sınırı: Full App QA #84 C3 WebView kaynağını açmadı, ilan metni okumadı ve AI gönderme/iptal/fallback/CAPTCHA etkileşimini UI üzerinden test etmedi. Yeşil koşu genel uygulama smoke/UI denetimidir.
- C3 #30 diff bağımsız statik incelendi: AI gönderiminden önce ayrı açık onay; olası sağlayıcı sırası/fallback açıklaması; kaynak URL prompt dışında; CV/profil gönderilmiyor; HTTPS, aynı-host üst seviye gezinme ve subresource URL kontrolleri; CAPTCHA/login/403'te durma; boş/401 ve unsafe URL testleri.
- Telefon denemesi için APK alındı: Actions #638 artefaktı `14842318551`, içinden `app-debug.apk` çıkarıldı (14,888,667 bayt; SHA-256 `99a02f7571538482aa1158a2a161717e6ead81d99caccd10261c5ddf43e13190`). CI imza adımı başarılı; bu oturumda `apksigner` bulunmadığı için APK imzası bağımsız yerel araçla kontrol edilmedi.
- Sınır: DNS çözümlemesi özel IP'ye yönlendirilen public hostname riskini tamamen engellediği kanıtlanmadı; PR da tam ağ-seviyesi SSRF koruması iddia etmiyor. Kişisel veri maskesi eksiksiz değil; kullanıcıya açıklanıyor.
- C3 üç Resmî Gazete örneği bağımsız kontrol edildi: AFAD 13 kadro/P8-P9-P16-P21-P32-P43 ve P4/23–30 Kasım; TÜRKAK 3 ön lisans pozisyonu/P93≥70/23 Kasım–7 Aralık; TGA 13 pozisyon/3 Kasım–4 Aralık ve 4857 iş kanunu statüsü. Bunlar uygulamanın otomatik okuyucusunu/telefon WebView'ini kanıtlamaz.
- C3 #30 PR açıklaması güncel CI kanıtıyla düzeltildi. GitHub'a COMMENTED hakem notu bırakıldı (review id `6026649200`); APPROVE değil. Kayıt PR yazarıyla aynı `aries919-bot` hesabından geldiği için bağımsız insan incelemesi sayılmaz.
- C3 PR #29/#30 için gerçek cihaz WebView, gönderme iptali, onaylı sağlayıcı akışı, yönlendirme ve giriş/CAPTCHA davranışı henüz denenmedi. PR #29/#30 için ONAY/merge yok.
- E1 ikiye ayrıldı: E1-Veri Cline; E1-UI/Xiaomi ChatGPT. Aynı dosyada paralel değişiklik yok.
- Diğer açık/eskiden kalmış PR'lar: #23/#18/#17/#9/#4/#2/#1; farklı tabanlı eski PR'lar main'e birleşmiş sayılmaz.

## İş listesi ve sahiplik
| İş | Durum | Sahip / Not |
|---|---|---|
| K1 Kaynak matrisi ve canlı okuyucu denetimi | CLINE BEKLİYOR | sahip: cline; eski PR #24'e dokunma |
| A1 İki ekran taslağı | ONAYLANDI | sahip: chatgpt; ana ekran + İş & Kariyer Cem tarafından onaylandı |
| A2 Tüm uygulama tema sistemi | PR AÇIK | sahip: chatgpt; #25/#27/#28 CI yeşil; bağımsız inceleme ve telefon testi |
| B1 Streaming, bellek ve hata yönetimi | PR AÇIK | sahip: chatgpt; #26 CI yeşil; canlı API, bağımsız inceleme ve telefon testi |
| C1 Sunucu ilan toplayıcı + tekilleştirme JSON | DEVAM — TEST + LOCAL D1 PASS; KAYNAK/PROD EŞİĞİ RED | sahip: chatgpt; jarvis-cloud PR #5 açık/taslak, HEAD `0ec0fdd`; 33 test PASS; canlı audit 112 private deadline-unknown + kamu 1/43; 6 Ashby robots HTTP 401 stale; local D1 8/8 PASS; production publish SKIPPED; 10/100 sağlanmadı. |
| C2 İlan listeleri, geçmiş, Haber/Duyuru ayrımı | PR AÇIK | sahip: chatgpt; #22 + #31; son HEAD `4ec0158`; CI #671/#116 sürüyor, gerçek telefon testi bekliyor |
| C3 İlan şeması, AI alan çıkarımı, kullanıcı tetikli tarayıcı yedeği | DEVAM | sahip: chatgpt; #29/#30 açık taslak; #30 APK #638 + Full QA #84 yeşil; gerçek cihaz ve bağımsız insan incelemesi bekliyor |
| C4 CV + KPSS tercih/uygunluk modülü | YAPILMADI | sahip: chatgpt; kişisel profil telefonda kalır |
| C5 Gelecek alım takvimi ve bildirim | YAPILMADI | sahip: chatgpt; resmî tarih ile tahmin ayrı |
| D1 Film & Dizi | YAPILMADI | sahip: chatgpt; yalnız yasal/izinli akış |
| E1-Veri Altın/Borsa kaynak ve JSON sunucusu | CLINE BEKLİYOR | sahip: cline; UI dosyalarını değiştirme |
| E1-UI Piyasa ekranı/grafik/alarmlar/Xiaomi yönlendirmesi | YAPILMADI | sahip: chatgpt; Cline veri sözleşmesi bekleniyor |
| Z1 Gerçek telefon QA | TELEFONDA DENENECEK | mikrofon, Xiaomi pil/otomatik başlatma, gerçek bildirim/alarm, canlı API, gerçek oynatma ve tema kalıcılığı |

## 23.11 kullanıcı ekranı teknik teşhisleri
- C2 ekran kök nedenleri: Haber tarama düğmeleri Takip'e, Takip'teki genel kura düğmesi kişisel Sonuç'a gidiyordu; #31 doğru Haber hedeflerine yönlendiriyor. İlan seçicisinden Kura/Staj çıkarıldı; Kura/Sınav manuel araması yalnız süreç panosunu besliyor; Haber arşivinde kura filtresi yok. Geçmiş, yayımlanmış ve tarihsiz/doğrulanmamış sonuçlar yaklaşanlardan ayrıldı. Yayın tarihi ile son tarama saati ayrıldı. Eski kategori tercihi yalnız ilk açılışta tek sefer göç ettiriliyor. Haber için kamu alım sorgusu/45 sn üst sınırı var.
- C2 PR #31 açık/taslak: `fix/c2-news-search-in-news` → `is/c3-ilan-semasi-ai-kaynak-kaniti`; C3 PR #30'a yığılmıştır, #30 önce birleşmelidir. HEAD `c6795908ccd753f47b216f34c59bc697df4f4d5d`; APK #673 SUCCESS; Full QA #118 tüm 5 işi SUCCESS (141 test/0 fail; Lint 0 error; API29/33/34/35 UI 0 hata, 20 ekran/API).
- İlan aramasının 0/12 nedeni hâlâ HTML arama zinciri: DDG güvenlik kontrolü, Bing sayfası parse edilemiyor, Brave limit; 12 sorgu/4 iş parçacığı ve 90 sn üst sınırı. Kategori10 Haber araması 45 sn. Kaynak kataloğu canlı reader değil; kalıcı çözüm K1/C1 Cline kaynak denetimi ve ücretsiz sunucu JSON toplayıcısıdır. #31 0/12'yi çözmez.
- Bildirim davranışı: Kariyer takibi etkinse WorkManager 08:30/20:30 civarında iki günlük tarama planlar (kesin saat garantisi yok); manuel tarama bildirim üretmez. Yalnız yeni, güvenilir resmî kaynaklı, gelecek son tarihli ilanlar ve belirli süreç aşamaları bildirilir; Android 13+ POST_NOTIFICATIONS izni yoksa sessizce atlanır.
- Chrome beyaz ekran: kariyer kartı kaynak bağlantıları `Intent.ACTION_VIEW` ile harici tarayıcıya açılır; Chrome render'ı Aries kontrol etmez. Görseldeki `app.simplelogin.io` SimpleLogin web uygulaması alan adıdır (iş/kamu ilan portalı değildir); kırpılmış görüntü, o URL'ye hangi sonuç/düğmenin götürdüğünü kanıtlamaz.
- Tema: A2 #28 hâlâ açık/taslak. `is/a2-theme-completion` dalında kaydedilen açık/koyu tercih var; #638'in C3 dalındaki `MainActivity` bunu içermiyor. Uygulama kodunda seçilen arka plan görselini kaydedip ekrana bağlayan akış bulunmadı.
- Film: #638'in C3 kaynak ağacındaki `MediaSourceBrowser` arama tarafı yalnız DuckDuckGo HTML ve iki DOM seçicisi kullanıyor; tarayıcı isteği/DOM kaynaklarından HLS/DASH/MP4 vb. aday akışı yakalamaya çalışıyor ve yaklaşık 18 sn sonra kaynak değiştiriyor. Çoklu motor keşfi ayrı medya PR/branch'inde; DRM/giriş/CAPTCHA/özel player kısıtlarını aşma yok. Gerçek oynatma cihazda denenmedi.
- C2 APK #673 artefaktı `14856755454`: 14,905,051 bayt; SHA-256 `4c017118fa000eded5403f1d540785a975c9bb5e760d65cc6d6495cca178bb0b`. PR #31 test derlemesi; ana dala birleşmedi.
- 23.11.2026 resmî alım örneği: İçişleri 1.170 + Jandarma 9.000 = 10.170; İçişleri Malatya tablosunda 3 P3 lisans yeri ve Yeşilyurt'ta 1 P94 temizlik görevlisi yeri var. P94 başvurusu yalnız uygun unvan/şartlarla değerlendirilmeli.

## H/K1/C1 v3 görev kuyruğu — 24.11.2026

> Aktif kapsam yalnız İş & Kariyer/Haber doğrulamasıdır. Cline kullanılmaz; bütün işler ChatGPT tarafından ayrı dallarda PR ile yapılır. Cem yalnız telefon doğrulaması ve birleştirme onayı verir. Ayrıntılı kurallar: [NOT_C1.md](NOT_C1.md).

| Kimlik | Durum | Sahip / Kanıt / Sonraki adım |
|---|---|---|
| K1-E1 | KABUL | ChatGPT hakem kararı; Actions #45388925995 SUCCESS, 3 sayfa/43 benzersiz ilan/8 kurum; Cem 3 ilanı e-Şube'de elle doğruladı. 1 kamu kaynağı/43 ilan; 10/100 eşiği henüz sağlanmadı. |
| K1-F | İPTAL | v3 ile iptal; yerine K1-F2 geldi. |
| K1-F2 | PR AÇIK — HAKEM ENGELİ | PR #33: [K1-F2](https://github.com/aries919-bot/Jarvis/pull/33), `is/K1-F2` → `cline/K1`; diff yalnız 2 silme dosyası. Actions [#45413014752](https://github.com/aries919-bot/Jarvis/actions/runs/45413014752) testleri 53/53 SUCCESS; [#45413014756](https://github.com/aries919-bot/Jarvis/actions/runs/45413014756) Hakem RED: silinen `scripts/render_probe.py` satırındaki tarayıcı UA kalıbı tarandı. Ayrı düzeltme PR #37 Claude onayı bekliyor; #33 birleştirilmedi. |
| HAKEM-DIFF | PR AÇIK — BAĞIMSIZ HAKEM / OTOMATİK AI BLOKE | PR #39: https://github.com/aries919-bot/Jarvis/pull/39, fix/hakem-added-diff-only → cline/K1; yalnız scripts/hakem_gate.py + scripts/test_hakem_gate.py. Güncel test #45428314746: 59/59 SUCCESS. Hakem #45428313177 RED: CF_ACCOUNT_ID ve CF_API_TOKEN boş; AI katmanı çalışmadı. Diff/testler bağımsız incelendi; PR birleştirilmedi. |
| H1 | PR AÇIK — ADAYLAR EKLENDİ | [PR #35](https://github.com/aries919-bot/Jarvis/pull/35), `is/H1` → `durum`; HABER_SEMA.md'de 6 kategori x 3 = 18 resmî aday kaydı var. K1-B ölçümüne göre yalnız 8 adres erişilebilir; kalan adaylar ölçüm/robots/TLS bekler. H1 henüz kabul değil. |
| K1-B | ÖLÇÜM VAR — PR AÇIK / KISMİ RED | PR #36: https://github.com/aries919-bot/Jarvis/pull/36; son ölçüm #45429536647: 20 benzersiz URL, 5 ÇALIŞIYOR / 15 ENGELLİ / 0 ÖLÜ. Modlar: 9 aktif ölçüm / 8 geçici kapalı / 3 yalnız yönlendirme. 8 kapalı adres = 5 TLS + 3 timeout; 3 Kariyer Kapısı/e-Devlet linkinde ağ isteği yok. Testler 15/15. Gerçek kamu ilanı: 1 kaynak (e-Şube) / 43 doğrulanmış ilan. |
| K1-C | PR AÇIK — GÜNCEL RAPOR + TEST | PR #38: https://github.com/aries919-bot/Jarvis/pull/38, is/K1-C → durum; 4 dosya: rapor, aday listesi, rapor testi, workflow. K1-B #45429536647'ye göre 20 URL: 5 ÇALIŞIYOR / 15 ENGELLİ / 0 ÖLÜ; rapor 20/20 benzersiz kaynağı gösterir. CI #45430917966: 6/6 SUCCESS. Kamu 1 kaynak / 43 doğrulanmış ilan; PR birleştirilmedi. |
| H2 | PR AÇIK — KISMİ / BOTAŞ STALE | PR #40: https://github.com/aries919-bot/Jarvis/pull/40, is/H2-verified-reader → durum, taslak. Test CI #45437948449: 19/19 SUCCESS. Canlı reader #45437928827 SUCCESS; 2 kaynakta 1 fresh / 1 stale / 1 kart. YHGM kura kartı doğru tarih ayrımıyla üretildi; BOTAŞ detay URL'si TIMEOUT + tek retry TIMEOUT oldu. Artefakt #14942950569. H2 yeni kamu işi sayısına katkı sağlamadı; merge yok. |
| C1-1 | DEVAM — 33/33 TEST + 8/8 D1 LOCAL PASS; KAYNAK/PROD EŞİĞİ RED | jarvis-cloud PR #5 açık/taslak; HEAD `0ec0fdd`; 13 dosya +2.529/-6. QA #45464977542: 33/33 PASS, local Wrangler/D1 8/8 PASS. Canlı audit: 16 kaynak, 10 fresh/6 stale; 112 özel aday deadline unknown; kamu 1/43; yalnız 1 kaynakta 43 açık deadline, 10/100 RED. Production Worker publish SKIPPED. |
| C1-2 | RAPOR HAZIR — RED | [gorevler/C1-2.md](../gorevler/C1-2.md); live report artefakt [#14954582973](https://github.com/aries919-bot/Jarvis/actions/runs/45464977542/artifacts/14954582973). 9/15 özel kaynak fresh, 6 stale; 112 private deadline unknown; kamu 1/43; doğrulanmış eşik 1 kaynak/43 ilan, 10/100 NOT_MET. UI yok. |
| K1-M | YAPILMADI | Gerekli workflow/betiklerin temiz PR ile main'e taşınması; film commitleri hariç, cron yalnız main'de. |
| U1 | YAPILMADI | Uygulama entegrasyonu; arama motoru kazıması kapalı, H1 Haber kartları ve Custom Tabs. Telefon doğrulaması Cem'de. |

### K1-E1 doğrulama kaydı — 24.11.2026
- Commit: [efd1aaea](https://github.com/aries919-bot/Jarvis/commit/efd1aaea25f4e7554241c68b88c7ffe72a121d99); workflow: [kamu-ilan-cek #45388925995](https://github.com/aries919-bot/Jarvis/actions/runs/45388925995) — push, completed/success; tüm 7 ana adım SUCCESS.
- JSON `durum` dalında tekrar okundu: [career/esube_kamu_ilanlari.json](../career/esube_kamu_ilanlari.json), 3 sayfa, 43 satır, 43 benzersiz ilan, 0 tekrar, 8 kurum.
- Cem'in telefon doğrulaması: 00009905681 Muğla Merkez / 6 pozisyon; 00009905686 İzmir Merkez / 10 pozisyon; 00009905689 İstanbul Merkez / 24 pozisyon. Üçü de MSB Personel GM ve son başvuru 27.11.2026. Sayfa altı “3 / 3, Toplam Kayıt: 43”.
- Satırlarda yayın tarihi yok; `publishedAt=null`. e-Şube satır tarihi son başvurudur.
- Kaynak sayımı: 1 kamu kaynağı / 43 ilan. 10 kaynak / 100 ilan eşiği sağlanmadı; bu yüzden yalnız K1-E1 veri hattı KABUL, bütün C1 hedefi değil.
- Tarih sınırı örneği: 00009908955, Karayolları 9. Bölge / Diyarbakır / son başvuru 24.11.2026. 24.11 sonrasında güncel listeden çıkarılmalıdır.

### K1-F2 denetimi — 24.11.2026
- `cline/K1` tabanından `is/K1-F2` dalı açıldı.
- Yalnız `.github/workflows/iskur-render.yml` ve `scripts/render_probe.py` silindi. Compare çıktısı: 2 dosya removed, 0 ekleme; toplam 606 satır silindi.
- PR #33 açık. Actions commit-run listesi boş ve birleşik commit durumları boş; 46 test yeşil kanıtı henüz yok. Yerel `git clone` denemesi DNS çözümlemesinde başarısız oldu. Test kanıtı gelene kadar K1-F2 tamamlanmış sayılmaz.

### C1-1 başlangıç denetimi — 24.11.2026
- jarvis-cloud PR #5 RED: 15 adayın tamamı özel sektör Lever/Ashby; kamu kaynağı yok; CI #45382024807 başarısız.
- Statik QA: GET `/kariyer-feed` yok; `firstSeenAt` kalıcı değil; kısmi kaynak hatasında son iyi veri ezilebilir; Worker zamanlanmış işte ağır toplama yapıyor. Ağır toplama Actions'a taşınmalı.
- Test açığı: 403 ve timeout senaryoları eksik; canlı gerçek ilan kanıtı yok. 15 kayıt sentetik fixture'dır.
- Başlangıç kaynak ölçümü: Kariyer Kapısı `/isealim` JS kabuğu; e-Şube arama formu statik GET'te ilan döndürmüyor; ilan.gov.tr önceki ölçümde 502 verdi; ÇSGB örneği 11.10.2026'da sona ermişti. Bu bulgular 10 kaynak/100 ilan sayımına eklenmez.

## PR hakem ve birleştirme akışı (v3)
- Her iş kendi dalında geliştirilir ve doğru tabana PR açılır; `main`'e doğrudan yazılmaz.
- ChatGPT PR'ı sıfırdan şüpheci inceler; diff dosya sayısını yeniden sayar, komut/test çıktısı ve Actions bağlantısını doğrular.
- CI/test kanıtı yoksa iş tamamlandı/KABUL sayılmaz. Kaynak ve ilan sayıları bağımsız yeniden sayılır.
- PR birleştirilmeden önce Cem'in açık “birleştir” onayı beklenir. Cline görev sahibi değildir ve kullanılmaz.

## Son 10 kayıt
1. K1 legacy PR #24 20 kaynak envanteri; güncel Cline K1 denetimi bekleniyor.
3. A1 iki ekran tasarımı onaylandı.
4. A2 #25 ilk CI NPE; ikinci deneme test+APK SUCCESS, kök neden bilinmiyor.
5. B1 #26 bellek/streaming; CI yeşil, bağımsız inceleme ve telefon testi bekliyor.
6. A2 #27 workflow-only tanılama; PR/push CI SUCCESS.
7. A2 #28 açık/koyu mod, dashboard sayacı ve skeleton; PR/push CI SUCCESS.
8. Sahiplik güncellemesi: K1 ve E1-Veri Cline; C1 ChatGPT (jarvis-cloud PR #5); diğer uygulama işleri ChatGPT.
9. C2 #22 kategoriler ve Haber/Süreç/Program ayrımı; APK + API29/33/34/35 Full QA SUCCESS.
10. C3 #30 HEAD `5f6eebb`: APK #638 + Full QA #84 SUCCESS; PR açıklaması ve COMMENTED hakem notu güncellendi (onay değil); genel emülatör QA C3 WebView akışını kapsamıyor.
**SONRAKİ ADIM:** C1 PR #5'te kimlik doğrulamalı GET /kariyer-feed rotası, kalıcı firstSeenAt ve eşik altı koşulda son iyi feed korumasını tamamla; sonra CI'ı yeşil çalıştırıp gerçek canlı kaynak başına sayıları ve JSON'da 10 kaynak/100 ilanı doğrula. Android entegrasyonuna geçme.

11. 24.11 C1 bağımsız QA: PR #5'te GET feed rotası/firstSeenAt yok; kısmi feed son iyiyi ezebilir; Worker ağır toplama yapıyor. #45382024807 failure (step/log yok). Kariyer Kapısı JS kabuğu, e-Şube formu, ilan.gov.tr 502; ÇSGB örneği süresi geçmiş. Cline K1 dalı mevcut; işine dokunulmadı.


### K1-B düzeltme notu — 24.11.2026

- Kamu gerçek ilan kaynağı: **1 (e-Şube) / 43 doğrulanmış ilan**. 10 kaynak / 100 ilan eşiği sağlanmadı.
- BOTAŞ'ın 2025 personel alımına ilişkin sonuç duyurusu yeni ilan değil; kart etiketi **Süresi geçti**.
- Sağlık Bakanlığı YHGM 2.764 sürekli işçi kura/sonuç duyurusu yeni ilan değil; kategori **Haber / Kura**.
- TLS hatalı 5 URL: https://www.ilan.gov.tr/ ; https://www.tuseb.gov.tr/public/tr/duyurular ; https://www.tuseb.gov.tr/kurumsal/insan-kaynaklari ; https://www.ptt.gov.tr/duyurular ; https://ilan.gov.tr/ . Beşinde aynı hata: CERTIFICATE_VERIFY_FAILED: unable to get local issuer certificate. Issuer zinciri/runner CA doğrulaması tamamlanamadı; expired hatası yok. Sunucu ara sertifikası ile runner CA deposu farkı bu logdan ayrıştırılamadı. SSL doğrulaması kapatılmadı.
- Timeout adresleri: Resmî Gazete /ilanlar ve / kök sayfası. [#45405355596](https://github.com/aries919-bot/Jarvis/actions/runs/45405355596), [#45405516646](https://github.com/aries919-bot/Jarvis/actions/runs/45405516646), [#45405558813](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813) loglarında robots izinli + TIMEOUT olarak üç çağrı var; tek retry sınırı aşıldı. TPAO PDF robots BİLİNMİYOR kaldı; hedef PDF'ye erişilmedi. Artık yeni istek yapılmayacak.
- #32 docs-only PR birleştirildi: commit 23d2a0877169c16e5bd2c99a1f2076cc5f36bd6d; iki doküman dosyası. PR eklemelerinde token/secret, CV-iletişim, TCKN ve KPSS/e-Devlet kimlik desen taraması: 5/5 NO MATCH.
- İlk yetki testi: dal açma/dosya yazma EVET (PR #37 ve #38 dallarında); PR açma EVET (#37/#38); Actions okuma EVET (#45415984321 ve #45415984470). docs/NOT_C1.md v3 FINAL durum dalında doğrulandı; docs/TALIMAT.md başlığı v3 FINAL'e yönlendirildi ve v2'nin yerine geçtiği belirtildi. main'e yazılmadı.


### K1-B / K1-C / H2 son kanıt güncellemesi — 24.11.2026

- K1-B son ölçüm #45429536647: 20/20 benzersiz URL; 5 ÇALIŞIYOR, 15 ENGELLİ, 0 ÖLÜ. 9 aktif ölçüm, 8 geçici kapalı (TLS 5 + timeout 3), 3 yalnız yönlendirme. Kapalı ve yönlendirme URL'lerinde yeni ölçüm yapılmadı.
- K1-C PR #38 raporu yeni K1-B satır sayımıyla güncellendi. Rapor CI #45430917966: 6/6 test SUCCESS.
- H2 PR #40: YHGM 1 fresh kura/haber kaydı; BOTAŞ detay sayfası iki timeout denemesi sonrası stale kaldı. CI #45437928827 başarılı; test #45437948449 19/19. Artefakt #14942950569 kontrol edildi. H2 iki kaynağın ikisini de canlı doğrulamadı; tam kabul yok.
- H2 firstSeenAt branch cache'i geçici korur; kalıcı veri deposu C1-1 Worker görevidir.
- Gerçek kamu ilan kaynağı ve ilan sayısı değişmedi: 1 kaynak / 43 doğrulanmış ilan. 10/100 hedefi RED; C1-2/arayüz entegrasyonuna geçiş yok.


### C1-1 Worker PR #5 devam denetimi — 24.11.2026

- PR: https://github.com/aries919-bot/jarvis-cloud/pull/5 — hedef `main`, taslak/açık, birleştirilmedi. HEAD `61c05e818a7844ef5a2ea23476e68fc0acf50032`; diff yeniden sayımı 12 dosya, +2.209 / -5.
- Worker tarafı: kimlik doğrulamalı GET/POST `/kariyer-feed`; JSON D1'de ham metin olarak tutulur ve GET'te ham döner. Eski `career_feed(id, generated_at, payload)` şeması korunur; stale metadata ayrı `career_feed_meta` tablosunda D1 batch ile birlikte yazılır. Ağır toplama Worker zamanlayıcısından çıkarılıp Actions betiğine taşındı.
- Kaynak birleştirme: başarıyla okunan kaynak yalnız kendi ilanlarını yeniler; hata veren kaynak kendi son iyi kayıtlarını stale tutar. `firstSeenAt` önceki feed'den korunur. Türkiye yerel takviminde süresi geçen yeni/eski kayıtlar güncel listeden çıkarılır.
- Kart alanları: kategori, resmî ATS başvuru bağlantısı, nasıl başvurulur, şart açıklaması, publishedAt/deadline/collectedAt/firstSeenAt ve güncellik etiketi. ATS URL'leri yalnız Lever/Ashby resmî hostlarına izin verir. User-Agent: `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)`.
- Test dosyalarında 29 vaka tanımlı: kariyer okuyucu 14, kaynak/kalıcılık 6, Worker HTTP rotası 3, e-Şube girdisi 6. **Çalıştırıldığı kanıtlanmadı**: son PR CI #45447411669 ve push CI #45447399299 ikisi de `runner_id=0`, `steps=[]`; komut/test adımı hiç başlamadı.
- PR eklenen satırları `main` ile karşılaştırmalı tarandı: 2.209 eklenen satır, token/özel anahtar/CV/KPSS/e-Devlet deseninde 0 eşleşme.
- Kamu kaynağı hâlâ yalnız e-Şube: girdinin çekim zamanı `2026-11-24T11:57:55+00:00`; 43 satır / 43 benzersiz ilan. 24.11.2026 referans gününde 43'ünün son tarihi geçmemişti (1 ilan 24.11, 42 ilan 27.11). 15 özel işveren panosu yalnız adaydır; Actions başlamadığı için kaynak başına canlı sayıları ölçülemedi.
- Sonuç: C1-1 **RED / kısmi kod hazır**. CI yeşil değil, canlı Actions çıktısı yok, Worker'ın dağıtılmış uçtan uca kalıcılığı doğrulanmadı. Kamu 1 kaynak / 43 ilan; 10 kaynak / 100 doğrulanmış ilan eşiği RED. Arayüz işi yok, PR birleştirilmez.

**SONRAKİ ADIM:** jarvis-cloud Actions runner sorunu çözülüp PR/push CI gerçek runner'da çalışmalı; ardından canlı kaynak raporu ve Worker dağıtımı sonrası kalıcı feed doğrulanmalı.

### Actions runner tanılaması — 24.11.2026

- GitHub Status sayfası kontrol anında “All Systems Operational”; Actions bileşeni “Operational” gösteriyordu. 24 Kasım geçmiş olay kaydı Copilot model sağlayıcısına aitti, Actions'a değil. Genel Actions kesintisi kanıtı yok.
- GitHub Community'de 18.11.2026 tarihli özel depo tartışmasında aynı belirti var: `runner_id=0`, boş `steps`, kullanılabilir günlük yok. Topluluk yanıtı; başarısız koşunun açıklama/annotation alanını, Actions kullanım-bütçe sınırını, Settings → Actions → General izinlerini kontrol etmeyi; sebep görünmüyorsa GitHub Support'a run ID'leri iletmeyi öneriyor. Bu bir topluluk önerisidir, kesin kök neden değildir.
- Bu depo için Actions izin endpoint'i bu denetim aracından okunamadı. Faturaya, hesap kısıtına veya depo ayarına dair varsayım yapılmıyor.
- Telefonda GitHub depo Settings → Actions → General altında GitHub-hosted runner kullanımının açık olduğu; Billing → Actions kullanım/bütçe sınırının dolmadığı; başarısız koşu sayfasında üst annotation bulunup bulunmadığı kontrol edilmeli. Annotation/ayar sorunu görünmüyorsa Support'a şu run ID'ler gönderilmeli: `45447411669`, `45447399299`.


### C1-1 düzeltme ve son CI kanıtı — 24.11.2026
- PR #5 güncel HEAD: `197295813ed0ac5cc5ec49d8df7d95b677d77322`; açık/taslak, main'e birleşmedi.
- PR diff yeniden sayımı: 12 dosya, +2.270 / -5. PR açıklaması güncel HEAD ve kanıtlarla eşitlendi.
- Statik QA düzeltmesi: robots.txt politikası origin başına bir kez alınır, karar her ilan panosu API yolu için ayrı hesaplanır. `fetchText` içinde `redirect: "manual"`; 3xx otomatik izlenmez.
- Yeni iki regresyon testi: aynı API hostundaki farklı yol için farklı robots kararı; HTTP yönlendirmesinin otomatik takip edilmemesi.
- Test sayımı yeniden yapıldı: `career-feed` 16 + `career-feed-state` 6 + `career-feed-route` 3 + `esube-input` 6 = **31 yazılı test**. Çalıştırıldıkları kanıtlanmadı.
- PR patch yeniden sayımı: 12 dosya ve 2.270 eklenen satır. Eklenen satırlarda token/secret/özel anahtar/CV/KPSS/e-Devlet/TCKN desenleri: **0 eşleşme**.
- Son PR koşusu [#45449449913](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45449449913), güncel HEAD ile eşleşiyor, conclusion failure. Job `138642181122` `syntax-and-unit`: GitHub API ham job kaydı `runner_id=0`, `runner_name=""`, `steps=[]`. Log endpoint 404 BlobNotFound; syntax ve test komutu başlamadı.
- Kamu: son ölçülmüş snapshot 1 e-Şube kaynağı / 43 benzersiz kayıt; çekim zamanı `2026-11-24T11:57:55+00:00`. 15 Lever/Ashby panosu adaydır; canlı ilan sayıları ölçülmedi.
- Eşik 10 kaynak / 100 doğrulanmış ilan sağlanmadı. C1-1 RED; C1-2 ve UI entegrasyonu yok. Worker deploy ve uçtan uca D1 kalıcılık testi yok.
- Depo metadata okuması `admin=true`, `push=true` gösteriyor; bu Actions runner/billing ayarlarını kanıtlamaz. Actions permissions endpoint'i entegrasyondan okunamadı. Kök neden kesin değil.
**SONRAKİ ADIM:** runner atanmama sorununun Actions annotation/billing/repo settings tarafında çözülmesi; ardından güncel HEAD için 31 testli PR CI yeşile dönmeli. Merge ve deploy yok.

### C1-1 yeniden deneme — 24.11.2026
- Son PR koşusu [#45449449913](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45449449913) için başarısız job bir kez yeniden çalıştırıldı (attempt 2).
- Yeni job [138647328405](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45449449913/job/138647328405): `syntax-and-unit`, completed/failure, `steps=[]`. Hiçbir syntax veya test adımı çalışmadı. İlk attempt 1'de runner_id=0 idi; ikinci denemenin job adım listesi de boş.
- Node v22.16.0 ile ayrı, izole mikro-harness çalıştırıldı; aynı robots karar fonksiyonu kuralı için `trendyol=reddedildi`, `getmidas=izinli` ve manuel redirect ayarı için iki PASS çıktısı alındı. Bu, depo testleri değildir; `npm run test:c1` yerine geçmez.
- PR #5 açıklaması yeniden deneme ve sınırlı mikro-harness kanıtıyla güncellendi. HEAD `197295813ed0ac5cc5ec49d8df7d95b677d77322`; 12 dosya, +2.270/-5; 31 test tanımı.
- Sonuç değişmedi: C1-1 RED; kamu 1 kaynak/43 ölçülmüş benzersiz kayıt; 15 özel pano aday, canlı ilan adetleri ölçülmedi. Merge/deploy yok.
**SONRAKİ ADIM:** GitHub Actions runner atama/hesap ayarı sorunu giderilmeli; güncel PR HEAD üzerinde tüm 31 test yeşil geçmeli. Sonra güncel e-Şube JSON'u ve kaynak başına canlı ilan sayıları raporlanmalı.

### C1-1 güncel QA ve Codespaces ayrımı — 24.11.2026
- jarvis-cloud PR #5 HEAD `197295813ed0ac5cc5ec49d8df7d95b677d77322`, 12 dosya (+2.270/-5), 31 test yazılı. Üçüncü Actions denemesi [#45449449913](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45449449913) da RED: job [138653036621](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45449449913/job/138653036621), `runner_id=0`, `runner_name=""`, `steps=[]`; log BlobNotFound, artefakt yok. Test komutu başlamadı.
- Kullanıcının ekran görüntüsü Codespaces ücretsiz kullanım/bütçe uyarısıdır. Bu Actions kotasının dolduğunu kanıtlamaz. GitHub resmî belgeleri Codespaces compute/storage ve Actions hosted-runner compute kullanımını ayrı açıklar; önceden oluşturulmuş Codespaces üretimi Actions runner compute kullanır. Ücret/kart eklenmedi. Ayrı Actions usage ve repo runner/policy ayarı kontrol edilmeli. Belgeler: https://docs.github.com/en/billing/concepts/product-billing/github-codespaces ve https://docs.github.com/en/billing/concepts/product-billing/github-actions
- Resmî GitHub durum sayfasında 24.11 kontrolünde All Systems Operational, Actions Operational; mevcut Actions genel kesintisi kanıtı yok: https://www.githubstatus.com/
- jarvis-cloud workflow listesinde validate.yml ve deploy.yml var; validate işi syntax + `npm run test:c1` + PR dışı canlı kaynak raporunu çalıştırır. Üçüncü denemede sıfır step olduğundan hiçbir QA çalışmadı; ayrı Android Full App QA bu backend deposunun workflow'unda yok. Yerel Node `v22.16.0`; özel depoyu container'a çekme girişimi DNS nedeniyle başarısız olduğundan gerçek npm test komutu yerelde de çalıştırılmadı.
- Kamu ayrı sayım değişmedi: 1 e-Şube kaynağı / 43 benzersiz ilan; snapshot `2026-11-24T11:57:55+00:00`. 15 Lever/Ashby panosu aday, canlı sayıları yok. 10 kaynak/100 doğrulanmış ilan eşiği RED.
- Cem birleştirme izni verdi; fakat test/CI ve kaynak eşikleri sağlanmadığı için PR #5 birleştirilmedi. C1-1 RED, C1-2 ve UI beklemede.


### C1-1: düşük maliyetli runner alternatifi — 24.11.2026
- jarvis-cloud PR #5 HEAD `411d7c931429844fa32f8a8b76273df59caa2181`, 12 dosya (+2.271/-6), 31 test yazılı.
- Workflow `ubuntu-latest` yerine `ubuntu-slim` kullanacak şekilde değiştirilip denendi. [CI #45454211048](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45454211048), job [138659159236](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45454211048/job/138659159236): `runner_id=0`, `runner_name=""`, `steps=[]`; log BlobNotFound. Bu da runner başlatmadı; 31 test 0 çalıştı.
- Hesap karşılaştırması: public Jarvis [Actions #45388925995](https://github.com/aries919-bot/Jarvis/actions/runs/45388925995), job [138427138064](https://github.com/aries919-bot/Jarvis/actions/runs/45388925995/job/138427138064): `ubuntu-latest`, gerçek runner `1000001350`, 10 adım SUCCESS. Genel Actions erişimi var; private jarvis-cloud runner tahsisi başarısız.
- GitHub dokümanı: public standart runner'lar ücretsiz/sınırsız; private runner'lar included compute kullanımından düşer. `ubuntu-slim` 1 CPU/5 GB ile düşük maliyetli seçenektir ama private included compute havuzundan ayrı/limitsiz değildir. Kaynak: https://docs.github.com/en/actions/reference/runners/github-hosted-runners
- Codespaces ekranı tek başına Actions kotasının tükendiğini kanıtlamaz; normal Codespaces compute/storage ayrı ölçülür, Codespaces prebuild runner compute kullanabilir. Private Actions runner usage, GitHub included compute usage'dan düşer. Billing satırı/annotation görülmeden kök neden kesin değil. Belgeler: https://docs.github.com/en/billing/concepts/product-billing/github-codespaces ; https://docs.github.com/en/billing/concepts/product-billing/github-actions
- Private backend kodunu public Jarvis deposuna taşımak kaynak kodunu açacağından yapılmadı. Self-hosted runner ücretsiz alternatif ancak erişilebilir kayıtlı cihaz yok; telefon runner'ı için Cem'den kurulum/komut istenmedi. Ücretli bütçe/kart eklenmedi.
- Yerel Node v22.16.0; özel repo içeriğini container'a indirme DNS/egress ile başarısız (`Could not resolve host: github.com`; raw IP bağlantısı da başarısız). Yerel gerçek test koşusu yok.
- Kamu: 1 e-Şube / 43 benzersiz ilan (snapshot `2026-11-24T11:57:55+00:00`). 15 Lever/Ashby kaynak adayında canlı ilan adedi ölçülmedi. 10 kaynak/100 doğrulanmış ilan eşiği RED.
- PR #5 açık/taslak/birleştirilmedi. Cem izin verdi; zorunlu QA/eşik geçilmediği için güvenli merge yok. C1-2, Worker dağıtımı ve Android entegrasyonu bekliyor.


- PR olayı da aynı alternatifte başarısız: [PR CI #45454224453](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45454224453), job [138659214985](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45454224453/job/138659214985), `ubuntu-slim`, `runner_id=0`, `steps=[]`. Hem push hem pull_request denemesi runner almadan durdu.


### Ana Jarvis Actions ekranı ve C1 QA ayrımı — 24.11.2026
- Cem'in ekran görüntüsündeki `Aries Career Source Audit` ve `Aries Full Application QA` workflow'ları ana `Jarvis` deposunun Actions geçmişinde doğrulandı. Bunlar private `jarvis-cloud` PR #5 workflow'larından ayrıdır.
- Career Source Audit başarılı run [#45163647889](https://github.com/aries919-bot/Jarvis/actions/runs/45163647889), job `137654992530`, SHA `8187ac87acfdd6ba1dc4829ccd736379d8bf667a`. Offline regression + public HTML probe + artifact upload SUCCESS. Artifact [14814205874](https://github.com/aries919-bot/Jarvis/actions/runs/45163647889/artifacts/14814205874) indirildi ve JSON yeniden sayıldı: 20 kaynak, 13 probed, 7 reader_ok, 3 açık başvuru penceresi kaynağı, 35 vacancy candidate links, 2 programme links. **35 aday link doğrulanmış/açık ilan sayısı değildir.**
- Kaynak bazında adaylar: Sayıştay 1 (deadline 25.11.2026); Anadolu Üniversitesi 1 (30.11.2026); TGA 1 (04.12.2026); Malatya Turgut Özal Üniversitesi 4 (deadline unknown); TUSAŞ 5 (deadline unknown); ROKETSAN 20 (deadline unknown); TEI 3 iş + 2 program (deadline unknown). Diğer 13 tanımlı kaynakta iş adayı 0; bazıları tasarım gereği taranmadı, bazıları network_error, blocked_or_captcha veya manual_only. ÖSYM 2 süreç duyurusu, iş ilanı değil. Kamu kaynağı/e-Şube bu artifact'te yok.
- Workflow `.github/workflows/career-source-audit.yml` ana Jarvis dalındaki `scripts/career_source_audit.py` ve `scripts/test_career_source_audit.py` dosyalarını test eder; private `jarvis-cloud/src/career-feed*.js` kodunu checkout etmez. Bu nedenle C1 backend testlerinin yerine geçmez.
- Full Application QA başarılı run [#45249266761](https://github.com/aries919-bot/Jarvis/actions/runs/45249266761), job `137954281603`, branch `fix/c2-news-search-in-news`, SHA `c6795908ccd753f47b216f34c59bc697df4f4d5d`: Android 14 emulator, 1 test / 0 failure / 0 skipped, BUILD SUCCESSFUL, 69 Gradle tasks, 20 screenshots, 0 error / 40 warning / 1 review finding; artifact `14857116162`. Başka branch/SHA ve Android UI kapsamı olduğundan C1 backend QA kanıtı değildir.
- Düzeltme: “Full App QA workflow'u yok” yalnız private `jarvis-cloud` deposu için doğruydu; ana Jarvis repo geçmişinde var. Ana Jarvis default `main` workflow klasöründe bugün yalnız `android.yml` var; named audit workflow run'ı kendi branch'inde mevcut.
- C1-1 karar değişmedi: 31 backend testi 0 çalıştırıldı; kamu snapshot 1 e-Şube / 43 ilan; 10 kaynak/100 doğrulanmış ilan eşiği sağlanmadı; PR #5 merge edilmedi.


### C1-1 ücretsiz runner alternatifi — 24.11.2026
- Ücretsiz public Actions runner yolu kuruldu: deney dalı `qa/c1-private-runner`, workflow `.github/workflows/c1-private-backend-qa-public.yml`. Private `jarvis-cloud` kaynağını public repoya kopyalamaz; Actions runner üzerinde checkout edip test etmeyi hedefler, credentials persist edilmez.
- [Run #45457283140](https://github.com/aries919-bot/Jarvis/actions/runs/45457283140), job [138670034709](https://github.com/aries919-bot/Jarvis/actions/runs/45457283140/job/138670034709): public runner gerçek olarak başladı; workflow checkout SUCCESS. Private repo read-only secret `JARVIS_CLOUD_READ_TOKEN` veya `JARVIS_SOURCE_READ_TOKEN` tanımlı olmadığından token kontrolü RED, private checkout/Node/test SKIPPED. Logda token boş olduğu ve secret olmadığı açık; hiçbir token değeri okunmadı veya yazdırılmadı.
- Böylece ödeme/Codespaces gerektirmeyen runner bulundu. Kalan zorunlu erişim koşulu: repo sahibi Jarvis public repo Settings → Secrets and variables → Actions içine yalnız `jarvis-cloud` için fine-grained read-only token'ı `JARVIS_CLOUD_READ_TOKEN` adıyla eklemeli. Token sohbetten paylaşılmamalı; bu ücretsiz UI ayarıdır. Alternatif erişilebilir self-hosted runner, mevcut değil.
- İlk taslak workflow düzeltildi; hatalı eski dosya deney dalından silindi. Aktif workflow yalnız public runner deney dalında.
- 31 backend test hâlâ 0 çalıştırıldı / 0 PASS. PR #5 open/draft/unmerged. Kamu 1 e-Şube / 43; 15 özel aday ölçülmedi; 10 kaynak/100 ilan eşiği RED.


### C1-1 secret sonrası backend QA sonucu — 24.11.2026
- Cem `JARVIS_CLOUD_READ_TOKEN` secret'ının kaydedildiğini bildirdi; değer sohbetten istenmedi/okunmadı/loglanmadı.
- Ücretsiz public runner workflow [#45461343191](https://github.com/aries919-bot/Jarvis/actions/runs/45461343191), job `138684419009`, SHA `e6f934670dc024db3f80c0dc85e5fed40b3d17b9`: public workflow checkout SUCCESS, secret check SUCCESS, private `jarvis-cloud` checkout SUCCESS, Node 22 SUCCESS, `npm run test:c1` SUCCESS.
- Node TAP sonucu: **31 tests / 31 pass / 0 fail / 0 cancelled / 0 skipped**. CI conclusion SUCCESS.
- İlk gerçek test çalışması [#45461164106](https://github.com/aries919-bot/Jarvis/actions/runs/45461164106) 29 PASS / 2 FAIL buldu. İki hata fixture beklentisiydi: mock, 9 Lever board'ın her birine birer geçmiş ilan veriyordu; toplam beklenen 1/15 idi. Testler sağlayıcıya göre düzeltildi: 9 Lever x 1 expired, 6 Ashby x 0; toplam expired 9. Üretim kodu ve source catalog sayısı değiştirilmedi. Private branch head `8fca11674762cfd63bdc6b8412df3374d36a6d83`.
- C1 test kapısı artık **PASS**; ancak genel C1-1 **RED / kabul bekliyor**: kamu kanıtı 1 e-Şube kaynağı/43 snapshot; 15 özel ATS adayının canlı sayımı yok; 10 kaynak/100 doğrulanmış ilan eşiği sağlanmadı. Worker production deployment ve gerçek D1 QA'sı da kanıtlanmadı. PR #5 açık/taslak/birleştirilmedi.


### C1-1 canlı ölçüm ve D1 kontrolü — 24.11.2026
- Ücretsiz runner [#45461343191](https://github.com/aries919-bot/Jarvis/actions/runs/45461343191): 31/31 backend regresyon testi SUCCESS.
- [Canlı audit #45461843163](https://github.com/aries919-bot/Jarvis/actions/runs/45461843163), 18:39:24 UTC: özel kaynaklar Trendyol 35, Midas 16, Commencis 7, Dream Games 17, Aleph 5, Ajax Systems 11, Insider One 22, Binance 0 Türkiye, Picus 1 = **114 özel iş adayı**. Hepsinin deadline unknown; doğrulanmış güncel ilan değil.
- 6 Ashby panosu robots.txt HTTP 401 nedeniyle stale/ölçülemedi; API çağrısı yok; 0 ilan varsayılmadı.
- Kamu ayrı: **1 e-Şube / 43 benzersiz ilan**. Snapshot 11:57:55 UTC, 12 saat sınırının altında ama live HTTP refresh değil. Kamu verisi özel adaylarla birleştirilmedi.
- Toplam 16 kaynak; fresh 10, stale 6; 114 unknown deadline; 43 public open deadline; doğrulanmış açık ilan veren kaynak 1; threshold 10/100 NOT_MET.
- Yerel Wrangler + D1 smoke [#45462136030](https://github.com/aries919-bot/Jarvis/actions/runs/45462136030) sürüyor: 31 test ve live audit geçti; smoke job halen in_progress idi. D1 sonucu henüz yok. Production publish SKIPPED; Worker URL/token çifti mevcut değil.
- PR #5 güncel HEAD `66c2734371190025617f6b678fd64382f18ddf15`, 13 dosya (+2.439/-6), açık/taslak. C1-1 genel kabul RED; 10/100 eşiği sağlanmadan C1-2/UI yok.


### C1-1 son doğrulama — 24.11.2026 18:47 UTC
- Private PR HEAD `86318bacfdc28d6f4da7252910dc9950ab12daaf`; PR #5 açık/taslak/birleştirilmedi, 13 dosya (+2.484/-6). `main` SHA `fb291c0ce06b05c61f988e71c3d6587ffc945e93`; PR main'e yazılmadı.
- Ücretsiz runner son koşu [#45463284336](https://github.com/aries919-bot/Jarvis/actions/runs/45463284336), job `138691370107`: private checkout SUCCESS; Node 22 SUCCESS; **32 test / 32 PASS / 0 FAIL / 0 skipped**; canlı kaynak audit SUCCESS; yerel Wrangler Worker + D1 HTTP smoke **8/8 PASS**. Job conclusion SUCCESS.
- D1 yerel smoke kontrolleri: health 200; auth yoksa 401; boş D1 ilk GET not_ready + stale; auth POST 202 ile feed kaydı; GET orijinal raw JSON'u byte-identical döndürür ve fresh header verir; stale metadata ayrı kalıcıdır; POST text/plain 415; geçersiz generatedAt 400. Bu izole Wrangler local D1 testidir, Cloudflare production D1 değildir.
- Deadline çıkarımı düzeltildi: Lever/Ashby açıklamasının görünen 700 karakteri yerine temizlenmiş açıklamanın ilk 50.000 karakteri taranır; kartta gösterilen conditions metni 700 karakterle sınırlı kalır. Yeni regresyon testi, 700 karakter sonundaki tarihin çıkarılmasını doğrular. Canlı ölçümde kaynak açıklamalarında açık deadline bulunmadığından ilan sayıları değişmedi.
- Son canlı ölçüm [#45463284336](https://github.com/aries919-bot/Jarvis/actions/runs/45463284336), `collectedAt=2026-11-24T18:47:41.235Z`. Özel ATS: Trendyol 44 raw/35 Türkiye adayı; Midas 17/16; Commencis 7/7; Dream Games 17/17; Aleph 171/5; Ajax Systems 176/11; Insider One 112/22; Binance 4/0 Türkiye; Picus 15/1. Tekrar sayım **35+16+7+17+5+11+22+0+1=114** özel aday. 114'ünün tamamı deadline unknown / Doğrulanamadı; 100 doğrulanmış ilan sayısına eklenemez.
- 6 Ashby: Bold Games, fal, The Global Talent Co., Sensor Tower, XDS, Cambly; robots.txt HTTP 401, stale/ölçülemedi, API çağrısı yapılmadı. 0 ilan sayılmadı.
- Kamu ayrı: e-Şube **1 kaynak / 43 benzersiz ilan**. Girdi snapshot `2026-11-24T11:57:55+00:00`; audit anında 6 saat 49 dk civarında, 12 saat input sınırı içinde. Bu, yeni HTTP e-Şube çekimi değildir. `publishedAt=null`; son başvuru kaynakta `deadline`.
- Summary: 16 kaynak; fresh 10/stale 6; fresh listing 157 (=114 deadline unknown + 43 kamu open deadline); openDeadline 43; unknown 114; verified-source count 1; threshold NOT_MET. Hedef 10 kaynak/100 ilan karşılanmadı.
- Worker production publish hâlâ SKIPPED: `JARVIS_WORKER_URL` / `JARVIS_WORKER_TOKEN` çifti public QA workflow'unda yok. Production Worker ve Cloudflare D1 davranışı kanıtlanmadı; deploy yapılmadı.
- PR diff tam yeniden incelendi: 13 dosya, +2.484/-6; eklenen satırlarda token/secret/private-key, CV, KPSS, e-Devlet eşleşmesi 0. 11 haneli regex 49 eşleşme verdi; 47 benzersiz değer, bunların tamamı e-Şube JSON'undaki `ilan_no` ilan numaralarıdır, TCKN değildir.
- Sonuç: backend birim test ve local D1 smoke kapıları PASS; genel C1-1 RED. Kaynak eşiği ve production Worker/D1 QA tamamlanmadan merge, C1-2 veya UI yok.


### C1-1 / C1-2 güncel son ölçüm — 24.11.2026 18:57 UTC
- Son public QA [#45464977542](https://github.com/aries919-bot/Jarvis/actions/runs/45464977542), job `138697350676`: 33/33 test SUCCESS; Wrangler local Worker + D1 smoke 8/8 PASS; live source audit SUCCESS; JSON/TXT artifact [#14954582973](https://github.com/aries919-bot/Jarvis/actions/runs/45464977542/artifacts/14954582973).
- Current private PR #5 HEAD `0ec0fdd5050ac75f17153341d27685dbfb4061d4`, 13 files +2,529/-6; open/draft/unmerged. Main SHA `fb291c0ce06b05c61f988e71c3d6587ffc945e93`; untouched.
- Latest live audit `collectedAt=2026-11-24T18:57:08.386Z`: Trendyol 44 raw/35 Türkiye candidate; Midas 17/16; Commencis 7/5; Dream Games 17/17; Aleph 171/5; Ajax Systems 176/11; Insider One 112/22; Binance 4/0; Picus 15/1. Private candidate sum re-count: `35+16+5+17+5+11+22+0+1=112`.
- Commencis internship and Cloud Camp entries were excluded as non-vacancy programmes; `International Accountant` regression remains included. New test validates this rule.
- 6 Ashby: Bold Games, fal, The Global Talent Co., Sensor Tower, XDS, Cambly. robots.txt HTTP 401, stale/unmeasured; no API requests were sent.
- Public separately: 1 e-Şube / 43 unique IDs, snapshot `2026-11-24T11:57:55+00:00` (approx 6h59 age, within 12h); not a new HTTP refresh; `publishedAt=null`, deadline from source.
- Totals from artifact: 16 sources, fresh 10/stale 6; 155 unique records = 112 private deadline unknown +43 public deadline open. Open deadline entries 43 from only 1 source. Threshold NOT_MET.
- QA workflow has read-only private checkout and does not expose secret value. Production publish skipped; no Cloudflare Worker/D1 deploy.
- C1-2 report created/updated at `gorevler/C1-2.md`; decision RED, actual numbers retained. UI/C1-2 app integration remains blocked by 10 sources/100 verified listings.
