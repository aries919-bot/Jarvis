# PR #61 DÜZELT kararı — hakem kapısı diff sınırı

## Kullanıcı kararı
- PR #61 kararı: **DÜZELT**
- Hakem notu: yok
- Karar PR yorumuyla kaydedildi: https://github.com/aries919-bot/Jarvis/pull/61#issuecomment-7166885167
- Formal `REQUEST_CHANGES` GitHub review denemesi HTTP 422 ile reddedildi; bağlı hesap PR yazarı. Karar formal review olarak sunulamaz.

## Kanıt
- PR: https://github.com/aries919-bot/Jarvis/pull/61
- Güncel PR HEAD: `8769ece09b1f85fb0326cb38d582c2fa46e70609`
- Hedef dal: `cline/K1`
- PR diff: 72.234 bayt (GitHub fetch_pr sonucu).
- Güvenilir hedef-dal hakem betiği: `scripts/hakem_gate.py`, `MAX_DIFF_BYTES = 60_000`.
- `get_diff()` 60.000 baytı aşınca `RuntimeError("PR diff 60 KB sınırını aştı")` üretir.
- Hakem workflow run: https://github.com/aries919-bot/Jarvis/actions/runs/45540255463
- Hakem job: 138956811522, FAILURE; test job: 138956767546, SUCCESS.
- Hakem logu: `python scripts/hakem_gate.py` sonrasında RED ve exit code 1. Hata ayrıntısı güvenlik nedeniyle loga yazılmıyor.
- Kaynak kod + ölçülen diff boyutu, RED nedeninin diff sınırıyla uyumlu olduğunu gösteriyor. Bu bir test suite hatası değildir.

## Düzeltme planı
1. Hakem kapısındaki diff sınırı için ayrı, küçük bir PR hazırla; PR #61'in kendi güvenilir kontrolünü değiştirmesine güvenme. Hedef dalda çalışacak güvenilir checker güncellenmelidir.
2. Üst sınırın belirlenmesini gerçek PR boyutlarıyla gerekçelendir; sınırsız okuma yapma. Öneri: kontrollü 100 KB sınırı, response byte limit ve timeout korunarak.
3. Unit test ekle: sınır altı diff kabul edilir; sınırı aşan diff RED; byte sayımı UTF-8 üzerinden yapılır.
4. Test ve workflow exact-head CI sonuçlarını doğrula.
5. Hakem kapısı düzeldikten sonra PR #61 için yeni CI çalıştır.
6. PR #61'in üç göz denetimini tekrar et: kapsam/secret; test+ilan/tarih sayımları; robots/TLS/last-good/bozuk ağ.
7. C1 eşikleri ayrı: 10 kaynak/100 doğrulanmış ilan. Son ölçümde H2 19 kaynak kaydı, 11 kurum ve 13 açık doğrulanmış ilan; e-Şube 42 kart ayrı snapshot. Cross-feed tekilleştirme ve production Worker GET/D1 kanıtı yok. 100 eşiği karşılanmadı.
8. Kullanıcının yeni onayı olmadan PR merge/deploy etme.

## Üç göz ara sonucu
- Göz 1 — kapsam/güvenlik: PR açık ve draft; merge edilmedi. Formal review sayısı 0. Bu turda secret taraması tekrar çalıştırılmadı; temiz olduğu iddia edilmez.
- Göz 2 — teknik: test job başarılı; hakem job diff boyutu sınırında RED. PR diff 72.234 > 60.000.
- Göz 3 — operasyon: C1 canlı veri eşiği ve production Worker GET/D1 hâlâ kanıtlanmış değil; stale kaynaklar ayrı izlenmeli.
- Ara karar: **RED / DÜZELT bekliyor**. Bu teşhis merge izni değildir.

## Sonraki adım
Önce hakem diff sınırı için ayrı küçük PR; ardından PR #61 exact-head test ve üç göz denetimi.


## Ayrı hakem-kapısı düzeltme PR'ı
- PR #64: https://github.com/aries919-bot/Jarvis/pull/64
- Dal: `fix/hakem-diff-limit-20261125`, hedef `cline/K1`.
- HEAD: `7bc95985b870ae0311955221c725140f8904174b`.
- Diff: 2 dosya, +21/-2. `MAX_DIFF_BYTES` 100.000'e çıkarıldı; sınır altı ve üstü için iki mock tabanlı test eklendi.
- Compare sonucu: `ahead`, 2 commit; yalnız `scripts/hakem_gate.py` ve `scripts/test_hakem_gate.py`.
- PR taslak ve açık. Bu kontrol anında PR-triggered Actions run listesi boş; CI henüz kanıtlanmadı. Merge edilebilirlik henüz true değil.
- Secret scan/test CI/üç göz tamamlana kadar merge yok. PR #64 merge edilmeden PR #61'in hakem job'u eski 60 KB checker ile RED vermeye devam eder.
