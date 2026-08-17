package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-empty` props. */
public data class UPEmptyProps(
    val icon: String = UPConfig.empty.icon,
    val text: String = UPConfig.empty.text,
    val textColor: String = UPConfig.empty.textColor,
    val textSize: UPRawValue = UPConfig.empty.textSize,
    val iconColor: String = UPConfig.empty.iconColor,
    val iconSize: UPRawValue = UPConfig.empty.iconSize,
    val mode: String = UPConfig.empty.mode,
    val width: UPRawValue = UPConfig.empty.width,
    val height: UPRawValue = UPConfig.empty.height,
    val show: Boolean = UPConfig.empty.show,
    val marginTop: UPRawValue = UPConfig.empty.marginTop,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
