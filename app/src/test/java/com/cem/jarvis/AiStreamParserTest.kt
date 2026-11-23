package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiStreamParserTest {
    @Test
    fun readsOpenAiCompatibleDelta() {
        val payload = """{"choices":[{"delta":{"content":"Merhaba"}}]}"""
        assertEquals("Merhaba", AiStreamParser.openAiDelta(payload))
    }

    @Test
    fun ignoresOpenAiEmptyDeltaAndDoneMarker() {
        assertEquals("", AiStreamParser.openAiDelta("""{"choices":[{"delta":{}}]}"""))
        assertEquals("", AiStreamParser.openAiDelta("[DONE]"))
    }

    @Test
    fun readsGeminiCandidateText() {
        val payload = """{"candidates":[{"content":{"parts":[{"text":"İlk "},{"text":"parça"}]}}]}"""
        assertEquals("İlk parça", AiStreamParser.geminiDelta(payload))
    }

    @Test
    fun readsPartialReplyBeforeJsonCloses() {
        assertEquals("Merhaba", AiStreamParser.partialReply("""{"command":"","reply":"Merhaba"""))
    }

    @Test
    fun decodesEscapedQuotesAndNewLinesIncrementally() {
        val partial = AiStreamParser.partialReply("""{"reply":"Satır 1\n\"evet\"""")
        assertEquals("Satır 1\n\"evet\"", partial)
    }

    @Test
    fun returnsNullUntilReplyFieldArrives() {
        assertNull(AiStreamParser.partialReply("""{"command":"open_app"""))
        assertTrue(AiStreamParser.partialReply("""{"reply":"A""")!!.startsWith("A"))
    }
}
