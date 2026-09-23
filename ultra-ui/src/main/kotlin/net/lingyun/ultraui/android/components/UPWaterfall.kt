package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upTestTag

/**
 * `getColumnsCount()`: `auto` derives the count from the available width and `minColumnWidth`
 * (7px inter-column gap, clamped to at least `columnsMin`); a numeric value is used directly.
 */
internal fun upWaterfallColumnCount(
    columns: UPRawValue,
    columnsMin: Int,
    minColumnWidth: Int,
    availableWidthPx: Int,
): Int {
    val auto = (columns as? String)?.trim()?.equals("auto", ignoreCase = true) == true
    if (auto) {
        val gap = 7
        var count = maxOf(1, availableWidthPx / (minColumnWidth + gap))
        if (count < columnsMin) count = columnsMin
        return count
    }
    val n = when (columns) {
        is Number -> columns.toInt()
        is String -> columns.trim().toIntOrNull() ?: 2
        else -> 2
    }
    return maxOf(1, n)
}

/**
 * Native Compose counterpart of uview-plus `u-waterfall`.
 *
 * A masonry layout that packs `modelValue` into columns by shortest-column height — a custom
 * [Layout] measures every child at the column width and greedily places each into whichever
 * column is currently shortest, exactly the `getMinHeightColumnIndex` strategy upstream uses.
 * `columns` fixes the count or, when `"auto"`, derives it from the width and `minColumnWidth`.
 * `content` renders each item.
 *
 * Difference: upstream appends items one-by-one every `addTime` ms (a staggered reveal for images
 * still loading); the port lays everything out at once, so `addTime` has no counterpart. `idKey`
 * matters only for upstream's imperative `remove(id)`, which callers drive by editing `modelValue`.
 */
@Composable
public fun UPWaterfall(
    props: UPWaterfallProps = UPWaterfallProps(),
    modifier: Modifier = Modifier,
    columnSpacing: androidx.compose.ui.unit.Dp = 8.dp,
    itemSpacing: androidx.compose.ui.unit.Dp = 8.dp,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable (item: Map<String, UPRawValue>, index: Int) -> Unit,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPWaterfall")
    val density = androidx.compose.ui.platform.LocalDensity.current
    val colSpacingPx = with(density) { columnSpacing.roundToPx() }
    val itemSpacingPx = with(density) { itemSpacing.roundToPx() }

    Layout(
        modifier = modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("waterfall"),
        content = {
            props.modelValue.forEachIndexed { index, raw ->
                content(raw.upStringKeyMapOrEmpty(), index)
            }
        },
    ) { measurables, constraints ->
        val totalWidth = constraints.maxWidth
        val columns = upWaterfallColumnCount(props.columns, (props.columnsMin as? Number)?.toInt() ?: 2, props.minColumnWidth, totalWidth)
        val columnWidth = if (columns > 0) (totalWidth - colSpacingPx * (columns - 1)) / columns else totalWidth
        val childConstraints = constraints.copy(minWidth = 0, maxWidth = columnWidth.coerceAtLeast(0))

        val columnHeights = IntArray(columns)
        val placements = ArrayList<Triple<androidx.compose.ui.layout.Placeable, Int, Int>>(measurables.size)
        measurables.forEach { measurable ->
            val placeable = measurable.measure(childConstraints)
            val target = columnHeights.indices.minByOrNull { columnHeights[it] } ?: 0
            val x = target * (columnWidth + colSpacingPx)
            val y = columnHeights[target]
            placements += Triple(placeable, x, y)
            columnHeights[target] = y + placeable.height + itemSpacingPx
        }
        val layoutHeight = (columnHeights.maxOrNull() ?: 0).let { if (it > 0) it - itemSpacingPx else 0 }
            .coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(totalWidth, layoutHeight) {
            placements.forEach { (placeable, x, y) -> placeable.place(x, y) }
        }
    }
}
