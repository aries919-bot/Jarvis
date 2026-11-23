package com.cem.jarvis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FinanceDataTest {
    @Test
    fun parsesTurkishGoldAndCurrencyWithSourceAndFreshness() {
        val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.format(java.util.Date())
        val json = """
            {
              "date":"2026-11-23",
              "updated_at":"__NOW__",
              "is_stale":false,
              "sources":["tcmb","truncgil"],
              "try":{
                "usd":{"bid":50.40,"ask":50.49,"source":"tcmb","updated_at":"__NOW__","stale":false},
                "xau_gram":{"bid":5730.97,"ask":5731.95,"source":"truncgil","updated_at":"__NOW__","stale":false}
              }
            }
        """.trimIndent().replace("__NOW__", now)

        val snapshot = FinanceData.parseTurkishMarket(json)

        assertEquals("2026-11-23", snapshot.date)
        assertFalse(snapshot.stale)
        assertEquals(listOf("tcmb", "truncgil"), snapshot.sources)
        assertEquals(2, snapshot.prices.size)
        assertEquals(5730.97, snapshot.prices.first { it.id == "xau_gram" }.bid, 0.001)
    }

    @Test
    fun staleProviderOrInstrumentIsSurfacedAsStale() {
        val json = """
            {
              "date":"2026-11-20",
              "updated_at":"2026-11-20T12:00:00.000Z",
              "is_stale":true,
              "sources":["truncgil"],
              "try":{"xau_gram":{"bid":5000,"ask":5100,"source":"truncgil","updated_at":"2026-11-20T12:00:00.000Z","stale":false}}
            }
        """.trimIndent()

        assertTrue(FinanceData.parseTurkishMarket(json).stale)
    }

    @Test
    fun parsesFinnhubQuoteWithoutInventingMissingValues() {
        val quote = FinanceData.parseFinnhubQuote(
            "THYAO.IS",
            """{"c":312.5,"d":4.2,"dp":1.36,"pc":308.3,"t":1795262400}"""
        )

        assertEquals(312.5, quote.current, 0.001)
        assertEquals(1.36, quote.changePercent!!, 0.001)
        assertEquals(1795262400L, quote.timestampSeconds)
    }

    @Test
    fun currentTimestampIsFreshAndOldTimestampIsStale() {
        val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.format(java.util.Date())
        assertFalse(FinanceData.timestampIsStale(now))
        assertTrue(FinanceData.timestampIsStale("2020-01-01T00:00:00.000Z"))
    }

    @Test
    fun symbolValidationRejectsUrlOrWhitespaceInput() {
        assertTrue(FinanceData.isValidSymbol("THYAO.IS"))
        assertTrue(FinanceData.isValidSymbol("BIST:THYAO"))
        assertFalse(FinanceData.isValidSymbol("https://example.com"))
        assertFalse(FinanceData.isValidSymbol("THYAO IS"))
        assertFalse(FinanceData.isValidSymbol(""))
    }
}
