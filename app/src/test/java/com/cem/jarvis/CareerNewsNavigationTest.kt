package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Test

class CareerNewsNavigationTest {
    @Test
    fun processFilterUsesProcessSearch() {
        assertEquals(6, CareerNewsNavigation.queryForFilter(1))
    }

    @Test
    fun studentProgramFilterUsesStudentSearch() {
        assertEquals(9, CareerNewsNavigation.queryForFilter(2))
    }

    @Test
    fun generalAndOtherNewsFiltersUsePublicRecruitmentNewsSearch() {
        listOf(0, 3, 4, 5).forEach { filter ->
            assertEquals(10, CareerNewsNavigation.queryForFilter(filter))
        }
    }

    @Test
    fun unknownFilterNeverRoutesToAnotherAppSection() {
        assertEquals(10, CareerNewsNavigation.queryForFilter(Int.MIN_VALUE))
    }

    @Test
    fun listingsPickerContainsOnlyVacancyCategories() {
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 7, 8), CareerListingCategoryMap.queryCategories())
        assertFalse(CareerListingCategoryMap.queryCategories().contains(6))
        assertFalse(CareerListingCategoryMap.queryCategories().contains(9))
    }

    @Test
    fun oldNewsOnlyPickerPositionsResetInsteadOfRedirecting() {
        assertEquals(0, CareerListingCategoryMap.positionForQueryCategory(6))
        assertEquals(0, CareerListingCategoryMap.positionForQueryCategory(9))
        assertEquals(6, CareerListingCategoryMap.positionForQueryCategory(7))
        assertEquals(7, CareerListingCategoryMap.positionForQueryCategory(8))
    }

    @Test
    fun listingPickerPositionMapsBackToVacancyCategory() {
        assertEquals(7, CareerListingCategoryMap.queryCategoryForPosition(6))
        assertEquals(8, CareerListingCategoryMap.queryCategoryForPosition(7))
        assertEquals(0, CareerListingCategoryMap.queryCategoryForPosition(99))
    }
}
