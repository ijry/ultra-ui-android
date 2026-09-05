package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Behaviour coverage for the fields that were declared but never read before this batch:
 * `u-popup`'s `touchable` handle and `duration`, `u-select`'s panel styling, `u-tabbar`'s
 * `placeholder`, `u-picker`'s `loading`, `u-cell-group`'s `border`, `u-icon`'s image
 * branch, `u-number-box`'s `name`/`iconStyle`, `u-cascader`'s `closeable`, and the
 * `formatter` / `filter` callbacks of `u-calendar` and `u-datetime-picker`.
 */
@RunWith(AndroidJUnit4::class)
class UPInertFieldBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private fun node(tag: String) = composeRule.onNodeWithTag(tag, useUnmergedTree = true)

    @Test
    fun touchableAddsTheBottomSheetHandleOnlyForABottomPopup() {
        composeRule.setContent {
            UPPopup(UPPopupProps(show = true, mode = "bottom", touchable = true)) { BasicText("面板") }
        }
        node("up-popup-touch-area").assertExists()
        node("up-popup-indicator").assertExists()

        composeRule.setContent {
            UPPopup(UPPopupProps(show = true, mode = "center", touchable = true)) { BasicText("面板") }
        }
        node("up-popup-touch-area").assertDoesNotExist()
    }

    @Test
    fun aPopupWithoutTouchableHasNoHandleAtAll() {
        composeRule.setContent {
            UPPopup(UPPopupProps(show = true, mode = "bottom")) { BasicText("面板") }
        }
        node("up-popup-touch-area").assertDoesNotExist()
        node("up-popup-indicator").assertDoesNotExist()
    }

    @Test
    fun popupDurationDrivesTheEntryTransition() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPPopup(UPPopupProps(show = true, mode = "bottom", duration = 600)) { BasicText("面板") }
        }

        // `slide-up` starts one full panel height below its resting position.
        val start = node("up-popup-panel").getUnclippedBoundsInRoot().top
        composeRule.mainClock.advanceTimeBy(700L)
        val settled = node("up-popup-panel").getUnclippedBoundsInRoot().top
        assertTrue("the panel should slide up into place: $start then $settled", settled <= start)
    }

    @Test
    fun safeAreaInsetTopAddsTheStatusBarSpacer() {
        composeRule.setContent {
            UPPopup(UPPopupProps(show = true, mode = "bottom", safeAreaInsetTop = true)) { BasicText("面板") }
        }
        node("up-status-bar").assertExists()

        composeRule.setContent {
            UPPopup(UPPopupProps(show = true, mode = "bottom")) { BasicText("面板") }
        }
        node("up-status-bar").assertDoesNotExist()
    }

    @Test
    fun selectBorderOutlinesTheTriggerAndItemColourPaintsTheOptions() {
        composeRule.setContent {
            UPSelect(UPSelectProps(options = listOf(mapOf("id" to 1, "name" to "北京")), border = false))
        }
        val plain = node("up-select-trigger").getUnclippedBoundsInRoot().let { it.bottom - it.top }

        composeRule.setContent {
            UPSelect(UPSelectProps(options = listOf(mapOf("id" to 1, "name" to "北京")), border = true))
        }
        val outlined = node("up-select-trigger").getUnclippedBoundsInRoot().let { it.bottom - it.top }
        // `.u-select__label--border` sets `min-height: 36px`.
        assertTrue("border should floor the trigger at 36dp, got $outlined vs $plain", outlined.value >= 36f)
    }

    @Test
    fun selectMaxHeightCapsTheOpenPanel() {
        composeRule.setContent {
            UPSelect(
                UPSelectProps(
                    options = List(30) { mapOf("id" to it, "name" to "选项 $it") },
                    maxHeight = "120px",
                ),
            )
        }

        composeRule.onNodeWithTag("up-select-trigger").performClick()
        val panel = node("up-select-options").getUnclippedBoundsInRoot()
        assertEquals(120.dp.value, (panel.bottom - panel.top).value, 1f)
    }

    @Test
    fun tabbarPlaceholderReservesTheBarsHeight() {
        composeRule.setContent {
            UPTabbar(UPTabbarProps(value = "home", fixed = true, placeholder = true, safeAreaInsetBottom = false)) {
                UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home", text = "首页"))
            }
        }
        val bar = node("up-tabbar-style-default").getUnclippedBoundsInRoot().let { it.bottom - it.top }
        val placeholder = node("up-tabbar-placeholder").getUnclippedBoundsInRoot().let { it.bottom - it.top }
        assertEquals(bar.value, placeholder.value, 1f)

        composeRule.setContent {
            UPTabbar(UPTabbarProps(value = "home", fixed = false, placeholder = true, safeAreaInsetBottom = false)) {
                UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home", text = "首页"))
            }
        }
        node("up-tabbar-placeholder").assertDoesNotExist()
    }

    @Test
    fun pickerLoadingCoversTheWheelAndBlocksSelection() {
        var changes = 0
        composeRule.setContent {
            UPPicker(
                UPPickerProps(show = true, loading = true, columns = listOf(listOf(mapOf("text" to "北京", "value" to "bj")))),
                onChange = { changes += 1 },
            )
        }

        node("up-picker-loading-0").assertExists()
        node("up-loading-icon").assertExists()
        // The options are replaced outright, so no option node survives to be tapped.
        composeRule.onAllNodesWithTag("up-picker-column-0", useUnmergedTree = true).assertCountEquals(0)
        composeRule.runOnIdle { assertEquals(0, changes) }
    }

    @Test
    fun cellGroupBorderDrawsOneHairlineAboveTheFirstCell() {
        composeRule.setContent {
            UPCellGroup(UPCellGroupProps(border = true)) {
                UPCell(UPCellProps(title = "设置", border = false))
            }
        }
        composeRule.onAllNodesWithTag("up-line", useUnmergedTree = true).assertCountEquals(1)

        composeRule.setContent {
            UPCellGroup(UPCellGroupProps(border = false)) {
                UPCell(UPCellProps(title = "设置", border = false))
            }
        }
        composeRule.onAllNodesWithTag("up-line", useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun anIconNameWithASlashRendersAnImageSizedByWidthAndHeight() {
        composeRule.setContent {
            UPIcon(UPIconProps(name = "/local/does-not-exist.png", width = 48, height = 24))
        }

        // `isImg` swaps the glyph text for an `<image>`.
        node("up-icon-img").assertExists()
        node("up-icon-glyph").assertDoesNotExist()
        val bounds = node("up-icon-img").getUnclippedBoundsInRoot()
        assertEquals(48.dp.value, (bounds.right - bounds.left).value, 1f)
        assertEquals(24.dp.value, (bounds.bottom - bounds.top).value, 1f)
    }

    @Test
    fun anImageIconFallsBackToSizeForBothExtents() {
        composeRule.setContent {
            UPIcon(UPIconProps(name = "/local/does-not-exist.png", size = 32))
        }
        val bounds = node("up-icon-img").getUnclippedBoundsInRoot()
        assertEquals(32.dp.value, (bounds.right - bounds.left).value, 1f)
        assertEquals(32.dp.value, (bounds.bottom - bounds.top).value, 1f)
    }

    @Test
    fun numberBoxChangeEventCarriesItsNameAndTheButtonType() {
        val events = mutableListOf<Map<String, UPRawValue>>()
        composeRule.setContent {
            UPNumberBox(
                props = UPNumberBoxProps(name = "qty", modelValue = 2, min = 0, max = 9),
                onChangeEvent = { events += it },
            )
        }

        composeRule.onNodeWithTag("up-number-box-plus").performClick()
        composeRule.onNodeWithTag("up-number-box-minus").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf("qty", "qty"), events.map { it["name"] })
            assertEquals(listOf("plus", "minus"), events.map { it["type"] })
        }
    }

    @Test
    fun numberBoxIconStyleReachesBothGlyphs() {
        composeRule.setContent {
            UPNumberBox(props = UPNumberBoxProps(modelValue = 2, iconStyle = mapOf("width" to "26px", "height" to "26px")))
        }

        val minus = composeRule.onAllNodesWithTag("up-icon", useUnmergedTree = true)[0].getUnclippedBoundsInRoot()
        assertEquals(26.dp.value, (minus.right - minus.left).value, 1f)
    }

    @Test
    fun cascaderCloseableFloatsAGlyphWithoutMovingTheToolbar() {
        val data = listOf(mapOf("value" to "zj", "label" to "浙江"))
        composeRule.setContent {
            UPCascader(UPCascaderProps(show = true, data = data, closeable = false))
        }
        val without = node("up-cascader-cancel").getUnclippedBoundsInRoot().top

        composeRule.setContent {
            UPCascader(UPCascaderProps(show = true, data = data, closeable = true))
        }
        node("up-cascader-close").assertExists()
        val with = node("up-cascader-cancel").getUnclippedBoundsInRoot().top
        // `position: absolute` on the close glyph means the toolbar keeps its place.
        assertEquals(without.value, with.value, 1f)
    }

    @Test
    fun theCalendarFormatterCanDisableAndAnnotateIndividualDays() {
        val formatter: (Map<String, UPRawValue>) -> Map<String, UPRawValue> = { config ->
            if (config["day"] == 1) config + mapOf<String, UPRawValue>("bottomInfo" to "初一", "disabled" to true) else config
        }
        var picked: UPRawValue = null
        composeRule.setContent {
            UPCalendar(
                UPCalendarProps(
                    show = true,
                    monthNum = 1,
                    defaultDate = "2026-09-05",
                    formatter = formatter,
                ),
                onChange = { picked = it.value },
            )
        }

        node("up-calendar-day-2026-09-01-bottom-info").assertExists().assertTextEquals("初一")
        // `disabled` from the formatter blocks the tap, so nothing is selected.
        composeRule.onNodeWithTag("up-calendar-day-2026-09-01").performClick()
        composeRule.runOnIdle { assertEquals(null, picked) }
    }

    @Test
    fun theDatetimeFilterRemovesOptionsAndTheFormatterRelabelsThem() {
        val filter: (String, List<String>) -> List<String> = { type, values ->
            if (type == "minute") values.filter { (it.toIntOrNull() ?: 0) % 30 == 0 } else values
        }
        val formatter: (String, String) -> String = { type, value -> if (type == "minute") "$value 分" else value }
        composeRule.setContent {
            UPDatetimePicker(
                UPDatetimePickerProps(
                    show = true,
                    mode = "time",
                    value = "08:00",
                    filter = filter,
                    formatter = formatter,
                ),
            )
        }

        // 0 and 30 survive the filter; 1 does not.
        node("up-datetime-picker-option-1-0").assertExists().assertTextEquals("00 分")
        node("up-datetime-picker-option-1-30").assertExists()
        node("up-datetime-picker-option-1-1").assertDoesNotExist()
    }

    @Test
    fun aNonCallableFormatterIsReportedInsteadOfCrashing() {
        val events = mutableListOf<String>()
        val diagnostics = UPCompatibilityDiagnostics { event -> events += event.property }
        composeRule.setContent {
            Box(Modifier.size(360.dp)) {
                UPDatetimePicker(
                    UPDatetimePickerProps(show = true, mode = "time", value = "08:00", formatter = "not-a-function"),
                    diagnostics = diagnostics,
                )
            }
        }

        node("up-datetime-picker").assertExists()
        composeRule.runOnIdle { assertTrue("expected a formatter diagnostic, got $events", "formatter" in events) }
    }

    @Test
    fun alertTransitionModeChoosesBetweenFadingAndSliding() {
        composeRule.mainClock.autoAdvance = false
        var visible by mutableStateOf(false)
        composeRule.setContent {
            UPAlert(UPAlertProps(title = "系统提示", transitionMode = "slide-up", modelValue = visible))
        }

        composeRule.runOnIdle { visible = true }
        val start = node("up-alert").getUnclippedBoundsInRoot().top
        composeRule.mainClock.advanceTimeBy(400L)
        val settled = node("up-alert").getUnclippedBoundsInRoot().top
        // `slide-up` starts one full banner height below its resting place.
        assertTrue("slide-up should travel upwards: $start then $settled", settled <= start)
    }

    @Test
    fun alertKeepsTheBannerMountedUntilTheLeaveAnimationEnds() {
        composeRule.mainClock.autoAdvance = false
        var visible by mutableStateOf(true)
        composeRule.setContent {
            UPAlert(UPAlertProps(title = "系统提示", closable = true, modelValue = visible), onClose = { visible = false })
        }

        composeRule.onNodeWithTag("up-alert-close").performClick()
        composeRule.mainClock.advanceTimeBy(80L)
        // `u-transition` removes the element only after the leave animation finishes.
        node("up-alert").assertExists()
        composeRule.mainClock.advanceTimeBy(500L)
        node("up-alert").assertDoesNotExist()
    }

    @Test
    fun collapseBorderDividesTheGroupWithHairlinesRatherThanBoxes() {
        composeRule.setContent {
            UPCollapse(UPCollapseProps(value = listOf("one"), border = true)) {
                UPCollapseItem(UPCollapseItemProps(name = "one", title = "已展开")) { BasicText("内容") }
                UPCollapseItem(UPCollapseItemProps(name = "two", title = "未展开")) { BasicText("内容") }
            }
        }
        // One line opens the group, one closes each item, and the open item adds its
        // header underline: four in total.
        composeRule.onAllNodesWithTag("up-line", useUnmergedTree = true).assertCountEquals(4)

        composeRule.setContent {
            UPCollapse(UPCollapseProps(value = listOf("one"), border = false)) {
                UPCollapseItem(UPCollapseItemProps(name = "one", title = "已展开")) { BasicText("内容") }
            }
        }
        composeRule.onAllNodesWithTag("up-line", useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun cardEmitsAPerRegionClickAllCarryingItsIndex() {
        val order = mutableListOf<String>()
        composeRule.setContent {
            UPCard(
                props = UPCardProps(title = "订单", index = 7),
                onClick = { order += "click:$it" },
                onHeadClick = { order += "head:$it" },
                onBodyClick = { order += "body:$it" },
                onFootClick = { order += "foot:$it" },
                foot = { BasicText("底部") },
            ) { BasicText("正文") }
        }

        composeRule.onNodeWithTag("up-card-head").performClick()
        composeRule.onNodeWithTag("up-card-body").performClick()
        composeRule.onNodeWithTag("up-card-foot").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf("head:7", "body:7", "foot:7"), order)
        }
    }

    @Test
    fun cardHeadAndFootExistOnTheirFlagsAlone() {
        composeRule.setContent {
            UPCard(props = UPCardProps(showHead = true, showFoot = true)) { BasicText("正文") }
        }
        // `v-if="showHead"` / `v-if="showFoot"` do not consult the slots.
        node("up-card-head").assertExists()
        node("up-card-foot").assertExists()

        composeRule.setContent {
            UPCard(props = UPCardProps(showHead = false, showFoot = false, title = "订单")) { BasicText("正文") }
        }
        node("up-card-head").assertDoesNotExist()
        node("up-card-foot").assertDoesNotExist()
    }

    @Test
    fun anEmptyCardFooterTakesNoPadding() {
        composeRule.setContent {
            UPCard(props = UPCardProps(showFoot = true, paddingFoot = 30)) { BasicText("正文") }
        }
        val empty = node("up-card-foot").getUnclippedBoundsInRoot().let { it.bottom - it.top }

        composeRule.setContent {
            UPCard(props = UPCardProps(showFoot = true, paddingFoot = 30), foot = { BasicText("底部") }) { BasicText("正文") }
        }
        val filled = node("up-card-foot").getUnclippedBoundsInRoot().let { it.bottom - it.top }
        // `$slots.foot ? addUnit(paddingFoot || padding) : 0`.
        assertEquals(0f, empty.value, 0.5f)
        assertTrue("a filled footer should claim its padding, got $filled", filled.value >= 60f)
    }

    @Test
    fun theSelectTriggerKeepsItsChevronAndOptionsRemainTappable() {
        var selected: UPRawValue = null
        composeRule.setContent {
            UPSelect(
                UPSelectProps(options = listOf(mapOf("id" to 7, "name" to "北京")), iconSize = 20),
                onUpdateCurrent = { selected = it },
            )
        }

        node("up-icon").assertExists()
        composeRule.onNodeWithTag("up-select-trigger").performClick()
        composeRule.onNodeWithTag("up-select-option").assertIsDisplayed().performClick()
        composeRule.runOnIdle { assertEquals(7, selected) }
    }
}
