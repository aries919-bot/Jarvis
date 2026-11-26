package com.cem.jarvis

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException

class AiResponseValidationTest {
    @Test
    fun acceptsResponseAtExactlyOneMegabyte() {
        val bytes = ByteArray(Net.MAX_RESPONSE_BYTES) { 'a'.code.toByte() }
        val result = Net.readLimitedBytes(ByteArrayInputStream(bytes))
        assertEquals(Net.MAX_RESPONSE_BYTES, result.size)
    }

    @Test
    fun rejectsResponseLargerThanOneMegabyte() {
        val bytes = ByteArray(Net.MAX_RESPONSE_BYTES + 1) { 'b'.code.toByte() }
        try {
            Net.readLimitedBytes(ByteArrayInputStream(bytes))
            fail("1 MB üstü yanıt reddedilmeliydi.")
        } catch (expected: IOException) {
            assertTrue(expected.message.orEmpty().contains("1 MB"))
        }
    }

    @Test
    fun treatsMissingInputStreamAsEmptyBody() {
        assertEquals("", Net.readLimitedText(null))
    }

    @Test
    fun rejectsMalformedAndEmptyJsonObjects() {
        for (body in listOf("", "{bozuk")) {
            try {
                Ai.parseResponseObject(body, "Test API")
                fail("Geçersiz JSON kabul edilmemeliydi.")
            } catch (expected: IOException) {
                assertTrue(expected.message.orEmpty().contains("Test API"))
            }
        }
    }

    @Test
    fun rejectsMissingOrBlankRequiredAnswer() {
        for (json in listOf(JSONObject(), JSONObject().put("answer", "  "))) {
            try {
                Ai.requiredResponseString(json, "answer", "Test API")
                fail("Eksik yanıt alanı kabul edilmemeliydi.")
            } catch (expected: IOException) {
                assertTrue(expected.message.orEmpty().contains("answer"))
            }
        }
    }

    @Test
    fun returnsValidatedRequiredAnswer() {
        assertEquals(
            "Doğrulanmış yanıt",
            Ai.requiredResponseString(
                JSONObject().put("answer", "Doğrulanmış yanıt"),
                "answer",
                "Test API"
            )
        )
    }

    @Test
    fun rejectsMalformedJsonArrays() {
        try {
            Ai.parseResponseArray("{not-an-array}", "Test API")
            fail("Nesne, liste yanıtı olarak kabul edilmemeliydi.")
        } catch (expected: IOException) {
            assertTrue(expected.message.orEmpty().contains("liste"))
        }
    }
}