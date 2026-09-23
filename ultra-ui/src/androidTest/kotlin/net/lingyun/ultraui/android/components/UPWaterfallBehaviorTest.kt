package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.upTestTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-waterfall`: lays out every item once, packing them by shortest column. (Column packing math
 * is covered by the pure `upWaterfallColumnCount` unit test.)
 */
@RunWith(AndroidJUnit4::class)
class UPWaterfallBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersEveryItem() {
        val items = (1..5).map { mapOf("id" to it, "label" to "卡片$it", "h" to it) }
        composeRule.setContent {
            UPWaterfall(UPWaterfallProps(modelValue = items, columns = 2), modifier = Modifier.fillMaxWidth()) { item, _ ->
                val h = (item["h"] as Int) * 20
                BasicText(
                    item["label"].toString(),
                    modifier = Modifier.fillMaxWidth().height(h.dp).upTestTag("waterfall-cell"),
                )
            }
        }

        composeRule.onNodeWithTag("up-waterfall").assertExists()
        composeRule.onNodeWithText("卡片1").assertExists()
        composeRule.onNodeWithText("卡片5").assertExists()
    }
}
