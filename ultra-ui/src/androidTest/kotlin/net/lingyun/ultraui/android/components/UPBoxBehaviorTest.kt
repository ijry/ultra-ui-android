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
 * `up-box`: the three-panel layout, its default region titles, slot overrides and click.
 */
@RunWith(AndroidJUnit4::class)
class UPBoxBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersThreeRegionsWithDefaultTitlesAndFixedHeight() {
        composeRule.setContent { UPBox(UPBoxProps(height = "200px")) }

        composeRule.onNodeWithTag("up-box").assertHeightIsEqualTo(200.dp)
        composeRule.onNodeWithTag("up-box-left").assertExists()
        composeRule.onNodeWithTag("up-box-right-top").assertExists()
        composeRule.onNodeWithTag("up-box-right-bottom").assertExists()
        composeRule.onNodeWithText("左").assertExists()
        composeRule.onNodeWithText("右上").assertExists()
        composeRule.onNodeWithText("右下").assertExists()
    }

    @Test
    fun regionSlotsReplaceDefaultContentAndClickFires() {
        var clicked = 0
        composeRule.setContent {
            UPBox(
                UPBoxProps(),
                onClick = { clicked += 1 },
                left = { BasicText("自定义左", modifier = Modifier.upTestTag("box-slot-left")) },
            )
        }

        composeRule.onNodeWithTag("up-box-slot-left").assertExists()
        // The default left title is gone once the slot takes over.
        composeRule.onNodeWithText("左").assertDoesNotExist()
        composeRule.onNodeWithTag("up-box").performClick()
        composeRule.runOnIdle { assertEquals(1, clicked) }
    }
}
