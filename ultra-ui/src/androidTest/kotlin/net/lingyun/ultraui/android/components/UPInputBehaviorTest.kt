package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPInputBehaviorTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun inputEmitsInputChangeAndClearAndHonorsMaxLength() {
        val inputs = mutableListOf<String>()
        val changes = mutableListOf<String>()
        var clears = 0
        composeRule.setContent {
            UPInput(
                UPInputProps(clearable = true, onlyClearableOnFocused = false, maxlength = 4),
                onInput = { inputs += it },
                onChange = { changes += it },
                onClear = { clears++ },
            )
        }

        composeRule.onNodeWithTag("up-input-field").performTextInput("12345")
        composeRule.onNodeWithTag("up-input-field").performImeAction()
        composeRule.onNodeWithTag("up-input-clear").performClick()
        composeRule.runOnIdle {
            assertEquals("", inputs.last())
            assertTrue(changes.contains("1234"))
            assertEquals(1, clears)
        }
    }

    @Test
    fun inputSuppressesEditingWhenDisabledOrReadonly() {
        var disabledInputs = 0
        var readonlyInputs = 0
        composeRule.setContent {
            Column {
                UPInput(UPInputProps(disabled = true), onInput = { disabledInputs++ })
                UPInput(UPInputProps(readonly = true), onInput = { readonlyInputs++ })
            }
        }

        composeRule.onAllNodesWithTag("up-input-field")[0].assertIsNotEnabled()
        runCatching { composeRule.onAllNodesWithTag("up-input-field")[0].performTextInput("x") }
        runCatching { composeRule.onAllNodesWithTag("up-input-field")[1].performTextInput("x") }
        composeRule.runOnIdle {
            assertEquals(0, disabledInputs)
            assertEquals(0, readonlyInputs)
        }
    }

    @Test
    fun passwordToggleAndTextareaCountAreVisibleAndInteractive() {
        var textChanges = 0
        composeRule.setContent {
            Column {
                UPInput(
                    UPInputProps(password = true, passwordVisibilityToggle = true, value = "secret"),
                )
                UPTextarea(
                    UPTextareaProps(value = "abc", count = true, maxlength = 5),
                    onChange = { textChanges++ },
                )
            }
        }

        composeRule.onNodeWithTag("up-input-password-toggle").assertExists().performClick()
        composeRule.onNodeWithTag("up-textarea-count").assertTextContains("3/5")
        composeRule.onNodeWithTag("up-textarea-field").performTextInput("d")
        composeRule.onNodeWithTag("up-textarea-field").performImeAction()
        composeRule.runOnIdle { assertTrue(textChanges >= 1) }
    }

    @Test
    fun searchEmitsIconClearSearchAndCustomEvents() {
        var search = 0
        var custom = 0
        var clear = 0
        var icon = 0
        composeRule.setContent {
            UPSearch(
                UPSearchProps(value = "abc", clearabled = true, onlyClearableOnFocused = false),
                onSearch = { search++ },
                onCustom = { custom++ },
                onClear = { clear++ },
                onClickIcon = { icon++ },
            )
        }

        composeRule.onNodeWithTag("up-search-icon").performClick()
        composeRule.onNodeWithTag("up-search-field").performImeAction()
        composeRule.onNodeWithTag("up-search-clear").performClick()
        composeRule.onNodeWithTag("up-search-action").performClick()
        composeRule.runOnIdle {
            assertEquals(1, icon)
            assertEquals(1, search)
            assertEquals(1, clear)
            assertEquals(1, custom)
        }
    }

    @Test
    fun codeInputMasksDigitsAndFinishesAtMaxLength() {
        val inputs = mutableListOf<String>()
        val changes = mutableListOf<String>()
        val finishes = mutableListOf<String>()
        composeRule.setContent {
            UPCodeInput(
                UPCodeInputProps(maxlength = 4, dot = true, disabledDot = true),
                onInput = { inputs += it },
                onChange = { changes += it },
                onFinish = { finishes += it },
            )
        }

        composeRule.onNodeWithTag("up-code-input-field").performTextInput("12.34")
        composeRule.runOnIdle {
            assertEquals("1234", inputs.last())
            assertEquals("1234", changes.last())
            assertEquals(listOf("1234"), finishes)
        }
        composeRule.onNodeWithTag("up-code-input-cell-0").assertTextContains("•")
    }
}
