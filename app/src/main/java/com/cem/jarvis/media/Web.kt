package com.cem.jarvis.media

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.RenderProcessGoneDetail
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private const val TMDB_IMG = "https://image.tmdb.org/t/p/w342"
private val webHttp = OkHttpClient.Builder()
    .connectTimeout(8, TimeUnit.SECONDS)
    .readTimeout(12, TimeUnit.SECONDS)
    .callTimeout(15, TimeUnit.SECONDS)
    .build()

fun webImg(p: String?): String = when {
    p.isNullOrBlank() -> ""
    p.startsWith("http") -> p
    else -> TMDB_IMG + p
}

private fun getJson(url: String): String =
    webHttp.newCall(Request.Builder().url(url).header("User-Agent", "AriesApp/1.0").build())
        .execute().use { response ->
            if (!response.isSuccessful) throw java.io.IOException("HTTP " + response.code)
            response.body?.string().orEmpty()
        }

private fun showItem(sh: JSONObject): Item? {
    val im = sh.optJSONObject("image")?.optString("medium")
    if (im.isNullOrBlank()) return null
    val r = sh.optJSONObject("rating")?.optDouble("average") ?: Double.NaN
    return Item(sh.getInt("id"), null, sh.optString("name"),
        sh.optString("summary").replace(Regex("<[^>]*>"), ""), im,
        if (r.isNaN()) null else r, null, "tv", sh.optString("premiered"), null,
        source = "tvmaze")
}

suspend fun tvmazeSchedule(country: String): List<Item> = withContext(Dispatchers.IO) {
    val day = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val arr = JSONArray(getJson("https://api.tvmaze.com/schedule?country=$country&date=$day"))
    val seen = HashSet<Int>()
    val out = mutableListOf<Item>()
    for (i in 0 until arr.length()) {
        val sh = arr.getJSONObject(i).optJSONObject("show") ?: continue
        val it = showItem(sh) ?: continue
        if (seen.add(it.id)) out += it
        if (out.size >= 25) break
    }
    out
}

suspend fun webSearch(q: String, mediaType: String? = null): List<Item> = withContext(Dispatchers.IO) {
    val out = mutableListOf<Item>()
    if (mediaType != "movie") runCatching {
        val arr = JSONArray(getJson("https://api.tvmaze.com/search/shows?q=" + Uri.encode(q)))
        for (i in 0 until minOf(arr.length(), 10)) {
            showItem(arr.getJSONObject(i).getJSONObject("show"))?.let { out += it }
        }
    }
    runCatching {
        val u = "https://tr.wikipedia.org/w/api.php?action=query&generator=search&gsrlimit=8" +
            "&prop=pageimages%7Cextracts&exintro=1&explaintext=1&exlimit=max&piprop=thumbnail" +
            "&pithumbsize=342&format=json&gsrsearch=" + Uri.encode(q)
        val pages = JSONObject(getJson(u)).optJSONObject("query")?.optJSONObject("pages")
        pages?.keys()?.forEach { k ->
            val pg = pages.getJSONObject(k)
            val th = pg.optJSONObject("thumbnail")?.optString("source")
            if (!th.isNullOrBlank())
                out += Item(pg.getInt("pageid"),
                    if (mediaType == "movie") pg.optString("title") else null,
                    if (mediaType == "movie") null else pg.optString("title"),
                    pg.optString("extract"), th, null, null, mediaType ?: "tv", null, null,
                    source = "wikipedia")
        }
    }
    out
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebDialog(query: String, onClose: () -> Unit) {
    var text by remember { mutableStateOf(query) }
    var wv by remember { mutableStateOf<WebView?>(null) }
    var webViewGeneration by remember { mutableStateOf(0) }
    var custom by remember { mutableStateOf<View?>(null) }
    var cbk by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }
    fun go(t: String) {
        val u = t.trim()
        val url = if (u.startsWith("http://") || u.startsWith("https://")) u
            else if (u.contains(".") && !u.contains(" ")) "https://$u"
            else "https://www.google.com/search?q=" + Uri.encode(u)
        wv?.loadUrl(url)
    }
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✕", color = Color.White, fontSize = 22.sp, modifier = Modifier.clickable(onClick = onClose).padding(8.dp))
                    Text("‹", color = Color.White, fontSize = 26.sp, modifier = Modifier.clickable { wv?.goBack() }.padding(8.dp))
                    Text("›", color = Color.White, fontSize = 26.sp, modifier = Modifier.clickable { wv?.goForward() }.padding(8.dp))
                    Text("⟳", color = Color.White, fontSize = 22.sp, modifier = Modifier.clickable { wv?.reload() }.padding(8.dp))
                    TextField(text, { text = it }, Modifier.weight(1f), singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = { go(text) }))
                }
                key(webViewGeneration) {
                    AndroidView(modifier = Modifier.weight(1f).fillMaxWidth(), factory = { c ->
                    WebView(c).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(v: WebView?, url: String?) {
                                if (url != null) text = url
                            }

                            @RequiresApi(26)
                            override fun onRenderProcessGone(
                                view: WebView,
                                detail: RenderProcessGoneDetail
                            ): Boolean {
                                (view.parent as? android.view.ViewGroup)?.removeView(view)
                                view.stopLoading()
                                view.destroy()
                                wv = null
                                cbk?.onCustomViewHidden()
                                custom = null
                                cbk = null
                                text = "Web sayfası kapandı. Aramayı yeniden açabilirsin."
                                webViewGeneration++
                                return true
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) { custom = view; cbk = callback }
                            override fun onHideCustomView() { custom = null; cbk = null }
                        }
                        wv = this
                        loadUrl("https://www.google.com/search?q=" + Uri.encode(query))
                    }
                    })
                }
            }
            custom?.let { v ->
                AndroidView(modifier = Modifier.fillMaxSize(), factory = { c ->
                    FrameLayout(c).apply { setBackgroundColor(0xFF000000.toInt()); addView(v) }
                })
                Text("Küçült", color = Color.White, modifier = Modifier.padding(16.dp).clickable {
                    cbk?.onCustomViewHidden(); custom = null; cbk = null
                })
            }
        }
    }
}

suspend fun tvmazeSeasonCount(showId: Int): Int = withContext(Dispatchers.IO) {
    val arr = JSONArray(getJson("https://api.tvmaze.com/shows/$showId/seasons"))
    (0 until arr.length()).count { arr.optJSONObject(it)?.optInt("number", 0)?.let { n -> n > 0 } == true }
        .coerceAtLeast(1)
}

suspend fun tvmazeEpisodesForSeason(showId: Int, season: Int): List<Ep> = withContext(Dispatchers.IO) {
    val arr = JSONArray(getJson("https://api.tvmaze.com/shows/$showId/episodes?specials=0"))
    buildList {
        for (i in 0 until arr.length()) {
            val episode = arr.optJSONObject(i) ?: continue
            if (episode.optInt("season", -1) != season) continue
            val number = episode.optInt("number", 0)
            if (number <= 0) continue
            add(Ep(number, episode.optString("name").takeIf { it.isNotBlank() && it != "null" },
                episode.optInt("runtime", 0).takeIf { it > 0 }))
        }
    }.sortedBy { it.episode_number }
}

suspend fun tvmazePage(page: Int): List<Item> = withContext(Dispatchers.IO) {
    val arr = JSONArray(getJson("https://api.tvmaze.com/shows?page=$page"))
    val out = mutableListOf<Item>()
    for (i in 0 until arr.length()) {
        showItem(arr.getJSONObject(i))?.let { out += it }
        if (out.size >= 30) break
    }
    out.sortedByDescending { it.vote_average ?: 0.0 }
}
