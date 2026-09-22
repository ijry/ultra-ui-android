package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.upTestTag
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `up-copy`: a successful copy reports the resolved notice and fires `success`; empty content
 * skips the copy and reports the empty outcome. The default slot shows the copy label.
 */
@RunWith(AndroidJUnit4::class)
class UPCopyBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun copyReportsSuccessWithNoticeAndFiresSuccess() {
        val results = mutableListOf<Triple<UPCopyResult, String, String>>()
        var success = 0
        composeRule.setContent {
            UPCopy(
                UPCopyProps(content = "hello", notice = "已复制"),
                onSuccess = { success += 1 },
                onResult = { r, n, s -> results += Triple(r, n, s) },
            ) { BasicText("复制", modifier = Modifier.upTestTag("copy-slot")) }
        }

        composeRule.onNodeWithTag("up-copy-slot").assertExists()
        composeRule.onNodeWithTag("up-copy").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(Triple(UPCopyResult.Success, "已复制", "toast")), results)
            assertEquals(1, success)
        }
    }

    @Test
    fun emptyContentReportsEmptyAndDoesNotFireSuccess() {
        val results = mutableListOf<UPCopyResult>()
        var success = 0
        composeRule.setContent {
            UPCopy(
                UPCopyProps(content = ""),
                onSuccess = { success += 1 },
                onResult = { r, _, _ -> results += r },
            )
        }

        // Default slot renders the copy label.
        composeRule.onNodeWithText("复制").assertExists()
        composeRule.onNodeWithTag("up-copy").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(UPCopyResult.Empty), results)
            assertEquals(0, success)
        }
    }
}
