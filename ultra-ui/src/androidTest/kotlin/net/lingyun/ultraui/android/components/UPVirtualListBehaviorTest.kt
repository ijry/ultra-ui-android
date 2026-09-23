package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-virtual-list`: renders visible rows from a bounded LazyColumn (off-screen rows are recycled).
 */
@RunWith(AndroidJUnit4::class)
class UPVirtualListBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersLeadingRowsWithinTheBoundedHeight() {
        val data = (1..200).map { mapOf("id" to it, "label" to "行$it") }
        composeRule.setContent {
            UPVirtualList(
                UPVirtualListProps(listData = data, itemHeight = 50, height = "200"),
                modifier = Modifier.fillMaxWidth().height(200.dp),
            ) { item, _ ->
                BasicText(item["label"].toString())
            }
        }

        composeRule.onNodeWithTag("up-virtual-list").assertExists()
        // The first row is visible; a far-off row is not composed (virtualized).
        composeRule.onNodeWithText("行1").assertExists()
        composeRule.onNodeWithText("行150").assertDoesNotExist()
    }
}
