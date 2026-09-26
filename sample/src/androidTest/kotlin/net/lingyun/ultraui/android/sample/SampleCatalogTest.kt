package net.lingyun.ultraui.android.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.sample.catalog.demoGroups
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The sample home is now a 1:1 reproduction of the uview-plus demo app: a searchable
 * component index whose groups mirror `components.config.js`, each row opening a
 * per-component demo page.
 */
@RunWith(AndroidJUnit4::class)
class SampleCatalogTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun indexShowsEveryUpstreamGroupName() {
        listOf("基础组件", "表单组件", "数据组件", "反馈组件", "布局组件", "导航组件", "其他组件").forEach { group ->
            composeRule.onNodeWithText(group).performScrollTo().assertExists()
        }
    }

    @Test
    fun indexNavigatesIntoPerComponentDemoPages() {
        listOf("Button 按钮", "Input 输入框", "Tree 树形").forEach { title ->
            composeRule.onNodeWithText(title).performScrollTo().performClick()
            composeRule.onNodeWithText("返回").assertIsDisplayed()
            composeRule.onNodeWithText("返回").performClick()
        }
    }

    @Test
    fun registryMirrorsUpstreamComponentCatalog() {
        val total = demoGroups.sumOf { it.entries.size }
        assert(total == 105) { "expected 105 upstream demo entries, found $total" }
    }
}
