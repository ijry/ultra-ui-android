package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `up-cate-tab`: the left menu, tab-mode content switch and the update:current emit.
 */
@RunWith(AndroidJUnit4::class)
class UPCateTabBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val tabList = listOf(
        mapOf("name" to "水果", "children" to listOf(mapOf("name" to "苹果"), mapOf("name" to "香蕉"))),
        mapOf("name" to "蔬菜", "children" to listOf(mapOf("name" to "白菜"))),
    )

    @Test
    fun tabModeShowsOnlyTheActiveCategoryAndEmitsCurrent() {
        val updates = mutableListOf<Int>()
        composeRule.setContent {
            UPCateTab(
                UPCateTabProps(mode = "tab", tabList = tabList, current = 0),
                modifier = Modifier.fillMaxWidth().height(300.dp),
                onUpdateCurrent = { updates += it },
            )
        }

        // Menu shows both categories.
        composeRule.onNodeWithTag("up-cate-tab-item-0").assertExists()
        composeRule.onNodeWithTag("up-cate-tab-item-1").assertExists()
        // tab mode: only the active category's page is present.
        composeRule.onNodeWithTag("up-cate-tab-page-0").assertExists()
        composeRule.onNodeWithTag("up-cate-tab-page-1").assertDoesNotExist()
        composeRule.onNodeWithText("苹果").assertExists()

        composeRule.onNodeWithTag("up-cate-tab-item-1").performClick()
        composeRule.runOnIdle { assertEquals(listOf(1), updates) }
    }

    @Test
    fun followModeStacksEveryCategory() {
        composeRule.setContent {
            UPCateTab(
                UPCateTabProps(mode = "follow", tabList = tabList),
                modifier = Modifier.fillMaxWidth().height(300.dp),
            )
        }

        composeRule.onNodeWithTag("up-cate-tab-page-0").assertExists()
        composeRule.onNodeWithTag("up-cate-tab-page-1").assertExists()
    }
}
