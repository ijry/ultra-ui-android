package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val BackTopComponentName: String = "UPBackTop"
private val BackTopModes: Set<String> = setOf("circle", "square")

/** Native Compose counterpart of uview-plus `u-back-top`. */
@Composable
public fun UPBackTop(
    props: UPBackTopProps = UPBackTopProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onBackToTop: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val mode = upSafeEnum(props.mode, BackTopModes, "circle", diagnostics, BackTopComponentName, "mode")
    val scrollTop = props.scrollTop.asFiniteFloatOrNull() ?: 0f
    val threshold = props.top.asFiniteFloatOrNull() ?: 400f
    if (scrollTop <= threshold) return

    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, BackTopComponentName)
    val iconColor = UPColor.parse(props.iconStyle["color"].upStringValueOrEmpty(), UPTheme.Tips)
    val iconSize = props.iconStyle["fontSize"].upTextUnitOr(19.sp)
    val shape = if (mode == "circle") RoundedCornerShape(percent = 50) else RoundedCornerShape(4.dp)
    val callback: () -> Unit = {
        onClick?.invoke()
        onBackToTop?.invoke()
    }

    Box(
        modifier = modifier
            .zIndex(props.zIndex.asFiniteFloatOrNull() ?: 9f)
            .padding(end = upRawDp(props.right, 20.dp), bottom = upRawDp(props.bottom, 100.dp))
            .background(Color.White, shape)
            .applyUPResolvedStyle(style)
            .upTestTag("back-top")
            .upClickable(enabled = onClick != null || onBackToTop != null, onClick = callback),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (props.text.isEmpty()) 12.dp else 14.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UPIcon(
                props = UPIconProps(
                    name = props.icon,
                    color = "#%08X".format(iconColor.value.toLong()),
                    size = iconSize,
                ),
                diagnostics = diagnostics,
            )
            if (props.text.isNotEmpty()) {
                BasicText(props.text, style = TextStyle(color = iconColor, fontSize = iconSize))
            }
        }
    }
}

/** Convenience overload for generated source that supplies a scroll position directly. */
@Composable
public fun UPBackTop(
    scrollTop: Any?,
    top: Any? = 400,
    text: String = "",
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPBackTop(
        props = UPBackTopProps(scrollTop = scrollTop, top = top, text = text),
        modifier = modifier,
        onClick = onClick,
        diagnostics = diagnostics,
    )
}
