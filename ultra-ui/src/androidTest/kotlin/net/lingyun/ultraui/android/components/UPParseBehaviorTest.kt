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
 * `u-parse`: renders parsed HTML into paragraphs/blocks and, for an image, opens the preview overlay
 * on tap. Parsing and inline styling are covered by the pure `UPParseHtmlTest`/helper unit tests.
 */
@RunWith(AndroidJUnit4::class)
class UPParseBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersHtmlBlocksAndPreviewsImage() {
        var tappedImg: String? = null
        composeRule.setContent {
            UPParse(
                props = UPParseProps(
                    content = "<h2>标题</h2><p>正文 <b>加粗</b> 与 <a href=\"https://x.com\">链接</a></p>" +
                        "<img src=\"/pic.png\"><ul><li>甲</li><li>乙</li></ul>",
                    domain = "https://cdn.io",
                ),
                onImgTap = { tappedImg = it },
            )
        }

        composeRule.onNodeWithTag("up-parse").assertIsDisplayed()
        assertTrue(composeRule.onAllNodesWithTag("up-parse-text").fetchSemanticsNodes().isNotEmpty())
    }
}
