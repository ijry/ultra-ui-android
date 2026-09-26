package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-picker-data`: opens the single-column picker from the disabled field. Selection resolution is
 * covered by the pure `upPickerDataSelection` unit tests.
 */
@RunWith(AndroidJUnit4::class)
class UPPickerDataBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun opensPickerFromTrigger() {
        val options: List<UPRawValue> = listOf(
            mapOf<String, UPRawValue>("id" to 10, "name" to "语文"),
            mapOf<String, UPRawValue>("id" to 20, "name" to "数学"),
        )
        composeRule.setContent {
            UPPickerData(props = UPPickerDataProps(title = "请选择科目", options = options, modelValue = 20))
        }

        composeRule.onNodeWithTag("up-picker-data").assertIsDisplayed()
        composeRule.onNodeWithTag("up-picker-data-trigger").performClick()
        composeRule.onNodeWithTag("up-picker").assertExists()
    }
}
