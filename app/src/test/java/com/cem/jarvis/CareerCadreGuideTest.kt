package com.cem.jarvis

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerCadreGuideTest {
    @Test
    fun extractsTheFullRangeOfQualificationCodesWithoutTreatingSpecialConditionsAsFieldCodes() {
        val codes = CareerEligibility.extractQualificationCodes(
            "Aranan nitelik kodları: 2001, 2023, 2061, 2065, 2086, 2111, 2119. Özel koşul: 7324."
        )

        assertTrue(codes.containsAll(setOf(2001, 2023, 2061, 2065, 2086, 2111, 2119)))
        assertFalse(codes.contains(7324))
    }

    @Test
    fun explainsElectricalAndRailwayRequirementsSeparately() {
        assertTrue(CareerCadreGuide.qualificationCodeExplanation(2023).orEmpty().contains("Elektrik-Elektronik"))
        assertTrue(CareerCadreGuide.qualificationCodeExplanation(2086).orEmpty().contains("Raylı Sistemler"))
        assertFalse(
            CareerCadreGuide.qualificationCodeExplanation(2086).orEmpty().contains("Herhangi bir ortaöğretim")
        )
    }

    @Test
    fun displaysUnknownQualificationCodesAsNeedsOfficialVerification() {
        val explanation = CareerCadreGuide.explainQualificationCodes(setOf(2077))
        assertTrue(explanation.contains("2077"))
        assertTrue(explanation.contains("güncel kılavuzdaki açıklamasını"))
    }

    @Test
    fun comparesAnExplicitP94MinimumAgainstTheCandidateDeclaredScore() {
        val meets = CareerEligibility.assess(
            "Kamu personel alımı",
            "KPSS ortaöğretim P94 puan türünde en az 70 puan şartı",
            p94Score = 72.5
        )
        val below = CareerEligibility.assess(
            "Kamu personel alımı",
            "KPSS ortaöğretim P94 puan türünde en az 70 puan şartı",
            p94Score = 64.0
        )

        assertTrue(meets.startsWith("P94 PUAN ŞARTI KARŞILANIYOR"))
        assertTrue(below.startsWith("P94 PUAN ŞARTI KARŞILANMIYOR"))
    }

    @Test
    fun historicalCadreSamplesAreNotTreatedAsAnUpcomingVacancyList() {
        val names = CareerCadreGuide.historicalP94Cadres.map { it.title }

        assertTrue(names.any { it.contains("Teknisyen") })
        assertTrue(names.any { it.contains("Destek personeli") })
        assertTrue(names.any { it.contains("Vagon teknisyeni") })
        assertTrue(CareerCadreGuide.KPSS_2026_2_START.isNotBlank())
        assertTrue(CareerCadreGuide.KPSS_2026_2_END.isNotBlank())
    }
}
