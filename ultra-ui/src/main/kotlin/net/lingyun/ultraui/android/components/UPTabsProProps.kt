package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPTabsPro], mirroring uview-plus `u-tabs-pro`.
 *
 * A thin wrapper over `u-tabs` that also renders a content area below the tab bar. It forwards the
 * `u-tabs` styling props (`lineColor`, `activeStyle`, `lineWidth`, `shapeMode`, …) and adds
 * `showContent` to toggle the slot area under the tabs.
 */
public data class UPTabsProProps(
    val list: List<UPRawValue> = emptyList(),
    val keyName: String = "name",
    val current: UPRawValue = 0,
    val contentMode: String = "static",
    val lineColor: String = "",
    val activeStyle: UPStyleInput = mapOf("color" to "#303133"),
    val inactiveStyle: UPStyleInput = mapOf("color" to "#606266"),
    val lineWidth: UPRawValue = 20,
    val lineHeight: UPRawValue = 3,
    val lineBgSize: String = "cover",
    val itemStyle: UPStyleInput = mapOf("height" to "44px"),
    val scrollable: Boolean = true,
    val duration: UPRawValue = 300,
    val iconStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val shapeMode: String = "",
    val showContent: Boolean = true,
    val contentStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
