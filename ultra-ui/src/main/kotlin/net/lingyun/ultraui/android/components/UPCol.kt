package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

private const val ColComponentName = "UPCol"
private const val GridUnits = 12

/** Native Compose counterpart of uview-plus `u-col`. */
@Composable
public fun UPCol(
    props: UPColProps = UPColProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable ColumnScope.() -> Unit,
) {
    val span = props.span.upIntOrDefault(UPConfig.col.span.upIntOrDefault(GridUnits)).coerceIn(0, GridUnits)
    val offset = props.offset.upIntOrDefault(UPConfig.col.offset.upIntOrDefault(0)).coerceIn(0, GridUnits)
    val justify = resolveUPMainAxis(
        value = props.justify,
        fallback = "start",
        diagnostics = diagnostics,
        component = ColComponentName,
        property = "justify",
    )
    val align = resolveUPCrossAxis(
        value = props.align,
        fallback = "stretch",
        diagnostics = diagnostics,
        component = ColComponentName,
        property = "align",
    )
    val textAlign = resolveUPTextAlign(props.textAlign, diagnostics, ColComponentName)
    val horizontalAlignment = when (textAlign) {
        "center" -> Alignment.CenterHorizontally
        "right" -> Alignment.End
        else -> when (justify) {
            net.lingyun.ultraui.android.core.UPMainAxisAlignment.Center,
            net.lingyun.ultraui.android.core.UPMainAxisAlignment.SpaceAround,
            net.lingyun.ultraui.android.core.UPMainAxisAlignment.SpaceEvenly -> Alignment.CenterHorizontally
            net.lingyun.ultraui.android.core.UPMainAxisAlignment.End -> Alignment.End
            else -> Alignment.Start
        }
    }
    val verticalArrangement = when (align) {
        net.lingyun.ultraui.android.core.UPCrossAxisAlignment.End -> Arrangement.Bottom
        net.lingyun.ultraui.android.core.UPCrossAxisAlignment.Center -> Arrangement.Center
        else -> Arrangement.Top
    }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, ColComponentName)
    val gutter = LocalUPRowLayoutContext.current?.gutter ?: 0.dp
    val halfGutter = gutter / 2f
    val root = modifier
        .fillMaxWidth()
        .then(UPColParentDataModifier(span, offset))
        .padding(horizontal = halfGutter)
        .applyUPResolvedStyle(style)
        .upTestTag("col-$span-$offset")
        .let { base ->
            if (onClick == null) base else base.upClickable(onClick = onClick)
        }

    Column(
        modifier = root,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
    ) {
        with(UPColumnLayoutScopeAdapter) { content() }
    }
}

/** Direct argument form for generated Android source. */
@Composable
public fun UPCol(
    span: UPRawValue = UPConfig.col.span,
    offset: UPRawValue = UPConfig.col.offset,
    justify: String = UPConfig.col.justify,
    align: String = UPConfig.col.align,
    textAlign: String = UPConfig.col.textAlign,
    customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable ColumnScope.() -> Unit,
) {
    UPCol(
        props = UPColProps(
            span = span,
            offset = offset,
            justify = justify,
            align = align,
            textAlign = textAlign,
            customStyle = customStyle,
        ),
        modifier = modifier,
        onClick = onClick,
        diagnostics = diagnostics,
        content = content,
    )
}
