package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val LoadmoreComponentName = "UPLoadmore"

/** Native Compose counterpart of uview-plus `u-loadmore`. */
@Composable
public fun UPLoadmore(
    props: UPLoadmoreProps = UPLoadmoreProps(),
    modifier: Modifier = Modifier,
    onLoadmore: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val status = upSafeEnum(props.status, setOf("loadmore", "loading", "nomore"), "loadmore", diagnostics, LoadmoreComponentName, "status")
    val top = upRawDp(props.marginTop, 10.dp).coerceAtLeast(0.dp)
    val bottom = upRawDp(props.marginBottom, 10.dp).coerceAtLeast(0.dp)
    val fontSize = upRawDp(props.fontSize, 14.dp).coerceAtLeast(0.dp)
    val iconSize = upRawDp(props.iconSize, 17.dp).coerceAtLeast(0.dp)
    val height = props.height.toString().takeUnless { it == "auto" }?.let { upRawDp(it, 0.dp) }
    val text = when {
        status == "loading" -> props.loadingText
        status == "nomore" && props.isDot -> "●"
        status == "nomore" -> props.nomoreText
        else -> props.loadmoreText
    }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, LoadmoreComponentName)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (height != null && height > 0.dp) Modifier.height(height) else Modifier)
            .padding(top = top, bottom = bottom)
            .background(UPColor.parse(props.bgColor, Color.Transparent))
            .applyUPResolvedStyle(style)
            .upTestTag("loadmore")
            .upClickable(enabled = status == "loadmore", onClick = { onLoadmore?.invoke() }),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (props.line) LoadmoreLine(props.lineColor, props.dashed)
        Row(
            modifier = Modifier.padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (status == "loading" && props.icon) {
                UPLoadingIcon(
                    props = UPLoadingIconProps(show = true, mode = props.loadingIcon, size = iconSize, color = props.iconColor),
                    diagnostics = diagnostics,
                )
            }
            BasicText(
                text,
                style = TextStyle(
                    color = UPColor.parse(props.color, UPTheme.Content),
                    fontSize = if (props.isDot && status == "nomore") 15.sp else fontSize.value.sp,
                ),
            )
        }
        if (props.line) LoadmoreLine(props.lineColor, props.dashed)
    }
}

@Composable
private fun LoadmoreLine(color: String, dashed: Boolean) {
    Box(
        modifier = Modifier
            .width(70.dp)
            .height(if (dashed) 1.dp else 0.5.dp)
            .background(UPColor.parse(color, UPTheme.Border))
            .upTestTag("loadmore-line"),
    )
}
