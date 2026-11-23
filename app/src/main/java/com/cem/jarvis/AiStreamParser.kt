package com.cem.jarvis

/**
 * Dependency-free parsers for the small JSON envelopes used by OpenAI-compatible
 * and Gemini SSE events. Avoids Android org.json so ordinary JVM tests exercise
 * the same parsing behavior as the application.
 */
internal object AiStreamParser {
    fun openAiDelta(payload: String): String {
        if (payload == "[DONE]") return ""
        val choice = firstObject(arrayField(payload, "choices")) ?: return ""
        val delta = objectField(choice, "delta") ?: return ""
        return stringField(delta, "content") ?: ""
    }

    fun geminiDelta(payload: String): String {
        val candidate = firstObject(arrayField(payload, "candidates")) ?: return ""
        val content = objectField(candidate, "content") ?: return ""
        return arrayObjects(arrayField(content, "parts"))
            .mapNotNull { stringField(it, "text") }
            .joinToString("")
    }

    fun geminiFinished(payload: String): Boolean {
        val candidate = firstObject(arrayField(payload, "candidates")) ?: return false
        return !stringField(candidate, "finishReason").isNullOrBlank()
    }

    fun partialReply(raw: String): String? {
        val start = stringValueStart(raw, "reply") ?: return null
        return decodeStringAt(raw, start, allowIncomplete = true)?.first
    }

    private fun valueStart(source: String, field: String): Int? {
        val key = Regex("\"" + Regex.escape(field) + "\"\\s*:\\s*").find(source) ?: return null
        return key.range.last + 1
    }

    private fun stringValueStart(source: String, field: String): Int? {
        val start = valueStart(source, field) ?: return null
        var i = start
        while (i < source.length && source[i].isWhitespace()) i++
        return if (i < source.length && source[i] == '"') i + 1 else null
    }

    private fun stringField(source: String, field: String): String? {
        val start = stringValueStart(source, field) ?: return null
        return decodeStringAt(source, start, allowIncomplete = false)?.first
    }

    private fun decodeStringAt(source: String, start: Int, allowIncomplete: Boolean): Pair<String, Int>? {
        var i = start
        val out = StringBuilder()
        while (i < source.length) {
            val ch = source[i++]
            if (ch == '"') return out.toString() to i
            if (ch != '\\') {
                out.append(ch)
                continue
            }
            if (i >= source.length) return if (allowIncomplete) out.toString() to i else null
            when (val escaped = source[i++]) {
                '"' -> out.append('"')
                '\\' -> out.append('\\')
                '/' -> out.append('/')
                'b' -> out.append('\b')
                'f' -> out.append('\u000C')
                'n' -> out.append('\n')
                'r' -> out.append('\r')
                't' -> out.append('\t')
                'u' -> {
                    if (i + 4 > source.length) return if (allowIncomplete) out.toString() to i else null
                    val code = source.substring(i, i + 4).toIntOrNull(16)
                        ?: return if (allowIncomplete) out.toString() to i else null
                    out.append(code.toChar())
                    i += 4
                }
                else -> out.append(escaped)
            }
        }
        return if (allowIncomplete) out.toString() to i else null
    }

    private fun arrayField(source: String, field: String): String {
        val start = valueStart(source, field) ?: return ""
        var i = start
        while (i < source.length && source[i].isWhitespace()) i++
        if (i >= source.length || source[i] != '[') return ""
        return balancedContainer(source, i, '[', ']') ?: ""
    }

    private fun objectField(source: String, field: String): String? {
        val start = valueStart(source, field) ?: return null
        var i = start
        while (i < source.length && source[i].isWhitespace()) i++
        if (i >= source.length || source[i] != '{') return null
        return balancedContainer(source, i, '{', '}')
    }

    private fun balancedContainer(source: String, start: Int, open: Char, close: Char): String? {
        var depth = 0
        var inString = false
        var escaped = false
        for (i in start until source.length) {
            val ch = source[i]
            if (inString) {
                if (escaped) escaped = false
                else when (ch) {
                    '\\' -> escaped = true
                    '"' -> inString = false
                }
                continue
            }
            when (ch) {
                '"' -> inString = true
                open -> depth++
                close -> {
                    depth--
                    if (depth == 0) return source.substring(start, i + 1)
                }
            }
        }
        return null
    }

    private fun firstObject(array: String): String? = arrayObjects(array).firstOrNull()

    private fun arrayObjects(array: String): List<String> {
        if (array.isBlank()) return emptyList()
        val open = array.indexOf('[')
        if (open < 0) return emptyList()
        val out = ArrayList<String>()
        var i = open + 1
        while (i < array.length) {
            while (i < array.length && (array[i].isWhitespace() || array[i] == ',')) i++
            if (i >= array.length || array[i] == ']') break
            if (array[i] != '{') return out
            val item = balancedContainer(array, i, '{', '}') ?: return out
            out.add(item)
            i += item.length
        }
        return out
    }
}


/** Extracts data payloads from individual SSE lines; comments and metadata are ignored. */
internal object SseLineParser {
    fun dataFromLine(line: String): String? {
        val value = line.trim()
        if (value.isEmpty() || value.startsWith(":") ||
            value.startsWith("event:") || value.startsWith("id:") || value.startsWith("retry:")
        ) return null
        val data = when {
            value.startsWith("data:") -> value.substring(5).trim()
            value.startsWith("{") -> value
            else -> return null
        }
        return data.ifEmpty { null }
    }
}
