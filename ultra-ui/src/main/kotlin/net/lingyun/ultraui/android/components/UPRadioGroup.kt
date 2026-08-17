package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val RadioGroupComponentName = "UPRadioGroup"
private val RadioPlacements = setOf("row", "column")
private val RadioIconPlacements = setOf("left", "right")

/** Native Compose counterpart of uview-plus `u-radio-group`. */
@Composable
public fun UPRadioGroup(
    props: UPRadioGroupProps = UPRadioGroupProps(),
    onInput: ((UPRawValue) -> Unit)? = null,
    onChange: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val placement = upSafeEnum(
        props.placement,
        RadioPlacements,
        "row",
        diagnostics,
        RadioGroupComponentName,
        "placement",
    )
    val iconPlacement = upSafeEnum(
        props.iconPlacement,
        RadioIconPlacements,
        "left",
        diagnostics,
        RadioGroupComponentName,
        "iconPlacement",
    )
    val external = resolveUPModelValue(props.modelValue, props.value)
    var selected by remember(external) { mutableStateOf(external) }
    LaunchedEffect(external) {
        selected = external
    }

    fun toggle(name: UPRawValue) {
        selected = name
        onInput?.invoke(name)
        onChange?.invoke(name)
    }

    val context = UPRadioGroupContext(
        selected = selected,
        disabled = props.disabled,
        shape = props.shape,
        activeColor = props.activeColor,
        inactiveColor = props.inactiveColor,
        size = props.size,
        labelSize = props.labelSize,
        labelColor = props.labelColor,
        labelDisabled = props.labelDisabled,
        iconColor = props.iconColor,
        iconSize = props.iconSize,
        iconPlacement = iconPlacement,
        borderBottom = props.borderBottom,
        toggle = ::toggle,
    )
    val gap = selectionDimension(props.gap, 10.dp)
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, RadioGroupComponentName)
    val root = modifier
        .fillMaxWidth()
        .applyUPResolvedStyle(style)
        .upTestTag("radio-group")

    CompositionLocalProvider(LocalUPRadioGroup provides context) {
        if (placement == "column") {
            Column(
                modifier = root,
                verticalArrangement = Arrangement.spacedBy(gap),
            ) {
                content()
            }
        } else {
            Row(
                modifier = root,
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                with(UPColumnScopeAdapter) { content() }
            }
        }
    }
}

/** Convenience direct-value overload for generated source using a raw model value. */
@Composable
public fun UPRadioGroup(
    value: UPRawValue,
    onInput: ((UPRawValue) -> Unit)? = null,
    onChange: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    UPRadioGroup(
        props = UPRadioGroupProps(modelValue = value),
        content = content,
        onInput = onInput,
        onChange = onChange,
        diagnostics = diagnostics,
        modifier = modifier,
    )
}
