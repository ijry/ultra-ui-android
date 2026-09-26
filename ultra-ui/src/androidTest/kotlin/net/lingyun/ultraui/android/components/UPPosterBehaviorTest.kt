package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-poster`: renders the poster composition from `json` (container + absolutely-positioned views),
 * including a text view and a qrcode view. Gradient/font helpers are covered by unit tests.
 */
@RunWith(AndroidJUnit4::class)
class UPPosterBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersPosterViewsFromJson() {
        val json: Map<String, UPRawValue> = mapOf(
            "css" to mapOf<String, UPRawValue>("width" to "600rpx", "height" to "800rpx", "background" to "#ffffff"),
            "views" to listOf<UPRawValue>(
                mapOf<String, UPRawValue>("type" to "text", "text" to "海报标题", "css" to mapOf<String, UPRawValue>("left" to "40rpx", "top" to "40rpx", "color" to "#303133", "fontSize" to "40rpx")),
                mapOf<String, UPRawValue>("type" to "qrcode", "text" to "https://x.com", "css" to mapOf<String, UPRawValue>("left" to "40rpx", "top" to "200rpx", "width" to "160rpx", "height" to "160rpx")),
            ),
        )
        composeRule.setContent {
            UPPoster(props = UPPosterProps(json = json))
        }

        composeRule.onNodeWithTag("up-poster").assertIsDisplayed()
        composeRule.onNodeWithText("海报标题").assertExists()
        composeRule.onNodeWithTag("up-qrcode").assertExists()
    }
}
