package com.cem.jarvis

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiErrorTest {
    @Test
    fun rateLimitServerAndRequestTimeoutStatusesAreTemporary() {
        listOf(408, 429, 500, 502, 503, 504).forEach { code ->
            assertTrue("HTTP $code should be temporary", ApiError(code, "test").temporary)
        }
    }

    @Test
    fun authAndClientErrorsAreNotTransientRetries() {
        listOf(400, 401, 403, 404).forEach { code ->
            assertFalse("HTTP $code should not retry same provider", ApiError(code, "test").temporary)
        }
    }

    @Test
    fun invalidCredentialsAreIdentifiedSeparately() {
        assertTrue(ApiError(401, "test").badKey)
        assertTrue(ApiError(403, "test").badKey)
        assertFalse(ApiError(429, "test").badKey)
    }
}
