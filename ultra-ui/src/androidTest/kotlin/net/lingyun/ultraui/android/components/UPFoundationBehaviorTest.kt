package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPFoundationBehaviorTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun badgeZeroIsHiddenUnlessShowZeroIsEnabled() {
        var showZero by mutableStateOf(false)
        composeRule.setContent { UPBadge(UPBadgeProps(value = 0, showZero = showZero)) }
        composeRule.onNodeWithTag("up-badge").assertDoesNotExist()

        composeRule.runOnIdle { showZero = true }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-badge").assertExists()
    }

    @Test
    fun tagEmitsItsNameWhenClicked() {
        var clicked: Any? = null
        composeRule.setContent {
            UPTag(UPTagProps(text = "标签", name = "tag-1"), onClick = { clicked = it })
        }
        composeRule.onNodeWithTag("up-tag").performClick()
        composeRule.runOnIdle { assertEquals("tag-1", clicked) }
    }

    @Test
    fun linkAndTextExposeClickableRoots() {
        var linkClicks = 0
        var textClicks = 0
        composeRule.setContent {
            Column {
                UPLink(UPLinkProps(text = "链接"), onClick = { linkClicks++ })
                UPText(UPTextProps(text = "正文"), onClick = { textClicks++ })
            }
        }
        composeRule.onNodeWithTag("up-link").performClick()
        composeRule.onNodeWithTag("up-text").performClick()
        composeRule.runOnIdle {
            assertEquals(1, linkClicks)
            assertEquals(1, textClicks)
        }
    }

    @Test
    fun titleKeepsPrefixAndLineHasStableTags() {
        composeRule.setContent {
            UPTitle { androidx.compose.foundation.text.BasicText("标题") }
            UPLine()
        }
        composeRule.onNodeWithTag("up-title-prefix").assertExists()
        composeRule.onNodeWithTag("up-line").assertExists()
    }
}
