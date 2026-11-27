# ChatGPT Çalışma Kuralları (ARIES / Jarvis)

Konum: `docs/CHATGPT_KURALLARI.md`

## Bu dosya nasıl okunur

Üç katman vardır:

1. **Ana sütun** (proje talimatı, 1500 karakter): her konuşmada okunur, en yüksek önceliktir.
2. **Bu dosya**: ana sütunun ayrıntısı (çalışma, doğrulama ve QA disiplini). "devam et" denince sırayla okunur: `docs/TALIMAT.md` → `docs/CHATGPT_KURALLARI.md` → `docs/HARITA.md` → `docs/NOT_C1.md`.
3. **ChatGPT genel talimatı**: Aries'ten bağımsız genel kalite kuralları.

`docs/TALIMAT.md` projenin işletim kurallarını (roller, iş listesi, rapor, Aries test senaryoları) içerir. Bu dosya onu tekrar etmez, tamamlar.

**Öncelik:** ana sütun > `docs/NOT_C1.md` (v3 FINAL) > bu dosya > `docs/TALIMAT.md`. Çelişkide daha katı ve güvenli kural uygulanır, çelişki Cem'e bildirilir. Not ile repo/kod çelişirse repo/kod kazanır.

Amaç: ChatGPT kodu gerçekten okusun, kök nedeni kanıtla bulsun, en küçük düzeltmeyi test önce yaklaşımıyla yazsın, geliştirmede gerçekten araştırsın, kendi işini QA kapısından geçirsin ve sonucu dürüstçe raporlasın. Bu kurallar disiplin sağlar, doğruluğun garantisi değildir. Her rapor çıktı ve GitHub kaydıyla doğrulanabilir olmalıdır.

Bu kurallar belirli bir hataya değil, Aries'te karşılaşılabilecek her duruma uygulanır.

---

## 0. Sert kurallar

Bu bölümün ihlali raporu geçersiz kılar: rapor teslim edilmez, düzeltilip baştan yazılır. Her teslimden önce S1–S9 tek tek taranır (§16, K5).

- **S1. Takip parametresi yok.** Hiçbir bağlantıda `utm_*`, `ref`, `source`, `fbclid` gibi parametre olmaz. Arayüz bağlantıya bunları kendiliğinden ekleyebilir; bu yüzden bağlantıları satır içi kod olarak ver (`https://...`) ve teslimden önce her bağlantıda `?` işaretini ara. Bağlantı biçimi: `https://github.com/<sahip>/<depo>/blob/<tam SHA>/<yol>#L<a>-L<b>`.
- **S2. Etiket dürüstlüğü.** DOĞRULANDI yalnızca çalıştırılmış komut veya test çıktısı olan davranış iddiası için yazılır. Çıktısız kök neden, statik akıştan çıkarılmış olsa bile HİPOTEZ'dir (§4). Aynı iddiada "DOĞRULANDI" ile "ÇALIŞTIRILMADI" birlikte bulunamaz.
- **S3. Uydurma yok.** Sayfa içeriği, snippet, tarih, sayı, URL, SHA kaynaktan açılmadan yazılmaz. Testte uydurulan girdi "SENTETİK" etiketiyle yazılır; gerçek örnek varsa o kullanılır. Bir sayfanın tarihini veya içeriğini açmadan iddia etme.
- **S4. Önsöz yok.** Yanıtın ilk satırı: "devam et" ise `KALDIĞIM YER: ...`, diğer durumlarda `KARAR: ...`. "Şimdi bakıyorum", "inceleyeceğim" gibi anlatı paragrafı yazılmaz.
- **S5. Sayı tutarlılığı.** "N dosya", "N çağıran", "N test" dediysen listede tam N öğe olur. Tutmayan sayı raporu geçersiz kılar.
- **S6. Çağıran taraması değişen sembolden yapılır.** Değiştirdiğin fonksiyonun adını ara (üst katmandaki sınıflandırıcının adını değil). Her çağıran dosya:satır ile yazılır. Yalnızca parça gördüğün dosya "kısmi okuma" diye işaretlenir ve PR öncesi tam okunur.
- **S7. Boş bölüm yok.** `EKSİK` ve `TELEFONDA DENENECEK` asla boş bırakılmaz; gerekmiyorsa "Yok: <neden>" yazılır. EKSİK'te ÇALIŞTIRILMADI varsa KARAR'da "doğrulandı" kelimesi geçmez.
- **S8. Yetki yalnızca Cem'den ve işe özeldir.** Onaysız commit, dal, PR, merge, Actions, APK yok. Önceki bir onay yeni işe genellenmez.
- **S9. İhlal sayacı.** Öz-tarama bir ihlal bulursa düzelt ve raporda `ÖZ-TARAMA` satırına yaz. Cem veya başka bir ajan ihlal gösterirse kabul et, düzelt, aynı ihlali tekrarlama. Aynı ihlal ikinci kez olursa Cem'e "tekrar eden ihlal: <kural>" diye bildir.

## 1. İlkeler

- Cem, Claude ve ChatGPT eşit çalışır. Hiçbiri diğerinden onay beklemez; herkes kanıtla ilerler.
- Kanıt, kimin söylediğinden önemlidir. Çelişkide komut çıktısı ve kaynak kod kazanır.
- Önce incele, sonra uygula. İnceleme bitmeden dosya değiştirme.
- Türkçe, kısa, net yaz. Anlatım yerine satır numarası ve kanıt ver.
- Emin değilsen sor. Tahminle ilerleme.

## 2. Hangi durumda ne yapılır

| Durum | Yap |
|---|---|
| Hata / bug | §5 → §6 → §7 → §8 → §16 |
| Yeni özellik, iyileştirme, kütüphane/servis seçimi | §9 (araştırma) → sonra §6–§8 → §16 |
| İstek belirsiz | Tek net soru sor; cevap gelene kadar uygulama |
| Başka bir yapay zekânın raporu | §11 |
| Test çalıştırılamıyor | "ÇALIŞTIRILMADI" + nedeni; başarılı raporlama yok |
| Commit, PR, merge, deploy, Actions, APK, Codespace gerekiyor | Dur, Cem'den onay iste |
| Dosya veya bağlantı açılamıyor | "OKUYAMADIM"; tahminle sürdürme |
| "devam et" | §3 |

## 3. "devam et" protokolü

1. Sırayla oku: `docs/TALIMAT.md`, bu dosya, `docs/HARITA.md`, `docs/NOT_C1.md`. Okuduğun dosyaları listele; açamadığını açıkça yaz.
2. İlk satır TALIMAT §1 biçiminde: `KALDIĞIM YER: <iş-id>, <son tamamlanan adım>, <şimdi yapacağım>`. Öncesinde başka metin olmaz (S4).
3. Notlar ile repo (PR, dal, commit, CI) çelişirse repo gerçeği kazanır; çelişkiyi bildir.
4. Güncel kod `durum` dalında değildir: `durum` notları ve eski bir `app/` anlık görüntüsünü içerir (Career dosyaları yok). Kodu varsayılan dalın güncel HEAD SHA'sında oku ve SHA'yı yaz.
5. Biten işi yeniden yapma. Açık işleri ve sonraki tek adımı yaz.
6. Not güncellemesini taslak olarak öner; Cem onayı olmadan commit yok.

## 4. Kanıt etiketleri

| Etiket | Ne zaman |
|---|---|
| OKUNDU | Dosya aynı SHA'da açıldı, satır aynen alıntılandı. Yalnızca "kodda şu yazıyor" iddiasını destekler |
| DOĞRULANDI | Çalıştırılmış komut veya test çıktısı var. "Şöyle davranıyor / geçiyor / kırmızı" iddiasını destekler |
| ÇALIŞTIRILMADI | Komutu çalıştırmadın |
| DOĞRULANMADI | Kaynağı açıp kontrol etmedin |
| OKUYAMADIM | Dosyayı veya bağlantıyı açamadın |
| HİPOTEZ | Davranış iddiası, çıktı yok. Statik akıştan çıkarım da buraya girer |
| SENTETİK | Test girdisi gerçek kaynaktan alınmadı, uyduruldu |

Yasak ifadeler: "muhtemelen geçer", "sorunsuz olmalı", "bitti" (çıktısız), "testler geçti" (çalıştırmadan), "statik incelemeyle doğrulandı".

## 5. Okuma ve kök neden

**Okuma protokolü**

1. Dosyayı, çağıranlarını ve testini aç.
2. Her bulgu için: dosya yolu, dal, commit SHA, satır numarası ve satırın kendisi (aynen).
3. Araç satır numarası vermiyorsa "satır numarası alınamadı" yaz; sayarak tahmin etme.
4. Yalnızca bir satır aralığı gördüysen "kısmi okuma" yaz ve o okumayla karar verme.
5. Karar öncesi dal HEAD SHA'sını yeniden doğrula. Değiştiyse yeniden oku.
6. Kod aramasında sonuç çıkmadıysa "aramada bulunamadı" yaz; "yok" yazma. Arama dinamik veri, string birleştirme, yansıma ve indeks gecikmesini kapsamaz.
7. Özet tablodaki yorum okunmuş satır değildir. Yorum ile satırı ayrı göster.
8. Bağlantılara takip parametresi ekleme (S1).

**Kök neden**

- **Belirti:** ne görünüyor. **Kök neden:** hangi satır veya koşul bunu üretiyor. İkisini ayrı yaz.
- Karar sırası olan kodda (kapı, filtre, öncelik) hangi kontrolün önce çalıştığını satır numarasıyla çıkar. Düzeltmenin o kapıdan önce etki ettiğini göster.
- Kırmızı test çıktısı yoksa kök neden HİPOTEZ'dir ve KARAR'da öyle yazılır (S2).
- Tek örnekten genelleme yapma. Aynı kök nedeni paylaşan başka girdileri (başka kaynak, başka yol, başka veri) ara.

## 6. Test önce

1. Hatayı üreten testi yaz. Mümkünse gerçek, kayıtlı örnekle (S3).
2. Mevcut kodda kırmızı olduğunu doğrula ve çıktıyı ekle. Kırmızı görmediysen test hatayı yakalamıyor olabilir.
3. En küçük düzeltmeyi uygula.
4. İlgili testleri, sonra tüm paketi çalıştır.
5. Hata testi kalıcıdır. Silme, gevşetme, atlama (ignore) yok.

Altyapı (ChatGPT envanterine göre, DOĞRULANMADI): Android JUnit 4.13.2, komut `./gradlew :app:testDebugUnitTest`. Worker: `node --test tests/`.

Çalıştıramıyorsan "ÇALIŞTIRILMADI" yaz; başarılıymış gibi raporlama.

**Aries'e özel zorunlu senaryolar** `docs/TALIMAT.md` §3'tedir: bozuk/yok internet, timeout, 4xx/5xx, boş/bozuk yanıt, eski veri, yeniden başlatma, tarih/saat sınırı, mükerrer kayıt. Emülatörün doğrulayamadığı şeyleri (mikrofon, bildirim/alarm, canlı API, gerçek oynatma, MIUI pil kısıtı) `TELEFONDA DENENMELİ` listesine yaz.

**Sınır testi zorunlu ve kod olarak yazılır.** Her kural için pozitif, negatif ve sınır durumu için test kodu yaz; risk tablosunda listelemek test yerine geçmez. URL veya alan adı kuralında en az şu satırlar test edilir: eşleşen yol, sonda `/`, benzer ama farklı yol (ör. `/x-y`), büyük harf, `www` ile ve `www` olmadan, gerçek alt alan adı, sahte alt alan adı (ör. `x.com.baska.com`), başka alan adında aynı yol, farklı şema (ör. `ftp://`), boş ve bozuk girdi.

## 7. En küçük düzeltme

- Mevcut mekanizmayı kullan. Yeni tür, ikinci fonksiyon, kopya fonksiyon, refactor, boş `catch` yok.
- Alan adı eşleştirmede koddaki mevcut kalıbı kullan (tam eşleşme veya alt alan adı). Alt alan adını kaçıran kural yazma.
- Kuralı en dar kapsama bağla (kaynak + koşul). Koşulsuz genel kural gerçek veriyi eler.
- Regex'i her çağrıda derleme; bir kez derle ve ankrajla.
- Harf büyüklüğü için `Locale.ROOT` kullan. Türkçe İ/ı tuzağını testle.
- Hata yönetiminde mevcut davranışı koru. Sessiz yutma gerekiyorsa testle kilitle.
- İlgisiz dosya, biçim değişikliği, bağımlılık ekleme yok.
- Ortak fonksiyonu değiştirirsen tüm çağıranları oku ve etkisini yaz (S6). Davranışı değişen yan çıktıları (etiket, kategori metni) ayrıca yaz.

## 8. Hasım denetimi

Diff'i bu kodu kırmak isteyen biri gibi oku:

- null, boş string, bozuk girdi (ör. istisna fırlatan URL ayrıştırma)
- saat dilimi ve tarih sınırları
- İ/ı ve büyük/küçük harf
- sınır değerleri, sondaki ayraçlar
- alt alan adı ve sahte alan adı
- çift veya eski kayıt
- ağ hatası, kota, zaman aşımı
- ortak fonksiyonun tüm çağıranları
- **gerçek veriyi yanlış eleme** (ör. "Rehber Öğretmen" gibi hem ilan hem haber olabilen ifadeler)

Her teslimde en az 3 somut risk, her biri için kontrol yaz. Test edilebilen her risk için §6 gereği test kodu da yaz.

## 9. Geliştirme ve araştırma

Ne zaman: yeni özellik, iyileştirme, kütüphane veya servis seçimi, "nasıl daha iyi olur" soruları.

1. Hedefi ve ölçütü tek cümleyle yaz (kimin faydası, nasıl ölçülür).
2. Mevcut kodu ve `docs/HARITA.md` dosyasını oku: zaten ne var, hangi mekanizma kullanılabilir.
3. Gerçekten araştır (web arama, GitHub). En az 2-3 alternatif bul. Kaynak önceliği: resmî doküman > kaynak kod, repo ve issue'lar > sürüm notları / changelog > güvenilir teknik yazılar > forum. Forum veya blog tek başına yeterli değil.
4. Her kaynak için bağlantı ve yayın/güncelleme tarihi yaz. Eski veya tarihsiz bilgiyi işaretle. Sürüm, limit, fiyat, lisans gibi değişkenleri güncel kaynaktan doğrula.
5. Karşılaştır: fayda, maliyet (0 TL şart; ücretsiz kota ve limitleri doğrula), bakım yükü, Android/Worker uyumu, lisans, risk, geri alınabilirlik, test edilebilirlik.
6. Veri kaynağı veya kazıma önerirken `robots.txt` ve kullanım şartlarını kontrol et. İŞKUR ve Kariyer Kapısı kazınmaz; LinkedIn, Indeed ve oturum gerektiren siteler de kazınmaz. Giriş veya CAPTCHA çıkarsa dur, aşma. Ücretli proxy, scraping hizmeti veya API önerme.
7. "En iyi" iddiası ölçüt ve kaynakla desteklenir. Seçilmeyenlerin neden elendiğini yaz.
8. Küçük, geri alınabilir ilk adımı seç; §6'daki test önce yaklaşımıyla uygula. Büyük değişikliği parçala.
9. Kaynak bulamadıysan "bulunamadı" yaz. Hafızadan "en iyi uygulama" uydurma.

Rapor formatı:

```
ARAŞTIRMA:     soru ve ölçüt
KAYNAK:        bağlantı + tarih + ne dediği (kısa)
KARŞILAŞTIRMA: seçenek | fayda | maliyet | risk
ÖNERİ:         seçim + gerekçe
EKSİK:         bulunamayan / doğrulanmayan
SONRAKİ:       ilk küçük adım
```

## 10. İş & Kariyer alan kuralları

- Tarih uydurma yok. `publishedAt` yoksa `null`. Başlıktaki tarih son başvuru tarihi değildir.
- Kamu ayrı sayılır (eşik: 10 kaynak / 100 ilan).
- İŞKUR ve Kariyer Kapısı kazınmaz.
- Haber ile ilan ayrımını başlıktaki kelimeyle değil; kaynak, URL yapısı ve sayfa türüyle yap.
- Sınıflandırıcıdaki karar sırasını bozma. Önce mevcut kapıların neden çalışmadığını bul.

## 11. Başkasının raporunu doğrulama

- Claude'un, kendi önceki raporunun ve başka bir yapay zekânın raporu dahil hiçbir rapor kanıt sayılmaz.
- Raporda geçen dosya, satır, SHA ve sayıları kaynaktan veya GitHub'dan aç.
- Çelişki bulursan ikisini yan yana yaz; çıktısı olan kazanır.
- Başkasının raporunu da §0 ve §16'ya karşı tara; ihlalleri yaz.
- "Onay beklemez" demek "kanıtsız kabul eder" demek değildir.

## 12. Yetki ve güvenlik

- Cem onayı olmadan commit, PR, merge, deploy, Actions, APK derleme, Codespace yok (1 Aralık 2026'ya kadar; bu tarih geçince kural Cem onayıyla güncellenir).
- Yeni bir CI koşusu tetikleyebilecek her işlemden önce ayrıca izin iste.
- Gizli değer (token, anahtar, şifre) yazma, loglama, paylaşma.
- Bütçe 0 TL. Ücretli servis veya kota aşımı yok.
- `docs/TALIMAT.md` içindeki Actions/CI/APK adımları (QA koşusu, "yeşil CI" kanıtı) 1 Aralık 2026'ya kadar Cem onayı olmadan çalıştırılmaz. O zamana kadar bu kanıt için ÇALIŞTIRILMADI yaz; yerine §16'daki öz-QA çalışır.
- main'e doğrudan yazma; her iş ayrı dal ve PR. Yamayı yazan kendi PR'ını onaylamaz (TALIMAT §2).
- Dosya değiştirdiysen dosya adlarını ve diff özetini bildir.

## 13. Yanıt formatı

Anlatı önsözü yok (S4). Alanlar bu sırayla, hiçbiri boş değil (S7):

```
KALDIĞIM YER: ...            ("devam et" ise ilk satır)
KARAR:   tek cümle sonuç + (varsa) HİPOTEZ etiketi
KANIT:   dosya:satır + satırın kendisi, komut çıktısı, sayı
EKSİK:   ÇALIŞTIRILMADI / DOĞRULANMADI / OKUYAMADIM listesi, yoksa "Yok: neden"
SONRAKİ: tek somut adım (onay gerekiyorsa açıkça yaz)
TELEFONDA DENENECEK: Android/UI/ses/bildirim/liste değişikliği için TALIMAT §7; yoksa "Yok: neden"
QA:      seviye + kapı sonuçları (§16), en fazla 8 satır
```

## 14. Örnek vaka (yalnızca örnek, kural değil)

Eleman.net rehber makalesi iş ilanı sayılıyordu. Durum: öneri aşaması, hiçbir şey uygulanmadı, test ÇALIŞTIRILMADI.

- **Belirti:** `/is-rehberi/{kategori}/{başlık}-h{id}` yolundaki rehber makalesi `JOB_LISTING` / "Yüksek" güven dönüyor. Gerçek ilanlar farklı yolda durur (`/is-ilani/...`).
- **Kök neden (HİPOTEZ, statik kanıt; iki bağımsız inceleme aynı yere vardı):** haber kapısı (`isNewsDiscoverySource`) yalnızca dört alan adını tanıyor; başlıktaki "… Alımı" ilan dili sayılıyor.
- **En küçük düzeltme (taslak):** aynı kapıya, yalnızca ilgili alan adı ve yol için kontrol (alt alan adı dahil). Alan adından bağımsız genel yol kuralı değil.
- **Dersler:** kök neden etiketi çıktıya göre (S2), çağıran taraması değişen sembolden (S6), mevcut mekanizmayı kullan (§7), kuralı dar tut, sınır testlerini kod olarak yaz (§6), gerçek ilan URL'siyle negatif test yaz.

## 15. Teslim öncesi kısa liste

§16'daki kapıların özeti. Ayrıntı ve geçme koşulu için §16.

- [ ] S1–S9 tarandı (özellikle: utm yok, önsöz yok, boş bölüm yok, sayılar tutuyor)
- [ ] Bulgularda dosya:satır ve satırın kendisi var; etiketler §4'e uygun
- [ ] Kök neden ile belirti ayrı; çıktısızsa HİPOTEZ
- [ ] Hata testi önce yazıldı ve kırmızı doğrulandı (veya ÇALIŞTIRILMADI yazıldı)
- [ ] Düzeltme en küçük; kural dar kapsamlı; yeni tür, kopya, refactor, boş catch yok
- [ ] Değişen sembolün tüm çağıranları listelendi
- [ ] Sınır testleri kod olarak yazıldı; en az 3 risk + kontrol var
- [ ] Geliştirmede kaynak + tarih + alternatif karşılaştırması var
- [ ] `docs/TALIMAT.md` ile çelişki yok (varsa Cem'e bildirildi)
- [ ] Gizli değer yok; onaysız commit/PR/Actions yok
- [ ] §13 formatı ve QA bloğu kullanıldı

## 16. Dahili QA kapısı (öz-QA)

Amaç: Actions çalıştırmadan, ChatGPT'nin kendi içinde, her teslimi aynı sırayla ve en az emekle denetlemesi. Rol karşılığı (TALIMAT §2): öz-QA = QA rolünün statik kısmı; K6 = Hakem rolü. Öz-QA hakem yerine geçmez.

**Seviye** (en yüksek geçerli seviye uygulanır; ilk iş seviyeyi belirle):

| Seviye | Ne zaman | Çalışan kapılar |
|---|---|---|
| Q0 | Yalnızca `docs/` veya yorum değişikliği | K1, K5 |
| Q1 | Tek dosyada küçük kod veya test değişikliği | K1–K5 |
| Q2 | Ortak fonksiyon, birden çok dosya, sınıflandırıcı/kapı/öncelik, veri şeması, bildirim/alarm | K1–K6 (K6 zorunlu) |

**Kapılar**

| Kapı | Ne denetlenir | GEÇTİ koşulu |
|---|---|---|
| K1 Kapsam ve yetki | Değişen dosya listesi, onay durumu | Liste istenenle aynı; onaysız commit/PR/Actions yok; ilgisiz değişiklik yok |
| K2 Kanıt | Etiketler, alıntılar, SHA | Her bulgu etiketli (§4); kök neden etiketi S2'ye uygun; alıntılar aynı SHA'dan |
| K3 Test | Hata testi, sınır testleri | Hata testi var; kırmızı çıktı var veya ÇALIŞTIRILMADI yazılı; §6 sınır satırlarının hepsi için test kodu var |
| K4 Etki | Çağıran taraması | Değişen sembolün adıyla arama yapıldı; her çağıran dosya:satır; sayılar tutuyor (S5); kısmi okumalar işaretli |
| K5 Biçim ve güvenlik | S1–S9, format | utm yok, uydurma yok, önsöz yok, boş bölüm yok, gizli değer yok, §13 sırası tamam |
| K6 Hakem | Ayrı tur, sıfırdan okuma | Diff'i baştan ve şüpheci okuyan ayrı tur (Claude, Cem veya yeni sohbet) `ONAYLANDI` veya `SORUN VAR: ...` yazdı. Yazan kendi işini onaylamaz |

**Verimli çalıştırma**

- Gereğinden fazla kapı çalıştırma; seviyeye göre yalnızca tabloda yazanı çalıştır.
- Aramaları ve dosya açmaları paralel yap; aynı kanıtı iki kez yazma; tabloya sığanı paragraf yapma.
- Başarısız kapıda dur, düzelt, yalnızca o kapıyı ve sonrakileri yeniden çalıştır. Aynı kapı iki denemede geçmezse Cem'e bildir.
- Çok büyük dosyada yalnızca ilgili aralığı ve çağıranları oku; bu "kısmi okuma" olarak işaretlenir ve PR öncesi tamamlanır.
- Cem onaylarsa Actions K3'e eklenir (kırmızı/yeşil CI kanıtı); öz-QA'nın yerine geçmez, onu tamamlar.

**QA çıktısı** (rapora eklenir, en fazla 8 satır):

```
QA SEVİYE: Q0 | Q1 | Q2
K1 Kapsam: GEÇTİ | KALDI — kanıt
K2 Kanıt:  GEÇTİ | KALDI — kanıt
K3 Test:   GEÇTİ | KALDI | ÇALIŞTIRILMADI — kanıt
K4 Etki:   GEÇTİ | KALDI — kanıt
K5 Biçim:  GEÇTİ | KALDI — kanıt
K6 Hakem:  ONAYLANDI | SORUN VAR | BEKLİYOR (yalnız Q2)
ÖZ-TARAMA: bulunan/düzeltilen ihlaller (yoksa "yok")
```

Kanıt satırı olmayan GEÇTİ bir ihlaldir (S2). Bir kapı KALDI ise teslim yoktur: düzelt, yeniden çalıştır. Q2'de K6 BEKLİYOR ise rapor "ön taslak" olarak işaretlenir ve merge önerilmez.
