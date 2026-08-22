package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
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
}
