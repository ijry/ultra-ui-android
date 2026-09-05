package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-action-sheet`'s structure and its reachability rules: a header close button, a
 * description divider, dividers only *between* items, a loading item that shows a spinner
 * instead of its name and cannot be picked, and a scrim that only dismisses when
 * `closeOnClickOverlay` allows it while `cancel` always does.
 */
@RunWith(AndroidJUnit4::class)
class UPActionSheetBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val actions: List<UPRawValue> = listOf(
        mapOf("name" to "拍照", "subname" to "使用相机"),
        mapOf("name" to "从相册选择"),
    )

    private fun node(tag: String) = composeRule.onNodeWithTag(tag, useUnmergedTree = true)

    @Test
    fun theHeaderCarriesItsOwnCloseButtonWhichCancels() {
        var cancels = 0
        val updates = mutableListOf<Boolean>()
        composeRule.setContent {
            UPActionSheet(
                props = UPActionSheetProps(show = true, title = "请选择操作", actions = actions),
                onCancel = { cancels += 1 },
                onUpdateShow = { updates += it },
            )
        }

        node("up-action-sheet-close").assertExists()
        composeRule.onNodeWithTag("up-action-sheet-close", useUnmergedTree = true).performClick()
        composeRule.runOnIdle {
            assertEquals(1, cancels)
            assertEquals(listOf(false), updates)
        }
    }

    @Test
    fun aSheetWithoutATitleHasNoHeaderAtAll() {
        composeRule.setContent {
            UPActionSheet(props = UPActionSheetProps(show = true, actions = actions))
        }
        // `v-if="title"` gates the whole header, close button included.
        node("up-action-sheet-header").assertDoesNotExist()
        node("up-action-sheet-close").assertDoesNotExist()
    }

    @Test
    fun dividersSitBetweenItemsAndOneMoreUnderTheDescription() {
        composeRule.setContent {
            UPActionSheet(
                props = UPActionSheetProps(show = true, description = "选择后立即执行", actions = actions),
            )
        }
        // One `u-line` under the description, one between the two items, none after the last.
        composeRule.onAllNodesWithTag("up-line", useUnmergedTree = true).assertCountEquals(2)

        composeRule.setContent {
            UPActionSheet(props = UPActionSheetProps(show = true, actions = actions))
        }
        // Without a description only the between-items divider survives.
        composeRule.onAllNodesWithTag("up-line", useUnmergedTree = true).assertCountEquals(1)
    }

    @Test
    fun aLoadingItemShowsASpinnerInsteadOfItsNameAndCannotBePicked() {
        val picked = mutableListOf<UPRawValue>()
        val loadingActions: List<UPRawValue> = listOf(
            mapOf("name" to "拍照"),
            mapOf("name" to "上传中", "loading" to true),
        )
        composeRule.setContent {
            UPActionSheet(
                props = UPActionSheetProps(show = true, actions = loadingActions),
                onSelect = { picked += it },
            )
        }

        node("up-action-sheet-item-1-loading").assertExists()
        // The spinner replaces the label, so the name is not rendered at all.
        composeRule.onAllNodesWithTag("up-action-sheet-item-1", useUnmergedTree = true).assertCountEquals(1)
        composeRule.onNodeWithTag("up-action-sheet-item-1").performClick()
        composeRule.runOnIdle { assertTrue("a loading item must not report, got $picked", picked.isEmpty()) }
    }

    @Test
    fun theScrimOnlyDismissesWhenItIsAllowedToButCancelAlwaysDoes() {
        val updates = mutableListOf<Boolean>()
        composeRule.setContent {
            UPActionSheet(
                props = UPActionSheetProps(show = true, actions = actions, cancelText = "取消", closeOnClickOverlay = false),
                onUpdateShow = { updates += it },
            )
        }

        composeRule.onNodeWithTag("up-action-sheet-overlay").performClick()
        // `closeHandler()` guards on the flag, so the scrim swallows the tap.
        composeRule.runOnIdle { assertTrue("the scrim must not dismiss, got $updates", updates.isEmpty()) }

        composeRule.onNodeWithTag("up-action-sheet-cancel").performClick()
        // `cancel()` is unconditional.
        composeRule.runOnIdle { assertEquals(listOf(false), updates) }
    }

    @Test
    fun customContentReplacesTheActionListAndClosesOnTap() {
        val picked = mutableListOf<UPRawValue>()
        val updates = mutableListOf<Boolean>()
        composeRule.setContent {
            UPActionSheet(
                props = UPActionSheetProps(show = true, actions = actions),
                onSelect = { picked += it },
                onUpdateShow = { updates += it },
                content = { BasicText("自定义内容") },
            )
        }

        // `v-if="$slots.default"` with the list under `v-else`: the actions are gone.
        node("up-action-sheet-slot").assertExists()
        node("up-action-sheet-item-0").assertDoesNotExist()

        composeRule.onNodeWithTag("up-action-sheet-slot").performClick()
        // `slotClickHandler()` closes when `closeOnClickAction` is set, without selecting.
        composeRule.runOnIdle {
            assertEquals(listOf(false), updates)
            assertTrue("the slot is not an action, got $picked", picked.isEmpty())
        }
    }

    @Test
    fun theCancelRowIsSeparatedByItsOwnGap() {
        composeRule.setContent {
            UPActionSheet(props = UPActionSheetProps(show = true, actions = actions, cancelText = "取消"))
        }

        val gap = node("up-gap").getUnclippedBoundsInRoot()
        val cancel = node("up-action-sheet-cancel").getUnclippedBoundsInRoot()
        // `<u-gap height="6">` sits immediately above the cancel row.
        assertEquals(6f, (gap.bottom - gap.top).value, 1f)
        assertTrue("the gap should precede the cancel row: $gap vs $cancel", gap.bottom.value <= cancel.top.value + 1f)
    }
}
