package com.cem.jarvis.media

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import java.net.URLEncoder

private data class FoundVideo(val url: String, val page: String)

@Composable
fun MediaSourceBrowser(query: String, onBack: () -> Unit) {
    val ctx = LocalContext.current
    var address by remember(query) { mutableStateOf("") }
    var pageTitle by remember { mutableStateOf("Video kaynakları") }
    var currentPage by remember { mutableStateOf("https://www.google.com") }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var selected by remember { mutableStateOf<FoundVideo?>(null) }
    var status by remember { mutableStateOf("Web'de kaynak aranıyor…") }
    val videosFound = remember { mutableStateListOf<FoundVideo>() }

    fun searchWeb(text: String) {
        val target = if (text.startsWith("http://") || text.startsWith("https://")) text
            else "https://www.google.com/search?q=" + URLEncoder.encode("$text video izle", "UTF-8")
        address = target
        status = "Sayfa yükleniyor…"
        webView?.loadUrl(target)
    }

    DisposableEffect(Unit) {
        onDispose { webView?.destroy(); webView = null }
    }

    Column(Modifier.fillMaxSize().background(Color(0xFF0B0D14))) {
        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = Color.White, fontSize = 30.sp, modifier = Modifier.clickable(onClick = onBack).padding(end = 10.dp))
            Text("Kaynak bul", color = Color.White, fontSize = 18.sp, modifier = Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = address, onValueChange = { address = it }, modifier = Modifier.weight(1f),
                singleLine = true, placeholder = { Text("Site adresi veya arama") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onGo = { searchWeb(address) })
            )
            TextButton(onClick = { searchWeb(address.ifBlank { query }) }) { Text("Git") }
        }
        if (selected == null) {
            Text(status, color = Color(0xFF9AA1B2), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
            AndroidView(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = true
                        settings.loadsImagesAutomatically = true
                        settings.userAgentString = settings.userAgentString
                        addJavascriptInterface(object {
                            @JavascriptInterface fun found(url: String, title: String) {
                                if (!isPlayableUrl(url)) return
                                Handler(Looper.getMainLooper()).post {
                                    val item = FoundVideo(url, title.ifBlank { pageTitle })
                                    if (videosFound.none { it.url == item.url }) videosFound.add(0, item)
                                    status = "${videosFound.size} video bağlantısı bulundu"
                                }
                            }
                        }, "AriesBridge")
                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView, url: String) {
                                super.onPageFinished(view, url)
                                address = url
                                currentPage = url
                                pageTitle = view.title ?: url
                                status = "Sayfa açık. Video algılanıyor…"
                                val js = """
                                    (function(){
                                      function scan(){
                                        var urls=[];
                                        document.querySelectorAll('video').forEach(function(v){
                                          if(v.currentSrc) urls.push(v.currentSrc);
                                          if(v.src) urls.push(v.src);
                                          v.querySelectorAll('source').forEach(function(s){if(s.src)urls.push(s.src);});
                                        });
                                        document.querySelectorAll('source,a').forEach(function(e){
                                          var u=e.src||e.href||'';
                                          if(/\.(mp4|m3u8|mpd|webm|m4v)(\?|#|$)/i.test(u)) urls.push(u);
                                        });
                                        urls.forEach(function(u){try{AriesBridge.found(u,document.title||'');}catch(e){}});
                                      }
                                      scan();
                                      if(!window.__ariesObserver){
                                        window.__ariesObserver=new MutationObserver(scan);
                                        window.__ariesObserver.observe(document.documentElement,{childList:true,subtree:true,attributes:true});
                                      }
                                      setInterval(scan,2500);
                                    })();
                                """.trimIndent()
                                view.evaluateJavascript(js, null)
                            }
                        }
                        webView = this
                        loadUrl("https://www.google.com/search?q=" + URLEncoder.encode("$query video izle", "UTF-8"))
                    }
                },
                update = { webView = it }
            )
            if (videosFound.isNotEmpty()) {
                Column(Modifier.fillMaxWidth().heightIn(max = 190.dp).background(Color(0xFF171A24)).padding(8.dp)) {
                    Text("Bulunan video bağlantıları", color = Color.White, fontSize = 14.sp)
                    LazyColumn {
                        items(videosFound.toList()) { item ->
                            Text(item.url, color = Color(0xFF8FB4FF), fontSize = 11.sp,
                                modifier = Modifier.fillMaxWidth().clickable { selected = item.copy(page = currentPage) }.padding(vertical = 7.dp))
                        }
                    }
                }
            }
        } else {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("‹ Kaynaklara dön", color = Color(0xFF8FB4FF), modifier = Modifier.clickable { selected = null })
                Spacer(Modifier.width(12.dp))
                Text("Video oynatılıyor", color = Color.White)
            }
            SourceVideoPlayer(selected!!.url, selected!!.page, ctx, Modifier.weight(1f))
        }
    }
}

private fun isPlayableUrl(url: String): Boolean {
    val u = url.lowercase()
    return (u.startsWith("http://") || u.startsWith("https://")) &&
        listOf(".mp4", ".m3u8", ".mpd", ".webm", ".m4v", ".mov").any { ext ->
            u.contains(ext) || u.contains("$ext?")
        }
}

@Composable
private fun SourceVideoPlayer(url: String, page: String, context: Context, modifier: Modifier = Modifier) {
    val player = remember(url) {
        val cookies = runCatching { CookieManager.getInstance().getCookie(page) }.getOrNull()
        val headers = mutableMapOf("Referer" to page)
        if (!cookies.isNullOrBlank()) headers["Cookie"] = cookies
        val http = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/120 Mobile Safari/537.36")
            .setDefaultRequestProperties(headers)
        val dataSource = DefaultDataSource.Factory(context, http)
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSource))
            .build().apply {
                setMediaItem(MediaItem.fromUri(url))
                prepare()
                playWhenReady = true
            }
    }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { c -> PlayerView(c).apply { this.player = player; useController = true } },
        update = { it.player = player }
    )
}
