# Aries — Durum Haritası

> Tek gerçek durum dosyası. Sohbet geçmişinden önce bu dosya okunur.
> Son kontrol: 23.11.2026. Repo: `aries919-bot/Jarvis`.
> Bu dal yalnızca proje notları içindir; Android kodu burada geliştirilmez.

## Repo gerçeği
- Varsayılan `main`: `63e877d96cb910e6e98d56834ae55ae8edafab2a`.
- Notlar `durum` dalında; repo okuma/yazma erişimi doğrulandı.
- Cline sahiplik kuralı kaydedildi. Henüz `cline/*` dalı veya Cline PR'ı yok; Cline henüz `DEVAM: K1` bildirmedi.
- K1 PR #24 (`is/kariyer-kaynak-denetimi-1`) eski ChatGPT çalışma koludur, `cline/K1` değildir. Cline devralana kadar PR #24'e müdahale/inceleme/merge yok; Cline K1'i kendi dalında yapıp raporlayacak.
- A2 PR #25 açık/taslak, `is/a2-expressive-theme` → `main`, HEAD `2f0fbd8efb6c51083d1e0e0e484d867e1d6d0f73`. İlk CI'da `:app:checkDebugAarMetadata` NPE (`path is null`) vardı; aynı run'ın ikinci denemesi test+APK SUCCESS. Kök neden bulunmadı, hata tekrarlanmadı.
- A2 tanılama PR #27 açık/taslak, `fix/a2-gradle-metadata-stacktrace` → A2; HEAD `ce88f0c7985cbf94cbe2fdba64b113ad5dc37455`; workflow-only. PR #45177070960 ve push #45177061495 SUCCESS.
- A2 devam PR #28 açık/taslak, `is/a2-theme-completion` → A2, HEAD `8db45d9c5ce503b96ca415949c4645b769155300`; 7 dosya. PR CI #45180833170 ve push CI #45180819864 SUCCESS; test, APK ve artefakt başarılı. PR artefakt #14821841078 (13,436,611 bayt).
- PR #28: ortak açık/koyu palet, yerel tercih saklama, gerçek görev/hatırlatma sayacı, Film/Dizi skeleton ve kontrast regresyonları. Gerçek telefon doğrulaması ve bağımsız inceleme bekliyor.
- B1 PR #26 açık/taslak, `is/b1-ai-memory-foundation` → A2, HEAD `dbab9d9d275cd8058a1d94900806fb04d82da9cf`; PR CI #45175633112 ve push #45175625717 SUCCESS. Bağımsız inceleme, canlı API ve telefon doğrulaması bekliyor.
- E1 ikiye ayrıldı: E1-Veri Cline; E1-UI/Xiaomi ChatGPT. Aynı dosyada paralel değişiklik yapılmayacak.
- Kariyer zinciri: PR #3 `kariyer-modulu` → `film-dizi-v2` açık/taslak; PR #22 `is/kariyer-1-21-verified-search` → `kariyer-modulu` açık/taslak. PR #22'de C2 kategorileri ve Haber/Süreç/Program ayrımı var; Full App QA (Android 29/33/34/35) + APK CI SUCCESS (#45141772884, #45141772982). Henüz main'e birleşmedi.
- Diğer açık PR'lar: #23/#18/#17/#9/#4/#2/#1; eski/farklı tabanlı PR'lar main'e alınmış sayılmaz.

## İş listesi ve sahiplik
| İş | Durum | Sahip / Not |
|---|---|---|
| K1 Kaynak matrisi ve canlı okuyucu denetimi | CLINE BEKLİYOR | sahip: cline. Eski PR #24'e dokunma; `cline/K1` ve Cline raporunu bekle. |
| A1 İki ekran taslağı | ONAYLANDI | sahip: chatgpt. Ana ekran + İş & Kariyer Cem tarafından onaylandı. |
| A2 Tüm uygulama tema sistemi | PR AÇIK | sahip: chatgpt. #25/#27/#28 taslak ve CI yeşil; telefon testleri ve bağımsız inceleme bekliyor. |
| B1 Streaming, bellek ve hata yönetimi | PR AÇIK | sahip: chatgpt. PR #26 CI yeşil; bağımsız inceleme, canlı API ve telefon testi bekliyor. |
| C1 Sunucu ilan toplayıcı + tekilleştirme JSON | CLINE BEKLİYOR | sahip: cline. K1 onayından sonra başla; yalnız doğrulanmış açık kaynakları kullan. |
| C2 İlan listeleri, geçmiş, Haber/Duyuru ayrımı | PR AÇIK | sahip: chatgpt. PR #22'de kategori grupları ve Haber/Süreç/Öğrenci ayrımı var; bağımsız inceleme ve telefon testi bekliyor. |
| C3 İlan şeması, AI alan çıkarımı, kullanıcı tetikli tarayıcı yedeği | DEVAM | sahip: chatgpt. `is/c3-ilan-semasi-ai-kaynak-kaniti` → `is/kariyer-1-21-verified-search`; alan şeması/provenans, kaynak kanıtı ve yalnız kullanıcı eylemiyle WebView yenileme. |
| C4 CV + KPSS tercih/uygunluk modülü | YAPILMADI | sahip: chatgpt. Kişisel profil telefonda kalır. |
| C5 Gelecek alım takvimi ve bildirim | YAPILMADI | sahip: chatgpt. Resmî tarih ile tahmin ayrı. |
| D1 Film & Dizi | YAPILMADI | sahip: chatgpt. Yalnız yasal/izinli akış. |
| E1-Veri Altın/Borsa kaynak ve JSON sunucusu | CLINE BEKLİYOR | sahip: cline. UI dosyalarını değiştirme. |
| E1-UI Piyasa ekranı/grafik/alarmlar/Xiaomi yönlendirmesi | YAPILMADI | sahip: chatgpt. Veri sözleşmesi Cline ile netleşmeden entegrasyon yok. |
| Z1 Gerçek telefon QA | TELEFONDA DENENECEK | sahip: chatgpt. Mikrofon, Xiaomi pil, bildirim, canlı API, gerçek oynatma, tema kalıcılığı. |

## Cline'a ayrılmış net işler (tek amaç, 2-3 satır)
**K1 — ilk sıradaki iş / sahip: cline**
- Dosyalar: `.github/workflows/career-source-audit.yml`, `career/sources.json`, `docs/KARIYER_KAYNAK_DENETIMI.md`, `scripts/career_source_audit.py`, `scripts/test_career_source_audit.py`.
- İş: Kaynak başına erişim/giriş/CAPTCHA durumunu, çekim zamanı, ilan sayısı ve hatayı ölç; otomasyon için yalnız açık ve izinsiz giriş gerektirmeyen sayfaları seç.
- Bitti ölçütü: Her önerilen kaynakta gerçek sayfa denetimi; PR başına en az 3 gerçek ilan örneği veya neden bulunmadığı; test sonucu ve telefonda denenecekler PR açıklamasında; `cline/K1` PR ve yeşil CI.

**C1 — K1 onayından sonra / sahip: cline**
- Oluşturulacak dosyalar: `worker/src/career-feed.ts`, `career/feeds.json`, `.github/workflows/career-feed.yml`, `worker/test/career-feed.test.ts`.
- İş: Yalnız K1'de doğrulanmış kaynaklardan günde iki kez ücretsiz Actions/Worker ile çek, şemayı normalize et, kurum+pozisyon+il ile tekilleştir, süresi dolanı geçmişe taşı, kaynak sağlık metriklerini JSON'a yaz.
- Bitti ölçütü: En az 3 resmî örnek kaynakta doğrulanmış ilan; bozuk internet/boş yanıt/tekrar/eski veri testleri; kaynak ve çekim zamanı raporu; `cline/C1` PR ve yeşil CI.

**E1-Veri — sahip: cline**
- Oluşturulacak dosyalar: `market/sources.json`, `worker/src/market-data.ts`, `.github/workflows/market-data.yml`, `worker/test/market-data.test.ts`.
- İş: Ücretsiz altın/BIST/kur veri kaynaklarını güncellik ve erişim bakımından ölç; zaman damgalı JSON, yedek kaynak ve eski veri uyarısı üret. Anahtarları koda koyma; gerekiyorsa GitHub Secrets.
- Bitti ölçütü: Kaynak başına son güncelleme/yaş/hata, geçersiz-eksik-eskimiş veri ve zaman aşımı regresyon testleri; `cline/E1` PR ve yeşil CI.

## Cline PR hakem akışı
- Cline işe yalnız `DEVAM: <iş-id>` mesajıyla başlar ve `cline/<iş-id>` dalında ayrı PR açar.
- PR açıklamasında: değişenler, çalıştırılan testler, gerçek test sonucu ve telefonda denenecekler bulunmalı. Eksikse onay/merge yok.
- ChatGPT PR'ı sıfırdan inceler; sayı/kaynak iddialarını yeniden ölçer. QA yeşil ve şartlar tam ise merge yetkisi kontrol edilir; yetki yoksa `ONAYLANDI` yorumu bırakılır ve Cem'den merge istenir.
- `sahip: cline` işin kaynak koduna veya Cline dalına ChatGPT müdahale etmez.

## K1 geçmiş kanıtı (legacy PR #24; Cline'ın yeni işi sayılmaz)
- Eski artifact be740a3: 20 kaynak, 12 probe, 6 çalışan okuyucu (3 detay + 3 indeks), 3 açık ilan detay kaynağı, 2 program okuyucu (3 program bağlantısı).
- Eski PR dosyaları: `.github/workflows/career-source-audit.yml`, `career/sources.json`, `docs/KARIYER_KAYNAK_DENETIMI.md`, `scripts/career_source_audit.py`, `scripts/test_career_source_audit.py`.
- Bu değerler yalnız geçmiş artifact'teki ölçümdür; Cline K1'in yeni denetimiyle tekrar doğrulanmadan güncel sayılmaz.

## Son 10 kayıt
1. TGA detay okuyucusu eklendi; okuyucu/indeks sayıları ayrıştırıldı.
2. K1 legacy PR #24: 20 kaynak matrisi; artık Cline K1 denetimine devredildi, PR'a müdahale yok.
3. A1 iki ekran tasarımı Cem tarafından onaylandı.
4. A2 PR #25 ilk CI NPE; ikinci deneme test+APK SUCCESS, kök neden belirsiz.
5. B1 PR #26 bellek/streaming değişiklikleri; CI yeşil, bağımsız inceleme bekliyor.
6. A2 tanılama PR #27 workflow-only; PR/push CI SUCCESS.
7. A2 PR #28 açık/koyu mod, yerel tercih, dashboard özeti ve skeleton; PR/push CI SUCCESS.
8. Cline sahiplik kuralı eklendi: K1/C1/E1-Veri Cline; A1/A2/B1 ve kalan uygulama işleri ChatGPT.
9. C2 PR #22 kategori filtreleri + Haber/Süreç/Program ayrımı; Full App QA/API29-35 ve APK CI SUCCESS, PR açık.
10. C3 DEVAM: kaynak metninden yapılandırılmış alan çıkarımı ve her alan için kanıt/provenans; Cline işlerine dokunulmayacak.
**SONRAKİ ADIM:** ChatGPT `is/c3-ilan-semasi-ai-kaynak-kaniti` dalında C3 kaynak metni → şema/provenans/AI doğrulaması ve kullanıcı tetikli yenilemeyi uygular; Cline `DEVAM: K1` diyene kadar Cline'ın sunucu/veri dosyalarına dokunulmaz.
