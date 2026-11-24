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

## 7. K1-B ile ölçülmüş resmî duyuru / kariyer sayfaları

**Durum: PR AÇIK, ölçüm bekliyor.** Aşağıda K1-B'nin genişletilmiş 24.11.2026 ölçümünde `ÇALIŞIYOR` sınıfına giren 11 URL (PDF ekleri dâhil) aktif kaynak adayı olarak tutulur. Bu sınıf yalnız robots izni + TLS doğrulamalı HTTP 2xx + boş olmayan yanıt anlamına gelir; canlı ilan bulunduğunu, tarihlerin yayın tarihi olduğunu veya otomatik okuyucunun hazır olduğunu kanıtlamaz.

### Ölçüm kanıtı — 24.11.2026

- Actions: [K1-B ölçüm run #45405558813](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813) — job SUCCESS; özel testler **9/9 geçti**; tam tablo Actions özetinde, makine raporu artefaktı [14926070923](https://github.com/aries919-bot/Jarvis/actions/runs/45405558813/artifacts/14926070923).
- Commit: `b1e6c1b808ea0901595d80983791ce68cc848d75`; ölçüm zamanı: `2026-11-24T13:38:46Z`.
- Hedef sayısı yeniden sayıldı: **20 URL / 20 benzersiz URL**. H1'de e-Şube iki kategoride tekrarlandığı için tek URL sayıldı; Şişecam açık pozisyon bağlantısı ayrı URL; ayrıca `ilan.gov.tr/` ve Resmî Gazete kök adresi ek kontrol olarak ölçüldü.
- Sonuç: **8 ÇALIŞIYOR / 12 ENGELLİ / 0 ÖLÜ**. 20 ölçüm satırı logda tekrar sayıldı.
- TLS doğrulaması varsayılan Python SSL context ile açık kaldı. User-Agent: `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)`. Her host için robots.txt önce kontrol edildi; izin belirsiz/yasaksa hedef sayfaya istek yapılmadı. Sadece GET kullanıldı.
- “Tarihli duyuru: EVET” sinyali yalnız sayfa metninde tarih kalıbının yakınında duyuru/ilan/personel/kura vb. kelime bulunduğunu gösterir; `publishedAt` kanıtı veya güncel iş ilanı sayısı değildir. HAYIR da sitede hiç ilan bulunmadığını kanıtlamaz.

### Kamu

1. **İŞKUR e-Şube Açık İş İlanı Arama** — https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx  
   K1-B: robots izinli, HTTP 200, 79.115 bayt, tarihli duyuru EVET, ÇALIŞIYOR. K1-E1'de kamu filtresiyle 3 sayfa / 43 kayıt doğrulandı. e-Şube satırındaki tarih son başvurudur; `publishedAt=null`, `deadline=son başvuru`. Yalnız izinli, girişsiz ve CAPTCHA'sız yüzey kullanılabilir.

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

### Büyük özel şirket

1. **Turkcell Kariyer Fırsatları** — https://www.turkcell.com.tr/hakkimizda/insankaynaklari/kariyer-firsatlari/detay  
   K1-B: robots izinli, HTTP 200, 132.486 bayt, tarihli duyuru HAYIR, ÇALIŞIYOR. Sayfa erişilebilir; bu ölçüm açık pozisyon veya tarihli ilan bulunduğunu kanıtlamaz. İlan alt sayfaları ayrıca incelenmeli.
2. **Koç Kariyerim ilan listesi** — https://www.kockariyerim.com/jobAd/list  
   K1-B: robots izinli, HTTP 200, 707.465 bayt, tarihli duyuru HAYIR, ÇALIŞIYOR. Sayfa erişilebilir; pozisyonların içeriği, başvuru durumu ve son tarihleri ayrı doğrulanmadan iş ilanı sayılmaz.

### Kamu bağlı büyük şirket

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

### Büyük özel şirket
1. Turkcell Kariyer Fırsatları — https://www.turkcell.com.tr/hakkimizda/insankaynaklari/kariyer-firsatlari/detay — K1-B'de robots izinli, HTTP 200; tarihli ilan sinyali yok.
2. Koç Kariyerim ilan listesi — https://www.kockariyerim.com/jobAd/list — K1-B'de robots izinli, HTTP 200; tarihli ilan sinyali yok.
3. Alarko Kariyer Fırsatları — https://alarko.com.tr/kariyer-ve-yasam/kariyer-firsatlari/ — Resmî şirket kariyer sayfası adayı; K1-B'de ölçülmedi, robots/TLS ölçümü bekler.

### Kamu bağlı büyük şirket
1. BOTAŞ Personel Alımı — https://www.botas.gov.tr/Sayfa/personel-alimi/124 — Resmî personel alım süreci sayfası adayı; K1-B'de bu özel URL ölçülmedi. İŞKUR'a yönlendirdiği ilanlar kazınmaz; resmî İŞKUR sayfasına yönlendirme yapılır.
2. PTT Personel Alımı Hakkında Duyuru — https://www.ptt.gov.tr/duyurular/sirketimiz-personel-alimi-hakkinda-duyuru-9c2abed7a9a6486a97a474e9f73286c3 — Resmî duyuru örneği; ilan geçici ertelenmiştir, güncel açık iş olarak gösterilmez. Bu tam URL K1-B'de ölçülmedi; PTT duyuru ana sayfasında TLS issuer hatası görüldüğü için yeni istek öncesi ölçüm gerekir.
3. TPAO 33 personel alımı PDF — https://www.tpao.gov.tr/file/2512/sinav-ilani-33-personel-alimi-29-12-2025-37736953d2e834d07.pdf — K1-B'de timeout/robots belirsiz; PDF'ye erişim yapılmadı ve yeniden denenmeyecek.

## 8. H1 kabul ölçütleri

- Şema alanları zorunlu/nullable ayrımıyla doğrulanır; geçersiz kategori ve içerik türü reddedilir.
- `publishedAt=null`, `deadline=null`, geçmiş deadline, sınav/kura başlık tarihi, boş yanıt ve bozuk tarih için test bulunur.
- Kura/sonuç/staj kayıtları yeni iş ilanı sayacına girmez.
- `firstSeenAt` aynı kaynak ilan kimliği için güncellemelerde değişmez.
- Kaynaklardan biri stale olduğunda yalnız onun kayıtları stale işaretlenir.
- İlk taramada her kategori için 2–3 aday araştırıldı; K1-B sonrası aktif aday listesi yalnız ÇALIŞIYOR olan 8 URL ile sınırlıdır. Bazı kategorilerde tek veya iki kaynak kaldığından kaynak çeşitliliği hedefi henüz karşılanmış sayılmaz. Girişli sayfalar yalnız yönlendirmedir; tarihli duyuru/ilan içeriği ve otomasyon uygunluğu H2/K1-C'de ayrıca doğrulanır.


## 7B. 24.11.2026 genişletilmiş resmî kaynak ölçümü

- Actions: [K1-B run #45473805274](https://github.com/aries919-bot/Jarvis/actions/runs/45473805274), branch `is/K1-B-wide`, ölçülen commit `7fcee2257e4dfd92bde994e4b6437de1a9f796c4`.
- Tekrar sayım: **28 benzersiz URL; 11 ÇALIŞIYOR / 17 ENGELLİ / 0 ÖLÜ**. Testler **16/16 SUCCESS**. Robots unknown/disallow durumlarında hedef sayfaya istek gönderilmedi; TLS doğrulaması kapatılmadı.
- ÇALIŞIYOR URL'ler:
  1. e-Şube: robots 200 / HTTP 200 / 79.089 bayt / tarihli duyuru EVET.
  2. YHGM kura: robots 200 / HTTP 200 / 89.063 bayt / EVET.
  3. Turkcell kariyer: robots 200 / HTTP 200 / 132.490 bayt / tarihli duyuru HAYIR.
  4. Koç Kariyerim: robots 200 / HTTP 200 / 707.465 bayt / HAYIR.
  5. BOTAŞ duyurular: robots 200 / HTTP 200 / 55.753 bayt / EVET.
  6. Adli Tıp Kurumu 2026-2 duyurusu: robots 404 (robots dosyası yok) / HTTP 200 / 8.598 bayt / EVET.
  7. Adli Tıp Ek-1 PDF: robots 404 / HTTP 200 / 217.209 bayt / EVET.
  8. Anadolu Üniversitesi sözleşmeli personel duyurusu: robots 200 / HTTP 200 / 36.462 bayt / EVET.
  9. Düzce Üniversitesi öğretim elemanı PDF: robots 404 / HTTP 200 / 434.992 bayt / EVET.
  10. Tarsus Üniversitesi öğretim elemanı duyurusu: robots 200 / HTTP 200 / 26.000 bayt / EVET.
  11. Tarsus Üniversitesi resmî ilan PDF: robots 200 / HTTP 200 / 771.374 bayt / EVET.
- Aynı kurumun resmî HTML sayfası + bağlı PDF'si ayrı URL olabilir; kaynak sayımında kurum/işveren tek kaynak sayılır. Yeni aktif kurumlar: Adli Tıp Kurumu, Anadolu Üniversitesi, Düzce Üniversitesi, Tarsus Üniversitesi. PDF ekleri kaynak sayısını artırmaz.
- ENGELLİ örnekleri: Anadolu PDF doğrudan uç noktası ağ hatası; TÜRKAK sayfası HTTP 200 dönmesine rağmen erişim sınıflandırıcısında engelli (otomatik okuyucuya alınmadı); Şişecam 403; İlan.gov.tr TLS doğrulama hatası; Resmî Gazete robots timeout/önceden ölçülmüş kapalı; ÖSYM robots bilinmiyor; Kariyer Kapısı ve e-Devlet linkleri yalnız yönlendirme. Ayrıntı için Actions job özeti ve artifact.
- **Sınır:** ÇALIŞIYOR yalnız robots/TLS/HTTP erişimini gösterir; iş ilanı, başvuru şartı, açık deadline veya otomatik okuyucu kanıtı değildir. H2'ye yalnız ölçülmüş ve metni ayrıştırılabilen resmî adresler eklenir.
