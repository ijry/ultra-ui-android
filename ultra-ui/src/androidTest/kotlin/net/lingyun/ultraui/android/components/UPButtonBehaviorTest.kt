package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-button` is the most-used component in the library and carried a 基本完成 label
 * without a single device assertion. These cover the contract a generated call relies
 * on: the label renders, clicks reach the caller, and `disabled`/`loading` suppress
 * them the way uview does.
 */
@RunWith(AndroidJUnit4::class)
class UPButtonBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun buttonRendersItsTextAndReportsClicks() {
        var clicks = 0
        composeRule.setContent {
            UPButton(UPButtonProps(text = "提交"), onClick = { clicks++ })
        }
        composeRule.onNodeWithText("提交").assertIsDisplayed()
        composeRule.onNodeWithTag("up-button").performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun disabledButtonSwallowsClicks() {
        var clicks = 0
        composeRule.setContent {
            UPButton(UPButtonProps(text = "禁用", disabled = true), onClick = { clicks++ })
        }
        composeRule.onNodeWithTag("up-button").performClick()
        composeRule.runOnIdle { assertEquals(0, clicks) }
    }

    @Test
    fun loadingButtonSwallowsClicksAndShowsLoadingText() {
        var clicks = 0
        composeRule.setContent {
            UPButton(
                UPButtonProps(text = "提交", loading = true, loadingText = "提交中"),
                onClick = { clicks++ },
            )
        }
        composeRule.onNodeWithText("提交中").assertIsDisplayed()
        composeRule.onNodeWithTag("up-button").performClick()
        composeRule.runOnIdle { assertEquals(0, clicks) }
    }
}
