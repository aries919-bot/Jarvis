package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerNewsQuestionAnswerTest {
    private val storyUrl = "https://www.dsi.gov.tr/duyurular/teknik-personel"
    private val reviewTime = "2026-11-25T14:10:00Z"

    private fun record(
        publishedAt: String? = null,
        claims: List<CareerNewsClaim> = emptyList(),
        corrections: List<CareerNewsCorrection> = emptyList()
    ) = CareerNewsVerificationRecord(
        storyId = "story-qa-1",
        headline = "DSİ 12 elektrik teknisyeni alımı yapacağını duyurdu",
        publisher = "Devlet Su İşleri",
        originalSourceUrl = storyUrl,
        publishedAt = publishedAt,
        collectedAt = "2026-11-25T14:00:00Z",
        firstSeenAt = "2026-11-25T13:55:00Z",
        claims = claims,
        correctionHistory = corrections
    )

    private fun evidence(
        id: String = "evidence-1",
        stance: CareerEvidenceStance = CareerEvidenceStance.SUPPORTS,
        type: CareerEvidenceSourceType = CareerEvidenceSourceType.PRIMARY_SOURCE
    ) = CareerNewsEvidence(
        id = id,
        publisher = "Devlet Su İşleri",
        sourceUrl = storyUrl,
        sourceType = type,
        stance = stance,
        excerpt = "Resmî duyuruda 12 elektrik teknisyeni alımı yapılacağı belirtiliyor.",
        publishedAt = null,
        checkedAt = "2026-11-25T14:05:00Z"
    )

    private fun claim(
        evidence: List<CareerNewsEvidence> = emptyList(),
        review: CareerHumanReviewDecision? = null,
        reviewedAt: String? = null,
        note: String = ""
    ) = CareerNewsClaim(
        id = "claim-1",
        text = "DSİ 12 elektrik teknisyeni alımı yapacak.",
        evidence = evidence,
        reviewDecision = review,
        reviewedAt = reviewedAt,
        reviewNote = note
    )

    @Test
    fun publicationDateNullNeverFallsBackToCollectionTime() {
        val result = CareerNewsQuestionAnswer.answer(record(), "Bu haberin yayın tarihi nedir?")
        assertTrue(result.answer.contains("yayın tarihi kaynak kaydında belirtilmemiş"))
        assertTrue(result.answer.contains("2026-11-25T14:00:00Z"))
        assertFalse(result.answer.contains("Yayın tarihi: 2026-11-25T14:00:00Z"))
    }

    @Test
    fun collectionAndFirstSeenTimesAreAnsweredSeparately() {
        val collected = CareerNewsQuestionAnswer.answer(record(), "Aries bu haberi ne zaman topladı?")
        val firstSeen = CareerNewsQuestionAnswer.answer(record(), "İlk görülme zamanı nedir?")
        assertTrue(collected.answer.contains("2026-11-25T14:00:00Z"))
        assertTrue(firstSeen.answer.contains("2026-11-25T13:55:00Z"))
        assertTrue(firstSeen.answer.contains("yayın tarihi yerine kullanılamaz"))
    }

    @Test
    fun sourceQuestionReturnsOnlyTheOriginalHttpsSource() {
        val result = CareerNewsQuestionAnswer.answer(record(), "Bu haberin asıl kaynağı kim?")
        assertEquals(CareerNewsQAStatus.ANSWERED, result.status)
        assertEquals(listOf(storyUrl), result.sources.map { it.url })
        assertTrue(result.answer.contains("Devlet Su İşleri"))
    }

    @Test
    fun primarySupportWithoutHumanDecisionIsNotCalledVerified() {
        val result = CareerNewsQuestionAnswer.answer(
            record(claims = listOf(claim(evidence = listOf(evidence())))),
            "Bu iddia doğru mu?"
        )
        assertTrue(result.answer.contains("İnsan incelemesi bekliyor"))
        assertTrue(result.answer.contains("açık insan onayı bekleniyor"))
        assertFalse(result.answer.contains("Tüm iddialar doğrulandı"))
    }

    @Test
    fun primarySupportAndHumanConfirmationReturnsVerified() {
        val verified = claim(
            evidence = listOf(evidence()),
            review = CareerHumanReviewDecision.CONFIRM,
            reviewedAt = reviewTime,
            note = "Resmî duyuruyla karşılaştırıldı."
        )
        val result = CareerNewsQuestionAnswer.answer(record(claims = listOf(verified)), "Bu haber doğrulandı mı?")
        assertTrue(result.answer.contains("Tüm iddialar doğrulandı"))
        assertTrue(result.answer.contains("Birincil kanıt ve açık insan onayı"))
    }

    @Test
    fun contradictoryEvidenceIsShownAsConflict() {
        val conflicting = claim(evidence = listOf(
            evidence(id = "support", stance = CareerEvidenceStance.SUPPORTS),
            evidence(id = "contradiction", stance = CareerEvidenceStance.CONTRADICTS)
        ))
        val result = CareerNewsQuestionAnswer.answer(record(claims = listOf(conflicting)), "İddia doğru mu?")
        assertTrue(result.answer.contains("Çelişkili kanıt"))
        assertTrue(result.sources.size >= 2)
    }

    @Test
    fun secondaryEvidenceOnlyIsContextMissing() {
        val contextual = claim(evidence = listOf(evidence(type = CareerEvidenceSourceType.SECONDARY)))
        val result = CareerNewsQuestionAnswer.answer(record(claims = listOf(contextual)), "İddianın durumu nedir?")
        assertTrue(result.answer.contains("Bağlam eksik"))
        assertTrue(result.answer.contains("birincil kaynak kanıtı bulunmuyor"))
    }

    @Test
    fun evidenceQuestionReturnsExcerptStanceAndSafeUrl() {
        val item = evidence()
        val result = CareerNewsQuestionAnswer.answer(record(claims = listOf(claim(evidence = listOf(item)))), "Hangi kanıt bu iddiayı destekliyor?")
        assertTrue(result.answer.contains("Resmî duyuruda 12 elektrik teknisyeni"))
        assertTrue(result.answer.contains("Destekliyor"))
        assertEquals(listOf(storyUrl), result.sources.map { it.url })
    }

    @Test
    fun reviewerIdentityIsNotInvented() {
        val reviewed = claim(evidence = listOf(evidence()), review = CareerHumanReviewDecision.CONFIRM,
            reviewedAt = reviewTime, note = "Resmî kaynak okundu.")
        val result = CareerNewsQuestionAnswer.answer(record(claims = listOf(reviewed)), "Kim doğruladı?")
        assertTrue(result.answer.contains("İnceleyen kişinin adı bu kayıtta tutulmuyor"))
        assertTrue(result.answer.contains("Resmî kaynak okundu"))
    }

    @Test
    fun correctionHistoryReturnsBeforeAfterAndSource() {
        val correction = CareerNewsCorrection(
            id = "correction-1",
            claimId = "claim-1",
            correctedAt = reviewTime,
            previousVerdict = CareerClaimVerdict.VERIFIED,
            newVerdict = CareerClaimVerdict.NEEDS_EVIDENCE,
            previousText = "DSİ 12 elektrik teknisyeni alacak.",
            revisedText = "DSİ 10 elektrik teknisyeni alacak.",
            previousEvidence = emptyList(),
            previousReviewDecision = CareerHumanReviewDecision.CONFIRM,
            previousReviewedAt = reviewTime,
            previousReviewNote = "İlk duyuru okundu.",
            explanation = "Kurum daha sonra kontenjanı düzeltti.",
            sourceUrl = "https://www.dsi.gov.tr/duyurular/duzeltme"
        )
        val result = CareerNewsQuestionAnswer.answer(record(corrections = listOf(correction)), "Düzeltme geçmişi nedir?")
        assertTrue(result.answer.contains("12 elektrik teknisyeni"))
        assertTrue(result.answer.contains("10 elektrik teknisyeni"))
        assertEquals(correction.sourceUrl, result.sources.single().url)
    }

    @Test
    fun multipleClaimsAskForClarificationInsteadOfMixingEvidence() {
        val first = claim().copy(text = "DSİ 12 elektrik teknisyeni alacak.")
        val second = claim().copy(id = "claim-2", text = "Başvuru 10 Aralık 2026 tarihinde sona erecek.")
        val result = CareerNewsQuestionAnswer.answer(record(claims = listOf(first, second)), "Bu iddia neden bekliyor?")
        assertEquals(CareerNewsQAStatus.NEEDS_CLARIFICATION, result.status)
        assertTrue(result.answer.contains("birden fazla ayrı iddia"))
    }

    @Test
    fun claimTermsSelectOnlyTheMatchingClaim() {
        val first = claim().copy(text = "DSİ 12 elektrik teknisyeni alacak.")
        val second = claim().copy(id = "claim-2", text = "Başvuru 10 Aralık 2026 tarihinde sona erecek.")
        val result = CareerNewsQuestionAnswer.answer(record(claims = listOf(first, second)), "Elektrik teknisyeni alımı iddiası nedir?")
        assertTrue(result.answer.contains("12 elektrik teknisyeni"))
        assertFalse(result.answer.contains("10 Aralık"))
    }

    @Test
    fun targetedVerdictOnMultipleClaimsDoesNotReturnWholeStoryVerdict() {
        val first = claim(
            evidence = listOf(evidence()),
            review = CareerHumanReviewDecision.CONFIRM,
            reviewedAt = reviewTime,
            note = "Elektrik alım iddiası kaynakla karşılaştırıldı."
        )
        val second = claim().copy(
            id = "claim-2",
            text = "Başvuru 10 Aralık 2026 tarihinde sona erecek."
        )
        val result = CareerNewsQuestionAnswer.answer(
            record(claims = listOf(first, second)),
            "Elektrik teknisyeni alımı iddiası doğru mu?"
        )
        assertTrue(result.answer.contains("Elektrik teknisyeni alımı"))
        assertFalse(result.answer.contains("10 Aralık 2026"))
    }

    @Test
    fun ambiguousVerdictOnMultipleClaimsAsksWhichClaim() {
        val first = claim().copy(text = "DSİ 12 elektrik teknisyeni alacak.")
        val second = claim().copy(id = "claim-2", text = "Başvuru 10 Aralık 2026 tarihinde sona erecek.")
        val result = CareerNewsQuestionAnswer.answer(record(claims = listOf(first, second)), "Bu iddia doğru mu?")
        assertEquals(CareerNewsQAStatus.NEEDS_CLARIFICATION, result.status)
    }

    @Test
    fun broadWholeNewsQuestionStillSummarizesAllClaims() {
        val first = claim(
            evidence = listOf(evidence()),
            review = CareerHumanReviewDecision.CONFIRM,
            reviewedAt = reviewTime,
            note = "İlk iddia doğrulandı."
        )
        val second = claim().copy(id = "claim-2", text = "Başvuru 10 Aralık 2026 tarihinde sona erecek.")
        val result = CareerNewsQuestionAnswer.answer(record(claims = listOf(first, second)), "Bu haberin genel durumu nedir?")
        assertTrue(result.answer.contains("İnsan incelemesi bekliyor"))
        assertTrue(result.answer.contains("Başvuru 10 Aralık 2026"))
    }

    @Test
    fun emptyQuestionAndPersonalDataAreAbstained() {
        val empty = CareerNewsQuestionAnswer.answer(record(), "  ")
        val personal = CareerNewsQuestionAnswer.answer(record(), "Benim KPSS puanım 72 ve telefonum 05321234567")
        assertEquals(CareerNewsQAStatus.ABSTAINED, empty.status)
        assertEquals(CareerNewsQAStatus.ABSTAINED, personal.status)
    }

    @Test
    fun noClaimsDoesNotInventAStoryVerdict() {
        val result = CareerNewsQuestionAnswer.answer(record(), "Bu haber doğru mu?")
        assertTrue(result.answer.contains("ayrı bir iddia kaydedilmedi"))
        assertTrue(result.sources.any { it.url == storyUrl })
    }
}
