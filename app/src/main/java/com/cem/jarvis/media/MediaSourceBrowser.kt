package com.cem.jarvis.media

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.View
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

private data class AriesTitleInfo(
    val title: String,
    val isTv: Boolean,
    val year: String,
    val genres: String,
    val overview: String,
    val posterUrl: String?,
    val rating: Double,
    val seasons: List<AriesSeason>,
    val totalEpisodes: Int,
    val runtimeMin: Int
)

// Aries teması (uygulama ekran görüntülerinden)
private val AriesBg = Color(0xFF0A0E17)
private val AriesCard = Color(0xFF161B28)
private val AriesChip = Color(0xFF222838)
private val AriesAccent = Color(0xFF93B0FF)
private val AriesMuted = Color(0xFF9AA3B5)
private val AriesText = Color(0xFFE6E9F0)
private val AriesFire = Color(0xFFFFA726)

// TMDB anahtarı projede zaten varsa otomatik bulunur (BuildConfig).
// Bulunamazsa buraya yapıştır (v3 anahtarı veya v4 token olabilir).
private const val ARIES_TMDB_KEY_FALLBACK = ""

/**
 * Seekee tarzı arama ekranı + aranan dizi/filmin TMDB bilgi kartı.
 * Üstte kart (poster, sezon/bölüm sayısı, sezon çipleri), altta site sonuçları.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MediaSourceBrowser(query: String, onBack: () -> Unit) {
    var searchText by remember(query) { mutableStateOf(query) }
    var activeQuery by remember(query) { mutableStateOf(query) }
    var activeSite by remember { mutableStateOf<AriesSiteResult?>(null) }
    var pageTitle by remember { mutableStateOf("") }
    var pageUrl by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Film ve dizi siteleri aranıyor…") }
    var loading by remember { mutableStateOf(false) }
    val results = remember { mutableStateListOf<AriesSiteResult>() }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var info by remember { mutableStateOf<AriesTitleInfo?>(null) }
    var infoNote by remember { mutableStateOf("") }
    var fullscreenView by remember { mutableStateOf<View?>(null) }
    var fullscreenCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }
    val hostContext = LocalContext.current

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
        val key = ariesTmdbKey()
        if (key.isBlank()) {
            infoNote = "Dizi/film kartı için TMDB anahtarı bulunamadı."
            return@LaunchedEffect
        }
        val found = withContext(Dispatchers.IO) {
            runCatching { fetchAriesTitleInfo(activeQuery, key) }.getOrNull()
        }
        if (found == null) infoNote = "Bu arama için bilgi kartı bulunamadı." else info = found
    }

    fun startSearch(text: String) {
        val term = text.trim()
        if (term.isEmpty()) return
        searchText = term
        activeQuery = term
        activeSite = null
        val searchViewAttached = results.isEmpty()
        results.clear()
        loading = true
        status = "Farklı sitelerde arama yapılıyor…"
        if (searchViewAttached) {
            val encoded = URLEncoder.encode("$term izle film dizi", "UTF-8")
            webView?.loadUrl("https://html.duckduckgo.com/html/?q=$encoded")
        }
    }

    fun openSite(site: AriesSiteResult) {
        activeSite = site
        pageTitle = site.title
        pageUrl = site.url
        loading = true
        status = "Site açılıyor…"
    }

    BackHandler(enabled = activeSite != null) {
        webView?.stopLoading()
        activeSite = null
        status = if (results.isEmpty()) "Arama sonucu bekleniyor" else "${results.size} site bulundu"
    }

    BackHandler(enabled = fullscreenView != null) { exitFullscreen() }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.removeJavascriptInterface("AriesSearchBridge")
            webView?.destroy()
            webView = null
        }
    }

    Box(Modifier.fillMaxSize()) {
    Column(Modifier.fillMaxSize().background(AriesBg)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "‹",
                color = AriesText,
                fontSize = 32.sp,
                modifier = Modifier
                    .clickable {
                        if (activeSite != null) {
                            webView?.stopLoading()
                            activeSite = null
                        } else onBack()
                    }
                    .padding(end = 12.dp)
            )
            Column(Modifier.weight(1f)) {
                Text(
                    if (activeSite == null) "Ara" else pageTitle.ifBlank { "Site" },
                    color = AriesText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (activeSite != null) {
                    Text(
                        pageUrl,
                        color = AriesMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (activeSite != null) {
                Text(
                    "Yenile",
                    color = AriesAccent,
                    modifier = Modifier.clickable { webView?.reload() }.padding(6.dp)
                )
            }
        }

        if (activeSite == null) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("Film veya dizi adı") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { startSearch(searchText) })
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { startSearch(searchText) }) { Text("Ara") }
            }

            Text(
                status,
                color = AriesMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (results.isEmpty()) {
                Box(Modifier.padding(horizontal = 12.dp)) { AriesInfoBlock(info, infoNote) }
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            createAriesSearchWebView(
                                context = context,
                                onTitle = { pageTitle = it },
                                onUrl = { pageUrl = it },
                                onLoading = { loading = it },
                                onStatus = { status = it },
                                onResult = { result ->
                                    if (results.none { it.url == result.url }) results.add(result)
                                },
                                onSearchFinished = {
                                    loading = false
                                    status = if (results.isEmpty())
                                        "Bu aramada site sonucu alınamadı. Başka kelimeler deneyebilirsin."
                                    else "${results.size} site sonucu bulundu"
                                }
                            ).also {
                                webView = it
                                val encoded = URLEncoder.encode("$activeQuery izle film dizi", "UTF-8")
                                it.loadUrl("https://html.duckduckgo.com/html/?q=$encoded")
                            }
                        },
                        update = { webView = it }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item(key = "aries-info") { AriesInfoBlock(info, infoNote) }
                    items(results, key = { it.url }) { result ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AriesCard)
                                .clickable { openSite(result) }
                                .padding(14.dp)
                        ) {
                            Text(
                                result.title,
                                color = AriesText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (result.snippet.isNotBlank()) {
                                Text(
                                    result.snippet,
                                    color = AriesMuted,
                                    fontSize = 14.sp,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 5.dp)
                                )
                            }
                            Row(
                                Modifier.fillMaxWidth().padding(top = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    hostOfAries(result.url),
                                    color = AriesMuted,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "İzle  ›",
                                    color = Color(0xFF0A0E17),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AriesAccent)
                                        .padding(horizontal = 22.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                status,
                color = AriesMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
            AndroidView(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                factory = { context ->
                    createAriesSiteWebView(
                        context = context,
                        onTitle = { pageTitle = it },
                        onUrl = { pageUrl = it },
                        onLoading = { loading = it },
                        onStatus = { status = it },
                        onShowCustom = { view, callback ->
                            fullscreenCallback = callback
                            fullscreenView = view
                        },
                        onHideCustom = {
                            fullscreenCallback = null
                            fullscreenView = null
                        }
                    ).also {
                        webView = it
                        it.loadUrl(activeSite!!.url)
                    }
                },
                update = { webView = it }
            )
        }
    }
    fullscreenView?.let { view ->
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(modifier = Modifier.fillMaxSize(), factory = { view })
        }
    }
    }
}

@Composable
private fun AriesInfoBlock(info: AriesTitleInfo?, note: String) {
    if (info != null) {
        AriesTitleCard(info)
    } else if (note.isNotBlank()) {
        Text(note, color = AriesMuted, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
    }
}

@Composable
private fun AriesTitleCard(info: AriesTitleInfo) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF151B2E), Color(0xFF241A2E))))
            .padding(14.dp)
    ) {
        Row {
            AriesPoster(info.posterUrl, Modifier.width(96.dp).height(140.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    info.title,
                    color = AriesText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val meta = listOf(
                    info.year,
                    if (info.isTv) "TV Dizisi" else "Film",
                    info.genres
                ).filter { it.isNotBlank() }.joinToString(" · ")
                Text(
                    meta,
                    color = AriesMuted,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (info.isTv) {
                    val seasonCount = info.seasons.size
                    val episodeCount = if (info.totalEpisodes > 0) info.totalEpisodes
                    else info.seasons.sumOf { it.episodes }
                    Text(
                        "$seasonCount Sezon · $episodeCount Bölüm",
                        color = AriesAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else if (info.runtimeMin > 0) {
                    Text(
                        "${info.runtimeMin} dk",
                        color = AriesAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (info.rating > 0.0) {
                    Text(
                        "🔥 " + String.format("%.1f", info.rating),
                        color = AriesFire,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
        if (info.overview.isNotBlank()) {
            Text(
                info.overview,
                color = AriesMuted,
                fontSize = 13.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        if (info.isTv && info.seasons.isNotEmpty()) {
            Text(
                "Sezonlar",
                color = AriesText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(info.seasons, key = { it.number }) { season ->
                    Column(
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AriesChip)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${season.number}. Sezon",
                            color = AriesText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("${season.episodes} bölüm", color = AriesMuted, fontSize = 11.sp)
                    }
                }
            }
        }
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
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(AriesChip)) {
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
        if (!value.isNullOrBlank()) return value
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
    val runtime = if (isTv) {
        d.optJSONArray("episode_run_time")?.optInt(0, 0) ?: 0
    } else d.optInt("runtime")

    return AriesTitleInfo(
        title = (if (isTv) d.optString("name") else d.optString("title")).ifBlank { cleaned },
        isTv = isTv,
        year = date.take(4),
        genres = genres,
        overview = d.optString("overview"),
        posterUrl = poster?.let { "https://image.tmdb.org/t/p/w342$it" },
        rating = d.optDouble("vote_average", 0.0),
        seasons = seasons,
        totalEpisodes = d.optInt("number_of_episodes"),
        runtimeMin = runtime
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
    return WebView(context).apply {
        configureAriesWebView()
        addJavascriptInterface(object {
            @JavascriptInterface
            fun add(title: String, url: String, snippet: String) {
                if (!url.startsWith("http://") && !url.startsWith("https://")) return
                if (hostOfAries(url).contains("duckduckgo.com")) return
                onResult(
                    AriesSiteResult(
                        title = title.take(180).ifBlank { hostOfAries(url) },
                        url = url.take(2000),
                        snippet = snippet.take(400)
                    )
                )
            }

            @JavascriptInterface
            fun done() = onSearchFinished()
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
                      var rows = document.querySelectorAll('.result');
                      rows.forEach(function(row) {
                        var a = row.querySelector('a.result__a');
                        if (!a) return;
                        var u = a.href || '';
                        try {
                          var parsed = new URL(u);
                          if (parsed.searchParams.has('uddg')) u = parsed.searchParams.get('uddg');
                        } catch(e) {}
                        if (!/^https?:\/\//i.test(u)) return;
                        var sn = row.querySelector('.result__snippet');
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
    onHideCustom: () -> Unit
): WebView {
    return WebView(context).apply {
        configureAriesWebView()
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
                onStatus("Site açıldı. Sayfanın kendi oynatıcısını kullanabilirsin.")
                if (!url.isNullOrBlank()) onUrl(url)
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
