package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPMessageInput], mirroring uview-plus `u-message-input`.
 *
 * `mode` picks the cell decoration: `box` outlines every cell and highlights the active one,
 * `bottomLine` draws an underline, `middleLine` a centred line. `fontSize`/`width` are `rpx`
 * against the 750-wide design canvas, and the cell height equals its width. `dotFill` masks
 * every filled cell with a bullet.
 */
public data class UPMessageInputProps(
    val maxlength: UPRawValue = 4,
    val dotFill: Boolean = false,
    val mode: String = "box",
    val modelValue: UPRawValue = "",
    val breathe: Boolean = true,
    val focus: Boolean = false,
    val bold: Boolean = false,
    val fontSize: UPRawValue = 60,
    val activeColor: String = "#2979ff",
    val inactiveColor: String = "#606266",
    val width: UPRawValue = "80",
    val disabledKeyboard: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
