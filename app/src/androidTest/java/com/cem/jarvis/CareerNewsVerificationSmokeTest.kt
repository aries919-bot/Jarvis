package com.cem.jarvis

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CareerNewsVerificationSmokeTest {
    private lateinit var context: Context

    @Before
    fun clearH1Database() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("career_news_verification_v1.db")
    }

    @Test
    fun opensH1ActivityAndPersistsUnknownPublicationDateAsNull() {
        val headline = "Kurum personel alım duyurusu"
        val sourceUrl = "https://www.dsi.gov.tr/duyurular"
        val storyId = CareerNewsVerificationRecord.stableStoryId(sourceUrl, headline)
        val intent = Intent(context, CareerNewsVerificationActivity::class.java).apply {
            putExtra(CareerNewsVerificationActivity.EXTRA_STORY_ID, storyId)
            putExtra(CareerNewsVerificationActivity.EXTRA_TITLE, headline)
            putExtra(CareerNewsVerificationActivity.EXTRA_URL, sourceUrl)
            putExtra(CareerNewsVerificationActivity.EXTRA_PUBLISHER, "Devlet Su İşleri")
        }

        val scenario = ActivityScenario.launch<CareerNewsVerificationActivity>(intent)
        try {
            onView(withText("HABER / İDDİA DOĞRULAMA")).check(matches(isDisplayed()))
            onView(withText(headline)).check(matches(isDisplayed()))
            onView(withText("＋  İddia ekle")).check(matches(isDisplayed()))
            onView(withText("Asıl kaynak sayfasını aç")).check(matches(isDisplayed()))

            val stored = CareerNewsVerificationStore(SQLiteCareerNewsStorage(context)).load(storyId)
            assertNotNull(stored)
            assertEquals(headline, stored!!.headline)
            assertEquals(null, stored.publishedAt)
            assertEquals("https://www.dsi.gov.tr/duyurular", stored.originalSourceUrl)
        } finally {
            scenario.close()
        }
    }

    @Test
    fun sqliteLastGoodCopyRecoversCorruptPrimaryWithoutLosingPreviousRecord() {
        val storage = SQLiteCareerNewsStorage(context)
        try {
            val store = CareerNewsVerificationStore(storage)
            val sourceUrl = "https://www.dsi.gov.tr/duyurular"
            val first = CareerNewsVerificationRecord(
                storyId = CareerNewsVerificationRecord.stableStoryId(sourceUrl, "Kurum personel duyurusu"),
                headline = "Kurum personel duyurusu",
                publisher = "Devlet Su İşleri",
                originalSourceUrl = sourceUrl,
                publishedAt = null,
                collectedAt = null,
                firstSeenAt = null
            )
            val updated = first.copy(headline = "Kurum personel duyurusu güncellendi")
            assertEquals(true, store.save(first).saved)
            assertEquals(true, store.save(updated).saved)

            assertEquals(true, storage.putAtomically(mapOf("record." + first.storyId to "{broken-primary")))
            assertEquals(first, store.load(first.storyId))
            assertEquals(1, store.snapshot().recoveredFromBackup)
            assertEquals(0, store.snapshot().corruptRecords)
        } finally {
            storage.close()
        }
    }
}
