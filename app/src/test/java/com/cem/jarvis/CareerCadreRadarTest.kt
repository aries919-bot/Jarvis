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
    fun officialKpss2026_2P94AndP93TablesAreDetectedSeparately() {
        val p94 = Net.Hit(
            "KPSS-2026/2 Ortaöğretim P94 TABLO-1",
            "https://dokuman.osym.gov.tr/pdfdokuman/2026/KPSS/TERCIH2/tablo1_2026-2.pdf",
            "Ortaöğretim tercih tablosu"
        )
        val p93 = Net.Hit(
            "KPSS-2026/2 Ön Lisans P93 TABLO-2",
            "https://dokuman.osym.gov.tr/pdfdokuman/2026/KPSS/TERCIH2/tablo2_2026-2.pdf",
            "Ön lisans tercih tablosu"
        )

        val tables = CareerCadreRadar.detect2026_2GuideTables(listOf(p94, p93))
        assertEquals(p94.url, tables.p94Url)
        assertEquals(p93.url, tables.p93Url)
        assertTrue(tables.hasAnyTable)
        assertTrue(CareerCadreRadar.kpss2026_2CadreStatus(true, true).contains("P94 TABLO-1 ve P93 TABLO-2"))
    }

    @Test
    fun previousKpss2026_1TableIsNotMistakenForUpcoming2026_2Guide() {
        val historic = Net.Hit(
            "KPSS-2026/1 Ortaöğretim P94 TABLO-1",
            "https://dokuman.osym.gov.tr/pdfdokuman/2026/KPSS/TERCIH1/tablo1_2026-1.pdf",
            "ÖSYM geçmiş tercih tablosu"
        )
        val tables = CareerCadreRadar.detect2026_2GuideTables(listOf(historic))
        assertEquals(null, tables.p94Url)
        assertEquals(null, tables.p93Url)
        assertFalse(tables.hasAnyTable)
    }

    @Test
    fun thirdPartyCopyOfKpss2026_2TableIsNotMarkedOfficial() {
        val copied = Net.Hit(
            "KPSS-2026/2 Ortaöğretim P94 TABLO-1",
            "https://example.com/TERCIH2/tablo1_2026-2.pdf",
            "ÖSYM tablosu kopyası"
        )
        val tables = CareerCadreRadar.detect2026_2GuideTables(listOf(copied))
        assertEquals(null, tables.p94Url)
        assertFalse(tables.hasAnyTable)
    }

    @Test
    fun p94AndP93GuideDetectionCanReportPartialPublication() {
        val p94 = Net.Hit(
            "KPSS-2026/2 P94 TABLO-1",
            "https://dokuman.osym.gov.tr/pdfdokuman/2026/KPSS/TERCIH2/tablo1.pdf",
            "Ortaöğretim"
        )
        val tables = CareerCadreRadar.detect2026_2GuideTables(listOf(p94))
        assertTrue(tables.p94Url != null)
        assertEquals(null, tables.p93Url)
        assertTrue(CareerCadreRadar.kpss2026_2CadreStatus(true, false).contains("P93 TABLO-2 bağlantısı henüz bulunmadı"))
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
