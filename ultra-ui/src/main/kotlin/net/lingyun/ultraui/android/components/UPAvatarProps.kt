package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-avatar` props. */
public data class UPAvatarProps(
    val src: String = UPConfig.avatar.src,
    val shape: String = UPConfig.avatar.shape,
    val size: UPRawValue = UPConfig.avatar.size,
    val mode: String = UPConfig.avatar.mode,
    val text: UPRawValue = UPConfig.avatar.text,
    val bgColor: String = UPConfig.avatar.bgColor,
    val color: String = UPConfig.avatar.color,
    val fontSize: UPRawValue = UPConfig.avatar.fontSize,
    val icon: String = UPConfig.avatar.icon,
    val mpAvatar: Boolean = UPConfig.avatar.mpAvatar,
    val randomBgColor: Boolean = UPConfig.avatar.randomBgColor,
    val defaultUrl: String = UPConfig.avatar.defaultUrl,
    val colorIndex: UPRawValue = UPConfig.avatar.colorIndex,
    val name: UPRawValue = UPConfig.avatar.name,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
