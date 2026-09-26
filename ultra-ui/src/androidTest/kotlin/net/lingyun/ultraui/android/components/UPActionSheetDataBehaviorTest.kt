package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
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
 * `u-action-sheet-data`: opens the action sheet from the disabled field and emits the selected value.
 */
@RunWith(AndroidJUnit4::class)
class UPActionSheetDataBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun opensSheetAndSelectsValue() {
        var picked: UPRawValue = null
        val options: List<UPRawValue> = listOf(
            mapOf<String, UPRawValue>("value" to 1, "name" to "北京"),
            mapOf<String, UPRawValue>("value" to 2, "name" to "上海"),
        )
        composeRule.setContent {
            UPActionSheetData(
                props = UPActionSheetDataProps(title = "请选择城市", options = options),
                onUpdateModelValue = { picked = it },
            )
        }

        composeRule.onNodeWithTag("up-action-sheet-data").assertIsDisplayed()
        composeRule.onNodeWithTag("up-action-sheet-data-trigger").performClick()
        composeRule.onNodeWithText("上海").performClick()
        composeRule.runOnIdle { assertEquals(2, picked) }
    }
}
