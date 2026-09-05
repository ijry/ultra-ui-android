package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Behaviour coverage for the motion-parity batch: `u-notice-bar`'s carousel and marquee,
 * `u-collapse-item`'s animated panel and root `customStyle`, and `u-sticky`'s `disabled`.
 */
@RunWith(AndroidJUnit4::class)
class UPMotionParityBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun columnNoticeRendersOneMessageAtATimeAndAdvancesOnItsInterval() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPNoticeBar(
                UPNoticeBarProps(
                    text = listOf("第一条通知", "第二条通知"),
                    direction = "column",
                    duration = 1500,
                ),
            )
        }

        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("第一条通知")
        composeRule.mainClock.advanceTimeBy(1_600L)
        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("第二条通知")
        composeRule.mainClock.advanceTimeBy(1_600L)
        // `circular` wraps back to the first message.
        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("第一条通知")
    }

    @Test
    fun aSingleColumnMessageNeverAdvances() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPNoticeBar(UPNoticeBarProps(text = listOf("只有一条"), direction = "column", duration = 200))
        }

        composeRule.mainClock.advanceTimeBy(2_000L)
        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("只有一条")
    }

    @Test
    fun stepModeUsesTheCarouselEvenWhenTheDirectionIsRow() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPNoticeBar(
                UPNoticeBarProps(
                    text = listOf("步进一", "步进二"),
                    direction = "row",
                    step = true,
                    duration = 800,
                ),
            )
        }

        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("步进一")
        composeRule.mainClock.advanceTimeBy(900L)
        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("步进二")
    }

    @Test
    fun rowModeJoinsTheMessagesIntoOneScrollingLineAndMovesIt() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPNoticeBar(
                UPNoticeBarProps(
                    text = listOf("系统维护通知", "请注意及时保存"),
                    direction = "row",
                    speed = 80,
                ),
            )
        }

        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true)
            .assertTextEquals("系统维护通知  请注意及时保存")
        composeRule.mainClock.advanceTimeBy(16L)
        val start = composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().left
        composeRule.mainClock.advanceTimeBy(1_000L)
        val later = composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().left

        assertTrue("the marquee should travel leftwards", later < start)
    }

    @Test
    fun columnNoticeWrapperForwardsItsIntervalToTheCarousel() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPColumnNotice(
                UPColumnNoticeProps(text = listOf("竖向一", "竖向二"), duration = 700),
            )
        }

        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("竖向一")
        composeRule.mainClock.advanceTimeBy(800L)
        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("竖向二")
    }

    @Test
    fun rowNoticeWrapperForwardsItsSpeedToTheMarquee() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPRowNotice(UPRowNoticeProps(text = "横向滚动的一段较长通知文字", speed = 120))
        }

        composeRule.mainClock.advanceTimeBy(16L)
        val start = composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().left
        composeRule.mainClock.advanceTimeBy(800L)
        val later = composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().left

        assertTrue("the row wrapper should scroll too", later < start)
    }

    @Test
    fun collapseItemKeepsThePanelWhileItAnimatesShut() {
        composeRule.mainClock.autoAdvance = false
        var open by mutableStateOf(true)
        composeRule.setContent {
            UPCollapseItem(props = UPCollapseItemProps(title = "标题", open = open, duration = 600)) {
                BasicText("面板内容")
            }
        }

        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertExists()
        val opened = composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().let { it.bottom - it.top }

        composeRule.runOnIdle { open = false }
        composeRule.mainClock.advanceTimeBy(300L)
        val midway = composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().let { it.bottom - it.top }
        assertTrue("the panel should still be collapsing, not gone", midway < opened)

        composeRule.mainClock.advanceTimeBy(600L)
        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun collapseItemAppliesCustomStyleToItsRoot() {
        composeRule.setContent {
            UPCollapseItem(
                props = UPCollapseItemProps(
                    title = "标题",
                    isOpen = false,
                    customStyle = mapOf("height" to "88px"),
                ),
            ) {}
        }

        val bounds = composeRule.onNodeWithTag("up-collapse-item").getUnclippedBoundsInRoot()
        assertEquals(88.dp.value, (bounds.bottom - bounds.top).value, 1f)
    }

    @Test
    fun collapseItemZeroDurationOpensAndClosesInstantly() {
        var open by mutableStateOf(false)
        composeRule.setContent {
            UPCollapseItem(props = UPCollapseItemProps(title = "标题", open = open, duration = 0)) {
                BasicText("面板内容")
            }
        }

        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertDoesNotExist()
        composeRule.runOnIdle { open = true }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertExists()
    }

    @Test
    fun collapseItemHeaderStillTogglesThePanel() {
        composeRule.setContent {
            UPCollapseItem(props = UPCollapseItemProps(title = "标题", clickable = true, isLink = true)) {
                BasicText("面板内容")
            }
        }

        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("up-collapse-item-0-header").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertExists()
    }

    @Test
    fun stickyDisabledDropsTheTopOffset() {
        composeRule.setContent {
            UPSticky(UPStickyProps(offsetTop = 40, customNavHeight = 20)) { BasicText("吸顶") }
        }
        val enabled = composeRule.onNodeWithTag("up-sticky").getUnclippedBoundsInRoot()
            .let { it.bottom - it.top }

        composeRule.setContent {
            UPSticky(UPStickyProps(offsetTop = 40, customNavHeight = 20, disabled = true)) { BasicText("吸顶") }
        }
        val disabled = composeRule.onNodeWithTag("up-sticky").getUnclippedBoundsInRoot()
            .let { it.bottom - it.top }

        assertTrue("disabled should remove the 60dp of sticky offset", disabled < enabled)
        assertEquals(60.dp.value, (enabled - disabled).value, 1f)
    }

    @Test
    fun actionSheetSafeAreaInsetBottomCanBeTurnedOff() {
        val events = mutableListOf<String>()
        composeRule.setContent {
            UPActionSheet(
                props = UPActionSheetProps(
                    show = true,
                    actions = listOf(mapOf("name" to "拍照")),
                    safeAreaInsetBottom = false,
                ),
                diagnostics = UPCompatibilityDiagnostics { events += it.property },
            )
        }

        composeRule.onNodeWithTag("up-action-sheet-panel").assertExists()
        composeRule.runOnIdle { assertEquals(emptyList<String>(), events) }
    }
}
