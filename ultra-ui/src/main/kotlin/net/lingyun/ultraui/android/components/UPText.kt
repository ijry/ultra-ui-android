package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPCompatibilityEvent
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

/** Native formatted text counterpart of uview-plus `u-text`. */
@Composable
public fun UPText(
    props: UPTextProps = UPTextProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onOpen: ((String) -> Unit)? = null,
    onCall: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return
    val mode = upSafeEnum(
        props.mode.ifEmpty { "text" },
        setOf("text", "price", "phone", "name", "date", "link"),
        "text",
        diagnostics,
        "UPText",
        "mode",
    )
    val align = upSafeEnum(props.align, setOf("left", "center", "right"), "left", diagnostics, "UPText", "align")
    val decoration = upSafeEnum(props.decoration, setOf("none", "underline", "line-through"), "none", diagnostics, "UPText", "decoration")
    val wordWrap = upSafeEnum(props.wordWrap, setOf("normal", "break-word", "anywhere"), "normal", diagnostics, "UPText", "wordWrap")
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPText")
    LaunchedEffect(props.openType, diagnostics) {
        if (props.openType.isNotEmpty()) diagnostics.report(
            UPCompatibilityEvent("UPText", "openType", props.openType, "Mini-program openType is not available on Android."),
        )
    }
    val raw = upRawText(props.text)
    val formatted = formatUPText(raw, mode, props.format)
    val color = style.color ?: when {
        props.color.isNotEmpty() -> UPColor.parse(props.color, net.lingyun.ultraui.android.core.UPTheme.Content)
        props.type.isNotEmpty() -> upTypeColor(props.type, net.lingyun.ultraui.android.core.UPTheme.Content)
        else -> net.lingyun.ultraui.android.core.UPTheme.Content
    }
    val fontSize = (style.fontSize ?: upRawDp(props.size, 15.dp)).value.sp
    val lineHeight = props.lineHeight.takeIf { it != "normal" && it != "" }?.let { upRawDp(it, 0.dp) }?.takeIf { it > 0.dp }?.value?.sp
    val maxLines = props.lines.upIntOrDefault(Int.MAX_VALUE).let { if (it <= 0) Int.MAX_VALUE else it }
    val margin = upRawDp(props.margin, 0.dp)
    val root = modifier
        .then(if (props.block || props.flex1) Modifier.fillMaxWidth() else Modifier)
        .padding(margin)
        .applyUPResolvedStyle(style)
        .upTestTag("text")
        .upClickable(enabled = onClick != null || ((mode == "link") && onOpen != null) || ((mode == "phone") && props.call && onCall != null)) {
            onClick?.invoke()
            when (mode) {
                "link" -> if (props.href.isNotEmpty()) onOpen?.invoke(props.href)
                "phone" -> if (props.call) onCall?.invoke(raw)
            }
        }
    Row(
        modifier = root,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (props.prefixIcon.isNotEmpty()) {
            UPIcon(UPIconProps(name = props.prefixIcon, color = color.toUPHex(), customStyle = props.iconStyle), diagnostics = diagnostics)
        }
        BasicText(
            text = formatted,
            style = TextStyle(
                color = color,
                fontSize = fontSize,
                lineHeight = lineHeight ?: TextStyle.Default.lineHeight,
                fontWeight = style.fontWeight ?: if (props.bold) FontWeight.Bold else FontWeight.Normal,
                textAlign = style.textAlign ?: when (align) { "center" -> TextAlign.Center; "right" -> TextAlign.End; else -> TextAlign.Start },
                textDecoration = when (decoration) { "underline" -> TextDecoration.Underline; "line-through" -> TextDecoration.LineThrough; else -> TextDecoration.None },
            ),
            maxLines = maxLines,
            overflow = if (maxLines != Int.MAX_VALUE) TextOverflow.Ellipsis else TextOverflow.Clip,
            softWrap = wordWrap != "normal" || true,
            modifier = Modifier.then(if (props.block || props.flex1) Modifier.weight(1f, fill = false) else Modifier),
        )
        if (props.suffixIcon.isNotEmpty()) {
            UPIcon(UPIconProps(name = props.suffixIcon, color = color.toUPHex(), customStyle = props.iconStyle), diagnostics = diagnostics)
        }
    }
}

/** Direct argument form for generated Android source. */
@Composable
@Suppress("LongParameterList")
public fun UPText(
    text: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.text.text,
    type: String = net.lingyun.ultraui.android.core.UPConfig.text.type,
    show: Boolean = net.lingyun.ultraui.android.core.UPConfig.text.show,
    prefixIcon: String = net.lingyun.ultraui.android.core.UPConfig.text.prefixIcon,
    suffixIcon: String = net.lingyun.ultraui.android.core.UPConfig.text.suffixIcon,
    mode: String = net.lingyun.ultraui.android.core.UPConfig.text.mode,
    href: String = net.lingyun.ultraui.android.core.UPConfig.text.href,
    format: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.text.format,
    call: Boolean = net.lingyun.ultraui.android.core.UPConfig.text.call,
    openType: String = net.lingyun.ultraui.android.core.UPConfig.text.openType,
    bold: Boolean = net.lingyun.ultraui.android.core.UPConfig.text.bold,
    block: Boolean = net.lingyun.ultraui.android.core.UPConfig.text.block,
    lines: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.text.lines,
    color: String = net.lingyun.ultraui.android.core.UPConfig.text.color,
    size: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.text.size,
    iconStyle: net.lingyun.ultraui.android.core.UPStyleInput = net.lingyun.ultraui.android.core.UPConfig.text.iconStyle,
    decoration: String = net.lingyun.ultraui.android.core.UPConfig.text.decoration,
    margin: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.text.margin,
    lineHeight: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.text.lineHeight,
    align: String = net.lingyun.ultraui.android.core.UPConfig.text.align,
    wordWrap: String = net.lingyun.ultraui.android.core.UPConfig.text.wordWrap,
    flex1: Boolean = net.lingyun.ultraui.android.core.UPConfig.text.flex1,
    customStyle: net.lingyun.ultraui.android.core.UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onOpen: ((String) -> Unit)? = null,
    onCall: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPText(
        props = UPTextProps(type, show, text, prefixIcon, suffixIcon, mode, href, format, call, openType, bold, block, lines, color, size, iconStyle, decoration, margin, lineHeight, align, wordWrap, flex1, customStyle),
        modifier = modifier,
        onClick = onClick,
        onOpen = onOpen,
        onCall = onCall,
        diagnostics = diagnostics,
    )
}

@Suppress("UNCHECKED_CAST")
private fun formatUPText(raw: String, mode: String, format: UPRawValue): String {
    val transformed = (format as? Function1<*, *>)?.let { formatter ->
        (formatter as? (String) -> Any?)?.invoke(raw)?.toString()
    } ?: raw
    return when (mode) {
        "price" -> if (transformed.startsWith("¥")) transformed else "¥$transformed"
        "phone" -> if (transformed.length >= 7) transformed.take(3) + "****" + transformed.takeLast(4) else transformed
        "name" -> when (transformed.length) {
            0, 1 -> transformed
            2 -> transformed.take(1) + "*"
            else -> transformed.take(1) + "*".repeat(transformed.length - 2) + transformed.takeLast(1)
        }
        else -> transformed
    }
}

private fun androidx.compose.ui.graphics.Color.toUPHex(): String = "#%08X".format(toArgb())
