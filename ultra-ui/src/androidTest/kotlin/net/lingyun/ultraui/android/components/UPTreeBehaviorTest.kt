package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-tree`: expand/collapse a branch and emit node-click. (Flatten correctness is covered by the
 * pure `upTreeFlatten` unit test.)
 */
@RunWith(AndroidJUnit4::class)
class UPTreeBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val data = listOf<Any?>(
        mapOf(
            "id" to "a", "label" to "父节点A",
            "children" to listOf<Any?>(
                mapOf("id" to "a1", "label" to "子节点A1"),
                mapOf("id" to "a2", "label" to "子节点A2"),
            ),
        ),
        mapOf("id" to "b", "label" to "父节点B"),
    )

    @Test
    fun tappingABranchExpandsAndCollapsesIt() {
        val clicks = mutableListOf<String>()
        composeRule.setContent {
            UPTree(UPTreeProps(data = data), onNodeClick = { clicks += it["id"].toString() })
        }

        // Collapsed initially: children hidden.
        composeRule.onNodeWithText("父节点A").assertExists()
        composeRule.onNodeWithText("子节点A1").assertDoesNotExist()

        // Tap the row expands it (expandOnClickNode defaults true) and emits node-click.
        composeRule.onNodeWithTag("up-tree-node-a").performClick()
        composeRule.onNodeWithText("子节点A1").assertExists()
        composeRule.runOnIdle { assertEquals(listOf("a"), clicks) }

        // Tap again collapses.
        composeRule.onNodeWithTag("up-tree-node-a").performClick()
        composeRule.onNodeWithText("子节点A1").assertDoesNotExist()
    }

    @Test
    fun defaultExpandAllShowsEveryNode() {
        composeRule.setContent {
            UPTree(UPTreeProps(data = data, defaultExpandAll = true))
        }

        composeRule.onNodeWithText("子节点A1").assertExists()
        composeRule.onNodeWithText("子节点A2").assertExists()
    }
}
