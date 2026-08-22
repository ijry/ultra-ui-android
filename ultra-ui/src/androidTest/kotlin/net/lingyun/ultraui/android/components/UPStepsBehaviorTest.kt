package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-steps` derives each item's state from its index versus `current`:
 * `current > index` is finished (a checkmark), `current == index` is in progress (the
 * step number on a filled activeColor disc) and `current < index` is waiting (the
 * number in inactiveColor). Ignoring `current` renders every step identically, which
 * is what a hardcoded index does.
 */
@RunWith(AndroidJUnit4::class)
class UPStepsBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun eachStepDerivesItsStateFromCurrent() {
        composeRule.setContent {
            UPSteps(UPStepsProps(current = 1)) {
                UPStepsItem(UPStepsItemProps(title = "第一步"))
                UPStepsItem(UPStepsItemProps(title = "第二步"))
                UPStepsItem(UPStepsItemProps(title = "第三步"))
            }
        }
        // index 0 < current -> finished, so it shows a marker rather than "1".
        composeRule.onNodeWithTag("up-steps-item-0-finish").assertExists()
        // index 1 == current -> in progress, numbered 2.
        composeRule.onNodeWithTag("up-steps-item-1-process").assertExists()
        composeRule.onNodeWithText("2").assertExists()
        // index 2 > current -> waiting, numbered 3.
        composeRule.onNodeWithTag("up-steps-item-2-wait").assertExists()
        composeRule.onNodeWithText("3").assertExists()
    }

    @Test
    fun currentZeroLeavesTheFirstStepInProgressAndTheRestWaiting() {
        composeRule.setContent {
            UPSteps(UPStepsProps(current = 0)) {
                UPStepsItem(UPStepsItemProps(title = "A"))
                UPStepsItem(UPStepsItemProps(title = "B"))
            }
        }
        composeRule.onNodeWithTag("up-steps-item-0-process").assertExists()
        composeRule.onNodeWithTag("up-steps-item-1-wait").assertExists()
    }

    @Test
    fun anErroredStepIsMarkedRegardlessOfCurrent() {
        composeRule.setContent {
            UPSteps(UPStepsProps(current = 2)) {
                UPStepsItem(UPStepsItemProps(title = "A"))
                UPStepsItem(UPStepsItemProps(title = "B", error = true))
                UPStepsItem(UPStepsItemProps(title = "C"))
            }
        }
        composeRule.onNodeWithTag("up-steps-item-1-error").assertExists()
    }

    @Test
    fun titlesRenderInBothDirections() {
        composeRule.setContent {
            UPSteps(UPStepsProps(current = 0, direction = "column")) {
                UPStepsItem(UPStepsItemProps(title = "纵向一"))
                UPStepsItem(UPStepsItemProps(title = "纵向二"))
            }
        }
        composeRule.onNodeWithText("纵向一").assertIsDisplayed()
        composeRule.onNodeWithText("纵向二").assertIsDisplayed()
    }

    @Test
    fun dotModeReplacesTheNumberedCircle() {
        composeRule.setContent {
            UPSteps(UPStepsProps(current = 1, dot = true)) {
                UPStepsItem(UPStepsItemProps(title = "A"))
                UPStepsItem(UPStepsItemProps(title = "B"))
            }
        }
        // The dot is a leaf Box with no text of its own, so it only surfaces in the
        // unmerged tree — the parent row merges childless descendants away.
        composeRule.onNodeWithTag("up-steps-item-1-dot", useUnmergedTree = true).assertExists()
    }

    @Test
    fun customIconsReplaceTheNumberedCircleWhenSet() {
        composeRule.setContent {
            UPSteps(UPStepsProps(current = 1, activeIcon = "checkmark", inactiveIcon = "clock")) {
                UPStepsItem(UPStepsItemProps(title = "A"))
                UPStepsItem(UPStepsItemProps(title = "B"))
                UPStepsItem(UPStepsItemProps(title = "C"))
            }
        }
        // With either icon set, uview swaps the circle out for every step.
        composeRule.onNodeWithTag("up-steps-item-0-icon", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-steps-item-2-icon", useUnmergedTree = true).assertExists()
        // ...so the numbered fallback must not be rendered at all.
        composeRule.onAllNodesWithText("3").assertCountEquals(0)
    }
}
