package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull

@Composable
public fun UPBadge(
    props: UPBadgeProps = UPBadgeProps(),
    content: @Composable () -> Unit = {},
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val type = upSafeEnum(props.type, setOf("primary", "success", "warning", "error", "info"), "error", diagnostics, "UPBadge", "type")
    val shapeName = upSafeEnum(props.shape, setOf("circle", "horn", "dot"), "circle", diagnostics, "UPBadge", "shape")
    val numberType = upSafeEnum(props.numberType, setOf("overflow", "ellipsis", "number"), "overflow", diagnostics, "UPBadge", "numberType")
    val value = resolveUPModelValue(props.modelValue, props.value)
    val number = value.upIntOrDefault(0)
    val rawValue = upRawText(value)
    val numericValue = value.asFiniteFloatOrNull()
    // Match uview's `Number(value) === 0 ? showZero : true` rule while
    // retaining non-numeric strings as visible badge labels.
    val isNumericZero = numericValue?.let { it == 0f } ?: rawValue.isEmpty()
    val visible = props.show && (props.isDot || if (isNumericZero) props.showZero else rawValue.isNotEmpty())

    // uview does not render the badge wrapper when `show`/zero visibility
    // rules hide the badge. Preserve slotted content, but avoid exposing a
    // phantom `u-badge` node to accessibility and generated test contracts.
    if (!visible) {
        content()
        return
    }

    Box(modifier = Modifier.upTestTag("badge")) {
        content()
        val base = props.bgColor?.takeIf(String::isNotEmpty)?.let { UPColor.parse(it, upTypeColor(type)) } ?: upTypeColor(type)
        val foreground = props.color?.takeIf(String::isNotEmpty)?.let { UPColor.parse(it, Color.White) } ?: Color.White
        val label = when {
            props.isDot -> ""
            numberType == "ellipsis" -> "…"
            numberType == "number" -> upRawText(value)
            number > props.max -> "${props.max}+"
            else -> upRawText(value)
        }
        val shape = if (shapeName == "circle" || shapeName == "dot") androidx.compose.foundation.shape.RoundedCornerShape(50) else androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
        val badge = Modifier
            .align(if (props.absolute) Alignment.TopEnd else Alignment.CenterEnd)
            .padding(2.dp)
            .background(if (props.inverted) Color.Transparent else base, shape)
            .then(if (props.inverted) Modifier.border(1.dp, base, shape) else Modifier)
            .padding(horizontal = if (props.isDot) 4.dp else 5.dp, vertical = if (props.isDot) 4.dp else 2.dp)
            .upTestTag("badge-label")
        BasicText(label, modifier = badge, style = androidx.compose.ui.text.TextStyle(color = foreground, fontSize = 10.sp, lineHeight = 12.sp))
    }
}
