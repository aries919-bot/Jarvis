# C1 — e-Şube kamu snapshot yenileme, 25.11.2026

## Amaç
12 saat sınırını aşan son e-Şube snapshot'ını, mevcut girişsiz resmî okuyucuyla yeniden üretmek.

## Değişiklik
Yalnız `.github/workflows/kamu-ilan-cek.yml` push branch filtresine geçici `review/c1-esube-refresh-20261125` dalı eklendi. Kod, robots kontrolü, HTTP akışı, sayfa sınırı, istek aralığı veya veri sözleşmesi değiştirilmedi. İş akışı kendi dalında bir kez tetiklendiğinde mevcut okuyucu kullanılacak; sonuç `durum` dalındaki `career/esube_kamu_ilanlari.json` dosyasına yazılacak.

## Güvenlik
- robots.txt önce kontrol edilir. İzin yok/unknown ise hedef sayfaya istek yapılmaz.
- Giriş, CAPTCHA veya e-Devlet yok; kullanıcı verisi/token yok.
- Sadece kamu işyeri türü e-Şube açık ilan listesi, resmî kaynaktan.
- İstekler arası 2 saniye, en çok 40 sayfa, User-Agent `AriesKaynakTara/1.0`.
- Deadline geçen ilanlar açık ilan olarak raporlanmaz; ilk görülme zamanı korunur.

## Kabul kanıtı
Actions komut çıktısı, çekilen sayfa ve ilan sayısı, benzersiz ilan numarası sayısı, deadline'ı geçmiş/kalan ilan sayısı ve JSON `collectedAt` alanı yeniden sayılacak. Başarısız olursa önceki last-good veri bozulmadan kalmalıdır.

Bu geçici dal filtresi yalnız bu ölçüm koşusu içindir. PR taslaktır, birleştirme yoktur.
