# C1 — Yeni resmî kamu kaynakları robots ön denetimi (25.11.2026)

## Amaç
C1 kamu ilan eşiğine eklenebilecek yeni kurum kaynaklarını belirlemek. İlk aşama yalnızca robots.txt ölçümüdür. Robots izni doğrulanmadan hiçbir duyuru sayfası veya PDF indirilmez.

## Kaynak adayları ve resmî ilan sayımları

| Kurum / resmî kaynak | Açık kadro sayısı | Son başvuru | İlan/yayın tarihi | Başvuru / önemli şart |
|---|---:|---|---|---|
| Mardin Artuklu Üniversitesi | 40 (36 öğretim üyesi + 4 öğretim görevlisi) | 04.12.2026 | 20.11.2026 | Öğretim üyesi başvuruları Rektörlük/ilgili birime şahsen; öğretim görevlisi başvuruları ilgili birime şahsen/posta. İlgili kadro için 657/48, ALES en az 70; yabancı dil genel olarak en az 50, bazı MYO kadrolarında muaf. Öğretim görevlisinde tezli yüksek lisans; araştırma görevlisi koşulları ilgili eğitim statüsünü gerektirir. |
| TGA | 13 aday pozisyon; **0 sayıldı** | Arama dizininde 04.12.2026; doğrudan sayfa verisi çelişkili | 03.11.2026 olarak indekslenmiş | Resmî kariyer sayfasının arama özeti 13 pozisyon ve 03.11–04.12 başvuru aralığı gösteriyor; fakat doğrudan açılan aynı sayfa 2 pozisyon ve 25.06–26.07 tarihlerini gösterdi. Resmî kariyer portalı /jobs sayfası da “yayınlanmış açık pozisyon bulunamadı” diyor. Çelişki çözülene kadar Doğrulanamadı; 13 sayısı açık ilana eklenmez. |
| Balıkesir Üniversitesi | 7 | 04.12.2026 mesai bitimi | 20.11.2026 | İlgili fakülte dekanlığına/üniversite birimine şahsen veya posta. ALES en az 70, yabancı dil en az 50; öğretim görevlisinde tezli yüksek lisans. Araştırma görevlisi kadrolarında ilgili alanda yüksek lisans öğrenciliği şartları var. |
| Orta Anadolu Kalkınma Ajansı (ORAN) | 7 (6 uzman + 1 destek) | 03.12.2026 | İlan tarihi tabloda 19.10.2026; kurum duyurusu 26.10.2026 | e-Devlet/Kariyer Kapısı üzerinden başvuru. Kayseri, Sivas veya Yozgat'ta görevlendirme kabulü; ileri düzey yabancı dil ve pozisyona özel şartlar. Sözlü sınav. |
| Atatürk Üniversitesi | 1 | 04.12.2026 | 20.11.2026 | İlgili birime dosya ile başvuru. Hukuk Fakültesi mezunu, Kamu Hukuku alanında yüksek lisans ve doktora şartı. |
| Zafer Kalkınma Ajansı | 2 uzman | 14.12.2026 | 03.11.2026 | Kariyer Kapısı üzerinden 25.11–14.12 başvuru. TR33 (Afyonkarahisar, Kütahya, Manisa, Uşak) bölgesinde görevlendirme; özel şartlar tam ilan metninde. |
| Doğu Karadeniz Kalkınma Ajansı (DOKA) | 1 uzman | 06.12.2026 | 02.11.2026 | Kariyer Kapısı üzerinden başvuru; Rize Yatırım Destek Ofisi veya TR90 bölgesinde görevlendirme. Lisans mezuniyeti, ilgili KPSS puan türünde en az 80 ve İngilizce YDS/e-YDS C düzeyi (70) şartı. |

**Aday kadro yeniden sayımı:** Mardin 40 + Balıkesir 7 + ORAN 7 + Atatürk 1 + Zafer 2 + DOKA 1 = **58 kadro adayı**. TGA'nın 13 pozisyonu resmî sayfa çelişkisi nedeniyle bu toplama alınmadı. Son exact-head robots ölçümünde Atatürk (1), Balıkesir (7), Zafer (2) ve DOKA (1) ALLOW; bu dört kaynaktaki **11 kadro** robots ve resmî ilan metniyle doğrulanmış yeni açık kadrodur. Mardin 40 ve ORAN 7, robots HTTP 404 nedeniyle UNKNOWN ve sayılmaz. TGA 13 aday pozisyon robots açısından ALLOW olsa da sayfa/portal verileri çelişkili olduğu için Doğrulanamadı ve sayılmaz. Kadro sayısı, doğrulanmış ilan kaydı adedi değil; her kurumun ilanındaki pozisyon/kadro sayısıdır.

### Gelecekte açılacak kaynak — güncel açık sayısına eklenmez
- Yükseköğretim Kalite Kurulu (YÖKAK): 5 uzman yardımcısı. Başvuru 01.12.2026–14.12.2026 arasında açılacağından 25.11.2026 tarihinde henüz açık değildir. applicationOpensAt=2026-12-01; mevcut açık ilan toplamına eklenmez.

## Robots ölçümü
- Workflow: .github/workflows/c1-public-source-robots.yml
- Betik: scripts/c1_public_source_robots.py
- User-Agent: AriesKaynakTara/1.0
- Yalnızca her origin için /robots.txt alınır; TLS doğrulaması açıktır, yönlendirme takip edilmez.
- Exact-head CI: https://github.com/aries919-bot/Jarvis/actions/runs/45532429537 — job 138930761817 SUCCESS; Python syntax check SUCCESS; robots-only ölçüm SUCCESS; artifact 14985447797. collectedAt=2026-11-25T02:22:15.837584Z.
- Son exact-head ölçüm: https://github.com/aries919-bot/Jarvis/actions/runs/45532648217 — job 138931501320 SUCCESS; syntax check ve robots-only ölçüm SUCCESS; artifact 14985038772. collectedAt=2026-11-25T02:24:12.356812Z.
- Sonuç: Atatürk 200/ALLOW; Balıkesir 200/ALLOW; Zafer 200/ALLOW; DOKA (duyuru + PDF) 200/ALLOW; TGA (5 sayfa yolu + kariyer portalı) 200/ALLOW; YÖKAK 200/ALLOW. Mardin Artuklu 404/UNKNOWN; DOĞAKA/ORAN 404/UNKNOWN. 404 kaynaklarına hedef sayfa isteği yapılmadı.
- TGA'nın tga.gov.tr yolları izinli görünse de sayfa içeriği çelişkili; bu, robots izninden bağımsız veri doğrulama sorunudur. TGA kariyer alt alanı için /jobs yolu da robots kontrol listesine eklendi.
- HTTP 401/403/404/5xx, timeout, redirect veya ağ hatası UNKNOWN sayılır; hedef sayfaya istek yapılmaz.

## Önceki kamu sayımı düzeltmesi
- Önceki H2 + e-Şube manuel sayımı: 6 kaynak / 47 kart (e-Şube 42 + H2 5).
- Marmara Üniversitesi H2 kartının resmî başvuru süresi 20.11.2026 saat 13:00'te sona ermiştir; 25.11.2026 itibarıyla açık ilan değildir. Tarihsel sayımda 47 - 1 = **46** kalır.
- e-Şube snapshot 2026-11-24T23:51:48Z; 12 saatlik sınır 25.11.2026 11:51:48Z'de dolmuştur.
- H2 son toplama 2026-11-25T00:25:02Z; 12 saatlik sınır 12:25:02Z'de dolmuştur.
- Bu nedenle 25.11.2026 öğleden sonra önceki 46/47 kaydı güncel açık ilan sayısı olarak kullanılamaz. Yeni canlı çekim gereklidir.
- Önceki özel ATS 114 adayın tamamında deadline bilinmiyordu; doğrulanmış kamu ilanı sayısına eklenmez.

## Kaynak dayanakları
- Mardin Artuklu Üniversitesi öğretim üyesi: https://www.artuklu.edu.tr/tr/personel-daire-baskanligi/duyurular/20112026-tarihli-ve-33436-sayili-resmi-gazete-ogretim-uyesi-alim-ilani-profdr-docdr-drogruyesi-20112026-04122026
- Mardin Artuklu Üniversitesi öğretim görevlisi: https://www.artuklu.edu.tr/tr/personel-daire-baskanligi/duyurular/20112026-tarihli-ve-33436-sayili-resmi-gazete-ogretim-elemani-alim-ilani-ogrgor-2011202604122026
- TGA: https://tga.gov.tr/kariyer
- Balıkesir Üniversitesi: https://personel.balikesir.edu.tr/duyurular/20-11-2026-tarihli-ogretim-elemani-ilanimiz-202611201352
- ORAN duyurusu: https://www.dogaka.gov.tr/duyuru/t-c-orta-anadolu-kalkinma-ajansi-oran-personel-alim-ilani/855
- Atatürk Üniversitesi: https://atauni.edu.tr/personel-daire-baskanligi/2026/11/20/ataturk-universitesi-ogretim-uyesi-alim-ilani-20-11-2026/
- Zafer Kalkınma Ajansı: https://www.zafer.gov.tr/duyurular/227
- DOKA: https://www.doka.org.tr/announcement-detail.php?id=1221
- YÖKAK (gelecek başvuru): https://www.yokak.gov.tr/2026/11/13/yuksekogretim-kalite-kurulu-uzman-yardimcisi-alim-ilani-3/
- Marmara süresi 20.11.2026 13:00: https://pdb.marmara.edu.tr/notice/universitemiz-sozlesmeli-personel-alimi-1

## Kabul ölçütü
1. Actions robots-only raporu exact commit SHA ve artifact ile alınmalı.
2. Her kaynağın her sayfa yolu için robots izni ayrı gösterilmeli.
3. İzinli kaynaklar için sonraki ayrı görevde sayfa/PDF başvuru tarihleri ve kadro sayıları tekrar okunmalı; robots unknown olanlara dokunulmamalı.
4. Taze e-Şube ve H2 snapshot'ları alınmalı; her kaynakta deadline geçenler çıkarılmalı.
5. Kamu raporu ayrı verilmeli. Hedef 10 kaynak / 100 doğrulanmış açık ilan; eşiğin altında RED.

## Şu anki karar
**RED / ön denetim sürüyor.** Dört yeni kamu kaynağında robots izni doğrulandı ve 11 kadro açık. Mardin/ORAN robots UNKNOWN; TGA ilan verisi çelişkili; YÖKAK başvurusu henüz açılmadı. Eski e-Şube/H2 snapshot'ları stale olduğundan birleşik güncel kamu toplamı kanıtlanmadı. Production Worker GET ve D1 kalıcılık kanıtı da yok. Hiçbir PR birleştirilmedi, deploy yapılmadı.

**SONRAKİ ADIM:** Bu PR'daki robots-only Actions sonucunu incele; yalnız izinli kaynakları sonraki canlı ilan ölçümüne geçir.
