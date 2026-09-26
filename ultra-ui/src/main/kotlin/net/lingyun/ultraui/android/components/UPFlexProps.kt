package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPFlexGlass
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract for the general flexbox container UPFlex. */
public data class UPFlexProps(
    val direction: String = UPConfig.flex.direction,
    val justify: String = UPConfig.flex.justify,
    val align: String = UPConfig.flex.align,
    val wrap: Boolean = UPConfig.flex.wrap,
    val gap: UPRawValue = UPConfig.flex.gap,
    val glass: UPFlexGlass? = null,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
