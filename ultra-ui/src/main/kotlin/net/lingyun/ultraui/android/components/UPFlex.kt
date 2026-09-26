package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPCrossAxisAlignment
import net.lingyun.ultraui.android.core.UPFlexGlass
import net.lingyun.ultraui.android.core.UPMainAxisAlignment
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upGlass
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val FlexComponentName = "UPFlex"

internal fun upFlexIsHorizontal(direction: String): Boolean =
    direction == "row" || direction == "row-reverse"

internal fun upFlexIsReverse(direction: String): Boolean =
    direction == "row-reverse" || direction == "column-reverse"

internal fun resolveFlexCrossAxis(
    value: String,
    diagnostics: UPCompatibilityDiagnostics,
    component: String,
): UPCrossAxisAlignment {
    val normalized = upSafeEnum(
        value = value,
        allowed = setOf("flex-start", "flex-end", "center", "stretch", "baseline"),
        fallback = "stretch",
        diagnostics = diagnostics,
        component = component,
        property = "align",
    )
    return when (normalized) {
        "flex-end" -> UPCrossAxisAlignment.End
        "center" -> UPCrossAxisAlignment.Center
        "stretch" -> UPCrossAxisAlignment.Stretch
        else -> UPCrossAxisAlignment.Start // flex-start, baseline(degraded)
    }
}
private fun horizontalArrangement(main: UPMainAxisAlignment, gap: Dp): Arrangement.Horizontal =
    when (main) {
        UPMainAxisAlignment.SpaceBetween -> Arrangement.SpaceBetween
        UPMainAxisAlignment.SpaceAround -> Arrangement.SpaceAround
        UPMainAxisAlignment.SpaceEvenly -> Arrangement.SpaceEvenly
        UPMainAxisAlignment.Center -> Arrangement.spacedBy(gap, Alignment.CenterHorizontally)
        UPMainAxisAlignment.End -> Arrangement.spacedBy(gap, Alignment.End)
        else -> Arrangement.spacedBy(gap, Alignment.Start)
    }

private fun verticalArrangement(main: UPMainAxisAlignment, gap: Dp): Arrangement.Vertical =
    when (main) {
        UPMainAxisAlignment.SpaceBetween -> Arrangement.SpaceBetween
        UPMainAxisAlignment.SpaceAround -> Arrangement.SpaceAround
        UPMainAxisAlignment.SpaceEvenly -> Arrangement.SpaceEvenly
        UPMainAxisAlignment.Center -> Arrangement.spacedBy(gap, Alignment.CenterVertically)
        UPMainAxisAlignment.End -> Arrangement.spacedBy(gap, Alignment.Bottom)
        else -> Arrangement.spacedBy(gap, Alignment.Top)
    }

private fun rowVerticalAlignment(cross: UPCrossAxisAlignment): Alignment.Vertical =
    when (cross) {
        UPCrossAxisAlignment.Center -> Alignment.CenterVertically
        UPCrossAxisAlignment.End -> Alignment.Bottom
        else -> Alignment.Top // Start + Stretch(degraded)
    }

private fun columnHorizontalAlignment(cross: UPCrossAxisAlignment): Alignment.Horizontal =
    when (cross) {
        UPCrossAxisAlignment.Center -> Alignment.CenterHorizontally
        UPCrossAxisAlignment.End -> Alignment.End
        else -> Alignment.Start
    }

/** Native Compose general flexbox container; distinct from the 12-col UPRow/UPCol. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun UPFlex(
    props: UPFlexProps = UPFlexProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    val horizontal = upFlexIsHorizontal(props.direction)
    if (upFlexIsReverse(props.direction)) {
        diagnostics.report(FlexComponentName, "direction", props.direction, "reverse-degraded-to-base")
    }
    val gap = upRawDp(props.gap, 0.dp).coerceAtLeast(0.dp)
    val main = resolveUPMainAxis(props.justify, "flex-start", diagnostics, FlexComponentName, "justify")
    val cross = resolveFlexCrossAxis(props.align, diagnostics, FlexComponentName)
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, FlexComponentName)
    var root = modifier.applyUPResolvedStyle(style)
    val glass = props.glass
    if (glass != null && glass.enabled) {
        root = root.upGlass(glass, upRawDp(glass.cornerRadius, style.borderRadius ?: 0.dp))
    }
    root = root.upTestTag("flex").let { base ->
        if (onClick == null) base else base.upClickable(onClick = onClick)
    }

    if (props.wrap) {
        if (horizontal) {
            FlowRow(
                modifier = root,
                horizontalArrangement = horizontalArrangement(main, gap),
                verticalArrangement = Arrangement.spacedBy(gap),
            ) { content() }
        } else {
            FlowColumn(
                modifier = root,
                verticalArrangement = verticalArrangement(main, gap),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) { content() }
        }
    } else {
        if (horizontal) {
            Row(
                modifier = root,
                horizontalArrangement = horizontalArrangement(main, gap),
                verticalAlignment = rowVerticalAlignment(cross),
            ) { content() }
        } else {
            Column(
                modifier = root,
                verticalArrangement = verticalArrangement(main, gap),
                horizontalAlignment = columnHorizontalAlignment(cross),
            ) { content() }
        }
    }
}
/** Direct argument form for generated Android source. */
@Composable
public fun UPFlex(
    direction: String = UPConfig.flex.direction,
    justify: String = UPConfig.flex.justify,
    align: String = UPConfig.flex.align,
    wrap: Boolean = UPConfig.flex.wrap,
    gap: UPRawValue = UPConfig.flex.gap,
    glass: UPFlexGlass? = null,
    customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    UPFlex(
        props = UPFlexProps(direction, justify, align, wrap, gap, glass, customStyle),
        modifier = modifier,
        onClick = onClick,
        diagnostics = diagnostics,
        content = content,
    )
}
