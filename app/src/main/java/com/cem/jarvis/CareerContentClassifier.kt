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
        """\b(?:personel|surekli isci|isci|memur|teknisyen|tekniker|muhendis|usta|operator|sofor|guvenlik gorevlisi|destek personeli|uzman erbas|astsubay|subay)\b.{0,55}\b(?:alim|alimi|ilan|ilani|istihdam|alinacak|acik pozisyon)\b|\b(?:alim|alimi|alinacak|istihdam)\b.{0,55}\b(?:personel|surekli isci|isci|memur|teknisyen|tekniker|muhendis|usta|operator|sofor|uzman erbas|astsubay|subay)\b"""
    )

    private val vacancyTerms = listOf(
        "personel alimi", "isci alimi", "surekli isci alimi",
        "memur alimi", "teknisyen alimi", "tekniker alimi",
        "muhendis alimi", "usta alimi", "operator alimi",
        "guvenlik gorevlisi alimi", "uzman erbas alimi", "astsubay alimi", "subay alimi",
        "acik pozisyon", "is ilani", "is ilanlari", "acik is ilanlari",
        "ise alim duyurusu", "isveren personel ariyor"
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

        // "Alım geliyor / bekleniyor" türü haber başlıkları, personel-alımı kelimesini
        // içerse bile yayımlanmış bir başvuru ilanı değildir. Açık başvuru sinyali veya
        // açıkça etiketlenmiş son tarih yoksa bunları iş ilanı akışına sokma.
        val unconfirmedFuturePhrases = listOf(
            "alimi geliyor", "alim geliyor", "alimi bekleniyor", "alim bekleniyor",
            "alimi gundemde", "personel alimi gundemde", "alimi iddia", "alimi iddiasi",
            "kulislere yansidi", "kulislere gore", "hazirliklari suruyor",
            "alimi planlaniyor", "alinmasi planlaniyor", "alimi yolda",
            "yakinda personel alimi", "basvuru tarihi bekleniyor",
            "basvuru takvimi bekleniyor", "basvuru tarihinin aciklanmasi bekleniyor",
            "resmi aciklama bekleniyor", "henuz resmi aciklama yok"
        )
        val confirmedApplicationPhrases = listOf(
            "basvurular basladi", "basvuru alimi basladi", "basvurular alinmaya baslandi",
            "basvuruya acildi", "basvurular devam ediyor", "basvurular suruyor",
            "basvuru kabul ediliyor", "basvuru kabul edilmektedir",
            "basvurular kabul edilmektedir", "basvuru yapilabilir"
        )
        val rawNoticeText = CareerListingParser.noticeText(title, snippet)
        val hasApplicationEvidence = CareerListingParser.deadlineDate(rawNoticeText) != null ||
            confirmedApplicationPhrases.any(text::contains)
        if (unconfirmedFuturePhrases.any(text::contains) && !hasApplicationEvidence &&
            (stage == null || stage == "BAŞVURU / YENİ ALIM")) {
            val trustedInstitution = CareerSourceCatalog.isTrustedUrl(url) || runCatching {
                val sourceHost = URI(url).host.orEmpty().lowercase(Locale.ROOT)
                sourceHost.endsWith(".gov.tr") || sourceHost.endsWith(".bel.tr")
            }.getOrDefault(false)
            return CareerContentClassification(
                if (trustedInstitution) CareerContentType.INSTITUTION_NOTICE else CareerContentType.NEWS,
                "Yaklaşan alım haberi · başvuru doğrulanmadı",
                "Metin alımın geleceğini, beklendiğini veya hazırlık aşamasında olduğunu anlatıyor; yayımlanmış başvuru açılışı ya da açıkça belirtilmiş son başvuru tarihi bulunmadı. Bu kayıt aktif iş ilanı değil, haber/duyuru olarak ayrıldı.",
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

        // News publishers are useful discovery sources, but even a story that repeats
        // application language is not the employer's original vacancy or application page.
        // Keep it in News & Announcements and let searches find the primary source separately.
        if (CareerSourceCatalog.isNewsDiscoverySource(url)) {
            return CareerContentClassification(
                CareerContentType.NEWS,
                "Kamu personel haberi · resmî ilan değil",
                "Bu bağlantı bir haber/yayın kaynağıdır. Haberde başvuru başladığı yazsa bile şartlar, son tarih ve başvuru adresi doğrudan kurumun veya işverenin kendi sayfasından doğrulanmalıdır.",
                "Yüksek"
            )
        }

        // Generic application-opening language alone is not proof of a job vacancy:
        // event, course, grant and student programme registrations use the same wording.
        val explicitJobLanguage = vacancyPattern.containsMatchIn(text) ||
            vacancyTerms.any(text::contains) ||
            listOf(
                "personel alim ilan", "isci alim ilan", "memur alim ilan",
                "teknisyen alim ilan", "tekniker alim ilan",
                "ise alim basvurulari", "recruitment opening", "job vacancy"
            ).any(text::contains)

        val parsedUri = runCatching { URI(url) }.getOrNull()
        val path = parsedUri?.path.orEmpty().lowercase(Locale.ROOT)
        val host = parsedUri?.host.orEmpty().lowercase(Locale.ROOT).removePrefix("www.")
        val normalizedTitle = normalize(title)
        val roleInTitle = listOf(
            "elektrik", "teknisyen", "tekniker", "muhendis",
            "operator", "bakim", "uretim", "sofor", "depo",
            "personel", "isci", "guvenlik", "destek", "saha", "teknik"
        ).any(normalizedTitle::contains)
        val directJobPage = (
            Regex("""/(?:ilan|ilan-detay|job|jobs|position|vacancy|is-ilani|is-ilanlari)(?:/|$)""")
                .containsMatchIn(path) && roleInTitle
            ) || (
                host == "esube.iskur.gov.tr" &&
                    path.contains("istacikisilanlari")
            ) || host == "acikisharita.iskur.gov.tr"

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
