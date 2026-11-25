# DURUM — Aries

## Son güncelleme: 25 Kasım 2026

## Yetkili takip dosyaları
- İş sırası ve kararlar: docs/HARITA.md
- Sert kurallar ve H1/C1 kabul kapıları: docs/NOT_C1.md
- QA ekip ve test kanıtları: gorevler/QA-EKIP-PANOSU-2026-11-25.md
- H1 emülatör hata raporu: gorevler/H1-EMULATOR-FAIL-ANALYSIS-2026-11-25.md
- Kariyer Question Answering/RAG: gorevler/ARIES-CAREER-QA-RAG-2026-11-25.md

## Aktif iş — H1 Haber doğrulama
- PR #71 açık/taslak, merge edilmedi. HEAD b8a78126cbc6713cb4d42838027a6189274f19e7; GitHub diff yeniden sayımı 10 dosya / +2.669 / -26. Kaynak test tanımları: 20 model +10 store +4 instrumentation = 34.
- APK/unit run #45692016951 SUCCESS: birim testleri, APK derleme ve artifact upload başarılı.
- Merge-ref emulator run #45692017824 son kontrolde IN_PROGRESS. Checkout/merge parent doğrulaması ve AndroidX dependencyInsight başarılı; instrumentation step 7 çalışıyor. Son XML artifact henüz yok.
- Kritik düzeltme: önceki emulator run #45685959506 GitHub SUCCESS görünse de artifact 15057322360 XML yeniden sayımı 4 test /1 PASS /3 FAIL/0 ERROR/0 SKIP. Üç UI test RootViewWithoutFocusException (has-window-focus=false). Eski action scriptinde set -e olmadığı için Gradle failure exit code'u yutulmuş; PASS kararı geri çekildi.
- Emulator workflow scripti set -euo pipefail olacak şekilde düzeltildi: commit 8d560a7d4e18db6fa6fdd1b131b47a831d6fd4bd.
- CareerNewsVerificationActivity ilk açılış SQLite load + koşulsuz save UI thread'deydi. Tek FIFO ExecutorService ile async load/persist, mevcut kaydı açarken yeniden yazmama, yalnız yeni story için initial save ve Activity kapanırken sıranın sonunda DB close: commit b8a78126cbc6713cb4d42838027a6189274f19e7.
- PR #71 için gerçek Android telefon testi yok. Yeni emülatör XML 4/4 ve telefon testi olmadan merge yok.
- PR #69, #71 ile 6 dosyada çakıştığı ve eski SharedPreferences mimarisi taşıdığı için merge dışı bekletiliyor; #71 kabul edilirse superseded olarak kapatılabilir.

## Aktif iş — Aries İş & Kariyer Question Answering / RAG
- Cloud PR #12 açık/taslak; HEAD a80e557d1dc8373446a3a41b15d50f998d5e3e46; base PR #11 HEAD b1415d6a76fd3856c60cfec891a575c3bef0341e; 4 dosya / +1043 / -20.
- Test dosyasında 26 test tanımı var; **çalıştırılmış test sayısı 0**. GitHub CI job başlatılamadığı için geçiş kanıtı bulunmuyor.
- Tasarım: Türkçe normalize + BM25 + Cloudflare BGE-M3 embedding + RRF; seçilmiş kaynak kayıtları üzerinden Gemma 4. Son tarih/şart/başvuru yolu gibi kesin alanlar deterministik; positionCount/city yalnız kaynakta varsa döner, yoksa belirtilmemiş; kanıt yoksa veya konu dışıysa abstain; AI kapasite doluysa BM25 fallback.
- Provider allowlist: kamu kategorileri yalnız iskur-esube-json / h2-official-announcement; Büyük özel yalnız jobs.lever.co Lever / jobs.ashbyhq.com Ashby. Stale, expired, unknown deadline ve possibleDuplicate kayıtlar cevap dışında.
- Gizlilik: CV/özgeçmiş, kişisel KPSS puanı, kimlik, telefon, e-posta, bearer/API token desenleri AI çağrısından önce engellenir. Kullanıcı profili/sohbet geçmişi/e-Devlet verisi D1 veya modele gönderilmez; soru/token loglanmaz.
- Son 7 CI run #45692792848, #45692810534, #45692919011, #45692938639, #45693799901, #45693834527, #45694369322: hepsi failure; her job steps=[] ve 2–3 saniyede kapanıyor. Son run #45694369322/job 139487790097: 20:22:41Z–20:22:42Z. Log BlobNotFound; annotation içeriğine erişilemiyor. Bu kod testi sonucu değil; runner/job-start engeli. Tekrarlı commit/rerun döngüsü durduruldu. PR #12 merge/deploy yok.
- Backend QA henüz Android ekranına bağlanmadı. H1 haber SQLite verisi için ayrı News QA servisi/ekranı tasarlanacak; kariyer feed'iyle birleştirilmeyecek.

## Kaynak politikası / C1
- PR #70 MERGED; commit 09f98b324a9a1f38dbbc25853ec2eba2fc544db9; exact-head CI #45677312902 SUCCESS.
- C1 Worker PR #11 RED. Kamu sayımı 12 resmî kaynak /55 doğrulanmış açık ilan; 100 hedefine 45 eksik. Worker production authenticated GET/D1 SKIPPED; H2 city null nedeniyle exact cross-feed dedup kanıtı yok.
- Resmî ilan keşif araştırmasında 7 açık duyuru adayı bulundu; kontenjan toplamı ilan kartı sayısı kabul edilmedi. 4 yeni kaynak adayı feed'e alınmadı; sayaç değişmedi.
- Kabul eşiği 10 resmî kaynak /100 doğrulanmış ilan + production Worker GET/D1 ve city kanıtlı dedup.

## Mimari ve güvenlik
- CareerActivity.kt yaklaşık 2.795 satır; CareerMonitor.kt yaklaşık 2.049 satır. Küçük class-boundary extraction H1 kapıları sonrasında.
- MainActivity ilk açılışta RECORD_AUDIO, CALL_PHONE, READ_CONTACTS, POST_NOTIFICATIONS topluca istiyor. CALL_PHONE ve READ_CONTACTS gerçek özelliklerde kullanılıyor; sorun ilk açılışta erken istenmeleri. Just-in-time izin PR'ı ayrı.
- Reminders.kt AlarmManager.setAlarmClock + USE_EXACT_ALARM kullanıyor; Play policy uygunluğu ayrı denetlenecek.
- denetle.py diff'in ilk 12 KB'sini Groq'a gönderdiği için çalıştırılmıyor.
- Android CI Gradle hız optimizasyonu ayrı; test process'e TMDB/keystore sırlarını aktarmama kapısı var.

## Sıradaki adımlar
1. H1 emulator #45692017824 tamamlanınca XML içinden test sayısını yeniden say; 4/4 olmadan PASS/merge yok.
2. H1 gerçek Android telefon kontrolü. Sonra PR #71 merge değerlendirmesi.
3. Cloud PR #12 için GitHub Actions runner/job-start blokajı çözülmeden commit/rerun döngüsü yok; CI exact-head üzerinde gerçekten çalışmalı.
4. PR #12 CI geçtiğinde QA/RAG davranışını örnek gerçek feed ve sahte veriyle ölç; ardından Android kariyer ekranına bağla.
5. Ayrı H1 News QA: yerel Claim/Evidence/ReviewHistory kayıtlarında kanıta dayalı arama ve cevap; kayıt yoksa abstain.
6. C1 kamu ilan sayısını 100'e çıkarma, Worker GET/D1 ve şehirli dedup kapıları.

## Çalışma kuralı
Önce H1 4/4 emulator + gerçek telefon; sonra kariyer QA exact-head CI ve Android entegrasyonu; sonra C1 kaynak ve D1 kapıları. C1 eşik altındayken genel kabul yok. Film/tema/Borsa bu iş sırasının dışındadır.


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


## 26.11.2026 — H1 Android Package Manager engeli: sonraki teknik adım
- Araştırma, sys.boot_completed=1 sonrasında system_server yeniden başlatma olasılığını belirledi. PID ve boot_progress_enable_screen aynı nesle ait olmadan Package Manager hazır sayılmıyor.
- Runner güncellendi: 29aaa17e532c13a394d1ed7dd7c53bc613d0bec9; boş PM yanıtını RED yapan düzeltme: e390e05e1485717c3a07ce3af06db6b3840062f3.
- Yerel mock testler: 1 pozitif, 3 negatif doğru sonuç verdi; bu emulator kanıtı değildir.
- Exact HEAD e390e05 için [emulator #45712459718](https://github.com/aries919-bot/Jarvis/actions/runs/45712459718) ve [APK #45712459742](https://github.com/aries919-bot/Jarvis/actions/runs/45712459742) bitmedi. Dört UI testinin XML sayımı gelmeden H1 PASS yok.
- PR #71 açık/taslak kalır. C1 kamu feed 6/47 last-good, 6/6 stale, 10/100 ve production GET/D1 eksik.

**SONRAKİ:** Current exact-head CI sonuçlarını al; emülatör artifact XML'sini yeniden say; Package Manager tekrardan koparsa framework PID/event ve crash-buffer artifact'iyle gerçek log üzerinden kök nedeni doğrula. Aynı ayarı körlemesine tekrar etme.


## 26.11.2026 — H1 exact-head güncellemesi
- `e390e05e1485717c3a07ce3af06db6b3840062f3` üzerinde [APK/unit CI #45712459742](https://github.com/aries919-bot/Jarvis/actions/runs/45712459742) SUCCESS: `testDebugUnitTest` 1m50s, `assembleDebug` 1m10s, artifact ID 15068252085 (14,197,729 byte). SHA-256 ZIP `d3ceae0d1781cfb0f6ae19cdeb587494ea4f04310d2117f3fe23c46aafb43c91`; çıkarılan debug APK `045bf135f19f4cac514ad48b05b3c33b374c7f0cc805a2debd4d07cdc3a4fb6d`.
- Test sayımı: tüm unit kaynaklarında 142 @Test tanımı /12 dosya; H1 model+store+QA içinden 47; Android instrumentation 4. CI unit task SUCCESS, ancak 142/142 ayrı test-report sayımı yapılmış değildir.
- [Emulator #45712459718](https://github.com/aries919-bot/Jarvis/actions/runs/45712459718) current HEAD'de IN_PROGRESS; instrumentation step sürüyor; XML doğrulama ve artifact pending. 4/4 gerçek UI testcase raporu gelmeden H1 kabul yok.
- FUTO ikinci bulgusu: Package Manager var görünse de StorageManager/installer yolu hazır olmayabilir; gerekirse `pm install-create` preflight. Yalnız Aries loguyla StorageManager arızası görülürse uygulanacak.
- C1 feed 6/47 last-good; 6/6 stale; 10/100 ve production Worker GET/D1 RED.

**SONRAKİ:** Emulator run sonucunu al; XML sayısını ve failure/error/skipped değerlerini yeniden say; hata varsa gerçek crash/events loguna göre bir sonraki kontrollü düzeltmeyi yap.


### Bekleyen bakım bulgusu — workflow path filters
- Workflow `.github/workflows/h1-android-ui-smoke.yml` içindeki pull_request.paths listesi `scripts/run-h1-emulator-tests.sh` ve `scripts/verify_h1_emulator_xml.py` dosyalarını kapsamıyor. Bu iki betikten biri tek başına değişirse PR event'i bu workflow'u tetiklemeyebilir; mevcut run tetiklendi çünkü aynı PR'da workflow dosyası da değişti.
- Mevcut emulator koşusu tamamlanmadan workflow dosyasına dokunulmadı; aksi halde concurrency `cancel-in-progress: true` mevcut kanıt koşusunu iptal eder. Sonuç alındıktan sonraki kontrollü düzeltmede iki script yolu trigger listesine eklenmeli.
- FUTO'nun daha yeni CI olayı ayrıca `pm install-create`/abandon preflight ile PackageInstaller + StorageManager yolunun hazır oluşunu doğruluyor. Bu sadece mevcut Aries artifact'inde aynı storage/installer hatası görülürse uygulanacak; şimdilik tahminle commit yok.


## 26.11.2026 — H1 Espresso focus failure
- [Emulator #45712459718](https://github.com/aries919-bot/Jarvis/actions/runs/45712459718) XML yeniden sayımı: 4 test /3 failure /0 error /0 skipped; 1 SQLite recovery passed. 3 UI failure `RootViewWithoutFocusException`, root `has-window-focus=false`. PM/system_server preflight bu run'da geçti.
- Benzer API34 Android CI issue #372, boot-time Pixel Launcher ANR dialogunun Espresso focus'u tuttuğunu ve build sonrası CLOSE_SYSTEM_DIALOGS + HOME ile kapatılabildiğini kanıtlayan başka proje raporunu içeriyor: https://github.com/RizzoG99/PersonalFinanceTracker/issues/372. Aries loglarında launcher top-resumed timeout ve diğer system ANR izleri var; kendi failure anı mCurrentFocus kaydı olmadığından aynı dialog Aries için henüz kuvvetli hipotezdir.
- Fix commit `b51c53a5797c19d4e18ed81b679024fc14fa3723`: build APKs first; inspect focused system window; only if ANR/Application Error dialog holds focus send CLOSE_SYSTEM_DIALOGS + HOME; fail closed if it remains. UI testleri aynen kaldı.
- Local mocked integration: clear=0, recovered ANR=0, persistent ANR=22; 3/3. Bash syntax PASS; no emulator claim.
- Exact-head emulator [#45716694004](https://github.com/aries919-bot/Jarvis/actions/runs/45716694004), APK [#45716694105](https://github.com/aries919-bot/Jarvis/actions/runs/45716694105) IN_PROGRESS.
- C1 6/47 last-good public; 6 stale; 10/100 + production GET/D1 RED.

**SONRAKİ:** Yeni CI sonucunu al; window-focus preflight logunu ve 4-test XML raporunu incele; gerekirse gerçek mCurrentFocus kaydına göre bir sonraki çözümü seç.


## 26.11.2026 — Kamu kaynak adayları: 10 kurum / 10 duyuru
- Resmî kurum araştırması ayrı görev dosyasına yazıldı: [gorevler/C1-KAMU-KAYNAK-ADAYLARI-2026-11-26.md](../gorevler/C1-KAMU-KAYNAK-ADAYLARI-2026-11-26.md).
- Adaylar: Sahil Güvenlik (10 pozisyon), Bursa Teknik Üniversitesi (1), YÖKAK (5), Adli Tıp Kurumu (181), Anadolu Üniversitesi (1), Zafer Kalkınma Ajansı (2), ORAN (7), Dicle Kalkınma Ajansı (4), Bartın Üniversitesi (1), TGA (13). Toplam 10 farklı kurum / 10 duyuru kartı / 225 pozisyon; pozisyon sayısı ilan kartı sayısı değildir.
- Her kurum için kaynak başına 1 duyuru kartı. Resmî kaynak sayfaları ve başvuru sonları görev dosyasında; başvuru hedefleri kazınmaz.
- Sayıştay (16 pozisyon, son başvuru 25.11.2026), OKA (5, son başvuru 22.11.2026), MEVKA (1, son başvuru 13.11.2026) geçmiş tarihli olduğu için aktif sayaç dışında bırakıldı.
- Bu manuel keşif Worker'a aktarılmış değildir: gerçek feed ölçümü hâlâ 6 kaynak /47 last-good kart, 6/6 stale, 0 fresh; production authenticated GET/D1 yok. Kamu eşiği 10 kaynak/100 doğrulanmış ilan: pipeline ölçümünde RED; manuel aday listesinde 10 kaynak ama yalnız 10 kart (90 eksik).

**KARAR:** Kaynak keşfi 10 kuruma genişletildi; canlı feed/100 kart kanıtı yok, kamu C1 RED.


## 26.11.2026 — H1 latest APK CI
- HEAD `b51c53a5797c19d4e18ed81b679024fc14fa3723` [APK/unit #45716694105](https://github.com/aries919-bot/Jarvis/actions/runs/45716694105) SUCCESS; artifact 15069888409 (14,198,084 byte; sha256 `8f98ae33b42003beb70851e5b596e0b6354f7d24559b9585219e1f0ae46fbb79`).
- Exact-head emulator [#45716694004](https://github.com/aries919-bot/Jarvis/actions/runs/45716694004) still IN_PROGRESS; four-test XML not yet available.
