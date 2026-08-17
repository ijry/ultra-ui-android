package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-cell-group` props. */
public data class UPCellGroupProps(
    val title: String = UPConfig.cellGroup.title,
    val border: Boolean = UPConfig.cellGroup.border,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
