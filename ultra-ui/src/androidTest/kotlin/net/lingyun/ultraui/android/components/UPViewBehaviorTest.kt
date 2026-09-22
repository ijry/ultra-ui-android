package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.upTestTag
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `up-view`: applies its style props, hosts children and emits click.
 */
@RunWith(AndroidJUnit4::class)
class UPViewBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun appliesHeightHostsContentAndFiresClick() {
        var clicks = 0
        composeRule.setContent {
            UPView(UPViewProps(height = "80px"), onClick = { clicks += 1 }) {
                BasicText("视图内容", modifier = Modifier.upTestTag("view-slot"))
            }
        }

        composeRule.onNodeWithText("视图内容").assertExists()
        composeRule.onNodeWithTag("up-view").assertHeightIsEqualTo(80.dp)
        composeRule.onNodeWithTag("up-view").performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
    }
}
