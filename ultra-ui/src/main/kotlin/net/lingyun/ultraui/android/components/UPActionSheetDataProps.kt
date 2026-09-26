package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPActionSheetData], mirroring uview-plus `u-action-sheet-data`.
 *
 * A form-field trigger: a disabled [UPInput] shows the label of the currently selected option
 * (matched from [modelValue] via [valueKey] -> [labelKey], with [title] as placeholder); tapping it
 * opens a [UPActionSheet] of [options] (with [description]) whose selection updates the bound value.
 */
public data class UPActionSheetDataProps(
    val modelValue: UPRawValue = "",
    val title: String = "",
    val description: String = "",
    val options: List<UPRawValue> = emptyList(),
    val valueKey: String = "value",
    val labelKey: String = "name",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
