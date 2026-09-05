package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.awaitCancellation
import net.lingyun.ultraui.android.core.UPImageLoader
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPTabsPaginationImageBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun tabsCapsuleShapeAppliesItsOwnItemHeightAndDropsTheSlidingLine() {
        composeRule.setContent {
            UPTabs(UPTabsProps(list = listOf("全部", "待付款"), shapeMode = "capsule"))
        }

        composeRule.onNodeWithTag("up-tabs-item-0").assertHeightIsEqualTo(30.dp)
        composeRule.onNodeWithTag("up-tabs-line").assertDoesNotExist()
    }

    @Test
    fun tabsCardShapeKeepsTheSlidingLineWhileItemStyleWinsOverTheShapeHeight() {
        composeRule.setContent {
            UPTabs(
                UPTabsProps(
                    list = listOf("全部", "待付款"),
                    shapeMode = "card",
                    itemStyle = mapOf<String, UPRawValue>("height" to "60px"),
                ),
            )
        }

        composeRule.onNodeWithTag("up-tabs-item-0").assertHeightIsEqualTo(60.dp)
        composeRule.onNodeWithTag("up-tabs-line", useUnmergedTree = true).assertExists()
    }

    @Test
    fun tabsEmitClickBeforeTheDisabledGuardAndSkipRepeatedSelections() {
        val clicked = mutableListOf<Int>()
        val changed = mutableListOf<Int>()
        composeRule.setContent {
            UPTabs(
                UPTabsProps(
                    list = listOf(
                        mapOf("name" to "全部"),
                        mapOf("name" to "禁用", "disabled" to true),
                        mapOf("name" to "已完成", "badge" to mapOf("value" to 3)),
                    ),
                ),
                onClick = { clicked += it },
                onChange = { changed += it },
            )
        }

        composeRule.onNodeWithTag("up-tabs-item-0").performClick()
        composeRule.onNodeWithTag("up-tabs-item-1").performClick()
        composeRule.onNodeWithTag("up-tabs-item-2").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(0, 1, 2), clicked)
            assertEquals(listOf(2), changed)
        }
    }

    @Test
    fun tabsRenderTheIconAndBadgeDeclaredOnAnItem() {
        composeRule.setContent {
            UPTabs(
                UPTabsProps(
                    list = listOf(mapOf("name" to "收藏", "icon" to "star-fill", "badge" to mapOf("value" to 6))),
                    iconStyle = mapOf<String, UPRawValue>("color" to "#ff9900"),
                ),
            )
        }

        composeRule.onNodeWithContentDescription("u-icon: star-fill", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-badge", useUnmergedTree = true).assertExists()
    }

    @Test
    fun paginationRendersTotalAndCyclesThroughThePageSizes() {
        val sizes = mutableListOf<Int>()
        composeRule.setContent {
            UPPagination(
                UPPaginationProps(total = 42, layout = "total, sizes, prev, pager, next"),
                onUpdatePageSize = { sizes += it },
            )
        }

        composeRule.onNodeWithTag("up-pagination-total").assertTextEquals("共 42 条")
        composeRule.onNodeWithTag("up-pagination-sizes").assertTextEquals("10条/页")
        composeRule.onNodeWithTag("up-pagination-sizes").performClick()
        composeRule.onNodeWithTag("up-pagination-sizes").assertTextEquals("20条/页")
        composeRule.runOnIdle { assertEquals(listOf(20), sizes) }
    }

    @Test
    fun paginationCollapsesTheMiddlePagesAndDisablesPrevOnTheFirstPage() {
        val pages = mutableListOf<Int>()
        composeRule.setContent {
            UPPagination(UPPaginationProps(total = 100, pageSize = 10), onCurrentChange = { pages += it })
        }

        composeRule.onNodeWithTag("up-pagination-prev").assertIsNotEnabled()
        composeRule.onNodeWithTag("up-pagination-page-4").assertTextEquals("...")
        composeRule.onNodeWithTag("up-pagination-page-4").assertIsNotEnabled()
        composeRule.onNodeWithTag("up-pagination-page-5").assertTextEquals("10")
        composeRule.onNodeWithTag("up-pagination-page-5").performClick()
        composeRule.onNodeWithTag("up-pagination-page-1").assertTextEquals("...")
        composeRule.runOnIdle { assertEquals(listOf(10), pages) }
    }

    @Test
    fun paginationHidesItselfWhenASinglePageIsAskedToStayHidden() {
        composeRule.setContent {
            UPPagination(UPPaginationProps(total = 8, pageSize = 10, hideOnSinglePage = true))
        }

        composeRule.onNodeWithTag("up-pagination").assertDoesNotExist()
    }

    @Test
    fun imagePlaceholderUsesTheLoadingIconProp() {
        val pending = UPImageLoader { awaitCancellation() }
        composeRule.setContent {
            UPImage(props = UPImageProps(src = "pending", loadingIcon = "clock"), loader = pending)
        }

        composeRule.onNodeWithTag("up-image-loading", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithContentDescription("u-icon: clock", useUnmergedTree = true).assertExists()
    }
}
