package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPListProps(
    val showScrollbar: Boolean = false,
    val lowerThreshold: UPRawValue = 50,
    val upperThreshold: UPRawValue = 0,
    val scrollTop: UPRawValue = 0,
    val offsetAccuracy: UPRawValue = 10,
    val enableFlex: Boolean = false,
    val pagingEnabled: Boolean = false,
    val scrollable: Boolean = true,
    val scrollIntoView: String = "",
    val scrollWithAnimation: Boolean = false,
    val enableBackToTop: Boolean = false,
    val height: UPRawValue = 0,
    val width: UPRawValue = 0,
    val preLoadScreen: UPRawValue = 1,
    val refresherEnabled: Boolean = false,
    val refresherThreshold: UPRawValue = 45,
    val refresherDefaultStyle: String = "black",
    val refresherBackground: String = "#FFF",
    val refresherTriggered: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPListItemProps(
    val anchor: UPRawValue = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPIndexListProps(
    val inactiveColor: String = "#606266",
    val activeColor: String = "#5677fc",
    val indexList: List<UPRawValue> = emptyList(),
    val sticky: Boolean = true,
    val customNavHeight: UPRawValue = 0,
    val safeBottomFix: Boolean = false,
    val itemMargin: UPRawValue = "0rpx",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPIndexItemProps(
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPIndexAnchorProps(
    val text: UPRawValue = "",
    val color: String = "#606266",
    val size: UPRawValue = 14,
    val bgColor: String = "#f1f1f1",
    val height: UPRawValue = 32,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPScrollListProps(
    val indicatorWidth: UPRawValue = 50,
    val indicatorBarWidth: UPRawValue = 20,
    val indicator: Boolean = true,
    val indicatorColor: String = "#f2f2f2",
    val indicatorActiveColor: String = "#3c9cff",
    val indicatorStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
