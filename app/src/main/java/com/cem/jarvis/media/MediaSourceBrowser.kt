package com.cem.jarvis.media

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private data class AriesSiteResult(
    val title: String,
    val url: String,
    val snippet: String
)

private data class AriesMediaCandidate(
    val url: String,
    val mime: String?,
    val headers: Map<String, String>,
    val score: Int
)

private data class AriesSeason(val number: Int, val episodes: Int)

private data class AriesSimilar(val title: String, val posterUrl: String?, val isTv: Boolean)

private data class AriesWatch(
    val title: String,
    val season: Int?,
    val episode: Int,
    val total: Int,
    val isMovie: Boolean = false
)

private data class AriesTitleInfo(
    val id: Int,
    val title: String,
    val isTv: Boolean,
    val year: String,
    val genres: String,
    val overview: String,
    val posterUrl: String?,
    val trailerUrl: String?,
    val rating: Double,
    val seasons: List<AriesSeason>,
    val totalEpisodes: Int,
    val runtimeMin: Int,
    val source: String = "tmdb"
)

// Ana uygulamayla ortak Aries tasarım sistemi.
private val AriesBg = Ap.Bg
private val AriesCard = Ap.Card
private val AriesChip = Ap.Card2
private val AriesText = Ap.Text
private val AriesBody = Ap.Text
private val AriesMuted = Ap.Muted

// İzleme sayfası da aynı yüzey ve vurgu ölçeğini kullanır.
private val AriesWatchBg = Ap.Bg
private val AriesWatchCard = Ap.Card
private val AriesWatchChip = Ap.Card2
private val AriesWatchSel = Ap.AccentSel

// TMDB anahtarı projede zaten varsa otomatik bulunur (BuildConfig).
// Bulunamazsa buraya yapıştır (v3 anahtarı veya v4 token olabilir).
private const val ARIES_TMDB_KEY_FALLBACK = ""

private fun ariesDdgUrl(term: String): String =
    "https://html.duckduckgo.com/html/?q=" + URLEncoder.encode(term.trim(), "UTF-8")

private fun ariesChipLabels(count: Int): List<String> {
    if (count <= 0) return emptyList()
    if (count <= 6) return (1..count).map { it.toString() }
    return listOf("1", "2", "…", (count - 1).toString(), count.toString())
}

private fun ariesWatchQuery(w: AriesWatch): String = when {
    w.season != null -> "${w.title} ${w.season}. sezon ${w.episode}. bölüm"
    w.isMovie -> "${w.title} film"
    else -> w.title
}

private fun ariesCleanTitle(raw: String): String {
    val trailing = Regex("(?i)(\\s+(izle|dizi|film|full|hd))+$")
    return raw.replace(trailing, "").trim().ifBlank { raw.trim() }
}

/**
 * Seekee düzeninde arama ekranı + izleme sayfası.
 * Arama: katalog kartı ve web sonuç kartları.
 * Katalog bilgisi ayrı, web sayfası ayrı açılır; kaynaklar otomatik taranır ve uygun akış oynatıcıya aktarılır.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MediaSourceBrowser(
    query: String,
    onBack: () -> Unit,
    playerOverlay: Boolean = false,
    onPlayMedia: (String, String?, Map<String, String>, Item, Int, Int, (String) -> Unit, Int, Boolean, () -> Unit) -> Unit
) {
    var searchText by remember(query) { mutableStateOf(query) }
    var activeQuery by remember(query) { mutableStateOf(query) }
    var webTerm by remember(query) { mutableStateOf(query) }
    var activeSite by remember { mutableStateOf<AriesSiteResult?>(null) }
    var pageTitle by remember { mutableStateOf("") }
    var pageUrl by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Film ve dizi siteleri aranıyor…") }
    var searching by remember { mutableStateOf(true) }
    val results = remember { mutableStateListOf<AriesSiteResult>() }
    var searchView by remember { mutableStateOf<WebView?>(null) }
    var siteView by remember { mutableStateOf<WebView?>(null) }
    var info by remember { mutableStateOf<AriesTitleInfo?>(null) }
    var infoNote by remember { mutableStateOf("") }
    var similar by remember { mutableStateOf<List<AriesSimilar>>(emptyList()) }
    var watching by remember { mutableStateOf<AriesWatch?>(null) }
    var trailerPlaying by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }
    var autoPlayedMediaUrl by remember { mutableStateOf<String?>(null) }
    var mediaWasDetected by remember { mutableStateOf(false) }
    var sourceScanExhausted by remember { mutableStateOf(false) }
    val attemptedSourceUrls = remember { mutableStateListOf<String>() }
    val mediaCandidates = remember { mutableStateListOf<AriesMediaCandidate>() }
    val failedMediaUrls = remember { mutableStateListOf<String>() }
    var playbackCandidateUrl by remember { mutableStateOf<String?>(null) }
    var fullscreenView by remember { mutableStateOf<View?>(null) }
    var fullscreenCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }
    val hostContext = LocalContext.current

    // Keep the source page mounted behind ExoPlayer so a failed stream or
    // the player's back action returns to the exact same page and watch state.
    LaunchedEffect(playerOverlay, activeSite?.url) {
        siteView?.let { view ->
            if (playerOverlay) view.onPause() else view.onResume()
        }
    }

    val onShowCustomCb: (View, WebChromeClient.CustomViewCallback) -> Unit = { view, callback ->
        fullscreenCallback = callback
        fullscreenView = view
    }
    val onHideCustomCb: () -> Unit = {
        fullscreenCallback = null
        fullscreenView = null
    }

    fun exitFullscreen() {
        fullscreenCallback?.onCustomViewHidden()
        fullscreenCallback = null
        fullscreenView = null
    }

    DisposableEffect(fullscreenView) {
        val activity = hostContext.findAriesActivity()
        val window = activity?.window
        if (fullscreenView != null && window != null) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            if (window != null) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                WindowCompat.getInsetsController(window, window.decorView)
                    .show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    LaunchedEffect(activeQuery) {
        info = null
        infoNote = ""
        similar = emptyList()
        val key = ariesTmdbKey()
        val found = withContext(Dispatchers.IO) {
            val tmdb = if (key.isNotBlank()) runCatching { fetchAriesTitleInfo(activeQuery, key) }.getOrNull() else null
            tmdb ?: runCatching { fetchAriesTvMazeTitleInfo(activeQuery) }.getOrNull()
        }
        if (found == null) {
            infoNote = "Katalog bilgisi bulunamadı. Web sonuçlarını aşağıdaki kartlardan açabilirsin."
        } else {
            info = found
            if (key.isNotBlank() && found.source == "tmdb") {
                similar = withContext(Dispatchers.IO) {
                    runCatching { fetchAriesSimilar(found.id, found.isTv, key) }.getOrDefault(emptyList())
                }
            }
        }
    }

    fun runWebSearch(term: String) {
        webTerm = term
        results.clear()
        searching = true
        status = "\"$term\" için siteler aranıyor…"
        searchView?.loadUrl(ariesDdgUrl(term))
    }

    fun startSearch(text: String) {
        val term = text.trim()
        if (term.isEmpty()) return
        searchText = term
        activeQuery = term
        activeSite = null
        watching = null
        playerError = null
        autoPlayedMediaUrl = null
        mediaCandidates.clear()
        failedMediaUrls.clear()
        playbackCandidateUrl = null
        runWebSearch(term)
    }

    fun openSite(site: AriesSiteResult) {
        trailerPlaying = false
        playerError = null
        autoPlayedMediaUrl = null
        mediaWasDetected = false
        mediaCandidates.clear()
        failedMediaUrls.clear()
        playbackCandidateUrl = null
        sourceScanExhausted = false
        attemptedSourceUrls.add(site.url)
        activeSite = site
        pageTitle = site.title
        pageUrl = site.url
        status = "Kaynak taranıyor: ${site.title}"
    }

    fun openNextAutoSource() {
        val next = results.firstOrNull {
            it.url !in attemptedSourceUrls && !isAriesDiscoveryOnlyHost(it.url)
        }
        if (next != null) {
            openSite(next)
            return
        }
        // If every remaining result is a catalogue/search page, leave one in the
        // in-app browser as a fallback instead of treating it as a playable host.
        val fallback = results.firstOrNull { it.url !in attemptedSourceUrls }
        if (activeSite == null && fallback != null) {
            attemptedSourceUrls.add(fallback.url)
            activeSite = fallback
            pageTitle = fallback.title
            pageUrl = fallback.url
        }
        sourceScanExhausted = true
        status = "Otomatik tarama tamamlandı. Doğrudan akış bulunamadı; açık sayfa Aries içinde bırakıldı."
    }

    fun openResultInPlayer(site: AriesSiteResult) {
        attemptedSourceUrls.clear()
        openSite(site)
    }

    fun enterWatch(title: String, season: Int?, episode: Int, total: Int, isMovie: Boolean = false) {
        val w = AriesWatch(title, season, episode, total, isMovie)
        trailerPlaying = false
        playerError = null
        watching = w
        activeSite = null
        autoPlayedMediaUrl = null
        mediaCandidates.clear()
        failedMediaUrls.clear()
        playbackCandidateUrl = null
        attemptedSourceUrls.clear()
        runWebSearch(ariesWatchQuery(w))
    }

    fun selectEpisode(episode: Int) {
        val w = watching ?: return
        trailerPlaying = false
        playerError = null
        val next = w.copy(episode = episode)
        watching = next
        activeSite = null
        autoPlayedMediaUrl = null
        mediaCandidates.clear()
        failedMediaUrls.clear()
        playbackCandidateUrl = null
        attemptedSourceUrls.clear()
        runWebSearch(ariesWatchQuery(next))
    }

    // Collect a short burst of requests, prefer a master HLS manifest, and hand off only one
    // candidate at a time. A failed candidate is remembered so the next one can be tried.
    LaunchedEffect(
        activeSite?.url, watching, mediaCandidates.size, playbackCandidateUrl,
        failedMediaUrls.size, sourceScanExhausted
    ) {
        val selectedSite = activeSite
        val currentWatch = watching
        if (selectedSite != null && currentWatch != null &&
            playbackCandidateUrl == null && !sourceScanExhausted
        ) {
            kotlinx.coroutines.delay(3200)
            if (activeSite?.url != selectedSite.url || watching != currentWatch ||
                playbackCandidateUrl != null || sourceScanExhausted
            ) return@LaunchedEffect

            val candidate = mediaCandidates
                .filterNot { item -> failedMediaUrls.contains(item.url) }
                .maxByOrNull { it.score }

            if (candidate == null) {
                if (mediaCandidates.isNotEmpty() && failedMediaUrls.size >= mediaCandidates.size) {
                    playerError = "Bu kaynaktaki akışlar açılamadı; sıradaki kaynak deneniyor."
                    status = playerError.orEmpty()
                    openNextAutoSource()
                }
                return@LaunchedEffect
            }

            val page = siteView?.url?.takeIf { it.startsWith("http", true) } ?: selectedSite.url
            val headers = linkedMapOf<String, String>()
            candidate.headers.forEach { (name, value) ->
                if (isAriesForwardableHeader(name) && value.isNotBlank()) headers[name] = value
            }
            headers.putIfAbsent("Referer", page)
            headers.putIfAbsent(
                "User-Agent",
                siteView?.settings?.userAgentString
                    ?: "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 Chrome/120.0.0.0 Mobile Safari/537.36"
            )
            val origin = runCatching {
                val uri = Uri.parse(page)
                "${uri.scheme}://${uri.authority}"
            }.getOrNull()
            if (!origin.isNullOrBlank()) headers.putIfAbsent("Origin", origin)
            if (!headers.containsKey("Cookie")) {
                val cookie = CookieManager.getInstance().getCookie(candidate.url)
                if (!cookie.isNullOrBlank()) headers["Cookie"] = cookie
            }

            playbackCandidateUrl = candidate.url
            autoPlayedMediaUrl = candidate.url
            status = "Video akışı bulundu; Aries oynatıcısına aktarılıyor…"
            siteView?.evaluateJavascript(
                "document.querySelectorAll('video,audio').forEach(function(v){try{v.pause();}catch(e){}});",
                null
            )
            val playableItem = info?.toCatalogItem(activeQuery, currentWatch.isMovie)
                ?: createWebQueryItem(activeQuery, currentWatch.isMovie)

            onPlayMedia(
                candidate.url,
                candidate.mime?.takeIf { it.isNotBlank() } ?: inferAriesMimeType(candidate.url),
                headers,
                playableItem,
                currentWatch.season ?: 0,
                currentWatch.episode,
                { failedUrl ->
                    if (activeSite?.url == selectedSite.url && playbackCandidateUrl == failedUrl) {
                        if (!failedMediaUrls.contains(failedUrl)) failedMediaUrls.add(failedUrl)
                        playbackCandidateUrl = null
                        autoPlayedMediaUrl = null
                        status = "Bu video akışı açılamadı; aynı kaynaktaki diğer akış deneniyor…"
                    }
                },
                currentWatch.total,
                currentWatch.isMovie
            ) {
                if (!currentWatch.isMovie && currentWatch.total > currentWatch.episode) {
                    selectEpisode(currentWatch.episode + 1)
                }
            }
        }
    }

    // Keep the current source page open if no playable stream is detected.
    LaunchedEffect(activeSite?.url, watching, autoPlayedMediaUrl, sourceScanExhausted, mediaWasDetected) {
        val current = activeSite
        if (watching != null && current != null && autoPlayedMediaUrl == null &&
            !mediaWasDetected && !sourceScanExhausted
        ) {
            kotlinx.coroutines.delay(18000)
            if (activeSite?.url == current.url && autoPlayedMediaUrl == null &&
                !mediaWasDetected && !sourceScanExhausted
            ) {
                playerError = "Bu sayfada oynatılabilir akış bulunamadı; sıradaki kaynak deneniyor."
                status = playerError.orEmpty()
                openNextAutoSource()
            }
        }
    }

    BackHandler(enabled = activeSite != null && watching == null) {
        siteView?.stopLoading()
        activeSite = null
        status = if (results.isEmpty()) "Arama sonucu bekleniyor" else "${results.size} site bulundu"
    }

    BackHandler(enabled = watching != null) {
        siteView?.stopLoading()
        watching = null
        activeSite = null
        status = if (results.isEmpty()) "Arama sonucu bekleniyor" else "${results.size} site bulundu"
    }

    BackHandler(enabled = fullscreenView != null) { exitFullscreen() }

    DisposableEffect(Unit) {
        onDispose {
            searchView?.stopLoading()
            searchView?.removeJavascriptInterface("AriesSearchBridge")
            searchView?.destroy()
            searchView = null
        }
    }

    Box(Modifier.fillMaxSize().alpha(if (playerOverlay) 0f else 1f)) {
        val currentWatch = watching
        if (currentWatch != null) {
            AriesWatchScreen(
                watch = currentWatch,
                sources = results.toList(),
                searching = searching,
                currentUrl = activeSite?.url,
                scanExhausted = sourceScanExhausted,
                playerError = playerError,
                similar = similar,
                trailerUrl = info?.trailerUrl,
                trailerPlaying = trailerPlaying,
                onPlayTrailer = { trailerPlaying = true },
                onBack = {
                    siteView?.stopLoading()
                    watching = null
                    activeSite = null
                    trailerPlaying = false
                    playerError = null
                    status = if (results.isEmpty()) "Arama sonucu bekleniyor"
                        else "${results.size} site bulundu"
                },
                onSelectEpisode = { selectEpisode(it) },
                onSelectSource = { openSite(it) },
                onSimilar = { startSearch(it) },
                video = {
                    val selectedSite = activeSite
                    when {
                        selectedSite != null -> AriesSiteWeb(
                            url = selectedSite.url,
                            modifier = Modifier.fillMaxSize(),
                            onTitle = { pageTitle = it },
                            onUrl = { pageUrl = it },
                            onStatus = { status = it },
                            onShowCustom = onShowCustomCb,
                            onHideCustom = onHideCustomCb,
                            onCreated = { siteView = it },
                            onGone = { v -> if (siteView === v) siteView = null },
                            onMediaFound = { mediaUrl, detectedMime, capturedHeaders ->
                                if (isSupportedMediaUrl(mediaUrl) || isAriesMediaMime(detectedMime)) {
                                    val normalizedUrl = mediaUrl.trim()
                                    val score = ariesMediaCandidateScore(normalizedUrl, detectedMime)
                                    if (score > 0 && failedMediaUrls.none { it == normalizedUrl }) {
                                         mediaWasDetected = true
                                        val index = mediaCandidates.indexOfFirst { it.url == normalizedUrl }
                                        if (index < 0) {
                                            mediaCandidates.add(
                                                AriesMediaCandidate(
                                                    url = normalizedUrl,
                                                    mime = detectedMime ?: inferAriesMimeType(normalizedUrl),
                                                    headers = capturedHeaders,
                                                    score = score
                                                )
                                            )
                                            status = "Video akışı bulundu; en uygun kaynak seçiliyor…"
                                        } else {
                                            val old = mediaCandidates[index]
                                            mediaCandidates[index] = old.copy(
                                                mime = detectedMime ?: old.mime,
                                                headers = old.headers + capturedHeaders,
                                                score = maxOf(old.score, score)
                                            )
                                        }
                                    }
                                }
                            }
                        )
                        trailerPlaying && info?.trailerUrl != null -> AriesSiteWeb(
                            url = info!!.trailerUrl!!,
                            modifier = Modifier.fillMaxSize(),
                            onTitle = { pageTitle = it },
                            onUrl = { pageUrl = it },
                            onStatus = { status = it },
                            onShowCustom = onShowCustomCb,
                            onHideCustom = onHideCustomCb,
                            onCreated = { siteView = it },
                            onGone = { v -> if (siteView === v) siteView = null }
                        )
                        else -> Text(
                            playerError ?: if (searching) "Web sonuçları aranıyor…"
                            else "Uygun akış bulunamazsa seçilen kaynak uygulama içinde açılır.",
                            color = AriesMuted,
                            fontSize = 15.sp
                        )
                    }
                }
            )
        } else {
            Column(Modifier.fillMaxSize().background(AriesBg)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(AriesCard)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "‹",
                        color = AriesText,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .clickable {
                                if (activeSite != null) {
                                    siteView?.stopLoading()
                                    activeSite = null
                                } else onBack()
                            }
                            .padding(end = 14.dp)
                    )
                    if (activeSite == null) {
                        BasicTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = AriesText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(AriesText),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { startSearch(searchText) }),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Ara",
                            color = AriesText,
                            fontSize = 16.sp,
                            modifier = Modifier.clickable { startSearch(searchText) }.padding(8.dp)
                        )
                    } else {
                        Column(Modifier.weight(1f)) {
                            Text(
                                pageTitle.ifBlank { "Site" },
                                color = AriesText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                pageUrl,
                                color = AriesMuted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            "Yenile",
                            color = AriesText,
                            modifier = Modifier.clickable { siteView?.reload() }.padding(6.dp)
                        )
                    }
                }

                val openedSite = activeSite
                if (openedSite == null) {
                    Text(
                        status,
                        color = AriesMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val firstResult = results.firstOrNull()
                        if (firstResult != null) {
                            item(key = firstResult.url) {
                                AriesWebCard(firstResult) { openResultInPlayer(firstResult) }
                            }
                        }
                        val currentInfo = info
                        if (currentInfo != null) {
                            if (currentInfo.isTv && currentInfo.seasons.isNotEmpty()) {
                                items(currentInfo.seasons, key = { "season-${it.number}" }) { season ->
                                    AriesRichCard(
                                        info = currentInfo,
                                        season = season,
                                        onWatch = {
                                            enterWatch(currentInfo.title, season.number, 1, season.episodes)
                                        },
                                        onEpisode = { ep ->
                                            enterWatch(currentInfo.title, season.number, ep, season.episodes)
                                        }
                                    )
                                }
                            } else {
                                item(key = "aries-single") {
                                    AriesRichCard(
                                        info = currentInfo,
                                        season = null,
                                        onWatch = { enterWatch(currentInfo.title, null, 0, 0, true) },
                                        onEpisode = { }
                                    )
                                }
                            }
                        } else {
                            item(key = "aries-fallback") {
                                val fallbackTitle = ariesCleanTitle(activeQuery)
                                AriesFallbackCard(fallbackTitle, infoNote) {
                                    enterWatch(fallbackTitle, null, 0, 0)
                                }
                            }
                        }
                        items(results.drop(1), key = { it.url }) { result ->
                            AriesWebCard(result) { openResultInPlayer(result) }
                        }
                    }
                } else {
                    Text(
                        status,
                        color = AriesMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    AriesSiteWeb(
                        url = openedSite.url,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        onTitle = { pageTitle = it },
                        onUrl = { pageUrl = it },
                        onStatus = { status = it },
                        onShowCustom = onShowCustomCb,
                        onHideCustom = onHideCustomCb,
                        onCreated = { siteView = it },
                        onGone = { v -> if (siteView === v) siteView = null }
                    )
                }
            }
        }

        // Arama sonuçlarını toplayan görünmez WebView
        Box(Modifier.size(1.dp)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    createAriesSearchWebView(
                        context = context,
                        onTitle = { },
                        onUrl = { },
                        onLoading = { },
                        onStatus = { },
                        onResult = { result ->
                            if (results.none { it.url == result.url }) results.add(result)
                        },
                        onSearchFinished = {
                            searching = false
                            if (watching != null && results.isNotEmpty()) {
                                attemptedSourceUrls.clear()
                                sourceScanExhausted = false
                                status = "${results.size} kaynak bulundu. İlk kaynak otomatik deneniyor…"
                                openNextAutoSource()
                            } else {
                                status = if (results.isEmpty())
                                    "Bu aramada site sonucu alınamadı. Başka kelimeler deneyebilirsin."
                                else "${results.size} site sonucu bulundu"
                            }
                        }
                    ).also {
                        searchView = it
                        it.loadUrl(ariesDdgUrl(webTerm))
                    }
                }
            )
        }

        fullscreenView?.let { view ->
            Box(Modifier.fillMaxSize().background(Color.Black)) {
                AndroidView(modifier = Modifier.fillMaxSize(), factory = { view })
            }
        }
    }
}

@Composable
private fun AriesSiteWeb(
    url: String,
    modifier: Modifier,
    onTitle: (String) -> Unit,
    onUrl: (String) -> Unit,
    onStatus: (String) -> Unit,
    onShowCustom: (View, WebChromeClient.CustomViewCallback) -> Unit,
    onHideCustom: () -> Unit,
    onCreated: (WebView) -> Unit,
    onGone: (WebView) -> Unit,
    onMediaFound: ((String, String?, Map<String, String>) -> Unit)? = null
) {
    val loaded = remember { arrayOfNulls<String>(1) }
    val holder = remember { arrayOfNulls<WebView>(1) }
    DisposableEffect(Unit) {
        onDispose {
            val view = holder[0]
            if (view != null) {
                view.stopLoading()
                view.removeJavascriptInterface("AriesMediaBridge")
                view.destroy()
                onGone(view)
            }
            holder[0] = null
        }
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            createAriesSiteWebView(
                context = context,
                onTitle = onTitle,
                onUrl = onUrl,
                onLoading = { },
                onStatus = onStatus,
                onShowCustom = onShowCustom,
                onHideCustom = onHideCustom,
                onMediaFound = onMediaFound
            ).also {
                holder[0] = it
                loaded[0] = url
                if (url.contains("youtube-nocookie.com", ignoreCase = true)) {
                    it.settings.mediaPlaybackRequiresUserGesture = false
                }
                it.loadUrl(url)
                onCreated(it)
            }
        },
        update = { view ->
            if (loaded[0] != url) {
                loaded[0] = url
                view.loadUrl(url)
            }
        }
    )
}

@Composable
private fun AriesWatchScreen(
    watch: AriesWatch,
    sources: List<AriesSiteResult>,
    searching: Boolean,
    currentUrl: String?,
    scanExhausted: Boolean,
    playerError: String?,
    similar: List<AriesSimilar>,
    trailerUrl: String?,
    trailerPlaying: Boolean,
    onPlayTrailer: () -> Unit,
    onBack: () -> Unit,
    onSelectEpisode: (Int) -> Unit,
    onSelectSource: (AriesSiteResult) -> Unit,
    onSimilar: (String) -> Unit,
    video: @Composable () -> Unit
) {
    val titleText = if (watch.season != null) {
        "${watch.title} ${watch.season}.Sezon (${watch.episode})"
    } else watch.title

    Column(Modifier.fillMaxSize().background(AriesWatchBg)) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color(0xFF151515))
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "×",
                color = AriesText,
                fontSize = 34.sp,
                modifier = Modifier.clickable { onBack() }.padding(end = 22.dp)
            )
            Text(
                "Aries – Film ve Dizi",
                color = AriesText,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (currentUrl == null && !trailerPlaying && trailerUrl != null) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFDCE5FF))
                        .clickable { onPlayTrailer() }
                        .padding(horizontal = 28.dp, vertical = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("▶", color = Color(0xFF10131D), fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Fragman sayfasını aç",
                            color = Color(0xFF10131D),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                video()
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item(key = "w-title") {
                Text(titleText, color = AriesText, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
            item(key = "w-sources") {
                if (sources.isEmpty()) {
                    Text(
                        if (searching) "Kaynaklar aranıyor…" else "Bu bölüm için kaynak bulunamadı.",
                        color = AriesMuted,
                        fontSize = 14.sp
                    )
                } else {
                    Column {
                        Text(
                            "Kaynaklar",
                            color = AriesText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(sources, key = { it.url }) { source ->
                                val selected = source.url == currentUrl
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) AriesWatchSel else AriesWatchChip)
                                        .clickable { onSelectSource(source) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(hostOfAries(source.url), color = AriesText, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
            item(key = "w-auto-scan") {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(AriesWatchCard).padding(14.dp)
                ) {
                    Text(
                        "Otomatik kaynak tarama",
                        color = AriesText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        when {
                            currentUrl != null && scanExhausted -> "Doğrudan video adresi bulunamadı. Açık web sayfası Aries içinde bırakıldı."
                            currentUrl != null -> "Açık sayfanın ağ istekleri ve oynatıcı kaynakları taranıyor."
                            searching -> "Aries izleme kaynaklarını arıyor…"
                            sources.isNotEmpty() -> "Kaynaklar bulundu. İlk sayfa otomatik açılıyor; akış bulunamazsa sonraki kaynak deneniyor."
                            else -> "Bu arama için kaynak bulunamadı. Başlığı veya bölüm numarasını değiştirerek tekrar deneyebilirsin."
                        },
                        color = AriesMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            }
            if (watch.total > 0) {
                item(key = "w-episodes") {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Bölüm", color = AriesText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("Toplam ${watch.total} bölüm ›", color = AriesMuted, fontSize = 15.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items((1..watch.total).toList(), key = { it }) { number ->
                                val selected = number == watch.episode
                                Box(
                                    Modifier
                                        .width(82.dp)
                                        .height(78.dp)
                                        .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                                        .background(if (selected) AriesWatchSel else AriesWatchChip)
                                        .clickable { onSelectEpisode(number) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(number.toString(), color = AriesText, fontSize = 27.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
            if (similar.isNotEmpty()) {
                item(key = "w-similar") {
                    Column {
                        Text(
                            "Benzerler",
                            color = AriesText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(similar) { item ->
                                Column(
                                    Modifier.width(130.dp).clickable { onSimilar(item.title) }
                                ) {
                                    AriesPoster(item.posterUrl, Modifier.fillMaxWidth().height(190.dp))
                                    Text(
                                        item.title,
                                        color = AriesText,
                                        fontSize = 15.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun AriesRichCard(
    info: AriesTitleInfo,
    season: AriesSeason?,
    onWatch: () -> Unit,
    onEpisode: (Int) -> Unit
) {
    val title = if (season != null) "${info.title} ${season.number}.Sezon" else info.title
    val meta = listOf(
        if (info.isTv) "TV Dizisi" else "Film",
        info.year,
        info.genres
    ).filter { it.isNotBlank() }.joinToString(" · ")
    val labels = ariesChipLabels(season?.episodes ?: 0)

    Column(
        Modifier
            .fillMaxWidth()
            .background(AriesCard)
            .padding(16.dp)
    ) {
        Text(
            title,
            color = AriesText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Row(Modifier.fillMaxWidth().padding(top = 10.dp)) {
            Column(Modifier.weight(1f)) {
                Text(meta, color = AriesBody, fontSize = 15.sp)
                if (info.overview.isNotBlank()) {
                    Text(
                        info.overview,
                        color = AriesBody,
                        fontSize = 15.sp,
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            AriesPoster(info.posterUrl, Modifier.width(112.dp).height(168.dp))
        }
        if (labels.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth().padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                labels.forEach { label ->
                    val episode = label.toIntOrNull()
                    Box(
                        Modifier
                            .weight(1f)
                            .height(70.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AriesChip)
                            .clickable(enabled = episode != null) {
                                if (episode != null) onEpisode(episode)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = AriesText, fontSize = 16.sp)
                    }
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(66.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AriesChip)
                .clickable { onWatch() },
            contentAlignment = Alignment.Center
        ) {
            Text("İzle  ›", color = AriesText, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
        AriesSourceRow(info.source.uppercase())
    }
}

@Composable
private fun AriesFallbackCard(title: String, note: String, onWatch: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(AriesCard)
            .padding(16.dp)
    ) {
        Text(
            title,
            color = AriesText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (note.isNotBlank()) {
            Text(note, color = AriesMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AriesChip)
                .clickable { onWatch() },
            contentAlignment = Alignment.Center
        ) {
            Text("İzle  ›", color = AriesText, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
        AriesSourceRow("Aries")
    }
}

@Composable
private fun AriesWebCard(result: AriesSiteResult, onOpen: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(AriesCard)
            .clickable { onOpen() }
            .padding(16.dp)
    ) {
        Text(
            result.title,
            color = AriesText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (result.snippet.isNotBlank()) {
            Text(
                result.snippet,
                color = AriesBody,
                fontSize = 15.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        AriesSourceRow(hostOfAries(result.url))
    }
}

@Composable
private fun AriesSourceRow(name: String) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val isTvMaze = name.equals("tvmaze", ignoreCase = true)
    Row(
        Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(26.dp).clip(CircleShape).background(AriesChip),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1).uppercase(), color = AriesText, fontSize = 12.sp)
        }
        Spacer(Modifier.width(10.dp))
        Text(
            if (isTvMaze) "TVmaze ↗" else name,
            color = AriesMuted,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).clickable(enabled = isTvMaze) {
                uriHandler.openUri("https://www.tvmaze.com")
            }
        )
    }
}

@Composable
private fun AriesPoster(url: String?, modifier: Modifier) {
    var bmp by remember(url) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(url) {
        if (url != null) {
            bmp = withContext(Dispatchers.IO) {
                runCatching {
                    URL(url).openStream().use { BitmapFactory.decodeStream(it) }
                }.getOrNull()
            }
        }
    }
    Box(modifier.clip(RoundedCornerShape(12.dp)).background(AriesChip)) {
        bmp?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private fun ariesTmdbKey(): String {
    if (ARIES_TMDB_KEY_FALLBACK.isNotBlank()) return ARIES_TMDB_KEY_FALLBACK
    val fields = listOf("TMDB_API_KEY", "TMDB_KEY", "TMDB_TOKEN", "TMDB_READ_TOKEN", "TMDB_ACCESS_TOKEN")
    for (field in fields) {
        val value = runCatching {
            Class.forName("com.cem.jarvis.BuildConfig").getField(field).get(null) as? String
        }.getOrNull()
        if (!value.isNullOrBlank()) return value.trim()
    }
    return ""
}

private fun ariesTmdbGet(path: String, extra: String, key: String): String? {
    val bearer = key.length > 40
    val full = "https://api.themoviedb.org/3/$path?language=tr-TR$extra" +
        if (bearer) "" else "&api_key=$key"
    val conn = URL(full).openConnection() as HttpURLConnection
    return try {
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        conn.setRequestProperty("accept", "application/json")
        if (bearer) conn.setRequestProperty("Authorization", "Bearer $key")
        if (conn.responseCode != 200) null
        else conn.inputStream.bufferedReader().use { it.readText() }
    } finally {
        conn.disconnect()
    }
}

private fun fetchAriesTitleInfo(rawQuery: String, key: String): AriesTitleInfo? {
    val trailing = Regex("(?i)(\\s+(izle|dizi|film|full|hd))+$")
    val cleaned = rawQuery.replace(trailing, "").trim().ifBlank { rawQuery.trim() }
    val wantsTv = Regex("(?i)\\bdizi\\b").containsMatchIn(rawQuery)
    val wantsMovie = Regex("(?i)\\bfilm\\b").containsMatchIn(rawQuery)

    val searchBody = ariesTmdbGet(
        "search/multi",
        "&query=" + URLEncoder.encode(cleaned, "UTF-8"),
        key
    ) ?: return null
    val arr = JSONObject(searchBody).optJSONArray("results") ?: return null

    var preferred: JSONObject? = null
    var fallback: JSONObject? = null
    for (i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        val type = o.optString("media_type")
        if (type != "tv" && type != "movie") continue
        if (fallback == null) fallback = o
        if ((wantsTv && type == "tv") || (wantsMovie && type == "movie")) {
            preferred = o
            break
        }
    }
    val chosen = preferred ?: fallback ?: return null
    val id = chosen.optInt("id")
    val isTv = chosen.optString("media_type") == "tv"

    val detailBody = ariesTmdbGet(if (isTv) "tv/$id" else "movie/$id", "", key) ?: return null
    val d = JSONObject(detailBody)

    val genreArr = d.optJSONArray("genres")
    val genres = buildList {
        if (genreArr != null) for (i in 0 until genreArr.length()) {
            add(genreArr.getJSONObject(i).optString("name"))
        }
    }.filter { it.isNotBlank() }.take(3).joinToString(" · ")

    val seasonArr = d.optJSONArray("seasons")
    val seasons = buildList {
        if (seasonArr != null) for (i in 0 until seasonArr.length()) {
            val s = seasonArr.getJSONObject(i)
            val number = s.optInt("season_number")
            if (number > 0) add(AriesSeason(number, s.optInt("episode_count")))
        }
    }

    val date = if (isTv) d.optString("first_air_date") else d.optString("release_date")
    val poster = d.optString("poster_path").takeIf { it.isNotBlank() && it != "null" }
    val trailerUrl = runCatching {
        val videosBody = ariesTmdbGet(if (isTv) "tv/$id/videos" else "movie/$id/videos", "", key)
        val videos = videosBody?.let { JSONObject(it).optJSONArray("results") }
        if (videos == null) null else {
            val candidates = (0 until videos.length()).map { videos.getJSONObject(it) }
            val trailer = candidates.firstOrNull {
                it.optString("site", "").equals("YouTube", true) &&
                    it.optString("type", "").equals("Trailer", true) &&
                    it.optBoolean("official", false)
            } ?: candidates.firstOrNull {
                it.optString("site", "").equals("YouTube", true) &&
                    it.optString("type", "").equals("Trailer", true)
            }
            trailer?.optString("key")?.takeIf { !it.isNullOrBlank() && it != "null" }
                ?.let { "https://www.youtube-nocookie.com/embed/$it?autoplay=1&playsinline=1" }
        }
    }.getOrNull()
    val runtime = if (isTv) {
        d.optJSONArray("episode_run_time")?.optInt(0, 0) ?: 0
    } else d.optInt("runtime")

    return AriesTitleInfo(
        id = id,
        title = (if (isTv) d.optString("name") else d.optString("title")).ifBlank { cleaned },
        isTv = isTv,
        year = date.take(4),
        genres = genres,
        overview = d.optString("overview"),
        posterUrl = poster?.let { "https://image.tmdb.org/t/p/w342$it" },
        trailerUrl = trailerUrl,
        rating = d.optDouble("vote_average", 0.0),
        seasons = seasons,
        totalEpisodes = d.optInt("number_of_episodes"),
        runtimeMin = runtime,
        source = "tmdb"
    )
}

private fun AriesTitleInfo.toCatalogItem(query: String, requestedMovie: Boolean): Item {
    val isMovie = !isTv || requestedMovie
    val fallbackDate = year.takeIf { it.length == 4 }?.plus("-01-01")
    return Item(
        id = id,
        title = if (isMovie) title else null,
        name = if (isMovie) null else title,
        overview = overview,
        poster_path = posterUrl,
        vote_average = rating.takeIf { it > 0.0 },
        popularity = null,
        media_type = if (isMovie) "movie" else "tv",
        first_air_date = if (isMovie) null else fallbackDate,
        release_date = if (isMovie) fallbackDate else null,
        source = source,
        genres = genres.split(" · ").filter { it.isNotBlank() }
    )
}

private fun createWebQueryItem(query: String, requestedMovie: Boolean): Item {
    val clean = ariesCleanTitle(query).ifBlank { "İsimsiz içerik" }
    val rawId = clean.hashCode()
    val stableId = (if (rawId == Int.MIN_VALUE) 1 else kotlin.math.abs(rawId)).coerceAtLeast(1)
    return Item(
        id = stableId,
        title = if (requestedMovie) clean else null,
        name = if (requestedMovie) null else clean,
        overview = null,
        poster_path = null,
        vote_average = null,
        popularity = null,
        media_type = if (requestedMovie) "movie" else "tv",
        first_air_date = null,
        release_date = null,
        source = "web-query"
    )
}

private fun fetchAriesTvMazeTitleInfo(rawQuery: String): AriesTitleInfo? {
    val cleaned = rawQuery.replace(Regex("(?i)(\\s+(izle|dizi|film|full|hd))+$"), "").trim().ifBlank { rawQuery.trim() }
    val searchUrl = "https://api.tvmaze.com/search/shows?q=" + URLEncoder.encode(cleaned, "UTF-8")
    val search = (URL(searchUrl).openConnection() as HttpURLConnection).run {
        connectTimeout = 8000; readTimeout = 8000
        try { inputStream.bufferedReader().use { it.readText() } } finally { disconnect() }
    }
    val arr = org.json.JSONArray(search)
    if (arr.length() == 0) return null
    val normalized = cleaned.replace(Regex("[^\\p{L}0-9]"), "").lowercase()
    var chosen: JSONObject? = null
    for (i in 0 until arr.length()) {
        val candidate = arr.optJSONObject(i)?.optJSONObject("show") ?: continue
        if (chosen == null) chosen = candidate
        val name = candidate.optString("name").replace(Regex("[^\\p{L}0-9]"), "").lowercase()
        if (name == normalized || name.contains(normalized) || normalized.contains(name)) { chosen = candidate; break }
    }
    val show = chosen ?: return null
    val id = show.optInt("id", -1)
    if (id <= 0) return null
    val detailUrl = "https://api.tvmaze.com/shows/$id"
    val detailText = (URL(detailUrl).openConnection() as HttpURLConnection).run {
        connectTimeout = 8000; readTimeout = 8000
        try { inputStream.bufferedReader().use { it.readText() } } finally { disconnect() }
    }
    val d = JSONObject(detailText)
    val seasonUrl = "https://api.tvmaze.com/shows/$id/seasons"
    val seasonText = (URL(seasonUrl).openConnection() as HttpURLConnection).run {
        connectTimeout = 8000; readTimeout = 8000
        try { inputStream.bufferedReader().use { it.readText() } } finally { disconnect() }
    }
    val seasonArr = org.json.JSONArray(seasonText)
    val seasons = ArrayList<AriesSeason>()
    var totalEpisodes = 0
    for (i in 0 until seasonArr.length()) {
        val season = seasonArr.optJSONObject(i) ?: continue
        val number = season.optInt("number", 0)
        if (number <= 0) continue
        val count = season.optInt("episodeOrder", 0)
        seasons.add(AriesSeason(number, count))
        totalEpisodes += count
    }
    val image = d.optJSONObject("image")
    val poster = image?.optString("medium")?.takeIf { !it.isNullOrBlank() && it != "null" }
    val genresArray = d.optJSONArray("genres")
    val genres = buildList {
        if (genresArray != null) for (i in 0 until genresArray.length()) {
            genresArray.optString(i).takeIf { it.isNotBlank() }?.let(::add)
        }
    }.take(3).joinToString(" · ")
    val date = d.optString("premiered").takeIf { it != "null" }.orEmpty()
    val overview = d.optString("summary").replace(Regex("<[^>]*>"), " ").replace(Regex("\\s+"), " ").trim()
    return AriesTitleInfo(
        id = id, title = d.optString("name").ifBlank { cleaned }, isTv = true,
        year = date.take(4), genres = genres, overview = overview, posterUrl = poster,
        trailerUrl = null, rating = d.optJSONObject("rating")?.optDouble("average", 0.0) ?: 0.0,
        seasons = seasons, totalEpisodes = totalEpisodes, runtimeMin = d.optInt("runtime", 0),
        source = "tvmaze"
    )
}

@SuppressLint("SetJavaScriptEnabled")
private fun createAriesSearchWebView(
    context: android.content.Context,
    onTitle: (String) -> Unit,
    onUrl: (String) -> Unit,
    onLoading: (Boolean) -> Unit,
    onStatus: (String) -> Unit,
    onResult: (AriesSiteResult) -> Unit,
    onSearchFinished: () -> Unit
): WebView {
    val mainHandler = Handler(Looper.getMainLooper())
    return WebView(context).apply {
        configureAriesWebView()
        addJavascriptInterface(object {
            @JavascriptInterface
            fun add(title: String, url: String, snippet: String) {
                if (!url.startsWith("http://") && !url.startsWith("https://")) return
                if (hostOfAries(url).contains("duckduckgo.com")) return
                val result = AriesSiteResult(
                    title = title.take(180).ifBlank { hostOfAries(url) },
                    url = url.take(2000),
                    snippet = snippet.take(400)
                )
                mainHandler.post { onResult(result) }
            }

            @JavascriptInterface
            fun done() { mainHandler.post { onSearchFinished() } }
        }, "AriesSearchBridge")

        webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                if (!title.isNullOrBlank()) onTitle(title)
            }
        }
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                onLoading(true)
                if (!url.isNullOrBlank()) onUrl(url)
            }

            override fun onPageFinished(view: WebView, url: String?) {
                onLoading(false)
                if (!url.isNullOrBlank()) onUrl(url)
                onStatus("Arama sonuçları alınıyor…")
                val js = """
                    (function() {
                      function txt(el) {
                        return (el && (el.innerText || el.textContent) || '').trim();
                      }
                      var anchors = document.querySelectorAll('a.result__a, a[data-testid="result-title-a"]');
                      anchors.forEach(function(a) {
                        var u = a.href || '';
                        try {
                          var parsed = new URL(u, location.href);
                          if (parsed.searchParams.has('uddg')) u = parsed.searchParams.get('uddg');
                          else u = parsed.href;
                        } catch(e) {}
                        if (!/^https?:\/\//i.test(u)) return;
                        var row = a.closest('.result') || a.closest('[data-testid="result"]') || a.parentElement;
                        var sn = row ? row.querySelector('.result__snippet, [data-result="snippet"]') : null;
                        AriesSearchBridge.add(txt(a), u, txt(sn));
                      });
                      AriesSearchBridge.done();
                    })();
                """.trimIndent()
                view.evaluateJavascript(js, null)
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private val ARIES_MEDIA_SNIFFER_SCRIPT = """
    (function () {
      if (window.__ariesSnifferInstalled) return;
      window.__ariesSnifferInstalled = true;

      var candidates = [];
      var flushTimer = null;
      var sentUrls = Object.create(null);

      function headersObject(source) {
        var out = {};
        try {
          if (!source) return out;
          if (typeof source.forEach === 'function') {
            source.forEach(function (value, key) { out[String(key)] = String(value); });
          } else if (Array.isArray(source)) {
            source.forEach(function (pair) { if (pair && pair.length > 1) out[String(pair[0])] = String(pair[1]); });
          } else if (typeof source === 'object') {
            Object.keys(source).forEach(function (key) { out[key] = String(source[key]); });
          }
        } catch (e) {}
        return out;
      }

      function absolute(raw) {
        try { return new URL(String(raw || ''), location.href).href; }
        catch (e) { return ''; }
      }

      function score(url, mime) {
        var u = String(url || '').toLowerCase();
        var m = String(mime || '').toLowerCase();
        if (u.indexOf('.m3u8') >= 0 || m.indexOf('mpegurl') >= 0) return 100;
        if (u.indexOf('.mpd') >= 0 || m.indexOf('dash+xml') >= 0) return 95;
        if (u.indexOf('.mp4') >= 0 || u.indexOf('.m4v') >= 0 || m.indexOf('video/mp4') >= 0) return 85;
        if (u.indexOf('.webm') >= 0 || m.indexOf('video/webm') >= 0) return 80;
        if (u.indexOf('.mov') >= 0 || m.indexOf('video/quicktime') >= 0) return 75;
        return 0;
      }

      function queue(raw, mime, requestHeaders) {
        var url = absolute(raw);
        if (!url || !/^https?:\/\//i.test(url)) return;
        var s = score(url, mime);
        if (!s) return;
        var previous = candidates.find(function (item) { return item.url === url; });
        if (previous) {
          if (s > previous.score) { previous.score = s; previous.mime = mime || previous.mime; }
          if (requestHeaders) previous.headers = Object.assign(previous.headers || {}, requestHeaders);
        } else {
          candidates.push({ url: url, mime: mime || '', headers: requestHeaders || {}, score: s });
        }
        if (flushTimer) clearTimeout(flushTimer);
        flushTimer = setTimeout(flush, 1400);
      }

      function flush() {
        if (!candidates.length) return;
        candidates.sort(function (a, b) { return b.score - a.score; });
        candidates.forEach(function (item) {
          if (sentUrls[item.url]) return;
          sentUrls[item.url] = true;
          try {
            if (window.AriesMediaBridge && window.AriesMediaBridge.found) {
              window.AriesMediaBridge.found(item.url, item.mime || '', JSON.stringify(item.headers || {}));
            }
          } catch (e) {}
        });
      }

      function inspectBody(text, url, requestHeaders) {
        if (!text) return;
        var body = String(text).slice(0, 4096).replace(/^\s+/, '');
        if (body.indexOf('#EXTM3U') === 0) queue(url, 'application/vnd.apple.mpegurl', requestHeaders);
        else if (/<MPD(?:\s|>)/i.test(body)) queue(url, 'application/dash+xml', requestHeaders);
        var bodyText = String(text).slice(0, 1000000);
        var regex = /(?:https?:)?\/\/[^"'\\\s<>]+?\.(?:m3u8|mpd|mp4)(?:\?[^"'\\\s<>]*)?|(?:\.\.\/|\.\/|\/)?[A-Za-z0-9_~%+./-]+\.(?:m3u8|mpd|mp4)(?:\?[^"'\\\s<>]*)?/ig;
        var match;
        while ((match = regex.exec(bodyText)) !== null) {
          try { queue(new URL(match[0], url || location.href).href, '', requestHeaders); } catch (e) {}
        }
      }

      var startAttempted = typeof WeakSet !== 'undefined' ? new WeakSet() : null;
      var startSelectors = [
        '.vjs-big-play-button',
        '.plyr__control--overlaid',
        '[data-plyr="play"]',
        '.jw-display-icon-container',
        '.jw-icon-playback',
        '.html5-video-player .ytp-large-play-button',
        'button[aria-label*="play" i]',
        '[role="button"][aria-label*="play" i]'
      ];

      function visiblePlayerControl(element) {
        if (!element || !element.getBoundingClientRect) return false;
        var rect = element.getBoundingClientRect();
        var style = window.getComputedStyle(element);
        return rect.width >= 20 && rect.height >= 20 &&
          style.display !== 'none' && style.visibility !== 'hidden' &&
          Number(style.opacity || 1) > 0 && rect.bottom > 0 && rect.right > 0 &&
          rect.top < window.innerHeight && rect.left < window.innerWidth;
      }

      function startVisiblePlayers() {
        try {
          document.querySelectorAll('video').forEach(function (video) {
            if (startAttempted && startAttempted.has(video)) return;
            if (!video.paused) {
              if (startAttempted) startAttempted.add(video);
              return;
            }
            if (video.readyState < 1) return;
            if (startAttempted) startAttempted.add(video);
            var result = video.play();
            if (result && result.catch) result.catch(function () {});
          });

          for (var i = 0; i < startSelectors.length; i++) {
            var controls = document.querySelectorAll(startSelectors[i]);
            for (var j = 0; j < controls.length; j++) {
              var control = controls[j];
              if (startAttempted && startAttempted.has(control)) continue;
              if (!visiblePlayerControl(control)) continue;
              if (startAttempted) startAttempted.add(control);
              try { control.click(); } catch (e) {}
            }
          }
        } catch (e) {}
      }

      function scanDom() {
        try {
          document.querySelectorAll('video, audio, source').forEach(function (element) {
            var raw = element.currentSrc || element.src || element.getAttribute('src') || '';
            queue(raw, element.getAttribute('type') || '', {});
          });
          performance.getEntriesByType('resource').forEach(function (entry) { queue(entry.name, '', {}); });
          startVisiblePlayers();
        } catch (e) {}
      }

      try {
        var originalFetch = window.fetch;
        if (originalFetch) {
          window.fetch = function (input, init) {
            var url = '';
            var requestHeaders = {};
            try {
              url = typeof input === 'string' ? input : (input && input.url) || '';
              requestHeaders = headersObject((init && init.headers) || (input && input.headers));
            } catch (e) {}
            return originalFetch.apply(this, arguments).then(function (response) {
              try {
                var contentType = response.headers.get('content-type') || '';
                var responseUrl = response.url || url;
                if (score(responseUrl, contentType) > 0) {
                  queue(responseUrl, contentType, requestHeaders);
                } else if (/text|json|xml|mpegurl|dash\+xml|octet-stream/i.test(contentType)) {
                  var clone = response.clone();
                  var reader = clone.body && clone.body.getReader ? clone.body.getReader() : null;
                  if (reader) {
                    reader.read().then(function (part) {
                      if (part && part.value) {
                        inspectBody(new TextDecoder().decode(part.value.slice(0, 65536)), responseUrl, requestHeaders);
                      }
                      try { reader.cancel(); } catch (e) {}
                    }).catch(function () {});
                  } else {
                    clone.text().then(function (body) {
                      inspectBody(String(body).slice(0, 65536), responseUrl, requestHeaders);
                    }).catch(function () {});
                  }
                }
              } catch (e) {}
              return response;
            });
          };
        }
      } catch (e) {}

      try {
        var originalOpen = XMLHttpRequest.prototype.open;
        var originalHeader = XMLHttpRequest.prototype.setRequestHeader;
        var originalSend = XMLHttpRequest.prototype.send;
        XMLHttpRequest.prototype.open = function (method, url) {
          this.__ariesUrl = absolute(url);
          this.__ariesHeaders = {};
          return originalOpen.apply(this, arguments);
        };
        XMLHttpRequest.prototype.setRequestHeader = function (name, value) {
          try { this.__ariesHeaders[String(name)] = String(value); } catch (e) {}
          return originalHeader.apply(this, arguments);
        };
        XMLHttpRequest.prototype.send = function () {
          this.addEventListener('loadend', function () {
            try {
              var contentType = this.getResponseHeader('content-type') || '';
              var responseUrl = this.responseURL || this.__ariesUrl || '';
              if (this.status < 200 || this.status >= 400) return;
              if (score(responseUrl, contentType) > 0) {
                queue(responseUrl, contentType, this.__ariesHeaders || {});
                return;
              }
              var responseType = this.responseType || '';
              if (responseType === '' || responseType === 'text') {
                inspectBody(this.responseText || '', responseUrl, this.__ariesHeaders || {});
              } else if (responseType === 'arraybuffer' && this.response && this.response.byteLength > 0 && this.response.byteLength <= 4194304) {
                var bytes = new Uint8Array(this.response, 0, Math.min(65536, this.response.byteLength));
                inspectBody(new TextDecoder().decode(bytes), responseUrl, this.__ariesHeaders || {});
              } else if (responseType === 'blob' && this.response && this.response.size > 0 && this.response.size <= 4194304) {
                this.response.slice(0, 65536).text().then((body) => {
                  inspectBody(body, responseUrl, this.__ariesHeaders || {});
                }).catch(function () {});
              } else if (responseType === 'json' && this.response) {
                inspectBody(JSON.stringify(this.response), responseUrl, this.__ariesHeaders || {});
              }
            } catch (e) {}
          });
          return originalSend.apply(this, arguments);
        };
      } catch (e) {}

      if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', scanDom, { once: true });
      } else {
        scanDom();
      }
      // Players often attach their source only after an explicit play gesture.
      // Re-scan on media lifecycle events without clicking page links or ads.
      ['play', 'playing', 'loadedmetadata', 'canplay', 'durationchange'].forEach(function (eventName) {
        document.addEventListener(eventName, function () { scanDom(); }, true);
      });
      try {
        new MutationObserver(scanDom).observe(document, { childList: true, subtree: true, attributes: true, attributeFilter: ['src'] });
      } catch (e) {}
      setInterval(scanDom, 1800);
      window.__ariesScanMedia = scanDom;
    })();
    """.trimIndent()

private fun createAriesSiteWebView(
    context: android.content.Context,
    onTitle: (String) -> Unit,
    onUrl: (String) -> Unit,
    onLoading: (Boolean) -> Unit,
    onStatus: (String) -> Unit,
    onShowCustom: (View, WebChromeClient.CustomViewCallback) -> Unit,
    onHideCustom: () -> Unit,
    onMediaFound: ((String, String?, Map<String, String>) -> Unit)? = null
): WebView {
    val mainHandler = Handler(Looper.getMainLooper())
    return WebView(context).apply {
        configureAriesWebView()
        settings.mediaPlaybackRequiresUserGesture = false
        addJavascriptInterface(object {
            @JavascriptInterface
            fun found(rawUrl: String, contentType: String?, rawHeaders: String?) {
                val candidate = rawUrl.trim()
                val mime = contentType?.substringBefore(";")?.trim()?.lowercase()
                val requestHeaders = runCatching {
                    val json = JSONObject(rawHeaders.orEmpty())
                    buildMap {
                        val keys = json.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val value = json.optString(key)
                            if (isAriesForwardableHeader(key) && value.isNotBlank()) put(key, value)
                        }
                    }
                }.getOrDefault(emptyMap())
                if (isSupportedMediaUrl(candidate) || isAriesMediaMime(mime)) {
                    mainHandler.post { onMediaFound?.invoke(candidate, mime, requestHeaders) }
                }
            }
        }, "AriesMediaBridge")

        // Install before site scripts run. onPageFinished-only injection misses
        // players that request their manifests during initial page startup.
        val documentStartInstalled = runCatching {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
                WebViewCompat.addDocumentStartJavaScript(this, ARIES_MEDIA_SNIFFER_SCRIPT, setOf("*"))
                true
            } else false
        }.getOrDefault(false)

        webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                if (!title.isNullOrBlank()) onTitle(title)
            }

            override fun onShowCustomView(view: View?, callback: WebChromeClient.CustomViewCallback?) {
                if (view != null && callback != null) onShowCustom(view, callback)
                else callback?.onCustomViewHidden()
            }

            override fun onHideCustomView() {
                onHideCustom()
            }
        }
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                onLoading(true)
                if (!url.isNullOrBlank()) onUrl(url)
                if (!documentStartInstalled) view?.evaluateJavascript(ARIES_MEDIA_SNIFFER_SCRIPT, null)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                onLoading(false)
                onStatus("Sayfanın medya istekleri taranıyor…")
                if (!url.isNullOrBlank()) onUrl(url)
                // Also run a DOM/resource scan after load; network hooks were installed at document start.
                view?.evaluateJavascript("window.__ariesScanMedia && window.__ariesScanMedia();", null)
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): android.webkit.WebResourceResponse? {
                val candidate = request?.url?.toString().orEmpty()
                val requestHeaders = request?.requestHeaders.orEmpty()
                    .filterKeys { isAriesForwardableHeader(it) }
                if (isSupportedMediaUrl(candidate)) {
                    mainHandler.post {
                        onMediaFound?.invoke(candidate, inferAriesMimeType(candidate), requestHeaders)
                    }
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                val candidate = url.orEmpty()
                if (isSupportedMediaUrl(candidate)) {
                    mainHandler.post {
                        onMediaFound?.invoke(candidate, inferAriesMimeType(candidate), emptyMap())
                    }
                }
                super.onLoadResource(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val scheme = request?.url?.scheme ?: return false
                return scheme != "http" && scheme != "https"
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.configureAriesWebView() {
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.loadsImagesAutomatically = true
    settings.useWideViewPort = true
    settings.loadWithOverviewMode = true
    settings.mediaPlaybackRequiresUserGesture = true
    settings.javaScriptCanOpenWindowsAutomatically = true
    settings.setSupportMultipleWindows(false)
    settings.allowFileAccess = false
    settings.allowContentAccess = true
    settings.userAgentString =
        "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
    CookieManager.getInstance().setAcceptCookie(true)
    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
}

private fun isAriesDiscoveryOnlyHost(url: String): Boolean {
    val host = runCatching { Uri.parse(url).host.orEmpty().lowercase().removePrefix("www.") }.getOrDefault("")
    return listOf(
        "justwatch.com", "themoviedb.org", "imdb.com", "tvmaze.com",
        "thetvdb.com", "wikipedia.org", "rottentomatoes.com",
        "metacritic.com", "trakt.tv", "letterboxd.com"
    ).any { host == it || host.endsWith(".$it") }
}

private fun hostOfAries(url: String): String =
    runCatching { Uri.parse(url).host?.removePrefix("www.") ?: url }.getOrDefault(url)

private fun Context.findAriesActivity(): Activity? {
    var current: Context = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

private fun fetchAriesSimilar(id: Int, isTv: Boolean, key: String): List<AriesSimilar> {
    val body = ariesTmdbGet(
        if (isTv) "tv/$id/recommendations" else "movie/$id/recommendations",
        "",
        key
    ) ?: return emptyList()
    val arr = JSONObject(body).optJSONArray("results") ?: return emptyList()
    val list = ArrayList<AriesSimilar>()
    for (i in 0 until minOf(arr.length(), 12)) {
        val o = arr.getJSONObject(i)
        val name = if (isTv) o.optString("name") else o.optString("title")
        if (name.isBlank()) continue
        val poster = o.optString("poster_path").takeIf { it.isNotBlank() && it != "null" }
        list.add(AriesSimilar(name, poster?.let { "https://image.tmdb.org/t/p/w342$it" }, isTv))
    }
    return list
}
private fun isAriesForwardableHeader(name: String): Boolean {
    val key = name.trim().lowercase()
    return key in setOf(
        "authorization", "cookie", "referer", "origin", "user-agent",
        "accept", "x-api-key", "x-auth-token", "x-requested-with"
    ) || (key.startsWith("x-") && key.length <= 64)
}

private fun isAriesMediaMime(mime: String?): Boolean =
    !mime.isNullOrBlank() && (
        mime.contains("mpegurl") || mime.contains("vnd.apple.mpegurl") ||
            mime.contains("dash+xml") || mime.contains("video/mp4") ||
            mime.contains("video/webm") || mime.contains("video/quicktime")
    )

private fun inferAriesMimeType(url: String, responseType: String? = null): String? {
    val mime = responseType?.lowercase().orEmpty()
    return when {
        mime.contains("mpegurl") || mime.contains("vnd.apple.mpegurl") -> "application/x-mpegURL"
        mime.contains("dash+xml") -> "application/dash+xml"
        mime.contains("video/mp4") -> "video/mp4"
        mime.contains("video/webm") -> "video/webm"
        mime.contains("video/quicktime") -> "video/quicktime"
        else -> {
            val path = runCatching { Uri.parse(url).path.orEmpty().lowercase() }.getOrDefault("")
            when {
                path.endsWith(".m3u8") -> "application/x-mpegURL"
                path.endsWith(".mpd") -> "application/dash+xml"
                path.endsWith(".mp4") || path.endsWith(".m4v") -> "video/mp4"
                path.endsWith(".webm") -> "video/webm"
                path.endsWith(".mov") -> "video/quicktime"
                else -> null
            }
        }
    }
}

private fun ariesMediaCandidateScore(url: String, mime: String?): Int {
    val path = runCatching { Uri.parse(url).path.orEmpty().lowercase() }.getOrDefault("")
    val type = mime.orEmpty().lowercase()
    return when {
        path.endsWith("master.m3u8") -> 110
        path.endsWith(".m3u8") || type.contains("mpegurl") -> 100
        path.endsWith(".mpd") || type.contains("dash+xml") -> 95
        path.endsWith(".mp4") || path.endsWith(".m4v") || type.contains("video/mp4") -> 85
        path.endsWith(".webm") || type.contains("video/webm") -> 80
        path.endsWith(".mov") || type.contains("video/quicktime") -> 75
        path.endsWith(".mkv") -> 70
        else -> 0
    }
}

private fun isSupportedMediaUrl(url: String): Boolean {
    if (!url.startsWith("https://", ignoreCase = true) && !url.startsWith("http://", ignoreCase = true)) return false
    val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return false
    val path = uri.path.orEmpty().lowercase()
    if (listOf(".m3u8", ".mp4", ".mpd", ".webm", ".m4v", ".mov", ".mkv").any { path.endsWith(it) }) return true
    val format = listOf("format", "type", "mime", "ext", "output").mapNotNull { uri.getQueryParameter(it) }
        .joinToString(" ").lowercase()
    return listOf("m3u8", "mpd", "mpegurl", "dash+xml", "video/mp4", "video/webm", "video/quicktime")
        .any { format.contains(it) }
}
