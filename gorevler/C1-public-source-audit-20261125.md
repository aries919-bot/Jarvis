# C1 — Yeni resmî kamu kaynakları robots ön denetimi (25.11.2026)

## Amaç
C1 kamu ilan eşiğine eklenebilecek yeni kurum kaynaklarını belirlemek. İlk aşama yalnızca robots.txt ölçümüdür. Robots izni doğrulanmadan hiçbir duyuru sayfası veya PDF indirilmez.

## Kaynak adayları ve resmî ilan sayımları

| Kurum / resmî kaynak | Açık kadro sayısı | Son başvuru | İlan/yayın tarihi | Başvuru / önemli şart |
|---|---:|---|---|---|
| Mardin Artuklu Üniversitesi | 40 (36 öğretim üyesi + 4 öğretim görevlisi) | 04.12.2026 | 20.11.2026 | Öğretim üyesi başvuruları Rektörlük/ilgili birime şahsen; öğretim görevlisi başvuruları ilgili birime şahsen/posta. İlgili kadro için 657/48, ALES en az 70; yabancı dil genel olarak en az 50, bazı MYO kadrolarında muaf. Öğretim görevlisinde tezli yüksek lisans; araştırma görevlisi koşulları ilgili eğitim statüsünü gerektirir. |
| Türkiye Turizm Tanıtım ve Geliştirme Ajansı (TGA) | 13 | 04.12.2026 | 03.11.2026 | TGA kariyer sayfasından çevrimiçi başvuru. Genel şartlar: T.C. vatandaşı, 18+, kamu haklarından mahrum olmama, göreve engel sağlık sorunu olmama. Şoför pozisyonu için en az ilkokul, 6 yıl deneyim, B sınıfı ehliyet ve profesyonel şoförlük deneyimi. Diğer pozisyonların özel şartları Ek-1'de. |
| Balıkesir Üniversitesi | 7 | 04.12.2026 mesai bitimi | 20.11.2026 | İlgili fakülte dekanlığına/üniversite birimine şahsen veya posta. ALES en az 70, yabancı dil en az 50; öğretim görevlisinde tezli yüksek lisans. Araştırma görevlisi kadrolarında ilgili alanda yüksek lisans öğrenciliği şartları var. |
| Orta Anadolu Kalkınma Ajansı (ORAN) | 7 (6 uzman + 1 destek) | 03.12.2026 | İlan tarihi tabloda 19.10.2026; kurum duyurusu 26.10.2026 | e-Devlet/Kariyer Kapısı üzerinden başvuru. Kayseri, Sivas veya Yozgat'ta görevlendirme kabulü; ileri düzey yabancı dil ve pozisyona özel şartlar. Sözlü sınav. |
| Atatürk Üniversitesi | 1 | 04.12.2026 | 20.11.2026 | İlgili birime dosya ile başvuru. Hukuk Fakültesi mezunu, Kamu Hukuku alanında yüksek lisans ve doktora şartı. |

**Yeniden sayım:** 40 + 13 + 7 + 7 + 1 = **68 kadro**. Bu beş kaynak, başvuru süresi 25.11.2026 tarihinde henüz kapanmamış resmî ilanlardır. Kadro sayısı, doğrulanmış ilan kaydı adedi değil; her kurumun ilanındaki pozisyon/kadro sayısıdır.

### Gelecekte açılacak kaynak — güncel açık sayısına eklenmez
- Yükseköğretim Kalite Kurulu (YÖKAK): 5 uzman yardımcısı. Başvuru 01.12.2026–14.12.2026 arasında açılacağından 25.11.2026 tarihinde henüz açık değildir. applicationOpensAt=2026-12-01; mevcut açık ilan toplamına eklenmez.

## Robots ölçümü
- Workflow: .github/workflows/c1-public-source-robots.yml
- Betik: scripts/c1_public_source_robots.py
- User-Agent: AriesKaynakTara/1.0
- Yalnızca her origin için /robots.txt alınır; TLS doğrulaması açıktır, yönlendirme takip edilmez.
- robots HTTP 200 ve sayfa yolu için izin sonucu true olmadan duyuru sayfasına/PDF'ye istek gönderilmez.
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
- ORAN duyurusu: https://www.dogaka.gov.tr/tr/duyuru/t-c-orta-anadolu-kalkinma-ajansi-oran-personel-alim-ilani/855
- Atatürk Üniversitesi: https://atauni.edu.tr/personel-daire-baskanligi/2026/11/20/ataturk-universitesi-ogretim-uyesi-alim-ilani-20-11-2026/
- YÖKAK (gelecek başvuru): https://www.yokak.gov.tr/2026/11/13/yuksekogretim-kalite-kurulu-uzman-yardimcisi-alim-ilani-3/
- Marmara süresi 20.11.2026 13:00: https://pdb.marmara.edu.tr/notice/universitemiz-sozlesmeli-personel-alimi-1

## Kabul ölçütü
1. Actions robots-only raporu exact commit SHA ve artifact ile alınmalı.
2. Her kaynağın her sayfa yolu için robots izni ayrı gösterilmeli.
3. İzinli kaynaklar için sonraki ayrı görevde sayfa/PDF başvuru tarihleri ve kadro sayıları tekrar okunmalı; robots unknown olanlara dokunulmamalı.
4. Taze e-Şube ve H2 snapshot'ları alınmalı; her kaynakta deadline geçenler çıkarılmalı.
5. Kamu raporu ayrı verilmeli. Hedef 10 kaynak / 100 doğrulanmış açık ilan; eşiğin altında RED.

## Şu anki karar
**RED / ön denetim sürüyor.** 68 yeni kadro adayı resmî metinlerden elle sayıldı; robots doğrulaması, H2/e-Şube canlı yenilemesi ve birleşik güncel sayım henüz yok. Production Worker GET ve D1 kalıcılık kanıtı da yok. Hiçbir PR birleştirilmedi, deploy yapılmadı.

**SONRAKİ ADIM:** Bu PR'daki robots-only Actions sonucunu incele; yalnız izinli kaynakları sonraki canlı ilan ölçümüne geçir.
