package com.cem.jarvis.media

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
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

// Seekee ekran görüntüsündeki gri tema
private val AriesBg = Color(0xFF1E1E1E)
private val AriesCard = Color(0xFF2B2B2B)
private val AriesChip = Color(0xFF3C3C3C)
private val AriesText = Color.White
private val AriesBody = Color(0xFFE0E0E0)
private val AriesMuted = Color(0xFFB0B0B0)

// TMDB anahtarı projede zaten varsa otomatik bulunur (BuildConfig).
// Bulunamazsa buraya yapıştır (v3 anahtarı veya v4 token olabilir).
private const val ARIES_TMDB_KEY_FALLBACK = ""

private fun ariesDdgUrl(term: String): String {
    val full = if (term.contains("izle", ignoreCase = true)) term else "$term izle"
    return "https://html.duckduckgo.com/html/?q=" + URLEncoder.encode(full, "UTF-8")
}

private fun ariesChipLabels(count: Int): List<String> {
    if (count <= 0) return emptyList()
    if (count <= 6) return (1..count).map { it.toString() }
    return listOf("1", "2", "…", (count - 1).toString(), count.toString())
}

/**
 * Seekee düzeninde arama ekranı: üstte TMDB'den sezon kartları (poster sağda,
 * bölüm çipleri, İzle düğmesi), altında web sonuçları.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MediaSourceBrowser(query: String, onBack: () -> Unit) {
    var searchText by remember(query) { mutableStateOf(query) }
    var activeQuery by remember(query) { mutableStateOf(query) }
    var webTerm by remember(query) { mutableStateOf(query) }
    var activeSite by remember { mutableStateOf<AriesSiteResult?>(null) }
    var pageTitle by remember { mutableStateOf("") }
    var pageUrl by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Film ve dizi siteleri aranıyor…") }
    var loading by remember { mutableStateOf(false) }
    val results = remember { mutableStateListOf<AriesSiteResult>() }
    var searchView by remember { mutableStateOf<WebView?>(null) }
    var siteView by remember { mutableStateOf<WebView?>(null) }
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

    fun runWebSearch(term: String) {
        webTerm = term
        results.clear()
        loading = true
        status = "\"$term\" için siteler aranıyor…"
        searchView?.loadUrl(ariesDdgUrl(term))
    }

    fun startSearch(text: String) {
        val term = text.trim()
        if (term.isEmpty()) return
        searchText = term
        activeQuery = term
        activeSite = null
        runWebSearch(term)
    }

    fun openSite(site: AriesSiteResult) {
        activeSite = site
        pageTitle = site.title
        pageUrl = site.url
        loading = true
        status = "Site açılıyor…"
    }

    BackHandler(enabled = activeSite != null) {
        siteView?.stopLoading()
        activeSite = null
        status = if (results.isEmpty()) "Arama sonucu bekleniyor" else "${results.size} site bulundu"
    }

    BackHandler(enabled = fullscreenView != null) { exitFullscreen() }

    DisposableEffect(Unit) {
        onDispose {
            siteView?.stopLoading()
            siteView?.destroy()
            siteView = null
            searchView?.stopLoading()
            searchView?.removeJavascriptInterface("AriesSearchBridge")
            searchView?.destroy()
            searchView = null
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(AriesBg)) {
            Row(
                Modifier.fillMaxWidth().background(AriesCard).padding(horizontal = 14.dp, vertical = 12.dp),
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

            if (activeSite == null) {
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
                    val currentInfo = info
                    if (currentInfo != null) {
                        item(key = "aries-summary") {
                            val summary = if (currentInfo.isTv) {
                                val eps = if (currentInfo.totalEpisodes > 0) currentInfo.totalEpisodes
                                else currentInfo.seasons.sumOf { it.episodes }
                                "${currentInfo.title} · ${currentInfo.seasons.size} Sezon · $eps Bölüm"
                            } else {
                                currentInfo.title +
                                    if (currentInfo.runtimeMin > 0) " · ${currentInfo.runtimeMin} dk" else ""
                            }
                            Text(
                                summary,
                                color = AriesText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                        if (currentInfo.isTv && currentInfo.seasons.isNotEmpty()) {
                            items(currentInfo.seasons, key = { "season-${it.number}" }) { season ->
                                AriesRichCard(
                                    info = currentInfo,
                                    season = season,
                                    onWatch = {
                                        runWebSearch("${currentInfo.title} ${season.number}. sezon")
                                    },
                                    onEpisode = { ep ->
                                        runWebSearch(
                                            "${currentInfo.title} ${season.number}. sezon $ep. bölüm"
                                        )
                                    }
                                )
                            }
                        } else {
                            item(key = "aries-single") {
                                AriesRichCard(
                                    info = currentInfo,
                                    season = null,
                                    onWatch = { runWebSearch("${currentInfo.title} film") },
                                    onEpisode = { }
                                )
                            }
                        }
                    } else if (infoNote.isNotBlank()) {
                        item(key = "aries-note") {
                            Text(
                                infoNote,
                                color = AriesMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                    items(results, key = { it.url }) { result ->
                        AriesWebCard(result) { openSite(result) }
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
                            siteView = it
                            it.loadUrl(activeSite!!.url)
                        }
                    },
                    update = { siteView = it }
                )
            }
        }

        // Arama sonuçlarını toplayan görünmez WebView (arama ekranının arkasında çalışır)
        Box(Modifier.size(1.dp)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    createAriesSearchWebView(
                        context = context,
                        onTitle = { },
                        onUrl = { },
                        onLoading = { loading = it },
                        onStatus = { },
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
            AriesPoster(info.posterUrl, Modifier.width(104.dp).height(156.dp))
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
                            .height(44.dp)
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
                .height(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AriesChip)
                .clickable { onWatch() },
            contentAlignment = Alignment.Center
        ) {
            Text("İzle  ›", color = AriesText, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
        AriesSourceRow("TMDB")
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
            name,
            color = AriesMuted,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text("•••", color = AriesMuted, fontSize = 16.sp)
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
