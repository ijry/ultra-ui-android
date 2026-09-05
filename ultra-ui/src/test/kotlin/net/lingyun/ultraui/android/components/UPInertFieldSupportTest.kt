package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPCompatibilityEvent
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The pure halves of the fields that used to be declared and never read: `u-popup`'s
 * transition table and drag thresholds, `u-select`'s panel cap and stacking, `u-tabbar`'s
 * placeholder guard, and the `formatter` / `filter` callbacks of `u-calendar` and
 * `u-datetime-picker`.
 */
class UPInertFieldSupportTest {
    private fun recorder(): Pair<UPCompatibilityDiagnostics, MutableList<UPCompatibilityEvent>> {
        val events = mutableListOf<UPCompatibilityEvent>()
        return UPCompatibilityDiagnostics { event -> events += event } to events
    }

    @Test
    fun popupTransitionsFollowUpstreamsPositionComputed() {
        // `position()`: only centre mode consults `zoom`.
        assertEquals("fade-zoom", upPopupTransitionMode("center", zoom = true, pageInline = false))
        assertEquals("fade", upPopupTransitionMode("center", zoom = false, pageInline = false))
        assertEquals("slide-up", upPopupTransitionMode("bottom", zoom = true, pageInline = false))
        assertEquals("slide-down", upPopupTransitionMode("top", zoom = true, pageInline = false))
        assertEquals("slide-left", upPopupTransitionMode("left", zoom = false, pageInline = false))
        assertEquals("slide-right", upPopupTransitionMode("right", zoom = false, pageInline = false))
        // `:mode="pageInline ? 'none' : position"` wins over every other mode.
        assertEquals("none", upPopupTransitionMode("center", zoom = true, pageInline = true))
        assertEquals("none", upPopupTransitionMode("bottom", zoom = false, pageInline = true))
    }

    @Test
    fun onlyTheFadingTransitionsInterpolateOpacity() {
        assertTrue(upPopupTransitionFades("fade"))
        assertTrue(upPopupTransitionFades("fade-zoom"))
        assertFalse(upPopupTransitionFades("slide-up"))
        assertFalse(upPopupTransitionFades("none"))
        // `translate3d` offsets, as fractions of the panel.
        assertEquals(0f to 1f, upPopupTransitionOffsetFraction("slide-up"))
        assertEquals(0f to -1f, upPopupTransitionOffsetFraction("slide-down"))
        assertEquals(-1f to 0f, upPopupTransitionOffsetFraction("slide-left"))
        assertEquals(1f to 0f, upPopupTransitionOffsetFraction("slide-right"))
        assertEquals(0f to 0f, upPopupTransitionOffsetFraction("fade-zoom"))
        assertEquals(300, upPopupTransitionDuration(UPPopupProps().duration))
        assertEquals(0, upPopupTransitionDuration(-40))
    }

    @Test
    fun theBottomSheetDragOnlyArmsForABottomPopup() {
        assertTrue(upPopupDragEnabled(touchable = true, mode = "bottom"))
        assertFalse(upPopupDragEnabled(touchable = true, mode = "center"))
        assertFalse(upPopupDragEnabled(touchable = false, mode = "bottom"))
    }

    @Test
    fun theDragKeepsTheOldHeightOutsideItsBounds() {
        // `newHeight = touchStartHeight - deltaY`, committed only inside [min, max].
        assertEquals(500f, upPopupDragHeightOrNull(400f, -100f, 200f, 800f))
        assertEquals(300f, upPopupDragHeightOrNull(400f, 100f, 200f, 800f))
        // Below the floor and above the ceiling upstream does nothing rather than clamp.
        assertNull(upPopupDragHeightOrNull(400f, 300f, 200f, 800f))
        assertNull(upPopupDragHeightOrNull(400f, -500f, 200f, 800f))
        // A zero drag is not a change at all.
        assertNull(upPopupDragHeightOrNull(400f, 0f, 200f, 800f))
        // `maxHeight` falls back to 80% of the window; `minHeight` to 200px.
        assertEquals(640f, upPopupDragMaxHeightPx(null, 800f))
        assertEquals(500f, upPopupDragMaxHeightPx(500f, 800f))
        assertEquals(200f, upPopupDragMinHeightPx(null))
        assertEquals(200f, upPopupDragMinHeightPx(0f))
        assertEquals(320f, upPopupDragMinHeightPx(320f))
    }

    @Test
    fun aLongOrFastDownwardDragClosesTheSheet() {
        // `deltaY > 100` closes regardless of speed.
        assertTrue(upPopupShouldCloseAfterDrag(120f, elapsedMillis = 4_000L))
        // 30..100 needs velocity > 0.5 px/ms: 60px in 60ms is 1.0, in 400ms is 0.15.
        assertTrue(upPopupShouldCloseAfterDrag(60f, elapsedMillis = 60L))
        assertFalse(upPopupShouldCloseAfterDrag(60f, elapsedMillis = 400L))
        // A short drag never closes, and an upward drag is not a dismissal at all.
        assertFalse(upPopupShouldCloseAfterDrag(20f, elapsedMillis = 10L))
        assertFalse(upPopupShouldCloseAfterDrag(-200f, elapsedMillis = 10L))
    }

    @Test
    fun selectMaxHeightResolvesViewportUnitsAgainstTheWindow() {
        // `90vh` of an 800dp-tall window.
        assertEquals(720f, upSelectMaxHeightDp(UPSelectProps().maxHeight, 800f, 360f))
        assertEquals(400f, upSelectMaxHeightDp("50vh", 800f, 360f))
        assertEquals(240f, upSelectMaxHeightDp("240px", 800f, 360f))
        assertEquals(240f, upSelectMaxHeightDp(240, 800f, 360f))
        // `rpx` resolves against the 750-wide design canvas, so 750rpx is the full width.
        assertEquals(360f, upSelectMaxHeightDp("750rpx", 800f, 360f))
        assertNull(upSelectMaxHeightDp("", 800f, 360f))
        assertNull(upSelectMaxHeightDp("0vh", 800f, 360f))
        // `optionsWrapStyle.zIndex = this.zIndex + 1`.
        assertEquals(11_001f, upSelectOptionsZIndex(UPSelectProps().zIndex))
        assertEquals(43f, upSelectOptionsZIndex(42))
        assertEquals(11_001f, upSelectOptionsZIndex("not-a-number"))
    }

    @Test
    fun tabbarPlaceholderNeedsBothFixedAndPlaceholder() {
        // `setPlaceholderHeight() { if (!this.fixed || !this.placeholder) return }`.
        assertTrue(upTabbarPlaceholderVisible(fixed = true, placeholder = true))
        assertFalse(upTabbarPlaceholderVisible(fixed = false, placeholder = true))
        assertFalse(upTabbarPlaceholderVisible(fixed = true, placeholder = false))
        assertEquals(1f, upTabbarZIndex(UPTabbarProps().zIndex))
        assertEquals(9f, upTabbarZIndex("9"))
    }

    @Test
    fun cascaderStacksAtTheSharedPopupLayerUnlessOverridden() {
        // `uZIndex() { return this.zIndex ? this.zIndex : zIndex.popup }`; 0 is falsy.
        assertEquals(10_075f, upCascaderZIndex(UPCascaderProps().zIndex))
        assertEquals(10_075f, upCascaderZIndex(0))
        assertEquals(12_000f, upCascaderZIndex(12_000))
    }

    @Test
    fun theCalendarFormatterOverridesOnlyTheKeysItReturns() {
        val (diagnostics, events) = recorder()
        val base = UPCalendarDayMetadata(date = "2026-09-05", bottomInfo = "原始")
        val formatter: (Map<String, UPRawValue>) -> Map<String, UPRawValue> = { config ->
            config + mapOf<String, UPRawValue>("bottomInfo" to "第${config["day"]}天", "dot" to true)
        }

        val formatted = applyCalendarFormatter(formatter, base, day = 5, week = 6, month = 9, disabled = false, diagnostics = diagnostics, component = "UPCalendar")
        assertEquals("第5天", formatted.bottomInfo)
        assertTrue(formatted.dot)
        // Keys the callback leaves alone keep the computed value.
        assertEquals(base.date, formatted.date)
        assertFalse(formatted.disabled)
        assertTrue(events.isEmpty())
    }

    @Test
    fun anUnusableCalendarFormatterIsReportedAndTheDaySurvives() {
        val (diagnostics, events) = recorder()
        val base = UPCalendarDayMetadata(date = "2026-09-05", bottomInfo = "原始")

        // Absent: no diagnostics, no change.
        assertEquals(base, applyCalendarFormatter(null, base, 5, 6, 9, false, diagnostics, "UPCalendar"))
        assertTrue(events.isEmpty())

        // Not callable.
        assertEquals(base, applyCalendarFormatter("nope", base, 5, 6, 9, false, diagnostics, "UPCalendar"))
        // Throws.
        val boom: (Map<String, UPRawValue>) -> Map<String, UPRawValue> = { error("boom") }
        assertEquals(base, applyCalendarFormatter(boom, base, 5, 6, 9, false, diagnostics, "UPCalendar"))
        assertEquals(listOf("formatter", "formatter"), events.map { it.property })
    }

    @Test
    fun datetimeColumnTypesFollowTheModesColumnOrder() {
        assertEquals(listOf("year", "month", "day", "hour", "minute"), datetimeColumnTypes("datetime"))
        assertEquals(listOf("year", "month", "day", "hour", "minute", "second"), datetimeColumnTypes("datetimesecond"))
        assertEquals(listOf("year", "month", "day"), datetimeColumnTypes("date"))
        assertEquals(listOf("year", "month", "day", "hour"), datetimeColumnTypes("datehour"))
        assertEquals(listOf("year", "month"), datetimeColumnTypes("year-month"))
        assertEquals(listOf("hour", "minute"), datetimeColumnTypes("time"))
        assertEquals(listOf("hour", "minute", "second"), datetimeColumnTypes("timesecond"))
        // `type === 'year' ? `${value}` : padZero(value)`.
        assertEquals("2026", datetimeColumnValueText("year", 2026))
        assertEquals("09", datetimeColumnValueText("month", 9))
        assertEquals("00", datetimeColumnValueText("second", 0))
    }

    @Test
    fun theDatetimeFilterDropsOptionsAndFallsBackWhenItEmptiesAColumn() {
        val (diagnostics, events) = recorder()
        val evenMinutes: (String, List<String>) -> List<String> = { _, values ->
            values.filter { (it.toIntOrNull() ?: 0) % 2 == 0 }
        }

        assertEquals(listOf(0, 2, 4), applyDatetimeFilter(evenMinutes, "minute", listOf(0, 1, 2, 3, 4), diagnostics, "UPDatetimePicker"))
        assertTrue(events.isEmpty())

        // Upstream logs "日期filter结果不能为空" and carries on with the unfiltered column.
        val nothing: (String, List<String>) -> List<String> = { _, _ -> emptyList() }
        assertEquals(listOf(1, 2), applyDatetimeFilter(nothing, "minute", listOf(1, 2), diagnostics, "UPDatetimePicker"))
        assertEquals(listOf("filter"), events.map { it.property })
    }

    @Test
    fun theDatetimeFormatterDecoratesEachOptionAndDegradesSafely() {
        val (diagnostics, events) = recorder()
        val suffixed: (String, String) -> String = { type, value -> if (type == "year") "${value}年" else "${value}分" }

        assertEquals("2026年", applyDatetimeFormatter(suffixed, "year", 2026, diagnostics, "UPDatetimePicker"))
        assertEquals("09分", applyDatetimeFormatter(suffixed, "minute", 9, diagnostics, "UPDatetimePicker"))
        // Absent formatter: the padded value, years excepted.
        assertEquals("09", applyDatetimeFormatter(null, "minute", 9, diagnostics, "UPDatetimePicker"))
        assertEquals("2026", applyDatetimeFormatter(null, "year", 2026, diagnostics, "UPDatetimePicker"))
        assertTrue(events.isEmpty())

        assertEquals("09", applyDatetimeFormatter("nope", "minute", 9, diagnostics, "UPDatetimePicker"))
        val boom: (String, String) -> String = { _, _ -> error("boom") }
        assertEquals("09", applyDatetimeFormatter(boom, "minute", 9, diagnostics, "UPDatetimePicker"))
        assertEquals(listOf("formatter", "formatter"), events.map { it.property })
    }
}
