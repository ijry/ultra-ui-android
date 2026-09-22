package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-keyboard`: the bottom-sheet wrapper, its tooltip cancel/confirm and the inner keyboard's
 * change events, plus the mode-driven default tip.
 */
@RunWith(AndroidJUnit4::class)
class UPKeyboardBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun numberModeShowsTooltipAndBubblesChangeConfirmCancel() {
        val changes = mutableListOf<Any>()
        var confirmed = 0
        var cancelled = 0
        composeRule.setContent {
            UPKeyboard(
                UPKeyboardProps(show = true, mode = "number"),
                onChange = { changes += it },
                onConfirm = { confirmed += 1 },
                onCancel = { cancelled += 1 },
            )
        }

        composeRule.onNodeWithTag("up-keyboard-tooltip").assertExists()
        composeRule.onNodeWithText("数字键盘").assertExists()

        composeRule.onNodeWithTag("up-number-keyboard-key-8").performClick()
        composeRule.onNodeWithTag("up-keyboard-confirm").performClick()
        composeRule.onNodeWithTag("up-keyboard-cancel").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf<Any>(8), changes)
            assertEquals(1, confirmed)
            assertEquals(1, cancelled)
        }
    }

    @Test
    fun carModeHostsTheCarKeyboard() {
        composeRule.setContent {
            UPKeyboard(UPKeyboardProps(show = true, mode = "car"))
        }

        composeRule.onNodeWithText("车牌号键盘").assertExists()
        composeRule.onNodeWithTag("up-car-keyboard").assertExists()
    }

    @Test
    fun hiddenWhenShowIsFalse() {
        composeRule.setContent { UPKeyboard(UPKeyboardProps(show = false)) }
        composeRule.onNodeWithTag("up-keyboard-tooltip").assertDoesNotExist()
    }
}
