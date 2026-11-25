# C1 resmî kaynak snapshot — 2026-11-26

## Amaç ve karar

Bu kayıt, durum dalındaki docs/HARITA.md ve docs/NOT_C1.md devamı olarak yalnızca İş & Kariyer C1 kaynak doğrulama ve Haber doğrulama işini ilerletir. Android kodu, Worker kodu, üretim ayarları veya uygulama ekranları değiştirilmedi.

**Ara karar: C1 GENEL RED / ENTEGRASYON BEKLEMEDE.** Aşağıdaki resmî duyuruların başvuru koşulları ve bağlantıları kontrol edildi. Yeni kayıtlar mevcut Worker veya proje sayaçlarına eklenmedi; sourceId, şehir, canonical ilan kimliği, cross-source exact dedup ve gerçek ingest kanıtı henüz yok. Kadro sayısı ilan kartı sayısı değildir.

Araştırma tarihi 2026-11-26. Adayların Worker'a ilk başarılı aktarımı yapılmadığından collectedAt=null ve firstSeenAt=null kalmalıdır. Resmî kaynak yayın tarihi vermiyorsa publishedAt=null yazılmalıdır. Başvuru başlangıç tarihi publishedAt yerine kullanılamaz.

## Mevcut sayımlar — birbirinden ayrı tutulur

| Veri katmanı | Kaynak başına açık/son iyi kart | Toplam | Tazelik / kullanım |
|---|---|---:|---|
| Worker/H2 son iyi kamu feed'i | e-Şube 42; Anadolu 1; Düzce 1; Marmara 1; Tarsus 1; Zafer 1 | 6 kaynak / 47 kart | Son ölçümler 24–25 Kasım. 12 saat freshness sınırını aştıkları için stale last-good veridir; bugünün fresh ilanı diye gösterilmez. |
| Proje-geneli manuel resmî envanter | Önceki HARITA kaydı | 12 kaynak / 55 kart | Ayrı manuel veri katmanı; 6/47 ile toplanamaz ve bugüne ait fresh sayım değildir. |
| Bu nottaki yeni aktif duyuru adayları | Aşağıda 6 kurum, her biri 1 ana duyuru adayı | 6 aday duyuru | Resmî sayfada/PDF'de doğrulandı; Worker'a alınmadı, mevcut sayaçlara eklenmedi. |

Yeniden sayım: Worker feed 42+1+1+1+1+1=47. 12/55 envanter ile 6/47 Worker alt kümesi birleştirilmez. Altı yeni aday, mevcut sayaçlara henüz eklenmemiştir. Kamu sıfır değildir; fakat mevcut feed snapshot'ı stale durumdadır. Sabit eşik en az 10 benzersiz kaynak ve 100 doğrulanmış, süresi geçmemiş benzersiz ilan kartıdır. Eşik sağlanmadığı için genel C1 RED kalır.

## Resmî kaynaklardan kontrol edilen aktif başvuru adayları

Her kurum aşağıda bir ana duyuru adayı olarak sayılmıştır. Kontenjanlar ayrı metadata'dır; kart sayısını artırmaz.

| Kurum / kategori | Duyuru / kontenjan | publishedAt | deadline / süreç | Başvuru yolu | Şart özeti | Resmî kaynak |
|---|---|---|---|---|---|---|
| Sahil Güvenlik Komutanlığı — Kamu / KPSS | 1 duyuru / 10 memur | 2026-11-13 | 2026-11-27; ilanda saat yok | e-Devlet Sahil Güvenlik başvuru ekranı | 657 genel şartları; kadroya göre lisans/ön lisans; KPSS 2026 P3 veya P93 ve unvana özgü koşullar | Resmî Gazete PDF s.84: https://www.resmigazete.gov.tr/eskiler/2026/11/20261113.pdf ; başvuru: https://www.turkiye.gov.tr/sahil-guvenlik-komutanligi-is-basvurusu |
| Hazine ve Maliye Bakanlığı — Kamu / KPSS | 1 duyuru / en çok 10 Stajyer Hazine Kontrolörü | 2026-11-13 | 2026-12-04; saat belirtilmemiş | e-Devlet/Hazine ve Maliye Bakanlığı Kariyer Kapısı; https://kariyerkapisi.gov.tr | Hukuk, İktisat, Siyasal Bilgiler, İşletme veya İİBF alanında 4 yıllık mezuniyet; 2025/2026 KPSS P17/P18/P22/P23/P28/P47/P48 puanlarından en az 80; 01.01.1991 veya sonrası doğum | Resmî PDF: https://ms.hmb.gov.tr/uploads/2026/11/Ilan-Metni-Internet-Sitesi-491a08379759c79c.pdf ; kurum duyurusu: https://www.hmb.gov.tr/duyuru/stajyer-hazine-kontrolorlugu-giris-sinavi-ilani-3 |
| Cumhurbaşkanlığı Savunma Sanayii Başkanlığı — Kamu / KPSS'siz | 1 duyuru / 26 proje bazlı sözleşmeli pozisyon | null; PDF'de kesin yayın günü bulunmadı | 2026-12-21 23:59 | e-Devlet üzerinden Kariyer Kapısı: https://kariyerkapisi.gov.tr/isealim | İlgili 4 yıllık lisans; ilgili alanda en az 5 yıl tecrübe; son 2 yılda YDS İngilizce en az 70 veya kabul edilen eşdeğerlik/muafiyet | Resmî PDF: https://www.ssb.gov.tr/storage/img/proje-bazli-sozlesmeli-personel-alimi-giris-sinavi-ilan-metni.pdf |
| Bartın Üniversitesi — Kamu / KPSS | 1 duyuru / 1 Büro Personeli (4/B) | 2026-11-20 | Elektronik başvuru 2026-12-05 13:00; imzalı ek evrak teslimi 2026-12-08 17:30 (ayrı süreç alanı) | e-Devlet/Kariyer Kapısı; ek Aday Başvuru Bilgileri belgesi üniversiteye şahsen veya noter vekiliyle teslim edilir | Herhangi bir ön lisans programından mezuniyet; 2026 KPSS P93 en az 60; 01.01.1991 veya sonrası doğum; ilandaki diğer koşullar | Resmî PDF: https://cdn.bartin.edu.tr/onceinsan/fa8dc0ccaa1fa07ccf0ef519114d8f0a/ilan-metni.pdf ; kurum arşivi: https://onceinsan.bartin.edu.tr/arsiv/2026/11/duyuru-arsiv.html |
| Ankara Yıldırım Beyazıt Üniversitesi — Kamu / KPSS | 1 duyuru / 1 Sistem Uzmanı | 2026-11-23 | 2026-12-07 13:00 | e-Devlet AYBÜ/Kariyer Kapısı Kamu İşe Alım: https://kariyerkapisi.gov.tr/isealim | Belirli 4 yıllık mühendislik lisansları; sistem yönetiminde en az 5 yıl tecrübe; ilgili Windows Server, Exchange, AD/DNS/DHCP, VMware/Linux bilgisi; KPSS P3 %70 + yabancı dil %30 sıralaması | Kurum duyurusu: https://www.aybu.edu.tr/pdb/tr/duyuru/29093/S%C3%B6zle%C5%9Fmeli-Bili%C5%9Fim-Personeli-Al%C4%B1m-%C4%B0lan%C4%B1 ; PDF: https://www.aybu.edu.tr/GetFile?id=Upload/6d8fa00a-9d6f-4188-bc34-4ff2d432ba8b.pdf |
| Manisa Celal Bayar Üniversitesi — Kamu / KPSS'siz akademik | 1 ana duyuru adayı; portal birden fazla alt kadro başvurusu listeliyor | 2026-11-25 | 2026-12-09 17:00 | Üniversitenin resmî akademik başvuru portalı: https://aed-basvuru.mcbu.edu.tr/AED/Announcement/Public | Kadro/alan bazında profesör, doçent veya doktor öğretim üyesi niteliği ve ilandaki özel akademik koşullar | Resmî başvuru portalı: https://aed-basvuru.mcbu.edu.tr/AED/Announcement/Public ; Resmî Gazete ilan PDF'si portalda 25.11.2026 tarihli olarak listelenir. |

### Kart sayısı ve kontenjan denetimi

Yeni aktif aday ana duyuru sayısı 6'dır: Sahil Güvenlik 1 + HMB 1 + SSB 1 + Bartın 1 + AYBÜ 1 + MCBÜ 1. Kadro/pozisyon toplamı en çok 70 olabilir; bu 70 ilan demek değildir. MCBÜ portalında birden çok alt kadro başvuru girdisi olduğundan, sourceId ve ilan kimliği incelemesi tamamlanmadan bunlar tek ana duyurunun alt satırları mı yoksa ayrı kartlar mı kesinleştirilmez. Yeni adaylar Worker'a alınmadı.

## Başvurusu henüz açılmamış — aktif sayaca eklenmez

| Kurum / kategori | Duyuru / kontenjan | publishedAt | Başvuru aralığı | Şart özeti | Resmî kanıt |
|---|---|---|---|---|---|
| Sosyal Güvenlik Kurumu — Kamu / KPSS | 1 duyuru / 50 Müfettiş Yardımcısı | 2026-11-19 | 2026-12-07–2026-12-18 | İlgili 4 yıllık lisans alanları; KPSS P43/P48 en az 80; yaş koşulu; e-Devlet/Kariyer Kapısı üzerinden başvuru | Resmî Gazete: https://www.resmigazete.gov.tr/eskiler/2026/11/20261119.pdf ; kurum PDF: https://www.sgk.gov.tr/Download/DownloadFile?d=8239a229-a87e-4cb7-833f-3e114f5682f7&f=751d042b-d30e-4ce1-a27e-7761b10857b8.pdf |
| Kültür ve Turizm Bakanlığı — Kamu / KPSS | 1 duyuru / 20 Uzman Yardımcısı | 2026-11-24 | 2026-12-09–2026-12-23 | İlanda sayılan lisans bölümleri; kadroya göre 2025/2026 KPSS P3/P28/P29/P34 en az 80; yaş sınırı | Resmî Gazete PDF: https://www.resmigazete.gov.tr/eskiler/2026/11/20261124.pdf (s.69–73); başvuru https://kariyerkapisi.gov.tr/isealim |

Bu iki duyuruda başvuru başlangıç tarihi applicationOpensAt alanına yazılmalıdır; başlangıç tarihinden önce “Doğrulandı/açık başvuru” etiketi verilmez.

## Kamu dışı aday — ayrı tutulur

Çağ Üniversitesi 25.11.2026 tarihli öğretim elemanı duyurusu özel üniversite kaydıdır; kamu sayacına eklenmez. İki araştırma görevlisi pozisyonu bir duyuruda yer alır. Resmî Gazete ve üniversite başvuru metninin tek kart/alt kadro yapısı ayrıca eşleştirilmeden genel sayaca dahil edilmez.

## 26.11.2026 Resmî Gazete dizini — yalnız aday kaynak listesi

Resmî Gazete “Çeşitli İlanlar” dizininde MEB, MSB, Sağlık Bakanlığı, DSİ, Düzce Üniversitesi, Karamanoğlu Mehmetbey Üniversitesi, Sakarya Üniversitesi, Türkiye Su Enstitüsü, belediyeler ve çeşitli üniversiteler listelenmiştir. Dizin HTML isteği bu incelemede zaman aşımına uğradı; bireysel personel ilanı PDF'leri alınamadı ve şartları doğrulanamadı. Bu nedenle kurum adları aday kaynak listesi olarak kalır; ilan sayısına eklenmez. Resmî dizin: https://www.resmigazete.gov.tr/ilanlar/eskiilanlar/2026/11/20261126-4.htm

## H1 Haber doğrulama / PR #71 güncel CI kapısı

- PR #71 exact HEAD: e390e05e1485717c3a07ce3af06db6b3840062f3.
- APK/unit run #45712459742: SUCCESS — https://github.com/aries919-bot/Jarvis/actions/runs/45712459742.
- Emulator run #45712459718: son GitHub API sorgusunda in_progress — https://github.com/aries919-bot/Jarvis/actions/runs/45712459718.
- Job 139550204961: adım 1–6 başarılı; adım 7 Android instrumentation çalışıyor; adım 8 XML doğrulaması ve adım 9 rapor yüklemesi bekliyor. Son artefakt sorgusunda henüz artefakt yok.
- H1 PASS sayılmaz. Exact-head XML'de tam 4 testcase ve failure=0, error=0, skipped=0 görülmelidir. Önceki false-green ve 0-test install arızaları geçerli negatif kanıttır.
- Bu emülatör işi tamamlanmadan concurrency nedeniyle workflow dosyası/path filter değiştirilmemelidir.

## C1 üretim kapısı

- C1 PR #11 ve #12 taslak zincirinde; production authenticated Worker GET/D1, ingest sonrası firstSeenAt dayanıklılığı ve tüm kaynakların tek feed'de exact dedup doğrulaması yok.
- Private Actions runner sonuçlarında runner_id=0, steps=[] ve log/artefact eksikliği görülmüştür. Bu, testlerin çalışıp geçtiği anlamına gelmez. Aynı boş runner koşusu tekrarlanarak ilerleme iddia edilmeyecektir.
- Local Node/D1 testi production Cloudflare Worker GET/D1 kanıtı yerine geçmez.
- Production token değerleri repo veya bu not içine yazılmaz.

## Sonraki işler

1. H1 emulator run #45712459718 sonucunu ve XML artefact'ini yeniden sorgula; 4/4 test ve sıfır failure/error/skipped koşulunu bağımsız say.
2. 26.11.2026 Resmî Gazete dizinindeki kurum adaylarının her bir bireysel PDF'sini/kurum duyurusunu resmî kaynaktan al; ihale, sonuç, iptal, burs veya sınav sonucu olanları personel ilanı kabul etme.
3. Altı aktif + iki gelecekteki duyuru için sourceId, canonical URL, kurum/pozisyon/şehir/deadline kimliğini kaydet; MCBÜ portalındaki alt kadro granülerliğini çöz; exact dedup yapmadan Worker sayaçlarına ekleme.
4. Kaynak başına lastGoodAt ve collectedAt yenilenmeden eski 47 kartı fresh diye sunma. Tek kaynak hatası yalnız o sourceId'yi stale yapmalı.
5. Deadline geçen kayıtları güncel akıştan çıkar; deadline, applicationOpensAt ve ek evrak teslim tarihini ayrı tut.
6. 10 benzersiz kaynak /100 doğrulanmış ilan ve production authenticated GET/D1 kapıları geçilmeden genel C1 RED kalır.


## Ek tarama — MEB usta öğretici duyuruları ve 26 Kasım Resmî Gazete adayları

### MEB e-Olgunlaşma — doğrulanmış, başvurusu geçmiş duyurular

MEB'in kendi e-Olgunlaşma ilan sayfasında üç ayrı duyuru kaydı doğrudan okundu. Bunlar başvurusu bitmiş olduğundan açık iş sayımına eklenmez; Haber geçmişinde etiketleri Süresi geçti olmalıdır. Sayfalarda yayımlanma tarihi alanı gösterilmediğinden publishedAt=null bırakılır.

| Kurum / kategori | Duyuru | Kontenjan | publishedAt | deadline | Sınav tarihi (deadline değildir) | Başvuru / şart | Resmî bağlantı |
|---|---|---:|---|---|---|---|---|
| Mardin Olgunlaşma Enstitüsü — Kamu / KPSS'siz | Görsel Sanatlar / Sanat ve Tasarım Usta Öğretici | 1 | null | 2026-11-20 | 2026-11-26 | e-Olgunlaşma üzerinden e-Devlet ile; ilgili alana ait kabul edilen belge/diploma, sağlık raporu, ikamet, adli sicil ve fotoğraf; beceri sınavı | https://e-olgunlasma.meb.gov.tr/Ilan/IlanDetay?ilanId=b59b23e8-d94a-4e6a-ba8e-78f02ca20e06 |
| Mardin Olgunlaşma Enstitüsü — Kamu / KPSS'siz | Bilişim Teknolojileri / Veritabanı Programcılığı Usta Öğretici | 1 | null | 2026-11-20 | 2026-11-24 | e-Olgunlaşma üzerinden e-Devlet ile; ilgili alan yeterlilik belgesi/diploma ve ilandaki belgeler; beceri sınavı | https://e-olgunlasma.meb.gov.tr/Ilan/IlanDetay?ilanId=f7c0e251-be0e-4399-bb6c-66478257f2c2 |
| Kahramanmaraş Olgunlaşma Enstitüsü — Kamu / KPSS'siz | Türk Halk Bilimi Kadrosuz Usta Öğretici Ek Alım | 1 | null | 2026-11-25 | 2026-11-26 | e-Devlet üzerinden başvuru; kabul edilen sanatkâr/ustalık/mesleki yeterlilik veya diploma belgeleri; başvuru belgeleri sınav günü elden teslim | https://e-olgunlasma.meb.gov.tr/Ilan/IlanDetay?ilanId=a9a6a617-c7f4-4658-8fd6-dbd0f6be966a |

Yeniden sayım: Mardin 1+1, Kahramanmaraş 1 = **3 ayrı duyuru / 3 pozisyon / 2 kurum kaynağı**. Deadline geçmiş olduğu için **0 güncel açık ilan**. Sınav tarihleri ilan başlığı veya publishedAt yerine yazılmadı. e-Devlet arkasındaki form/login ekranı kazınmaz; yalnız resmî başvuru yönlendirmesi kullanılır.

### 26.11.2026 Resmî Gazete yeni duyuru dizini — doğrulama sınırı

Resmî Gazete 33442 sayılı 26.11.2026 tarihli “Çeşitli İlanlar” dizini aşağıdaki kurum başlıklarını listeliyor: MEB Mesleki ve Teknik Eğitim GM, MSB Tedarik Hizmetleri GM, Sağlık Bakanlığı Halk Sağlığı GM ve Strateji Geliştirme Başkanlığı, DSİ, Düzce Üniversitesi, Çevre Bakanlığı Yapı İşleri GM, Karamanoğlu Mehmetbey Üniversitesi, Sakarya Üniversitesi, Türkiye Su Enstitüsü, Özburun Belediyesi, KSÜ, Mersin Üniversitesi, Afyonkarahisar Sağlık Bilimleri Üniversitesi, Boğaziçi Üniversitesi, İstanbul Beykent Üniversitesi ve Kültür ve Turizm Bakanlığı.

Resmî index URL: https://www.resmigazete.gov.tr/ilanlar/eskiilanlar/2026/11/20261126-4.htm

Bu oturumda Resmî Gazete günlük PDF'si açılamadı (HTTP/fetch timeout). Kurumların kendi ilan sayfalarında da her yeni metin bulunamadı: SUEN duyuru listesi 28.08.2026/22.07.2026 kayıtlarında kalıyor; Mersin Personel Daire sayfasında 26.11 duyurusu görülmedi; KSÜ başvuru portalı 502 döndürdü. Dolayısıyla dizin başlığı tek başına her kurum için aktif ilan kanıtı değildir.

Ayrı bir ikincil derleme 26 Kasım sayısında şu adayları raporluyor: Türkiye Su Enstitüsü 1 bilgisayar mühendisi; Özburun Belediyesi 2 sürekli işçi; KSÜ 1 öğretim görevlisi; KSÜ akademik kadro duyurusu; Mersin Üniversitesi 1 sistem yöneticisi. Aynı derlemenin KSÜ akademik alımı metninde bir bölümde 42, bir tabloda 54 kadro yazması iç tutarsızlıktır. Bu yüzden kontenjan ve deadline detayları resmî PDF ile görülmeden doğrulanmış kabul edilmez. Bu adaylar:
- Worker/manuel kamu sayaçlarına eklenmedi;
- ilan kartı sayısına ve 10/100 eşiğine eklenmedi;
- ayrı takip edilecek Doğrulanamadı kaynak adayları olarak kaldı.
İkincil derleme yalnız araştırma ipucu olarak: https://www.habereguven.com/resmi-gazetede-personel-alim-ilanlari-yayimlandi-4-kuruma-59-personel-alinacak

**Düzeltme kuralı:** Resmî Gazete sayısının yayın tarihi 26.11.2026 olabilir; bu, başlıklar içinde yer alan her kurumun ilan PDF'si açılmadan şartları, kesin kontenjanı veya deadline'ı doğrulamaz. Süre “yayımlandığı tarihten itibaren 30 gün” gibi göreli yazılmışsa resmi metinde kesin son gün kontrol edilmeden son tarih uydurulmaz.

### Ek kanıt sayacı

- Yeni resmî MEB expired duyuruları: 2 kurum /3 duyuru /3 kontenjan; güncel open=0.
- 26 Kasım RG dizinindeki ek institution leads: 18 başlık satırı, ancak bunların içinden bireysel personel ilanı olduğu kesinleştirilen ve koşulları resmî içerikle okunabilen ilave kayıt bu incelemede 0; bu 0 kaynak/0 ilan değil, **henüz ölçülememiş** demektir.
- Worker 6/47 last-good stale ve eski manuel envanter 12/55 değişmedi. Yeni MEB expired kayıtları da bu açık ilan sayılarına eklenmez.
**KARAR:** C1 10/100 RED ve production GET/D1 BLOCKED kalır.


## 23.11.2026 Resmî Gazete 33439 — ikinci tur PDF ilan ayrıştırması

Resmî PDF: https://www.resmigazete.gov.tr/eskiler/2026/11/20261123.pdf (Resmî Gazete 23.11.2026, sayı 33439). PDF metin içeriği açıldı; screenshot aracı 41,936,566 byte dosya nedeniyle 400 verdi. Dolayısıyla metin tabanlı resmî PDF kanıtı vardır, görsel PDF QA tamamlanmamıştır.

### Yeni kamu ilanları — resmî metin ve takvim doğrulandı

| Kurum / kategori | Duyuru adedi | Kontenjan | publishedAt | deadline | Başvuru | Şart özeti | Resmî bağlantı |
|---|---:|---:|---|---|---|---|---|
| İçişleri Bakanlığı — Kamu / KPSS | 1 | 1.170 pozisyon (119 merkez +1.051 taşra) | 2026-11-23 | 2026-12-11 16:00 | Başvuru 07.12.2026'da açılır; İçişleri Bakanlığı sitesindeki Duyurular bağlantısından e-Devlet başvuru ekranı. Şahsen/posta yok. | 2026 KPSS-B; lisans P3, ön lisans P93, lise P94 puan türünde en az 60; 18 yaş ve 657/4-B genel şartları. Pozisyona özel mezuniyet/sertifika şartları Ek tablolardadır. | https://www.resmigazete.gov.tr/eskiler/2026/11/20261123.pdf (s.18–30); kurum: https://www.icisleri.gov.tr |
| Türk Akreditasyon Kurumu (TÜRKAK) — Kamu / KPSS | 1 | 3 Koruma ve Güvenlik Görevlisi | 2026-11-23 | 2026-12-07 23:59 | e-Devlet Türk Akreditasyon Kurumu/Kariyer Kapısı Kamu İşe Alım veya https://kariyerkapisi.gov.tr üzerinden elektronik. | Ön lisans güvenlik/koruma ile ilişkili tabloda sayılan programlardan mezuniyet; KPSS P93 en az 70; 35 yaş sınırı; geçerli özel güvenlik kimlik kartı ve sağlık/özel güvenlik koşulları; sözlü sınav. | https://www.resmigazete.gov.tr/eskiler/2026/11/20261123.pdf (s.57–59); kurum: https://www.turkak.org.tr |
| Yozgat Bozok Üniversitesi — Kamu / KPSS'siz akademik | 1 | 7 akademik kadro | 2026-11-23 | 2026-12-11 (ilan aralığında açıkça yazılı) | https://personelbasvuru.bozok.edu.tr/ üzerinden elektronik; kurum sayfasında e-Devlet şifresiyle giriş gerektiği yazıyor. | Profesör/doçent/doktor öğretim üyesi; 2547, akademik yükseltilme ve atanma ölçütleri; tabloda hukuk, tarım-gıda/kenevir, tıp, turizm alanlarında özel doktora/doçentlik ve yayın/tecrübe şartları. | https://www.resmigazete.gov.tr/eskiler/2026/11/20261123.pdf (s.62–63); portal: https://personelbasvuru.bozok.edu.tr |
| Düzce Üniversitesi — Kamu / KPSS'siz akademik | 1 ek ana duyuru | 4 akademik kadro | 2026-11-23 | 2026-12-07 | https://abas.duzce.edu.tr üzerinden elektronik başvuru | Profesör 1 ve doktor öğretim üyesi 3: İktisat Tarihi, Gelişim Psikolojisi, Yönetim/Organizasyon ve Gelişimsel Pediatri alanlarında ilan-özel akademik derece/uzmanlık şartları. | https://www.resmigazete.gov.tr/eskiler/2026/11/20261123.pdf (s.68–70); portal: https://abas.duzce.edu.tr |

**İçişleri 1.170 pozisyon = 1 duyuru kartıdır.** Pozisyon adedi 1.170 diye ilan sayısı 1.170 yapılmaz. Başvuru 07.12.2026'da açıldığından 26.11.2026 tarihinde future/opening-soon statüsündedir; bugün açık ilan sayısına girmez. applicationOpensAt=2026-12-07, deadline=2026-12-11T16:00, publishedAt=2026-11-23.

TÜRKAK bir kamu kaynağı olarak 3 pozisyonlu tek duyurudur. Yozgat Bozok da 7 kadrolu tek duyurudur. Düzce'nin 23.11.2026 Resmî Gazete metninde iki ayrı başlık bulunmaktadır: daha önce H2 feed'deki 5 kadrolu Öğretim Elemanı ilanı ile bu ek 4 kadrolu Öğretim Üyesi ilanı. H2'deki ilk kartla ikinci kartı birleştirmeyin; farklı pozisyon/unvan ve ayrı resmî duyuru kimliği vardır. Buna rağmen Worker'a yazmadan önce canonical notice URL/sourceId ve exact dedup uygulanmalıdır. Düzce benzersiz kurum sayısı yine birdir.

### Kamu kaynak adayı — deadline teyidi eksik, sayaca eklenmez

| Kurum / kategori | Duyuru | Pozisyon | publishedAt | deadline | Neden beklemede | Resmî bağlantı |
|---|---|---:|---|---|---|---|
| Kütahya Dumlupınar Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Üyesi ilanı | Çok sayıda akademik satır; ana ilan tek kart | 2026-11-23 | null | Resmî PDF, yayımlandığı tarihten itibaren 15 gün diyor ancak açık bir ISO/yerel tarih yazmıyor; kesin son gün kurum portalında teyit edilmeden hesaplanmaz. | https://www.resmigazete.gov.tr/eskiler/2026/11/20261123.pdf (s.70–73); başvuru: https://ilan.dpu.edu.tr/ |
| Ondokuz Mayıs Üniversitesi — Kamu / Kura | 1 sürekli işçi ilanı | 21 pozisyon | 2026-11-23 (Resmî Gazete); İŞKUR liste yayın tarihi bilinmiyor | null | Üniversite, başvurunun İŞKUR e-Şube ilanı yayımlandıktan sonra 5 gün içinde yapılacağını söylüyor. e-Şube ilan detayına giriş/şifre gerektiğinden sayfa kazınmaz; resmi e-Şube üzerinden kullanıcıya yönlendirme ve tarih doğrulaması gerekir. | https://www.resmigazete.gov.tr/eskiler/2026/11/20261123.pdf (s.73–78); kurum: https://www.omu.edu.tr ; e-Şube: https://esube.iskur.gov.tr |

Bu iki kayıt kamu kurum adayı olsa da doğrulanmış deadline bulunmadığı için güncel doğrulanmış açık ilan sayısına eklenmez. OMÜ için 7 temizlik, 2 kaynakçı, 2 oto bakım ve tabloda sayılan diğer iş türleri 21 pozisyonu oluşturur; 21 ayrı ilan kartı değildir.

### Kamu dışı üniversite ilanları — ayrı özel kaynak havuzu

| Kurum / kategori | Duyuru | Pozisyon özeti | publishedAt | deadline | Başvuru | Resmî bağlantı |
|---|---:|---|---|---|---|---|
| Antalya Belek Üniversitesi — KPSS'siz (özel üniversite) | 1 | 6 akademik kadro: 1 profesör, 2 doktor öğretim üyesi, 3 öğretim görevlisi | 2026-11-23 | 2026-12-07 | Öğretim üyesi dosyaları ilgili rektörlük/dekanlığa; öğretim görevlisi dosyaları ilgili birime şahsen veya posta ile. | https://www.resmigazete.gov.tr/eskiler/2026/11/20261123.pdf (s.64–66); kurum: https://www.belek.edu.tr |
| Yaşar Üniversitesi — KPSS'siz (özel üniversite) | 1 | 2 öğretim üyesi kadrosu | 2026-11-23 | null | Profesör/doçent adayları İnsan Kaynakları Müdürlüğüne; ilan içindeki profesör/doçent başvurusu fiziki dosya ile. PDF'de bu bölüm için takvim satırı açıkça doğrulanmadı. | https://www.resmigazete.gov.tr/eskiler/2026/11/20261123.pdf (s.61–62); kurum: https://www.yasar.edu.tr |
| Özyeğin Üniversitesi — KPSS'siz (özel üniversite) | 1 | Öğretim üyesi ve öğretim görevlisi kadroları; exact total slot count notu PDF tablosu tamamen eşleştirilmeden yazılmadı | 2026-11-23 | 2026-12-08 17:00 | PDF'de belirtilen insan kaynakları e-posta adresine elektronik dosya; şahsen/posta yok. | https://www.resmigazete.gov.tr/eskiler/2026/11/20261123.pdf (s.78–81); kurum: https://www.ozyegin.edu.tr |

Bu üç özel üniversite kaydı kamu kaynağı sayımına girmez. Antalya Belek ve Özyeğin'in takvimleri açıkça doğrulandı; Yaşar için exact deadline henüz doğrulanmadı. Özel kaynaklar kendi ayrı sayacında tutulmalıdır.

### 23 Kasım Resmî Gazete'de exact dedup / eski kayıt ayrımı

- Tarsus Üniversitesi 23.11.2026 Öğretim Görevlisi ilanı; 2 pozisyon satırı ve toplam 3 kontenjan içerir, son başvuru 07.12.2026. H2'deki Tarsus 1 duyuru /3 kontenjan /07.12.2026 kartıyla eşleşir; yeni karta veya kaynağa eklenmez.
- Düzce Üniversitesi 23.11.2026 Öğretim Elemanı ilanı 5 kontenjan ve 07.12 deadline ile H2 mevcut Düzce 5 kontenjan kartıyla eşleşir; ikinci kez sayılmaz. Aynı sayının 68–70. sayfalarındaki ayrı Öğretim Üyesi başlığı 4 akademik kadrolu ayrı duyurudur ve canonical notice dedup sonrası ayrı kart olarak değerlendirilir.
- İçişleri Bakanlığı'nın 1.170 pozisyonu da tek 4/B ana duyurusudur. 119 merkez+1.051 taşra dağılımı alt pozisyon metadata'sıdır.

### Yeniden sayım — yeni PDF turu

- Yeni resmî kamu aktif ve tarihleri açık duyurular: TÜRKAK 1 + Yozgat Bozok 1 + Düzce ek akademik duyuru 1 = **3 yeni kart**. Düzce kurum kaynağı zaten mevcut feed'de var; yeni benzersiz kamu kurum sayısı bu üç kartla +2 olur (TÜRKAK, Yozgat Bozok).
- Future/opening-soon kamu duyurusu: İçişleri Bakanlığı **1 ana duyuru /1.170 pozisyon**, şu an açık sayısına 0.
- Resmî kamu adayları deadline belirsiz: Kütahya Dumlupınar 1 ve Ondokuz Mayıs 1; doğrulanmış open=0.
- Yeni özel üniversite duyuruları: Antalya Belek 1, Yaşar 1 (deadline null), Özyeğin 1; kamuya eklenmez.
- Tarsus ve Düzce Öğretim Elemanı önceki H2 kayıtlarıyla aynı; tekrar sayılmadı.
- Bu ek kayıtların tamamı henüz Worker ingest/dedup işleminden geçmedi. collectedAt=null, firstSeenAt=null kalır. Bu elden sayım 6/47 veya 12/55 geçmiş sayılarını geriye dönük güncellemez.

**KARAR:** Yeni PDF araştırması, kamuya 3 tarih/doğrulama koşulunu sağlayan aday duyuru daha buldu; bunun 2'si yeni kurum kaynağıdır. Toplam 10/100 kabulü veya production GET/D1 kanıtı anlamına gelmez. C1 RED kalır.
