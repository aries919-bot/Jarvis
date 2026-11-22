package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerMonitorPolicyTest {
    @Test
    fun rejectsP3AndP93ForSecondaryEducationP94Candidate() {
        assertTrue(CareerEligibility.assess("Memur alımı", "KPSS P3 puanı ile başvuru alınacaktır.").startsWith("PUAN TÜRÜ UYUŞMUYOR"))
        assertTrue(CareerEligibility.assess("Teknisyen alımı", "KPSS P93 puan türü aranır.").startsWith("PUAN TÜRÜ UYUŞMUYOR"))
    }

    @Test
    fun centralPlacementP94HasNoInventedUniversalCutoff() {
        val result = CareerEligibility.assess(
            "KPSS-2026/2 merkezi yerleştirme",
            "Ortaöğretim KPSS P94 puanı ile tercih yapılır; genel bir asgari puan belirtilmemiştir."
        )
        assertTrue(result.startsWith("P94 İLE TERCİH DEĞERLENDİRMESİ"))
        assertFalse(result.startsWith("PUAN ŞARTI UYUŞMUYOR"))
    }

    @Test
    fun p94CutoffIsNotJudgedWithoutAUserConfirmedScore() {
        val result = CareerEligibility.assess("Personel alımı", "KPSS P94 en az 70 puan şartı aranıyor.")
        assertTrue(result.startsWith("P94 PUAN ŞARTI VAR"))
        assertTrue(result.contains("puanı girilmediği"))
        assertFalse(result.startsWith("PUAN ŞARTI UYUŞMUYOR"))
    }

    @Test
    fun separatesAssociateTechnicianRolesOnlyWhenGraduationIsExplicitlyRequired() {
        assertTrue(
            CareerEligibility.assess("Elektrik Teknikeri", "Elektrik ön lisans mezunu olmak şarttır.")
                .startsWith("MEZUNİYET SONRASI")
        )
        assertFalse(
            CareerEligibility.assess("Elektrik Teknikeri", "Elektrik teknisyeni aranıyor.")
                .startsWith("MEZUNİYET SONRASI")
        )
    }

    @Test
    fun qualificationCodesAreNotAutoMatchedWithoutConfirmedDiplomaField() {
        assertTrue(CareerEligibility.assessQualificationCodes(setOf(2023)).startsWith("NİTELİK KODU KONTROLÜ"))
        assertTrue(CareerEligibility.assessQualificationCodes(setOf(2086)).startsWith("NİTELİK KODU KONTROLÜ"))
        assertTrue(CareerEligibility.assessQualificationCodes(setOf(2023, 2086)).startsWith("NİTELİK KODU KONTROLÜ"))
        assertTrue(CareerEligibility.assessQualificationCodes(setOf(2001)).startsWith("NİTELİK KODU KONTROLÜ"))
        assertTrue(
            CareerEligibility.assess("TCDD Teknisyen", "KPSS P94 · Nitelik kodu: 2086")
                .startsWith("P94 İLE TERCİH DEĞERLENDİRMESİ")
        )
        val p94CodeReview = CareerEligibility.assess(
            "Elektrik Teknisyeni",
            "KPSS P94 · Nitelik kodu: 2023"
        )
        assertTrue(p94CodeReview.startsWith("P94 İLE TERCİH DEĞERLENDİRMESİ"))
        assertTrue(p94CodeReview.contains("NİTELİK KODU KONTROLÜ"))

        val unknownScoreAndCode = CareerEligibility.assess(
            "Elektrik Teknisyeni",
            "KPSS P94 en az 70 puan · Nitelik kodu: 2023"
        )
        assertTrue(unknownScoreAndCode.startsWith("P94 PUAN ŞARTI VAR"))
        assertTrue(unknownScoreAndCode.contains("NİTELİK KODU KONTROLÜ"))

        val wrongPuanType = CareerEligibility.assess(
            "Elektrik Teknikeri",
            "KPSS P93 · Nitelik kodu: 2023"
        )
        assertTrue(wrongPuanType.startsWith("PUAN TÜRÜ UYUŞMUYOR"))
    }

    @Test
    fun extractsFourDigitQualificationCodesWithoutTreatingYearsAsTheOnlyMatch() {
        assertEquals(setOf(2001, 2023, 2086), CareerEligibility.extractQualificationCodes("KPSS-2026/2 · Nitelik kodları: 2001, 2023, 2086"))
    }

    @Test
    fun normalizesTrackingParametersWhenDeduplicatingTheSameJob() {
        val first = CareerSearchEngine.fingerprint(
            "Elektrik Teknisyeni",
            "https://careers.example/ilan?id=7&utm_source=mail"
        )
        val second = CareerSearchEngine.fingerprint(
            "Elektrik teknisyeni",
            "https://careers.example/ilan?id=7"
        )
        assertEquals(first, second)
    }

    @Test
    fun parsesOnlyExplicitDeadlineTextAndMarksExpiredDates() {
        assertEquals(
            "17.12.2026",
            CareerListingParser.deadlineDate("Başvuruların son günü: 17.12.2026")
        )
        assertEquals(
            null,
            CareerListingParser.deadlineDate("İlan tarihi: 17.12.2026")
        )
        val expired = CareerListingParser.deadlineStatus(
            "Son başvuru tarihi: 01.01.2020",
            java.text.SimpleDateFormat("dd.MM.yyyy").parse("22.11.2026")!!.time
        )
        assertTrue(expired!!.startsWith("SÜRESİ GEÇMİŞ"))
        assertEquals(
            "23.11.2026",
            CareerListingParser.deadlineDate("Son başvuru tarihi: 23 Kasım 2026")
        )
        val future = CareerListingParser.deadlineStatus(
            "Son müracaat tarihi: 23 Kasım 2026",
            java.text.SimpleDateFormat("dd.MM.yyyy").parse("22.11.2026")!!.time
        )
        assertTrue(future!!.startsWith("SON TARİH BİLGİSİ"))
    }

    @Test
    fun ranksRelevantLocalElectricalRolesAboveUnrelatedJobs() {
        val strong = CareerEligibility.profileMatchScore(
            "Elektrik bakım teknisyeni",
            "Malatya'da pano ve üretim bakım personeli aranıyor.",
            "elektrik teknisyeni, pano montajı, bakım-onarım, üretim",
            "Malatya, Battalgazi, Yeşilyurt",
            "ALAN EŞLEŞMESİ VAR"
        )
        val weak = CareerEligibility.profileMatchScore(
            "Mağaza satış danışmanı",
            "İstanbul'da mağaza personeli aranıyor.",
            "elektrik teknisyeni, pano montajı, bakım-onarım, üretim",
            "Malatya, Battalgazi, Yeşilyurt",
            "ŞARTLARI DOĞRULA"
        )
        assertTrue(strong > weak)
    }

    @Test
    fun hardEligibilityMismatchReducesProfileMatchScore() {
        val eligible = CareerEligibility.profileMatchScore(
            "Elektrik bakım teknisyeni",
            "Malatya elektrik pano bakım işi",
            "elektrik teknisyeni pano",
            "Malatya",
            "ALAN EŞLEŞMESİ VAR"
        )
        val mismatch = CareerEligibility.profileMatchScore(
            "Elektrik bakım teknisyeni",
            "Malatya elektrik pano bakım işi",
            "elektrik teknisyeni pano",
            "Malatya",
            "PUAN TÜRÜ UYUŞMUYOR"
        )
        assertTrue(eligible > mismatch)
    }

    @Test
    fun detectsElectricalTradeRelevanceWithoutClaimingDiplomaEligibility() {
        val result = CareerEligibility.assess("Pano montaj teknisyeni", "Elektrik meslek lisesi mezunu.")
        assertTrue(result.startsWith("TEKNİK ALAN İFADESİ VAR"))
        assertFalse(result.startsWith("ALAN EŞLEŞMESİ VAR"))
        assertTrue(CareerEligibility.relevance("Elektrik teknisyeni", "Pano bakım personeli", "Malatya") >= 20)
    }
}
