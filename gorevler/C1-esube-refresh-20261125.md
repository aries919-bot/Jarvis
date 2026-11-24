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

## Sonuç — 25.11.2026

- Actions: https://github.com/aries919-bot/Jarvis/actions/runs/45513413529 — SUCCESS.
- Komut çıktısı: `robots: izinli`; sayfa 1: 15, sayfa 2: 15, sayfa 3: 12 yeni ilan; toplam 42.
- JSON yeniden sayımı: `cekim_zamani=2026-11-24T23:51:48+00:00`; 3 sayfa; 42 ham satır; 42 benzersiz `ilan_no`; 0 tekrar; 42 açık; 0 kapalı/süresi geçmiş.
- Son başvuru tarihi 42 kaydın tamamında `27.11.2026`.
- Kurum sayısı 7: MSB Personel GM 20; Emniyet GM 10; Durağan Belediyesi 2; Ondokuz Mayıs Üniversitesi Sağlık Uygulama ve Araştırma Merkezi 7; Devlet Tiyatroları GM 1; Elazığ Karayolları 8. Bölge 1; Türkiye Denizcilik İşletmeleri 1.
- `durum` dalındaki dosya blob SHA: `ba3638d46b9af690691ad9590f05f8ba57a3472c`.
- Çekim tarihi UTC'dir; başvuru son tarihiyle karıştırılmaz. Eski 43 kayıtlı veriden 42 açık kayda inmesi, önceki bir kaydın süresi geçtiği için açık listeden çıkmasıyla tutarlıdır.
- PR #58 açık/taslak; birleştirilmedi. Günlük çalışma main'e veya cline/K1'e alınmış değildir.
