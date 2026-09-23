package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-dragsort`: renders one row per item using the default `label`. (Reorder correctness is
 * covered by the pure `upDragsortMove` unit test; gesture-driven reordering needs a device.)
 */
@RunWith(AndroidJUnit4::class)
class UPDragsortBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersOneRowPerItemWithLabels() {
        composeRule.setContent {
            UPDragsort(
                UPDragsortProps(
                    initialList = listOf(
                        mapOf("id" to 1, "label" to "第一项"),
                        mapOf("id" to 2, "label" to "第二项"),
                        mapOf("id" to 3, "label" to "第三项"),
                    ),
                ),
            )
        }

        composeRule.onNodeWithTag("up-dragsort").assertExists()
        composeRule.onNodeWithTag("up-dragsort-item-0").assertExists()
        composeRule.onNodeWithTag("up-dragsort-item-2").assertExists()
        composeRule.onNodeWithText("第一项").assertExists()
        composeRule.onNodeWithText("第三项").assertExists()
    }
}
