package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class RemindersParserTest {
    private fun fixedNow(): Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(2026, Calendar.NOVEMBER, 26, 8, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }

    @Test
    fun parsesRelativeReminder() {
        val now = fixedNow()
        val parsed = Reminders.parse("10 dakika sonra x hatırlat", now)
        assertNotNull(parsed)
        assertEquals(now.timeInMillis + 10 * 60_000L, parsed!!.time)
        assertEquals("x", result.text)
    }

    @Test
    fun parsesTomorrowAtNine() {
        val parsed = Reminders.parse("yarın 9'da x hatırlat", fixedNow())
        assertNotNull(parsed)
        val result = requireNotNull(parsed)
        val actual = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = result.time
        }
        assertEquals(27, actual.get(Calendar.DAY_OF_MONTH))
        assertEquals(9, actual.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, actual.get(Calendar.MINUTE))
        assertEquals("x", parsed.text)
    }

    @Test
    fun parsesClockTimeWithoutReminderKeyword() {
        val parsed = Reminders.parse("07:30 alarm", fixedNow())
        assertNotNull(parsed)
        val actual = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = parsed!!.time
        }
        assertEquals(7, actual.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, actual.get(Calendar.MINUTE))
        assertEquals("alarm", result.text)
    }

    @Test
    fun rejectsMalformedTimeInput() {
        assertNull(Reminders.parse("yarın x hatırlat", fixedNow()))
        assertNull(Reminders.parse("hatırlat bana bir şey", fixedNow()))
    }
}
