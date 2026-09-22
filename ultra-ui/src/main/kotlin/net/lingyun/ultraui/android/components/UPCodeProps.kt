package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPCode], mirroring uview-plus `u-code`.
 *
 * `changeText` carries a literal `X` that the running countdown replaces with the current
 * second, exactly like the documented `X秒重新获取`. `keepRunning`/`uniqueKey` drive the H5
 * "resume after refresh" persistence upstream; they have no Android counterpart (see [UPCode]).
 */
public data class UPCodeProps(
    val seconds: UPRawValue = 60,
    val startText: String = "获取验证码",
    val changeText: String = "X秒重新获取",
    val endText: String = "重新获取",
    val keepRunning: Boolean = false,
    val uniqueKey: String = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
