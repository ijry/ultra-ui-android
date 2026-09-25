package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-qrcode`: renders the encoded matrix and, when `allowPreview` is set, opens an enlarged preview
 * overlay on tap while emitting the `preview` callback. Matrix correctness itself is covered by the
 * pure `upQrcodeMatrix` unit tests against upstream `qrcode.js`.
 */
@RunWith(AndroidJUnit4::class)
class UPQrcodeBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersAndPreviewsOnTap() {
        var previewed = false
        composeRule.setContent {
            UPQrcode(
                props = UPQrcodeProps(`val` = "https://uview-plus.jiangruyi.com", allowPreview = true),
                onPreview = { previewed = true },
            )
        }

        composeRule.onNodeWithTag("up-qrcode").assertIsDisplayed()
        composeRule.onNodeWithTag("up-qrcode").performClick()
        composeRule.runOnIdle { assertTrue(previewed) }
        composeRule.onNodeWithTag("up-qrcode-preview").assertExists()
    }
}
