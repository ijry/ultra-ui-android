package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPBadgeProps(
    val isDot: Boolean = UPConfig.badge.isDot,
    val value: UPRawValue = UPConfig.badge.value,
    val modelValue: UPRawValue? = null,
    val show: Boolean = UPConfig.badge.show,
    val max: Int = UPConfig.badge.max,
    val type: String = UPConfig.badge.type,
    val showZero: Boolean = UPConfig.badge.showZero,
    val bgColor: String? = UPConfig.badge.bgColor,
    val color: String? = UPConfig.badge.color,
    val shape: String = UPConfig.badge.shape,
    val numberType: String = UPConfig.badge.numberType,
    val offset: List<UPRawValue> = UPConfig.badge.offset,
    val inverted: Boolean = UPConfig.badge.inverted,
    val absolute: Boolean = UPConfig.badge.absolute,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
