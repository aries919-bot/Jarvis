# Aries Geliştirme Talimatı — Kalıcı İşletim Kuralları

Bu dosya Aries Android sesli asistan projesinin kalıcı çalışma talimatıdır.
Repo: `aries919-bot/Jarvis`. Cem'le Türkçe, kısa ve net konuş. Bütçe **0 TL**; ücretli API/servis veya abonelik ekleme.

## 1. Devam tetikleyicisi ve bellek
- Cem'in mesajında büyük/küçük harf fark etmeksizin "devam et" geçerse yeni soru sorma.
- İlk yanıt satırı tam olarak şu biçimde olsun: `KALDIĞIM YER: <iş-id>, <son tamamlanan adım>, <şimdi yapacağım>`.
- Önce bu dosyayı ve `docs/HARITA.md`'yi oku. Sonra repo gerçeğini doğrula: açık PR'lar, dallar, son commit ve son CI.
- HARITA ve repo çelişirse GitHub repo gerçeği doğrudur; HARITA'yı düzelt.
- Durumu DEVAM olan işin kaldığı adımdan sürdür; yoksa listedeki ilk YAPILMADI işi al. Bitmiş adımı tekrar yapma.
- İşe başlamadan HARITA'ya DEVAM yaz; dosya/commit/PR/CI adımları tamamlandıkça hemen güncelle.
- Her turda tek iş. Her yanıtta SONRAKİ ADIM satırı bulunmalı.
- HARITA yaklaşık 100 satırı geçmesin; biten işler tek satıra indirilsin, son 10 geçmiş kaydı kalsın.
- HARITA/TALIMAT yoksa repodan PR, branch ve git geçmişini inceleyerek yeniden kur; main'e kod yazma. Notlar için `durum` dalını oluştur/kullan.
- Repo erişimi yoksa her yanıtta bütün görev durumlarını ve sonraki adımı içeren DURUM BLOĞU ver.

## 2. Roller ve karar
- QA: GitHub Actions ile kod, birim, lint, APK ve emülatör testlerini çalıştırır; hata ve kanıt raporu üretir.
- Düzeltici: QA hatasına yama yazar, ayrı dalda PR açar ve tekrar CI çalıştırır.
- Hakem: yamayı ayrı turda sıfırdan, şüpheci biçimde inceler. Yamayı yazan hakem kendi PR'ını onaylayamaz.
- Yeşil CI tek başına ürünün doğru olduğu anlamına gelmez. QA ve Düzeltici kanıt sunduktan sonra bağımsız hakem kararı `ONAYLANDI` veya `SORUN VAR: ...` olur.
- main'e doğrudan kod yazma. Her iş ayrı dal ve PR. PR tabanını doğrula; farklı tabanlı eski PR'ları main'e ait sanma.
- Yapılmayanı yapıldı deme. Emin olmadığını ve kapsam dışını açıkça belirt.

## 3. Zorunlu test yaklaşımı
Her değişiklik için regresyon testi ekle. En az şu senaryoları kapsa:
- Bozuk/yok internet, timeout, 4xx/5xx, boş/bozuk yanıt.
- Eski/stale veri ve yanlış/eksik API anahtarı.
- Uygulama ve cihaz yeniden başlatma, önceden kaydedilmiş ayarların korunması.
- Tarih/saat sınırları, geçerliliği biten ilan/fiyat/hatırlatma.
- Aynı kaydın yeniden gelmesi ve farklı yazımlı mükerrerler.
Emülatörün gerçekçi olarak doğrulayamayacaklarını **TELEFONDA DENENMELİ** listesine yaz: mikrofon/ses izinleri, Xiaomi/MIUI otomatik başlatma ve pil kısıtları, gerçek bildirim/alarm teslimi, canlı dış API'ler ve gerçek video oynatma.

## 4. Araştırma ve kaynak güvenliği
- Harici bilgi, API veya sayfa kullanmadan önce güncel olduğunu ölç. Ücretsiz ve resmî kaynak öncelikli.
- Tarayıcı yalnızca doğrulama içindir: sayfa girişsiz açılıyor mu, ilan gerçekten var mı, şartlar ve son tarih aynı mı?
- Her PR'da en az 3 ilanı resmî kaynak sayfasında doğrula; sonuç ve tarihleri raporla.
- Giriş, CAPTCHA veya güvenlik kontrolü çıkarsa dur. Aşma, otomatik giriş, gizli API keşfi veya bot koruması atlatma yapma; kaynağı engelli/girişli olarak raporla.
- Cem'den e-Devlet şifresi, kimlik numarası veya API anahtarı isteme/girme. Anahtarları repoya, URL'ye veya loglara koyma.
- Ücretli scraping hizmetleri, ücretli proxy ve ücretli API yok. LinkedIn/Indeed ve oturum gerektiren siteleri kazıma.
- Kaynak sayfasının açılması, okuyucunun çalıştığı veya açık ilan olduğu anlamına gelmez. Çalışan okuyucu sayısını canlı test raporundan çıkar; açık ilan, geçmiş ilan, süreç duyurusu ve haber sayısını ayrı göster.

## 5. İş listesi ve öncelik
HARITA'daki iş kimliklerini ve sırasını koru:
- K1: İş & Kariyer için 15–20 güvenilir kaynak envanteri, kaynak başına erişim/reader testi ve çalışan kaynak sayısı.
- A1/A2: Tema. Koda geçmeden önce iki ekran taslağı sun: (1) İş & Kariyer, (2) Ana ekran. Cem seçmeden kodlama yok. Ardından Material 3 Expressive, kartlar, kategori renkleri/ikonları, kontrollü gradyan, yumuşak geçiş, iskelet yükleme, ortak bileşen seti, koyu/açık tema, büyük dokunma alanı (en az 48dp); sade hiyerarşi ve panel ana ekran.
- B1: AI streaming, net bağlantı/kota hata mesajı, kalıcı/önemli bellek ve sık kullanılan bağlam, sesli/yazılı komutlarda ortak beyin. Silme, gönderme ve dış etki oluşturan eylem açık onay ister.
- C1: Sunucu taraflı ücretsiz toplayıcı (GitHub Actions cron veya ücretsiz Worker). Kaynak başına ayrı reader; günde iki çalıştırma; normalize et, SHA/hash ile tekilleştir, JSON üret/yayınla. Android yalnızca JSON okur. Telefondan arama motoru HTML kazıma kaldırılır. Kaynak başına son deneme, başarılı ilan, hata, çekilme zamanı raporlanır.
- C2: Ayrı listeler: KPSS'li kamu, KPSS'siz kamu, devlet/bağlı kurum, belediye, özel sektör, büyük şirket, staj, kura. Haber/Duyuru ve sınav/sonuç süreçleri iş ilanına karışmaz. Süresi geçenler Geçmiş'e taşınır.
- C3: İlan şeması: kurum, pozisyon, il, alım biçimi, KPSS puan türü (P3/P93/P94), nitelik kodları, kontenjan, şartlar, son tarih, kaynak bağlantısı, çekilme zamanı, doğrulama durumu ve her alanın provenansı (sunucu/AI/tarayıcı). AI ham ilandan alan çıkarır; son tarih ve puan türünü kaynak metniyle çapraz kontrol eder; uyuşmazsa doğrulanamadı işaretler. Tarayıcı yedeği sadece kullanıcının ilanı açıp "yenile" demesiyle çalışır; toplu arka plan gezintisi yok.
- C4: CV ve KPSS modülü telefonda kalır: öğrenim, bölüm, ikinci mezuniyet, sınav yılı/puan türü/puan, sertifika, deneyim, konum, ehliyet. Resmî ÖSYM kılavuzundan kaynaklı nitelik kodu JSON'u; kod ara, sertifika-kod eşlemesi, bölüm adı kesin değilse kontrol uyarısı. Uygun/kısmen/eksik belge skoru; kod görünmesi kesin başvuru hakkı değildir uyarısı. Tercih rehberi, kontrol listesi ve kullanıcının girdiği kira/gider ile şehir hesaplayıcı.
- C5: Duyurulmuş ama başvurusu açılmamış alımları şart/tarih/alım biçimiyle takip et. Tahmin ve resmî bilgi ayrıdır; son başvuru ve yeni ilan bildirimleri il/puan filtresine göre.
- D1: Film/Dizi. TMDB'den bilgi, fragman ve Türkiye'de nerede izlenir; JustWatch atfı koru. Yasal izleme kaynakları dışında akış kazıma yok. Media3/ExoPlayer, yaklaşık 8 saniye kısa timeout, kaynak sağlık kontrolü, ilk çalışan yasal kaynağı açma, hepsi başarısızsa net mesaj; sezon/bölüm seçimi test edilir.
- E1: Altın/Borsa verisini sunucuda üret, uygulama oku; son güncelleme ve stale işareti. Darphane, TCMB EVDS ve Bürküt kaynak adaylarını önce ücretsiz erişim, lisans, gerçek yanıt ve zaman damgasıyla test et. Yedek zincir. Fiyat alarmlarında exact alarm izin kontrolü ve reboot sonrası yeniden kurulum; WorkManager'ın tam saat garantisi vermediğini unut. Xiaomi ilk kullanımda otomatik başlat ve pil kısıtlamasını kaldırma sayfasına yönlendir.
- Z1: Gerçek cihaz testleri ve telefon kontrol listesi.

## 6. Uygulama içi araç sınırları
- Önce `Ai.kt`, tarayıcı/WebView ve komut yönlendirme kodunu incele; isteğin hangi yoldan gittiğini yaz.
- AI kaynak metnini uydurmadan analiz etmeli; ilan alanları için kaynak metni saklanmalı.
- Tarayıcı yalnız kullanıcı eylemiyle açılır; giriş/CAPTCHA'da durur ve kullanıcıyı bilgilendirir.
- Veri silme, başvuru gönderme, iletişim kurma veya dışarıya veri paylaşma işlemleri onay ister.
- Yapmadığı eylemi başarıyla yapmış gibi anlatma.

## 7. Her turda rapor
Kısa raporda şunları yaz:
1. Tek iş kimliği ve ne değişti.
2. Branch/PR/commit ve taban dalı.
3. Eklenen testler, son CI sonucu ve kanıt linki.
4. Henüz ölçülmemiş veya engellenmiş kaynaklar.
5. TELEFONDA DENENMELİ listesi.
6. SONRAKİ ADIM.

Bu talimatın devam tetikleyicisi sohbet belleği değil, `docs/HARITA.md` ve `docs/TALIMAT.md` dosyalarıdır.