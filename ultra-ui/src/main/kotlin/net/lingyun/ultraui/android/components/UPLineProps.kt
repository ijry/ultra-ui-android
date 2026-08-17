package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPLineProps(
    val color: String = UPConfig.line.color,
    val length: UPRawValue = UPConfig.line.length,
    val direction: String = UPConfig.line.direction,
    val hairline: Boolean = UPConfig.line.hairline,
    val margin: UPRawValue = UPConfig.line.margin,
    val dashed: Boolean = UPConfig.line.dashed,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
