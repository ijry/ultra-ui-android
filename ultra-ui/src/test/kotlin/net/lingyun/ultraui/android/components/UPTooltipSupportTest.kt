package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `u-tooltip`'s `getTooltipStyle()` arithmetic, its `forcePosition` merge and the
 * process-wide `singleton` registry.
 */
class UPTooltipSupportTest {
    @Test
    fun aBubbleThatFitsIsCentredOnItsTrigger() {
        // 100px trigger at x=200 in a 375px window, 60px bubble: room on both sides.
        val left = upTooltipBubbleLeftPx(
            triggerLeftPx = 200f,
            triggerWidthPx = 100f,
            bubbleWidthPx = 60f,
            windowWidthPx = 375f,
        )
        assertEquals(220f, left, 1e-4f)
    }

    @Test
    fun aBubbleTooCloseToTheLeftEdgePinsAtTheScreenGap() {
        // Trigger hugging the left edge: half the bubble would land off-screen, so upstream
        // pins it `screenGap` in instead of centring.
        val left = upTooltipBubbleLeftPx(
            triggerLeftPx = 4f,
            triggerWidthPx = 20f,
            bubbleWidthPx = 200f,
            windowWidthPx = 375f,
        )
        assertEquals(UPTooltipScreenGapPx, left, 1e-4f)
    }

    @Test
    fun aBubbleTooCloseToTheRightEdgePinsThere() {
        val left = upTooltipBubbleLeftPx(
            triggerLeftPx = 350f,
            triggerWidthPx = 20f,
            bubbleWidthPx = 200f,
            windowWidthPx = 375f,
        )
        // `windowWidth - screenGap - bubbleWidth`.
        assertEquals(375f - UPTooltipScreenGapPx - 200f, left, 1e-4f)
    }

    @Test
    fun theArrowKeepsPointingAtTheTriggerAfterTheBubbleIsPushedAside() {
        // The bubble was pinned at the gap, so the arrow has to move within it to stay
        // under the trigger's centre.
        val bubbleLeft = UPTooltipScreenGapPx
        val arrow = upTooltipIndicatorLeftPx(
            bubbleLeftPx = bubbleLeft,
            triggerLeftPx = 4f,
            triggerWidthPx = 20f,
            bubbleWidthPx = 200f,
        )
        // Trigger centre is 14; 14 - 12 - 7 is below zero, so it clamps to the bubble edge.
        assertEquals(0f, arrow, 1e-4f)

        // A centred bubble puts the arrow in the middle.
        val centred = upTooltipIndicatorLeftPx(
            bubbleLeftPx = 220f,
            triggerLeftPx = 200f,
            triggerWidthPx = 100f,
            bubbleWidthPx = 60f,
        )
        assertEquals(250f - 220f - UPTooltipIndicatorWidthPx / 2f, centred, 1e-4f)
    }

    @Test
    fun theArrowNeverLeavesTheBubble() {
        val arrow = upTooltipIndicatorLeftPx(
            bubbleLeftPx = 0f,
            triggerLeftPx = 900f,
            triggerWidthPx = 20f,
            bubbleWidthPx = 60f,
        )
        assertEquals(60f - UPTooltipIndicatorWidthPx, arrow, 1e-4f)
    }

    @Test
    fun verticalPlacementsClearTheTriggerAndSidePlacementsCentreOnIt() {
        // `translateY(-100%)` plus `marginTop: -10px`.
        assertEquals(
            100f - 40f - UPTooltipVerticalGapPx,
            upTooltipBubbleTopPx("top", triggerTopPx = 100f, triggerHeightPx = 30f, bubbleHeightPx = 40f),
            1e-4f,
        )
        assertEquals(
            100f + 30f + UPTooltipVerticalGapPx,
            upTooltipBubbleTopPx("bottom", triggerTopPx = 100f, triggerHeightPx = 30f, bubbleHeightPx = 40f),
            1e-4f,
        )
        // `top: '-' + (triggerInfo.height - tooltipInfo.height) / 2` centres it vertically.
        assertEquals(
            100f + (30f - 40f) / 2f,
            upTooltipBubbleTopPx("left", triggerTopPx = 100f, triggerHeightPx = 30f, bubbleHeightPx = 40f),
            1e-4f,
        )
    }

    @Test
    fun sidePlacementsSitOneIndicatorWidthClearOfTheTrigger() {
        assertEquals(
            200f - UPTooltipIndicatorWidthPx - 60f,
            upTooltipSideBubbleLeftPx("left", triggerLeftPx = 200f, triggerWidthPx = 100f, bubbleWidthPx = 60f),
            1e-4f,
        )
        assertEquals(
            200f + 100f + UPTooltipIndicatorWidthPx,
            upTooltipSideBubbleLeftPx("right", triggerLeftPx = 200f, triggerWidthPx = 100f, bubbleWidthPx = 60f),
            1e-4f,
        )
    }

    @Test
    fun forcePositionOnlyOverridesTheEdgesItNames() {
        val empty = upForcedPosition(emptyMap<String, Any?>())
        assertTrue(empty.isEmpty)
        assertNull(empty.top)

        val forced = upForcedPosition(mapOf("top" to "40px", "right" to 12))
        assertEquals("40px", forced.top)
        assertEquals(12, forced.right)
        // `{...style, ...forcePosition}` leaves everything else to the computed placement.
        assertNull(forced.left)
        assertNull(forced.bottom)
    }

    @Test
    fun theCopyToastFollowsShowToastAndTheOutcome() {
        assertEquals("复制成功", upTooltipCopyToastMessage(showToast = true, success = true))
        assertEquals("复制失败", upTooltipCopyToastMessage(showToast = true, success = false))
        assertNull(upTooltipCopyToastMessage(showToast = false, success = true))
    }

    @Test
    fun theCopyActionOccupiesSlotZeroWhenItIsShown() {
        // "如果需要展示复制按钮，此处 index 需要加 1，因为复制按钮在第一个位置".
        assertEquals(1, upTooltipButtonEventIndex(showCopy = true, buttonIndex = 0))
        assertEquals(2, upTooltipButtonEventIndex(showCopy = true, buttonIndex = 1))
        assertEquals(0, upTooltipButtonEventIndex(showCopy = false, buttonIndex = 0))
    }

    @Test
    fun theSingletonRegistryClosesThePreviousHolderAndOnlyItsOwnerReleases() {
        val first = Any()
        val second = Any()
        val closed = mutableListOf<Any>()

        UPTooltipSingletonRegistry.claim(first) { closed += it }
        assertTrue(closed.isEmpty())
        assertEquals(first, UPTooltipSingletonRegistry.activeOwner())

        // A second claim closes the first, exactly as `activeSingletonTooltip.close()` does.
        UPTooltipSingletonRegistry.claim(second) { closed += it }
        assertEquals(listOf(first), closed)
        assertEquals(second, UPTooltipSingletonRegistry.activeOwner())

        // Re-claiming from the same owner must not close itself.
        UPTooltipSingletonRegistry.claim(second) { closed += it }
        assertEquals(listOf(first), closed)

        // Only the current holder may release the slot.
        UPTooltipSingletonRegistry.release(first)
        assertEquals(second, UPTooltipSingletonRegistry.activeOwner())
        UPTooltipSingletonRegistry.release(second)
        assertNull(UPTooltipSingletonRegistry.activeOwner())
    }
}
