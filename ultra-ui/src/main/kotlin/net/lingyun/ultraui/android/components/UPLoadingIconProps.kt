package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-loading-icon` props. */
public data class UPLoadingIconProps(
    val show: Boolean = UPConfig.loadingIcon.show,
    val color: String = UPConfig.loadingIcon.color,
    val textColor: String = UPConfig.loadingIcon.textColor,
    val vertical: Boolean = UPConfig.loadingIcon.vertical,
    val mode: String = UPConfig.loadingIcon.mode,
    val size: UPRawValue = UPConfig.loadingIcon.size,
    val textSize: UPRawValue = UPConfig.loadingIcon.textSize,
    val text: UPRawValue = UPConfig.loadingIcon.text,
    val timingFunction: String = UPConfig.loadingIcon.timingFunction,
    val duration: UPRawValue = UPConfig.loadingIcon.duration,
    val inactiveColor: String = UPConfig.loadingIcon.inactiveColor,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
