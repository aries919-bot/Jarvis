package com.cem.jarvis.media

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cem.jarvis.BuildConfig
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val IMG = "https://image.tmdb.org/t/p/w342"

data class Item(
    val id: Int, val title: String?, val name: String?, val overview: String?,
    val poster_path: String?, val vote_average: Double?, val popularity: Double?,
    val media_type: String?, val first_air_date: String?, val release_date: String?
) {
    val label get() = title ?: name ?: ""
    val year get() = (release_date ?: first_air_date ?: "").take(4)
}
data class Page(val results: List<Item>)
data class Video(val key: String, val site: String, val type: String)
data class Videos(val results: List<Video>)
data class Provider(val provider_name: String)
data class Region(val link: String?, val flatrate: List<Provider>?)
data class Providers(val results: Map<String, Region>?)

interface Tmdb {
    @GET("trending/all/week") suspend fun trending(@Query("language") l: String = "tr-TR"): Page
    @GET("movie/popular") suspend fun movies(@Query("language") l: String = "tr-TR"): Page
    @GET("tv/popular") suspend fun series(@Query("language") l: String = "tr-TR"): Page
    @GET("discover/tv") suspend fun anime(
        @Query("with_genres") g: String = "16", @Query("with_original_language") o: String = "ja",
        @Query("sort_by") s: String = "popularity.desc", @Query("language") l: String = "tr-TR"
    ): Page
    @GET("search/multi") suspend fun search(@Query("query") q: String, @Query("language") l: String = "tr-TR"): Page
    @GET("{type}/{id}/videos") suspend fun videos(@Path("type") t: String, @Path("id") id: Int): Videos
    @GET("{type}/{id}/watch/providers") suspend fun providers(@Path("type") t: String, @Path("id") id: Int): Providers
}

private val api: Tmdb = Retrofit.Builder()
    .baseUrl("https://api.themoviedb.org/3/")
    .client(OkHttpClient.Builder().addInterceptor { c ->
        c.proceed(c.request().newBuilder().addHeader("Authorization", "Bearer ${BuildConfig.TMDB_TOKEN}").build())
    }.build())
    .addConverterFactory(GsonConverterFactory.create())
    .build().create(Tmdb::class.java)

class MediaVM : ViewModel() {
    val tabs = listOf("Trendler", "Filmler", "Dizileri", "Anime")
    var tab by mutableStateOf(0)
    var list by mutableStateOf<List<Item>>(emptyList())
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var history by mutableStateOf(listOf<String>())
    var searching by mutableStateOf(false)

    init { load(0) }

    fun load(i: Int) {
        tab = i; searching = false
        fetch {
            when (i) {
                0 -> Page(tvmazePage(0))
                2 -> Page(tvmazePage(1))
                else -> if (BuildConfig.TMDB_TOKEN.isBlank()) throw IllegalStateException("tmdb")
                        else if (i == 1) api.movies() else api.anime()
            }
        }
    }

    fun search(q: String) {
        if (q.isBlank()) return
        history = (listOf(q) + history.filter { it != q }).take(5)
        searching = true
        fetch {
            val tm = if (BuildConfig.TMDB_TOKEN.isNotBlank()) runCatching {
                api.search(q).results.filter { it.media_type != "person" && it.poster_path != null }
            }.getOrDefault(emptyList()) else emptyList()
            Page(if (tm.isNotEmpty()) tm else webSearch(q))
        }
    }

    private var pageNo = 1
    fun more() {
        val t = tab
        viewModelScope.launch {
            try {
                val extra = if (t == 0 || t == 2) tvmazePage(pageNo + (if (t == 2) 5 else 0)) else emptyList()
                pageNo++
                list = list + extra.filter { n -> list.none { it.id == n.id } }
            } catch (e: Exception) { }
        }
    }

    fun removeHistory(q: String) { history = history - q }

    private fun fetch(block: suspend () -> Page) = viewModelScope.launch {
        loading = true; error = null
        try { list = block().results } catch (e: Exception) { error = if (e.message == "tmdb") "Filmler ve Anime listesi için TMDB anahtarı gerekli. Arama anahtarsız çalışır." else "Bağlantı kurulamadı. İnternetini kontrol et." }
        loading = false
    }
}

@Composable
fun FilmDiziCard(onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1B2440)).clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF8FB4FF)), Alignment.Center) { Text("▶", color = Color.Black, fontSize = 20.sp) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text("Film ve Dizi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Ara, keşfet, izle", color = Color(0xFF9AA1B2), fontSize = 12.sp)
        }
    }
}

@Composable
fun MediaRoot(onExit: () -> Unit, vm: MediaVM = viewModel()) {
    var detail by remember { mutableStateOf<Item?>(null) }
    var trailer by remember { mutableStateOf<String?>(null) }
    when {
        trailer != null -> TrailerPlayer(trailer!!) { trailer = null }
        detail != null -> DetailScreen(detail!!, vm, onBack = { detail = null }, onOpen = { detail = it }, onTrailer = { trailer = it })
        else -> SearchScreen(vm, onBack = onExit, onOpen = { detail = it })
    }
}

@Composable
fun SearchScreen(vm: MediaVM, onBack: () -> Unit, onOpen: (Item) -> Unit) {
    var q by remember { mutableStateOf("") }
    var web2 by remember { mutableStateOf(false) }
    if (web2) WebDialog(q) { web2 = false }
    Column(Modifier.fillMaxSize().background(Color(0xFF0B0D14)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = Color.White, fontSize = 30.sp, modifier = Modifier.clickable(onClick = onBack).padding(end = 12.dp))
            Text("Ara", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("🌐", fontSize = 24.sp, modifier = Modifier.clickable { web2 = true }.padding(8.dp))
        }
        LazyColumn(Modifier.weight(1f)) {
            if (vm.history.isNotEmpty() && !vm.searching) {
                item { Text("Geçmiş", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
                items(vm.history) { h ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(h, color = Color.White, modifier = Modifier.clickable { q = h; vm.search(h) })
                        Text("✕", color = Color(0xFF9AA1B2), modifier = Modifier.clickable { vm.removeHistory(h) })
                    }
                }
            }
            item {
                Text(if (vm.searching) "Sonuçlar" else "Popülerlik Sıralaması", color = Color.White, fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                if (!vm.searching) LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    items(vm.tabs.size) { i ->
                        Text(vm.tabs[i], fontSize = 16.sp, fontWeight = if (i == vm.tab) FontWeight.Bold else FontWeight.Normal,
                            color = if (i == vm.tab) Color.White else Color(0xFF9AA1B2),
                            modifier = Modifier.clickable { vm.load(i) }.padding(vertical = 8.dp))
                    }
                }
            }
            if (vm.loading) item { CircularProgressIndicator(Modifier.padding(24.dp)) }
            vm.error?.let { e -> item { Text(e, color = Color(0xFFFF8A7A), modifier = Modifier.padding(16.dp)) } }
            if (!vm.loading && vm.error == null && vm.list.isEmpty()) item { Text("Sonuç bulunamadı. Başka bir ad dene.", color = Color(0xFF9AA1B2), modifier = Modifier.padding(16.dp)) }
            items(vm.list.size) { i -> RankRow(i + 1, vm.list[i]) { onOpen(vm.list[i]) } }
            if (!vm.searching && vm.list.size >= 20) item {
                Button(onClick = { vm.more() }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Text("Daha fazla göster") }
            }
        }
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFF202433)).padding(start = 16.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically) {
            TextField(q, { q = it }, Modifier.weight(1f), singleLine = true, placeholder = { Text("Film veya dizi ara") },
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White))
            Button(onClick = { vm.search(q) }) { Text("Ara") }
        }
    }
}

@Composable
fun RankRow(rank: Int, media: Item, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp)) {
        Box {
            AsyncImage(webImg(media.poster_path), media.label, Modifier.size(58.dp, 82.dp).clip(RoundedCornerShape(10.dp)))
            Text("$rank", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp,
                modifier = Modifier.background(if (rank == 1) Color(0xFFFF5A3C) else Color(0xFFD8A21A), RoundedCornerShape(bottomEnd = 8.dp)).padding(horizontal = 7.dp, vertical = 2.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(media.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${media.year} · ${if (media.media_type == "movie" || media.title != null) "Film" else "TV Dizisi"} · ★ ${"%.1f".format(media.vote_average ?: 0.0)}",
                color = Color(0xFF9AA1B2), fontSize = 13.sp)
            Text(media.overview.orEmpty(), color = Color(0xFF9AA1B2), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun DetailScreen(media: Item, vm: MediaVM, onBack: () -> Unit, onOpen: (Item) -> Unit, onTrailer: (String) -> Unit) {
    val ctx = LocalContext.current
    val type = if (media.media_type == "movie" || media.title != null) "movie" else "tv"
    var trailerKey by remember(media.id) { mutableStateOf<String?>(null) }
    var region by remember(media.id) { mutableStateOf<Region?>(null) }
    LaunchedEffect(media.id) {
        if (media.poster_path?.startsWith("http") == true) return@LaunchedEffect
        runCatching { trailerKey = api.videos(type, media.id).results.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }?.key }
        runCatching { region = api.providers(type, media.id).results?.get("TR") }
    }
    LazyColumn(Modifier.fillMaxSize().background(Color(0xFF0B0D14)).padding(16.dp)) {
        item { Text("‹", color = Color.White, fontSize = 30.sp, modifier = Modifier.clickable(onClick = onBack)) }
        item {
            Row(Modifier.padding(vertical = 16.dp)) {
                AsyncImage(webImg(media.poster_path), media.label, Modifier.size(120.dp, 175.dp).clip(RoundedCornerShape(14.dp)))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(media.label, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("${media.year} · ★ ${"%.1f".format(media.vote_average ?: 0.0)}", color = Color(0xFF9AA1B2))
                }
            }
            Text(media.overview.orEmpty().ifBlank { "Özet bulunamadı." }, color = Color(0xFFD5D9E4), fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { trailerKey?.let(onTrailer) }, enabled = trailerKey != null, modifier = Modifier.fillMaxWidth()) {
                Text(if (trailerKey != null) "▶ Fragmanı oynat" else "Fragman bulunamadı")
            }
            Spacer(Modifier.height(10.dp))
            val names = region?.flatrate?.joinToString { it.provider_name }
            OutlinedButton(
                onClick = { region?.link?.let { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it))) } },
                enabled = region?.link != null, modifier = Modifier.fillMaxWidth()
            ) { Text(if (names.isNullOrBlank()) "Türkiye'de yasal yayın bulunamadı" else "Nerede izlenir: $names") }
            Text("Yayın bilgisi JustWatch tarafından sağlanır.", color = Color(0xFF6E7488), fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
            var web by remember { mutableStateOf(false) }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(onClick = { web = true }, modifier = Modifier.fillMaxWidth()) { Text("Webde ara") }
            if (web) WebDialog(media.label) { web = false }
            Text("Benzerler", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(vm.list.filter { it.id != media.id }.take(8)) { s ->
                    Column(Modifier.width(110.dp).clickable { onOpen(s) }) {
                        AsyncImage(webImg(s.poster_path), s.label, Modifier.size(110.dp, 160.dp).clip(RoundedCornerShape(12.dp)))
                        Text(s.label, color = Color.White, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun TrailerPlayer(key: String, onClose: () -> Unit) {
    val owner = LocalLifecycleOwner.current
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = { ctx ->
            YouTubePlayerView(ctx).apply {
                owner.lifecycle.addObserver(this)
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) { youTubePlayer.loadVideo(key, 0f) }
                })
            }
        })
        Text("‹", color = Color.White, fontSize = 34.sp, modifier = Modifier.padding(16.dp).clickable(onClick = onClose))
    }
}
