package com.cem.jarvis

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** Testable persistence boundary; implementations commit a key/value group atomically. */
interface CareerNewsVerificationKeyValueStore {
    fun get(key: String): String?
    fun getMany(keys: Set<String>): Map<String, String?> = keys.associateWith { get(it) }
    fun keys(): Set<String>
    fun putAtomically(values: Map<String, String>): Boolean
}

class SQLiteCareerNewsStorage(context: Context) :
    SQLiteOpenHelper(context.applicationContext, "career_news_verification_v1.db", null, 1),
    CareerNewsVerificationKeyValueStore {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS verification_kv (" +
                "key TEXT PRIMARY KEY NOT NULL, value TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Additive migrations only. Never drop the user's evidence or correction history.
        onCreate(db)
    }

    override fun get(key: String): String? =
        readableDatabase.query(
            "verification_kv",
            arrayOf("value"),
            "key = ?",
            arrayOf(key),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }

    override fun getMany(keys: Set<String>): Map<String, String?> {
        if (keys.isEmpty()) return emptyMap()
        val keyList = keys.toList()
        val placeholders = keyList.joinToString(",") { "?" }
        val found = readableDatabase.query(
            "verification_kv",
            arrayOf("key", "value"),
            "key IN ($placeholders)",
            keyList.toTypedArray(),
            null,
            null,
            null
        ).use { cursor ->
            buildMap {
                while (cursor.moveToNext()) put(cursor.getString(0), cursor.getString(1))
            }
        }
        return keyList.associateWith { found[it] }
    }

    override fun keys(): Set<String> =
        readableDatabase.query(
            "verification_kv",
            arrayOf("key"),
            null,
            null,
            null,
            null,
            null
        ).use { cursor ->
            buildSet {
                while (cursor.moveToNext()) add(cursor.getString(0))
            }
        }

    override fun putAtomically(values: Map<String, String>): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            values.forEach { (key, value) ->
                val row = ContentValues().apply {
                    put("key", key)
                    put("value", value)
                }
                if (db.insertWithOnConflict(
                        "verification_kv",
                        null,
                        row,
                        SQLiteDatabase.CONFLICT_REPLACE
                    ) == -1L
                ) throw IllegalStateException("SQLite row write failed")
            }
            db.setTransactionSuccessful()
            true
        } catch (_: Exception) {
            false
        } finally {
            db.endTransaction()
        }
    }
}

data class CareerNewsVerificationStoreSnapshot(
    val records: List<CareerNewsVerificationRecord>,
    val recoveredFromBackup: Int,
    val corruptRecords: Int
)

data class CareerNewsVerificationSaveResult(
    val saved: Boolean,
    val reason: String? = null
)

/**
 * Separate from legacy career_news_items. Every story has a primary and last-good JSON value.
 * Corrupt data is reported, never silently replaced with an empty archive.
 */
class CareerNewsVerificationStore(
    private val storage: CareerNewsVerificationKeyValueStore,
    private val maxRecords: Int = DEFAULT_MAX_RECORDS
) {
    companion object {
        const val DEFAULT_MAX_RECORDS = 300
        private const val RECORD_PREFIX = "record."
        private const val BACKUP_PREFIX = "last_good."
    }

    private fun recordKey(storyId: String) = RECORD_PREFIX + storyId
    private fun backupKey(storyId: String) = BACKUP_PREFIX + storyId

    private fun preservesHistory(
        previous: CareerNewsVerificationRecord,
        next: CareerNewsVerificationRecord
    ): Boolean {
        if (previous.storyId != next.storyId ||
            previous.originalSourceUrl != next.originalSourceUrl ||
            previous.firstSeenAt != next.firstSeenAt
        ) return false
        if (next.correctionHistory.size < previous.correctionHistory.size ||
            next.correctionHistory.take(previous.correctionHistory.size) != previous.correctionHistory
        ) return false

        val oldClaims = previous.claims.associateBy { it.id }
        val newClaims = next.claims.associateBy { it.id }
        if (!newClaims.keys.containsAll(oldClaims.keys)) return false

        val appendedCorrections = next.correctionHistory.drop(previous.correctionHistory.size)
        oldClaims.forEach { (claimId, oldClaim) ->
            val updated = newClaims[claimId] ?: return false
            val hasCorrection = appendedCorrections.any { correction ->
                correction.claimId == claimId &&
                    correction.previousText == oldClaim.text &&
                    correction.revisedText == updated.text &&
                    correction.previousEvidence == oldClaim.evidence &&
                    correction.previousReviewDecision == oldClaim.reviewDecision &&
                    correction.previousReviewedAt == oldClaim.reviewedAt &&
                    correction.previousReviewNote == oldClaim.reviewNote
            }
            if (oldClaim.text != updated.text && !hasCorrection) return false
            if (!updated.evidence.containsAll(oldClaim.evidence) && !hasCorrection) return false
            if (oldClaim.reviewDecision != null && updated.reviewDecision == null &&
                updated.evidence == oldClaim.evidence && !hasCorrection
            ) return false
        }
        return true
    }

    fun load(storyId: String): CareerNewsVerificationRecord? {
        if (storyId.isBlank()) return null
        val primary = storage.get(recordKey(storyId))
            ?.let { CareerNewsVerificationRecord.fromJson(it) }
        if (primary != null && primary.storyId == storyId) return primary
        return storage.get(backupKey(storyId))
            ?.let { CareerNewsVerificationRecord.fromJson(it) }
            ?.takeIf { it.storyId == storyId }
    }

    /** Read multiple story records with one SQLite IN query for smooth list rendering. */
    fun loadMany(storyIds: Collection<String>): Map<String, CareerNewsVerificationRecord> {
        val ids = storyIds.filter { it.isNotBlank() }.distinct()
        if (ids.isEmpty()) return emptyMap()
        val keys = ids.flatMap { listOf(recordKey(it), backupKey(it)) }.toSet()
        val values = storage.getMany(keys)
        return buildMap {
            ids.forEach { storyId ->
                val primary = values[recordKey(storyId)]
                    ?.let { CareerNewsVerificationRecord.fromJson(it) }
                    ?.takeIf { it.storyId == storyId }
                val recovered = primary ?: values[backupKey(storyId)]
                    ?.let { CareerNewsVerificationRecord.fromJson(it) }
                    ?.takeIf { it.storyId == storyId }
                if (recovered != null) put(storyId, recovered)
            }
        }
    }

    fun save(record: CareerNewsVerificationRecord): CareerNewsVerificationSaveResult {
        val errors = record.validationErrors()
        if (errors.isNotEmpty()) {
            return CareerNewsVerificationSaveResult(false, "Kayıt doğrulaması başarısız: " + errors.joinToString(" "))
        }
        if (maxRecords < 1) return CareerNewsVerificationSaveResult(false, "Arşiv kapasitesi kapalı.")

        val key = recordKey(record.storyId)
        val backup = backupKey(record.storyId)
        val primaryRaw = storage.get(key)
        val backupRaw = storage.get(backup)
        val previous = primaryRaw?.let { CareerNewsVerificationRecord.fromJson(it) }
            ?.takeIf { it.storyId == record.storyId }
            ?: backupRaw?.let { CareerNewsVerificationRecord.fromJson(it) }
                ?.takeIf { it.storyId == record.storyId }

        if ((primaryRaw != null || backupRaw != null) && previous == null) {
            return CareerNewsVerificationSaveResult(
                false,
                "Mevcut kayıt ve son iyi kopyası okunamıyor; veri kaybını önlemek için yazma durduruldu."
            )
        }
        if (previous != null && !preservesHistory(previous, record)) {
            return CareerNewsVerificationSaveResult(
                false,
                "Önceki kanıt veya düzeltme geçmişi silinemez; değişiklik için kayıtlı bir iddia düzeltmesi gerekir."
            )
        }

        val recordCount = storage.keys().count { it.startsWith(RECORD_PREFIX) }
        if (primaryRaw == null && backupRaw == null && recordCount >= maxRecords) {
            return CareerNewsVerificationSaveResult(false, "Haber doğrulama arşivi dolu; eski kayıt silinmedi.")
        }

        val values = linkedMapOf(key to record.toJson().toString())
        values[backup] = (previous ?: record).toJson().toString()
        val saved = storage.putAtomically(values)
        return if (saved) {
            CareerNewsVerificationSaveResult(true)
        } else {
            CareerNewsVerificationSaveResult(false, "Yerel kayıt diske yazılamadı.")
        }
    }

    fun snapshot(): CareerNewsVerificationStoreSnapshot {
        val keys = storage.keys().filter { it.startsWith(RECORD_PREFIX) }.sorted()
        val records = ArrayList<CareerNewsVerificationRecord>()
        var recovered = 0
        var corrupt = 0
        keys.forEach { key ->
            val storyId = key.removePrefix(RECORD_PREFIX)
            val primary = storage.get(key)?.let { CareerNewsVerificationRecord.fromJson(it) }
                ?.takeIf { it.storyId == storyId }
            if (primary != null) {
                records += primary
            } else {
                val backup = storage.get(backupKey(storyId))
                    ?.let { CareerNewsVerificationRecord.fromJson(it) }
                    ?.takeIf { it.storyId == storyId }
                if (backup != null) {
                    records += backup
                    recovered += 1
                } else {
                    corrupt += 1
                }
            }
        }
        return CareerNewsVerificationStoreSnapshot(
            records = records.sortedByDescending { it.collectedAt.orEmpty() },
            recoveredFromBackup = recovered,
            corruptRecords = corrupt
        )
    }
}
