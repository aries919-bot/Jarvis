package com.cem.jarvis

import java.util.Locale

class MathParser(private val s: String) {
    private var pos = 0

    fun parse(): Double {
        val v = expr()
        if (pos != s.length) throw IllegalArgumentException("fazla karakter")
        return v
    }

    private fun expr(): Double {
        var v = term()
        while (pos < s.length && (s[pos] == '+' || s[pos] == '-')) {
            val op = s[pos++]
            val r = term()
            v = if (op == '+') v + r else v - r
        }
        return v
    }

    private fun term(): Double {
        var v = factor()
        while (pos < s.length && (s[pos] == '*' || s[pos] == '/')) {
            val op = s[pos++]
            val r = factor()
            v = if (op == '*') v * r else v / r
        }
        return v
    }

    private fun factor(): Double {
        if (pos < s.length && s[pos] == '-') {
            pos++
            return -factor()
        }
        if (pos < s.length && s[pos] == '(') {
            pos++
            val v = expr()
            if (pos >= s.length || s[pos] != ')') throw IllegalArgumentException("parantez")
            pos++
            return v
        }
        val start = pos
        while (pos < s.length && (s[pos].isDigit() || s[pos] == '.')) pos++
        if (start == pos) throw IllegalArgumentException("sayı yok")
        return s.substring(start, pos).toDouble()
    }
}

fun evalMath(input: String): Double? {
    val cleaned = input.lowercase(Locale("tr", "TR"))
        .replace("çarpı", "*").replace("çarpi", "*").replace("kere", "*")
        .replace("bölü", "/").replace("artı", "+").replace("eksi", "-")
        .replace(",", ".").replace(" ", "").replace(":", "")
    if (cleaned.isEmpty() || !Regex("^[0-9.+\\-*/()]+$").matches(cleaned)) return null
    return try {
        val v = MathParser(cleaned).parse()
        if (v.isNaN() || v.isInfinite()) null else v
    } catch (e: Exception) {
        null
    }
}

fun formatNumber(v: Double): String =
    if (v == Math.floor(v) && Math.abs(v) < 1e15) v.toLong().toString()
    else "%.2f".format(Locale.US, v)

fun waNumber(raw: String): String {
    var d = raw.filter { it.isDigit() }
    if (d.startsWith("00")) {
        d = d.substring(2)
    } else if (d.startsWith("0")) {
        d = "90" + d.substring(1)
    } else if (d.length == 10) {
        d = "90" + d
    }
    return d
}

// Türkçe hal ve iyelik eklerini temizler: "annemi" -> "anne", "Ayşe'ye" -> "ayşe", "whatsapp'ı" -> "whatsapp".
// Yalnızca aramalarda yedek olarak kullanılır (önce söylenen biçim denenir); kök en az 3 harf kalır.
private val TR_SUFFIXES = listOf(
    "nin", "nın", "nun", "nün", "dan", "den", "tan", "ten", "yla", "yle",
    "ya", "ye", "yi", "yı", "yu", "yü", "da", "de", "ta", "te",
    "in", "ın", "un", "ün", "na", "ne", "ni", "nı", "nu", "nü",
    "a", "e", "i", "ı", "u", "ü", "m"
)

fun cleanSuffixes(word: String): String {
    var w = word.lowercase(Locale("tr", "TR")).trim()
    val apos = w.indexOfFirst { it == '\'' || it == '\u2019' }
    if (apos > 0) w = w.substring(0, apos)
    for (pass in 0 until 2) {
        val hit = TR_SUFFIXES.firstOrNull { w.endsWith(it) && w.length - it.length >= 3 } ?: break
        w = w.dropLast(hit.length)
    }
    return w
}
