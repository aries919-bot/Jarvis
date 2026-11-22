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
}
