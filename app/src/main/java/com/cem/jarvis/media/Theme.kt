package com.cem.jarvis.media

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cem.jarvis.Pal

/** Native screens and Compose media share one persisted palette. */
internal object Ap {
    private fun color(hex: String): Color = Color(android.graphics.Color.parseColor(hex))
    val Bg get() = color(Pal.BG)
    val Card get() = color(Pal.CARD)
    val Card2 get() = color(Pal.CARD2)
    val Raised get() = color(Pal.RAISED)
    val Line get() = color(Pal.LINE)
    val Text get() = color(Pal.TEXT)
    val Muted get() = color(Pal.MUTED)
    val Accent get() = color(Pal.ACCENT)
    val OnAccent get() = color(Pal.ON_ACCENT)
    val AccentSoft get() = color(Pal.ACCENT_SOFT)
    val AccentSel get() = color(Pal.RAISED)
    val Green get() = color(Pal.GREEN)
    val Amber get() = color(Pal.AMBER)
    val Red get() = color(Pal.RED)
    val Blue get() = color(Pal.BLUE)
}

@Composable
internal fun AriesMaterialTheme(content: @Composable () -> Unit) {
    val colors = if (Pal.lightMode) {
        lightColorScheme(
            primary = Ap.Accent, onPrimary = Ap.OnAccent,
            secondary = Ap.Blue, onSecondary = Ap.Bg,
            background = Ap.Bg, onBackground = Ap.Text,
            surface = Ap.Card, onSurface = Ap.Text,
            surfaceVariant = Ap.Card2, onSurfaceVariant = Ap.Muted,
            outline = Ap.Line, error = Ap.Red, onError = Ap.Bg
        )
    } else {
        darkColorScheme(
            primary = Ap.Accent, onPrimary = Ap.OnAccent,
            secondary = Ap.Blue, onSecondary = Ap.Bg,
            background = Ap.Bg, onBackground = Ap.Text,
            surface = Ap.Card, onSurface = Ap.Text,
            surfaceVariant = Ap.Card2, onSurfaceVariant = Ap.Muted,
            outline = Ap.Line, error = Ap.Red, onError = Ap.Bg
        )
    }
    MaterialTheme(colorScheme = colors, content = content)
}
