package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
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
import java.util.Calendar

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
