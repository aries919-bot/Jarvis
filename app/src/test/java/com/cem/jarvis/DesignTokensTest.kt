package com.cem.jarvis

import org.junit.Assert.assertTrue
import org.junit.Test

class DesignTokensTest {
    @Test fun touchTargetsMeetAndroidMinimum() {
        assertTrue("Interactive controls must be at least 48dp", Tok.MIN_TOUCH_DP >= 48)
    }

    @Test fun paletteValuesAreValidHexColors() {
        val colors = listOf(
            Pal.BG, Pal.CARD, Pal.CARD2, Pal.RAISED, Pal.LINE,
            Pal.TEXT, Pal.MUTED, Pal.ACCENT, Pal.ON_ACCENT,
            Pal.GREEN, Pal.RED, Pal.AMBER, Pal.BLUE
        )
        colors.forEach { assertTrue("Invalid color: $it", it.matches(Regex("#[0-9A-Fa-f]{6}"))) }
    }

    @Test fun primaryTextHasAccessibleContrast() {
        assertTrue("Primary text contrast must be at least 4.5:1", contrast(Pal.TEXT, Pal.BG) >= 4.5)
        assertTrue("Muted text contrast must be at least 4.5:1", contrast(Pal.MUTED, Pal.BG) >= 4.5)
        assertTrue("Accent label contrast must be at least 4.5:1", contrast(Pal.ON_ACCENT, Pal.ACCENT) >= 4.5)
    }

    private fun contrast(foreground: String, background: String): Double {
        fun luminance(hex: String): Double {
            val rgb = hex.removePrefix("#").chunked(2).map { it.toInt(16) / 255.0 }
            val linear = rgb.map { if (it <= 0.04045) it / 12.92 else Math.pow((it + 0.055) / 1.055, 2.4) }
            return 0.2126 * linear[0] + 0.7152 * linear[1] + 0.0722 * linear[2]
        }
        val a = luminance(foreground)
        val b = luminance(background)
        return (maxOf(a, b) + 0.05) / (minOf(a, b) + 0.05)
    }
}
