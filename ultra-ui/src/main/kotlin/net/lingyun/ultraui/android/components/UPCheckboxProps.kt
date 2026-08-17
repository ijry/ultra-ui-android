package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-checkbox` props. */
public data class UPCheckboxProps(
    val name: UPRawValue = UPConfig.checkbox.name,
    val shape: String = UPConfig.checkbox.shape,
    val size: UPRawValue = UPConfig.checkbox.size,
    val checked: Boolean = UPConfig.checkbox.checked,
    val disabled: UPRawValue = UPConfig.checkbox.disabled,
    val activeColor: String = UPConfig.checkbox.activeColor,
    val inactiveColor: String = UPConfig.checkbox.inactiveColor,
    val iconSize: UPRawValue = UPConfig.checkbox.iconSize,
    val iconColor: String = UPConfig.checkbox.iconColor,
    val label: UPRawValue = UPConfig.checkbox.label,
    val labelSize: UPRawValue = UPConfig.checkbox.labelSize,
    val labelColor: String = UPConfig.checkbox.labelColor,
    val labelDisabled: UPRawValue = UPConfig.checkbox.labelDisabled,
    val usedAlone: Boolean = UPConfig.checkbox.usedAlone,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
