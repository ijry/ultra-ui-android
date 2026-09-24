package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPPullRefresh], mirroring uview-plus `u-pull-refresh`.
 *
 * A pull-to-refresh wrapper. `refreshing` is the controlled refreshing flag (the host sets it
 * true on `refresh` then false when done). `threshold` is the pull distance (px) that arms a
 * refresh; `damping` scales raw finger travel; `maxDistance` caps the pulled distance.
 */
public data class UPPullRefreshProps(
    val refreshing: Boolean = false,
    val threshold: Int = 80,
    val damping: Float = 0.4f,
    val maxDistance: Int = 120,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
