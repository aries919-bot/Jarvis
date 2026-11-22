package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class CareerListingIntelligenceTest {
    private fun date(value: String): Long =
        SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR")).parse(value)!!.time

    @Test
    fun doesNotClaimThatASearchResultIsAnOpenApplication() {
        val facts = CareerListingIntelligence.analyze(
            "Elektrik teknisyeni personel alımı",
            "Açık pozisyon ve başvuru duyurusu",
            "https://www.dsi.gov.tr/duyurular",
            date("23.11.2026")
        )
        assertTrue(facts.applicationStatus.contains("BİLİNMİYOR"))
        assertFalse(facts.applicationStatus.contains("BAŞVURU AÇIK"))
        assertEquals("Kurumun kendi duyurusu · başvuru yöntemi ilanda belirtilir", facts.applicationChannel)
    }

    @Test
    fun onlyParsesADeadlineWhenItIsExplicitlyLabelledAsTheLastApplicationDate() {
        val labelled = CareerListingIntelligence.analyze(
            "Elektrik teknisyeni alımı",
            "Son başvuru tarihi: 30.11.2026",
            "https://kariyerkapisi.gov.tr/isealim",
            date("23.11.2026")
        )
        assertEquals("30.11.2026", labelled.explicitDeadline)
        assertTrue(labelled.applicationStatus.contains("Başvurunun hâlâ açık olduğu"))
        val titleDeadline = CareerListingIntelligence.analyze(
            "Teknisyen alımı · Son başvuru tarihi: 30.11.2026",
            "Elektrik teknisyeni ve bakım personeli",
            "https://kariyerkapisi.gov.tr/isealim",
            date("23.11.2026")
        )
        assertEquals("30.11.2026", titleDeadline.explicitDeadline)
        val publicationDateOnly = CareerListingIntelligence.analyze(
            "Elektrik teknisyeni alımı",
            "Duyuru tarihi: 20.11.2026",
            "https://www.dsi.gov.tr/duyurular",
            date("23.11.2026")
        )
        assertNull(publicationDateOnly.explicitDeadline)
    }

    @Test
    fun detectsResultNoticesAsProcessUpdatesRatherThanNewApplications() {
        val facts = CareerListingIntelligence.analyze(
            "Noter kurası sonuçları açıklandı",
            "Asıl ve yedek aday listesi yayımlandı.",
            "https://www.ogm.gov.tr/duyurular",
            date("23.11.2026")
        )
        assertEquals("İşe alım süreci / sonuç duyurusu", facts.noticeType)
        assertTrue(facts.applicationStatus.startsWith("SÜREÇ DUYURUSU"))
    }

    @Test
    fun detectsApplicationChannelForTheMainOfficialPortals() {
        assertTrue(
            CareerListingIntelligence.analyze("KPSS tercih", "P94", "https://dokuman.osym.gov.tr/tablo.pdf")
                .applicationChannel.startsWith("ÖSYM")
        )
        assertTrue(
            CareerListingIntelligence.analyze("Personel alımı", "Başvuru", "https://kariyerkapisi.gov.tr/isealim")
                .applicationChannel.startsWith("Kariyer Kapısı")
        )
        assertTrue(
            CareerListingIntelligence.analyze("Açık iş", "İlan", "https://engelsiz.iskur.gov.tr/faaliyetlerimiz")
                .applicationChannel.startsWith("İŞKUR")
        )
        assertTrue(
            CareerListingIntelligence.analyze("Açık iş", "İlan", "https://www.turkiye.gov.tr/kriterlere-gore-acik-is-sorgulama")
                .applicationChannel.startsWith("e-Devlet")
        )
    }

    @Test
    fun doesNotConflateDefenceIndustryAndKitEmployerPortalsWithCentralCivilServicePlacement() {
        val defence = CareerListingIntelligence.analyze(
            "Elektrik-elektronik bakım teknisyeni",
            "Açık pozisyon",
            "https://akariyer.aselsan.com/"
        )
        assertEquals("Savunma sanayii / şirket alımı", defence.employerGroup)
        assertTrue(defence.applicationChannel.startsWith("İşverenin kendi"))

        val kit = CareerListingIntelligence.analyze(
            "Teknisyen alımı",
            "Başvuru duyurusu",
            "https://www.tcdd.gov.tr/duyurular"
        )
        assertEquals("KİT / kamu şirketi alımı · usul doğrulanmalı", kit.employerGroup)
        assertTrue(kit.applicationChannel.startsWith("İşverenin kendi"))
    }

    @Test
    fun extractsExplicitPositionCountsWithoutTreatingYearsOrScoresAsVacancies() {
        assertEquals("7.615", CareerListingIntelligence.extractQuota("Toplam 7.615 personel alımı yapılacaktır."))
        assertEquals("32", CareerListingIntelligence.extractQuota("32 kişilik teknisyen kadrosu"))
        assertEquals("14", CareerListingIntelligence.extractQuota("14 kontenjan için başvuru"))
        assertNull(CareerListingIntelligence.extractQuota("KPSS 2026/2 duyurusu, sınav tarihi 25.11.2026, puan 78,5"))
        assertNull(CareerListingIntelligence.extractQuota("Toplam 0 personel"))
    }

    @Test
    fun surfacesKnownQualificationCodeSignalsWithoutCallingThemAnEligibilityDecision() {
        val codes = listOf("2023", "3290", "2001", "2086")
        val facts = CareerListingIntelligence.analyze(
            "Teknisyen / teknik personel alımı",
            "Nitelik kodları: ${codes.joinToString(", ")}",
            "https://www.dsi.gov.tr/duyurular"
        )
        codes.forEach { code ->
            assertTrue(
                "Nitelik kodu $code ayrı sinyal olarak gösterilmeli",
                facts.requirementSignals.any { it.contains("Nitelik kodu $code") }
            )
        }
        assertTrue(facts.applicationStatus.contains("BİLİNMİYOR"))
    }

    @Test
    fun surfacesEducationAndKpssSignalsWithoutDeclaringEligibility() {
        val facts = CareerListingIntelligence.analyze(
            "Teknisyen alımı",
            "KPSS P94, ortaöğretim mezunu, elektrik alanı ve 2023 nitelik kodu",
            "https://www.dsi.gov.tr/duyurular"
        )
        assertTrue(facts.requirementSignals.any { it.contains("P94") })
        assertTrue(facts.requirementSignals.any { it.contains("Elektrik") })
        assertTrue(facts.applicationStatus.contains("BİLİNMİYOR"))
    }
}
