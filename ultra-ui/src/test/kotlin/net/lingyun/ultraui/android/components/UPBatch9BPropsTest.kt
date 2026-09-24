package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import androidx.compose.ui.unit.dp
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
        // uview sizes the step marker from stepsItem.js `iconSize: 17`.
        assertEquals(17, UPStepsItemProps().iconSize)
        assertEquals(50, UPListProps().lowerThreshold)
        assertTrue(UPIndexListProps().sticky)
        assertEquals("", UPIndexAnchorProps().text)
    }

    @Test
    fun toolbarDefaultsMatchUviewTexts() {
        val toolbar = UPToolbarProps()
        assertTrue(toolbar.show)
        assertEquals("取消", toolbar.cancelText)
        assertEquals("确定", toolbar.confirmText)
        assertEquals("#909193", toolbar.cancelColor)
        // Upstream leaves confirmColor empty; the label falls back to the theme primary.
        assertEquals("", toolbar.confirmColor)
        assertEquals("", toolbar.title)
        assertFalse(toolbar.rightSlot)
    }

    @Test
    fun noNetworkDefaultsMatchUviewTips() {
        val noNetwork = UPNoNetworkProps()
        assertEquals("哎呀，网络信号丢失", noNetwork.tips)
        // Upstream ships a base64 PNG; the port defaults to an empty source.
        assertEquals("", noNetwork.image)
        assertEquals("", noNetwork.zIndex)
    }

    @Test
    fun codeDefaultsAndPromptTextMatchUview() {
        val code = UPCodeProps()
        assertEquals("获取验证码", code.startText)
        assertEquals("X秒重新获取", code.changeText)
        assertEquals("重新获取", code.endText)
        assertFalse(code.keepRunning)
        // Idle before start, the X placeholder becomes the second while running, endText after.
        assertEquals("获取验证码", upCodeText(code, running = false, started = false, secNum = 60))
        assertEquals("42秒重新获取", upCodeText(code, running = true, started = true, secNum = 42))
        assertEquals("重新获取", upCodeText(code, running = false, started = true, secNum = 0))
    }

    @Test
    fun messageInputDefaultsAndValueClippingMatchUview() {
        val mi = UPMessageInputProps()
        assertEquals("box", mi.mode)
        assertFalse(mi.dotFill)
        assertTrue(mi.breathe)
        assertEquals("#2979ff", mi.activeColor)
        assertEquals("#606266", mi.inactiveColor)
        // The modelValue watcher stringifies then clips to maxlength.
        assertEquals("123", upMessageInputValue("12345", 3))
        assertEquals("12", upMessageInputValue("12", 4))
        assertEquals("", upMessageInputValue("99", 0))
    }

    @Test
    fun boxDefaultsAndColorFallbackMatchUview() {
        val box = UPBoxProps()
        assertEquals(listOf("#EEFCFF", "#FCF8FF", "#FDF8F2"), box.bgColors)
        assertEquals("160px", box.height)
        assertEquals("6px", box.borderRadius)
        assertEquals("15px", box.gap)
        assertEquals("左", box.leftTitle)
        assertEquals("右上", box.rightTopTitle)
        assertEquals("右下", box.rightBottomTitle)
        // Short arrays fall back to the default palette per index.
        assertEquals("#FCF8FF", upBoxColor(listOf("#000000"), 1))
        assertEquals("#111111", upBoxColor(listOf("#000000", "#111111", "#222222"), 1))
    }

    @Test
    fun agreementDefaultUrlsMatchUview() {
        val agreement = UPAgreementProps()
        assertEquals("/pages/user_agreement/agreement/info?title=用户协议", agreement.urlProtocol)
        assertEquals("/pages/user_agreement/agreement/info?title=隐私政策", agreement.urlPrivacy)
    }

    @Test
    fun copyDefaultsMatchUview() {
        val copy = UPCopyProps()
        assertEquals("", copy.content)
        assertEquals("toast", copy.alertStyle)
        assertEquals("复制成功", copy.notice)
    }

    @Test
    fun floatButtonDefaultsMatchUview() {
        val fab = UPFloatButtonProps()
        assertEquals("#2979ff", fab.backgroundColor)
        assertEquals("#fff", fab.color)
        assertEquals("50px", fab.width)
        assertEquals("30px", fab.right)
        assertFalse(fab.isMenu)
        assertTrue(fab.list.isEmpty())
    }

    @Test
    fun numberKeyboardKeysAndChangeValueMatchUview() {
        assertEquals("number", UPNumberKeyboardProps().mode)
        // Number mode with the dot enabled: 11 keys ending in dot then 0.
        assertEquals(listOf("1","2","3","4","5","6","7","8","9",".","0"), upNumberKeyboardKeys("number", false))
        // dotDisabled drops the dot.
        assertEquals(listOf("1","2","3","4","5","6","7","8","9","0"), upNumberKeyboardKeys("number", true))
        // card swaps the dot for X.
        assertEquals(listOf("1","2","3","4","5","6","7","8","9","X","0"), upNumberKeyboardKeys("card", false))
        // Plain digits become numbers unless the dot is disabled; dot/X stay strings.
        assertEquals(5, upNumberKeyboardChangeValue("number", false, "5"))
        assertEquals("5", upNumberKeyboardChangeValue("number", true, "5"))
        assertEquals(".", upNumberKeyboardChangeValue("number", false, "."))
        assertEquals("X", upNumberKeyboardChangeValue("card", false, "X"))
    }

    @Test
    fun carKeyboardKeySetsAndRowSlicingMatchUview() {
        assertFalse(UPCarKeyboardProps().autoChange)
        assertEquals(36, upCarKeyboardAreaKeys.size)
        assertEquals("京", upCarKeyboardAreaKeys.first())
        assertEquals("学", upCarKeyboardAreaKeys.last())
        assertEquals(36, upCarKeyboardEngKeys.size)
        assertEquals("1", upCarKeyboardEngKeys.first())
        assertEquals("M", upCarKeyboardEngKeys.last())
        // Sliced into rows of 10/10/10/6.
        val rows = upCarKeyboardRows(upCarKeyboardEngKeys)
        assertEquals(listOf(10, 10, 10, 6), rows.map { it.size })
        // [20,30) captures ASDFGHJKL then Z, so the 4th row starts at X.
        assertEquals("X", rows[3].first())
    }

    @Test
    fun keyboardDefaultsAndTipTextMatchUview() {
        val kb = UPKeyboardProps()
        assertEquals("number", kb.mode)
        assertTrue(kb.tooltip)
        assertTrue(kb.showCancel)
        assertTrue(kb.showConfirm)
        assertEquals("取消", kb.cancelText)
        assertEquals("确认", kb.confirmText)
        assertEquals(10075, kb.zIndex)
        // Per-mode default tips.
        assertEquals("数字键盘", upKeyboardDefaultTip("number"))
        assertEquals("身份证键盘", upKeyboardDefaultTip("card"))
        assertEquals("车牌号键盘", upKeyboardDefaultTip("car"))
    }

    @Test
    fun chooseDefaultsAndCurrentIndexMatchUview() {
        val choose = UPChooseProps()
        assertEquals("radio", choose.type)
        assertEquals("title", choose.labelName)
        assertTrue(choose.wrap)
        assertFalse(choose.customClick)
        // modelValue defaults to false (nothing selected -> -1); numbers/strings map to indices.
        assertEquals(-1, upChooseCurrentIndex(false))
        assertEquals(2, upChooseCurrentIndex(2))
        assertEquals(1, upChooseCurrentIndex("1"))
    }

    @Test
    fun viewStyleMapDropsEmptyFieldsAndLayersCustomStyle() {
        val map = upViewStyleMap(
            UPViewProps(
                backgroundColor = "#ffffff",
                width = "100px",
                customStyle = mapOf("padding" to "8px", "backgroundColor" to "#000000"),
            ),
        )
        // Empty fields are dropped; customStyle wins on conflicts.
        assertEquals("100px", map["width"])
        assertEquals("#000000", map["backgroundColor"])
        assertEquals("8px", map["padding"])
        assertFalse(map.containsKey("color"))
    }

    @Test
    fun cateTabDefaultsMatchUview() {
        val cate = UPCateTabProps()
        assertEquals("follow", cate.mode)
        assertEquals("name", cate.tabKeyName)
        assertEquals("name", cate.itemKeyName)
        assertEquals(0, cate.current)
    }

    @Test
    fun couponDefaultsAndHeightMapMatchUview() {
        val coupon = UPCouponProps()
        assertEquals("￥", coupon.unit)
        assertEquals("left", coupon.unitPosition)
        assertEquals("优惠券", coupon.title)
        assertEquals("使用", coupon.actionText)
        assertEquals("coupon", coupon.shape)
        assertEquals("medium", coupon.size)
        assertFalse(coupon.disabled)
        // rpx/2 height map.
        assertEquals(80.dp, upCouponHeight("small"))
        assertEquals(90.dp, upCouponHeight("medium"))
        assertEquals(110.dp, upCouponHeight("large"))
    }

    @Test
    fun lazyLoadDefaultsAndFadeMillisMatchUview() {
        val lazy = UPLazyLoadProps()
        assertEquals("widthFix", lazy.imgMode)
        assertEquals("200", lazy.height)
        assertTrue(lazy.isEffect)
        // duration drives the fade; isEffect=false disables it (0ms).
        assertEquals(500, upLazyLoadFadeMillis(true, 500))
        assertEquals(0, upLazyLoadFadeMillis(false, 500))
    }

    @Test
    fun guideDefaultsMatchUview() {
        val guide = UPGuideProps()
        assertFalse(guide.show)
        assertEquals("up-guide-default", guide.storageKey)
        assertTrue(guide.once)
        assertTrue(guide.showSkip)
        assertEquals("跳过", guide.skipText)
        assertEquals("下一步", guide.nextText)
        assertEquals("立即体验", guide.finishText)
        assertTrue(guide.indicator)
        assertEquals("#111111", guide.bgColor)
        assertEquals(10075, guide.zIndex)
    }

    @Test
    fun cityLocateDefaultsMatchUview() {
        val city = UPCityLocateProps()
        assertEquals(listOf<Any?>("🔥"), city.indexList)
        assertEquals("wgs84", city.locationType)
        assertEquals("name", city.nameKey)
        assertEquals("", city.currentCity)
    }

    @Test
    fun calendarStripSupportComputesMonthDaysAndSwitch() {
        // 2026-02 has 28 days; day 15 is selected, day 20 is today.
        val days = upCalendarStripMonthDays("2026-02", "2026-02-15", "2026-02-20", "", "")
        assertEquals(28, days.size)
        assertEquals(1, days.first().day)
        assertTrue(days.first { it.day == 15 }.selected)
        assertTrue(days.first { it.day == 20 }.today)
        // Month label + shift.
        assertEquals("2026年02月", upCalendarStripMonthLabel("2026-02", ""))
        assertEquals("2026-03", upCalendarStripShiftMonth("2026-02", 1))
        assertEquals("2025-12", upCalendarStripShiftMonth("2026-02", -2))
        // Day clamps to the target month length (Jan 31 -> Feb 28).
        assertEquals("2026-02-28", upCalendarStripDayInMonth("2026-02", 31))
        // Week label maps Sunday(0) to the last slot.
        val week = listOf("一","二","三","四","五","六","日")
        assertEquals("日", upCalendarStripWeekLabel(week, 0))
        assertEquals("一", upCalendarStripWeekLabel(week, 1))
        // minDate bound disables earlier days.
        assertTrue(upCalendarStripDisabled("2026-02-01", "2026-02-10", ""))
        assertFalse(upCalendarStripDisabled("2026-02-15", "2026-02-10", ""))
    }

    @Test
    fun tabsProDefaultsMatchUview() {
        val pro = UPTabsProProps()
        assertEquals("name", pro.keyName)
        assertEquals("static", pro.contentMode)
        assertTrue(pro.scrollable)
        assertTrue(pro.showContent)
        assertEquals(mapOf("height" to "44px"), pro.itemStyle)
    }

    @Test
    fun dragsortDefaultsAndMoveHelperMatchUview() {
        val drag = UPDragsortProps()
        assertTrue(drag.draggable)
        assertEquals("vertical", drag.direction)
        assertEquals(3, drag.columns)
        // splice move: element 0 to index 2.
        assertEquals(listOf("b", "c", "a"), upDragsortMove(listOf("a", "b", "c"), 0, 2))
        assertEquals(listOf("a", "b", "c"), upDragsortMove(listOf("a", "b", "c"), 1, 1))
        // Per-item and global draggable gates.
        assertTrue(upDragsortItemDraggable(true, mapOf("id" to 1)))
        assertFalse(upDragsortItemDraggable(true, mapOf("id" to 1, "draggable" to false)))
        assertFalse(upDragsortItemDraggable(false, mapOf("id" to 1)))
    }

    @Test
    fun waterfallDefaultsAndColumnCountMatchUview() {
        val wf = UPWaterfallProps()
        assertEquals("id", wf.idKey)
        assertEquals(230, wf.minColumnWidth)
        // Numeric columns used directly.
        assertEquals(3, upWaterfallColumnCount(3, 2, 230, 1080))
        assertEquals(2, upWaterfallColumnCount("2", 2, 230, 1080))
        // auto: floor(width / (minColumnWidth + 7)), clamped to columnsMin.
        assertEquals(4, upWaterfallColumnCount("auto", 2, 230, 1000))
        assertEquals(2, upWaterfallColumnCount("auto", 2, 230, 300))
    }

    @Test
    fun virtualListDefaultsAndKeyMatchUview() {
        val vl = UPVirtualListProps()
        assertEquals(50, vl.itemHeight)
        assertEquals(4, vl.buffer)
        assertEquals("id", vl.keyField)
        // getItemKey: item[keyField] or the index fallback.
        assertEquals(7, upVirtualListKey(mapOf("id" to 7), "id", 3))
        assertEquals(3, upVirtualListKey(mapOf("name" to "x"), "id", 3))
    }

    @Test
    fun treeFlattenRespectsExpandedKeys() {
        val fields = UPTreeFields()
        val data = listOf<Any?>(
            mapOf("id" to "a", "label" to "A", "children" to listOf<Any?>(
                mapOf("id" to "a1", "label" to "A1"),
                mapOf("id" to "a2", "label" to "A2"),
            )),
            mapOf("id" to "b", "label" to "B"),
        )
        // Collapsed: only the two roots are visible.
        val collapsed = upTreeFlatten(data, fields, emptySet())
        assertEquals(listOf("a", "b"), collapsed.map { it.key })
        assertTrue(collapsed.first().hasChildren)
        // Expanding "a" reveals its children between a and b.
        val expanded = upTreeFlatten(data, fields, setOf("a"))
        assertEquals(listOf("a", "a1", "a2", "b"), expanded.map { it.key })
        assertEquals(1, expanded[1].level)
        // defaultExpandAll seed collects every key.
        assertEquals(setOf("a", "a1", "a2", "b"), upTreeAllKeys(data, fields).toSet())
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
