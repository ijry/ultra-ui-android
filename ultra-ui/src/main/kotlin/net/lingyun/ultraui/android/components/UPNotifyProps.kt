package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-notify` props. */
public data class UPNotifyProps(
    val top: UPRawValue = UPConfig.notify.top,
    val type: String = UPConfig.notify.type,
    val color: String = UPConfig.notify.color,
    val bgColor: String = UPConfig.notify.bgColor,
    val message: String = UPConfig.notify.message,
    val duration: UPRawValue = UPConfig.notify.duration,
    val fontSize: UPRawValue = UPConfig.notify.fontSize,
    val safeAreaInsetTop: Boolean = UPConfig.notify.safeAreaInsetTop,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
