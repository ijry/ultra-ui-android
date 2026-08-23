package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
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
 */
@RunWith(AndroidJUnit4::class)
class UPTooltipBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private fun topOf(tag: String): Float =
        composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.top

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
        composeRule.onNodeWithTag("up-popover-trigger").performClick()
        composeRule.onNodeWithTag("up-popover-content").assertExists()
        assertTrue(
            "default direction=top should put the panel above the trigger",
            composeRule.onNodeWithTag("up-popover-content").fetchSemanticsNode().boundsInRoot.top <
                composeRule.onNodeWithTag("up-popover-trigger").fetchSemanticsNode().boundsInRoot.top,
        )
    }

    @Test
    fun popoverPlacesThePanelToTheLeftOrRight() {
        composeRule.setContent {
            UPPopover(UPPopoverProps(text = "气泡", direction = "right", triggerMode = "manual", show = true))
        }
        val panel = composeRule.onNodeWithTag("up-popover-content").fetchSemanticsNode().boundsInRoot
        val trigger = composeRule.onNodeWithTag("up-popover-trigger").fetchSemanticsNode().boundsInRoot
        assertTrue("direction=right should put the panel after the trigger", panel.left >= trigger.right)
    }

    @Test
    fun popoverManualModeIgnoresTaps() {
        composeRule.setContent {
            UPPopover(UPPopoverProps(text = "气泡", triggerMode = "manual"))
        }
        composeRule.onNodeWithTag("up-popover-trigger").performClick()
        composeRule.onNodeWithTag("up-popover-content").assertDoesNotExist()
    }
}
