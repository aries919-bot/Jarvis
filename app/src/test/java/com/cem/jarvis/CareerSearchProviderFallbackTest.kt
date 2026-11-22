package com.cem.jarvis

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
        assertFalse(CareerSearchEngine.hasUsableResult(listOf(homepage)))
    }

    @Test
    fun trustedRecruitmentNoticeCountsAsUsableSearchResult() {
        val notice = Net.Hit(
            "Sözleşmeli personel alımı başvuru duyurusu",
            "https://www.sg.gov.tr/duyurular",
            "Personel alımı, başvuru ve sınav takvimi"
        )
        assertTrue(CareerSearchEngine.hasUsableResult(listOf(notice)))
    }

    @Test
    fun untrustedJobBoardLinkDoesNotSuppressOfficialFallbackSearch() {
        val untrusted = Net.Hit(
            "Elektrik teknisyeni iş ilanı",
            "https://example.invalid/ilan",
            "Başvuru ve son tarih"
        )
        assertFalse(CareerSearchEngine.hasUsableResult(listOf(untrusted)))
    }
}
