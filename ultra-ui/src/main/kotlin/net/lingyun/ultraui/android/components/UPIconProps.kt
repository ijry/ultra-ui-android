package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-icon` props. */
public data class UPIconProps(
    val name: String = UPConfig.icon.name,
    val color: String = UPConfig.icon.color,
    val size: UPRawValue = UPConfig.icon.size,
    val bold: Boolean = UPConfig.icon.bold,
    val index: UPRawValue = UPConfig.icon.index,
    val hoverClass: String = UPConfig.icon.hoverClass,
    val customPrefix: String = UPConfig.icon.customPrefix,
    val label: UPRawValue = UPConfig.icon.label,
    val labelPos: String = UPConfig.icon.labelPos,
    val labelSize: UPRawValue = UPConfig.icon.labelSize,
    val labelColor: String = UPConfig.icon.labelColor,
    val space: UPRawValue = UPConfig.icon.space,
    val imgMode: String = UPConfig.icon.imgMode,
    val width: UPRawValue = UPConfig.icon.width,
    val height: UPRawValue = UPConfig.icon.height,
    val top: UPRawValue = UPConfig.icon.top,
    val stop: Boolean = UPConfig.icon.stop,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
