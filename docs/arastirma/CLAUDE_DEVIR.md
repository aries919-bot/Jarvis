# Claude devir kaydı — 25 Kasım 2026

Bu dosya, Claude'dan sonra çalışacak kişi/ajan için: ne yapıldı, nerede kalındı, ne yapılmadı.

## İstek
Cem: ChatGPT'ye verilecek bir geliştirme notu (benzer uygulamalar, siteler, açık kaynak; nasıl hızlı/kusursuz çalışıyorlar; ne kullanıyorlar; Aries'e uygula/geliştir) + Aries'in eksiklerinin net listesi + yazılanların GitHub'a kaydı. Alanlar: İş&Kariyer, Film, Borsa, Asistan çekirdeği.

## Yapılanlar
1. Cem'in yapıştırdığı önceki Claude notu (Bölüm 1–6, Borsa madde 9'da kesik) okundu. Eksik kalan Asistan çekirdeği, Keşif görevi ve kaynaklar tamamlandı.
2. Aries deposu bulundu: `aries919-bot/Jarvis` (Android, `com.cem.jarvis`). `main`, `durum` ve özellik dalları okundu.
3. `docs/HARITA.md`, `docs/TALIMAT.md`, `docs/NOT_C1.md`, `docs/EK_BULGULAR.md` okundu; çalışma düzenine (tek iş/dal/PR, NOT_C1 kapsamı) uyuldu.
4. Kod okundu: `AndroidManifest.xml`, `app/build.gradle.kts`, `Ai.kt`, `Store.kt`, `FinanceData.kt` (finance dalı), `FilmDizi.kt` (film-dizi-v2). Career ve media dizinlerinin dosya listeleri alındı.
5. Web araştırması (Asistan çekirdeği): Mem0, açık kaynak Android asistanlar, Gemini Nano/ML Kit Prompt API, AppFunctions, openWakeWord, Türkçe Whisper, BIST veri sağlayıcıları.
6. Çıktı: `docs/arastirma/CHATGPT_GELISTIRME_NOTU.md` (ChatGPT'ye yapıştırılacak not) + bu dosya.

## Okunmayanlar / doğrulanmayanlar
- `jarvis-cloud` (private): GitHub aracı 404 verdi; Worker/D1 yalnız HARITA/NOT_C1'den biliniyor.
- `CareerMonitor.kt` (124 KB), `CareerActivity.kt` (157 KB), `MainActivity.kt`, `Commands.kt`, `Reminders.kt`, `Views.kt`, `MediaSourceBrowser.kt` içerik olarak okunmadı; yalnız boyut/ad ve HARITA tanımı kullanıldı.
- Hiçbir şey derlenmedi/çalıştırılmadı; hiçbir özelliğin çalıştığı iddia edilmiyor.
- `scripts/` ve OpenCode ortamında litellm kullanımı kontrol edilmedi.
- Not Bölüm 5–7'deki rakip bulgularının kaynak URL'leri ilk taslaktan gelmiyor (kesik); ChatGPT teyit etmeli.

## Önemli bulgular (özet)
1. En büyük eksik entegrasyon: iş/borsa/film kodu `main`'de yok, 3 ayrı dalda.
2. Film dalında web sayfasından akış yakalayan oynatma yolu var; NOT_C1 madde 9 ve TALIMAT D1 ile çelişir.
3. Asistan: JSON-komut ayrıştırma, 1500 karakterlik bellek; akış, araç çağırma, wake word yok.
4. Güvenlik: public depoda Worker adresi, `/hizli` auth belirsiz, TMDB token APK'da, Worker günlükleri kapalı, `allowBackup=true`.
5. Borsa: Finnhub'ın BIST kapsamı belirsiz; KAP/TEFAS/EVDS/portföy yok.

## Karar bekleyen (Cem'e)
- NOT_C1 “aktif kapsam yalnız İş&Kariyer” diyor. Not, Film/Borsa/Asistan işlerini kuyruğa koydu. C1 eşiği geçilmeden bunlara geçilsin mi, yoksa paralel mi ilerlesin? Bu Cem'in kararıdır; Claude değiştirmedi.

## Sonraki adım
1. Cem notu ChatGPT'ye yapıştırır (“NOT BAŞLANGICI–NOT SONU” arası).
2. ChatGPT envanteri kendi okumasıyla doğrular; Bölüm 4 tablosunu düzeltir; HARITA'ya bu dosyalara bir satır bağlantı ekler (Claude HARITA'ya dokunmadı, ChatGPT'nin eşzamanlı commit'leriyle çakışmamak için).
3. İsteğe bağlı Claude turları: (a) `CareerMonitor`/`MediaSourceBrowser` okuyup ayrıntılı envanter, (b) entegrasyon planı (dal sırası + çakışma listesi), (c) borsa veri kaynaklarının kullanım şartı/erişim testi.

## Kural hatırlatma
`main`'e doğrudan yazma yok (bu dosyalar yalnız `durum` dalında). Sırlar yazılmaz: Worker adresi/token bu dosyalara kopyalanmadı. Kanıtsız “bitti” yok.

## 2. tur (25.11.2026 sabah)
- Cem rakiplerin arka plan mimarisini sordu (Aries neden geride kaldı, nasıl çalışıyorlar). Ayrı bir Claude sohbetindeki 7 araştırmadan "Asistan çekirdeği" başarısız olmuş ve o sohbette ücretsiz mesaj hakkı bitmişti; konu burada yeniden yapıldı.
- Çıktı: `docs/arastirma/RAKIP_ARKA_PLAN.md` (Ghostfolio, KAP sarmalayıcıları, Simplify/ApplyTalon, AIHawk, Jellyfin, öneri sistemleri, Home Assistant Assist, Pipecat + "neden geride kaldı" çıkarımları).
- Okunmayanlar: Teal/Huntr iç mimarisi, Fintables/Kariyer.net iç mimarisi (kapalı kaynak, yalnız dış açıklamalar), Plex/Stremio (kapsam dışı bırakıldı).
- Sonraki adım: ChatGPT bu dosyayı da okusun; öneri sistemi (TF-IDF) ve "niyet→araç→LLM yedek" sıralaması Bölüm 9 B grubuna eklenebilir.
