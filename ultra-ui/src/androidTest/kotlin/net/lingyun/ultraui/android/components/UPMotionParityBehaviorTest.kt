package net.lingyun.ultraui.android.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
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
 * Behaviour coverage for the motion-parity batch: `u-notice-bar`'s carousel and marquee,
 * `u-collapse-item`'s animated panel and root `customStyle`, and `u-sticky`'s `disabled`.
 */
@RunWith(AndroidJUnit4::class)
class UPMotionParityBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun columnNoticeRendersOneMessageAtATimeAndAdvancesOnItsInterval() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPNoticeBar(
                UPNoticeBarProps(
                    text = listOf("第一条通知", "第二条通知"),
                    direction = "column",
                    duration = 1500,
                ),
            )
        }

        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("第一条通知")
        composeRule.mainClock.advanceTimeBy(1_600L)
        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("第二条通知")
        composeRule.mainClock.advanceTimeBy(1_600L)
        // `circular` wraps back to the first message.
        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("第一条通知")
    }

    @Test
    fun aSingleColumnMessageNeverAdvances() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPNoticeBar(UPNoticeBarProps(text = listOf("只有一条"), direction = "column", duration = 200))
        }

        composeRule.mainClock.advanceTimeBy(2_000L)
        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("只有一条")
    }

    @Test
    fun stepModeUsesTheCarouselEvenWhenTheDirectionIsRow() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPNoticeBar(
                UPNoticeBarProps(
                    text = listOf("步进一", "步进二"),
                    direction = "row",
                    step = true,
                    duration = 800,
                ),
            )
        }

        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("步进一")
        composeRule.mainClock.advanceTimeBy(900L)
        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("步进二")
    }

    @Test
    fun rowModeJoinsTheMessagesIntoOneScrollingLineAndMovesIt() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPNoticeBar(
                UPNoticeBarProps(
                    text = listOf("系统维护通知", "请注意及时保存"),
                    direction = "row",
                    speed = 80,
                ),
            )
        }

        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true)
            .assertTextEquals("系统维护通知  请注意及时保存")
        composeRule.mainClock.advanceTimeBy(16L)
        val start = composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().left
        composeRule.mainClock.advanceTimeBy(1_000L)
        val later = composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().left

        assertTrue("the marquee should travel leftwards", later < start)
    }

    @Test
    fun columnNoticeWrapperForwardsItsIntervalToTheCarousel() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPColumnNotice(
                UPColumnNoticeProps(text = listOf("竖向一", "竖向二"), duration = 700),
            )
        }

        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("竖向一")
        composeRule.mainClock.advanceTimeBy(800L)
        composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true).assertTextEquals("竖向二")
    }

    @Test
    fun rowNoticeWrapperForwardsItsSpeedToTheMarquee() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPRowNotice(UPRowNoticeProps(text = "横向滚动的一段较长通知文字", speed = 120))
        }

        composeRule.mainClock.advanceTimeBy(16L)
        val start = composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().left
        composeRule.mainClock.advanceTimeBy(800L)
        val later = composeRule.onNodeWithTag("up-notice-bar-text", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().left

        assertTrue("the row wrapper should scroll too", later < start)
    }

    @Test
    fun collapseItemKeepsThePanelWhileItAnimatesShut() {
        composeRule.mainClock.autoAdvance = false
        var open by mutableStateOf(true)
        composeRule.setContent {
            UPCollapseItem(props = UPCollapseItemProps(title = "标题", open = open, duration = 600)) {
                BasicText("面板内容")
            }
        }

        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertExists()
        val opened = composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().let { it.bottom - it.top }

        composeRule.runOnIdle { open = false }
        composeRule.mainClock.advanceTimeBy(300L)
        val midway = composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true)
            .getUnclippedBoundsInRoot().let { it.bottom - it.top }
        assertTrue("the panel should still be collapsing, not gone", midway < opened)

        composeRule.mainClock.advanceTimeBy(600L)
        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun collapseItemAppliesCustomStyleToItsRoot() {
        composeRule.setContent {
            UPCollapseItem(
                props = UPCollapseItemProps(
                    title = "标题",
                    isOpen = false,
                    customStyle = mapOf("height" to "88px"),
                ),
            ) {}
        }

        val bounds = composeRule.onNodeWithTag("up-collapse-item").getUnclippedBoundsInRoot()
        assertEquals(88.dp.value, (bounds.bottom - bounds.top).value, 1f)
    }

    @Test
    fun collapseItemZeroDurationOpensAndClosesInstantly() {
        var open by mutableStateOf(false)
        composeRule.setContent {
            UPCollapseItem(props = UPCollapseItemProps(title = "标题", open = open, duration = 0)) {
                BasicText("面板内容")
            }
        }

        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertDoesNotExist()
        composeRule.runOnIdle { open = true }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertExists()
    }

    @Test
    fun collapseItemHeaderStillTogglesThePanel() {
        composeRule.setContent {
            UPCollapseItem(props = UPCollapseItemProps(title = "标题", clickable = true, isLink = true)) {
                BasicText("面板内容")
            }
        }

        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("up-collapse-item-0-header").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-collapse-item-0-content", useUnmergedTree = true).assertExists()
    }

    @Test
    fun stickyClaimsOnlyItsContentHeightRatherThanPaddingItDown() {
        // `position: sticky` with `top: stickyTop` does not move the element until the page
        // scrolls; a band that padded its content down by `offsetTop` would push the whole
        // page down at rest, which is not what upstream does.
        composeRule.setContent {
            UPSticky(UPStickyProps(offsetTop = 40, customNavHeight = 20)) { UPGap(UPGapProps(height = 24)) }
        }
        val band = composeRule.onNodeWithTag("up-sticky").getUnclippedBoundsInRoot()
        assertEquals(24.dp.value, (band.bottom - band.top).value, 1f)
    }

    @Test
    fun stickyPinsItsContentOnceScrolledPastTheOffsetAndReportsIt() {
        composeRule.mainClock.autoAdvance = false
        val scrollState = ScrollState(0)
        val fixedEvents = mutableListOf<Any?>()
        val unfixedEvents = mutableListOf<Any?>()
        composeRule.setContent {
            Column(Modifier.height(200.dp).verticalScroll(scrollState)) {
                UPGap(UPGapProps(height = 300))
                UPSticky(
                    props = UPStickyProps(offsetTop = 0, index = "first"),
                    onFixed = { fixedEvents += it },
                    onUnfixed = { unfixedEvents += it },
                ) { UPGap(UPGapProps(height = 40)) }
                UPGap(UPGapProps(height = 600))
            }
        }

        // Still below the line: not pinned, and no event yet.
        composeRule.onNodeWithTag("up-sticky-content-fixed", useUnmergedTree = true).assertDoesNotExist()
        composeRule.runOnIdle { assertTrue("no event before pinning, got $fixedEvents", fixedEvents.isEmpty()) }

        composeRule.runOnIdle { runBlocking { scrollState.scrollTo(400) } }
        composeRule.waitForIdle()
        // `setFixed(top)` flips once the band's own top reaches `stickyTop`, and `fixed`
        // carries `index` so a caller with several bands can tell them apart.
        composeRule.onNodeWithTag("up-sticky-content-fixed", useUnmergedTree = true).assertExists()
        composeRule.runOnIdle { assertEquals(listOf<Any?>("first"), fixedEvents) }

        composeRule.runOnIdle { runBlocking { scrollState.scrollTo(0) } }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-sticky-content-fixed", useUnmergedTree = true).assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(listOf<Any?>("first"), unfixedEvents) }
    }

    @Test
    fun stickyDisabledNeverPins() {
        val scrollState = ScrollState(0)
        composeRule.setContent {
            Column(Modifier.height(200.dp).verticalScroll(scrollState)) {
                UPGap(UPGapProps(height = 300))
                UPSticky(UPStickyProps(offsetTop = 0, disabled = true)) { UPGap(UPGapProps(height = 40)) }
                UPGap(UPGapProps(height = 600))
            }
        }

        composeRule.runOnIdle { runBlocking { scrollState.scrollTo(400) } }
        composeRule.waitForIdle()
        // `disabled` falls back to `position: static`, so scrolling past changes nothing.
        composeRule.onNodeWithTag("up-sticky-content-fixed", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun sliderHeightSetsTheTrackThicknessAndLengthCapsTheAxis() {
        composeRule.setContent {
            UPSlider(UPSliderProps(value = 40, height = 8, length = 200))
        }

        val bounds = composeRule.onNodeWithTag("up-slider").getUnclippedBoundsInRoot()
        assertEquals(200.dp.value, (bounds.right - bounds.left).value, 1f)
        // `innerStyleCpu.height` is `blockSize` for a single-value slider.
        assertEquals(18.dp.value, (bounds.bottom - bounds.top).value, 1f)
    }

    @Test
    fun aRangeSliderThatShowsItsValuesGetsTwentyFourExtraPixels() {
        composeRule.setContent {
            UPSlider(UPSliderProps(isRange = true, rangeValue = listOf(20, 60), showValue = true))
        }

        val bounds = composeRule.onNodeWithTag("up-slider").getUnclippedBoundsInRoot()
        assertEquals(42.dp.value, (bounds.bottom - bounds.top).value, 1f)
    }

    @Test
    fun sliderBlockStyleResizesTheThumb() {
        composeRule.setContent {
            UPSlider(UPSliderProps(value = 50, blockStyle = mapOf("width" to "34px", "height" to "34px")))
        }

        composeRule.onNodeWithTag("up-slider-thumb-start").assertHeightIsEqualTo(34.dp)
    }

    @Test
    fun sliderUseNativeReportsItsDowngradeButStillRenders() {
        val events = mutableListOf<String>()
        composeRule.setContent {
            UPSlider(
                props = UPSliderProps(value = 30, useNative = true),
                diagnostics = UPCompatibilityDiagnostics { events += it.property },
            )
        }

        composeRule.onNodeWithTag("up-slider").assertExists()
        composeRule.onNodeWithTag("up-slider-thumb-start").assertExists()
        composeRule.runOnIdle { assertTrue(events.contains("useNative")) }
    }

    @Test
    fun aRangeSliderIgnoresUseNativeEntirely() {
        val events = mutableListOf<String>()
        composeRule.setContent {
            UPSlider(
                props = UPSliderProps(isRange = true, rangeValue = listOf(10, 90), useNative = true),
                diagnostics = UPCompatibilityDiagnostics { events += it.property },
            )
        }

        composeRule.onNodeWithTag("up-slider-thumb-end").assertExists()
        composeRule.runOnIdle { assertEquals(emptyList<String>(), events) }
    }

    @Test
    fun listScrollIntoViewJumpsToTheMatchingAnchor() {
        composeRule.setContent {
            UPList(UPListProps(height = 120, scrollIntoView = "row-9")) {
                repeat(10) { index ->
                    UPListItem(props = UPListItemProps(anchor = "row-$index")) {
                        UPGap(UPGapProps(height = 40, bgColor = "#eeeeee"))
                    }
                }
            }
        }
        composeRule.waitForIdle()

        // The last row can only be on screen if the list scrolled to its anchor.
        composeRule.onNodeWithTag("up-list-item-row-9").assertIsDisplayed()
    }

    @Test
    fun listItemsWithoutAnAnchorKeepTheGenericTag() {
        composeRule.setContent {
            UPList(UPListProps(height = 120)) {
                UPListItem { UPGap(UPGapProps(height = 40)) }
            }
        }

        composeRule.onNodeWithTag("up-list-item").assertExists()
    }

    @Test
    fun actionSheetSafeAreaInsetBottomCanBeTurnedOff() {
        val events = mutableListOf<String>()
        composeRule.setContent {
            UPActionSheet(
                props = UPActionSheetProps(
                    show = true,
                    actions = listOf(mapOf("name" to "拍照")),
                    safeAreaInsetBottom = false,
                ),
                diagnostics = UPCompatibilityDiagnostics { events += it.property },
            )
        }

        composeRule.onNodeWithTag("up-action-sheet-panel").assertExists()
        composeRule.runOnIdle { assertEquals(emptyList<String>(), events) }
    }
}
