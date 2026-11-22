package com.cem.jarvis.media

import java.net.URI
import java.net.URLDecoder
import java.util.Locale

/**
 * Pure media-source rules shared by the WebView scanner and JVM unit tests.
 * Catalogue, trailer and subscription pages are never promoted to episode streams.
 */
internal object AriesMediaPolicy {
    private val discoveryOnlyDomains = setOf(
        "justwatch.com", "themoviedb.org", "imdb.com", "tvmaze.com",
        "thetvdb.com", "wikipedia.org", "rottentomatoes.com",
        "metacritic.com", "trakt.tv", "letterboxd.com",
        "youtube.com", "youtu.be", "youtube-nocookie.com", "googlevideo.com"
    )

    private val mediaExtensions = listOf(".m3u8", ".mp4", ".mpd", ".webm", ".m4v", ".mov", ".mkv")

    private fun uri(raw: String): URI? =
        runCatching { URI(raw.trim()) }.getOrNull()

    fun hostOf(raw: String): String =
        uri(raw)?.host?.lowercase(Locale.ROOT)?.removePrefix("www.").orEmpty()

    fun isDiscoveryOnlyHost(raw: String): Boolean {
        val parsed = uri(raw) ?: return false
        val scheme = parsed.scheme?.lowercase(Locale.ROOT) ?: return false
        if (scheme != "http" && scheme != "https") return false
        val host = parsed.host?.lowercase(Locale.ROOT)?.removePrefix("www.") ?: return false
        return discoveryOnlyDomains.any { host == it || host.endsWith(".$it") }
    }

    fun isSupportedMediaUrl(raw: String, mimeType: String? = null): Boolean {
        val parsed = uri(raw) ?: return false
        val scheme = parsed.scheme?.lowercase(Locale.ROOT) ?: return false
        if (scheme != "http" && scheme != "https") return false
        if (parsed.host.isNullOrBlank() || parsed.rawUserInfo != null || isDiscoveryOnlyHost(raw)) return false
        val path = parsed.path.orEmpty().lowercase(Locale.ROOT)
        if (mediaExtensions.any { extension -> path.endsWith(extension) }) return true
        if (inferMimeType(raw, mimeType) != null) return true

        val formatValues = runCatching {
            parsed.rawQuery.orEmpty().split("&").mapNotNull { part ->
                val pair = part.split("=", limit = 2)
                val key = URLDecoder.decode(pair[0], "UTF-8").lowercase(Locale.ROOT)
                if (key !in setOf("format", "type", "mime", "ext", "output")) null
                else URLDecoder.decode(pair.getOrNull(1).orEmpty(), "UTF-8")
            }.joinToString(" ").lowercase(Locale.ROOT)
        }.getOrDefault("")
        return listOf(
            "m3u8", "mpd", "mpegurl", "dash+xml", "video/mp4",
            "video/webm", "video/quicktime", "matroska"
        ).any { value -> formatValues.contains(value) }
    }

    /**
     * Preserve credentials only when a redirect stays on the exact same origin.
     * Custom x-* headers may contain tokens, so they are stripped cross-origin too.
     */
    fun headersForRedirect(
        fromUrl: String,
        toUrl: String,
        headers: Map<String, String>
    ): Map<String, String> {
        val from = uri(fromUrl) ?: return emptyMap()
        val to = uri(toUrl) ?: return emptyMap()
        val sameOrigin =
            from.scheme.equals(to.scheme, ignoreCase = true) &&
                from.host.equals(to.host, ignoreCase = true) &&
                from.port == to.port
        if (sameOrigin) return headers

        val blocked = setOf("authorization", "cookie", "proxy-authorization", "x-api-key", "x-auth-token")
        return headers.filterKeys { key ->
            val normalized = key.trim().lowercase(Locale.ROOT)
            normalized !in blocked && !normalized.startsWith("x-")
        }
    }

    fun inferMimeType(raw: String, responseType: String? = null): String? {
        val mime = responseType.orEmpty().substringBefore(";").trim().lowercase(Locale.ROOT)
        when {
            mime.contains("mpegurl") || mime.contains("vnd.apple.mpegurl") -> return "application/x-mpegURL"
            mime.contains("dash+xml") -> return "application/dash+xml"
            mime.contains("video/mp4") -> return "video/mp4"
            mime.contains("video/webm") -> return "video/webm"
            mime.contains("video/quicktime") -> return "video/quicktime"
            mime.contains("matroska") -> return "video/x-matroska"
        }

        val path = uri(raw)?.path.orEmpty().lowercase(Locale.ROOT)
        return when {
            path.endsWith(".m3u8") -> "application/x-mpegURL"
            path.endsWith(".mpd") -> "application/dash+xml"
            path.endsWith(".mp4") || path.endsWith(".m4v") -> "video/mp4"
            path.endsWith(".webm") -> "video/webm"
            path.endsWith(".mov") -> "video/quicktime"
            path.endsWith(".mkv") -> "video/x-matroska"
            else -> null
        }
    }

    fun candidateScore(raw: String, mime: String?): Int {
        if (isDiscoveryOnlyHost(raw)) return 0
        val path = uri(raw)?.path.orEmpty().lowercase(Locale.ROOT)
        val type = inferMimeType(raw, mime).orEmpty().lowercase(Locale.ROOT)
        return when {
            path.endsWith("master.m3u8") -> 110
            path.endsWith(".m3u8") || type.contains("mpegurl") -> 100
            path.endsWith(".mpd") || type.contains("dash+xml") -> 95
            path.endsWith(".mp4") || path.endsWith(".m4v") || type.contains("video/mp4") -> 85
            path.endsWith(".webm") || type.contains("video/webm") -> 80
            path.endsWith(".mov") || type.contains("video/quicktime") -> 75
            path.endsWith(".mkv") || type.contains("matroska") -> 70
            else -> 0
        }
    }

    /**
     * Validate a small response prefix before a URL is handed to Media3.
     * HTML/error bodies and unverified HLS/DASH labels are rejected.
     */
    fun validatedMimeFromPrefix(
        rawUrl: String,
        declaredMime: String?,
        responseMime: String?,
        prefixBytes: ByteArray
    ): String? {
        if (!isSupportedMediaUrl(rawUrl, declaredMime ?: responseMime)) return null

        val declared = declaredMime.orEmpty().substringBefore(";").trim().lowercase(Locale.ROOT)
        val contentType = responseMime.orEmpty().substringBefore(";").trim().lowercase(Locale.ROOT)
        val prefixText = String(prefixBytes, Charsets.UTF_8).removePrefix("\uFEFF").trimStart()
        if (
            contentType.contains("text/html") ||
            prefixText.startsWith("<!doctype html", ignoreCase = true) ||
            prefixText.startsWith("<html", ignoreCase = true)
        ) return null

        // HLS and DASH are accepted only when the response actually contains a manifest.
        if (prefixText.startsWith("#EXTM3U", ignoreCase = true)) {
            return "application/x-mpegURL"
        }
        if (prefixText.contains("<MPD", ignoreCase = true)) {
            return "application/dash+xml"
        }
        if (
            inferMimeType(rawUrl) == "application/x-mpegURL" ||
            inferMimeType(rawUrl) == "application/dash+xml" ||
            declared.contains("mpegurl") ||
            declared.contains("dash+xml") ||
            contentType.contains("mpegurl") ||
            contentType.contains("dash+xml")
        ) return null

        // ISO base media files (MP4/MOV) contain an ftyp box near the beginning.
        val ftypIndex = (4 until (prefixBytes.size - 3).coerceAtLeast(4))
            .firstOrNull { index ->
                prefixBytes[index] == 'f'.code.toByte() &&
                    prefixBytes[index + 1] == 't'.code.toByte() &&
                    prefixBytes[index + 2] == 'y'.code.toByte() &&
                    prefixBytes[index + 3] == 'p'.code.toByte()
            }
        if (ftypIndex != null) {
            val brand = if (prefixBytes.size >= ftypIndex + 8) {
                String(prefixBytes, ftypIndex + 4, 4, Charsets.US_ASCII)
            } else ""
            return if (brand == "qt  ") "video/quicktime" else "video/mp4"
        }

        // WebM and Matroska use the EBML document header.
        val isEbml = prefixBytes.size >= 4 &&
            prefixBytes[0] == 0x1A.toByte() &&
            prefixBytes[1] == 0x45.toByte() &&
            prefixBytes[2] == 0xDF.toByte() &&
            prefixBytes[3] == 0xA3.toByte()
        if (isEbml) {
            val header = String(prefixBytes, Charsets.ISO_8859_1).lowercase(Locale.ROOT)
            return if (header.contains("webm")) "video/webm" else "video/x-matroska"
        }

        // MIME and file extensions alone are insufficient evidence of a playable file.
        return null
    }
}
