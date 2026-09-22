package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

/** `bgColors[index]` with the upstream default palette as the safety net for short arrays. */
internal fun upBoxColor(colors: List<String>, index: Int): String =
    colors.getOrNull(index) ?: listOf("#EEFCFF", "#FCF8FF", "#FDF8F2")[index]

/**
 * Native Compose counterpart of uview-plus `up-box`.
 *
 * The layout is a `flex: 1` left panel, a fixed-width `gap`, then a right column holding two
 * `flex: 1` panels separated by a `gap`-tall spacer. Each panel clips to `borderRadius` and
 * paints its `bgColors` entry. When a region slot ([left]/[rightTop]/[rightBottom]) is null the
 * default content renders a 36rpx icon beside the region title, matching the template defaults.
 */
@Composable
public fun UPBox(
    props: UPBoxProps = UPBoxProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    left: (@Composable () -> Unit)? = null,
    rightTop: (@Composable () -> Unit)? = null,
    rightBottom: (@Composable () -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPBox")
    val height = upDimension(props.height, 160.dp)
    val gap = upDimension(props.gap, 15.dp)
    val radius = upDimension(props.borderRadius, 6.dp)
    val shape = RoundedCornerShape(radius)

    @Composable
    fun Panel(panelModifier: Modifier, colorIndex: Int, slot: (@Composable () -> Unit)?, icon: String, title: String, titleSize: Int, tag: String) {
        Box(
            modifier = panelModifier
                .clip(shape)
                .background(UPColor.parse(upBoxColor(props.bgColors, colorIndex), Color.Transparent))
                .upTestTag(tag),
            contentAlignment = Alignment.Center,
        ) {
            if (slot != null) {
                slot()
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon.isNotEmpty()) UPIcon(UPIconProps(name = icon, size = 36), diagnostics = diagnostics)
                    BasicText(title, modifier = Modifier.padding(start = 8.dp), style = TextStyle(color = UPTheme.Main, fontSize = titleSize.sp))
                }
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .applyUPResolvedStyle(style)
            .upTestTag("box")
            .then(if (onClick != null) Modifier.upClickable(onClick = onClick) else Modifier),
    ) {
        Panel(Modifier.weight(1f).fillMaxSize(), 0, left, props.leftIcon, props.leftTitle, 16, "box-left")
        Spacer(Modifier.width(gap))
        Column(modifier = Modifier.weight(1f).fillMaxSize()) {
            Panel(Modifier.weight(1f).fillMaxWidth(), 1, rightTop, props.rightTopIcon, props.rightTopTitle, 15, "box-right-top")
            Spacer(Modifier.height(gap))
            Panel(Modifier.weight(1f).fillMaxWidth(), 2, rightBottom, props.rightBottomIcon, props.rightBottomTitle, 15, "box-right-bottom")
        }
    }
}
