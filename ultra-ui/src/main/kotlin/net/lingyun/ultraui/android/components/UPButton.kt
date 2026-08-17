package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPCompatibilityEvent
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag
import net.lingyun.ultraui.android.core.upClickable

private const val ButtonComponent = "UPButton"

@Composable
public fun UPButton(
    props: UPButtonProps = UPButtonProps(),
    onClick: (() -> Unit)? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val type = upSafeEnum(props.type, setOf("info", "primary", "error", "warning", "success"), "info", diagnostics, ButtonComponent, "type")
    val size = upSafeEnum(props.size, setOf("large", "normal", "small", "mini"), "normal", diagnostics, ButtonComponent, "size")
    val shapeName = upSafeEnum(props.shape, setOf("circle", "square"), "square", diagnostics, ButtonComponent, "shape")
    val loadingMode = upSafeEnum(props.loadingMode, setOf("spinner", "circle", "semicircle"), "spinner", diagnostics, ButtonComponent, "loadingMode")
    val unsupported = remember(props.openType, props.formType, props.appParameter, props.hoverStopPropagation) {
        buildList {
            if (props.openType.isNotEmpty()) add(UPCompatibilityEvent(ButtonComponent, "openType", props.openType, "Mini-program openType is not available on Android."))
            if (props.formType.isNotEmpty()) add(UPCompatibilityEvent(ButtonComponent, "formType", props.formType, "Form semantics are supplied by generated Android code."))
            if (props.appParameter.isNotEmpty()) add(UPCompatibilityEvent(ButtonComponent, "appParameter", props.appParameter, "appParameter is only supported by mini-program platforms."))
        }
    }
    LaunchedEffect(unsupported, diagnostics) { unsupported.forEach(diagnostics::report) }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, ButtonComponent)
    val available = availableScreenWidth()
    val throttle = props.throttleTime.upLongOrDefault(0L).coerceAtLeast(0L)
    val fill = upTypeColor(type)
    val textColor = when {
        props.color.isNotEmpty() -> UPColor.parse(props.color, ColorWhite)
        props.plain -> fill
        else -> ColorWhite
    }
    val background = when {
        props.plain -> if (props.plain) Color.Transparent else fill
        props.color.isNotEmpty() -> UPColor.parse(props.color, fill)
        else -> fill
    }
    val height = when (size) {
        "large" -> 48.dp
        "small" -> 32.dp
        "mini" -> 24.dp
        else -> 40.dp
    }
    val horizontal = when (size) {
        "large" -> 20.dp
        "small" -> 12.dp
        "mini" -> 8.dp
        else -> 16.dp
    }
    val radius = if (shapeName == "circle") 999.dp else 4.dp
    val shape = RoundedCornerShape(radius)
    val root = modifier
        .defaultMinSize(minWidth = if (size == "mini") 48.dp else 80.dp, minHeight = height)
        .background(background.copy(alpha = if (props.disabled) 0.6f else 1f), shape)
        .then(if (props.hairline || props.plain) Modifier.border(1.dp, fill, shape) else Modifier)
        .padding(horizontal = horizontal, vertical = 0.dp)
        .applyUPResolvedStyle(style)
        .upTestTag("button")
        .upClickable(enabled = !props.disabled && !props.loading && onClick != null, throttleMillis = throttle) { onClick?.invoke() }
    Row(
        modifier = root,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (props.loading) {
            UPLoadingIcon(
                show = true,
                color = textColor.toHexStringOrFallback(),
                mode = loadingMode,
                size = props.loadingSize,
                text = props.loadingText,
                textColor = textColor.toHexStringOrFallback(),
            )
        } else {
            if (props.icon.isNotEmpty()) {
                UPIcon(UPIconProps(name = props.icon, color = props.iconColor.ifEmpty { textColor.toHexStringOrFallback() }, size = 18), modifier = Modifier.padding(end = 5.dp), diagnostics = diagnostics)
            }
            if (content != null) content()
            else BasicText(
                text = upRawText(props.text),
                style = TextStyle(color = textColor, fontSize = when (size) { "large" -> 16.sp; "mini" -> 11.sp; else -> 14.sp }, fontWeight = FontWeight.Normal),
            )
        }
    }
}

@Composable
public fun UPButton(
    text: String,
    type: String = "info",
    size: String = "normal",
    onClick: (() -> Unit)? = null,
) {
    UPButton(props = UPButtonProps(text = text, type = type, size = size), onClick = onClick)
}

private val ColorWhite = androidx.compose.ui.graphics.Color.White
private fun androidx.compose.ui.graphics.Color.toHexStringOrFallback(): String = "#%08X".format(toArgb())
