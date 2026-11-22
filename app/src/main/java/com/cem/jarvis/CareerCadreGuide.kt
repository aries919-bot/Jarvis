package com.cem.jarvis

/**
 * P94 cadre examples extracted from ÖSYM's 2026/1 middle-school table.
 * These are historical examples, not a forecast or promise for KPSS-2026/2.
 */
data class HistoricalP94Cadre(
    val title: String,
    val examples: String,
    val requirementCodes: String,
    val note: String,
    val searchTerm: String
)

object CareerCadreGuide {
    const val LAST_OFFICIAL_CHECK = "22.11.2026"
    const val KPSS_2026_2_START = "17 Aralık 2026"
    const val KPSS_2026_2_END = "24 Aralık 2026"
    const val KPSS_2026_1_TABLE_URL =
        "https://dokuman.osym.gov.tr/pdfdokuman/2026/KPSS/TERCIH1/tablo1_t1d03072026.pdf"
    const val KPSS_2026_1_QUALIFICATIONS_URL =
        "https://dokuman.osym.gov.tr/pdfdokuman/2026/KPSS/TERCIH1/ortaogretim-nitelik_t1d03072026.pdf"
    const val OSYM_CALENDAR_URL =
        "https://www.osym.gov.tr/Sayfa/SinavTakvimi/tr-TR"
    const val OSYM_2026_1_PAGE_URL =
        "https://www.osym.gov.tr/kpss20261-bazi-kamu-kurum-ve-kuruluslarinin-kadro-ve-pozisyonlarina-yerlestirme-yapmak-icin-tercih-kilavuzu-ve-tablolar"

    val historicalP94Cadres = listOf(
        HistoricalP94Cadre(
            title = "Teknisyen · elektrik-elektronik",
            examples = "2026/1 tablosunda Gelir İdaresi, Hazine ve Maliye, SGK, TMO ve başka kurumlarda örnekleri vardı.",
            requirementCodes = "2023",
            note = "Elektrik-Elektronik Teknolojisi veya kılavuzda tek tek belirtilen Endüstriyel Otomasyon dalları. Sadece 'elektrik' kelimesi yeterli değildir.",
            searchTerm = "Elektrik teknisyeni"
        ),
        HistoricalP94Cadre(
            title = "Teknisyen · makine / metal / tesisat",
            examples = "2026/1 tablosunda makine, metal ve tesisat-iklimlendirme alan kodlarıyla teknisyen kadroları vardı.",
            requirementCodes = "2061 · 2065 · 2111",
            note = "Bu kodlar farklı lise alanlarını ifade eder; elektrik alanıyla aynı kabul edilmemelidir.",
            searchTerm = "Teknisyen makine metal tesisat"
        ),
        HistoricalP94Cadre(
            title = "Teknisyen · bilişim ve diğer teknik alanlar",
            examples = "2026/1 tablosunda bilişim, meteoroloji ve farklı teknik alanlarla açılmış teknisyen pozisyonları da vardı.",
            requirementCodes = "2011 · 2069 · diğer kodlar",
            note = "Her pozisyonun alan/dal kodu ve varsa özel koşulları ayrı kontrol edilmelidir.",
            searchTerm = "Teknisyen bilişim meteoroloji"
        ),
        HistoricalP94Cadre(
            title = "Teknisyen / vagon teknisyeni · raylı sistemler",
            examples = "2026/1'de TCDD Taşımacılık A.Ş. farklı illerde teknisyen ve vagon teknisyeni pozisyonları açtı.",
            requirementCodes = "2086",
            note = "Raylı Sistemler Teknolojisi alanının ilgili makine, elektrik-elektronik, mekatronik veya araç dalı istenir; genel elektrik diploması otomatik olarak yeterli sayılmaz.",
            searchTerm = "Raylı sistemler teknisyeni vagon teknisyeni"
        ),
        HistoricalP94Cadre(
            title = "Veteriner sağlık teknisyeni",
            examples = "2026/1'de Tarım İşletmeleri Genel Müdürlüğü ve Türkiye İlaç ve Tıbbi Cihaz Kurumunda örnekleri vardı.",
            requirementCodes = "2047",
            note = "Veteriner sağlık / hayvan sağlığı alanı ve ilgili dal şartı aranır.",
            searchTerm = "Veteriner sağlık teknisyeni"
        ),
        HistoricalP94Cadre(
            title = "Destek personeli",
            examples = "2026/1'de SGK, Meteoroloji, üniversiteler, Strateji ve Bütçe Başkanlığı ve Ulaştırma ve Altyapı Bakanlığında örnekleri vardı.",
            requirementCodes = "İlana göre değişir",
            note = "Bazı ilanlar 2001 isterken bazıları alan, temizlik, bakım, vardiya veya başka özel koşullar içerir. Başlık tek başına uygunluk göstermez.",
            searchTerm = "Destek personeli"
        ),
        HistoricalP94Cadre(
            title = "Aşçı",
            examples = "2026/1'de Gelir İdaresi Başkanlığında Elazığ, Erzincan ve Sakarya örnekleri vardı.",
            requirementCodes = "2117",
            note = "Yiyecek-İçecek Hizmetleri veya kılavuzda sayılan ilişkili alan/dallar aranır.",
            searchTerm = "Aşçı"
        ),
        HistoricalP94Cadre(
            title = "Hizmetli · bahçıvan · dağıtıcı",
            examples = "2026/1'de Hazine ve Maliye ile Milli Güvenlik Kurulu gibi kurumlarda örnek unvanlar vardı.",
            requirementCodes = "2001 veya ilandaki ek koşullar",
            note = "2001 herhangi bir ortaöğretim alanından mezuniyeti ifade eder; ek özel koşullar ayrıca kontrol edilir.",
            searchTerm = "Hizmetli bahçıvan dağıtıcı"
        ),
        HistoricalP94Cadre(
            title = "Şoför",
            examples = "2026/1'de Hazine ve Maliye Bakanlığı ve Türk Tarih Kurumu örnekleri vardı.",
            requirementCodes = "2001 + sürücü belgesi/özel koşul kodları",
            note = "Ehliyet sınıfı, deneyim, SRC/psikoteknik ve diğer şartlar ilan bazında değişebilir.",
            searchTerm = "Şoför"
        ),
        HistoricalP94Cadre(
            title = "Koruma ve güvenlik görevlisi",
            examples = "2026/1'de üniversite ve kamu kurumu örnekleri vardı.",
            requirementCodes = "İlanda yer alan güvenlik ve diğer özel koşul kodları",
            note = "Özel güvenlik kimlik kartı, geçerlilik, boy/kilo ve diğer şartlar kılavuz/ilan üzerinden doğrulanmalıdır.",
            searchTerm = "Koruma güvenlik görevlisi"
        ),
        HistoricalP94Cadre(
            title = "Bekçi",
            examples = "2026/1 ortaöğretim tablosunda Çankırı İl Özel İdaresi örneği vardı.",
            requirementCodes = "2001",
            note = "Bu yalnızca 2026/1 tarihli örnektir; yeni dönemde aynı kurum/unvan açılacağı anlamına gelmez.",
            searchTerm = "Bekçi"
        ),
        HistoricalP94Cadre(
            title = "Sağlık memuru",
            examples = "2026/1'de Türkiye Taşkömürü Kurumunda örnek pozisyon vardı.",
            requirementCodes = "2087",
            note = "Sağlık alanı ve dal şartı aranır; elektrik alanı için uygun kadro değildir.",
            searchTerm = "Sağlık memuru"
        )
    )

    fun qualificationCodeExplanation(code: Int): String? = when (code) {
        2001 -> "Herhangi bir ortaöğretim alanından mezuniyet."
        2011 -> "Bilişim Teknolojileri alanı ve dallarından mezuniyet."
        2023 -> "Elektrik-Elektronik Teknolojisi alanı/dalları veya kılavuzda sayılan Endüstriyel Otomasyon Elektronik/Kontrol dalları."
        2047 -> "Hayvan Sağlığı / Hayvan Yetiştiriciliği ve Sağlığı alanlarında belirtilen veteriner sağlık dalları."
        2061 -> "Makine Teknolojisi veya Makine ve Tasarım Teknolojisi alanı ve dalları."
        2065 -> "Metal Teknolojisi alanı ve dalları."
        2069 -> "Meteoroloji alanı; ayrıca kılavuzda sayılan Meteoroloji Meslek Lisesi bilişim/elektronik dalları."
        2085 -> "Raylı Sistemler Teknolojisi alanı ve dalları."
        2086 -> "Raylı Sistemler Teknolojisi alanında Makine, Elektrik-Elektronik, Mekatronik veya Raylı Sistem Araçları dalı."
        2087 -> "Acil Sağlık Hizmetleri alanı – Acil Tıp Teknisyenliği dalı."
        2111 -> "Tesisat Teknolojisi ve İklimlendirme alanı ve dalları."
        2112 -> "Tesisat Teknolojisi alanı ve dalları."
        2117 -> "Yiyecek-İçecek Hizmetleri veya kılavuzda sayılan ilişkili alan/dallar."
        2119 -> "Yenilenebilir Enerji Teknolojileri alanı ve dalları."
        else -> null
    }

    fun extractQualificationCodes(text: String): Set<Int> =
        Regex("(?<!\\d)(20\\d{2}|21\\d{2})(?!\\d)")
            .findAll(text)
            .map { it.value.toInt() }
            .toSet()

    fun explainQualificationCodes(codes: Set<Int>): String {
        if (codes.isEmpty()) {
            return "Nitelik kodu başlık/özetten çıkarılamadı. İlanın tam metni ve güncel ÖSYM kılavuzu kontrol edilmeli."
        }
        return codes.sorted().joinToString("\n") { code ->
            "$code · " + (qualificationCodeExplanation(code)
                ?: "Bu kodun güncel kılavuzdaki açıklamasını ve ilandaki özel koşulları ayrıca kontrol et.")
        }
    }
}
