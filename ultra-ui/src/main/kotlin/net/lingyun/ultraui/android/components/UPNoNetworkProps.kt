package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPNoNetwork], mirroring uview-plus `u-no-network`.
 *
 * `image` upstream defaults to a bundled base64 PNG data-URI meant for the web `<image>`
 * element; the Android port leaves it empty so callers pass a drawable resource path or URL
 * instead of shipping the web artwork. `zIndex` mirrors the empty-string default, where the
 * overlay falls back to its own stacking value.
 */
public data class UPNoNetworkProps(
    val tips: String = "哎呀，网络信号丢失",
    val zIndex: UPRawValue = "",
    val image: String = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
