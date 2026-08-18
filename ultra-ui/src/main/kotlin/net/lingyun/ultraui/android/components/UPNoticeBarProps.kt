package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-notice-bar` props. */
public data class UPNoticeBarProps(
    val text: UPRawValue = emptyList<UPRawValue>(),
    val direction: String = "row",
    val step: Boolean = false,
    val icon: String = "volume",
    val mode: String = "",
    val color: String = "#f9ae3d",
    val bgColor: String = "#fdf6ec",
    val speed: UPRawValue = 80,
    val fontSize: UPRawValue = 14,
    val duration: UPRawValue = 2000,
    val disableTouch: Boolean = true,
    val url: String = "",
    val linkType: String = "navigateTo",
    val justifyContent: String = "flex-start",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
