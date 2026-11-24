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

## Canlı ölçüm — 25.11.2026

- H2 push koşusu: https://github.com/aries919-bot/Jarvis/actions/runs/45512710235 — SUCCESS; artifact `h2-official-feeds-71`, 10 JSON dosyası.
- H2 parser + last-good test paketi: 45/45 PASS. PR genel script testi: https://github.com/aries919-bot/Jarvis/actions/runs/45512723185 — `Ran 146 tests`, `OK`. PR Hakem otomatik job: https://github.com/aries919-bot/Jarvis/actions/runs/45512722835 — ONAY; bu sonuç insan/harici incelemesi değildir.
- `collectedAt`: `2026-11-24T23:46:14Z`.

| Kaynak | Kaynak durumu | Duyuru kaydı | Açık doğrulanmış |
|---|---|---:|---:|
| Anadolu Üniversitesi | fresh | 1 | 1 |
| Adli Tıp Kurumu | fresh | 1 | 0 — başvuru 30.11.2026 09:00'da açılır |
| Düzce Üniversitesi | fresh | 1 | 1 |
| Tarsus Üniversitesi | fresh | 1 | 1 |
| Marmara Üniversitesi | fresh | 1 | 1 |
| Zafer Kalkınma Ajansı | fresh | 1 | 1 |
| YHGM | fresh | 1 kura/sonuç | 0 — deadline 13.10.2025 geçmiş |
| BOTAŞ | stale | 0 | 0 — iki timeout denemesi |
| ÇOMÜ | stale | 0 | 0 — robots HTTP 200 fakat içerik güvenli çözümlenemedi; hedef istenmedi |
| Bilecik Şeyh Edebali Üniversitesi | stale | 0 | 0 — Resmî Gazete robots isteği timeout; hedef PDF istenmedi |

- Yeniden sayım: 10 kaynak tanımı; 7 fresh; 3 stale; 5 açık doğrulanmış duyuru; 1 başvuru başlangıcını bekleyen; 1 süresi geçmiş kura/sonuç.
- H2 JSON'u C1 Worker'a entegre değildir. C1 kamu sayımıyla birleştirilip eşik geçti diye sunulamaz.
- K1-B erişim ölçümü: https://github.com/aries919-bot/Jarvis/actions/runs/45512722928 — SUCCESS; 28 uç noktanın 12'si ÇALIŞIYOR, 16'sı ENGELLİ. e-Şube robots ALLOW + HTTP 200; bu yalnız erişim ölçümüdür, ilan satırı sayısının yerine geçmez.
- PR #57 açık/taslak; bağımsız insan/harici review kaydı yok; birleştirilmedi.
