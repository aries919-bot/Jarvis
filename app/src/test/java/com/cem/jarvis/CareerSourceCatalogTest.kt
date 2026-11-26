package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerSourceCatalogTest {
    @Test
    fun acceptsHttpsGovernmentAndMunicipalDomainsAsOfficialSources() {
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://www.dsi.gov.tr/duyurular"))
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://www.sgk.gov.tr/"))
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://example.bel.tr/"))
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://sub.dsi.gov.tr/duyurular"))
        assertTrue(CareerSourceCatalog.isTrustedUrl("https://engelsiz.iskur.gov.tr/faaliyetlerimiz/iskur-genclik-programi"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://example.edu.tr/staj"))
        assertTrue(CareerSourceCatalog.isTrustedUrl("HTTPS://WWW.DSI.GOV.TR/duyurular"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("http://www.dsi.gov.tr/"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://www.dsi.gov.tr.evil.example/"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://www.dsi.gov.tr@evil.example/"))
        assertFalse(CareerSourceCatalog.isTrustedUrl("https://fake-government.example/"))
    }

    @Test
    fun classifiesGeneralGovernmentSitesSeparatelyFromKpssPortals() {
        assertEquals("Kamu / resmî kurum", CareerSourceCatalog.categoryFor("https://www.dsi.gov.tr/duyurular"))
        assertEquals("Kamu / KPSS ve merkezi yerleştirme", CareerSourceCatalog.categoryFor("https://www.osym.gov.tr/duyurular"))
        assertEquals("Kamu / işe alım ve başvuru portalı", CareerSourceCatalog.categoryFor("https://kariyerkapisi.gov.tr/isealim"))
        assertEquals("Kamu / resmî personel ilanı", CareerSourceCatalog.categoryFor("https://kamuilan.sbb.gov.tr/"))

        assertEquals("Belediye / kamu", CareerSourceCatalog.categoryFor("https://www.example.bel.tr/duyurular"))
    }

    @Test
    fun exposesNewGovernmentInstitutionsInTheOfficialSourceDirectory() {
        val names = CareerSourceCatalog.sources.map { it.name }
        assertTrue(names.contains("Devlet Su İşleri Genel Müdürlüğü"))
        assertTrue(names.contains("Devlet Hava Meydanları İşletmesi"))
        assertTrue(names.contains("Kıyı Emniyeti Genel Müdürlüğü"))
        assertTrue(names.contains("Sosyal Güvenlik Kurumu"))
        assertTrue(names.contains("PTT – duyurular"))
        assertTrue(names.contains("Malatya Turgut Özal Üniversitesi – Personel Daire Başkanlığı"))
        assertTrue(names.contains("İnönü Üniversitesi – Personel Daire Başkanlığı"))
        assertTrue(names.contains("Fırat Üniversitesi – Personel Daire Başkanlığı"))
        assertTrue(names.contains("Adıyaman Üniversitesi – Personel Daire Başkanlığı"))
        assertTrue(names.contains("e-Devlet – İş ve Kariyer hizmetleri"))
        assertTrue(names.contains("İŞKUR Gençlik Programı"))
        assertTrue(names.contains("İŞKUR İşgücü Uyum Programı (İUP)"))
        assertTrue(names.contains("GÜÇ – Gençliğin Üretim Çağı / Geleceğim Meslekte"))
        assertTrue(names.contains("GÜÇ – program ve hizmetler"))
        assertTrue(names.contains("İŞKUR Staj Portalı – lise öğrencileri"))
        assertTrue(names.contains("İŞKUR Staj Portalı – genel giriş"))
        assertTrue(names.contains("Ulusal Staj Programı – üniversite öğrencileri"))
        assertTrue(names.contains("Kariyer Kapısı – açık kamu ilanları"))
        assertTrue(names.contains("İçişleri Bakanlığı – duyurular"))
        assertTrue(names.contains("Millî Savunma Bakanlığı – Personel Temin Sistemi"))
        assertTrue(names.contains("Jandarma Genel Komutanlığı – duyurular"))
        assertTrue(names.contains("Çalışma ve Sosyal Güvenlik Bakanlığı – duyurular"))
        assertTrue(names.contains("Kültür ve Turizm Bakanlığı – Personel Genel Müdürlüğü"))
        assertTrue(names.contains("Savunma Sanayii Başkanlığı – duyurular"))
        assertEquals(
            "Üniversite / kamu personel alımı",
            CareerSourceCatalog.categoryFor("https://personel.ozal.edu.tr/announcements-all")
        )
    }

    @Test
    fun separatesPublicKuraKpssPrivateAndUnclassifiedJobBoardResults() {
        assertEquals(
            "Kura ile kamu alımı",
            CareerSourceCatalog.recruitmentGroup("Noter kurası sonuçları", "Asıl ve yedek aday listesi", "https://www.ogm.gov.tr/duyurular")
        )
        assertEquals(
            "KPSS ile kamu alımı",
            CareerSourceCatalog.recruitmentGroup("Personel alımı", "KPSS P94 şartı", "https://www.kariyerkapisi.gov.tr/")
        )
        assertEquals(
            "Kamu / kamu bağlantılı",
            CareerSourceCatalog.recruitmentGroup("Teknisyen alımı", "Başvuru duyurusu", "https://www.dsi.gov.tr/")
        )
        assertEquals(
            "Savunma sanayii / şirket alımı",
            CareerSourceCatalog.recruitmentGroup("Teknik personel", "Açık pozisyon", "https://akariyer.aselsan.com/")
        )
        assertEquals(
            "Özel sektör",
            CareerSourceCatalog.recruitmentGroup("Elektrik teknisyeni", "Açık pozisyon", "https://careers.borusan.com/")
        )
        assertEquals(
            "Savunma sanayii / şirket alımı",
            CareerSourceCatalog.recruitmentGroup("Elektrik-elektronik bakım teknisyeni", "Açık pozisyon", "https://kariyer.tusas.com/acik-pozisyonlarimiz")
        )
        assertEquals(
            "KİT / kamu şirketi alımı · usul doğrulanmalı",
            CareerSourceCatalog.recruitmentGroup("Teknisyen alımı", "Başvuru duyurusu", "https://www.tcdd.gov.tr/duyurular")
        )
        assertEquals(
            "Platform / sektör doğrulanmalı",
            CareerSourceCatalog.recruitmentGroup("Üretim personeli", "İş ilanı", "https://www.kariyer.net/is-ilanlari")
        )
    }

    @Test
    fun doesNotInferKpssRequirementFromKariyerKapisiDomainAlone() {
        assertEquals(
            "Kamu / kamu bağlantılı",
            CareerSourceCatalog.recruitmentGroup(
                "Sözleşmeli personel alımı",
                "Başvuru koşulları ve değerlendirme süreci kurum ilanında açıklanmıştır.",
                "https://kariyerkapisi.gov.tr/isealim"
            )
        )
        assertEquals(
            "KPSS ile kamu alımı",
            CareerSourceCatalog.recruitmentGroup(
                "Sözleşmeli personel alımı",
                "KPSS P94 puanı ile başvuru alınacaktır.",
                "https://kariyerkapisi.gov.tr/isealim"
            )
        )
    }

    @Test
    fun identifiesExplicitKpssFreePublicRecruitmentWithoutGuessingFromMissingKpssText() {
        assertEquals(
            "KPSS'siz kamu alımı",
            CareerSourceCatalog.recruitmentGroup(
                "Sürekli işçi alımı",
                "İlanda KPSS şartı aranmaz; başvuru İŞKUR üzerinden yapılacaktır.",
                "https://www.ogm.gov.tr/duyurular"
            )
        )
        assertEquals(
            "Kamu / kamu bağlantılı",
            CareerSourceCatalog.recruitmentGroup(
                "Personel alımı",
                "Başvuru şartları ve KPSS bilgisi kurum duyurusunda incelenmelidir.",
                "https://www.dsi.gov.tr/duyurular"
            )
        )
    }

    @Test
    fun doesNotLabelAnUnverifiedJobBoardKuraPostAsConfirmedPublicEmployment() {
        assertEquals(
            "Kura duyurusu · statü doğrulanmalı",
            CareerSourceCatalog.recruitmentGroup(
                "Noter kurası ile personel alımı",
                "Asıl ve yedek aday listesi yayımlandı.",
                "https://www.kariyer.net/is-ilani"
            )
        )
    }

    @Test
    fun doesNotAssumeEveryEduTrUniversityIsAStateInstitution() {
        assertEquals(
            "Üniversite / kamu personel alımı",
            CareerSourceCatalog.categoryFor("https://personel.ozal.edu.tr/announcements-all")
        )
        assertEquals(
            "Üniversite / statü doğrulanmalı",
            CareerSourceCatalog.categoryFor("https://personel.vakifuni.edu.tr/duyurular")
        )
        assertEquals(
            "Üniversite / statü doğrulanmalı",
            CareerSourceCatalog.recruitmentGroup(
                "Üniversite personel alımı",
                "Başvuru duyurusu",
                "https://personel.vakifuni.edu.tr/duyurular"
            )
        )
        assertEquals(
            "KPSS / şartı doğrulanmalı",
            CareerSourceCatalog.recruitmentGroup(
                "Personel alımı",
                "KPSS P94 şartı aranır",
                "https://personel.vakifuni.edu.tr/duyurular"
            )
        )
        assertEquals(
            "Kamu / kamu bağlantılı",
            CareerSourceCatalog.recruitmentGroup(
                "Malatya Turgut Özal Üniversitesi personel alımı",
                "Başvuru duyurusu",
                "https://personel.vakifuni.edu.tr/duyurular"
            )
        )
    }


    @Test
    fun doesNotAttributeGenericParentDomainsToASpecificInstitution() {
        assertEquals("gov.tr", CareerSourceCatalog.sourceName("https://gov.tr/duyurular"))
        assertEquals("ozal.edu.tr", CareerSourceCatalog.sourceName("https://ozal.edu.tr/"))
        assertEquals(
            "Malatya Turgut Özal Üniversitesi – Personel Daire Başkanlığı",
            CareerSourceCatalog.sourceName("https://personel.ozal.edu.tr/announcements-all")
        )
    }


    @Test
    fun includesCurrentUniversityInternshipAndVocationalCollegeCareerSources() {
        val sourceByName = CareerSourceCatalog.sources.associateBy { it.name }
        val ministryInternship = sourceByName["Adalet Bakanlığı – Ulusal Staj Programı duyurusu"]
        assertEquals(
            "https://edb.adalet.gov.tr/SayfaDetay/ulusal-staj-programi20112026014736",
            ministryInternship?.url
        )
        assertTrue(CareerSourceCatalog.isTrustedUrl(ministryInternship!!.url))
        assertTrue(CareerSourceCatalog.categoryFor(ministryInternship.url).contains("Kamu"))

        val vocationalCareer = sourceByName["GÜÇ – Gençliğin Üretim Çağı / Geleceğim Meslekte"]
        assertEquals("https://guc.iskur.gov.tr/", vocationalCareer?.url)
        assertTrue(CareerSourceCatalog.isTrustedUrl(vocationalCareer!!.url))
    }


    @Test
    fun keepsInternshipsAndYouthProgramsOutOfPublicStaffAppointmentBuckets() {
        val adalet = "https://edb.adalet.gov.tr/SayfaDetay/ulusal-staj-programi"
        assertEquals(
            "Öğrenci / staj / gençlik programı",
            CareerSourceCatalog.recruitmentGroup(
                "Ulusal Staj Programı başvuruları",
                "Üniversite ve yüksekokul öğrencilerinin kayıtlarını tamamlaması gerekmektedir.",
                adalet
            )
        )
        assertEquals(
            "Öğrenci / staj / gençlik programı",
            CareerSourceCatalog.recruitmentGroup(
                "İŞKUR Gençlik Programı",
                "Üniversite öğrencileri için program başvurusu",
                "https://esube.iskur.gov.tr/Istihdam/IstIskurGenclikProgramArama.aspx"
            )
        )
        assertEquals(
            "Kamu / kamu bağlantılı",
            CareerSourceCatalog.recruitmentGroup(
                "Sözleşmeli personel alımı",
                "Başvuru duyurusu",
                "https://www.dsi.gov.tr/duyurular"
            )
        )
    }


    @Test
    fun includesJandarmaOfficialPersonnelRecruitmentPortal() {
        val source = CareerSourceCatalog.sources.firstOrNull { it.name == "Jandarma Personel Temin Sistemi" }
        assertEquals("https://personeltemin.jandarma.gov.tr/", source?.url)
        assertTrue(CareerSourceCatalog.isTrustedUrl(source!!.url))
    }


    @Test
    fun allowsMemur365ForNewsDiscoveryWithoutTrustingItAsAnOfficialSource() {
        val url = "https://memur365.com/kamu-personel-alimi"
        assertTrue(CareerSourceCatalog.isNewsDiscoverySource(url))
        assertFalse(CareerSourceCatalog.isTrustedUrl(url))
        assertTrue(CareerSourceCatalog.isSecondary(url))
        assertTrue(CareerSourceCatalog.categoryFor(url).contains("Haber"))
        assertEquals(
            "Memur 365 – kamu personel haberleri",
            CareerSourceCatalog.sourceName(url)
        )
    }

}
