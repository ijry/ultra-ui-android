package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPLoadingIconBehaviorTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadingIconHidesWhenShowIsFalse() {
        composeRule.setContent { UPLoadingIcon(UPLoadingIconProps(show = false)) }

        composeRule.onNodeWithTag("up-loading-icon").assertDoesNotExist()
    }

    @Test
    fun unknownModeFallsBackToSpinner() {
        composeRule.setContent { UPLoadingIcon(UPLoadingIconProps(mode = "not-a-mode")) }

        composeRule
            .onNodeWithTag("up-loading-icon")
            .assertExists()
            .assertContentDescriptionEquals("u-loading-icon: spinner")
    }

    @Test
    fun verticalTextIsBelowTheLoadingGlyph() {
        composeRule.setContent {
            UPLoadingIcon(
                UPLoadingIconProps(
                    mode = "circle",
                    vertical = true,
                    text = "加载中",
                ),
            )
        }

        val spinnerBounds = composeRule.onNodeWithTag("up-loading-icon-spinner").getUnclippedBoundsInRoot()
        val textBounds = composeRule.onNodeWithTag("up-loading-icon-text").getUnclippedBoundsInRoot()
        assertTrue("vertical=true 必须将文字放在加载图标下方", textBounds.top >= spinnerBounds.bottom)
    }
}
