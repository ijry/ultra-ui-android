package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.width
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPImageLoaders
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-swiper` defaults to `autoplay: true`, so a generated swiper is expected to
 * advance on its own every `interval` ms and — when `circular` is set — wrap past the
 * last slide. Without that the component only ever moves when tapped, and autoplay /
 * interval / duration / circular are all silent no-ops.
 */
@RunWith(AndroidJUnit4::class)
class UPSwiperBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val slides: List<UPRawValue> = listOf("A", "B", "C")

    private val imageSlides: List<UPRawValue> = listOf(
        mapOf("id" to "first", "url" to "https://cdn.test/a.png", "title" to "第一张"),
        mapOf("id" to "second", "url" to "https://cdn.test/b.png", "title" to "第二张"),
        mapOf("id" to "third", "url" to "https://cdn.test/c.mp4", "poster" to "https://cdn.test/c.png"),
    )

    @Test
    fun autoplayAdvancesThroughSlidesOnItsOwn() {
        val seen = mutableListOf<Int>()
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPSwiper(
                props = UPSwiperProps(list = slides, autoplay = true, interval = 500, duration = 0),
                onChange = { seen += it },
            )
        }
        composeRule.mainClock.advanceTimeBy(1_600)
        composeRule.runOnIdle {
            assertTrue("expected autoplay to emit change events, got $seen", seen.isNotEmpty())
        }
    }

    @Test
    fun autoplayStopsWhenDisabled() {
        val seen = mutableListOf<Int>()
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPSwiper(
                props = UPSwiperProps(list = slides, autoplay = false, interval = 300),
                onChange = { seen += it },
            )
        }
        composeRule.mainClock.advanceTimeBy(2_000)
        composeRule.runOnIdle { assertEquals(emptyList<Int>(), seen) }
    }

    @Test
    fun circularWrapsPastTheLastSlide() {
        val seen = mutableListOf<Int>()
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPSwiper(
                props = UPSwiperProps(
                    list = slides,
                    current = 2,
                    autoplay = true,
                    interval = 400,
                    duration = 0,
                    circular = true,
                ),
                onChange = { seen += it },
            )
        }
        composeRule.mainClock.advanceTimeBy(500)
        composeRule.runOnIdle {
            assertEquals("circular should wrap 2 -> 0", listOf(0), seen)
        }
    }

    @Test
    fun withoutCircularAutoplayStopsOnTheLastSlide() {
        val seen = mutableListOf<Int>()
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            UPSwiper(
                props = UPSwiperProps(
                    list = slides,
                    current = 2,
                    autoplay = true,
                    interval = 400,
                    duration = 0,
                    circular = false,
                ),
                onChange = { seen += it },
            )
        }
        composeRule.mainClock.advanceTimeBy(1_400)
        composeRule.runOnIdle { assertEquals(emptyList<Int>(), seen) }
    }

    @Test
    fun swiperRendersItsCurrentSlide() {
        composeRule.setContent {
            UPSwiper(UPSwiperProps(list = slides, autoplay = false, current = 1))
        }
        composeRule.onNodeWithTag("up-swiper").assertExists()
    }

    @Test
    fun loadingReplacesTheSlidesWithALoadingIcon() {
        composeRule.setContent {
            UPSwiper(UPSwiperProps(list = slides, autoplay = false, loading = true, indicator = true))
        }

        composeRule.onNodeWithTag("up-swiper").assertExists()
        composeRule.onNodeWithTag("up-swiper-loading").assertExists()
        composeRule.onNodeWithTag("up-swiper-item-0").assertDoesNotExist()
        composeRule.onNodeWithTag("up-swiper-indicator").assertDoesNotExist()
    }

    @Test
    fun imageSlidesRenderThroughUpImageInsteadOfPlainText() {
        composeRule.setContent {
            UPSwiper(
                props = UPSwiperProps(list = imageSlides, autoplay = false, imgMode = "aspectFit", radius = 8),
                loader = UPImageLoaders.Empty,
            )
        }

        composeRule.onNodeWithTag("up-swiper-item-0").assertExists()
        composeRule.onNodeWithTag("up-image", useUnmergedTree = true).assertExists()
    }

    @Test
    fun showTitleOverlaysTheCaptionAndHidesTheIndicator() {
        composeRule.setContent {
            UPSwiper(
                props = UPSwiperProps(list = imageSlides, autoplay = false, indicator = true, showTitle = true),
                loader = UPImageLoaders.Empty,
            )
        }

        composeRule.onNodeWithText("第一张", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-swiper-indicator").assertDoesNotExist()
    }

    @Test
    fun indicatorShowsWhenTitlesAreOff() {
        composeRule.setContent {
            UPSwiper(
                props = UPSwiperProps(list = imageSlides, autoplay = false, indicator = true, showTitle = false),
                loader = UPImageLoaders.Empty,
            )
        }

        composeRule.onNodeWithTag("up-swiper-indicator").assertExists()
        composeRule.onNodeWithText("第一张", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun currentItemIdSelectsTheSlideAndBeatsCurrent() {
        val seen = mutableListOf<Int>()
        composeRule.setContent {
            UPSwiper(
                props = UPSwiperProps(list = imageSlides, autoplay = false, current = 0, currentItemId = "second"),
                loader = UPImageLoaders.Empty,
                onChange = { seen += it },
            )
        }

        // `current = 0` would leave no backward control; resolving "second" puts the swiper on index 1.
        composeRule.onNodeWithTag("up-swiper-previous").assertExists()
        composeRule.onNodeWithTag("up-swiper-next").performClick()
        composeRule.runOnIdle { assertEquals(listOf(2), seen) }
    }

    @Test
    fun pagingControlsReportTheNeighbouringIndex() {
        val seen = mutableListOf<Int>()
        composeRule.setContent {
            UPSwiper(
                props = UPSwiperProps(list = slides, autoplay = false, current = 1),
                onChange = { seen += it },
            )
        }

        composeRule.onNodeWithTag("up-swiper-next").performClick()
        composeRule.runOnIdle { assertEquals(listOf(2), seen) }
        composeRule.onNodeWithTag("up-swiper-previous").performClick()
        composeRule.runOnIdle { assertEquals(listOf(2, 1), seen) }
    }

    @Test
    fun withoutCircularTheFirstSlideHasNoBackwardControl() {
        composeRule.setContent {
            UPSwiper(UPSwiperProps(list = slides, autoplay = false, current = 0, circular = false))
        }

        composeRule.onNodeWithTag("up-swiper-previous").assertDoesNotExist()
        composeRule.onNodeWithTag("up-swiper-next").assertExists()
    }

    @Test
    fun circularKeepsBothControlsOnTheFirstSlide() {
        composeRule.setContent {
            UPSwiper(UPSwiperProps(list = slides, autoplay = false, current = 0, circular = true))
        }

        composeRule.onNodeWithTag("up-swiper-previous").assertExists()
        composeRule.onNodeWithTag("up-swiper-next").assertExists()
    }

    @Test
    fun verticalStacksTheSlidesInsteadOfLiningThemUp() {
        composeRule.setContent {
            UPSwiper(UPSwiperProps(list = slides, autoplay = false, current = 0, vertical = true))
        }

        val first = composeRule.onNodeWithTag("up-swiper-item-0").getUnclippedBoundsInRoot()
        val second = composeRule.onNodeWithTag("up-swiper-item-1").getUnclippedBoundsInRoot()
        assertTrue(second.top.value > first.top.value)
        assertEquals(first.left.value, second.left.value, 0.5f)
    }

    @Test
    fun horizontalLinesTheSlidesUpSideBySide() {
        composeRule.setContent {
            UPSwiper(UPSwiperProps(list = slides, autoplay = false, current = 0, vertical = false))
        }

        val first = composeRule.onNodeWithTag("up-swiper-item-0").getUnclippedBoundsInRoot()
        val second = composeRule.onNodeWithTag("up-swiper-item-1").getUnclippedBoundsInRoot()
        assertTrue(second.left.value > first.left.value)
        assertEquals(first.top.value, second.top.value, 0.5f)
    }

    @Test
    fun displayMultipleItemsSharesTheViewportBetweenSlides() {
        composeRule.setContent {
            UPSwiper(UPSwiperProps(list = slides, autoplay = false, current = 0, displayMultipleItems = 2))
        }

        val root = composeRule.onNodeWithTag("up-swiper").getUnclippedBoundsInRoot()
        val slide = composeRule.onNodeWithTag("up-swiper-item-0").getUnclippedBoundsInRoot()
        assertEquals(root.width.value / 2f, slide.width.value, 1f)
    }

    @Test
    fun peekingMarginsInsetTheCurrentSlide() {
        composeRule.setContent {
            UPSwiper(
                UPSwiperProps(list = slides, autoplay = false, current = 0, previousMargin = 20, nextMargin = 20),
            )
        }

        val root = composeRule.onNodeWithTag("up-swiper").getUnclippedBoundsInRoot()
        val slide = composeRule.onNodeWithTag("up-swiper-item-0").getUnclippedBoundsInRoot()
        assertEquals(20f, slide.left.value - root.left.value, 1f)
        assertEquals(root.width.value - 40f, slide.width.value, 1f)
    }
}
