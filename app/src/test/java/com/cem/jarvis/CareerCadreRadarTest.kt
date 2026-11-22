package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerCadreRadarTest {
    @Test
    fun centralPreferenceWindowIsPublishedButCadreListIsNotAssumed() {
        assertEquals("17–24 Aralık 2026", CareerCadreRadar.PREFERENCE_WINDOW)
        assertTrue(CareerCadreRadar.kpss2026_2CadreStatus(false).contains("kadro ve kontenjan tablosu"))
        assertFalse(CareerCadreRadar.kpss2026_2CadreStatus(false).contains("alım kesinleşti"))
    }

    @Test
    fun secondaryTableIncludesTechnicianAndWagonTechnicianAsHistoricalExamples() {
        val titles = CareerCadreRadar.p94History.map { it.title }
        assertTrue(titles.contains("Teknisyen"))
        assertTrue(titles.contains("Vagon teknisyeni"))
    }

    @Test
    fun associateCadresAreClearlyMarkedForGraduation() {
        assertTrue(CareerCadreRadar.associateTrackLabel(false).startsWith("MEZUNİYET SONRASI"))
        assertTrue(CareerCadreRadar.associateTrackLabel(true).startsWith("P93"))
    }

    @Test
    fun previousPlacementScoreIsExplicitlyHistorical() {
        assertEquals(76.91127, CareerCadreRadar.P94_ELAZIG_TECHNICIAN_2026_1_LOWEST, 0.000001)
        assertTrue(CareerCadreRadar.P94_ELAZIG_TECHNICIAN_NOTE.contains("geçmiş dönem"))
        assertTrue(CareerCadreRadar.P94_ELAZIG_TECHNICIAN_NOTE.contains("tahmini değildir"))
    }

    @Test
    fun differentRecruitmentRoutesAreNotCollapsedIntoOneType() {
        val titles = CareerCadreRadar.recruitmentRoutes.map { it.first }
        assertTrue(titles.contains("KPSS merkezi yerleştirme"))
        assertTrue(titles.contains("4/B sözleşmeli personel"))
        assertTrue(titles.contains("Sürekli işçi"))
        assertTrue(titles.contains("KİT sözleşmeli personel"))
    }
}
