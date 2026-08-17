package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPTagProps(
    val type: String = UPConfig.tag.type,
    val disabled: Boolean = UPConfig.tag.disabled,
    val size: String = UPConfig.tag.size,
    val shape: String = UPConfig.tag.shape,
    val text: UPRawValue = UPConfig.tag.text,
    val bgColor: String = UPConfig.tag.bgColor,
    val color: String = UPConfig.tag.color,
    val borderColor: String = UPConfig.tag.borderColor,
    val closeColor: String = UPConfig.tag.closeColor,
    val name: UPRawValue = UPConfig.tag.name,
    val plainFill: Boolean = UPConfig.tag.plainFill,
    val plain: Boolean = UPConfig.tag.plain,
    val closable: Boolean = UPConfig.tag.closable,
    val show: Boolean = UPConfig.tag.show,
    val icon: String = UPConfig.tag.icon,
    val iconColor: String = UPConfig.tag.iconColor,
    val textSize: UPRawValue = UPConfig.tag.textSize,
    val height: UPRawValue = UPConfig.tag.height,
    val padding: UPRawValue = UPConfig.tag.padding,
    val borderRadius: UPRawValue = UPConfig.tag.borderRadius,
    val autoBgColor: UPRawValue = UPConfig.tag.autoBgColor,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
