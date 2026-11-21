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
            "yabancidizi.fan"
        ).forEach { domain -> assertTrue("Missing $domain", query.contains("site:$domain")) }
    }

    @Test
    fun searchQueryDoesNotAddUnscopedFallbackOrCatalogueDomain() {
        val query = AriesMediaSourceCatalog.searchQuery("Breaking Bad 2008 1. sezon 1. bölüm")
        assertFalse(query.contains("justwatch.com"))
        assertFalse(query.contains("OR \"Breaking Bad"))
        assertTrue(query.contains("OR Breaking Bad 2008"))
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
        assertTrue(AriesMediaSourceCatalog.isDiscoveryOnlyHost("https://www.tvmaze.com/shows/1/example"))
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
