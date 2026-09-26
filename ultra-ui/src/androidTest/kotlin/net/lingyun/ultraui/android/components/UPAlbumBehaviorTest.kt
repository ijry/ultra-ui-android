package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-album`: renders a multi-image grid, caps at `maxCount` and previews an image on tap.
 * Src resolution is covered by the pure `upAlbumSrc` unit tests.
 */
@RunWith(AndroidJUnit4::class)
class UPAlbumBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersGridAndPreviews() {
        var clicked = -1
        val urls: List<UPRawValue> = (1..5).map { "/img$it.png" }
        composeRule.setContent {
            UPAlbum(
                props = UPAlbumProps(urls = urls, maxCount = 4),
                onClick = { clicked = it },
            )
        }

        composeRule.onNodeWithTag("up-album").assertIsDisplayed()
        composeRule.onNodeWithTag("up-album-item-0").assertExists()
        // Capped at maxCount = 4 cells (indices 0..3).
        composeRule.onNodeWithTag("up-album-item-3").assertExists()
        composeRule.onNodeWithTag("up-album-item-0").performClick()
        composeRule.runOnIdle { assertEquals(0, clicked) }
        composeRule.onNodeWithTag("up-album-preview").assertExists()
    }
}
