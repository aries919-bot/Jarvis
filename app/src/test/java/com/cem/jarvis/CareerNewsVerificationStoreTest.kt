package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerNewsVerificationStoreTest {
    private class MemoryStorage : CareerNewsVerificationKeyValueStore {
        val values = linkedMapOf<String, String>()
        var failWrites = false

        override fun get(key: String): String? = values[key]
        override fun keys(): Set<String> = values.keys.toSet()
        override fun putAtomically(values: Map<String, String>): Boolean {
            if (failWrites) return false
            this.values.putAll(values)
            return true
        }
    }

    private fun record(headline: String = "Kurum personel duyurusu") =
        CareerNewsVerificationRecord(
            storyId = CareerNewsVerificationRecord.stableStoryId(
                "https://kurum.gov.tr/duyuru/1",
                "Kurum personel duyurusu"
            ),
            headline = headline,
            publisher = "Örnek kurum",
            originalSourceUrl = "https://kurum.gov.tr/duyuru/1",
            publishedAt = null,
            collectedAt = "2026-11-25T14:00:00Z",
            firstSeenAt = null
        )

    @Test
    fun savesAndLoadsWithoutTouchingLegacyArchiveKeys() {
        val storage = MemoryStorage()
        storage.values["legacy.career_news_items"] = "[{\"title\":\"eski haber\"}]"
        val store = CareerNewsVerificationStore(storage)

        assertTrue(store.save(record()).saved)
        assertEquals(record(), store.load(record().storyId))
        assertEquals("[{\"title\":\"eski haber\"}]", storage.values["legacy.career_news_items"])
    }

    @Test
    fun updateKeepsPreviousGoodRecordAsBackup() {
        val storage = MemoryStorage()
        val store = CareerNewsVerificationStore(storage)
        val first = record()
        val second = first.copy(headline = "Kurum personel duyurusu güncellendi")

        assertTrue(store.save(first).saved)
        assertTrue(store.save(second).saved)
        val backupKey = "last_good." + first.storyId
        val backup = CareerNewsVerificationRecord.fromJson(storage.values[backupKey]!!)
        assertEquals(first, backup)
        assertEquals(second, store.load(first.storyId))
    }

    @Test
    fun corruptedPrimaryRecoversLastGoodAndReportsRecovery() {
        val storage = MemoryStorage()
        val store = CareerNewsVerificationStore(storage)
        val first = record()
        val second = first.copy(headline = "Kurum personel duyurusu güncellendi")
        assertTrue(store.save(first).saved)
        assertTrue(store.save(second).saved)
        storage.values["record." + first.storyId] = "{broken-json"

        assertEquals(first, store.load(first.storyId))
        val snapshot = store.snapshot()
        assertEquals(1, snapshot.recoveredFromBackup)
        assertEquals(0, snapshot.corruptRecords)
        assertEquals(first, snapshot.records.single())
    }

    @Test
    fun corruptPrimaryAndBackupBlockOverwrite() {
        val storage = MemoryStorage()
        val store = CareerNewsVerificationStore(storage)
        val storyId = record().storyId
        storage.values["record." + storyId] = "{bad-primary"
        storage.values["last_good." + storyId] = "{bad-backup"

        val result = store.save(record())
        assertFalse(result.saved)
        assertTrue(result.reason.orEmpty().contains("veri kaybını önlemek"))
        assertEquals("{bad-primary", storage.values["record." + storyId])
        assertEquals("{bad-backup", storage.values["last_good." + storyId])
    }

    @Test
    fun capacityRefusesNewRecordsInsteadOfEvictingOldOnes() {
        val storage = MemoryStorage()
        val store = CareerNewsVerificationStore(storage, maxRecords = 1)
        val first = record()
        val second = first.copy(
            storyId = "story-2",
            originalSourceUrl = "https://kurum.gov.tr/duyuru/2"
        )
        assertTrue(store.save(first).saved)
        val result = store.save(second)
        assertFalse(result.saved)
        assertEquals(first, store.load(first.storyId))
        assertNull(store.load(second.storyId))
        assertEquals(1, store.snapshot().records.size)
    }

    @Test
    fun invalidRecordAndFailedCommitAreReported() {
        val storage = MemoryStorage()
        val store = CareerNewsVerificationStore(storage)
        val invalid = record().copy(originalSourceUrl = "http://kurum.gov.tr/duyuru/1")
        assertFalse(store.save(invalid).saved)
        assertTrue(storage.values.isEmpty())

        storage.failWrites = true
        assertFalse(store.save(record()).saved)
        assertTrue(storage.values.isEmpty())
    }
}
