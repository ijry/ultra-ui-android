package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upTestTag

/** Resolved table styling shared with cells via [LocalUPTable]. */
internal data class UPTableContext(
    val borderColor: Color,
    val align: TextAlign,
    val fontSizeSp: Float,
    val color: Color,
    val paddingHorizontal: Float,
    val paddingVertical: Float,
    val thStyle: net.lingyun.ultraui.android.core.UPStyleInput,
)

internal val LocalUPTable = compositionLocalOf<UPTableContext?> { null }

/** `align` string → Compose [TextAlign], defaulting to centre like upstream. */
internal fun upTableAlign(align: String): TextAlign = when (align.trim().lowercase()) {
    "left", "start" -> TextAlign.Start
    "right", "end" -> TextAlign.End
    else -> TextAlign.Center
}

private fun upTablePxFont(fontSize: String, fallback: Float): Float {
    val n = fontSize.trim().removeSuffix("px").removeSuffix("rpx").toFloatOrNull() ?: return fallback
    return n
}

/** Parses a CSS-like `padding` shorthand into (vertical, horizontal) dp; `5px 3px` -> (5, 3). */
internal fun upTablePadding(padding: String): Pair<Float, Float> {
    val parts = padding.trim().split(Regex("\\s+")).mapNotNull { it.removeSuffix("px").removeSuffix("rpx").toFloatOrNull() }
    return when (parts.size) {
        0 -> 5f to 3f
        1 -> parts[0] to parts[0]
        else -> parts[0] to parts[1]
    }
}

/**
 * Native Compose counterpart of uview-plus `u-table`.
 *
 * The HTML-like table: a `u-table` frame (top + left 1px border, `bgColor` fill) whose `u-tr` rows
 * hold `u-td`/`u-th` cells that each draw their bottom + right border, so the seams render as
 * single 1px lines. Cell alignment/font/colour/border default from the table via [LocalUPTable];
 * a cell can override its own `textAlign`/`fontSize`/`color`/`borderColor`. `content` holds the rows.
 */
@Composable
public fun UPTable(
    props: UPTableProps = UPTableProps(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTable")
    val borderColor = UPColor.parse(props.borderColor, UPTheme.Border)
    val (padV, padH) = upTablePadding(props.padding)
    val context = UPTableContext(
        borderColor = borderColor,
        align = upTableAlign(props.align),
        fontSizeSp = upTablePxFont(props.fontSize, 14f),
        color = UPColor.parse(props.color, UPTheme.Content),
        paddingHorizontal = padH,
        paddingVertical = padV,
        thStyle = props.thStyle,
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(UPColor.parse(props.bgColor, Color.White))
            .drawBehind {
                val stroke = 1.dp.toPx()
                // top
                drawLine(borderColor, Offset(0f, stroke / 2f), Offset(size.width, stroke / 2f), stroke)
                // left
                drawLine(borderColor, Offset(stroke / 2f, 0f), Offset(stroke / 2f, size.height), stroke)
            }
            .applyUPResolvedStyle(style)
            .upTestTag("table"),
    ) {
        CompositionLocalProvider(LocalUPTable provides context) {
            content()
        }
    }
}

/** Native Compose counterpart of `u-tr`: a flex row of cells. */
@Composable
public fun UPTr(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(modifier.fillMaxWidth().upTestTag("tr"), content = content)
}

private fun Modifier.cellBorders(color: Color): Modifier = this.drawBehind {
    val stroke = 1.dp.toPx()
    // bottom
    drawLine(color, Offset(0f, size.height - stroke / 2f), Offset(size.width, size.height - stroke / 2f), stroke)
    // right
    drawLine(color, Offset(size.width - stroke / 2f, 0f), Offset(size.width - stroke / 2f, size.height), stroke)
}

/** Native Compose counterpart of `u-td`. Use inside a [UPTr] row. */
@Composable
public fun RowScope.UPTd(
    props: UPTdProps = UPTdProps(),
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable () -> Unit)? = null,
) {
    val table = LocalUPTable.current
    val borderColor = if (props.borderColor.isNotEmpty()) UPColor.parse(props.borderColor, UPTheme.Border) else table?.borderColor ?: UPTheme.Border
    val align = if (props.textAlign.isNotEmpty()) upTableAlign(props.textAlign) else table?.align ?: TextAlign.Center
    val widthMod = if (props.width != "auto" && props.width.isNotEmpty()) Modifier.width(upTablePxFont(props.width, 0f).dp) else Modifier.weight(1f)
    Box(
        modifier = widthMod
            .cellBorders(borderColor)
            .padding(horizontal = (table?.paddingHorizontal ?: 3f).dp, vertical = (table?.paddingVertical ?: 5f).dp)
            .upTestTag("td"),
        contentAlignment = when (align) { TextAlign.Start -> Alignment.CenterStart; TextAlign.End -> Alignment.CenterEnd; else -> Alignment.Center },
    ) {
        if (content != null) content() else Unit
    }
}

/** Native Compose counterpart of `u-td` with a plain text label. */
@Composable
public fun RowScope.UPTd(text: String, props: UPTdProps = UPTdProps()) {
    val table = LocalUPTable.current
    val fontSize = if (props.fontSize.isNotEmpty()) upTablePxFont(props.fontSize, 14f) else table?.fontSizeSp ?: 14f
    val color = if (props.color.isNotEmpty()) UPColor.parse(props.color, UPTheme.Content) else table?.color ?: UPTheme.Content
    UPTd(props = props) {
        BasicText(text, style = TextStyle(color = color, fontSize = fontSize.sp))
    }
}

/** Native Compose counterpart of `u-th`: a bold header cell on the header background. */
@Composable
public fun RowScope.UPTh(
    props: UPThProps = UPThProps(),
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable () -> Unit)? = null,
) {
    val table = LocalUPTable.current
    val borderColor = table?.borderColor ?: UPTheme.Border
    val align = table?.align ?: TextAlign.Center
    val widthMod = if (props.width.isNotEmpty()) Modifier.width(upTablePxFont(props.width, 0f).dp) else Modifier.weight(1f)
    val thResolved = rememberUPResolvedStyle(table?.thStyle ?: emptyMap<String, net.lingyun.ultraui.android.core.UPRawValue>(), diagnostics, "UPTh.thStyle")
    Box(
        modifier = widthMod
            .background(Color(0xFFF5F6F8))
            .cellBorders(borderColor)
            .applyUPResolvedStyle(thResolved)
            .padding(horizontal = (table?.paddingHorizontal ?: 3f).dp, vertical = (table?.paddingVertical ?: 5f).dp)
            .upTestTag("th"),
        contentAlignment = when (align) { TextAlign.Start -> Alignment.CenterStart; TextAlign.End -> Alignment.CenterEnd; else -> Alignment.Center },
    ) {
        if (content != null) content() else Unit
    }
}

/** Native Compose counterpart of `u-th` with a plain bold text label. */
@Composable
public fun RowScope.UPTh(text: String, props: UPThProps = UPThProps()) {
    UPTh(props = props) {
        BasicText(text, style = TextStyle(color = UPTheme.Main, fontSize = 14.sp, fontWeight = FontWeight.Bold))
    }
}
