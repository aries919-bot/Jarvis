package com.cem.jarvis

object Privacy {
    private val rules = listOf(
        Regex("""\beyJ[\w-]+\.[\w-]+\.[\w-]*""") to "[TOKEN]",
        Regex("""(?i)(api[_ -]?key|token|secret|password)\s*[:=]\s*\S+""") to "$1=[GIZLI]",
        Regex("""[\w.+-]+@[\w.-]+\.[A-Za-z]{2,}""") to "[EPOSTA]",
        Regex("""(?i)\bTR\d{2}(?:\s?\d{4}){5}\s?\d{2}\b""") to "[IBAN]",
        Regex("""\b(?:\d[ -]?){15}\d\b""") to "[KART]",
        Regex("""(?:\+90|0)\s?\(?5\d{2}\)?[\s-]?\d{3}[\s-]?\d{2}[\s-]?\d{2}\b""") to "[TELEFON]",
        Regex("""\b(?:\d{1,3}\.){3}\d{1,3}\b""") to "[IP]"
    )

    private val apiKeyPattern = Regex(
        """\b(?=[A-Za-z0-9_-]*\d)(?=[A-Za-z0-9_-]*[A-Za-z])[A-Za-z0-9_-]{32,}\b"""
    )
    private val tcknPattern = Regex("""(?<![\p{L}\d])([1-9]\d{10})(?![\p{L}\d])""")
    private val addressPattern = Regex(
        """(?:(?<![\p{L}\d_])([\p{L}][\p{L}'’.-]*(?:\s+[\p{L}][\p{L}'’.-]*){0,2})\s+(?<![\p{L}\d_])(?:mahalle(?:si)?|mah)\.?(?![\p{L}\d_])\s*[:.]?\s*|(?<![\p{L}\d_])(?:mahalle(?:si)?|mah|sokak|sok|sk|cadde|cad|cd|no(?=\s*[:.]?\s*\d)|daire|apt)\.?(?![\p{L}\d_])\s*[:.]?\s*)[^\n;]{1,120}""",
        setOf(RegexOption.IGNORE_CASE, RegexOption.UNICODE_CASE)
    )
    private val addressMarkerPattern = Regex(
        """(?<![\p{L}\d_])(?:mahalle(?:si)?|mah|sokak|sok|sk|cadde|cad|cd|no(?=\s*[:.]?\s*\d)|daire|apt)\.?(?![\p{L}\d_])""",
        setOf(RegexOption.IGNORE_CASE, RegexOption.UNICODE_CASE)
    )
    private val addressFirstValuePattern = Regex(
        """(?<![\p{L}\d_])(?:mahalle(?:si)?|mah|sokak|sok|sk|cadde|cad|cd|no(?=\s*[:.]?\s*\d)|daire|apt)\.?(?![\p{L}\d_])\s*[:.]?\s*([\p{L}\d]+)""",
        setOf(RegexOption.IGNORE_CASE, RegexOption.UNICODE_CASE)
    )

    fun isValidTckn(value: String): Boolean {
        if (!Regex("""^[1-9]\d{10}$""").matches(value)) return false
        val digits = value.map { it.digitToInt() }
        val odd = digits[0] + digits[2] + digits[4] + digits[6] + digits[8]
        val even = digits[1] + digits[3] + digits[5] + digits[7]
        val tenth = ((odd * 7 - even) % 10 + 10) % 10
        val eleventh = digits.take(10).sum() % 10
        return digits[9] == tenth && digits[10] == eleventh
    }

    private fun maskAddresses(value: String): String =
        addressPattern.replace(value) { match ->
            val mahalleName = match.groups[1]?.value
            val markerCount = addressMarkerPattern.findAll(match.value).count()
            val firstValue = addressFirstValuePattern.find(match.value)?.groupValues?.getOrNull(1)
            val looksLikeAddress = mahalleName != null ||
                match.value.any(Char::isDigit) ||
                markerCount > 1 ||
                (firstValue?.firstOrNull()?.isUpperCase() == true)
            if (looksLikeAddress) "[ADRES]" else match.value
        }

    fun sanitizeCloudText(input: String): String {
        var sanitized = rules.fold(input) { text, (pattern, replacement) ->
            pattern.replace(text, replacement)
        }
        sanitized = apiKeyPattern.replace(sanitized, "[ANAHTAR]")
        sanitized = tcknPattern.replace(sanitized) { match ->
            if (isValidTckn(match.groupValues[1])) "[TC]" else match.value
        }
        return maskAddresses(sanitized)
    }
}
