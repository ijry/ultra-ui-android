package net.lingyun.ultraui.android.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
    fun catalogShowsAllChineseRoutesAndRequestedComponentNames() {
        listOf("基础展示", "弹层与内容", "输入与选择", "布局与进度", "原生交互", "导航与更多").forEach { title ->
            composeRule.onNodeWithText(title).assertExists()
        }

        requestedComponentNames.forEach { name ->
            composeRule.onNodeWithText(name).assertExists()
        }
    }

    @Test
    fun catalogNavigatesToEveryNewRouteAndKeepsExistingIconPagesReachable() {
        listOf(
            "基础展示" to "按钮",
            "弹层与内容" to "弹窗",
            "输入与选择" to "输入框",
            "布局与进度" to "行布局",
            "原生交互" to "警告提示",
            "导航与更多" to "导航栏",
        ).forEach { (routeTitle, sectionTitle) ->
            composeRule.onNodeWithText(routeTitle).performClick()
            composeRule.onNodeWithText(sectionTitle).assertIsDisplayed()
            composeRule.onNodeWithText("返回").performClick()
        }

        composeRule.onNodeWithText("图标").performScrollTo().performClick()
        composeRule.onNodeWithText("常用图标").assertIsDisplayed()
        composeRule.onNodeWithText("返回").performClick()

        composeRule.onNodeWithText("加载中图标").performScrollTo().performClick()
        composeRule.onNodeWithText("基本案列").assertIsDisplayed()
    }

    @Test
    fun catalogStartsBelowTheStatusBarSafeArea() {
        val header = composeRule.onNodeWithText("基础展示")

        assertTrue(
            "目录首标题必须位于系统状态栏安全区域下方",
            header.getUnclippedBoundsInRoot().top >= 32.dp,
        )
    }

    private companion object {
        val requestedComponentNames = listOf(
            "按钮", "标签", "徽标", "分割线", "间隔", "线条", "链接", "文本", "标题",
            "遮罩", "弹窗", "模态框", "轻提示", "单元格", "单元格组", "图片", "头像", "头像组", "空状态", "加载页", "加载更多",
            "输入框", "文本域", "搜索框", "验证码输入", "开关", "评分", "步进器", "复选框", "复选框组", "单选框", "单选框组",
            "行布局", "列布局", "栅格", "栅格项", "线性进度", "环形进度",
            "警告提示", "操作菜单", "通知", "返回顶部", "卡片", "折叠面板", "折叠项", "下拉菜单", "下拉项", "滚动通知",
            "导航栏", "迷你导航栏", "状态栏", "底部安全区", "标签页", "标签项", "分段器", "步骤条", "步骤项", "列表", "列表项", "索引列表", "索引项", "索引锚点", "滚动列表", "气泡弹出", "文字提示", "吸顶", "滑动操作", "滑动操作项", "轮播图", "轮播指示器", "骨架屏", "展开阅读", "纵向通知", "横向通知", "数字滚动", "倒计时", "选择器", "选择器列", "分页", "下拉选择",
        )
    }
}
