package com.cem.jarvis.media

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaSourceCatalogTest {
    @Test
    fun searchQueryIncludesSeekeeObservedSources() {
        val query = AriesMediaSourceCatalog.searchQuery("Dead End")
        listOf(
            "dizipalizm.com",
            "dizialbi.net",
            "321moviesfree.com",
            "cuevana19.com",
            "123pelicula.com",
            "justwatch.com"
        ).forEach { domain -> assertTrue("Missing $domain", query.contains("site:$domain")) }
    }

    @Test
    fun sourcePriorityRecognizesNestedDomains() {
        assertTrue(AriesMediaSourceCatalog.sourcePriority("https://www20.321moviesfree.com/title") > 0)
        assertTrue(AriesMediaSourceCatalog.sourcePriority("https://play.cuevana19.com/watch") > 0)
        assertTrue(AriesMediaSourceCatalog.sourcePriority("https://ver.123pelicula.com/movie") > 0)
    }

    @Test
    fun cataloguePagesAreNotTreatedAsPlayableSources() {
        assertTrue(AriesMediaSourceCatalog.isDiscoveryOnlyHost("https://www.justwatch.com/tr/film/example"))
        assertFalse(AriesMediaSourceCatalog.isDiscoveryOnlyHost("https://dizipalizm.com/diziler/"))
    }

    @Test
    fun searchTermIsNormalizedBeforeBuildingQuery() {
        val query = AriesMediaSourceCatalog.searchQuery("  Dead \"End\"\n Job  ")
        assertTrue(query.contains("Dead End Job"))
        assertFalse(query.contains("\n"))
        assertFalse(query.contains("\"End\""))
    }
}
