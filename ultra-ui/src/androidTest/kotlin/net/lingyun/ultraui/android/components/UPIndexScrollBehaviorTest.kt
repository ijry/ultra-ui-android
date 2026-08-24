package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-index-list` draws a tappable index rail down its right edge from `indexList`, and
 * `u-scroll-list` shows a scroll indicator by default (`indicator: true`). Rendering
 * neither leaves every colour, size and the whole index rail a silent no-op.
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
    fun indexRailIsAbsentWhenNoCharactersAreGiven() {
        composeRule.setContent {
            UPIndexList(UPIndexListProps()) { UPIndexItem { UPGap(UPGapProps(height = 40)) } }
        }
        composeRule.onNodeWithTag("up-index-list-rail").assertDoesNotExist()
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
