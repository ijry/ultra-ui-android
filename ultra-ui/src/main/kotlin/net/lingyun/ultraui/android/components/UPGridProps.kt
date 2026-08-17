package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** Props for the uview-plus `u-grid` component. */
public data class UPGridProps(
    val col: UPRawValue = UPConfig.grid.col,
    val border: Boolean = UPConfig.grid.border,
    val align: String = UPConfig.grid.align,
    val gap: UPRawValue = UPConfig.grid.gap,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
