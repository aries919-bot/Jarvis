package com.cem.jarvis

import org.json.JSONObject

/**
 * Small, deterministic parsers for provider SSE payloads and partial JSON chat replies.
 * Parsing stays separate from networking so malformed/empty events can be regression-tested.
 */
internal object AiStreamParser {
    fun openAiDelta(payload: String): String {
        if (payload == "[DONE]") return ""
        val choices = JSONObject(payload).optJSONArray("choices") ?: return ""
        val delta = choices.optJSONObject(0)?.optJSONObject("delta") ?: return ""
        return delta.optString("content", "")
    }

    fun geminiDelta(payload: String): String {
        val candidate = JSONObject(payload).optJSONArray("candidates")?.optJSONObject(0) ?: return ""
        val parts = candidate.optJSONObject("content")?.optJSONArray("parts") ?: return ""
        return buildString {
            for (i in 0 until parts.length()) {
                val part = parts.optJSONObject(i) ?: continue
                append(part.optString("text", ""))
            }
        }
    }

    /**
     * Returns the visible reply JSON string while it is still being generated.
     * Incomplete escape sequences are held until the next chunk arrives.
     */
    fun partialReply(raw: String): String? {
        val match = Regex("\"reply\"\\s*:\\s*\"").find(raw) ?: return null
        var index = match.range.last + 1
        val out = StringBuilder()
        while (index < raw.length) {
            val ch = raw[index++]
            if (ch == '"') break
            if (ch != '\\') {
                out.append(ch)
                continue
            }
            if (index >= raw.length) break
            when (val escaped = raw[index++]) {
                '"' -> out.append('"')
                '\\' -> out.append('\\')
                '/' -> out.append('/')
                'b' -> out.append('\b')
                'f' -> out.append('\u000C')
                'n' -> out.append('\n')
                'r' -> out.append('\r')
                't' -> out.append('\t')
                'u' -> {
                    if (index + 4 > raw.length) break
                    val code = raw.substring(index, index + 4).toIntOrNull(16) ?: break
                    out.append(code.toChar())
                    index += 4
                }
                else -> out.append(escaped)
            }
        }
        return out.toString()
    }
}
