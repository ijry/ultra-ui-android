package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class UPBatch10BehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun calendarSelectsAndConfirmsTheControlledDateShape() {
        val changes = mutableListOf<UPCalendarEvent>()
        val confirms = mutableListOf<UPCalendarEvent>()
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(show = true, defaultDate = "2026-08-20", mode = "single"),
                onChange = { changes += it },
                onConfirm = { confirms += it },
            )
        }

        composeRule.onNodeWithTag("up-calendar-day-2026-08-20").performClick()
        composeRule.onNodeWithTag("up-calendar-confirm").performClick()
        composeRule.runOnIdle {
            assertEquals("2026-08-20", changes.single().value)
            assertEquals("2026-08-20", confirms.single().value)
        }
    }

    @Test
    fun calendarShowsRangePromptWhenEndDateExceedsMaxRange() {
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(
                    show = true,
                    defaultDate = listOf("2026-08-20"),
                    mode = "range",
                    maxRange = 2,
                    rangePrompt = "最多相差两天",
                ),
            )
        }

        composeRule.onNodeWithTag("up-calendar-day-2026-08-23").performClick()
        composeRule.onNodeWithTag("up-calendar-prompt").assertTextEquals("最多相差两天")
    }

    @Test
    fun calendarShowsConfiguredToastWhenForbiddenDateIsTapped() {
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(
                    show = true,
                    defaultDate = "2026-08-20",
                    forbidDays = listOf("2026-08-21"),
                    forbidDaysToast = "该日期不可选",
                ),
            )
        }

        composeRule.onNodeWithTag("up-calendar-day-2026-08-21").performClick()
        composeRule.onNodeWithTag("up-calendar-prompt").assertTextEquals("该日期不可选")
    }

    @Test
    fun calendarTimePanelUsesDefaultTimeAndUpdatesSingleResult() {
        val confirms = mutableListOf<UPCalendarEvent>()
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(
                    show = true,
                    defaultDate = "2026-08-20",
                    enableTime = true,
                    timePrecision = "minute",
                    defaultTime = "07:08",
                ),
                onConfirm = { confirms += it },
            )
        }

        composeRule.onNodeWithTag("up-calendar-time-single-hour").assertTextEquals("07")
        composeRule.onNodeWithTag("up-calendar-time-single-hour-increase").performClick()
        composeRule.onNodeWithTag("up-calendar-time-single-hour").assertTextEquals("08")
        composeRule.onNodeWithTag("up-calendar-confirm").performClick()
        composeRule.runOnIdle {
            assertEquals("2026-08-20 08:08", confirms.single().value)
        }
    }

    @Test
    fun calendarRendersConfiguredMonthSequenceAndFormat() {
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(
                    show = true,
                    defaultDate = "2026-08-20",
                    monthNum = 3,
                    monthFormat = "YYYY/MM",
                ),
            )
        }

        composeRule.onNodeWithTag("up-calendar-month-2026-08").assertExists()
        composeRule.onNodeWithTag("up-calendar-month-2026-09").assertExists()
        composeRule.onNodeWithTag("up-calendar-month-2026-10").assertExists()
        composeRule.onNodeWithTag("up-calendar-month-title-2026-08").assertTextEquals("2026/08")
        composeRule.onNodeWithTag("up-calendar-month-title-2026-09").assertTextEquals("2026/09")
        composeRule.onNodeWithTag("up-calendar-month-title-2026-10").assertTextEquals("2026/10")
    }

    @Test
    fun calendarMonthSwitchShowsOneMonthAndNavigatesWithinConfiguredSequence() {
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(
                    show = true,
                    defaultDate = "2026-08-20",
                    monthNum = 3,
                    monthSwitch = true,
                ),
            )
        }

        composeRule.onNodeWithTag("up-calendar-month-2026-08").assertExists()
        composeRule.onNodeWithTag("up-calendar-month-2026-09").assertDoesNotExist()
        composeRule.onNodeWithTag("up-calendar-prev").performClick()
        composeRule.onNodeWithTag("up-calendar-month-2026-08").assertExists()
        composeRule.onNodeWithTag("up-calendar-next").performClick()
        composeRule.onNodeWithTag("up-calendar-month-2026-08").assertDoesNotExist()
        composeRule.onNodeWithTag("up-calendar-month-2026-09").assertExists()
        composeRule.onNodeWithTag("up-calendar-prev").performClick()
        composeRule.onNodeWithTag("up-calendar-month-2026-08").assertExists()
    }

    @Test
    fun calendarRejectsSameDayRangeWhenEndTimePrecedesStartTime() {
        val confirms = mutableListOf<UPCalendarEvent>()
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(
                    show = true,
                    mode = "range",
                    defaultDate = listOf("2026-08-20"),
                    allowSameDay = true,
                    enableTime = true,
                    rangeResultMode = "boundary",
                    defaultTime = "14:30",
                ),
                onConfirm = { confirms += it },
            )
        }

        composeRule.onNodeWithTag("up-calendar-day-2026-08-20").performClick()
        composeRule.onNodeWithTag("up-calendar-time-range-end-minute-decrease").performClick()
        composeRule.onNodeWithTag("up-calendar-confirm").performClick()
        composeRule.onNodeWithTag("up-calendar-prompt").assertTextEquals("结束时间不能早于开始时间")
        composeRule.runOnIdle { assertTrue(confirms.isEmpty()) }
    }

    @Test
    fun calendarRowHeightChangesDateCellHeight() {
        val rowHeight = mutableStateOf(56)
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(show = true, defaultDate = "2026-08-20", rowHeight = rowHeight.value),
            )
        }
        val defaultHeight = composeRule
            .onNodeWithTag("up-calendar-day-2026-08-20")
            .getUnclippedBoundsInRoot()
            .let { it.bottom - it.top }

        composeRule.runOnIdle {
            rowHeight.value = 84
        }
        composeRule.waitForIdle()
        val expandedHeight = composeRule
            .onNodeWithTag("up-calendar-day-2026-08-20")
            .getUnclippedBoundsInRoot()
            .let { it.bottom - it.top }

        assertTrue(expandedHeight > defaultHeight)
        composeRule.onNodeWithTag("up-calendar-day-2026-08-20").assertHeightIsEqualTo(52.dp)
    }

    @Test
    fun calendarCustomListRendersMetadataAndDisablesTheDate() {
        val changes = mutableListOf<UPCalendarEvent>()
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(
                    show = true,
                    defaultDate = "2026-08-20",
                    customList = listOf(
                        mapOf(
                            "date" to "2026-08-20",
                            "topInfo" to "假",
                            "bottomInfo" to "生日",
                            "dot" to true,
                            "disabled" to true,
                        ),
                    ),
                ),
                onChange = { changes += it },
            )
        }

        composeRule.onNodeWithTag("up-calendar-day-2026-08-20-top-info", useUnmergedTree = true).assertTextEquals("假")
        composeRule.onNodeWithTag("up-calendar-day-2026-08-20-bottom-info", useUnmergedTree = true).assertTextEquals("生日")
        composeRule.onNodeWithTag("up-calendar-day-2026-08-20-dot", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-calendar-day-2026-08-20-disabled", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-calendar-day-2026-08-20").performClick()
        composeRule.runOnIdle { assertTrue(changes.isEmpty()) }
    }

    @Test
    fun calendarShowsMonthMarkTodayHighlightAndLunarLabelWhenEnabled() {
        val showDecorations = mutableStateOf(true)
        val calendar = Calendar.getInstance()
        val today = String.format(
            Locale.ROOT,
            "%04d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH),
        )
        val todayMonth = today.substringBeforeLast('-')
        val todayMonthMark = (calendar.get(Calendar.MONTH) + 1).toString()
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(
                    show = true,
                    defaultDate = today,
                    showMark = showDecorations.value,
                    showToday = showDecorations.value,
                    todayColor = "#ff5500",
                    showLunar = showDecorations.value,
                ),
            )
        }

        composeRule.onNodeWithTag("up-calendar-month-$todayMonth-mark").assertTextEquals(todayMonthMark)
        composeRule.onNodeWithTag("up-calendar-today").assertTextEquals("今天")
        composeRule.onNodeWithTag("up-calendar-day-$today-today", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-calendar-day-$today-today-color-ff5500", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-calendar-day-$today-lunar", useUnmergedTree = true).assertExists()

        composeRule.runOnIdle {
            showDecorations.value = false
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-calendar-month-$todayMonth-mark").assertDoesNotExist()
        composeRule.onNodeWithTag("up-calendar-today").assertDoesNotExist()
        composeRule.onNodeWithTag("up-calendar-day-$today-lunar", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun calendarConvertsAFixedGregorianDateIntoItsLunarLabel() {
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(show = true, defaultDate = "2026-08-31", showLunar = true),
            )
        }

        composeRule.onNodeWithTag("up-calendar-day-2026-08-31-lunar", useUnmergedTree = true)
            .assertTextEquals("七月十九")
    }

    @Test
    fun calendarRendersRangeBoundaryLabelsAndMiddleState() {
        composeRule.setContent {
            UPCalendar(
                props = UPCalendarProps(
                    show = true,
                    mode = "range",
                    defaultDate = listOf("2026-08-20", "2026-08-23"),
                    startText = "入住",
                    endText = "离店",
                ),
            )
        }

        composeRule.onNodeWithTag("up-calendar-day-2026-08-20-bottom-info", useUnmergedTree = true).assertTextEquals("入住")
        composeRule.onNodeWithTag("up-calendar-day-2026-08-23-bottom-info", useUnmergedTree = true).assertTextEquals("离店")
        composeRule.onNodeWithTag("up-calendar-day-2026-08-21-range-middle", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-calendar-day-2026-08-22-range-middle", useUnmergedTree = true).assertExists()
    }

    @Test
    fun datetimePickerChangesAColumnAndConfirmsTimestamp() {
        val date = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 20, 14, 35, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val changes = mutableListOf<UPDatetimePickerEvent>()
        val confirms = mutableListOf<UPDatetimePickerEvent>()
        composeRule.setContent {
            UPDatetimePicker(
                props = UPDatetimePickerProps(show = true, value = date.timeInMillis),
                onChange = { changes += it },
                onConfirm = { confirms += it },
            )
        }

        // Columns are wheels showing visibleItemCount options at a time, so the target
        // minute has to be scrolled into view before it can be tapped.
        composeRule.onNodeWithTag("up-datetime-picker-option-4-36").performScrollTo().performClick()
        composeRule.onNodeWithTag("up-datetime-picker-confirm").performClick()
        composeRule.runOnIdle {
            assertEquals(36, changes.last().selectedValues[4])
            assertEquals(changes.last().value, confirms.single().value)
        }
    }

    @Test
    fun cascaderEmitsTheCompleteSelectedPath() {
        val data = listOf(
            mapOf(
                "value" to "zhejiang",
                "label" to "浙江",
                "children" to listOf(mapOf("value" to "hangzhou", "label" to "杭州")),
            ),
        )
        val changes = mutableListOf<UPCascaderEvent>()
        composeRule.setContent {
            UPCascader(UPCascaderProps(show = true, data = data), onChange = { changes += it })
        }

        composeRule.onNodeWithTag("up-cascader-option-0-0").performClick()
        composeRule.onNodeWithTag("up-cascader-option-1-0").performClick()
        composeRule.runOnIdle { assertEquals(listOf("zhejiang", "hangzhou"), changes.last().value) }
    }

    @Test
    fun sliderGestureEmitsAQuantizedValue() {
        val values = mutableListOf<UPRawValue>()
        composeRule.setContent {
            UPSlider(
                props = UPSliderProps(value = 0, min = 0, max = 100, step = 10),
                onChanging = { values += it.value },
                onChange = { values += it.value },
            )
        }

        composeRule.onNodeWithTag("up-slider").performClick()
        composeRule.runOnIdle {
            assertTrue(values.isNotEmpty())
            assertEquals(50f, (values.last() as Number).toFloat(), 10f)
        }
    }

    @Test
    fun tabbarItemsUseParentSelectionAndEmitTheirName() {
        val selected = mutableListOf<UPTabbarChangeEvent>()
        composeRule.setContent {
            UPTabbar(UPTabbarProps(value = "home"), onChange = { selected += it }) {
                UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home", text = "首页"))
                UPTabbarItem(UPTabbarItemProps(name = "user", icon = "account", text = "我的", badge = 2))
            }
        }

        composeRule.onNodeWithTag("up-tabbar-item-user").assertExists().performClick()
        composeRule.runOnIdle {
            assertEquals("user", selected.single().value)
            assertEquals(1, selected.single().index)
        }
    }
}
