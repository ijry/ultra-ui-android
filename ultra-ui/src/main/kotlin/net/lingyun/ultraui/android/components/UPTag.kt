package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

@Composable
public fun UPTag(
    props: UPTagProps = UPTagProps(),
    onClick: ((UPRawValue) -> Unit)? = null,
    onClose: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return
    val type = upSafeEnum(props.type, setOf("primary", "success", "warning", "error", "info"), "primary", diagnostics, "UPTag", "type")
    val size = upSafeEnum(props.size, setOf("large", "medium", "mini"), "medium", diagnostics, "UPTag", "size")
    val shapeName = upSafeEnum(props.shape, setOf("circle", "square"), "square", diagnostics, "UPTag", "shape")
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTag")
    val main = upTypeColor(type)
    val bg = if (props.bgColor.isNotEmpty()) UPColor.parse(props.bgColor, main) else main
    val foreground = if (props.color.isNotEmpty()) UPColor.parse(props.color, if (props.plain) main else Color.White) else if (props.plain) main else Color.White
    val borderColor = if (props.borderColor.isNotEmpty()) UPColor.parse(props.borderColor, main) else main
    val shape = if (shapeName == "circle") RoundedCornerShape(50) else RoundedCornerShape(4.dp)
    val textSize = upRawDp(props.textSize, when (size) { "large" -> 15.dp; "mini" -> 10.dp; else -> 12.dp }).value.sp
    val root = Modifier
        .background(if (props.plain) Color.Transparent else bg, shape)
        .border(1.dp, borderColor.copy(alpha = if (props.disabled) .4f else 1f), shape)
        .padding(horizontal = if (size == "mini") 5.dp else 8.dp, vertical = 3.dp)
        .applyUPResolvedStyle(style)
        .upTestTag("tag")
        .upClickable(enabled = !props.disabled && onClick != null) { onClick?.invoke(props.name) }
    Row(modifier = root, horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        if (props.icon.isNotEmpty()) UPIcon(UPIconProps(name = props.icon, color = props.iconColor.ifEmpty { foreground.toHex() }, size = textSize.value), diagnostics = diagnostics)
        BasicText(upRawText(props.text), style = androidx.compose.ui.text.TextStyle(color = foreground.copy(alpha = if (props.disabled) .5f else 1f), fontSize = textSize))
        if (props.closable) {
            UPIcon(UPIconProps(name = "close", color = props.closeColor, size = 14), modifier = Modifier.upClickable(enabled = onClose != null) { onClose?.invoke(props.name) }, diagnostics = diagnostics)
        }
    }
}

private fun Color.toHex(): String = "#%08X".format(toArgb())
