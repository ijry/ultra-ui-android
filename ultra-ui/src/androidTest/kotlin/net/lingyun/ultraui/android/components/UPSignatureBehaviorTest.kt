package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-signature`: the pad and toolbar render; clear resets and confirm on an empty pad reports error.
 * (Drawing itself needs real touch input; stroke emptiness is covered by the pure unit test.)
 */
@RunWith(AndroidJUnit4::class)
class UPSignatureBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun toolbarButtonsWireClearAndConfirm() {
        var cleared = 0
        var errored = 0
        composeRule.setContent {
            UPSignature(
                UPSignatureProps(),
                onClear = { cleared += 1 },
                onError = { errored += 1 },
            )
        }

        composeRule.onNodeWithTag("up-signature-canvas").assertExists()
        composeRule.onNodeWithTag("up-signature-clear").performClick()
        composeRule.runOnIdle { assertEquals(1, cleared) }
        // Empty pad: confirm reports an error rather than a bitmap.
        composeRule.onNodeWithTag("up-signature-confirm").performClick()
        composeRule.runOnIdle { assertEquals(1, errored) }
    }

    @Test
    fun toolbarHiddenWhenShowToolbarFalse() {
        composeRule.setContent { UPSignature(UPSignatureProps(showToolbar = false)) }
        composeRule.onNodeWithTag("up-signature-toolbar").assertDoesNotExist()
    }
}
