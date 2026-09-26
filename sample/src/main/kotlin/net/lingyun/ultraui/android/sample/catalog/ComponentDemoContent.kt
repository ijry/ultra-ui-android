package net.lingyun.ultraui.android.sample.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.components.UPBadge
import net.lingyun.ultraui.android.components.UPBadgeProps
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
import net.lingyun.ultraui.android.components.UPCell
import net.lingyun.ultraui.android.components.UPCellGroup
import net.lingyun.ultraui.android.components.UPCellProps
import net.lingyun.ultraui.android.components.UPCol
import net.lingyun.ultraui.android.components.UPGrid
import net.lingyun.ultraui.android.components.UPGridItem
import net.lingyun.ultraui.android.components.UPIcon
import net.lingyun.ultraui.android.components.UPIconProps
import net.lingyun.ultraui.android.components.UPImage
import net.lingyun.ultraui.android.components.UPImageProps
import net.lingyun.ultraui.android.components.UPLoadingIcon
import net.lingyun.ultraui.android.components.UPLoadingIconProps
import net.lingyun.ultraui.android.components.UPLoadingPage
import net.lingyun.ultraui.android.components.UPLoadingPageProps
import net.lingyun.ultraui.android.components.UPRow
import net.lingyun.ultraui.android.components.UPTag
import net.lingyun.ultraui.android.components.UPTagProps
import net.lingyun.ultraui.android.components.UPText
import net.lingyun.ultraui.android.components.UPTextProps
import net.lingyun.ultraui.android.components.UPTitle
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection

/**
 * 逐组件 demo 内容分发。每个 id 对应上游 `pages/componentsX/<id>` 的独立演示页，
 * 尚未逐页迁移的组件回落到 [PendingDemo]（提示到旧版分组示例查看）。
 */
@Composable
public fun ComponentDemoContent(id: String, onEvent: (String) -> Unit) {
    when (id) {
        "color" -> ColorDemo()
        "icon" -> IconDemo()
        "image" -> ImageDemo()
        "button" -> ButtonDemo(onEvent)
        "text" -> TextDemo(onEvent)
        "layout" -> LayoutDemo()
        "cell" -> CellDemo(onEvent)
        "badge" -> BadgeDemo()
        "tag" -> TagDemo(onEvent)
        "loading-icon" -> LoadingIconDemo()
        "loading-page" -> LoadingPageDemo()
        else -> if (!renderFormGroup(id, onEvent) &&
            !renderDataGroup(id, onEvent) &&
            !renderFeedbackGroup(id, onEvent) &&
            !renderLayoutGroup(id, onEvent) &&
            !renderNavigationGroup(id, onEvent)
        ) {
            PendingDemo()
        }
    }
}

@Composable
private fun ColorDemo() {
    val swatches = listOf(
        "primary" to "#2979ff",
        "success" to "#19be6b",
        "warning" to "#ff9900",
        "error" to "#fa3534",
        "info" to "#909399",
    )
    DemoSection(title = "主题色") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            swatches.forEach { (name, hex) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(width = 60.dp, height = 32.dp)
                            .background(Color(android.graphics.Color.parseColor(hex)), RoundedCornerShape(4.dp)),
                    )
                    Text("$name  $hex", color = UPTheme.Content)
                }
            }
        }
    }
}

@Composable
private fun IconDemo() {
    DemoSection(title = "图标") {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            listOf("photo", "star-fill", "heart-fill", "map", "setting").forEach { n ->
                UPIcon(props = UPIconProps(name = n, size = "28px", color = UPTheme.Primary.toHex()))
            }
        }
    }
}

@Composable
private fun ImageDemo() {
    DemoSection(title = "图片") {
        UPImage(
            props = UPImageProps(
                src = "https://example.com/demo.png",
                width = "120px",
                height = "120px",
                shape = "circle",
            ),
        )
    }
}

@Composable
private fun ButtonDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "按钮") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            UPButton(props = UPButtonProps(text = "主要", type = "primary"), onClick = { onEvent("按钮：primary") })
            UPButton(props = UPButtonProps(text = "成功", type = "success", plain = true), onClick = { onEvent("按钮：success plain") })
            UPButton(props = UPButtonProps(text = "警告", type = "warning", shape = "circle"), onClick = { onEvent("按钮：warning circle") })
        }
    }
}

@Composable
private fun TextDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "文本") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            UPText(props = UPTextProps(text = "普通文本"))
            UPText(props = UPTextProps(text = "主色加粗文本", type = "primary", bold = true), onClick = { onEvent("文本：点击") })
            UPText(props = UPTextProps(text = "13800000000", mode = "phone", type = "primary"))
        }
    }
}

@Composable
private fun LayoutDemo() {
    DemoSection(title = "Grid 布局") {
        UPGrid(col = 3) {
            listOf("一", "二", "三").forEach { t ->
                UPGridItem {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(UPTheme.Light, RoundedCornerShape(6.dp))
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text(t, color = UPTheme.Content) }
                }
            }
        }
    }
}

@Composable
private fun CellDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "单元格") {
        UPCellGroup {
            UPCell(props = UPCellProps(title = "个人信息", value = "查看", isLink = true), onClick = { onEvent("单元格：个人信息") })
            UPCell(props = UPCellProps(title = "地址管理", label = "编辑收货地址", isLink = true), onClick = { onEvent("单元格：地址管理") })
        }
    }
}

@Composable
private fun BadgeDemo() {
    DemoSection(title = "徽标") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            UPBadge(props = UPBadgeProps(value = 8, type = "error"), content = { BadgeAnchor("消息") })
            UPBadge(props = UPBadgeProps(isDot = true, type = "primary"), content = { BadgeAnchor("动态") })
        }
    }
}

@Composable
private fun BadgeAnchor(label: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(UPTheme.Light, RoundedCornerShape(8.dp))
            .border(0.5.dp, UPTheme.Border, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) { Text(label, color = UPTheme.Content) }
}

@Composable
private fun TagDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "标签") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            UPTag(props = UPTagProps(text = "可关闭", type = "success", closable = true, name = "tag"), onClose = { onEvent("标签：关闭 $it") })
            UPTag(props = UPTagProps(text = "plain", type = "primary", plain = true))
        }
    }
}

@Composable
private fun LoadingIconDemo() {
    DemoSection(title = "加载动画") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            UPLoadingIcon(props = UPLoadingIconProps(show = true, mode = "spinner"))
            UPLoadingIcon(props = UPLoadingIconProps(show = true, mode = "circle", color = "#2979ff"))
        }
    }
}

@Composable
private fun LoadingPageDemo() {
    DemoSection(title = "加载页") {
        Box(modifier = Modifier.fillMaxWidth().size(200.dp)) {
            UPLoadingPage(props = UPLoadingPageProps(loading = true, loadingText = "正在加载"))
        }
    }
}

@Composable
private fun PendingDemo() {
    DemoSection(title = "演示迁移中") {
        Text(
            text = "该组件的独立演示页正在从旧版分组示例逐步迁移。可先在首页底部「查看旧版分组示例」中体验。",
            color = UPTheme.Content,
            fontSize = 14.sp,
            lineHeight = 22.sp,
        )
    }
}

private fun Color.toHex(): String {
    val a = (alpha * 255).toInt()
    val r = (red * 255).toInt()
    val g = (green * 255).toInt()
    val b = (blue * 255).toInt()
    return String.format("#%02X%02X%02X%02X", a, r, g, b)
}
