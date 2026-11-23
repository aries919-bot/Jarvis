package com.cem.jarvis

import org.json.JSONObject
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A model field is not trusted by itself. Its evidence must be a literal excerpt
 * from the page text and the extracted value must agree with that excerpt.
 */
data class CareerListingField(
    val value: String,
    val evidence: String,
    val provenance: String,
    val status: String,
    val note: String
)

data class CareerListingSchema(
    val employer: CareerListingField,
    val position: CareerListingField,
    val province: CareerListingField,
    val recruitmentMethod: CareerListingField,
    val kpssScoreType: CareerListingField,
    val qualificationCodes: CareerListingField,
    val quota: CareerListingField,
    val conditions: CareerListingField,
    val deadline: CareerListingField,
    val sourceUrl: String,
    val fetchedAtEpochMillis: Long,
    val sourceTextTruncated: Boolean
) {
    fun fields(): List<Pair<String, CareerListingField>> = listOf(
        "Kurum / işveren" to employer,
        "Pozisyon" to position,
        "İl" to province,
        "Alım şekli" to recruitmentMethod,
        "KPSS puan türü" to kpssScoreType,
        "Nitelik kodları" to qualificationCodes,
        "Kontenjan" to quota,
        "Şartlar" to conditions,
        "Son başvuru tarihi" to deadline
    )

    fun report(): String = buildString {
        appendLine("Kaynak sayfası alan raporu")
        appendLine("Kaynak: $sourceUrl")
        appendLine("Alınma zamanı: " + SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("tr", "TR")).format(Date(fetchedAtEpochMillis)))
        appendLine(if (sourceTextTruncated) "Uyarı: Sayfa metni uzun olduğu için kısaltıldı; hiçbir alan kesin kabul edilmemeli." else "Not: Bu rapor kaynak sayfa metninden alıntı eşleştirmesidir; başvurunun hâlen açık olduğunu veya hukuki uygunluğu tek başına kanıtlamaz.")
        appendLine()
        fields().forEach { (label, field) ->
            appendLine("$label: " + field.value.ifBlank { "Bulunamadı" })
            appendLine("Durum: " + field.status + " · " + field.note)
            if (field.evidence.isNotBlank()) appendLine("Kanıt: “${field.evidence}”")
            appendLine("Provenans: ${field.provenance}")
            appendLine()
        }
    }
}

object CareerListingSchemaParser {
    private const val SOURCE_PROVENANCE = "Kaynak sayfası metni + AI alan çıkarımı"

    private fun normalize(value: String): String = value.lowercase(Locale("tr", "TR"))
        .replace('ı', 'i').replace('ş', 's').replace('ğ', 'g')
        .replace('ü', 'u').replace('ö', 'o').replace('ç', 'c')
        .replace(Regex("[^a-z0-9./-]+"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()

    private fun digits(value: String): String = value.filter(Char::isDigit)

    private fun containsValue(evidence: String, value: String): Boolean {
        if (evidence.isBlank() || value.isBlank()) return false
        val normalizedEvidence = normalize(evidence)
        val normalizedValue = normalize(value)
        if (normalizedEvidence.contains(normalizedValue)) return true
        val parts = value.split(Regex("[,;\\s]+")).filter { it.isNotBlank() }
        return parts.size > 1 && parts.all { normalizedEvidence.contains(normalize(it)) }
    }

    private fun field(
        name: String,
        raw: JSONObject,
        sourceText: String,
        truncated: Boolean
    ): CareerListingField {
        val value = raw.optString("value").trim().take(1200)
        val evidence = raw.optString("evidence").trim().take(2000)
        val sourceContainsEvidence = evidence.isNotBlank() &&
            normalize(sourceText).contains(normalize(evidence))
        val valueMatchesEvidence = containsValue(evidence, value)
        val semanticMatch = when (name) {
            "deadline" -> {
                val parsed = CareerListingParser.deadlineDate(evidence)
                parsed != null && normalize(value).contains(normalize(parsed))
            }
            "quota" -> {
                val parsed = CareerListingIntelligence.extractQuota(evidence)
                parsed != null && digits(value) == digits(parsed)
            }
            "kpssScoreType" -> {
                val explicit = Regex("(?i)\\bKPSS[\\s/-]*P[\\s/-]*(3|93|94)\\b")
                    .find(evidence)?.groupValues?.getOrNull(1)
                explicit != null && normalize(value).replace(" ", "") == "p$explicit"
            }
            "qualificationCodes" -> {
                val evidenceCodes = Regex("\\b\\d{4}\\b").findAll(evidence)
                    .map { it.value }.toSet()
                val requestedCodes = Regex("\\b\\d{4}\\b").findAll(value)
                    .map { it.value }.toSet()
                normalize(evidence).contains("nitelik kod") &&
                    requestedCodes.isNotEmpty() && evidenceCodes.containsAll(requestedCodes)
            }
            else -> valueMatchesEvidence
        }

        val status: String
        val note: String
        when {
            value.isBlank() -> {
                status = "BULUNAMADI"
                note = "Kaynak metninde açık bilgi bulunmadı."
            }
            !sourceContainsEvidence || !valueMatchesEvidence || !semanticMatch -> {
                status = "DOĞRULANAMADI · KONTROL ET"
                note = "AI alanı kaynak metnindeki alıntıyla eşleşmedi; kesin bilgi olarak kullanılamaz."
            }
            truncated -> {
                status = "KONTROL ET · METİN KISALDI"
                note = "Alıntı görüldü ancak sayfanın tamamı alınamadı."
            }
            else -> {
                status = "ALINTIYLA EŞLEŞTİ"
                note = "Değer, aynı kaynak sayfasından alınan alıntıda bulundu; ayrıca resmî şart kontrolü gerekir."
            }
        }
        return CareerListingField(
            value = if (status == "DOĞRULANAMADI · KONTROL ET") "" else value,
            evidence = evidence.takeIf { sourceContainsEvidence }.orEmpty(),
            provenance = SOURCE_PROVENANCE,
            status = status,
            note = note
        )
    }

    fun parse(
        aiJson: String,
        sourceText: String,
        sourceUrl: String,
        fetchedAtEpochMillis: Long,
        sourceTextTruncated: Boolean
    ): CareerListingSchema {
        require(CareerListingPagePolicy.validateHttpsUrl(sourceUrl) == null) {
            "Kaynak adresi güvenli HTTPS adresi değil."
        }
        require(sourceText.isNotBlank()) { "Kaynak sayfa metni boş." }
        val fence = Char(96).toString().repeat(3)
        val cleaned = aiJson.trim()
            .removePrefix(fence + "json")
            .removePrefix(fence)
            .removeSuffix(fence)
            .trim()
        val root = JSONObject(cleaned)
        fun get(name: String) = root.optJSONObject(name) ?: JSONObject()
        return CareerListingSchema(
            employer = field("employer", get("employer"), sourceText, sourceTextTruncated),
            position = field("position", get("position"), sourceText, sourceTextTruncated),
            province = field("province", get("province"), sourceText, sourceTextTruncated),
            recruitmentMethod = field("recruitmentMethod", get("recruitmentMethod"), sourceText, sourceTextTruncated),
            kpssScoreType = field("kpssScoreType", get("kpssScoreType"), sourceText, sourceTextTruncated),
            qualificationCodes = field("qualificationCodes", get("qualificationCodes"), sourceText, sourceTextTruncated),
            quota = field("quota", get("quota"), sourceText, sourceTextTruncated),
            conditions = field("conditions", get("conditions"), sourceText, sourceTextTruncated),
            deadline = field("deadline", get("deadline"), sourceText, sourceTextTruncated),
            sourceUrl = sourceUrl,
            fetchedAtEpochMillis = fetchedAtEpochMillis,
            sourceTextTruncated = sourceTextTruncated
        )
    }
}

data class CareerListingPageCheck(
    val ready: Boolean,
    val message: String,
    val text: String,
    val truncated: Boolean
)

/** Gate for the user-triggered WebView fallback. It deliberately does not solve CAPTCHAs. */
object CareerListingPagePolicy {
    const val MAX_PAGE_TEXT_CHARS = 24_000

    fun validateHttpsUrl(rawUrl: String): String? {
        val uri = runCatching { URI(rawUrl.trim()) }.getOrNull()
            ?: return "Kaynak bağlantısı okunamadı."
        if (!uri.scheme.equals("https", ignoreCase = true)) return "Güvenlik nedeniyle yalnız HTTPS kaynaklar açılabilir."
        if (uri.host.isNullOrBlank() || uri.rawUserInfo != null) return "Kaynak adresi geçerli değil."
        if (uri.port != -1 && uri.port != 443) return "Standart dışı bağlantı noktası engellendi."
        val host = uri.host.lowercase(Locale.ROOT)
        if (host == "localhost" || host.endsWith(".localhost") || host.endsWith(".local") ||
            host == "127.0.0.1" || host == "::1"
        ) return "Yerel ağ adresleri kaynak olarak açılamaz."
        return null
    }

    fun inspect(httpStatus: Int?, title: String, body: String): CareerListingPageCheck {
        if (httpStatus != null && httpStatus !in 200..299) {
            val message = when (httpStatus) {
                401, 403 -> "Kaynak erişimi reddetti (HTTP $httpStatus). Giriş veya güvenlik kontrolü aşılmayacak."
                else -> "Kaynak sayfa açılamadı (HTTP $httpStatus)."
            }
            return CareerListingPageCheck(false, message, "", false)
        }
        val combined = (title + "\n" + body).trim()
        if (combined.isBlank()) {
            return CareerListingPageCheck(false, "Sayfadan okunabilir metin alınamadı.", "", false)
        }
        val normalized = normalizePageText(combined)
        val captchaMarkers = listOf(
            "captcha", "verify you are human", "i am not a robot",
            "checking your browser", "attention required", "cloudflare ray id",
            "enable javascript and cookies to continue", "guvenlik dogrulamasi",
            "robot olmadiginizi", "insan oldugunuzu dogrulayin"
        )
        if (captchaMarkers.any(normalized::contains)) {
            return CareerListingPageCheck(false, "Kaynak CAPTCHA / bot kontrolü gösteriyor. Bu kontrol aşılmayacak.", "", false)
        }
        val candidateResultMarkers = listOf(
            "asil ve yedek aday listesi", "asil aday listesi", "yedek aday listesi",
            "adaylarin ad soyad listesi", "aday adi soyadi listesi",
            "kura sonuc listesi", "noter kurasi sonuc listesi",
            "sinav sonuc listesi", "mulakat sonuc listesi",
            "kazanan aday listesi", "yerlesen aday listesi"
        )
        val hasCandidateResultTitle = listOf(
            "kura sonuclari", "sinav sonuclari", "mulakat sonuclari",
            "basvuru sonuclari", "yerlestirme sonuclari", "sonuc listesi"
        ).any(normalized::contains)
        val hasCandidateIdentityColumns = listOf(
            "ad soyad", "adi soyadi", "aday no", "aday numarasi",
            "basvuru no", "tc kimlik", "kimlik numarasi"
        ).any(normalized::contains)
        if (candidateResultMarkers.any(normalized::contains) ||
            (hasCandidateResultTitle && hasCandidateIdentityColumns)
        ) {
            return CareerListingPageCheck(false, "Bu sayfa aday/kura/sınav sonuç listesi içeriyor olabilir. Başka adayların verisi işlenmeyecek.", "", false)
        }
        val loginMarkers = listOf(
            "giris yaparak devam edin", "oturum acarak devam edin",
            "login required", "sign in to continue", "please log in to continue"
        )
        if (loginMarkers.any(normalized::contains)) {
            return CareerListingPageCheck(false, "Kaynak giriş/oturum istiyor. Giriş otomasyonu yapılmayacak.", "", false)
        }
        if (body.isBlank()) {
            return CareerListingPageCheck(false, "Sayfadan okunabilir ilan metni alınamadı.", "", false)
        }
        val truncated = combined.length > MAX_PAGE_TEXT_CHARS
        val text = combined.take(MAX_PAGE_TEXT_CHARS)
        return CareerListingPageCheck(
            true,
            if (truncated) "Sayfa metni alındı ancak uzun olduğu için kısaltılacak; alanlar kontrol gerektirir." else "Sayfa metni kullanıcı isteğiyle alındı.",
            text,
            truncated
        )
    }

    fun sanitizeForAi(value: String): String = value
        .replace(Regex("[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}"), "[E-POSTA]")
        .replace(Regex("\\b\\d{11}\\b"), "[KIMLIK NUMARASI]")
        .replace(Regex("\\bTR\\d{2}(?:\\s?\\d{4}){5}\\s?\\d{2}\\b", RegexOption.IGNORE_CASE), "[IBAN]")
        .replace(Regex("(\\+?90\\s?)?\\(?0?[2345]\\d{2}\\)?[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}"), "[TELEFON]")

    private fun normalizePageText(value: String): String = value.lowercase(Locale("tr", "TR"))
        .replace('ı', 'i').replace('ş', 's').replace('ğ', 'g')
        .replace('ü', 'u').replace('ö', 'o').replace('ç', 'c')
        .replace(Regex("\\s+"), " ")
        .trim()
}
