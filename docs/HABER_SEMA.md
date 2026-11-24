# HABER KARTI ŞEMASI (H1)

Durum: PR AÇIK, ölçüm bekliyor. H1 bağımsız olarak kabul edilmiş değildir; resmî kaynak listesi K1-B ölçüm tablosundan sonra yalnız ÇALIŞIYOR adreslerle daraltılır.
Kapsam: İş & Kariyer haberleri. Kaynak adayı olmak, kazıma izni veya canlı ilan kanıtı değildir.

## 1. Filtreler ve kategori sınıflandırması

Arayüz filtreleri (sıra sabit): **Tümü**, **Kamu**, **Kura**, **KPSS**, **KPSS'siz**, **Büyük özel şirket**, **Kamu bağlı büyük şirket**.

Kartın `category` alanı Tümü olamaz; aşağıdaki değerlerden tam biri olmalıdır:
- `Kamu`: bakanlık, genel müdürlük, üniversite, belediye ve doğrudan kamu kurumu ilanı.
- `Kura`: noter kurası/çekiliş süreciyle ilgili duyuru. İlanın kendisi ayrıca Kamu veya ilgili işveren kategorisinde sınıflandırılabilir; kura haberi `contentType=lottery` olur.
- `KPSS`: ilanın açıkça KPSS puanı/puan türü şartı koyduğu alım.
- `KPSS'siz`: resmî ilan metninde KPSS şartı bulunmadığı doğrulanmış alım. Şartlar okunamıyorsa bu etiket verilmez.
- `Büyük özel şirket`: özel hukuk tüzel kişisi olan büyük ölçekli şirketin kendi kariyer/duyuru kanalı.
- `Kamu bağlı büyük şirket`: kamu sermayeli/bağlı büyük şirket (ör. PTT, BOTAŞ, TPAO). Şirket statüsü teyit edilmeden bu kategori kullanılmaz.

Bir ilanda birden fazla sınıflandırma anlamlıysa kartta bir ana kategori ve `tags` alanında ikincil etiketler tutulur. KPSS/KPSS'siz ayrımı ilan metnindeki şartlardan türetilir; kurumun geçmiş alımlarından tahmin edilmez.

## 2. JSON sözleşmesi

Zorunlu üst seviye:
- `schemaVersion`: sabit `1`.
- `collectedAt`: bu kaydın kaynakta son kontrol edildiği UTC ISO-8601 zaman damgası.
- `sourceId`: `career/adaylar.txt` / kaynak denetiminde tanımlı kararlı kaynak kimliği.
- `items`: kart listesi.

Her `items[]` kaydı:

| Alan | Tür | Kural |
|---|---|---|
| `id` | string | Kaynak ilan numarası varsa o; yoksa kanonik resmî URL + kurum/başlık kararlı özeti. |
| `institution` | string | İlanı veren kurum/işveren; aracı haber sitesi değil. |
| `title` | string | Resmî duyurudaki başlık; başlıkta geçen tarih sınav tarihi olabilir. |
| `category` | enum | Kamu / Kura / KPSS / KPSS'siz / Büyük özel şirket / Kamu bağlı büyük şirket. |
| `contentType` | enum | `job`, `lottery`, `result`, `internship`, `exam`, `other`. |
| `officialSourceUrl` | HTTPS URL | İlan/duyurunun resmî asıl sayfası; arama sonucu veya haber kopyası olmaz. |
| `howToApply` | object | `method`, `url`, `instructions`. Başvuru yeri/yöntemi resmî metinden aktarılır. |
| `conditions` | string[] | Eğitim, bölüm, KPSS, yaş, deneyim, belge ve diğer şartlar; yalnız kaynakta yazanlar. |
| `deadline` | ISO-8601 tarih/saat veya null | Son başvuru. Kaynakta yoksa null; başka tarihten türetilmez. |
| `publishedAt` | ISO-8601 tarih/saat veya null | Kaynağın açıkça bildirdiği yayın tarihi. Yoksa null. |
| `collectedAt` | ISO-8601 UTC | Bu kaynak kaydının son başarıyla kontrol edildiği zaman. Yayın tarihi değildir. |
| `firstSeenAt` | ISO-8601 UTC | Bu `id`'nin sistemde ilk kez görüldüğü kalıcı zaman; güncellemelerde korunur. |
| `freshnessStatus` | enum | `Doğrulandı`, `Süresi geçti`, `Doğrulanamadı`. |
| `tags` | string[] | Örn. `KPSS`, `KPSS'siz`, `önlisans`, `ortaöğretim`, `kura`. Kanıtlı olmalı. |
| `sourceStatus` | enum | `fresh` veya `stale`; kaynak bazında belirlenir. |

### Örnek kayıt (yalnız sözleşme örneği; gerçek ilan değildir)

```json
{
  "schemaVersion": 1,
  "collectedAt": "2026-11-24T12:00:00Z",
  "sourceId": "ornek-resmi-kaynak",
  "items": [
    {
      "id": "ornek-kaynak-kaydi-001",
      "institution": "Örnek kamu kurumu",
      "title": "Örnek personel alımı duyurusu",
      "category": "Kamu",
      "contentType": "job",
      "officialSourceUrl": "https://example.gov.tr/duyuru/ornek",
      "howToApply": {
        "method": "Resmî ilandaki başvuru sistemi",
        "url": "https://example.gov.tr/basvuru",
        "instructions": "Başvuru tarihleri ve belgeler resmî duyurudan kontrol edilir."
      },
      "conditions": ["Şartlar resmî duyuru metninden alınmalıdır."],
      "deadline": null,
      "publishedAt": null,
      "collectedAt": "2026-11-24T12:00:00Z",
      "firstSeenAt": "2026-11-24T12:00:00Z",
      "freshnessStatus": "Doğrulanamadı",
      "tags": [],
      "sourceStatus": "fresh"
    }
  ]
}
```

## 3. Tarih ve güncellik kuralları

1. Tarih alanları birbirinin yerine kullanılamaz. `collectedAt` yayın tarihi değildir; `firstSeenAt` da yayın tarihi değildir.
2. Kaynakta yayın tarihi yoksa `publishedAt=null`. Başlık, URL slug'ı, PDF dosya adı, arama motoru tarihi veya çekim zamanı yayın tarihi diye kaydedilmez.
3. e-Şube açık iş ilanı satırında görülen tarih son başvurudur: `deadline` alanına yazılır, `publishedAt=null` kalır.
4. Duyuru başlığındaki tarih çoğunlukla sınav/kura tarihidir. Yayın tarihi kabul edilmez.
5. `deadline < bugün` ise `Süresi geçti`; güncel listede gösterilmez, arşive alınır.
6. `deadline` yoksa veya tarih kanıtlanamıyorsa başvuru süresi açık kabul edilmez: `Doğrulanamadı`.
7. `Doğrulandı` için yayın tarihi kanıtı ve gelecekte/açık son başvuru tarihi gerekir. Saat dilimi Türkiye yerel saatiyle yorumlanır; saklama UTC'dir.
8. Geçmiş tarihli ilan yeni ilan gibi yeniden yayınlanmaz. Kaynak güncellenmişse değişiklik geçmişi saklanır.

## 4. İçerik türü / yeni iş ilanı ayrımı

- `contentType=job`: başvuru alınan iş/personel alımı. `deadline` ve şartlar bulunmadan “başvurusu açık” denmez.
- `lottery`: kura günü, kura listesi veya kura süreci. Haber'de gösterilir, yeni iş ilanı sayılmaz.
- `result`: sınav/kura/yerleştirme sonucu. Haber'de gösterilir, yeni iş ilanı sayılmaz.
- `internship`: staj programı. Haber'de gösterilir, yeni iş ilanı sayılmaz.
- `exam`: sınav/tercih takvimi. Haber'de gösterilir; personel alımı ilanıyla karıştırılmaz.

## 5. Son iyi veri ve sekme izolasyonu

- Son iyi veri tek bir global havuz değildir; `sourceId` bazında tutulur.
- Bir kaynak hata verirse yalnız o kaynağın son başarılı JSON'u `stale` olur. Diğer kaynakların güncel verileri değişmez.
- Başarısız/boş/403/500/timeout yanıtı, boş ilan listesi diye yorumlanıp son iyi veriyi ezemez.
- Kamu, kura, KPSS, KPSS'siz, özel ve kamu bağlı şirket filtreleri yalnız kendi kategori/etiket verisini gösterir. “Tümü” dışındaki sekmeler birbirinin verisini sızdırmaz.
- Tekilleştirme anahtarı `sourceId + id`; ilk görülme zamanı kalıcıdır.

## 6. Başvuru ve bağlantı güvenliği

- Her kartta resmî asıl kaynak linki bulunur. Başvuru bağlantısı ayrı `howToApply.url` alanıdır.
- Dış siteler uygulama içi WebView'da değil Android Custom Tabs ile açılır. Beyaz ekran/boş WebView gösterilmez.
- Kariyer Kapısı ve giriş isteyen e-Devlet/İŞKUR sayfaları kazınmaz; kullanıcı resmî sayfaya yönlendirilir ve girişini kendisi yapar.
- İŞKUR `iskur.gov.tr/ilanlar/*` boş kabuk olduğundan kaynak sayılmaz. e-Şube yalnız izinli, girişsiz ve CAPTCHA'sız sayfada ölçülür.
- Herhangi bir kaynak için otomasyon başlamadan robots.txt, TLS, HTTP durumu, yönlendirme, giriş/CAPTCHA ve içerik gerçekten ölçülür. Robots sonucu bilinmiyorsa otomasyon adayı değildir.
- User-Agent: `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)`. Tarayıcı taklidi, TLS doğrulamasını kapatma ve robots ihlali yasaktır.

## 7. Resmî duyuru / kariyer sayfası adayları

Aşağıdakiler H1 kategori tasarımı için resmî sayfa adaylarıdır. Bu liste canlı okuyucu onayı değildir. Her adres için robots.txt, HTTP/TLS, erişim, giriş/CAPTCHA, içerik ve ilan sayısı K1-B/K1-C/H2 aşamalarında ayrı ölçülür. Giriş isteyen sayfalar yalnız kullanıcıya yönlendirme içindir.

### Kamu

1. İŞKUR e-Şube Açık İş İlanı Arama — https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx  
   K1-E1'de kamu filtresiyle 3 sayfa/43 kayıt doğrulandı. Yayın tarihi yok; satır tarihi son başvuru tarihidir.
2. Resmî İlan Portalı — https://www.ilan.gov.tr/  
   Resmî ilan kaynağı adayı. Daha önce TLS sertifika zinciri sorunu raporlandı; doğrulama kapatılmadan K1-B'de yeniden ölçülür.
3. Resmî Gazete — https://www.resmigazete.gov.tr/ilanlar  
   Resmî ilan/ek duyurular için aday. İlan metninin personel alımı olup olmadığı ayrıca sınıflandırılır.

### Kura

1. Sağlık Bakanlığı Yönetim Hizmetleri Genel Müdürlüğü — https://yhgm.saglik.gov.tr/TR-111525/tasra-teskilati-hizmet-birimlerinde-istihdam-edilmek-uzere-2764-surekli-isci-alim-kurasi-hakkinda-duyuru.html  
   Sürekli işçi alım kura duyurusu örneği; kura ve sonuç haberlerini ayırmak için aday.
2. TÜSEB resmî duyuruları — https://www.tuseb.gov.tr/public/tr/duyurular  
   Personel alımı, noter kura ve sonuç duyuruları yayımlanıyor; başlık/tarih türü ayrıştırılmalı.
3. TÜSEB İnsan Kaynakları — https://www.tuseb.gov.tr/kurumsal/insan-kaynaklari  
   Personel alım duyurusu arşivi için aday; alt sayfalar ve erişim ayrıca ölçülecek.

### KPSS

1. ÖSYM KPSS duyuru grubu — https://osym.gov.tr/SinavGrubu/Menu/338  
   KPSS tercih kılavuzları ve yerleştirme duyuruları.
2. ÖSYM tüm duyurular — https://www.osym.gov.tr/Duyurular/Index  
   İlan/tercih/sınav/sonuç ayrımı yapılmalı; sınav tarihi yayın tarihi sayılmamalı.
3. Kariyer Kapısı Kamu İşe Alım — https://kariyerkapisi.gov.tr/isealim  
   Kullanıcıya resmî başvuru sayfası olarak yönlendirme. Giriş gerektiren bölümler kazınmaz/yoklanmaz.

### KPSS'siz

1. İŞKUR e-Şube Açık İş İlanı Arama — https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx  
   KPSS'siz sınıfı yalnız ilan şartlarında KPSS istenmediği açıkça görülen kayıtlar için kullanılır; tüm e-Şube ilanları otomatik KPSS'siz değildir.
2. Malatya Su ve Kanalizasyon İdaresi Kariyer Kapısı — https://www.turkiye.gov.tr/msvk-kariyer-kapisi-kamu-ise-alim  
   Resmî e-Devlet/Kariyer Kapısı yönlendirme adayı; oturum gerektirir, kazınmaz.
3. İzmir Büyükşehir Belediyesi Kariyer Kapısı — https://www.turkiye.gov.tr/bb-kariyer-kapisi-kamu-ise-alim  
   Resmî e-Devlet/Kariyer Kapısı yönlendirme adayı; oturum gerektirir, kazınmaz. KPSS koşulu her ilanda ayrı okunur.

### Büyük özel şirket

1. Turkcell Kariyer Fırsatları — https://www.turkcell.com.tr/hakkimizda/insankaynaklari/kariyer-firsatlari/detay  
   Şirketin kendi kariyer sayfası; pozisyonların ilan/başvuru alt sayfaları ayrıca bulunmalı.
2. Şişecam Kariyer — https://www.sisecam.com/tr/kariyer  
   Şirketin kariyer sayfası; açık pozisyon listesi https://careers.sisecam.com/?locale=tr_TR adresinde.
3. Koç Kariyerim — https://www.kockariyerim.com/jobAd/list  
   Koç Topluluğu şirketlerinin ilan platformu. Giriş/robots/otomasyon uygunluğu ayrıca ölçülür.

### Kamu bağlı büyük şirket

1. PTT A.Ş. Duyurular — https://www.ptt.gov.tr/duyurular  
   PTT personel alımı ve resmî açıklamalarının ana duyuru sayfası. PTT SSS, ilk defa atanacak personel için KPSS şartını belirtir; KPSS'siz diye etiketlenmez.
2. BOTAŞ Duyurular — https://www.botas.gov.tr/Kategori/duyurular/2  
   Personel alımı, sınav ve sonuç duyuruları var; sınav tarihleri yayın tarihi sayılmaz.
3. TPAO Personel Alımı Duyurusu — https://www.tpao.gov.tr/file/2512/sinav-ilani-33-personel-alimi-29-12-2025-37736953d2e834d07.pdf  
   Resmî personel alımı PDF örneği. PDF içindeki tarih ve KPSS şartı metinden ayrı çıkarılır; dosya adı yayın tarihi sayılmaz.

## 8. H1 kabul ölçütleri

- Şema alanları zorunlu/nullable ayrımıyla doğrulanır; geçersiz kategori ve içerik türü reddedilir.
- `publishedAt=null`, `deadline=null`, geçmiş deadline, sınav/kura başlık tarihi, boş yanıt ve bozuk tarih için test bulunur.
- Kura/sonuç/staj kayıtları yeni iş ilanı sayacına girmez.
- `firstSeenAt` aynı kaynak ilan kimliği için güncellemelerde değişmez.
- Kaynaklardan biri stale olduğunda yalnız onun kayıtları stale işaretlenir.
- Her kategori için en az 2–3 resmî aday listelenmiştir; ancak bu adayların ölçümü/otomasyon izni H1'in tamamlanmasıyla karıştırılmaz.
