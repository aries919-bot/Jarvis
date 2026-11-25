package com.cem.jarvis

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerForbiddenSourcesPolicyTest {
    @Test
    fun forbiddenJobBoardsAndUnlistedAcademicDomainsAreRejected() {
        listOf(
            "https://www.linkedin.com/jobs/",
            "https://tr.indeed.com/jobs",
            "https://sub.linkedin.com/jobs/"
        ).forEach { url ->
            assertFalse(CareerSourceCatalog.isTrustedUrl(url))
            assertFalse(CareerSourceCatalog.isSecondary(url))
        }
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://example.edu.tr/staj"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://personel.vakifuni.edu.tr/duyurular"))
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://personel.ozal.edu.tr/announcements-all"))
        assertFalse(CareerSourceCatalog.sources.any {
            it.url.contains("linkedin.com", ignoreCase = true) ||
                it.url.contains("indeed.com", ignoreCase = true)
        })
    }
}