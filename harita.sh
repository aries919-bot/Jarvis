#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$ROOT"

python3 - <<'PY'
from pathlib import Path

DESCRIPTIONS = {
    "app/src/main/java/com/cem/jarvis/MainActivity.kt": "Ana ekran, uygulama başlangıcı, ana gezinme ve genel ekran yönlendirmeleri.",
    "app/src/main/java/com/cem/jarvis/Views.kt": "Uygulama genelinde kullanılan ortak görünüm ve arayüz yardımcıları.",
    "app/src/main/java/com/cem/jarvis/Ai.kt": "Yapay zekâ bağlantıları ve model istemcileri; gizli Worker adresleri/anahtarlar içerir, dış modele ham hâliyle verilmez.",
    "app/src/main/java/com/cem/jarvis/Commands.kt": "Metin/ses komutlarının ayrıştırılması ve komut yönlendirmesi.",
    "app/src/main/java/com/cem/jarvis/Util.kt": "Genel yardımcı işlevler ve ortak uygulama altyapısı.",
    "app/src/main/java/com/cem/jarvis/Store.kt": "Uygulama ayarları ve kalıcı yerel veri saklama işlemleri.",
    "app/src/main/java/com/cem/jarvis/Notif.kt": "Android bildirim kanalları ve bildirim üretimi.",
    "app/src/main/java/com/cem/jarvis/Reminders.kt": "Hatırlatmalar, görevler ve zamanlanmış kullanıcı bildirimleri.",
    "app/src/main/java/com/cem/jarvis/CareerActivity.kt": "İş ve Kariyer ekranı, sekmeler, ilan/başvuru/takip/sonuç/haber arayüzü.",
    "app/src/main/java/com/cem/jarvis/CareerMonitor.kt": "Kariyer kaynakları, arama akışları, aday toplama, izleme ve süreç bildirimleri.",
    "app/src/main/java/com/cem/jarvis/CareerListingIntelligence.kt": "İlan kanıtı, son tarih, başvuru kanalı ve nitelik sinyallerinin analizi.",
    "app/src/main/java/com/cem/jarvis/CareerContentClassifier.kt": "İş ilanı, haber, duyuru, kura, sonuç ve program türlerinin ayrıştırılması.",
    "app/src/main/java/com/cem/jarvis/CareerEmployerResearcher.kt": "İşveren/şirket, kaynak ve ilan güvenilirliği araştırması.",
    "app/src/main/java/com/cem/jarvis/CareerCadreRadar.kt": "Kamu alımları, kadro ve personel alımı radar verileri.",
    "app/src/main/java/com/cem/jarvis/media/FilmDizi.kt": "Film, dizi ve anime keşif/listeleme ekranları.",
    "app/src/main/java/com/cem/jarvis/media/FilmDiziActivity.kt": "Film ve Dizi bölümünün Activity bağlantısı.",
    "app/src/main/java/com/cem/jarvis/media/Library.kt": "Medya kütüphanesi ve yerel medya modeli.",
    "app/src/main/java/com/cem/jarvis/media/MediaSourceBrowser.kt": "Web medya kaynaklarını bulma, inceleme ve oynatıcıya yönlendirme.",
    "app/src/main/java/com/cem/jarvis/media/PlayerControls.kt": "Medya oynatıcısının kontrolleri ve oynatma arayüzü.",
    "app/src/main/java/com/cem/jarvis/media/SearchResults.kt": "Film/dizi arama sonuçlarının modellenmesi ve sunulması.",
    "app/src/main/java/com/cem/jarvis/media/Web.kt": "Medya bölümündeki web sayfası ve tarayıcı yardımcıları.",
}

GROUPS = [
    ("Uygulama çekirdeği", [
        "app/src/main/java/com/cem/jarvis/MainActivity.kt",
        "app/src/main/java/com/cem/jarvis/Views.kt",
        "app/src/main/java/com/cem/jarvis/Ai.kt",
        "app/src/main/java/com/cem/jarvis/Commands.kt",
        "app/src/main/java/com/cem/jarvis/Util.kt",
        "app/src/main/java/com/cem/jarvis/Store.kt",
        "app/src/main/java/com/cem/jarvis/Notif.kt",
        "app/src/main/java/com/cem/jarvis/Reminders.kt",
    ]),
    ("İş ve Kariyer", [
        "app/src/main/java/com/cem/jarvis/CareerActivity.kt",
        "app/src/main/java/com/cem/jarvis/CareerMonitor.kt",
        "app/src/main/java/com/cem/jarvis/CareerListingIntelligence.kt",
        "app/src/main/java/com/cem/jarvis/CareerContentClassifier.kt",
        "app/src/main/java/com/cem/jarvis/CareerEmployerResearcher.kt",
        "app/src/main/java/com/cem/jarvis/CareerCadreRadar.kt",
    ]),
    ("Film, dizi ve medya", [
        "app/src/main/java/com/cem/jarvis/media/FilmDizi.kt",
        "app/src/main/java/com/cem/jarvis/media/FilmDiziActivity.kt",
        "app/src/main/java/com/cem/jarvis/media/Library.kt",
        "app/src/main/java/com/cem/jarvis/media/MediaSourceBrowser.kt",
        "app/src/main/java/com/cem/jarvis/media/PlayerControls.kt",
        "app/src/main/java/com/cem/jarvis/media/SearchResults.kt",
        "app/src/main/java/com/cem/jarvis/media/Web.kt",
    ]),
]

lines = [
    "# HARITA — Kaynak kodu yönlendirmesi",
    "",
    "> Bu dosya `sh harita.sh` ile üretilir. Kaynak kodun tamamını kopyalamaz; doğru dosyaya ve bölüme yönlendirir.",
    "> Bir değişiklikten önce ilgili dosyada `grep -n` ile sınıf/fonksiyon yerini bul ve yalnız gerekli satır aralığını oku.",
    "",
]
used = set()
for heading, files in GROUPS:
    lines.extend([f"## {heading}", ""])
    for name in files:
        path = Path(name)
        if not path.is_file():
            continue
        used.add(name)
        lines.append(f"- `{name}` — {DESCRIPTIONS.get(name, 'Kotlin uygulama kaynağı.')}")
    lines.append("")

main_root = Path("app/src/main/java")
extras = sorted(str(p) for p in main_root.rglob("*.kt") if str(p) not in used) if main_root.exists() else []
if extras:
    lines.extend(["## Diğer üretim Kotlin dosyaları", ""])
    for name in extras:
        lines.append(f"- `{name}` — Ek Kotlin uygulama kaynağı; ihtiyaç duyulan sınıfı `grep -n` ile bul.")
    lines.append("")

for heading, folder in (("Birim testleri", Path("app/src/test")), ("Emülatör / enstrümantasyon testleri", Path("app/src/androidTest"))):
    files = sorted(str(p) for p in folder.rglob("*.kt")) if folder.exists() else []
    lines.extend([f"## {heading}", ""])
    if files:
        lines.extend(f"- `{name}`" for name in files)
    else:
        lines.append("- Henüz test dosyası yok.")
    lines.append("")

for heading, files in (
    ("Derleme ve Android yapılandırması", ["build.gradle.kts", "settings.gradle.kts", "gradle.properties", "app/build.gradle.kts", "app/src/main/AndroidManifest.xml", "app/src/main/res/values/themes.xml"]),
    ("Otomatik kalite kontrol", [".github/workflows/android.yml", ".github/workflows/aries-full-app-qa.yml", "qa/README.md", "scripts/qa/build_qa_report.py", "scripts/qa/test_repo_maintenance.py"]),
    ("Proje yönergeleri ve güvenlik belgeleri", ["AGENTS.md", "is.sh", "denetle.py", "docs/JARVIS_GIZLILIK_ANALIZI.md", "docs/EK_BULGULAR.md"]),
):
    lines.extend([f"## {heading}", ""])
    for name in files:
        if Path(name).is_file():
            lines.append(f"- `{name}`")
    lines.append("")

Path("HARITA.md").write_text("\n".join(lines).rstrip() + "\n", encoding="utf-8")
print(f"HARITA.md guncellendi ({len(used) + len(extras)} kaynak Kotlin dosyasi tarandi).")
PY
