package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The wheel geometry that replaces the platform `<picker-view>`. */
class UPPickerWheelSupportTest {
    @Test
    fun theWheelPadsHalfAViewportSoEitherEndCanReachTheCentre() {
        // 5 visible rows: two above and two below the indicator band.
        assertEquals(2f, upPickerWheelPaddingFraction(5), 1e-4f)
        assertEquals(1f, upPickerWheelPaddingFraction(3), 1e-4f)
        // An even count leaves half a row, which is why this is a fraction of item height.
        assertEquals(1.5f, upPickerWheelPaddingFraction(4), 1e-4f)
        // A single visible row needs no padding at all.
        assertEquals(0f, upPickerWheelPaddingFraction(1), 1e-4f)
        assertEquals(0f, upPickerWheelPaddingFraction(0), 1e-4f)
    }

    @Test
    fun theIndexFollowsTheNearestRowAndClampsAtBothEnds() {
        assertEquals(0, upPickerWheelIndexAt(scrollOffsetPx = 0f, itemHeightPx = 44f, count = 5))
        // Just past halfway rounds to the next row, as the wheel visibly does.
        assertEquals(1, upPickerWheelIndexAt(23f, 44f, 5))
        assertEquals(0, upPickerWheelIndexAt(21f, 44f, 5))
        assertEquals(3, upPickerWheelIndexAt(132f, 44f, 5))
        // Overscroll cannot select past the last option.
        assertEquals(4, upPickerWheelIndexAt(9_999f, 44f, 5))
        assertEquals(0, upPickerWheelIndexAt(-50f, 44f, 5))
        // Degenerate inputs resolve to the first row rather than dividing by zero.
        assertEquals(0, upPickerWheelIndexAt(100f, 0f, 5))
        assertEquals(0, upPickerWheelIndexAt(100f, 44f, 0))
    }

    @Test
    fun theOffsetForAnIndexIsTheInverseOfTheIndexForAnOffset() {
        for (index in 0 until 5) {
            val offset = upPickerWheelOffsetFor(index, itemHeightPx = 44f, count = 5)
            assertEquals(index, upPickerWheelIndexAt(offset, 44f, 5))
        }
        // Out-of-range indexes clamp instead of scrolling past the ends.
        assertEquals(4 * 44f, upPickerWheelOffsetFor(99, 44f, 5), 1e-4f)
        assertEquals(0f, upPickerWheelOffsetFor(-3, 44f, 5), 1e-4f)
    }

    @Test
    fun theChangedColumnIsTheFirstOneThatMoved() {
        // `picker-view` emits the whole index array, so upstream diffs it to find the column.
        assertEquals(1, upPickerChangedColumn(listOf(0, 0, 0), listOf(0, 2, 0)))
        assertEquals(0, upPickerChangedColumn(listOf(0, 0), listOf(3, 5)))
        assertNull(upPickerChangedColumn(listOf(1, 2), listOf(1, 2)))
        // A newly grown array counts the new column as moved when it is not zero.
        assertEquals(2, upPickerChangedColumn(listOf(0, 0), listOf(0, 0, 4)))
        // "把 undefined 转为合法假值 0": a new column that landed on 0 is not a change.
        assertNull(upPickerChangedColumn(listOf(0, 0), listOf(0, 0, 0)))
    }
}
