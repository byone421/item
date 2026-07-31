package com.zenonewrong.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

class ExpiryNotificationTest {
    @Test
    fun summaryOnlyIncludesUpcomingItems() {
        assertEquals("即将到期 2 件", buildExpirySummary(2))
        assertNull(buildExpirySummary(0))
    }

    @Test
    fun delayTargetsNextNineAm() {
        val zone = ZoneId.of("Asia/Shanghai")
        val beforeNine = ZonedDateTime.of(2026, 7, 27, 8, 30, 0, 0, zone)
        val atNine = ZonedDateTime.of(2026, 7, 27, 9, 0, 0, 0, zone)

        assertEquals(Duration.ofMinutes(30), delayUntilNextReminder(beforeNine))
        assertEquals(Duration.ofHours(24), delayUntilNextReminder(atNine))
    }

    @Test
    fun enabledReminderRangesAreIndependent() {
        val daysByTag = mapOf("yellow" to 3, "blue" to 7, "green" to 10)

        assertEquals(listOf(0 until 3), reminderRanges(daysByTag, setOf("yellow")))
        assertEquals(listOf(3 until 7), reminderRanges(daysByTag, setOf("blue")))
        assertEquals(
            listOf(0 until 3, 7 until 10),
            reminderRanges(daysByTag, setOf("yellow", "green"))
        )
    }
}
