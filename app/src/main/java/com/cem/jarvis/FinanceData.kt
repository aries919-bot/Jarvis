package com.cem.jarvis

import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

data class FinancePricePoint(
    val id: String,
    val title: String,
    val bid: Double,
    val ask: Double,
    val source: String,
    val updatedAt: String,
    val stale: Boolean
)

data class TurkishMarketSnapshot(
    val date: String,
    val updatedAt: String,
    val stale: Boolean,
    val sources: List<String>,
    val prices: List<FinancePricePoint>
)

data class FinanceSymbolCandidate(
    val symbol: String,
    val description: String,
    val displaySymbol: String,
    val type: String
)

data class FinanceQuote(
    val symbol: String,
    val current: Double,
    val change: Double?,
    val changePercent: Double?,
    val previousClose: Double?,
    val timestampSeconds: Long
)

object FinanceData {
    const val TURKEY_MARKET_URL =
        "https://cdn.jsdelivr.net/gh/MehmetAkgul/currency-api-tr@main/v1/currencies/try-full.json"

    private const val FINNHUB_BASE = "https://finnhub.io/api/v1"

    private val goldAssets = listOf(
        Triple("xau_gram", "Gram altın", "TRY"),
        Triple("xau_ceyrek", "Çeyrek altın", "TRY"),
        Triple("xau_yarim", "Yarım altın", "TRY"),
        Triple("xau_tam", "Tam altın", "TRY"),
        Triple("xau_cumhuriyet", "Cumhuriyet altını", "TRY")
    )

    private val currencyAssets = listOf(
        Triple("usd", "ABD doları", "TRY"),
        Triple("eur", "Euro", "TRY"),
        Triple("gbp", "İngiliz sterlini", "TRY")
    )

    fun fetchTurkishMarket(): TurkishMarketSnapshot {
        val json = request(TURKEY_MARKET_URL)
        return parseTurkishMarket(json)
    }

    internal fun parseTurkishMarket(json: String): TurkishMarketSnapshot {
        val root = JSONObject(json)
        val tryRates = root.optJSONObject("try")
            ?: throw IOException("Piyasa verisi beklenen biçimde değil.")

        val rootUpdatedAt = root.optString("updated_at")
        val rootStale = root.optBoolean("is_stale", false) || timestampIsStale(rootUpdatedAt)
        val prices = (goldAssets + currencyAssets).mapNotNull { (id, title, _) ->
            val item = tryRates.optJSONObject(id) ?: return@mapNotNull null
            val bid = item.optDouble("bid", Double.NaN)
            val ask = item.optDouble("ask", Double.NaN)
            if (!bid.isFinite() || !ask.isFinite() || bid <= 0.0 || ask <= 0.0) {
                return@mapNotNull null
            }
            val updatedAt = item.optString("updated_at", rootUpdatedAt)
            FinancePricePoint(
                id = id,
                title = title,
                bid = bid,
                ask = ask,
                source = item.optString("source", "belirtilmedi"),
                updatedAt = updatedAt,
                stale = item.optBoolean("stale", false) || rootStale || timestampIsStale(updatedAt)
            )
        }

        if (prices.isEmpty()) throw IOException("Altın ve döviz fiyatı bulunamadı.")
        return TurkishMarketSnapshot(
            date = root.optString("date"),
            updatedAt = rootUpdatedAt,
            stale = rootStale || prices.any { it.stale },
            sources = (0 until root.optJSONArray("sources").let { it?.length() ?: 0 })
                .mapNotNull { root.optJSONArray("sources")?.optString(it) }
                .filter { it.isNotBlank() },
            prices = prices
        )
    }

    fun searchFinnhubSymbols(query: String, apiKey: String): List<FinanceSymbolCandidate> {
        require(apiKey.isNotBlank()) { "Finnhub API anahtarı girilmedi." }
        val clean = query.trim()
        require(clean.length in 1..60) { "Hisse araması 1–60 karakter olmalı." }
        val address = "$FINNHUB_BASE/search?q=" + encode(clean) + "&token=" + encode(apiKey)
        val json = request(address)
        return parseFinnhubSymbols(json)
    }

    internal fun parseFinnhubSymbols(json: String): List<FinanceSymbolCandidate> {
        val root = JSONObject(json)
        val array = root.optJSONArray("result") ?: return emptyList()
        val output = ArrayList<FinanceSymbolCandidate>()
        val seen = HashSet<String>()
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val symbol = item.optString("symbol").trim().uppercase(Locale.ROOT)
            val description = item.optString("description").trim()
            if (!isValidSymbol(symbol) || description.isBlank() || !seen.add(symbol)) continue
            output += FinanceSymbolCandidate(
                symbol = symbol,
                description = description.take(120),
                displaySymbol = item.optString("displaySymbol").ifBlank { symbol }.take(40),
                type = item.optString("type").take(50)
            )
            if (output.size >= 12) break
        }
        return output
    }

    fun fetchFinnhubQuote(symbol: String, apiKey: String): FinanceQuote {
        require(apiKey.isNotBlank()) { "Finnhub API anahtarı girilmedi." }
        val clean = symbol.trim().uppercase(Locale.ROOT)
        require(isValidSymbol(clean)) { "Hisse kodu geçersiz." }
        val json = request("$FINNHUB_BASE/quote?symbol=" + encode(clean) + "&token=" + encode(apiKey))
        return parseFinnhubQuote(clean, json)
    }

    internal fun parseFinnhubQuote(symbol: String, json: String): FinanceQuote {
        val root = JSONObject(json)
        val current = root.optDouble("c", Double.NaN)
        if (!current.isFinite() || current <= 0.0) {
            throw IOException("$symbol için Finnhub fiyat döndürmedi. Sembol kapsamını ve veri erişimini kontrol et.")
        }
        val change = root.optDouble("d", Double.NaN).takeIf { it.isFinite() }
        val changePercent = root.optDouble("dp", Double.NaN).takeIf { it.isFinite() }
        val previousClose = root.optDouble("pc", Double.NaN).takeIf { it.isFinite() && it > 0.0 }
        return FinanceQuote(
            symbol = symbol,
            current = current,
            change = change,
            changePercent = changePercent,
            previousClose = previousClose,
            timestampSeconds = root.optLong("t", 0L)
        )
    }

    internal fun timestampIsStale(value: String, maxAgeHours: Long = 36L): Boolean {
        if (value.isBlank()) return true
        val patterns = listOf("yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ssX")
        val date = patterns.firstNotNullOfOrNull { pattern ->
            runCatching {
                SimpleDateFormat(pattern, Locale.US).apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                    isLenient = false
                }.parse(value)
            }.getOrNull()
        } ?: return true
        val age = System.currentTimeMillis() - date.time
        return age > TimeUnitHours.toMillis(maxAgeHours) || age < -TimeUnitHours.toMillis(1)
    }

    private object TimeUnitHours {
        fun toMillis(hours: Long): Long = hours * 60L * 60L * 1000L
    }

    fun isValidSymbol(symbol: String): Boolean =
        symbol.length in 1..24 &&
            symbol == symbol.uppercase(Locale.ROOT) &&
            Regex("^[A-Z0-9][A-Z0-9._:-]{0,23}$").matches(symbol)

    private fun request(address: String): String {
        val connection = (URL(address).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8_000
            readTimeout = 12_000
            instanceFollowRedirects = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "Aries-Android/1.22")
        }
        try {
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (code !in 200..299) {
                val reason = when (code) {
                    401, 403 -> "Finnhub anahtarı geçersiz veya veri erişimi yok."
                    429 -> "Finnhub istek sınırına ulaşıldı. Biraz sonra tekrar dene."
                    else -> "Veri sağlayıcısı HTTP $code yanıtı verdi."
                }
                throw IOException(reason)
            }
            if (body.isBlank()) throw IOException("Veri sağlayıcısı boş yanıt verdi.")
            return body
        } finally {
            connection.disconnect()
        }
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, Charsets.UTF_8.name())
}
