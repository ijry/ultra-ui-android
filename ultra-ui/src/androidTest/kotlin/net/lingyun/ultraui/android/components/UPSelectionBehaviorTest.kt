package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPSelectionBehaviorTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun switchEmitsConfiguredRawActiveValueAndAsyncChangeKeepsVisualStateControlled() {
        var value by mutableStateOf("off")
        val inputs = mutableListOf<Any?>()
        val changes = mutableListOf<Any?>()
        composeRule.setContent {
            UPSwitch(
                props = UPSwitchProps(
                    modelValue = value,
                    activeValue = "on",
                    inactiveValue = "off",
                    asyncChange = true,
                ),
                onInput = { inputs += it },
                onChange = { changes += it },
            )
        }

        composeRule.onNodeWithTag("up-switch").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf("on"), inputs)
            assertEquals(listOf("on"), changes)
            assertEquals("off", value)
        }

        value = "on"
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-switch-dot").assertIsDisplayed()
    }

    @Test
    fun rateHonorsMinimumAndDisabledState() {
        var value by mutableStateOf(1f)
        var disabled by mutableStateOf(false)
        val emitted = mutableListOf<Float>()
        composeRule.setContent {
            UPRate(
                props = UPRateProps(
                    modelValue = value,
                    count = 5,
                    minCount = 2,
                    disabled = disabled,
                ),
                onInput = { emitted += it },
                onChange = { emitted += it },
            )
        }

        composeRule.onNodeWithTag("up-rate-item-0").performClick()
        composeRule.runOnIdle { assertEquals(listOf(2f, 2f), emitted) }

        emitted.clear()
        disabled = true
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-rate-item-4").performClick()
        composeRule.runOnIdle { assertTrue(emitted.isEmpty()) }
    }

    @Test
    fun numberBoxClampsAndReportsPlusMinusOverlimit() {
        var value by mutableStateOf<Any?>(2)
        val input = mutableListOf<Any?>()
        val changes = mutableListOf<Any?>()
        var overlimit = 0
        var plus = 0
        var minus = 0
        composeRule.setContent {
            UPNumberBox(
                props = UPNumberBoxProps(modelValue = value, min = 1, max = 3),
                onInput = { input += it },
                onChange = { changes += it },
                onOverlimit = { overlimit += 1 },
                onPlus = { plus += 1 },
                onMinus = { minus += 1 },
            )
        }

        composeRule.onNodeWithTag("up-number-box-plus").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(3), input)
            assertEquals(listOf(3), changes)
            assertEquals(1, plus)
        }

        composeRule.onNodeWithTag("up-number-box-plus").performClick()
        composeRule.onNodeWithTag("up-number-box-minus").performClick()
        composeRule.runOnIdle {
            assertEquals(1, overlimit)
            assertEquals(1, minus)
        }
    }

    @Test
    fun checkboxGroupEmitsMultiSelectionAndLabelDisabledSuppressesLabelClick() {
        var selected by mutableStateOf<List<Any?>>(emptyList())
        val events = mutableListOf<List<Any?>>()
        composeRule.setContent {
            UPCheckboxGroup(
                props = UPCheckboxGroupProps(modelValue = selected),
                onInput = { events += it },
                onChange = { events += it },
            ) {
                UPCheckbox(UPCheckboxProps(name = "a", label = "A"))
                UPCheckbox(UPCheckboxProps(name = "b", label = "B", labelDisabled = true))
            }
        }

        composeRule.onAllNodesWithTag("up-checkbox")[0].performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(listOf("a"), listOf("a")), events)
        }
        composeRule.onNodeWithTag("up-checkbox-label-b").performClick()
        composeRule.runOnIdle { assertEquals(2, events.size) }

        selected = listOf("a")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-checkbox-mark-a").assertTextContains("✓")
    }

    @Test
    fun radioGroupAllowsOnlyOneCheckedValueAndRespectsGap() {
        var selected by mutableStateOf<Any?>("")
        val events = mutableListOf<Any?>()
        composeRule.setContent {
            UPRadioGroup(
                props = UPRadioGroupProps(modelValue = selected, placement = "column", gap = "10px"),
                onInput = { events += it },
                onChange = { events += it },
            ) {
                UPRadio(UPRadioProps(name = "a", label = "A"))
                UPRadio(UPRadioProps(name = "b", label = "B"))
            }
        }

        composeRule.onNodeWithTag("up-radio-label-a").performClick()
        composeRule.onNodeWithTag("up-radio-label-b").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf("a", "a", "b", "b"), events)
            assertEquals("", selected)
        }

        selected = "b"
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-radio-mark-b").assertTextContains("✓")
    }

    @Test
    fun numberBoxInputAcceptsTextAndRendersControlledValue() {
        var value by mutableStateOf<Any?>(1)
        val values = mutableListOf<Any?>()
        composeRule.setContent {
            UPNumberBox(
                props = UPNumberBoxProps(modelValue = value, min = 0, max = 99),
                onInput = { values += it },
            )
        }
        composeRule.onNodeWithTag("up-number-box-field").performTextInput("12")
        composeRule.runOnIdle { assertTrue(values.isNotEmpty()) }
    }
}
