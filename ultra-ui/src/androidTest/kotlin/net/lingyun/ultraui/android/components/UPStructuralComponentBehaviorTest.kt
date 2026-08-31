package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPStructuralComponentBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val tallStyle: Map<String, UPRawValue> = mapOf("height" to "64px")

    @Test
    fun indexAnchorUsesTheNameFromAnOptionObject() {
        composeRule.setContent {
            UPIndexAnchor(UPIndexAnchorProps(text = mapOf("name" to "B"), height = 48))
        }

        composeRule.onNodeWithText("B").assertExists()
        composeRule.onNodeWithTag("up-index-anchor").assertHeightIsEqualTo(48.dp)
    }

    @Test
    fun indexItemPreservesSlottedContentAndCustomStyle() {
        composeRule.setContent {
            UPIndexItem(UPIndexItemProps(customStyle = tallStyle)) {
                UPText(UPTextProps(text = "索引项内容"))
            }
        }

        composeRule.onNodeWithText("索引项内容").assertExists()
        composeRule.onNodeWithTag("up-index-item").assertHeightIsEqualTo(64.dp)
    }

    @Test
    fun pickerColumnPreservesSlottedContentAndCustomStyle() {
        composeRule.setContent {
            UPPickerColumn(UPPickerColumnProps(customStyle = tallStyle)) {
                UPText(UPTextProps(text = "选择列内容"))
            }
        }

        composeRule.onNodeWithText("选择列内容").assertExists()
        composeRule.onNodeWithTag("up-picker-column").assertHeightIsEqualTo(64.dp)
    }

    @Test
    fun pickerCanBeEmbeddedInVerticallyScrollableContent() {
        composeRule.setContent {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                UPPicker(
                    UPPickerProps(
                        show = true,
                        columns = listOf(listOf("一", "二", "三", "四", "五", "六")),
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("up-picker").assertExists()
        composeRule.onNodeWithText("一").assertExists()
    }

    @Test
    fun datetimePickerCanBeEmbeddedInVerticallyScrollableContent() {
        composeRule.setContent {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                UPDatetimePicker(UPDatetimePickerProps(show = true, mode = "time"))
            }
        }

        composeRule.onNodeWithTag("up-datetime-picker").assertExists()
        composeRule.onNodeWithTag("up-datetime-picker-column-0").assertExists()
    }

    @Test
    fun safeBottomMatchesTheNavigationBarInset() {
        var navigationBarHeight = Dp.Unspecified
        composeRule.setContent {
            val density = LocalDensity.current
            navigationBarHeight = with(density) {
                WindowInsets.navigationBars.getBottom(this).toDp()
            }
            Box(Modifier.testTag("safe-bottom-layout")) {
                UPSafeBottom()
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("safe-bottom-layout").assertHeightIsEqualTo(navigationBarHeight)
    }

    @Test
    fun swiperIndicatorLineModeUsesOneTrackAndOneMovingBar() {
        var lineWidthPx = 0f
        composeRule.setContent {
            lineWidthPx = with(LocalDensity.current) { 22.dp.toPx() }
            UPSwiperIndicator(UPSwiperIndicatorProps(length = 3, current = 1, indicatorMode = "line"))
        }

        composeRule.onNodeWithTag("up-swiper-indicator-line-track").assertWidthIsEqualTo(66.dp)
        composeRule.onNodeWithTag("up-swiper-indicator-line-bar").assertWidthIsEqualTo(22.dp)
        val trackBounds = composeRule.onNodeWithTag("up-swiper-indicator-line-track").fetchSemanticsNode().boundsInRoot
        val barBounds = composeRule.onNodeWithTag("up-swiper-indicator-line-bar").fetchSemanticsNode().boundsInRoot
        assertEquals(lineWidthPx, barBounds.left - trackBounds.left, 1f)
    }

    @Test
    fun swiperIndicatorDotModeWidensTheCurrentDotAndReportsClicks() {
        val clicked = mutableListOf<Int>()
        composeRule.setContent {
            UPSwiperIndicator(
                props = UPSwiperIndicatorProps(length = 3, current = 1, indicatorMode = "dot"),
                onClick = { clicked += it },
            )
        }

        composeRule.onNodeWithTag("up-swiper-indicator-dot-0").assertWidthIsEqualTo(5.dp)
        composeRule.onNodeWithTag("up-swiper-indicator-dot-1").assertWidthIsEqualTo(12.dp)
        composeRule.onNodeWithTag("up-swiper-indicator-dot-2").performClick()
        composeRule.runOnIdle { assertEquals(listOf(2), clicked) }
    }

    @Test
    fun tabsItemPreservesSlottedContentAndCustomStyle() {
        composeRule.setContent {
            UPTabsItem(UPTabsItemProps(customStyle = tallStyle)) {
                UPText(UPTextProps(text = "标签页内容"))
            }
        }

        composeRule.onNodeWithText("标签页内容").assertExists()
        composeRule.onNodeWithTag("up-tabs-item").assertHeightIsEqualTo(64.dp)
    }
}
