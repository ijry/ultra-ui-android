package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-cropper`: renders the crop frame + toolbar and reports the transform on confirm. The pixel
 * crop/export is delegated to the host; scale-bound/quality math is unit-tested.
 */
@RunWith(AndroidJUnit4::class)
class UPCropperBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersFrameAndConfirms() {
        var result: UPCropperResult? = null
        composeRule.setContent {
            UPCropper(
                src = "/sdcard/photo.png",
                props = UPCropperProps(noTab = false, exportWidth = "200px", exportHeight = "200px"),
                onConfirm = { result = it },
            )
        }

        composeRule.onNodeWithTag("up-cropper").assertIsDisplayed()
        composeRule.onNodeWithTag("up-cropper-frame").assertExists()
        composeRule.onNodeWithText("确定").performClick()
        composeRule.runOnIdle {
            assertTrue(result != null)
            assertTrue(result!!.exportWidth > 0f)
        }
    }
}
