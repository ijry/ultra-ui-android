package net.lingyun.ultraui.android.sample

import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SampleCatalogTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun catalogShowsGroupsButNoUnimplementedComponentRoutes() {
        composeRule.onNodeWithText("Components A").assertExists()
        composeRule.onNodeWithText("Components B").assertExists()
        composeRule.onNodeWithText("Components C").assertExists()
        composeRule.onNodeWithText("按钮").assertDoesNotExist()
    }

    @Test
    fun catalogStartsBelowTheStatusBarSafeArea() {
        val header = composeRule.onNodeWithText("Components A")

        assertTrue(
            "目录首标题必须位于系统状态栏安全区域下方",
            header.getUnclippedBoundsInRoot().top >= 32.dp,
        )
    }
}
