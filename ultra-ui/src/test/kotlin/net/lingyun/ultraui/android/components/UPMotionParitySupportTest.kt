package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UPMotionParitySupportTest {
    @Test
    fun stickyKeepsTheDefaultZIndexUnlessATruthyOverrideArrives() {
        // `uZindex() { return this.zIndex ? this.zIndex : zIndex.sticky }`.
        assertEquals(970f, upStickyZIndex(UPStickyProps().zIndex), 0f)
        assertEquals(970f, upStickyZIndex(""), 0f)
        assertEquals(970f, upStickyZIndex(null), 0f)
        // JavaScript's `||` also falls through for 0.
        assertEquals(970f, upStickyZIndex(0), 0f)
        assertEquals(1200f, upStickyZIndex(1200), 0f)
        assertEquals(1200f, upStickyZIndex("1200"), 0f)
    }

    @Test
    fun noticeIntervalFallsBackToTwoSecondsAndNeverSpins() {
        assertEquals(2000L, upNoticeIntervalMillis(UPNoticeBarProps().duration))
        assertEquals(1500L, upNoticeIntervalMillis(1500))
        assertEquals(1500L, upNoticeIntervalMillis("1500"))
        assertEquals(2000L, upNoticeIntervalMillis("slow"))
        // A non-positive interval would busy-loop the carousel.
        assertEquals(1L, upNoticeIntervalMillis(0))
        assertEquals(1L, upNoticeIntervalMillis(-500))
    }

    @Test
    fun marqueeDurationIsDistanceOverSpeed() {
        // (box + text) / speed * 1000 = (200 + 600) / 80 * 1000 = 10000ms.
        assertEquals(10_000, upNoticeMarqueeDurationMillis(200f, 600f, 80))
        // Twice the speed halves the loop.
        assertEquals(5_000, upNoticeMarqueeDurationMillis(200f, 600f, 160))
        assertEquals(10_000, upNoticeMarqueeDurationMillis(200f, 600f, "80"))
        // A malformed or non-positive speed falls back to upstream's 80px/s.
        assertEquals(10_000, upNoticeMarqueeDurationMillis(200f, 600f, "fast"))
        assertEquals(10_000, upNoticeMarqueeDurationMillis(200f, 600f, 0))
        // Nothing measured yet means nothing to animate.
        assertEquals(0, upNoticeMarqueeDurationMillis(0f, 600f, 80))
        assertEquals(0, upNoticeMarqueeDurationMillis(200f, 0f, 80))
    }

    @Test
    fun aPartialSweepUsesTheSameSpeedAsAFullLoop() {
        assertEquals(2_500, upNoticeMarqueeSweepMillis(200f, 80))
        assertEquals(10_000, upNoticeMarqueeSweepMillis(800f, 80))
        assertEquals(0, upNoticeMarqueeSweepMillis(0f, 80))
        assertEquals(0, upNoticeMarqueeSweepMillis(-40f, 80))
        // Even a sub-millisecond distance still has to advance by one tick.
        assertEquals(1, upNoticeMarqueeSweepMillis(0.01f, 80))
    }

    @Test
    fun theCarouselWrapsInBothDirections() {
        assertEquals(1, upNoticeNextIndex(0, 3))
        assertEquals(0, upNoticeNextIndex(2, 3))
        assertEquals(2, upNoticePreviousIndex(0, 3))
        assertEquals(1, upNoticePreviousIndex(2, 3))
        assertEquals(0, upNoticeNextIndex(0, 0))
        assertEquals(0, upNoticePreviousIndex(0, 0))
        assertEquals(0, upNoticeNextIndex(0, 1))
    }

    @Test
    fun draggingIsOnlyAllowedWhenTouchIsEnabledAndThereIsSomewhereToGo() {
        assertTrue(upNoticeTouchEnabled(disableTouch = false, count = 3))
        assertFalse(upNoticeTouchEnabled(disableTouch = true, count = 3))
        assertFalse(upNoticeTouchEnabled(disableTouch = false, count = 1))
        assertFalse(upNoticeTouchEnabled(disableTouch = false, count = 0))
    }

    @Test
    fun sliderHeightOverridesSizeForTheTrackThickness() {
        // `sizeLocal = height !== '' ? height : size`.
        assertEquals(2f, upSliderTrackThickness(UPSliderProps().height, UPSliderProps().size), 0f)
        assertEquals(6f, upSliderTrackThickness(6, "2px"), 0f)
        assertEquals(6f, upSliderTrackThickness("6px", "2px"), 0f)
        assertEquals(4f, upSliderTrackThickness("", 4), 0f)
        assertEquals(4f, upSliderTrackThickness("", "4px"), 0f)
        // A zero or malformed pair still leaves a hairline to draw.
        assertEquals(1f, upSliderTrackThickness("", 0), 0f)
        assertEquals(2f, upSliderTrackThickness("", "thin"), 0f)
    }

    @Test
    fun sliderRowLeavesRoomForRangeValueLabels() {
        // `innerStyleCpu.height = (isRange && showValue) ? blockSize + 24 : blockSize`.
        assertEquals(18f, upSliderInnerHeightDp(18, isRange = false, showValue = false), 0f)
        assertEquals(18f, upSliderInnerHeightDp(18, isRange = true, showValue = false), 0f)
        assertEquals(18f, upSliderInnerHeightDp(18, isRange = false, showValue = true), 0f)
        assertEquals(42f, upSliderInnerHeightDp(18, isRange = true, showValue = true), 0f)
        assertEquals(48f, upSliderInnerHeightDp("24px", isRange = true, showValue = true), 0f)
        assertEquals(1f, upSliderInnerHeightDp(0, isRange = false, showValue = false), 0f)
    }

    @Test
    fun sliderLengthOnlyOverridesTheAxisWhenItIsNotAuto() {
        assertNull(upSliderLengthDp(UPSliderProps().length))
        assertNull(upSliderLengthDp("auto"))
        assertNull(upSliderLengthDp(""))
        assertNull(upSliderLengthDp(0))
        assertEquals(200f, upSliderLengthDp(200))
        assertEquals(200f, upSliderLengthDp("200px"))
    }

    @Test
    fun aRangeSliderNeverFallsBackToTheNativeControl() {
        assertFalse(upSliderUsesNative(useNative = false, isRange = false))
        assertFalse(upSliderUsesNative(useNative = false, isRange = true))
        assertTrue(upSliderUsesNative(useNative = true, isRange = false))
        assertFalse(upSliderUsesNative(useNative = true, isRange = true))
    }

    @Test
    fun collapseDurationFallsBackToThreeHundredMilliseconds() {
        assertEquals(300, upCollapseDurationMillis(UPCollapseItemProps().duration))
        assertEquals(0, upCollapseDurationMillis(0))
        assertEquals(600, upCollapseDurationMillis(600))
        assertEquals(600, upCollapseDurationMillis("600"))
        assertEquals(300, upCollapseDurationMillis("slow"))
        assertEquals(0, upCollapseDurationMillis(-200))
    }
}
