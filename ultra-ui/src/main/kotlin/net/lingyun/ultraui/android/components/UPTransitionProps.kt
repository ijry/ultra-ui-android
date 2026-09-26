package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPTransition], mirroring uview-plus `u-transition`.
 *
 * [show] drives the enter/leave animation, [mode] selects the effect (fade, zoom, fade-zoom,
 * fade-up/down/left/right, slide-up/down/left/right), [duration] is the animation time in ms and
 * [timingFunction] the easing (`linear`/`ease`/`ease-in`/`ease-out`/`ease-in-out`).
 */
public data class UPTransitionProps(
    val show: Boolean = false,
    val mode: String = "fade",
    val duration: UPRawValue = "300",
    val timingFunction: String = "ease-out",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
