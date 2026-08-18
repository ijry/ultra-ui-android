package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-dropdown-item` props. */
public data class UPDropdownItemProps(
    val modelValue: UPRawValue? = "",
    val value: UPRawValue = "",
    val title: UPRawValue = "",
    val options: List<UPRawValue> = emptyList(),
    val disabled: Boolean = false,
    val height: UPRawValue = "auto",
    val closeOnClickOverlay: Boolean = true,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    /** Optional compatibility extension for generated multi-select payloads. */
    val multiple: Boolean = false,
)
