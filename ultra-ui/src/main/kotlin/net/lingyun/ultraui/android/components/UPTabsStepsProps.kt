package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPTabsProps(
    val duration: UPRawValue = 300,
    val list: List<UPRawValue> = emptyList(),
    val lineColor: String = "",
    val activeStyle: UPStyleInput = mapOf("color" to "#303133"),
    val inactiveStyle: UPStyleInput = mapOf("color" to "#606266"),
    val lineWidth: UPRawValue = 20,
    val lineHeight: UPRawValue = 3,
    val lineBgSize: String = "cover",
    val itemStyle: UPStyleInput = mapOf("height" to "44px"),
    val scrollable: Boolean = true,
    val current: UPRawValue = 0,
    val keyName: String = "name",
    val iconStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val shapeMode: String = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPTabsItemProps(
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPSubsectionProps(
    val list: List<UPRawValue> = emptyList(),
    val current: UPRawValue = 0,
    val activeColor: String = "#3c9cff",
    val inactiveColor: String = "#303133",
    val mode: String = "button",
    val fontSize: UPRawValue = 12,
    val bold: Boolean = true,
    val bgColor: String = "#eeeeef",
    val keyName: String = "name",
    val activeColorKeyName: String = "activeColorKey",
    val inactiveColorKeyName: String = "inactiveColorKey",
    val disabled: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPStepsProps(
    val direction: String = "row",
    val current: UPRawValue = 0,
    val activeColor: String = "#3c9cff",
    val inactiveColor: String = "#969799",
    val activeIcon: String = "",
    val inactiveIcon: String = "",
    val dot: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPStepsItemProps(
    val title: UPRawValue = "",
    val desc: UPRawValue = "",
    val iconSize: UPRawValue = 14,
    val error: Boolean = false,
    val itemStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
