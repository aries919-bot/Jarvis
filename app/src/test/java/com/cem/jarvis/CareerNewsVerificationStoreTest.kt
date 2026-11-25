package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerNewsVerificationStoreTest {
    @Test
    fun createsSeparateStoryRecordWithUnknownPublishedAt() {
        var raw: String? = null
        val store = CareerNewsVerificationStore.forTests(
            readRaw = { raw },
            writeRaw = { raw = it; true }
        )

        val record = store.getOrCreate(
            headline = "Kurum personel duyurusu",
            publisher = "Örnek kurum",
            originalSourceUrl = "https://kurum.gov.tr/duyuru/1",
            collectedAtMillis = 1_795_600_000_000L,
            firstSeenAtMillis = 1_795_600_001_000L
        )

        assertNotNull(record)
        assertNull(record!!.publishedAt)
        assertEquals(1, store.all()!!.size)
        assertEquals(record, store.find(record.storyId))
    }

    @Test
    fun updateReplacesSameStoryWithoutDuplicatingIt() {
        var raw: String? = null
        val store = CareerNewsVerificationStore.forTests({ raw }, { raw = it; true })
        val initial = store.getOrCreate(
            "Kurum personel duyurusu",
            "Örnek kurum",
            "https://kurum.gov.tr/duyuru/1",
            1_795_600_000_000L,
            1_795_600_001_000L
        )!!
        val updated = initial.copy(headline = "Kurum personel duyurusu · güncellendi")
        assertTrue(store.save(updated))
        assertEquals(1, store.all()!!.size)
        assertEquals(updated, store.find(initial.storyId))
    }

    @Test
    fun malformedArchiveIsNotSilentlyOverwritten() {
        var raw: String? = "this is not json"
        var writes = 0
        val store = CareerNewsVerificationStore.forTests(
            readRaw = { raw },
            writeRaw = { writes++; raw = it; true }
        )
        assertNull(store.all())
        assertNull(
            store.getOrCreate(
                "Kurum personel duyurusu",
                "Örnek kurum",
                "https://kurum.gov.tr/duyuru/1",
                0L,
                0L
            )
        )
        assertEquals("this is not json", raw)
        assertEquals(0, writes)
    }

    @Test
    fun failedWriteIsReportedAndDoesNotClaimPersistence() {
        val store = CareerNewsVerificationStore.forTests({ null }, { false })
        val record = CareerNewsVerificationRecord(
            storyId = CareerNewsVerificationRecord.stableStoryId(
                "https://kurum.gov.tr/duyuru/1",
                "Kurum personel duyurusu"
            ),
            headline = "Kurum personel duyurusu",
            publisher = "Örnek kurum",
            originalSourceUrl = "https://kurum.gov.tr/duyuru/1",
            publishedAt = null,
            collectedAt = "2026-11-25T14:00:00Z",
            firstSeenAt = "2026-11-25T14:00:00Z"
        )
        assertFalse(store.save(record))
        assertTrue(store.all()!!.isEmpty())
    }
    @Test
    fun existingEvidenceAndCorrectionHistoryCannotBeSilentlyRemoved() {
        var raw: String? = null
        val store = CareerNewsVerificationStore.forTests({ raw }, { raw = it; true })
        val claim = CareerNewsClaim(id = "claim-1", text = "Kurum 12 teknisyen alacak.")
        val evidence = CareerNewsEvidence(
            id = "evidence-1",
            publisher = "Örnek kurum",
            sourceUrl = "https://kurum.gov.tr/duyuru/1",
            sourceType = CareerEvidenceSourceType.PRIMARY_SOURCE,
            stance = CareerEvidenceStance.SUPPORTS,
            excerpt = "Kurum 12 teknisyen alacağını duyurdu.",
            publishedAt = null,
            checkedAt = "2026-11-25T14:00:00Z"
        )
        val initial = CareerNewsVerificationRecord(
            storyId = CareerNewsVerificationRecord.stableStoryId(
                "https://kurum.gov.tr/duyuru/1", "Personel duyurusu"
            ),
            headline = "Personel duyurusu",
            publisher = "Örnek kurum",
            originalSourceUrl = "https://kurum.gov.tr/duyuru/1",
            publishedAt = null,
            collectedAt = "2026-11-25T14:00:00Z",
            firstSeenAt = "2026-11-25T13:00:00Z"
        ).addClaim(claim)!!
            .addEvidence("claim-1", evidence)!!
            .reviewClaim(
                "claim-1",
                CareerHumanReviewDecision.CONFIRM,
                "2026-11-25T14:05:00Z",
                "Resmî kaynak kontrol edildi."
            )!!
        assertTrue(store.save(initial))

        val corrected = initial.correctClaim(
            revisedClaim = claim.copy(text = "Kurum 10 teknisyen alacak."),
            correctedAt = "2026-11-25T14:10:00Z",
            explanation = "Kurumun düzeltme duyurusu kontenjanı değiştirdi.",
            sourceUrl = "https://kurum.gov.tr/duyuru/duzeltme"
        )!!
        assertTrue(store.save(corrected))
        assertFalse(store.save(corrected.copy(correctionHistory = emptyList())))
        assertFalse(store.save(initial.copy(
            claims = listOf(initial.claims.single().copy(evidence = emptyList()))
        )))
        assertEquals(1, store.find(initial.storyId)!!.correctionHistory.size)
    }

}
