package net.lingyun.ultraui.android.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

@Composable
public fun UPDivider(
    props: UPDividerProps = UPDividerProps(),
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val position = upSafeEnum(props.textPosition, setOf("left", "center", "right"), "center", diagnostics, "UPDivider", "textPosition")
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPDivider")
    val color = UPColor.parse(props.lineColor, net.lingyun.ultraui.android.core.UPTheme.Border)
    val thickness = if (props.hairline) .5.dp else 1.dp
    @Composable fun Rule(modifier: Modifier) {
        Canvas(modifier = modifier.height(thickness).upTestTag("divider-line")) {
            drawLine(color, androidx.compose.ui.geometry.Offset.Zero, androidx.compose.ui.geometry.Offset(size.width, 0f), strokeWidth = thickness.toPx(), pathEffect = if (props.dashed) PathEffect.dashPathEffect(floatArrayOf(6f, 4f)) else null)
        }
    }
    Row(modifier = Modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("divider"), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val hasText = props.text.isNotEmpty() || props.dot
        if (!hasText || position != "left") Rule(Modifier.weight(1f))
        if (hasText) {
            if (props.dot) Box(Modifier.width(4.dp).height(4.dp).background(color, androidx.compose.foundation.shape.CircleShape))
            if (props.text.isNotEmpty()) BasicText(props.text, style = androidx.compose.ui.text.TextStyle(color = UPColor.parse(props.textColor, net.lingyun.ultraui.android.core.UPTheme.Tips), fontSize = upRawDp(props.textSize, 14.dp).value.sp))
        }
        if (!hasText || position != "right") Rule(Modifier.weight(1f))
    }
}
