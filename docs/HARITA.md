# Aries — Durum Haritası

> Tek gerçek durum dosyası. Sohbet geçmişinden önce bu dosya okunur.
> Son yeniden kurma: 23.11.2026. Repo: `aries919-bot/Jarvis`.
> Bu dal yalnızca proje notları içindir; Android kodu burada geliştirilmez.

## Repo gerçeği
- Varsayılan `main`: `63e877d96cb910e6d98d56834ae55ae8edafab2a`.
- `durum` dalı main'den oluşturuldu; `docs/HARITA.md` ve `docs/TALIMAT.md` ilk kez burada tutuluyor.
- Main'de bu iki dosya yoktu. PR #23'teki kök `HARITA.md` yalnızca kod dosyası yönlendirme haritasıdır; bu durum dosyasının yerine geçmez ve PR #23 `kariyer-modulu` tabanlıdır.
- Açık taslak PR'lar: #24 K1 (main tabanlı); #23 kod haritası, #22 Aries 1.22, #18 tema, #17 QA laboratuvarı, #9 KPSS kadro radarı, #4 medya, #3 kariyer, #2/#1 medya (çoğu `kariyer-modulu` tabanlı). Base'i farklı PR'lar main'e alınmış sayılmaz.
- Son kontrol: PR #24 açık/taslak, head `297a60969bc40387c55243c880c11483a266cf32`, base `main`, mergeable. Son QA 45160335723 ve 45160326703: ikisi de SUCCESS.
- PR #22 head `865ab4ec52bcdafb372ddab3e079e199d8742ac9`, #23 `b3e9982906967a10ddae29366f76674377a81c4a`, #18 `edf1434e6af8ffc8e70c46b9f099dae06c4099c8`, #17 `3deb19a36227761527858576654012776c9ae604`. Bunlar açık/taslak ve `kariyer-modulu` tabanlı; K1 PR'ına karıştırılmaz.

## K1 — İş & Kariyer kaynak envanteri
- Durum: **PR AÇIK** (CI YEŞİL; hakem onayı bekliyor). Dal: `is/kariyer-kaynak-denetimi-1`; PR #24.
- Son commit: `297a60969bc40387c55243c880c11483a266cf32`.
- Dosyalar: `career/sources.json`, `scripts/career_source_audit.py`, `scripts/test_career_source_audit.py`, `.github/workflows/career-source-audit.yml`, `docs/KARIYER_KAYNAK_DENETIMI.md`.
- Canlı rapor (23.11.2026): 20 kayıt; 14 probed; **5 çalışan okuyucu** = 3 HTML detay + 2 HTML indeks; **3 aktif açık ilan detay kaynağı**.
- Detay kaynakları: Sayıştay (11–25.11.2026), Anadolu Üniversitesi (16–30.11.2026), TGA (03.11–04.12.2026). En az 3 ilan kaynak sayfasından elle doğrulandı; ayrıca Zafer Kalkınma Ajansı (25.11–14.12.2026, duyurulmuş ama henüz açılmamış) tarayıcıda doğrulandı.
- İndeks okuyucuları ÇSGB ve Özal Üniversitesi; geçmiş/sonuç kayıtları içerebildiği için açık ilan sayısına dahil edilmez.
- Kısıtlar: Kariyer Kapısı ve İŞKUR dinamik; e-Devlet oturumlu; Resmî Gazete PDF okuyucu bekliyor; ilan.gov.tr ve İnönü SSL sertifika hatası; Fırat timeout; Zafer ve Battalgazi güvenlik/CAPTCHA işareti verdi ve otomasyon durdu; aşma yok.
- CI: [45160335723](https://github.com/aries919-bot/Jarvis/actions/runs/45160335723) ve [45160326703](https://github.com/aries919-bot/Jarvis/actions/runs/45160326703), SUCCESS. Artifact: `aries-career-source-status`.
- Hakem notu: Patch bu asistan tarafından yazıldı. Kendi yamamı APPROVE/ONAYLANDI yapamam. PR #24 bağımsız inceleme bekliyor. Kaynak sayısı API/toplayıcı üretimde çalışıyor demek değildir; yalnızca denetim probe'u doğrulandı.

## İş listesi
| İş | Durum | Not |
|---|---|---|
| K1 Kaynak tablosu ve okuyucu sağlığı | PR AÇIK | 20 kaynak; 5 reader; bağımsız hakem bekliyor |
| A1 Tema: 2 ekran taslağı | YAPILMADI | Koda geçmeden Cem'e İş & Kariyer + Ana ekran taslağı sun |
| A2 Material 3 Expressive tüm uygulama | YAPILMADI | A1 seçimi sonrası; eski PR #18 ayrı incelenecek |
| B1 Yapay zekâ: streaming, bellek, ortak ses/yazı beyni, net hata | YAPILMADI | Ücretsiz; güvenli onaylı eylemler |
| C1 Sunucu tarafı İş & Kariyer toplayıcı + JSON | YAPILMADI | K1 hakem kararı sonrası; ücretsiz Actions/Worker, arama motoru kazıma yok |
| C2 Ayrı listeler, geçmiş, Haber/Duyuru ayrımı | YAPILMADI | KPSS'li/kpss'siz kamu, bağlı kurum, belediye, özel, büyük şirket, staj, kura |
| C3 İlan şeması, AI alan çıkarımı, kullanıcı tetikli tarayıcı yedeği | YAPILMADI | Kaynak/AI/tarayıcı provenansı; son tarih ve puan çapraz kontrol |
| C4 CV + KPSS tercih modülü | YAPILMADI | Profil ve nitelik kodları telefonda kalır |
| C5 Gelecek alım, takvim ve bildirim | YAPILMADI | Resmî tarih / tahmin ayrı |
| D1 Film & Dizi | YAPILMADI | Yasal kaynak; TMDB JustWatch atfı; Media3 ve sezon/bölüm |
| E1 Altın/Borsa + Xiaomi | YAPILMADI | PR #22'de kısmi Finance geliştirmesi var, ana dala alınmış değil |
| Z1 Gerçek telefon testleri | TELEFONDA DENENECEK | Mikrofon, Xiaomi pil, exact alarm/bildirim, canlı API, gerçek video |

## En son 10 kayıt
1. K1: 20 kaynaklık liste ve ücretsiz kaynak sağlık probe'u eklendi.
2. QA ilk turda T.C. normalizasyon hash hatasını buldu; düzeltildi.
3. QA Türkçe ay adlı tarih penceresini buldu; düzeltildi ve regresyon testi eklendi.
4. TGA doğrudan detay okuyucu olarak eklendi.
5. Rapor 5 okuyucu / 3 aktif detay ilan kaynağı olarak ayrıştırıldı.
6. K1 PR #24 açıldı; CI son committe yeşil.
7. Hiçbir PR bu turda merge edilmedi veya bağımsız onay almadı.
8. Mevcut mobil kariyer taraması hâlâ Worker endpoint + cihazda arama motoru HTML fallback'ine sahip; C1 bunu kaldıracak.
9. Önceki açık PR #22/#23/#18/#17 farklı base dalında; otomatik olarak main'e taşınmayacak.
10. Sonraki iş, K1 için bağımsız hakem incelemesi; ardından C1 sunucu toplayıcı.

**SONRAKİ ADIM:** PR #24'ü yamayı yazmamış bağımsız bir gözle incelet; sonuç ONAYLANDI değilse düzelt. Sonra C1 için ayrı dal/PR aç.
