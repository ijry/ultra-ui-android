package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-dropdown` styles the *title bar* from the parent: `height` bounds it,
 * `borderBottom` underlines it, `titleSize` sets the label size and `menuIcon` /
 * `menuIconSize` pick the caret. Hardcoding those in the item leaves every one of
 * them a silent no-op.
 */
@RunWith(AndroidJUnit4::class)
class UPDropdownBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val options: List<UPRawValue> = listOf(
        mapOf("label" to "选项一", "value" to 1),
        mapOf("label" to "选项二", "value" to 2),
    )

    @Test
    fun titleBarHonoursTheParentHeight() {
        composeRule.setContent {
            UPDropdown(UPDropdownProps(height = 56)) {
                UPDropdownItem(UPDropdownItemProps(title = "排序", options = options))
            }
        }
        composeRule.onNodeWithTag("up-dropdown-title-0").assertHeightIsEqualTo(56.dp)
    }

    @Test
    fun menuIconIsTakenFromTheParent() {
        composeRule.setContent {
            UPDropdown(UPDropdownProps(menuIcon = "arrow-right")) {
                UPDropdownItem(UPDropdownItemProps(title = "排序", options = options))
            }
        }
        // The caret is the parent's `menuIcon`, not a hardcoded arrow-down.
        composeRule.onNodeWithTag("up-dropdown-icon-0", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText("排序").assertExists()
    }

    @Test
    fun clickingATitleOpensItsMenuAndReportsOpenState() {
        val opens = mutableListOf<Boolean>()
        composeRule.setContent {
            UPDropdown(UPDropdownProps(), onUpdateOpen = { opens += it }) {
                UPDropdownItem(UPDropdownItemProps(title = "排序", options = options))
            }
        }
        composeRule.onNodeWithTag("up-dropdown-menu-0").assertDoesNotExist()
        composeRule.onNodeWithTag("up-dropdown-title-0").performClick()
        composeRule.onNodeWithTag("up-dropdown-menu-0").assertExists()
        composeRule.runOnIdle { assertEquals(listOf(true), opens) }
    }

    @Test
    fun selectingAnOptionReportsItsValue() {
        val picked = mutableListOf<UPRawValue>()
        composeRule.setContent {
            UPDropdown(UPDropdownProps()) {
                UPDropdownItem(
                    UPDropdownItemProps(title = "排序", options = options),
                    onChange = { picked += it },
                )
            }
        }
        composeRule.onNodeWithTag("up-dropdown-title-0").performClick()
        composeRule.onNodeWithTag("up-dropdown-option-0-1").performClick()
        composeRule.runOnIdle { assertEquals(listOf<UPRawValue>(2), picked) }
    }

    @Test
    fun openingOneTitleClosesTheOther() {
        composeRule.setContent {
            UPDropdown(UPDropdownProps()) {
                UPDropdownItem(UPDropdownItemProps(title = "排序", options = options))
                UPDropdownItem(UPDropdownItemProps(title = "筛选", options = options))
            }
        }
        composeRule.onNodeWithTag("up-dropdown-title-0").performClick()
        composeRule.onNodeWithTag("up-dropdown-menu-0").assertExists()
        composeRule.onNodeWithTag("up-dropdown-title-1").performClick()
        composeRule.onNodeWithTag("up-dropdown-menu-0").assertDoesNotExist()
        composeRule.onNodeWithTag("up-dropdown-menu-1").assertExists()
    }

    /** Runs the node's GetTextLayoutResult action, which carries the resolved font size. */
    private fun laidOutHeight(text: String): Int {
        val results = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        composeRule.onNodeWithText(text).fetchSemanticsNode()
            .config[androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult]
            .action
            ?.invoke(results)
        return results.first().size.height
    }

    @Test
    fun titleSizeComesFromTheParent() {
        // The label sits in a weight(1f) cell inside a fixed-height bar, so its node
        // bounds cannot reveal the font size. Ask the text node for its layout instead:
        // the same string at 22sp must occupy a taller line than at 12sp.
        composeRule.setContent {
            androidx.compose.foundation.layout.Column {
                UPDropdown(UPDropdownProps(titleSize = 22, height = 60)) {
                    UPDropdownItem(UPDropdownItemProps(title = "大号", options = options))
                }
                UPDropdown(UPDropdownProps(titleSize = 12, height = 60)) {
                    UPDropdownItem(UPDropdownItemProps(title = "小号", options = options))
                }
            }
        }
        val big = laidOutHeight("大号")
        val small = laidOutHeight("小号")
        assertTrue("titleSize=22 ($big) should lay out taller than 12 ($small)", big > small)
    }

    @Test
    fun closeOnClickOverlayAliasOverridesTheUviewName() {
        // Earlier Android templates emit `closeOnClickOverlay`; it must win when set.
        composeRule.setContent {
            UPDropdown(UPDropdownProps(closeOnClickMask = true, closeOnClickOverlay = false)) {
                UPDropdownItem(UPDropdownItemProps(title = "排序", options = options))
            }
        }
        composeRule.onNodeWithTag("up-dropdown-title-0").performClick()
        composeRule.onNodeWithTag("up-dropdown-menu-0").assertExists()
        // Selecting still closes; the alias only governs mask taps.
        composeRule.onNodeWithTag("up-dropdown-option-0-0").performClick()
        composeRule.onNodeWithTag("up-dropdown-menu-0").assertDoesNotExist()
    }
}
