package com.cem.jarvis.media

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.C
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/*
 * Ekran görüntüsündeki oynatıcı: başlık, kilit, 10 sn geri/ileri, oynat/duraklat, süre çubuğu,
 * Netlik, Hız, Ses, Altyazılar, Bölüm, Sonraki Bölüm. Sol yarıda parlaklık, sağ yarıda ses kaydırma.
 * Çift dokunuş: sol yarı -10 sn, sağ yarı +10 sn. Hız seçimi hatırlanır. Yatay ekrana geçer.
 * ResumePlayer içinden çağrılır (PlayerView'da useController = false).
 */
@Composable
fun PlayerControls(
    player: ExoPlayer, title: String, hasNext: Boolean,
    onNext: () -> Unit, onEpisodes: () -> Unit, onClose: () -> Unit,
    videoSize: String = "Büyük", onVideoSizeChange: (String) -> Unit = {},
    onFullscreen: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val act = ctx as? Activity
    val prefs = remember { ctx.getSharedPreferences("aries_player", Context.MODE_PRIVATE) }
    val am = remember { ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var show by remember { mutableStateOf(true) }
    var locked by remember { mutableStateOf(false) }
    var fullscreen by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(true) }
    var pos by remember { mutableLongStateOf(0L) }
    var dur by remember { mutableLongStateOf(0L) }
    var speed by remember { mutableFloatStateOf(prefs.getFloat("speed", 1f)) }
    var quality by remember { mutableStateOf("Otomatik") }
    var menu by remember { mutableStateOf<String?>(null) }
    var hud by remember { mutableStateOf<String?>(null) }
    var volF by remember { mutableFloatStateOf(am.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) }

    DisposableEffect(Unit) {
        act?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            act?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            act?.window?.let { WindowCompat.getInsetsController(it, it.decorView).show(WindowInsetsCompat.Type.systemBars()) }
        }
    }
    DisposableEffect(fullscreen) {
        val window = act?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (fullscreen) controller.hide(WindowInsetsCompat.Type.systemBars())
            else controller.show(WindowInsetsCompat.Type.systemBars())
        }
        onDispose { if (fullscreen && window != null) WindowCompat.getInsetsController(window, window.decorView).show(WindowInsetsCompat.Type.systemBars()) }
    }
    LaunchedEffect(speed) { player.setPlaybackSpeed(speed); prefs.edit().putFloat("speed", speed).apply() }
    LaunchedEffect(Unit) { while (true) { pos = player.currentPosition; dur = player.duration.coerceAtLeast(0); playing = player.isPlaying; delay(500) } }
    LaunchedEffect(show, playing, menu, pos / 4000) { if (show && playing && menu == null) { delay(3500); show = false } }
    LaunchedEffect(hud) { if (hud != null) { delay(900); hud = null } }

    fun seekBy(ms: Long) { player.seekTo((player.currentPosition + ms).coerceIn(0, dur)); pos = player.currentPosition }
    fun params(f: (androidx.media3.common.TrackSelectionParameters.Builder) -> Unit) {
        player.trackSelectionParameters = player.trackSelectionParameters.buildUpon().also(f).build()
    }

    Box(
        Modifier.fillMaxSize()
            .pointerInput(locked) {
                detectTapGestures(
                    onTap = { show = !show; menu = null },
                    onDoubleTap = { o -> if (!locked) { seekBy(if (o.x < size.width / 2) -10_000 else 10_000); hud = if (o.x < size.width / 2) "⟲ 10 sn" else "10 sn ⟳" } }
                )
            }
            .pointerInput(locked) {
                if (locked) return@pointerInput
                detectVerticalDragGestures { change, dy ->
                    val d = -dy / size.height
                    if (change.position.x < size.width / 2) {          // parlaklık
                        val w = act?.window ?: return@detectVerticalDragGestures
                        val lp = w.attributes
                        val cur = if (lp.screenBrightness < 0) 0.5f else lp.screenBrightness
                        lp.screenBrightness = (cur + d).coerceIn(0.02f, 1f); w.attributes = lp
                        hud = "☀ ${(lp.screenBrightness * 100).roundToInt()}%"
                    } else {                                           // ses
                        volF = (volF + d).coerceIn(0f, 1f)
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, (volF * am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).roundToInt(), 0)
                        hud = "🔊 ${(volF * 100).roundToInt()}%"
                    }
                }
            }
    ) {
        hud?.let { Text(it, color = Ap.Text, fontSize = 16.sp, modifier = Modifier.align(Alignment.TopCenter).padding(top = 70.dp).background(Color(0xAA000000), RoundedCornerShape(12.dp)).padding(horizontal = 14.dp, vertical = 8.dp)) }

        if (show && locked) Text("🔒", fontSize = 28.sp, modifier = Modifier.align(Alignment.CenterEnd).padding(24.dp).clickable { locked = false })

        if (show && !locked) Column(
            Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xAA000000), Color.Transparent, Color(0xBB000000)))).padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Üst: geri, başlık, kilit
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("‹", color = Ap.Text, fontSize = 34.sp, modifier = Modifier.clickable(onClick = onClose).padding(end = 12.dp))
                Text(title, color = Ap.Text, fontSize = 15.sp, modifier = Modifier.weight(1f), maxLines = 1)
                Text("🔓", fontSize = 24.sp, modifier = Modifier.clickable { locked = true; show = false })
            }
            // Orta: -10 / oynat / +10
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("⟲10", color = Ap.Text, fontSize = 22.sp, modifier = Modifier.clickable { seekBy(-10_000) }.padding(24.dp))
                Text(if (playing) "❚❚" else "▶", color = Ap.Text, fontSize = 40.sp,
                    modifier = Modifier.clickable { if (player.isPlaying) player.pause() else player.play(); playing = player.isPlaying }.padding(24.dp))
                Text("10⟳", color = Ap.Text, fontSize = 22.sp, modifier = Modifier.clickable { seekBy(10_000) }.padding(24.dp))
            }
            // Alt: süre çubuğu + seçenekler
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(fmtTime(pos), color = Ap.Text, fontSize = 12.sp)
                    Slider(
                        value = if (dur > 0) pos.toFloat() / dur else 0f,
                        onValueChange = { player.seekTo((it * dur).toLong()); pos = player.currentPosition },
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    )
                    Text(fmtTime(dur), color = Ap.Text, fontSize = 12.sp)
                }
                val hasText = player.currentTracks.groups.any { it.type == C.TRACK_TYPE_TEXT }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Opt("HD Netlik ($quality)") { menu = "q" }
                    Opt("Hız (${"%.2f".format(speed).trimEnd('0').trimEnd('.')}x)") { menu = "s" }
                    Opt("Ses") { menu = "a" }
                    Opt("Altyazılar", enabled = hasText) { menu = "t" }
                    Opt("Görüntü ($videoSize)") { menu = "v" }
                    Opt(if (fullscreen) "Tam ekrandan çık" else "Tam ekran") { fullscreen = !fullscreen; onFullscreen() }
                    Opt("Bölüm") { onEpisodes() }
                    if (hasNext) Opt("Sonraki Bölüm ▸") { onNext() }
                }
            }
        }

        // Açılır menüler
        if (menu != null && !locked) Column(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 84.dp).background(Ap.Card2, RoundedCornerShape(16.dp)).padding(8.dp)
        ) {
            when (menu) {
                "q" -> listOf("Otomatik" to 0, "360P" to 360, "540P" to 540, "720P" to 720, "1080P" to 1080).forEach { (l, h) ->
                    MenuItem(l, quality == l) {
                        quality = l
                        params { if (h == 0) it.clearVideoSizeConstraints() else it.setMaxVideoSize(Int.MAX_VALUE, h) }
                        menu = null
                    }
                }
                "s" -> listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f).forEach { v -> MenuItem("${v}x", speed == v) { speed = v; menu = null } }
                "a" -> player.currentTracks.groups.filter { it.type == C.TRACK_TYPE_AUDIO }.forEachIndexed { i, g ->
                    MenuItem(g.getTrackFormat(0).language ?: "Ses ${i + 1}", g.isSelected) {
                        params { it.setOverrideForType(TrackSelectionOverride(g.mediaTrackGroup, 0)) }; menu = null
                    }
                }
                "v" -> listOf("Küçük", "Orta", "Büyük", "Ekrana sığdır").forEach { size ->
                    MenuItem(size, videoSize == size || (size == "Ekrana sığdır" && videoSize == "Büyük")) {
                        onVideoSizeChange(if (size == "Ekrana sığdır") "Büyük" else size)
                        menu = null
                    }
                }
                "t" -> {
                    MenuItem("Kapalı", false) { params { it.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true) }; menu = null }
                    player.currentTracks.groups.filter { it.type == C.TRACK_TYPE_TEXT }.forEachIndexed { i, g ->
                        MenuItem(g.getTrackFormat(0).language ?: "Altyazı ${i + 1}", g.isSelected) {
                            params { it.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false).setOverrideForType(TrackSelectionOverride(g.mediaTrackGroup, 0)) }; menu = null
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun Opt(text: String, enabled: Boolean = true, onClick: () -> Unit) =
    Text(text, color = if (enabled) Color.White else Color(0x66FFFFFF), fontSize = 12.sp,
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick).padding(8.dp))

@Composable private fun MenuItem(text: String, selected: Boolean, onClick: () -> Unit) =
    Text(text, color = Ap.Text, fontSize = 14.sp,
        modifier = Modifier.fillMaxWidth().background(if (selected) Ap.Raised else Color.Transparent, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick).padding(horizontal = 18.dp, vertical = 10.dp))
