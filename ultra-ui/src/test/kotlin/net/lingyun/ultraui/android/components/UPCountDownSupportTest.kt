package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Every expectation here was produced by running upstream's `u-count-down/utils.js`
 * unchanged under node, so the numbers are transcribed rather than reasoned about.
 */
class UPCountDownSupportTest {
    @Test
    fun timeDataSplitsMillisecondsIntoCalendarBuckets() {
        assertEquals(UPCountDownTime(0, 0, 0, 0, 0), upCountDownParseTimeData(0L))
        assertEquals(UPCountDownTime(1, 1, 1, 1, 123), upCountDownParseTimeData(90_061_123L))
        assertEquals(UPCountDownTime(0, 0, 1, 1, 0), upCountDownParseTimeData(61_000L))
        // `getRemainTime()` clamps at zero before `parseTimeData` ever sees the value.
        assertEquals(UPCountDownTime(0, 0, 0, 0, 0), upCountDownParseTimeData(-5_000L))
    }

    @Test
    fun missingUnitsFoldIntoTheNextSmallerOne() {
        assertEquals("00:01:01", upCountDownParseFormat("HH:mm:ss", upCountDownParseTimeData(61_000L)))
        assertEquals("01:01", upCountDownParseFormat("mm:ss", upCountDownParseTimeData(61_000L)))
        // No `DD`, so a day becomes 24 extra hours.
        assertEquals("25:01:01", upCountDownParseFormat("HH:mm:ss", upCountDownParseTimeData(90_061_000L)))
        assertEquals("01天01时01分01秒", upCountDownParseFormat("DD天HH时mm分ss秒", upCountDownParseTimeData(90_061_000L)))
        // No `DD` and no `HH`: 25 hours collapse into 1501 minutes.
        assertEquals("1501:01", upCountDownParseFormat("mm:ss", upCountDownParseTimeData(90_061_000L)))
        assertEquals("90061", upCountDownParseFormat("ss", upCountDownParseTimeData(90_061_000L)))
        assertEquals("61:01", upCountDownParseFormat("mm:ss", upCountDownParseTimeData(3_661_123L)))
        assertEquals("24:00:00", upCountDownParseFormat("HH:mm:ss", upCountDownParseTimeData(86_400_000L)))
        assertEquals("01", upCountDownParseFormat("DD", upCountDownParseTimeData(86_400_000L)))
    }

    @Test
    fun millisecondsPadToThreeDigitsAndAbsorbEverythingBelowThem() {
        assertEquals("01:01:01.123", upCountDownParseFormat("HH:mm:ss.SSS", upCountDownParseTimeData(3_661_123L)))
        assertEquals("01:123", upCountDownParseFormat("ss:SSS", upCountDownParseTimeData(1_123L)))
        assertEquals("3661123", upCountDownParseFormat("SSS", upCountDownParseTimeData(3_661_123L)))
        // A 30 day budget folded all the way down overflows a 32 bit accumulator.
        assertEquals("2592000000", upCountDownParseFormat("SSS", upCountDownParseTimeData(2_592_000_000L)))
        assertEquals("007", upCountDownParseFormat("SSS", upCountDownParseTimeData(7L)))
    }

    @Test
    fun onlyTheFirstOccurrenceOfEachTokenIsSubstituted() {
        // Upstream calls `String.prototype.replace` with a string needle.
        assertEquals("00:01:01 HH", upCountDownParseFormat("HH:mm:ss HH", upCountDownParseTimeData(61_000L)))
        assertEquals("00DD", upCountDownParseFormat("DDDD", upCountDownParseTimeData(61_000L)))
    }

    @Test
    fun sameSecondGatingAndTickIntervalsMatchUpstream() {
        assertTrue(upCountDownIsSameSecond(1_999L, 1_000L))
        assertFalse(upCountDownIsSameSecond(2_000L, 1_999L))
        assertTrue(upCountDownIsSameSecond(0L, 999L))
        assertEquals(30L, upCountDownTickIntervalMillis(millisecond = false))
        assertEquals(50L, upCountDownTickIntervalMillis(millisecond = true))
    }

    @Test
    fun theControllerIsInertUntilItReachesAComposition() {
        val controller = UPCountDownController()
        controller.start()
        controller.pause()
        controller.reset()

        val calls = mutableListOf<String>()
        controller.attach(
            UPCountDownController.Binding(
                start = { calls += "start" },
                pause = { calls += "pause" },
                reset = { calls += "reset" },
            ),
        )
        controller.start()
        controller.pause()
        controller.reset()
        assertEquals(listOf("start", "pause", "reset"), calls)

        controller.detach()
        controller.start()
        assertEquals(listOf("start", "pause", "reset"), calls)
    }
}
