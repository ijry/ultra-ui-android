package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/** Resolves the (label, index) for [modelValue] by matching [options] on [valueKey] -> [labelKey]. */
internal fun upPickerDataSelection(
    options: List<UPRawValue>,
    modelValue: UPRawValue,
    valueKey: String,
    labelKey: String,
): Pair<String, Int> {
    if (modelValue == null || modelValue.upStringValueOrEmpty().isEmpty()) return "" to -1
    options.forEachIndexed { index, raw ->
        val opt = raw.upStringKeyMapOrEmpty()
        if (opt[valueKey].upLooseEquals(modelValue)) return opt[labelKey].upStringValueOrEmpty() to index
    }
    return "" to -1
}

/**
 * Native Compose counterpart of uview-plus `u-picker-data`.
 *
 * Shows a disabled [UPInput] with the selected option's label (resolved from
 * [`UPPickerDataProps.modelValue`] via [valueKey] -> [labelKey], placeholder [title]); a transparent
 * cover opens a single-column [UPPicker] over [options] (labelled by [labelKey]). Confirming emits
 * [onUpdateModelValue] with the chosen [valueKey] value and updates the shown label, matching upstream;
 * [onConfirm]/[onCancel]/[onClose] mirror the picker events.
 */
@Composable
public fun UPPickerData(
    props: UPPickerDataProps = UPPickerDataProps(),
    modifier: Modifier = Modifier,
    onUpdateModelValue: ((UPRawValue) -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPPickerData")
    var show by remember { mutableStateOf(false) }
    val initial = remember(props.modelValue, props.options) {
        upPickerDataSelection(props.options, props.modelValue, props.valueKey, props.labelKey)
    }
    var current by remember(initial) { mutableStateOf(initial.first) }
    var defaultIndex by remember(initial) { mutableIntStateOf(initial.second) }

    Box(modifier = modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("picker-data")) {
        UPInput(
            props = UPInputProps(
                value = current,
                disabled = true,
                disabledColor = "#ffffff",
                placeholder = props.title,
                border = "none",
            ),
            diagnostics = diagnostics,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .upClickable(onClick = { show = true })
                .upTestTag("picker-data-trigger"),
        )
    }

    UPPicker(
        props = UPPickerProps(
            show = show,
            columns = listOf(props.options),
            keyName = props.labelKey,
            defaultIndex = if (defaultIndex >= 0) listOf(defaultIndex) else emptyList(),
        ),
        onUpdateShow = { show = it },
        onConfirm = { event ->
            show = false
            val picked = event.value.firstOrNull().upStringKeyMapOrEmpty()
            onUpdateModelValue?.invoke(picked[props.valueKey])
            current = picked[props.labelKey].upStringValueOrEmpty()
            defaultIndex = event.indexs.firstOrNull() ?: defaultIndex
            onConfirm?.invoke()
        },
        onCancel = { show = false; onCancel?.invoke() },
        onClose = { onClose?.invoke() },
        diagnostics = diagnostics,
    )
}
