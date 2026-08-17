package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** Props for the uview-plus `u-row` component. */
public data class UPRowProps(
    val gutter: UPRawValue = UPConfig.row.gutter,
    val justify: String = UPConfig.row.justify,
    val align: String = UPConfig.row.align,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
