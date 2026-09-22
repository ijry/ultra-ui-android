package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPKeyboard], mirroring uview-plus `u-keyboard`.
 *
 * A bottom-sheet wrapper around the number/card/car keyboards with an optional tooltip bar.
 * `mode` picks `number`/`card` (→ number keyboard) or `car` (→ car keyboard). The tooltip's tip
 * defaults per mode when `tips` is empty (`数字键盘`/`身份证键盘`/`车牌号键盘`).
 */
public data class UPKeyboardProps(
    val mode: String = "number",
    val dotDisabled: Boolean = false,
    val tooltip: Boolean = true,
    val showTips: Boolean = true,
    val tips: String = "",
    val showCancel: Boolean = true,
    val showConfirm: Boolean = true,
    val random: Boolean = false,
    val safeAreaInsetBottom: Boolean = true,
    val closeOnClickOverlay: Boolean = true,
    val show: Boolean = false,
    val overlay: Boolean = true,
    val zIndex: UPRawValue = 10075,
    val cancelText: String = "取消",
    val confirmText: String = "确认",
    val autoChange: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
