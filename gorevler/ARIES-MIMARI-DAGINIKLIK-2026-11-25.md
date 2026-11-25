# Aries mimari ve dosya düzeni denetimi — 25.11.2026

## Kapsam ve yöntem
- İncelenen depo: `aries919-bot/Jarvis`.
- Referanslar: `kariyer-modulu` taban ağacı, H1 PR #71 exact HEAD `6b392a3aa02d42700743657350d081d7f7ad6aeb`, `durum/docs/HARITA.md`, `durum/docs/NOT_C1.md`.
- Ayrı gerçek ajan çalıştırma aracı bu oturumda bulunmadı. Bu nedenle dört bağımsız QA geçişi yürütüldü: mimari/dosya, güvenlik-yönetişim, H1 veri bütünlüğü, CI/test/C1 kapıları. Ayrı ajan/bağımsız harici hakem kullanıldığı iddia edilmez.
- Kod taşıma/yeniden adlandırma yapılmadı. H1 exact-head CI sürerken büyük refaktör aynı çalışma dalına karıştırılmadı.
- Bu görev H1 ve C1 önceliğine bağlıdır. Film/tema alanına kod değişikliği bu denetimde kapsam dışı bırakıldı.

## QA-1 — mimari ve dosya yapısı
### Kanıt
- `kariyer-modulu` taban ağacı: 79 kayıt; 30 Kotlin dosyası.
- H1 PR #71 HEAD ağacı: 91 kayıt; 36 Kotlin dosyası.
- En büyük dosyalar (PR #71 HEAD tree): `CareerActivity.kt` 155,317 byte; `CareerMonitor.kt` 119,687 byte; `MediaSourceBrowser.kt` 87,463 byte; `MainActivity.kt` 75,911 byte; `Ai.kt` 34,226 byte.
- Taban kaynağında satır denetimi: `CareerActivity.kt` 2,795 satır; `CareerMonitor.kt` 2,049 satır; `MainActivity.kt` 1,802 satır; `Ai.kt` 735 satır.
- `CareerMonitor.kt` tek dosyada şu bağımsız alanları barındırıyor: `CareerSourceCatalog` (satır 20–383), `CareerEligibility` (384–555), `CareerListingParser` (556–706), `CareerSearchEngine` (719–1259), `CareerRecruitmentTracker` (1260–1449), scheduler + WorkManager worker'ları (1450–2049).
- `CareerActivity.kt` içinde arama/ilan, Haber, süreç/kura, CV, takip, kayıtlı başvurular ve UI yardımcıları tek Activity'de bulunuyor.
- `app/src/main/java/com/cem/jarvis/media/` içinde derlemeye girmeyen 5 sürüm yedeği var: `MediaSourceBrowser.kt.pre-player-component`, `.pre-exoplayer`, `.pre-media-scan`, `.pre-player`, `.v6yedek`. Toplamları yaklaşık 262,725 byte. Bunlar kaynak ağacını kirletiyor; medya/film alanı bu iş talimatında kapsam dışı olduğu için silinmedi/taşınmadı.
- `settings.gradle.kts` yalnız `:app` modülünü içeriyor; tek modül şimdilik uygun, ilk çözüm Gradle modüllerini çoğaltmak değil paket/sınıf sınırlarını iyileştirmek.

### Mimari karar
Dosyalar rastgele topluca taşınmayacak. İlk düşük riskli refaktör, `CareerMonitor.kt`'yi aynı `com.cem.jarvis` paketinde, sınıf sınırlarına göre ayrı dosyalara bölmektir. Sonraki aşama `CareerActivity` UI ve veri sorumluluklarını View/controller/repository parçalarına ayırmaktır. Her aşama ayrı PR ve exact-head CI ile yapılır.

## QA-2 — güvenlik, kaynak politikası ve proje yönetişimi
### Bulgular
- `denetle.py`, `.git/fark.diff` dosyasının ilk 12,000 karakterini basit regex taramasından sonra Groq API'ye gönderiyor. Bu denetim aracı kodu dış servise aktarabilir; regex kapsamı tam secret/kişisel veri taraması değildir. Bu script bu görevde çalıştırılmadı. Yeni QA sürecinde dış servise kod/diff gönderimi varsayılan olarak kapalı olmalı; yerel test ve GitHub Actions kanıtı esas alınmalı.
- `AGENTS.md` eski Cline/Groq/Claude/OpenCode akışını anlatıyor; güncel `docs/NOT_C1.md` iş akışıyla tutarsız. Ayrı ajan aracı yoksa ajan kullanıldığı yazılmamalı.
- `DURUM.md` son güncellemeyi 31 Ekim 2026 gösteriyor ve artık güncel otorite değil; güncel iş kaydı `docs/HARITA.md` + `docs/NOT_C1.md`.
- `.gitignore` içinde `gorevler/` ve `HARITA.md` desenleri bulunuyor. Bu durum yerel Git akışında görev notlarının yanlışlıkla ignore edilmesine neden olabilir; yalnızca `docs/` altındaki kontrollü kayıt yolu kullanılmalı veya ignore deseni ayrı küçük PR'da düzeltilmeli.
- Android manifestinde RECORD_AUDIO, CALL_PHONE, READ_CONTACTS, INTERNET, ACCESS_NETWORK_STATE, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED, SET_ALARM, USE_EXACT_ALARM, BLUETOOTH_CONNECT izinleri var. Bu denetim bunların tamamının runtime kullanımını doğrulamadı; izin-minimizasyonu ayrı güvenlik görevi olmalı. İzinler bu görevde değiştirilmedi.

## QA-3 — H1 haber doğrulama veri modeli ve UI
- PR #71 exact HEAD: `6b392a3aa02d42700743657350d081d7f7ad6aeb`; 10 dosya / +2,578 / -26.
- İnsan inceleme geçmişi append-only: karar, UTC zaman, gerekçe ve o sıradaki kanıt kimlikleri korunuyor; yeni kanıt aktif kararı arşivleyip sıfırlıyor; Store geçmiş silmeyi/yeniden yazmayı reddediyor.
- Kanıt formunda isteğe bağlı `publishedAt` UTC alanı var; boş değer null, geçersiz tarih reddediliyor.
- Düzeltme geçmişi eski kanıtların yayıncı, tür, stance, alıntı, yayın/kontrol tarihi ve kaynak URL'sini gösteriyor.
- Gerçek UI akışını hedefleyen instrumentation eklendi: iddia → kanıt/yayın tarihi → insan incelemesi → yeni kanıtla arşiv → düzeltme → eski kanıt ayrıntısı.
- Test sayımı dosyalar tekrar sayılarak: 20 model + 10 SQLite store + 4 Android instrumentation = 34 test.

## QA-4 — CI, C1 ve kabul kapıları
- H1 exact-head APK/unit [#45679523180](https://github.com/aries919-bot/Jarvis/actions/runs/45679523180): SUCCESS; unit test, assembleDebug ve artifact upload geçti. Artifact ayrıntısı Actions run sayfasındadır.
- H1 exact-head emulator/SQLite [#45679523114](https://github.com/aries919-bot/Jarvis/actions/runs/45679523114): son kontrolde IN_PROGRESS; job log endpoint'i BlobNotFound döndü. Log erişim hatası test PASS/FAIL anlamına gelmez.
- PR #71 gerçek telefon testinden geçmedi; emülatör PASS olmadan kabul yok.
- PR #70 exact-head [#45677312902](https://github.com/aries919-bot/Jarvis/actions/runs/45677312902): SUCCESS; hakem yalnız PR kapsamı için ONAY verdi. H1 sırası tamamlanmadan merge edilmez.
- C1 private PR #11: son kanıt 12 kamu kaynağı / 55 doğrulanmış açık ilan; 100 ilan şartına 45 eksik. Worker production authenticated GET/D1 ve şehir kanıtlı cross-feed dedup kapısı eksik; genel C1 kararı RED.
- `Ai.kt` içinde DDG/Bing/Brave HTML arama parser'ları mevcut. `NOT_C1.md` telefondaki HTML fallback kazımasının kapanmasını öngörüyor; ancak Worker production GET doğrulanmadan fallback kapatma da doğrulanmış çözüm değildir. Bu geçiş C1 production GET işiyle aynı PR'da değil, ayrı ve testli yapılmalı.
- Mevcut Actions: Android unit + APK workflow ve H1 emulator smoke workflow. Lint/static-analysis/test matrix kapsamı ayrıca değerlendirilmeli; mevcut iki yeşil workflow tüm uygulamanın tam QA'sı olarak sunulamaz.

## Önerilen düzen — küçük PR sırası
1. H1 PR #71 exact-head emulator PASS/FAIL sonucunu kapat; diff'i tekrar incele; gerçek telefon adımını kaydet.
2. H1 kabulünden sonra kaynak/UA politikası PR #70'i birleştir.
3. `CareerMonitor.kt` için ayrı PR: `CareerSourceCatalog.kt`, `CareerEligibility.kt`, `CareerListingParser.kt`, `CareerSearchEngine.kt`, `CareerRecruitmentTracker.kt`, `CareerWorkScheduler.kt`. İlk aşamada package/import davranışını değiştirme; yalnız class-boundary extraction.
4. Her split PR'da dosya listesi sadece taşınan sınıflar + doğrudan testler; mevcut unit tests + assembleDebug + kaynak policy tests zorunlu.
5. `CareerActivity.kt` için ayrı PR'lar: önce kayıtlı ilan/başvuru veri erişimi repository'si; sonra Haber sayfası controller/renderer; sonra arama/sonuç sayfası. Tek PR'da 2,800 satır refaktörü yapılmaz.
6. H1 modeli/store/activity için alt paket taşıması ancak H1 ve C1 kaynak politikası sabitlendikten sonra; manifest ve test paket adları birlikte güncellenir.
7. `AGENTS.md`, `DURUM.md`, `.gitignore`, `denetle.py` yönetişim düzeltmeleri ayrı docs/tooling PR'ında. Groq'a kod/diff gönderimi varsayılan kapalı.
8. `MainActivity.kt`, `Ai.kt`, medya yedek dosyaları bu H1/C1 refaktörlerinden ayrı alanlardır; film/tema kodu bu iş paketinde değiştirilmez. Gerekirse sonradan ayrı onaylı görev açılır.

## KARAR
**RED / temizlik tamamlanmadı.** Mimari denetim tamamlandı ve refaktör sırası belirlendi; henüz kaynak dosyalarını taşıma/silme yapılmadı. H1/C1 kabul kapıları ve ayrı refaktör PR'ları bitmeden uygulama genelinin düzenlendiği söylenemez.

## SONRAKİ ADIM
H1 emülatör exact-head run #45679523114 sonucunu kapat. Sonrasında PR #70; ardından ilk küçük yapısal PR `CareerMonitor.kt` class-boundary extraction.
