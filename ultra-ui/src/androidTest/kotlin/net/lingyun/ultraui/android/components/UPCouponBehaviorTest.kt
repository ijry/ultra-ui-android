package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertHeightIsEqualTo
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
 * `up-coupon`: the three-column layout, its size-driven height, and the click (blocked when
 * disabled).
 */
@RunWith(AndroidJUnit4::class)
class UPCouponBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersAmountInfoActionAndFiresClick() {
        var clicks = 0
        composeRule.setContent {
            UPCoupon(
                UPCouponProps(amount = "50", limit = "满199可用", title = "新人券", desc = "全场通用", size = "medium"),
                onClick = { clicks += 1 },
            )
        }

        composeRule.onNodeWithText("50").assertExists()
        composeRule.onNodeWithText("满199可用").assertExists()
        composeRule.onNodeWithText("新人券").assertExists()
        composeRule.onNodeWithText("使用").assertExists()
        composeRule.onNodeWithTag("up-coupon").assertHeightIsEqualTo(90.dp)
        composeRule.onNodeWithTag("up-coupon").performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun disabledCouponBlocksTheClick() {
        var clicks = 0
        composeRule.setContent {
            UPCoupon(UPCouponProps(amount = "10", disabled = true), onClick = { clicks += 1 })
        }

        composeRule.onNodeWithTag("up-coupon").performClick()
        composeRule.runOnIdle { assertEquals(0, clicks) }
    }
}
