package com.cem.jarvis

/** Keeps each Haber filter connected to a search that stays within Haber. */
internal object CareerNewsNavigation {
    fun queryForFilter(filter: Int): Int = when (filter) {
        1 -> 6  // Kura / süreç sonuçları
        2 -> 9  // Staj / öğrenci programları
        else -> 10 // Güncel kamu personel alım haberleri
    }
}

/** The listings picker contains only real vacancy categories. Kura/news/program searches stay in Haber. */
internal object CareerListingCategoryMap {
    private val queryCategoryIds = listOf(0, 1, 2, 3, 4, 5, 7, 8)

    fun queryCategoryForPosition(position: Int): Int =
        queryCategoryIds.getOrElse(position) { 0 }

    fun positionForQueryCategory(category: Int): Int =
        queryCategoryIds.indexOf(category).takeIf { it >= 0 } ?: 0

    fun queryCategories(): List<Int> = queryCategoryIds.toList()
}
