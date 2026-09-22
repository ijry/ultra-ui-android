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
 * `u-lazy-load`: renders the image host and echoes `index` on click.
 */
@RunWith(AndroidJUnit4::class)
class UPLazyLoadBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersImageHostAndEchoesIndexOnClick() {
        val clicks = mutableListOf<Any?>()
        composeRule.setContent {
            UPLazyLoad(
                UPLazyLoadProps(index = 3, image = "cover.png"),
                onClick = { clicks += it },
            )
        }

        composeRule.onNodeWithTag("up-lazy-load").assertExists()
        composeRule.onNodeWithTag("up-lazy-load-image").assertExists()
        composeRule.onNodeWithTag("up-lazy-load").performClick()
        composeRule.runOnIdle { assertEquals(listOf<Any?>(3), clicks) }
    }
}
