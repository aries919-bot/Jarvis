package com.cem.jarvis

import android.content.Context
import android.content.SharedPreferences

/** Testable persistence boundary; implementations commit a key/value group atomically. */
interface CareerNewsVerificationKeyValueStore {
    fun get(key: String): String?
    fun keys(): Set<String>
    fun putAtomically(values: Map<String, String>): Boolean
}

class SharedPreferencesCareerNewsStorage(context: Context) : CareerNewsVerificationKeyValueStore {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("career_news_verification_v1", Context.MODE_PRIVATE)

    override fun get(key: String): String? = preferences.getString(key, null)

    override fun keys(): Set<String> = preferences.all.keys

    override fun putAtomically(values: Map<String, String>): Boolean {
        val editor = preferences.edit()
        values.forEach { (key, value) -> editor.putString(key, value) }
        return editor.commit()
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
            ?.let(CareerNewsVerificationRecord::fromJson)
        if (primary != null && primary.storyId == storyId) return primary
        return storage.get(backupKey(storyId))
            ?.let(CareerNewsVerificationRecord::fromJson)
            ?.takeIf { it.storyId == storyId }
    }

    fun save(record: CareerNewsVerificationRecord): CareerNewsVerificationSaveResult {
        val errors = record.validationErrors()
        if (errors.isNotEmpty()) {
            return CareerNewsVerificationSaveResult(false, "Kayıt doğrulaması başarısız: \${errors.joinToString(" ")}")
        }
        if (maxRecords < 1) return CareerNewsVerificationSaveResult(false, "Arşiv kapasitesi kapalı.")

        val key = recordKey(record.storyId)
        val backup = backupKey(record.storyId)
        val primaryRaw = storage.get(key)
        val backupRaw = storage.get(backup)
        val previous = primaryRaw?.let(CareerNewsVerificationRecord::fromJson)
            ?.takeIf { it.storyId == record.storyId }
            ?: backupRaw?.let(CareerNewsVerificationRecord::fromJson)
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
        if (previous != null) values[backup] = previous.toJson().toString()
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
            val primary = storage.get(key)?.let(CareerNewsVerificationRecord::fromJson)
                ?.takeIf { it.storyId == storyId }
            if (primary != null) {
                records += primary
            } else {
                val backup = storage.get(backupKey(storyId))
                    ?.let(CareerNewsVerificationRecord::fromJson)
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
