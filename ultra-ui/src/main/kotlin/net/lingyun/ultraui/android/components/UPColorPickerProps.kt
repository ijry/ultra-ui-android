package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPColorPicker], mirroring uview-plus `u-color-picker`.
 *
 * A bottom-sheet HSL colour picker. `modelValue` is the current colour (`#rrggbb`). `commonColors`
 * lists preset swatches shown for quick selection.
 */
public data class UPColorPickerProps(
    val modelValue: String = "#ff0000",
    val commonColors: List<UPRawValue> = emptyList(),
    val show: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
