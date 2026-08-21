package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPTabbarProps(
    val value: UPRawValue? = null,
    val modelValue: UPRawValue? = null,
    val current: UPRawValue? = null,
    val safeAreaInsetBottom: Boolean = true,
    val border: Boolean = true,
    val zIndex: UPRawValue = 1,
    val activeColor: String = "#1989fa",
    val inactiveColor: String = "#7d7e80",
    val fixed: Boolean = true,
    val placeholder: Boolean = true,
    val borderColor: String = "",
    val backgroundColor: String = "",
    val styleType: String = "default",
    val animationType: String = "none",
    val activeBackgroundColor: String = "",
    val inactiveBackgroundColor: String = "",
    val itemShape: String = "default",
    val iconScale: UPRawValue = 1.1,
    val textMode: String = "always",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPTabbarItemProps(
    val name: UPRawValue? = null,
    val icon: UPRawValue = "",
    val activeIcon: UPRawValue = "",
    val inactiveIcon: UPRawValue = "",
    val badge: UPRawValue? = null,
    val dot: Boolean = false,
    val text: UPRawValue = "",
    val badgeStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val mode: String = "",
    val activeClass: String = "",
    val inactiveClass: String = "",
    val midButtonBgColor: String = "",
    val midButtonIconColor: String = "",
    val midButtonIconSize: UPRawValue = 26,
    val midButtonBoxShadow: String = "",
    val midButtonInnerBoxShadow: String = "",
    val midButtonOffsetY: UPRawValue = -10,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPTabbarChangeEvent(val value: UPRawValue?, val index: Int)
