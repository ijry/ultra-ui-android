package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPPickerData], mirroring uview-plus `u-picker-data`.
 *
 * A single-column picker form field: a disabled [UPInput] shows the label of the selected option
 * (matched from [modelValue] via [valueKey] -> [labelKey], placeholder [title]); tapping it opens a
 * [UPPicker] whose one column is [options]. Confirming updates the bound value.
 *
 * [description] is declared upstream but never passed to `up-picker`, so it is inert; see docs.
 */
public data class UPPickerDataProps(
    val modelValue: UPRawValue = "",
    val title: String = "",
    val description: String = "",
    val options: List<UPRawValue> = emptyList(),
    val valueKey: String = "id",
    val labelKey: String = "name",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
