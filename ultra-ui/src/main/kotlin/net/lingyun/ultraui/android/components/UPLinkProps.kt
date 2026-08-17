package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPLinkProps(
    val color: String = UPConfig.link.color,
    val fontSize: UPRawValue = UPConfig.link.fontSize,
    val underLine: Boolean = UPConfig.link.underLine,
    val href: String = UPConfig.link.href,
    val mpTips: String = UPConfig.link.mpTips,
    val lineColor: String = UPConfig.link.lineColor,
    val text: UPRawValue = UPConfig.link.text,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
