package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `u-tabs-pro`.
 *
 * Wraps [UPTabs] and, when `showContent`, renders a content slot below the tab bar that receives
 * the active index/item. The tab styling props forward straight to `u-tabs`; `change`/`click`
 * bubble up as [onChange]/[onClick] and `update:current` as [onUpdateCurrent]. The `content`
 * lambda is handed the current index and the current list item map.
 */
@Composable
public fun UPTabsPro(
    props: UPTabsProProps = UPTabsProProps(),
    modifier: Modifier = Modifier,
    onChange: ((Int) -> Unit)? = null,
    onClick: ((Int) -> Unit)? = null,
    onUpdateCurrent: ((Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable (current: Int, item: Map<String, UPRawValue>?) -> Unit)? = null,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTabsPro")
    val maxIndex = (props.list.size - 1).coerceAtLeast(0)
    var current by remember(props.current, props.list.size) {
        mutableIntStateOf(props.current.upIntOrDefault(0).coerceIn(0, maxIndex))
    }

    Column(modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("tabs-pro")) {
        UPTabs(
            props = UPTabsProps(
                list = props.list,
                keyName = props.keyName,
                current = current,
                lineColor = props.lineColor,
                activeStyle = props.activeStyle,
                inactiveStyle = props.inactiveStyle,
                lineWidth = props.lineWidth,
                lineHeight = props.lineHeight,
                lineBgSize = props.lineBgSize,
                itemStyle = props.itemStyle,
                scrollable = props.scrollable,
                duration = props.duration,
                iconStyle = props.iconStyle,
                shapeMode = props.shapeMode,
            ),
            onChange = {
                current = it
                onChange?.invoke(it)
                onUpdateCurrent?.invoke(it)
            },
            onClick = { onClick?.invoke(it) },
            diagnostics = diagnostics,
        )
        if (props.showContent) {
            val item = props.list.getOrNull(current)?.upStringKeyMapOrEmpty()
            val contentStyle = rememberUPResolvedStyle(props.contentStyle, diagnostics, "UPTabsPro.content")
            Column(Modifier.fillMaxWidth().applyUPResolvedStyle(contentStyle).upTestTag("tabs-pro-content")) {
                content?.invoke(current, item)
            }
        }
    }
}
