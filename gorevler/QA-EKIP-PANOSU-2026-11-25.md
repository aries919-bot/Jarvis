# Aries QA ekip panosu — 25.11.2026

## Çalışma modeli
Bu oturumda bağımsız alt ajan başlatma aracı bulunmuyor. Bu nedenle aşağıdaki roller ayrı QA iş akışları olarak atanmıştır; tüm bulguları koordinatör olarak ben doğrularım. Harici ajan çalıştırıldığı iddia edilmez. Bağımsızlık için her rol kendi kapsamındaki dosya/kanıtı inceler; son karar çapraz kontrolden sonra verilir.

## Hızlandırma kuralı
- Birbirinden bağımsız QA işleri aynı anda yürütülür.
- Aynı dosya/branch üzerinde eşzamanlı kod yazılmaz.
- Bir CI sürerken yalnız farklı dosya/depodaki bağımsız incelemeler yapılır.
- Kırmızı bulgu için tekrar tekrar aynı testi çalıştırmak yerine kök neden ve tek takip işi açılır.
- Her iş: dosya/commit/CI kanıtı + yeniden sayılmış test/ilan sayısı + KARAR.
- Büyük refaktör yok; küçük PR, tek sorumluluk, geri alınabilir değişiklik.

## Atamalar

### QA-07 — PR diff, secret ve yasak kaynak denetimi
**Sorumluluk:** PR diff scope, hard-coded secret, kişisel veri sızıntısı, yasak platform ve browser UA taraması.
- Durum: TAMAMLANDI.
- PR #71: 10 dosya / +2.578 / -26; secret pattern 0; forbidden LinkedIn/Indeed URL 0; fake browser UA 0. publishedAt literal yalnızca iki tarih doğrulama testinde.
- PR #70: 4 dosya / +75 / -20; secret pattern 0; fake browser UA 0; AriesKaynakTara/1.0 eklenmiş. LinkedIn/Indeed dizeleri yalnızca yasak kaynakları reddeden test assertion'larında.
- PR #71 diff'te manifest değişikliği yalnız Activity kaydı için; izin ekleme/değiştirme olmadığı ayrıca doğrulanmalı.

### QA-08 — CI hızı ve tekrarlanabilirlik
**Sorumluluk:** Gradle tekrar derleme, cache, timeout, test raporu ve artifact doğrulaması.
- Durum: İLK İNCELEME TAMAM; OPTİMİZASYON TAKİPTE.
- android.yml iki ayrı Gradle çağrısı kullanıyor: testDebugUnitTest ve assembleDebug; tek Gradle çağrısında birleştirme, aynı compile graph'ını tekrar çalıştırmayı azaltabilir.
- H1 emülatör workflow job timeout 35 dakika; step 5 yaklaşık 18 dakikadır IN_PROGRESS, log endpoint BlobNotFound. Timeout dolmadan fail/stall kararı yok.
- H1 workflow test raporu upload adımı zaten if: always() kullanıyor; bu koruma PASS. Ana Android build workflow'unda iki Gradle çağrısını tek çağrıya birleştirme optimizasyonu ayrı takip işi.

## Atamalar

### QA-01 — H1 veri modeli ve SQLite bütünlüğü
**Sorumluluk:** Haber iddiası, kanıt, insan incelemesi, reviewHistory, correctionHistory, last-good kayıt ve bozuk primary davranışı.
- Durum: KOD İNCELEMESİ TAMAM; CI BEKLENİYOR.
- Kanıt: PR #71 HEAD `6b392a3aa02d42700743657350d081d7f7ad6aeb`; 20 model + 10 store testi.
- Kabul: append-only geçmiş, sessiz overwrite yok, UTC ve nullable tarih kuralları, bozuk primary'de veri kaybı yok.
- Takip: [PR #71](https://github.com/aries919-bot/Jarvis/pull/71).

### QA-02 — H1 Android UI / emülatör / cihaz
**Sorumluluk:** Gerçek Activity akışı, publishedAt formu, eski kanıt ayrıntısı, SQLite round-trip, test runner.
- Durum: EMÜLATÖR IN_PROGRESS; APK/unit PASS. 19:12:37Z kontrolünde job 20 dk sürmüştü; step 5 hâlâ in_progress, log URL BlobNotFound. Job timeout 35 dk; 19:27:22Z öncesi fail varsayılmaz.
- Kanıt: [APK/unit #45679523180](https://github.com/aries919-bot/Jarvis/actions/runs/45679523180) SUCCESS; [emülatör #45679523114](https://github.com/aries919-bot/Jarvis/actions/runs/45679523114) IN_PROGRESS.
- Kabul: exact HEAD için emülatör PASS; ardından gerçek Android telefonda test. Timeout/runner sorunu PASS sayılmaz.
- Takip: PR #71 açık/draft; merge yok.

### QA-03 — C1 kaynak politikası ve ağ güvenliği
**Sorumluluk:** Kaynak allowlist, LinkedIn/Indeed yasağı, UA, official/secondary ayrımı, robots/timeout davranışı.
- Durum: MERGED; exact-head CI + scoped öz-QA ONAY tamam.
- Merge commit: 09f98b324a9a1f38dbbc25853ec2eba2fc544db9; base kariyer-modulu.
- Kanıt: PR #70 HEAD `7ab7aec5e5fa34209fec5c56d20a3c468af89a78`; [CI #45677312902](https://github.com/aries919-bot/Jarvis/actions/runs/45677312902) SUCCESS; 4 dosya, +75/-20 (GitHub PR metadata).
- Kabul: tüm HTTP istekleri tanımlı UA; LinkedIn/Indeed katalog/sorgu yok; resmi olmayan katalog girdileri primary alert üretemez.
- Takip: [PR #70](https://github.com/aries919-bot/Jarvis/pull/70) merged; source policy base'e girdi.

### QA-04 — C1 canlı veri, kamu sayımı ve Worker
**Sorumluluk:** e-Şube/H2 input, source bazlı stale, tarih alanları, mükerrer, kamu/özel ayrımı, production GET/D1.
- Durum: RED; resmî ilan keşif dalgası 1 tamamlandı.
- Ek resmî kaynak araştırması: 25.11 itibarıyla açık 7 duyuru adayı (Sayıştay, Zafer, TGA, HMB, Anadolu, Bartın, SSB); 2 gelecek (ATK 181 pozisyon, YÖKAK 5 pozisyon). Mevcut H2'de Zafer/TGA/Anadolu zaten var; yeni kaynak adayı Sayıştay/HMB/Bartın/SSB = 4. Henüz feed'e eklenmedi; 55 sayısı değişmedi.
- Kontenjan toplamı ilan kartı sayısı değildir. Exact duplicate ve source mapping yapılmadan 100 eşiğine ekleme yok.
- Araştırma kaydı: gorevler/C1-RESMI-ILAN-ARASTIRMASI-2026-11-25.md.
- Kanıt: private PR #11 HEAD `b1415d6a76fd3856c60cfec891a575c3bef0341e`; public exact-head QA #45651444155 SUCCESS.
- Yeniden sayım: 12 kamu kaynağı /55 doğrulanmış açık kamu ilanı; 100 eşiğine 45 eksik. 35 toplam kaynak, 27 fresh, 8 stale; 116 unknown deadline; 0 geçmiş deadline.
- Kritik: H2 input city null; canlı cross-feed exact dedup kanıtlanamıyor. Worker production authenticated GET/POST + D1 PUBLISH SKIPPED.
- İlave entegrasyon riski: PR #11 base branch `fix/c1-source-category-after-timestamp`; main'e bağımsız entegrasyon değildir, base zinciri çözülmeden merge edilmez.
- Takip: [Cloud PR #11](https://github.com/aries919-bot/jarvis-cloud/pull/11).

### QA-05 — Mimari ve dosya sınırları
**Sorumluluk:** Büyük Kotlin dosyaları, bağımlılık haritası, küçük güvenli refaktör planı.
- Durum: ENVANTER TAMAM; KOD REFAKTÖRÜ BEKLİYOR.
- Kanıt: H1 HEAD ağacı 91 kayıt /36 Kotlin; CareerActivity 155,317 byte (~2,795 satır), CareerMonitor 119,687 byte (~2,049 satır), MainActivity 75,911 byte (~1,802 satır), Ai.kt 34,226 byte (~735 satır).
- CareerMonitor sınıf sınırları: SourceCatalog, Eligibility, ListingParser, SearchEngine, RecruitmentTracker, Scheduler ve 3 Worker sınıfı.
- Kabul: ilk PR yalnız class-boundary extraction; paket/behavior değişmez; unit + APK + kaynak policy testleri yeşil.
- Takip: H1 ve PR #70 sırasından sonra ilk refaktör PR'ı.

### QA-06 — Güvenlik, izinler ve araç zinciri
**Sorumluluk:** Manifest izinlerinin gerçek kullanımını doğrulama, kişisel veri, token, diff aktarımı, eski ajan talimatları.
- Durum: İLK BULGULAR VAR; AYRI GÜVENLİK PR'I GEREKİYOR.
- Kesin bulgu: MainActivity.kt onCreate satır 140–145 uygulama açılır açılmaz RECORD_AUDIO + CALL_PHONE + READ_CONTACTS + POST_NOTIFICATIONS topluca istiyor. Tam Kotlin kaynak taramasında CALL_PHONE ve READ_CONTACTS kullanımı Commands.kt'de bulundu (ACTION_CALL/ACTION_DIAL ve ContactsContract.Phone.CONTENT_URI); izinler kullanılmıyor değil. Sorun, kullanıcı ilgili sesli komut/arama/kişi bulma özelliğini açmadan ilk açılışta istenmeleri. Just-in-time izin PR'ı önerilir.
- Ek bulgu: Reminders.kt AlarmManager.setAlarmClock çağırıyor; manifest USE_EXACT_ALARM içeriyor. Android resmi dokümanına göre setAlarmClock exact alarm izni gerektirir; USE_EXACT_ALARM otomatik verilir ama esasen çalar saat/takvim uygulamaları için sınırlıdır. Jarvis'in ana işlevi alarm değilse Play policy riski; SCHEDULE_EXACT_ALARM kullanıcı onayı veya inexact alarm/WorkManager alternatifi değerlendirilmeli.
- Bulgu: `denetle.py` diff'in ilk 12 KB'sini Groq'a gönderiyor; bu araç çalıştırılmayacak.
- Manifestte RECORD_AUDIO, CALL_PHONE, READ_CONTACTS, INTERNET, ACCESS_NETWORK_STATE, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED, SET_ALARM, USE_EXACT_ALARM, BLUETOOTH_CONNECT izinleri var. Her iznin kullanım gerekçesi/gerçek çağrısı tek tek kanıtlanmadı.
- Kabul: izin envanteri + kod kullanım yeri + kaldırma/regresyon testi; token/CV/KPSS/e-Devlet içeriği hiçbir test/log/harici servise gitmez.

## Koordinatör kontrol listesi
- [x] QA-01 model/store kod incelemesi.
- [x] QA-02 test senaryosu ve exact-head workflow incelemesi.
- [x] QA-03 kaynak/UA policy diff incelemesi.
- [x] QA-04 H2 city/dedup/Worker ve kamu sayımı denetimi.
- [x] QA-05 büyük dosya ve sınıf sınırı envanteri.
- [ ] QA-02 emülatör exact-head PASS ve telefon testi.
- [x] QA-06 Kotlin kaynak taraması: CALL_PHONE/READ_CONTACTS Commands.kt'de kullanılıyor; açılışta toplu istenmeleri UX/privacy sorunu. USE_EXACT_ALARM policy/ürün kategorisi riski; ayrı PR ve policy kontrolü.
- [ ] QA-04 45 ek kamu ilanı + Worker GET/D1 + city kanıtlı dedup.
- [ ] QA-05 CareerMonitor extraction PR.
- [ ] Son PR diff + exact-head CI + test sayımı çapraz kontrolü.

## Genel karar
**RED / genel kabul yok.** Altı QA iş akışı tanımlandı; beşinde somut kanıt toplandı. Emülatör, izin kullanımı, C1 100 ilan ve Worker/D1 kapıları açık. Kod değişikliği sadece açık kabul kapıları kapandıktan sonra yapılır.


### QA-09 — H1 eski PR çakışma denetimi
**Sorumluluk:** H1 haber claim/evidence PR dallarının mükerrer iş ve eski mimari taşıyıp taşımadığını kontrol etme.
- Durum: TAMAMLANDI — PR #69 MERGE EDİLMEMELİ.
- PR #69: 8 dosya / +1.572 / -35; PR #71: 10 dosya / +2.578 / -26.
- Ortak 6 dosya: build.gradle.kts, CareerActivity.kt, CareerNewsVerification.kt, CareerNewsVerificationStore.kt, CareerNewsVerificationTest.kt, CareerNewsVerificationStoreTest.kt.
- #69 yalnız SharedPreferences store + eski UI/links helper; #71 SQLite store, ayrı Activity, append-only review history, publishedAt formu ve 4 instrumentation testi getiriyor. #71 Activity Custom Tabs + HTTPS doğrulamasını zaten yapıyor.
- Karar: #69 taslak kalacak; #71 kabul edilip birleşirse #69 superseded olarak kapatılabilir. #69'dan bağımsız kod cherry-pick edilmiyor.
- Kanıt: [PR #69](https://github.com/aries919-bot/Jarvis/pull/69), [PR #71](https://github.com/aries919-bot/Jarvis/pull/71), rapor: [H1 PR çakışma incelemesi](H1-PR69-71-Cakisma-QA-2026-11-25.md).


### QA-02 — emülatör hata analizi + yeni merge-ref retesti
- İlk emülatör run #45679523114: FAILURE; artifact 15055314040 XML: 4 instrumentation / 1 PASS / 3 FAIL / 0 ERROR / 0 SKIP.
- 3 UI testi aynı RootViewWithoutFocusException ile başarısız; SQLite last-good recovery testi geçti. Logcat 177+ frame skip, 2.5–3.2 sn renderer jank, Activity PAUSED geçişi gösteriyor. Senkron SQLite onCreate load + koşulsuz save P2 performans riski; ayrı iş belgesine yazıldı.
- AndroidX Test issue #2248 aynı Trace.forceEnableAppTracing() NoSuchMethodError'u belgeliyor. Trace API 1.1.0'dan beri var; app'e tracing 1.2.0 eklendi. Kaynak: https://github.com/android/android-test/issues/2248 ; https://developer.android.com/reference/androidx/tracing/Trace
- İlk merge-ref doğrulama run #45685844306 testlere ulaşmadan eski PR API base.sha snapshot'ına takıldı; workflow kontrolü gerçek merge commit parent'larına çevrildi.
- Yeni HEAD 5df909fb1d0893aa0fb308f33b5fb8064aec8811; H1 merge-ref run #45685959506 ve Android APK/unit #45685959491 devam ediyor. Merge-ref verify PASS: parent1 canlı #70 merge 09f98b3..., parent2 yeni PR HEAD.
- Workflow: github.sha checkout, AOSP default API34 Pixel 2, dependencyInsight, 30 dk test timeout, log + XML/HTML artifact if: always().
- Karar: emülatör HALA RED; yeni run tamamlanmadan sonuç yok; telefon testi ayrıca gerekli.

### QA-02 güncel kanıt — 25.11.2026 19:34Z
- Yeni H1 HEAD `5df909fb1d0893aa0fb308f33b5fb8064aec8811`; GitHub diff 10 dosya / +2.607 / -26; testler 20 model +10 store +4 instrumentation =34.
- APK/unit [#45685959491](https://github.com/aries919-bot/Jarvis/actions/runs/45685959491) SUCCESS (unit + assemble + artifact).
- Merge-ref emulator [#45685959506](https://github.com/aries919-bot/Jarvis/actions/runs/45685959506): checkout/parent assertion SUCCESS; AndroidX dependencyInsight SUCCESS; instrumentation step IN_PROGRESS.
- Yeni workflow gerçek PR merge commit'ini test ediyor; ilk ebeveyn #70 merge SHA `09f98b324a9a1f38dbbc25853ec2eba2fc544db9`, ikinci ebeveyn PR HEAD. AOSP default API34 Pixel2.
- Önceki run #45679523114 XML: 4 test, 1 pass, 3 fail. Üç UI failure aynı window focus exception; no emulator PASS. Current emulator completion and physical phone remain required.
- QA-07 retest: 10 files; secret pattern 0; forbidden LinkedIn/Indeed URL 0; browser impersonation UA 0.
- H1 **RED/beklemede** until merge-ref emulator passes 4/4 and phone test done.
