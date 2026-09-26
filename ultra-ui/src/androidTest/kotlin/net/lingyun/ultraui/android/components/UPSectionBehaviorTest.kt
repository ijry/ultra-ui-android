package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-section`: renders the title + accent line + right area and fires the right-click. (Reconstructed
 * from `section.js`; upstream ships no `.vue` in this snapshot.)
 */
@RunWith(AndroidJUnit4::class)
class UPSectionBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersTitleAndFiresRightClick() {
        var clicked = false
        composeRule.setContent {
            UPSection(props = UPSectionProps(title = "推荐阅读", subTitle = "查看更多"), onClick = { clicked = true })
        }
        composeRule.onNodeWithTag("up-section").assertIsDisplayed()
        composeRule.onNodeWithTag("up-section-line").assertExists()
        composeRule.onNodeWithText("推荐阅读").assertExists()
        composeRule.onNodeWithTag("up-section-right").performClick()
        composeRule.runOnIdle { assertTrue(clicked) }
    }
}
