package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** Props for the uview-plus `u-line-progress` component. */
public data class UPLineProgressProps(
    val activeColor: String = UPConfig.lineProgress.activeColor,
    val inactiveColor: String = UPConfig.lineProgress.inactiveColor,
    val percentage: UPRawValue = UPConfig.lineProgress.percentage,
    val showText: Boolean = UPConfig.lineProgress.showText,
    val height: UPRawValue = UPConfig.lineProgress.height,
    val fromRight: Boolean = UPConfig.lineProgress.fromRight,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
