package com.cem.jarvis.media

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.net.URLEncoder

private data class AriesSiteResult(
    val title: String,
    val url: String,
    val snippet: String
)

private val AriesSearchBackground = Color(0xFF242424)
private val AriesSearchCard = Color(0xFF303030)
private val AriesSearchMuted = Color(0xFFBDBDBD)

/**
 * Seekee benzeri arama/sonuç/site içi gezinme ekranı.
 * Arama motoru sonuçlarını kartlara dönüştürür; seçilen siteyi aynı WebView'da açar.
 * Site kendi HTML5 oynatıcısını sağlıyorsa WebView içinde çalışır.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MediaSourceBrowser(query: String, onBack: () -> Unit) {
    var searchText by remember(query) { mutableStateOf(query) }
    var activeSite by remember { mutableStateOf<AriesSiteResult?>(null) }
    var pageTitle by remember { mutableStateOf("") }
    var pageUrl by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Film ve dizi siteleri aranıyor…") }
    var loading by remember { mutableStateOf(false) }
    val results = remember { mutableStateListOf<AriesSiteResult>() }
    var webView by remember { mutableStateOf<WebView?>(null) }

    fun startSearch(text: String) {
        val term = text.trim()
        if (term.isEmpty()) return
        searchText = term
        activeSite = null
        results.clear()
        loading = true
        status = "Farklı sitelerde arama yapılıyor…"
        val encoded = URLEncoder.encode("$term izle film dizi", "UTF-8")
        webView?.loadUrl("https://html.duckduckgo.com/html/?q=$encoded")
    }

    fun openSite(site: AriesSiteResult) {
        activeSite = site
        pageTitle = site.title
        pageUrl = site.url
        loading = true
        status = "Site açılıyor…"
        webView?.loadUrl(site.url)
    }

    BackHandler(enabled = activeSite != null) {
        webView?.stopLoading()
        activeSite = null
        status = if (results.isEmpty()) "Arama sonucu bekleniyor" else "${results.size} site bulundu"
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.removeJavascriptInterface("AriesSearchBridge")
            webView?.destroy()
            webView = null
        }
    }

    Column(Modifier.fillMaxSize().background(AriesSearchBackground)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "‹",
                color = Color.White,
                fontSize = 32.sp,
                modifier = Modifier
                    .clickable { if (activeSite != null) activeSite = null else onBack() }
                    .padding(end = 12.dp)
            )
            Column(Modifier.weight(1f)) {
                Text(
                    if (activeSite == null) "Film ve dizi ara" else pageTitle.ifBlank { "Site" },
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (activeSite != null) {
                    Text(
                        pageUrl,
                        color = AriesSearchMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (activeSite != null) {
                Text(
                    "Yenile",
                    color = Color.White,
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
                color = AriesSearchMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (results.isEmpty()) {
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
                                val encoded = URLEncoder.encode("$query izle film dizi", "UTF-8")
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
                    items(results, key = { it.url }) { result ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AriesSearchCard)
                                .clickable { openSite(result) }
                                .padding(14.dp)
                        ) {
                            Text(
                                result.title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (result.snippet.isNotBlank()) {
                                Text(
                                    result.snippet,
                                    color = Color(0xFFE0E0E0),
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
                                    color = AriesSearchMuted,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "İzle  ›",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF414141))
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
                color = AriesSearchMuted,
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
                        onStatus = { status = it }
                    ).also {
                        webView = it
                        it.loadUrl(activeSite!!.url)
                    }
                },
                update = { webView = it }
            )
        }
    }
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
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
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
    onStatus: (String) -> Unit
): WebView {
    return WebView(context).apply {
        configureAriesWebView()
        webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                if (!title.isNullOrBlank()) onTitle(title)
            }
        }
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
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
