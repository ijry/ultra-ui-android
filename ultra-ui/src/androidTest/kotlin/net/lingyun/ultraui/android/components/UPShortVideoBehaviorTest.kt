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
 * `u-short-video`: renders the tab bar + vertical feed page with its action rail; tapping the stage
 * pauses (emitting videoPause) and the like action fires. Playback is delegated to a host player.
 */
@RunWith(AndroidJUnit4::class)
class UPShortVideoBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersFeedPausesAndLikes() {
        var paused = -1
        var liked = -1
        val videos: List<UPRawValue> = listOf(
            mapOf<String, UPRawValue>("poster" to "/v1.png", "likeCount" to "12", "author" to mapOf<String, UPRawValue>("name" to "作者甲")),
            mapOf<String, UPRawValue>("poster" to "/v2.png", "likeCount" to "34", "author" to mapOf<String, UPRawValue>("name" to "作者乙")),
        )
        composeRule.setContent {
            UPShortVideo(
                props = UPShortVideoProps(videoList = videos),
                onVideoPause = { paused = it },
                onLike = { _, index -> liked = index },
            )
        }

        composeRule.onNodeWithTag("up-short-video").assertIsDisplayed()
        composeRule.onNodeWithTag("up-short-video-item-0").assertExists()
        composeRule.onNodeWithTag("up-short-video-item-0").performClick()
        composeRule.runOnIdle { assertEquals(0, paused) }
        composeRule.onNodeWithTag("up-short-video-like-0").performClick()
        composeRule.runOnIdle { assertEquals(0, liked) }
    }
}
