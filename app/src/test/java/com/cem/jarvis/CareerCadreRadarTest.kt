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
    fun electricAssociateQualificationCodeIsGroundedAndGraduationRequired() {
        assertEquals(3290, CareerCadreRadar.P93_ELECTRIC_PROGRAM_CODE)
        assertTrue(CareerCadreRadar.P93_ELECTRIC_TEDAS_NOTE.contains("Elektrik veya Elektrik Teknolojisi"))
        assertTrue(CareerCadreRadar.P93_ELECTRIC_TEDAS_NOTE.contains("Mezuniyet"))
        assertTrue(CareerCadreRadar.P94_ELECTRIC_CODE_NOTE.contains("2023"))
        assertTrue(CareerCadreRadar.P94_ELECTRIC_CODE_NOTE.contains("Elektrik-Elektronik Teknolojisi"))
    }

    @Test
    fun tedasElectricalTechnicianIsShownAsHistoricalP93Example() {
        val entry = CareerCadreRadar.p93History.firstOrNull { it.title.contains("Elektrik (3290)") }
        assertTrue(entry != null)
        assertTrue(entry!!.institutionExamples.contains("TEDAŞ"))
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
