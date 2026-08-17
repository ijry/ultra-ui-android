package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPImageLoaders
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val EmptyComponentName = "UPEmpty"
private val EmptyModes = setOf("car", "page", "search", "address", "wifi", "order", "coupon", "favor", "permission", "history", "news", "message", "list", "data", "comment")
private val EmptyLabels = mapOf(
    "car" to "暂无车辆", "page" to "暂无页面", "search" to "没有搜索结果", "address" to "暂无地址", "wifi" to "网络连接失败",
    "order" to "暂无订单", "coupon" to "暂无优惠券", "favor" to "暂无收藏", "permission" to "暂无权限", "history" to "暂无历史记录",
    "news" to "暂无新闻", "message" to "暂无消息", "list" to "暂无列表", "data" to "暂无数据", "comment" to "暂无评论",
)

/** Native Compose counterpart of uview-plus `u-empty`. */
@Composable
public fun UPEmpty(
    props: UPEmptyProps = UPEmptyProps(),
    modifier: Modifier = Modifier,
    content: (@Composable ColumnScope.() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return
    val mode = upSafeEnum(props.mode, EmptyModes, "data", diagnostics, EmptyComponentName, "mode")
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, EmptyComponentName)
    val iconSize = upRawDp(props.iconSize, 90.dp).coerceAtLeast(0.dp)
    val textSize = upRawDp(props.textSize, 14.dp).coerceAtLeast(0.dp)
    val marginTop = upRawDp(props.marginTop, 0.dp).coerceAtLeast(0.dp)
    val text = props.text.ifEmpty { EmptyLabels[mode].orEmpty() }
    val icon = props.icon.ifEmpty { if (mode == "message") "chat" else "empty-$mode" }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = marginTop)
            .applyUPResolvedStyle(style)
            .upTestTag("empty"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (props.icon.contains('/') || props.icon.endsWith(".png") || props.icon.endsWith(".jpg")) {
            UPImage(
                props = UPImageProps(src = props.icon, width = props.width, height = props.height, mode = "widthFix"),
                loader = UPImageLoaders.Android,
                diagnostics = diagnostics,
            )
        } else {
            UPIcon(UPIconProps(name = icon, size = iconSize.value, color = props.iconColor), diagnostics = diagnostics)
        }
        Spacer(Modifier.height(14.dp))
        if (text.isNotEmpty()) {
            BasicText(
                text,
                style = TextStyle(color = UPColor.parse(props.textColor, UPTheme.Light), fontSize = textSize.value.sp),
            )
        }
        if (content != null) {
            Column(modifier = Modifier.upTestTag("empty-content"), content = content)
        }
    }
}
