# H1 QA kişisel veri ve gizli anahtar freni

**Durum:** YAPILMADI — inceleme görevi açıldı; mevcut H1 CI koşusu sürerken kod değiştirilmedi.

## Bulgu
`CareerNewsQuestionAnswer` çevrimdışı, tek haber kaydındaki iddia/kanıt/geçmişle cevap veriyor ve soruyu depoya yazmıyor. Mevcut kişisel veri freni e-posta, telefon, 11 haneli numara ve bazı KPSS/CV/e-Devlet ifadelerini tanıyor. Mevcut test yalnızca "Benim KPSS puanım ... ve telefonum ..." örneğini kapsıyor.

Eksik test örnekleri:
- "KPSS sonucum 82" / "sınav puanım" gibi kişisel sınav sonucu ifadeleri
- IBAN ve banka hesap bilgisi
- Bearer/API/GitHub token biçimleri (yalnızca sentetik test sabitleri; gerçek anahtar kullanılmaz)
- "e-Devlet giriş/parola" ve CV/özgeçmiş içeriğinin farklı yazımları

Bu ifadeler için mevcut kodun kesin olarak sızdırma yaptığı iddia edilmiyor: QA motoru ağ kullanmıyor ve soruyu kaydetmiyor. Risk, kullanıcının açıkça gizli/kişisel bir değer içeren soruyu güvenli şekilde reddettiğine dair yeterli kapsama/test bulunmaması.

## Kabul koşulları
1. E-posta, telefon, TCKN, IBAN, KPSS sonucu, e-Devlet parolası, CV/özgeçmiş ve API/Bearer token içeren sorular `ABSTAINED` verir.
2. Testler sadece sentetik ve sahte örnek kullanır; gerçek kimlik/hesap/erişim bilgisi kullanılmaz.
3. Normal haber soruları (kaynak, yayın tarihi, iddia, kanıt, çelişki, düzeltme) cevaplanmaya devam eder.
4. Gizli değerin kendisi loga, SQLite'a, analytics'e veya hata metnine yazılmaz; yalnız red durumu gösterilir.
5. Yanlış pozitif kontrolü eklenir: teknik haber içindeki genel "token", "hesap", "puan" kelimeleri tek başına soruyu gereksiz yere reddetmemeli; bağlam/biçim doğrulaması kullanılır.
6. Unit test sayısı ve exact-head CI bağlantısı not edilir.

## Değişmeyecekler
- Canlı web/API çağrısı, üçüncü taraf modele gönderme veya kullanıcı prompt'unu uzaktan işleme eklenmez.
- Token/CV/KPSS/e-Devlet verisi saklanmaz veya talep edilmez.
- H1 haber kayıt şeması ve kanıt geçmişi bu görev kapsamında değiştirilmez.
- Aktif emülatör CI koşusu bitmeden aynı PR'a commit atılıp concurrency ile koşu iptal edilmez.

## SONRAKİ
H1 emülatör XML kararı alındıktan sonra ayrı küçük commit ile kişisel veri fren testlerini güçlendir; workflow path filtresine ilgili soru-cevap kaynağı ve unit-test dizinini de ekle.
