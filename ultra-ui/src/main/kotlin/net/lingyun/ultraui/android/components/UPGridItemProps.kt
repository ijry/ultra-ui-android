package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** Props for the uview-plus `u-grid-item` component. */
public data class UPGridItemProps(
    val name: UPRawValue = UPConfig.gridItem.name,
    val bgColor: String = UPConfig.gridItem.bgColor,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
