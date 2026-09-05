package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `<picker-view>` is a snapping wheel: the selection lives in an indicator band of exactly
 * `itemHeight` in the middle, scrolling settles with one option centred there, and the
 * `change` event comes from that scroll rather than from a tap. A flat scrolling column has
 * none of those properties, which is what this covers.
 */
@RunWith(AndroidJUnit4::class)
class UPPickerWheelBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val cities: List<UPRawValue> = listOf(listOf("北京", "上海", "广州", "深圳", "杭州", "成都"))

    @Test
    fun theWheelDrawsAnIndicatorBandOfExactlyOneItemHeight() {
        composeRule.setContent {
            UPPicker(UPPickerProps(show = true, columns = cities, itemHeight = 44, visibleItemCount = 5))
        }

        val band = composeRule.onNodeWithTag("up-picker-indicator-0", useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot
        assertEquals(44.dp.value, (band.bottom - band.top) / composeRule.density.density, 1f)

        // `indicatorStyle` marks the middle of the `visibleItemCount * itemHeight` wheel.
        val wheel = composeRule.onNodeWithTag("up-picker-column-0", useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot
        val bandCentre = (band.top + band.bottom) / 2f
        val wheelCentre = (wheel.top + wheel.bottom) / 2f
        assertTrue("the band should be centred in the wheel: $bandCentre vs $wheelCentre", Math.abs(bandCentre - wheelCentre) < 4f)
    }

    @Test
    fun theFirstOptionStartsInTheIndicatorBand() {
        composeRule.setContent {
            UPPicker(UPPickerProps(show = true, columns = cities, itemHeight = 44, visibleItemCount = 5))
        }

        // The half-viewport padding is what lets the first option reach the centre at all;
        // without it the wheel would start with option 0 at the very top.
        val band = composeRule.onNodeWithTag("up-picker-indicator-0", useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot
        val first = composeRule.onNodeWithTag("up-picker-option-0-0", useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot
        assertTrue("option 0 should start in the band: $first vs $band", Math.abs(first.top - band.top) < 4f)
    }

    @Test
    fun scrollingTheWheelMovesTheSelectionAndReportsIt() {
        val changes = mutableListOf<UPPickerEvent>()
        composeRule.setContent {
            UPPicker(
                UPPickerProps(show = true, columns = cities, itemHeight = 44, visibleItemCount = 5),
                onChange = { changes += it },
            )
        }

        composeRule.onNodeWithTag("up-picker-column-0").performTouchInput { swipeUp() }
        composeRule.waitForIdle()

        // The wheel drives the selection, so a settled scroll is a `change` — upstream's
        // `picker-view` reports on scroll, not on tap.
        composeRule.runOnIdle {
            assertTrue("scrolling should report a change, got $changes", changes.isNotEmpty())
            val last = changes.last()
            assertEquals(0, last.columnIndex)
            assertTrue("the selection should have moved off option 0, got ${last.indexs}", last.indexs.first() > 0)
        }
    }

    @Test
    fun aDisabledOptionCannotBeSelected() {
        val columns: List<UPRawValue> = listOf(
            listOf(
                mapOf("text" to "北京"),
                mapOf("text" to "上海", "disabled" to true),
            ),
        )
        val changes = mutableListOf<UPPickerEvent>()
        composeRule.setContent {
            UPPicker(UPPickerProps(show = true, columns = columns), onChange = { changes += it })
        }

        composeRule.onNodeWithTag("up-picker-option-0-1", useUnmergedTree = true).performClick()
        // `.u-picker__view__column__item--disabled` is `cursor: not-allowed` plus 0.35 alpha,
        // and the option is never selectable.
        composeRule.runOnIdle { assertTrue("a disabled option must not report, got $changes", changes.isEmpty()) }
    }

    @Test
    fun immediateChangeFalseKeepsTheWheelSilentUntilConfirm() {
        val changes = mutableListOf<UPPickerEvent>()
        val confirms = mutableListOf<UPPickerEvent>()
        composeRule.setContent {
            UPPicker(
                UPPickerProps(show = true, columns = cities, immediateChange = false),
                onChange = { changes += it },
                onConfirm = { confirms += it },
            )
        }

        composeRule.onNodeWithTag("up-picker-column-0").performTouchInput { swipeUp() }
        composeRule.waitForIdle()
        composeRule.runOnIdle { assertTrue("immediateChange=false should stay silent, got $changes", changes.isEmpty()) }

        composeRule.onNodeWithTag("up-picker-confirm").performClick()
        composeRule.runOnIdle { assertTrue("confirm should still report, got $confirms", confirms.isNotEmpty()) }
    }
}
