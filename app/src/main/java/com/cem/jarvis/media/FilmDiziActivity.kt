package com.cem.jarvis.media

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme

class FilmDiziActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Düğme, giriş kutusu ve yükleme göstergesi artık varsayılan mor/açık tema yerine
        // uygulamanın ortak koyu temasını kullanır (renkler Theme.kt içindeki Ap).
        val scheme = darkColorScheme(
            primary = Ap.Accent,
            onPrimary = Ap.OnAccent,
            secondary = Ap.Accent,
            background = Ap.Bg,
            onBackground = Ap.Text,
            surface = Ap.Card,
            onSurface = Ap.Text,
            surfaceVariant = Ap.Raised,
            onSurfaceVariant = Ap.Muted,
            outline = Ap.Line,
            error = Ap.Red
        )
        setContent { MaterialTheme(colorScheme = scheme) { MediaRoot(onExit = { finish() }) } }
    }
}
