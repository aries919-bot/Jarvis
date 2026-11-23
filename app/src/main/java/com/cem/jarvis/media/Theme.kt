package com.cem.jarvis.media

import androidx.compose.ui.graphics.Color

/**
 * Film & Dizi (Compose) renkleri. Ana uygulamadaki Pal (Views.kt) ile aynı değerler;
 * böylece üç bölüm (Ana, Kariyer, Film & Dizi) tek bir görsel dil kullanır.
 * Renk değiştirmek için önce Views.kt içindeki Pal'ı, sonra burayı güncelle.
 */
internal object Ap {
    val Bg = Color(0xFF0B0D12)
    val Card = Color(0xFF12151C)
    val Card2 = Color(0xFF1A1F2B)
    val Raised = Color(0xFF222838)
    val Line = Color(0xFF2B3245)
    val Text = Color(0xFFEDEFF5)
    val Muted = Color(0xFF9AA3B8)
    val Accent = Color(0xFFC4B5FD)
    val OnAccent = Color(0xFF17122B)
    val AccentSoft = Color(0xFF272046)
    val AccentSel = Color(0xFF3B3366)
    val Amber = Color(0xFFF5B455)
    val Red = Color(0xFFFF6B6B)
    val Green = Color(0xFF34D399)
}
