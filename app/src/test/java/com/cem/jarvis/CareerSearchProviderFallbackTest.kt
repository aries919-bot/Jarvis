package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerSearchProviderFallbackTest {
    @Test
    fun trustedInstitutionHomepageDoesNotSuppressFallbackSearch() {
        val homepage = Net.Hit(
            "Sahil Güvenlik Komutanlığı",
            "https://www.sg.gov.tr/",
            "Kurum ana sayfası"
        )
        assertFalse(CareerSearchEngine.containsUsableJobResult(listOf(homepage)))
    }

    @Test
    fun secondaryJobBoardListingIsAcceptedButStillMarkedAsUnverifiedSector() {
        val listing = Net.Hit(
            "Elektrik teknisyeni iş ilanı",
            "https://www.kariyer.net/is-ilani/elektrik-teknisyeni",
            "Elektrik bakım teknisyeni için başvuru ve son tarih bilgileri."
        )

        assertTrue(CareerSearchEngine.isEligibleSearchResult(listing))
        assertTrue(CareerSearchEngine.containsUsableJobResult(listOf(listing)))
        assertEquals(
            "Platform / sektör doğrulanmalı",
            CareerSourceCatalog.recruitmentGroup(listing.title, listing.snippet, listing.url)
        )
    }

    @Test
    fun secondaryHomepageWithoutJobContentDoesNotCountAsUsableResult() {
        val homepage = Net.Hit(
            "Kariyer.net",
            "https://www.kariyer.net/",
            "İş arayanlar için kariyer platformu."
        )

        assertFalse(CareerSearchEngine.isEligibleSearchResult(homepage))
    }

    @Test
    fun trustedRecruitmentNoticeCountsAsUsableSearchResult() {
        val notice = Net.Hit(
            "Sözleşmeli personel alımı başvuru duyurusu",
            "https://www.sg.gov.tr/duyurular",
            "Personel alımı, başvuru ve sınav takvimi"
        )
        assertTrue(CareerSearchEngine.containsUsableJobResult(listOf(notice)))
    }

    @Test
    fun untrustedJobBoardLinkDoesNotSuppressOfficialFallbackSearch() {
        val untrusted = Net.Hit(
            "Elektrik teknisyeni iş ilanı",
            "https://example.invalid/ilan",
            "Başvuru ve son tarih"
        )
        assertFalse(CareerSearchEngine.containsUsableJobResult(listOf(untrusted)))
    }
}
