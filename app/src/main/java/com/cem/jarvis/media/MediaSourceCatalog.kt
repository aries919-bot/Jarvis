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
        "dizilla.club",
        "justwatch.com"
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
        val siteFilters = searchDomains.joinToString(" OR ") { "site:$it" }
        return "\"$clean\" ($siteFilters) OR \"$clean\" izle"
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
