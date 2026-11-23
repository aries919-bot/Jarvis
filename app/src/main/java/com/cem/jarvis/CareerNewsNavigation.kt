package com.cem.jarvis

/** Keeps each Haber filter connected to a search that stays within Haber. */
internal object CareerNewsNavigation {
    fun queryForFilter(filter: Int): Int = when (filter) {
        1 -> 6  // Kura / süreç sonuçları
        2 -> 9  // Staj / öğrenci programları
        else -> 10 // Güncel kamu personel alım haberleri
    }
}
