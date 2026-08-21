package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

class UPBatch10PropsTest {
    @Test
    fun calendarDefaultsPreserveUviewFields() {
        val props = UPCalendarProps()

        assertEquals("single", props.mode)
        assertEquals("#3c9cff", props.color)
        assertEquals(56, props.rowHeight)
        assertEquals("all", props.rangeResultMode)
        assertTrue(props.showMark)
        assertTrue(props.showConfirm)
        assertTrue(props.showToday)
        assertTrue(props.overlay)
        assertTrue(props.safeAreaInsetBottom)
        assertFalse(props.safeAreaInsetTop)
        assertFalse(props.closeOnClickOverlay)
    }

    @Test
    fun datetimePickerKeepsValueAndDynamicDateBounds() {
        val props = UPDatetimePickerProps()

        assertEquals("", props.value)
        assertEquals("datetime", props.mode)
        assertEquals("bottom", props.popupMode)
        assertEquals(0, props.minHour)
        assertEquals(23, props.maxHour)
        assertEquals(0, props.minMinute)
        assertEquals(59, props.maxMinute)
        assertTrue((props.minDate as Number).toLong() < (props.maxDate as Number).toLong())
        assertEquals("请选择", props.placeholder)
    }

    @Test
    fun datetimePickerPreservesInputCompatibilityFields() {
        val props = UPDatetimePickerProps(
            hasInput = true,
            format = "YYYY-MM-DD",
            toolbarRightSlot = true,
            maskClass = "mask",
            maskStyle = "opacity: .5",
        )

        assertTrue(props.hasInput)
        assertEquals("YYYY-MM-DD", props.format)
        assertTrue(props.toolbarRightSlot)
        assertEquals("mask", props.maskClass)
        assertEquals("opacity: .5", props.maskStyle)
    }

    @Test
    fun cascaderUsesDataAndLabelDefaultsFromUview() {
        val data = listOf(
            mapOf(
                "value" to "zhejiang",
                "label" to "浙江",
                "children" to listOf(mapOf("value" to "hangzhou", "label" to "杭州")),
            ),
        )
        val props = UPCascaderProps(data = data, modelValue = listOf("zhejiang"))

        assertEquals(data, props.data)
        assertEquals(listOf("zhejiang"), props.modelValue)
        assertEquals("value", props.valueKey)
        assertEquals("label", props.labelKey)
        assertEquals("children", props.childrenKey)
        assertTrue(props.maskCloseAble)
        assertTrue(props.closeable)
    }

    @Test
    fun sliderPreservesSingleAndRangeControlledValues() {
        val props = UPSliderProps(value = 30, isRange = true, rangeValue = listOf(0, 0), min = 0, max = 100, step = 5)

        assertEquals(30, props.value)
        assertTrue(props.isRange)
        assertEquals(listOf(0, 0), props.rangeValue)
        assertEquals(0, props.min)
        assertEquals(100, props.max)
        assertEquals(5, props.step)
        assertEquals(18, props.blockSize)
        assertFalse(props.disabled)
    }

    @Test
    fun sliderTrackUsesZeroToValueForSingleAndValueToValueForRange() {
        assertEquals(
            0f to 0.3f,
            sliderTrackFractions(listOf(30f), min = 0f, max = 100f, range = false),
        )
        assertEquals(
            0.2f to 0.7f,
            sliderTrackFractions(listOf(20f, 70f), min = 0f, max = 100f, range = true),
        )
    }

    @Test
    fun tabbarAndItemsExposeBackendValueAndVisualFields() {
        val tabbar = UPTabbarProps(value = "home", fixed = true, placeholder = true)
        val item = UPTabbarItemProps(
            name = "home",
            icon = "home",
            activeIcon = "home-fill",
            badge = 3,
            dot = true,
        )

        assertEquals("home", tabbar.value)
        assertTrue(tabbar.safeAreaInsetBottom)
        assertTrue(tabbar.border)
        assertEquals("#1989fa", tabbar.activeColor)
        assertEquals("#7d7e80", tabbar.inactiveColor)
        assertTrue(tabbar.fixed)
        assertTrue(tabbar.placeholder)
        assertEquals("default", tabbar.styleType)
        assertEquals("none", tabbar.animationType)
        assertEquals("home", item.name)
        assertEquals("home-fill", item.activeIcon)
        assertEquals(3, item.badge)
        assertTrue(item.dot)
    }

    @Test
    fun tabbarItemDefaultsMatchUviewMiddleButtonContract() {
        val item = UPTabbarItemProps()

        assertEquals("", item.mode)
        assertEquals(26, item.midButtonIconSize)
        assertEquals(-10, item.midButtonOffsetY)
    }

    @Test
    fun calendarSelectionSupportsSingleMultipleAndRangeModes() {
        assertEquals(listOf("2026-08-20"), updateCalendarSelection("single", emptyList(), "2026-08-20", 10, false))
        assertEquals(
            listOf("2026-08-20", "2026-08-21"),
            updateCalendarSelection("multiple", listOf("2026-08-20"), "2026-08-21", 10, false),
        )
        assertEquals(
            emptyList<String>(),
            updateCalendarSelection("multiple", listOf("2026-08-20"), "2026-08-20", 10, false),
        )
        assertEquals(
            listOf("2026-08-20", "2026-08-23"),
            updateCalendarSelection("range", listOf("2026-08-20"), "2026-08-23", 10, false),
        )
        assertEquals(
            listOf("2026-08-19"),
            updateCalendarSelection("range", listOf("2026-08-20"), "2026-08-19", 10, false),
        )
        assertEquals(
            listOf("2026-08-20"),
            updateCalendarSelection("range", listOf("2026-08-20"), "2026-08-20", 10, false),
        )
        assertEquals(
            listOf("2026-08-20", "2026-08-20"),
            updateCalendarSelection("range", listOf("2026-08-20"), "2026-08-20", 10, true),
        )
    }

    @Test
    fun calendarDateBoundsDisableDatesOutsideTheAllowedWindow() {
        val props = UPCalendarProps(minDate = "2026-08-10", maxDate = "2026-08-20")

        assertTrue(calendarDateAllowed(props, "2026-08-10"))
        assertTrue(calendarDateAllowed(props, "2026-08-20"))
        assertFalse(calendarDateAllowed(props, "2026-08-09"))
        assertFalse(calendarDateAllowed(props, "2026-08-21"))
    }

    @Test
    fun datetimeColumnsAndTimestampFollowModeAndIndexes() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 20, 14, 35, 42)
            set(Calendar.MILLISECOND, 0)
        }
        val props = UPDatetimePickerProps(value = calendar.timeInMillis, mode = "datetime")
        val state = resolveDatetimeSelection(props)

        assertEquals(listOf(2026, 8, 20, 14, 35, 42), state.values)
        assertEquals(5, datetimeColumns(props, state).size)
        assertEquals(calendar.timeInMillis - 42_000L, datetimeTimestamp(props, state))
        assertEquals(2, datetimeColumns(props.copy(mode = "year-month"), state).size)
    }

    @Test
    fun datetimePickerUsesUviewColumnShapeForAllSupportedModes() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 20, 14, 35, 42)
            set(Calendar.MILLISECOND, 0)
        }
        val state = resolveDatetimeSelection(UPDatetimePickerProps(value = calendar.timeInMillis))

        assertEquals(2, datetimeColumns(UPDatetimePickerProps(mode = "time"), state).size)
        assertEquals(4, datetimeColumns(UPDatetimePickerProps(mode = "datehour"), state).size)
        assertEquals(3, datetimeColumns(UPDatetimePickerProps(mode = "timesecond"), state).size)
        assertEquals(6, datetimeColumns(UPDatetimePickerProps(mode = "datetimesecond"), state).size)
        assertEquals(
            calendar.timeInMillis,
            datetimeTimestamp(UPDatetimePickerProps(mode = "datetimesecond"), state),
        )
    }

    @Test
    fun datetimePickerColumnsRespectExactDateBounds() {
        val min = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 20, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val max = Calendar.getInstance().apply {
            set(2027, Calendar.FEBRUARY, 10, 23, 59, 59)
            set(Calendar.MILLISECOND, 0)
        }
        val props = UPDatetimePickerProps(
            value = min.timeInMillis,
            minDate = min.timeInMillis,
            maxDate = max.timeInMillis,
            mode = "date",
        )

        val minColumns = datetimeColumns(props, resolveDatetimeSelection(props))
        assertEquals((8..12).toList(), minColumns[1])
        assertEquals((20..31).toList(), minColumns[2])

        val maxColumns = datetimeColumns(
            props,
            resolveDatetimeSelection(props.copy(value = max.timeInMillis)),
        )
        assertEquals((1..2).toList(), maxColumns[1])
        assertEquals((1..10).toList(), maxColumns[2])
    }

    @Test
    fun datetimePickerColumnsRespectExactTimeBounds() {
        val min = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 20, 14, 35, 10)
            set(Calendar.MILLISECOND, 0)
        }
        val max = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 21, 10, 20, 30)
            set(Calendar.MILLISECOND, 0)
        }
        val props = UPDatetimePickerProps(
            value = min.timeInMillis,
            minDate = min.timeInMillis,
            maxDate = max.timeInMillis,
            mode = "datetimesecond",
        )

        val minColumns = datetimeColumns(props, resolveDatetimeSelection(props))
        assertEquals((14..23).toList(), minColumns[3])
        assertEquals((35..59).toList(), minColumns[4])
        assertEquals((10..59).toList(), minColumns[5])

        val maxColumns = datetimeColumns(
            props,
            resolveDatetimeSelection(props.copy(value = max.timeInMillis)),
        )
        assertEquals((0..10).toList(), maxColumns[3])
        assertEquals((0..20).toList(), maxColumns[4])
        assertEquals((0..30).toList(), maxColumns[5])
    }

    @Test
    fun datetimePickerNormalizesDependentColumnsAfterParentChanges() {
        val date = Calendar.getInstance().apply {
            set(2026, Calendar.JANUARY, 31, 14, 35, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val props = UPDatetimePickerProps(value = date.timeInMillis, mode = "datetime")
        val state = resolveDatetimeSelection(props)

        val february = updateDatetimeSelection(props, state, column = 1, option = 2)

        assertEquals(listOf(2026, 2, 28, 14, 35, 0), february.values)
        assertEquals(28, datetimeEvent(props, february).selectedValues[2])
    }

    @Test
    fun datetimePickerPreservesTimeStringsAndClearsHiddenDateFields() {
        val time = resolveDatetimeSelection(UPDatetimePickerProps(mode = "time", value = "14:35"))
        val timeSecond = resolveDatetimeSelection(UPDatetimePickerProps(mode = "timesecond", value = "14:35:42"))
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 20, 14, 35, 42)
            set(Calendar.MILLISECOND, 0)
        }
        val dateState = resolveDatetimeSelection(UPDatetimePickerProps(value = calendar.timeInMillis))

        assertEquals(listOf(14, 35), time.values.takeLast(3).take(2))
        assertEquals(listOf(14, 35, 42), timeSecond.values.takeLast(3))
        assertEquals(3, datetimeValueIndex("timesecond", 0))
        assertEquals("14:35", datetimeEvent(UPDatetimePickerProps(mode = "time"), time).value)
        assertEquals("14:35:42", datetimeEvent(UPDatetimePickerProps(mode = "timesecond"), timeSecond).value)

        val date = Calendar.getInstance().apply { timeInMillis = datetimeTimestamp(UPDatetimePickerProps(mode = "date"), dateState) }
        assertEquals(0, date.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, date.get(Calendar.MINUTE))
        val dateHour = Calendar.getInstance().apply { timeInMillis = datetimeTimestamp(UPDatetimePickerProps(mode = "datehour"), dateState) }
        assertEquals(14, dateHour.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, dateHour.get(Calendar.MINUTE))
    }

    @Test
    fun cascaderBuildsAValuePathAndSelectedOptions() {
        val data = listOf(
            mapOf(
                "value" to "zhejiang",
                "label" to "浙江",
                "children" to listOf(mapOf("value" to "hangzhou", "label" to "杭州")),
            ),
        )
        val first = updateCascaderSelection(UPCascaderProps(data = data), emptyList(), 0, data.first())
        val child = (data.first()["children"] as List<*>).first()
        val second = updateCascaderSelection(UPCascaderProps(data = data), first.value, 1, child)

        assertEquals(listOf("zhejiang"), first.value)
        assertEquals(listOf("zhejiang", "hangzhou"), second.value)
        assertEquals(listOf("浙江", "杭州"), second.selectedOptions.map { (it as Map<*, *>)["label"] })
        assertTrue(cascaderOptionsAt(UPCascaderProps(data = data), first.value, 1).isNotEmpty())
    }

    @Test
    fun sliderQuantizesAndTabbarResolvesAliases() {
        assertEquals(35f, quantizeSliderValue(33f, min = 0f, max = 100f, step = 5f))
        assertEquals(0f, quantizeSliderValue(-10f, min = 0f, max = 100f, step = 5f))
        assertEquals(100f, quantizeSliderValue(110f, min = 0f, max = 100f, step = 5f))
        assertEquals("model", resolveTabbarValue(UPTabbarProps(value = "value", modelValue = "model", current = "current")))
        assertEquals("value", resolveTabbarValue(UPTabbarProps(value = "value", current = "current")))
        assertEquals("current", resolveTabbarValue(UPTabbarProps(current = "current")))
    }

    @Test
    fun sliderModeUsesExplicitRangePropAndResetsValues() {
        assertEquals(
            listOf(30f),
            resolveSliderValues(UPSliderProps(value = 30, isRange = false, rangeValue = listOf(10, 90)), 0f, 100f, 1f),
        )
        assertEquals(
            listOf(10f, 90f),
            resolveSliderValues(UPSliderProps(value = 30, isRange = true, rangeValue = listOf(10, 90)), 0f, 100f, 1f),
        )
    }

    @Test
    fun sliderPositionMapsAcrossHorizontalAndVerticalTracks() {
        val size = IntSize(width = 200, height = 300)

        assertEquals(0.25f, sliderPositionFraction(Offset(50f, 150f), size, vertical = false))
        assertEquals(0.5f, sliderPositionFraction(Offset(50f, 150f), size, vertical = true))
        assertEquals(1f, sliderPositionFraction(Offset(50f, -10f), size, vertical = true))
        assertEquals(0f, sliderPositionFraction(Offset(50f, 310f), size, vertical = true))
    }

    @Test
    fun sliderAxisOffsetsGrowRightwardAndUpward() {
        assertEquals(40f, sliderAxisOffset(0.2f, trackLength = 200f, vertical = false))
        assertEquals(160f, sliderAxisOffset(0.2f, trackLength = 200f, vertical = true))
    }

    @Test
    fun tabbarRecognizesOnlyTheUviewMiddleButtonMode() {
        assertTrue(isTabbarMiddleButton("midButton"))
        assertFalse(isTabbarMiddleButton("middle"))
        assertFalse(isTabbarMiddleButton(""))
    }

    @Test
    fun tabbarIconSizeUsesMidButtonSizeOnlyForTheMiddleButton() {
        assertEquals(24, tabbarIconSize("", midButtonIconSize = 26))
        assertEquals(24, tabbarIconSize("middle", midButtonIconSize = 26))
        assertEquals(26, tabbarIconSize("midButton", midButtonIconSize = 26))
        assertEquals(40, tabbarIconSize("midButton", midButtonIconSize = 40))
    }

    @Test
    fun tabbarBadgeVisibilityMatchesUviewPositiveValueRule() {
        assertTrue(tabbarBadgeIsVisible(dot = true, badge = 0))
        assertTrue(tabbarBadgeIsVisible(dot = false, badge = 3))
        assertFalse(tabbarBadgeIsVisible(dot = false, badge = 0))
    }

    @Test
    fun tabbarUsesIndexForUnnamedItemsAndChangesOnlyWhenSelectionDiffers() {
        assertEquals(2, tabbarItemValue(null, 2))
        assertEquals(2, tabbarItemValue("", 2))
        assertEquals("settings", tabbarItemValue("settings", 2))
        assertEquals(null, tabbarChangeEvent("home", "home", 1))
        assertEquals(UPTabbarChangeEvent(1, 1), tabbarChangeEvent("home", 1, 1))
    }
}
