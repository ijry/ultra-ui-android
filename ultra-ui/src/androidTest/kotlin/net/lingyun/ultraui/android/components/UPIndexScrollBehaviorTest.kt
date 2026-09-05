package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-index-list` draws a draggable index rail down its right edge (falling back to a
 * generated A-Z when `indexList` is empty), magnifies the letter under the finger and
 * scrolls the matching anchor into view; `u-scroll-list` shows a scroll indicator by
 * default (`indicator: true`). Rendering neither leaves every colour, size and the whole
 * index rail a silent no-op.
 */
@RunWith(AndroidJUnit4::class)
class UPIndexScrollBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val letters: List<UPRawValue> = listOf("A", "B", "C")

    @Test
    fun indexRailRendersOneEntryPerIndexCharacter() {
        composeRule.setContent {
            UPIndexList(UPIndexListProps(indexList = letters)) {
                UPIndexItem { UPGap(UPGapProps(height = 40)) }
            }
        }
        composeRule.onNodeWithTag("up-index-list-rail").assertExists()
        composeRule.onNodeWithTag("up-index-list-entry-0", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-index-list-entry-2", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText("B").assertExists()
    }

    @Test
    fun tappingAnIndexCharacterReportsIt() {
        val picked = mutableListOf<UPRawValue>()
        composeRule.setContent {
            UPIndexList(UPIndexListProps(indexList = letters), onIndexClick = { value, _ -> picked += value }) {
                UPIndexItem { UPGap(UPGapProps(height = 40)) }
            }
        }
        composeRule.onNodeWithTag("up-index-list-entry-1", useUnmergedTree = true).performClick()
        composeRule.runOnIdle { assertEquals(listOf<UPRawValue>("B"), picked) }
    }

    @Test
    fun theRailFallsBackToAGeneratedAlphabet() {
        composeRule.setContent {
            UPIndexList(UPIndexListProps()) { UPIndexItem { UPGap(UPGapProps(height = 40)) } }
        }
        // `uIndexList()` generates A-Z when `indexList` is empty, so the rail is never gone.
        composeRule.onNodeWithTag("up-index-list-rail").assertExists()
        composeRule.onNodeWithTag("up-index-list-entry-0", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-index-list-entry-25", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-index-list-entry-26", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun draggingTheRailWalksThroughTheLettersAndShowsTheMagnifier() {
        val picked = mutableListOf<UPRawValue>()
        composeRule.setContent {
            UPIndexList(UPIndexListProps(indexList = letters), onSelect = { picked += it }) {
                UPIndexItem { UPIndexAnchor(UPIndexAnchorProps(text = "A")) }
                UPIndexItem { UPIndexAnchor(UPIndexAnchorProps(text = "B")) }
                UPIndexItem { UPIndexAnchor(UPIndexAnchorProps(text = "C")) }
            }
        }

        val rail = composeRule.onNodeWithTag("up-index-list-rail")
        rail.performTouchInput {
            down(topCenter)
            moveTo(bottomCenter)
        }
        composeRule.waitForIdle()
        // A drag from the top to the bottom of the rail passes through every letter, and
        // upstream's debounce means each is reported once, in order.
        composeRule.runOnIdle {
            assertEquals(listOf<UPRawValue>("A", "B", "C"), picked.distinct())
        }
        // `.u-index-list__indicator` is up while the finger is down.
        composeRule.onNodeWithTag("up-index-list-indicator", useUnmergedTree = true).assertExists()
    }

    @Test
    fun theMagnifierLingersAfterReleaseThenGoesAway() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPIndexList(UPIndexListProps(indexList = letters)) {
                UPIndexItem { UPIndexAnchor(UPIndexAnchorProps(text = "A")) }
            }
        }

        composeRule.onNodeWithTag("up-index-list-rail").performTouchInput {
            down(topCenter)
            moveTo(center)
            up()
        }
        composeRule.mainClock.advanceTimeBy(100L)
        // `sleep(300)` keeps it visible so the user can read the letter they landed on.
        composeRule.onNodeWithTag("up-index-list-indicator", useUnmergedTree = true).assertExists()
        composeRule.mainClock.advanceTimeBy(400L)
        composeRule.onNodeWithTag("up-index-list-indicator", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun stickyDecidesWhichAnchorModeIsInForce() {
        composeRule.setContent {
            UPIndexList(UPIndexListProps(indexList = letters, sticky = true)) {
                UPIndexItem { UPIndexAnchor(UPIndexAnchorProps(text = "A")) }
            }
        }
        composeRule.onNodeWithTag("up-index-anchor-sticky", useUnmergedTree = true).assertExists()

        composeRule.setContent {
            UPIndexList(UPIndexListProps(indexList = letters, sticky = false)) {
                UPIndexItem { UPIndexAnchor(UPIndexAnchorProps(text = "A")) }
            }
        }
        composeRule.onNodeWithTag("up-index-anchor-static", useUnmergedTree = true).assertExists()
    }

    @Test
    fun aStandaloneAnchorDefaultsToTheStickyMode() {
        composeRule.setContent { UPIndexAnchor(UPIndexAnchorProps(text = "A")) }
        // `parentSticky() { return indexList ? indexList.sticky : true }`.
        composeRule.onNodeWithTag("up-index-anchor-sticky", useUnmergedTree = true).assertExists()
    }

    @Test
    fun scrollListShowsItsIndicatorByDefault() {
        composeRule.setContent {
            UPScrollList(UPScrollListProps()) { UPGap(UPGapProps(height = 60)) }
        }
        // `indicator` defaults to true upstream.
        composeRule.onNodeWithTag("up-scroll-list-indicator").assertExists()
    }

    @Test
    fun scrollListIndicatorHonoursItsWidth() {
        composeRule.setContent {
            UPScrollList(UPScrollListProps(indicatorWidth = 80)) { UPGap(UPGapProps(height = 60)) }
        }
        composeRule.onNodeWithTag("up-scroll-list-indicator").assertWidthIsEqualTo(80.dp)
    }

    @Test
    fun scrollListIndicatorCanBeTurnedOff() {
        composeRule.setContent {
            UPScrollList(UPScrollListProps(indicator = false)) { UPGap(UPGapProps(height = 60)) }
        }
        composeRule.onNodeWithTag("up-scroll-list-indicator").assertDoesNotExist()
    }
}
