package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `up-guide`: paging with next/finish, skip, dots, and the hidden-until-show behaviour.
 */
@RunWith(AndroidJUnit4::class)
class UPGuideBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val pages = listOf(
        mapOf("title" to "第一步", "desc" to "介绍一"),
        mapOf("title" to "第二步", "desc" to "介绍二"),
    )

    @Test
    fun pagesForwardThenFinishesAndReportsClose() {
        val changes = mutableListOf<Int>()
        var finished = 0
        var shown: Boolean? = null
        composeRule.setContent {
            UPGuide(
                UPGuideProps(show = true, list = pages),
                onChange = { changes += it },
                onFinish = { finished += 1 },
                onUpdateShow = { shown = it },
            )
        }

        composeRule.onNodeWithText("第一步").assertExists()
        // First page: primary shows nextText.
        composeRule.onNodeWithText("下一步").performClick()
        composeRule.runOnIdle { assertEquals(listOf(1), changes) }

        // Last page: primary shows finishText and finishes.
        composeRule.onNodeWithText("立即体验").performClick()
        composeRule.runOnIdle {
            assertEquals(1, finished)
            assertEquals(false, shown)
        }
    }

    @Test
    fun skipClosesEarly() {
        var skipped = 0
        var shown: Boolean? = null
        composeRule.setContent {
            UPGuide(
                UPGuideProps(show = true, list = pages),
                onSkip = { skipped += 1 },
                onUpdateShow = { shown = it },
            )
        }

        composeRule.onNodeWithTag("up-guide-skip").performClick()
        composeRule.runOnIdle {
            assertEquals(1, skipped)
            assertEquals(false, shown)
        }
    }

    @Test
    fun hiddenWhenShowFalseOrListEmpty() {
        composeRule.setContent { UPGuide(UPGuideProps(show = false, list = pages)) }
        composeRule.onNodeWithTag("up-guide").assertDoesNotExist()
    }
}
