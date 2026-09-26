package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-barcode`: renders the encoded bars plus the human-readable text, and falls back to the upstream
 * error panel (emitting `error`) on invalid input. Encoding itself is covered by the pure
 * `upBarcodeEncode` unit tests against upstream.
 */
@RunWith(AndroidJUnit4::class)
class UPBarcodeBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersBarsAndText() {
        composeRule.setContent {
            UPBarcode(props = UPBarcodeProps(value = "Hello123", format = "CODE128"))
        }
        composeRule.onNodeWithTag("up-barcode").assertIsDisplayed()
        composeRule.onNodeWithTag("up-barcode-text").assertExists()
    }

    @Test
    fun invalidValueShowsErrorPanelAndEmits() {
        var errored = false
        composeRule.setContent {
            UPBarcode(
                props = UPBarcodeProps(value = "12", format = "EAN13"),
                onError = { errored = true },
            )
        }
        composeRule.onNodeWithTag("up-barcode-error").assertExists()
        composeRule.runOnIdle { assertTrue(errored) }
    }
}
