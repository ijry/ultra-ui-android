package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPDividerProps(
    val dashed: Boolean = UPConfig.divider.dashed,
    val hairline: Boolean = UPConfig.divider.hairline,
    val dot: Boolean = UPConfig.divider.dot,
    val textPosition: String = UPConfig.divider.textPosition,
    val text: String = UPConfig.divider.text,
    val textSize: UPRawValue = UPConfig.divider.textSize,
    val textColor: String = UPConfig.divider.textColor,
    val lineColor: String = UPConfig.divider.lineColor,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
