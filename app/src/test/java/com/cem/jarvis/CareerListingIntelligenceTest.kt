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

    @Test
    fun doesNotInferKpssScoreTypeFromEducationLevelAlone() {
        val associate = CareerListingIntelligence.analyze(
            "Elektrik teknikeri",
            "Elektrik ön lisans mezunu olmak; KPSS puan türü ilanda ayrıca belirtilmelidir.",
            "https://careers.example.com/jobs/1"
        )
        assertTrue(associate.requirementSignals.any { it.startsWith("Öğrenim şartı: ön lisans") })
        assertFalse(associate.requirementSignals.any { it.contains("KPSS puan türü: P93") })

        val bachelor = CareerListingIntelligence.analyze(
            "Mühendis",
            "Lisans mezunu olmak ve deneyim şartı aranır.",
            "https://careers.example.com/jobs/2"
        )
        assertTrue(bachelor.requirementSignals.any { it.startsWith("Öğrenim şartı: lisans") })
        assertFalse(bachelor.requirementSignals.any { it.contains("KPSS puan türü: P3") })

        val secondary = CareerListingIntelligence.analyze(
            "Üretim personeli",
            "Lise ve dengi okul mezunu olmak.",
            "https://careers.example.com/jobs/3"
        )
        assertTrue(secondary.requirementSignals.any { it.startsWith("Öğrenim şartı: ortaöğretim/lise") })
        assertFalse(secondary.requirementSignals.any { it.contains("KPSS puan türü: P94") })
    }

    @Test
    fun showsKpssScoreTypeOnlyWhenTheResultTextNamesItExplicitly() {
        val p93 = CareerListingIntelligence.analyze(
            "Tekniker alımı",
            "Elektrik ön lisans mezunu olmak ve KPSS P93 puanına sahip olmak.",
            "https://kariyerkapisi.gov.tr/isealim"
        )
        assertTrue(p93.requirementSignals.any { it == "KPSS puan türü: P93 · ön lisans" })
        assertFalse(p93.requirementSignals.any { it.startsWith("Öğrenim şartı: ön lisans") })
    }


    @Test
    fun sendsAdaletInternshipApplicantsToTheActualUlusalStajRegistrationPortal() {
        val facts = CareerListingIntelligence.analyze(
            "Ulusal Staj Programı başvuruları",
            "Üniversite ve yüksekokul öğrencilerinin kayıtlarını 14.12.2026 tarihine kadar tamamlaması gerekmektedir.",
            CareerCadreRadar.ADALET_UNIVERSITY_INTERNSHIP_URL
        )
        assertEquals(
            "Adalet Bakanlığı duyurusu · kayıt Ulusal Staj Programı portalından",
            facts.applicationChannel
        )
        assertEquals("14.12.2026", facts.explicitDeadline)
    }


    @Test
    fun recognizesKpssScoreTypeWrittenWithHyphensOrSlashes() {
        val p94 = CareerListingIntelligence.analyze(
            "Ortaöğretim personel alımı",
            "KPSS-P94 puanı ile tercih yapılacaktır.",
            "https://www.osym.gov.tr/"
        )
        assertTrue(p94.requirementSignals.any { it == "KPSS puan türü: P94 · ortaöğretim" })

        val p93 = CareerListingIntelligence.analyze(
            "Elektrik teknikeri",
            "Elektrik ön lisans mezunu ve KPSS/P93 puan türü aranıyor.",
            "https://kariyerkapisi.gov.tr/isealim"
        )
        assertTrue(p93.requirementSignals.any { it == "KPSS puan türü: P93 · ön lisans" })
    }


    @Test
    fun labelsStudentInternshipsAsProgrammesNotPublicVacancies() {
        val facts = CareerListingIntelligence.analyze(
            "Ulusal Staj Programı başvuruları",
            "Üniversite ve yüksekokul öğrencilerinin kayıtlarını 14.12.2026 tarihine kadar tamamlaması gerekmektedir.",
            CareerCadreRadar.ADALET_UNIVERSITY_INTERNSHIP_URL
        )
        assertEquals("Öğrenci / staj / gençlik programı", facts.employerGroup)
        assertEquals("Öğrenci / staj / gençlik programı", facts.noticeType)
        assertTrue(facts.applicationStatus.startsWith("SON TARİH BULUNDU"))
        assertTrue(CareerEligibility.assess(
            "Ulusal Staj Programı",
            "Elektrik öğrencisi staj başvurusu",
            CareerCadreRadar.ADALET_UNIVERSITY_INTERNSHIP_URL,
            false
        ).startsWith("ÖĞRENCİ / STAJ PROGRAMI"))
    }


    @Test
    fun routesDistinctIskurSubportalsToTheirActualServicePaths() {
        val nationalInternship = CareerListingIntelligence.analyze(
            "Ulusal Staj Programı",
            "Üniversite öğrencileri için kayıt işlemleri.",
            "https://ulusalstajprogrami.iskur.gov.tr/"
        )
        assertEquals("Ulusal Staj Programı portalı · doğrudan kayıt", nationalInternship.applicationChannel)

        val guc = CareerListingIntelligence.analyze(
            "Geleceğim Meslekte",
            "Meslek yüksekokulu son sınıf öğrencilerine kariyer danışmanlığı.",
            "https://guc.iskur.gov.tr/"
        )
        assertEquals(
            "İŞKUR GÜÇ · program rehberi; başvuru ilgili program portalından",
            guc.applicationChannel
        )

        val staj = CareerListingIntelligence.analyze(
            "Staj başvurusu",
            "Öğrenci staj kaydı.",
            "https://staj.iskur.gov.tr/"
        )
        assertEquals("İŞKUR Staj Platformu · staj başvuru portalı", staj.applicationChannel)

        val highSchoolInternship = CareerListingIntelligence.analyze(
            "Lise staj başvurusu",
            "Lise öğrencileri için staj portalı.",
            "https://stajportali.iskur.gov.tr/"
        )
        assertEquals("İŞKUR Lise Staj Portalı · ilgili staj başvuru ekranı", highSchoolInternship.applicationChannel)

        val esube = CareerListingIntelligence.analyze(
            "Açık iş ilanı",
            "İŞKUR e-Şube üzerinden başvuru.",
            "https://esube.iskur.gov.tr/"
        )
        assertEquals("İŞKUR e-Şube · açık iş / program başvurusu", esube.applicationChannel)

        val otherIskurPortal = CareerListingIntelligence.analyze(
            "Program duyurusu",
            "İlgili programı inceleyin.",
            "https://engelsiz.iskur.gov.tr/"
        )
        assertTrue(otherIskurPortal.applicationChannel.startsWith("İŞKUR alt portalı"))
    }


    @Test
    fun labelsOldUndatedRecruitmentPostsAsStaleRatherThanOpen() {
        val facts = CareerListingIntelligence.analyze(
            "Elektrik teknisyeni personel alımı",
            "İlan tarihi: 01.01.2026",
            "https://www.dsi.gov.tr/duyurular",
            date("23.11.2026")
        )
        assertTrue(facts.applicationStatus.startsWith("ESKİ KAYIT"))
        assertFalse(facts.applicationStatus.contains("BAŞVURU AÇIK"))
    }

    @Test
    fun pointsJandarmaApplicantsToItsPersonnelRecruitmentPortal() {
        val facts = CareerListingIntelligence.analyze(
            "Uzman erbaş alımı",
            "Başvurular başladı. Son başvuru tarihi: 07.12.2026",
            "https://personeltemin.jandarma.gov.tr/"
        )
        assertEquals(
            "Jandarma Personel Temin Sistemi · kurumun kendi personel başvuru portalı",
            facts.applicationChannel
        )
    }

}
