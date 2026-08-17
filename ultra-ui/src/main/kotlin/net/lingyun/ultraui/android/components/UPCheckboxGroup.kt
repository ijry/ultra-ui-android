package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
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
import net.lingyun.ultraui.android.core.upListOrEmpty
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val CheckboxGroupComponentName = "UPCheckboxGroup"
private val CheckboxPlacements = setOf("row", "column")
private val CheckboxIconPlacements = setOf("left", "right")

/** Native Compose counterpart of uview-plus `u-checkbox-group`. */
@Composable
public fun UPCheckboxGroup(
    props: UPCheckboxGroupProps = UPCheckboxGroupProps(),
    onInput: ((List<UPRawValue>) -> Unit)? = null,
    onChange: ((List<UPRawValue>) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val placement = upSafeEnum(
        props.placement,
        CheckboxPlacements,
        "row",
        diagnostics,
        CheckboxGroupComponentName,
        "placement",
    )
    val iconPlacement = upSafeEnum(
        props.iconPlacement,
        CheckboxIconPlacements,
        "left",
        diagnostics,
        CheckboxGroupComponentName,
        "iconPlacement",
    )
    val external = resolveUPModelValue(props.modelValue, props.value).upListOrEmpty()
    var selected by remember(external) { mutableStateOf(external.toList()) }
    LaunchedEffect(external) {
        selected = external.toList()
    }

    fun toggle(name: UPRawValue) {
        val next = if (selected.any { selectionRawEquals(it, name) }) {
            selected.filterNot { selectionRawEquals(it, name) }
        } else {
            selected + name
        }
        selected = next
        onInput?.invoke(next)
        onChange?.invoke(next)
    }

    val context = UPCheckboxGroupContext(
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
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, CheckboxGroupComponentName)
    val root = modifier
        .fillMaxWidth()
        .applyUPResolvedStyle(style)
        .upTestTag("checkbox-group")

    CompositionLocalProvider(LocalUPCheckboxGroup provides context) {
        if (placement == "column") {
            Column(
                modifier = root,
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                content()
            }
        } else {
            Row(
                modifier = root,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                with(UPColumnScopeAdapter) { content() }
            }
        }
    }
}

/** Convenience direct-value overload for generated source using a list model. */
@Composable
public fun UPCheckboxGroup(
    value: List<UPRawValue>,
    onInput: ((List<UPRawValue>) -> Unit)? = null,
    onChange: ((List<UPRawValue>) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    UPCheckboxGroup(
        props = UPCheckboxGroupProps(modelValue = value),
        content = content,
        onInput = onInput,
        onChange = onChange,
        diagnostics = diagnostics,
        modifier = modifier,
    )
}
