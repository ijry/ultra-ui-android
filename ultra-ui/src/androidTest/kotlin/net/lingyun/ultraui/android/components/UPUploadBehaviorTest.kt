package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
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
 * `u-upload`: renders the file thumbnails + add button, fires `onAddClick` and `onDelete`. The native
 * picker/upload engine is delegated to the host.
 */
@RunWith(AndroidJUnit4::class)
class UPUploadBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersThumbnailsAddsAndDeletes() {
        var added = false
        var deleted = -1
        val files: List<UPRawValue> = listOf(
            mapOf<String, UPRawValue>("url" to "/a.png"),
            mapOf<String, UPRawValue>("url" to "/b.png", "status" to "uploading"),
        )
        composeRule.setContent {
            UPUpload(
                props = UPUploadProps(fileList = files),
                onAddClick = { added = true },
                onDelete = { deleted = it },
            )
        }

        composeRule.onNodeWithTag("up-upload").assertIsDisplayed()
        composeRule.onNodeWithTag("up-upload-item-0").assertExists()
        composeRule.onNodeWithTag("up-upload-add").performClick()
        composeRule.runOnIdle { assertTrue(added) }
        composeRule.onNodeWithTag("up-upload-delete-1").performClick()
        composeRule.runOnIdle { assertEquals(1, deleted) }
    }
}
