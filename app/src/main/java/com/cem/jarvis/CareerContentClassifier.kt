package com.cem.jarvis

import java.net.URI
import java.util.Locale

enum class CareerContentType {
    JOB_LISTING,
    PROCESS_NOTICE,
    STUDENT_PROGRAM,
    INSTITUTION_NOTICE,
    NEWS,
    REVIEW_REQUIRED
}

data class CareerContentClassification(
    val type: CareerContentType,
    val label: String,
    val reason: String,
    val confidence: String
) {
    val isJobListing: Boolean get() = type == CareerContentType.JOB_LISTING
}

object CareerContentClassifier {
    private fun normalize(value: String): String = value.lowercase(Locale("tr", "TR"))
        .replace('ı', 'i').replace('ş', 's').replace('ğ', 'g')
        .replace('ü', 'u').replace('ö', 'o').replace('ç', 'c')
        .replace(Regex("[^a-z0-9/._-]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()

    private val processPhrases = listOf(
        "noter kurasi", "kura cekimi", "kura cekilisi", "kura sonucu",
        "kura sonuclari", "kura tarihi", "kuraya katilacak",
        "asil ve yedek", "asil aday", "yedek aday", "kurada asil",
        "sinav sonucu", "sinav sonuclari", "mulakat sonucu",
        "sozlu sinav", "nihai liste", "evrak teslim", "evrak kontrol",
        "yerlestirme sonucu", "yerlestirme sonuclari", "basvuru sonucu",
        "basvuru sonuclari", "atamaya esas", "degerlendirme sonucu",
        "sonuc listesi", "aday listesi", "sinava girmeye hak kazanan"
    )

    private val vacancyPattern = Regex(
        """\b(?:personel|surekli isci|isci|memur|teknisyen|tekniker|muhendis|usta|operator|sofor|guvenlik gorevlisi|destek personeli)\s+(?:alim[i]?|ilan[i]?|istihdam|alacak|alimi)\b"""
    )

    fun classify(title: String, snippet: String, url: String): CareerContentClassification {
        val text = normalize("$title $snippet")
        val stage = CareerRecruitmentTracker.detectStage(title, snippet)
        val studentProgram = CareerSourceCatalog.isStudentProgram(title, snippet, url)

        if (studentProgram) {
            return CareerContentClassification(
                CareerContentType.STUDENT_PROGRAM,
                "Öğrenci / staj / program",
                "Bu kayıt bir öğrenci, staj veya gençlik programına işaret ediyor; kamu personel kadrosu değildir.",
                "Yüksek"
            )
        }

        if ((stage != null && stage != "BAŞVURU / YENİ ALIM") ||
            processPhrases.any(text::contains)
        ) {
            return CareerContentClassification(
                CareerContentType.PROCESS_NOTICE,
                "İşe alım süreci / kura / sonuç",
                "Metin yeni başvuru açılışından çok daha önce başlamış bir alımın kura, sınav, evrak veya sonuç aşamasını anlatıyor.",
                "Yüksek"
            )
        }

        val explicitJobLanguage = vacancyPattern.containsMatchIn(text) ||
            listOf(
                "is ilani", "acik pozisyon", "basvuruya acildi",
                "personel alim ilan", "isci alim ilan", "memur alim ilan",
                "teknisyen alim ilan", "tekniker alim ilan",
                "ise alim basvurulari", "recruitment opening", "job vacancy"
            ).any(text::contains)

        val path = runCatching { URI(url).path.orEmpty().lowercase(Locale.ROOT) }.getOrDefault("")
        val normalizedTitle = normalize(title)
        val roleInTitle = listOf(
            "elektrik", "teknisyen", "tekniker", "muhendis",
            "operator", "bakim", "uretim", "sofor", "depo"
        ).any(normalizedTitle::contains)
        val directJobPage = Regex(
            """/(?:ilan-detay|job|jobs|position|vacancy|is-ilanlari)(?:/|$)"""
        ).containsMatchIn(path) && roleInTitle

        if (explicitJobLanguage || directJobPage) {
            return CareerContentClassification(
                CareerContentType.JOB_LISTING,
                "İş ilanı adayı",
                "İşveren alımı, açık pozisyon veya doğrudan iş ilanı sayfası ifadesi bulundu. Başvurunun halen açık olduğu ayrıca kaynak sayfasından doğrulanmalıdır.",
                if (directJobPage || vacancyPattern.containsMatchIn(text)) "Yüksek" else "Orta"
            )
        }

        val newsTerms = listOf(
            "haber", "news", "basin aciklamasi", "aciklama yapildi",
            "etkinlik", "fuar", "toplanti", "ziyaret", "protokol",
            "bilgilendirme", "duyuru", "guncelleme", "istatistik",
            "rapor yayimlandi", "kampanya", "egitim programi"
        )
        val isInstitutionDomain = runCatching {
            val host = URI(url).host.orEmpty().lowercase(Locale.ROOT)
            host.endsWith(".gov.tr") || host.endsWith(".bel.tr") ||
                host.endsWith(".edu.tr") || CareerSourceCatalog.isTrustedUrl(url)
        }.getOrDefault(false)

        if (newsTerms.any(text::contains)) {
            return CareerContentClassification(
                if (isInstitutionDomain) CareerContentType.INSTITUTION_NOTICE else CareerContentType.NEWS,
                if (isInstitutionDomain) "Kurum duyurusu / bilgilendirme" else "Haber / bilgilendirme",
                "Metinde açık bir personel/işçi alımı veya pozisyon ilanı tespit edilmedi.",
                "Orta"
            )
        }

        return CareerContentClassification(
            CareerContentType.REVIEW_REQUIRED,
            "İçerik türü doğrulanmalı",
            "Başlık ve arama özeti, bu kaydın gerçek bir iş ilanı olduğunu kanıtlamak için yeterli değil. Yanlışlıkla ilan listesine alınmadı.",
            "Düşük"
        )
    }
}
