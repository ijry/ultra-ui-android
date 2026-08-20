package net.lingyun.ultraui.android.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPTheme

/** A completed sample page that can be shown from the catalog. */
public data class SampleDestination(
    val route: String,
    val group: String,
    val title: String,
    val components: List<String> = emptyList(),
)

/** Stable route identifiers reserved for the first UltraUI Android milestone. */
public object SampleRoutes {
    public const val Catalog: String = "catalog"
    public const val Foundation: String = "foundation"
    public const val LayerContent: String = "layer-content"
    public const val InputSelection: String = "input-selection"
    public const val LayoutProgress: String = "layout-progress"
    public const val NativeInteraction: String = "native-interaction"
    public const val NavigationMore: String = "navigation-more"
    public const val Icon: String = "icon"
    public const val LoadingIcon: String = "loading-icon"
}

public val foundationComponentNames: List<String> = listOf(
    "按钮",
    "标签",
    "徽标",
    "分割线",
    "间隔",
    "线条",
    "链接",
    "文本",
    "标题",
)

public val layerContentComponentNames: List<String> = listOf(
    "遮罩",
    "弹窗",
    "模态框",
    "轻提示",
    "单元格",
    "单元格组",
    "图片",
    "头像",
    "头像组",
    "空状态",
    "加载页",
    "加载更多",
)

public val inputSelectionComponentNames: List<String> = listOf(
    "输入框",
    "文本域",
    "搜索框",
    "验证码输入",
    "开关",
    "评分",
    "步进器",
    "复选框",
    "复选框组",
    "单选框",
    "单选框组",
)

public val layoutProgressComponentNames: List<String> = listOf(
    "行布局",
    "列布局",
    "栅格",
    "栅格项",
    "线性进度",
    "环形进度",
)

public val nativeInteractionComponentNames: List<String> = listOf(
    "警告提示",
    "操作菜单",
    "通知",
    "返回顶部",
    "卡片",
    "折叠面板",
    "折叠项",
    "下拉菜单",
    "下拉项",
    "滚动通知",
)

public val navigationMoreComponentNames: List<String> = listOf(
    "导航栏", "迷你导航栏", "状态栏", "底部安全区", "标签页", "标签项", "分段器", "步骤条", "步骤项",
    "列表", "列表项", "索引列表", "索引项", "索引锚点", "滚动列表", "气泡弹出", "文字提示", "吸顶",
    "滑动操作", "滑动操作项", "轮播图", "轮播指示器", "骨架屏", "展开阅读", "纵向通知", "横向通知",
    "数字滚动", "倒计时", "选择器", "选择器列", "分页", "下拉选择",
)

/** The full public sample catalog for generated uview-plus compatible Android components. */
public val sampleDestinations: List<SampleDestination> = listOf(
    SampleDestination(
        route = SampleRoutes.Foundation,
        group = "组件总览",
        title = "基础展示",
        components = foundationComponentNames,
    ),
    SampleDestination(
        route = SampleRoutes.LayerContent,
        group = "组件总览",
        title = "弹层与内容",
        components = layerContentComponentNames,
    ),
    SampleDestination(
        route = SampleRoutes.InputSelection,
        group = "组件总览",
        title = "输入与选择",
        components = inputSelectionComponentNames,
    ),
    SampleDestination(
        route = SampleRoutes.LayoutProgress,
        group = "组件总览",
        title = "布局与进度",
        components = layoutProgressComponentNames,
    ),
    SampleDestination(
        route = SampleRoutes.NativeInteraction,
        group = "组件总览",
        title = "原生交互",
        components = nativeInteractionComponentNames,
    ),
    SampleDestination(
        route = SampleRoutes.NavigationMore,
        group = "组件总览",
        title = "导航与更多",
        components = navigationMoreComponentNames,
    ),
    SampleDestination(route = SampleRoutes.Icon, group = "独立示例", title = "图标", components = listOf("图标")),
    SampleDestination(route = SampleRoutes.LoadingIcon, group = "独立示例", title = "加载中图标", components = listOf("加载中图标")),
)

private val sampleGroups: List<String> = listOf("组件总览", "独立示例")

/** The home page shared by all deterministic UltraUI component demos. */
@Composable
public fun SampleCatalog(
    destinations: List<SampleDestination> = sampleDestinations,
    onDestinationClick: (SampleDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(UPTheme.Background)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        sampleGroups.forEach { group ->
            Text(
                text = group,
                color = UPTheme.Tips,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            destinations.filter { it.group == group }.forEach { destination ->
                SampleDestinationRow(destination = destination, onClick = { onDestinationClick(destination) })
            }
        }
    }
}

@Composable
private fun SampleDestinationRow(destination: SampleDestination, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.ui.graphics.Color.White)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = destination.title,
            color = UPTheme.Main,
            fontWeight = FontWeight.SemiBold,
        )
        if (destination.components.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                destination.components.chunked(4).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { component ->
                            Text(text = component, color = UPTheme.Content)
                        }
                    }
                }
            }
        }
        HorizontalDivider(color = UPTheme.Border)
    }
}
