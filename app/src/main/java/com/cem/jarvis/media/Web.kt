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

// Search metadata is provided by TMDB in MediaVM. Do not fall back to TVmaze,
// Wikipedia, or general web pages as if they were film/series catalogue results.
suspend fun webSearch(q: String): List<Item> = emptyList()

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebDialog(query: String, onClose: () -> Unit) {
    var text by remember { mutableStateOf(query) }
    var wv by remember { mutableStateOf<WebView?>(null) }
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
                AndroidView(modifier = Modifier.weight(1f).fillMaxWidth(), factory = { c ->
                    WebView(c).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(v: WebView?, url: String?) { if (url != null) text = url }
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
