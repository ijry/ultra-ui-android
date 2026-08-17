package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-radio-group` props. */
public data class UPRadioGroupProps(
    val modelValue: UPRawValue = UPConfig.radioGroup.modelValue,
    val value: UPRawValue = UPConfig.radioGroup.value,
    val disabled: Boolean = UPConfig.radioGroup.disabled,
    val shape: String = UPConfig.radioGroup.shape,
    val activeColor: String = UPConfig.radioGroup.activeColor,
    val inactiveColor: String = UPConfig.radioGroup.inactiveColor,
    val name: UPRawValue = UPConfig.radioGroup.name,
    val size: UPRawValue = UPConfig.radioGroup.size,
    val placement: String = UPConfig.radioGroup.placement,
    val label: UPRawValue = UPConfig.radioGroup.label,
    val labelColor: String = UPConfig.radioGroup.labelColor,
    val labelSize: UPRawValue = UPConfig.radioGroup.labelSize,
    val labelDisabled: Boolean = UPConfig.radioGroup.labelDisabled,
    val iconColor: String = UPConfig.radioGroup.iconColor,
    val iconSize: UPRawValue = UPConfig.radioGroup.iconSize,
    val borderBottom: Boolean = UPConfig.radioGroup.borderBottom,
    val iconPlacement: String = UPConfig.radioGroup.iconPlacement,
    val gap: UPRawValue = UPConfig.radioGroup.gap,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
