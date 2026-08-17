package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPMainAxisAlignment
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val RowComponentName = "UPRow"
private const val GridUnits = 12

/** Native Compose counterpart of uview-plus `u-row`. */
@Composable
public fun UPRow(
    props: UPRowProps = UPRowProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    val gutter = upRawDp(props.gutter, 0.dp).coerceAtLeast(0.dp)
    val justify = resolveUPMainAxis(
        value = props.justify,
        fallback = "start",
        diagnostics = diagnostics,
        component = RowComponentName,
        property = "justify",
    )
    val align = resolveUPCrossAxis(
        value = props.align,
        fallback = "center",
        diagnostics = diagnostics,
        component = RowComponentName,
        property = "align",
    )
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, RowComponentName)
    val root = modifier
        .fillMaxWidth()
        .applyUPResolvedStyle(style)
        .upTestTag("row")
        .let { base ->
            if (onClick == null) base else base.upClickable(onClick = onClick)
        }

    CompositionLocalProvider(LocalUPRowLayoutContext provides UPRowLayoutContext(gutter)) {
        Layout(
            modifier = root,
            content = { with(UPRowScopeAdapter) { content() } },
        ) { measurables, constraints ->
            val width = if (constraints.hasBoundedWidth) {
                constraints.maxWidth
            } else {
                constraints.minWidth
            }
            val gutterPx = gutter.roundToPx()
            val childSpecs = measurables.map { measurable ->
                val data = measurable.parentData as? UPColParentData
                val span = (data?.span ?: GridUnits).coerceIn(0, GridUnits)
                val offset = (data?.offset ?: 0).coerceIn(0, GridUnits)
                val widthPx = (width * span / GridUnits).coerceAtLeast(0)
                val offsetPx = (width * offset / GridUnits).coerceAtLeast(0)
                Triple(measurable, widthPx, offsetPx)
            }
            val totalChildWidth = childSpecs.sumOf { it.second + it.third }
            val freeSpace = (width - totalChildWidth).coerceAtLeast(0)
            val (leadingSpace, betweenSpace) = rowSpacing(justify, freeSpace, childSpecs.size)
            val placeables = childSpecs.map { (measurable, childWidth, _) ->
                measurable.measure(
                    constraints.copy(
                        minWidth = childWidth,
                        maxWidth = childWidth,
                        minHeight = 0,
                    ),
                )
            }
            val height = maxOf(constraints.minHeight, placeables.maxOfOrNull { it.height } ?: 0)
            var x = leadingSpace - gutterPx / 2
            layout(width, height) {
                childSpecs.forEachIndexed { index, (_, _, offsetPx) ->
                    x += offsetPx
                    val placeable = placeables[index]
                    val y = when (align) {
                        net.lingyun.ultraui.android.core.UPCrossAxisAlignment.Start -> 0
                        net.lingyun.ultraui.android.core.UPCrossAxisAlignment.End -> height - placeable.height
                        net.lingyun.ultraui.android.core.UPCrossAxisAlignment.Stretch,
                        net.lingyun.ultraui.android.core.UPCrossAxisAlignment.Center -> (height - placeable.height) / 2
                    }.coerceAtLeast(0)
                    placeable.placeRelative(x, y)
                    x += placeable.width + betweenSpace
                }
            }
        }
    }
}

/** Direct argument form for generated Android source. */
@Composable
public fun UPRow(
    gutter: UPRawValue = UPConfig.row.gutter,
    justify: String = UPConfig.row.justify,
    align: String = UPConfig.row.align,
    customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    UPRow(
        props = UPRowProps(
            gutter = gutter,
            justify = justify,
            align = align,
            customStyle = customStyle,
        ),
        modifier = modifier,
        onClick = onClick,
        diagnostics = diagnostics,
        content = content,
    )
}

private fun rowSpacing(
    justify: UPMainAxisAlignment,
    freeSpace: Int,
    childCount: Int,
): Pair<Int, Int> = when {
    childCount <= 0 -> 0 to 0
    justify == UPMainAxisAlignment.End -> freeSpace to 0
    justify == UPMainAxisAlignment.Center -> freeSpace / 2 to 0
    justify == UPMainAxisAlignment.SpaceBetween && childCount > 1 -> 0 to freeSpace / (childCount - 1)
    justify == UPMainAxisAlignment.SpaceAround -> freeSpace / (childCount * 2) to freeSpace / childCount
    justify == UPMainAxisAlignment.SpaceEvenly -> freeSpace / (childCount + 1) to freeSpace / (childCount + 1)
    else -> 0 to 0
}
