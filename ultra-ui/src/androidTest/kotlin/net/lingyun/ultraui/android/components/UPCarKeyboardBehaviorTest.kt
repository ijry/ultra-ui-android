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
 * `u-car-keyboard`: the Chinese/English mode toggle, key change events and backspace.
 */
@RunWith(AndroidJUnit4::class)
class UPCarKeyboardBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun startsInChineseAndTogglesToEnglish() {
        val changes = mutableListOf<String>()
        composeRule.setContent {
            UPCarKeyboard(UPCarKeyboardProps(), onChange = { changes += it })
        }

        // Chinese province prefixes are shown first.
        composeRule.onNodeWithTag("up-car-keyboard-key-京").performClick()

        // Toggle to the English/number plate.
        composeRule.onNodeWithTag("up-car-keyboard-mode").performClick()
        composeRule.onNodeWithTag("up-car-keyboard-key-Q").performClick()

        composeRule.runOnIdle { assertEquals(listOf("京", "Q"), changes) }
    }

    @Test
    fun backspaceFiresAndItReportsTheDeleteKey() {
        var backspaces = 0
        composeRule.setContent {
            UPCarKeyboard(UPCarKeyboardProps(), onBackspace = { backspaces += 1 })
        }

        composeRule.onNodeWithTag("up-car-keyboard-backspace").performClick()
        composeRule.runOnIdle { assertEquals(1, backspaces) }
    }
}
