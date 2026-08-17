package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-rate` props. */
public data class UPRateProps(
    val modelValue: UPRawValue = UPConfig.rate.modelValue,
    val value: UPRawValue = UPConfig.rate.value,
    val count: UPRawValue = UPConfig.rate.count,
    val disabled: Boolean = UPConfig.rate.disabled,
    val readonly: Boolean = UPConfig.rate.readonly,
    val size: UPRawValue = UPConfig.rate.size,
    val inactiveColor: String = UPConfig.rate.inactiveColor,
    val activeColor: String = UPConfig.rate.activeColor,
    val gutter: UPRawValue = UPConfig.rate.gutter,
    val minCount: UPRawValue = UPConfig.rate.minCount,
    val allowHalf: Boolean = UPConfig.rate.allowHalf,
    val activeIcon: String = UPConfig.rate.activeIcon,
    val inactiveIcon: String = UPConfig.rate.inactiveIcon,
    val touchable: Boolean = UPConfig.rate.touchable,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
