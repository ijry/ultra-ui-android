package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import net.lingyun.ultraui.android.core.upTestTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-transition`: mounts and runs the enter lifecycle when `show` flips true, and unmounts after the
 * leave animation once `show` flips false. Effect maths are covered by the pure `upTransitionSpec`
 * unit tests.
 */
@RunWith(AndroidJUnit4::class)
class UPTransitionBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun entersOnShowAndUnmountsAfterLeave() {
        var show by mutableStateOf(false)
        var afterEnter = false
        var afterLeave = false
        composeRule.setContent {
            UPTransition(
                props = UPTransitionProps(show = show, mode = "fade-up", duration = 40),
                onAfterEnter = { afterEnter = true },
                onAfterLeave = { afterLeave = true },
            ) {
                BasicText(text = "内容", modifier = Modifier.upTestTag("transition-body"))
            }
        }

        // Hidden initially (not mounted).
        composeRule.onNodeWithTag("up-transition").assertDoesNotExist()

        show = true
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-transition").assertIsDisplayed()
        composeRule.onNodeWithTag("transition-body").assertExists()
        composeRule.runOnIdle { assertTrue(afterEnter) }

        show = false
        composeRule.waitForIdle()
        composeRule.runOnIdle { assertTrue(afterLeave) }
        composeRule.onNodeWithTag("up-transition").assertDoesNotExist()
    }
}
