package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * uview exposes `selectionStart`/`selectionEnd` to preselect a range on focus and
 * `cursor` to place the caret. Both default to -1 (unset). Holding the text as a bare
 * String cannot carry a selection at all, so those props end up silent no-ops.
 */
@RunWith(AndroidJUnit4::class)
class UPInputSelectionBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private fun selectionOf(tag: String): TextRange? =
        composeRule.onNodeWithTag(tag).fetchSemanticsNode()
            .config.getOrNull(SemanticsProperties.TextSelectionRange)

    @Test
    fun inputAppliesSelectionStartAndEndOnFocus() {
        composeRule.setContent {
            UPInput(
                UPInputProps(
                    value = "hello world",
                    focus = true,
                    selectionStart = 0,
                    selectionEnd = 5,
                ),
            )
        }
        composeRule.runOnIdle {}
        assertEquals(TextRange(0, 5), selectionOf("up-input-field"))
    }

    @Test
    fun inputPlacesTheCaretAtCursorWhenNoRangeIsGiven() {
        composeRule.setContent {
            UPInput(UPInputProps(value = "abcdef", focus = true, cursor = 3))
        }
        composeRule.runOnIdle {}
        assertEquals(TextRange(3, 3), selectionOf("up-input-field"))
    }

    @Test
    fun textareaAppliesSelectionStartAndEndOnFocus() {
        composeRule.setContent {
            UPTextarea(
                UPTextareaProps(
                    value = "abcdefgh",
                    focus = true,
                    selectionStart = 2,
                    selectionEnd = 6,
                ),
            )
        }
        composeRule.runOnIdle {}
        assertEquals(TextRange(2, 6), selectionOf("up-textarea-field"))
    }

    @Test
    fun unsetSelectionLeavesTheCaretAtTheEnd() {
        composeRule.setContent {
            UPInput(UPInputProps(value = "abcd", focus = true))
        }
        composeRule.runOnIdle {}
        // Defaults are -1/-1, so uview leaves the caret alone; Compose parks it at the end.
        assertEquals(TextRange(4, 4), selectionOf("up-input-field"))
    }

    @Test
    fun outOfRangeSelectionIsClampedToTheText() {
        composeRule.setContent {
            UPInput(UPInputProps(value = "abc", focus = true, selectionStart = 1, selectionEnd = 99))
        }
        composeRule.runOnIdle {}
        assertEquals(TextRange(1, 3), selectionOf("up-input-field"))
    }
}
