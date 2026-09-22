package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-message-input`: cells rendered from `maxlength`, `change` on each edit and `finish`
 * once the length reaches `maxlength`, and the digit-only clipping the invisible field applies.
 */
@RunWith(AndroidJUnit4::class)
class UPMessageInputBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersOneCellPerMaxlengthAndEmitsChangeThenFinish() {
        val changes = mutableListOf<String>()
        var finished: String? = null
        composeRule.setContent {
            UPMessageInput(
                UPMessageInputProps(maxlength = 4),
                onChange = { changes += it },
                onFinish = { finished = it },
            )
        }

        composeRule.onNodeWithTag("up-message-input-cell-0").assertExists()
        composeRule.onNodeWithTag("up-message-input-cell-3").assertExists()

        composeRule.onNodeWithTag("up-message-input-field").performTextInput("12")
        composeRule.runOnIdle {
            assertEquals(listOf("12"), changes)
            assertEquals(null, finished)
        }

        composeRule.onNodeWithTag("up-message-input-field").performTextInput("34")
        composeRule.runOnIdle {
            assertEquals(listOf("12", "1234"), changes)
            assertEquals("1234", finished)
        }
    }

    @Test
    fun nonDigitsAreIgnoredAndInputStopsAtMaxlength() {
        val changes = mutableListOf<String>()
        composeRule.setContent {
            UPMessageInput(UPMessageInputProps(maxlength = 3), onChange = { changes += it })
        }

        composeRule.onNodeWithTag("up-message-input-field").performTextInput("1a2b3c4")
        composeRule.runOnIdle {
            // Letters filtered out, then clipped to three digits.
            assertEquals(listOf("123"), changes)
        }
    }
}
