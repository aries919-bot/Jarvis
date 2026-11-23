package com.cem.jarvis

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerListingSchemaTest {
    private val url = "https://personel.example.gov.tr/ilan/elektrik-teknisyeni"
    private val page = """
        Devlet Su İşleri Genel Müdürlüğü
        Kurum: Devlet Su İşleri Genel Müdürlüğü.
        Pozisyon: Elektrik Teknisyeni.
        Görev yeri: Malatya.
        KPSS P94 puan türü ile başvuru yapılacaktır.
        Nitelik kodu: 2023.
        Toplam 5 kişi alınacaktır.
        Şart: Elektrik meslek lisesi mezunu olmak.
        Son başvuru tarihi: 30.11.2026.
    """.trimIndent()

    private fun field(value: String, evidence: String) = JSONObject()
        .put("value", value)
        .put("evidence", evidence)

    private fun validAiJson(): String = JSONObject()
        .put("employer", field("Devlet Su İşleri Genel Müdürlüğü", "Kurum: Devlet Su İşleri Genel Müdürlüğü."))
        .put("position", field("Elektrik Teknisyeni", "Pozisyon: Elektrik Teknisyeni."))
        .put("province", field("Malatya", "Görev yeri: Malatya."))
        .put("recruitmentMethod", field("KPSS", "KPSS P94 puan türü ile başvuru yapılacaktır."))
        .put("kpssScoreType", field("P94", "KPSS P94 puan türü ile başvuru yapılacaktır."))
        .put("qualificationCodes", field("2023", "Nitelik kodu: 2023."))
        .put("quota", field("5", "Toplam 5 kişi alınacaktır."))
        .put("conditions", field("Elektrik meslek lisesi mezunu olmak", "Şart: Elektrik meslek lisesi mezunu olmak."))
        .put("deadline", field("30.11.2026", "Son başvuru tarihi: 30.11.2026."))
        .toString()

    @Test
    fun acceptsFieldsOnlyWhenTheirEvidenceExistsOnTheSameSourcePage() {
        val schema = CareerListingSchemaParser.parse(validAiJson(), page, url, 1234L, false)
        assertEquals("Devlet Su İşleri Genel Müdürlüğü", schema.employer.value)
        assertEquals("Elektrik Teknisyeni", schema.position.value)
        assertEquals("Malatya", schema.province.value)
        assertEquals("P94", schema.kpssScoreType.value)
        assertEquals("2023", schema.qualificationCodes.value)
        assertEquals("5", schema.quota.value)
        assertEquals("30.11.2026", schema.deadline.value)
        assertTrue(schema.fields().all { it.second.status == "ALINTIYLA EŞLEŞTİ" })
        assertTrue(schema.fields().all { it.second.provenance.contains("AI alan çıkarımı") })
        assertEquals(url, schema.sourceUrl)
        assertEquals(1234L, schema.fetchedAtEpochMillis)
    }

    @Test
    fun rejectsInventedEmployerAndDeadlineFromPublicationDate() {
        val tampered = JSONObject(validAiJson())
            .put("employer", field("ASELSAN", "Kurum: Devlet Su İşleri Genel Müdürlüğü."))
            .put("deadline", field("30.11.2026", "Duyuru tarihi: 30.11.2026."))
        val schema = CareerListingSchemaParser.parse(tampered.toString(), page + "\nDuyuru tarihi: 30.11.2026.", url, 10L, false)
        assertEquals("", schema.employer.value)
        assertEquals("DOĞRULANAMADI · KONTROL ET", schema.employer.status)
        assertEquals("", schema.deadline.value)
        assertEquals("DOĞRULANAMADI · KONTROL ET", schema.deadline.status)
    }

    @Test
    fun requiresKpssPrefixAndExplicitQualificationCodeLabel() {
        val tampered = JSONObject(validAiJson())
            .put("kpssScoreType", field("P94", "P94 puanı ile tercih yapılacaktır."))
            .put("qualificationCodes", field("2023", "2023 yılı mezunu olmak."))
        val schema = CareerListingSchemaParser.parse(tampered.toString(), page, url, 10L, false)
        assertEquals("", schema.kpssScoreType.value)
        assertEquals("", schema.qualificationCodes.value)
    }

    @Test
    fun truncatedPageNeverMarksFieldsAsVerified() {
        val schema = CareerListingSchemaParser.parse(validAiJson(), page, url, 10L, true)
        assertTrue(schema.fields().filter { it.second.value.isNotBlank() }
            .all { it.second.status == "KONTROL ET · METİN KISALDI" })
        assertTrue(schema.sourceTextTruncated)
    }

    @Test(expected = Exception::class)
    fun malformedAiJsonFailsClosed() {
        CareerListingSchemaParser.parse("{not-json}", page, url, 10L, false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsHttpOrUnsafeSourceUrls() {
        CareerListingSchemaParser.parse(validAiJson(), page, "http://example.gov.tr/ilan", 10L, false)
    }

    @Test
    fun pagePolicyRejectsHttpErrorsCaptchasLoginAndEmptyPages() {
        assertFalse(CareerListingPagePolicy.inspect(403, "", "").ready)
        assertTrue(CareerListingPagePolicy.inspect(403, "", "").message.contains("aşılmayacak"))
        assertFalse(CareerListingPagePolicy.inspect(200, "Checking your browser", "").ready)
        assertFalse(CareerListingPagePolicy.inspect(200, "Giriş yaparak devam edin", "").ready)
        assertFalse(CareerListingPagePolicy.inspect(200, "", "  ").ready)
    }

    @Test
    fun pagePolicyDoesNotSendCandidateResultListsToAi() {
        val checked = CareerListingPagePolicy.inspect(
            200,
            "Kura sonuçları",
            "Asıl ve yedek aday listesi yayımlandı."
        )
        assertFalse(checked.ready)
        assertTrue(checked.message.contains("Başka adayların verisi"))
    }

    @Test
    fun masksContactAndIdentityDataBeforeAiExtraction() {
        val raw = "İletişim: aday@example.org, 0505 111 22 33, 12345678901, TR120006200000000000123456"
        val safe = CareerListingPagePolicy.sanitizeForAi(raw)
        assertFalse(safe.contains("aday@example.org"))
        assertFalse(safe.contains("0505 111 22 33"))
        assertFalse(safe.contains("12345678901"))
        assertFalse(safe.contains("TR120006200000000000123456"))
        assertTrue(safe.contains("[E-POSTA]"))
        assertTrue(safe.contains("[TELEFON]"))
        assertTrue(safe.contains("[KIMLIK NUMARASI]"))
        assertTrue(safe.contains("[IBAN]"))
    }

    @Test
    fun pagePolicyAcceptsHttpsAndCapsOversizedPageText() {
        assertEquals(null, CareerListingPagePolicy.validateHttpsUrl(url))
        assertTrue(CareerListingPagePolicy.validateHttpsUrl("http://example.gov.tr/ilan") != null)
        assertTrue(CareerListingPagePolicy.validateHttpsUrl("https://user:pass@example.gov.tr/ilan") != null)
        val longText = "A".repeat(CareerListingPagePolicy.MAX_PAGE_TEXT_CHARS + 5)
        val checked = CareerListingPagePolicy.inspect(200, "İlan", longText)
        assertTrue(checked.ready)
        assertTrue(checked.truncated)
        assertEquals(CareerListingPagePolicy.MAX_PAGE_TEXT_CHARS, checked.text.length)
    }
}
