package net.lingyun.ultraui.android.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

/** Native Compose counterpart of uview-plus `u-line`. */
@Composable
public fun UPLine(
    props: UPLineProps = UPLineProps(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val direction = upSafeEnum(
        value = props.direction,
        allowed = setOf("row", "col"),
        fallback = "row",
        diagnostics = diagnostics,
        component = "UPLine",
        property = "direction",
    )
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPLine")
    val color = UPColor.parse(props.color, net.lingyun.ultraui.android.core.UPTheme.Border)
    val thickness = if (props.hairline) 0.5.dp else 1.dp
    val margin = upRawDp(props.margin, 0.dp).coerceAtLeast(0.dp)
    val percent = props.length.asPercentFraction()
    val root = modifier
        .then(
            if (direction == "row") {
                if (percent != null) Modifier.fillMaxWidth(percent) else Modifier.width(upRawDp(props.length, 0.dp))
            } else {
                if (percent != null) Modifier.fillMaxHeight(percent) else Modifier.height(upRawDp(props.length, 0.dp))
            },
        )
        .then(if (direction == "row") Modifier.height(thickness) else Modifier.width(thickness))
        .padding(if (direction == "row") androidx.compose.foundation.layout.PaddingValues(vertical = margin) else androidx.compose.foundation.layout.PaddingValues(horizontal = margin))
        .applyUPResolvedStyle(style)
        .upTestTag("line")

    Canvas(modifier = root) {
        val pathEffect = if (props.dashed) PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())) else null
        if (direction == "row") {
            val centerY = size.height / 2f
            drawLine(color, Offset(0f, centerY), Offset(size.width, centerY), strokeWidth = thickness.toPx(), pathEffect = pathEffect)
        } else {
            val centerX = size.width / 2f
            drawLine(color, Offset(centerX, 0f), Offset(centerX, size.height), strokeWidth = thickness.toPx(), pathEffect = pathEffect)
        }
    }
}

/** Direct argument form for generated Android source. */
@Composable
public fun UPLine(
    color: String = net.lingyun.ultraui.android.core.UPConfig.line.color,
    length: net.lingyun.ultraui.android.core.UPRawValue = net.lingyun.ultraui.android.core.UPConfig.line.length,
    direction: String = net.lingyun.ultraui.android.core.UPConfig.line.direction,
    hairline: Boolean = net.lingyun.ultraui.android.core.UPConfig.line.hairline,
    margin: net.lingyun.ultraui.android.core.UPRawValue = net.lingyun.ultraui.android.core.UPConfig.line.margin,
    dashed: Boolean = net.lingyun.ultraui.android.core.UPConfig.line.dashed,
    customStyle: net.lingyun.ultraui.android.core.UPStyleInput = emptyMap<String, net.lingyun.ultraui.android.core.UPRawValue>(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPLine(
        props = UPLineProps(color, length, direction, hairline, margin, dashed, customStyle),
        modifier = modifier,
        diagnostics = diagnostics,
    )
}

private fun Any?.asPercentFraction(): Float? {
    val text = this as? String ?: return null
    val trimmed = text.trim()
    if (!trimmed.endsWith('%')) return null
    return trimmed.dropLast(1).toFloatOrNull()?.div(100f)?.takeIf(Float::isFinite)?.coerceIn(0f, 1f)
}
