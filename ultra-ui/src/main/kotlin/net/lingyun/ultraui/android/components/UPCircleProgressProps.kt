package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** Props for the uview-plus `u-circle-progress` component. */
public data class UPCircleProgressProps(
    val percentage: UPRawValue = UPConfig.circleProgress.percentage,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
