package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-canvas`: renders a drawing surface, fires `ready` once laid out and executes the `onDraw` lambda.
 */
@RunWith(AndroidJUnit4::class)
class UPCanvasBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersSurfaceAndFiresReadyAndDraws() {
        var ready = false
        var drawn = false
        composeRule.setContent {
            UPCanvas(
                props = UPCanvasProps(width = 120, height = 120),
                onReady = { ready = true },
                onDraw = {
                    drawRect(color = Color.Red, topLeft = Offset.Zero, size = size)
                    drawn = true
                },
            )
        }

        composeRule.onNodeWithTag("up-canvas").assertIsDisplayed()
        composeRule.runOnIdle {
            assertTrue(ready)
            assertTrue(drawn)
        }
    }
}
