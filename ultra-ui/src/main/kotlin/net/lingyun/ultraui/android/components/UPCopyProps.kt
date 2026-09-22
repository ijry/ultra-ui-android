package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPCopy], mirroring uview-plus `up-copy`.
 *
 * `content` is the string written to the clipboard. `alertStyle` picks how upstream announces
 * the result (`toast` or `modal`); Android has no built-in toast/modal here, so the resolved
 * `notice` is surfaced through a callback for the host to present. `notice` defaults to
 * `复制成功` from the i18n table.
 */
public data class UPCopyProps(
    val content: String = "",
    val alertStyle: String = "toast",
    val notice: String = "复制成功",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
