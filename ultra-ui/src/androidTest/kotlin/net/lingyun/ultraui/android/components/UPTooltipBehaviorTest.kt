package net.lingyun.ultraui.android.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-tooltip` defaults to `direction: 'top'` and `triggerMode: 'longpress'`. An
 * implementation that always renders the bubble below its trigger, and opens on both
 * tap and long-press, leaves `direction`, `triggerMode` and the whole `buttons` slot as
 * silent no-ops.
 *
 * The bubble now lives in its own window-level `Popup`, so every geometric assertion has
 * to compare `boundsInWindow`: `boundsInRoot` is relative to each composition's own root,
 * and the popup's root is not the page's.
 */
@RunWith(AndroidJUnit4::class)
class UPTooltipBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private fun topOf(tag: String): Float =
        composeRule.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode().boundsInWindow.top

    private fun boundsOf(tag: String) =
        composeRule.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode().boundsInWindow

    @Test
    fun defaultLongpressModeIgnoresAPlainTap() {
        composeRule.setContent { UPTooltip(UPTooltipProps(text = "提示")) }
        composeRule.onNodeWithTag("up-tooltip-trigger").performClick()
        composeRule.onNodeWithTag("up-tooltip-content").assertDoesNotExist()
    }

    @Test
    fun longpressOpensTheBubbleInLongpressMode() {
        composeRule.setContent { UPTooltip(UPTooltipProps(text = "提示")) }
        composeRule.onNodeWithTag("up-tooltip-trigger").performTouchInput { longClick() }
        composeRule.onNodeWithTag("up-tooltip-content").assertExists()
    }

    @Test
    fun clickModeOpensOnTapAndIgnoresLongpress() {
        composeRule.setContent { UPTooltip(UPTooltipProps(text = "提示", triggerMode = "click")) }
        composeRule.onNodeWithTag("up-tooltip-trigger").performClick()
        composeRule.onNodeWithTag("up-tooltip-content").assertExists()
    }

    @Test
    fun manualModeOnlyFollowsTheShowProp() {
        composeRule.setContent {
            UPTooltip(UPTooltipProps(text = "提示", triggerMode = "manual", show = true))
        }
        // No gesture at all: `show` alone drives it.
        composeRule.onNodeWithTag("up-tooltip-content").assertExists()
    }

    @Test
    fun manualModeIgnoresGestures() {
        composeRule.setContent {
            UPTooltip(UPTooltipProps(text = "提示", triggerMode = "manual"))
        }
        composeRule.onNodeWithTag("up-tooltip-trigger").performTouchInput { longClick() }
        composeRule.onNodeWithTag("up-tooltip-content").assertDoesNotExist()
    }

    @Test
    fun directionTopPlacesTheBubbleAboveTheTrigger() {
        composeRule.setContent {
            UPTooltip(UPTooltipProps(text = "提示", direction = "top", triggerMode = "manual", show = true))
        }
        assertTrue(
            "bubble should sit above the trigger for direction=top",
            topOf("up-tooltip-content") < topOf("up-tooltip-trigger"),
        )
    }

    @Test
    fun directionBottomPlacesTheBubbleBelowTheTrigger() {
        composeRule.setContent {
            UPTooltip(UPTooltipProps(text = "提示", direction = "bottom", triggerMode = "manual", show = true))
        }
        assertTrue(
            "bubble should sit below the trigger for direction=bottom",
            topOf("up-tooltip-content") > topOf("up-tooltip-trigger"),
        )
    }

    @Test
    fun extraButtonsRenderAndReportTheirIndex() {
        val taps = mutableListOf<Int>()
        val buttons: List<UPRawValue> = listOf("收藏", "举报")
        composeRule.setContent {
            UPTooltip(
                UPTooltipProps(text = "提示", triggerMode = "manual", show = true, buttons = buttons),
                onButtonClick = { _, index -> taps += index },
            )
        }
        composeRule.onNodeWithText("举报").assertExists()
        composeRule.onNodeWithTag("up-tooltip-button-1").performClick()
        composeRule.runOnIdle { assertEquals(listOf(1), taps) }
    }

    @Test
    fun copyPrefersCopyTextAndFallsBackToText() {
        val copied = mutableListOf<UPRawValue>()
        composeRule.setContent {
            UPTooltip(
                UPTooltipProps(text = "显示文本", copyText = "复制专用", triggerMode = "manual", show = true),
                onCopy = { copied += it },
            )
        }
        composeRule.onNodeWithTag("up-tooltip-copy").performClick()
        composeRule.runOnIdle { assertEquals(listOf<UPRawValue>("复制专用"), copied) }
    }

    @Test
    fun copyUsesTextWhenCopyTextIsEmpty() {
        val copied = mutableListOf<UPRawValue>()
        composeRule.setContent {
            UPTooltip(
                UPTooltipProps(text = "显示文本", triggerMode = "manual", show = true),
                onCopy = { copied += it },
            )
        }
        composeRule.onNodeWithTag("up-tooltip-copy").performClick()
        composeRule.runOnIdle { assertEquals(listOf<UPRawValue>("显示文本"), copied) }
    }

    @Test
    fun popoverDefaultsToClickTriggerAndTopPlacement() {
        composeRule.setContent { UPPopover(UPPopoverProps(text = "气泡")) }
        composeRule.onNodeWithTag("up-tooltip-trigger").performClick()
        composeRule.onNodeWithTag("up-tooltip-content").assertExists()
        assertTrue(
            "default direction=top should put the panel above the trigger",
            topOf("up-tooltip-content") < topOf("up-tooltip-trigger"),
        )
    }

    @Test
    fun popoverPlacesThePanelToTheLeftOrRight() {
        composeRule.setContent {
            UPPopover(UPPopoverProps(text = "气泡", direction = "right", triggerMode = "manual", show = true))
        }
        val panel = boundsOf("up-tooltip-content")
        val trigger = boundsOf("up-tooltip-trigger")
        assertTrue("direction=right should put the panel after the trigger", panel.left >= trigger.right)
    }

    @Test
    fun overlayBlocksTouchesBehindTheBubbleAndClosesOnTap() {
        var behindTaps = 0
        composeRule.setContent {
            Box {
                BasicText(
                    "背后内容",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { behindTaps += 1 }
                        .testTag("behind"),
                )
                UPTooltip(UPTooltipProps(text = "提示", triggerMode = "manual", show = true))
            }
        }

        // The scrim is transparent but present, so the page behind cannot be reached.
        composeRule.onNodeWithTag("up-tooltip-overlay", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-tooltip-overlay", useUnmergedTree = true).performClick()
        composeRule.runOnIdle { assertEquals(0, behindTaps) }
        // Tapping it closes the bubble, like `overlayClickHandler`.
        composeRule.onNodeWithTag("up-tooltip-content").assertDoesNotExist()
    }

    @Test
    fun overlayFalseLeavesNoScrimAtAll() {
        composeRule.setContent {
            UPTooltip(UPTooltipProps(text = "提示", triggerMode = "manual", show = true, overlay = false))
        }
        composeRule.onNodeWithTag("up-tooltip-content").assertExists()
        composeRule.onNodeWithTag("up-tooltip-overlay", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun theBubbleEscapesATriggerThatWouldClipIt() {
        composeRule.setContent {
            // A 30dp-tall trigger box would clip a bubble drawn inside it; the window-level
            // popup means the bubble is not bounded by its parent at all.
            Box(Modifier.size(60.dp).testTag("cage")) {
                UPTooltip(UPTooltipProps(text = "一段很长的提示文本内容", triggerMode = "manual", show = true))
            }
        }

        val cage = boundsOf("cage")
        val bubble = boundsOf("up-tooltip-content")
        assertTrue("the bubble should be wider than its 60dp cage, got $bubble", bubble.width > cage.width)
    }

    @Test
    fun aBubbleNearTheLeftEdgeIsPinnedInsideTheScreen() {
        composeRule.setContent {
            Box(Modifier.fillMaxSize()) {
                UPTooltip(
                    UPTooltipProps(text = "一段很长的提示文本内容用于挤到边缘", triggerMode = "manual", show = true),
                    modifier = Modifier.align(Alignment.TopStart),
                )
            }
        }

        // `screenGap: 12` keeps it clear of the edge instead of hanging off-screen.
        val bubble = boundsOf("up-tooltip-content")
        assertTrue("the bubble should stay on screen, got left=${bubble.left}", bubble.left >= 0f)
    }

    @Test
    fun singletonModeClosesTheOtherBubble() {
        composeRule.setContent {
            Column {
                UPTooltip(UPTooltipProps(text = "第一个", triggerMode = "click", singleton = true))
                UPTooltip(UPTooltipProps(text = "第二个", triggerMode = "click", singleton = true))
            }
        }

        val triggers = composeRule.onAllNodesWithTag("up-tooltip-trigger", useUnmergedTree = true)
        triggers[0].performClick()
        composeRule.waitForIdle()
        assertEquals(1, composeRule.onAllNodesWithTag("up-tooltip-content", useUnmergedTree = true).fetchSemanticsNodes().size)

        triggers[1].performClick()
        composeRule.waitForIdle()
        // `activeSingletonTooltip.close()` leaves exactly one bubble open.
        assertEquals(1, composeRule.onAllNodesWithTag("up-tooltip-content", useUnmergedTree = true).fetchSemanticsNodes().size)
    }

    @Test
    fun withoutSingletonBothBubblesStayOpen() {
        composeRule.setContent {
            Column {
                UPTooltip(UPTooltipProps(text = "第一个", triggerMode = "click"))
                UPTooltip(UPTooltipProps(text = "第二个", triggerMode = "click"))
            }
        }

        val triggers = composeRule.onAllNodesWithTag("up-tooltip-trigger", useUnmergedTree = true)
        triggers[0].performClick()
        triggers[1].performClick()
        composeRule.waitForIdle()
        assertEquals(2, composeRule.onAllNodesWithTag("up-tooltip-content", useUnmergedTree = true).fetchSemanticsNodes().size)
    }

    @Test
    fun forcePositionOverridesTheComputedPlacement() {
        composeRule.setContent {
            UPTooltip(
                UPTooltipProps(
                    text = "提示",
                    direction = "top",
                    triggerMode = "manual",
                    show = true,
                    forcePosition = mapOf("top" to "200px", "left" to "40px"),
                ),
            )
        }

        val bubble = boundsOf("up-tooltip-content")
        // `{...style, ...forcePosition}`: the named edges win outright.
        assertEquals(200f, bubble.top, 2f)
        assertEquals(40f, bubble.left, 2f)
    }

    @Test
    fun copyReportsSlotZeroAndTheToastFollowsShowToast() {
        val indexes = mutableListOf<Int>()
        val toasts = mutableListOf<String>()
        composeRule.setContent {
            UPTooltip(
                UPTooltipProps(text = "显示文本", triggerMode = "manual", show = true, buttons = listOf("收藏")),
                onIndexClick = { indexes += it },
                onToast = { toasts += it },
            )
        }

        composeRule.onNodeWithTag("up-tooltip-copy", useUnmergedTree = true).performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(0), indexes)
            assertEquals(listOf("复制成功"), toasts)
        }
    }

    @Test
    fun showToastFalseSuppressesTheCopyFeedback() {
        val toasts = mutableListOf<String>()
        composeRule.setContent {
            UPTooltip(
                UPTooltipProps(text = "显示文本", triggerMode = "manual", show = true, showToast = false),
                onToast = { toasts += it },
            )
        }
        composeRule.onNodeWithTag("up-tooltip-copy", useUnmergedTree = true).performClick()
        composeRule.runOnIdle { assertTrue("showToast=false should stay silent, got $toasts", toasts.isEmpty()) }
    }

    @Test
    fun anExtraButtonReportsItsSlotAfterTheCopyAction() {
        val indexes = mutableListOf<Int>()
        composeRule.setContent {
            UPTooltip(
                UPTooltipProps(text = "提示", triggerMode = "manual", show = true, buttons = listOf("收藏", "举报")),
                onIndexClick = { indexes += it },
            )
        }
        composeRule.onNodeWithTag("up-tooltip-button-1", useUnmergedTree = true).performClick()
        // The copy button occupies slot 0, so the second extra button is slot 2.
        composeRule.runOnIdle { assertEquals(listOf(2), indexes) }
    }

    @Test
    fun thePopoverHasNoCopyButtonOfItsOwn() {
        composeRule.setContent {
            UPPopover(UPPopoverProps(text = "气泡", triggerMode = "manual", show = true))
        }
        composeRule.onNodeWithTag("up-tooltip-content").assertExists()
        // `u-popover` fills the content slot instead, so the copy action is absent.
        composeRule.onNodeWithTag("up-tooltip-copy", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun popoverManualModeIgnoresTaps() {
        composeRule.setContent {
            UPPopover(UPPopoverProps(text = "气泡", triggerMode = "manual"))
        }
        composeRule.onNodeWithTag("up-tooltip-trigger").performClick()
        composeRule.onNodeWithTag("up-tooltip-content").assertDoesNotExist()
    }
}
