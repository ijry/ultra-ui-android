package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-list` carries a scroll/refresh contract: reaching the bottom fires
 * `scrolltolower`, the caller-set height bounds the viewport, and the refresher
 * props drive pull-to-refresh. Without these the component is a plain column and
 * every one of those props is a silent no-op.
 */
@RunWith(AndroidJUnit4::class)
class UPListBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun listHonoursItsConfiguredHeight() {
        composeRule.setContent {
            UPList(UPListProps(height = 200)) {
                repeat(20) { UPListItem { UPGap(UPGapProps(height = 40, bgColor = "#eeeeee")) } }
            }
        }
        // 20 items of 40dp would be 800dp tall, so an unbounded list would overflow.
        // Asserting the ceiling is what proves `height` actually clamps the viewport.
        composeRule.onNodeWithTag("up-list").assertHeightIsEqualTo(200.dp)
    }

    @Test
    fun scrollingToTheBottomFiresScrollToLower() {
        var lowerCount = 0
        composeRule.setContent {
            UPList(
                props = UPListProps(height = 200, lowerThreshold = 50),
                onScrollToLower = { lowerCount++ },
            ) {
                repeat(20) { index ->
                    UPListItem(modifier = Modifier.height(40.dp)) { UPGap(UPGapProps(height = 40)) }
                }
            }
        }
        composeRule.onNodeWithTag("up-list-sentinel").performScrollTo()
        composeRule.runOnIdle { assertTrue("expected scrolltolower to fire", lowerCount > 0) }
    }

    @Test
    fun refresherTriggeredIsReportedBackWhenPullingIsEnabled() {
        val refreshes = mutableListOf<Boolean>()
        val triggeredUpdates = mutableListOf<Boolean>()
        composeRule.setContent {
            UPList(
                props = UPListProps(height = 200, refresherEnabled = true),
                onRefresherRefresh = { refreshes += true },
                onUpdateRefresherTriggered = { triggeredUpdates += it },
            ) {
                repeat(6) { UPListItem { UPGap(UPGapProps(height = 40)) } }
            }
        }
        // The indicator only exists while refresherEnabled is set, and activating it
        // must report both the refresh and the triggered-state change back to the caller.
        composeRule.onNodeWithTag("up-list-refresher").assertExists().performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(true), refreshes)
            assertEquals(listOf(true), triggeredUpdates)
        }
    }

    @Test
    fun refresherIsAbsentUntilItIsEnabled() {
        composeRule.setContent {
            UPList(UPListProps(height = 200)) { UPListItem { UPGap(UPGapProps(height = 40)) } }
        }
        composeRule.onNodeWithTag("up-list-refresher").assertDoesNotExist()
    }

    @Test
    fun nonScrollableListStillRendersItsContent() {
        composeRule.setContent {
            UPList(UPListProps(scrollable = false)) { UPListItem { UPGap(UPGapProps(height = 30)) } }
        }
        composeRule.onNodeWithTag("up-list").assertExists()
        composeRule.onNodeWithTag("up-list-item").assertExists()
    }
}
