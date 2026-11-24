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
| C1 Sunucu ilan toplayıcı + tekilleştirme JSON | DEVAM | sahip: chatgpt; jarvis-cloud PR #5 açık/taslak; statik QA'da GET rota/ilk görülme/son iyi veri koruma eksikleri; canlı ilan sayısı bilinmiyor |
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
| HAKEM-DIFF | PR AÇIK — CLAUDE ONAYI BEKLENİYOR | [PR #37](https://github.com/aries919-bot/Jarvis/pull/37), fix/hakem-diff-lines → cline/K1; yalnız scripts/hakem_gate.py + scripts/test_hakem_gate.py. Güncel test [#45415984321](https://github.com/aries919-bot/Jarvis/actions/runs/45415984321) 55/55 SUCCESS; [Hakem #45415984470](https://github.com/aries919-bot/Jarvis/actions/runs/45415984470) Cloudflare Secrets eksikliğiyle RED. Merge yok; Claude incelemesi bekleniyor. |
| H1 | PR AÇIK — ADAYLAR EKLENDİ | [PR #35](https://github.com/aries919-bot/Jarvis/pull/35), `is/H1` → `durum`; HABER_SEMA.md'de 6 kategori x 3 = 18 resmî aday kaydı var. K1-B ölçümüne göre yalnız 8 adres erişilebilir; kalan adaylar ölçüm/robots/TLS bekler. H1 henüz kabul değil. |
| K1-B | ÖLÇÜM VAR — PR AÇIK / KISMİ RED | [PR #36](https://github.com/aries919-bot/Jarvis/pull/36); [Actions #45405558813](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813), 20/20 URL: 8 ÇALIŞIYOR, 12 ENGELLİ, 0 ÖLÜ. Gerçek kamu ilan kaynağı 1 (e-Şube)/43 doğrulanmış ilan. TLS: 5 URL issuer doğrulama hatası. Resmî Gazete timeout adresleri tekrarlı Actions çağrılarında 3 kez istendi; tek retry sınırı aşıldı, daha fazla istek yok. |
| K1-C | PR AÇIK — DENETİM HAZIR | [PR #38](https://github.com/aries919-bot/Jarvis/pull/38), `is/K1-C` → `durum`; yalnız K1-B'de ölçülmüş 20 URL, 8 ÇALIŞIYOR / 12 ENGELLİ / 0 ÖLÜ; kamu 1 kaynak / 43 ilan. İki dosya 20'şer satır olarak geri okunup sayıldı. `durum` tabanında Actions workflow'u yok; CI kanıtı yok, merge edilmedi. |
| H2 | YAPILMADI | Ölçülen duyuru sayfalarından H1 şemasına uygun JSON okuyucu. |
| C1-1 | YAPILMADI | jarvis-cloud PR #5 düzeltmesi; kimlik doğrulamalı GET /kariyer-feed, kalıcı firstSeenAt, kaynak bazlı son iyi veri, kamu JSON'u ve hata testleri. |
| C1-2 | YAPILMADI | Canlı kaynak/ilan sayımı, kamu ayrı; eşik sağlanmıyorsa RED ve gerçek sayı. |
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
