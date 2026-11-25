# H1 şema uyum düzeltmesi — 25.11.2026

## Amaç
H1 haber kartı kategorilerini kullanıcının belirlediği değerlerle eşitlemek ve kaynakta yayın tarihi bulunmayan ancak deadline'ı ve başvuru açıklığı doğrulanmış ilanlarda publishedAt=null değerini korumak.

## Bulgular
- Önceki şema Büyük özel şirket ve Kamu bağlı büyük şirket etiketlerini kullanıyordu; doğru değerler Büyük özel ve Kamu bağlı şirket.
- publishedAt=null kabul edilse de freshnessStatus=Doğrulandı kayıtları reddediliyor ve sayaçtan çıkarılıyordu. H2 canlı çıktısında Balıkesir Üniversitesi ve TGA için yayın tarihi kaynakta yok; deadline ve açık başvuru doğrulanmış.

## Değişiklik
- scripts/h1_schema.py: kategori enumları düzeltildi; yayın tarihi bulunmaması, diğer doğrulama koşulları sağlanan işi sayaç dışı bırakmıyor.
- scripts/test_h1_schema.py: tam kategori değerleri ve publishedAt=null açık iş ilanı için regresyon testleri eklendi.
- docs/HABER_SEMA.md: filtre adları ve tarih kuralı düzeltildi; collectedAt/firstSeenAt yayın tarihi yerine geçemez.

## Kabul
- H1 şema testleri CI'da yeşil.
- Geçmiş deadline, gelecekte açılacak başvuru, stale kaynak, kura/sonuç ve hatalı tarih regresyonları geçmeye devam eder.
- C1 kaynak toplama, Worker veya deploy değişikliği yok.