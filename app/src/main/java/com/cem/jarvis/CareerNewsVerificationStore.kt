package com.cem.jarvis

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** Testable persistence boundary; implementations commit a key/value group atomically. */
interface CareerNewsVerificationKeyValueStore {
    fun get(key: String): String?
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

    fun load(storyId: String): CareerNewsVerificationRecord? {
        if (storyId.isBlank()) return null
        val primary = storage.get(recordKey(storyId))
            ?.let { CareerNewsVerificationRecord.fromJson(it) }
        if (primary != null && primary.storyId == storyId) return primary
        return storage.get(backupKey(storyId))
            ?.let { CareerNewsVerificationRecord.fromJson(it) }
            ?.takeIf { it.storyId == storyId }
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
