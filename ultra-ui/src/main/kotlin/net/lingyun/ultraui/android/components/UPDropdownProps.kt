package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-dropdown` props. */
public data class UPDropdownProps(
    val activeColor: String = "#2979ff",
    val inactiveColor: String = "#606266",
    val closeOnClickMask: Boolean = true,
    val closeOnClickSelf: Boolean = true,
    val duration: UPRawValue = 300,
    val height: UPRawValue = 40,
    val borderBottom: Boolean = false,
    val titleSize: UPRawValue = 14,
    val borderRadius: UPRawValue = 0,
    val menuIcon: String = "arrow-down",
    val menuIconSize: UPRawValue = 14,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    /** Compatibility aliases for earlier Android templates. */
    val closeOnClickOverlay: Boolean? = null,
    val direction: String? = null,
    val menu: UPRawValue? = null,
    val bgColor: String = "",
)
