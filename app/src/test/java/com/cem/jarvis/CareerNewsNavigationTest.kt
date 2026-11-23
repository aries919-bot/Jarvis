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
}
