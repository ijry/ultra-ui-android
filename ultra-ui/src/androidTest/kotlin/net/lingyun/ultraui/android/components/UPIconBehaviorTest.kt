package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPIconBehaviorTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun iconRendersPinnedGlyphLabelAndEmitsItsIndexOnClick() {
        var clickedIndex: UPRawValue = "not-clicked"

        composeRule.setContent {
            UPIcon(
                props = UPIconProps(name = "map", label = "地图", index = 7),
                onClick = { clickedIndex = it },
            )
        }

        composeRule.onNodeWithTag("up-icon").assertExists().assertHasClickAction().performClick()
        composeRule.onNodeWithTag("up-icon-glyph", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText("地图").assertExists()
        composeRule.runOnIdle { assertEquals(7, clickedIndex) }
    }

    @Test
    fun iconPlacesTopLabelAboveItsGlyph() {
        composeRule.setContent {
            UPIcon(UPIconProps(name = "map", label = "顶部文字", labelPos = "top"))
        }

        val labelBounds = composeRule.onNodeWithTag("up-icon-label").getUnclippedBoundsInRoot()
        val glyphBounds = composeRule.onNodeWithTag("up-icon-glyph").getUnclippedBoundsInRoot()
        assertTrue("labelPos=top 必须将 label 放在 glyph 上方", labelBounds.bottom <= glyphBounds.top)
    }

    @Test
    fun unknownGlyphReportsDowngradeAndDoesNotRenderAReplacementCharacter() {
        val events = mutableListOf<String>()
        val diagnostics = UPCompatibilityDiagnostics { event -> events += "${event.property}:${event.reason}" }

        composeRule.setContent {
            UPIcon(
                props = UPIconProps(name = "not-a-uview-glyph"),
                diagnostics = diagnostics,
            )
        }

        composeRule.onNodeWithTag("up-icon").assertExists()
        composeRule.onNodeWithTag("up-icon-glyph", useUnmergedTree = true).assertDoesNotExist()
        composeRule.runOnIdle {
            assertEquals(listOf("name:Unknown uview-plus icon glyph."), events)
        }
    }
}
