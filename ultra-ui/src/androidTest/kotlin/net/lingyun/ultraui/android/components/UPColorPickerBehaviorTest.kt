package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-color-picker`: renders the picker surfaces, applies a common-colour tap and confirms a hex.
 * (Colour math is covered by the pure HSL/hex unit tests.)
 */
@RunWith(AndroidJUnit4::class)
class UPColorPickerBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun commonColorSelectionUpdatesValueAndConfirmEmitsHex() {
        val updates = mutableListOf<String>()
        var confirmed: String? = null
        composeRule.setContent {
            UPColorPicker(
                UPColorPickerProps(show = true, modelValue = "#ff0000", commonColors = listOf<UPRawValue>("#00ff00", "#0000ff")),
                onUpdateModelValue = { updates += it },
                onConfirm = { confirmed = it },
            )
        }

        composeRule.onNodeWithTag("up-color-picker").assertExists()
        composeRule.onNodeWithTag("up-color-picker-square").assertExists()
        composeRule.onNodeWithTag("up-color-picker-hue").assertExists()

        // Tapping the green swatch updates the value to green.
        composeRule.onNodeWithTag("up-color-picker-common-0").performClick()
        composeRule.runOnIdle { assertEquals("#00ff00", updates.lastOrNull()) }

        composeRule.onNodeWithTag("up-color-picker-confirm").performClick()
        composeRule.runOnIdle { assertEquals("#00ff00", confirmed) }
    }
}
