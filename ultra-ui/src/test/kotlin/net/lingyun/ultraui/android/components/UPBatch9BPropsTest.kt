package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.UPRawValue
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
    fun pullRefreshDefaultsAndStatusMatchUview() {
        val pr = UPPullRefreshProps()
        assertEquals(80, pr.threshold)
        assertEquals(120, pr.maxDistance)
        // distance = min(diff * damping, maxDistance).
        assertEquals(40f, upPullRefreshDistance(100f, 0.4f, 120), 0.001f)
        assertEquals(120f, upPullRefreshDistance(1000f, 0.4f, 120), 0.001f)
        assertEquals(0f, upPullRefreshDistance(-50f, 0.4f, 120), 0.001f)
        // status: refreshing dominates, else release past threshold, else pull.
        assertEquals(UPPullRefreshStatus.Pull, upPullRefreshStatus(40f, 80, false))
        assertEquals(UPPullRefreshStatus.Release, upPullRefreshStatus(90f, 80, false))
        assertEquals(UPPullRefreshStatus.Refreshing, upPullRefreshStatus(10f, 80, true))
    }

    @Test
    fun tableDefaultsAndAlignMatchUview() {
        val table = UPTableProps()
        assertEquals("#e4e7ed", table.borderColor)
        assertEquals("center", table.align)
        assertEquals("5px 3px", table.padding)
        assertEquals("14px", table.fontSize)
        assertEquals("auto", UPTdProps().width)
        // align string -> Compose TextAlign.
        assertEquals(androidx.compose.ui.text.style.TextAlign.Start, upTableAlign("left"))
        assertEquals(androidx.compose.ui.text.style.TextAlign.End, upTableAlign("right"))
        assertEquals(androidx.compose.ui.text.style.TextAlign.Center, upTableAlign("center"))
    }

    @Test
    fun goodsSkuCombMatchesOnlyWhenFullySelected() {
        val tree = listOf<Any?>(
            mapOf("label" to "颜色", "name" to "color", "children" to listOf<Any?>(mapOf("id" to "r", "name" to "红"), mapOf("id" to "b", "name" to "蓝"))),
            mapOf("label" to "尺寸", "name" to "size", "children" to listOf<Any?>(mapOf("id" to "s", "name" to "S"), mapOf("id" to "m", "name" to "M"))),
        )
        val list = listOf<Any?>(
            mapOf("color" to "r", "size" to "s", "price" to 10, "stock" to 5),
            mapOf("color" to "b", "size" to "m", "price" to 12, "stock" to 0),
        )
        // Partial selection -> null.
        assertEquals(null, upGoodsSkuComb(tree, list, mapOf("color" to "r")))
        // Full match.
        assertEquals(10, upGoodsSkuComb(tree, list, mapOf("color" to "r", "size" to "s"))?.get("price"))
        // Full but no matching row -> null.
        assertEquals(null, upGoodsSkuComb(tree, list, mapOf("color" to "r", "size" to "m")))
        // color=r keeps size=s reachable, size=m unreachable.
        assertFalse(upGoodsSkuLeafDisabled(tree, list, mapOf("color" to "r"), "size", "s"))
        assertTrue(upGoodsSkuLeafDisabled(tree, list, mapOf("color" to "r"), "size", "m"))
    }

    @Test
    fun signatureDefaultsAndEmptinessMatchUview() {
        val sig = UPSignatureProps()
        assertEquals("#ffffff", sig.bgColor)
        assertEquals("#000000", sig.color)
        assertTrue(sig.showToolbar)
        // A pad with no multi-point strokes is empty.
        assertTrue(upSignatureIsEmpty(emptyList()))
        val drawn = UPSignatureStroke().apply { points.add(androidx.compose.ui.geometry.Offset(0f, 0f)); points.add(androidx.compose.ui.geometry.Offset(5f, 5f)) }
        assertFalse(upSignatureIsEmpty(listOf(drawn)))
    }

    @Test
    fun colorPickerHslHexRoundTripMatchesUview() {
        val cp = UPColorPickerProps()
        assertEquals("#ff0000", cp.modelValue)
        // Primary hues.
        assertEquals("#ff0000", upColorHslToHex(0f, 100f, 50f))
        assertEquals("#00ff00", upColorHslToHex(120f, 100f, 50f))
        assertEquals("#0000ff", upColorHslToHex(240f, 100f, 50f))
        assertEquals("#ffffff", upColorHslToHex(0f, 0f, 100f))
        // hex -> hsl round trip stays in family.
        val hsl = upColorHexToHsl("#0000ff")
        assertEquals(240f, hsl.h, 1f)
        assertEquals(100f, hsl.s, 1f)
        assertEquals(50f, hsl.l, 1f)
        // 3-digit shorthand.
        assertEquals(0f, upColorHexToHsl("#f00").h, 1f)
    }

    @Test
    fun pdfReaderDefaultsAndViewerUrlMatchUview() {
        val pdf = UPPdfReaderProps()
        assertEquals("500px", pdf.height)
        assertEquals("https://uview-plus.jiangruyi.com/h5", pdf.baseUrl)
        // viewerUrl = baseUrl + viewer path + encoded src.
        assertEquals(
            "https://host/static/pdfjs/web/viewer.html?file=https%3A%2F%2Fx.com%2Fa.pdf",
            upPdfReaderViewerUrl("https://host", "https://x.com/a.pdf"),
        )
    }

    @Test
    fun qrcodeDefaultsAndFinderCenterMatchUview() {
        val qr = UPQrcodeProps()
        assertEquals(200, qr.size)
        assertEquals("px", qr.unit)
        assertEquals(true, qr.show)
        assertEquals("#ffffff", qr.background)
        assertEquals("#000000", qr.foreground)
        assertEquals("#000000", qr.pdground)
        assertEquals(40, qr.iconSize)
        assertEquals(3, qr.lv)
        assertEquals(0, qr.quietZone)
        assertEquals("生成中", qr.loadingText)
        assertEquals(false, qr.allowPreview)
        // Finder-pattern centres (3x3) of the three corners use pdground; a v1 (21x21) grid.
        assert(upQrcodeIsFinderCenter(3, 3, 21))
        assert(upQrcodeIsFinderCenter(3, 17, 21))
        assert(upQrcodeIsFinderCenter(17, 3, 21))
        assert(!upQrcodeIsFinderCenter(10, 10, 21))
        assert(!upQrcodeIsFinderCenter(3, 10, 21))
    }

    @Test
    fun barcodeDefaultsAndLayoutMatchUview() {
        val bc = UPBarcodeProps()
        assertEquals("auto", bc.format)
        assertEquals(200, bc.width)
        assertEquals(80, bc.height)
        assertEquals(true, bc.displayValue)
        assertEquals("monospace", bc.font)
        assertEquals("center", bc.textAlign)
        assertEquals("bottom", bc.textPosition)
        assertEquals(2, bc.textMargin)
        assertEquals(14, bc.fontSize)
        assertEquals(10, bc.margin)
        // width 200 + margins 10*2 = 220; height 80 + textHeight(14+2=16) + margins 20 = 116.
        val layout = upBarcodeLayout(200, 80, true, 14, 2, "bottom", 10, 10, 10, 10)
        assertEquals(220, layout.canvasWidth)
        assertEquals(116, layout.canvasHeight)
        assertEquals(16, layout.textHeight)
        // No text band when displayValue=false; height floored at 60.
        val bare = upBarcodeLayout(50, 30, false, 14, 2, "bottom", 0, 0, 0, 0)
        assertEquals(100, bare.canvasWidth)
        assertEquals(60, bare.canvasHeight)
    }

    @Test
    fun refreshVirtualListDefaultsAndControllerMatchUview() {
        val rv = UPRefreshVirtualListProps()
        assertEquals(50, rv.itemHeight)
        assertEquals("100%", rv.height)
        assertEquals(4, rv.buffer)
        assertEquals("id", rv.keyField)
        val controller = UPRefreshVirtualListController()
        controller.beginRefresh()
        assertEquals(true, controller.refreshing)
        controller.finishRefresh()
        assertEquals(false, controller.refreshing)
        controller.scrollTo(120)
        assertEquals(120, controller.scrollTop)
        val token = controller.remountToken
        controller.scrollToTop()
        assertEquals(0, controller.scrollTop)
        assert(controller.remountToken > token)
    }

    @Test
    fun transitionDefaultsSpecAndDurationMatchUview() {
        val tr = UPTransitionProps()
        assertEquals(false, tr.show)
        assertEquals("fade", tr.mode)
        assertEquals("300", tr.duration)
        assertEquals("ease-out", tr.timingFunction)
        // fade only.
        assertEquals(UPTransitionSpec(fade = true, scale = false, offsetXSign = 0, offsetYSign = 0), upTransitionSpec("fade"))
        assertEquals(UPTransitionSpec(fade = true, scale = true, offsetXSign = 0, offsetYSign = 0), upTransitionSpec("fade-zoom"))
        // fade-up rises from below (positive Y), fade-down drops from above (negative Y).
        assertEquals(1, upTransitionSpec("fade-up").offsetYSign)
        assertEquals(-1, upTransitionSpec("fade-down").offsetYSign)
        assertEquals(-1, upTransitionSpec("fade-left").offsetXSign)
        assertEquals(1, upTransitionSpec("fade-right").offsetXSign)
        // slide variants do not fade.
        assertEquals(false, upTransitionSpec("slide-up").fade)
        assertEquals(1, upTransitionSpec("slide-up").offsetYSign)
        // Unknown mode falls back to fade.
        assertEquals(UPTransitionSpec(fade = true, scale = false, offsetXSign = 0, offsetYSign = 0), upTransitionSpec("nope"))
        // Duration parses String|Number ms.
        assertEquals(300, upTransitionDurationMillis("300"))
        assertEquals(450, upTransitionDurationMillis(450))
        assertEquals(300, upTransitionDurationMillis(null))
    }

    @Test
    fun parseDefaultsUrlResolutionAndInlineHelpersMatchUview() {
        val pr = UPParseProps()
        assertEquals(true, pr.copyLink)
        assertEquals(true, pr.previewImg)
        assertEquals(true, pr.setTitle)
        assertEquals(true, pr.showImgMenu)
        assertEquals(false, pr.lazyLoad)
        assertEquals(false, pr.selectable)
        assertEquals(true, pr.pauseVideo)
        // URL resolution against a domain.
        assertEquals("https://x.com/a.png", upParseResolveUrl("https://cdn.io", "https://x.com/a.png"))
        assertEquals("https://cdn.io/a.png", upParseResolveUrl("https://cdn.io/", "/a.png"))
        assertEquals("https://cdn.io/img/a.png", upParseResolveUrl("https://cdn.io", "img/a.png"))
        assertEquals("https://x.com/a.png", upParseResolveUrl("https://cdn.io", "//x.com/a.png"))
        assertEquals("rel.png", upParseResolveUrl("", "rel.png"))
        // Inline text flatten + style map.
        val nodes = upParseHtml("<p>Hi <b>bold</b><br>next</p>")
        assertEquals("Hi bold\nnext", upParseInlineText(nodes))
        assertEquals(mapOf("color" to "red", "font-size" to "12px"), upParseStyleMap("color: red; font-size:12px;"))
        // Inline span: bold for <b>, null for a plain <span>.
        assertEquals(androidx.compose.ui.text.font.FontWeight.Bold, upParseInlineSpan(UPParseElement("b", emptyMap(), emptyList()), emptyMap())?.fontWeight)
        assertEquals(null, upParseInlineSpan(UPParseElement("span", emptyMap(), emptyList()), emptyMap()))
    }

    @Test
    fun markdownDefaultsAndConversionMatchPipeline() {
        val md = UPMarkdownProps()
        assertEquals(true, md.previewImg)
        assertEquals(true, md.copyLink)
        assertEquals(false, md.showLineNumber)
        assertEquals("light", md.theme)
        // Headings, emphasis, inline code.
        assertEquals("<h1>Title</h1>", upMarkdownToHtml("# Title"))
        assertEquals("<p><strong>b</strong> and <em>i</em> and <code>c</code></p>", upMarkdownToHtml("**b** and *i* and `c`"))
        // Links and images.
        assertEquals("<p><a href=\"http://x.com\">go</a></p>", upMarkdownToHtml("[go](http://x.com)"))
        assertEquals("<p><img src=\"a.png\" alt=\"alt\"></p>", upMarkdownToHtml("![alt](a.png)"))
        // Lists.
        assertEquals("<ul><li>a</li><li>b</li></ul>", upMarkdownToHtml("- a\n- b"))
        assertEquals("<ol><li>one</li><li>two</li></ol>", upMarkdownToHtml("1. one\n2. two"))
        // Blockquote + hr.
        assertEquals("<blockquote>quote</blockquote>", upMarkdownToHtml("> quote"))
        assertEquals("<hr>", upMarkdownToHtml("---"))
        // Fenced code escapes HTML and, with line numbers, prefixes each line.
        assertEquals("<pre><code class=\"language-js\">a&lt;b</code></pre>", upMarkdownToHtml("```js\na<b\n```"))
        assertEquals("<pre><code>1 x\n2 y</code></pre>", upMarkdownToHtml("```\nx\ny\n```", showLineNumber = true))
    }

    @Test
    fun table2DefaultsAndSortHelpersMatchUview() {
        val t = UPTable2Props()
        assertEquals("id", t.rowKey)
        assertEquals(true, t.showHeader)
        assertEquals(true, t.fixedHeader)
        assertEquals("暂无数据", t.emptyText)
        assertEquals("36px", t.rowHeight)
        assertEquals(listOf<Any?>("ascending", "descending"), t.sortOrders)
        // Sort-order cycle: none -> ascending -> descending -> none.
        val orders = listOf("ascending", "descending")
        assertEquals("ascending", upTable2NextSortOrder("", orders))
        assertEquals("descending", upTable2NextSortOrder("ascending", orders))
        assertEquals("", upTable2NextSortOrder("descending", orders))
        // Numeric-aware sort by key.
        val data = listOf<Any?>(
            mapOf<String, Any?>("id" to 1, "age" to 30),
            mapOf<String, Any?>("id" to 2, "age" to 9),
            mapOf<String, Any?>("id" to 3, "age" to 21),
        )
        val asc = upTable2SortData(data, "age", "ascending").map { (it as Map<*, *>)["age"] }
        assertEquals(listOf<Any?>(9, 21, 30), asc)
        val desc = upTable2SortData(data, "age", "descending").map { (it as Map<*, *>)["age"] }
        assertEquals(listOf<Any?>(30, 21, 9), desc)
        // Unknown order leaves input untouched.
        assertEquals(data, upTable2SortData(data, "age", ""))
    }

    @Test
    fun posterHelpersMatchUpstream() {
        assertEquals(true, upPosterIsGradient("linear-gradient(135deg, #ff0000, #0000ff)"))
        assertEquals(true, upPosterIsGradient("radial-gradient(#fff, #000)"))
        assertEquals(false, upPosterIsGradient("#ff0000"))
        assertEquals(listOf("#ff0000", "#0000ff"), upPosterExtractColors("linear-gradient(135deg, #ff0000, #0000ff)"))
        assertEquals(listOf("rgba(0,0,0,0.5)", "#fff"), upPosterExtractColors("linear-gradient(rgba(0,0,0,0.5), #fff)"))
        // rpx halves to sp; px/number as-is; blank defaults to 14sp.
        assertEquals(14f, upPosterFontSizeSp("28rpx").value, 0.01f)
        assertEquals(16f, upPosterFontSizeSp("16px").value, 0.01f)
        assertEquals(14f, upPosterFontSizeSp("").value, 0.01f)
    }

    @Test
    fun uploadDefaultsAndItemSourceMatchUview() {
        val u = UPUploadProps()
        assertEquals("image", u.accept)
        assertEquals("camera-fill", u.uploadIcon)
        assertEquals("#D3D4D6", u.uploadIconColor)
        assertEquals(true, u.previewFullImage)
        assertEquals(true, u.deletable)
        assertEquals(true, u.previewImage)
        assertEquals(false, u.disabled)
        assertEquals("aspectFill", u.imageMode)
        assertEquals(52, u.maxCount.upIntOrDefault(0))
        // thumb wins over url; falls back to url.
        assertEquals("t.png", upUploadItemSource(mapOf("thumb" to "t.png", "url" to "u.png")))
        assertEquals("u.png", upUploadItemSource(mapOf("url" to "u.png")))
        assertEquals("", upUploadItemSource(emptyMap()))
    }

    @Test
    fun albumDefaultsAndSrcResolutionMatchUview() {
        val a = UPAlbumProps()
        assertEquals(9, a.maxCount.upIntOrDefault(0))
        assertEquals(3, a.rowCount.upIntOrDefault(0))
        assertEquals("scaleToFill", a.singleMode)
        assertEquals("aspectFill", a.multipleMode)
        assertEquals(true, a.previewFullImage)
        assertEquals(true, a.showMore)
        assertEquals("square", a.shape)
        assertEquals("px", a.unit)
        // src resolution: plain string, keyName, then url/src fallbacks.
        assertEquals("a.png", upAlbumSrc("a.png", ""))
        assertEquals("k.png", upAlbumSrc(mapOf("photo" to "k.png"), "photo"))
        assertEquals("u.png", upAlbumSrc(mapOf("url" to "u.png"), ""))
        assertEquals("s.png", upAlbumSrc(mapOf("src" to "s.png"), ""))
    }

    @Test
    fun canvasDefaultsMatchUview() {
        val c = UPCanvasProps()
        assertEquals(300, c.width.upIntOrDefault(0))
        assertEquals(300, c.height.upIntOrDefault(0))
        assertEquals("px", c.unit)
        assertEquals("#ffffff", c.bgColor)
        assertEquals(false, c.useRootHeightAndWidth)
        assertEquals(false, c.disableScroll)
    }

    @Test
    fun cropperDefaultsScaleBoundsAndQualityMatchUview() {
        val cr = UPCropperProps()
        assertEquals(true, cr.canScale)
        assertEquals(true, cr.canRotate)
        assertEquals(true, cr.noTab)
        assertEquals(false, cr.inner)
        assertEquals("300rpx", cr.areaWidth)
        assertEquals("260rpx", cr.exportWidth)
        assertEquals("transparent", cr.fillColor)
        // Empty min/max fall back to 0.3 / 4.
        assertEquals(0.3f to 4f, upCropperScaleBounds(UPRawValueBoundsInput(""), UPRawValueBoundsInput("")))
        assertEquals(0.5f to 3f, upCropperScaleBounds(UPRawValueBoundsInput("0.5"), UPRawValueBoundsInput("3")))
        // quality: parseInt(q) || 0.9.
        assertEquals(0.9f, upCropperQuality(""), 0.001f)
        assertEquals(0.9f, upCropperQuality("0.9"), 0.001f)
        assertEquals(80f, upCropperQuality("80"), 0.001f)
    }

    @Test
    fun shortVideoDefaultsMatchUview() {
        val sv = UPShortVideoProps()
        assertEquals(0, sv.currentTab)
        assertEquals(0, sv.currentVideo)
        assertEquals(4, sv.tabsList.size)
        assertTrue(sv.videoList.isEmpty())
        assertEquals("推荐", sv.tabsList[0].upStringKeyMapOrEmpty()["name"].upStringValueOrEmpty())
    }

    @Test
    fun novelReaderDefaultsThemeAndParagraphsMatchUview() {
        val nr = UPNovelReaderProps()
        assertEquals("scroll", nr.mode)
        assertEquals(true, nr.showBack)
        assertEquals("arrow-left", nr.backIcon)
        assertEquals(18, nr.defaultSettings["fontSize"].upIntOrDefault(0))
        assertEquals("day", nr.defaultSettings["theme"].upStringValueOrEmpty())
        // Theme palette table (day/night) mirrors theme-vars.scss.
        assertEquals(0xFFF7F8FA, upNovelReaderTheme("day").background)
        assertEquals(0xFF202124, upNovelReaderTheme("night").background)
        assertEquals(upNovelReaderTheme("day"), upNovelReaderTheme("unknown"))
        // Content normalizes across strings and lists, splitting on line breaks.
        assertEquals(listOf("a", "b", "c"), upNovelParagraphs("a\nb\nc"))
        assertEquals(listOf("x", "y", "z"), upNovelParagraphs(listOf("x", "y\nz")))
    }

    @Test
    fun actionSheetDataDefaultsAndLabelMatchUview() {
        val a = UPActionSheetDataProps()
        assertEquals("value", a.valueKey)
        assertEquals("name", a.labelKey)
        val options = listOf<UPRawValue>(
            mapOf<String, UPRawValue>("value" to 1, "name" to "北京"),
            mapOf<String, UPRawValue>("value" to 2, "name" to "上海"),
        )
        assertEquals("上海", upActionSheetDataLabel(options, 2, "value", "name"))
        assertEquals("北京", upActionSheetDataLabel(options, "1", "value", "name"))
        assertEquals("", upActionSheetDataLabel(options, "", "value", "name"))
        assertEquals("", upActionSheetDataLabel(options, 9, "value", "name"))
    }

    @Test
    fun pickerDataDefaultsAndSelectionMatchUview() {
        val pd = UPPickerDataProps()
        assertEquals("id", pd.valueKey)
        assertEquals("name", pd.labelKey)
        val options = listOf<UPRawValue>(
            mapOf<String, UPRawValue>("id" to 10, "name" to "语文"),
            mapOf<String, UPRawValue>("id" to 20, "name" to "数学"),
        )
        assertEquals("数学" to 1, upPickerDataSelection(options, 20, "id", "name"))
        assertEquals("语文" to 0, upPickerDataSelection(options, "10", "id", "name"))
        assertEquals("" to -1, upPickerDataSelection(options, "", "id", "name"))
        assertEquals("" to -1, upPickerDataSelection(options, 99, "id", "name"))
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
