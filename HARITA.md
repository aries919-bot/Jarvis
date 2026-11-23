# HARITA — Kaynak kodu yönlendirmesi

> Bu dosya `sh harita.sh` ile üretilir. Kaynak kodun tamamını kopyalamaz; doğru dosyaya ve bölüme yönlendirir.
> Bir değişiklikten önce ilgili dosyada `grep -n` ile sınıf/fonksiyon yerini bul ve yalnız gerekli satır aralığını oku.

## Uygulama çekirdeği

- `app/src/main/java/com/cem/jarvis/MainActivity.kt` — Ana ekran, uygulama başlangıcı, ana gezinme ve genel ekran yönlendirmeleri.
- `app/src/main/java/com/cem/jarvis/Views.kt` — Uygulama genelinde kullanılan ortak görünüm ve arayüz yardımcıları.
- `app/src/main/java/com/cem/jarvis/Ai.kt` — Yapay zekâ bağlantıları ve model istemcileri; gizli Worker adresleri/anahtarlar içerir, dış modele ham hâliyle verilmez.
- `app/src/main/java/com/cem/jarvis/Commands.kt` — Metin/ses komutlarının ayrıştırılması ve komut yönlendirmesi.
- `app/src/main/java/com/cem/jarvis/Util.kt` — Genel yardımcı işlevler ve ortak uygulama altyapısı.
- `app/src/main/java/com/cem/jarvis/Store.kt` — Uygulama ayarları ve kalıcı yerel veri saklama işlemleri.
- `app/src/main/java/com/cem/jarvis/Notif.kt` — Android bildirim kanalları ve bildirim üretimi.
- `app/src/main/java/com/cem/jarvis/Reminders.kt` — Hatırlatmalar, görevler ve zamanlanmış kullanıcı bildirimleri.

## İş ve Kariyer

- `app/src/main/java/com/cem/jarvis/CareerActivity.kt` — İş ve Kariyer ekranı, sekmeler, ilan/başvuru/takip/sonuç/haber arayüzü.
- `app/src/main/java/com/cem/jarvis/CareerMonitor.kt` — Kariyer kaynakları, arama akışları, aday toplama, izleme ve süreç bildirimleri.
- `app/src/main/java/com/cem/jarvis/CareerListingIntelligence.kt` — İlan kanıtı, son tarih, başvuru kanalı ve nitelik sinyallerinin analizi.
- `app/src/main/java/com/cem/jarvis/CareerContentClassifier.kt` — İş ilanı, haber, duyuru, kura, sonuç ve program türlerinin ayrıştırılması.
- `app/src/main/java/com/cem/jarvis/CareerEmployerResearcher.kt` — İşveren/şirket, kaynak ve ilan güvenilirliği araştırması.
- `app/src/main/java/com/cem/jarvis/CareerCadreRadar.kt` — Kamu alımları, kadro ve personel alımı radar verileri.

## Film, dizi ve medya

- `app/src/main/java/com/cem/jarvis/media/FilmDizi.kt` — Film, dizi ve anime keşif/listeleme ekranları.
- `app/src/main/java/com/cem/jarvis/media/FilmDiziActivity.kt` — Film ve Dizi bölümünün Activity bağlantısı.
- `app/src/main/java/com/cem/jarvis/media/Library.kt` — Medya kütüphanesi ve yerel medya modeli.
- `app/src/main/java/com/cem/jarvis/media/MediaSourceBrowser.kt` — Web medya kaynaklarını bulma, inceleme ve oynatıcıya yönlendirme.
- `app/src/main/java/com/cem/jarvis/media/PlayerControls.kt` — Medya oynatıcısının kontrolleri ve oynatma arayüzü.
- `app/src/main/java/com/cem/jarvis/media/SearchResults.kt` — Film/dizi arama sonuçlarının modellenmesi ve sunulması.
- `app/src/main/java/com/cem/jarvis/media/Web.kt` — Medya bölümündeki web sayfası ve tarayıcı yardımcıları.

## Birim testleri

- `app/src/test/java/com/cem/jarvis/CareerCadreRadarTest.kt`
- `app/src/test/java/com/cem/jarvis/CareerContentClassifierTest.kt`
- `app/src/test/java/com/cem/jarvis/CareerEmployerResearcherTest.kt`
- `app/src/test/java/com/cem/jarvis/CareerListingIntelligenceTest.kt`
- `app/src/test/java/com/cem/jarvis/CareerMonitorPolicyTest.kt`
- `app/src/test/java/com/cem/jarvis/CareerRecruitmentTrackerTest.kt`
- `app/src/test/java/com/cem/jarvis/CareerSearchParsingTest.kt`
- `app/src/test/java/com/cem/jarvis/CareerSearchProviderFallbackTest.kt`
- `app/src/test/java/com/cem/jarvis/CareerSourceCatalogTest.kt`

## Emülatör / enstrümantasyon testleri

- Henüz test dosyası yok.

## Derleme ve Android yapılandırması

- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle.properties`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/values/themes.xml`

## Otomatik kalite kontrol

- `.github/workflows/android.yml`
- `scripts/qa/test_repo_maintenance.py`

## Proje yönergeleri ve güvenlik belgeleri

- `AGENTS.md`
- `is.sh`
- `denetle.py`
- `harita.sh`
- `HARITA.md`
- `docs/JARVIS_GIZLILIK_ANALIZI.md`
- `docs/EK_BULGULAR.md`
