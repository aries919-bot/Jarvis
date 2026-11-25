package com.cem.jarvis

import java.util.Locale

enum class CareerNewsQAStatus {
    ANSWERED,
    NEEDS_CLARIFICATION,
    ABSTAINED
}

data class CareerNewsQASource(
    val label: String,
    val url: String,
    val excerpt: String,
    val detail: String
)

data class CareerNewsQAAnswer(
    val status: CareerNewsQAStatus,
    val answer: String,
    val sources: List<CareerNewsQASource> = emptyList()
)

/**
 * Deterministic, on-device question answering over one saved news record.
 * It never calls an AI model, a network endpoint, or an external search service.
 */
object CareerNewsQuestionAnswer {
    private val stopWords = setOf(
        "acaba", "ama", "bana", "bu", "da", "de", "daha", "en", "icin", "ile",
        "mi", "mu", "mı", "mü", "ne", "nedir", "nasıl", "nasil", "olan",
        "olarak", "ve", "veya", "var", "yok", "hangi", "kim", "nerede",
        "haber", "iddia", "iddiasi", "iddianin", "lutfen", "acikla"
    )

    fun answer(record: CareerNewsVerificationRecord, question: String): CareerNewsQAAnswer {
        val normalized = normalize(question)
        if (normalized.isBlank()) {
            return abstain("Bir soru yaz. Yalnızca bu haberin kayıtlı bilgilerini yanıtlayabilirim.")
        }
        if (containsPersonalData(question, normalized)) {
            return abstain("Bu ekran kişisel CV, KPSS sonucu, kimlik, telefon veya hesap bilgilerini değerlendirmez.")
        }

        if (has(normalized, "yayın tarihi", "yayim tarihi", "ne zaman yayımland", "ne zaman yayinlandi")) {
            val published = record.publishedAt
            val answer = if (published == null) {
                "Asıl haberin yayın tarihi kaynak kaydında belirtilmemiş. Bu tarih toplama veya ilk görülme zamanından çıkarılamaz."
            } else {
                "Kaydedilmiş yayın tarihi: $published."
            }
            return answered(
                answer + " Aries toplama zamanı: " + (record.collectedAt ?: "bilinmiyor") +
                    ". İlk görülme zamanı: " + (record.firstSeenAt ?: "bilinmiyor") + ".",
                listOf(originalSource(record))
            )
        }

        if (has(normalized, "toplama zamanı", "toplama zamani", "ne zaman topladı", "ne zaman topladi")) {
            return answered(
                "Aries'in toplama zamanı: " + (record.collectedAt ?: "kayıtta bilinmiyor") +
                    ". İlk görülme zamanı: " + (record.firstSeenAt ?: "kayıtta bilinmiyor") + ".",
                listOf(originalSource(record))
            )
        }

        if (has(normalized, "ilk görülme", "ilk gorulme", "ilk ne zaman görüld", "ilk ne zaman goruldu")) {
            return answered(
                "İlk görülme zamanı: " + (record.firstSeenAt ?: "eski kayıtta belirtilmemiş") +
                    ". Bu değer yayın tarihi yerine kullanılamaz.",
                listOf(originalSource(record))
            )
        }

        if (has(normalized, "düzeltme geçmişi", "duzeltme gecmisi", "önce ne yazıyordu", "once ne yaziyordu")) {
            return correctionAnswer(record, null)
        }

        if (has(normalized, "asıl kaynak", "asil kaynak", "haberi kim yayımladı", "haberi kim yayinladi", "yayıncı kim", "yayinci kim")) {
            return answered(
                "Haber kaydındaki yayıncı: ${record.publisher}. Asıl bağlantı: ${record.originalSourceUrl}",
                listOf(originalSource(record))
            )
        }

        if (has(normalized, "haber durumu", "doğru mu", "dogru mu", "gerçek mi", "gercek mi", "doğrulandı mı", "dogrulandi mi", "iddia sonucu", "karar ne")) {
            return verdictAnswer(record)
        }

        if (has(normalized, "kanıt", "kanit", "kaynaklar", "hangi kaynak", "dayanak")) {
            return evidenceAnswer(record, null)
        }

        if (has(normalized, "kim doğruladı", "kim dogruladi", "kim inceledi", "insan incelemesi", "inceleyen kişi")) {
            return humanReviewAnswer(record, null)
        }

        val selection = selectClaim(record, tokens(normalized))
        return when (selection) {
            is ClaimSelection.Selected -> claimAnswer(record, selection.claim, normalized)
            ClaimSelection.Ambiguous -> CareerNewsQAAnswer(
                CareerNewsQAStatus.NEEDS_CLARIFICATION,
                "Bu haberde birden fazla ayrı iddia var. Hangi iddiayı kastettiğini birkaç kelimeyle belirt; iddiaları birbirine karıştırmayayım."
            )
            ClaimSelection.None -> if (record.claims.isEmpty()) {
                answered(
                    "Bu haber için henüz ayrı bir iddia kaydı yok. Önce kaynakla karşılaştırılabilecek tek bir olgusal iddia eklenmeli.",
                    listOf(originalSource(record))
                )
            } else {
                CareerNewsQAAnswer(
                    CareerNewsQAStatus.NEEDS_CLARIFICATION,
                    "Soruyu bu haberin hangi iddiasıyla ilgili olduğunu belirterek yeniden yaz. Kayıt dışı bilgi tahmin edilmeyecek."
                )
            }
        }
    }

    private sealed class ClaimSelection {
        data class Selected(val claim: CareerNewsClaim) : ClaimSelection()
        data object Ambiguous : ClaimSelection()
        data object None : ClaimSelection()
    }

    private fun selectClaim(record: CareerNewsVerificationRecord, queryTokens: Set<String>): ClaimSelection {
        if (record.claims.isEmpty()) return ClaimSelection.None
        if (record.claims.size == 1) return ClaimSelection.Selected(record.claims.single())
        if (queryTokens.isEmpty()) return ClaimSelection.Ambiguous

        val ranked = record.claims.map { claim ->
            claim to tokens(claim.text).intersect(queryTokens).size
        }.sortedByDescending { it.second }
        val best = ranked.firstOrNull() ?: return ClaimSelection.None
        if (best.second == 0) return ClaimSelection.Ambiguous
        if (ranked.size > 1 && best.second == ranked[1].second) return ClaimSelection.Ambiguous
        return ClaimSelection.Selected(best.first)
    }

    private fun claimAnswer(
        record: CareerNewsVerificationRecord,
        claim: CareerNewsClaim,
        question: String
    ): CareerNewsQAAnswer {
        if (has(question, "kanıt", "kanit", "kaynak", "dayanak")) {
            return evidenceAnswer(record, claim)
        }
        if (has(question, "kim doğruladı", "kim dogruladi", "kim inceledi", "insan incelemesi")) {
            return humanReviewAnswer(record, claim)
        }
        if (has(question, "düzeltme", "duzeltme", "önce ne yaz", "once ne yaz")) {
            return correctionAnswer(record, claim.id)
        }
        return answered(
            "İddia: ${claim.text}\nDurum: ${claimVerdictText(claim)}",
            claim.evidence.mapNotNull { evidenceSource(it) }
        )
    }

    private fun verdictAnswer(record: CareerNewsVerificationRecord): CareerNewsQAAnswer {
        if (record.claims.isEmpty()) {
            return answered(
                "Bu haber için ayrı bir iddia kaydedilmediği için doğrulama kararı verilemez.",
                listOf(originalSource(record))
            )
        }
        val lines = record.claims.map { claim ->
            "• ${claim.text}\n  ${claimVerdictText(claim)}"
        }
        val overall = when (record.verdict()) {
            CareerNewsVerdict.NEEDS_CLAIMS -> "İddia eklenmedi"
            CareerNewsVerdict.NEEDS_EVIDENCE -> "Kanıt bekliyor"
            CareerNewsVerdict.CONTEXT_MISSING -> "Bağlam eksik"
            CareerNewsVerdict.REVIEW_PENDING -> "İnsan incelemesi bekliyor"
            CareerNewsVerdict.CONFLICTING_EVIDENCE -> "Çelişkili kanıt"
            CareerNewsVerdict.VERIFIED -> "Tüm iddialar doğrulandı"
            CareerNewsVerdict.REFUTED -> "İddialar çürütüldü"
            CareerNewsVerdict.PARTIALLY_VERIFIED -> "Kısmen doğrulandı"
        }
        val sources = (listOf(originalSource(record)) +
            record.claims.flatMap { it.evidence }.mapNotNull { evidenceSource(it) })
            .distinctBy { it.url }
        return answered("Haberin kayıtlı genel durumu: $overall.\n" + lines.joinToString("\n"), sources)
    }

    private fun claimVerdictText(claim: CareerNewsClaim): String {
        val status = when (claim.verdict()) {
            CareerClaimVerdict.NEEDS_EVIDENCE -> "Kanıt yok; doğrulama yapılamaz."
            CareerClaimVerdict.CONTEXT_MISSING -> "Yalnız bağlam/ikincil kanıt var; birincil kaynak kanıtı bulunmuyor."
            CareerClaimVerdict.SUPPORTS_PENDING_REVIEW -> "Birincil kaynak destekliyor; açık insan onayı bekleniyor."
            CareerClaimVerdict.CONTRADICTION_PENDING_REVIEW -> "Birincil kaynak çelişiyor; insan incelemesi bekleniyor."
            CareerClaimVerdict.CONFLICTING_EVIDENCE -> "Destekleyen ve çelişen kanıtlar birlikte mevcut."
            CareerClaimVerdict.VERIFIED -> "Birincil kanıt ve açık insan onayı mevcut: doğrulandı."
            CareerClaimVerdict.REFUTED -> "Birincil karşıt kanıt ve açık insan kararı mevcut: çürütüldü."
        }
        val review = claim.reviewDecision?.let { decision ->
            " İnsan kararı: ${reviewDecisionText(decision)} (${claim.reviewedAt ?: "zaman yok"}). Not: ${claim.reviewNote}"
        } ?: " İnsan kararı henüz kaydedilmedi."
        return status + review
    }

    private fun evidenceAnswer(
        record: CareerNewsVerificationRecord,
        claim: CareerNewsClaim?
    ): CareerNewsQAAnswer {
        val claims = claim?.let { listOf(it) } ?: record.claims
        if (claims.isEmpty()) {
            return answered(
                "Henüz iddia veya kanıt eklenmedi. Asıl haber kaydı: ${record.originalSourceUrl}",
                listOf(originalSource(record))
            )
        }
        val evidence = claims.flatMap { current ->
            current.evidence.map { current.text to it }
        }
        if (evidence.isEmpty()) {
            return answered("Seçilen iddia için henüz kanıt kaydedilmedi. Birincil kaynak veya güvenilir bağımsız yayın eklenmeli.")
        }
        val lines = evidence.map { (claimText, item) ->
            "İddia: $claimText\n${item.publisher} · ${sourceTypeText(item.sourceType)} · ${stanceText(item.stance)}\n" +
                "Alıntı: ${item.excerpt}\nKontrol: ${item.checkedAt} · Yayın: ${item.publishedAt ?: "bilinmiyor"}"
        }
        return answered(lines.joinToString("\n\n"), evidence.mapNotNull { evidenceSource(it.second) })
    }

    private fun humanReviewAnswer(
        record: CareerNewsVerificationRecord,
        claim: CareerNewsClaim?
    ): CareerNewsQAAnswer {
        val claims = claim?.let { listOf(it) } ?: record.claims
        if (claims.isEmpty()) return answered("Henüz insan incelemesi yapılabilecek bir iddia kaydedilmedi.")
        val lines = claims.map { item ->
            val current = item.reviewDecision?.let {
                "Güncel karar: ${reviewDecisionText(it)} · ${item.reviewedAt ?: "zaman yok"} · ${item.reviewNote}"
            } ?: "Güncel insan kararı yok."
            val history = item.reviewHistory.map {
                "Eski karar: ${reviewDecisionText(it.decision)} · ${it.reviewedAt} · ${it.note}"
            }
            "İddia: ${item.text}\n$current\n" + history.joinToString("\n") +
                "\nNot: İnceleyen kişinin adı bu kayıtta tutulmuyor."
        }
        return answered(
            lines.joinToString("\n\n"),
            claims.flatMap { it.evidence }.mapNotNull { evidenceSource(it) }
        )
    }

    private fun correctionAnswer(
        record: CareerNewsVerificationRecord,
        claimId: String?
    ): CareerNewsQAAnswer {
        val corrections = record.correctionHistory
            .filter { claimId == null || it.claimId == claimId }
            .takeLast(20)
        if (corrections.isEmpty()) return answered("Bu haber/iddia için kayıtlı bir düzeltme geçmişi yok.")
        val lines = corrections.map {
            "Önce: ${it.previousText}\nSonra: ${it.revisedText}\nZaman: ${it.correctedAt}\nAçıklama: ${it.explanation}"
        }
        val sources = corrections.mapNotNull {
            if (CareerNewsVerificationPolicy.validateHttpsUrl(it.sourceUrl) == null) {
                CareerNewsQASource("Düzeltme kaynağı", it.sourceUrl, it.explanation, it.correctedAt)
            } else null
        }
        return answered(lines.joinToString("\n\n"), sources)
    }

    private fun originalSource(record: CareerNewsVerificationRecord) = CareerNewsQASource(
        label = record.publisher,
        url = record.originalSourceUrl,
        excerpt = record.headline,
        detail = "Asıl haber kaydı"
    )

    private fun evidenceSource(evidence: CareerNewsEvidence): CareerNewsQASource? {
        if (CareerNewsVerificationPolicy.validateHttpsUrl(evidence.sourceUrl) != null) return null
        return CareerNewsQASource(
            label = evidence.publisher + " · " + sourceTypeText(evidence.sourceType) + " · " + stanceText(evidence.stance),
            url = evidence.sourceUrl,
            excerpt = evidence.excerpt,
            detail = "Kontrol: ${evidence.checkedAt} · Yayın: ${evidence.publishedAt ?: "bilinmiyor"}"
        )
    }

    private fun sourceTypeText(value: CareerEvidenceSourceType): String = when (value) {
        CareerEvidenceSourceType.PRIMARY_SOURCE -> "Birincil/resmî"
        CareerEvidenceSourceType.INDEPENDENT_REPORTING -> "Bağımsız haber"
        CareerEvidenceSourceType.SECONDARY -> "İkincil"
        CareerEvidenceSourceType.UNKNOWN -> "Türü bilinmiyor"
    }

    private fun stanceText(value: CareerEvidenceStance): String = when (value) {
        CareerEvidenceStance.SUPPORTS -> "Destekliyor"
        CareerEvidenceStance.CONTRADICTS -> "Çelişiyor"
        CareerEvidenceStance.CONTEXT_ONLY -> "Bağlam"
    }

    private fun reviewDecisionText(value: CareerHumanReviewDecision): String = when (value) {
        CareerHumanReviewDecision.CONFIRM -> "Doğrulandı"
        CareerHumanReviewDecision.REFUTE -> "Çürütüldü"
        CareerHumanReviewDecision.INCONCLUSIVE -> "Kararsız"
    }

    private fun containsPersonalData(raw: String, normalized: String): Boolean {
        val digits = raw.filter { it.isDigit() }
        val hasNationalId = Regex("\\b\\d(?:[\\s-]?\\d){10}\\b").containsMatchIn(raw) ||
            digits.length == 11
        val hasPhone = Regex("(?:\\+?90[\\s().-]*)?0?5\\d{2}[\\s().-]*\\d{3}[\\s().-]*\\d{2}[\\s().-]*\\d{2}").containsMatchIn(raw)
        val hasEmail = Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE).containsMatchIn(raw)
        val personalPhrase = listOf(
            "benim kpss", "kpss puanim", "puanimi", "cv metnim", "cv'm",
            "ozgecmisim", "e devlet sifrem", "sifrem", "kimlik numaram", "telefon numaram"
        ).any { normalized.contains(it) }
        return hasNationalId || hasPhone || hasEmail || personalPhrase
    }

    private fun normalize(value: String): String = value
        .lowercase(Locale("tr", "TR"))
        .replace('ı', 'i')
        .replace('ç', 'c')
        .replace('ğ', 'g')
        .replace('ö', 'o')
        .replace('ş', 's')
        .replace('ü', 'u')
        .replace(Regex("[^\\p{L}\\p{N} ]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()

    private fun tokens(value: String): Set<String> = normalize(value)
        .split(' ')
        .filter { it.length > 2 && it !in stopWords }
        .toSet()

    private fun has(value: String, vararg phrases: String): Boolean =
        phrases.any { value.contains(normalize(it)) }

    private fun answered(
        value: String,
        sources: List<CareerNewsQASource> = emptyList()
    ) = CareerNewsQAAnswer(CareerNewsQAStatus.ANSWERED, value, sources.distinctBy { it.url })

    private fun abstain(value: String) =
        CareerNewsQAAnswer(CareerNewsQAStatus.ABSTAINED, value)
}
