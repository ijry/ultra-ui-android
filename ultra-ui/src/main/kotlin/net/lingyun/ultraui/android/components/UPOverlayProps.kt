package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-overlay` props. */
public data class UPOverlayProps(
    val show: Boolean = UPConfig.overlay.show,
    val zIndex: UPRawValue = UPConfig.overlay.zIndex,
    val duration: UPRawValue = UPConfig.overlay.duration,
    val opacity: UPRawValue = UPConfig.overlay.opacity,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
