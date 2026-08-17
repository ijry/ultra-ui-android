package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPTextProps(
    val type: String = UPConfig.text.type,
    val show: Boolean = UPConfig.text.show,
    val text: UPRawValue = UPConfig.text.text,
    val prefixIcon: String = UPConfig.text.prefixIcon,
    val suffixIcon: String = UPConfig.text.suffixIcon,
    val mode: String = UPConfig.text.mode,
    val href: String = UPConfig.text.href,
    val format: UPRawValue = UPConfig.text.format,
    val call: Boolean = UPConfig.text.call,
    val openType: String = UPConfig.text.openType,
    val bold: Boolean = UPConfig.text.bold,
    val block: Boolean = UPConfig.text.block,
    val lines: UPRawValue = UPConfig.text.lines,
    val color: String = UPConfig.text.color,
    val size: UPRawValue = UPConfig.text.size,
    val iconStyle: UPStyleInput = UPConfig.text.iconStyle,
    val decoration: String = UPConfig.text.decoration,
    val margin: UPRawValue = UPConfig.text.margin,
    val lineHeight: UPRawValue = UPConfig.text.lineHeight,
    val align: String = UPConfig.text.align,
    val wordWrap: String = UPConfig.text.wordWrap,
    val flex1: Boolean = UPConfig.text.flex1,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
