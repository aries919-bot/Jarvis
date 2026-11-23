package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
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

    @Test
    fun publicHiringNewsQueryTargetsNewsAndOfficialDiscoverySources() {
        val queries = CareerSearchEngine.queryList(10, "elektrik teknisyeni", "Malatya")
        assertTrue(queries.any { it.contains("memur365.com") })
        assertTrue(queries.any { it.contains("memurlar.net") })
        assertTrue(queries.any { it.contains("kamupersoneli.net") })
        assertTrue(queries.any { it.contains("iskamu.com") })
        assertTrue(queries.any { it.contains("kamuilan.sbb.gov.tr") })
        assertTrue(queries.any { it.contains("resmigazete.gov.tr") })
        assertTrue(queries.any { it.contains("Malatya") })
    }

    @Test
    fun publicHiringNewsPublishersStayInNewsClassification() {
        val sources = listOf(
            "https://www.kamupersoneli.net/ilan/personel-alimi",
            "https://www.iskamu.com/personel-alimi"
        )
        sources.forEach { url ->
            assertTrue(CareerSourceCatalog.isNewsDiscoverySource(url))
            assertEquals(
                CareerContentType.NEWS,
                CareerContentClassifier.classify(
                    "Kamu personel alımı başvuru duyurusu",
                    "Yeni personel alımı ve başvuru şartları",
                    url
                ).type
            )
        }
    }

    @Test
    fun scanOrHeadlineDateIsNotTreatedAsSourcePublicationDate() {
        val undated = "Kura sonuçları 22.11.2025 tarihinde açıklandı"
        assertNull(CareerListingParser.publicationDate(undated))
        assertNull(CareerListingParser.publicationTimestamp(undated))
    }

    @Test
    fun explicitlyLabelledPublicationDateIsExtractedSeparately() {
        assertEquals(
            "22.11.2025",
            CareerListingParser.publicationDate("Yayın tarihi: 22.11.2025")
        )
    }
}
