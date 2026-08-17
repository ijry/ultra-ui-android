package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-radio` props. */
public data class UPRadioProps(
    val name: UPRawValue = UPConfig.radio.name,
    val shape: String = UPConfig.radio.shape,
    val disabled: UPRawValue = UPConfig.radio.disabled,
    val labelDisabled: UPRawValue = UPConfig.radio.labelDisabled,
    val activeColor: String = UPConfig.radio.activeColor,
    val inactiveColor: String = UPConfig.radio.inactiveColor,
    val iconSize: UPRawValue = UPConfig.radio.iconSize,
    val labelSize: UPRawValue = UPConfig.radio.labelSize,
    val label: UPRawValue = UPConfig.radio.label,
    val size: UPRawValue = UPConfig.radio.size,
    val color: String = UPConfig.radio.color,
    val labelColor: String = UPConfig.radio.labelColor,
    val iconColor: String = UPConfig.radio.iconColor,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
