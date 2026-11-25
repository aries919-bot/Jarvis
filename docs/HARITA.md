# Aries — Durum Haritası

> Tek gerçek durum dosyası. Önce bu dosya ve docs/TALIMAT.md okunur.
> Son kontrol: 25.11.2026. Repo: `aries919-bot/Jarvis`.
> Bu dal yalnızca proje notları içindir; Android kodu burada geliştirilmez.
> **C1 özel ve öncelikli kurallar:** [docs/NOT_C1.md](NOT_C1.md). TALIMAT.md ile çelişirse NOT_C1.md geçerlidir.

## H/K1/C1 v3 aktif sıra — 24.11.2026

> Bu blok, eski görev/sahiplik kayıtlarından önce geçerlidir. Cline kullanılmaz. Yalnız İş & Kariyer/Haber doğrulama işi yürütülür. Ayrıntı: [NOT_C1.md](NOT_C1.md).

| Kimlik | Durum | Kanıt / Sonraki adım |
|---|---|---|
| K1-E1 | KABUL | Actions #45388925995 SUCCESS; 3 sayfa/43 benzersiz kamu ilanı/8 kurum; Cem 3 kaydı e-Şube'de doğruladı. Sayım: 1 kamu kaynağı/43 ilan. |
| K1-F | İPTAL | v3 gereği iptal; K1-F2 ile değiştirildi. |
| K1-F2 | TEST KABUL — MERGE BEKLİYOR | PR #33; Actions #45403456464 SUCCESS; 46/46 geçti, 0 hata. PR diff'i yalnız 2 dosya silme. Test workflow PR #34 ve hedef dalı `is/K1-test-workflow`. |
| H1 | ŞEMA KABUL — PR #63 MERGED / GENEL H1 DEVAM | Exact-head #45628989453: 12/12 test; ChatGPT öz-QA ONAY; 4 dosya +43/-20; `durum` merge SHA `4145ce5`. Bu yalnız şema sözleşmesi; kaynak envanteri/ilan kabulü değil. |
| K1-B | YAPILMADI | ilan.gov.tr ve Resmî Gazete HTTP/TLS/robots ölçümü. |
| K1-C | YAPILMADI | Ölçülen kaynak denetimi ve aday listesi. |
| H2 | PR #61 AÇIK/TASLAK — GENEL RED | BOTAŞ kategori + deadline parser kusuru PR #65 ile düzeltildi, H2 head'e alındı. H2 reader 60/60; scripts 161/161; Hakem #45646136859 ONAY. Yeni post-merge live #45646698179 sürüyor; 10/100, cross-feed ve production GET kapıları ayrı. |
| C1-1 | DEVAM — KOD QA PASS / GENEL RED | PR #5 diff'i 60 KB sınırını aşıyor; Worker production GET BLOCKED; son exact-head QA #45634071990: 46/46 test, 11/11 local D1, 17/17 senaryo; kamu 6 kaynak/47 açık ilan, 10/100 RED. Ayrıntı: [gorevler/C1-1.md](../gorevler/C1-1.md). |
| C1-2 | RAPOR HAZIR — RED | Son exact-head rapor #45634071990: kamu 6 kaynak/47 doğrulanmış açık kart; özel deadline-unknown kayıtlar sayılmaz; 10/100 eşik sağlanmadı. [gorevler/C1-2.md](../gorevler/C1-2.md). |
| K1-M | YAPILMADI | Temiz workflow/betik PR'si; film commitleri hariç. Görev tanımı: [gorevler/K1-M.md](../gorevler/K1-M.md). |
| U1 | YAPILMADI | Uygulama H1 JSON entegrasyonu; yalnız eşik sağlanınca. |

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
| C1 Sunucu ilan toplayıcı + tekilleştirme JSON | DEVAM — CODE QA PASS; HAKEM/10-100/PROD RED | jarvis-cloud PR #5 (main hedefli, açık/taslak, HEAD `d78df610`, 13 dosya, Hakem 60 KB sınırı aşıldı); timestamp düzeltmesi PR #6 (C1 dalına, açık/taslak); kategori düzeltmesi PR #8 (PR #6 tabanında, açık/taslak; 2 dosya +9/-0); eski unstacked PR #7 kapatıldı. Paralel QA + Hakem [#45504819883](https://github.com/aries919-bot/Jarvis/actions/runs/45504819883): 38/38 test, 11/11 local D1, 12/12 senaryo, public date contract, category ve security diff PASS. Live: 16 toplam /10 fresh /6 stale; 9 private source fresh=114 deadline-unknown, 6 Ashby robots HTTP 401 stale, kamu e-Şube 1/42 open. Verified source/listing 1/42; production GET BLOCKED; 10/100 RED. Report `qa-reports/c1/latest.json`. |
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
| H1 | PR AÇIK — ŞEMA + KAYNAK LİSTESİ GÜNCELLENDİ | [PR #35](https://github.com/aries919-bot/Jarvis/pull/35), `is/H1` → `durum`; H1 kart şeması, uygulama açılış tarihi `applicationOpensAt` ayrımı eklendi. K1-B geniş ölçüm #45473805274: 28 benzersiz URL, 11 ÇALIŞIYOR /17 ENGELLİ /0 ÖLÜ; H1 hâlâ kabul değil. |
| K1-B | ÖLÇÜM VAR — PR AÇIK / KISMİ RED | PR #36; geniş ölçüm [#45473805274](https://github.com/aries919-bot/Jarvis/actions/runs/45473805274): 28 URL /11 ÇALIŞIYOR /17 ENGELLİ /0 ÖLÜ; testler 16/16. Adli Tıp, Anadolu, Düzce, Tarsus resmî sayfaları ve PDF'leri ölçüldü. Robots unknown/401/TLS kapalı hedeflere istek yok. Erişim sinyali ilan sayısı kanıtı değildir. |
| K1-C | PR AÇIK — GÜNCEL RAPOR + TEST | PR #38: https://github.com/aries919-bot/Jarvis/pull/38, is/K1-C → durum; 4 dosya: rapor, aday listesi, rapor testi, workflow. K1-B #45429536647'ye göre 20 URL: 5 ÇALIŞIYOR / 15 ENGELLİ / 0 ÖLÜ; rapor 20/20 benzersiz kaynağı gösterir. CI #45430917966: 6/6 SUCCESS. Kamu 1 kaynak / 43 doğrulanmış ilan; PR birleştirilmedi. |
| H2 | ÇEKİRDEK + SON WORKFLOW `cline/K1`'DE | PR #51 ve #52 `cline/K1`'e alındı (çekirdek SHA `27042ab4`; son entegrasyon merge `d99e58c6`); PR #53 H2_PLAN kanıt notu merge SHA `f22ba0b9`. Tam H2+seed testleri 41/41; genel Python 142/142; Hakem ONAY [#45488851398](https://github.com/aries919-bot/Jarvis/actions/runs/45488851398). Seed'li canlı [#45488834084](https://github.com/aries919-bot/Jarvis/actions/runs/45488834084): 6 kaynak, 4 fresh/2 stale, 5 kart; Tarsus 1 stale last-good kart ve eski firstSeenAt korunur; BOTAŞ stale/0; güncel açık/fresh Anadolu 1 + Düzce 1; ATK başvurusu 30.11.2026'da açılacak; YHGM geçmiş kura. 6 saat cron yalnız `main` varsayılan dalına geldiğinde çalışır; Android H2 JSON entegrasyonu yok. |
| C1-1 | DEVAM — KOD QA PASS; GENEL RED | jarvis-cloud PR #5 `is/c1-career-feed` → `main`, açık/taslak; HEAD `d78df6104265cca25de67617ccb5031ba3d3bfdb`, 13 dosya / +2.698/-7; patch yeniden sayımı **114.958 UTF-8 bayt** (60.000 sınırını aşıyor). Stacked #6/#8/#9 açık/taslak. Public exact-head QA [#45634071990](https://github.com/aries919-bot/Jarvis/actions/runs/45634071990), private SHA `8a43b99981db601de3442e8394b71d0651d0e34e`: 46/46 test, 11/11 local D1, 17/17 senaryo; 5 dosyalı PR #9 diff 22.465 bayt / 60.000, security PASS. Güncel e-Şube snapshot `2026-11-25T13:49:08Z`: 42/42 unique/open, publishedAt=null. Kamu toplamı e-Şube 42 + H2 5 = **6 kaynak/47 açık kart**; özel 112 aday deadline unknown, 6 Ashby robots 401 stale; BOTAŞ timeout, ÇOMÜ robots UNKNOWN, Bilecik robots timeout. Toplam katalog 26 kaynak /17 fresh /9 stale /114 unknown deadline. 10/100 RED. Production authenticated GET BLOCKED (`JARVIS_WORKER_URL/JARVIS_WORKER_TOKEN` yok); merge/deploy yok. Ayrıntı: [gorevler/C1-1.md](../gorevler/C1-1.md). |
| C1-2 | RAPOR HAZIR — RED | [gorevler/C1-2.md](../gorevler/C1-2.md). Son exact-head Actions Hakem #45634071990: **6 fresh kamu kaynağı /47 doğrulanmış açık kart** (e-Şube 42 + Anadolu 1 + Düzce 1 + Tarsus 1 + Marmara 1 + Zafer 1). YHGM geçmiş kura ve ATK'nın 30.11.2026 başvuru açılışı aktif sayılmaz. Özel 112 deadline-unknown aday doğrulanmış sayılmaz. 10/100 RED; Android UI yok. |
| K1-M | YAPILMADI | Gerekli workflow/betiklerin temiz PR ile main'e taşınması; film commitleri hariç, cron yalnız main'de. Sıradaki sabit sırada C1-1/C1-2 sonrasıdır; [görev](../gorevler/K1-M.md). |
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
**SONRAKİ ADIM:** C1-1 hâlâ RED. Bir sonraki geliştirme: robots izinli yeni resmî kamu kaynaklarını ekleyip her biri için gerçek açık deadline'lı ilanı kanıtlamak; 10 kaynak/100 doğrulanmış ilan eşiğine yaklaşmak. PR #5 için 60 KB Hakem sınırı ve private Actions failure ayrıca çözülmeli; production Worker/D1 GET-POST/kalıcı firstSeenAt kanıtlanmalı. PR #5 `main` hedefli olduğundan birleştirme için ayrıca kullanıcı onayı gerekir. Eşik ve tüm kabul kapıları tamamlanmadan C1-2/UI yok.

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


### H1 / H2 canlı doğrulama durumu — 24.11.2026 20:04 UTC
- K1-B geniş ölçüm [#45473805274](https://github.com/aries919-bot/Jarvis/actions/runs/45473805274): 28 URL /11 ÇALIŞIYOR /17 ENGELLİ /0 ÖLÜ; 16/16 test. Robots unknown/401 ve TLS hatalarında hedef URL çağrılmadı.
- H2 test [#45476832673](https://github.com/aries919-bot/Jarvis/actions/runs/45476832673): 32/32 PASS. H2 live [#45476818354](https://github.com/aries919-bot/Jarvis/actions/runs/45476818354): 6 source /5 fresh /1 stale /5 card; artifact #14961200293 indirildi ve JSON'lar açılarak yeniden sayıldı.
- H2 live kartları: YHGM 1 geçmiş kura (deadline 2025); ATK 1 (181 pozisyon, published 20.11, opens 30.11 09:00 +03, deadline 6.12, henüz başvuru başlamadı); Anadolu 1 (deadline 30.11, Doğrulandı); Düzce 1 duyuru/5 kadro (deadline 7.12, Doğrulandı); Tarsus 1 duyuru/3 kadro (deadline 7.12, Doğrulandı); BOTAŞ stale timeout.
- Android `MainActivity.kt` lines 694–699 displays a “Haberleri yenile” button; lines 1386–1388 “haberleri getir/oku/göster” command also fetches. `Ai.kt` `Net.newsTitles()` reads Google News RSS and returns up to 8 titles; UI displays first 5, without article URL/date. This on-demand general RSS is separate from H2 official notice cards.
- H2 `schedule: 17 */6 * * *` UTC added; since PR #40 targets `durum` rather than default branch, schedule isn't live. No Worker publication or Android H2 JSON refresh yet.
- Güncel birleşik kamu kanıtı (H2 post-merge live #45489853859, `collectedAt=2026-11-24T21:19:09Z`; C1 exact-head QA #45494237356, `collectedAt=2026-11-24T21:45:23.083Z`): **3 fresh kamu kaynağı /44 açık doğrulanmış kart** (e-Şube 42 + Anadolu 1 + Düzce 1). Tarsus'un 1 duyuru kartı stale last-good olarak korunur ama güncel açık sayısına eklenmez; ATK 30.11.2026'da açılır, YHGM geçmiş kura, BOTAŞ stale/boş. 114 private ATS kaydı deadline unknown; sayılmaz. 10/100 eşiği RED.

### C1 paralel QA ve Hakem sonucu — 25.11.2026
- Workflow [#45504819883](https://github.com/aries919-bot/Jarvis/actions/runs/45504819883), private exact SHA `68e858424c3589b7d6cffea7ba26c7941027b017`, base `74816f3954c3fa8164ef0266b848b0b9e9a897d4`.
- Sonuç JSON: [qa-reports/c1/latest.json](https://github.com/aries919-bot/Jarvis/blob/durum/qa-reports/c1/latest.json). Hakem **RED**: code QA PASS; production authenticated GET BLOCKED; doğrulanmış eşik 1 kaynak/42 ilan, şart 10/100.
- QA sayımları: `npm run test:c1` 38/38; yerel Worker+D1 smoke 11/11; regresyon senaryosu kapsamı 12/12; public data contract PASS; source categoryErrors boş; security diff PASS (2 dosya, 1.638 bayt, yasaklı desen 0).
- Canlı kaynak: 16 toplam /10 fresh /6 stale. Private fresh source başına aday: Trendyol 35, Midas 16, Commencis 5, Dream Games 17, Aleph 5, Ajax Systems 13, Insider One 22, Binance 0 Türkiye, Picus 1. Tekrar sayımı: `35+16+5+17+5+13+22+0+1=114`; 114'ünün deadline durumu unknown, doğrulanmış açık özel ilan 0.
- Altı Ashby (Bold Games, fal, The Global Talent Co., Sensor Tower, XDS, Cambly) robots HTTP 401 / `robots-erisilemedi`; API istekleri gönderilmedi, kaynaklar stale olarak raporlandı.
- Kamu ayrı: e-Şube 1 kaynak /43 benzersiz raw /42 açık /1 süresi geçmiş; snapshot `2026-11-24T11:57:55Z`, audit sırasında 10,9 saat yaşında. `publishedAt=null`, `deadline` kaynaktan.
- Source category bug düzeltildi; private `Büyük özel`, e-Şube `Kamu`; hata/robots/son deneme alanları QA raporunda görünüyor. Üretim Worker URL/token ayarı olmadığından production GET BLOCKED; deploy yapılmadı.
- PR #6 ve stacked PR #8 açık/taslak; PR #7 eski taban nedeniyle kapatıldı. Hiçbiri merge edilmedi.


## C1 toplu hakem güncellemesi — 25.11.2026
- En son iç denetim notu: [gorevler/C1-toplu-hakem-25-11.md](../gorevler/C1-toplu-hakem-25-11.md). GitHub PR #8'e yorum ID `7147163607` ile toplu inceleme notu eklendi; bu ChatGPT iç denetimidir, bağımsız Claude/insan hakem onayı değildir.
- PR #6 + PR #8 birleşik compare: 7 commit / 7 dosya. PR #6 timestamp alanlarını ayırıyor; PR #8 üç kaynak dönüş yolunda kategori alanını koruyor. PR #6/#8 açık/taslak.
- C1 exact-head QA #45504819883: 38/38 test, 11/11 local D1, 12/12 hata senaryosu PASS. Hakem RED: son ölçüm 1 kaynak/42 ilan; 10/100 eşiği karşılanmadı; üretim authenticated GET BLOCKED.
- Son e-Şube snapshot `2026-11-24T11:57:55Z`; 25.11.2026 itibarıyla 12 saat freshness penceresini aşmıştır. Taze çekim yokken kamu güncel sayımı 0 kabul edilir; önceki 42 açık ilan geçmiş ölçümdür, güncel kanıt değildir.
- H2 okuyucu run #45489853859 attempt 2 yeniden başlatıldı; reader job `138848189009` son kontrolde resmî sayfaları okuma adımında çalışıyordu. Yeni sayımlar tamamlanmadı.
- **Merge yok.** Sonraki iş: H2 ve e-Şube taze çekimini doğrula; ardından kamu kaynağı ve ilanlarını yeniden say. Unknown deadline kayıtları sayılmaz.

## Son kanıtlı durum — 25.11.2026 03:03 TRT

> Bu ek, üstteki 24.11 ve daha eski tarihli sayım notlarını günceller. Eski Actions raporları kendi çalışma anlarının tarihsel kaydı olarak korunur.

### H1 Haber şeması
- PR #35: https://github.com/aries919-bot/Jarvis/pull/35 — is/H1 → durum, HEAD 9785696ac4788805d23ad327e3690a75a521eb2c, açık, birleştirilmedi.
- Güncel PR Actions #45514829808 SUCCESS; komut python -m unittest scripts.test_h1_schema -v; çıktı Ran 12 tests in 0.019s / OK.
- Diff: 5 dosya; h1-schema workflow, HABER_SEMA, HARITA, h1_schema.py ve test_h1_schema.py.
- Son düzeltme: sayım fonksiyonu publishedAt yoksa, stale ise, deadline geçmişse veya applicationOpensAt henüz gelmediyse iş ilanı saymıyor.
- Formal GitHub review kaydı yok. PR yorum ID 7148071138 kanıt bildirimidir; bağımsız ONAY değildir.

### H2 resmî kaynak okuyucusu
- PR #57: https://github.com/aries919-bot/Jarvis/pull/57 — review/h2-public-sources-20261125 → cline/K1, HEAD ae51188ff2bb7c55ef813be7205825b0c5b05db0, açık/taslak, birleştirilmedi.
- H2 canlı veri Actions #45512710235, artifact 14976107324; kaynak collectedAt 2026-11-24T23:46:14Z. Katalog 10 kaynak tanımı, 7 fresh / 3 stale.
- Doğrulanmış açık iş kartları: Anadolu 1, Düzce 1, Marmara 1, Tarsus 1, Zafer 1. ATK başvurusu 30.11.2026 09:00 +03 öncesi 0 açık; YHGM geçmiş kura; Bilecik, BOTAŞ ve ÇOMÜ stale.
- H2 açık iş toplamı 5 kart / 5 kurum kaynağı. Kontenjan/pozisyon sayısı kart adedini artırmaz.
- H2 PR CI: reader #45513755673, Hakem #45513755686, K1-B #45513755635 SUCCESS. K1-B ölçümü 28 URL: 11 ÇALIŞIYOR / 17 ENGELLİ / 0 ÖLÜ; URL sayısı ilan veya kurum sayısı değildir.
- PR #57'de formal GitHub review kaydı yok. Actions botunun ONAY yorumları bağımsız insan incelemesi sayılmaz.

### Kamu ve C1 birleşik manuel sayımı
- e-Şube JSON: career/esube_kamu_ilanlari.json; durum commit c5d1a1d9c105703c3766659e7f9a3be1fbb01b18; çekim 2026-11-24T23:51:48Z; 3 sayfa. Yeniden sayım 42 ham / 42 benzersiz / 42 açık / 0 süresi geçmiş / 42 deadline mevcut. Yayın tarihi yok: publishedAt=null.
- e-Şube 12 saatlik tazelik sınırı: 2026-11-25T11:51:48Z.
- H2'nin beş açık kartı ayrı feed'dedir; C1 Worker'a bağlandıkları kanıtlanmamıştır.
- Ayrı feed'lerin manuel birleşik kamu görünümü: 6 kaynak / 47 doğrulanmış açık iş kartı (e-Şube 42 + H2 5). Bu tek bir Worker feed'i değildir.
- C1 exact-head live audit #45504819883: 9 fresh özel ATS kaynağında 114 aday; tamamı deadline unknown. Kaynak sayıları: Trendyol 35, Midas 16, Commencis 5, Dream Games 17, Aleph 5, Ajax Systems 13, Insider One 22, Binance 0 Türkiye, Picus 1. Altı Ashby robots HTTP 401 nedeniyle stale; API çağrısı yok.
- Sabit kabul eşiği 10 kaynak / 100 doğrulanmış, deadline'ı açık ve süresi geçmemiş iş. Fiilî birleşik görünüm 6 kaynak / 47 iş: RED.
- C1 otomatik qa-reports/c1/latest.json, Actions #45504819883'ün eski 43/42 e-Şube görüntüsüdür ve değiştirilmedi. Güncel çapraz-feed sayımı ayrı manuel ek rapor qa-reports/c1/manual-recount-2026-11-25.json dosyasındadır.
- Production Worker authenticated GET hâlâ BLOCKED; JARVIS_WORKER_URL / JARVIS_WORKER_TOKEN kanıtı yok. Yerel Worker/D1 smoke production değildir. H2 C1 Worker'a ve Android'e entegre değildir.
- C1 PR #5/#6/#8 ve Jarvis PR #35/#57 birleştirilmedi.

KARAR: C1 RED; H1 şema testleri PASS, bağımsız review ve kabul bekliyor.
SONRAKİ ADIM: H2/e-Şube ayrı feed'lerini C1 Worker'a bağlayan dar kapsamlı PR hazırla; exact-head QA, kaynak sayımı ve production GET geçmeden ONAY verme.

## C1 H2 → tek Actions feed doğrulaması — 25.11.2026 03:25 TRT

- Entegrasyon PR'ı: [jarvis-cloud PR #9](https://github.com/aries919-bot/jarvis-cloud/pull/9), `fix/c1-h2-official-feed` → `fix/c1-source-category-after-timestamp` (PR #8 tabanı), açık/taslak, HEAD `8a43b99981db601de3442e8394b71d0651d0e34e`. Merge yok. Formal review 0.
- Görev/kanıt: [gorevler/C1-H2-entegrasyon-25-11.md](../gorevler/C1-H2-entegrasyon-25-11.md).
- Public exact-SHA Hakem [Actions #45517626605](https://github.com/aries919-bot/Jarvis/actions/runs/45517626605), checked-out private SHA `8a43b99981db601de3442e8394b71d0651d0e34e`, base `68e858424c3589b7d6cffea7ba26c7941027b017`.
- `npm run test:c1`: 46/46 PASS; local Wrangler+D1 smoke: 11/11 PASS; H2 parser + last-good test step SUCCESS; required regression matrix 17/17; public date contract PASS; exact diff/security PASS.
- Diff yeniden sayımı: 5 dosya / +442 / -10 / 22.465 bayt. İzinli dosya listesi tam eşleşti; 60 KB sınırı altında; forbidden-pattern bulgusu 0; UA doğru; diff-check temiz.
- Tek Actions collector çıktısı `collectedAt=2026-11-25T00:25:02.241Z`: 26 kaynak, 17 fresh / 9 stale, 163 fresh kayıt, 47 doğrulanmış açık iş, 116 deadline-unknown, 0 expired.
- Kamu ayrı: 6 kaynak / 47 açık doğrulanmış kart. e-Şube 42 + H2 5 (Anadolu 1, Düzce 1, Tarsus 1, Marmara 1, Zafer 1). e-Şube snapshot `2026-11-24T23:51:48Z`, 42 ham/42 benzersiz/42 açık, publishedAt=null.
- H2: 10 kaynak, 7 fresh / 3 stale, 7 ham kart. YHGM 1 kura (iş sayılmaz); ATK 1 future-open (30.11.2026 09:00 +03 öncesi sayılmaz); Anadolu, Düzce, Tarsus, Marmara, Zafer toplam 5 doğrulanmış açık kart. BOTAŞ timeout, ÇOMÜ robots UNKNOWN (hedef istenmedi), Bilecik Resmî Gazete robots timeout.
- Özel ATS fresh: Trendyol 35, Midas 16, Commencis 5, Dream Games 17, Aleph 5, Ajax Systems 13, Insider One 22, Binance 0, Picus 1 = 114. Hepsinin deadlineStatus unknown; doğrulanmış ilana girmez. Bold Games, fal, Global Talent Co., Sensor Tower, XDS, Cambly robots HTTP 401 stale; API hedefleri çağrılmadı.
- Sabit eşik 10 kaynak /100 doğrulanmış açık ilan; fiilî 6/47: RED. Production authenticated GET BLOCKED (`JARVIS_WORKER_URL/JARVIS_WORKER_TOKEN` yok), publish SKIPPED; production deploy/POST yok. Yerel D1 production kanıtı değildir.
- Hakem raporu `qa-reports/c1/latest.json` exact run #45517626605'tir. HAKEM RED, çünkü 10/100 ve production GET şartları sağlanmadı; kod QA PASS bu RED'i kaldırmaz.
- PR #9 taslak/açık; PR #5/#6/#8 ve H1/H2 PR #35/#57 birleştirilmedi. Android entegrasyonu yok. `qa-reports/c1/latest.json` run #45517626605 çıktısıdır.


## C1 günlük sayım düzeltmesi — 25.11.2026
- Yeni denetim notu: [gorevler/C1-GUNLUK-SAYIM-25-11.md](../gorevler/C1-GUNLUK-SAYIM-25-11.md), durum commit `56e68c2a4e7ff0348d56ad7b29ab8c4f583a6bc3`.
- 25.11.2026 03:25 TRT Actions #45517626605 sayımı tarihsel olarak 26 kaynak /17 fresh /9 stale, 6 kamu kaynağı /47 doğrulanmış açık kart idi. Current-day sayı değildir.
- e-Şube snapshot `2026-11-24T23:51:48Z`; 12 saatlik fresh-until `2026-11-25T11:51:48Z` (14:51:48 TRT). Bu eşikten sonra yeni başarılı snapshot bulunmadığından 42 e-Şube kartı current toplamdan çıkarılmalı; yeni çekim olmadan kamu toplamı güncel diye raporlanmaz.
- Düzce'nin 23.11 öğretim üyesi ilanı ayrı yeni duyuru (4 kadro, deadline 07.12.2026); öğretim elemanı ilanı (5 kadro) H2'de zaten tek kart, tekrar sayılmaz. Bartın (1 sözleşmeli ilan, 05.12.2026 13:00) robots ölçümü erişilemediği için reader'a eklenmedi. TGA 13 pozisyon tek ana duyuru; kartı çoğaltma ve kategori teyidi bekliyor.
- PR #9 exact-head Actions #45517626605 kod QA 46/46, local D1 11/11 ve regresyon 17/17 PASS; karar RED: production GET BLOCKED ve 10/100 eşiği karşılanmadı. PR açık/taslak, formal review 0; merge/deploy yok.
- Workflow e-Şube'yi canlı çekmiyor; durum dalındaki statik JSON'u okuyor. Schedule yalnız default branch'te işler; PR #9 merge edilmediğinden otomatik günlük canlı yenileme kanıtı yok.
- **Güncel C1 kararı: RED / bugünkü canlı kaynak sayımı henüz kanıtlanmadı.** 6/47 yalnız geçmiş run sonucu; 114 özel ATS adayı deadline unknown olduğu için doğrulanmış açık sayılmaz.
- SONRAKİ ADIM: C1 Actions'a e-Şube'nin robots-uyumlu canlı okuyucusunu entegre et; Düzce öğretim üyesi kartını mevcut kurum kaynağı altında ayır; Bartın robots'u ölçmeden ekleme; exact-head CI ve her kaynağın fresh sayımı geçmeden eşiği yeniden değerlendir.


### C1 günlük otomasyon notu — 25.11.2026
- Mevcut e-Şube reader zaten var: `cline/K1/scripts/esube_kamu_okuyucu.py`; robots-first, girişsiz, AriesKaynakTara/1.0, 2 sn bekleme, en çok 40 sayfa. Başarılı çıktı `durum/career/esube_kamu_ilanlari.json` dosyasına mevcut workflow tarafından yazılır.
- e-Şube workflow `cline/K1/.github/workflows/kamu-ilan-cek.yml`: cron 05:17 ve 14:17 UTC; H2 workflow: 6 saatte bir; C1 PR #9 workflow: 06:00 ve 18:00 UTC. Schedule yalnız varsayılan dalda çalışır; workflow dosyalarının `main`'e alındığına dair kanıt yok. PR'lar açık olduğu için cron satırları canlı çalışıyor varsayılmaz.
- 25.11 kontrolünde durum JSON'u hâlâ `2026-11-24T23:51:48Z` e-Şube çekim zamanında. Sabah schedule'ı çalışıp yeni JSON üretmiş kanıtı yok. 14:17 UTC slotu bu kontrol anında henüz gelmemişti.
- Doğru çözüm yeni reader yazmak değil; var olan e-Şube/H2 workflow'larının default branch'te etkinleşmesini ve C1 workflow'unun yeni `durum` JSON snapshot'ını tüketmesini CI kanıtıyla doğrulamaktır.
- Ayrıntı ve ölçüm engelleri: [C1-GUNLUK-SAYIM-25-11.md](../gorevler/C1-GUNLUK-SAYIM-25-11.md). Güncel C1 RED; 6/47 geçmiş run sayısıdır, bugünün şu anki doğrulanmış toplamı değildir.


## 12. Taze e-Şube + exact-head C1 yeniden denetimi — 25.11.2026 17:29 TRT

- [Actions #45634071990](https://github.com/aries919-bot/Jarvis/actions/runs/45634071990), workflow commit `0faf381297e92f59b7fedd7510603ad63e0135ac`, private exact SHA `8a43b99981db601de3442e8394b71d0651d0e34e`. QA job'ları: 46/46 test PASS; local D1 11/11 PASS; regresyon 17/17 PASS; public-data contract PASS; H2 reader PASS; security diff PASS. Son Hakem job'ı RED; run sonucu bu nedenle failure.
- e-Şube JSON: `collectedAt=2026-11-25T13:49:08Z`; QA anında yaş 0,63 saat. Yeniden sayım 42 ham /42 benzersiz /0 tekrar /42 açık /0 expired /42 deadline; `publishedAt=null`. Kaynak/kurum: MSB 20, Emniyet 10, Durağan Belediyesi 2, OMÜ Sağlık 7, Devlet Tiyatroları 1, Elazığ Karayolları 1, Türkiye Denizcilik İşletmeleri 1.
- H2 güncel doğrulanmış açık kartlar: Anadolu 1, Düzce 1, Tarsus 1, Marmara 1, Zafer 1. e-Şube ile kamu **6 kaynak /47 doğrulanmış açık kart**. YHGM kura sayılmaz; ATK başvuru açılışı 30.11.2026 olduğundan henüz aktif sayılmaz. BOTAŞ timeout; ÇOMÜ robots UNKNOWN; Bilecik robots timeout.
- Özel ATS: Trendyol 32, Midas 16, Commencis 5, Dream Games 17, Aleph 5, Ajax Systems 13, Insider One 23, Binance 0 Türkiye açığı, Picus 1 = **112 deadline-unknown aday**. Bold Games, fal, Global Talent Co., Sensor Tower, XDS, Cambly robots HTTP 401; hedef API istenmedi. Bunlar 100 doğrulanmış ilana eklenmez.
- Genel live collector: 26 kaynak /17 fresh /9 stale; 47 doğrulanmış açık ilan; 114 deadline unknown; 0 süresi geçmiş. **10 kaynak /100 ilan eşiği: 6/47 — RED.**
- Production authenticated GET **BLOCKED**: `JARVIS_WORKER_URL/JARVIS_WORKER_TOKEN` yok; POST/deploy yapılmadı. Private PR #5 patch yeniden sayımı 114.958 UTF-8 bayt ve Hakem 60 KB sınırının üstünde. PR #5/#6/#8/#9 açık/taslak, merge yok.
- Bu bölüm, yukarıdaki 25.11 eski snapshot (2026-11-24T23:51:48Z) ve eski 3/44 sayımını günceller. Güncel veri 2026-11-25T13:49:08Z snapshot'ına aittir. Günlük schedule'ın `main` üzerinden çalıştığı henüz kanıtlanmadı.
- Ayrıntı: [gorevler/C1-1.md](../gorevler/C1-1.md). K1-M görev metni: [gorevler/K1-M.md](../gorevler/K1-M.md).

## 13. Hakem yokken öz-QA ve son birleştirme durumu — 25.11.2026

- Kullanıcı talimatı: bağımsız Hakem bulunmadığında ChatGPT exact-head QA yapar, ONAY/RED kararını PR yorumuna ve `gorevler/C1-SELF-QA-VE-BIRLESTIRME-PLANI-2026-11-25.md` dosyasına kaydeder. Bu bağımsız dış inceleme değildir. Kural `docs/NOT_C1.md` §12'ye eklendi.
- H1 PR #63: öz-QA ONAY; Actions #45628989453 12/12 test; 4 dosya +43/-20; 25.11.2026'da `durum` dalına squash merge, SHA `4145ce51398c796e61a95305cc6d39d2779dbbec`. H1'in tüm kaynak/ilan hedefi ayrıca kabul edilmiş değildir.
- H2 PR #61: genel RED. PR #65 ile BOTAŞ parser kategorisi/tarih bağlamı düzeltildi ve H2 dalına squash merge edildi. Post-merge reader #45646698179: 19 kaynak /17 fresh /2 stale; 11 kurum /13 doğrulanmış açık ilan kartı /125 pozisyon. 10/100, cross-feed tekilleştirme ve production GET kapıları hâlâ açık.
- e-Şube ayrı ölçümü: 42/42 benzersiz/açık kayıt, `collectedAt=2026-11-25T13:49:08Z`; `publishedAt=null`. H2 13 kart ile cross-feed birleşimi/tekilleştirme kanıtı bulunmuyor.
- C1 exact-head Hakem #45634071990: 46/46 test, local D1 11/11, senaryo 17/17 PASS; karar RED: kamu 6 kaynak/47 doğrulanmış kart, 10/100 sağlanmadı; production authenticated GET BLOCKED. H2 PR #61 genişletilmiş feed'i bu C1 raporuna dahil değildir.
- PR #58 tek seferlik e-Şube tetik dalı; geçici workflow değişikliği kalıcı olarak merge edilmez. PR #59/#60 robots/link-audit açık; bu turda merge kararı verilmedi.
- SONRAKİ ADIM: BOTAŞ düzeltmesi tamamlandı. Sıradaki iş, e-Şube/H2 ayrı feed'lerini C1 collector'a kaynak ve ilan kartı bazında tekilleştirerek bağlamak; H1 tarih/kategori sözleşmesi, kaynak başına last-good ve firstSeenAt korunumu testleri; ardından gerçek 10/100 ve production authenticated GET/D1 QA.
## 14. BOTAŞ parser düzeltmesi — PR #65 merged / H2 live yenileme sürüyor (25.11.2026)

- PR #65: https://github.com/aries919-bot/Jarvis/pull/65; base H2 `is/C1-h2-public-source-feed-20261125`; 2 dosya / +16 / -3. BOTAŞ iş ve sonuç parser çıktıları `Kamu bağlı şirket`; deadline bağlamı `İlanlar` Türkçe büyük İ biçimiyle eşleştirilir.
- İlk CI koşusu #45645955364/#45645955473 RED: yeni regression test `İlanlar` / `ilanlar` Türkçe küçük harf uyuşmazlığını ortaya çıkardı; H2 60 testin 1'i hata, scripts 161 testin 1'i hata. Parser çağrı bağlamı düzeltildi.
- Başarılı exact-head kontrolleri head `3020790f3b450f6e0f0cf04cfb4df598b749a6a7`: H2 Reader #45646136860 60/60 PASS; Python Script Tests #45646137068 161/161 PASS; K1-B #45646137091 24/24 PASS ve 28 URL için 12 ÇALIŞIYOR /16 ENGELLİ /0 ÖLÜ; Hakem #45646136859 ONAY.
- PR #65 squash merge: 25.11.2026; SHA `fe37be93202442394504f455b4a1caeb31f298cf`; PR #61 head'i güncellendi. Bu PR H2 geliştirme dalına düzeltmeyi aldı; `cline/K1`, `main` veya production'a merge değildir.
- Post-merge exact H2 push live reader [#45646698179](https://github.com/aries919-bot/Jarvis/actions/runs/45646698179): SUCCESS; artifact [15037029949](https://github.com/aries919-bot/Jarvis/actions/runs/45646698179/artifacts/15037029949), exact head `fe37be93202442394504f455b4a1caeb31f298cf`, `collectedAt=2026-11-25T15:34:38Z`. JSON yeniden sayımı: 19 kaynak, 17 fresh/2 stale; 13 doğrulanmış açık kart/11 kurum/125 pozisyon. Açık kart dağılımı: Anadolu 1; Atatürk 2; Balıkesir 1; DOKA 1; Düzce 1; Mardin Artuklu öğretim görevlisi 1; Marmara 2; ORAN 1; Tarsus 1; TGA 1; Zafer 1. Stale: BOTAŞ timeout ve Mardin Artuklu öğretim üyesi deadline kanıtı yok. ATK başvuru açılışı 30.11.2026; iki Munzur kartının aynı gün deadline saati belirsiz; YHGM geçmiş kura. Bunlar 13 açık karta dahil değil.
- PR #61 genel kararı hâlâ RED: 10 kaynak/100 doğrulanmış ilan kartı eşiği, e-Şube/H2 cross-feed tekilleştirme ve production Worker GET/D1 kapıları kapanmadı.

## 16. Aries ana sürüm + C1/H1 rakip ve güvenilirlik denetimi — 25.11.2026

- Rapor: [gorevler/ARIES-ANA-SURUM-C1-H1-RAKIP-DENETIMI-2026-11-25.md](../gorevler/ARIES-ANA-SURUM-C1-H1-RAKIP-DENETIMI-2026-11-25.md).
- Yalnız kod/branch denetimi ve rakip araştırması yapıldı; uygulama kodu, Worker, D1, APK, merge veya deploy değişmedi.
- Varsayılan main SHA `63e877d96cb910e6d98d56834ae55ae8edafab2a`; main'de CareerActivity bulunmuyor. C2/C3 özellik dalı HEAD `c6795908ccd753f47b216f34c59bc697df4f4d5d` için APK #45249266638 ve Full QA #45249266761 SUCCESS; bu main'e birleşme kanıtı değildir.
- C2/C3 CareerMonitor'da Indeed/LinkedIn kaynakları ve arama sorguları; `edu.tr` geniş güven alanı tespit edildi. Ai.kt Chrome benzeri UA kullanıyor. NOT_C1 ile çeliştiği için P0 küçük PR önerildi; bu denetimde düzeltilmedi.
- Ai.newsTitles RSS'te yalnız başlık alıyor; story/claim/kanıt/düzeltme zinciri H1'de tamamlanmalı.
- Rakip güncelliği: Simplify (2.6.0, yaklaşık 24.11.2026), Kariyer.net (Google Play güncelleme 10.11.2026), Ground News (2026 sonbahar sürüm/filtreleri), 02.11.2026 kontrol edilmiş ATS API kataloğu. Ayrıntılı URL ve sınırlar görev raporunda.
- C1 son H2 reader 19 kayıt /17 fresh /2 stale /13 açık kart; e-Şube ayrı 42 kayıt. 13+42 dedup kanıtı olmadan tekil toplam değildir; 10/100 RED.
- **SONRAKİ ADIM:** P0 kaynak/UA kurallarını ayrı PR ile düzelt; H1 story/claim/evidence sözleşmesi ve C1 cross-feed exact-head CI. Kullanıcı onayı olmadan merge/deploy yok.


## 17. C1 P0 kaynak politikası / UA düzeltmesi — 25.11.2026
- PR #66: https://github.com/aries919-bot/Jarvis/pull/66, `fix/c1-source-policy-ua-guard-20261125` → `fix/c3-ai-consent-and-navigation-guard`; açık/taslak, birleştirilmedi.
- HEAD `17f3fb796d482ea779c5ee2cf9f099828c7b3d72`; compare 4 dosya / +24 / -13; 4 commit; taban 5f6eebb.
- Indeed/LinkedIn kaynakları, güvenilir/ikincil alan listeleri ve arama sorguları kaldırıldı. Genel `edu.tr` trusted listesinden çıkarıldı.
- `Ai.kt` kaynak isteklerinde tarayıcı taklidi kaldırıldı; AriesKaynakTara/1.0 UA 4 noktada.
- Test dosyası: yasaklı iş panoları için 1 test; genel edu.tr için mevcut test beklentisi düzeltildi.
- CI exact-head henüz başlamadı: workflow run listesi 0, commit status 0. **KABUL DEĞİL.**
- Kalan: mobil DDG/Bing/Brave HTML fallback kapatılmadı; Worker production GET kanıtı yok. Custom Tabs ve H1 claim/evidence modeli ayrı görev.
- Ayrıntı: [C1-P0-source-policy-ua-2026-11-25.md](../gorevler/C1-P0-source-policy-ua-2026-11-25.md).


### 17.1 PR #66 taban ve CI tetikleme güncellemesi — 25.11.2026
- PR #66 HEAD güncel: `dc71e10dac04f17d0608678376d3ad38a3e1edf2`.
- Actions workflow dosyasındaki pull_request taban filtresi `is/c3-ilan-semasi-ai-kaynak-kaniti` dalını içerdiği için PR hedefi bu dala alındı.
- Bu nedenle güncel compare, C3 PR #30 değişikliklerini de içerir: 11 commit / 9 dosya / +207 / -41. Bu toplamın tamamı C1 P0 değişikliği değildir; C3 taban değişiklikleri de vardır.
- Yeni HEAD için Actions run listesi ve commit status listesi hâlâ 0. Test PASS iddiası yok. Tetikleme nedeni ayrıca incelenecek.
- PR #66 taslak ve birleştirilmedi; C3 PR #30 ile çakışma riski nedeniyle ana dala taşınamaz.


### 17.2 PR #66 exact-head CI — 25.11.2026
- Tested HEAD: `dc71e10dac04f17d0608678376d3ad38a3e1edf2`.
- Android APK [#45653228021](https://github.com/aries919-bot/Jarvis/actions/runs/45653228021): run started; repository maintenance tests SUCCESS; Android unit tests IN_PROGRESS; APK build pending.
- Full App QA [#45653227891](https://github.com/aries919-bot/Jarvis/actions/runs/45653227891): static/Lint/JUnit job and Android API29/33/34/35 UI audits in progress.
- Final conclusion not available yet. No PASS/ONAY/merge claim.


### 17.3 PR #66 hakem statik incelemesi — 25.11.2026
- Patch okunurken yeni regresyon testindeki `assertTrue` import eksiği bulundu; CI sonucu beklenmeden düzeltildi.
- Düzeltme commit'i: `29503fe0769f6f6dd4aa5765eaf71420b212ff0f`; güncel HEAD bu SHA'dır.
- Önceki HEAD `dc71e10` için açılmış CI koşuları çalışmaya devam ediyordu; yeni HEAD için Actions run listesi henüz dönmedi. Önceki SHA sonucu yeni HEAD'e PASS olarak aktarılamaz.
- PR #66 KABUL/merge bekliyor; kaynak kodu ve test diff'i yeniden sayımı tamamlandı.


### 17.4 PR #66 kapatıldı; temiz kapsam PR #67 — 25.11.2026
- PR #66 kapatıldı (unmerged). Sebep: C3 parent commitleri nedeniyle 9 dosya / +207 / -41 diff oluşmuştu; görev kapsamını dar tutmak için yeniden oluşturuldu.
- Yeni dal: `fix/c1-source-policy-ua-clean-20261125`, taban `is/c3-ilan-semasi-ai-kaynak-kaniti`.
- PR #67: https://github.com/aries919-bot/Jarvis/pull/67 — open/draft; HEAD `9a7dca3c10a78a670f7aafab74c23fe48638416c`.
- Exact compare: 4 commit / 4 dosya / +28 / -13; sadece Ai.kt, CareerMonitor.kt ve iki test dosyası. 0 behind.
- PR #67 exact-head Actions run listesi henüz boş; test PASS yok. Eski PR #66 runları PR #67 için kanıt sayılmaz.


### 17.5 PR #67 son test genişletmesi — 25.11.2026
- PR #67 HEAD `164aad8cb593099d9b76d45e8e416d1893364d78`.
- Yeni test ayrıca katalog içinde Indeed/LinkedIn URL'si bulunmadığını doğrular.
- Compare: 5 commit / 4 dosya / +32 / -13; 0 behind.
- Exact-head Actions workflow run listesi ve combined status henüz boş; CI PASS yok.


## 18. H1 iddia/kanıt çekirdeği — 25.11.2026
- PR #68: https://github.com/aries919-bot/Jarvis/pull/68, `feat/h1-story-claim-evidence-20261125` → `is/c3-ilan-semasi-ai-kaynak-kaniti`; açık/taslak, merge yok.
- HEAD `5f5af96834de429dcb302d939a5fff444a2b01c0`; exact compare 5 commit /2 dosya /+664/-0, 0 behind.
- Production dosyası `CareerNewsVerification.kt` 435 satır; test dosyası 229 satır /11 @Test.
- Model story/claim/evidence/review/correction history; publishedAt null korunur; collectedAt/firstSeenAt ayrılır; doğrulama birincil kanıt + insan onayı gerektirir; çelişen kanıt otomatik Çelişkili olur; yeni kanıt/claim düzeltmesi eski insan onayını sıfırlar.
- PII regex guard: email/phone/IBAN/11 haneli kimlik numarası örüntüsü içeren excerpt kaydedilmez. Bu tüm PII'yi kapsadığı garantisi değildir.
- Unit tests: 11 yazıldı ve kaynak dosyasında yeniden sayıldı. Actions workflow runs 0; combined statuses 0. **CI PASS yok; KABUL/merge yok.**
- PR #68 henüz Haber UI/SharedPreferences entegrasyonu yapmıyor; mevcut haber arşivine otomatik doğrulama durumu yazmıyor. Ayrı UI/store PR gerekir.
- PR #67 (C1 P0) açık/taslak; 4 dosyalı temiz diff; exact-head CI henüz kanıtlanmadı. C1 Worker feed ve 10/100 kapıları bağımsız RED/BLOCKED olarak kalır.
- Görev raporu: [H1-claim-evidence-2026-11-25.md](../gorevler/H1-claim-evidence-2026-11-25.md); şema: [HABER_SEMA.md](HABER_SEMA.md).


## 19. PR #70 — C1 P0 kaynak politikası, doğru CI tabanı — 25.11.2026
- PR #70: https://github.com/aries919-bot/Jarvis/pull/70; `fix/c1-source-policy-ua-ci-20261125` → `kariyer-modulu`; açık/taslak, birleştirilmedi.
- HEAD `5d15ef30e016a0105cc8e1b62ed05b0405a48c77`; exact compare 4 commit /4 dosya /+33/-13; 0 behind.
- Indeed/LinkedIn source/trusted/secondary/search kayıtları production dosyasından çıkarıldı; genel edu.tr trusted listeden çıkarıldı; Chrome/Mozilla taklidi yerine AriesKaynakTara/1.0 dört request noktasında kullanılıyor.
- Yeni policy test 1; mevcut CareerSourceCatalogTest 13 test, eski edu.tr trusted assert'i false oldu.
- İlk PR #70 CI #45657277644: 96 testten 1 eski assertion RED (example.edu.tr trusted beklentisi). Bu assertion düzeltildi.
- Güncel HEAD CI [#45657731556](https://github.com/aries919-bot/Jarvis/actions/runs/45657731556) Android APK #686: son kontrolde IN_PROGRESS; PASS kanıtı yok.
- HTML fallback henüz kaldırılmadı; Worker production GET BLOCKED. C1 kamu 6 kaynak/47 doğrulanmış açık ilan, 10/100 RED.
- Ayrıntı: [C1-P0-source-policy-ua-2026-11-25.md](../gorevler/C1-P0-source-policy-ua-2026-11-25.md).

## 20. PR #69 — H1 story/claim/evidence/correction çekirdeği — 25.11.2026
- PR #69: https://github.com/aries919-bot/Jarvis/pull/69; `feat/h1-story-claim-evidence-ci-20261125` → `kariyer-modulu`; açık/taslak, birleştirilmedi.
- HEAD `fdfdd539a2e81826e53898aa07004ee21a0bd55b`; exact compare 8 commit /2 dosya /+741/-0; 0 behind.
- Production model `CareerNewsVerification.kt`: 500 satır; unit test dosyası 241 satır /11 @Test.
- Story fields: nullable publishedAt, collectedAt, firstSeenAt; atomic claims; supporting/contradictory/context evidence; explicit human review; strict UTC timestamp; HTTPS URL and PII guard.
- Correction history now preserves previous claim text, revised text, full previous evidence list, prior human decision/time and previous/new verdict; active claim's evidence and review reset after correction. Old source evidence is not lost.
- İlk exact-head CI #45656877300 compile error: escaped Locale string; fixed. Current exact-head run [#45657661662](https://github.com/aries919-bot/Jarvis/actions/runs/45657661662) Android APK #685: IN_PROGRESS at last check; PASS not yet.
- PR #68, wrong CI-target branch, closed unmerged. PR #67 wrong base closed unmerged. PR #66/#67/#68 merges yok.
- UI / SharedPreferences integration yok; existing news archive is untouched. UI integration separate PR required.
- Ayrıntı: [H1-claim-evidence-2026-11-25.md](../gorevler/H1-claim-evidence-2026-11-25.md).


### 18.1 H1 PR #68 yerine CI hedefli PR #69 — 25.11.2026
- PR #68 tabanı `is/c3-ilan-semasi-ai-kaynak-kaniti` için `pull_request.branches` filtresi eşleşmiyordu; o hedefte Actions run çıkmaması kod hatası değil workflow tetik kuralıydı.
- Temiz CI hedefi `kariyer-modulu` tabanında PR #69: https://github.com/aries919-bot/Jarvis/pull/69.
- HEAD `c3cd6e65f46c35e996fed30d4d45b0352b307d70`; compare 10 commit /3 dosya /+752/-0 /0 behind. Production 510 satır; test 241 satır /11 @Test /38 assert; test scope JSON dependency.
- İlk PR #69 koşusu #45657242423 eski HEAD `d09786662a6ca26bd548a0a947282b0e775e5dd5` içindi ve yeni commit gelince iptal edildi; yeni exact-head PASS değildir.
- İlk koşuda JSON round-trip Android local mock JSON sınıfları nedeniyle RuntimeException verdi; yalnız testImplementation `org.json:json:20240303` eklendi. Güncel HEAD CI bekleniyor.
- H1 veri modeli source URL'nin resmîliğini kendi başına kanıtlamaz; PRIMARY_SOURCE seçimi UI'da resmî/orijinal kaynak ve insan incelemesiyle sınırlandırılmalı. PR #69 UI/store entegrasyonu değildir.

## 19. C1 P0 kaynak/UA PR #70 — 25.11.2026
- PR #70: https://github.com/aries919-bot/Jarvis/pull/70, `fix/c1-source-policy-ua-ci-20261125` → `kariyer-modulu`; açık/taslak.
- Güncel HEAD `7ef01daf1785eebc3ad26bbf6254cc89e765cc3f`; compare 5 commit /4 dosya /+47/-13 /0 behind.
- Indeed/LinkedIn katalog, trusted/secondary ve arama sorgularından çıkarıldı; genel `.edu.tr` trusted listeden çıkarıldı; Chrome/Mozilla UA `AriesKaynakTara/1.0` oldu; genel/izinli üniversite testi güncellendi. Ek koruma olarak `isTrustedUrl` başında allowlist dışı `.edu.tr` reddedilir.
- İlk exact-head run #45657277644 başarısızdı: 96 test /1 hata, `CareerSourceCatalogTest` akademik alan kuralı. Bu run yeni koruma commitinden önceki HEAD'deydi.
- Yeni HEAD için Android run #45658345528: [Actions](https://github.com/aries919-bot/Jarvis/actions/runs/45658345528), son kontrolde unit tests in_progress; APK adımı pending. PASS/ONAY yok.
- PR #67 eski C3 hedefinde açık ve exact-head CI'siz; #70, aynı kapsamın gerçek Android CI hedefindeki sürümüdür. PR #67/#70 birleştirilmedi.
- C1 10 kaynak/100 doğrulanmış ilan ve kamu ayrı raporu eşikleri değişmedi; bu P0 kaynak kodu düzeltmesi ilan sayısını yükseltmez.


### 18.2 PR #69 exact-head unit test + APK PASS — 25.11.2026
- Latest tested HEAD `c3cd6e65f46c35e996fed30d4d45b0352b307d70`.
- Android APK [#45658340086](https://github.com/aries919-bot/Jarvis/actions/runs/45658340086): **SUCCESS**. `testDebugUnitTest`, `assembleDebug` and artifact upload all SUCCESS.
- Artifact [15043163794](https://github.com/aries919-bot/Jarvis/actions/runs/45658340086/artifacts/15043163794), 14,082,661 bytes; SHA-256 `514af7cdba04e162f0bfd065cfad22afa257c5b158b90cc1f6dcf15578bce38f`.
- Model test source: 11 @Test /38 assert calls. This is exact-head model unit-test + debug APK evidence, not Full App QA or real-phone evidence.
- PR #69 remains open/draft; no merge/deploy. H1 UI/store integration remains next separate step.

## 21. PR #70 User-Agent tek sabit / exact-head QA — 25.11.2026
- C1 PR #70 güncel HEAD: `bf8ca39c1dee78b8436069e5cfabcbbd3afbc146`; compare 8 commit /4 dosya /+46/-13 /0 behind.
- `Net.ARIES_SOURCE_USER_AGENT` tek sabit olarak tanımlandı; dört kaynak/RSS isteği bunu kullanır. Regresyon testi sabitin doğru Aries kimliği olduğunu ve Mozilla/Chrome token'ı taşımadığını doğrular.
- Önceki HEAD #45657731556 (#686) unit test + APK + upload SUCCESS, artifact 15043108366; ancak sabit refaktöründen önceki head'dir.
- Güncel exact-head [#45658875411](https://github.com/aries919-bot/Jarvis/actions/runs/45658875411) (#692): unit test SUCCESS; APK derlemesi IN_PROGRESS (son kontrol). Final SUCCESS henüz yok.
- Güncel PR70 raporu: [C1-P0-source-policy-ua-2026-11-25.md](../gorevler/C1-P0-source-policy-ua-2026-11-25.md).

## 22. PR #69 H1 gerekçe ve düzeltme izi / yeni exact-head — 25.11.2026
- H1 PR #69 güncel HEAD: `ebaee5c4c30d746ed85191ce5b249925a4181fc0`; compare 13 commit /3 dosya /+800/-0 /0 behind.
- Veri modeli 519 satır; testler 280 satır /13 @Test; `app/build.gradle.kts` içinde yalnız testImplementation `org.json` desteği.
- Human CONFIRM/REFUTE gerekçesi zorunlu; düzeltme kaydı önceki iddia/kanıt/review kararını ve gerekçeyi korur; no-op correction reddedilir.
- Eski CI #45658340086 (#688) önceki head'de test/APK/upload SUCCESS, ama son gerekçe/no-op testlerini içermiyor.
- Güncel exact-head [#45659231791](https://github.com/aries919-bot/Jarvis/actions/runs/45659231791) (#695): birim testleri IN_PROGRESS (son kontrol). PASS henüz yok.
- Haber ekranı/yerel arşiv entegrasyonu yapılmadı; eski haber kayıtları otomatik VERIFIED yapılmaz. [H1 görev raporu](../gorevler/H1-claim-evidence-2026-11-25.md).
### 22.1 H1 latest exact-head duplicate test correction — 25.11.2026
- Güncel PR #69 HEAD `79848e8848653de5fc495e5e35dcc5e305aff545`; compare 14 commit /3 dosya /+783/-0 /0 behind.
- Test dosyası 12 @Test. #45659231791 (#695) iki kez tanımlanmış `humanReviewRequiresRationaleAndNoOpCorrectionIsRejected` nedeniyle compile test aşamasında FAIL oldu; duplicate tanım kaldırıldı.
- Güncel exact-head run #45660034027 (#696): `Birim testleri` IN_PROGRESS son kontrol; henüz PASS yok.
- PR açıklaması ve `gorevler/H1-claim-evidence-2026-11-25.md` bu son SHA/sayı/test durumuna güncellendi.
### 22.2 H1 storyId URL kararlılığı — 25.11.2026
- StoryId artık headline yerine canonical HTTPS URL'ye dayanır; aynı resmî URL'de başlık değişirse aynı story kaydı korunur. Host/scheme lowercase; fragment ve trailing slash kaldırılır; default port normalize edilir; path case ve query korunur.
- Regresyon testi: fragment/trailing slash/host case ve headline update aynı storyId; farklı URL farklı storyId.
- PR #69 latest HEAD `36cba13cb382fb55a8a583c878177429f69c42f6`; exact compare 16 commit /3 dosya /+807/-0 /0 behind; 12 @Test.
- Önceki head `79848e8` run #45660034027 (#696) 12 tests + APK + artifact SUCCESS; ancak URL-only storyId iyileştirmesini içermiyor.
- Yeni exact-head run [#45660576132](https://github.com/aries919-bot/Jarvis/actions/runs/45660576132) (#697) IN_PROGRESS; final PASS henüz yok.
### 21.1 C1 PR #70 scoped öz-QA — 25.11.2026
- ChatGPT öz-QA karar kaydı: PR #70 kapsamı için ONAY; bu bağımsız Claude/insan review değildir.
- Diff: Ai.kt + CareerMonitor.kt + 2 test dosyası; başka dosya yok.
- Kod yeniden taraması: CareerMonitor production'da Indeed/LinkedIn ve generic edu.tr trust kalıbı 0; Ai.kt'te Mozilla/Chrome UA 0; Aries UA tek sabit + dört kullanım.
- Exact-head #45658875411 (#692) tüm birim test, APK ve artifact upload adımları SUCCESS; artifact 15044212711.
- PR #70 açık/taslak; merge/deploy yok. C1 genel gate hâlâ 6/47 RED ve production GET BLOCKED.

### 19.1 PR #70 exact-head CI PASS — 25.11.2026
- Current tested HEAD `bf8ca39c1dee78b8436069e5cfabcbbd3afbc146`.
- [Android Actions #45658875411](https://github.com/aries919-bot/Jarvis/actions/runs/45658875411): SUCCESS. Unit tests, APK build and artifact upload all SUCCESS.
- Artifact [15044212711](https://github.com/aries919-bot/Jarvis/actions/runs/45658875411/artifacts/15044212711), 14,061,515 bytes; SHA-256 `2a8f2183f474b907ee9345266b75c7a6d483c0982939073b2667f6560ce0b9de`.
- Static source recount: Indeed/LinkedIn production occurrences 0; Chrome/Mozilla browser-UA 0; Aries source UA 1 literal +4 uses; generic edu.tr trust removed and unlisted academic hosts rejected.
- PR #70 remains open/draft; no merge/deploy. C1 10/100 and public reporting gates remain RED.

## 20. H1 Haber arşivi entegrasyonu PR #71 — 25.11.2026
- PR #71: https://github.com/aries919-bot/Jarvis/pull/71, `feat/h1-news-store-ui-20261125` → `kariyer-modulu`; open/draft.
- Current HEAD `a985d9600da7237bd45b954ca5e82a9c850f1944`; compare 34 commit /8 files /+1725/-25 /0 behind.
- Adds private claim/evidence screen, isolated SharedPreferences store with last-good, no silent eviction, primary-source allowlist, human review and append-only corrections; old `career_news_items` remains unchanged; unknown publication/first-seen dates remain null.
- CareerActivity external links switched to Custom Tabs; exact ACTION_VIEW count 0 in that file.
- Tests: 17 model +6 store =23 @Test.
- First run #45661731482 failed compile on EditText.singleLine and escaped string interpolation. Both were fixed in HEAD `a985d96`.
- Exact-head Android run [#45662111632](https://github.com/aries919-bot/Jarvis/actions/runs/45662111632): latest status in_progress at last check; no PASS yet.
- H1 core PR #69 exact-head tests + APK already PASS, but PR #71 is the actual UI/store superset. No merge/deploy; phone/emulator test pending.
- Detailed task: [H1-news-store-ui-2026-11-25.md](../gorevler/H1-news-store-ui-2026-11-25.md).


### 20.1 PR #71 SQLite ve sınırlandırma güncellemesi — 25.11.2026
- H1 yerel depo SharedPreferences'tan platform SQLite'a geçirildi; yeni harici DB kütüphanesi yok. Primary + last-good JSON aynı SQLite transaction'ında yazılır; ilk kayıtta da backup vardır.
- Sınırlar: 300 hikâye, 16 KB/hikâye, 50 claim/hikâye, 25 evidence/claim, 100 correction/hikâye. Kapasite dolunca kayıt silinmez; yeni kayıt reddedilir.
- PII koruması publisher/review/correction metinlerine genişletildi.
- Güncel PR #71 HEAD `b9abd9433badf90d0cda306c8c81daa001b0a383`; compare 43 commit /8 files /+1811/-25 /0 behind; 18 model +6 store =24 @Test.
- Exact-head Actions [#45663064814](https://github.com/aries919-bot/Jarvis/actions/runs/45663064814): queued; PASS yok.
- Store fake adapter testleri geçse bile SQLite Android adapter için gerçek emulator/telefon smoke testi hâlâ gerekir.


## 19. H1 Haber kartı + yerel doğrulama arşivi ve C1 kaynak güvenliği — 25.11.2026
- PR #69: https://github.com/aries919-bot/Jarvis/pull/69 açık/taslak; HEAD `b095528b9bb7974578c564be9e84791fa300ff55`; compare `kariyer-modulu` tabanına göre 29 commit /8 dosya /+1.572 /-35 /0 behind.
- PR #69 kapsamı: H1 claim/evidence, primary-source allowlist gate, ayrı 1.5 MB yerel arşiv, append-only correctionHistory, Haber kartı iddia/kanıt/review/correction UI, Custom Tabs ve 18 yeni JUnit testi.
- Browser 1.10.0 denemesi AAR metadata hatası verdi: compileSdk 36 + AGP 8.9.1 gerekir. AGP/compileSdk yükseltilmedi; Browser 1.8.0'a sabitlendi.
- Exact-head Android run [#45665491590](https://github.com/aries919-bot/Jarvis/actions/runs/45665491590): AAR metadata ve 18 yeni + mevcut testlerin test task'ı SUCCESS; APK assemble adımı son kontrolde IN_PROGRESS. Final run conclusion bekleniyor.
- PR #70: https://github.com/aries919-bot/Jarvis/pull/70 açık/taslak; HEAD `bf8ca39c1dee78b8436069e5cfabcbbd3afbc146`; 8 commit /4 dosya /+46 /-13 /0 behind. Indeed/LinkedIn katalog/güven/arama izleri kaldırıldı, genel .edu.tr trusted listesinden çıkarıldı, UA AriesKaynakTara/1.0. [Exact-head Android run #45658875411](https://github.com/aries919-bot/Jarvis/actions/runs/45658875411): unit tests SUCCESS, APK SUCCESS; artifact `15044212711`.
- İlk PR #70 denemesi test beklentisi yüzünden RED idi (eski test genel edu.tr için true bekliyordu); beklenti false olarak düzeltildi ve yeni exact head test+APK PASS aldı.
- Eski PR #67 ve #68 kapatıldı (ikisi de unmerged); sırasıyla #70 ve #69 bunların yerini aldı. PR #66 da unmerged kapalıdır.
- Full App QA workflow'u `kariyer-modulu` tabanında bulunmadığından Android emülatör UI run yok. Gerçek telefon testi yok.
- C1 kaynak/Worker sayımları bu PR'larla değişmedi; 10 kaynak/100 doğrulanmış ilan gate'i hâlâ ayrı RED/BLOCKED. PR #70'in #69'dan önce ana feature tabanına alınması gerekir; merge yapılmadı.

### 20.2 PR #71 SQLite, append-only ve toplu okuma sağlamlaştırması — 25.11.2026
- PR #71 exact HEAD `3a1017528ebb1d87ca8e7f87022ae610ce1bdc6c`; compare `kariyer-modulu` tabanına göre 52 commit /8 dosya /+1.979 /-26 /0 behind.
- Test yeniden sayımı: `CareerNewsVerificationTest.kt` 17 @Test + `CareerNewsVerificationStoreTest.kt` 8 @Test = **25 test**.
- İlk PR #71 CI #45663064814, test dosyasında aynı fonksiyon iki kez bulunduğu için `compileDebugUnitTestKotlin` aşamasında RED oldu. Yinelenen metot kaldırıldı; ara HEAD `2597f36210d077a19b0e8e4ec100713d6736e141` için run [#45666479386](https://github.com/aries919-bot/Jarvis/actions/runs/45666479386) SUCCESS.
- Yeni exact-head sağlamlaştırmaları: storyId aynı canonical URL'de başlık değişiminden etkilenmez; store eski evidence/review/correctionHistory öğelerinin sessizce silinmesini reddeder; önceki reviewNote düzeltme kaydında korunur; haber kartı listesi doğrulama durumlarını tek toplu SQLite IN sorgusuyla yükler.
- Store testinde toplu okuma sayacı 1 sorguyu doğrular; primary bozuksa last-good kayıttan geri döner.
- Güncel exact-head Android run [#45667520387](https://github.com/aries919-bot/Jarvis/actions/runs/45667520387) yeni HEAD için başladı; son kontrolde pending. Önceki ara head SUCCESS sonucu güncel HEAD'e aktarılmaz.
- PR #71 açık/taslak; merge/deploy yok. Full App QA workflow'u `kariyer-modulu` tabanında yok; emulator ve telefon denemesi yok.
- PR #71, PR #69'daki H1 çekirdeğini ve UI entegrasyonunu kapsayan güncel SQLite adayıdır. PR #69 ayrıca açık/taslak ve CI PASS durumda; iki PR da birleştirilmedi.
- C1 sayımı/Worker değişmedi; 10 kaynak/100 doğrulanmış ilan ve production GET kapıları ayrı RED/BLOCKED kalır.

### 20.3 PR #71 exact-head CI sonucu — 25.11.2026
- Tested HEAD: `3a1017528ebb1d87ca8e7f87022ae610ce1bdc6c`.
- Android APK run [#45667520387](https://github.com/aries919-bot/Jarvis/actions/runs/45667520387): **SUCCESS**. JUnit task, APK assemble and artifact upload all SUCCESS.
- Log kanıtı: `BUILD SUCCESSFUL in 1m 31s` (testDebugUnitTest) ve `BUILD SUCCESSFUL in 50s` (assembleDebug). Test dosyası anotasyon yeniden sayımı 17 model +8 store =25 yeni @Test.
- APK artifact `aries-apk`, ID `15049240153`, 14,176,012 bytes, unexpired.
- This is Android unit-test + debug APK evidence only. No Full App QA/emulator workflow on `kariyer-modulu`; no real phone test.
- PR #71 remains open/draft, unmerged. PR #69 and #70 remain open/draft; no PR merged/deployed.
- C1 10/100, public-source evidence, Worker authenticated production GET and e-Şube/H2 dedup counts unchanged; still separate RED/BLOCKED gates.

### 20.4 PR #71 Android emülatör smoke workflow — 25.11.2026
- Current PR #71 HEAD `0a376b4c7355a5a6822252da4d9286606ce23bde`; base `kariyer-modulu`; compare 57 commit /10 dosya /+2.132 /-26 /0 behind.
- Test yeniden sayımı: 17 model +8 SQLite store +2 Android instrumentation = **27 @Test**.
- Yeni `.github/workflows/h1-android-ui-smoke.yml`: `pull_request` + `workflow_dispatch`; duplicate branch `push` trigger kaldırıldı; concurrency key PR numarası; checkout `github.event.pull_request.head.sha || github.sha` ile exact head'e açıkça sabitlendi.
- Android emulator runner immutable commit `a421e43855164a8197daf9d8d40fe71c6996bb0d`; API 34 / Google APIs / x86_64. CI sadece H1 Activity + SQLite yerel davranışını test eder.
- Instrumentation iki test: Haber doğrulama Activity açılır, başlık ve iddia/kaynak düğmeleri görünür, `publishedAt=null` SQLite'a kaydolur; ikinci test bozuk primary'den last-good kurtarmayı doğrular.
- Exact-head normal Android APK run [#45669708914](https://github.com/aries919-bot/Jarvis/actions/runs/45669708914): SUCCESS; JUnit, assembleDebug ve artifact upload SUCCESS. Artifact `aries-apk`, ID `15049965420`, 14,176,024 bytes.
- Exact-head emulator run [#45669709252](https://github.com/aries919-bot/Jarvis/actions/runs/45669709252): son kontrolde `Run Android emulator instrumentation tests` IN_PROGRESS; final conclusion/artifact yok. Önceki emulator koşuları push/PR concurrency düzenlemesi sırasında iptal edildi; bunlar PASS sayılmaz.
- Smokescreen yalnız Activity görünümü + SQLite create/last-good kurtarmayı doğrular; claim/evidence/review düğmelerinin tamamı uçtan uca UI tıklama testinden geçmedi. Bu ayrı takip işidir.
- PR #71 open/draft/unmerged. PR #70 source-policy P0 ayrı ve exact-head Android test+APK PASS; #71 base'de bu değişiklikler henüz birleştirilmedi. Merge sırası/entegrasyon ayrıca ele alınmalı. Real phone test yok; merge/deploy yok.

### 20.5 Üçüncü hakem denetimi ve emülatör kök neden düzeltmesi — 25.11.2026
- Hakem denetimi: PR #71 için kapsam/diff yeniden sayıldı: base `kariyer-modulu`, HEAD `4a9a05bda6a699666769bdcb99d1cabb122991c0`, 57 commit /10 dosya /+2.132 /-26 /0 behind. Önceki 27 @Test sayımı değişmedi; son commit yalnız workflow YAML'ına 2 satır ortam değişkeni ekledi.
- Önceki emulator run [#45669709252](https://github.com/aries919-bot/Jarvis/actions/runs/45669709252) **FAIL**: instrumentation testleri başlamadı. Log: Emulator 37.3.10, runner `-accel off` ile başlattı; emulator açıkça `Software CPU emulation (-accel off) is not supported` dedi ve ADB cihazı bulunamadı; sonunda `Timeout waiting for emulator to boot`. Bu uygulama testi hatası değil, CI emulator boot altyapısı hatasıdır; yine de PASS sayılamaz. Artifact yok.
- Kök neden için workflow adımına `ANDROID_I_WANT_MY_TCG: "yes"` eklendi (emulator logunun önerdiği TCG yazılım emülasyonu). Düzeltme commit'i `4a9a05bda6a699666769bdcb99d1cabb122991c0`; exact-head APK run [#45673231753](https://github.com/aries919-bot/Jarvis/actions/runs/45673231753) ve emulator run [#45673231634](https://github.com/aries919-bot/Jarvis/actions/runs/45673231634) başlatıldı, son kontrolde IN_PROGRESS. Sonuçları gelmeden PASS yok.
- **Hakem bulgusu / BLOCKER:** `CareerNewsVerificationStore.preservesHistory()` insan incelemesi kararının CONFIRM→REFUTE/INCONCLUSIVE gibi değiştirilmesini engellemiyor; `CareerNewsClaim.withHumanReview()` önceki kararı doğrudan overwrite ediyor. `withEvidence()` yeni kanıt eklenince eski insan kararını null yapıyor. Her iki durumda da eski review için append-only geçmiş kaydı oluşmuyor. Bu nedenle “önceki insan incelemesi sessizce silinemez” iddiası tam karşılanmıyor. Çözüm: review geçmişini append-only model/store'a ekle veya her karar değişimi/kanıt sonrası invalidation için açık audit entry oluştur; eski kararın zaman/not/kanıt bağını koruyan regresyon testleri ekle.
- Diğer kapsam bulguları: kanıt ekleme formu `publishedAt=null` sabitliyor (tarih alanı yok; kaynak tarihi girilemiyor); correction UI önceki kanıtların yalnız sayısını gösteriyor, yayıncı/URL/alinti detayını göstermiyor. Bunlar ayrı takip işi.
- PR #71, PR #70 kaynak politikası düzeltmesini içermiyor. #70 merge edilmeden PR #71'in tabanındaki eski `CareerSourceCatalog` ile birincil kaynak sınıflandırması tek başına kabul edilmemeli. #70 exact-head Android CI [#45658875411](https://github.com/aries919-bot/Jarvis/actions/runs/45658875411) PASS; PR hâlâ ayrı.
- **Hakem kararı: RED / değişiklik gerekli.** Normal APK CI önceki HEAD'de PASS olsa da yeni HEAD CI bekliyor; emulator önceki HEAD'de FAIL (altyapı); audit geçmişi açığı P1 blocker. Merge/deploy yok; gerçek telefon testi yok.

## 18) H1 PR #71 — append-only review geçmişi ve UI akışı / 25.11.2026
- Hakem blocker: önceki `CONFIRM/REFUTE/INCONCLUSIVE` kararı yeni kanıt veya yeniden inceleme sırasında kaybolabiliyordu. `CareerNewsReviewEvent` ile karar + UTC zaman + gerekçe + o anki evidence ID listesi append-only saklanıyor.
- Yeni kanıt eski review'u arşivler ve aktif kararı sıfırlar; yeniden inceleme önceki kararı arşivler; Store reviewHistory silme/yeniden yazmayı reddeder.
- PR #71 exact HEAD `6b392a3aa02d42700743657350d081d7f7ad6aeb`; 10 dosya / +2.578 / -26.
- UI iyileştirmesi: evidence formuna isteğe bağlı publishedAt UTC alanı eklendi (boşsa null; geçersizse reddedilir). Correction geçmişi önceki evidence yayıncı/tür/stance/alinti/yayın-kontrol tarihi ve kaynak linkini gösterir.
- Test sayımı dosyalardan tekrar: 20 model +10 store +4 Android instrumentation = **34 test**.
- Gerçek UI akışı instrumentation'a eklendi: claim → evidence + publishedAt → review → yeni evidence ile review arşivi → correction → eski evidence ayrıntısı.
- Exact-head APK/unit [#45679523180](https://github.com/aries919-bot/Jarvis/actions/runs/45679523180) IN_PROGRESS; emulator/SQLite [#45679523114](https://github.com/aries919-bot/Jarvis/actions/runs/45679523114) IN_PROGRESS.
- Önceki emulator #45675851473 yeni HEAD sebebiyle CANCELLED; PASS değildir. Eski APK/unit #45675851347 SUCCESS, ancak yeni UI testlerini kapsamaz.
- **KARAR: RED / beklemede.** Güncel exact-head iki CI PASS ve gerçek telefon denemesi olmadan H1 merge yok.
- Ayrıntı: `gorevler/H1-UI-PUBLISHEDAT-EMULATOR-2026-11-25.md`.

## 19) C1 PR #11 — H2 + e-Şube exact-head QA / 25.11.2026
- Private HEAD `b1415d6a76fd3856c60cfec891a575c3bef0341e`; H2 reader SHA `fe37be93202442394504f455b4a1caeb31f298cf`.
- Public exact-head QA [#45651444155](https://github.com/aries919-bot/Jarvis/actions/runs/45651444155) SUCCESS; artifact [15041491104](https://github.com/aries919-bot/Jarvis/actions/runs/45651444155/artifacts/15041491104) JSON/TXT açılıp sayaçlar yeniden sayıldı.
- `collectedAt=2026-11-25T16:11:57.566Z`; 35 kaynak /27 fresh /8 stale; 12 benzersiz kamu kaynağı /55 doğrulanmış açık kamu kartı. e-Şube 42 + H2 13. 116 deadline belirsiz kayıt (112 özel +4 H2); geçmiş deadline 0.
- Stale kaynaklar: 6 Ashby 401 robots, BOTAŞ timeout, Mardin Artuklu öğretim üyesi deadline kanıtı eksik.
- **Hakem bulgusu:** H2 mapper `city: null` yazıyor; upstream H2 `make_item()` şehir alanı üretmiyor. Bu nedenle exact kurum+pozisyon+şehir+deadline eşleşmesi canlı H2/e-Şube verisinde çalışamaz. Sentetik dedup testi uçtan uca kanıt değildir. Şehir resmî metinde yoksa tahmin edilmez; olası tekrarlar doğrulanmış sayımdan çıkarılır.
- Artifact `PUBLISH status=SKIPPED`; production authenticated Worker GET/POST ve D1 kalıcılığı kanıtlanmadı.
- **KARAR: RED.** 12 kaynak kapısı geçiyor; 55/100 kart eşiği 45 eksik. Production GET/D1 ve şehir kanıtlı cross-feed dedup eksik. PR #11 merge yok.
- Ayrıntı: `gorevler/C1-H2-FEED-QA-2026-11-25.md`.
## 20) C1 PR #70 — resmî olmayan kaynakların ikincil kalması / 25.11.2026
- PR #70 exact HEAD `7ab7aec5e5fa34209fec5c56d20a3c468af89a78`, 4 dosya; PR metadata diff +75/-20.
- LinkedIn/Indeed kaynak katalog ve sorgulardan çıkarıldı; genel `.edu.tr` trust kaldırıldı ve açık üniversite allowlist'i korundu; istek UA'sı `AriesKaynakTara/1.0`.
- Öz-QA'da SecretCV/Yenibiriş/İşbul'un non-official iken primary alert üretme riski bulundu. `isSecondary()` artık katalogdaki `official=false` metadata'sından türetiliyor ve kaynak host seti cache ediliyor.
- Ara HEAD #45676772911 FAILED: 98 testte 2 assertion testi yanlış varsayımı; beklentiler düzeltildi.
- Güncel exact-head Android APK/unit [#45677312902](https://github.com/aries919-bot/Jarvis/actions/runs/45677312902) SUCCESS; `testDebugUnitTest`, `assembleDebug`, APK artifact #15052113398 başarılı.
- **HAKEM: ONAY.** Yalnız kaynak/UA P0 kapsamı için. H1 kabulünden sonra C1 sırasındaki ilk merge adımı.
- Ayrıntı: `gorevler/C1-SOURCE-POLICY-2026-11-25.md`.

## 21) H1 UI publishedAt ve emulator akışı — 25.11.2026
- PR #71 HEAD `6b392a3aa02d42700743657350d081d7f7ad6aeb`; 10 dosya / +2.578 / -26.
- 20 model +10 store +4 Android instrumentation =34 test. UI testi iddia/kanıt/yayın tarihi/insan incelemesi/yeni kanıtla arşiv/düzeltme/önceki kanıt ayrıntısı akışını deniyor.
- Exact-head APK/unit [#45679523180](https://github.com/aries919-bot/Jarvis/actions/runs/45679523180) **SUCCESS**; birim testleri, APK derlemesi ve artifact upload geçti.
- Exact-head emulator/SQLite [#45679523114](https://github.com/aries919-bot/Jarvis/actions/runs/45679523114) son kontrolde IN_PROGRESS; BlobNotFound log erişimi test sonucu değildir.
- Önceki emulator #45675851473 yeni HEAD sebebiyle CANCELLED; PASS değildir.
- **H1 karar: RED / beklemede.** Güncel emulator PASS ve gerçek telefon doğrulaması olmadan merge yok.
- C1 PR #70 exact-head CI PASS ve öz-QA ONAY; H1 sonrası merge sırasını bekler.
- C1 PR #11 hâlâ RED: 12 kamu kaynağı /55 doğrulanmış açık ilan; 100 eşiğine 45 eksik; Worker production GET/D1 ve şehir kanıtlı dedup yok.
- Ayrıntı: `gorevler/H1-UI-PUBLISHEDAT-EMULATOR-2026-11-25.md`.

## 22) Aries mimari/dosya düzeni ekip QA — 25.11.2026
- Dört ayrı QA geçişi yürütüldü: mimari dosya sınırları, güvenlik/yönetişim, H1 veri bütünlüğü, CI/test/C1 kapıları. Gerçek ayrı ajan aracı bulunmadığı için ajan kullanıldığı iddia edilmedi.
- Envanter: `kariyer-modulu` 79 kayıt/30 Kotlin; H1 HEAD 91 kayıt/36 Kotlin.
- Büyük sorumluluklar: CareerActivity 2.795 satır; CareerMonitor 2.049; MainActivity 1.802; Ai.kt 735. CareerMonitor tek dosyada kaynak kataloğu, uygunluk, tarih parser, search engine, recruitment tracker ve Worker scheduler içeriyor.
- Media klasöründe 5 eski backup yaklaşık 262.725 byte; kapsam dışı olduğundan dokunulmadı.
- `denetle.py` ilk 12 KB diff'i Groq'a gönderiyor; çalıştırılmıyor. `AGENTS.md` ve `DURUM.md` eski ajan talimatları içeriyordu; `durum` dalında güncellendi. `.gitignore` gorevler/HARITA desenleri ayrıca düzenleme işi.
- Mimari görev kaydı: `gorevler/ARIES-MIMARI-DAGINIKLIK-2026-11-25.md`.
- Refaktör kararı: H1 ve #70 sırası tamamlandıktan sonra CareerMonitor class-boundary extraction küçük PR'larla; ardından CareerActivity repository/page controller ayrımı. Büyük toplu taşıma yok.
- **KARAR: RED / refaktör bekliyor.** Envanter ve sıra hazır; kaynak dosyaları henüz taşınmadı/silinmedi.

## 23) Paralel QA ekip panosu — 25.11.2026
- Altı ayrı QA iş akışı tanımlandı: H1 model/SQLite, H1 Android UI/emülatör, C1 kaynak/UA, C1 canlı kamu/Worker, mimari dosya sınırları, güvenlik/izinler.
- Gerçek alt ajan aracı bulunmadığı için bunlar bağımsız ajan çalıştırması değil; ayrı sorumluluk ve kanıt kapsamlı öz-QA hatlarıdır. Koordinatör tüm çıktıları çapraz denetler.
- QA-01 model/store kod incelemesi tamam; PR #71 exact HEAD `6b392a3aa02d42700743657350d081d7f7ad6aeb`, 20 model +10 store test.
- QA-02 APK/unit #45679523180 SUCCESS; emülatör #45679523114 IN_PROGRESS. Exact-head emülatör ve telefon testi bekliyor.
- QA-03 PR #70 exact HEAD `7ab7aec5e5fa34209fec5c56d20a3c468af89a78`, 4 dosya +75/-20; CI #45677312902 SUCCESS; H1 sırası bekliyor.
- QA-04 C1 #11 RED: 12 kamu kaynağı/55 ilan; 45 eksik; H2 city null, Worker production GET/D1 kanıtı yok. PR base `fix/c1-source-category-after-timestamp`, ana entegrasyon zinciri çözülmeden bağımsız merge edilmez.
- QA-05 büyük dosya envanteri tamam; CareerMonitor extraction, H1 ve #70 kapılarından sonra küçük PR.
- QA-06 `denetle.py` Groq'a diff gönderdiği için çalıştırılmıyor; manifest izinlerinin kod kullanım haritası açık.
- Görev panosu: `gorevler/QA-EKIP-PANOSU-2026-11-25.md`.
- Hız kuralı: bağımsız hatlar paralel, aynı branch/dosya üzerinde yazma sıralı; her hat kendi test/kanıtını üretir; kırmızı bulguda döngü yerine kök neden ve tek takip işi.
- **KARAR: RED / genel kabul yok.** Emülatör, telefon, izin envanteri, 45 kamu ilanı ve Worker/D1 kapıları açık.

## 24) Güvenlik QA — Android izin akışı bulgusu — 25.11.2026
- Manifest 10 izin içeriyor: RECORD_AUDIO, CALL_PHONE, READ_CONTACTS, INTERNET, ACCESS_NETWORK_STATE, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED, SET_ALARM, USE_EXACT_ALARM, BLUETOOTH_CONNECT.
- MainActivity onCreate satır 140–145 uygulama açılışında RECORD_AUDIO + CALL_PHONE + READ_CONTACTS + POST_NOTIFICATIONS izinlerini topluca istiyor.
- MainActivity tam kaynak taramasında CALL_PHONE / READ_CONTACTS yalnız izin listesinde bulundu; ACTION_CALL, ACTION_DIAL, Phone.CONTENT_URI ve Phone.query kullanımı bulunmadı. ContactsContract.CommonDataKinds.Phone import'u var, kullanım bulunmadı.
- RECORD_AUDIO sesli komut akışında; BLUETOOTH_CONNECT yalnız kullanıcı ses çıkış seçicisini açtığında isteniyor; POST_NOTIFICATIONS CareerActivity'de bildirim akışıyla da isteniyor. Reminders.kt `setAlarmClock` kullanıyor; exact alarm izni gerekmeyebilir, ayrıca doğrulanmalı.
- **Güvenlik bulgusu:** CALL_PHONE ve READ_CONTACTS açılışta gereksiz isteniyor gibi görünüyor. Manifest ve onCreate'den kaldırma, tam repo araması + regresyon testi ve ayrı küçük PR gerektirir. Bu oturumda kod değiştirilmedi; aktif kapsam ve PR'lar kirletilmedi.
- Emülatör #45679523114 son kontrol: `in_progress`, conclusion `null`; step 5 hâlâ çalışıyor. 35 dakikalık workflow timeout dolmadan fail/stall kararı verilmez.
- Ayrıntı: `gorevler/QA-EKIP-PANOSU-2026-11-25.md`.

## 25) C1 kamu ilanı keşif dalgası 1 — 25.11.2026
- Resmî sayfalardan 7 açık duyuru adayı bulundu: Sayıştay 16; Zafer 2; TGA 13; Hazine Kontrolörleri Kurulu en fazla 10; Anadolu 1; Bartın 1; SSB 26 pozisyon. Kontenjan sayıları ilan kartı sayısı olarak sayılmadı.
- Yeni kaynak adayı 4: Sayıştay, HMB, Bartın, SSB. H2'de Zafer/TGA/Anadolu zaten mevcut; exact duplicate kontrolü zorunlu.
- Gelecek açılış: ATK 181 pozisyon 30 Kasım'da, YÖKAK 5 pozisyon 1 Aralık'ta açılıyor; bugün açık sayımına eklenmez.
- DİKA son başvuru tarihi teyit edilemedi; dışlandı. Üçüncü taraf listeleri sadece keşif içindi; resmî kaynak yerine kullanılmadı.
- Kamu sayımı hâlâ 12 kaynak /55 doğrulanmış ilan. Yeni araştırmadan feed'e eklenen 0; exact duplicate ve Worker entegrasyonu yapılmadı.
- Araştırma: `gorevler/C1-RESMI-ILAN-ARASTIRMASI-2026-11-25.md`.
- **KARAR: C1 RED; 45 ilan eksiği sürüyor.** Kaynak keşfi ilerledi, sayım henüz değişmedi.

## 26) QA-07 diff/secret ve QA-08 CI hız denetimi — 25.11.2026
- PR #71 exact diff: 10 dosya / +2.578 / -26. Secret pattern 0; LinkedIn/Indeed URL 0; fake Mozilla/Chrome UA 0. publishedAt sabitleri yalnız iki tarih doğrulama testinde.
- PR #70 exact diff: 4 dosya / +75 / -20. Secret pattern 0; fake browser UA 0; AriesKaynakTara/1.0 eklenmiş. LinkedIn/Indeed dizeleri yalnız yasak kaynakları reddeden test assertion'larında.
- CI optimizasyon bulgusu: android.yml testDebugUnitTest ve assembleDebug için iki ayrı Gradle çağrısı çalıştırıyor; sonraki ayrı PR'da tek Gradle çağrısına birleştirme değerlendirilecek.
- H1 emülatör job timeout 35 dk; run #45679523114 step 5 IN_PROGRESS; log endpoint BlobNotFound. Timeout dolmadan FAIL/STALL denmez.
- Sonraki workflow PR'ında test report upload if: always() yapılmalı.
- Ayrıntı: `gorevler/QA-EKIP-PANOSU-2026-11-25.md`.

## 27) H1 emülatör uzun koşu — 25.11.2026 19:12Z
- Run #45679523114 exact HEAD `6b392a3aa02d42700743657350d081d7f7ad6aeb`: 18:52:22Z'de başladı; 19:12:37Z'de hâlâ IN_PROGRESS. Step 5 Android instrumentation; önceki setup adımları PASS.
- Job timeout 35 dk; teorik timeout sınırı 19:27:22Z. Bu sınırdan önce test sonucu uydurulmayacak.
- Job log isteği BlobNotFound döndürdü; bu PASS/FAIL kanıtı değildir. Test raporu upload adımı henüz başlamadı.
- APK/unit run #45679523180 SUCCESS aynı exact HEAD.
- **Karar: H1 RED/beklemede; emülatör ve telefon doğrulaması olmadan merge yok.**

## 28) C1 kaynak politikası PR #70 birleştirildi — 25.11.2026
- PR #70 exact HEAD `7ab7aec5e5fa34209fec5c56d20a3c468af89a78`; 4 dosya / +75 / -20.
- Exact-head Android unit + APK CI [#45677312902](https://github.com/aries919-bot/Jarvis/actions/runs/45677312902) SUCCESS; scoped öz-QA ONAY; secret/browser-UA/source-policy diff taraması temiz.
- PR #70 draft ready yapıldı ve merge edildi: commit `09f98b324a9a1f38dbbc25853ec2eba2fc544db9`; `kariyer-modulu` branch head bu commit.
- PR #71 artık güncel base üzerinde mergeable; ancak emülatör run #45679523114 hâlâ PR head'i checkout ediyor. #70 base entegrasyonunu emülatörde doğrulamak için mevcut run kapandıktan sonra H1 workflow merge-ref checkout edecek şekilde güncellenecek ve exact new-head CI tekrar çalıştırılacak.
- PR #71 merge yok; emülatör + gerçek telefon testi bekleniyor.
- **KARAR:** #70 yalnız kaynak/UA P0 kapsamı için kabul edildi. C1 genel 10/100 ve production GET/D1 hâlâ RED.

## 29) H1 emülatör workflow ek kontrolü — 25.11.2026
- `.github/workflows/h1-android-ui-smoke.yml` exact PR71 HEAD'de tekrar okundu.
- Job timeout 35 dakika; emulator API 34 / Pixel 6 / x86_64.
- Report upload adımı zaten `if: always()` kullanıyor; önceki QA-08 notundaki bu öneri uygulanmış durumda, açık eksik değil.
- Checkout `github.event.pull_request.head.sha` kullanıyor; PR #70 merge commit'i emulator testine dahil değil. Mevcut run bittikten sonra checkout `github.sha` (PR merge ref) olacak şekilde tek workflow değişikliği yapılacak.
- Ana `android.yml` içinde testDebugUnitTest ve assembleDebug iki ayrı Gradle çağrısı; birleştirme olası hız optimizasyonu olarak ayrı iş.

## 30) Android izin taraması düzeltmesi — 25.11.2026
- Tüm 21 ana Kotlin dosyasında permission API taraması yapıldı.
- CALL_PHONE ve READ_CONTACTS gerçekten kullanılıyor: Commands.kt satır 89'da ACTION_CALL/ACTION_DIAL, satır 92–94'te ContactsContract Phone.CONTENT_URI. Bu nedenle kaldırma kararı yok.
- Sorun zamanlama: MainActivity onCreate satır 140–145 ilk açılışta RECORD_AUDIO, CALL_PHONE, READ_CONTACTS ve POST_NOTIFICATIONS izinlerini birlikte istiyor. Bunlar sesli komut, arama ve kişi bulma özelliği ilk kullanıldığında ayrı ayrı istenmeli; POST_NOTIFICATIONS bildirim bağlamında istenmeli.
- BLUETOOTH_CONNECT zaten ses çıkış seçimi sırasında just-in-time isteniyor.
- Reminders.kt satır 85 `AlarmManager.setAlarmClock` çağırıyor. Android resmi belgeleri setAlarmClock için exact alarm izni gerektiğini söylüyor; `USE_EXACT_ALARM` otomatik verilir fakat esasen çalar saat/takvim uygulamaları için sınırlı. Jarvis'in ana işlevi alarm değilse Play policy riski doğar; kullanıcı kontrollü `SCHEDULE_EXACT_ALARM` veya inexact alarm/WorkManager alternatifi değerlendirilmelidir. [Android exact alarm policy](https://developer.android.com/about/versions/14/changes/schedule-exact-alarms).
- **KARAR:** İzinler kullanılmıyor diye silinmeyecek. Just-in-time izin akışı ve exact alarm ürün/policy değerlendirmesi ayrı güvenlik PR'ı.


## 31) H1 emülatör başarısızlık analizi ve merge-ref retesti — 25.11.2026
- Önceki run #45679523114 FAILURE; artifact 15055314040 XML tekrar sayımı 4 UI instrumentation test / 1 PASS / 3 FAIL / 0 error / 0 skipped. Geçen tek test SQLite last-good corrupt-primary recovery. Üç UI test aynı RootViewWithoutFocusException (window-focus=false).
- Logcat'te H1 Activity'de 177+ frame skip ve 2.5–3.2 sn renderer jank, Activity'nin test sürecinde PAUSED olması gözlendi. OnCreate içindeki senkron SQLite load + her açılışta koşulsuz save P2 risk olarak ayrı düzeltmeye yazıldı; focus hatasının tek nedeni olduğu kanıtlanmadı.
- Aynı logcat AndroidXTracer `Trace.forceEnableAppTracing()` NoSuchMethodError içeriyor. AndroidX Test issue #2248 monitor 1.7.1 patch çözümünü; resmi API dokümanı forceEnableAppTracing'in tracing 1.1.0 ile eklendiğini belirtiyor. App'e tracing 1.2.0 eklendi. [AndroidX Test issue](https://github.com/android/android-test/issues/2248) · [Trace API](https://developer.android.com/reference/androidx/tracing/Trace).
- İlk workflow kontrol koşusu #45685844306 yalnız eski PR API base.sha snapshot'ını yanlış karşılaştırdığı için testlere ulaşmadan fail oldu; uygulama testi koşmadı. Workflow kontrolü gerçek merge commit ebeveynlerini doğrulayacak şekilde düzeltildi.
- Yeni PR HEAD `5df909fb1d0893aa0fb308f33b5fb8064aec8811`; H1 run [#45685959506](https://github.com/aries919-bot/Jarvis/actions/runs/45685959506) ve APK/unit [#45685959491](https://github.com/aries919-bot/Jarvis/actions/runs/45685959491) IN_PROGRESS. H1 workflow verify step PASS; merge commit parent1 gerçek #70 merge `09f98b324a9a1f38dbbc25853ec2eba2fc544db9`, parent2 PR HEAD.
- Workflow `github.sha` merge-ref checkout, AOSP default API34 Pixel 2, dependencyInsight, test adımı 30 dk timeout, emülatör logu + XML/HTML raporları if: always() ile yükleniyor.
- PR #69, PR #71 ile 6 dosyada çakıştığı ve eski SharedPreferences mimarisini kullandığı için MERGE DIŞI tutuluyor. #71 geçmeden #69 kapatılmayacak.
- Kamu C1 sayımı değişmedi: 12 resmî kaynak /55 doğrulanmış açık kamu ilanı; 45 eksik. Worker production GET/D1, city kanıtlı dedup eksik. C1 RED.
- **KARAR:** H1 RED/beklemede; yeni emülatör sonucu ve gerçek telefon kontrolü olmadan #71 merge yok. Ayrıntı `gorevler/H1-EMULATOR-FAIL-ANALYSIS-2026-11-25.md`.


## 32) H1 yeni exact-head build kanıtı — 25.11.2026 19:34Z
- PR #71 HEAD `5df909fb1d0893aa0fb308f33b5fb8064aec8811`; PR diff GitHub API yeniden sayımı 10 dosya / +2.607 / -26. Test tanımı kaynaklardan 20 model +10 store +4 Android instrumentation =34.
- APK/unit [#45685959491](https://github.com/aries919-bot/Jarvis/actions/runs/45685959491) **SUCCESS**; birim test adımı ve APK derlemesi/upload tamamlandı.
- Merge-ref emulator [#45685959506](https://github.com/aries919-bot/Jarvis/actions/runs/45685959506): verify step #3 PASS (parent1 #70 merge `09f98b3...`, parent2 PR HEAD), AndroidX dependencyInsight PASS; instrumentation step #7 IN_PROGRESS.
- Önceki emulator #45679523114: FAILURE; 4 testten 1 PASS /3 FAIL /0 ERROR /0 SKIP. 3 UI test RootViewWithoutFocusException. Bu eski test sonucu düzelmiş sayılmaz; yeni emulator 4/4 PASS gelmeli.
- Workflow son revizyonu: `github.sha` PR merge-ref checkout, AOSP default API34 Pixel 2, tracing/monitor dependencyInsight, 30 dk emulator action timeout, log/XML/HTML report artifact if: always().
- Yeni #71 HEAD için static diff scan tekrar sayımı: 10 dosya; secret pattern 0; LinkedIn/Indeed URL 0; browser impersonation UA 0.
- **KARAR:** APK/unit PASS; emulator hâlâ RED/beklemede; telefon yok; PR #71 merge yok. PR #69 eski/duble SharedPreferences yaklaşımı nedeniyle merge dışı.


## 33) C1 H2 city alanı upstream kök neden — 25.11.2026
- H2 reader pinned SHA `fe37be93202442394504f455b4a1caeb31f298cf`; `career/h2_sources.json` gerçek JSON count: 19 source record / city-location alanı eksik 19.
- `scripts/h2_announcement_reader.py` `make_item()` signature + return JSON'unda city/location yok; `parse_official_notice()` de çıkarmıyor. Dolayısıyla Cloud `h2-input.js` upstream field olmadan city'yi kurtaramaz.
- Kurum merkez adresinden veya alan adı/başlıktan görev şehri çıkarılmayacak. Yalnız resmi sayfa/PDF'de pozisyon satırına bağlı açık işyeri şehri kullanılacak; birden çok şehir için ayrı kayıt veya location array. Yoksa null / possible duplicate / unique public count dışında.
- İş belgesi: `gorevler/C1-H2-CITY-FIELD-AUDIT-2026-11-25.md` commit `99055f2b5ecee19a2b3936443f8d9fefd375548c`. Kod değişmedi.
- **KARAR:** C1 RED; 12 kaynak /55 doğrulanmış açık kamu ilanı, 45 eksik. Şehirli cross-feed dedup yok, Worker GET/D1 SKIPPED.

## 34) H1 false-green düzeltmesi, asenkron SQLite ve Aries Career QA — 25.11.2026

### H1 PR #71 — eski emulator PASS geri çekildi
- Önceki H1 run [#45685959506](https://github.com/aries919-bot/Jarvis/actions/runs/45685959506) GitHub'da SUCCESS görünüyordu; ancak artifact `15057322360` içindeki `TEST-emulator-5554 - 14-_app-.xml` tekrar sayımı **4 test / 1 PASS / 3 FAIL / 0 ERROR / 0 SKIP**. Geçen yalnızca `sqliteLastGoodCopyRecoversCorruptPrimaryWithoutLosingPreviousRecord`.
- 3 UI test: `opensH1ActivityAndPersistsUnknownPublicationDateAsNull`, `evidenceReviewAndCorrectionFlowPersistsPublicationDateAndAuditDetails`, `displaysArchivedHumanReviewHistoryFromSqlite`; hepsi `RootViewPicker$RootViewWithoutFocusException` / `has-window-focus=false`.
- Logcat H1 Activity açılışında uzun frame skip/jank ve Android sistem süreçlerinde odak/kaynak baskısı gösteriyor. Önceki workflow scriptinde `set -o pipefail` vardı ama `set -e` yoktu; Gradle başarısız olsa bile action son komut exit code 0 verdiğinden job yeşil görünüyordu. PASS kararı **geri çekildi**.
- İş akışı `.github/workflows/h1-android-ui-smoke.yml` artık `set -euo pipefail` kullanıyor; gerçek instrumentation failure job'u başarısız yapmalı. Commit: `8d560a7d4e18db6fa6fdd1b131b47a831d6fd4bd`.
- H1 Activity ilk açılış SQLite `load` ve gereksiz koşulsuz `save` UI thread'deydi. `CareerNewsVerificationActivity.kt` asenkron açılış/yazma, tek FIFO ExecutorService, yalnız kayıt yoksa initial save ve kuyruğun sonunda storage close olacak şekilde düzeltildi. Commit: `b8a78126cbc6713cb4d42838027a6189274f19e7`.
- Güncel PR #71 HEAD `b8a78126cbc6713cb4d42838027a6189274f19e7`; GitHub diff yeniden sayımı 10 dosya / +2.669 / -26; test tanımları 20 model +10 store +4 instrumentation =34.
- Güncel APK/unit [#45692016951](https://github.com/aries919-bot/Jarvis/actions/runs/45692016951) **SUCCESS**; GitHub job steps arasında Birim testleri, APK derle ve upload hepsi SUCCESS.
- Güncel merge-ref emulator [#45692017824](https://github.com/aries919-bot/Jarvis/actions/runs/45692017824) son sorguda **IN_PROGRESS**; merge-ref parent doğrulaması ve AndroidX dependencyInsight SUCCESS; instrumentation step 7 çalışıyor. Artifact XML henüz yok.
- Gerçek telefonda test yok. **PR #71 RED/beklemede; 4/4 XML test sonucu ve telefon kontrolü olmadan merge yok.** PR #69 ortak 6 dosya/eski SharedPreferences mimarisi nedeniyle merge dışı.

### Aries İş & Kariyer QA/RAG — PR #12
- Cloud PR #12: https://github.com/aries919-bot/jarvis-cloud/pull/12; branch `feat/career-qa-rag-20261125`; son HEAD `d49de634b9e07030c0117039b62027b76405b435`; base PR #11 HEAD `b1415d6a76fd3856c60cfec891a575c3bef0341e`.
- GitHub diff son yeniden sayımı 4 dosya / +992 / -20; `tests/career-qa.test.mjs` içinde 24 adet test tanımı. Bunlar çalışmış test değildir.
- Retrieval/answer tasarımı: Turkish normalize + BM25 + Cloudflare BGE-M3 embedding + RRF; kaynak seçildikten sonra Gemma 4; tarih/şart/başvuru yolu deterministik kayıttan çıkarılır; kanıt/konu dışı ise abstain.
- Kaynak kapısı: Kamu/Kura/KPSS/KPSS'siz/Kamu bağlı şirket yalnız `iskur-esube-json` veya `h2-official-announcement`; Büyük özel yalnız `jobs.lever.co` Lever veya `jobs.ashbyhq.com` Ashby. Source stale, expired, unknown deadline veya possible duplicate kayıt dışı.
- Gizlilik: CV/özgeçmiş, kişisel KPSS puanı, T.C. kimlik, telefon, e-posta, bearer/API/token desenleri AI çağrısından önce reddedilir. Soru/Authorization loglanmaz; CV, kişisel profil, geçmiş sohbet veya e-Devlet verisi D1/AI'a gönderilmez. AI capacity `rejectIfBusy` ile hızlıca reddedilirse BM25 fallback.
- Cloudflare resmî model sayfaları: BGE-M3 https://developers.cloudflare.com/workers-ai/models/bge-m3/ — multilingual embeddings, $0.0118/1M input tokens; Gemma 4 https://developers.cloudflare.com/workers-ai/models/gemma-4-26b-a4b-it/ — multilingual answer model, $0.10/1M input and $0.30/1M output tokens; `rejectIfBusy` https://developers.cloudflare.com/workers-ai/features/reject-if-busy/. Gerçek maliyet/latency/kalite ölçümü henüz yapılmadı.
- CI engeli: son 7 Cloud run #45692460706, #45692517637, #45692608933, #45692642608, #45692792848, #45692810534, #45692919011 hepsi FAILURE; GitHub jobs API her birinde **0 step** ve 2–3 sn çalışma kaydı döndürdü. Örnek #45692919011/job `139482779972`: 20:13:48Z başladı, 20:13:50Z bitti, `steps=[]`. Log URL BlobNotFound; annotation içerik endpointi bağlı arayüzden alınamıyor. Bu bir kod testi failure/pass kanıtı değil; runner/job başlangıç engeli. PR #12 taslak, merge/deploy yok; tekrarlı rerun/commit döngüsü durduruldu.
- Rapor: `gorevler/ARIES-CAREER-QA-RAG-2026-11-25.md`, commit `71ddd4a722942113f748c1afc586891ef041ed56`.

### Ayrı takip — H1 News QA
- H1 yerel SQLite içindeki Claim/Evidence/ReviewHistory kayıtları için ayrı haber soru-cevap servisi tasarlanacak; Kariyer feed'i ile veri birleştirilmez. Yalnız kayıtlı iddia/kanıt/insan kararı, kaynak URL ve checkedAt ile cevap; doğrulama yoksa abstain. Henüz kodlanmadı.

### Kamu C1 kapısı değişmedi
- C1 PR #11 kamu sayımı **12 resmî kaynak /55 doğrulanmış açık ilan**; 100 hedefine 45 eksik. Worker production GET/D1 ve H2 city kanıtlı cross-feed dedup kapıları halen RED.
- QA/RAG PR #12 bu açığı ilan uydurarak doldurmaz.

**KARAR:** H1 emulator 4/4 tekrar kanıtı bekliyor ve fiziksel telefon yok; PR #71 merge yok. Career QA/RAG mimarisi kodlandı ancak CI runner adım başlatmadığı için test edilmedi, PR #12 merge/deploy yok. C1 12/55 ve RED.
## 35) Son QA/RAG HEAD ve exact-head engelleri — 25.11.2026 20:20Z

- Career QA/RAG Cloud PR #12 son HEAD 4b010df6fbd30229ec1868cf12e86a03b3eab055; GitHub diff 4 dosya / +1.045 / -20. Test dosyası kaynak sayımı 26 test tanımı. Bu testler çalıştırılmadı.
- 26 test tanımı artık: source-provider allowlist, Lever/Ashby host, sahte public provider red, stale izolasyonu, geçersiz deadline, Turkish BM25/synonyms, RRF, embedding response, deadline/conditions/application/count/location exact fields, null publishedAt/city, PII/CV/KPSS/token block, off-topic abstain, AI busy fallback, auth/body/stale feed.
- QA cevap sözleşmesinde positionCount ve city yalnız açıkça kayıtlıysa gösterilir; null/eksik city kurum adresinden/host'tan tahmin edilmez. Kamu provider sadece esube/H2; Büyük özel provider sadece resmi Lever/Ashby board hostları.
- Son 7 Cloud Actions run: #45692642608, #45692792848, #45692810534, #45692919011, #45692938639, #45693799901, #45693834527. API her run'da 1 job / 0 step / failure / 2–3s döndürüyor. En son #45693834527 job 139485957163: 20:19:25Z başladı, 20:19:27Z bitti. Log BlobNotFound. Kod testleri çalışmadı; CI runner/job startup engeli. PR #12 draft, merge/deploy yok.
- H1 yeni HEAD b8a78126cbc6713cb4d42838027a6189274f19e7; APK/unit [#45692016951](https://github.com/aries919-bot/Jarvis/actions/runs/45692016951) SUCCESS. Emulator [#45692017824](https://github.com/aries919-bot/Jarvis/actions/runs/45692017824) son kontrolde IN_PROGRESS; verify ve dependencyInsight PASS, instrumentation step beklemede/çalışıyor. Hata log URL BlobNotFound; artifact XML yok. H1 4/4 sonucu yok, telefon testi yok, PR #71 merge yok.
- H1 önceki run #45685959506'in sahte PASS düzeltmesi bölüm 34'te geçerlidir: XML 4/1 pass/3 fail; set -e düzeltmesi ve asenkron SQLite commitleri uygulandı.
- Güncel C1 sayımı 12 kamu kaynak /55 doğrulanmış açık ilan /45 eksik; production Worker GET/D1 ve city dedup RED.
**KARAR:** H1 emulator 4/4 ve telefon testini bekle; Cloud runner adım başlatma engeli çözülene dek PR #12 taslak/RED. C1 kapıları değişmedi.

## 36) QA/RAG exact-head son durum — 25.11.2026 20:22Z
- Cloud PR #12 HEAD a80e557d1dc8373446a3a41b15d50f998d5e3e46; GitHub diff 4 dosya / +1043 / -20; 26 test tanımı, çalıştırılan test 0.
- Son yedi run #45692792848, #45692810534, #45692919011, #45692938639, #45693799901, #45693834527, #45694369322; hepsinde 1 job / 0 step / failure / 1–3 saniye. En son #45694369322 job 139487790097 20:22:41Z–20:22:42Z.
- Son kod düzeltmeleri: provider allowlist (public esube/H2, Büyük özel Lever/Ashby), PII block, off-topic abstain, AI rejectIfBusy + BM25 fallback, deterministic positionCount/city extraction. Null positionCount/city açıkça belirtilmemiş; şehir kurum merkezinden çıkarılmaz.
- Test verilerindeki Bearer örneği fake şeklinde kısaltıldı; gerçek/token biçimine benzeyen test fixture kaldırıldı.
- CI logu BlobNotFound; test adımı hiç başlamadı. PR #12 draft, merge/deploy yok. Yeni commit/rerun döngüsü durduruldu.
- Ayrıntı `gorevler/ARIES-CAREER-QA-RAG-2026-11-25.md`; son rapor commiti 6ba88e0992e370733d0ce303621d5047d4eb4c8b.

## 37) H1 Haber QA ve C1/Worker QA güncel notu — 25.11.2026
- H1 PR #71 current HEAD `0eaaa74153dd3eacdd66c701b9727c2b8978983f`; yeni yerel Haber QA `CareerNewsQuestionAnswer.kt` + Activity'de soru düğmesi + 17 JUnit testi.
- Haber QA cihaz içi deterministic; LLM/ağ/Worker/kariyer feed çağrısı yok. Sadece aynı haberin claim/evidence/stance/sourceType/reviewHistory/correctionHistory ve ayrı yayın/toplama/ilk görülme zamanlarından cevap verir. PII soruları abstain; belirsiz çoklu claim sorusu clarification.
- İlk exact-head run #45698403489: 142 total /138 pass /4 fail. Hata sınıfları: ilk görülme test beklentisi yanlış, asıl kaynağı Türkçe çekimi eksik, aynı URL'deki ayrı kanıt tekilleme hatası, CONTEXT_MISSING açıklamasında açık etiket eksik. Fix commit `0eaaa741...`; QA test tanımı 14 → 17.
- Yeni APK/unit run #45699134292 ve merge-ref emulator #45699134296 son sorguda IN_PROGRESS. Eski b8a7812 exact-head CI PASS yeni QA kodunu kapsamaz. Güncel sonuçlar gelmeden H1 ONAY/merge yok.
- PR #71 son diff: 12 file / +3.308 / -26. Secret/PII-token pattern 0; LinkedIn/Indeed URL 0; fake browser UA 0. Diff, PR dosyaları listesinden yeniden sayıldı.
- H1 ilk açılış SQLite I/O P2 işi PR #71 içinde daha önce düzeltildi: tek FIFO executor ile async load; mevcut kayıtta no-op save; close işi queue sonuna alındı. Ayrıntı `gorevler/H1-SQLITE-ASYNC-OPEN-2026-11-25.md`.
- Cloud QA/RAG PR #12: runner-start engeli nedeniyle test adımı çalışmıyor (private repo 0-step job); yerel pure QA 18/18 ve route 7/7 sonuçları ayrı notlandı. PR #12 taslak, merge/deploy yok.
- C1 kamu kapısı değişmedi: 12 resmî kaynak /55 doğrulanmış açık ilan /45 eksik; Worker production GET/D1 ve city/dedup halen RED.
- Haber QA raporu: `gorevler/H1-NEWS-QA-2026-11-25.md`.
**KARAR:** H1 yeni exact-head CI beklemede, gerçek telefon testi zorunlu; C1 RED; QA/RAG backend private Worker PR #12 merge/deploy yok.


## 38. 26.11.2026 — H1 false-green CI denetimi ve C1 kaynak ekleri


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


## 26.11.2026 — H1 framework readiness araştırması
- FUTO Notes CI vakası, Android system_server sürecinin sys.boot_completed=1 sonrasında yeniden başlayabildiğini ve boot_progress_enable_screen olayının güncel PID ile eşleştirilmesi gerektiğini gösteriyor: https://gitlab.futo.org/futo-notes/futo-notes/-/blob/papercuts-bridge-guidance-close-round10/scripts/ci-android-sync-leg.test.mjs
- PR #71 runner düzeltmeleri: 29aaa17e532c13a394d1ed7dd7c53bc613d0bec9, fail-closed düzeltmesi e390e05e1485717c3a07ce3af06db6b3840062f3.
- Yerel mock kontrolleri: 1 pozitif exit 0; 3 negatif (Package Manager yok, PID marker eski, paket listesi boş) exit 21. Bash syntax PASS. Bunlar cihaz/emulator testi değildir.
- Exact HEAD e390e05: emulator [#45712459718](https://github.com/aries919-bot/Jarvis/actions/runs/45712459718) ve APK [#45712459742](https://github.com/aries919-bot/Jarvis/actions/runs/45712459742) henüz IN_PROGRESS. H1 XML 4/4 kanıtı yok.
- Önceki HEAD APK run #45711684204 SUCCESS; emulator #45711684280 sonraki commit nedeniyle CANCELLED. Aradaki HEAD 29aaa CI koşuları da yeni commit nedeniyle iptal edildi; bu koşular test sonucu olarak sayılmaz.
- C1: 6 kaynak /47 last-good kamu ilanı; 6 kaynak stale; eşik 10/100 ve production GET/D1 yok.

**KARAR:** H1 RED/BLOCKED — current exact-head CI bekleniyor. C1 RED. Merge/deploy yok.

## 26.11.2026 — H1 exact-head APK PASS, emulator bekliyor
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


## 26.11.2026 — C1 resmî kaynak snapshot ve H1 son CI kontrolü
- Ayrıntılı kaynak/ilan başvuru koşulu ve resmî link kaydı: [gorevler/C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md](../gorevler/C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md). GitHub commit: 085330c3379c6541f6b440e97328b4e6899186d6; dosya API'den tekrar okundu ve SHA d3312ce594e4b9fcade5d7bf7939afcbc784503a ile doğrulandı.
- Yeni kontrol edilen aktif başvuru adayları: 6 ana duyuru (Sahil Güvenlik, HMB, SSB, Bartın, AYBÜ, MCBÜ). Başvurusu ileride açılacak SGK ve Kültür-Turizm Bakanlığı duyuruları 2 ayrı gelecek adaydır. Bu 8 kayıt Worker'a ingest edilmedi; mevcut sayaçlara eklenmedi. MCBÜ portal alt-kadro granülerliği ve exact dedup çözülmeden ilan adedi artırılmaz.
- 26.11.2026 Resmî Gazete dizinindeki diğer kurum başlıkları yalnız aday liste olarak tutuldu; bireysel ilan PDF'si ve şartlar doğrulanamadığı için sayaca eklenmedi.
- Kamu sayımı: Worker/H2 last-good 6 kaynak /47 kart (12 saatten eski; stale); ayrı manuel proje envanteri 12 kaynak /55 kart. Bunlar toplanmaz. Kamu 0 değildir; bugünün fresh sayımı diye sunulamaz. 10 kaynak /100 doğrulanmış açık kart eşiği hâlâ RED.
- C1 Worker authenticated production GET/D1, firstSeenAt üretim kalıcılığı ve bütünleşik cross-feed dedup kanıtı yok. PR #11/#12 draft; merge/deploy yok.
- H1 PR #71 HEAD e390e05e1485717c3a07ce3af06db6b3840062f3: APK/unit [#45712459742](https://github.com/aries919-bot/Jarvis/actions/runs/45712459742) SUCCESS. Emulator [#45712459718](https://github.com/aries919-bot/Jarvis/actions/runs/45712459718) son kontrolde IN_PROGRESS; job #139550204961, adım 1–6 SUCCESS, instrumentation adımı 7 IN_PROGRESS, XML adımı 8/artefact adımı 9 PENDING, artefact listesi boş. Exact XML 4 test/0 failure/0 error/0 skipped görülmeden H1 PASS yok. Aktif run bitmeden workflow path-filter dosyasına dokunulmaz.
**KARAR:** C1 RED; H1 APK/unit PASS ama instrumentation BLOCKED/IN_PROGRESS; merge/deploy yok.


## 26.11.2026 — MEB eski başvuruları ve Resmî Gazete yeni adayları
- MEB e-Olgunlaşma resmî sayfalarından 3 ayrı usta öğretici duyurusu doğrulandı: Mardin Görsel Sanatlar 1 (deadline 20.11.2026; sınav 26.11), Mardin Veritabanı Programcılığı 1 (deadline 20.11; sınav 24.11), Kahramanmaraş Türk Halk Bilimi 1 (deadline 25.11; sınav 26.11). Her üçünün publishedAt alanı sayfada bulunmadığından null. Toplam 3 duyuru /2 kurum; 3'ü de Süresi geçti, güncel açık sayımına 0 eklenir. Resmî sayfalar görev notunda.
- 26.11.2026 Resmî Gazete 33442 index 18 civarı kurum başlığı listeliyor. Günlük PDF web katmanında açılamadı. İkincil haberde SUEN, Özburun Belediyesi, KSÜ ve Mersin Üniversitesi için alım adayları veriliyor; KSÜ kadro sayısı haberde 42/54 çelişkili. SUEN kurum duyuru listesinde en yeni personel girdileri Ağustos/Temmuz; Mersin PDB'de 26.11 duyurusu bulunamadı; KSÜ başvuru portalı 502. Bu kayıtlar Doğrulanamadı aday listesi; resmî PDF teyidi olmadan source/ilan sayacına eklenmez.
- Görev notu güncellendi: [C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md](../gorevler/C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md), commit 8505fa98d375645ed0f8ab54f024ebdc83ce5699.
**KARAR:** Kamu MEB expired duyuruları current=0; Worker 6/47 stale, manuel 12/55 ayrı; 10/100 RED. H1 emulator run #45712459718 son kontrolde IN_PROGRESS; APK/unit #45712459742 SUCCESS; emulator XML yok, H1 instrumentation PASS değil.


## 26.11.2026 — 23 Kasım Resmî Gazete ikinci PDF turu
- Ayrıntılı kayıt: [gorevler/C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md](../gorevler/C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md), commit 2e02e316eed128170fdef23cb02a7fdf386ea91e.
- Resmî Gazete 33439 PDF metninden 3 yeni tarih/şartı net kamu duyurusu daha doğrulandı: TÜRKAK 1 duyuru/3 güvenlik görevlisi (deadline 07.12.2026 23:59; KPSS P93≥70); Yozgat Bozok 1 duyuru/7 akademik kadro (deadline 11.12.2026; KPSS'siz akademik); Düzce'nin ayrı Öğretim Üyesi başlığı 1 ek duyuru/4 kadro (deadline 07.12.2026). Bunlar Worker ingest/dedup sonrası sayaca alınabilir; şu an yalnız görev kaydıdır. Düzce'nin daha önce H2'deki 5 kadrolu Öğretim Elemanı duyurusu ayrı kalır; Tarsus 3 kadrolu 07.12.2026 duyurusu H2 ile eşleşti ve tekrar sayılmadı.
- İçişleri Bakanlığı 4/B duyurusu tek ana karttır: 119 merkez +1.051 taşra =1.170 pozisyon. publishedAt 23.11.2026; applicationOpensAt 07.12.2026; deadline 11.12.2026 16:00; KPSS P3/P93/P94 en az 60. Başvuru henüz açılmadığı için current open sayısına 0; 1.170 ilan diye sayılmaz.
- Kamu sayacı belirsiz kayıtları ayrı: Kütahya Dumlupınar 15 günlük süreyi açık son tarihe dönüştürmeden deadline=null; Ondokuz Mayıs Üniversitesi 21 sürekli işçi için e-Şube ilan yayın tarihi bilinmeden deadline=null, e-Şube detayına kazıma yok. Her ikisi doğrulanmış açık sayısına 0.
- Özel üniversite başvuruları ayrı havuz: Antalya Belek 1 duyuru/6 akademik pozisyon, deadline 07.12; Özyeğin 1 duyuru, deadline 08.12 17:00; Yaşar 1 duyuru fakat exact deadline teyidi yok. Hiçbiri kamu kaynağı sayımına eklenmez.
- H2 exact dedup: Düzce Öğretim Elemanı 5 kadro ve Tarsus Öğretim Görevlisi 3 kadro mevcut H2 kartlarıyla eşleşti; yeniden sayılmadı. Düzce ayrı Öğretim Üyesi 4 kadro ana duyurusu yeni aday.
- PDF screenshot isteği dosya boyutu 41,936,566 byte nedeniyle 400 döndü; metin PDF'si analiz edildi, görsel screenshot incelemesi tamamlanmadı.
**KARAR:** Kamuya 3 yeni tarih/doğrulama koşulunu sağlayan aday duyuru (2 yeni kurum kaynağı) bulundu. Worker feed ve production GET/D1'e bağlanmadılar; current counts still last-good 6/47 stale, separate manual 12/55. 10/100 RED.

## 26.11.2026 — H1 emulator exact-head sonucu: FAILURE
- Önceki #45712459718 in_progress durumu kapandı. Güncel run: https://github.com/aries919-bot/Jarvis/actions/runs/45712459718 — FAILURE.
- XML artefact #15069268534 yeniden okundu: 4 test /1 PASS /3 FAIL /0 error /0 skipped. Üç UI testinin her biri Espresso RootViewWithoutFocusException (has-window-focus=false); SQLite last-good recovery testi PASS. XML gate 4/4 pass şartını doğru şekilde reddetti.
- APK/unit aynı HEAD run #45712459742 SUCCESS; bu sonuç emülatör instrumentation yerine geçmez.
- Tanı: emulator online, boot_completed=1, system_server PID 552 sabit, Package service bulundu, 7.0G data boş. WindowManager test EmptyActivity'yi öne alıp Jarvis task'ını arkaya taşırken focus kaydı ve system_server slow dispatch/monitor contention görüldü. Muhtemel focus handoff/timing yarışı; tek kök neden kesinleşmedi.
- Google AndroidX 18.09.2026 düzeltmesi aynı ActivityScenario/EmptyActivity geçişlerinden sonra focus kaybını kaydeder ve odak istemeyen UI-thread View.performClick yaklaşımı kullanır: https://android.googlesource.com/platform/frameworks/support/+/36c1b8edfc954907a546e09c7f42e20d39ecb083. Benzerlik vardır, aynı Aries kök nedeni olduğu kanıtlanmış değildir.
- Ayrıntılı görev/artefact ve sonraki test-only planı: [H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md](../gorevler/H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md).
**KARAR:** H1 RED; 4/4 testi geçmedi. Aynı head kör rerun yok; test atlama/XML gate gevşetme yok; PR #71 merge/deploy yok.

## 26.11.2026 — H1 Espresso focus kök neden ve CI düzeltmesi
- Exact HEAD `e390e05...` emulator [#45712459718](https://github.com/aries919-bot/Jarvis/actions/runs/45712459718): XML tests=4, failures=3, errors=0, skipped=0. 1 SQLite last-good testi geçti; 3 Activity/Espresso testi `RootViewWithoutFocusException` (root has-window-focus=false) ile düştü.
- System_server PID 552 + Package Manager preflight geçti; bu kez PM Broken pipe değil. Artifact loglarında Nexus Launcher top-resumed/pause timeout ve sistem ANR kayıtları mevcut; failure anında dumpsys window alınmadığı için Aries'te ANR dialogu kesin teşhis değil.
- Benzer API34 Pixel/Google APIs CI vakasında Pixel Launcher ANR dialogunun Espresso focus'u tuttuğu, APK build sonrası `CLOSE_SYSTEM_DIALOGS + HOME` ile kapatılınca testlerin değişmeden geçtiği raporlanmış: https://github.com/RizzoG99/PersonalFinanceTracker/issues/372 ve fix PR https://github.com/RizzoG99/PersonalFinanceTracker/pull/373.
- Aries runner commit `b51c53a5797c19d4e18ed81b679024fc14fa3723`: önce app/androidTest APK build, sonra ANR/Application Error focus preflight; yalnız dialog odaktaysa kapat; kalırsa fail-closed exit 22. Window/activity/process diagnostics eklendi. Espresso testleri değiştirilmedi.
- Yerel mock: temiz focus PASS; ANR kapanınca PASS; kapanmayan ANR RED/exit22; 3/3. Bash syntax PASS. Emulator PASS değildir.
- Exact current HEAD emulator [#45716694004](https://github.com/aries919-bot/Jarvis/actions/runs/45716694004) ve APK [#45716694105](https://github.com/aries919-bot/Jarvis/actions/runs/45716694105) IN_PROGRESS.
- C1: 6 kaynak/47 last-good kamu kartı; 6 stale; 10/100 ve production GET/D1 eksik. RED.

**KARAR:** H1 RED/BLOCKED — yeni exact-head CI bekleniyor. C1 RED. Merge/deploy yok.

## 26.11.2026 — H1 yeni HEAD: emulator launcher-focus preflight
- PR #71 head b51c53a5797c19d4e18ed81b679024fc14fa3723; önceki exact-head e390e05 koşusu #45712459718 FAILURE olarak kalır (4 test /1 pass /3 focus failure /0 error /0 skipped).
- Eski logcat'te NexusLauncherActivity için top-resumed state loss timeout ve Activity pause timeout kayıtları saptandı. Kesin ANR dialog kanıtı eski artefact'te yok; bu yüzden kök neden halen hipotezdir.
- Yeni PR commit'i scripts/run-h1-emulator-tests.sh içine window/activity/process dumpsys ve instrumentation öncesi Application Not Responding/Application Error focus preflight/recovery ekledi. Espresso testleri ve 4/4 XML gate aynen duruyor; test atlama veya beklenti düşürme yok.
- Yeni exact-head emulator #45716694004 IN_PROGRESS, job #139564450544 step 1–6 SUCCESS, step7 preflight/instrumentation running; XML/artefact henüz yok: https://github.com/aries919-bot/Jarvis/actions/runs/45716694004
- Yeni APK/unit #45716694105 IN_PROGRESS, unit step SUCCESS, APK build running: https://github.com/aries919-bot/Jarvis/actions/runs/45716694105
- Ayrıntılı test-kök neden kaydı: [H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md](../gorevler/H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md).
**KARAR:** H1 RED/beklemede; yeni head için exact XML 4/4 PASS yok. PR #71 merge/deploy yok.

## 26.11.2026 — Kamu kaynak adayları: 10 kurum / 10 duyuru
- Resmî kurum araştırması ayrı görev dosyasına yazıldı: [gorevler/C1-KAMU-KAYNAK-ADAYLARI-2026-11-26.md](../gorevler/C1-KAMU-KAYNAK-ADAYLARI-2026-11-26.md).
- Adaylar: Sahil Güvenlik (10 pozisyon), Bursa Teknik Üniversitesi (1), YÖKAK (5), Adli Tıp Kurumu (181), Anadolu Üniversitesi (1), Zafer Kalkınma Ajansı (2), ORAN (7), Dicle Kalkınma Ajansı (4), Bartın Üniversitesi (1), TGA (13). Toplam 10 farklı kurum / 10 duyuru kartı / 225 pozisyon; pozisyon sayısı ilan kartı sayısı değildir.
- Her kurum için kaynak başına 1 duyuru kartı. Resmî kaynak sayfaları ve başvuru sonları görev dosyasında; başvuru hedefleri kazınmaz.
- Sayıştay (16 pozisyon, son başvuru 25.11.2026), OKA (5, son başvuru 22.11.2026), MEVKA (1, son başvuru 13.11.2026) geçmiş tarihli olduğu için aktif sayaç dışında bırakıldı.
- Bu manuel keşif Worker'a aktarılmış değildir: gerçek feed ölçümü hâlâ 6 kaynak /47 last-good kart, 6/6 stale, 0 fresh; production authenticated GET/D1 yok. Kamu eşiği 10 kaynak/100 doğrulanmış ilan: pipeline ölçümünde RED; manuel aday listesinde 10 kaynak ama yalnız 10 kart (90 eksik).

**KARAR:** Kaynak keşfi 10 kuruma genişletildi; canlı feed/100 kart kanıtı yok, kamu C1 RED.


## 26.11.2026 — H1 latest HEAD APK result
- [APK/unit CI #45716694105](https://github.com/aries919-bot/Jarvis/actions/runs/45716694105) HEAD `b51c53a5797c19d4e18ed81b679024fc14fa3723` SUCCESS; unit tests + APK build + artifact upload tamamlandı. Artifact ID 15069888409, 14,198,084 byte, sha256 `8f98ae33b42003beb70851e5b596e0b6354f7d24559b9585219e1f0ae46fbb79`.
- Exact-head emulator #45716694004 IN_PROGRESS; instrumentation XML henüz yok. H1 kabul yok.

## 26.11.2026 — b51c53a APK/unit başarı; emülatör beklemede
- APK/unit run #45716694105 exact HEAD b51c53a5797c19d4e18ed81b679024fc14fa3723 üzerinde SUCCESS: https://github.com/aries919-bot/Jarvis/actions/runs/45716694105.
- APK artifact #15069888409. İçindeki app-debug.apk 14,960,717 byte; SHA-256 96ba976ec3f341d51d403633246e96b74c90228de64ac270b9a96058e3d925ab. Debug APK kullanıcı telefonu kontrolü için görev notuna ayrıca bağlandı.
- Emulator #45716694004 hâlâ IN_PROGRESS; job #139564450544 step7; XML/artefact henüz yok. Önceki #45712459718 FAILURE geçerliliğini korur; yeni koşu onun üstünü örtmez.
**KARAR:** APK/unit PASS; H1 emulator RED/beklemede. Yeni exact-head 4/4 XML PASS ve cihaz QA olmadan PR #71 merge/deploy yok.

## 26.11.2026 — H2 canlı ölçüm ve C1 sayacının doğru ayrımı
- H2 PR #61 exact HEAD `fe37be93202442394504f455b4a1caeb31f298cf`; [live reader run #45646698179](https://github.com/aries919-bot/Jarvis/actions/runs/45646698179) SUCCESS, artifact [15037029949](https://github.com/aries919-bot/Jarvis/actions/runs/45646698179/artifacts/15037029949), `collectedAt=2026-11-25T15:34:38Z`.
- H2 ölçümü: 19 duyuru kaydı /17 fresh /2 stale; 11 benzersiz kurum /13 doğrulanmış açık ilan kartı /125 pozisyon. 125 pozisyon, 100 ilan kartı şartının yerine geçmez.
- Kaynak başına açık kart: Anadolu 1; Atatürk 2; Balıkesir 1; DOKA 1; Düzce 1; Mardin Artuklu 1; Marmara 2; ORAN 1; Tarsus 1; TGA 1; Zafer 1. Toplam 13.
- Stale: BOTAŞ (timeout; last-good korunur), Mardin Artuklu öğretim elemanı (açık başvuru kanıtı yok). Açık sayaca alınmayanlar: ATK başvurusu 30.11.2026 09:00'da açılır; iki Munzur ilanı 25.11 deadline'ını geçti; YHGM eski kura/sonuç kaydıdır. Balıkesir ve TGA için publishedAt=null; collectedAt yayın tarihi yerine kullanılmadı.
- H2 PR #61 test kanıtları: H2 60/60, Python script suite 161/161, K1-B 24/24; K1-B 28 URL =12 çalışıyor /16 engelli /0 ölü. URL sayısı ilan sayısı değildir. PR #61 hâlâ draft/açık ve RED; 100 kart, cross-feed dedup ve production GET/D1 eksik.
- e-Şube ayrı ölçüm: Jarvis `durum` SHA `b9a967b0960575f8696c9c0814b97f1aa54a7035`, `collectedAt=2026-11-25T13:49:08Z`, 42 satır /42 benzersiz ilan_no /7 işveren; 42 kaydın deadline'ı 27.11.2026, 26.11 itibarıyla 0 expired. e-Şube 1 source /42 record olarak ayrı kalır.
- H2 + e-Şube teorik toplamı çapraz-feed dedup olmadan en çok 55 karttır; bu birleşik feed sayımı değildir ve 100 hedefinden en az 45 kart eksiktir. H2 11 source eşiğini tek başına geçer; kart eşiği geçmez.
- Cloud PR #12 validation [run #45697424481](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45697424481) runner_id=0, steps yok, 2 saniyede failure: kod/test adımı başlamadı. Bu nedenle H2/e-Şube verisi Worker'a yayımlanmadı. Worker last-good snapshot hâlâ 6 source /47 kart, 6/6 stale; production authenticated GET/D1 yok.
- Manuel kamu aday dosyası 11 kurum/11 duyuru/226 pozisyon; 6 kurum H2 config'te zaten var, yalnız 5'i yeni ek aday. H2 config 19 kayıt/15 kurumdur; config sayısı fresh ölçüm sayısı değildir.
- Zafer kaydında fetch `url` DOKA domaini id=1222, `officialUrl` Zafer domaini; H2 live run Zafer'i fresh saymış. Bu nedenle kanıtlanmış hata değil, kaynak provenance kontrolü: [görev](../gorevler/C1-H2-ZAFER-URL-PROVENANCE-2026-11-26.md).

**KARAR:** H2 live 11 source/13 card PASS (H2 katmanı); e-Şube 1/42 ayrı; C1 Worker/production RED, 100 kart yok, merge/deploy yok.


## 26.11.2026 — Cloud runner admission: no-code failure
- Cloud PR #12 run [#45697424481](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45697424481), job 139498357281: `runner_id=0`, empty runner name, zero steps, ~2 seconds; logs 404 BlobNotFound. Workflow uses `ubuntu-latest`; checkout never started. This is pre-runner admission, not a test failure; exact cause UNKNOWN.
- Cross-repository GitHub issue search found same fingerprint, including zero-dependency `echo` smoke failures; evidence supports runner admission/dispatch class, not a specific billing or account diagnosis.
- Owner-side check task: [gorevler/C1-CLOUD-RUNNER-ADMISSION-2026-11-26.md](../gorevler/C1-CLOUD-RUNNER-ADMISSION-2026-11-26.md). Check Actions policy, hosted runner eligibility, org runner restrictions and usage limits before one minimal smoke test. No repeated code reruns.
- PR validation skips live source ingestion by design; scheduled/push runs perform upstream e-Şube clone, H2 live read and Worker authenticated GET/D1. No successful scheduled refresh/publish evidence.

**KARAR:** Cloud C1 runner admission BLOCKED; no production feed update, no merge/deploy.


## 26.11.2026 — 20 Kasım Resmî Gazete ek araştırması / C1 yeniden sayım
- Ayrıntılı görev kaydı: [C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md](../gorevler/C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md), yeni bölüm commit `765f56d5ae2aaf8f880b35d73b3b8683fb614005`.
- 20.11.2026 Resmî Gazete 33436 ve kurumların kendi duyuru sayfalarından 10 yeni kamu ana duyurusu doğrulandı: Atatürk 1, Mardin Artuklu 2, Necmettin Erbakan 1, Abdullah Gül Öğretim Elemanı 1, Bartın akademik 2, Balıkesir 2, İstanbul Üniversitesi-Cerrahpaşa Öğretim Elemanı 1. Bartın 4/B mevcut aday olduğundan tekrar sayılmadı. Aynı kurum içindeki farklı akademik başlıklar ayrı kart, alt kadro/pozisyonlar kart adedinden ayrı tutuldu.
- Yeni güncel elle doğrulanmış kamu aday toplamı: ilk küme 6 + 23 Kasım RG yeni 3 + 20 Kasım RG yeni 10 = **19 ana duyuru /15 kurum**. Bu manuel araştırma toplamıdır; Worker/H2'ye ingest edilmedi. `collectedAt=null`, `firstSeenAt=null`.
- Kamu future/opening-soon: 4 duyuru (SGK, Kültür ve Turizm Bakanlığı, İçişleri Bakanlığı 1.170 pozisyonu tek ana kart, Adli Tıp 181 pozisyonu tek ana kart). Kamu `deadline=null`/doğrulanamadı: 4 (AGÜ Öğretim Üyesi, İÜ-Cerrahpaşa Öğretim Üyesi, Kütahya Dumlupınar, OMÜ). Kamu expired: MEB 3 usta öğretici duyurusu + Sağlık Bakanlığı 3. Dönem atama kurası 1 kayıt; hiçbirisi current open'e eklenmez.
- Sağlık Bakanlığı'nın 25.11.2026 sayfası yeni ilan değil: 30.10.2026'da başlayan 2026/3 İlk Defa ve Yeniden Atama Kurası başvurusu 05.11.2026 18:00'de kapanmıştır; 25 Kasım sayfası itiraz sonucu ve 26 Kasım 10:30 kura saatini ilan eder. Münhal kadro pozisyon toplamı bu notta ayrıca sayılmadı, null tutuldu. Resmî sayfa: https://yhgm.saglik.gov.tr/TR-121745/2026-yili-3-donem-ilk-defa-ve-yeniden-atama-kurasi.html.
- Adli Tıp Kurumu 20.11.2026: 181 adet 4/B pozisyon tek duyurudur; başvuru 30.11.2026 09:00–06.12.2026 23:59; KPSS ilgili P3/P93/P94 en az 70. 26 Kasım itibarıyla başvuru future olduğu için açık sayısı 0. Resmî kurum metni: https://atk.gov.tr/haber20.11.26.html.
- 20 Kasım RG PDF 43,377,793 byte nedeniyle screenshot aracı 400/timeout döndürdü; metin katmanı + kurum sayfaları incelendi, görsel PDF QA tamamlanmadı.
- Özel üniversite adayları ayrı sayaçta: Belek, Yaşar, Özyeğin, Beykoz ve Hasan Kalyoncu; kamu 19/15 sayısına dahil değil. 26 Kasım RG indexindeki bireysel PDF'si doğrulanamayan SUEN/Özburun/KSÜ/Mersin vb. başlıklar `Doğrulanamadı`, açık sayaca eklenmedi.
**KARAR:** Kamu sıfır değil; manuel araştırma 19/15. Worker/H2 son iyi 6/47 hâlâ stale; eski manuel envanter 12/55 ayrı; üç katman toplanmaz. 10 kaynak/100 doğrulanmış ilan ve production authenticated GET/D1 kanıtı olmadığından C1 RED.

## 26.11.2026 — H1 yeni HEAD test durumu
- PR #71 HEAD `b51c53a5797c19d4e18ed81b679024fc14fa3723`. APK/unit [#45716694105](https://github.com/aries919-bot/Jarvis/actions/runs/45716694105) SUCCESS; artifact #15069888409, APK SHA-256 `96ba976ec3f341d51d403633246e96b74c90228de64ac270b9a96058e3d925ab`.
- Exact-head emulator [#45716694004](https://github.com/aries919-bot/Jarvis/actions/runs/45716694004) son sorguda IN_PROGRESS; job #139564450544, adım 1–6 SUCCESS, step7 instrumentation/preflight running, XML/artifact henüz yok. Önceki #45712459718 FAILURE: XML 4 test/1 pass/3 fail/0 error/0 skipped.
- Yeni script launcher ANR/window-focus preflight ve dumpsys tanısı eklemiştir; yeni sonuç gelmeden H1 ONAY/merge yok. PR #71 draft/open; merge/deploy yapılmadı.

## 26.11.2026 — 21–25 Kasım RG denetimi ve C1 sayım düzeltmesi
- Ayrıntılı kanıt: [C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md](../gorevler/C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md), commit `eccaed393d3bf6569cd4d377ece87c48e80d9812`.
- 24.11.2026 Resmî Gazete'de 4 yeni kamu akademik duyurusu doğrulandı: Marmara Üniversitesi Öğretim Elemanı 1; Marmara Öğretim Üyesi 1; ÇOMÜ Öğretim Üyesi 1; ÇOMÜ Öğretim Elemanı 1. Her biri ayrı ana duyuru, başvuru sonu 08.12.2026. Bu iki yeni kurumla aktif kamu sayımı **23 ana duyuru /17 kurum** oldu. Marmara'nın iki ilanı https://basvuru.marmara.edu.tr/personel; ÇOMÜ https://ilan.comu.edu.tr/ilan_bilgi/56 ve https://ilan.comu.edu.tr/ilan_bilgi/57.
- 21.11.2026 İstanbul Medeniyet Üniversitesi Akademik İlanı kurum arşivinde görünüyor; Resmî Gazete PDF bağlantısı timeout, kurumsal sayfada exact deadline yok. 1 kamu aday kaydı `deadline=null`, açık/verified 23 sayısına dahil değil.
- 25.11.2026 Resmî Gazete ek taraması yeni kamu personel alımı eklemedi: Biruni'nin 13.11 ilanı iptal; CTE, Çevre, MEB, MSB ve Marmara kayıtları ihale yasaklama; MCBÜ mevcut kayıt, tekrar sayılmadı. Çağ Üniversitesi 1 özel akademik duyuru/2 araştırma görevlisi, `publishedAt=25.11`, `deadline=09.12`, özel havuzda. Biruni iptal kartı aktiften kaldırılmalı.
- 20.11 RG dizininde görünen Orman GM Müfettiş Yardımcısı ilanı gerçekte 23.10.2026 yayımlı, 09.11.2026 23:59 başvurusu kapanmış 12 kadrolu tek kamu duyurusudur; `Süresi geçti`. 23.11 sınava hak kazanan listesi işe alım ilanı değildir.
- Yeni toplam: **23 açık kamu ana duyuru /17 benzersiz kamu kurumu**; 4 gelecek, 5 deadline-null, 5 süresi geçmiş kamu kaydı ayrı. 7 özel üniversite aday/duyurusu özel havuzda; kamuya karıştırılmaz.
- Resmî Gazete 24.11 günlük PDF screenshot 400/timeout; metin kaydı ve resmi başvuru portalları doğrulandı. 21.11 Medeniyet PDF bağlantısı da timeout. Bunlar `Doğrulanamadı` / deadline null olarak tutuldu.
**KARAR:** C1 RED. 23/17 sayısı manuel kaynak denetimidir, Worker feed'i değil. Worker/H2 6/47 son iyi veri stale; manuel envanter 12/55 ayrı. Sayılar toplanmaz. 100 doğrulanmış aktif ilan ve production authenticated GET/D1 kapısı yok.

## 26.11.2026 — Nişantaşı özel akademik ilanı doğrulama düzeltmesi
- İstanbul Nişantaşı Üniversitesi'nin 24.11.2026 ilanı artık `Doğrulandı`: Resmî 9 sayfalık PDF, kurum duyurusu ve İK portalı eşleşti. Tek ana duyuru kartıdır; profesör/doçent/dr. öğretim üyesi/öğretim görevlisi/araştırma görevlisi aynı PDF başlığında birlikte listelenir.
- `publishedAt=2026-11-24`; `deadline=2026-12-08`; dijital başvuru https://insankaynaklari.nisantasi.edu.tr/akademisyen-ilanlari . Kadro/programma göre doktora/uzmanlık/yayın/tecrübe ve ALES/yabancı dil şartları değişir; örnek tabloda ALES 70, yabancı dil 50 veya İngilizce programlarda 85. Araştırma görevlisi 2547 50/d. Resmî ilan PDF: https://www.nisantasi.edu.tr/Uploads/Akademisyenler/ik-kadro/24_11.2026%20tarihli%20resmi%20gazete%20%C4%B0lan%C4%B1.pdf.
- Önceki `deadline=null / Doğrulanamadı` notu düzeltilmiştir. Nişantaşı 1 duyurusu zaten özel havuzdaki 7 adayın içindeydi; toplam özel aday sayısı değişmedi. Kamu 23/17 değişmedi.
- PDF'nin tüm 9 sayfasına screenshot alındı; ana başlık ve kadro tablosu görsel olarak incelendi. Ayrıntılı düzeltme `gorevler/C1-RESMI-KAYNAK-SNAPSHOT-2026-11-26.md` içindedir; commit `fe3e038211db43efab0effa95b62f9ee6f4633c7`.

## 26.11.2026 — H1 exact-head emulator sonucu güncellendi
- PR #71 HEAD `b51c53a5797c19d4e18ed81b679024fc14fa3723` APK/unit [#45716694105](https://github.com/aries919-bot/Jarvis/actions/runs/45716694105) SUCCESS; artifact #15069888409.
- Aynı HEAD emulator [#45716694004](https://github.com/aries919-bot/Jarvis/actions/runs/45716694004) FAILURE. Job #139564450544: setup/dependency 1–6 SUCCESS; instrumentation step 7 timeout; XML step 8 FAILURE; artifact upload step 9 SUCCESS.
- Logda Android boot `997518 ms`; ColorBuffer/EmulatorConsole hataları; framework/Package Manager hazır ve window-focus preflight PASS. APK + androidTest APK derlemesi BUILD SUCCESSFUL. `connectedDebugAndroidTest` 30 dakika timeout; XML yok.
- Yeni koşuda dört test için PASS/FAIL sonucu üretilmedi. Bu, uygulama doğrulama hatası olarak sınıflandırılamaz; emulator/ADB/instrumentation hattı BLOCKED/UNKNOWN. Önceki #45712459718 focus failure ayrı tarihsel kayıttır.
- PR #71 açık/taslak, 14 dosya H1 kapsamı, GitHub formal review 0; merge/deploy yok.
- Ayrıntılı kanıt ve kontrollü sonraki tanı adımı: [H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md](../gorevler/H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md).

**KARAR:** H1 RED/BLOCKED. Aynı test workflow'u körlemesine tekrarlanmayacak; önce instrumentation launch tanısı izole edilecek.


## 26.11.2026 — H1 kontrollü instrumentation tanısı
- PR #71 HEAD `65095a5420d80b6c8cbf9a48eb36f3a5d803fcf6`; yalnız CI tanı betiği/workflow değişti, uygulama kodu ve dört test korunuyor.
- Yaklaşık 90 saniyelik ADB/Package Manager/instrumentation/Activity/window snapshotları ve canlı logcat eklendi; script değişiklikleri H1 workflow path filtresine alındı.
- Exact-head [emulator #45723036152](https://github.com/aries919-bot/Jarvis/actions/runs/45723036152) ve [APK/unit #45723036154](https://github.com/aries919-bot/Jarvis/actions/runs/45723036154) son kontrolde in_progress. Sonuç gelmeden PASS denmez.
- Ayrıntı: [H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md](../gorevler/H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md).
**KARAR:** H1 RED/BLOCKED; exact-head emulator XML 4 test/0 failure/0 error/0 skipped ve telefon kontrolü olmadan merge yok.


## 26.11.2026 — H1 CI tanısı: timeout ve ekran görüntüsü
- Genel teknik planın §9 maddeleri H1 ile eşleştirildi. Logcat + screenshot eklendi; izin otomasyonu/HoloView kapatma H1 CareerNewsVerificationActivity için uygulanabilir olmadığı için eklenmedi; dört gerçek UI/SQLite testi korunuyor.
- Önceki tanı run #45723036152 16+ saat `in_progress` kaldı, yeni commit gelince concurrency ile iptal oldu; artefakt yok. Sonuç olarak kullanılamaz.
- Script HEAD `9532d04343d1934dc01015f7dc78c7ec1ffc8e68`: ADB tanı komutları 15s sınırlandı, hata anında screenshot, logcat ve process-group cleanup eklendi; Gradle instrumentation'a 1500s internal timeout + 30s kill-after eklendi. Workflow step/job timeout ve XML 4/4 kapısı değiştirilmedi.
- Yeni [H1 run #45723550345](https://github.com/aries919-bot/Jarvis/actions/runs/45723550345) ve [APK/unit #45723550139](https://github.com/aries919-bot/Jarvis/actions/runs/45723550139) son kontrolde devam ediyor; henüz test sonucu yok.
- Ayrıntı: [H1 tanı görevi](../gorevler/H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md).
**KARAR:** H1 RED/BLOCKED; exact-head emulator XML 4/4 ve inceleme olmadan merge yok.
