package com.cem.jarvis

import org.junit.Assert.assertTrue
import org.junit.Test

class CareerMonitorPolicyTest {
    @Test
    fun rejectsP3AndP93ForSecondaryEducationP94Candidate() {
        assertTrue(CareerEligibility.assess("Memur alımı", "KPSS P3 puanı ile başvuru alınacaktır.").startsWith("PUAN TÜRÜ UYUŞMUYOR"))
        assertTrue(CareerEligibility.assess("Teknisyen alımı", "KPSS P93 puan türü aranır.").startsWith("PUAN TÜRÜ UYUŞMUYOR"))
    }

    @Test
    fun flagsMinimumP94ScoreAboveCandidateScore() {
        assertTrue(CareerEligibility.assess("Personel alımı", "KPSS P94 en az 70 puan şartı aranıyor.").startsWith("PUAN ŞARTI UYUŞMUYOR"))
    }

    @Test
    fun separatesAssociateTechnicianRolesUntilGraduation() {
        assertTrue(CareerEligibility.assess("Elektrik Teknikeri", "Elektrik ön lisans mezunu olmak şarttır.").startsWith("MEZUNİYET SONRASI"))
    }

    @Test
    fun detectsElectricalTradeRelevanceWithoutClaimingAcceptanceProbability() {
        val result = CareerEligibility.assess("Pano montaj teknisyeni", "Elektrik meslek lisesi mezunu.")
        assertTrue(result.startsWith("ALAN EŞLEŞMESİ VAR"))
        assertTrue(CareerEligibility.relevance("Elektrik teknisyeni", "Pano bakım personeli", "Malatya") >= 20)
    }
}
