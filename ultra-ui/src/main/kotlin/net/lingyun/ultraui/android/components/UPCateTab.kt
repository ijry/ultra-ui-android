package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upListOrEmpty
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `up-cate-tab`.
 *
 * A category browser: a fixed-width left menu of `tabList` next to a scrollable right pane. The
 * active menu row takes `activeColor`, bold, a white background and a 4dp primary bar on its left
 * edge (`.u-cate-tab__item-active::before`). `mode = tab` renders only the active category's
 * content; `mode = follow` stacks every category. Each right card shows the category title then a
 * flow of its `children` (`icon` + `itemKeyName`). Tapping a menu row emits `update:current`.
 *
 * Difference: upstream's `follow` mode two-way-links right-pane scrolling back to the highlighted
 * menu item via IntersectionObserver; the port renders the same stacked content and keeps the menu
 * highlight driven by taps (and the controlled `current`), leaving scroll-spy to the host.
 */
@Composable
public fun UPCateTab(
    props: UPCateTabProps = UPCateTabProps(),
    modifier: Modifier = Modifier,
    onUpdateCurrent: ((Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCateTab")
    val current = props.current.coerceIn(0, (props.tabList.size - 1).coerceAtLeast(0))
    val tabWidth = upDimension(props.tabWidth, 100.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .applyUPResolvedStyle(style)
            .upTestTag("cate-tab"),
    ) {
        // Left menu.
        Column(
            modifier = Modifier
                .width(tabWidth)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .background(UPColor.parse(props.bgColor, Color(0xFFF6F6F6)))
                .upTestTag("cate-tab-menu"),
        ) {
            props.tabList.forEachIndexed { index, raw ->
                val label = raw.upStringKeyMapOrEmpty()[props.tabKeyName].upStringValueOrEmpty()
                val active = index == current
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .then(if (active) Modifier.background(Color.White).leftActiveBar() else Modifier)
                        .upTestTag("cate-tab-item-$index")
                        .upClickable(onClick = { if (index != current) onUpdateCurrent?.invoke(index) }),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            color = if (active) UPColor.parse(props.activeColor, UPTheme.Main) else UPTheme.Content,
                            fontSize = if (active) 15.sp else 13.sp,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                        ),
                    )
                }
            }
        }

        // Right content pane.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
                .upTestTag("cate-tab-content"),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            props.tabList.forEachIndexed { index, raw ->
                if (props.mode == "follow" || (props.mode == "tab" && index == current)) {
                    val map = raw.upStringKeyMapOrEmpty()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(8.dp)
                            .upTestTag("cate-tab-page-$index"),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        BasicText(map[props.tabKeyName].upStringValueOrEmpty(), style = TextStyle(color = UPTheme.Main, fontSize = 14.sp, fontWeight = FontWeight.Medium))
                        map["children"].upListOrEmpty().forEach { child ->
                            val c = child.upStringKeyMapOrEmpty()
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val icon = c["icon"].upStringValueOrEmpty()
                                if (icon.isNotEmpty()) UPImage(UPImageProps(src = icon, width = 30, height = 30), diagnostics = diagnostics)
                                BasicText(c[props.itemKeyName].upStringValueOrEmpty(), style = TextStyle(color = UPTheme.Content, fontSize = 13.sp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/** `.u-cate-tab__item-active::before`: a 4dp primary bar centred on the left edge. */
private fun Modifier.leftActiveBar(): Modifier = drawBehind {
    val barWidth = 4.dp.toPx()
    val barHeight = 16.dp.toPx()
    val top = (size.height - barHeight) / 2f
    drawRect(
        color = UPTheme.Primary,
        topLeft = Offset(0f, top),
        size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
    )
}
