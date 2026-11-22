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
    fun explicitP94CutoffAboveCandidateScoreIsFlagged() {
        assertTrue(
            CareerEligibility.assess("Personel alımı", "KPSS P94 en az 70 puan şartı aranıyor.")
                .startsWith("PUAN ŞARTI UYUŞMUYOR")
        )
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
    fun matchesTheUsersHighSchoolFieldCodeButRejectsRailSystemsCode() {
        assertTrue(
            CareerEligibility.assessQualificationCodes(setOf(2023))
                .startsWith("ALAN KODU EŞLEŞİYOR")
        )
        assertTrue(
            CareerEligibility.assessQualificationCodes(setOf(2086))
                .startsWith("ALAN KODU UYUŞMUYOR")
        )
        assertTrue(
            CareerEligibility.assessQualificationCodes(setOf(2023, 2086))
                .startsWith("ALAN KODU UYUŞMUYOR")
        )
        assertTrue(
            CareerEligibility.assessQualificationCodes(setOf(2001))
                .startsWith("ORTAÖĞRETİM KODU EŞLEŞİYOR")
        )
        assertTrue(
            CareerEligibility.assess("TCDD Teknisyen", "KPSS P94 · Nitelik kodu: 2086")
                .startsWith("ALAN KODU UYUŞMUYOR")
        )
        assertTrue(
            CareerEligibility.assess("Elektrik Teknisyeni", "KPSS P94 · Nitelik kodu: 2023")
                .startsWith("ALAN KODU EŞLEŞİYOR")
        )
    }

    @Test
    fun extractsFourDigitQualificationCodesWithoutTreatingYearsAsTheOnlyMatch() {
        assertEquals(setOf(2001, 2023, 2086), CareerEligibility.extractQualificationCodes("Nitelik kodları: 2001, 2023, 2086, 2026"))
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
    fun detectsElectricalTradeRelevanceWithoutClaimingAcceptanceProbability() {
        val result = CareerEligibility.assess("Pano montaj teknisyeni", "Elektrik meslek lisesi mezunu.")
        assertTrue(result.startsWith("ALAN EŞLEŞMESİ VAR"))
        assertTrue(CareerEligibility.relevance("Elektrik teknisyeni", "Pano bakım personeli", "Malatya") >= 20)
    }
}
