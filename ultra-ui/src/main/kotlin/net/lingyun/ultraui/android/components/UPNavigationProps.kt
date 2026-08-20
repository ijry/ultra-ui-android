package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPNavbarProps(
    val safeAreaInsetTop: Boolean = true,
    val placeholder: Boolean = false,
    val fixed: Boolean = true,
    val border: Boolean = false,
    val leftIcon: String = "arrow-left",
    val leftText: String = "",
    val rightText: String = "",
    val rightIcon: String = "",
    val title: String = "",
    val titleColor: String = "",
    val bgColor: String = "",
    val statusBarBgColor: String = "",
    val titleWidth: UPRawValue = "400rpx",
    val height: UPRawValue = "44px",
    val leftIconSize: UPRawValue = 20,
    val leftIconColor: String = "",
    val autoBack: Boolean = false,
    val titleStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPNavbarMiniProps(
    val safeAreaInsetTop: Boolean = true,
    val fixed: Boolean = true,
    val leftIcon: String = "arrow-leftward",
    val bgColor: String = "rgba(0,0,0,.15)",
    val height: UPRawValue = "32px",
    val iconSize: UPRawValue = "20px",
    val iconColor: String = "#fff",
    val autoBack: Boolean = true,
    val homeUrl: String = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPStatusBarProps(
    val bgColor: String = "transparent",
    val height: UPRawValue = 0,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPSafeBottomProps(
    val safeAreaInsetBottom: Boolean = true,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
