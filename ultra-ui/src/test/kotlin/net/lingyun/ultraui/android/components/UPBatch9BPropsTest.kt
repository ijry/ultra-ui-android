package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPBatch9BPropsTest {
    @Test
    fun navigationTabsStepsAndListDefaultsMatchUview() {
        assertTrue(UPNavbarProps().safeAreaInsetTop)
        assertTrue(UPNavbarProps().fixed)
        assertEquals("arrow-left", UPNavbarProps().leftIcon)
        assertEquals("arrow-leftward", UPNavbarMiniProps().leftIcon)
        assertEquals("transparent", UPStatusBarProps().bgColor)
        assertTrue(UPSafeBottomProps().safeAreaInsetBottom)
        assertTrue(UPTabsProps().scrollable)
        assertEquals("button", UPSubsectionProps().mode)
        assertEquals("row", UPStepsProps().direction)
        assertEquals(50, UPListProps().lowerThreshold)
        assertTrue(UPIndexListProps().sticky)
        assertEquals("", UPIndexAnchorProps().text)
    }

    @Test
    fun popupStatusAndNumericPropsPreserveRawValuesAndAliases() {
        val style = mapOf<String, Any?>("padding" to "8px")
        val popover = UPPopoverProps(text = "更多", placement = "bottom", customStyle = style)
        val tooltip = UPTooltipProps(text = "复制", triggerMode = "click", show = true, customStyle = style)
        val countTo = UPCountToProps(startVal = "1.5", endVal = "9.5", decimals = "2", separator = ",")
        val countDown = UPCountDownProps(time = "60000", format = "mm:ss", autoStart = false)
        assertEquals("bottom", popover.placement)
        assertTrue(tooltip.show)
        assertEquals("1.5", countTo.startVal)
        assertEquals("9.5", countTo.endVal)
        assertEquals("60000", countDown.time)
        assertFalse(countDown.autoStart)
        assertEquals(style, tooltip.customStyle)
    }

    @Test
    fun pickerPaginationSelectAndSwipeContractsExposeBackendFields() {
        val picker = UPPickerProps(
            show = true,
            title = "请选择",
            columns = listOf(listOf(mapOf("text" to "北京", "value" to "bj"))),
            modelValue = listOf(0),
        )
        val pagination = UPPaginationProps(currentPage = 2, pageSize = 10, total = 42, layout = "prev, pager, next, total")
        val select = UPSelectProps(options = listOf(mapOf("id" to 1, "name" to "一")), current = 1)
        val swipe = UPSwipeActionItemProps(show = true, options = listOf(mapOf("text" to "删除")))
        assertTrue(picker.show)
        assertEquals(listOf(0), picker.modelValue)
        assertEquals(2, pagination.currentPage)
        assertEquals(42, pagination.total)
        assertEquals(1, select.current)
        assertTrue(swipe.show)
        assertEquals(1, swipe.options.size)
        assertEquals(emptyMap<String, Any?>(), UPPickerColumnProps().customStyle)
    }

    @Test
    fun pickerResolvesModelValuesBeforeLegacyValueAndDefaultIndexes() {
        val columns = listOf(
            listOf(
                mapOf("text" to "北京", "value" to "bj"),
                mapOf("text" to "上海", "value" to "sh"),
            ),
        )

        val modelValueProps = UPPickerProps(
            columns = columns,
            modelValue = listOf("sh"),
            value = listOf("bj"),
            defaultIndex = listOf(0),
        )
        val legacyValueProps = UPPickerProps(
            columns = columns,
            value = listOf("sh"),
            defaultIndex = listOf(0),
        )

        assertEquals(listOf(1), resolvePickerIndexes(modelValueProps))
        assertEquals(listOf("sh"), pickerModelValues(modelValueProps, listOf(1)))
        assertEquals(listOf(1), resolvePickerIndexes(legacyValueProps))
    }

    @Test
    fun readMoreControlledAliasAndToggleVisibilityMatchTheNativeContract() {
        assertTrue(resolveReadMoreOpen(UPReadMoreProps(modelValue = true, value = false)))
        assertFalse(resolveReadMoreOpen(UPReadMoreProps(modelValue = false, value = true)))
        assertTrue(resolveReadMoreOpen(UPReadMoreProps(value = true)))
        assertFalse(resolveReadMoreOpen(UPReadMoreProps(toggle = true)))
        assertFalse(shouldShowReadMoreControl(open = true, toggle = false))
        assertTrue(shouldShowReadMoreControl(open = true, toggle = true))
        assertTrue(shouldShowReadMoreControl(open = false, toggle = false))
    }

    @Test
    fun pickerEventKeepsUviewChangePayloadFields() {
        val props = UPPickerProps(
            columns = listOf(
                listOf("北京", "上海"),
                listOf("男", "女"),
            ),
        )
        val event = pickerEvent(props, listOf(1, 0), columnIndex = 0, index = 1)
        assertEquals(1, event.index)
        assertEquals(listOf(listOf("北京", "上海"), listOf("男", "女")), event.values)
        assertEquals(0, event.columnIndex)
        assertEquals(listOf("上海", "男"), event.value)
        assertEquals(listOf(1, 0), event.indexs)
        assertEquals(1, pickerEvent(props, listOf(0, 1), columnIndex = 1, index = 1).index)
        assertEquals(1, pickerEvent(props, listOf(0, 1), columnIndex = 1, index = 1).columnIndex)
    }
}
