package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerContentClassifierTest {
    @Test
    fun separatesKgmLotteryAnnouncementFromAnOpenWorkerVacancy() {
        val kura = CareerContentClassifier.classify(
            "Karayolları Genel Müdürlüğü 175 sürekli işçi alımı noter kurası duyurusu",
            "29 Aralık 2025 – 2 Ocak 2026 başvurularının ardından kura duyurusu yayımlandı.",
            "https://www.kgm.gov.tr/duyurular"
        )
        assertEquals(CareerContentType.PROCESS_NOTICE, kura.type)
        assertFalse(kura.isJobListing)

        val vacancy = CareerContentClassifier.classify(
            "Karayolları Genel Müdürlüğü elektrik teknisyeni işçi alımı",
            "Sürekli işçi pozisyonları için başvurular başladı. Başvuru koşulları ve son tarih ilanda yer alıyor.",
            "https://www.kgm.gov.tr/duyurular/elektrik-teknisyeni"
        )
        assertEquals(CareerContentType.JOB_LISTING, vacancy.type)
        assertTrue(vacancy.isJobListing)
    }

    @Test
    fun putsKuraExamAndFinalResultNoticesOutsideTheJobListingFeed() {
        val examples = listOf(
            "Noter kurası sonuçları açıklandı" to "Asıl ve yedek aday listesi yayımlandı.",
            "Sözlü sınav sonuçları" to "Nihai liste ve evrak teslim süreci.",
            "Teknisyen alımı yerleştirme sonucu" to "Başvuru sonuçları açıklandı."
        )
        examples.forEach { (title, snippet) ->
            val result = CareerContentClassifier.classify(title, snippet, "https://www.gov.tr/duyuru")
            assertEquals(title, CareerContentType.PROCESS_NOTICE, result.type)
            assertFalse(result.isJobListing)
        }
    }

    @Test
    fun doesNotTreatGenericInstitutionNewsAsAJob() {
        val result = CareerContentClassifier.classify(
            "Karayolları Genel Müdürlüğü yeni projelerini duyurdu",
            "Kurum tarafından basın açıklaması ve bilgilendirme yayımlandı.",
            "https://www.kgm.gov.tr/haberler"
        )
        assertEquals(CareerContentType.INSTITUTION_NOTICE, result.type)
        assertFalse(result.isJobListing)
    }

    @Test
    fun treatsStudentProgramsAsProgramsNotVacancies() {
        val result = CareerContentClassifier.classify(
            "Ulusal Staj Programı başvuruları",
            "Üniversite öğrencileri için staj ve gençlik programı.",
            "https://ulusalstajprogrami.iskur.gov.tr/"
        )
        assertEquals(CareerContentType.STUDENT_PROGRAM, result.type)
        assertFalse(result.isJobListing)
    }

    @Test
    fun refusesToGuessWhenOnlyACompanyOrInstitutionNameIsMentioned() {
        val result = CareerContentClassifier.classify(
            "Elektrik alanında güncel gelişmeler",
            "Karayolları Genel Müdürlüğü hakkında haber ve bilgilendirme.",
            "https://www.kgm.gov.tr/haberler"
        )
        assertEquals(CareerContentType.INSTITUTION_NOTICE, result.type)
        assertFalse(result.isJobListing)

        val ambiguous = CareerContentClassifier.classify(
            "Teknik personel bilgisi",
            "Yeni düzenlemenin teknik ayrıntıları.",
            "https://example.org/"
        )
        assertEquals(CareerContentType.REVIEW_REQUIRED, ambiguous.type)
    }
}
