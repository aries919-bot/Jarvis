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
- Cloud PR #12 açık/taslak; HEAD d49de634b9e07030c0117039b62027b76405b435; base PR #11 HEAD b1415d6a76fd3856c60cfec891a575c3bef0341e; 4 dosya / +992 / -20.
- Test dosyasında 24 test tanımı var; **çalıştırılmış test sayısı 0**. GitHub CI job başlatılamadığı için geçiş kanıtı bulunmuyor.
- Tasarım: Türkçe normalize + BM25 + Cloudflare BGE-M3 embedding + RRF; seçilmiş kaynak kayıtları üzerinden Gemma 4. Son tarih/şart/başvuru yolu gibi kesin alanlar deterministik; kanıt yoksa veya konu dışıysa abstain; AI kapasite doluysa BM25 fallback.
- Provider allowlist: kamu kategorileri yalnız iskur-esube-json / h2-official-announcement; Büyük özel yalnız jobs.lever.co Lever / jobs.ashbyhq.com Ashby. Stale, expired, unknown deadline ve possibleDuplicate kayıtlar cevap dışında.
- Gizlilik: CV/özgeçmiş, kişisel KPSS puanı, kimlik, telefon, e-posta, bearer/API token desenleri AI çağrısından önce engellenir. Kullanıcı profili/sohbet geçmişi/e-Devlet verisi D1 veya modele gönderilmez; soru/token loglanmaz.
- Son 7 CI run #45692460706, #45692517637, #45692608933, #45692642608, #45692792848, #45692810534, #45692919011: hepsi failure; her job steps=[] ve 2–3 saniyede kapanıyor. Log BlobNotFound; annotation içeriğine erişilemiyor. Bu kod testi sonucu değil; runner/job-start engeli. Tekrarlı commit/rerun döngüsü durduruldu. PR #12 merge/deploy yok.
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
