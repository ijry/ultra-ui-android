package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-back-top` props. */
public data class UPBackTopProps(
    val mode: String = UPConfig.backTop.mode,
    val icon: String = UPConfig.backTop.icon,
    val text: String = UPConfig.backTop.text,
    val duration: UPRawValue = UPConfig.backTop.duration,
    val scrollTop: UPRawValue = UPConfig.backTop.scrollTop,
    val top: UPRawValue = UPConfig.backTop.top,
    val bottom: UPRawValue = UPConfig.backTop.bottom,
    val right: UPRawValue = UPConfig.backTop.right,
    val zIndex: UPRawValue = UPConfig.backTop.zIndex,
    val iconStyle: Map<String, UPRawValue> = UPConfig.backTop.iconStyle,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
