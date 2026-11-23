package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CareerRecruitmentTrackerTest {
    @Test
    fun schedulesExplicitLotteryResultAnnouncementDate() {
        val title = "Noter kurası sonuçları 25.11.2026 tarihinde açıklanacaktır"
        val stage = CareerRecruitmentTracker.detectStage(title, "")
        assertEquals("NOTER KURASI SONUCU · ASIL / YEDEK", stage)
        val event = requireNotNull(CareerRecruitmentTracker.parseEventTime(title, "", stage))
        assertEquals("25.11.2026 09:00", SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR")).format(Date(event.first)))
        assertFalse(event.second)
    }

    @Test
    fun schedulesExplicitExamResultAnnouncementDate() {
        val title = "Sınav sonuçları 30 Kasım 2026 tarihinde duyurulacaktır"
        val stage = CareerRecruitmentTracker.detectStage(title, "")
        assertEquals("SINAV SONUCU", stage)
        val event = requireNotNull(CareerRecruitmentTracker.parseEventTime(title, "", stage))
        assertEquals("30.11.2026 09:00", SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR")).format(Date(event.first)))
        assertFalse(event.second)
    }

    @Test
    fun doesNotMistakeAppealDeadlineForUpcomingResultAnnouncement() {
        val title = "Kura sonucu açıklandı. İtiraz için son tarih 27.11.2026"
        val stage = CareerRecruitmentTracker.detectStage(title, "")
        assertEquals("NOTER KURASI SONUCU · ASIL / YEDEK", stage)
        assertNull(CareerRecruitmentTracker.parseEventTime(title, "", stage))
    }

    @Test
    fun publishedKuraResultsAreClassifiedAsHistorical() {
        assertTrue(
            CareerRecruitmentTracker.isCompletedResultNotice(
                "Noter kurası sonuçları açıklandı",
                "Asıl ve yedek aday listesi yayımlandı."
            )
        )
    }

    @Test
    fun scheduledFutureResultIsNotMistakenForPublishedHistory() {
        assertFalse(
            CareerRecruitmentTracker.isCompletedResultNotice(
                "Noter kurası sonuçları 25.11.2026 tarihinde açıklanacaktır",
                ""
            )
        )
    }

    @Test
    fun pastDatedKuraEventCannotTriggerNewProcessAlert() {
        val pastEvent = CareerProcessEvent(
            id = "past-kura",
            title = "Noter kurası sonuçları 20.11.2026 tarihinde açıklandı",
            url = "https://example.gov.tr/duyuru",
            snippet = "Asıl ve yedek aday listesi yayımlandı.",
            stage = "NOTER KURASI SONUCU · ASIL / YEDEK",
            detectedAt = System.currentTimeMillis(),
            eventAt = System.currentTimeMillis() - 86_400_000L,
            hasExplicitTime = false
        )
        assertTrue(CareerRecruitmentTracker.isHistoricalEvent(pastEvent))
        assertFalse(CareerRecruitmentTracker.shouldNotifyAsNew(pastEvent))
    }

    @Test
    fun undatedPublishedResultIsArchivedAndCannotTriggerNewAlert() {
        val oldResult = CareerProcessEvent(
            id = "old-result",
            title = "Kura sonuçları açıklandı",
            url = "https://example.gov.tr/sonuc",
            snippet = "Asıl ve yedek aday listesi duyuruldu.",
            stage = "NOTER KURASI SONUCU · ASIL / YEDEK",
            detectedAt = System.currentTimeMillis(),
            eventAt = null,
            hasExplicitTime = false
        )
        assertTrue(CareerRecruitmentTracker.isHistoricalEvent(oldResult))
        assertFalse(CareerRecruitmentTracker.shouldNotifyAsNew(oldResult))
    }

    @Test
    fun undatedResultHeadlineIsNotShownAsUpcomingWithoutFutureAnnouncementEvidence() {
        val unknown = CareerProcessEvent(
            id = "unknown-result",
            title = "Kura sonuçları",
            url = "https://example.gov.tr/sonuc",
            snippet = "",
            stage = "NOTER KURASI SONUCU · ASIL / YEDEK",
            detectedAt = System.currentTimeMillis(),
            eventAt = null,
            hasExplicitTime = false
        )
        assertTrue(CareerRecruitmentTracker.isHistoricalEvent(unknown))
        assertFalse(CareerRecruitmentTracker.shouldNotifyAsNew(unknown))
    }

    @Test
    fun undatedResultWithExplicitFutureAnnouncementWordingStaysInUpcomingChecks() {
        val upcoming = CareerProcessEvent(
            id = "future-result-no-date",
            title = "Kura sonuçları ilerleyen günlerde açıklanacaktır",
            url = "https://example.gov.tr/sonuc",
            snippet = "",
            stage = "NOTER KURASI SONUCU · ASIL / YEDEK",
            detectedAt = System.currentTimeMillis(),
            eventAt = null,
            hasExplicitTime = false
        )
        assertFalse(CareerRecruitmentTracker.isHistoricalEvent(upcoming))
        assertTrue(CareerRecruitmentTracker.shouldNotifyAsNew(upcoming))
    }
}
