# ARIES C1 NOTU v2 — 24 Kasım 2026

> TALIMAT.md'ye C1 özel ekidir. Çelişki halinde bu not geçerlidir.
> Kalıcı konum: `durum` dalı. C1 dışı işlere uygulanmaz.

## 1. Sert kurallar

1. Tek iş C1 hattıdır; A, D, E, tema, film ve bildirim yok.
2. “Bitti” kanıtı: çalışan komut çıktısı, kaynak başına ilan sayısı ve yeşil CI bağlantısı. Hakem sayıları yeniden sayar.
3. Kamu ayrı raporlanır; kamu ilanı 0 ise RED. Buna ek olarak en az 10 başarılı kaynak ve en az 100 doğrulanmış gerçek ilan gerekir. Menü/link/başlık ilan değildir. Eşik altındaysa gerçek sayı yazılır; arayüze geçilmez.
4. Tarih uydurulmaz. Alanlar: `publishedAt` (kaynakta yoksa null), `deadline`, `collectedAt`, `firstSeenAt`. Çekim tarihi yayın tarihi değildir. e-Şube'de `publishedAt=null`, `deadline=son başvuru`. `deadline < bugün` olan ilan stale olsa bile güncel listede gösterilmez.
5. Son iyi veri kaynak bazlıdır: bir kaynak düşerse yalnız onun eski verisi stale olur; diğerleri güncellenir.
6. Sekmeler yalnız kendi verisini gösterir. Dış site Custom Tabs ile açılır; beyaz ekran olmaz.
7. Her PR testi: gerçek ilan, bozuk internet, boş yanıt, HTTP 403/500, timeout, robots engeli, eski veri.
8. Yasak: robots ihlali, giriş/CAPTCHA aşma, LinkedIn/Indeed kazıma, film akışı kazıma, token/CV/KPSS/e-Devlet verisi. Açık User-Agent: `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)`; tarayıcı taklidi yok.
9. Ağır toplama (e-Şube) GitHub Actions'ta. Ücretsiz Cloudflare Worker (10 ms CPU/50 dış istek) JSON'u AYRIŞTIRMADAN ham metin olarak saklar ve `/kariyer-feed` ile sunar.
10. Cem Termux kullanmaz; komut istenmez. Cline'a uzun görev yapıştırılmaz. Görev `durum/gorevler/<id>.md` dosyasına yazılır ve Cline'a tek satır verilir: `git fetch origin durum; git show origin/durum:gorevler/<id>.md oku, görevi aynen uygula, o dosyayı commit etme.`
11. Her yanıt şu alanlarla biter: `KARAR: KABUL/RED`, `Kanıt:`, `Eksik:`, `Telefonda denenecek:`, `SONRAKİ ADIM:`.

## 2. Cline çalışırken

Cem “Cline çalışıyor” derse veya HARITA'da iş DEVAM ise Cline'ın dalına/dosyalarına dokunma; o iş hakkında karar verme, reddetme veya yeniden başlatma isteme. Bitmesini bekle. Beklerken yalnız bağımsız işler yapılır: kaynak adayı ölçümü (robots, HTTP, giriş, ilan/tarih), `career/adaylar.txt` önerisi, `jarvis-cloud` PR #5 kod incelemesi/eksik testleri ve HARITA güncellemesi. Cline bitince çıktıyı şüpheci denetle ve aşağıdaki sırayı sürdür.

## 3. Kanıtlanmış K1 bulguları — 24 Kasım, yeniden tahmin etme

### İŞKUR
- `/ilanlar/kurumdisi-kamu-isci-alim-ilanlari/` ve `/ilanlar/kamu-memur-alim-ilanlari/`: render sonrası ilan 0, tarih 0, XHR yok; boş kabuk.
- `/baglantilar/kurum-disi-kamu-isci-alimi-ilani/`: 404.
- Gerçek kaynak: `https://esube.iskur.gov.tr/Istihdam/AcikIsIlanAra.aspx`. Giriş yok, CAPTCHA yok, robots izinli.
- ASP.NET: Kamu radyosu `kamuRadio`; Ara postback `ctl04$ctlAcikIsPageCommand$CommandItem_Search`; sonraki sayfa `ctl04$ctlDataPagerDetay$btnNext`. Sayfa başına 15 ilan.
- Satır tarihi SON BAŞVURU (ör. 27.11.2026, “3 Gün Kaldı”); yayın tarihi yok. İlan no `0000` ile başlayan 11 hane, tekilleştirme anahtarı. Başvuru süresi 5 gün; açık ilan tazedir.
- İlk 15 ilan MSB Personel Genel Müdürlüğü'ne ait. Betik `scripts/esube_kamu_okuyucu.py`; K1-E1 kapsamında Cline yazıyor.

### Diğer kaynaklar
- `csgb.gov.tr/duyurular`: başlıklardaki tarihler sınav tarihi, yayın tarihi değil; çoğu sonuç duyurusu, bazıları 2023. URL sonundaki sayı (ör. 11082026) yayın tarihi olabilir iddiası TAHMİNDİR, doğrulanmadı. İkincil kaynak; “alım/ilan” süz, “kazananlara ilişkin” ele.
- Kariyer Kapısı güncel adres `https://kariyerkapisi.gov.tr/isealim`; JS kabuğu, ayrıca incelenecek. Eski adres kullanılmaz.
- `ilan.gov.tr`: sertifika zinciri sorunu; Actions'ta yeniden dene, TLS doğrulamasını kapatma.

### jarvis-cloud PR #5
- RED: 15 adayın hepsi özel sektör Lever/Ashby; kamu yok; CI başarısız; `/kariyer-feed` tanımsız; `firstSeenAt` kalıcı değil.

## 4. HARITA iş kimlikleri
Aşağıdaki kimlikler HARITA'da bulunur. Başlangıç durumu: K1 ve K1-E1 DEVAM; diğerleri YAPILMADI, sırası 5. bölümdeki gibidir.

## 5. Kesin görev sırası

“Devam et” denince HARITA'da ilk DEVAM işi seçilir; DEVAM yoksa aşağıdaki ilk YAPILMADI iş seçilir. Her iş CI YEŞİL + kanıt olunca HARITA güncellenir ve sıradaki görev, 10. kuraldaki yöntemle Cline'a verilir.

1. **K1-E1 (Cline) — DEVAM:** e-Şube kamu okuyucusu + `.github/workflows/kamu-ilan-cek.yml`. Bitiş: Actions yeşil; özet sayfa sayısı/toplam ilan/kurum sayısı; `career/esube_kamu_ilanlari.json` `durum` dalında; en az 3 ilan e-Şube'de elle doğrulandı.
2. **K1-F (Cline):** `scripts/render_probe.py` ve `.github/workflows/iskur-render.yml` içindeki Chrome/tarayıcı taklidi User-Agent'ını silmeden açık kimliğe çevir. Başka şeye dokunma. Bitiş: Actions yeşil.
3. **K1-B (Cline):** yalnız `.github/workflows/kaynak-yokla.yml` oluştur. `workflow_dispatch` ve `push` (branches [`cline/K1`], paths yalnız kendi dosyası). Şu adreslerde `curl -sS -w` ile HTTP kodu, bayt, hata yazdır: robots.txt, ana sayfa, /sitemap.xml. Adresler: `https://www.ilan.gov.tr`, `https://kariyerkapisi.gov.tr/isealim`, `https://www.resmigazete.gov.tr`. ilan.gov.tr için ayrıca `openssl s_client -connect www.ilan.gov.tr:443 -servername www.ilan.gov.tr` ile sertifika zinciri. SSL doğrulamasını kapatma (`-k` yok). User-Agent: `AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)`. Robots izin vermiyorsa o yola gitme. Sonucu `GITHUB_STEP_SUMMARY`'ye yaz. Başka dosyaya dokunma. Commit: `ci: kaynak yoklama`; `cline/K1` dalına push. Bitiş: Actions özeti her adres için robots/HTTP/bayt/hata.
4. **K1-C (ChatGPT):** `docs/KARIYER_KAYNAK_DENETIMI.md` ve `career/adaylar.txt` dosyalarını yalnız ölçülmüş adreslerle güncelle. Her kaynak ÇALIŞIYOR / ENGELLİ (neden) / ÖLÜ; en az 1 kamu kaynağı ÇALIŞIYOR. Bitiş: ölçüm kanıtı ve dosyalar.
5. **C1-1 (Cline):** jarvis-cloud PR #5 düzeltmesi; C1 görev metni ve 1. bölüm kuralları. Kamu girdisi `durum` dalındaki `career/esube_kamu_ilanlari.json`. Başlama şartı 1. işin bitmesi. Bitiş: `npm run test:c1` gerçek çıktısı, CI yeşil.
6. **C1-2 (Cline):** canlı rapor `node scripts/career-feed-live.mjs`; kaynak başına ilan sayısı, kamu ayrı, 3 ilan kaynağında doğrulandı. Bitiş: kamu en az 1 kaynak ve toplam 10 kaynak/100 ilan. Altındaysa RED ve gerçek sayı.
7. **K1-M (Cline):** cron yalnız main'e girince çalışır. `kamu-ilan-cek.yml`, `kaynak-tarama.yml` ve gerekli betik/kayıt dosyalarını temiz bir dalla main'e PR et; `cline/K1` içindeki film/oynatıcı commitlerini alma, yalnız gerekli commitleri cherry-pick et. Bitiş: PR açık, CI yeşil. Sonra hakem ONAYLANDI, ardından C2.

## 6. K1-B görev dosyası

Aynı görev ayrıca `gorevler/K1-B.md` dosyasında tam metin olarak bulunur. Kline'a görev sırası geldiğinde yalnız tek satırlık fetch/show talimatı verilir.
