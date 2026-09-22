package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-calendar-strip`: day selection emits the date, and month navigation shifts the header.
 */
@RunWith(AndroidJUnit4::class)
class UPCalendarStripBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun tappingADaySelectsItAndEmitsTheDate() {
        val changes = mutableListOf<String>()
        composeRule.setContent {
            UPCalendarStrip(
                UPCalendarStripProps(modelValue = "2026-02-10"),
                onUpdateModelValue = { changes += it },
            )
        }

        composeRule.onNodeWithTag("up-calendar-strip-title").assertExists()
        composeRule.onNodeWithTag("up-calendar-strip-day-15").performClick()
        composeRule.runOnIdle { assertEquals(listOf("2026-02-15"), changes) }
    }

    @Test
    fun readonlyBlocksSelection() {
        val changes = mutableListOf<String>()
        composeRule.setContent {
            UPCalendarStrip(
                UPCalendarStripProps(modelValue = "2026-02-10", readonly = true),
                onUpdateModelValue = { changes += it },
            )
        }

        composeRule.onNodeWithTag("up-calendar-strip-day-15").performClick()
        composeRule.runOnIdle { assertEquals(emptyList<String>(), changes) }
    }

    @Test
    fun nextMonthShiftsAndEmitsMonthChange() {
        val months = mutableListOf<String>()
        composeRule.setContent {
            UPCalendarStrip(
                UPCalendarStripProps(modelValue = "2026-02-10"),
                onMonthChange = { months += it },
            )
        }

        composeRule.onNodeWithTag("up-calendar-strip-next").performClick()
        composeRule.runOnIdle { assertEquals(listOf("2026-03"), months) }
    }
}
