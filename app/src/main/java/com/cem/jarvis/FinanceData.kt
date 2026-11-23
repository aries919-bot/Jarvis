package com.cem.jarvis

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.security.KeyStore
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

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
    val timestampSeconds: Long,
    val stale: Boolean = false
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

    fun fetchTurkishMarket(): TurkishMarketSnapshot =
        parseTurkishMarket(fetchTurkishMarketPayload())

    internal fun fetchTurkishMarketPayload(): String = request(TURKEY_MARKET_URL)

    internal fun asOfflineSnapshot(snapshot: TurkishMarketSnapshot): TurkishMarketSnapshot =
        snapshot.copy(stale = true, prices = snapshot.prices.map { it.copy(stale = true) })

    internal fun parseTurkishMarket(json: String): TurkishMarketSnapshot {
        val root = JSONObject(json)
        val tryRates = root.optJSONObject("try")
            ?: throw IOException("Piyasa verisi beklenen biçimde değil.")

        val rootUpdatedAt = root.optString("updated_at")
        // The provider-level is_stale flag documents cached gold data; do not apply it to FX rows.
        val providerGoldStale = root.optBoolean("is_stale", false)
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
                stale = item.optBoolean("stale", false) ||
                    (id.startsWith("xau_") && providerGoldStale) || timestampIsStale(updatedAt)
            )
        }

        if (prices.isEmpty()) throw IOException("Altın ve döviz fiyatı bulunamadı.")
        return TurkishMarketSnapshot(
            date = root.optString("date"),
            updatedAt = rootUpdatedAt,
            stale = prices.any { it.stale },
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

    internal fun timestampIsStale(value: String, maxAgeHours: Long = 2L): Boolean {
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

/**
 * Stores the Finnhub credential encrypted with a non-exportable Android Keystore AES key.
 * If an older app version left a plain preference, it is migrated and removed on first read.
 */
object FinanceKeyStore {
    private const val PREFS = "j"
    private const val ENCRYPTED_PREF = "finnhub_enc"
    private const val LEGACY_PREF = "finnhub"
    private const val KEY_ALIAS = "aries_finnhub_aes_v1"
    private const val GCM_IV_BYTES = 12

    fun hasKey(context: Context): Boolean = read(context).isNotBlank()

    fun save(context: Context, rawKey: String): Boolean {
        if (rawKey.isBlank()) return false
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            val encrypted = cipher.doFinal(rawKey.trim().toByteArray(Charsets.UTF_8))
            val payload = cipher.iv + encrypted
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString(ENCRYPTED_PREF, Base64.encodeToString(payload, Base64.NO_WRAP))
                .remove(LEGACY_PREF)
                .apply()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun read(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val saved = prefs.getString(ENCRYPTED_PREF, "").orEmpty()
        if (saved.isNotBlank()) {
            return try {
                val payload = Base64.decode(saved, Base64.NO_WRAP)
                if (payload.size <= GCM_IV_BYTES) throw IllegalStateException("Şifreli anahtar verisi eksik")
                val iv = payload.copyOfRange(0, GCM_IV_BYTES)
                val encrypted = payload.copyOfRange(GCM_IV_BYTES, payload.size)
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
                String(cipher.doFinal(encrypted), Charsets.UTF_8)
            } catch (_: Exception) {
                // A restored backup may contain ciphertext without the device-bound Keystore key.
                prefs.edit().remove(ENCRYPTED_PREF).apply()
                ""
            }
        }

        val legacy = prefs.getString(LEGACY_PREF, "").orEmpty()
        if (legacy.isNotBlank() && save(context, legacy)) return legacy
        return ""
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(ENCRYPTED_PREF)
            .remove(LEGACY_PREF)
            .apply()
        runCatching {
            KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
                deleteEntry(KEY_ALIAS)
            }
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val existing = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existing != null) return existing

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }
}

