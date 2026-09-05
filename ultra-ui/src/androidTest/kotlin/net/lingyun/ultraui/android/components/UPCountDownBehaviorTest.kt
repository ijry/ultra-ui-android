package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.upTestTag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-count-down`: the imperative `start()` / `pause()` / `reset()` ref methods, the
 * macro/micro tick split and the format degradation chain, all driven through the test
 * clock so no wall-clock sleeping is involved.
 */
@RunWith(AndroidJUnit4::class)
class UPCountDownBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private fun assertPrinted(expected: String) {
        composeRule.onNodeWithTag("up-count-down-text", useUnmergedTree = true).assertTextEquals(expected)
    }

    @Test
    fun autoStartCountsDownWithoutAnyImperativeCall() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { UPCountDown(UPCountDownProps(time = 61_000)) }

        assertPrinted("00:01:01")
        // Landing mid-second keeps the assertion clear of frame quantisation.
        composeRule.mainClock.advanceTimeBy(3_500L)
        assertPrinted("00:00:57")
    }

    @Test
    fun autoStartFalseHoldsTheInitialValueUntilStartIsCalled() {
        composeRule.mainClock.autoAdvance = false
        val controller = UPCountDownController()
        composeRule.setContent { UPCountDown(UPCountDownProps(time = 61_000, autoStart = false), controller = controller) }

        composeRule.mainClock.advanceTimeBy(5_000L)
        assertPrinted("00:01:01")

        composeRule.runOnIdle { controller.start() }
        composeRule.mainClock.advanceTimeBy(3_500L)
        assertPrinted("00:00:57")
    }

    @Test
    fun pauseFreezesTheClockAndStartResumesFromTheRemainder() {
        composeRule.mainClock.autoAdvance = false
        val controller = UPCountDownController()
        composeRule.setContent { UPCountDown(UPCountDownProps(time = 61_000), controller = controller) }

        composeRule.mainClock.advanceTimeBy(3_500L)
        assertPrinted("00:00:57")

        composeRule.runOnIdle { controller.pause() }
        composeRule.mainClock.advanceTimeBy(9_000L)
        // Paused time is not deducted, unlike a deadline that keeps running in the background.
        assertPrinted("00:00:57")

        composeRule.runOnIdle { controller.start() }
        composeRule.mainClock.advanceTimeBy(1_000L)
        assertPrinted("00:00:56")
    }

    @Test
    fun startIsIgnoredWhileTheClockIsAlreadyRunning() {
        composeRule.mainClock.autoAdvance = false
        val controller = UPCountDownController()
        composeRule.setContent { UPCountDown(UPCountDownProps(time = 61_000), controller = controller) }

        composeRule.mainClock.advanceTimeBy(3_500L)
        // `if (this.runing) return` — a redundant start must not re-base the deadline.
        composeRule.runOnIdle { controller.start() }
        composeRule.mainClock.advanceTimeBy(1_000L)
        assertPrinted("00:00:56")
    }

    @Test
    fun resetRestoresTheFullTimeAndObeysAutoStart() {
        composeRule.mainClock.autoAdvance = false
        val paused = UPCountDownController()
        val running = UPCountDownController()
        composeRule.setContent {
            UPCountDown(UPCountDownProps(time = 61_000, autoStart = false), controller = paused)
            UPCountDown(UPCountDownProps(time = 41_000), controller = running)
        }

        composeRule.runOnIdle { paused.start() }
        composeRule.mainClock.advanceTimeBy(3_500L)
        composeRule.onNodeWithText("00:00:57").assertExists()
        composeRule.onNodeWithText("00:00:37").assertExists()

        composeRule.runOnIdle {
            paused.reset()
            running.reset()
        }
        composeRule.onNodeWithText("00:01:01").assertExists()
        composeRule.onNodeWithText("00:00:41").assertExists()

        composeRule.mainClock.advanceTimeBy(3_500L)
        // `autoStart` false stays parked after a reset; true starts ticking again.
        composeRule.onNodeWithText("00:01:01").assertExists()
        composeRule.onNodeWithText("00:00:37").assertExists()
    }

    @Test
    fun theMacroTickRepaintsOncePerSecondWhileTheMicroTickIsContinuous() {
        composeRule.mainClock.autoAdvance = false
        val macro = mutableListOf<UPCountDownTime>()
        val micro = mutableListOf<UPCountDownTime>()
        composeRule.setContent {
            UPCountDown(UPCountDownProps(time = 5_000), onChange = { macro += it })
            UPCountDown(UPCountDownProps(time = 5_000, millisecond = true, format = "ss:SSS"), onChange = { micro += it })
        }

        composeRule.mainClock.advanceTimeBy(600L)
        composeRule.runOnIdle {
            // `macroTick` gates on `isSameSecond`, so only the 5 -> 4 crossing shows up.
            assertTrue("macro emitted $macro", macro.size <= 3)
            assertTrue("micro emitted ${micro.size} updates", micro.size >= 8)
        }
    }

    @Test
    fun finishFiresOnceTheRemainderReachesZero() {
        composeRule.mainClock.autoAdvance = false
        var finished = 0
        composeRule.setContent { UPCountDown(UPCountDownProps(time = 1_200), onFinish = { finished += 1 }) }

        composeRule.mainClock.advanceTimeBy(600L)
        composeRule.runOnIdle { assertEquals(0, finished) }

        composeRule.mainClock.advanceTimeBy(1_000L)
        assertPrinted("00:00:00")
        composeRule.runOnIdle { assertTrue("finish should have fired, got $finished", finished >= 1) }

        // The tick stops at zero rather than running negative.
        composeRule.mainClock.advanceTimeBy(2_000L)
        assertPrinted("00:00:00")
    }

    @Test
    fun theFormatFoldsMissingUnitsAndTheSlotReceivesTheSplitTime() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPCountDown(UPCountDownProps(time = 90_061_000, format = "mm:ss", autoStart = false))
            UPCountDown(UPCountDownProps(time = 90_061_000, autoStart = false)) { time ->
                BasicText("${time.days}天${time.hours}时", modifier = Modifier.upTestTag("count-down-slot"))
            }
        }

        // No `DD` and no `HH`: 25 hours collapse into 1501 minutes.
        composeRule.onNodeWithText("1501:01").assertExists()
        composeRule.onNodeWithTag("up-count-down-slot", useUnmergedTree = true).assertTextEquals("1天1时")
    }
}
