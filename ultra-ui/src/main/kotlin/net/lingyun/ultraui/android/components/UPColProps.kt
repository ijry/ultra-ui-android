package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** Props for the uview-plus `u-col` component. */
public data class UPColProps(
    val span: UPRawValue = UPConfig.col.span,
    val offset: UPRawValue = UPConfig.col.offset,
    val justify: String = UPConfig.col.justify,
    val align: String = UPConfig.col.align,
    val textAlign: String = UPConfig.col.textAlign,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
