package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-number-keyboard`: the key set per mode, the numeric coercion on change and backspace.
 */
@RunWith(AndroidJUnit4::class)
class UPNumberKeyboardBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun numberModeReportsNumbersAndBackspace() {
        val changes = mutableListOf<Any>()
        var backspaces = 0
        composeRule.setContent {
            UPNumberKeyboard(
                UPNumberKeyboardProps(mode = "number"),
                onChange = { changes += it },
                onBackspace = { backspaces += 1 },
            )
        }

        composeRule.onNodeWithTag("up-number-keyboard-key-5").performClick()
        composeRule.onNodeWithTag("up-number-keyboard-key-.").performClick()
        composeRule.onNodeWithTag("up-number-keyboard-backspace").performClick()
        composeRule.runOnIdle {
            // 5 becomes an Int, the dot stays a string.
            assertEquals(listOf<Any>(5, "."), changes)
            assertEquals(1, backspaces)
        }
    }

    @Test
    fun cardModeExposesTheXKeyAndKeepsItAString() {
        val changes = mutableListOf<Any>()
        composeRule.setContent {
            UPNumberKeyboard(UPNumberKeyboardProps(mode = "card"), onChange = { changes += it })
        }

        composeRule.onNodeWithTag("up-number-keyboard-key-X").performClick()
        composeRule.onNodeWithTag("up-number-keyboard-key-7").performClick()
        composeRule.runOnIdle {
            // card mode keeps everything as strings (dotDisabled defaults false but mode != number).
            assertEquals(listOf<Any>("X", 7), changes)
        }
    }
}
