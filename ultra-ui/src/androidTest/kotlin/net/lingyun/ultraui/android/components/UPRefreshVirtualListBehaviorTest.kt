package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upTestTag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-refresh-virtual-list`: renders the virtualized rows inside the pull-refresh wrapper, forwards
 * refresh through the controller, and re-seeds on `scrollToTop`. Composition of the already-tested
 * `UPPullRefresh` + `UPVirtualList`.
 */
@RunWith(AndroidJUnit4::class)
class UPRefreshVirtualListBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersRowsAndExposesController() {
        lateinit var controller: UPRefreshVirtualListController
        val data: List<UPRawValue> = (0 until 20).map { mapOf<String, UPRawValue>("id" to it, "label" to "行$it") }
        composeRule.setContent {
            controller = rememberUPRefreshVirtualListController()
            UPRefreshVirtualList(
                props = UPRefreshVirtualListProps(listData = data, itemHeight = 40),
                controller = controller,
            ) { item, index ->
                BasicText(text = item["label"].toString(), modifier = Modifier.fillMaxWidth().upTestTag("row-$index"))
            }
        }

        composeRule.onNodeWithTag("up-refresh-virtual-list").assertExists()
        composeRule.onNodeWithTag("up-virtual-list").assertExists()
        composeRule.onNodeWithTag("row-0").assertExists()
        // Controller drives the refreshing flag.
        composeRule.runOnIdle { controller.finishRefresh() }
        composeRule.runOnIdle {
            controller.scrollTo(80)
            assertEquals(80, controller.scrollTop)
            assertTrue(!controller.refreshing)
        }
    }
}
