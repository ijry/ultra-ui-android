package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.assertTextContains
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

@RunWith(AndroidJUnit4::class)
class UPLayoutProgressBehaviorTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rowMeasuresTwelveColumnSpansAndColClickDoesNotBubbleToRow() {
        var rowClicks = 0
        var colClicks = 0
        composeRule.setContent {
            Box(modifier = Modifier.width(300.dp)) {
                UPRow(
                    props = UPRowProps(gutter = "0px", justify = "start"),
                    onClick = { rowClicks++ },
                ) {
                    UPCol(
                        props = UPColProps(span = 4),
                        onClick = { colClicks++ },
                    ) {
                        Box(Modifier.fillMaxWidth().height(20.dp))
                    }
                    UPCol(
                        props = UPColProps(span = 8),
                    ) {
                        Box(Modifier.fillMaxWidth().height(40.dp))
                    }
                }
            }
        }

        val firstBounds = composeRule.onNodeWithTag("up-col-4-0", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val secondBounds = composeRule.onNodeWithTag("up-col-8-0", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val firstWidth = (firstBounds.right - firstBounds.left).value
        val secondWidth = (secondBounds.right - secondBounds.left).value
        assertTrue("firstBounds=$firstBounds firstWidth=$firstWidth", firstWidth > 90f)
        assertTrue("firstBounds=$firstBounds firstWidth=$firstWidth", firstWidth < 110f)
        assertTrue("secondBounds=$secondBounds secondWidth=$secondWidth", secondWidth > 190f)
        assertTrue("firstBounds=$firstBounds secondBounds=$secondBounds", secondBounds.left >= firstBounds.right - 1.dp)

        composeRule.onNodeWithTag("up-col-4-0", useUnmergedTree = true).performClick()
        composeRule.runOnIdle {
            assertEquals(1, colClicks)
            assertEquals(0, rowClicks)
        }
    }

    @Test
    fun rowAppliesHalfGutterOnEachColumnContentEdge() {
        composeRule.setContent {
            Box(modifier = Modifier.width(300.dp)) {
                UPRow(props = UPRowProps(gutter = "12px")) {
                    UPCol(props = UPColProps(span = 6)) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .testTag("gutter-first-content"),
                        )
                    }
                    UPCol(props = UPColProps(span = 6)) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .testTag("gutter-second-content"),
                        )
                    }
                }
            }
        }

        val firstBounds = composeRule.onNodeWithTag("gutter-first-content", useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        val secondBounds = composeRule.onNodeWithTag("gutter-second-content", useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        val gap = secondBounds.left - firstBounds.right
        assertTrue("firstBounds=$firstBounds secondBounds=$secondBounds gap=$gap", kotlin.math.abs(gap.value - 12f) < 2f)
    }

    @Test
    fun gridEmitsNamedPayloadAndUsesIndexWhenNameIsNull() {
        val payloads = mutableListOf<Any?>()
        composeRule.setContent {
            UPGrid(props = UPGridProps(col = 2, gap = "4px", border = true)) {
                UPGridItem(
                    props = UPGridItemProps(name = null),
                    onClick = { payloads += it },
                ) {
                    BasicText("第一项")
                }
                UPGridItem(
                    props = UPGridItemProps(name = "named"),
                    onClick = { payloads += it },
                ) {
                    BasicText("第二项")
                }
                UPGridItem(
                    props = UPGridItemProps(name = 3),
                    onClick = { payloads += it },
                ) {
                    BasicText("第三项")
                }
            }
        }

        composeRule.onNodeWithTag("up-grid-item-0").assertExists().performClick()
        composeRule.onNodeWithTag("up-grid-item-1").performClick()
        composeRule.onNodeWithTag("up-grid-item-2").performClick()
        composeRule.runOnIdle { assertEquals(listOf(0, "named", 3), payloads) }
    }

    @Test
    fun lineProgressClampsDisplaysTextAndSupportsRightAlignedFill() {
        val events = mutableListOf<String>()
        val diagnostics = UPCompatibilityDiagnostics { event -> events += "${event.property}:${event.reason}" }
        composeRule.setContent {
            UPLineProgress(
                props = UPLineProgressProps(percentage = 125, fromRight = true, showText = true),
                modifier = Modifier.width(200.dp),
                diagnostics = diagnostics,
            )
        }

        composeRule.onNodeWithTag("up-line-progress-text").assertExists().assertTextContains("100%")
        val root = composeRule.onNodeWithTag("up-line-progress").getUnclippedBoundsInRoot()
        val active = composeRule.onNodeWithTag("up-line-progress-active").getUnclippedBoundsInRoot()
        val rootWidth = root.right - root.left
        val activeWidth = active.right - active.left
        assertTrue(activeWidth <= rootWidth + 1.dp)
        assertTrue(kotlin.math.abs((active.right - root.right).value) < 1f)
        composeRule.runOnIdle { assertEquals(1, events.size) }

    }

    @Test
    fun lineProgressHidesTextBelowTenPercent() {
        composeRule.setContent {
            UPLineProgress(
                props = UPLineProgressProps(percentage = 5, showText = true),
                modifier = Modifier.width(200.dp),
            )
        }

        composeRule.onNodeWithTag("up-line-progress-text").assertDoesNotExist()
    }

    @Test
    fun circleProgressClampsToSemanticsRange() {
        val events = mutableListOf<String>()
        val diagnostics = UPCompatibilityDiagnostics { event -> events += event.property }
        composeRule.setContent {
            UPCircleProgress(
                props = UPCircleProgressProps(percentage = -20),
                diagnostics = diagnostics,
            )
        }

        composeRule.onNodeWithTag("up-circle-progress")
            .assertExists()
            .assertRangeInfoEquals(ProgressBarRangeInfo(0f, 0f..1f, 0))
        composeRule.runOnIdle { assertEquals(listOf("percentage"), events) }
    }
}
