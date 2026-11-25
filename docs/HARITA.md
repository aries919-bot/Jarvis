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
