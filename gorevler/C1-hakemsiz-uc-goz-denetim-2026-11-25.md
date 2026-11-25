# C1 hakemsiz üç turlu denetim usulü — 25.11.2026

## Yetki ve sınır
Kullanıcı talimatı: bağımsız hakem bulunmadığı sürece C1 + Haber doğrulama çalışması durmayacak. ChatGPT her değişikliği üç ayrı gözle denetleyecek. Bu, bağımsız insan/Claude review veya GitHub formal APPROVE değildir. Sonuçlar yalnız "HAKEMSİZ ONAY" ya da "RED" olarak raporlanır; formal review varmış gibi gösterilmez.

- Göz 1 — Kapsam/diff: hedef dal, değişen dosyalar, yalnız görev kapsamı, beklenmeyen dosya/secret/kişisel veri taraması.
- Göz 2 — Teknik/doğruluk: kod akışı, hata yolları, tarih alanları, kaynak başına kayıt ve deadline yeniden sayımı, testlerin gerçekten ilgili HEAD'i kapsaması.
- Göz 3 — Güvenlik/işletim: robots-first, TLS açık, giriş/CAPTCHA yok, UA doğru, last-good kaynak izolasyonu, boş/403/500/timeout/eski veri testleri, production ve gerçek cihaz kanıtının ayrıştırılması.
- Üç gözden biri başarısız veya kanıtsızsa RED. Test PASS tek başına onay değildir.
- Merge/deploy yapılmaz; kullanıcının açık birleştirme onayı ve mevcut proje kuralları ayrıca geçerlidir.

## Başlangıçta yenilenen belgeler
- `docs/HARITA.md` — durum dalı, blob SHA `455032030fe9b4b1e013d2aabb00ac0523140811`.
- `docs/NOT_C1.md` — durum dalı, blob SHA `ff1f46bef5b84fb3ec2264e4149edc8b5ba8dd4b`.
- Not: HARITA'nın ilk bölümünde eski C1 ölçümleri bulunuyor; daha aşağıdaki 25.11 ekleri ve görev dosyalarıyla tarihleri karşılaştırılmadan eski sayılar güncel kabul edilmez.

## İlk üç-göz uygulaması — PR #61 ve #63
### PR #61 — H2 public source feed
- PR: https://github.com/aries919-bot/Jarvis/pull/61
- HEAD: `a952007c5428bfc926ebf618c6b2bee95497fefb`.
- Python Actions #45540255507: SUCCESS; job `138956767842`.
- H2 PR Actions #45540255466: reader test job `138956767296` SUCCESS; live reader/seed steps SKIPPED (PR event).
- Hakem Actions #45540255463: tests job `138956767546` SUCCESS; hakem job `138956811522` FAILURE at deterministic/optional AI review step. Bu otomatik RED/başarısızlıktır; hata nedeni bu kayıtla tek başına kesinleştirilmedi.
- Formal GitHub reviews: 0. Review threads: 0.
- Önceki exact-head live artifact #45540249638 ve yeniden sayım raporu ayrı görev kaydında. Son bilinen manuel recount: 19 kaynak kaydı; 17 fresh /2 stale; 13 açık doğrulanmış kart, 11 kurum, 125 pozisyon. Bu, 100 kart eşiğini karşılamaz.
- Üç göz sonucu: Göz 1/2 için güncel tam diff ve artifact satırları bu ilk taramada yeniden çekilmedi; Göz 3'te Hakem job başarısız. **RED / HAKEMSİZ ONAY YOK.**

### PR #63 — H1 schema correction
- PR: https://github.com/aries919-bot/Jarvis/pull/63
- HEAD: `4e26b83c2ad265cf89347617cccd272f7813c0cd`.
- Exact-head Actions #45628989453: contract job `139256772123` SUCCESS; 14/14 test PASS.
- Formal GitHub reviews: 0. Review threads: 0.
- Üç göz sonucu: test/kontrat kapısı PASS; tam diff ve güvenlik taraması bu ilk taramada yeniden yapılmadı. **Şimdilik RED/BEKLEME; 14/14 test tek başına hakemsiz onay değildir.**

## Devam koşulu
Her PR için tam diff, HEAD, dosya listesi, exact-head Actions, kaynak/artifact yeniden sayımı ve üç ayrı kontrol sonucu kaydedilir. Kanıt olmayan alan "doğrulanmadı" kalır. Kaynak eşiği 10 kaynak /100 doğrulanmış açık ilan; kamu sayımı ayrı tutulur. Eşik altındaysa C1 genel karar RED ve U1/UI aşamasına geçilmez.
