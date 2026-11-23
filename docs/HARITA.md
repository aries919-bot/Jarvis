# Aries — Durum Haritası

> Tek gerçek durum dosyası. Önce bu dosya ve docs/TALIMAT.md okunur.
> Son kontrol: 23.11.2026. Repo: `aries919-bot/Jarvis`.
> Bu dal yalnızca proje notları içindir; Android kodu burada geliştirilmez.

## Repo gerçeği
- Varsayılan `main`: `63e877d96cb910e6d98d56834ae55ae8edafab2a` (2026-11-21); bu SHA için PR tetiklemeli workflow run listesi boş.
- Notlar `durum` dalında; GitHub okuma/yazma erişimi doğrulandı.
- `main` üzerinde docs/HARITA.md ve docs/TALIMAT.md yok; doğru kopyalar `durum` dalında.
- Branch araması sayfalandırılarak tamamlandı; `cline/*` dalı/PR'ı görünmüyor. Cline `DEVAM: K1` demedi. K1 sahipliği Cline'da.
- K1 eski PR #24 ChatGPT koludur; Cline K1 tamamlayana kadar #24'e dokunma.
- A2 PR #25 açık/taslak, `is/a2-expressive-theme` → `main`; ikinci CI denemesi test+APK SUCCESS, ilk Gradle metadata NPE'nin kök nedeni bilinmiyor.
- A2 tanılama PR #27 açık/taslak, `fix/a2-gradle-metadata-stacktrace` → A2; workflow-only, PR/push CI SUCCESS.
- A2 devam PR #28 açık/taslak, `is/a2-theme-completion` → A2; PR #45180833170 ve push #45180819864 SUCCESS. Açık/koyu tercih, dashboard sayacı ve Film/Dizi skeleton eklendi; telefon testi/bağımsız inceleme bekliyor.
- B1 PR #26 açık/taslak, `is/b1-ai-memory-foundation` → A2; PR #45175633112 ve push #45175625717 SUCCESS. Canlı sağlayıcı ve telefon testi/bağımsız inceleme bekliyor.
- C2 PR #22 açık/taslak, `is/kariyer-1-21-verified-search` → `kariyer-modulu`; APK + Full App QA API29/33/34/35 SUCCESS. Main'e birleşmedi; telefon testi/bağımsız inceleme bekliyor.
- C3 ana PR #29 açık/taslak: `is/c3-ilan-semasi-ai-kaynak-kaniti` → `kariyer-modulu`; #29 için GitHub review kaydı yok. PR #30 açık/taslak ve mergeable: `fix/c3-ai-consent-and-navigation-guard` → PR #29 dalı.
- C3 PR #30 HEAD `5f6eebb816fadfa8e1c7c51b4ccce1234d1cee03`; APK #638 (run 45226233478) SUCCESS; Full App QA #84 (run 45226233411) SUCCESS.
- Full QA #84: statik denetim/Lint/birim test/APK ve Android 29, 33, 34, 35 genel UI audit işlerinin tümü SUCCESS. API35 summary: 20 ekran, 0 hata, 26 uyarı, 1 manuel okunabilirlik incelemesi.
- QA kapsam sınırı: Full App QA #84 C3 WebView kaynağını açmadı, ilan metni okumadı ve AI gönderme/iptal/fallback/CAPTCHA etkileşimini UI üzerinden test etmedi. Yeşil koşu genel uygulama smoke/UI denetimidir.
- C3 #30 diff bağımsız statik incelendi: AI gönderiminden önce ayrı açık onay; olası sağlayıcı sırası/fallback açıklaması; kaynak URL prompt dışında; CV/profil gönderilmiyor; HTTPS, aynı-host üst seviye gezinme ve subresource URL kontrolleri; CAPTCHA/login/403'te durma; boş/401 ve unsafe URL testleri.
- Sınır: DNS çözümlemesi özel IP'ye yönlendirilen public hostname riskini tamamen engellediği kanıtlanmadı; PR da tam ağ-seviyesi SSRF koruması iddia etmiyor. Kişisel veri maskesi eksiksiz değil; kullanıcıya açıklanıyor.
- C3 üç Resmî Gazete örneği bağımsız kontrol edildi: AFAD 13 kadro/P8-P9-P16-P21-P32-P43 ve P4/23–30 Kasım; TÜRKAK 3 ön lisans pozisyonu/P93≥70/23 Kasım–7 Aralık; TGA 13 pozisyon/3 Kasım–4 Aralık ve 4857 iş kanunu statüsü. Bunlar uygulamanın otomatik okuyucusunu/telefon WebView'ini kanıtlamaz.
- C3 #30 PR açıklaması güncel CI kanıtıyla düzeltildi. GitHub'a COMMENTED hakem notu bırakıldı (review id `6026649200`); APPROVE değil. Kayıt PR yazarıyla aynı `aries919-bot` hesabından geldiği için bağımsız insan incelemesi sayılmaz.
- C3 PR #29/#30 için gerçek cihaz WebView, gönderme iptali, onaylı sağlayıcı akışı, yönlendirme ve giriş/CAPTCHA davranışı henüz denenmedi. PR #29/#30 için ONAY/merge yok.
- E1 ikiye ayrıldı: E1-Veri Cline; E1-UI/Xiaomi ChatGPT. Aynı dosyada paralel değişiklik yok.
- Diğer açık/eskiden kalmış PR'lar: #23/#18/#17/#9/#4/#2/#1; farklı tabanlı eski PR'lar main'e birleşmiş sayılmaz.

## İş listesi ve sahiplik
| İş | Durum | Sahip / Not |
|---|---|---|
| K1 Kaynak matrisi ve canlı okuyucu denetimi | CLINE BEKLİYOR | sahip: cline; eski PR #24'e dokunma |
| A1 İki ekran taslağı | ONAYLANDI | sahip: chatgpt; ana ekran + İş & Kariyer Cem tarafından onaylandı |
| A2 Tüm uygulama tema sistemi | PR AÇIK | sahip: chatgpt; #25/#27/#28 CI yeşil; bağımsız inceleme ve telefon testi |
| B1 Streaming, bellek ve hata yönetimi | PR AÇIK | sahip: chatgpt; #26 CI yeşil; canlı API, bağımsız inceleme ve telefon testi |
| C1 Sunucu ilan toplayıcı + tekilleştirme JSON | CLINE BEKLİYOR | sahip: cline; K1 onayından sonra |
| C2 İlan listeleri, geçmiş, Haber/Duyuru ayrımı | PR AÇIK | sahip: chatgpt; #22 CI yeşil; bağımsız inceleme ve telefon testi |
| C3 İlan şeması, AI alan çıkarımı, kullanıcı tetikli tarayıcı yedeği | DEVAM | sahip: chatgpt; #29/#30 açık taslak; #30 APK #638 + Full QA #84 yeşil; gerçek cihaz ve bağımsız insan incelemesi bekliyor |
| C4 CV + KPSS tercih/uygunluk modülü | YAPILMADI | sahip: chatgpt; kişisel profil telefonda kalır |
| C5 Gelecek alım takvimi ve bildirim | YAPILMADI | sahip: chatgpt; resmî tarih ile tahmin ayrı |
| D1 Film & Dizi | YAPILMADI | sahip: chatgpt; yalnız yasal/izinli akış |
| E1-Veri Altın/Borsa kaynak ve JSON sunucusu | CLINE BEKLİYOR | sahip: cline; UI dosyalarını değiştirme |
| E1-UI Piyasa ekranı/grafik/alarmlar/Xiaomi yönlendirmesi | YAPILMADI | sahip: chatgpt; Cline veri sözleşmesi bekleniyor |
| Z1 Gerçek telefon QA | TELEFONDA DENENECEK | mikrofon, Xiaomi pil/otomatik başlatma, gerçek bildirim/alarm, canlı API, gerçek oynatma ve tema kalıcılığı |

## Cline'a ayrılmış net işler
**K1 — ilk iş / sahip: cline**
- Dosyalar: `.github/workflows/career-source-audit.yml`, `career/sources.json`, `docs/KARIYER_KAYNAK_DENETIMI.md`, `scripts/career_source_audit.py`, `scripts/test_career_source_audit.py`.
- Kaynak başına erişim/giriş/CAPTCHA, çekim zamanı, ilan sayısı ve hatayı ölç; yalnız açık ve girişsiz sayfaları otomasyon adayı yap.
- Bitiş: her kaynak gerçek sayfa denetimi; PR başına en az 3 ilan örneği veya yokluk nedeni; test sonucu; `cline/K1` PR ve yeşil CI.

**C1 — K1 onayından sonra / sahip: cline**
- Dosyalar: `worker/src/career-feed.ts`, `career/feeds.json`, `.github/workflows/career-feed.yml`, `worker/test/career-feed.test.ts`.
- Yalnız K1 doğrulamalı kaynaklardan günde iki kez ücretsiz Actions/Worker ile çek; normalize et, kurum+pozisyon+il ile tekilleştir, süresi dolanı geçmişe taşı, kaynak sağlığını JSON'a yaz.
- Bitiş: en az 3 resmî örnek; bozuk internet/boş yanıt/tekrar/eski veri testleri; zaman ve kaynak raporu; `cline/C1` PR ve yeşil CI.

**E1-Veri — sahip: cline**
- Dosyalar: `market/sources.json`, `worker/src/market-data.ts`, `.github/workflows/market-data.yml`, `worker/test/market-data.test.ts`.
- Ücretsiz altın/BIST/kur kaynaklarını erişim, lisans ve zaman damgasıyla ölç; zaman damgalı JSON, yedek kaynak ve eski veri uyarısı üret. Anahtarları koda koyma.
- Bitiş: kaynak başına son güncelleme/yaş/hata; bozuk/eksik/eskimiş veri ve timeout testleri; `cline/E1` PR ve yeşil CI.

## Cline PR hakem akışı
- Cline yalnız `DEVAM: <iş-id>` ile başlar, `cline/<iş-id>` dalında PR açar.
- PR açıklamasında değişiklik, test, gerçek test sonucu ve telefon denemeleri olmalı; eksikse onay/merge yok.
- ChatGPT PR'ı sıfırdan inceler; kaynak/sayı iddialarını tekrar ölçer. Yetki yoksa ONAY yorumundan sonra Cem'den merge istenir.
- `sahip: cline` işlerinin kaynak koduna ve dallarına ChatGPT müdahale etmez.

## Son 10 kayıt
1. TGA detay okuyucusu eklendi; reader ve indeks sayıları ayrıştırıldı.
2. K1 legacy PR #24 20 kaynak envanteri; güncel Cline K1 denetimi bekleniyor.
3. A1 iki ekran tasarımı onaylandı.
4. A2 #25 ilk CI NPE; ikinci deneme test+APK SUCCESS, kök neden bilinmiyor.
5. B1 #26 bellek/streaming; CI yeşil, bağımsız inceleme ve telefon testi bekliyor.
6. A2 #27 workflow-only tanılama; PR/push CI SUCCESS.
7. A2 #28 açık/koyu mod, dashboard sayacı ve skeleton; PR/push CI SUCCESS.
8. Cline sahiplik ayrımı: K1/C1/E1-Veri Cline; diğer uygulama işleri ChatGPT.
9. C2 #22 kategoriler ve Haber/Süreç/Program ayrımı; APK + API29/33/34/35 Full QA SUCCESS.
10. C3 #30 HEAD `5f6eebb`: APK #638 + Full QA #84 SUCCESS; PR açıklaması ve COMMENTED hakem notu güncellendi (onay değil); genel emülatör QA C3 WebView akışını kapsamıyor.
**SONRAKİ ADIM:** Cem gerçek telefonda C3 kontrol listesini çalıştırıp sonucu bildirsin: kaynak açma/metin okuma, AI gönderme iptali ve onayı, yanlış anahtar/boş yanıt, 403/CAPTCHA/login, farklı host/alt kaynak, pencere kapatma ve yeniden başlatma. Bunlar denenmeden ONAY/merge yok. Cline `DEVAM: K1` demeden K1/C1/E1-Veri dosyalarına dokunma.
