package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerNewsVerificationTest {
    private val collectedAt = "2026-11-25T14:00:00Z"
    private val checkedAt = "2026-11-25T14:05:00Z"
    private val reviewedAt = "2026-11-25T14:10:00Z"

    private fun record(
        publishedAt: String? = null,
        collectedAt: String? = this.collectedAt,
        firstSeenAt: String? = this.collectedAt
    ) = CareerNewsVerificationRecord(
        storyId = CareerNewsVerificationRecord.stableStoryId(
            "https://kurum.gov.tr/duyuru/1",
            "Kurum yeni personel alımını duyurdu"
        ),
        headline = "Kurum yeni personel alımını duyurdu",
        publisher = "Örnek kurum",
        originalSourceUrl = "https://kurum.gov.tr/duyuru/1",
        publishedAt = publishedAt,
        collectedAt = collectedAt,
        firstSeenAt = firstSeenAt
    )

    private fun claim() = CareerNewsClaim(
        id = "claim-1",
        text = "Kurum 12 teknisyen alımı yapacağını duyurdu."
    )

    private fun evidence(
        id: String = "evidence-1",
        stance: CareerEvidenceStance = CareerEvidenceStance.SUPPORTS,
        sourceType: CareerEvidenceSourceType = CareerEvidenceSourceType.PRIMARY_SOURCE,
        url: String = "https://kurum.gov.tr/duyuru/1"
    ) = CareerNewsEvidence(
        id = id,
        publisher = "Örnek kurum",
        sourceUrl = url,
        sourceType = sourceType,
        stance = stance,
        excerpt = "Duyuruda 12 teknisyen alımı yapılacağı açıkça yazıyor.",
        publishedAt = null,
        checkedAt = checkedAt
    )

    @Test
    fun primaryEvidenceAloneNeverProducesVerifiedVerdict() {
        val withClaim = record().addClaim(claim())!!
        val withEvidence = withClaim.addEvidence("claim-1", evidence())!!
        assertEquals(
            CareerClaimVerdict.SUPPORTS_PENDING_REVIEW,
            withEvidence.claims.single().verdict()
        )
        assertEquals(CareerNewsVerdict.REVIEW_PENDING, withEvidence.verdict())
    }

    @Test
    fun verifiedRequiresPrimaryEvidenceAndExplicitHumanConfirmation() {
        val withEvidence = record().addClaim(claim())!!
            .addEvidence("claim-1", evidence())!!
        val reviewed = withEvidence.reviewClaim(
            "claim-1",
            CareerHumanReviewDecision.CONFIRM,
            reviewedAt,
            "Resmî duyuru metniyle karşılaştırıldı."
        )!!
        assertEquals(CareerClaimVerdict.VERIFIED, reviewed.claims.single().verdict())
        assertEquals(CareerNewsVerdict.VERIFIED, reviewed.verdict())
    }

    @Test
    fun secondaryEvidenceCannotVerifyAClaimEvenAfterConfirmation() {
        val reviewed = record().addClaim(claim())!!
            .addEvidence(
                "claim-1",
                evidence(sourceType = CareerEvidenceSourceType.SECONDARY)
            )!!
            .reviewClaim(
                "claim-1",
                CareerHumanReviewDecision.CONFIRM,
                reviewedAt,
                "İkincil kaynak resmî kaynağın yerini tutmuyor."
            )!!
        assertEquals(CareerClaimVerdict.CONTEXT_MISSING, reviewed.claims.single().verdict())
        assertEquals(CareerNewsVerdict.CONTEXT_MISSING, reviewed.verdict())
    }

    @Test
    fun supportingAndContradictingEvidenceAlwaysCreatesConflict() {
        val reviewed = record().addClaim(claim())!!
            .addEvidence("claim-1", evidence())!!
            .addEvidence(
                "claim-1",
                evidence(
                    id = "evidence-2",
                    stance = CareerEvidenceStance.CONTRADICTS,
                    url = "https://resmigazete.gov.tr/duyuru/duzeltme"
                )
            )!!
            .reviewClaim("claim-1", CareerHumanReviewDecision.CONFIRM, reviewedAt, "Kanıtlar karşılaştırıldı; çelişki kaydedildi.")!!
        assertEquals(
            CareerClaimVerdict.CONFLICTING_EVIDENCE,
            reviewed.claims.single().verdict()
        )
        assertEquals(CareerNewsVerdict.CONFLICTING_EVIDENCE, reviewed.verdict())
    }

    @Test
    fun refutedNeedsPrimaryContradictionAndExplicitHumanReview() {
        val withEvidence = record().addClaim(claim())!!
            .addEvidence(
                "claim-1",
                evidence(stance = CareerEvidenceStance.CONTRADICTS)
            )!!
        assertEquals(
            CareerClaimVerdict.CONTRADICTION_PENDING_REVIEW,
            withEvidence.claims.single().verdict()
        )
        val reviewed = withEvidence.reviewClaim(
            "claim-1",
            CareerHumanReviewDecision.REFUTE,
            reviewedAt,
            "Resmî düzeltme duyurusu bu iddiayı açıkça çürütüyor."
        )!!
        assertEquals(CareerClaimVerdict.REFUTED, reviewed.claims.single().verdict())
        assertEquals(CareerNewsVerdict.REFUTED, reviewed.verdict())
    }

    @Test
    fun addingEvidenceInvalidatesPreviousHumanReview() {
        val reviewed = record().addClaim(claim())!!
            .addEvidence("claim-1", evidence())!!
            .reviewClaim("claim-1", CareerHumanReviewDecision.CONFIRM, reviewedAt, "İlk kanıt resmî kaynakla karşılaştırıldı.")!!
        assertEquals(CareerClaimVerdict.VERIFIED, reviewed.claims.single().verdict())

        val updated = reviewed.addEvidence(
            "claim-1",
            evidence(
                id = "evidence-2",
                stance = CareerEvidenceStance.CONTRADICTS,
                url = "https://resmigazete.gov.tr/duyuru/duzeltme"
            )
        )!!
        assertNull(updated.claims.single().reviewedAt)
        assertEquals(
            CareerClaimVerdict.CONFLICTING_EVIDENCE,
            updated.claims.single().verdict()
        )
    }

    @Test
    fun correctionHistoryIsAppendOnlyAndResetsReview() {
        val reviewed = record().addClaim(claim())!!
            .addEvidence("claim-1", evidence())!!
            .reviewClaim("claim-1", CareerHumanReviewDecision.CONFIRM, reviewedAt, "İddia resmî metinle karşılaştırıldı.")!!
        val revised = reviewed.correctClaim(
            revisedClaim = reviewed.claims.single().copy(
                text = "Kurum 10 teknisyen alımı yapacağını duyurdu."
            ),
            correctedAt = "2026-11-25T14:20:00Z",
            explanation = "İlk metindeki kontenjan sayısı yanlış okunmuş.",
            sourceUrl = "https://kurum.gov.tr/duyuru/duzeltme"
        )!!

        assertEquals(1, revised.correctionHistory.size)
        assertEquals(CareerClaimVerdict.VERIFIED, revised.correctionHistory.single().previousVerdict)
        assertEquals(
            CareerClaimVerdict.NEEDS_EVIDENCE,
            revised.correctionHistory.single().newVerdict
        )
        assertTrue(revised.claims.single().evidence.isEmpty())
        assertNull(revised.claims.single().reviewedAt)
        assertEquals(0, reviewed.correctionHistory.size)
        val restored = CareerNewsVerificationRecord.fromJson(revised.toJson().toString())
        assertNotNull(restored)
        assertEquals(revised, restored)
        assertEquals("Kurum 12 teknisyen alımı yapacağını duyurdu.", restored!!.correctionHistory.single().previousText)
        assertEquals(1, restored.correctionHistory.single().previousEvidence.size)
        assertEquals(CareerHumanReviewDecision.CONFIRM, restored.correctionHistory.single().previousReviewDecision)
        assertEquals("İddia resmî metinle karşılaştırıldı.", restored.correctionHistory.single().previousReviewNote)
    }

    @Test
    fun publishedAtRemainsNullAndRoundTripsWithEvidenceAndCorrections() {
        val value = record().addClaim(claim())!!
            .addEvidence("claim-1", evidence())!!
            .reviewClaim("claim-1", CareerHumanReviewDecision.CONFIRM, reviewedAt, "Yayın tarihi ayrı tutuldu.")!!
        val restored = CareerNewsVerificationRecord.fromJson(value.toJson().toString())
        assertNotNull(restored)
        assertNull(restored!!.publishedAt)
        assertEquals(value, restored)
    }

    @Test
    fun rejectsHttpUrlsAndInvalidUtcTimestamps() {
        val invalidStory = record().copy(originalSourceUrl = "http://kurum.gov.tr/duyuru/1")
        assertFalse(invalidStory.validationErrors().isEmpty())

        val invalidEvidence = evidence().copy(
            sourceUrl = "http://kurum.gov.tr/duyuru/1",
            checkedAt = "not-a-date"
        )
        val value = record().addClaim(claim())!!.copy(
            claims = listOf(claim().copy(evidence = listOf(invalidEvidence)))
        )
        assertFalse(value.validationErrors().isEmpty())
        assertNull(CareerNewsVerificationRecord.fromJson(value.toJson().toString()))
    }

    @Test
    fun stableStoryIdIgnoresFragmentAndTrailingSlash() {
        val first = CareerNewsVerificationRecord.stableStoryId(
            "https://kurum.gov.tr/duyuru/1#detay",
            "  Kurum   personel alımı  "
        )
        val second = CareerNewsVerificationRecord.stableStoryId(
            "https://kurum.gov.tr/duyuru/1/",
            "kurum personel alımı"
        )
        val revisedHeadline = CareerNewsVerificationRecord.stableStoryId(
            "https://kurum.gov.tr/duyuru/1",
            "Kurum duyuru başlığını güncelledi"
        )
        assertEquals(first, second)
        assertEquals("Başlık değişse de aynı kaynak sayfası aynı haber kimliğini korur.", first, revisedHeadline)
    }
    @Test
    fun evidencePublicationTimestampMustBeValidUtcWhenProvided() {
        val invalid = evidence().copy(publishedAt = "2026-02-30T10:00:00Z")
        assertNull(record().addClaim(claim())!!.addEvidence("claim-1", invalid))
        val valid = evidence().copy(publishedAt = "2026-11-20T10:00:00Z")
        assertNotNull(record().addClaim(claim())!!.addEvidence("claim-1", valid))
    }

    @Test
    fun rejectsImpossibleDatesAndEvidenceExcerptsWithPersonalIdentifiers() {
        assertFalse(CareerNewsVerificationPolicy.isUtcTimestamp("2026-02-30T14:00:00Z"))
        assertFalse(CareerNewsVerificationPolicy.isUtcTimestamp("2026-11-25T25:00:00Z"))
        assertFalse(CareerNewsVerificationPolicy.validateHttpsUrl("https://192.168.1.20/").isNullOrBlank())
        assertFalse(CareerNewsVerificationPolicy.validateHttpsUrl("https://trusted.gov.tr@evil.example/").isNullOrBlank())
        assertTrue(CareerNewsVerificationPolicy.sanitizeEvidenceExcerpt(
            "İletişim: test@example.com, 12345678901"
        ).contains("[KIMLIK NUMARASI]"))

        val personalDataEvidence = evidence().copy(
            excerpt = "Aday kimlik numarası 12345678901 listede yayımlandı."
        )
        val value = record().addClaim(claim())!!.copy(
            claims = listOf(claim().copy(evidence = listOf(personalDataEvidence)))
        )
        assertFalse(value.validationErrors().isEmpty())
    }

    @Test
    fun humanReviewRequiresRationaleAndNoOpCorrectionIsRejected() {
        val withEvidence = record().addClaim(claim())!!
            .addEvidence("claim-1", evidence())!!
        assertNull(
            withEvidence.reviewClaim(
                "claim-1",
                CareerHumanReviewDecision.CONFIRM,
                reviewedAt
            )
        )
        assertNull(withEvidence.correctClaim(
            revisedClaim = withEvidence.claims.single(),
            correctedAt = "2026-11-25T14:20:00Z",
            explanation = "Metin değişmedi.",
            sourceUrl = "https://kurum.gov.tr/duyuru/1"
        ))
    }

    @Test
    fun unknownCollectionAndFirstSeenTimesStayNull() {
        val value = record(collectedAt = null, firstSeenAt = null)
        assertTrue(value.validationErrors().isEmpty())
        val restored = CareerNewsVerificationRecord.fromJson(value.toJson().toString())
        assertNotNull(restored)
        assertNull(restored!!.publishedAt)
        assertNull(restored.collectedAt)
        assertNull(restored.firstSeenAt)
    }

    @Test
    fun primaryEvidenceRequiresAnAllowlistedOfficialSource() {
        assertTrue(CareerNewsVerificationPolicy.isPrimarySourceUrl("https://www.dsi.gov.tr/duyurular"))
        assertFalse(CareerNewsVerificationPolicy.isPrimarySourceUrl("https://example.com/news"))
        assertFalse(CareerNewsVerificationPolicy.isPrimarySourceUrl("https://example.edu.tr/duyuru"))
        assertFalse(CareerNewsVerificationPolicy.isPrimarySourceUrl("https://www.kariyer.net/is-ilanlari"))

        val untrustedPrimary = evidence(
            sourceType = CareerEvidenceSourceType.PRIMARY_SOURCE,
            url = "https://example.com/news"
        )
        assertNull(record().addClaim(claim())!!.addEvidence("claim-1", untrustedPrimary))
    }

    @Test
    fun rejectsPersonalIdentifiersInClaimText() {
        val withPersonalData = record().addClaim(
            claim().copy(text = "Başvuranın kimlik numarası 12345678901.")
        )
        assertNull(withPersonalData)
    }

    @Test
    fun rejectsPersonalIdentifiersInReviewAndCorrectionNotes() {
        val reviewed = record().addClaim(claim())!!
            .addEvidence("claim-1", evidence())!!
        assertNull(reviewed.reviewClaim(
            "claim-1",
            CareerHumanReviewDecision.CONFIRM,
            reviewedAt,
            "İnceleyen telefon 05321234567"
        ))
    }

    @Test
    fun rejectsOversizedEvidenceRecords() {
        val oversized = record().addClaim(claim())!!.copy(
            claims = listOf(
                claim().copy(
                    evidence = (1..10).map { index ->
                        evidence(id = "evidence-" + index).copy(excerpt = "A".repeat(2000))
                    }
                )
            )
        )
        assertFalse(oversized.validationErrors().isEmpty())
    }


    @Test
    fun replacingHumanReviewArchivesPreviousDecisionAndEvidenceSnapshot() {
        val reviewed = record().addClaim(claim())!!
            .addEvidence("claim-1", evidence())!!
            .reviewClaim("claim-1", CareerHumanReviewDecision.CONFIRM, reviewedAt,
                "İlk resmî inceleme gerekçesi.")!!
        val updated = reviewed.reviewClaim(
            "claim-1",
            CareerHumanReviewDecision.INCONCLUSIVE,
            "2026-11-25T14:20:00Z",
            "Yeni değerlendirme kararsız kaldı."
        )!!

        val current = updated.claims.single()
        assertEquals(CareerHumanReviewDecision.INCONCLUSIVE, current.reviewDecision)
        assertEquals(1, current.reviewHistory.size)
        assertEquals(CareerHumanReviewDecision.CONFIRM, current.reviewHistory.single().decision)
        assertEquals(reviewedAt, current.reviewHistory.single().reviewedAt)
        assertEquals("İlk resmî inceleme gerekçesi.", current.reviewHistory.single().note)
        assertEquals(listOf("evidence-1"), current.reviewHistory.single().evidenceIds)

        val restored = CareerNewsVerificationRecord.fromJson(updated.toJson().toString())
        assertEquals(updated, restored)
    }

    @Test
    fun addingEvidenceArchivesPriorReviewBeforeResettingIt() {
        val reviewed = record().addClaim(claim())!!
            .addEvidence("claim-1", evidence())!!
            .reviewClaim("claim-1", CareerHumanReviewDecision.CONFIRM, reviewedAt,
                "İlk resmî inceleme gerekçesi.")!!
        val updated = reviewed.addEvidence(
            "claim-1",
            evidence(
                id = "evidence-2",
                stance = CareerEvidenceStance.CONTRADICTS,
                url = "https://resmigazete.gov.tr/duyuru/duzeltme"
            )
        )!!

        val current = updated.claims.single()
        assertNull(current.reviewDecision)
        assertEquals(1, current.reviewHistory.size)
        assertEquals(CareerHumanReviewDecision.CONFIRM, current.reviewHistory.single().decision)
        assertEquals(listOf("evidence-1"), current.reviewHistory.single().evidenceIds)
        assertEquals(CareerClaimVerdict.CONFLICTING_EVIDENCE, current.verdict())
    }
}
