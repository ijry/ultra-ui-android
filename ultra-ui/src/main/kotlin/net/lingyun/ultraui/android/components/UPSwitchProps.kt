package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-switch` props. */
public data class UPSwitchProps(
    val loading: Boolean = UPConfig.switch.loading,
    val disabled: Boolean = UPConfig.switch.disabled,
    val size: UPRawValue = UPConfig.switch.size,
    val activeColor: String = UPConfig.switch.activeColor,
    val inactiveColor: String = UPConfig.switch.inactiveColor,
    val dotActiveColor: String = UPConfig.switch.dotActiveColor,
    val dotInactiveColor: String = UPConfig.switch.dotInactiveColor,
    val modelValue: UPRawValue = UPConfig.switch.modelValue,
    val value: UPRawValue = UPConfig.switch.value,
    val activeValue: UPRawValue = UPConfig.switch.activeValue,
    val inactiveValue: UPRawValue = UPConfig.switch.inactiveValue,
    val asyncChange: Boolean = UPConfig.switch.asyncChange,
    val space: UPRawValue = UPConfig.switch.space,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
