package com.cem.jarvis

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerForbiddenSourcesPolicyTest {
    @Test
    fun forbiddenJobBoardsAndUnlistedAcademicDomainsAreRejected() {
        val forbidden = listOf(
            "https://www.linkedin.com/jobs/",
            "https://tr.indeed.com/jobs",
            "https://sub.linkedin.com/jobs/"
        )
        forbidden.forEach { url ->
            assertFalse(CareerSourceCatalog.isTrustedUrl(url))
            assertFalse(CareerSourceCatalog.isSecondary(url))
        }
        assertFalse(CareerSourceCatalog.sources.any {
            it.url.contains("linkedin.com", ignoreCase = true) ||
                it.url.contains("indeed.com", ignoreCase = true)
        })
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://example.edu.tr/staj"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://personel.vakifuni.edu.tr/duyurular"))
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://personel.ozal.edu.tr/announcements-all"))
    }
    @Test
    fun everyNonOfficialCatalogSourceRemainsSecondaryAndCannotTriggerPrimaryAlerts() {
        val nonOfficial = CareerSourceCatalog.sources.filterNot { it.official }
        assertTrue(nonOfficial.isNotEmpty())
        nonOfficial.forEach { source ->
            assertTrue("${source.name} must remain secondary", CareerSourceCatalog.isSecondary(source.url))
        }
        listOf(
            "https://www.secretcv.com/",
            "https://www.yenibiris.com/"
        ).forEach { url ->
            assertTrue(CareerSourceCatalog.isTrustedUrl(url))
            assertTrue(CareerSourceCatalog.isSecondary(url))
        }
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://www.isbul.net/"))
        assertTrue(CareerSourceCatalog.isSecondary("https://www.isbul.net/"))
    }

    @Test
    fun networkRequestsUseTheDeclaredAriesUserAgent() {
        assertEquals(
            "AriesKaynakTara/1.0 (+https://github.com/aries919-bot/Jarvis)",
            Net.ARIES_SOURCE_USER_AGENT
        )
        assertFalse(Net.ARIES_SOURCE_USER_AGENT.contains("Mozilla", ignoreCase = true))
        assertFalse(Net.ARIES_SOURCE_USER_AGENT.contains("Chrome", ignoreCase = true))
    }

}
