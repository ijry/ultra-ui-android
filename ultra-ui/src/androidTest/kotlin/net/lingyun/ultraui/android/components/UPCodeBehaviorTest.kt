package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-code`: the verification-code countdown driven through its `start()` / `reset()` ref
 * methods, the once-per-second `change` emission and the startText/changeText/endText phases,
 * all advanced with the test clock so no wall-clock sleeping is involved.
 */
@RunWith(AndroidJUnit4::class)
class UPCodeBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private fun assertPrinted(expected: String) {
        composeRule.onNodeWithTag("up-code-text", useUnmergedTree = true).assertTextEquals(expected)
    }

    @Test
    fun startCountsDownEmitsChangeEachSecondAndEndsWithEndText() {
        composeRule.mainClock.autoAdvance = false
        val controller = UPCodeController()
        val changes = mutableListOf<Int>()
        var started = 0
        var ended = 0
        composeRule.setContent {
            UPCode(
                UPCodeProps(seconds = 3),
                controller = controller,
                onChange = { changes += it },
                onStart = { started += 1 },
                onEnd = { ended += 1 },
            )
        }

        // Idle before start.
        assertPrinted("获取验证码")
        composeRule.runOnIdle { assertTrue(controller.canGetCode) }

        composeRule.runOnIdle { controller.start() }
        // `start()` emits `start` and the initial `change(3)`, and disables the button.
        assertPrinted("3秒重新获取")
        composeRule.runOnIdle {
            assertEquals(1, started)
            assertEquals(listOf(3), changes)
            assertFalse(controller.canGetCode)
        }

        composeRule.mainClock.advanceTimeBy(1_000L)
        assertPrinted("2秒重新获取")
        composeRule.mainClock.advanceTimeBy(1_000L)
        assertPrinted("1秒重新获取")

        // The final tick stops the timer, fires `end`, and shows endText.
        composeRule.mainClock.advanceTimeBy(1_000L)
        assertPrinted("重新获取")
        composeRule.runOnIdle {
            assertEquals(listOf(3, 2, 1), changes)
            assertEquals(1, ended)
            assertTrue(controller.canGetCode)
        }
    }

    @Test
    fun resetStopsTheCountdownAndRestoresStartText() {
        composeRule.mainClock.autoAdvance = false
        val controller = UPCodeController()
        composeRule.setContent { UPCode(UPCodeProps(seconds = 5), controller = controller) }

        composeRule.runOnIdle { controller.start() }
        composeRule.mainClock.advanceTimeBy(1_000L)
        assertPrinted("4秒重新获取")

        composeRule.runOnIdle { controller.reset() }
        assertPrinted("获取验证码")
        composeRule.runOnIdle { assertTrue(controller.canGetCode) }

        // A stopped timer does not keep ticking.
        composeRule.mainClock.advanceTimeBy(2_000L)
        assertPrinted("获取验证码")
    }
}
