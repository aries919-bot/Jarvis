package com.cem.jarvis.media

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AriesMediaPolicyTest {
    @Test
    fun youtubeAndCatalogueHostsAreNeverPlaybackCandidates() {
        listOf(
            "https://www.youtube.com/watch?v=abc123",
            "https://youtu.be/abc123",
            "https://r1---sn.googlevideo.com/videoplayback?mime=video%2Fmp4",
            "https://www.justwatch.com/tr/dizi/example",
            "https://www.themoviedb.org/tv/123"
        ).forEach { url ->
            assertTrue("Expected discovery-only host: $url", AriesMediaPolicy.isDiscoveryOnlyHost(url))
            assertFalse("Must not be a direct playback URL: $url", AriesMediaPolicy.isSupportedMediaUrl(url))
            assertEquals(0, AriesMediaPolicy.candidateScore(url, "video/mp4"))
        }
    }

    @Test
    fun recognizesDirectPublicMediaFormatsAndRejectsOtherSchemes() {
        assertTrue(AriesMediaPolicy.isSupportedMediaUrl("https://cdn.example/video/episode.m3u8"))
        assertTrue(AriesMediaPolicy.isSupportedMediaUrl("https://cdn.example/video/episode.mp4"))
        assertTrue(AriesMediaPolicy.isSupportedMediaUrl("https://cdn.example/play?id=7&type=video%2Fmp4"))
        assertFalse(AriesMediaPolicy.isSupportedMediaUrl("javascript:alert(1)"))
        assertFalse(AriesMediaPolicy.isSupportedMediaUrl("https://cdn.example/watch/episode"))
    }

    @Test
    fun prefersManifestsOverProgressiveFiles() {
        assertTrue(
            AriesMediaPolicy.candidateScore("https://cdn.example/master.m3u8", null) >
                AriesMediaPolicy.candidateScore("https://cdn.example/episode.mp4", null)
        )
        assertEquals("application/dash+xml", AriesMediaPolicy.inferMimeType("https://cdn.example/stream.mpd"))
        assertEquals("video/x-matroska", AriesMediaPolicy.inferMimeType("https://cdn.example/episode.mkv"))
    }

    @Test
    fun validatesHlsAndDashUsingTheirManifestSignatures() {
        assertEquals(
            "application/x-mpegURL",
            AriesMediaPolicy.validatedMimeFromPrefix(
                "https://cdn.example/stream.m3u8", "application/octet-stream",
                "application/octet-stream", "#EXTM3U\n#EXT-X-VERSION:3".toByteArray()
            )
        )
        assertEquals(
            "application/dash+xml",
            AriesMediaPolicy.validatedMimeFromPrefix(
                "https://cdn.example/stream.mpd", "application/octet-stream",
                "application/octet-stream", "<?xml version='1.0'?><MPD>".toByteArray()
            )
        )
    }

    @Test
    fun validatesBinaryMp4AndRejectsHtmlFalsePositives() {
        val mp4 = ByteArray(16)
        "ftyp".toByteArray().copyInto(mp4, 4)
        "isom".toByteArray().copyInto(mp4, 8)
        assertEquals(
            "video/mp4",
            AriesMediaPolicy.validatedMimeFromPrefix(
                "https://cdn.example/episode", "application/octet-stream",
                "application/octet-stream", mp4
            )
        )
        assertNull(
            AriesMediaPolicy.validatedMimeFromPrefix(
                "https://cdn.example/episode.mp4", "video/mp4",
                "text/html", "<html>not a video</html>".toByteArray()
            )
        )
    }
}
