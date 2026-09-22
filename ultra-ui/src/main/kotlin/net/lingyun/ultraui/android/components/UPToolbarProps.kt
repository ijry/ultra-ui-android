package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPToolbar], mirroring uview-plus `u-toolbar`.
 *
 * `confirmColor` defaults to the empty string upstream, where the template feeds it straight
 * into the confirm label's inline `color`; an empty value therefore falls back to the theme
 * primary. `rightSlot` swaps the built-in confirm button for the caller's `right` content.
 */
public data class UPToolbarProps(
    val show: Boolean = true,
    val cancelText: String = "取消",
    val confirmText: String = "确定",
    val cancelColor: String = "#909193",
    val confirmColor: String = "",
    val title: String = "",
    val rightSlot: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
