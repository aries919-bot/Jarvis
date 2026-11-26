package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacyTest {
    @Test
    fun masksTcknOnlyWhenOfficialChecksPass() {
        assertEquals("Kimlik [TC]", Privacy.sanitizeCloudText("Kimlik 12345678950"))
        assertEquals("12345678951", Privacy.sanitizeCloudText("12345678951"))
    }

    @Test
    fun doesNotTreatAddressMarkerFragmentsInsideWordsAsAddresses() {
        assertEquals("disk 2 doldu", Privacy.sanitizeCloudText("disk 2 doldu"))
        assertEquals("piano 3 tane", Privacy.sanitizeCloudText("piano 3 tane"))
    }

    @Test
    fun masksWholeGskKeyIncludingItsPrefix() {
        val fakeKey = "gsk_" + "a1".repeat(18)
        assertEquals(40, fakeKey.length)
        val sanitized = Privacy.sanitizeCloudText(fakeKey)
        assertEquals("[ANAHTAR]", sanitized)
        assertFalse(sanitized.startsWith("gsk_"))
        assertTrue(sanitized.startsWith("[ANAHTAR]"))
    }

    @Test
    fun masksNeighborhoodAndFollowingAddressAsOneValue() {
        assertEquals(
            "[ADRES]",
            Privacy.sanitizeCloudText("Yenişehir Mah. Atatürk Cad. No: 12 Daire: 3")
        )
    }
}
