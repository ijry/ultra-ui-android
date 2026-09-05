package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Behaviour coverage for `u-subsection`'s two modes, per-item colours and disabled state. */
@RunWith(AndroidJUnit4::class)
class UPSubsectionBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun buttonModeIsThirtyFourTallAndSubsectionModeThirtyTwo() {
        composeRule.setContent {
            UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月")))
        }
        composeRule.onNodeWithTag("up-subsection").assertHeightIsEqualTo(34.dp)

        composeRule.setContent {
            UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月"), mode = "subsection"))
        }
        composeRule.onNodeWithTag("up-subsection").assertHeightIsEqualTo(32.dp)
    }

    @Test
    fun theBarSlidesToTheSelectedItemAndReportsBothEvents() {
        val changes = mutableListOf<Int>()
        val updates = mutableListOf<Any?>()
        composeRule.setContent {
            UPSubsection(
                UPSubsectionProps(list = listOf("日", "周", "月")),
                onChange = { changes += it },
                onUpdateCurrent = { updates += it },
            )
        }
        val first = composeRule.onNodeWithTag("up-subsection-bar").getUnclippedBoundsInRoot().left

        composeRule.onNodeWithTag("up-subsection-item-2").performClick()
        composeRule.waitForIdle()
        val last = composeRule.onNodeWithTag("up-subsection-bar").getUnclippedBoundsInRoot().left

        assertTrue("the bar should travel right", last > first)
        composeRule.runOnIdle {
            assertEquals(listOf(2), changes)
            assertEquals(listOf<Any?>(2), updates)
        }
    }

    @Test
    fun aSingleItemStillRendersOneFullWidthBar() {
        composeRule.setContent {
            UPSubsection(UPSubsectionProps(list = listOf("全部")))
        }

        composeRule.onNodeWithTag("up-subsection-bar").assertExists()
        composeRule.onNodeWithTag("up-subsection-item-0").assertTextEquals("全部")
    }

    @Test
    fun anEmptyListRendersTheTrackWithoutABar() {
        composeRule.setContent {
            UPSubsection(UPSubsectionProps(list = emptyList()))
        }

        composeRule.onNodeWithTag("up-subsection").assertExists()
        composeRule.onNodeWithTag("up-subsection-bar").assertDoesNotExist()
    }

    @Test
    fun keyNameAndPerItemColorKeysComeOffObjectEntries() {
        composeRule.setContent {
            UPSubsection(
                UPSubsectionProps(
                    list = listOf(
                        mapOf("label" to "日", "activeColorKey" to "#ff0000"),
                        mapOf("label" to "周", "inactiveColorKey" to "#00ff00"),
                    ),
                    keyName = "label",
                ),
            )
        }

        composeRule.onNodeWithTag("up-subsection-item-0").assertTextEquals("日")
        composeRule.onNodeWithTag("up-subsection-item-1").assertTextEquals("周")
    }

    @Test
    fun disabledBlocksSelectionAndKeepsTheBarWhereItWas() {
        val changes = mutableListOf<Int>()
        composeRule.setContent {
            UPSubsection(
                UPSubsectionProps(list = listOf("日", "周", "月"), current = 1, disabled = true),
                onChange = { changes += it },
            )
        }

        composeRule.onNodeWithTag("up-subsection-item-2").assertIsNotEnabled()
        composeRule.onNodeWithTag("up-subsection-item-2").performClick()
        composeRule.runOnIdle { assertEquals(emptyList<Int>(), changes) }
    }

    @Test
    fun anUnknownModeFallsBackToButtonAndReportsItself() {
        val events = mutableListOf<String>()
        composeRule.setContent {
            UPSubsection(
                UPSubsectionProps(list = listOf("日", "周"), mode = "segmented"),
                diagnostics = UPCompatibilityDiagnostics { events += it.property },
            )
        }

        composeRule.onNodeWithTag("up-subsection").assertHeightIsEqualTo(34.dp)
        composeRule.runOnIdle { assertTrue(events.contains("mode")) }
    }

    @Test
    fun aControlledCurrentMovesTheBarWithoutAClick() {
        composeRule.setContent {
            UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月"), current = 0))
        }
        val first = composeRule.onNodeWithTag("up-subsection-bar").getUnclippedBoundsInRoot().left

        composeRule.setContent {
            UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月"), current = 2))
        }
        composeRule.waitForIdle()
        val last = composeRule.onNodeWithTag("up-subsection-bar").getUnclippedBoundsInRoot().left

        assertTrue("a controlled current should reposition the bar", last > first)
    }
}
