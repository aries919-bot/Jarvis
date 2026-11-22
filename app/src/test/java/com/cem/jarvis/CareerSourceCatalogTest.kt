package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerSourceCatalogTest {
    @Test
    fun acceptsHttpsGovernmentAndMunicipalDomainsAsOfficialSources() {
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://www.dsi.gov.tr/duyurular"))
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://www.sgk.gov.tr/"))
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://example.bel.tr/"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("http://www.dsi.gov.tr/"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://fake-government.example/"))
    }

    @Test
    fun classifiesGeneralGovernmentSitesSeparatelyFromKpssPortals() {
        assertEquals("Kamu / resmî kurum", CareerSourceCatalog.categoryFor("https://www.dsi.gov.tr/duyurular"))
        assertEquals("Kamu / KPSS ve merkezi yerleştirme", CareerSourceCatalog.categoryFor("https://www.osym.gov.tr/duyurular"))
        assertEquals("Belediye / kamu", CareerSourceCatalog.categoryFor("https://www.example.bel.tr/duyurular"))
    }

    @Test
    fun exposesNewGovernmentInstitutionsInTheOfficialSourceDirectory() {
        val names = CareerSourceCatalog.sources.map { it.name }
        assertTrue(names.contains("Devlet Su İşleri Genel Müdürlüğü"))
        assertTrue(names.contains("Devlet Hava Meydanları İşletmesi"))
        assertTrue(names.contains("Kıyı Emniyeti Genel Müdürlüğü"))
        assertTrue(names.contains("Sosyal Güvenlik Kurumu"))
        assertTrue(names.contains("PTT – duyurular"))
    }
}
