package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class CareerSearchParsingTest {
    @Test
    fun parsesDuckDuckGoResultsWhenAttributeOrderAndQuoteStyleChange() {
        val html = """
            <div class="result">
              <h2><a rel="nofollow" class="result__a" href="https://www.osym.gov.tr/duyuru/1">ÖSYM Personel Duyurusu</a></h2>
              <a class="result__snippet">Ortaöğretim KPSS ve tercih duyurusu.</a>
            </div>
            <div class="result">
              <h2><a href='https://www.kgm.gov.tr/duyuru/2' class='result__a'>Karayolları İşçi Alımı</a></h2>
              <a class='result__snippet'>Noter kurası ve asıl yedek aday listesi.</a>
            </div>
        """.trimIndent()

        val results = Net.parseDuckDuckGoHtml(html, 8)

        assertEquals(2, results.size)
        assertEquals("ÖSYM Personel Duyurusu", results[0].title)
        assertEquals("https://www.osym.gov.tr/duyuru/1", results[0].url)
        assertTrue(results[0].snippet.contains("Ortaöğretim KPSS"))
        assertEquals("Karayolları İşçi Alımı", results[1].title)
        assertEquals("https://www.kgm.gov.tr/duyuru/2", results[1].url)
        assertTrue(results[1].snippet.contains("Noter kurası"))
    }

    @Test
    fun parsesBingHeadingsAndKeepsTheirOwnCaptions() {
        val html = """
            <li class="b_algo">
              <h2><a href="https://www.ogm.gov.tr/duyuru/3">Orman Genel Müdürlüğü Kura</a></h2>
              <div class="b_caption"><p>Elektrikçi alımı noter kurası 08.10.2026 saat 11:00.</p></div>
            </li>
            <li class="b_algo">
              <h2><a href="https://www.ant.gov.tr/haberler.html">ANT Personel Alımı</a></h2>
              <div class="b_caption"><p>Asıl ve yedek kura sonuçları yayımlandı.</p></div>
            </li>
        """.trimIndent()

        val results = Net.parseBingHtml(html, 8)

        assertEquals(2, results.size)
        assertEquals("https://www.ogm.gov.tr/duyuru/3", results[0].url)
        assertTrue(results[0].snippet.contains("08.10.2026"))
        assertTrue(results[1].snippet.contains("Asıl ve yedek"))
    }

    @Test
    fun identifiesTheTurkishRecruitmentProcessStages() {
        assertEquals(
            "KURA TARİHİ / KURAYA KATILACAKLAR",
            CareerRecruitmentTracker.detectStage(
                "Noter kurası duyurusu",
                "Elektrikçi kadrosu için kura çekimi 08.10.2026 tarihinde yapılacaktır."
            )
        )
        assertEquals(
            "NOTER KURASI SONUCU · ASIL / YEDEK",
            CareerRecruitmentTracker.detectStage(
                "Noter kurası sonuçları belli oldu",
                "Asıl ve yedek aday listesi yayımlandı."
            )
        )
        assertEquals(
            "EVRAK / BELGE KONTROLÜ",
            CareerRecruitmentTracker.detectStage(
                "Evrak kontrol duyurusu",
                "Belgelerin teslim tarihi ilan edildi."
            )
        )
        assertEquals(
            "SINAV / MÜLAKAT TARİHİ",
            CareerRecruitmentTracker.detectStage(
                "Sözlü sınav tarihi açıklandı",
                "Sınava girecek adaylar listesi."
            )
        )
        assertEquals(
            "SINAV SONUCU",
            CareerRecruitmentTracker.detectStage(
                "Mülakat sonucu",
                "Sınav sonucu duyurusu."
            )
        )
    }

    @Test
    fun recordsMultipleStagesFromOneAnnouncementWithoutConfusingPublicationDeadlineWithExamDate() {
        val title = "Geçici işçi alımı noter kurası sonuçları"
        val snippet = "Asıl ve yedek adaylar belirlendi. Sözlü sınavın yeri, tarihi ve saati 16.10.2026 tarihine kadar duyurulacaktır."

        val stages = CareerRecruitmentTracker.detectStages(title, snippet)
        assertTrue(stages.contains("NOTER KURASI SONUCU · ASIL / YEDEK"))
        assertTrue(stages.contains("SINAV / MÜLAKAT TARİHİ"))

        val notaryListStages = CareerRecruitmentTracker.detectStages(
            "Noter kurasına katılacak aday listesi açıklandı",
            "Kura çekilişi 08.10.2026 saat 11:00'da yapılacaktır."
        )
        assertTrue(notaryListStages.contains("KURA TARİHİ / KURAYA KATILACAKLAR"))
        val initialNoticeStages = CareerRecruitmentTracker.detectStages(
            "Karayolları Genel Müdürlüğü 50 işçi alımına ilişkin noter kurası duyurusu",
            "Noter huzurunda 15.10.2026 tarihinde saat 09:30'da gerçekleşecektir."
        )
        assertTrue(initialNoticeStages.contains("KURA TARİHİ / KURAYA KATILACAKLAR"))

        val notaryDate = CareerRecruitmentTracker.parseEventTime(
            "Noter kurasına katılacak aday listesi",
            "Noter kurasına katılacak adaylar 08.10.2026 saat 11:00'da belirlenecektir.",
            "KURA TARİHİ / KURAYA KATILACAKLAR"
        )
        assertNotNull(notaryDate)
        assertTrue(notaryDate!!.second)

        val examAnnouncementDeadline = CareerRecruitmentTracker.parseEventTime(
            title,
            snippet,
            "SINAV / MÜLAKAT TARİHİ"
        )
        assertEquals(null, examAnnouncementDeadline)

        val actualExamDate = CareerRecruitmentTracker.parseEventTime(
            "Sözlü sınav tarihi açıklandı",
            "Sözlü sınav 20.10.2026 saat 09:30'da yapılacaktır.",
            "SINAV / MÜLAKAT TARİHİ"
        )
        assertNotNull(actualExamDate)
        assertTrue(actualExamDate!!.second)
        val formatted = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR"))
            .format(java.util.Date(actualExamDate.first))
        assertEquals("20.10.2026 09:30", formatted)
    }

    @Test
    fun extractsOnlyExplicitKuraDateAndTimeForReminder() {
        val parsed = CareerRecruitmentTracker.parseEventTime(
            "Noter kurası tarihi açıklandı",
            "Kura çekimi 08.10.2026 Perşembe günü saat 11:00'de yapılacaktır."
        )

        assertNotNull(parsed)
        assertTrue(parsed!!.second)
        val formatted = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR")).format(java.util.Date(parsed.first))
        assertEquals("08.10.2026 11:00", formatted)

        val namedMonth = CareerRecruitmentTracker.parseEventTime(
            "Noter kurası tarihi",
            "Kura çekimi 23 Kasım 2026 Pazartesi saat 09:30'da yapılacaktır."
        )
        assertNotNull(namedMonth)
        val namedMonthFormatted = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR"))
            .format(java.util.Date(namedMonth!!.first))
        assertEquals("23.11.2026 09:30", namedMonthFormatted)

        val noDate = CareerRecruitmentTracker.parseEventTime(
            "Kura sonuçları yayımlandı",
            "Asıl ve yedek aday listesi erişime açılmıştır."
        )
        assertEquals(null, noDate)
    }

    @Test
    fun deduplicatesSameEmployerListingAcrossOfficialAndSecondarySearchLinks() {
        val secondary = Net.Hit(
            "ASELSAN Elektrik Elektronik Teknisyeni İşe Alım",
            "https://www.kariyer.net/is-ilani/aselsan-elektrik-elektronik-teknisyeni",
            "ASELSAN Ankara tesislerinde elektrik elektronik bakım teknisyeni için başvuru yapılabilir."
        )
        val official = Net.Hit(
            "Elektrik Elektronik Teknisyeni - ASELSAN",
            "https://akariyer.aselsan.com/tr/job/123",
            "ASELSAN Ankara tesislerinde elektrik elektronik bakım teknisyeni için başvuru yapılabilir."
        )

        val result = CareerSearchEngine.deduplicateHits(listOf(secondary, official))

        assertEquals(1, result.size)
        assertEquals(official.url, result.single().url)
    }

    @Test
    fun keepsDifferentEmployersEvenWhenTheRoleTitleIsSimilar() {
        val aselsan = Net.Hit(
            "ASELSAN Elektrik Elektronik Teknisyeni",
            "https://akariyer.aselsan.com/job/1",
            "ASELSAN elektrik elektronik teknisyeni başvurusu"
        )
        val borusan = Net.Hit(
            "BORUSAN Elektrik Elektronik Teknisyeni",
            "https://careers.borusan.com/job/2",
            "BORUSAN elektrik elektronik teknisyeni başvurusu"
        )

        assertEquals(2, CareerSearchEngine.deduplicateHits(listOf(aselsan, borusan)).size)
    }

    @Test
    fun simplifiesProviderErrorsForTheTrackingScreen() {
        val message = CareerSearchEngine.friendlyDiagnostics(
            "Sorgu 1: DuckDuckGo güvenlik kontrolü | Sorgu 2: Brave HTTP 429"
        )
        assertNotNull(message)
        assertTrue(message!!.contains("DuckDuckGo"))
        assertTrue(message.contains("Brave"))
        assertFalse(message.contains("HTTP 429"))
    }

    @Test
    fun acceptsMemur365AsNewsDiscoveryButNotAnOfficialJobAlertSource() {
        val hit = Net.Hit(
            "Jandarmaya 10 Bin Personel Alımı Geliyor",
            "https://memur365.com/kamu-personel-alimi/jandarma-10-bin-personel",
            "Başvuru tarihi bekleniyor; henüz resmî başvuru ilanı yayımlanmadı."
        )
        assertTrue(CareerSearchEngine.isEligibleSearchResult(hit))
        assertTrue(CareerSourceCatalog.isNewsDiscoverySource(hit.url))
        assertFalse(CareerSourceCatalog.isTrustedUrl(hit.url))
    }

}
