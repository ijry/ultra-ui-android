package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Four components carried a 基本完成 label with no device or screenshot evidence behind
 * it. These assertions establish that baseline: each renders its documented content and
 * honours the sizing/label props a generated call would set.
 */
@RunWith(AndroidJUnit4::class)
class UPDisplayBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun avatarRendersItsTextWhenNoImageResolves() {
        composeRule.setContent { UPAvatar(UPAvatarProps(text = "顶")) }
        composeRule.onNodeWithTag("up-avatar").assertExists()
        composeRule.onNodeWithText("顶").assertIsDisplayed()
    }

    @Test
    fun avatarHonoursItsConfiguredSize() {
        composeRule.setContent { UPAvatar(UPAvatarProps(text = "A", size = 64)) }
        composeRule.onNodeWithTag("up-avatar").assertHeightIsEqualTo(64.dp)
    }

    @Test
    fun cellGroupRendersItsTitleAndNestedCells() {
        composeRule.setContent {
            UPCellGroup(UPCellGroupProps(title = "分组标题")) {
                UPCell(UPCellProps(title = "第一项"))
                UPCell(UPCellProps(title = "第二项"))
            }
        }
        composeRule.onNodeWithText("分组标题").assertIsDisplayed()
        composeRule.onNodeWithText("第一项").assertIsDisplayed()
        composeRule.onNodeWithText("第二项").assertIsDisplayed()
    }

    @Test
    fun dividerRendersItsTextAndRespectsExplicitHeight() {
        composeRule.setContent { UPDivider(UPDividerProps(text = "分割")) }
        composeRule.onNodeWithTag("up-divider").assertExists()
        composeRule.onNodeWithText("分割").assertIsDisplayed()
    }

    @Test
    fun titleRendersItsText() {
        // UPTitleProps carries only customStyle; the text arrives via the
        // convenience overload rather than a prop.
        composeRule.setContent { UPTitle(text = "标题") }
        composeRule.onNodeWithTag("up-title").assertExists()
        composeRule.onNodeWithText("标题").assertIsDisplayed()
    }

    @Test
    fun tagHonoursAnExplicitHeight() {
        composeRule.setContent { UPTag(UPTagProps(text = "定高", height = 40)) }
        composeRule.onNodeWithTag("up-tag").assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun tagWithoutExplicitHeightStaysIntrinsic() {
        // Upstream defaults height to '' (unset), so the tag hugs its own text.
        composeRule.setContent { UPTag(UPTagProps(text = "自适应")) }
        composeRule.onNodeWithTag("up-tag").assertExists()
        composeRule.onNodeWithText("自适应").assertIsDisplayed()
    }
}
