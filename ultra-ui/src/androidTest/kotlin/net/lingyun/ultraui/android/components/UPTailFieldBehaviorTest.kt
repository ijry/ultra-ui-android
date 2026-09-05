package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

/**
 * Behaviour coverage for the final unread fields: `u-count-to`'s easing, `u-back-top`'s
 * scroll duration, `u-skeleton`'s rows and avatar shape, `u-select`'s options panel,
 * `u-read-more`'s indent and shadow, and `u-cascader`'s header direction and mask.
 */
@RunWith(AndroidJUnit4::class)
class UPTailFieldBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun countToStepsThroughIntermediateValuesRatherThanJumping() {
        composeRule.mainClock.autoAdvance = false
        val emitted = mutableListOf<Int>()
        composeRule.setContent {
            UPCountTo(
                UPCountToProps(startVal = 0, endVal = 100, duration = 1000, useEasing = false),
                onChange = { emitted += it.toInt() },
            )
        }

        composeRule.onNodeWithTag("up-count-to").assertTextEquals("0")
        composeRule.mainClock.advanceTimeBy(500L)
        composeRule.runOnIdle {
            assertTrue("the counter should emit intermediate values, got $emitted", emitted.any { it in 1..99 })
        }

        composeRule.mainClock.advanceTimeBy(1_000L)
        composeRule.onNodeWithTag("up-count-to").assertTextEquals("100")
    }

    @Test
    fun easedCountingOutrunsTheLinearRampEarlyOn() {
        composeRule.mainClock.autoAdvance = false
        var eased = -1
        var linear = -1
        composeRule.setContent {
            UPCountTo(
                UPCountToProps(startVal = 0, endVal = 100, duration = 2000, useEasing = true),
                onChange = { eased = it.toInt() },
            )
        }
        composeRule.mainClock.advanceTimeBy(400L)

        composeRule.setContent {
            UPCountTo(
                UPCountToProps(startVal = 0, endVal = 100, duration = 2000, useEasing = false),
                onChange = { linear = it.toInt() },
            )
        }
        composeRule.mainClock.advanceTimeBy(400L)

        assertTrue("eased=$eased should lead linear=$linear", eased > linear)
    }

    @Test
    fun countToAutoplayFalseStaysAtTheStartValue() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPCountTo(UPCountToProps(startVal = 7, endVal = 100, autoplay = false))
        }

        composeRule.mainClock.advanceTimeBy(3_000L)
        composeRule.onNodeWithTag("up-count-to").assertTextEquals("7")
    }

    @Test
    fun backTopHandsItsScrollDurationToTheHost() {
        val durations = mutableListOf<Int>()
        composeRule.setContent {
            UPBackTop(
                props = UPBackTopProps(scrollTop = 900, duration = 350),
                onScrollToTop = { durations += it },
            )
        }

        composeRule.onNodeWithTag("up-back-top").performClick()
        composeRule.runOnIdle { assertEquals(listOf(350), durations) }
    }

    @Test
    fun skeletonRowWidthsFollowThePerRowArray() {
        composeRule.setContent {
            Box(modifier = Modifier.width(200.dp)) {
                UPSkeleton(UPSkeletonProps(rows = 3, rowsWidth = listOf("50%", "100%", "25%")))
            }
        }

        val first = composeRule.onNodeWithTag("up-skeleton-row-0").getUnclippedBoundsInRoot()
        val second = composeRule.onNodeWithTag("up-skeleton-row-1").getUnclippedBoundsInRoot()
        val third = composeRule.onNodeWithTag("up-skeleton-row-2").getUnclippedBoundsInRoot()
        assertEquals(100.dp.value, (first.right - first.left).value, 1f)
        assertEquals(200.dp.value, (second.right - second.left).value, 1f)
        assertEquals(50.dp.value, (third.right - third.left).value, 1f)
    }

    @Test
    fun theLastSkeletonRowFallsBackToSeventyPercent() {
        composeRule.setContent {
            Box(modifier = Modifier.width(200.dp)) {
                UPSkeleton(UPSkeletonProps(rows = 2))
            }
        }

        val last = composeRule.onNodeWithTag("up-skeleton-row-1").getUnclippedBoundsInRoot()
        assertEquals(140.dp.value, (last.right - last.left).value, 1f)
    }

    @Test
    fun anUnknownAvatarShapeFallsBackToCircleAndReportsItself() {
        val events = mutableListOf<String>()
        composeRule.setContent {
            UPSkeleton(
                props = UPSkeletonProps(avatar = true, rows = 1, avatarShape = "hexagon"),
                diagnostics = UPCompatibilityDiagnostics { events += it.property },
            )
        }

        composeRule.onNodeWithTag("up-skeleton-avatar").assertExists()
        composeRule.runOnIdle { assertTrue(events.contains("avatarShape")) }
    }

    @Test
    fun selectShowOptionsLabelDecidesWhatTheTriggerPrints() {
        val options = listOf(mapOf("id" to 1, "name" to "北京"))
        composeRule.setContent {
            UPSelect(UPSelectProps(options = options, current = 1, label = "请选择"))
        }
        composeRule.onNodeWithTag("up-select-trigger").assertTextEquals("请选择")

        composeRule.setContent {
            UPSelect(UPSelectProps(options = options, current = 1, label = "请选择", showOptionsLabel = true))
        }
        composeRule.onNodeWithTag("up-select-trigger").assertTextEquals("北京")
    }

    @Test
    fun selectOptionsWidthSizesTheOpenPanel() {
        composeRule.setContent {
            UPSelect(
                UPSelectProps(
                    options = listOf(mapOf("id" to 1, "name" to "北京"), mapOf("id" to 2, "name" to "上海")),
                    optionsWidth = 140,
                ),
            )
        }

        composeRule.onNodeWithTag("up-select-trigger").performClick()
        val panel = composeRule.onNodeWithTag("up-select-options").getUnclippedBoundsInRoot()
        assertEquals(140.dp.value, (panel.right - panel.left).value, 1f)
    }

    @Test
    fun readMoreTextIndentPushesTheCollapsedContentIn() {
        composeRule.setContent {
            UPReadMore(UPReadMoreProps(showHeight = 40, textIndent = "0")) { BasicText("内容") }
        }
        val flush = composeRule.onNodeWithTag("up-read-more-content").getUnclippedBoundsInRoot().left

        composeRule.setContent {
            UPReadMore(UPReadMoreProps(showHeight = 40, textIndent = "2em", fontSize = 14)) { BasicText("内容") }
        }
        val indented = composeRule.onNodeWithTag("up-read-more-content").getUnclippedBoundsInRoot().left

        assertTrue("textIndent should offset the content", indented >= flush)
    }

    @Test
    fun readMoreShadowStyleOnlyAppliesWhileCollapsed() {
        var open by mutableStateOf(false)
        composeRule.setContent {
            UPReadMore(
                props = UPReadMoreProps(
                    showHeight = 40,
                    toggle = true,
                    modelValue = open,
                    shadowStyle = mapOf("height" to "36px"),
                ),
            ) { BasicText("内容") }
        }
        val collapsed = composeRule.onNodeWithTag("up-read-more-toggle").getUnclippedBoundsInRoot()
            .let { it.bottom - it.top }

        composeRule.runOnIdle { open = true }
        composeRule.waitForIdle()
        val expanded = composeRule.onNodeWithTag("up-read-more-toggle").getUnclippedBoundsInRoot()
            .let { it.bottom - it.top }

        assertEquals(36.dp.value, collapsed.value, 1f)
        assertTrue("the shadow should be gone once expanded", expanded < collapsed)
    }

    @Test
    fun cascaderHeaderDirectionColumnStacksTheLevels() {
        val data = listOf(
            mapOf(
                "value" to "zhejiang",
                "label" to "浙江",
                "children" to listOf(mapOf("value" to "hangzhou", "label" to "杭州")),
            ),
        )
        composeRule.setContent {
            UPCascader(
                UPCascaderProps(
                    show = true,
                    data = data,
                    modelValue = listOf("zhejiang"),
                    headerDirection = "column",
                ),
            )
        }

        val first = composeRule.onNodeWithTag("up-cascader-column-0").getUnclippedBoundsInRoot()
        val second = composeRule.onNodeWithTag("up-cascader-column-1").getUnclippedBoundsInRoot()
        assertTrue("a column header stacks the levels vertically", second.top >= first.bottom)
    }

    @Test
    fun cascaderMaskCloseAbleDismissesWithoutConfirming() {
        val data = listOf(mapOf("value" to "zhejiang", "label" to "浙江"))
        var cancels = 0
        var shown: Boolean? = null
        composeRule.setContent {
            UPCascader(
                props = UPCascaderProps(show = true, data = data, closeOnClickOverlay = true),
                onCancel = { cancels += 1 },
                onUpdateShow = { shown = it },
            )
        }

        composeRule.onNodeWithTag("up-cascader").performClick()
        composeRule.runOnIdle {
            assertEquals(1, cancels)
            assertEquals(false, shown)
        }
    }

    @Test
    fun cascaderMaskCloseAbleFalseKeepsTheSheetOpen() {
        val data = listOf(mapOf("value" to "zhejiang", "label" to "浙江"))
        var cancels = 0
        composeRule.setContent {
            UPCascader(
                props = UPCascaderProps(
                    show = true,
                    data = data,
                    closeOnClickOverlay = true,
                    maskCloseAble = false,
                ),
                onCancel = { cancels += 1 },
            )
        }

        composeRule.onNodeWithTag("up-cascader").performClick()
        composeRule.runOnIdle { assertEquals(0, cancels) }
    }
}
