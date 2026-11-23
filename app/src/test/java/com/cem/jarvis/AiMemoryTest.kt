package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiMemoryTest {
    @Test
    fun memoryIsNotSharedByDefault() {
        val prompt = AiMemory.appendContext("system", "Bölümüm Elektrik", true, false)
        assertEquals("system", prompt)
    }

    @Test
    fun disabledMemoryIsNeverAdded() {
        val prompt = AiMemory.appendContext("system", "Bölümüm Elektrik", false, true)
        assertEquals("system", prompt)
    }

    @Test
    fun optedInMemoryIsAddedAsContext() {
        val prompt = AiMemory.appendContext("system", "Bölümüm Elektrik; KPSS P94", true, true)
        assertTrue(prompt.contains("Bölümüm Elektrik"))
        assertTrue(prompt.contains("KPSS P94"))
        assertTrue(prompt.contains("yalnızca ilgili olduklarında kullan"))
    }

    @Test
    fun secretsAndContactDetailsAreExcluded() {
        val prompt = AiMemory.appendContext(
            "system",
            "Bölümüm Elektrik; mailim cem@example.com; api_key=verysecret",
            true,
            true
        )
        assertTrue(prompt.contains("Bölümüm Elektrik"))
        assertTrue(!prompt.contains("cem@example.com"))
        assertTrue(!prompt.contains("verysecret"))
    }

    @Test
    fun personalIdentifiersPhoneNumbersAndCredentialsAreExcluded() {
        val prompt = AiMemory.appendContext(
            "system",
            "Güvenli not; kimlik 12345678901; telefon +90 555 123 45 67; bearer gsk_abcdefghijklmnop",
            true,
            true
        )
        assertTrue(prompt.contains("Güvenli not"))
        assertTrue(!prompt.contains("12345678901"))
        assertTrue(!prompt.contains("+90 555"))
        assertTrue(!prompt.contains("gsk_abcdefghijklmnop"))
    }

    @Test
    fun emptyMemoryLeavesPromptUnchanged() {
        assertEquals("system", AiMemory.appendContext("system", " ; \n ", true, true))
    }
}
