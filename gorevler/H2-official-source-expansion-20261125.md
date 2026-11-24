# H2 — Resmî kaynak genişletme, 25.11.2026

## Karar
CI doğrulaması bekleniyor; eşik sağlandı iddiası yok. Ayrı review/h2-public-sources-20261125 dalı açıldı. Cline'ın cline/K1 dalına doğrudan yazılmadı.

## Kaynak ölçümü ve sayım
Yeni ölçülmüş aday kurumlar:
- Marmara Üniversitesi Personel Daire Başkanlığı: 1 öğretim görevlisi duyurusu. Resmî sayfa yayın tarihini 24.11.2026, son başvuru tarihini 08.12.2026 veriyor.
- ÇOMÜ ilan sistemi: 24.11.2026 tarihli iki aktif akademik duyuru; ortak kaynak kurumu/domain tek sayılır, iki duyuru bir kaynak altında toplanır. Son tarih 08.12.2026 23:59.
- Zafer Kalkınma Ajansı: 2 uzman personel; yayın 03.11.2026; başvuru 25.11–14.12.2026. Başvuru Kariyer Kapısı'nda, okuyucu giriş yapmaz.
- Bilecik Şeyh Edebali Üniversitesi: 19.11.2026 tarihli Resmî Gazete bağlantılı öğretim elemanı ilanı. PDF bağlantısı yalnız üniversitenin resmî sayfasından; farklı resmî host çifti açık allowlist'e alınır, robots/TLS yine uygulanır.

## Kod değişikliği
- Kaynak kataloğuna 4 yeni kurum eklendi; toplam H2 kaynak tanımı 6'dan 10'a çıktı.
- Kaynak-özel yayın tarihi modları: ÇOMÜ Resmî Gazete tarihi, Zafer sayfa başlığı sonrası tarih, Bilecik sayfa başlığı tarihli Resmî Gazete kaydı.
- Tek alanlı son başvuru/son tarih alanları ayrıştırılır; yayın/çekim tarihi son başvuru yerine geçmez.
- ÇOMÜ'nün saat içeren başvuru aralığından bitiş tarihi ayrıştırılır.
- Bilecik için yalnız www.bilecik.edu.tr -> www.resmigazete.gov.tr resmî host çifti kabul edilir.
- Okuyucu yalnız GET yapar; robots belirsiz/engelli, timeout, PDF okuma hatası veya metin kanıtı eksikse kaynak stale kalır. Login/Kariyer Kapısı taraması yok.

## Beklenen CI
- Mevcut H2 testlerine 4 yeni tarih/host regresyon testi eklendi.
- Gerçek canlı çıktı ve kaynak başına ilan sayıları Actions artifact'i geldikten sonra yeniden sayılacak.
- 10 kaynak tanımı, 10 fresh kaynak demek değildir; yalnız fresh ve deadline açık duyuru sayılır. H2 ayrı feed'dir, C1 Worker'a entegre değildir.
- Genel kabul için 10 kaynak / 100 doğrulanmış açık ilan şartı değişmez. Bu PR tek başına eşiği sağladığını iddia etmez.

## Kaynak bağlantıları
- Marmara: https://pdb.marmara.edu.tr/notice/universitemiz-ogretim-gorevlisi-alim-ilani-1
- ÇOMÜ: https://ilan.comu.edu.tr/
- Zafer: https://www.zafer.gov.tr/duyurular/227
- Bilecik: https://www.bilecik.edu.tr/personel/Icerik/19-11-2026-tarihli-ve-33435-sayili-resmi-gazete-de-yayimlanan-ogretim-uyesi-disindaki-ogretim-elemani-lani
