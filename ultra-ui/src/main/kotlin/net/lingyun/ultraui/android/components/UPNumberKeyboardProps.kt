package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPNumberKeyboard], mirroring uview-plus `u-number-keyboard`.
 *
 * `mode` is `number` (digits, optional dot) or `card` (digits plus `X` for an ID card).
 * `dotDisabled` hides the `.` key in number mode (which also makes `0` span two cells).
 * `random` shuffles the key order.
 */
public data class UPNumberKeyboardProps(
    val mode: String = "number",
    val dotDisabled: Boolean = false,
    val random: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
