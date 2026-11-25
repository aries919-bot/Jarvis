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

### QA-01 — H1 veri modeli ve SQLite bütünlüğü
**Sorumluluk:** Haber iddiası, kanıt, insan incelemesi, reviewHistory, correctionHistory, last-good kayıt ve bozuk primary davranışı.
- Durum: KOD İNCELEMESİ TAMAM; CI BEKLENİYOR.
- Kanıt: PR #71 HEAD `6b392a3aa02d42700743657350d081d7f7ad6aeb`; 20 model + 10 store testi.
- Kabul: append-only geçmiş, sessiz overwrite yok, UTC ve nullable tarih kuralları, bozuk primary'de veri kaybı yok.
- Takip: [PR #71](https://github.com/aries919-bot/Jarvis/pull/71).

### QA-02 — H1 Android UI / emülatör / cihaz
**Sorumluluk:** Gerçek Activity akışı, publishedAt formu, eski kanıt ayrıntısı, SQLite round-trip, test runner.
- Durum: EMÜLATÖR IN_PROGRESS; APK/unit PASS.
- Kanıt: [APK/unit #45679523180](https://github.com/aries919-bot/Jarvis/actions/runs/45679523180) SUCCESS; [emülatör #45679523114](https://github.com/aries919-bot/Jarvis/actions/runs/45679523114) IN_PROGRESS.
- Kabul: exact HEAD için emülatör PASS; ardından gerçek Android telefonda test. Timeout/runner sorunu PASS sayılmaz.
- Takip: PR #71 açık/draft; merge yok.

### QA-03 — C1 kaynak politikası ve ağ güvenliği
**Sorumluluk:** Kaynak allowlist, LinkedIn/Indeed yasağı, UA, official/secondary ayrımı, robots/timeout davranışı.
- Durum: İNCELEME PASS; MERGE SIRASI BEKLİYOR.
- Kanıt: PR #70 HEAD `7ab7aec5e5fa34209fec5c56d20a3c468af89a78`; [CI #45677312902](https://github.com/aries919-bot/Jarvis/actions/runs/45677312902) SUCCESS; 4 dosya, +75/-20 (GitHub PR metadata).
- Kabul: tüm HTTP istekleri tanımlı UA; LinkedIn/Indeed katalog/sorgu yok; resmi olmayan katalog girdileri primary alert üretemez.
- Takip: PR #70, H1 sırası kapanmadan merge edilmez.

### QA-04 — C1 canlı veri, kamu sayımı ve Worker
**Sorumluluk:** e-Şube/H2 input, source bazlı stale, tarih alanları, mükerrer, kamu/özel ayrımı, production GET/D1.
- Durum: RED.
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
- Kesin bulgu: MainActivity.kt onCreate satır 140–145 uygulama açılır açılmaz RECORD_AUDIO + CALL_PHONE + READ_CONTACTS + POST_NOTIFICATIONS topluca istiyor. MainActivity taramasında CALL_PHONE / READ_CONTACTS yalnız izin listesinde; ACTION_CALL / ACTION_DIAL / Phone.CONTENT_URI / Phone.query kullanımı bulunamadı. ContactsContract.CommonDataKinds.Phone import'u var ama çağrı yok. Bunlar kaldırılmadan önce tüm proje için ikinci arama yapılmalı; ilk açılışta istenmesi gereksiz ve kullanıcı güvenini zedeleyen bir izin akışı.
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
- [x] QA-06 ilk izin kullanım taraması: CALL_PHONE ve READ_CONTACTS açılışta isteniyor; ilgili API kullanım izi bulunamadı. Tam proje çapı son tarama ve güvenli düzeltme ayrı PR.
- [ ] QA-04 45 ek kamu ilanı + Worker GET/D1 + city kanıtlı dedup.
- [ ] QA-05 CareerMonitor extraction PR.
- [ ] Son PR diff + exact-head CI + test sayımı çapraz kontrolü.

## Genel karar
**RED / genel kabul yok.** Altı QA iş akışı tanımlandı; beşinde somut kanıt toplandı. Emülatör, izin kullanımı, C1 100 ilan ve Worker/D1 kapıları açık. Kod değişikliği sadece açık kabul kapıları kapandıktan sonra yapılır.
