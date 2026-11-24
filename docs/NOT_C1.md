# ARIES C1 NOTU — 24 Kasım 2026

> Bu belge `docs/TALIMAT.md` dosyasına C1 özel ekidir. Çelişki halinde bu not geçerlidir.
> Kalıcı konum: `durum` dalı. C1 dışındaki işlere uygulanmaz.

## 1. Sert C1 kabul kuralları

1. Tek iş C1 sunucu toplayıcısıdır. A, D, E, tema, film ve bildirim işlerine dokunulmaz. C1 bitmeden arayüz entegrasyonuna geçilmez.
2. “Bitti” demek için üçünün de kanıtı gerekir: çalışan komutun gerçek çıktısı, kaynak başına ilan sayısı ve yeşil CI bağlantısı. Sayıları hakem bağımsız olarak yeniden sayar.
3. Kamu ilanları ayrı raporlanır; doğrulanmış kamu ilanı 0 ise karar RED'dir. Buna ek olarak toplamda en az 10 başarılı kaynak ve 100 doğrulanmış gerçek ilan gerekir. Menü, bağlantı, başlık, haber ve duyuru ilan değildir.
4. Tarih uydurulmaz. Alanlar:
   - `publishedAt`: yalnız kaynak açıkça veriyorsa; yoksa `null`.
   - `deadline`: kaynakta yazan son başvuru tarihi.
   - `collectedAt`: bu taramanın zamanı.
   - `firstSeenAt`: sistemin ilanı ilk gördüğü zaman; sonraki taramalarda korunur.
   Çekim tarihi yayın tarihi değildir. e-Şube'de yayın tarihi yoktur: `publishedAt=null`, `deadline=son başvuru`. `deadline < bugün` olan ilan, veri stale olsa bile güncel listede asla gösterilmez; geçmişe gider.
5. Son iyi veri kaynak bazında korunur. Bir kaynak hata verirse yalnız o kaynağın önceki ilanları stale işaretlenir; diğer kaynakların yeni verileri güncellenir. Bir kaynağın hatası bütün feed'i eskiye döndürmez.
6. Her sekme yalnız kendi veri kümesini gösterir; otomatik yönlendirme yoktur. Dış site Android Custom Tabs ile açılır; geri dönüşte beyaz ekran olmamalıdır.
7. Her PR'da testler: gerçek ilan, bozuk internet, boş yanıt, HTTP 403, HTTP 500, timeout, robots engeli ve eski veri.
8. Yasaklar: robots.txt ihlali; giriş/CAPTCHA/güvenlik kontrolü aşma; LinkedIn/Indeed kazıma; film akışı kazıma; token, CV, KPSS veya e-Devlet bilgisi isteme/toplama/loglama.
9. Mimari: ağır toplama (özellikle e-Şube) GitHub Actions'ta çalışır. Ücretsiz Cloudflare Worker (10 ms CPU, 50 dış istek sınırı) yalnız JSON'u saklar ve `/kariyer-feed` ile sunar. Worker'a ağır sayfalama/toplama yükü verilmez.
10. Cem'e Termux komutu verilmez. Ajanlara tek blok görev metni verilir; sonuç Actions özeti ve kanıt linklerinden istenir. Her rapor kısa Türkçe olur ve şu alanlarla biter: `KARAR: KABUL/RED`, `Kanıt:`, `Eksik:`, `Telefonda denenecek:`, `SONRAKİ ADIM:`.

## 2. Cline çalışırken bekleme ve bağımsız işler

Cem “Cline çalışıyor” derse veya HARITA'da K1/E1-Veri `DEVAM` durumundaysa:
- Cline'ın işini reddetme; RED kararı verme; yeniden başlatma isteme.
- Cline'ın dalına, dosyalarına ve çalışan işine dokunma.
- Cline bitene kadar yalnız çıktısına ihtiyaç duymayan bağımsız işleri yap:
  1. `ilan.gov.tr`, `kariyerkapisi.gov.tr/isealim` ve kurum duyuru sayfaları için kaynak adayı ölçümü (robots, HTTP, gerçek ilan, tarih alanı) ve `career/adaylar.txt` önerisi.
  2. `aries919-bot/jarvis-cloud` PR #5 kod incelemesi ve eksik test listesi.
  3. HARITA güncellemesi.
- Cline bitince çıktısını şüpheci biçimde denetle; ardından C1 düzeltme görevini tek blok olarak ver.
- Sıra: önce K1/E1-Veri (Cline), sonra C1 düzeltmesi. Cline'ın işini paralel veya yeniden başlatma.

## 3. Ölçülmüş K1 bulguları — yeniden tahmin etme

### İŞKUR
- `iskur.gov.tr/ilanlar/*` sayfaları boş kabuk: ilan yok, XHR yok, tarih 0. `/baglantilar/kurum-disi-kamu-isci-alimi-ilani/` 404. Bunlar kaynak kabul edilmez.
- Gerçek kaynak: `https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx`.
- Giriş yok, CAPTCHA yok, robots izinli olarak ölçüldü.
- ASP.NET formu: “Kamu” radyosu (`kamuRadio`) seçilir, Ara postback yapılır, sayfalama `btnNext` ile sürer. Sayfa başına 15 ilan.
- Satırdaki tarih son başvuru tarihidir (örnek: 27.11.2026, “3 Gün Kaldı”); yayın tarihi yoktur. İlan numarası 0000 ile başlayan 11 haneli kimliktir ve tekilleştirmede kullanılır.
- Başvuru süresi 5 gün; açık ilanlar bu nedenle zaten tazedir.
- Okuyucu betiği: Jarvis deposu `scripts/esube_kamu_okuyucu.py`; K1/E1 işi kapsamında Cline yazmaktadır. Bu dosyaya Cline tamamlamadan müdahale edilmez.

### Diğer kaynak adayları
- `csgb.gov.tr/duyurular`: başlıktaki tarihler sınav tarihi olabilir, yayın tarihi değildir; çoğu içerik sonuç duyurusudur. İkincil kaynak olarak değerlendir. Yayın tarihi adresin sonundaki tarihten (ör. `11082026`) çıkarılabilir; yalnız “alım/ilan” başlıklarını tut, “kazananlara ilişkin” içerikleri ele.
- Kariyer Kapısı'nın doğru adresi `https://kariyerkapisi.gov.tr/isealim`; eski `cb.gov.tr` adresi ölüdür. Yeni adres JavaScript kabuğudur; içerik JavaScript ile yüklenir ve ayrıca ölçülmelidir.
- `ilan.gov.tr`: sertifika zinciri sorunu ölçüldü. GitHub Actions ortamında yeniden denenmelidir; TLS doğrulaması kapatılmayacaktır.

## 4. jarvis-cloud PR #5 — bilinen hakem bulguları

Repo: `aries919-bot/jarvis-cloud`; PR #5; dal `is/c1-career-feed`; hedef `main`.
- PR #5 mevcut kanıtlarla RED'dir; K1 Cline işinin reddi anlamına gelmez.
- 15 aday kaynağın tamamı özel sektör Lever/Ashby kaynağıdır; kamu kaynağı yoktur.
- CI başarısızdır.
- `getCareerFeed` içe aktarılmış fakat kullanılmıyor; `/kariyer-feed` GET rotası tanımlı değildir.
- `firstSeenAt` taramalar arasında kalıcı değildir.
- Önceki incelemede kısmi kaynak sonucu son iyi feed'i ezebiliyordu; kaynak bazlı stale/son-iyi-veri davranışı zorunlu hale getirilmelidir.
- 15 test fixture'ı canlı ilan kanıtı değildir. Gerçek çalışan kaynak ve gerçek ilan sayısı henüz kanıtlanmamıştır.
- PR #5 için yeniden değerlendirme; kamu okuyucuları, kaynak bazlı hata/koruma, tarih kuralları, eksik testler, gerçek komut çıktısı, kaynak başına sayım ve yeşil CI kanıtı geldikten sonra yapılır.

## 5. Devam sırası

1. K1/E1-Veri Cline işi tamamlanana kadar Cline dosya ve dalına dokunma; bağımsız C1 denetim/ölçüm işleriyle sınırlı kal.
2. Cline PR ve Actions çıktısını tamamlayınca bağımsız doğrula.
3. Sonra C1 PR #5 düzeltme görevini tek blok olarak ajana ver.
4. En az 10 kaynak, en az 100 gerçek ilan ve ayrıca kamu ilanı 0'dan büyük olmadan KABUL/arayüz entegrasyonu yok.
