package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-avatar-group` props. */
public data class UPAvatarGroupProps(
    val urls: List<UPRawValue> = UPConfig.avatarGroup.urls,
    val maxCount: UPRawValue = UPConfig.avatarGroup.maxCount,
    val shape: String = UPConfig.avatarGroup.shape,
    val mode: String = UPConfig.avatarGroup.mode,
    val showMore: Boolean = UPConfig.avatarGroup.showMore,
    val size: UPRawValue = UPConfig.avatarGroup.size,
    val keyName: String = UPConfig.avatarGroup.keyName,
    val gap: UPRawValue = UPConfig.avatarGroup.gap,
    val extraValue: UPRawValue = UPConfig.avatarGroup.extraValue,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
