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
- K1-B geniş ölçüm PR #36: `is/K1-B-wide` → `durum`, PR AÇIK; Actions [#45405558813](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813) job SUCCESS, 20 benzersiz URL: 8 ÇALIŞIYOR / 12 ENGELLİ / 0 ÖLÜ; 9/9 özel test. H1 `is/H1` kaynak listesi yalnız 8 ÇALIŞIYOR adresle güncellendi; PR #35 AÇIK, KABUL değil.
- PR #32 ve #34 için kullanıcı birleştirdiğini bildirdi; ancak son GitHub API kontrolünde ikisi de `open` ve `closed_at=null` görünüyor. Ayrıca `.github/workflows/testler.yml` `cline/K1` dalında bulunamadı. Bu nedenle PR #33 hedefi henüz `cline/K1` yapılmadı; değişiklikten önce merge durumu repo üzerinde doğrulanmalı.

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

## C1/K1 v2 görev kuyruğu
| Kimlik | Durum | Sahip / Bitiş kanıtı |
|---|---|---|
| K1 | DEVAM | Cline; K1 kaynak hattı üst başlığı |
| K1-E1 | KABUL | Cline; Actions #45388925995 SUCCESS; 43 benzersiz kamu ilanı / 8 kurum; Cem 3 ilanı e-Şube'de girişsiz elle doğruladı |
| K1-F | DEVAM | Cline; [gorevler/K1-F.md](../gorevler/K1-F.md); yalnız iki dosyada User-Agent değişikliği + yeşil Actions bekleniyor |
| K1-B | PR AÇIK / ÖLÇÜM TAMAM | ChatGPT genişletmesi; PR #36; 20 URL / 8 ÇALIŞIYOR / 12 ENGELLİ / 0 ÖLÜ; Actions #45405558813; gerçek ilan okuyucusu kanıtı değil |
| H1 | PR AÇIK, ÖLÇÜM BEKLİYOR | PR #35; kaynak listesi 8 ÇALIŞIYOR URL ile güncellendi; 3 sayfada tarihli duyuru sinyali; gerçek ilan / `publishedAt` / reader denetimi bekliyor |
| K1-C | BEKLİYOR | ChatGPT; yalnız K1-B'de ölçülmüş ve içerik/robots uygunluğu ayrıca doğrulanmış kaynaklarla aday listesi |
| C1-1 | YAPILMADI | Cline; jarvis-cloud PR #5, `npm run test:c1` + yeşil CI |
| C1-2 | YAPILMADI | Cline; canlı rapor, kamu ayrı, 10 kaynak/100 ilan veya gerçek RED sayısı |
| K1-M | YAPILMADI | Cline; temiz cherry-pick PR main'e, CI yeşil |

### K1-B genişletilmiş erişim ölçümü — 24.11.2026

- PR: [#36 K1-B genişletilmiş ölçüm](https://github.com/aries919-bot/Jarvis/pull/36), `is/K1-B-wide` → `durum`, AÇIK; yalnız ölçüm aracı/manifest/test/workflow değişti.
- Actions: [run #45405558813](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813), job `measure` SUCCESS; özel testler **9/9** geçti; [JSON artefakt #14926070923](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813/artifacts/14926070923).
- Çalıştırılan kapsam: H1'deki 18 benzersiz aday URL/alt sayfa + `https://ilan.gov.tr/` + `https://www.resmigazete.gov.tr/` = **20 benzersiz URL**. Satırlar yeniden sayıldı: 20.
- Sonuç: **8 ÇALIŞIYOR / 12 ENGELLİ / 0 ÖLÜ**. 3 sayfada yakın duyuru bağlamında tarih sinyali (e-Şube, YHGM, BOTAŞ); bu `publishedAt` veya iş ilanı sayısı değildir.
- User-Agent `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)`; TLS doğrulaması açık; önce robots; belirsiz/yasak kaynaklara sayfa isteği yok; login/CAPTCHA/form yok.
- H1 [docs/HABER_SEMA.md](../docs/HABER_SEMA.md) aktif kaynak listesi yalnız 8 ÇALIŞIYOR URL ile güncellendi. Girişli Kariyer Kapısı/e-Devlet adresleri yalnız Custom Tabs yönlendirmesi. H1 PR #35 AÇIK, ölçüm/onay bekliyor; KABUL edilmedi.
- C1 kamu sayımı değişmedi: K1-E1 **1 kamu kaynak / 43 doğrulanmış ilan**; 10 kaynak / 100 doğrulanmış ilan hedefi henüz sağlanmadı.
- PR #32/#34 API durumları son kontrolde `open`; `testler.yml` cline/K1'de görünmüyor. Bu nedenle #33 tabanı `cline/K1` yapılmadı; merge kanıtı repo üzerinde görünür hale gelince değiştirilecek.

### K1-E1 doğrulama kaydı — 24.11.2026
- Commit: [efd1aaea](https://github.com/aries919-bot/Jarvis/commit/efd1aaea25f4e7554241c68b88c7ffe72a121d99); workflow: [kamu-ilan-cek #45388925995](https://github.com/aries919-bot/Jarvis/actions/runs/45388925995) — push, completed / success; tüm 7 ana adım SUCCESS.
- JSON `durum` dalında tekrar okundu: [career/esube_kamu_ilanlari.json](../career/esube_kamu_ilanlari.json), 3 sayfa, 43 satır, 43 benzersiz ilan, 0 tekrar, 8 kurum. İlk 3 örnek ilan no: 00009905681, 00009905684, 00009905686; JSON'da MSB ve son başvuru 27.11.2026.
- Kaynak sayımı: 1 kamu kaynağı / 43 ilan. 10 kaynak / 100 ilan toplam eşiği henüz sağlanmadı.
- Cem'in 24.11.2026 tarihli telefon doğrulaması: e-Şube > Açık İş İlanları > Kamu, giriş gerektirmeden; 00009905681 Muğla Merkez / 6 pozisyon / MSB Personel GM / 27.11.2026; 00009905686 İzmir Merkez / 10 pozisyon / aynı kurum / 27.11.2026; 00009905689 İstanbul Merkez / 24 pozisyon / aynı kurum / 27.11.2026. Sayfa altı `3 / 3, Toplam Kayıt: 43`; JSON 43 ile eşleşiyor. Kullanıcı teyidine göre 8 kurumla uyumlu. Satırlarda yayın tarihi yok; `publishedAt=null` doğru.
- Ek sınır testi örneği: 00009908955, Karayolları 9. Bölge / Diyarbakır / son başvuru 24.11.2026 / “8 Saat Kaldı”. 24.11 sonrası güncel listeden çıkarılıp geçmişe taşınmalı; bu otomatik tarih filtresinin ayrıca test edilmesi gerekir.
- K1-E1 kabulü: CI + JSON bağımsız sayımı + Cem'in üç ilanı kaynakta elle doğrulaması sağlandı. Kabul, 10 kaynak/100 ilan toplam hedefinin sağlandığı anlamına gelmez; mevcut ölçüm 1 kamu kaynağı / 43 ilan.

### K1-F görev kaydı — 24.11.2026
- K1-E1 kullanıcı tarafından kaynakta elle doğrulandı ve KABUL edildi. Sıradaki iş K1-F olarak DEVAM'a alındı.
- Görev dosyası: [gorevler/K1-F.md](../gorevler/K1-F.md). Yalnız `scripts/render_probe.py` ve `.github/workflows/iskur-render.yml` içindeki Chrome/tarayıcı taklidi User-Agent açık kimlikli `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)` ile değiştirilecek; başka değişiklik yok.
- Cline'a verilecek tek satır: `git fetch origin durum; git show origin/durum:gorevler/K1-F.md oku, görevi aynen uygula, o dosyayı commit etme.`

## Cline'a ayrılmış net işler
**K1 — ilk iş / sahip: cline**
- Dosyalar: `.github/workflows/career-source-audit.yml`, `career/sources.json`, `docs/KARIYER_KAYNAK_DENETIMI.md`, `scripts/career_source_audit.py`, `scripts/test_career_source_audit.py`.
- Kaynak başına erişim/giriş/CAPTCHA, çekim zamanı, ilan sayısı ve hatayı ölç; yalnız açık ve girişsiz sayfaları otomasyon adayı yap.
- Bitiş: her kaynak gerçek sayfa denetimi; PR başına en az 3 ilan örneği veya yokluk nedeni; test sonucu; `cline/K1` PR ve yeşil CI.

**C1 — sahip: chatgpt / DEVAM**
- C1 özel kabul/kanıt/kamu/firstSeenAt/kaynak-bazlı stale ve Cline bekleme kuralları: [NOT_C1.md](NOT_C1.md). Worker PR #5 ayrıntıları aşağıdadır.
- 15 aday (9 Lever, 6 Ashby); canlı çalışan kaynak ve ilan sayısı kanıtlanmadı. Testlerdeki 15 kayıt sentetik fixture'dır.
- 24.11 bağımsız PR #5 statik QA: GET `/kariyer-feed` rotası yok; `getCareerFeed` kullanılmıyor; `firstSeenAt` yok. Son iyi veri yalnız 0 başarılı kaynakta korunuyor; 1–9 kaynak veya 100 altı ilanla ezilebilir.
- Mimari ihlal: Worker `scheduled` içinde `runCareerFeed` çağırıp kaynak/robots/pagination topluyor; ağır toplama Actions'a taşınmalı, Worker yalnız JSON sunmalı.
- Test açığı: 403 ve timeout senaryosu yok; 503/boş/bozuk/robots ve sentetik ilan fixture'ları var. Gerçek ilan kanıtı yok.
- CI run #45382024807 failure; tek job `syntax-and-unit` failure, adımlar/loglar dönmedi. [CI #45382024807](https://github.com/aries919-bot/jarvis-cloud/actions/runs/45382024807). Yeşil CI, canlı kaynak sayımı ve 10/100 eşiği kanıtlanmadı.
- Kaynak adayı ölçümü: Kariyer Kapısı `/isealim` HTML'de filtre başlıkları var ama ilan satırları yok (JS içerik yükleme adayı; reader başarısı sayılmaz); e-Şube formu açılıyor ama statik sayfada sonuç ilanları yok (POST arama sonucu bekleniyor); ilan.gov.tr web erişimi 502 verdi (TLS doğrulaması kapatılmayacak); ÇSGB BTGM sayfasında 04.10.2026 tarihli gerçek 5 kişilik ilan doğrulandı fakat başvuru 11.10.2026'da bitti, güncel ilan değil. Bu adaylar C1'in 10 kaynak/100 ilan sayımına eklenmedi.

**E1-Veri — sahip: cline**
- Dosyalar: `market/sources.json`, `worker/src/market-data.ts`, `.github/workflows/market-data.yml`, `worker/test/market-data.test.ts`.
- Ücretsiz altın/BIST/kur kaynaklarını erişim, lisans ve zaman damgasıyla ölç; zaman damgalı JSON, yedek kaynak ve eski veri uyarısı üret. Anahtarları koda koyma.
- Bitiş: kaynak başına son güncelleme/yaş/hata; bozuk/eksik/eskimiş veri ve timeout testleri; `cline/E1` PR ve yeşil CI.

## Cline PR hakem akışı
- Cline yalnız `DEVAM: <iş-id>` ile başlar, `cline/<iş-id>` dalında PR açar.
- PR açıklamasında değişiklik, test, gerçek test sonucu ve telefon denemeleri olmalı; eksikse onay/merge yok.
- ChatGPT PR'ı sıfırdan inceler; kaynak/sayı iddialarını tekrar ölçer. Yetki yoksa ONAY yorumundan sonra Cem'den merge istenir.
- `sahip: cline` işlerinin kaynak koduna ve dallarına ChatGPT müdahale etmez.

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
**SONRAKİ ADIM:** PR #34'ün merge durumu ve `.github/workflows/testler.yml` dosyasının `cline/K1` tabanında görünmesi doğrulandıktan sonra PR #33 tabanını `cline/K1` yap ve yeni yeşil CI'ı kontrol et. H1 #35 açık kalır; K1-C/H2 ancak onay ve gerçek içerik/ilan denetimi için ayrı görevle ilerler. C1 genel 10 kaynak / 100 doğrulanmış ilan eşiği sağlanmadan Android entegrasyonuna geçme.

11. 24.11 C1 bağımsız QA: PR #5'te GET feed rotası/firstSeenAt yok; kısmi feed son iyiyi ezebilir; Worker ağır toplama yapıyor. #45382024807 failure (step/log yok). Kariyer Kapısı JS kabuğu, e-Şube formu, ilan.gov.tr 502; ÇSGB örneği süresi geçmiş. Cline K1 dalı mevcut; işine dokunulmadı.
