package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upTestTag
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-tabs-pro`: forwards tabs, drives the content slot with the active item, and bubbles change.
 */
@RunWith(AndroidJUnit4::class)
class UPTabsProBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val list = listOf(
        mapOf("name" to "全部"),
        mapOf("name" to "待付款"),
        mapOf("name" to "已完成"),
    )

    @Test
    fun switchingTabsUpdatesTheContentAndEmitsChange() {
        val changes = mutableListOf<Int>()
        composeRule.setContent {
            UPTabsPro(
                UPTabsProProps(list = list, current = 0),
                onChange = { changes += it },
            ) { _, item ->
                BasicText("内容：${item?.get("name")}", modifier = Modifier.upTestTag("tabs-pro-slot"))
            }
        }

        composeRule.onNodeWithText("内容：全部").assertExists()
        composeRule.onNodeWithText("已完成").performClick()
        composeRule.runOnIdle { assertEquals(listOf(2), changes) }
        composeRule.onNodeWithText("内容：已完成").assertExists()
    }

    @Test
    fun showContentFalseHidesTheContentArea() {
        composeRule.setContent {
            UPTabsPro(UPTabsProProps(list = list, showContent = false)) { _, _ ->
                BasicText("不该出现")
            }
        }

        composeRule.onNodeWithTag("up-tabs-pro-content").assertDoesNotExist()
    }
}
