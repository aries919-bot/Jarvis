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
