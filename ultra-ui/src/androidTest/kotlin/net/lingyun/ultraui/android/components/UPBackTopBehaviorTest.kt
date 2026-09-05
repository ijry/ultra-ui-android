package net.lingyun.ultraui.android.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-back-top` appears once `scrollTop > top`, fades in and out with `u-transition`, and
 * runs `uni.pageScrollTo({ scrollTop: 0, duration })` on tap. Handing it the host's
 * `ScrollState` lets it read the offset and perform that scroll itself, which is the part
 * previously written off as "the scroll container belongs to the host".
 */
@RunWith(AndroidJUnit4::class)
class UPBackTopBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun theButtonIsAFixedFortyDpSquareInItsOwnFill() {
        composeRule.setContent {
            UPBackTop(UPBackTopProps(scrollTop = 800, text = "顶部", bottom = 0, right = 0))
        }

        // `backTopStyle` pins it at 40x40 regardless of whether it carries a label.
        composeRule.onNodeWithTag("up-back-top").assertWidthIsEqualTo(40.dp)
        composeRule.onNodeWithTag("up-back-top").assertHeightIsEqualTo(40.dp)
        composeRule.onNodeWithTag("up-back-top-text", useUnmergedTree = true).assertExists()
    }

    @Test
    fun aScrollStateDrivesBothVisibilityAndTheScrollItself() {
        composeRule.mainClock.autoAdvance = false
        val scrollState = ScrollState(0)
        composeRule.setContent {
            Box(Modifier.height(200.dp)) {
                Column(Modifier.verticalScroll(scrollState)) {
                    UPGap(UPGapProps(height = 2000))
                }
                UPBackTop(
                    props = UPBackTopProps(top = 100, bottom = 0, right = 0),
                    scrollState = scrollState,
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        }

        // `show() { return getPx(scrollTop) > getPx(top) }` — still at the top, so hidden.
        composeRule.onNodeWithTag("up-back-top").assertDoesNotExist()

        composeRule.runOnIdle { runBlocking { scrollState.scrollTo(500) } }
        composeRule.mainClock.advanceTimeBy(400L)
        composeRule.onNodeWithTag("up-back-top").assertExists()

        // The tap performs the scroll, which is `uni.pageScrollTo` upstream.
        composeRule.onNodeWithTag("up-back-top").performClick()
        composeRule.mainClock.advanceTimeBy(400L)
        composeRule.runOnIdle { assertEquals(0, scrollState.value) }
    }

    @Test
    fun withoutAScrollStateTheDurationRidesAlongWithTheCallback() {
        val durations = mutableListOf<Int>()
        composeRule.setContent {
            UPBackTop(
                props = UPBackTopProps(scrollTop = 800, duration = 250, bottom = 0, right = 0),
                onScrollToTop = { durations += it },
            )
        }

        composeRule.onNodeWithTag("up-back-top").performClick()
        // A host whose container is not a Compose `ScrollState` still gets the resolved
        // duration instead of it being dropped.
        composeRule.runOnIdle { assertEquals(listOf(250), durations) }
    }

    @Test
    fun theButtonStaysMountedThroughItsFadeOut() {
        composeRule.mainClock.autoAdvance = false
        val scrollState = ScrollState(0)
        composeRule.setContent {
            Box(Modifier.height(200.dp)) {
                Column(Modifier.verticalScroll(scrollState)) { UPGap(UPGapProps(height = 2000)) }
                UPBackTop(
                    props = UPBackTopProps(top = 100, bottom = 0, right = 0),
                    scrollState = scrollState,
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        }

        composeRule.runOnIdle { runBlocking { scrollState.scrollTo(500) } }
        composeRule.mainClock.advanceTimeBy(400L)
        composeRule.onNodeWithTag("up-back-top").assertExists()

        composeRule.runOnIdle { runBlocking { scrollState.scrollTo(0) } }
        composeRule.mainClock.advanceTimeBy(60L)
        // `<u-transition mode="fade">` plays the leave animation before removal.
        composeRule.onNodeWithTag("up-back-top").assertExists()
        composeRule.mainClock.advanceTimeBy(500L)
        composeRule.onNodeWithTag("up-back-top").assertDoesNotExist()
    }

    @Test
    fun theThresholdIsStrictlyGreaterThanTop() {
        composeRule.setContent {
            UPBackTop(UPBackTopProps(scrollTop = 400, top = 400))
        }
        // `getPx(scrollTop) > getPx(top)`: equal is still hidden.
        composeRule.onNodeWithTag("up-back-top").assertDoesNotExist()

        composeRule.setContent {
            UPBackTop(UPBackTopProps(scrollTop = 401, top = 400, bottom = 0, right = 0))
        }
        composeRule.onNodeWithTag("up-back-top").assertExists()
    }

    @Test
    fun bottomAndRightOffsetTheButtonWithoutInflatingIt() {
        composeRule.setContent {
            Box(Modifier.height(200.dp)) {
                UPBackTop(
                    props = UPBackTopProps(scrollTop = 800, bottom = 24, right = 16),
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        }

        // A fixed offset is not laid out, so the button's own box stays 40x40 — applying
        // `bottom`/`right` as padding would have inflated it to 40 + 24 + 16.
        composeRule.onNodeWithTag("up-back-top").assertWidthIsEqualTo(40.dp)
        composeRule.onNodeWithTag("up-back-top").assertHeightIsEqualTo(40.dp)
        val bounds = composeRule.onNodeWithTag("up-back-top").fetchSemanticsNode().boundsInRoot
        assertTrue("the button should be pulled up from the bottom edge, got $bounds", bounds.bottom < 200 * composeRule.density.density)
    }
}
