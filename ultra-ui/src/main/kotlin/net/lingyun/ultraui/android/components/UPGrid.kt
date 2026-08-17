package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag
import net.lingyun.ultraui.android.core.upSafeEnum

private const val GridComponentName = "UPGrid"

/** Native Compose counterpart of uview-plus `u-grid`. */
@Composable
public fun UPGrid(
    props: UPGridProps = UPGridProps(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    val columns = props.col.upIntOrDefault(UPConfig.grid.col.upIntOrDefault(3)).coerceAtLeast(1)
    val gap = upRawDp(props.gap, 0.dp).coerceAtLeast(0.dp)
    val align = upSafeEnum(
        value = props.align,
        allowed = setOf("left", "center", "right"),
        fallback = "left",
        diagnostics = diagnostics,
        component = GridComponentName,
        property = "align",
    )
    val context = remember(columns, gap, props.border) {
        UPGridLayoutContext(columns = columns, gap = gap, border = props.border)
    }
    context.beginComposition()
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, GridComponentName)
    val root = modifier
        .fillMaxWidth()
        .applyUPResolvedStyle(style)
        .upTestTag("grid")

    CompositionLocalProvider(LocalUPGridLayoutContext provides context) {
        Layout(
            modifier = root,
            content = { with(UPRowScopeAdapter) { content() } },
        ) { measurables, constraints ->
            val width = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.minWidth
            val gapPx = gap.roundToPx().coerceAtLeast(0)
            val cellWidth = ((width - gapPx * (columns - 1)).toFloat() / columns)
                .toInt()
                .coerceAtLeast(0)
            val placeables = measurables.map { measurable ->
                measurable.measure(
                    constraints.copy(
                        minWidth = cellWidth,
                        maxWidth = cellWidth,
                        minHeight = 0,
                    ),
                )
            }
            val rowHeights = placeables
                .chunked(columns)
                .map { row -> row.maxOfOrNull { it.height } ?: 0 }
            val height = maxOf(
                constraints.minHeight,
                rowHeights.sum() + gapPx * (rowHeights.size - 1).coerceAtLeast(0),
            )
            layout(width, height) {
                var childIndex = 0
                var y = 0
                rowHeights.forEach { rowHeight ->
                    val rowCount = minOf(columns, placeables.size - childIndex)
                    val rowWidth = rowCount * cellWidth + (rowCount - 1).coerceAtLeast(0) * gapPx
                    val leading = when (align) {
                        "center" -> (width - rowWidth) / 2
                        "right" -> width - rowWidth
                        else -> 0
                    }.coerceAtLeast(0)
                    repeat(rowCount) { columnIndex ->
                        val placeable = placeables[childIndex]
                        val x = leading + columnIndex * (cellWidth + gapPx)
                        val verticalOffset = ((rowHeight - placeable.height) / 2).coerceAtLeast(0)
                        placeable.placeRelative(x, y + verticalOffset)
                        childIndex++
                    }
                    y += rowHeight + gapPx
                }
            }
        }
    }
}

/** Direct argument form for generated Android source. */
@Composable
public fun UPGrid(
    col: UPRawValue = UPConfig.grid.col,
    border: Boolean = UPConfig.grid.border,
    align: String = UPConfig.grid.align,
    gap: UPRawValue = UPConfig.grid.gap,
    customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    UPGrid(
        props = UPGridProps(
            col = col,
            border = border,
            align = align,
            gap = gap,
            customStyle = customStyle,
        ),
        modifier = modifier,
        diagnostics = diagnostics,
        content = content,
    )
}
