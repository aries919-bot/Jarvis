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
    fun requiresGeminiFinishReasonToTreatStreamAsComplete() {
        assertTrue(AiStreamParser.geminiFinished("""{"candidates":[{"finishReason":"STOP"}]}"""))
        assertTrue(!AiStreamParser.geminiFinished("""{"candidates":[{"content":{"parts":[{"text":"partial"}]}}]}"""))
        assertTrue(!AiStreamParser.geminiFinished("""{"candidates":[]}"""))
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

    @Test
    fun handlesBracesAndQuotesInsideStringValues() {
        val payload = """{"choices":[{"delta":{"content":"{ \"ok\": true }"}}]}"""
        assertEquals("{ \"ok\": true }", AiStreamParser.openAiDelta(payload))
    }

    @Test
    fun rejectsMissingArrayWithoutThrowing() {
        assertEquals("", AiStreamParser.openAiDelta("""{"other":[]}"""))
        assertEquals("", AiStreamParser.geminiDelta("""{"other":[]}"""))
    }

    @Test
    fun readsOnlySseDataAndIgnoresCommentsMetadataAndEmptyLines() {
        assertEquals("""{"x":1}""", SseLineParser.dataFromLine("""data: {"x":1}"""))
        assertEquals("""{"x":1}""", SseLineParser.dataFromLine("""{"x":1}"""))
        assertEquals("[DONE]", SseLineParser.dataFromLine("data: [DONE]"))
        assertNull(SseLineParser.dataFromLine(": keep-alive"))
        assertNull(SseLineParser.dataFromLine("event: message"))
        assertNull(SseLineParser.dataFromLine("id: 12"))
        assertNull(SseLineParser.dataFromLine("retry: 1000"))
        assertNull(SseLineParser.dataFromLine("data: "))
        assertNull(SseLineParser.dataFromLine(""))
    }
}
