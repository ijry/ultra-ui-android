package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertCountEquals
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-table` + `u-tr`/`u-th`/`u-td`: renders a header row and data rows with the expected cells.
 */
@RunWith(AndroidJUnit4::class)
class UPTableBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersHeaderAndDataCells() {
        composeRule.setContent {
            UPTable(props = UPTableProps()) {
                UPTr(modifier = androidx.compose.ui.Modifier) {
                    UPTh("学校")
                    UPTh("城市")
                }
                UPTr(modifier = androidx.compose.ui.Modifier) {
                    UPTd("浙江大学")
                    UPTd("杭州")
                }
                UPTr(modifier = androidx.compose.ui.Modifier) {
                    UPTd("清华大学")
                    UPTd("北京")
                }
            }
        }

        composeRule.onNodeWithTag("up-table").assertExists()
        composeRule.onNodeWithText("学校").assertExists()
        composeRule.onNodeWithText("浙江大学").assertExists()
        composeRule.onNodeWithText("北京").assertExists()
        // Two header cells and four data cells.
        composeRule.onAllNodesWithTag("up-th").assertCountEquals(2)
        composeRule.onAllNodesWithTag("up-td").assertCountEquals(4)
    }
}
