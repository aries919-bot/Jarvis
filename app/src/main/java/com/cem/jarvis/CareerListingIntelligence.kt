package com.cem.jarvis

import java.net.URI
import java.util.Locale

/**
 * Facts are extracted from the search title/snippet only. They are deliberately not
 * presented as proof that an application is currently open or that a person is eligible.
 */
data class CareerListingFacts(
    val employerGroup: String,
    val applicationChannel: String,
    val noticeType: String,
    val applicationStatus: String,
    val explicitDeadline: String?,
    val quotaText: String?,
    val requirementSignals: List<String>
)

object CareerListingIntelligence {
    private fun normalize(value: String): String = value.lowercase(Locale("tr", "TR"))
        .replace('ı', 'i').replace('ş', 's').replace('ğ', 'g')
        .replace('ü', 'u').replace('ö', 'o').replace('ç', 'c')
        .replace(Regex("""\s+"""), " ")
        .trim()

    fun analyze(title: String, snippet: String, url: String, nowMillis: Long = System.currentTimeMillis()): CareerListingFacts {
        val text = normalize("$title $snippet")
        val host = runCatching { URI(url).host.orEmpty().lowercase(Locale.ROOT).removePrefix("www.") }
            .getOrDefault("")
        val employerGroup = CareerSourceCatalog.recruitmentGroup(title, snippet, url)
        val stage = CareerRecruitmentTracker.detectStage(title, snippet)
        val deadlineSource = "$title $snippet"
        val deadline = CareerListingParser.deadlineDate(deadlineSource)
        val deadlineStatus = CareerListingParser.deadlineStatus(deadlineSource, nowMillis)
        val processOnly = !stage.isNullOrBlank() && stage != "BAŞVURU / YENİ ALIM"
        val noticeType = when {
            processOnly -> "İşe alım süreci / sonuç duyurusu"
            stage == "BAŞVURU / YENİ ALIM" -> "Yeni alım / başvuru duyurusu"
            CareerSourceCatalog.isLikelyJobResult(title, snippet) -> "İlan adayı"
            else -> "Kariyer veya kurum duyurusu"
        }
        val applicationStatus = when {
            deadlineStatus?.startsWith("SÜRESİ GEÇMİŞ", true) == true ->
                "SÜRESİ GEÇMİŞ · Kaynak özetindeki son tarih geçmiş; kurum sayfasında güncelleme kontrol edilmeli."
            processOnly -> "SÜREÇ DUYURUSU · Bu kayıt yeni başvuru açıldığını göstermez."
            deadline != null ->
                "SON TARİH BULUNDU · $deadline · Başvurunun hâlâ açık olduğu kaynak sayfasında teyit edilmeli."
            stage == "BAŞVURU / YENİ ALIM" || CareerSourceCatalog.isLikelyJobResult(title, snippet) ->
                "BAŞVURU DURUMU BİLİNMİYOR · Arama özeti aktif başvuruyu kanıtlamaz."
            else -> "AKTİFLİK BİLİNMİYOR · Kaynak sayfasını kontrol et."
        }
        val applicationChannel = when {
            host == "osym.gov.tr" || host.endsWith(".osym.gov.tr") ->
                "ÖSYM · tercih / sınav işlemleri"
            host == "kariyerkapisi.gov.tr" || host.endsWith(".kariyerkapisi.gov.tr") ||
                host == "isealimkariyerkapisi.cbiko.gov.tr" || host.endsWith(".cbiko.gov.tr") ->
                "Kariyer Kapısı · e-Devlet ile başvuru"
            host == "turkiye.gov.tr" || host.endsWith(".turkiye.gov.tr") ->
                "e-Devlet hizmeti · ilgili başvuru ekranını aç"
            host == "iskur.gov.tr" || host.endsWith(".iskur.gov.tr") ->
                "İŞKUR e-Şube · açık iş / başvuru"
            CareerSourceCatalog.isSecondary(url) ->
                "İlan platformu · işveren ve başvuru adresi doğrulanmalı"
            employerGroup.startsWith("KİT /") || employerGroup.startsWith("Savunma sanayii /") ->
                "İşverenin kendi kariyer/başvuru portalı"
            host.endsWith(".gov.tr") || host.endsWith(".bel.tr") || host.endsWith(".edu.tr") ->
                "Kurumun kendi duyurusu · başvuru yöntemi ilanda belirtilir"
            CareerSourceCatalog.categoryFor(url).contains("İşveren") ||
                CareerSourceCatalog.categoryFor(url).contains("Özel sektör") ->
                "İşverenin kendi kariyer portalı"
            else -> "Başvuru kanalı kaynakta doğrulanmalı"
        }
        val quotaText = extractQuota(text)
        val requirements = buildList {
            if (Regex("""\bkpss\s*p\s*94\b""").containsMatchIn(text) || text.contains("ortaogretim")) add("Ortaöğretim / KPSS P94")
            if (Regex("""\bkpss\s*p\s*93\b""").containsMatchIn(text) || text.contains("on lisans")) add("Ön lisans / KPSS P93")
            if (Regex("""\bkpss\s*p\s*3\b""").containsMatchIn(text) || text.contains("lisans mezunu")) add("Lisans / KPSS P3")
            if (text.contains("elektrik") || text.contains("elektrik elektronik")) add("Elektrik alanı/programı ifadesi · diploma kodu ayrıca kontrol edilmeli")
            if (Regex("""(?<!\d)2023(?!\d)""").containsMatchIn(text)) {
                add("Nitelik kodu 2023 ifadesi · ortaöğretim elektrik-elektronik alan/ dal şartını ÖSYM tablosundan doğrula")
            }
            if (Regex("""(?<!\d)3290(?!\d)""").containsMatchIn(text)) {
                add("Nitelik kodu 3290 ifadesi · elektrik / elektrik teknolojisi ön lisans mezuniyeti ve tercih koşullarını doğrula")
            }
            if (Regex("""(?<!\d)2001(?!\d)""").containsMatchIn(text)) {
                add("Nitelik kodu 2001 ifadesi · ortaöğretim mezuniyet şartını resmî tabloda doğrula")
            }
            if (Regex("""(?<!\d)2086(?!\d)""").containsMatchIn(text)) {
                add("Nitelik kodu 2086 ifadesi · raylı sistem alan/ program koşulunu resmî tabloda doğrula")
            }
            if (text.contains("engelli")) add("Engelli aday şartı ifadesi · yalnızca ilgili ilan koşulları için")
            if (text.contains("eski hukumlu") || text.contains("eski hükümlü")) add("Eski hükümlü aday şartı ifadesi · yalnızca ilgili ilan koşulları için")
        }
        return CareerListingFacts(
            employerGroup = employerGroup,
            applicationChannel = applicationChannel,
            noticeType = noticeType,
            applicationStatus = applicationStatus,
            explicitDeadline = deadline,
            quotaText = quotaText,
            requirementSignals = requirements.distinct()
        )
    }

    /**
     * Extracts only a number explicitly adjacent to a person/position/quota term.
     * A year, date, score or arbitrary number is not treated as a vacancy count.
     */
    fun extractQuota(text: String): String? {
        val normalized = normalize(text)
        val patterns = listOf(
            Regex("""toplam\s+(\d[\d.]*)\s*(?:kişi|kisi|kişilik|kisilik|personel|işçi|isci|kontenjan|pozisyon|memur|teknisyen|tekniker)\b"""),
            Regex("""(\d[\d.]*)\s*(?:kişilik|kisilik|kişi|kisi)\s*(?:kadro|personel|alım|alim|kontenjan|pozisyon)?\b"""),
            Regex("""(\d[\d.]*)\s*(?:personel|işçi|isci|kontenjan|pozisyon)\s*(?:alımı|alimi|istihdam|kadrosu)?\b""")
        )
        val raw = patterns.asSequence().mapNotNull { it.find(normalized)?.groupValues?.getOrNull(1) }.firstOrNull()
            ?: return null
        val digits = raw.filter(Char::isDigit)
        val count = digits.toLongOrNull() ?: return null
        if (count !in 1L..100_000L) return null
        return raw
    }
}
