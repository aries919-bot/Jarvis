package com.cem.jarvis

object Privacy {
    private val rules = listOf(
        Regex("\\beyJ[\\w-]+\\.[\\w-]+\\.[\\w-]*") to "[TOKEN]",
        Regex("(?i)(api[_ -]?key|token|secret|password)\\s*[:=]\\s*\\S+") to "$1=[GIZLI]",
        Regex("[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}") to "[EPOSTA]",
        Regex("(?i)\\bTR\\d{2}(?:\\s?\\d{4}){5}\\s?\\d{2}\\b") to "[IBAN]",
        Regex("\\b(?:\\d[ -]?){15}\\d\\b") to "[KART]",
        Regex("(?:\\+90|0)\\s?\\(?5\\d{2}\\)?[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}\\b") to "[TELEFON]",
        Regex("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b") to "[IP]"
    )

    fun sanitizeCloudText(input: String): String =
        rules.fold(input) { s, (r, to) -> r.replace(s, to) }
}
