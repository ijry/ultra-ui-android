package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/** Resolves the display label for [modelValue] by matching [options] on [valueKey] -> [labelKey]. */
internal fun upActionSheetDataLabel(
    options: List<UPRawValue>,
    modelValue: UPRawValue,
    valueKey: String,
    labelKey: String,
): String {
    if (modelValue == null || modelValue.upStringValueOrEmpty().isEmpty()) return ""
    for (raw in options) {
        val opt = raw.upStringKeyMapOrEmpty()
        if (opt[valueKey].upLooseEquals(modelValue)) return opt[labelKey].upStringValueOrEmpty()
    }
    return ""
}

/**
 * Native Compose counterpart of uview-plus `u-action-sheet-data`.
 *
 * Shows a disabled [UPInput] displaying the selected option's label (resolved from
 * [`UPActionSheetDataProps.modelValue`] via [valueKey] -> [labelKey], placeholder [title]); a
 * transparent cover opens a [UPActionSheet] of [options]. Selecting an action emits
 * [onUpdateModelValue] with its [valueKey] value and updates the shown label, matching upstream.
 */
@Composable
public fun UPActionSheetData(
    props: UPActionSheetDataProps = UPActionSheetDataProps(),
    modifier: Modifier = Modifier,
    onUpdateModelValue: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPActionSheetData")
    var show by remember { mutableStateOf(false) }
    var current by remember(props.modelValue, props.options) {
        mutableStateOf(upActionSheetDataLabel(props.options, props.modelValue, props.valueKey, props.labelKey))
    }

    Box(modifier = modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("action-sheet-data")) {
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
                .upTestTag("action-sheet-data-trigger"),
        )
    }

    UPActionSheet(
        props = UPActionSheetProps(
            show = show,
            actions = props.options,
            title = props.title,
            description = props.description,
            nameKey = props.labelKey,
            safeAreaInsetBottom = true,
        ),
        onUpdateShow = { show = it },
        onClose = { show = false },
        onSelect = { item ->
            val opt = item.upStringKeyMapOrEmpty()
            onUpdateModelValue?.invoke(opt[props.valueKey])
            current = opt[props.labelKey].upStringValueOrEmpty()
        },
        diagnostics = diagnostics,
    )
}
