# H1 CI false-green düzeltmesi ve C1 resmî kaynak ekleri

- **Kayıt zamanı (collectedAt):** 2026-11-25T21:24:16.850Z
- **Dal:** `durum`
- **Kapsam:** yalnız Haber doğrulama H1 CI ve İş&Kariyer C1 kaynak kanıtı
- **Kod değişikliği:** yok
- **PR kararı:** H1 ONAY YOK; merge yok.

## KARAR

H1 PR #71'in son exact HEAD'i `8a52e21e3dfb7e5e11d6f5009077f90edce2c09b` için GitHub Actions run #45699704565 yeşil görünse de gerçek emülatör raporu başarısızdır. XML'de 4 test, 3 failure, 0 error, 0 skipped bulunur. Yalnız SQLite last-good recovery testi geçmiştir. Bu nedenle workflow SUCCESS etiketi geçerli test başarısı sayılmaz.

## Kanıt

### H1 exact-head raporu
- PR: https://github.com/aries919-bot/Jarvis/pull/71
- HEAD: `8a52e21e3dfb7e5e11d6f5009077f90edce2c09b`
- Android APK run: https://github.com/aries919-bot/Jarvis/actions/runs/45699704415 — SUCCESS (yalnız APK build).
- Android emulator run: https://github.com/aries919-bot/Jarvis/actions/runs/45699704565 — GitHub sonucu SUCCESS, fakat artifact XML: `tests=4 failures=3 errors=0 skipped=0`.
- Artifact: `h1-android-emulator-reports`, ID `15063616173`, SHA-256 `da33e7d6084c64f6aabcadb488684ad41210be00f389fcfc266a0b1f43d10470`.
- `h1-emulator.log`: `BUILD FAILED in 6m 26s`, 69 actionable tasks.
- Üç başarısız test:
  1. `opensH1ActivityAndPersistsUnknownPublicationDateAsNull`
  2. `evidenceReviewAndCorrectionFlowPersistsPublicationDateAndAuditDetails`
  3. `displaysArchivedHumanReviewHistoryFromSqlite`
- Üçünde hata: `androidx.test.espresso.base.RootViewPicker$RootViewWithoutFocusException`; view root için `has-window-focus=false`. Tek geçen test: `sqliteLastGoodCopyRecoversCorruptPrimaryWithoutLosingPreviousRecord`.
- Test konumları: `CareerNewsVerificationSmokeTest.kt:49, :106, :240`.

### CI yeşilinin kök nedeni
- H1 workflow: `.github/workflows/h1-android-ui-smoke.yml`.
- Workflow, `reactivecircus/android-emulator-runner` action'ını commit `a421e43855164a8197daf9d8d40fe71c6996bb0d` ile pinliyor.
- Action'ın aynı commitindeki `src/script-parser.ts` çok satırlı metni satır sonlarından bölüyor (`split(/\\r\\n|\\n|\\r/)`) ve boş satırları çıkarıyor.
- Aynı action'ın `src/main.ts` dosyası her parçayı ayrı `sh -c` çağrısında çalıştırıyor.
- Mevcut workflow script'i:
  - satır 1: `set -euo pipefail`
  - satır 2: `gradle connectedDebugAndroidTest ... | tee h1-emulator.log`
- Bu nedenle shell seçenekleri ikinci satıra taşınmıyor. İkinci satırda pipe sonucu `tee`nin 0 kodu oluyor; Gradle'ın başarısız çıkışı action'a aktarılmıyor. XML ve `BUILD FAILED` bu sonucu doğruluyor.
- Action kaynakları:
  - https://github.com/ReactiveCircus/android-emulator-runner/blob/a421e43855164a8197daf9d8d40fe71c6996bb0d/src/script-parser.ts
  - https://github.com/ReactiveCircus/android-emulator-runner/blob/a421e43855164a8197daf9d8d40fe71c6996bb0d/src/main.ts

### Zorunlu düzeltme
1. Çok satırlı shell durumuna güvenme. Tek bir shell giriş noktası kullan: ör. workflow `script` alanında tek satır `bash scripts/run-h1-emulator-tests.sh`; betiğin içinde `set -euo pipefail` ve Gradle çağrısı/pipe olsun.
2. Runner sonrasında ayrı bir rapor-kontrol adımı çalıştır: XML yoksa, beklenen test sayısı 4 değilse, `failures>0`, `errors>0` veya beklenmeyen `skipped>0` ise workflow'u başarısız yap. Bu kontrol artifact yüklemesinden önce ve `if: always()` koşullu upload adımından ayrı olmalı.
3. CI için negatif doğrulama yap: kasıtlı başarısız bir instrumentation testi veya test raporundaki failure ile workflow kırmızıya dönmeli.
4. Sonraki gerçek koşuda H1 emülatör sonucu ancak XML 4/4 PASS ve CI kırmızı/yeşil sonucu XML ile tutarlıysa kabul edilir.
5. Üç `RootViewWithoutFocusException` ayrıca çözülmelidir. Espresso odağı hatasını yutmak, testi `assume`/skip etmek veya sadece rapor kontrolünü kaldırmak kabul edilmez. Önce emulator/window focus/Activity lifecycle logları incelenmeli.

## C1 resmî kaynak araştırması — 26.11.2026

Aşağıdaki yeni adaylar resmî PDF veya kurum duyurusunda bulundu. Bunlar **Worker feed'e yazılmadı ve 12/55 sayımına eklenmedi**; önce mevcut kayıtlarla exact duplicate, sourceId ve şehir alanı kontrolü yapılmalıdır. Kontenjan sayısı ilan kartı sayısı değildir.

| Kurum | Resmî kaynakta doğrulanan içerik | Başvuru sonu | Sayım kararı |
|---|---|---|---|
| Savunma Sanayii Başkanlığı | 26 proje bazlı sözleşmeli personel için bir resmî ilan. Dört yıllık lisans mezuniyeti, son iki yılda YDS İngilizce en az 70 (ilan metnindeki muafiyetler hariç) ve ilgili uzmanlık alanında en az 5 yıl tecrübe şartı. | 21.12.2026 23:59 | Bir ilan adayı; 26 kontenjan diye 26 ilan sayılmaz. |
| Hazine ve Maliye Bakanlığı / Hazine Kontrolörleri Kurulu | En fazla 10 Stajyer Hazine Kontrolörü için bir resmî sınav ilanı. İlgili lisans bölümleri; 2025/2026 KPSSP17/P18/P22/P23/P28/P47/P48 puanlarından en az 80; 1.1.2026 itibarıyla 35 yaşını doldurmamış olma. | 04.12.2026 | Bir ilan adayı; 10 kontenjan diye 10 ilan sayılmaz. Yazılı sınav tarihi 20.12.2026; bu başvuru son tarihi değildir. |
| Bartın Üniversitesi | 20.11.2026 tarihli akademik personel duyurusunda Öğretim Görevlisi alımı var. Erişilen resmî PDF arama çıktısında Bartın MYO Turizm ve Otel İşletmeciliği programına 1 Öğretim Görevlisi: ilgili lisans + aynı alanlardan tezli yüksek lisans + lisans sonrası 5 yıl deneyim ve gastronomi çalışmaları şartı görünüyor. Sayfanın öğretim üyesi kadroları ayrıca ayrıntılı sayılmadı. | 04.12.2026 23:59 | En az bir pozisyon adayı; bütün akademik kadrolar sayılmadı. Üniversitenin 2026/04 sözleşmeli personel ilanı ayrı bir duyurudur ve aynı kaynak/kurum olarak sayılmalıdır. |

Resmî linkler:
- SSB duyurusu: https://www.ssb.gov.tr/duyuru/proje-bazli-sozlesmeli-personel-alimi-giris-sinavi--IlsylX
- SSB PDF: https://www.ssb.gov.tr/storage/img/proje-bazli-sozlesmeli-personel-alimi-giris-sinavi-ilan-metni.pdf
- Hazine Kontrolörleri PDF: https://ms.hmb.gov.tr/uploads/2026/11/Ilan-Metni-Internet-Sitesi-491a08379759c79c.pdf
- Bartın akademik duyuru: https://akademiktayin.bartin.edu.tr/duyurular/ogretimuyesi202603veogretimelemanialimilani202603.html
- Bartın öğretim elemanı PDF: https://cdn.bartin.edu.tr/onceinsan/db33d57462be19ab33ef1c31c9e89097/20261120417removed.pdf
- Bartın 2026/04 sözleşmeli personel duyurusu: https://dinkulturu.bartin.edu.tr/duyurular/sozlesmelipersonelalimilani202604.html

Tarih alanları:
- SSB duyuru başlığı tarihi: 12.11.2026; başvuru 20.11.2026 10:00'da açılır. `publishedAt` yalnız resmî duyuru tarihi olarak kaydedilebilir.
- Hazine PDF'de ayrı bir sayfa yayın zamanı teyit edilmedi; `publishedAt=null` bırakılmalı. Başvuru penceresi 23.11.2026–04.12.2026; 20.12.2026 yazılı sınav günüdür.
- Bartın duyurusu 20.11.2026 tarihli; son başvuru 04.12.2026 23:59; 11.12.2026 giriş sınavıdır.
- Sayıştay 16 pozisyonluk duyurunun son başvurusu 25.11.2026 23:59:59 olduğundan 26.11.2026 itibarıyla `Süresi geçti`; açık ilan toplamına alınmaz.
- Yeni kayıtların `collectedAt` zamanı ingestion anında yazılmalı, `firstSeenAt` kaydın ilk başarılı eklenme anında atanmalı; yayın zamanı ve toplama/ilk görülme zamanı birbirine kopyalanmamalı.
- Kariyer Kapısı için kazıma yapılmaz; resmî duyurudaki başvuru yoluna yönlendirilir.

## C1 güncel kapı / yeniden kabul sınırı

- `docs/HARITA.md` ve `docs/NOT_C1.md` içinde son kaydedilen proje-geneli kamu envanteri: **12 resmî kaynak /55 doğrulanmış açık ilan /45 ilan eksiği**. Bu envanter 10 kaynak koşulunu karşılar ama 100 ilan koşulunu karşılamaz: **RED**.
- Bu sayı Worker'ın production feed'ine yayınlandığını kanıtlamaz. C1-1 teknik exact feed raporunda 26 kaynak katalogu, 17 fresh /9 stale, 161 ham satır; 47 doğrulanmış açık kart ve 114 deadline-unknown özel aday görüldü. Bu ayrı teknik ölçümdür; iki sayım doğrudan toplanmamalı.
- Kamu ilanları ayrı tutulur; 0 değil. Kaynak bazlı son iyi veri korunur; H2 cross-feed exact duplicate için kurum+pozisyon+şehir+deadline alanları gerekir. Şehir yoksa otomatik birleştirme yok.
- PR #11 (jarvis-cloud H2 entegrasyon) açık/taslak ve production GET/D1 kanıtı yok.
- PR #12 (Kariyer QA/RAG) açık/taslak; son exact HEAD `1692930b60b952589fdf5d849010a177e288bfb2`, run #45697424481 FAILURE; job kaydında yalnız `syntax-and-unit` ve steps=null göründü. Bu sonuç testlerin çalışıp başarısız olduğunu değil, CI job'unun test aşamasına geçmediğini gösteriyor. CI PASS sayılmaz.
- Worker authenticated production GET/POST, D1 kalıcılığı ve deploy kanıtı yok; `JARVIS_WORKER_URL` / `JARVIS_WORKER_TOKEN` eksik. Local D1 smoke production kanıtı değildir.
- H1 PR #71'de source-grounded evidence / human review / append-only corrections kapsamı mevcut olsa da emülatör exact-head testleri başarısız; H1 kabulü ve C1/Android entegrasyonu ayrı kapılardır.

## SONRAKİ ADIM

1. H1 için tek satır runner script + XML gate düzeltmesi görevi aç; gerçek UI focus problemini ayrıca çöz.
2. PR #71 yeni exact HEAD için APK build ve emulator XML sonuçlarını birlikte tekrar incele; başarısız XML varken yeşil Actions etiketine güvenme.
3. SSB, Hazine ve Bartın duyurularını Worker feed'e eklemeden önce exact duplicate/şehir/son başvuru/kurum kaynağı kimliği ile doğrula.
4. Worker production authenticated GET/D1 ve 10/100 QA tamamlanana kadar C1 **RED** kalır. PR #11/#12 taslak kalır; merge/deploy yapılmaz.


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
