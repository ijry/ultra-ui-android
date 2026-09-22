package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPLazyLoad], mirroring uview-plus `u-lazy-load`.
 *
 * Shows `loadingImg` until the image enters view, then swaps to `image` with a fade. `errorImg`
 * replaces it on failure. `threshold` (rpx) is the viewport pre-load distance upstream; on
 * Android there is no scroll-spy here, so a `visible` parameter drives the reveal instead.
 * `duration` (ms) and `isEffect` control the fade; `imgMode`/`height`/`borderRadius` size it.
 */
public data class UPLazyLoadProps(
    val index: UPRawValue = "",
    val image: String = "",
    val imgMode: String = "widthFix",
    val loadingImg: String = "",
    val errorImg: String = "",
    val threshold: UPRawValue = 100,
    val duration: UPRawValue = 500,
    val effect: String = "ease-in-out",
    val isEffect: Boolean = true,
    val borderRadius: UPRawValue = 0,
    val height: UPRawValue = "200",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
