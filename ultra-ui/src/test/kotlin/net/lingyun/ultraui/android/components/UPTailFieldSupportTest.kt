package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UPTailFieldSupportTest {
    @Test
    fun backTopScrollDurationFallsBackToOneHundredMilliseconds() {
        assertEquals(100, upBackTopScrollDurationMillis(UPBackTopProps().duration))
        assertEquals(0, upBackTopScrollDurationMillis(0))
        assertEquals(400, upBackTopScrollDurationMillis(400))
        assertEquals(400, upBackTopScrollDurationMillis("400"))
        assertEquals(100, upBackTopScrollDurationMillis("slow"))
        assertEquals(0, upBackTopScrollDurationMillis(-50))
    }

    @Test
    fun easedCountingStartsAtTheOriginAndLandsOnTheTarget() {
        // easingFn(0, b, c, d) = b, and at t = d it is within a rounding step of b + c.
        assertEquals(0.0, upCountToEasedValue(0.0, 0.0, 100.0, 2000.0), 1e-9)
        assertEquals(100.0, upCountToEasedValue(2000.0, 0.0, 100.0, 2000.0), 0.11)
        // Ease-out-expo covers most of the distance early on.
        assertTrue(upCountToEasedValue(500.0, 0.0, 100.0, 2000.0) > 50.0)
        // A zero duration has nowhere to interpolate.
        assertEquals(100.0, upCountToEasedValue(0.0, 0.0, 100.0, 0.0), 1e-9)
    }

    @Test
    fun linearCountingIsProportionalToTheElapsedTime() {
        assertEquals(0.0, upCountToValueAt(0.0, 1000.0, 0.0, 100.0, useEasing = false), 1e-9)
        assertEquals(25.0, upCountToValueAt(250.0, 1000.0, 0.0, 100.0, useEasing = false), 1e-9)
        assertEquals(50.0, upCountToValueAt(500.0, 1000.0, 0.0, 100.0, useEasing = false), 1e-9)
        assertEquals(100.0, upCountToValueAt(1000.0, 1000.0, 0.0, 100.0, useEasing = false), 1e-9)
        // Overshooting the duration still clamps at the target.
        assertEquals(100.0, upCountToValueAt(4000.0, 1000.0, 0.0, 100.0, useEasing = false), 1e-9)
    }

    @Test
    fun countingDownRunsTheSameCurveInReverse() {
        assertEquals(100.0, upCountToValueAt(0.0, 1000.0, 100.0, 0.0, useEasing = false), 1e-9)
        assertEquals(50.0, upCountToValueAt(500.0, 1000.0, 100.0, 0.0, useEasing = false), 1e-9)
        assertEquals(0.0, upCountToValueAt(1000.0, 1000.0, 100.0, 0.0, useEasing = false), 1e-9)
        // The eased variant never dips below the target either.
        assertTrue(upCountToValueAt(500.0, 1000.0, 100.0, 0.0, useEasing = true) >= 0.0)
        assertEquals(0.0, upCountToValueAt(1000.0, 1000.0, 100.0, 0.0, useEasing = true), 0.11)
        // A zero duration jumps straight to the end in both modes.
        assertEquals(0.0, upCountToValueAt(0.0, 0.0, 100.0, 0.0, useEasing = true), 1e-9)
    }

    @Test
    fun skeletonRowWidthPrefersThePerRowArrayAndFallsBackToSeventyPercent() {
        // A bare value applies to every row except the last, which upstream fixes at 70%.
        assertEquals("100%", upSkeletonRowWidth("100%", index = 0, rows = 3))
        assertEquals("70%", upSkeletonRowWidth("100%", index = 2, rows = 3))
        // An array is read per row.
        val widths: UPRawValue = listOf("80%", "60%", "40%")
        assertEquals("80%", upSkeletonRowWidth(widths, index = 0, rows = 3))
        assertEquals("40%", upSkeletonRowWidth(widths, index = 2, rows = 3))
        // Running past the end of the array restores the 100% / 70% defaults.
        val short: UPRawValue = listOf("80%")
        assertEquals("80%", upSkeletonRowWidth(short, index = 0, rows = 3))
        assertEquals("100%", upSkeletonRowWidth(short, index = 1, rows = 3))
        assertEquals("70%", upSkeletonRowWidth(short, index = 2, rows = 3))
    }

    @Test
    fun percentageWidthsBecomeFractionsAndAbsoluteOnesDoNot() {
        assertEquals(1f, upSkeletonWidthFractionOrNull("100%")!!, 0f)
        assertEquals(0.7f, upSkeletonWidthFractionOrNull("70%")!!, 1e-6f)
        assertEquals(1f, upSkeletonWidthFractionOrNull("140%")!!, 0f)
        assertNull(upSkeletonWidthFractionOrNull("120px"))
        assertNull(upSkeletonWidthFractionOrNull(120))
        assertNull(upSkeletonWidthFractionOrNull(""))
    }

    @Test
    fun selectOptionsWidthOnlyOverridesWhenItIsSet() {
        assertNull(upSelectOptionsWidthDp(UPSelectProps().optionsWidth))
        assertNull(upSelectOptionsWidthDp(null))
        assertNull(upSelectOptionsWidthDp(""))
        assertNull(upSelectOptionsWidthDp(0))
        assertNull(upSelectOptionsWidthDp("50%"))
        assertEquals(200f, upSelectOptionsWidthDp(200))
        assertEquals(200f, upSelectOptionsWidthDp("200px"))
    }

    @Test
    fun theTriggerOnlyEchoesTheSelectionWhenAskedTo() {
        assertEquals("选项", upSelectTriggerText(showOptionsLabel = false, currentLabel = "北京", label = "选项"))
        assertEquals("北京", upSelectTriggerText(showOptionsLabel = true, currentLabel = "北京", label = "选项"))
        // Nothing selected yet still needs a placeholder.
        assertEquals("选项", upSelectTriggerText(showOptionsLabel = true, currentLabel = "", label = "选项"))
    }

    @Test
    fun textIndentResolvesEmAgainstTheFontSize() {
        assertEquals(28f, upTextIndentPx(UPReadMoreProps().textIndent, fontSizePx = 14f)!!, 1e-6f)
        assertEquals(20f, upTextIndentPx("2em", fontSizePx = 10f)!!, 1e-6f)
        assertEquals(16f, upTextIndentPx("16px", fontSizePx = 14f)!!, 1e-6f)
        assertEquals(16f, upTextIndentPx(16, fontSizePx = 14f)!!, 1e-6f)
        assertNull(upTextIndentPx("", fontSizePx = 14f))
        assertNull(upTextIndentPx("0", fontSizePx = 14f))
        assertNull(upTextIndentPx("inherit", fontSizePx = 14f))
    }

    @Test
    fun theKnownEnumSetsMatchTheUpstreamClassNames() {
        assertEquals(setOf("circle", "square"), UPSkeletonAvatarShapes)
        assertEquals(setOf("row", "column"), UPCascaderHeaderDirections)
    }
}
