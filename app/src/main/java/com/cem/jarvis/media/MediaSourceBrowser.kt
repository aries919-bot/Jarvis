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

// Arama ekranı: Seekee gri teması
private val AriesBg = Color(0xFF1E1E1E)
private val AriesCard = Color(0xFF2B2B2B)
private val AriesChip = Color(0xFF3C3C3C)
private val AriesText = Color.White
private val AriesBody = Color(0xFFE0E0E0)
private val AriesMuted = Color(0xFFB0B0B0)

// İzleme sayfası: koyu tema
private val AriesWatchBg = Color(0xFF0B0B0B)
private val AriesWatchCard = Color(0xFF1C1C1E)
private val AriesWatchChip = Color(0xFF262626)
private val AriesWatchSel = Color(0xFF6E6E6E)

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
 * Katalog bilgisi ayrı, web sayfası ayrı açılır; doğrudan medya bağlantısı kullanıcı tarafından girilir.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MediaSourceBrowser(
    query: String,
    onBack: () -> Unit,
    onPlayMedia: (String, Item, Int, Int) -> Unit
) {
    var searchText by remember(query) { mutableStateOf(query) }
    var activeQuery by remember(query) { mutableStateOf(query) }
    var webTerm by remember(query) { mutableStateOf(query) }
    var activeSite by remember { mutableStateOf<AriesSiteResult?>(null) }
    var personalMediaUrl by remember { mutableStateOf("") }
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
    val attemptedSourceUrls = remember { mutableStateListOf<String>() }
    var fullscreenView by remember { mutableStateOf<View?>(null) }
    var fullscreenCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }
    val hostContext = LocalContext.current

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
        runWebSearch(term)
    }

    fun openSite(site: AriesSiteResult) {
        trailerPlaying = false
        playerError = null
        autoPlayedMediaUrl = null
        attemptedSourceUrls.add(site.url)
        activeSite = site
        pageTitle = site.title
        pageUrl = site.url
        status = "Kaynak taranıyor: ${site.title}"
    }

    fun openNextAutoSource() {
        val next = results.firstOrNull { it.url !in attemptedSourceUrls }
        if (next != null) openSite(next)
        else {
            activeSite = null
            status = "İlk kaynaklarda doğrudan video bulunamadı. Başka bir kaynak seçebilir veya web sayfasında izleyebilirsin."
        }
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
        attemptedSourceUrls.clear()
        runWebSearch(ariesWatchQuery(next))
    }

    LaunchedEffect(activeSite?.url, watching, autoPlayedMediaUrl, results.size) {
        val current = activeSite
        if (watching != null && current != null && autoPlayedMediaUrl == null) {
            kotlinx.coroutines.delay(12000)
            if (activeSite?.url == current.url && autoPlayedMediaUrl == null) {
                status = "Bu kaynakta doğrudan akış bulunamadı; sıradaki kaynak deneniyor…"
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

    Box(Modifier.fillMaxSize()) {
        val currentWatch = watching
        if (currentWatch != null) {
            AriesWatchScreen(
                watch = currentWatch,
                sources = results.toList(),
                searching = searching,
                currentUrl = activeSite?.url,
                personalMediaUrl = personalMediaUrl,
                onPersonalMediaUrlChange = { personalMediaUrl = it },
                playerError = playerError,
                onPlayPersonalMedia = {
                    if (isSupportedMediaUrl(personalMediaUrl)) {
                        val playableItem = info?.toCatalogItem(activeQuery, currentWatch.isMovie)
                            ?: createWebQueryItem(activeQuery, currentWatch.isMovie)
                        onPlayMedia(
                            personalMediaUrl.trim(),
                            playableItem,
                            currentWatch.season ?: 0,
                            currentWatch.episode
                        )
                        playerError = null
                    } else {
                        playerError = "Geçerli bir HTTPS MP4, M3U8 veya MPD bağlantısı gir."
                    }
                },
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
                            onMediaFound = { mediaUrl ->
                                if (autoPlayedMediaUrl != mediaUrl && isSupportedMediaUrl(mediaUrl)) {
                                    autoPlayedMediaUrl = mediaUrl
                                    val playableItem = info?.toCatalogItem(activeQuery, currentWatch.isMovie)
                                        ?: createWebQueryItem(activeQuery, currentWatch.isMovie)
                                    onPlayMedia(mediaUrl, playableItem, currentWatch.season ?: 0, currentWatch.episode)
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
                            else "Bir kaynak seç veya kendi doğrudan medya bağlantını gir.",
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
    onMediaFound: ((String) -> Unit)? = null
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
    personalMediaUrl: String,
    onPersonalMediaUrlChange: (String) -> Unit,
    playerError: String?,
    onPlayPersonalMedia: () -> Unit,
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
            item(key = "w-own-media") {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(AriesWatchCard).padding(14.dp)
                ) {
                    Text("Kendi medya bağlantın", color = AriesText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Kullanım iznin olan MP4 / M3U8 / MPD bağlantıları oynatılır. Web sayfalarından video çıkarılmaz.",
                        color = AriesMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 10.dp))
                    OutlinedTextField(
                        value = personalMediaUrl,
                        onValueChange = onPersonalMediaUrlChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("https://…") },
                        placeholder = { Text("video.mp4 veya yayın.m3u8") }
                    )
                    if (!playerError.isNullOrBlank()) {
                        Text(playerError, color = Color(0xFFFF8A7A), fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                    }
                    Button(onClick = onPlayPersonalMedia, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("▶  Bağlantıyı oynat")
                    }
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
private fun createAriesSiteWebView(
    context: android.content.Context,
    onTitle: (String) -> Unit,
    onUrl: (String) -> Unit,
    onLoading: (Boolean) -> Unit,
    onStatus: (String) -> Unit,
    onShowCustom: (View, WebChromeClient.CustomViewCallback) -> Unit,
    onHideCustom: () -> Unit,
    onMediaFound: ((String) -> Unit)? = null
): WebView {
    val mainHandler = Handler(Looper.getMainLooper())
    return WebView(context).apply {
        configureAriesWebView()
        addJavascriptInterface(object {
            @JavascriptInterface
            fun found(rawUrl: String) {
                val candidate = rawUrl.trim()
                if (isSupportedMediaUrl(candidate)) mainHandler.post { onMediaFound?.invoke(candidate) }
            }
        }, "AriesMediaBridge")
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
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                onLoading(false)
                onStatus("Sayfa taranıyor; doğrudan MP4 / M3U8 / MPD akışı aranıyor…")
                if (!url.isNullOrBlank()) onUrl(url)
                val scanScript = """
                    (function() {
                      function scan() {
                        document.querySelectorAll('video, audio, source').forEach(function(el) {
                          var raw = el.currentSrc || el.src || el.getAttribute('src') || '';
                          if (!raw) return;
                          try { raw = new URL(raw, location.href).href; } catch(e) { return; }
                          if (/^https:\/\//i.test(raw) && /\.(mp4|m3u8|mpd|webm|m4v|mov)(?:$|[?#])/i.test(raw)) {
                            AriesMediaBridge.found(raw);
                          }
                        });
                      }
                      scan();
                      if (!window.__ariesMediaObserver) {
                        window.__ariesMediaObserver = new MutationObserver(scan);
                        window.__ariesMediaObserver.observe(document.documentElement, {childList:true,subtree:true,attributes:true,attributeFilter:['src']});
                        setTimeout(scan, 1000); setTimeout(scan, 3000); setTimeout(scan, 7000);
                      }
                    })();
                """.trimIndent()
                view?.evaluateJavascript(scanScript, null)
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): android.webkit.WebResourceResponse? {
                val candidate = request?.url?.toString().orEmpty()
                if (isSupportedMediaUrl(candidate)) mainHandler.post { onMediaFound?.invoke(candidate) }
                return super.shouldInterceptRequest(view, request)
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
private fun isSupportedMediaUrl(url: String): Boolean {
    if (!url.startsWith("https://", ignoreCase = true) && !url.startsWith("http://", ignoreCase = true)) return false
    val path = runCatching { Uri.parse(url).path.orEmpty().lowercase() }.getOrDefault(url.lowercase())
    return listOf(".m3u8", ".mp4", ".mpd", ".webm", ".m4v", ".mov", ".mkv").any { path.endsWith(it) }
}
