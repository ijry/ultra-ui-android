package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPCompatibilityEvent
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

/**
 * `hasInput` mounts a readonly `u-input` trigger that opens the wheel on tap, and
 * `toolbarRightSlot` hands the confirm corner to the slot. Skipping either leaves the whole
 * input-driven picker flow — and its `inputProps`/`inputBorder` styling — unreachable.
 */
@RunWith(AndroidJUnit4::class)
class UPPickerInputBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val cities: List<UPRawValue> = listOf(
        listOf(mapOf("text" to "北京", "value" to "bj"), mapOf("text" to "上海", "value" to "sh")),
    )

    @Test
    fun hasInputKeepsTheTriggerMountedWhileTheWheelStaysClosed() {
        composeRule.setContent {
            UPPicker(UPPickerProps(hasInput = true, columns = cities, modelValue = listOf("sh")))
        }

        composeRule.onNodeWithTag("up-picker-input").assertIsDisplayed()
        // The confirmed value, resolved back to its `keyName` label, is what uview shows.
        composeRule.onNodeWithText("上海").assertExists()
        composeRule.onNodeWithTag("up-picker").assertDoesNotExist()
    }

    @Test
    fun tappingTheTriggerCoverOpensAndClosesTheWheel() {
        composeRule.setContent {
            UPPicker(UPPickerProps(hasInput = true, columns = cities))
        }

        composeRule.onNodeWithTag("up-picker-input-cover").performClick()
        composeRule.onNodeWithTag("up-picker").assertExists()
        composeRule.onNodeWithTag("up-picker-cancel").performClick()
        composeRule.onNodeWithTag("up-picker").assertDoesNotExist()
    }

    @Test
    fun aDisabledTriggerNeverOpensTheWheel() {
        composeRule.setContent {
            UPPicker(UPPickerProps(hasInput = true, disabled = true, columns = cities))
        }

        composeRule.onNodeWithTag("up-picker-input-cover").performClick()
        composeRule.onNodeWithTag("up-picker").assertDoesNotExist()
    }

    @Test
    fun unsupportedInputPropsOverridesAreReported() {
        val events = mutableListOf<UPCompatibilityEvent>()
        val diagnostics = UPCompatibilityDiagnostics { events += it }
        composeRule.setContent {
            UPPicker(
                UPPickerProps(hasInput = true, columns = cities, inputProps = mapOf("placeholder" to "选择城市", "focus" to true)),
                diagnostics = diagnostics,
            )
        }

        composeRule.onNodeWithText("选择城市").assertExists()
        composeRule.runOnIdle { assertEquals(listOf("inputProps.focus"), events.map { it.property }) }
    }

    @Test
    fun toolbarRightSlotTakesOverTheConfirmCorner() {
        composeRule.setContent {
            UPPicker(
                props = UPPickerProps(show = true, toolbarRightSlot = true, columns = cities),
                toolbarRight = { BasicText("重置") },
            )
        }

        composeRule.onNodeWithTag("up-picker-toolbar-right").assertExists()
        composeRule.onNodeWithText("重置").assertExists()
        composeRule.onNodeWithTag("up-picker-confirm").assertDoesNotExist()
    }

    @Test
    fun maskStylePaintsAnOverlayOverEveryWheelColumn() {
        composeRule.setContent {
            UPPicker(UPPickerProps(show = true, columns = cities, maskStyle = mapOf("backgroundColor" to "#00000033")))
        }

        composeRule.onNodeWithTag("up-picker-mask-0").assertExists()
    }

    @Test
    fun anAbsentMaskStyleLeavesTheWheelUncovered() {
        composeRule.setContent { UPPicker(UPPickerProps(show = true, columns = cities)) }

        composeRule.onNodeWithTag("up-picker-mask-0").assertDoesNotExist()
    }

    @Test
    fun maskClassAndWindowLevelPopupModesAreReportedAsDowngrades() {
        val events = mutableListOf<UPCompatibilityEvent>()
        val diagnostics = UPCompatibilityDiagnostics { events += it }
        composeRule.setContent {
            UPPicker(
                UPPickerProps(show = true, columns = cities, maskClass = "my-mask", popupMode = "center"),
                diagnostics = diagnostics,
            )
        }

        composeRule.runOnIdle {
            assertEquals(setOf("maskClass", "popupMode"), events.map { it.property }.toSet())
            assertTrue(events.all { it.component == "UPPicker" })
        }
    }

    @Test
    fun datetimeTriggerShowsTheConfirmedValueInTheModeFormat() {
        val date = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.AUGUST, 20, 14, 35, 0)
        }
        composeRule.setContent {
            UPDatetimePicker(UPDatetimePickerProps(hasInput = true, mode = "date", value = date.timeInMillis))
        }

        composeRule.onNodeWithTag("up-datetime-picker-input").assertIsDisplayed()
        composeRule.onNodeWithText("2026-08-20").assertExists()
        composeRule.onNodeWithTag("up-datetime-picker").assertDoesNotExist()
    }

    @Test
    fun tappingTheDatetimeTriggerOpensTheWheel() {
        composeRule.setContent {
            UPDatetimePicker(UPDatetimePickerProps(hasInput = true, mode = "time"))
        }

        composeRule.onNodeWithTag("up-datetime-picker-input-cover").performClick()
        composeRule.onNodeWithTag("up-datetime-picker-column-0").assertExists()
        composeRule.onNodeWithTag("up-datetime-picker-cancel").performClick()
        composeRule.onNodeWithTag("up-datetime-picker").assertDoesNotExist()
    }

    @Test
    fun datetimeToolbarRightSlotAndMaskStyleFollowThePicker() {
        composeRule.setContent {
            UPDatetimePicker(
                props = UPDatetimePickerProps(show = true, mode = "time", toolbarRightSlot = true, maskStyle = "background-color: rgba(0, 0, 0, 0.2)"),
                toolbarRight = { BasicText("清空") },
            )
        }

        composeRule.onNodeWithTag("up-datetime-picker-toolbar-right").assertExists()
        composeRule.onNodeWithText("清空").assertExists()
        composeRule.onNodeWithTag("up-datetime-picker-confirm").assertDoesNotExist()
        composeRule.onNodeWithTag("up-datetime-picker-mask-0").assertExists()
    }

    @Test
    fun datetimeMaskClassAndWindowLevelPopupModesAreReportedAsDowngrades() {
        val events = mutableListOf<UPCompatibilityEvent>()
        val diagnostics = UPCompatibilityDiagnostics { events += it }
        composeRule.setContent {
            UPDatetimePicker(
                UPDatetimePickerProps(show = true, mode = "time", maskClass = "my-mask", popupMode = "right"),
                diagnostics = diagnostics,
            )
        }

        composeRule.runOnIdle {
            assertEquals(setOf("maskClass", "popupMode"), events.map { it.property }.toSet())
            assertTrue(events.all { it.component == "UPDatetimePicker" })
        }
    }
}
