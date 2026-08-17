package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-checkbox-group` props. */
public data class UPCheckboxGroupProps(
    val name: UPRawValue = UPConfig.checkboxGroup.name,
    val modelValue: UPRawValue = UPConfig.checkboxGroup.modelValue,
    val value: List<UPRawValue> = UPConfig.checkboxGroup.value,
    val shape: String = UPConfig.checkboxGroup.shape,
    val disabled: Boolean = UPConfig.checkboxGroup.disabled,
    val activeColor: String = UPConfig.checkboxGroup.activeColor,
    val inactiveColor: String = UPConfig.checkboxGroup.inactiveColor,
    val size: UPRawValue = UPConfig.checkboxGroup.size,
    val placement: String = UPConfig.checkboxGroup.placement,
    val labelSize: UPRawValue = UPConfig.checkboxGroup.labelSize,
    val labelColor: String = UPConfig.checkboxGroup.labelColor,
    val labelDisabled: Boolean = UPConfig.checkboxGroup.labelDisabled,
    val iconColor: String = UPConfig.checkboxGroup.iconColor,
    val iconSize: UPRawValue = UPConfig.checkboxGroup.iconSize,
    val iconPlacement: String = UPConfig.checkboxGroup.iconPlacement,
    val borderBottom: Boolean = UPConfig.checkboxGroup.borderBottom,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
