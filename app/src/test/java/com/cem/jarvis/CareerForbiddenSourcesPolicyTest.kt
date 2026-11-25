package com.cem.jarvis

import org.junit.Assert.assertFalse
import org.junit.Test

class CareerForbiddenSourcesPolicyTest {
    @Test
    fun forbiddenJobBoardsAreNeverTrustedOrSecondarySources() {
        listOf(
            "https://www.linkedin.com/jobs/",
            "https://tr.indeed.com/jobs",
            "https://sub.linkedin.com/jobs/"
        ).forEach { url ->
            assertFalse(CareerSourceCatalog.isTrustedUrl(url))
            assertFalse(CareerSourceCatalog.isSecondary(url))
        }
    }
}
