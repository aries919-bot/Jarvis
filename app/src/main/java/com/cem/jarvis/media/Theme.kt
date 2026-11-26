package com.cem.jarvis.media

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Aries için ortak Material 3 renk ve yüzey sistemi. */
internal object Ap {
    val Bg = Color(0xFF0A111A)
    val Card = Color(0xFF111D2A)
    val Card2 = Color(0xFF18283A)
    val Raised = Color(0xFF21364A)
    val Line = Color(0xFF2C4054)
    val Text = Color(0xFFF3F7FC)
    val Muted = Color(0xFFA5B6C8)
    val Accent = Color(0xFF58D6CC)
    val OnAccent = Color(0xFF062522)
    val AccentSoft = Color(0xFF163C40)
    val AccentSel = Color(0xFF22565A)
    val Green = Color(0xFF55D6A5)
    val Amber = Color(0xFFF4C36A)
    val Red = Color(0xFFFF7777)
    val Blue = Color(0xFF83C5F4)
}

@Composable
internal fun AriesMaterialTheme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary = Ap.Accent,
        onPrimary = Ap.OnAccent,
        secondary = Ap.Blue,
        onSecondary = Ap.Bg,
        background = Ap.Bg,
        onBackground = Ap.Text,
        surface = Ap.Card,
        onSurface = Ap.Text,
        surfaceVariant = Ap.Card2,
        onSurfaceVariant = Ap.Muted,
        outline = Ap.Line,
        error = Ap.Red,
        onError = Ap.Bg
    )
    MaterialTheme(colorScheme = colors, content = content)
}
