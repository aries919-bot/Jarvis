package com.cem.jarvis.media

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
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

private const val TMDB_IMG = "https://image.tmdb.org/t/p/w342"
private val webHttp = OkHttpClient()

fun webImg(p: String?): String = when {
    p.isNullOrBlank() -> ""
    p.startsWith("http") -> p
    else -> TMDB_IMG + p
}

private fun getJson(url: String): String =
    webHttp.newCall(Request.Builder().url(url).header("User-Agent", "AriesApp/1.0").build())
        .execute().use { it.body?.string().orEmpty() }

private fun showItem(sh: JSONObject): Item? {
    val im = sh.optJSONObject("image")?.optString("medium")
    if (im.isNullOrBlank()) return null
    val r = sh.optJSONObject("rating")?.optDouble("average") ?: Double.NaN
    return Item(sh.getInt("id"), null, sh.optString("name"),
        sh.optString("summary").replace(Regex("<[^>]*>"), ""), im,
        if (r.isNaN()) null else r, null, "tv", sh.optString("premiered"), null)
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

suspend fun webSearch(q: String): List<Item> = withContext(Dispatchers.IO) {
    val out = mutableListOf<Item>()
    runCatching {
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
                out += Item(pg.getInt("pageid"), null, pg.optString("title"), pg.optString("extract"), th, null, null, "tv", null, null)
        }
    }
    out
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebDialog(query: String, onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(modifier = Modifier.fillMaxSize().padding(top = 52.dp), factory = { c ->
                WebView(c).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl("https://www.google.com/search?q=" + Uri.encode("$query nerede izlenir"))
                }
            })
            Text("✕ Kapat", color = Color.White, modifier = Modifier.padding(16.dp).clickable(onClick = onClose))
        }
    }
}
