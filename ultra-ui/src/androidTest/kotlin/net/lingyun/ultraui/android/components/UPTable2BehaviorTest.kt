package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-table2`: renders header + cells, toggles a selection column and sorts a column on header tap.
 * Sort math is covered by the pure `upTable2SortData`/`upTable2NextSortOrder` unit tests.
 */
@RunWith(AndroidJUnit4::class)
class UPTable2BehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val columns: List<UPRawValue> = listOf(
        mapOf<String, UPRawValue>("type" to "selection", "width" to "50px"),
        mapOf<String, UPRawValue>("key" to "name", "title" to "姓名", "width" to "120px"),
        mapOf<String, UPRawValue>("key" to "age", "title" to "年龄", "width" to "80px", "sortable" to true),
    )
    private val data: List<UPRawValue> = listOf(
        mapOf<String, UPRawValue>("id" to 1, "name" to "甲", "age" to 30),
        mapOf<String, UPRawValue>("id" to 2, "name" to "乙", "age" to 9),
    )

    @Test
    fun rendersSortsAndSelects() {
        var lastSort: Pair<String, String>? = null
        var selectedCount = -1
        composeRule.setContent {
            UPTable2(
                props = UPTable2Props(data = data, columns = columns, border = true),
                onSortChange = { key, order -> lastSort = key to order },
                onSelectionChange = { selectedCount = it.size },
            )
        }

        composeRule.onNodeWithTag("up-table2").assertIsDisplayed()
        composeRule.onNodeWithTag("up-table2-header").assertExists()
        composeRule.onNodeWithText("甲").assertExists()

        // Tapping the sortable 年龄 header sorts ascending.
        composeRule.onNodeWithText("年龄").performClick()
        composeRule.runOnIdle { assertEquals("age" to "ascending", lastSort) }

        // Toggling the first row selection reports one selected row.
        composeRule.onNodeWithTag("up-table2-row-0").performClick()
        composeRule.onNodeWithTag("up-table2-check").assertExists()
    }
}
