package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPBatch9BBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun navigationAndTabsExposeNativeSemantics() {
        var selected = -1
        composeRule.setContent {
            UPNavbar(UPNavbarProps(title = "订单"), onLeftClick = {})
            UPTabs(UPTabsProps(list = listOf("全部", "待支付"), current = 0), onChange = { selected = it })
        }
        composeRule.onNodeWithTag("up-navbar").assertExists()
        composeRule.onNodeWithText("订单").assertExists()
        composeRule.onNodeWithText("待支付").performClick()
        composeRule.runOnIdle { assertEquals(1, selected) }
    }

    @Test
    fun popoverTooltipAndSelectOpenAndEmitSelection() {
        var selected: Any? = null
        composeRule.setContent {
            UPPopover(UPPopoverProps(text = "打开"))
            UPTooltip(UPTooltipProps(text = "提示", triggerMode = "click"))
            UPSelect(UPSelectProps(options = listOf(mapOf("id" to 1, "name" to "北京"))), onUpdateCurrent = { selected = it })
        }
        composeRule.onNodeWithTag("up-popover").assertExists()
        composeRule.onNodeWithTag("up-tooltip").assertExists()
        composeRule.onNodeWithTag("up-select").assertExists()
        composeRule.onNodeWithText("打开").performClick()
        composeRule.onNodeWithText("提示").performClick()
        composeRule.onNodeWithTag("up-select-trigger").performClick()
        composeRule.onNodeWithText("北京").performClick()
        composeRule.runOnIdle { assertEquals(1, selected) }
    }

    @Test
    fun readMoreCountAndCountdownRenderStableText() {
        composeRule.setContent {
            UPReadMore(UPReadMoreProps(showHeight = 48, closeText = "展开", openText = "收起")) {
                BasicText("这是一段用于验证展开收起行为的较长内容。")
            }
            UPCountTo(UPCountToProps(startVal = 0, endVal = 100, autoplay = false))
            UPCountDown(UPCountDownProps(time = 61000, autoStart = false))
        }
        composeRule.onNodeWithTag("up-read-more").assertExists()
        composeRule.onNodeWithTag("up-count-to").assertTextContains("0")
        composeRule.onNodeWithTag("up-count-down").assertTextContains("00:01:01")
    }
}
