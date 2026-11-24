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
            Pal.TEXT, Pal.MUTED, Pal.WHITE, Pal.ACCENT, Pal.ON_ACCENT,
            Pal.ACCENT_SOFT, Pal.BUBBLE_ME, Pal.GREEN, Pal.RED, Pal.AMBER, Pal.BLUE
        )
        colors.forEach { assertTrue("Invalid color: $it", it.matches(Regex("#[0-9A-Fa-f]{6}"))) }
    }

    @Test fun primaryTextHasAccessibleContrast() {
        val original = Pal.lightMode
        try {
            Pal.lightMode = false
            assertTrue("Dark primary text contrast must be at least 4.5:1", contrast(Pal.TEXT, Pal.BG) >= 4.5)
            assertTrue("Dark muted text contrast must be at least 4.5:1", contrast(Pal.MUTED, Pal.BG) >= 4.5)
            assertTrue("Dark accent label contrast must be at least 4.5:1", contrast(Pal.ON_ACCENT, Pal.ACCENT) >= 4.5)
        } finally {
            Pal.lightMode = original
        }
    }

    @Test fun lightPaletteMaintainsReadableTextAndBubbles() {
        val original = Pal.lightMode
        try {
            Pal.lightMode = true
            assertTrue("Light primary text contrast must be at least 4.5:1", contrast(Pal.TEXT, Pal.BG) >= 4.5)
            assertTrue("Light muted text contrast must be at least 4.5:1", contrast(Pal.MUTED, Pal.BG) >= 4.5)
            assertTrue("Light accent label contrast must be at least 4.5:1", contrast(Pal.ON_ACCENT, Pal.ACCENT) >= 4.5)
            assertTrue("User message bubble text must remain readable", contrast(Pal.TEXT, Pal.BUBBLE_ME) >= 4.5)
        } finally {
            Pal.lightMode = original
        }
    }

    @Test fun textAndMutedTokensStayReadableAcrossEverySurfaceInBothThemes() {
        val original = Pal.lightMode
        try {
            for (light in listOf(false, true)) {
                Pal.lightMode = light
                val surfaces = listOf(Pal.BG, Pal.CARD, Pal.CARD2, Pal.RAISED)
                surfaces.forEach { surface ->
                    assertTrue("Primary text contrast failed on $surface (light=$light)", contrast(Pal.TEXT, surface) >= 4.5)
                    assertTrue("Muted text contrast failed on $surface (light=$light)", contrast(Pal.MUTED, surface) >= 4.5)
                }
                assertTrue("Selected control label contrast failed (light=$light)", contrast(Pal.ON_ACCENT, Pal.ACCENT) >= 4.5)
                assertTrue("User bubble text contrast failed (light=$light)", contrast(Pal.TEXT, Pal.BUBBLE_ME) >= 4.5)
            }
        } finally {
            Pal.lightMode = original
        }
    }

    @Test fun lightAndDarkModesUseDifferentSurfaces() {
        val original = Pal.lightMode
        try {
            Pal.lightMode = false
            val darkBackground = Pal.BG
            Pal.lightMode = true
            assertTrue("Light mode must use a different surface", darkBackground != Pal.BG)
        } finally {
            Pal.lightMode = original
        }
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
