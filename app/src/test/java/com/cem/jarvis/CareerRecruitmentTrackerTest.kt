package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
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
}
