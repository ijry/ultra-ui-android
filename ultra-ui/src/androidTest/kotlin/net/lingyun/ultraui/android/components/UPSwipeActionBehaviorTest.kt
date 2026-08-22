package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-swipe-action-item` hides its action buttons until the row is dragged past
 * `threshold`; `show` reflects the open state rather than "render the buttons". An
 * implementation that always draws them has no swipe at all, and `threshold`,
 * `duration`, `closeOnClick` and the open-state callbacks are silent no-ops.
 */
@RunWith(AndroidJUnit4::class)
class UPSwipeActionBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val options: List<UPRawValue> = listOf(mapOf("text" to "删除"))

    @Test
    fun actionsStayHiddenUntilTheRowIsSwiped() {
        composeRule.setContent {
            UPSwipeAction {
                UPSwipeActionItem(UPSwipeActionItemProps(options = options)) {
                    UPGap(UPGapProps(height = 48))
                }
            }
        }
        // Closed by default (`show: false`), so nothing actionable is on screen.
        composeRule.onNodeWithTag("up-swipe-action-option-0").assertDoesNotExist()
    }

    @Test
    fun swipingLeftOpensTheActions() {
        composeRule.setContent {
            UPSwipeAction {
                UPSwipeActionItem(
                    UPSwipeActionItemProps(options = options),
                    modifier = Modifier.height(48.dp),
                ) { UPGap(UPGapProps(height = 48)) }
            }
        }
        composeRule.onNodeWithTag("up-swipe-action-item").performTouchInput { swipeLeft() }
        composeRule.onNodeWithTag("up-swipe-action-option-0").assertExists()
    }

    @Test
    fun showTrueOpensTheRowWithoutAGesture() {
        composeRule.setContent {
            UPSwipeAction {
                UPSwipeActionItem(UPSwipeActionItemProps(show = true, options = options)) {
                    UPGap(UPGapProps(height = 48))
                }
            }
        }
        composeRule.onNodeWithTag("up-swipe-action-option-0").assertExists()
    }

    @Test
    fun clickingAnActionReportsItAndClosesTheRow() {
        val clicked = mutableListOf<Int>()
        composeRule.setContent {
            UPSwipeAction {
                UPSwipeActionItem(
                    UPSwipeActionItemProps(show = true, options = options),
                    onClick = { _, index -> clicked += index },
                ) { UPGap(UPGapProps(height = 48)) }
            }
        }
        composeRule.onNodeWithTag("up-swipe-action-option-0").performClick()
        composeRule.runOnIdle { assertEquals(listOf(0), clicked) }
        // closeOnClick defaults to true, so the row collapses again.
        composeRule.onNodeWithTag("up-swipe-action-option-0").assertDoesNotExist()
    }

    @Test
    fun disabledRowIgnoresSwipes() {
        composeRule.setContent {
            UPSwipeAction {
                UPSwipeActionItem(
                    UPSwipeActionItemProps(disabled = true, options = options),
                    modifier = Modifier.height(48.dp),
                ) { UPGap(UPGapProps(height = 48)) }
            }
        }
        composeRule.onNodeWithTag("up-swipe-action-item").performTouchInput { swipeLeft() }
        composeRule.onNodeWithTag("up-swipe-action-option-0").assertDoesNotExist()
    }

    @Test
    fun openingOneRowClosesTheOtherWhenAutoCloseIsOn() {
        val closed = mutableListOf<UPRawValue>()
        composeRule.setContent {
            UPSwipeAction(UPSwipeActionProps(autoClose = true)) {
                // Row "a" starts open; opening "b" by swipe must sweep "a" closed.
                UPSwipeActionItem(
                    UPSwipeActionItemProps(show = true, name = "a", options = options),
                    onClose = { closed += it },
                ) { UPGap(UPGapProps(height = 48)) }
                UPSwipeActionItem(
                    UPSwipeActionItemProps(name = "b", options = options),
                    modifier = Modifier.height(48.dp).testTag("row-b"),
                ) { UPGap(UPGapProps(height = 48)) }
            }
        }
        composeRule.onNodeWithTag("row-b").performTouchInput { swipeLeft() }
        composeRule.runOnIdle { assertEquals(listOf<UPRawValue>("a"), closed) }
    }

    @Test
    fun openingReportsOpendItemToTheParent() {
        val states = mutableListOf<Boolean>()
        composeRule.setContent {
            UPSwipeAction(onUpdateOpendItem = { states += it }) {
                UPSwipeActionItem(
                    UPSwipeActionItemProps(name = "row", options = options),
                    modifier = Modifier.height(48.dp),
                ) { UPGap(UPGapProps(height = 48)) }
            }
        }
        composeRule.onNodeWithTag("up-swipe-action-item").performTouchInput { swipeLeft() }
        composeRule.runOnIdle { assertTrue("expected opendItem update, got $states", states.contains(true)) }
    }
}
