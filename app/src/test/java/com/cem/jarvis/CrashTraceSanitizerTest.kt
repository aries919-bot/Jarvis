package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CrashTraceSanitizerTest {
    @Test
    fun preservesCrashSymbolsAndRedactsTheGeminiApiKey() {
        val apiKey = "AIzaSyA1b2C3d4E5f6G7h8I9j0K1l2M3n4O5p6"
        val trace = "java.lang.NullPointerException at performLaunchActivity: $apiKey"

        val sanitized = CrashTraceSanitizer.sanitizeCrashStackTrace(trace)

        assertTrue(sanitized.contains("java.lang.NullPointerException"))
        assertTrue(sanitized.contains("performLaunchActivity"))
        assertFalse(sanitized.contains(apiKey))
        assertTrue(sanitized.contains("[REDACTED_KEY]"))
    }

    @Test
    fun masksKnownKeyPrefixesEvenWhenTheKeyHasNoDigits() {
        val keys = listOf(
            "AIzaabcdefghijklmnop",
            "sk-abcdefghijklmnop",
            "gsk_abcdefghijklmnop"
        )

        val sanitized = CrashTraceSanitizer.sanitizeCrashStackTrace(keys.joinToString(" "))

        keys.forEach { assertFalse("Expected key to be masked: $it", sanitized.contains(it)) }
        assertEquals(3, sanitized.split("[REDACTED_KEY]").size - 1)
    }
}
