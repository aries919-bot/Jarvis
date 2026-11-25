# Aries İş & Kariyer QA/RAG — 25.11.2026

## Amaç ve sınır
Bu iş, Aries İş & Kariyer için kaynaklı soru-cevap katmanı kurar. Film/tema alanlarına girmez. Haber doğrulama verisi ayrı kalır; bu PR yalnızca kariyer feed'i üzerinde çalışır.

## PR
- PR #12: https://github.com/aries919-bot/jarvis-cloud/pull/12
- Branch: `feat/career-qa-rag-20261125`
- Son HEAD: `d49de634b9e07030c0117039b62027b76405b435`
- Base: `fix/c1-h2-final-integration-20261125` / PR #11 HEAD `b1415d6a76fd3856c60cfec891a575c3bef0341e`
- Draft, açık, deploy yok.
- GitHub diff yeniden sayımı: 4 dosya / +992 / -20 (PR API); dosyalar `src/career-qa.js`, `src/index.js`, `tests/career-qa.test.mjs`, `.github/workflows/validate.yml`.
- Test dosyası tanım sayımı: `grep /^test(/ tests/career-qa.test.mjs` karşılığı 24 test. Bu sayı testlerin geçtiği anlamına gelmez.

## Uygulanan mimari
1. `POST /kariyer-sor`: mevcut Worker Bearer `JARVIS_TOKEN` doğrulaması; yalnız `{question}` alanını kabul eder. CV/KPSS profili/sohbet geçmişi gövdesi kullanılmaz.
2. Kaynak kapısı: yalnız güncel feed içindeki `status=ok`, `stale=false`, `deadlineStatus=open`, `freshnessStatus=Doğrulandı`, geçerli gelecek son tarih, HTTPS resmî kaynak ve çözülmüş mükerrerlik kayıtları.
3. Sağlayıcı allowlist:
   - Kamu/Kura/KPSS/KPSS'siz/Kamu bağlı şirket: `iskur-esube-json` veya `h2-official-announcement`.
   - Büyük özel: yalnız `jobs.lever.co` üzerinde Lever veya `jobs.ashbyhq.com` üzerinde Ashby sağlayıcısı.
   - Genel HTML/üçüncü taraf kaynaklar bu QA havuzuna alınmaz.
4. Retrieval: Türkçe normalizasyon ve eş anlam, BM25 lexical arama, Cloudflare BGE-M3 embedding, Reciprocal Rank Fusion. Retrieval aday havuzu en fazla 100, cevap kaynakları en fazla 5 kayıt.
5. Generation: Gemma 4 26B A4B yalnız seçilmiş kaynak kayıtları ile; thinking kapalı, sıcaklık 0. AI capacity dolu ise `rejectIfBusy` reddi yakalanıp BM25 fallback'e dönülür.
6. Son tarih, şart ve başvuru yolu gibi kesin sorular doğrudan kayıttan çıkarılır. Yayın tarihi null ise null kalır; deadline yayın tarihi yerine kullanılamaz.
7. Kanıt eşiği yoksa/konu dışıysa `abstained`; model ya da embedding çalışmazsa deterministik/BM25 fallback.
8. Kaynak cevabında resmi HTTPS URL, kurum, pozisyon, kategori, publishedAt, deadline, collectedAt ve firstSeenAt ayrı döner.

## Gizlilik
- Kişisel KPSS puanı, CV/özgeçmiş metni, T.C. kimlik, telefon, e-posta ve yaygın token/anahtar biçimleri model çağrısından önce reddedilir.
- Soru ve token loglanmaz. Token yeni bir yerde saklanmaz; mevcut Authorization header kullanılır.
- Kullanıcı CV/KPSS/e-Devlet profili, geçmiş sohbet veya kişisel kayıt modele ve D1'e gönderilmez.
- Kaynak duyuruları güvenilmeyen veri kabul edilir; içerikteki talimatlar izlenmez.
- Cloudflare model belgelerinde BGE-M3 çok dilli embedding ve sıfır veri saklama olarak listelenir; Gemma 4 çok dilli metin üretimi sağlar. Model çağrıları ücretsiz varsayılmaz; yayımlanan liste fiyatları BGE-M3 için 1M input token başına 0.0118 USD, Gemma 4 için 1M input token başına 0.10 USD ve output token başına 0.30 USD. Gerçek maliyet/kapasite ölçümü henüz yok.
- Resmî belgeler: https://developers.cloudflare.com/workers-ai/models/bge-m3/ ; https://developers.cloudflare.com/workers-ai/models/gemma-4-26b-a4b-it/ ; https://developers.cloudflare.com/workers-ai/features/reject-if-busy/

## CI kanıtı ve engel
- PR #12 son HEAD `d49de634b9e07030c0117039b62027b76405b435`.
- Son yedi workflow koşusu: #45692460706, #45692517637, #45692608933, #45692642608, #45692792848, #45692810534, #45692919011.
- GitHub REST her koşuda tek job ve `steps=[]` döndürüyor; job 2–3 saniyede FAILURE. Örnek son run #45692919011 job `139482779972`, start 20:13:48Z, end 20:13:50Z, step count 0.
- Log indirici BlobNotFound döndürüyor; check-run yalnız 1 annotation sayısı bildiriyor, annotation içeriği bağlı GitHub aracıyla alınamıyor.
- Sonuç: CI test koduna ulaşmıyor; bu nedenle kod testi başarısızlığı veya başarı şeklinde sınıflandırılamaz. Kod testleri çalıştırılmış sayılmaz. Tekrarlı commit/rerun döngüsü durduruldu; runner/job başlangıç engeli ayrıca çözümlenmeden PR merge/deploy yok.

## Test tanımları — 24
Kapsam: provider/source eligibility, private Lever allowlist, public provider red, kaynak bazlı stale izolasyonu, geçersiz tarih, Türkçe BM25, unrelated abstention, RRF, embedding response parsing, deadline null publishedAt, şartların deterministik çıkarımı, kişisel ID/telefon/email/token/CV/KPSS blokları, AI capacity fallback, Worker auth, route body validation, stale feed, unsupported method, stored feed citation.

## Bağımlılık
PR #12, PR #11'e stack edilmiştir. C1 PR #11 henüz 12 kamu kaynağı /55 doğrulanmış açık ilan (45 eksik) seviyesinde; production Worker GET/D1, city kanıtlı dedup ve 10/100 kapısı RED. QA motoru eksik C1 kaynaklarını üretmez.

## Ayrı iş: H1 Haber QA
H1 yerel SQLite `CareerNewsVerificationRecord` içindeki iddia/kanıt/arşiv verileri için ayrı bir soru-cevap katmanı hazırlanacak. Bu katman:
- yalnız aktif story ve kayıtlı claim/evidence üzerinden çalışacak;
- doğrulanmış/çürütülmüş insan incelemesi kararını, karşıt kanıtı ve yayın tarihini birbirinden ayıracak;
- her cevapta kanıt URL'si ve checkedAt gösterecek;
- kanıt yoksa/insan incelemesi yoksa abstain edecek;
- kariyer feed'i ile H1 haber SQLite kayıtlarını birleştirmeyecek.
Bu haber QA henüz uygulanmadı.

## KARAR
**PR #12: RED / test runner engeli.** Kod prototipi ve 24 test tanımı var; GitHub CI adım başlatmadığından test kanıtı yok. Merge/deploy yok.
