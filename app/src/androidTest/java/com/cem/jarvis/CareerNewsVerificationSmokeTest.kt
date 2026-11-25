package com.cem.jarvis

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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


    @Test
    fun evidenceReviewAndCorrectionFlowPersistsPublicationDateAndAuditDetails() {
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
            onView(withText("＋  İddia ekle")).perform(click())
            onView(withHint("Tek ve doğrulanabilir iddia cümlesi yaz"))
                .perform(typeText("Kurum 12 teknisyen alımı yapacak."), closeSoftKeyboard())
            onView(withText("Kaydet")).perform(click())
            awaitRecord(storyId) { it?.claims?.size == 1 }

            onView(withText("＋ Kanıt ekle")).perform(click())
            onView(withHint("HTTPS kaynak bağlantısı"))
                .perform(typeText(sourceUrl), closeSoftKeyboard())
            onView(withHint("Kaynak kurum / yayıncı"))
                .perform(typeText("Devlet Su İşleri"), closeSoftKeyboard())
            onView(withHint("Kaynak yayın tarihi (UTC, isteğe bağlı)"))
                .perform(typeText("2026-11-20T10:00:00Z"), closeSoftKeyboard())
            onView(withHint("Kaynakta görülen kısa kanıt alıntısı"))
                .perform(typeText("Duyuruda 12 teknisyen alımı yazıyor."), closeSoftKeyboard())
            onView(withText("Kanıtı kaydet")).perform(click())
            awaitRecord(storyId) {
                it?.claims?.singleOrNull()?.evidence?.singleOrNull()?.publishedAt == "2026-11-20T10:00:00Z"
            }

            onView(withText("İnsan incelemesi")).perform(click())
            onView(withHint("Asıl kaynakla karşılaştırma notu"))
                .perform(typeText("Resmî duyuru metni karşılaştırıldı."), closeSoftKeyboard())
            onView(withText("Kaynağı açtım ve iddiayı kaynak metniyle karşılaştırdım"))
                .perform(click())
            onView(withText("İncelemeyi kaydet")).perform(click())
            awaitRecord(storyId) { it?.claims?.singleOrNull()?.reviewDecision != null }

            onView(withText("＋ Kanıt ekle")).perform(click())
            onView(withHint("HTTPS kaynak bağlantısı"))
                .perform(typeText(sourceUrl), closeSoftKeyboard())
            onView(withHint("Kaynak kurum / yayıncı"))
                .perform(typeText("Devlet Su İşleri"), closeSoftKeyboard())
            onView(withHint("Kaynakta görülen kısa kanıt alıntısı"))
                .perform(typeText("Aynı duyurunun ek açıklaması."), closeSoftKeyboard())
            onView(withText("Kanıtı kaydet")).perform(click())
            awaitRecord(storyId) {
                val claim = it?.claims?.singleOrNull()
                claim != null && claim.reviewDecision == null && claim.reviewHistory.size == 1 &&
                    claim.reviewHistory.single().note == "Resmî duyuru metni karşılaştırıldı."
            }
            onView(withText("ÖNCEKİ İNCELEMELER")).check(matches(isDisplayed()))
            onView(withText("Resmî duyuru metni karşılaştırıldı.")).check(matches(isDisplayed()))

            onView(withText("İddiayı düzelt")).perform(click())
            onView(withHint("Düzeltilmiş tek iddia cümlesi"))
                .perform(clearText(), typeText("Kurum 10 teknisyen alımı yapacak."), closeSoftKeyboard())
            onView(withHint("Düzeltmeyi destekleyen HTTPS kaynak"))
                .perform(typeText("https://www.dsi.gov.tr/duyurular/duzeltme"), closeSoftKeyboard())
            onView(withHint("Düzeltme nedeni"))
                .perform(typeText("Kurumun resmî düzeltme duyurusu kontenjanı güncelledi."), closeSoftKeyboard())
            onView(withText("Düzeltmeyi kaydet")).perform(click())
            awaitRecord(storyId) { it?.correctionHistory?.size == 1 }

            onView(withText("Önceki kanıt ayrıntıları")).check(matches(isDisplayed()))
            onView(withText("Duyuruda 12 teknisyen alımı yazıyor.")).check(matches(isDisplayed()))
            val stored = readRecord(storyId)
            assertEquals("Kurum 10 teknisyen alımı yapacak.", stored?.claims?.singleOrNull()?.text)
            assertEquals(2, stored?.correctionHistory?.singleOrNull()?.previousEvidence?.size)
        } finally {
            scenario.close()
        }
    }

    private fun awaitRecord(
        storyId: String,
        condition: (CareerNewsVerificationRecord?) -> Boolean
    ) {
        val deadline = SystemClock.uptimeMillis() + 8000
        while (SystemClock.uptimeMillis() < deadline) {
            val current = readRecord(storyId)
            if (condition(current)) {
                InstrumentationRegistry.getInstrumentation().waitForIdleSync()
                return
            }
            SystemClock.sleep(50)
        }
        throw AssertionError("H1 SQLite state did not reach the expected transition.")
    }

    private fun readRecord(storyId: String): CareerNewsVerificationRecord? {
        val storage = SQLiteCareerNewsStorage(context)
        return try {
            CareerNewsVerificationStore(storage).load(storyId)
        } finally {
            storage.close()
        }
    }

    @Test
    fun displaysArchivedHumanReviewHistoryFromSqlite() {
        val sourceUrl = "https://www.dsi.gov.tr/duyurular"
        val headline = "Kurum personel alım duyurusu"
        val storyId = CareerNewsVerificationRecord.stableStoryId(sourceUrl, headline)
        val evidence = CareerNewsEvidence(
            id = "evidence-1",
            publisher = "Devlet Su İşleri",
            sourceUrl = sourceUrl,
            sourceType = CareerEvidenceSourceType.PRIMARY_SOURCE,
            stance = CareerEvidenceStance.SUPPORTS,
            excerpt = "Kurum personel alımı yapacağını duyurdu.",
            publishedAt = null,
            checkedAt = "2026-11-25T14:00:00Z"
        )
        val archived = CareerNewsVerificationRecord(
            storyId = storyId,
            headline = headline,
            publisher = "Devlet Su İşleri",
            originalSourceUrl = sourceUrl,
            publishedAt = null,
            collectedAt = null,
            firstSeenAt = null
        ).addClaim(CareerNewsClaim(id = "claim-1", text = "Kurum personel alımı yapacak."))!!
            .addEvidence("claim-1", evidence)!!
            .reviewClaim("claim-1", CareerHumanReviewDecision.CONFIRM,
                "2026-11-25T14:05:00Z", "İlk inceleme arşivde saklanmalı.")!!
            .reviewClaim("claim-1", CareerHumanReviewDecision.INCONCLUSIVE,
                "2026-11-25T14:10:00Z", "İkinci inceleme kararsız kaldı.")!!

        val storage = SQLiteCareerNewsStorage(context)
        try {
            assertTrue(CareerNewsVerificationStore(storage).save(archived).saved)
        } finally {
            storage.close()
        }

        val intent = Intent(context, CareerNewsVerificationActivity::class.java).apply {
            putExtra(CareerNewsVerificationActivity.EXTRA_STORY_ID, storyId)
            putExtra(CareerNewsVerificationActivity.EXTRA_TITLE, headline)
            putExtra(CareerNewsVerificationActivity.EXTRA_URL, sourceUrl)
            putExtra(CareerNewsVerificationActivity.EXTRA_PUBLISHER, "Devlet Su İşleri")
        }
        val scenario = ActivityScenario.launch<CareerNewsVerificationActivity>(intent)
        try {
            onView(withText("ÖNCEKİ İNCELEMELER")).check(matches(isDisplayed()))
            onView(withText("İlk inceleme arşivde saklanmalı.")).check(matches(isDisplayed()))
        } finally {
            scenario.close()
        }
    }
}
