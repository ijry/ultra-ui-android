package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `up-float-button`: the main click, the `isMenu` list toggle and `item-click` payload.
 */
@RunWith(AndroidJUnit4::class)
class UPFloatButtonBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun plainButtonFiresClickAndNeverOpensAList() {
        var clicks = 0
        composeRule.setContent {
            UPFloatButton(UPFloatButtonProps(), modifier = Modifier.fillMaxSize(), onClick = { clicks += 1 })
        }

        composeRule.onNodeWithTag("up-float-button-main").performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
        composeRule.onNodeWithTag("up-float-button-list").assertDoesNotExist()
    }

    @Test
    fun menuTogglesTheListAndReportsItemClicksWithIndex() {
        val itemClicks = mutableListOf<Pair<String, Int>>()
        composeRule.setContent {
            UPFloatButton(
                UPFloatButtonProps(
                    isMenu = true,
                    list = listOf(
                        mapOf("name" to "star"),
                        mapOf("name" to "heart"),
                    ),
                ),
                modifier = Modifier.fillMaxSize(),
                onItemClick = { item, index -> itemClicks += (item["name"] as String) to index },
            )
        }

        // Hidden until the main button toggles it.
        composeRule.onNodeWithTag("up-float-button-item-0").assertDoesNotExist()

        composeRule.onNodeWithTag("up-float-button-main").performClick()
        composeRule.onNodeWithTag("up-float-button-item-0").assertExists()
        composeRule.onNodeWithTag("up-float-button-item-1").performClick()
        composeRule.runOnIdle { assertEquals(listOf("heart" to 1), itemClicks) }
    }
}
