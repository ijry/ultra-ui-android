package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPCarKeyboard], mirroring uview-plus `u-car-keyboard`.
 *
 * `random` shuffles the key order. `autoChange` switches from the Chinese province plate to the
 * English/number plate automatically after a single Chinese key, matching upstream's `sleep(200)`
 * flip.
 */
public data class UPCarKeyboardProps(
    val random: Boolean = false,
    val autoChange: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
