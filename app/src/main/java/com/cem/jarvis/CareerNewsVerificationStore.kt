package com.cem.jarvis

import android.content.SharedPreferences
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * A separate, bounded H1 archive. It never rewrites career_news_items and never drops
 * existing H1 records when the JSON is malformed or the storage budget is exceeded.
 */
class CareerNewsVerificationStore private constructor(
    private val readRaw: () -> String?,
    private val writeRaw: (String) -> Boolean
) {
    constructor(preferences: SharedPreferences) : this(
        { preferences.getString(KEY, null) },
        { raw -> preferences.edit().putString(KEY, raw).commit() }
    )

    fun all(): List<CareerNewsVerificationRecord>? = synchronized(LOCK) {
        readAllUnsafe()
    }

    fun find(storyId: String): CareerNewsVerificationRecord? =
        all()?.firstOrNull { it.storyId == storyId }

    fun getOrCreate(
        headline: String,
        publisher: String,
        originalSourceUrl: String,
        collectedAtMillis: Long,
        firstSeenAtMillis: Long
    ): CareerNewsVerificationRecord? = synchronized(LOCK) {
        val records = readAllUnsafe() ?: return@synchronized null
        val storyId = CareerNewsVerificationRecord.stableStoryId(originalSourceUrl, headline)
        records.firstOrNull { it.storyId == storyId }?.let { return@synchronized it }

        val now = utcNow()
        val collectedAt = collectedAtMillis.takeIf { it > 0L }?.let(::utcFromMillis) ?: now
        val firstSeenAt = firstSeenAtMillis.takeIf { it > 0L }?.let(::utcFromMillis) ?: now
        val record = CareerNewsVerificationRecord(
            storyId = storyId,
            headline = headline.trim(),
            publisher = publisher.trim().ifBlank { "Yayıncı bilinmiyor" },
            originalSourceUrl = originalSourceUrl.trim(),
            publishedAt = null,
            collectedAt = collectedAt,
            firstSeenAt = firstSeenAt
        )
        if (record.validationErrors().isNotEmpty()) return@synchronized null
        val output = JSONArray()
        records.forEach { output.put(it.toJson()) }
        output.put(record.toJson())
        val raw = output.toString()
        if (raw.toByteArray(Charsets.UTF_8).size > MAX_BYTES) return@synchronized null
        if (!writeRaw(raw)) return@synchronized null
        record
    }

    fun save(record: CareerNewsVerificationRecord): Boolean = synchronized(LOCK) {
        if (record.validationErrors().isNotEmpty()) return@synchronized false
        val records = readAllUnsafe() ?: return@synchronized false
        val output = JSONArray()
        var replaced = false
        records.forEach { existing ->
            if (existing.storyId == record.storyId) {
                if (record.firstSeenAt != existing.firstSeenAt) return@synchronized false
                if (record.correctionHistory.size < existing.correctionHistory.size ||
                    record.correctionHistory.take(existing.correctionHistory.size) != existing.correctionHistory
                ) return@synchronized false

                val oldClaims = existing.claims.associateBy { it.id }
                val newClaims = record.claims.associateBy { it.id }
                if (!newClaims.keys.containsAll(oldClaims.keys)) return@synchronized false
                oldClaims.forEach { (claimId, oldClaim) ->
                    val newClaim = newClaims[claimId] ?: return@synchronized false
                    val hasRecordedCorrection = record.correctionHistory.any { correction ->
                        correction.claimId == claimId &&
                            correction.previousText == oldClaim.text &&
                            correction.previousEvidence == oldClaim.evidence
                    }
                    if (oldClaim.text != newClaim.text && !hasRecordedCorrection) {
                        return@synchronized false
                    }
                    if (!newClaim.evidence.containsAll(oldClaim.evidence) && !hasRecordedCorrection) {
                        return@synchronized false
                    }
                }
                output.put(record.toJson())
                replaced = true
            } else {
                output.put(existing.toJson())
            }
        }
        if (!replaced) output.put(record.toJson())
        val raw = output.toString()
        if (raw.toByteArray(Charsets.UTF_8).size > MAX_BYTES) return@synchronized false
        writeRaw(raw)
    }

    private fun readAllUnsafe(): List<CareerNewsVerificationRecord>? {
        val raw = runCatching { readRaw() }.getOrNull() ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: return null
                    val parsed = CareerNewsVerificationRecord.fromJson(item.toString()) ?: return null
                    add(parsed)
                }
            }
        }.getOrNull()
    }

    private fun utcNow(): String = utcFromMillis(System.currentTimeMillis())

    private fun utcFromMillis(value: Long): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date(value))

    companion object {
        private const val KEY = "career_news_verification_v1"
        private const val MAX_BYTES = 1_500_000
        private val LOCK = Any()

        internal fun forTests(
            readRaw: () -> String?,
            writeRaw: (String) -> Boolean
        ) = CareerNewsVerificationStore(readRaw, writeRaw)
    }
}
