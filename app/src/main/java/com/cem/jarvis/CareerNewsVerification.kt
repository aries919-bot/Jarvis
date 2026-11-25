package com.cem.jarvis

import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.net.URI
import java.util.UUID

enum class CareerEvidenceSourceType {
    PRIMARY_SOURCE,
    INDEPENDENT_REPORTING,
    SECONDARY,
    UNKNOWN
}

enum class CareerEvidenceStance {
    SUPPORTS,
    CONTRADICTS,
    CONTEXT_ONLY
}

enum class CareerHumanReviewDecision {
    CONFIRM,
    REFUTE,
    INCONCLUSIVE
}

enum class CareerClaimVerdict {
    NEEDS_EVIDENCE,
    CONTEXT_MISSING,
    SUPPORTS_PENDING_REVIEW,
    CONTRADICTION_PENDING_REVIEW,
    CONFLICTING_EVIDENCE,
    VERIFIED,
    REFUTED
}

enum class CareerNewsVerdict {
    NEEDS_CLAIMS,
    NEEDS_EVIDENCE,
    CONTEXT_MISSING,
    REVIEW_PENDING,
    CONFLICTING_EVIDENCE,
    VERIFIED,
    REFUTED,
    PARTIALLY_VERIFIED
}

data class CareerNewsEvidence(
    val id: String,
    val publisher: String,
    val sourceUrl: String,
    val sourceType: CareerEvidenceSourceType,
    val stance: CareerEvidenceStance,
    val excerpt: String,
    val publishedAt: String?,
    val checkedAt: String
)

data class CareerNewsClaim(
    val id: String,
    val text: String,
    val evidence: List<CareerNewsEvidence> = emptyList(),
    val reviewDecision: CareerHumanReviewDecision? = null,
    val reviewedAt: String? = null,
    val reviewNote: String = ""
) {
    fun verdict(): CareerClaimVerdict {
        val hasSupport = evidence.any { it.stance == CareerEvidenceStance.SUPPORTS }
        val hasContradiction = evidence.any { it.stance == CareerEvidenceStance.CONTRADICTS }
        val hasPrimarySupport = evidence.any {
            it.sourceType == CareerEvidenceSourceType.PRIMARY_SOURCE &&
                it.stance == CareerEvidenceStance.SUPPORTS
        }
        val hasPrimaryContradiction = evidence.any {
            it.sourceType == CareerEvidenceSourceType.PRIMARY_SOURCE &&
                it.stance == CareerEvidenceStance.CONTRADICTS
        }

        if (hasSupport && hasContradiction) return CareerClaimVerdict.CONFLICTING_EVIDENCE
        if (hasPrimarySupport && reviewDecision == CareerHumanReviewDecision.CONFIRM &&
            reviewedAt != null && reviewNote.isNotBlank() && hasContradiction.not()
        ) return CareerClaimVerdict.VERIFIED
        if (hasPrimaryContradiction && reviewDecision == CareerHumanReviewDecision.REFUTE &&
            reviewedAt != null && reviewNote.isNotBlank() && hasSupport.not()
        ) return CareerClaimVerdict.REFUTED
        if (hasPrimarySupport) return CareerClaimVerdict.SUPPORTS_PENDING_REVIEW
        if (hasPrimaryContradiction) return CareerClaimVerdict.CONTRADICTION_PENDING_REVIEW
        if (evidence.isNotEmpty()) return CareerClaimVerdict.CONTEXT_MISSING
        return CareerClaimVerdict.NEEDS_EVIDENCE
    }

    /** Adding evidence invalidates an earlier human decision so the new evidence is reviewed. */
    fun withEvidence(item: CareerNewsEvidence): CareerNewsClaim = copy(
        evidence = evidence + item,
        reviewDecision = null,
        reviewedAt = null,
        reviewNote = ""
    )

    fun withHumanReview(
        decision: CareerHumanReviewDecision,
        reviewedAt: String,
        note: String = ""
    ): CareerNewsClaim = copy(
        reviewDecision = decision,
        reviewedAt = reviewedAt,
        reviewNote = note.trim().take(1000)
    )
}

data class CareerNewsCorrection(
    val id: String,
    val claimId: String,
    val correctedAt: String,
    val previousVerdict: CareerClaimVerdict,
    val newVerdict: CareerClaimVerdict,
    val previousText: String,
    val revisedText: String,
    val previousEvidence: List<CareerNewsEvidence>,
    val previousReviewDecision: CareerHumanReviewDecision?,
    val previousReviewedAt: String?,
    val previousReviewNote: String,
    val explanation: String,
    val sourceUrl: String
)

data class CareerNewsVerificationRecord(
    val schemaVersion: Int = 1,
    val storyId: String,
    val headline: String,
    val publisher: String,
    val originalSourceUrl: String,
    val publishedAt: String?,
    val collectedAt: String,
    val firstSeenAt: String,
    val claims: List<CareerNewsClaim> = emptyList(),
    val correctionHistory: List<CareerNewsCorrection> = emptyList()
) {
    fun verdict(): CareerNewsVerdict {
        if (claims.isEmpty()) return CareerNewsVerdict.NEEDS_CLAIMS
        val results = claims.map { it.verdict() }
        if (results.any { it == CareerClaimVerdict.CONFLICTING_EVIDENCE }) {
            return CareerNewsVerdict.CONFLICTING_EVIDENCE
        }
        if (results.all { it == CareerClaimVerdict.VERIFIED }) return CareerNewsVerdict.VERIFIED
        if (results.all { it == CareerClaimVerdict.REFUTED }) return CareerNewsVerdict.REFUTED
        if (results.any { it == CareerClaimVerdict.VERIFIED }) return CareerNewsVerdict.PARTIALLY_VERIFIED
        if (results.any { it == CareerClaimVerdict.REFUTED }) return CareerNewsVerdict.REVIEW_PENDING
        if (results.any {
                it == CareerClaimVerdict.SUPPORTS_PENDING_REVIEW ||
                    it == CareerClaimVerdict.CONTRADICTION_PENDING_REVIEW
            }
        ) return CareerNewsVerdict.REVIEW_PENDING
        if (results.any { it == CareerClaimVerdict.CONTEXT_MISSING }) {
            return CareerNewsVerdict.CONTEXT_MISSING
        }
        return CareerNewsVerdict.NEEDS_EVIDENCE
    }

    fun addClaim(claim: CareerNewsClaim): CareerNewsVerificationRecord? {
        if (claims.any { it.id == claim.id }) return null
        return copy(claims = claims + claim).takeIf { it.validationErrors().isEmpty() }
    }

    fun addEvidence(claimId: String, evidence: CareerNewsEvidence): CareerNewsVerificationRecord? {
        val index = claims.indexOfFirst { it.id == claimId }
        if (index < 0) return null
        val updated = claims.toMutableList()
        updated[index] = updated[index].withEvidence(evidence)
        return copy(claims = updated).takeIf { it.validationErrors().isEmpty() }
    }

    fun reviewClaim(
        claimId: String,
        decision: CareerHumanReviewDecision,
        reviewedAt: String,
        note: String = ""
    ): CareerNewsVerificationRecord? {
        val index = claims.indexOfFirst { it.id == claimId }
        if (index < 0 || !CareerNewsVerificationPolicy.isUtcTimestamp(reviewedAt)) return null
        val updated = claims.toMutableList()
        updated[index] = updated[index].withHumanReview(decision, reviewedAt, note)
        return copy(claims = updated).takeIf { it.validationErrors().isEmpty() }
    }

    /** Records the change and requires a fresh human review after the correction. */
    fun correctClaim(
        revisedClaim: CareerNewsClaim,
        correctedAt: String,
        explanation: String,
        sourceUrl: String
    ): CareerNewsVerificationRecord? {
        val index = claims.indexOfFirst { it.id == revisedClaim.id }
        if (index < 0 || revisedClaim.text.trim() == claims[index].text.trim()) return null
        val previous = claims[index].verdict()
        val resetReview = revisedClaim.copy(
            evidence = emptyList(),
            reviewDecision = null,
            reviewedAt = null,
            reviewNote = ""
        )
        val updatedClaims = claims.toMutableList()
        updatedClaims[index] = resetReview
        val entry = CareerNewsCorrection(
            id = UUID.randomUUID().toString(),
            claimId = revisedClaim.id,
            correctedAt = correctedAt,
            previousVerdict = previous,
            newVerdict = resetReview.verdict(),
            previousText = claims[index].text,
            revisedText = resetReview.text,
            previousEvidence = claims[index].evidence,
            previousReviewDecision = claims[index].reviewDecision,
            previousReviewedAt = claims[index].reviewedAt,
            previousReviewNote = claims[index].reviewNote,
            explanation = explanation.trim(),
            sourceUrl = sourceUrl.trim()
        )
        return copy(
            claims = updatedClaims,
            correctionHistory = correctionHistory + entry
        ).takeIf { it.validationErrors().isEmpty() }
    }

    fun validationErrors(): List<String> {
        val errors = mutableListOf<String>()
        if (schemaVersion != 1) errors += "Desteklenmeyen şema sürümü."
        if (storyId.isBlank() || storyId.length > 160) errors += "Hikâye kimliği geçersiz."
        if (headline.isBlank() || headline.length > 1000) errors += "Başlık boş veya çok uzun."
        if (publisher.isBlank() || publisher.length > 300) errors += "Yayıncı/kurum boş veya çok uzun."
        if (CareerNewsVerificationPolicy.validateHttpsUrl(originalSourceUrl) != null) {
            errors += "Asıl kaynak bağlantısı geçerli HTTPS adresi değil."
        }
        if (publishedAt != null && !CareerNewsVerificationPolicy.isUtcTimestamp(publishedAt)) {
            errors += "Yayın tarihi ISO-8601 UTC biçiminde değil."
        }
        if (!CareerNewsVerificationPolicy.isUtcTimestamp(collectedAt)) errors += "collectedAt geçersiz."
        if (!CareerNewsVerificationPolicy.isUtcTimestamp(firstSeenAt)) errors += "firstSeenAt geçersiz."
        if (claims.map { it.id }.distinct().size != claims.size) errors += "Tekrarlanan iddia kimliği."
        if (correctionHistory.map { it.id }.distinct().size != correctionHistory.size) {
            errors += "Tekrarlanan düzeltme kimliği."
        }

        claims.forEach { claim ->
            if (claim.id.isBlank() || claim.id.length > 160) errors += "İddia kimliği geçersiz."
            if (claim.text.isBlank() || claim.text.length > 1500 || claim.text.contains('\n')) {
                errors += "İddia tek ve okunabilir bir ifade olmalı."
            }
            if (claim.reviewNote.length > 1000) errors += "İnsan inceleme gerekçesi çok uzun."
            if (claim.reviewDecision == null && claim.reviewedAt != null) {
                errors += "İnsan inceleme zamanı karar olmadan kaydedilemez."
            }
            if (claim.reviewDecision != null &&
                (!CareerNewsVerificationPolicy.isUtcTimestamp(claim.reviewedAt) || claim.reviewNote.isBlank())
            ) {
                errors += "İnsan inceleme kararı için geçerli zaman ve gerekçe gerekli."
            }
            if (claim.evidence.map { it.id }.distinct().size != claim.evidence.size) {
                errors += "İddia içinde tekrarlanan kanıt kimliği."
            }
            claim.evidence.forEach { evidence ->
                if (evidence.id.isBlank() || evidence.id.length > 160) errors += "Kanıt kimliği geçersiz."
                if (evidence.publisher.isBlank() || evidence.publisher.length > 300) {
                    errors += "Kanıt yayıncısı boş veya çok uzun."
                }
                if (CareerNewsVerificationPolicy.validateHttpsUrl(evidence.sourceUrl) != null) {
                    errors += "Kanıt URL'si geçerli HTTPS adresi değil."
                }
                if (evidence.excerpt.isBlank() || evidence.excerpt.length > 2000) {
                    errors += "Kanıt alıntısı boş veya çok uzun."
                } else if (CareerNewsVerificationPolicy.sanitizeEvidenceExcerpt(evidence.excerpt) != evidence.excerpt) {
                    errors += "Kanıt alıntısı kişisel veri işareti içeriyor; kaydetmeden önce maskele."
                }
                if (!CareerNewsVerificationPolicy.isUtcTimestamp(evidence.checkedAt)) {
                    errors += "Kanıt kontrol zamanı geçersiz."
                }
                if (evidence.publishedAt != null &&
                    !CareerNewsVerificationPolicy.isUtcTimestamp(evidence.publishedAt)
                ) errors += "Kanıt yayın zamanı geçersiz."
            }
        }

        correctionHistory.forEach { correction ->
            if (correction.id.isBlank() || correction.id.length > 160) errors += "Düzeltme kimliği geçersiz."
            if (claims.none { it.id == correction.claimId }) errors += "Düzeltme bilinmeyen iddiaya bağlı."
            if (!CareerNewsVerificationPolicy.isUtcTimestamp(correction.correctedAt)) {
                errors += "Düzeltme zamanı geçersiz."
            }
            if (correction.previousText.isBlank() || correction.revisedText.isBlank()) {
                errors += "Düzeltme öncesi ve sonrası iddia metni gerekli."
            }
            if (correction.previousEvidence.any { evidence ->
                    evidence.id.isBlank() || evidence.publisher.isBlank() ||
                        CareerNewsVerificationPolicy.validateHttpsUrl(evidence.sourceUrl) != null ||
                        evidence.excerpt.isBlank() ||
                        CareerNewsVerificationPolicy.sanitizeEvidenceExcerpt(evidence.excerpt) != evidence.excerpt ||
                        !CareerNewsVerificationPolicy.isUtcTimestamp(evidence.checkedAt) ||
                        (evidence.publishedAt != null &&
                            !CareerNewsVerificationPolicy.isUtcTimestamp(evidence.publishedAt))
                }) errors += "Düzeltme geçmişindeki önceki kanıt geçersiz."
            if (correction.previousReviewDecision == null && correction.previousReviewedAt != null) {
                errors += "Önceki inceleme zamanı karar olmadan kaydedilemez."
            }
            if (correction.previousReviewNote.length > 1000) errors += "Önceki inceleme gerekçesi çok uzun."
            if (correction.previousReviewDecision != null &&
                (!CareerNewsVerificationPolicy.isUtcTimestamp(correction.previousReviewedAt) ||
                    correction.previousReviewNote.isBlank())
            ) errors += "Önceki inceleme zamanı veya gerekçesi geçersiz."
            if (correction.explanation.isBlank() || correction.explanation.length > 2000) {
                errors += "Düzeltme gerekçesi boş veya çok uzun."
            }
            if (CareerNewsVerificationPolicy.validateHttpsUrl(correction.sourceUrl) != null) {
                errors += "Düzeltme kaynak URL'si geçerli HTTPS adresi değil."
            }
        }
        return errors.distinct()
    }

    fun toJson(): JSONObject {
        val claimArray = JSONArray()
        claims.forEach { claim ->
            val evidenceArray = JSONArray()
            claim.evidence.forEach { evidence ->
                evidenceArray.put(evidence.toJson())
            }
            claimArray.put(JSONObject()
                .put("id", claim.id)
                .put("text", claim.text)
                .put("evidence", evidenceArray)
                .put("reviewDecision", claim.reviewDecision?.name ?: JSONObject.NULL)
                .put("reviewedAt", claim.reviewedAt ?: JSONObject.NULL)
                .put("reviewNote", claim.reviewNote))
        }
        val corrections = JSONArray()
        correctionHistory.forEach { correction ->
            val previousEvidence = JSONArray()
            correction.previousEvidence.forEach { previousEvidence.put(it.toJson()) }
            corrections.put(JSONObject()
                .put("id", correction.id)
                .put("claimId", correction.claimId)
                .put("correctedAt", correction.correctedAt)
                .put("previousVerdict", correction.previousVerdict.name)
                .put("newVerdict", correction.newVerdict.name)
                .put("previousText", correction.previousText)
                .put("revisedText", correction.revisedText)
                .put("previousEvidence", previousEvidence)
                .put("previousReviewDecision", correction.previousReviewDecision?.name ?: JSONObject.NULL)
                .put("previousReviewedAt", correction.previousReviewedAt ?: JSONObject.NULL)
                .put("previousReviewNote", correction.previousReviewNote)
                .put("explanation", correction.explanation)
                .put("sourceUrl", correction.sourceUrl))
        }
        return JSONObject()
            .put("schemaVersion", schemaVersion)
            .put("storyId", storyId)
            .put("headline", headline)
            .put("publisher", publisher)
            .put("originalSourceUrl", originalSourceUrl)
            .put("publishedAt", publishedAt ?: JSONObject.NULL)
            .put("collectedAt", collectedAt)
            .put("firstSeenAt", firstSeenAt)
            .put("claims", claimArray)
            .put("correctionHistory", corrections)
    }

    companion object {
        fun stableStoryId(sourceUrl: String, headline: String): String {
            val uri = runCatching { URI(sourceUrl.trim()) }.getOrNull()
            val normalizedUrl = if (uri?.scheme != null && !uri.host.isNullOrBlank()) {
                val scheme = uri.scheme.lowercase(Locale.ROOT)
                val host = uri.host.lowercase(Locale.ROOT)
                val path = uri.rawPath.orEmpty().trimEnd('/').ifBlank { "/" }
                buildString {
                    append(scheme).append("://").append(host)
                    val isDefaultPort = (scheme == "https" && uri.port == 443) ||
                        (scheme == "http" && uri.port == 80)
                    if (uri.port != -1 && !isDefaultPort) append(':').append(uri.port)
                    append(path)
                    uri.rawQuery?.let { append('?').append(it) }
                }
            } else {
                sourceUrl.substringBefore('#').trimEnd('/').lowercase(Locale.ROOT)
            }
            val identity = normalizedUrl.ifBlank {
                headline.trim().lowercase(Locale("tr", "TR")).replace(Regex("\\s+"), " ")
            }
            return UUID.nameUUIDFromBytes(identity.toByteArray(Charsets.UTF_8)).toString()
        }
        fun fromJson(raw: String): CareerNewsVerificationRecord? = runCatching {
            val root = JSONObject(raw)
            val claims = mutableListOf<CareerNewsClaim>()
            val claimArray = root.optJSONArray("claims") ?: JSONArray()
            for (i in 0 until claimArray.length()) {
                val item = claimArray.getJSONObject(i)
                val evidenceItems = mutableListOf<CareerNewsEvidence>()
                val evidenceArray = item.optJSONArray("evidence") ?: JSONArray()
                for (j in 0 until evidenceArray.length()) {
                    val evidence = evidenceArray.getJSONObject(j)
                    evidenceItems += evidenceFromJson(evidence)
                }
                val reviewName = nullableString(item, "reviewDecision")
                claims += CareerNewsClaim(
                    id = item.getString("id"),
                    text = item.getString("text"),
                    evidence = evidenceItems,
                    reviewDecision = reviewName?.let { CareerHumanReviewDecision.valueOf(it) },
                    reviewedAt = nullableString(item, "reviewedAt"),
                    reviewNote = item.optString("reviewNote")
                )
            }
            val corrections = mutableListOf<CareerNewsCorrection>()
            val correctionArray = root.optJSONArray("correctionHistory") ?: JSONArray()
            for (i in 0 until correctionArray.length()) {
                val item = correctionArray.getJSONObject(i)
                val previousEvidence = mutableListOf<CareerNewsEvidence>()
                val previousEvidenceArray = item.optJSONArray("previousEvidence") ?: JSONArray()
                for (j in 0 until previousEvidenceArray.length()) {
                    previousEvidence += evidenceFromJson(previousEvidenceArray.getJSONObject(j))
                }
                val previousReviewName = nullableString(item, "previousReviewDecision")
                corrections += CareerNewsCorrection(
                    id = item.getString("id"),
                    claimId = item.getString("claimId"),
                    correctedAt = item.getString("correctedAt"),
                    previousVerdict = CareerClaimVerdict.valueOf(item.getString("previousVerdict")),
                    newVerdict = CareerClaimVerdict.valueOf(item.getString("newVerdict")),
                    previousText = item.getString("previousText"),
                    revisedText = item.getString("revisedText"),
                    previousEvidence = previousEvidence,
                    previousReviewDecision = previousReviewName?.let { CareerHumanReviewDecision.valueOf(it) },
                    previousReviewedAt = nullableString(item, "previousReviewedAt"),
                    previousReviewNote = item.optString("previousReviewNote"),
                    explanation = item.getString("explanation"),
                    sourceUrl = item.getString("sourceUrl")
                )
            }
            CareerNewsVerificationRecord(
                schemaVersion = root.optInt("schemaVersion", 0),
                storyId = root.getString("storyId"),
                headline = root.getString("headline"),
                publisher = root.getString("publisher"),
                originalSourceUrl = root.getString("originalSourceUrl"),
                publishedAt = nullableString(root, "publishedAt"),
                collectedAt = root.getString("collectedAt"),
                firstSeenAt = root.getString("firstSeenAt"),
                claims = claims,
                correctionHistory = corrections
            ).takeIf { it.validationErrors().isEmpty() }
        }.getOrNull()
    }
}

object CareerNewsVerificationPolicy {
    private val utcTimestamp = Regex("""(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(?:\.(\d{1,3}))?Z""")

    fun isUtcTimestamp(value: String?): Boolean {
        val match = value?.let { utcTimestamp.matchEntire(it) } ?: return false
        val year = match.groupValues[1].toIntOrNull() ?: return false
        val month = match.groupValues[2].toIntOrNull() ?: return false
        val day = match.groupValues[3].toIntOrNull() ?: return false
        val hour = match.groupValues[4].toIntOrNull() ?: return false
        val minute = match.groupValues[5].toIntOrNull() ?: return false
        val second = match.groupValues[6].toIntOrNull() ?: return false
        if (year < 1 || month !in 1..12 || hour !in 0..23 || minute !in 0..59 || second !in 0..59) {
            return false
        }
        val leapYear = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
        val daysInMonth = when (month) {
            2 -> if (leapYear) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
        return day in 1..daysInMonth
    }

    fun sanitizeEvidenceExcerpt(value: String): String = value
        .replace(Regex("[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}"), "[E-POSTA]")
        .replace(Regex("\\b\\d{11}\\b"), "[KIMLIK NUMARASI]")
        .replace(Regex("\\bTR\\d{2}(?:\\s?\\d{4}){5}\\s?\\d{2}\\b", RegexOption.IGNORE_CASE), "[IBAN]")
        .replace(Regex("(\\+?90\\s?)?\\(?0?[2345]\\d{2}\\)?[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}"), "[TELEFON]")
        .trim()

    fun validateHttpsUrl(value: String): String? {
        val uri = runCatching { URI(value.trim()) }.getOrNull()
            ?: return "Kaynak bağlantısı okunamadı."
        if (!uri.scheme.equals("https", ignoreCase = true)) return "Yalnız HTTPS kaynaklar kabul edilir."
        if (uri.host.isNullOrBlank() || uri.rawUserInfo != null) return "Kaynak adresi geçerli değil."
        if (uri.port != -1 && uri.port != 443) return "Standart dışı bağlantı noktası engellendi."
        val host = uri.host.lowercase(Locale.ROOT).trim('[', ']')
        if (host == "localhost" || host.endsWith(".localhost") || host.endsWith(".local") ||
            host == "::1" || host == "0.0.0.0" || host.startsWith("127.")
        ) return "Yerel ağ adresleri kaynak olarak kabul edilmez."
        val octets = host.split(".").mapNotNull { it.toIntOrNull() }
        if (octets.size == 4 && octets.all { it in 0..255 }) {
            val privateAddress = octets[0] == 10 ||
                (octets[0] == 192 && octets[1] == 168) ||
                (octets[0] == 172 && octets[1] in 16..31) ||
                (octets[0] == 169 && octets[1] == 254) ||
                octets[0] == 127
            if (privateAddress) return "Özel ağ IP adresleri kaynak olarak kabul edilmez."
        }
        return null
    }
}


private fun CareerNewsEvidence.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("publisher", publisher)
    .put("sourceUrl", sourceUrl)
    .put("sourceType", sourceType.name)
    .put("stance", stance.name)
    .put("excerpt", excerpt)
    .put("publishedAt", publishedAt ?: JSONObject.NULL)
    .put("checkedAt", checkedAt)

private fun evidenceFromJson(json: JSONObject): CareerNewsEvidence = CareerNewsEvidence(
    id = json.getString("id"),
    publisher = json.getString("publisher"),
    sourceUrl = json.getString("sourceUrl"),
    sourceType = CareerEvidenceSourceType.valueOf(json.getString("sourceType")),
    stance = CareerEvidenceStance.valueOf(json.getString("stance")),
    excerpt = json.getString("excerpt"),
    publishedAt = nullableString(json, "publishedAt"),
    checkedAt = json.getString("checkedAt")
)

private fun nullableString(json: JSONObject, key: String): String? {
    if (!json.has(key) || json.isNull(key)) return null
    return json.optString(key).trim().takeIf { it.isNotEmpty() }
}
