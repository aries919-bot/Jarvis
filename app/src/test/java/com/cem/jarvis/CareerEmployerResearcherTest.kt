package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerEmployerResearcherTest {
    @Test
    fun extractsEmployerFromJobPostingJsonLd() {
        val html = """
            <html><head><title>Elektrik Teknisyeni</title>
            <meta name="description" content="Bakım ekibimize katılın">
            </head><body>
            <script type="application/ld+json">
            {
              "@context": "https://schema.org",
              "@type": "JobPosting",
              "title": "Elektrik Teknisyeni",
              "hiringOrganization": {
                "@type": "Organization",
                "name": "Örnek Enerji A.Ş."
              }
            }
            </script></body></html>
        """.trimIndent()
        assertEquals("Örnek Enerji A.Ş.", CareerEmployerResearcher.extractStructuredEmployer(html))
    }

    @Test
    fun doesNotInventAnEmployerWhenStructuredDataIsMissing() {
        val html = "<html><head><title>Elektrik teknisyeni iş ilanı</title></head><body>Başvur</body></html>"
        assertNull(CareerEmployerResearcher.extractStructuredEmployer(html))
    }

    @Test
    fun researchModelKeepsUnverifiedClaimsExplicit() {
        val model = CareerEmployerResearch(
            sourceUrl = "https://example.org/job/1",
            finalUrl = "https://example.org/job/1",
            sourceHost = "example.org",
            httpStatus = 200,
            pageTitle = "Elektrik Teknisyeni",
            pageDescription = "İş ilanı",
            structuredEmployer = null,
            employerName = null,
            sourceAssessment = "Tanımlı listede değil",
            employerAssessment = "Doğrulanmadı",
            companySearchQuery = "örnek",
            companyResults = emptyList(),
            registrySearchQuery = null,
            registryResults = emptyList(),
            limitations = "Sicil kaydı doğrulanmadı"
        )
        val prompt = model.toPromptText()
        assertTrue(prompt.contains("doğrulanmadı", ignoreCase = true))
        assertTrue(prompt.contains("sicil kaydı", ignoreCase = true))
    }

    @Test
    fun doesNotTreatNewsPublishersOrJobBoardsAsEmployers() {
        val newsHost = "memur365.com"
        assertNull(
            CareerEmployerResearcher.resolveMappedEmployer(
                "https://memur365.com/kamu-personel-alimi",
                newsHost,
                CareerSourceCatalog.sourceName("https://memur365.com/kamu-personel-alimi")
            )
        )

        val boardHost = "kariyer.net"
        assertNull(
            CareerEmployerResearcher.resolveMappedEmployer(
                "https://www.kariyer.net/is-ilani",
                boardHost,
                CareerSourceCatalog.sourceName("https://www.kariyer.net/is-ilanlari")
            )
        )

        val officialUrl = "https://personel.ozal.edu.tr/announcements-all"
        assertEquals(
            CareerSourceCatalog.sourceName(officialUrl),
            CareerEmployerResearcher.resolveMappedEmployer(
                officialUrl,
                "personel.ozal.edu.tr",
                CareerSourceCatalog.sourceName(officialUrl)
            )
        )
    }

}
