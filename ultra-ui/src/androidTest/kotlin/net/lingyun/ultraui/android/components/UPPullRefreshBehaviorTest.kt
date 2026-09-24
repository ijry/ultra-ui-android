package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-pull-refresh`: the controlled `refreshing` flag shows the refreshing header over the content.
 * (Pull distance/status math is covered by the pure helper unit tests.)
 */
@RunWith(AndroidJUnit4::class)
class UPPullRefreshBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun refreshingFlagShowsTheRefreshingHeaderAndContent() {
        composeRule.setContent {
            UPPullRefresh(UPPullRefreshProps(refreshing = true)) {
                BasicText("列表内容")
            }
        }

        composeRule.onNodeWithTag("up-pull-refresh").assertExists()
        composeRule.onNodeWithTag("up-pull-refresh-area").assertExists()
        composeRule.onNodeWithText("正在刷新...").assertExists()
        composeRule.onNodeWithText("列表内容").assertExists()
    }

    @Test
    fun idleStateHidesTheRefreshArea() {
        composeRule.setContent {
            UPPullRefresh(UPPullRefreshProps(refreshing = false)) {
                BasicText("列表内容")
            }
        }

        composeRule.onNodeWithTag("up-pull-refresh-area").assertDoesNotExist()
        composeRule.onNodeWithText("列表内容").assertExists()
    }
}
