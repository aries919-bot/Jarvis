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
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://sub.dsi.gov.tr/duyurular"))
        assertTrue(CareerSourceCatalog.isTrustedUrl("HTTPS://WWW.DSI.GOV.TR/duyurular"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("http://www.dsi.gov.tr/"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://www.dsi.gov.tr.evil.example/"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://www.dsi.gov.tr@evil.example/"))
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
        assertTrue(names.contains("Malatya Turgut Özal Üniversitesi – Personel Daire Başkanlığı"))
        assertTrue(names.contains("İnönü Üniversitesi – Personel Daire Başkanlığı"))
        assertTrue(names.contains("Fırat Üniversitesi – Personel Daire Başkanlığı"))
        assertTrue(names.contains("Adıyaman Üniversitesi – Personel Daire Başkanlığı"))
        assertTrue(names.contains("e-Devlet – İş ve Kariyer hizmetleri"))
        assertTrue(names.contains("İçişleri Bakanlığı – duyurular"))
        assertTrue(names.contains("Millî Savunma Bakanlığı – Personel Temin Sistemi"))
        assertTrue(names.contains("Jandarma Genel Komutanlığı – duyurular"))
        assertTrue(names.contains("Çalışma ve Sosyal Güvenlik Bakanlığı – duyurular"))
        assertTrue(names.contains("Kültür ve Turizm Bakanlığı – Personel Genel Müdürlüğü"))
        assertTrue(names.contains("Savunma Sanayii Başkanlığı – duyurular"))
        assertEquals(
            "Üniversite / kamu personel alımı",
            CareerSourceCatalog.categoryFor("https://personel.ozal.edu.tr/announcements-all")
        )
    }
}
