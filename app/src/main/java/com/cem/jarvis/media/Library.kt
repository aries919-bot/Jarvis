package com.cem.jarvis.media

import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
 * KURULUM
 * build.gradle:
 *   implementation("androidx.datastore:datastore-preferences:1.1.1")
 *   implementation("androidx.media3:media3-exoplayer:1.4.1")
 *   implementation("androidx.media3:media3-ui:1.4.1")
 *
 * (FilmDizi.kt içindeki gerekli değişiklikler yapıldı.)
 *
 * KULLANIM (FilmDizi.kt içinde):
 *   MediaRoot başına:      val lib: LibraryVM = viewModel()
 *   SearchScreen üstüne:   item { ContinueRow(lib) { detail = it.toItem() } }
 *                          item { FavoritesRow(lib) { detail = it.toItem() } }
 *   DetailScreen içine:    FavoriteButton(item, lib)  +  EpisodeList(item, lib)   (yalnızca dizilerde)
 *
 * NOT: TMDB hangi platformda izlediğini bilemez. "İzledim" işaretini sen dokunarak koyarsın.
 * Otomatik "kaldığın dakika" yalnızca kendi video bağlantını oynattığın ResumePlayer'da çalışır.
 */

// ---------- Modeller ----------
data class TvInfo(val number_of_seasons: Int?)
data class Ep(val episode_number: Int, val name: String?, val runtime: Int?)
data class Season(val episodes: List<Ep>?)

data class Entry(
    val id: Int, val type: String, val title: String, val poster: String?,
    val favorite: Boolean = false,
    val season: Int = 1, val episode: Int = 1,
    val positionMs: Long = 0, val durationMs: Long = 0,
    val watched: List<String>? = null,            // "s1e3"
    val updatedAt: Long = 0,
    val upNext: Boolean = false,                  // bölüm bitti, sıradaki bekliyor
    val dismissed: Boolean = false                // "Devam et"ten kaldırıldı
) {
    val progress get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    val hasProgress get() = updatedAt > 0
    fun toItem() = Item(id, if (type == "movie") title else null, if (type == "tv") title else null,
        null, poster, null, null, type, null, null)
}

// ---------- Depolama ----------
private val Context.libDs by preferencesDataStore("aries_library")
private val KEY = stringPreferencesKey("entries")
private val gson = Gson()
private val listType = object : TypeToken<List<Entry>>() {}.type

class LibraryVM(app: Application) : AndroidViewModel(app) {
    private val ds = app.libDs
    var entries by mutableStateOf<List<Entry>>(emptyList()); private set

    init {
        viewModelScope.launch {
            ds.data.map { p -> gson.fromJson<List<Entry>>(p[KEY] ?: "[]", listType) ?: emptyList() }
                .collect { entries = it }
        }
    }

    val favorites get() = entries.filter { it.favorite }
    // %90 altı = yarım kalan, bitirilen diziler = "Sıradaki bölüm"
    val continueWatching get() = entries
        .filter { !it.dismissed && ((it.hasProgress && it.progress < 0.9f) || it.upNext) }
        .sortedByDescending { it.updatedAt }

    fun dismiss(id: Int) {
        val list = entries.map { if (it.id == id) it.copy(dismissed = true) else it }
        entries = list
        viewModelScope.launch { ds.edit { it[KEY] = gson.toJson(list) } }
    }
    fun get(id: Int) = entries.firstOrNull { it.id == id }

    private fun update(item: Item, change: (Entry) -> Entry) {
        val type = if (item.media_type == "movie" || item.title != null) "movie" else "tv"
        val base = get(item.id) ?: Entry(item.id, type, item.label, item.poster_path)
        val next = change(base).copy(title = item.label.ifBlank { base.title }, poster = item.poster_path ?: base.poster)
        val list = listOf(next) + entries.filter { it.id != item.id }
        entries = list
        viewModelScope.launch { ds.edit { it[KEY] = gson.toJson(list) } }
    }

    fun toggleFavorite(item: Item) = update(item) { it.copy(favorite = !it.favorite) }

    fun toggleWatched(item: Item, s: Int, e: Int) = update(item) {
        val tag = "s${s}e$e"; val w = it.watched.orEmpty()
        it.copy(watched = if (tag in w) w - tag else w + tag, season = s, episode = e)
    }

    /** %90'ı geçince veya bitince "izlendi" sayılır; dizide sıradaki bölüme geçilir, konum sıfırlanır. */
    fun saveProgress(item: Item, s: Int, e: Int, pos: Long, dur: Long, ended: Boolean = false) = update(item) {
        val now = System.currentTimeMillis()
        val done = ended || (dur > 0 && pos >= dur * 0.9)
        when {
            done && it.type == "tv" -> it.copy(
                watched = (it.watched.orEmpty() + "s${s}e$e").distinct(),
                season = s, episode = e + 1, positionMs = 0, durationMs = 0,
                upNext = true, dismissed = false, updatedAt = now)
            done -> it.copy(positionMs = 0, durationMs = 0, updatedAt = 0, upNext = false)
            else -> it.copy(season = s, episode = e, positionMs = pos, durationMs = dur,
                upNext = false, dismissed = false, updatedAt = now)
        }
    }
}

// ---------- Favori butonu ----------
@Composable
fun FavoriteButton(item: Item, lib: LibraryVM) {
    val fav = lib.get(item.id)?.favorite == true
    OutlinedButton(onClick = { lib.toggleFavorite(item) }, modifier = Modifier.fillMaxWidth()) {
        Text(if (fav) "★ Favorilerde" else "☆ Favorilere ekle")
    }
}

// ---------- Kaldığın yerden devam ----------
@Composable
fun ContinueRow(lib: LibraryVM, onOpen: (Entry) -> Unit) {
    val list = lib.continueWatching
    if (list.isEmpty()) return
    Column(Modifier.padding(top = 16.dp)) {
        Text("Devam et", color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(list) { e ->
                Column(Modifier.width(120.dp).clickable { onOpen(e) }) {
                    Box {
                        AsyncImage(webImg(e.poster), e.title, Modifier.size(120.dp, 170.dp).clip(RoundedCornerShape(12.dp)))
                        Text("✕", color = Color.White, fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
                                .background(Color(0x99000000), RoundedCornerShape(50)).clickable { lib.dismiss(e.id) }
                                .padding(horizontal = 7.dp, vertical = 2.dp))
                    }
                    if (!e.upNext) LinearProgressIndicator(progress = { e.progress }, Modifier.fillMaxWidth().padding(top = 6.dp))
                    Text(e.title, color = Color.White, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    val ep = if (e.type == "tv") "Sezon ${e.season} · Bölüm ${e.episode}" else ""
                    Text(if (e.upNext) "Sıradaki: $ep" else listOf(ep, fmtRemaining(e.durationMs - e.positionMs)).filter { it.isNotEmpty() }.joinToString(" · "),
                        color = Color(0xFF9AA1B2), fontSize = 11.sp, maxLines = 2)
                }
            }
        }
    }
}

// ---------- Favoriler ----------
@Composable
fun FavoritesRow(lib: LibraryVM, onOpen: (Entry) -> Unit) {
    val list = lib.favorites
    if (list.isEmpty()) return
    Column(Modifier.padding(top = 16.dp)) {
        Text("Favorilerim", color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(list) { e ->
                Column(Modifier.width(100.dp).clickable { onOpen(e) }) {
                    AsyncImage(webImg(e.poster), e.title, Modifier.size(100.dp, 145.dp).clip(RoundedCornerShape(12.dp)))
                    Text(e.title, color = Color.White, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

// ---------- Bölüm listesi + izledim işareti ----------
@Composable
fun EpisodeList(item: Item, lib: LibraryVM) {
    var seasons by remember(item.id) { mutableIntStateOf(1) }
    var season by remember(item.id) { mutableIntStateOf(lib.get(item.id)?.season ?: 1) }
    var eps by remember(item.id, season) { mutableStateOf<List<Ep>>(emptyList()) }
    LaunchedEffect(item.id) { runCatching { seasons = api.tvInfo(item.id).number_of_seasons ?: 1 } }
    LaunchedEffect(item.id, season) { runCatching { eps = api.season(item.id, season).episodes.orEmpty() } }
    val watched = lib.get(item.id)?.watched.orEmpty()

    Column {
        Text("Bölümler", color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items((1..seasons).toList()) { s ->
                FilterChip(selected = s == season, onClick = { season = s }, label = { Text("Sezon $s") })
            }
        }
        eps.forEach { ep ->
            val done = "s${season}e${ep.episode_number}" in watched
            Row(Modifier.fillMaxWidth().clickable { lib.toggleWatched(item, season, ep.episode_number) }.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(if (done) Color(0xFF3A7D5C) else Color(0xFF202433)), Alignment.Center) {
                    Text(if (done) "✓" else "${ep.episode_number}", color = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(ep.name ?: "Bölüm ${ep.episode_number}", color = if (done) Color(0xFF9AA1B2) else Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    ep.runtime?.let { Text("$it dk", color = Color(0xFF9AA1B2), fontSize = 12.sp) }
                }
                Text(if (done) "İzlendi" else "İzledim", color = Color(0xFF8FB4FF), fontSize = 12.sp)
            }
        }
    }
}

// ---------- Süre biçimleri ----------
/** 4330000 ms -> "1:12:10", 612000 -> "10:12" */
fun fmtTime(ms: Long): String {
    val t = ms / 1000; val h = t / 3600; val m = (t % 3600) / 60; val sec = t % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, sec) else "%02d:%02d".format(m, sec)
}
/** "1 sa 12 dk kaldı", "53 dk kaldı", "1 dk'dan az kaldı" */
fun fmtRemaining(ms: Long): String {
    val min = (ms / 60000).coerceAtLeast(0)
    return when {
        min >= 60 -> "${min / 60} sa ${min % 60} dk kaldı"
        min >= 1 -> "$min dk kaldı"
        else -> "1 dk'dan az kaldı"
    }
}

// ---------- Kaldığın yerden oynatan oynatıcı (kendi bağlantın: mp4 / m3u8) ----------
@Composable
fun ResumePlayer(
    url: String, item: Item, season: Int, episode: Int, lib: LibraryVM,
    hasNext: Boolean, onNext: () -> Unit, onClose: () -> Unit,
    onEpisodes: () -> Unit = onClose,
    requestHeaders: Map<String, String> = emptyMap(),
    mimeType: String? = null,
    onPlaybackError: (PlaybackException) -> Unit = {}
) {
    val ctx = LocalContext.current
    val owner = LocalLifecycleOwner.current
    // İlk 30 sn içinde kalındıysa baştan başlar; aksi halde 5 sn geriden devam eder.
    val saved = remember {
        lib.get(item.id)?.takeIf { it.season == season && it.episode == episode && it.positionMs > 30_000 && it.progress < 0.9f }
    }
    var banner by remember { mutableStateOf(saved != null) }
    var ended by remember { mutableStateOf(false) }
    var videoSize by remember { mutableStateOf("Büyük") }

    val player = remember(url, requestHeaders, mimeType) {
        val httpFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setDefaultRequestProperties(requestHeaders)
        val dataSourceFactory = DefaultDataSource.Factory(ctx, httpFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
        ExoPlayer.Builder(ctx)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                val mediaItem = MediaItem.Builder().setUri(url).apply {
                    mimeType?.let { setMimeType(it) }
                }.build()
                setMediaItem(mediaItem)
                prepare()
                if (saved != null) seekTo((saved.positionMs - 5000).coerceAtLeast(0))
                playWhenReady = true
            }
    }
    fun save() { if (player.duration > 0) lib.saveProgress(item, season, episode, player.currentPosition, player.duration, ended) }

    LaunchedEffect(player) { while (true) { delay(5000); save() } }          // 5 sn'de bir
    LaunchedEffect(banner) { if (banner) { delay(6000); banner = false } }

    DisposableEffect(player) {
        val l = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) { if (state == Player.STATE_ENDED) { ended = true; save() } }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                onPlaybackError(error)
            }
        }
        val obs = LifecycleEventObserver { _, e -> if (e == Lifecycle.Event.ON_PAUSE) save() }   // uygulama arka plana gidince
        player.addListener(l); owner.lifecycle.addObserver(obs)
        onDispose { save(); owner.lifecycle.removeObserver(obs); player.removeListener(l); player.release() }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        val videoModifier = when (videoSize) {
            "Küçük" -> Modifier.fillMaxWidth(0.60f).aspectRatio(16f / 9f).align(Alignment.Center)
            "Orta" -> Modifier.fillMaxWidth(0.80f).aspectRatio(16f / 9f).align(Alignment.Center)
            else -> Modifier.fillMaxSize()
        }
        AndroidView(
            factory = { PlayerView(it).apply {
                this.player = player
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            } },
            modifier = videoModifier,
            update = { it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT; it.player = player }
        )
        PlayerControls(
            player, "${item.label}${if (item.title == null) " ($episode)" else ""}",
            hasNext, onNext, onEpisodes, onClose,
            videoSize = videoSize,
            onVideoSizeChange = { videoSize = it },
            onFullscreen = { videoSize = "Büyük" }
        )

        if (banner && saved != null) Row(
            Modifier.align(Alignment.TopCenter).padding(top = 60.dp).background(Color(0xCC000000), RoundedCornerShape(14.dp)).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Kaldığın yerden devam: ${fmtTime(saved.positionMs)}", color = Color.White, fontSize = 13.sp)
            Spacer(Modifier.width(12.dp))
            TextButton(onClick = { player.seekTo(0); banner = false }) { Text("Baştan başla") }
        }
        if (ended && hasNext) Button(onClick = onNext, modifier = Modifier.align(Alignment.Center)) { Text("Sonraki bölüm ▸") }
    }
}

// ---------- Aries ana sayfası: tek dokunuşla "Kaldığın yerden devam et" kartı ----------
/*
 * Ana sayfada FilmDiziCard'ın hemen altına:
 *     val lib: LibraryVM = viewModel()
 *     FilmDiziCard { ekran = "film" }
 *     Spacer(Modifier.height(10.dp))
 *     HomeContinueCard(lib) { entry -> ekran = "film"; /* istersen doğrudan oynatıcıyı aç: entry.toItem(), entry.season, entry.episode */ }
 * Devam edilecek bir şey yoksa kart hiç görünmez.
 */
@Composable
fun HomeContinueCard(lib: LibraryVM, onResume: (Entry) -> Unit) {
    val e = lib.continueWatching.firstOrNull() ?: return
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFF171A24))
            .clickable { onResume(e) }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(webImg(e.poster), e.title, Modifier.size(56.dp, 80.dp).clip(RoundedCornerShape(10.dp)))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Kaldığın yerden devam et", color = Color(0xFF9AA1B2), fontSize = 11.sp)
            Text(e.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            val ep = if (e.type == "tv") "Sezon ${e.season} · Bölüm ${e.episode}" else ""
            Text(
                if (e.upNext) "Sıradaki: $ep"
                else listOf(ep, fmtRemaining(e.durationMs - e.positionMs)).filter { it.isNotEmpty() }.joinToString(" · "),
                color = Color(0xFF9AA1B2), fontSize = 12.sp
            )
            if (!e.upNext) LinearProgressIndicator(progress = { e.progress }, Modifier.fillMaxWidth().padding(top = 6.dp))
        }
        Text("▶", color = Color(0xFF8FB4FF), fontSize = 24.sp, modifier = Modifier.padding(start = 10.dp))
    }
}
