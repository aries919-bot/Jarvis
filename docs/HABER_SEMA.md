# HABER KARTI ŞEMASI (H1)

Durum: PR AÇIK, ölçüm bekliyor. H1 bağımsız olarak kabul edilmiş değildir; resmî kaynak listesi K1-B ölçüm tablosundan sonra yalnız ÇALIŞIYOR adreslerle daraltılır.
Kapsam: İş & Kariyer haberleri. Kaynak adayı olmak, kazıma izni veya canlı ilan kanıtı değildir.

## 1. Filtreler ve kategori sınıflandırması

Arayüz filtreleri (sıra sabit): **Tümü**, **Kamu**, **Kura**, **KPSS**, **KPSS'siz**, **Büyük özel**, **Kamu bağlı şirket**.

Kartın `category` alanı Tümü olamaz; aşağıdaki değerlerden tam biri olmalıdır:
- `Kamu`: bakanlık, genel müdürlük, üniversite, belediye ve doğrudan kamu kurumu ilanı.
- `Kura`: noter kurası/çekiliş süreciyle ilgili duyuru. İlanın kendisi ayrıca Kamu veya ilgili işveren kategorisinde sınıflandırılabilir; kura haberi `contentType=lottery` olur.
- `KPSS`: ilanın açıkça KPSS puanı/puan türü şartı koyduğu alım.
- `KPSS'siz`: resmî ilan metninde KPSS şartı bulunmadığı doğrulanmış alım. Şartlar okunamıyorsa bu etiket verilmez.
- `Büyük özel`: özel hukuk tüzel kişisi olan büyük ölçekli şirketin kendi kariyer/duyuru kanalı.
- `Kamu bağlı şirket`: kamu sermayeli/bağlı büyük şirket (ör. PTT, BOTAŞ, TPAO). Şirket statüsü teyit edilmeden bu kategori kullanılmaz.

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
| `category` | enum | Kamu / Kura / KPSS / KPSS'siz / Büyük özel / Kamu bağlı şirket. |
| `contentType` | enum | `job`, `lottery`, `result`, `internship`, `exam`, `other`. |
| `officialSourceUrl` | HTTPS URL | İlan/duyurunun resmî asıl sayfası; arama sonucu veya haber kopyası olmaz. |
| `howToApply` | object | `method`, `url`, `instructions`. Başvuru yeri/yöntemi resmî metinden aktarılır. |
| `conditions` | string[] | Eğitim, bölüm, KPSS, yaş, deneyim, belge ve diğer şartlar; yalnız kaynakta yazanlar. |
| `applicationOpensAt` | ISO-8601 tarih/saat veya null (opsiyonel) | Kaynak açıkça duyurmuşsa başvurunun açılacağı an; deadline yerine geçmez. Yoksa null/alan yok. |
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
7. `Doğrulandı` için resmî ilan içeriği, son başvuru tarihi ve başvuru döneminin başlamış olması gerekir. Kaynak yayın tarihini açıkça veriyorsa `publishedAt` doldurulur; vermiyorsa `publishedAt=null` kalır, çekim zamanı veya ilk görülme tarihi yayın tarihi yerine kullanılamaz. Kaynak `applicationOpensAt` veriyorsa bu an gelmeden `Doğrulandı` denmez; `Doğrulanamadı` kalır. Saat dilimi Türkiye yerel saatiyle yorumlanır; saklama UTC'dir.
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

## 7. K1-B ile ölçülmüş resmî duyuru / kariyer sayfaları

**Durum: PR AÇIK, ölçüm bekliyor.** Aşağıda K1-B'nin genişletilmiş 24.11.2026 ölçümünde `ÇALIŞIYOR` sınıfına giren 11 URL (PDF ekleri dâhil) aktif kaynak adayı olarak tutulur. Bu sınıf yalnız robots izni + TLS doğrulamalı HTTP 2xx + boş olmayan yanıt anlamına gelir; canlı ilan bulunduğunu, tarihlerin yayın tarihi olduğunu veya otomatik okuyucunun hazır olduğunu kanıtlamaz.

### Ölçüm kanıtı — 24.11.2026

- Actions: [K1-B ölçüm run #45405558813](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813) — job SUCCESS; özel testler **9/9 geçti**; tam tablo Actions özetinde, makine raporu artefaktı [14926070923](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813/artifacts/14926070923).
- Commit: `b1e6c1b808ea0901595d80983791ce68cc848d75`; ölçüm zamanı: `2026-11-24T13:38:46Z`.
- Hedef sayısı yeniden sayıldı: **20 URL / 20 benzersiz URL**. H1'de e-Şube iki kategoride tekrarlandığı için tek URL sayıldı; Şişecam açık pozisyon bağlantısı ayrı URL; ayrıca `ilan.gov.tr/` ve Resmî Gazete kök adresi ek kontrol olarak ölçüldü.
- İlk dar ölçüm sonucu (önceki koşu): **8 ÇALIŞIYOR / 12 ENGELLİ / 0 ÖLÜ**. Bu sayı 28 URL'lik geniş ölçümle güncellendi; son ölçüm aşağıdaki 7B bölümündedir.
- TLS doğrulaması varsayılan Python SSL context ile açık kaldı. User-Agent: `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)`. Her host için robots.txt önce kontrol edildi; izin belirsiz/yasaksa hedef sayfaya istek yapılmadı. Sadece GET kullanıldı.
- “Tarihli duyuru: EVET” sinyali yalnız sayfa metninde tarih kalıbının yakınında duyuru/ilan/personel/kura vb. kelime bulunduğunu gösterir; `publishedAt` kanıtı veya güncel iş ilanı sayısı değildir. HAYIR da sitede hiç ilan bulunmadığını kanıtlamaz.

### Kamu

1. **İŞKUR e-Şube Açık İş İlanı Arama** — https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx  
   K1-B: robots izinli, HTTP 200, 79.115 bayt, tarihli duyuru EVET, ÇALIŞIYOR. K1-E1'de kamu filtresiyle 3 sayfa / 43 kayıt doğrulandı. e-Şube satırındaki tarih son başvurudur; `publishedAt=null`, `deadline=son başvuru`. Yalnız izinli, girişsiz ve CAPTCHA'sız yüzey kullanılabilir.

2. **Adli Tıp Kurumu 2026-2 personel alımı** — https://atk.gov.tr/haber20.11.26.html  
   K1-B geniş ölçüm: robots.txt HTTP 404 (dosya yok), HTTPS HTTP 200, 8.598 bayt. 20.11.2026 yayın tarihi; 181 pozisyon; başvuru 30.11.2026 09:00–06.12.2026 23:59. Ek-1 PDF aynı kurum hostunda ayrıca ölçüldü. P3/P93/P94 ilgili puan türü en az 70.

3. **Anadolu Üniversitesi sözleşmeli personel duyurusu** — https://anadolu.edu.tr/duyurular/sozlesmeli-personel-alimi-ilani-1794568099  
   K1-B: robots HTTP 200 / sayfa HTTP 200 / 36.462 bayt. Eklenme 13.11.2026; başvuru 16.11–30.11.2026. Tek sözleşmeli personel, 657 4/B ve KPSS(B) puan sıralaması. Resmî PDF uç noktası K1-B'de ağ hatası verdi; otomatik okuyucu PDF metnini almaz, duyuru sayfasındaki genel bilgiyi gösterir.

4. **Düzce Üniversitesi öğretim elemanı ilanı** — https://cdn.duzce.edu.tr/File/GetFile/4455f177-5bf2-414d-a4c6-9de77131bf51  
   K1-B: robots.txt HTTP 404 / PDF HTTP 200 / 434.992 bayt. Başlık açıkça 23.11.2026 tarihli; 5 akademik kadro; başvuru 23.11–07.12.2026. Lisans, ALES ve bazı kadrolarda yabancı dil/tezli yüksek lisans şartı.

5. **Tarsus Üniversitesi öğretim elemanı duyurusu** — https://personel.tarsus.edu.tr/tr/news-detail/ogretim-elemani-ilani/17895  
   K1-B: robots HTTP 200 / sayfa HTTP 200 / 26.000 bayt; bağlı PDF HTTP 200 / 771.374 bayt. Resmî Gazete tarihi 23.11.2026; 3 öğretim görevlisi kadrosu; son başvuru 07.12.2026 mesai bitimi. ALES 70; İngilizce kadrosunda yabancı dil 85, Kaynak Teknolojisi kadrolarında muafiyet.

### Kura

1. **Sağlık Bakanlığı YHGM kura duyurusu** — https://yhgm.saglik.gov.tr/TR-111525/tasra-teskilati-hizmet-birimlerinde-istihdam-edilmek-uzere-2764-surekli-isci-alim-kurasi-hakkinda-duyuru.html  
   K1-B: robots izinli, HTTP 200, 89.063 bayt, tarihli duyuru EVET, ÇALIŞIYOR. Kura/sonuç duyurusu `contentType=lottery/result` olarak sınıflandırılır; yeni iş ilanı sayacına eklenmez.

### KPSS

1. **Kariyer Kapısı Kamu İşe Alım** — https://kariyerkapisi.gov.tr/isealim  
   K1-B: robots.txt HTTP 404 (kurala göre izinli), HTTP 200, 18.791 bayt, tarihli duyuru HAYIR, ÇALIŞIYOR. Bu yüzeyde tarihli duyuru kanıtı bulunmadı; giriş/JS ile açılan kişisel ilan listeleri kazınmaz. Şimdilik yalnız kullanıcıya resmî site yönlendirmesi; ilan okuyucu kaynağı sayılmaz.

### KPSS'siz

1. **Malatya Su ve Kanalizasyon İdaresi Kariyer Kapısı yönlendirmesi** — https://www.turkiye.gov.tr/msvk-kariyer-kapisi-kamu-ise-alim  
   K1-B: robots izinli, HTTP 200, 16.283 bayt, tarihli duyuru HAYIR, ÇALIŞIYOR. e-Devlet oturumuna bağlıdır; yalnız dış bağlantı/Custom Tabs yönlendirmesi, kazıma yok.
2. **İzmir Büyükşehir Belediyesi Kariyer Kapısı yönlendirmesi** — https://www.turkiye.gov.tr/bb-kariyer-kapisi-kamu-ise-alim  
   K1-B: robots izinli, HTTP 200, 15.953 bayt, tarihli duyuru HAYIR, ÇALIŞIYOR. e-Devlet oturumuna bağlıdır; yalnız dış bağlantı/Custom Tabs yönlendirmesi, kazıma yok. KPSS koşulu her ilanda ayrıca resmî metinden doğrulanır.

### Büyük özel

1. **Turkcell Kariyer Fırsatları** — https://www.turkcell.com.tr/hakkimizda/insankaynaklari/kariyer-firsatlari/detay  
   K1-B: robots izinli, HTTP 200, 132.486 bayt, tarihli duyuru HAYIR, ÇALIŞIYOR. Sayfa erişilebilir; bu ölçüm açık pozisyon veya tarihli ilan bulunduğunu kanıtlamaz. İlan alt sayfaları ayrıca incelenmeli.
2. **Koç Kariyerim ilan listesi** — https://www.kockariyerim.com/jobAd/list  
   K1-B: robots izinli, HTTP 200, 707.465 bayt, tarihli duyuru HAYIR, ÇALIŞIYOR. Sayfa erişilebilir; pozisyonların içeriği, başvuru durumu ve son tarihleri ayrı doğrulanmadan iş ilanı sayılmaz.

### Kamu bağlı şirket

1. **BOTAŞ duyurular** — https://www.botas.gov.tr/Kategori/duyurular/2  
   K1-B: robots izinli, HTTP 200, 55.753 bayt, tarihli duyuru EVET, ÇALIŞIYOR. Personel alımı/sınav/sonuç içerikleri ayrı türlere ayrılır; sınav tarihi yayın tarihi olarak alınmaz.

### Aktif kaynak listesinden çıkarılan adaylar

K1-B'de **ENGELLİ** çıkan 12 URL aktif kaynak listesine alınmadı: İlan.gov.tr (www ve apex TLS doğrulama hatası), Resmî Gazete `/ilanlar` ve kök sayfa (timeout), iki TÜSEB adresi (TLS doğrulama hatası), iki ÖSYM adresi (robots sonucu belirsiz olduğu için hedef sayfa isteği yapılmadı), Şişecam iki adresi (HTTP 403), PTT (TLS doğrulama hatası), TPAO PDF (timeout). Bunlar `ÖLÜ` ilan edilmedi; bu ölçümde **0 ÖLÜ** sonucu var. Sonradan tekrar ölçüm ayrı PR/Actions koşusudur; TLS doğrulaması kapatılmaz ve robots belirsizliği izin sayılmaz.

## 7A. Kategori bazlı resmî kaynak adayları

Aşağıdaki listeler H1'in her kategori için 2–3 resmî aday şartını karşılayan aday kayıtlarıdır. Bu liste, otomatik kazıma izni veya gerçek ilan kanıtı değildir. K1-B'de ölçülmeyen yeni adresler robots/TLS/HTTP ölçümü tamamlanana kadar otomasyona alınmaz. Girişli sayfalar yalnız Custom Tabs yönlendirmesidir. Her ilanın KPSS/KPSS'siz niteliği yalnız o ilanın resmî şartlarından belirlenir; kaynak adı veya geçmiş ilanlardan varsayılmaz.

### Kamu
1. İŞKUR e-Şube Açık İş İlanı Arama — https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx — K1-B/K1-E1 ölçüldü: robots izinli, 200; 43 kamu kaydı. publishedAt=null, satır tarihi deadline.
2. İlan.gov.tr — https://www.ilan.gov.tr/ — K1-B ölçüldü: TLS CERTIFICATE_VERIFY_FAILED / unable to get local issuer certificate; engelli, tekrar denenmeyecek.
3. Resmî Gazete İlanlar — https://www.resmigazete.gov.tr/ilanlar — K1-B ölçüldü: robots izni olan hedefte timeout; tekrarlı workflow istekleri ayrıca K1-B denetim notuna işlendi.

### Kura
1. Sağlık Bakanlığı YHGM — 2.764 sürekli işçi kura duyurusu — https://yhgm.saglik.gov.tr/TR-111525/tasra-teskilati-hizmet-birimlerinde-istihdam-edilmek-uzere-2764-surekli-isci-alim-kurasi-hakkinda-duyuru.html — K1-B ölçüldü: robots izinli, 200. Kura/sonuç kaydıdır; yeni iş ilanı sayılmaz.
2. TÜSEB — TÜSEB-2026-4 noter kura çekilişi duyurusu — https://www.tuseb.gov.tr/public/kurumsal/insan-kaynaklari/personel-alim-ilani-tuseb-2026-4-hizmetli-personel-alimi-noter-kura-cekilisi-ilani-20260918 — Resmî duyuru adayı; doğrudan bu URL K1-B'de ölçülmedi. Aynı kurumun iki genel sayfasında TLS issuer doğrulama hatası görüldüğünden robots/TLS ölçülmeden otomasyon yok. 2026 başvuru ve kura tarihleri geçmiş arşiv kaydıdır.
3. MSB Personel Temin — sürekli işçi kura sonuç duyurusu — https://personeltemin.msb.gov.tr/AnaSayfa/DuyuruDetay/?id=cc3204cc-f222-4895-ab78-1aca4fd65120 — Resmî kura sonucu örneği; doğrudan URL K1-B'de ölçülmedi. Sadece aday kaydı, robots/TLS ölçümü bekler; sonuç haberi yeni ilan sayılmaz.

### KPSS
1. ÖSYM KPSS duyuru arşivi — https://www.osym.gov.tr/SinavGrubu/Index/3 — Resmî KPSS haber/duyuru sayfası; bu tam URL K1-B'de ölçülmedi, robots/TLS ölçümü bekler.
2. ÖSYM Sınav Takvimi — https://osym.gov.tr/Sayfa/SinavTakvimi — Resmî takvim; KPSS-2026/2 tercih dönemini 17–24.12.2026 olarak listeliyor. Takvimdeki sınav/tercih tarihleri publishedAt değildir.
3. ÖSYM KPSS-2026/1 tercih duyurusu — https://www.osym.gov.tr/kpss20261-bazi-kamu-kurum-ve-kuruluslarinin-kadro-ve-pozisyonlarina-yerlestirme-yapmak-icin-adaylardan-tercih-alinmasi — Resmî tarihli duyuru örneği; 2026/1 dönemi kapanmıştır, güncel ilan gibi gösterilmez.

### KPSS'siz
1. KariyerİBB — İstanbul Büyükşehir Belediyesi ve iştirakleri işe alım portalı — https://kariyer.ibb.istanbul/ — Resmî belediye portalı adayı; bu tam URL K1-B'de ölçülmedi. İlan şartı açıkça KPSS istemiyorsa ancak o ilan KPSS'siz olur.
2. Malatya Su ve Kanalizasyon İdaresi Kariyer Kapısı — https://www.turkiye.gov.tr/msvk-kariyer-kapisi-kamu-ise-alim — K1-B'de HTTP 200, fakat kimlik doğrulama gerektiren e-Devlet yönlendirmesi; içerik kazınmaz, Custom Tabs.
3. İzmir Büyükşehir Belediyesi Kariyer Kapısı — https://www.turkiye.gov.tr/bb-kariyer-kapisi-kamu-ise-alim — K1-B'de HTTP 200, fakat kimlik doğrulama gerektiren e-Devlet yönlendirmesi; içerik kazınmaz, Custom Tabs.

### Büyük özel
1. Turkcell Kariyer Fırsatları — https://www.turkcell.com.tr/hakkimizda/insankaynaklari/kariyer-firsatlari/detay — K1-B'de robots izinli, HTTP 200; tarihli ilan sinyali yok.
2. Koç Kariyerim ilan listesi — https://www.kockariyerim.com/jobAd/list — K1-B'de robots izinli, HTTP 200; tarihli ilan sinyali yok.
3. Alarko Kariyer Fırsatları — https://alarko.com.tr/kariyer-ve-yasam/kariyer-firsatlari/ — Resmî şirket kariyer sayfası adayı; K1-B'de ölçülmedi, robots/TLS ölçümü bekler.

### Kamu bağlı şirket
1. BOTAŞ Personel Alımı — https://www.botas.gov.tr/Sayfa/personel-alimi/124 — Resmî personel alım süreci sayfası adayı; K1-B'de bu özel URL ölçülmedi. İŞKUR'a yönlendirdiği ilanlar kazınmaz; resmî İŞKUR sayfasına yönlendirme yapılır.
2. PTT Personel Alımı Hakkında Duyuru — https://www.ptt.gov.tr/duyurular/sirketimiz-personel-alimi-hakkinda-duyuru-9c2abed7a9a6486a97a474e9f73286c3 — Resmî duyuru örneği; ilan geçici ertelenmiştir, güncel açık iş olarak gösterilmez. Bu tam URL K1-B'de ölçülmedi; PTT duyuru ana sayfasında TLS issuer hatası görüldüğü için yeni istek öncesi ölçüm gerekir.
3. TPAO 33 personel alımı PDF — https://www.tpao.gov.tr/file/2512/sinav-ilani-33-personel-alimi-29-12-2025-37736953d2e834d07.pdf — K1-B'de timeout/robots belirsiz; PDF'ye erişim yapılmadı ve yeniden denenmeyecek.

## 8. H1 kabul ölçütleri

- Şema alanları zorunlu/nullable ayrımıyla doğrulanır; geçersiz kategori ve içerik türü reddedilir.
- \`publishedAt=null\`, \`deadline=null\`, geçmiş deadline, başvuru başlangıç saati, boş liste ve bozuk tarih için test bulunur.
- Kura/sonuç/staj kayıtları yeni iş ilanı sayacına girmez.
- \`firstSeenAt\` aynı kaynak ilan kimliği için güncellemelerde değişmez.
- Kaynaklardan biri stale olduğunda yalnız onun kayıtları stale işaretlenir.
- Uygulama doğrulayıcısı: \`scripts/h1_schema.py\`; test: \`scripts/test_h1_schema.py\`; Actions: \`.github/workflows/h1-schema.yml\`.
- H1 doğrulayıcısı yalnız JSON sözleşmesini/yardımcı veri işlemlerini sınar; canlı okuyucu veya Android/Worker entegrasyonu değildir.
- İlk taramada her kategori için 2–3 aday araştırıldı; geniş K1-B ölçümünde aktif URL listesi 11 ÇALIŞIYOR adresle güncellendi. Bazı kategorilerde tek veya iki kaynak kaldığından kaynak çeşitliliği hedefi henüz karşılanmış sayılmaz. Girişli sayfalar yalnız yönlendirmedir; tarihli duyuru/ilan içeriği ve otomasyon uygunluğu H2/K1-C'de ayrıca doğrulanır.



## 9. İddia / kanıt / insan incelemesi / düzeltme geçmişi

H1 Claim Evidence core: PR #69 (açık/taslak; exact-head CI bekliyor). PR #68 eski CI hedefi nedeniyle superseded olarak tutulur. Kod: `CareerNewsVerification.kt`. Ayrıntı: [H1-claim-evidence-2026-11-25.md](../gorevler/H1-claim-evidence-2026-11-25.md).

### Haber kaydı
- `storyId`: kanonik kaynak URL'si + başlık üzerinden kararlı kimlik.
- `headline`, `publisher`, `originalSourceUrl`.
- `publishedAt`: kaynak açıkça vermediyse null; hiçbir durumda `collectedAt` veya `firstSeenAt` ile doldurulmaz.
- `collectedAt`: Aries'in kaynağı kontrol ettiği zaman.
- `firstSeenAt`: kaydın sistemde ilk görüldüğü kalıcı zaman.

### İddia ve kanıt
- Bir haber birden çok atomik/test edilebilir claim içerebilir; her claim ayrı değerlendirilir.
- Kanıt: yayıncı, HTTPS kaynak URL'si, kaynak türü (`PRIMARY_SOURCE`, `INDEPENDENT_REPORTING`, `SECONDARY`, `UNKNOWN`), yön (`SUPPORTS`, `CONTRADICTS`, `CONTEXT_ONLY`), doğrudan alıntı, nullable yayın zamanı ve kontrol zamanı.
- Arama snippet'i veya AI özeti tek başına birincil kaynak kanıtı değildir.
- URL'ler bu çekirdekte otomatik açılmaz/kazınmaz; kanıt bağlantısı olarak saklanır.

### Verdict güvenlik kapısı
- `VERIFIED`: birincil kaynak destek kanıtı + açık insan `CONFIRM` + çelişen kanıt olmaması.
- `REFUTED`: birincil kaynak çelişki kanıtı + açık insan `REFUTE` + destekleyici kanıt olmaması.
- Hem destekleyen hem çelişen kanıt varsa `CONFLICTING_EVIDENCE`; insan kararı bu çatışmayı otomatik silemez.
- Yalnız ikincil/bağlam kanıtı `CONTEXT_MISSING`; kanıt yoksa `NEEDS_EVIDENCE`.
- Yeni kanıt eski insan incelemesini sıfırlar. Claim düzeltilince önceki kanıt/review temizlenir ve yeniden inceleme gerekir.

### Düzeltme kaydı
Düzeltmeler append-only tutulur: claimId, önceki verdict, yeni verdict, açıklama, kaynak URL'si ve UTC correctedAt. Eski kaydı ezmek veya düzeltme geçmişini silmek yoktur.

### Kişisel veri / tarih
- Known email, phone, IBAN ve 11 haneli kimlik desenleri içeren alıntı reddedilir; maskeleme eksiksiz PII garantisi değildir. Aday kura/sonuç listelerinde kişisel veri toplanmaz.
- UTC tarih alanları biçim ve takvim aralığı açısından kontrol edilir. Yayın tarihi yoksa null kalır.

### Entegrasyon sınırı
PR #69 yalnız veri modeli + birim testtir; Haber ekranı, yerel kalıcı kayıt ve kullanıcı akışı henüz bağlanmadı. Mevcut arşivdeki eski haberler otomatik doğrulanmışa çevrilmez. UI/SharedPreferences entegrasyonu ayrı PR ve geriye dönük koruma testiyle yapılır.

## 10. H1 çekirdeği CI tabanı ve correction snapshot güncellemesi — 25.11.2026
- İlk PR #68 is/c3-ilan-semasi-ai-kaynak-kaniti tabanındaydı; Android workflow'un pull_request.branches filtresi bu tabanı dinlemedi. #68 kapatıldı (unmerged).
- Doğru CI tabanında PR #69 açıldı: https://github.com/aries919-bot/Jarvis/pull/69, hedef kariyer-modulu, HEAD fdfdd539a2e81826e53898aa07004ee21a0bd55b.
- CorrectionHistory sadece önceki verdict değil; önceki iddia metni, düzeltilmiş metin, eski kanıtların tümü, önceki review kararı/zamanı, açıklama ve kaynak linkini korur. Aktif claim'in kanıt ve review alanları sıfırlanır; yeni kanıt/insan incelemesi gerekir.
- Yeni exact-head testler 11 @Test'tir. İlk compile hatası Locale string escape'inden geldi; gerçek Kotlin literal'i düzeltildi. Yeni exact-head CI #45657661662 (#685) IN_PROGRESS; henüz PASS yok.
- PR #69 model/şema + testtir; Haber ekranı ve yerel kalıcı arşivle entegrasyonu değildir. Eski Haber kayıtları otomatik doğrulanmışa çevrilmez.
- Ayrıntı: [H1-claim-evidence-2026-11-25.md](../gorevler/H1-claim-evidence-2026-11-25.md).