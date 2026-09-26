package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-markdown`: converts Markdown to HTML and renders it through `u-parse`. Conversion itself is
 * covered by the pure `upMarkdownToHtml` unit tests.
 */
@RunWith(AndroidJUnit4::class)
class UPMarkdownBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersMarkdownThroughParse() {
        composeRule.setContent {
            UPMarkdown(
                props = UPMarkdownProps(
                    content = "# 标题\n\n正文 **加粗** 与 [链接](https://x.com)\n\n- 甲\n- 乙",
                    theme = "dark",
                ),
            )
        }
        composeRule.onNodeWithTag("up-markdown").assertIsDisplayed()
        composeRule.onNodeWithTag("up-parse").assertExists()
        assertTrue(composeRule.onAllNodesWithTag("up-parse-text").fetchSemanticsNodes().isNotEmpty())
    }
}
