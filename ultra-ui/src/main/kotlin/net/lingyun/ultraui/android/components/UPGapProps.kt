package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPGapProps(
    val bgColor: String = UPConfig.gap.bgColor,
    val height: UPRawValue = UPConfig.gap.height,
    val marginTop: UPRawValue = UPConfig.gap.marginTop,
    val marginBottom: UPRawValue = UPConfig.gap.marginBottom,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
