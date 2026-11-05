package com.cem.jarvis.media

import java.net.URI
import java.util.Locale

/**
 * Search/catalogue source registry. These domains are discovery candidates only:
 * direct playback is attempted only after the page exposes a public media URL
 * that passes the player's format and network validation.
 */
internal object AriesMediaSourceCatalog {
    val searchDomains: List<String> = listOf(
        "dizipalizm.com",
        "dizialbi.net",
        "321moviesfree.com",
        "cuevana19.com",
        "123pelicula.com",
        "yabancidizi.fan",
        "yabancidizi.tv",
        "dizilla.now",
        "dizilla.club"
    )

    private val priorities = listOf(
        "dizipalizm.com" to 150,
        "dizialbi.net" to 145,
        "321moviesfree.com" to 140,
        "cuevana19.com" to 135,
        "123pelicula.com" to 130,
        "yabancidizi.fan" to 125,
        "yabancidizi.tv" to 120,
        "dizilla.now" to 115,
        "dizilla.club" to 110
    )

    private val discoveryOnlyDomains = setOf(
        "justwatch.com", "themoviedb.org", "imdb.com", "tvmaze.com",
        "thetvdb.com", "wikipedia.org", "rottentomatoes.com",
        "metacritic.com", "trakt.tv", "letterboxd.com"
    )

    fun searchQuery(rawTerm: String): String {
        val clean = rawTerm.trim()
            .replace(Regex("[\\p{Cntrl}]"), " ")
            .replace('"', ' ')
            .replace(Regex("\\s+"), " ")
            .take(120)
            .trim()
        if (clean.isBlank()) return ""
        // Keep results scoped to actual candidate source domains. The previous
        // unscoped OR branch caused catalogue pages to outrank source pages.
        // Episode-specific pages are inconsistently indexed, so also search the
        // base series title while keeping the same source-domain restriction.
        val seriesBase = clean.replace(Regex("\\s+\\d+\\.\\s*sezon\\b.*$"), "").trim()
        val titleTerms = if (seriesBase.isNotBlank() && seriesBase != clean) {
            "($clean OR $seriesBase)"
        } else clean
        val siteFilters = searchDomains.joinToString(" OR ") { "site:$it" }
        return "$titleTerms ($siteFilters)"
    }

    fun hostOf(url: String): String =
        runCatching { URI(url.trim()).host?.lowercase(Locale.ROOT)?.removePrefix("www.") }
            .getOrNull().orEmpty()

    fun sourcePriority(url: String): Int {
        val host = hostOf(url)
        return priorities.firstOrNull { (domain, _) ->
            host == domain || host.endsWith(".$domain")
        }?.second ?: 0
    }

    fun isDiscoveryOnlyHost(url: String): Boolean {
        val host = hostOf(url)
        return discoveryOnlyDomains.any { host == it || host.endsWith(".$it") }
    }
}
