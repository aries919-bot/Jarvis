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

## Düzeltme — H1 exact-head emulator run tamamlandı (26.11.2026)
- Önceki H1 in_progress ifadesi bu ekle geçersizdir. Run #45712459718 artık FAILURE: https://github.com/aries919-bot/Jarvis/actions/runs/45712459718.
- XML artefact bağımsız yeniden sayım: 4 testcase /1 PASS /3 FAIL /0 error /0 skipped. Üç başarısız UI testinin tamamında Espresso RootViewWithoutFocusException ve DecorView has-window-focus=false; SQLite last-good recovery testi PASS.
- Job 139550204961 step7 instrumentation FAILURE, step8 XML gate FAILURE, step9 artifact upload SUCCESS. Artifact #15069268534, SHA-256 f9d848088c2ad8ca49985c0aeb44279f2e94202a1b1ecb8e9069e09ba97628d7.
- Diagnostics: emulator bağlı, sys.boot_completed=1, system_server PID preflight/failure 552, Package service bulundu, /data 7.0G boş. WindowManager logunda test EmptyActivity öne alınarak Jarvis task'ının arkaya geçtiği sırada Espresso odağı kayboluyor; slow-dispatch/monitor-contention kayıtları var. Bu focus handoff olasılığını güçlendirir; tek kök neden kesinleşmiş sayılmaz.
- AndroidX Google commit 18.09.2026 benzer EmptyActivity geçişi sonrası window focus kaybını tarif eder; test helper doğrudan UI thread View.performClick ve focus zorunlu olmayan view readiness yaklaşımına geçmiş: https://android.googlesource.com/platform/frameworks/support/+/36c1b8edfc954907a546e09c7f42e20d39ecb083 . Bu analog kanıttır, Aries kök nedeni kanıtı değildir.
- Ayrıntılı hata dosyası: gorevler/H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md (commit e1faa2eea61c3a4bcbd59d9f89f3a6a6a4141ff6).
- Sonraki adım test harness'i dar kapsamla düzeltmek: ActivityScenario lifecycle + focus gözlemi, UI thread doğrudan view click/readiness; dört test ve iddiaları koru, ayrıca gerçek tap/focus kapsamını bırakma. Aynı HEAD'i kör rerun etme; XML gate gevşetme yok.
**KARAR:** H1 RED; APK/unit SUCCESS yalnız build/unit kanıtı, emulator 4 testten 3 fail. PR #71 merge/deploy yok.
## H1 yeni exact-head kontrolü — 26.11.2026
- Bir önceki emulator run #45712459718 hâlâ FAILURE (4 test /1 PASS /3 FAIL); sonuç silinmez.
- PR #71 yeni HEAD b51c53a5797c19d4e18ed81b679024fc14fa3723; scripts/run-h1-emulator-tests.sh içine window/activity/process diagnostics ve instrumentation öncesi ANR/Application Error focus preflight/recovery eklendi.
- Eski logcat'te NexusLauncherActivity top-resumed state loss timeout ve pause timeout bulundu; doğrudan ANR dialog snapshot'ı yok. Yeni preflight bunu ölçmeyi amaçlar.
- H1 emulator #45716694004 ve APK/unit #45716694105 IN_PROGRESS; ilkinde job #139564450544 step1–6 success, step7 running; ikincide unit success, APK build running. Henüz XML/artifact yok.
- Ayrıntılı takip: gorevler/H1-ROOT-WINDOW-FOCUS-FAILURE-2026-11-26.md. H1 RED/beklemede, merge/deploy yok.

## 20.11.2026 Resmî Gazete 33436 — ek kamu ve özel üniversite ilanları (26.11.2026 kontrolü)

Resmî Gazete PDF: https://www.resmigazete.gov.tr/eskiler/2026/11/20261120.pdf. PDF metin katmanı incelendi. Dosya yaklaşık 43,377,793 byte olduğundan screenshot aracı 400/timeout verdi; görsel PDF QA bu turda tamamlanamadı. Bu sınırlama metin katmanı ve kurumların kendi duyuru sayfalarıyla çapraz doğrulanmış kayıtları otomatik olarak geçersiz kılmaz; ancak screenshot kanıtı olmadığı açıkça belirtilmelidir.

### Yeni kamu duyuruları — başvuru aralığı açık, 10 ana kart

| Kurum / kategori | Duyuru | Kontenjan bilgisi | publishedAt | deadline | Başvuru / şart özeti | Resmî kurum bağlantısı |
|---|---:|---|---|---|---|---|
| Atatürk Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Üyesi alımı | Kadro adedi PDF'deki alt kadro satırlarıyla ayrıca sayılmalı; kart adedi 1 | 2026-11-20 | 2026-12-04 | Profesör/doçent/doktor öğretim üyesi kadroları; 2547 ve üniversitenin atama ölçütleri; birim/kadro özel akademik şartlar. Resmî sayfa son tarihi açıkça verir; ilanın PDF'si ve başvuru dosyası kurum sayfasından açılır. | https://atauni.edu.tr/personel-daire-baskanligi/2026/11/20/ataturk-universitesi-ogretim-uyesi-alim-ilani-20-11-2026/ |
| Mardin Artuklu Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Elemanı (Öğr.Gör.) | PDF'deki anabilim dalı/pozisyon satırları ayrı pozisyon metadata'sı; tek duyuru | 2026-11-20 | 2026-12-04 | ALES ve yabancı dil barajı (ilan tablosuna göre); 2547/ilgili yönetmelik ve birim özel koşulları. Resmî personel duyurusundaki ilan metni esas alınır. | https://artuklu.edu.tr/tr/duyurular/20112026-tarihli-ve-33436-sayili-resmi-gazete-ogretim-elemani-alim-ilani-ogrgor-2011202604122026 |
| Mardin Artuklu Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Üyesi (Prof./Doç./Dr.Öğr.Üyesi) | Birden fazla akademik kadro satırı; tek ana duyuru | 2026-11-20 | 2026-12-04 | 2547, akademik atama ölçütleri ve anabilim dalı özel nitelikleri. Başvuru usulü ve gerekli dosyalar üniversitenin ilan metninde. | https://artuklu.edu.tr/tr/duyurular/20112026-tarihli-ve-33436-sayili-resmi-gazete-ogretim-uyesi-alim-ilani-profdr-docdr-drogruyesi-20112026-04122026 |
| Necmettin Erbakan Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Üyesi alımı | Kadro adedi PDF tablosuna göre alt pozisyonlardır; ana duyuru 1 | 2026-11-20 | 2026-12-07 | Profesör/doçent/doktor öğretim üyesi; 2547 ve akademik yükseltilme/atanma ölçütleri; alan ve birim özel koşulları. Başvuru dosyaları üniversitenin Personel Daire Başkanlığı duyurusuna göre teslim edilir. | https://erbakan.edu.tr/tr/birim/personel-db/duyuru/ogretim-uyesi-alim-ilani-20-11-2026-29285 |
| Abdullah Gül Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Elemanı alımı | Öğretim görevlisi/araştırma görevlisi kadro satırları; tek ana duyuru | 2026-11-20 | 2026-12-04 mesai bitimi | ALES/yabancı dil koşulları ve kadro özel nitelikleri; araştırma görevlisi için ilgili lisansüstü program/öğrencilik koşulu. Resmî PDB sayfası son tarihi açıklar; başvuru formları ve sınav takvimi aynı sayfada. | https://personel.agu.edu.tr/duyuru/oegretim-elemani-alim-ilani-20-11-2026 |
| Bartın Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Üyesi alımı (2026/03) | Birden çok akademik kadro; tek ana duyuru | 2026-11-20 | 2026-12-04 23:59 | 2547 ve Bartın Üniversitesi öğretim üyeliğine atanma ölçütleri; alan/fakülte özel koşulları. Başvuru UBYS akademik atama portalı üzerinden. 20 Kasım'daki 4/B personel duyurusundan ayrıdır. | https://akademiktayin.bartin.edu.tr/duyurular/ogretimuyesi202603veogretimelemanialimilani202603.html |
| Bartın Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Elemanı alımı (2026/03) | Öğretim görevlisi/araştırma görevlisi alt kadroları; tek ana duyuru | 2026-11-20 | 2026-12-04 23:59 | ALES en az 70 ve ilandaki yabancı dil/alan koşulları; 2547 ve ilgili yönetmelik. Başvuru şekli ve belge listesi üniversitenin resmî ilanında. | https://akademiktayin.bartin.edu.tr/duyurular/ogretimuyesi202603veogretimelemanialimilani202603.html |
| Balıkesir Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Üyesi alımı | Birden çok akademik kadro; tek ana duyuru | 2026-11-20 | 2026-12-04 mesai bitimi | Profesör/doçent/doktor öğretim üyesi; 2547 ve üniversite atama ölçütleri ile kadro özel şartları. Başvuru usulü PDB duyurusu ve PDF'de. | https://personel.balikesir.edu.tr/duyurular/20-11-2026-tarihli-ogretim-uyesi-ilanimiz-202611201405 |
| Balıkesir Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Elemanı alımı | Öğretim görevlisi/araştırma görevlisi alt kadroları; tek ana duyuru | 2026-11-20 | 2026-12-04 mesai bitimi | ALES en az 70; yabancı dil en az 50 ve ilan/alan özel şartları. Başvurular ilgili fakülte/yüksekokula, ilanda yazan yöntemle teslim edilir. | https://personel.balikesir.edu.tr/duyurular/20-11-2026-tarihli-ogretim-elemani-ilanimiz-202611201352 |
| İstanbul Üniversitesi-Cerrahpaşa — Kamu / KPSS'siz akademik | 1 Öğretim Elemanı alımı | Öğretim görevlisi/araştırma görevlisi kadroları; tek ana duyuru | 2026-11-20 | 2026-12-04 17:00 | ALES/yabancı dil ve kadro özel koşulları. Başvurular APBS üzerinden elektronik olarak yapılır; şahsen/posta ile başvuru yerine resmî APBS duyurusu esas alınır. | https://apbs.iu.edu.tr |

**20 Kasım aktif yeni kart yeniden sayımı:** Atatürk 1 + Mardin Artuklu 2 + Necmettin Erbakan 1 + Abdullah Gül (öğretim elemanı) 1 + Bartın akademik 2 + Balıkesir 2 + İstanbul Üniversitesi-Cerrahpaşa (öğretim elemanı) 1 = **10 ayrı ana duyuru**. Bu satırların pozisyon/kadro sayısı ilan kartı sayısına çevrilmez. Bartın Üniversitesi'nin 20.11.2026 tarihli 4/B sözleşmeli personel ilanı daha önceki altı adayın içindedir; burada yeniden sayılmamıştır.

### 20 Kasım Resmî Gazete'de deadline null — doğrulanamadı, açık sayaca dahil değil

| Kurum / kategori | Duyuru | publishedAt | deadline | Gerekçe ve resmî bağlantı |
|---|---|---|---|---|
| Abdullah Gül Üniversitesi — Kamu / KPSS'siz akademik | 1 ayrı Öğretim Üyesi alımı | 2026-11-20 | null | Resmî Gazete metni yayımdan itibaren 15 gün ifadesi kullanıyor; üniversitenin PDB sayfasında bu ayrı öğretim üyesi duyurusu için takvim doğrulanamadı. Son gün hesaplanmadı. https://personel.agu.edu.tr |
| İstanbul Üniversitesi-Cerrahpaşa — Kamu / KPSS'siz akademik | 1 ayrı Öğretim Üyesi alımı | 2026-11-20 | null | Resmî Gazete metni bulundu; üniversite APBS/PDB'de bu başlığın kesin son günü doğrulanamadı. Öğretim elemanı başlığına ait 04.12.2026 17:00 tarihi bu ayrı öğretim üyesi duyurusuna taşınamaz. https://apbs.iu.edu.tr |

### 20 Kasım özel üniversite duyuruları — kamu sayacından ayrı

| Kurum / kategori | Duyuru | publishedAt | deadline | Başvuru / şart özeti | Resmî kaynak |
|---|---|---|---|---|---|
| Beykoz Üniversitesi — Büyük özel / KPSS'siz akademik | 1 karma öğretim üyesi + öğretim elemanı duyurusu | 2026-11-20 | null | Öğretim üyesi metninde 15 günlük dönem, öğretim elemanı bölümünde ayrı 04.12 son günü var; tek ana duyuruya tek deadline atamak için alt başlık bazlı kartlaştırma gerekir. Kesinleştirilene kadar Doğrulanamadı. | https://www.beykoz.edu.tr |
| Hasan Kalyoncu Üniversitesi — Büyük özel / KPSS'siz akademik | 1 Öğretim Üyesi alımı | 2026-11-20 | null | Resmî kurum sayfasında ilan metni bulundu; kesin son gün bu incelemede kurumsal takvim alanından doğrulanamadı. Doğrulanamadı etiketi. | https://personel.hku.edu.tr/ogretim-uyesi-alim-ilani-53/ |

### Adli Tıp Kurumu — gelecek başvuru, güncel açık sayaca girmez

| Kurum / kategori | Duyuru | Pozisyon | publishedAt | applicationOpensAt | deadline | Başvuru / şart | Resmî bağlantı |
|---|---|---:|---|---|---|---|---|
| Adli Tıp Kurumu — Kamu / KPSS | 1 adet 4/B duyurusu | 181 pozisyon | 2026-11-20 | 2026-11-30 09:00 | 2026-12-06 23:59 | e-Devlet üzerinden Adli Tıp Kurumu/Kariyer Kapısı Kamu İşe Alım ekranı; her pozisyon için ilgili KPSS P3/P93/P94 türünden en az 70; 657/4-B genel şartları ve Ek-1'de unvan/öğrenim/özel şartlar. 181 pozisyon **1 duyurudur**, 181 ilan değildir. Başvuru henüz açılmadığından 26 Kasım current open=0. | https://atk.gov.tr/haber20.11.26.html ; pozisyon ve özel şart PDF: https://www.atk.gov.tr/Pdf/2026-2/ek-1.pdf |

### Sağlık Bakanlığı — kura duyurusu, başvurusu geçmiş (ayrı Kamu/Kura geçmişi)

- Sağlık Bakanlığı/Yönetim Hizmetleri GM, 2026 Yılı 3. Dönem İlk Defa ve Yeniden Atama Kurası: original notice 2026-10-30, EKİP tercih başvurusu 2026-11-05 18:00'de kapandı. 2026-11-19 başvuru değerlendirme sonuçları ve 20 Kasım'a kadar itiraz; 2026-11-25 itiraz sonuçları; kura 2026-11-26 10:30. 25 Kasım sayfası yeni personel ilanı değil, eski kura sürecinin sonuç/kura zamanı duyurusudur. `publishedAt=2026-10-30`, `deadline=2026-11-05T18:00`, güncel open=0; münhal kadro ekinin pozisyon adedi bu notta yeniden sayılmadığından sayı null bırakılır.
- Resmî süreç ve 25 Kasım sonuç sayfası: https://yhgm.saglik.gov.tr/TR-121745/2026-yili-3-donem-ilk-defa-ve-yeniden-atama-kurasi.html ; resmî GM duyuru dizini: https://yhgm.saglik.gov.tr/ . EKİP arayüzü giriş gerektirdiğinden kazınmaz; yalnızca resmî sayfaya yönlendirilir.

### 26.11.2026 tarihli aktif adayların toplu yeniden sayımı

- İlk aday kümesi: 6 ana duyuru (Sahil Güvenlik, HMB, SSB, Bartın 4/B, AYBÜ, MCBÜ).
- 23 Kasım RG ek turu: TÜRKAK 1 + Yozgat Bozok 1 + Düzce ayrı Öğretim Üyesi 1 = 3 yeni ana duyuru. İçişleri Bakanlığı 1 ayrı future notice/1.170 pozisyon; Tarsus ve Düzce Öğretim Elemanı H2 mevcut kayıtları exact notice ile eşleştiğinden tekrar sayılmaz.
- 20 Kasım RG ek turu: yukarıda sayılan 10 yeni ana duyuru / 7 kurum; Bartın kurumu zaten ilk altı içindedir.
- Sonuç: **19 aktif, deadline doğrulanmış kamu duyuru adayı / 15 kurum**. Bu, resmî metin ve kurum sayfası manuel araştırma kümesidir; Worker ingest sayısı değildir. 19 kartın sayımı kontenjanlardan değil ayrı ana ilan başlıklarından yapılmıştır.
- Gelecek başvuru: SGK 1 + Kültür ve Turizm Bakanlığı 1 + İçişleri Bakanlığı 1 + Adli Tıp Kurumu 1 = **4 duyuru**; 26 Kasım açık sayısına 0.
- Deadline null/Doğrulanamadı kamu adayları: AGÜ ayrı Öğretim Üyesi 1 + İÜ-Cerrahpaşa ayrı Öğretim Üyesi 1 + Kütahya Dumlupınar 1 + Ondokuz Mayıs Üniversitesi 1 = **4 duyuru**; kesin son tarih olmadan açık ilan sayısına 0.
- Expired kamu kayıtları: MEB e-Olgunlaşma 3 duyuru/2 kurum (önceki bölümdeki tarihlerle) + Sağlık Bakanlığı 3. Dönem İlk Defa/Yeniden Atama 1 kura duyurusu; bunlar güncel open sayısından çıkarılır. Sağlık kura pozisyon sayısı bu notta null.
- Özel üniversiteler ayrı: 23 Kasım Belek 1, Yaşar 1 (deadline null), Özyeğin 1; 20 Kasım Beykoz 1 (tek deadline çözülememiş), Hasan Kalyoncu 1 (deadline null); kamu 19/15 hesabına dahil değildir.
- 26 Kasım Resmî Gazete index'te kalan kurum adayları (SUEN, Özburun, KSÜ, Mersin vb.) bireysel kurum PDF'si/duyurusu doğrulanamadığından Doğrulanamadı bölümünde kalır; bu aktif sayaç toplamına eklenmemiştir.

### Worker, tazelik ve kabul eşiği — aynen korunur

- Worker/H2 son iyi kamu feed'i: 6 kaynak /47 kart (e-Şube 42; Anadolu 1; Düzce 1; Marmara 1; Tarsus 1; Zafer 1). Son ölçüm 24–25 Kasım ve 12 saat freshness eşiğinin dışında; stale last-good.
- Önceki manuel kamu envanteri: 12 kaynak /55 kart. Bu başka katmandır; 6/47 ile toplanmaz.
- 19/15 sayısı manuel aday araştırma notunun sayısıdır; Worker/H2 feed'e ingest edilmemiştir. Her adayda `collectedAt=null`, `firstSeenAt=null` olarak kalır. İlan kaynağında yayın günü yoksa `publishedAt=null`.
- Kamu sayısı sıfır değildir; Worker veri katmanı stale'dir. 10 kaynak/100 doğrulanmış, süresi geçmemiş ilan eşiği hâlâ sağlanmamıştır. Kamu özel ayrı raporlanır; üretim Worker authenticated GET/D1 ve gerçek ingestion/dedup kanıtı olmadan C1 ONAY verilemez.

**KARAR:** Manuel araştırmada 19 aktif kamu ana duyurusu /15 kurum doğrulanmış adaydır; 4 gelecek, 4 deadline-null, 4 expired duyuru ayrıca etiketlenmiştir. Bunların hiçbiri Worker count değildir. C1 genel karar RED; production GET/D1 entegrasyon kanıtı yok.

## Ek doğrulama ve sayım düzeltmesi — 21–25 Kasım RG taraması (26.11.2026)

### 24.11.2026 Resmî Gazete 33440 — 4 yeni kamu akademik duyurusu

Resmî günlük index `https://www.resmigazete.gov.tr/ilanlar/eskiilanlar/2026/11/20261124-4.htm` dört üniversite duyurusunu ayrı kurum başlıklarıyla listeler; ilgili PDF parçalarından ve kurumların kendi ilan portallarından aşağıdaki 4 ana kart doğrulanmıştır. PDF screenshot denemesi dosya boyutu nedeniyle 400/time-out verdi; metin katmanı ve resmî kurum portalları kullanıldı.

| Kurum / kategori | Duyuru | Kontenjan / alan | publishedAt | deadline | Başvuru / şart özeti | Resmî link |
|---|---:|---|---|---|---|---|
| Marmara Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Elemanı (Öğr.Gör./Arş.Gör.) | Birim/program tablosundaki kadrolar alt pozisyondur; tek ana duyuru | 2026-11-24 | 2026-12-08 | Online Marmara personel başvuru sistemi. 2547 ve ilgili öğretim elemanı yönetmeliği; ALES 70 ve yabancı dil/alan koşulları ilan tablosuna göre. | https://basvuru.marmara.edu.tr/personel ; Resmî Gazete PDF https://www.resmigazete.gov.tr/eskiler/2026/11/20261124.pdf (s.94–95) |
| Marmara Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Üyesi | 2 kadro: Dr. Öğr. Üyesi 1, Profesör 1 | 2026-11-24 | 2026-12-08 | Online Marmara personel başvuru sistemi. 2547 ve üniversite akademik yükseltme/atanma kriterleri; Eğitim Yönetiminde yüksek lisans+doktora, Q1 SSCI yayın/proje ve İngilizce ders tecrübesi; Halkla İlişkiler doçentliği, araştırma projesi yürütücülüğü ve İngilizce ders yeterliği. | https://basvuru.marmara.edu.tr/personel ; Resmî Gazete PDF https://www.resmigazete.gov.tr/eskiler/2026/11/20261124.pdf (s.96) |
| Çanakkale Onsekiz Mart Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Üyesi | Fakülte/anabilim dalı tablosundaki akademik kadrolar; ana kart 1 | 2026-11-24 | 2026-12-08 23:59 | Üniversitenin çevrimiçi ilan bilgi sistemi. 2547, 657 genel şartları ve üniversite akademik yükseltme/atanma ölçütleri; özel akademik alan/derece koşulları portalda. | https://ilan.comu.edu.tr/ilan_bilgi/56 ; kurum duyurusu https://www.comu.edu.tr/duyuru-25060.html |
| Çanakkale Onsekiz Mart Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Elemanı (Öğr.Gör./Arş.Gör.) | İki ilan programı/kadrosu alt pozisyon; ana kart 1 | 2026-11-24 | 2026-12-08 23:59 | Aynı üniversite ilan bilgi sistemi üzerinden online. ALES en az 70, merkezi yabancı dil en az 50; araştırma görevlisi için 35 yaş sınırı ve tezli yüksek lisans/doktora/sanatta yeterlik öğrenciliği; kadroya özel mezuniyet/tecrübe şartları portalda. | https://ilan.comu.edu.tr/ilan_bilgi/57 ; kurum duyurusu https://www.comu.edu.tr/duyuru-25060.html |

Yeniden sayım: Marmara 2 + ÇOMÜ 2 = **4 yeni ana duyuru / 2 yeni kamu kurumu**. Kadro veya pozisyon satırları ilan kartı sayısını artırmaz. Başvuru bitişleri kurum portallarında doğrulanmıştır; her iki kurumun da `publishedAt=2026-11-24`.

### 21.11.2026 Resmî Gazete 33437 — deadline netleşmeyen kamu adayı

| Kurum / kategori | Duyuru | publishedAt | deadline | Başvuru / şart bilgisi | Durum |
|---|---:|---|---|---|---|
| İstanbul Medeniyet Üniversitesi — Kamu / KPSS'siz akademik | 1 Öğretim Üyesi akademik ilanı | 2026-11-21 | null | Personel Daire Başkanlığı arşivinde 21.11.2026 tarihli akademik ilan bağlantısı mevcut; Resmî Gazete PDF bağlantısı 400/time-out verdi. Kurum sayfasında kesin son başvuru takvimi görünmediğinden 15 gün hesabıyla tarih türetilmedi. | Doğrulanamadı / deadline null; current active count'a eklenmez |

Resmî kurum arşivi: https://personel.medeniyet.edu.tr/tr/akademik-ilanlar (21.11.2026 Tarihli Akademik İlan satırı). Resmî Gazete günlük dosya: https://www.resmigazete.gov.tr/eskiler/2026/11/20261121.pdf . PDF tıklaması timeout olduğundan ilan metninin şartları ve deadline'ı tam doğrulanamadı.

### 25.11.2026 Resmî Gazete 33441 — yeni personel ilanı eklenmedi

Resmî index: https://www.resmigazete.gov.tr/ilanlar/eskiilanlar/2026/11/20261125-4.htm . Kurum/ilan başlıkları arasında:
- Biruni Üniversitesi'nin 13.11.2026 tarihli Öğretim Elemanı ilanı iptal edilmiş; yeni işe alım değil, mevcut kartın iptal durumu. İlgili Biruni kartı güncel feed'den çıkarılmalı/iptal etiketi almalı.
- Adalet Bakanlığı Ceza ve Tevkifevleri GM, Çevre Bakanlığı Yapı İşleri/Yerel Yönetimler, MEB Destek Hizmetleri/Temel Eğitim, MSB Tedarik Hizmetleri ve Marmara Üniversitesi başlıkları ihale yasaklama kararlarıdır; personel ilanı değildir.
- Çağ Üniversitesi'nin 25.11.2026 akademik duyurusu özel üniversite havuzunda 1 ana kart /2 araştırma görevlisi pozisyonu olarak doğrulandı; son başvuru 2026-12-09. Kurum duyurusu https://www.cag.edu.tr/tr/duyurular/akademik-personel-alimi-26 . İlgili kadrolar Uluslararası Finans ve Bankacılık ile Uluslararası Ticaret ve Lojistik alanlarında; ilan şartları/teslim yöntemi üniversitenin sayfasındadır. Kamu sayımına eklenmez.
- Manisa Celal Bayar Üniversitesi'nin 25.11.2026 Öğretim Üyesi ilanı daha önceki 6 aday kaydında 1 ana duyuru olarak yer alır; tekrar sayılmaz.
- İndeksin kalan kalemleri koruma kurulu kararları/diğer duyurular; ek doğrulanmış personel duyurusu olarak sayılmadı.

Biruni iptal metninin açık kaydı: 13.11.2026 tarihli önceki ilan iptal edilmiştir; eski ilanın bitiş tarihi 27.11.2026 yazsa da iptal duyurusundan sonra aktif gösterilmemelidir. Resmî indexteki bölüm: https://www.resmigazete.gov.tr/ilanlar/eskiilanlar/2026/11/20261125-4.htm . 25 Kasım endeksi 52 kaynak bölümüne ayrılmıştır; ikincil dizin dökümü kaynakların bir kısmının ihale yasağı/koruma kurulu kararı olduğunu gösterir ve tam kapsama iddiası taşımaz. Bireysel kartta esas kaynak Resmî Gazete / kurum metnidir.

### 20.11.2026 RG listesinde görünen Orman GM müfettiş yardımcısı — geçmiş ilan düzeltmesi

| Kurum / kategori | Duyuru | Pozisyon | publishedAt | deadline | Şart özeti | Etiket |
|---|---:|---:|---|---|---|---|
| Orman Genel Müdürlüğü — Kamu / KPSS | 1 Müfettiş Yardımcılığı Giriş Sınavı | 12 | 2026-10-23 | 2026-11-09 23:59 | İlgili orman mühendisliği, orman endüstri mühendisliği, hukuk, iktisat/işletme/maliye lisansları; mezuniyet grubuna göre KPSS P3/P4/P48 en az 75; 35 yaş sınırı; başvuru e-Devlet/Kariyer Kapısı üzerinden alınmıştır. | Süresi geçti |

Bu duyuru 20 Kasım sayısının dizininde görünse de 20 Kasım'da yayımlanmış yeni ilan değildir: Orman GM'nin kendi sayfasında gerçek `publishedAt=2026-10-23`, başvuru 26.10–09.11.2026 olarak yazılıdır. 23 Kasım'daki yazılı sınava çağrılma duyurusu da yeni işe alım ilanı değil sonuç/başvuru değerlendirme duyurusudur. 12 kontenjan 1 ana duyurudur. Resmî kaynak: https://www.ogm.gov.tr/tr/duyurular/tarim-ve-orman-bakanligi-orman-genel-mudurlugu-mufettis-yardimciligi-giris-sinavi-i%CC%87lani ; sonuç duyurusu: https://www.ogm.gov.tr/tr/duyurular/tarim-ve-orman-bakanligi-orman-genel-mudurlugu-mufettis-yardimciligi-giris-sinavinin-yazili-bolumune-katilmaya-hak-kazanan .

### Özel üniversite havuzuna ekler

- Çağ Üniversitesi — 1 akademik ana duyuru /2 araştırma görevlisi pozisyonu; `publishedAt=2026-11-25`, `deadline=2026-12-09`; başvuru üniversitenin İnsan Kaynakları birimine ilanda belirtilen yöntemle. Özel üniversite kaydıdır; kamu sayaçlarına girmez. Resmî duyuru: https://www.cag.edu.tr/tr/duyurular/akademik-personel-alimi-26 .
- İstanbul Nişantaşı Üniversitesi — 24.11.2026 Resmî Gazete dizininde bir öğretim üyesi duyuru başlığı yer alır. Bireysel PDF erişimi timeout ve üniversitenin ilgili sayfasında tam başvuru şart/takvim bu kontrolde doğrulanamadı; `publishedAt=2026-11-24`, `deadline=null`, `Doğrulanamadı`; özel kurum havuzunda 1 aday, açık özel ilan sayısına dahil değil. Resmî index: https://www.resmigazete.gov.tr/ilanlar/eskiilanlar/2026/11/20261124-4.htm .

### Düzeltilmiş toplu sayım — 26.11.2026

- Aktif, deadline'ı resmî kaynakla doğrulanmış kamu ana duyuruları: önceki 19 + 24 Kasım Marmara 2 + ÇOMÜ 2 = **23 duyuru /17 benzersiz kurum**. Sayım ana ilan başlığına göredir, pozisyon sayısına göre değil.
- Kamu gelecek başvurular: **4 duyuru** (SGK, Kültür ve Turizm Bakanlığı, İçişleri Bakanlığı, Adli Tıp Kurumu); applicationOpensAt ileride olduğundan current open=0.
- Kamu deadline null/doğrulanamayan: önceki 4 + İstanbul Medeniyet 1 = **5 aday**; aktif/verified sayısına eklenmez.
- Süresi geçmiş kamu duyuruları: önceki MEB 3 + Sağlık Bakanlığı 1 kura + OGM 1 müfettiş yardımcısı = **5 ana duyuru**. OGM 12 pozisyon, 1 duyurudur.
- Özel üniversite aday havuzu: önceki 5 + Çağ 1 + Nişantaşı 1 = **7 aday/duyuru**; deadline null olanlar Doğrulanamadı; bu sayı kamu 23/17'ye eklenmez.
- 25 Kasım Resmî Gazete taraması public active sayıya ek ilan getirmedi. MCBÜ mevcut kamu kaydıdır; Çağ özel havuza eklenmiştir; Biruni iptal ilanı aktiften çıkarılmalıdır. Tender bans, protection board decisions, sınav sonucu/duyuruları vacancy card değildir.
- 23/17 manual count is still not Worker ingest. For all manual-only candidates `collectedAt=null`, `firstSeenAt=null`; production Worker GET/D1 confirmation still missing.

**KARAR:** Kamu sıfır değil. Resmî kaynakla deadline doğrulanan manuel araştırma kümesi 23 ana duyuru/17 kurumdur; bu 100 doğrulanmış ilan eşiğini karşılamaz ve Worker feed sayısı değildir. 10 kaynak/100 doğrulanmış/süresi geçmemiş ilan ve production authenticated GET/D1 kapıları geçilmediğinden C1 RED.

## Düzeltme — İstanbul Nişantaşı Üniversitesi 24.11.2026 ilanı resmî PDF ile doğrulandı

Önceki bölümde Nişantaşı kaydı `deadline=null / Doğrulanamadı` olarak tutulmuştu. Bu ifade artık geçersizdir. Üniversitenin kendi duyuru sayfasındaki 24.11.2026 ilanı, dokuz sayfalık resmî ilan PDF'si ve İnsan Kaynakları akademik ilan portalı açıldı ve birbirleriyle eşleştirildi.

| Kurum / kategori | Duyuru kartı | publishedAt | deadline | Başvuru yolu | Şart özeti | Resmî kanıt |
|---|---:|---|---|---|---|---|
| İstanbul Nişantaşı Üniversitesi — Büyük özel / KPSS'siz akademik | 1 karma Öğretim Elemanı Alım İlanı | 2026-11-24 | 2026-12-08 | Yalnızca https://insankaynaklari.nisantasi.edu.tr/akademisyen-ilanlari adresindeki dijital başvuru sistemi. İstenen belgeler PDF yüklenir; profesör/doçent/dr. öğretim üyesi için özgeçmiş, öğrenim belgeleri, akademik çalışma/yayın dosyaları ve üniversitenin ek koşul puan tablosu; öğretim görevlisi/araştırma görevlisi için başvuru dilekçesi, ALES/yabancı dil, diplomalar/transkript ve ilgili belgeler. | 2547 ve ilgili öğretim üyesi/öğretim elemanı mevzuatı. Kadro ve programa göre özel diploma, uzmanlık/doktora, yayın/tecrübe; tabloda bazı programlarda ALES 70, yabancı dil 50 veya İngilizce programlarda 85. Araştırma görevlisi ataması 2547 50/d. | Kurum duyurusu https://www.nisantasi.edu.tr/duyuru/24112026-akademik-ilan-236481 ; 9 sayfalık resmî PDF https://www.nisantasi.edu.tr/Uploads/Akademisyenler/ik-kadro/24_11.2026%20tarihli%20resmi%20gazete%20%C4%B0lan%C4%B1.pdf ; başvuru portalı https://insankaynaklari.nisantasi.edu.tr/akademisyen-ilanlari |

PDF'nin ilk sayfasında tek ana başlık `ÖĞRETİM ELEMANI ALIM İLANI` bulunur; tabloda profesör, doçent, doktor öğretim üyesi, öğretim görevlisi ve araştırma görevlisi sütunları birlikte yer alır. Bu nedenle alt program/pozisyon satırlarını ayrı ana duyuru kartı yapmayın. Portalın tabloda her kadro satırı için ayrı `İlan No` ve `BAŞVUR` bağlantısı vermesi, duyurunun ana kartını çoğaltma gerekçesi değildir.

İlanın kendi metni başvuru başlangıcını 24.11.2026, bitişini 08.12.2026 olarak açıkça verir; ön değerlendirme 11.12, sınav 15.12, sonuç 18.12 tarihleridir. Sınav tarihi deadline veya publishedAt olarak kullanılmaz. Bu kayıt özel kurum havuzunda `Doğrulandı` olarak yer alır; kamu sayımına eklenmez.

**Özel kurum sayımı düzeltmesi:** özel havuzdaki Nişantaşı 1 duyuru adedi zaten 7 özel adayın içindeydi; yalnızca doğrulama etiketi ve deadline düzeltildi. Özel havuz toplamı hâlâ 7 aday/duyurudur. Kamu aktif toplamı 23/17 değişmez.

PDF ekran görüntüleri sayfalar 1–9 için alındı ve tablo/giriş metni görsel olarak kontrol edildi; böylece bu Nişantaşı PDF'si için önceki screenshot eksikliği giderildi.

## 26.11.2026 — PR #11 exact-head H2 + C1 birleşik canlı feed (yeni geçerli ölçüm)

### Exact SHA / test kanıtı
- Private `aries919-bot/jarvis-cloud` PR #11: HEAD `b1415d6a76fd3856c60cfec891a575c3bef0341e`; H2 reader sabit SHA `fe37be93202442394504f455b4a1caeb31f298cf`. PR açık/taslak, base `fix/c1-source-category-after-timestamp`, 6 dosya / +853 / -27; formal review 0. [PR #11](https://github.com/aries919-bot/jarvis-cloud/pull/11).
- Public exact-head QA [run #45651444155](https://github.com/aries919-bot/Jarvis/actions/runs/45651444155) SUCCESS. Log, private checkout'un tam `b1415d6a...` SHA'sini ve H2'nin tam `fe37be93...` SHA'sini doğruluyor.
- Aynı koşuda H2 Python testleri **60/60 PASS**, C1 Node testleri **56/56 PASS**, yerel Worker/D1 smoke **11/11 PASS**. Bu yerel D1 testidir; production değildir.
- Live artifact [#15041491104](https://github.com/aries919-bot/Jarvis/actions/runs/45651444155/artifacts/15041491104), ZIP SHA-256 `8c1e133c4543da5c1980e1c815f16ad09c690a096efe521c62570c2db42aca18`. Feed `collectedAt=2026-11-25T16:11:57.566Z`.

### Birleşik feed yeniden sayımı
- Toplam kaynak kaydı **35**; **27 fresh / 8 stale**.
- Benzersiz kurum sayısı (doğrulanmış açık ilanı bulunan): **12**; doğrulanmış açık ilan kartı: **55**.
- Public gate: **12 kurum /55 kart**. Kaynak eşiği 10/10 geçti; kart eşiği 55/100 kaldı, **45 kart eksik**. Genel eşik RED.
- Diğer sayaçlar: 171 toplam kayıt; 55 açık deadline; 116 deadline-unknown; 0 past; 0 cross-source duplicate; 0 possible-duplicate group; 0 unresolved duplicate.
- Kamu dışı özel ATS havuzu: 9 fresh kaynaktan 112 aday kayıt; deadline bilinmediğinden **0'ı doğrulanmış açık ilana eklenir**. Altı özel kaynak robots HTTP 401 nedeniyle stale; hedef sayfalarına istek gönderilmedi.
- Public 4 belirsiz kayıt 55'e eklenmedi: ATK başvurusu 30.11.2026 09:00'da açılacak; Munzur'un 25.11.2026 tarihli iki duyurusunda saat/mesai cutoff belirsiz; YHGM kaydı eski kura/sonuç duyurusu. BOTAŞ timeout ve Mardin Artuklu öğretim üyesi deadline belirsizliği kaynak bazlı stale kalır.

### Kamu kaynağı başına açık kart sayımı
| Kurum/kaynak | Kart | Not |
|---|---:|---|
| İŞKUR e-Şube JSON | 42 | 42 ayrı açık kayıt; ilan detay sayfası kazınmadı |
| Anadolu Üniversitesi | 1 | H2 |
| Atatürk Üniversitesi | 2 | İki ayrı ana duyuru |
| Balıkesir Üniversitesi | 1 | H2 |
| DOKA | 1 | H2 |
| Düzce Üniversitesi | 1 | H2 |
| Mardin Artuklu Üniversitesi | 1 | H2; öğretim elemanı ilanı |
| Marmara Üniversitesi | 2 | Öğretim elemanı + öğretim üyesi, ayrı duyurular |
| ORAN | 1 | 6 uzman + 1 destek; tek ana duyuru |
| Tarsus Üniversitesi | 1 | H2 |
| Türkiye Turizm Tanıtım ve Geliştirme Ajansı | 1 | H2 |
| Zafer Kalkınma Ajansı | 1 | 2 uzman; tek ana duyuru |
| **Toplam** | **55** | **12 benzersiz kurum** |

### H2 alt kümesi ve manuel envanter ayrımı
- H2 canlı okuyucu [run #45646698179](https://github.com/aries919-bot/Jarvis/actions/runs/45646698179), artifact [#15037029949](https://github.com/aries919-bot/Jarvis/actions/runs/45646698179/artifacts/15037029949): **19 kaynak kaydı /17 fresh /2 stale**, **13 açık kart /11 kurum**. H2 kartları ORAN, DOKA ve Zafer'i zaten içeriyor.
- İŞKUR e-Şube ayrı kaynakta **1 kaynak /42 kayıt**. Birleşik Worker feed bu iki ölçümü tek JSON içinde 12 kurum/55 kart olarak sayıyor; eski 6/47 baseline artık bu exact-head entegrasyon ölçümünün yerine kullanılmaz.
- Manuel kamu envanterinin son belgelenmiş toplamı **23 ana duyuru /17 kurum** olarak korunur. ORAN, DOKA ve Zafer H2 feed'de mevcuttur; bu üç kaynağın 23/17 manuel sayımına dahil olup olmadığı satır bazlı eşleştirme olmadan söylenemez. Bu nedenle 26/20 gibi yeni bir manuel toplam üretilmez. Manuel envanter ile Worker feed arasındaki tam URL/ilan kesişimi henüz tamamlanmadı; sayılar toplanmaz.
- Yeni manuel aktif duyuruların alanları H2 JSON'da: ORAN `publishedAt=2026-10-19`, `deadline=2026-12-03`, `collectedAt/firstSeenAt=2026-11-25T16:09:42Z`; DOKA `publishedAt=2026-11-02`, `deadline=2026-12-06`, aynı source collection/first-seen; Zafer `publishedAt=2026-11-03`, `deadline=2026-12-14`, aynı source collection/first-seen. Pozisyon sayıları kart adedini artırmaz.
- Dicle Kalkınma Ajansı (DİKA) 4 uzman duyurusu ayrıca **future/opening-soon**: resmi kurum sayfası 16.11.2026'da yayımlanmış; PDF'de Resmî Gazete ilan tarihi 14.11.2026; başvuru 04.12.2026–18.12.2026. `applicationOpensAt=2026-12-04`, `deadline=2026-12-18`; 26 açık kart/20 kurum sayısına eklenmez. Kaynak: https://www.dika.org.tr/duyuru/ajansimiza-dort-uzman-personel-alimi/322 .

### Production / karar
- `PUBLISH status=SKIPPED`: `JARVIS_WORKER_URL` / `JARVIS_WORKER_TOKEN` QA ortamında yapılandırılmadı. Production authenticated GET/POST, gerçek Cloudflare D1 ve kalıcı production `firstSeenAt` kanıtı yok.
- PR #11 formal review 0 ve hâlâ taslak; PR #5 de açık/taslak. Merge/deploy yok.
**KARAR:** C1 RED/DEVAM. 12 kurum kaynağı eşiği geçti; 55/100 kart eşiği geçmedi; production GET kanıtı eksik.


### C1-3 sıradaki iş
- Görev açıldı: [C1-3 — 100 doğrulanmış açık ilan + production feed kapısı](../gorevler/C1-3-100-OPEN-LISTINGS-2026-11-26.md) (yalnız `durum` dokümantasyonu).
- Manuel kamu envanterinin son kesin belgelenmiş toplamı **23 açık ana duyuru /17 kurum**; future/opening-soon önceki 4'e yeni DİKA duyurusu eklenince **5 aday** (crosswalk teyidi bekliyor), deadline-null/Doğrulanamadı 5, süresi geçmiş 5. Özel havuz 7 aday/duyuru olarak ayrı kalır.
- Feed sayısı farklıdır: **12 kurum /55 açık doğrulanmış kart**. Manuel 26/20 ile feed 12/55 birleştirilmez; önce canonical URL/ilan kesişimi çıkarılır.
- Sonraki iş, 23 manuel duyurunun H2 13 kartıyla tam eşleştirilmesi; yalnız eksik, robots-izinli, deadline'ı açık kaynakların eklenmesidir. Production Worker GET/D1 ise ayrı blokerdir.
