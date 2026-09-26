package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-novel-reader`: renders themed chapter text, toggles the toolbars on tap and opens the settings
 * panel. Theme/paragraph helpers are covered by the pure unit tests.
 */
@RunWith(AndroidJUnit4::class)
class UPNovelReaderBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersChapterTogglesControlsAndOpensSettings() {
        var settings = false
        val chapter = mapOf<String, UPRawValue>(
            "id" to "c1", "index" to 0, "title" to "第一章 开端",
            "content" to "第一段内容。\n第二段内容。",
        )
        val chapters: List<UPRawValue> = listOf(chapter, mapOf<String, UPRawValue>("id" to "c2", "index" to 1, "title" to "第二章"))
        composeRule.setContent {
            UPNovelReader(
                props = UPNovelReaderProps(chapters = chapters, currentChapter = chapter),
                onSettingsChange = { settings = true },
            )
        }

        composeRule.onNodeWithTag("up-novel-reader").assertIsDisplayed()
        composeRule.onNodeWithTag("up-novel-reader-content").assertExists()
        // Tap to reveal toolbars.
        composeRule.onNodeWithTag("up-novel-reader-content").performClick()
        composeRule.onNodeWithTag("up-novel-reader-topbar").assertExists()
        composeRule.onNodeWithText("设置").performClick()
        composeRule.onNodeWithTag("up-novel-reader-settings").assertExists()
        composeRule.onNodeWithText("A+").performClick()
        composeRule.runOnIdle { assertTrue(settings) }
    }
}
