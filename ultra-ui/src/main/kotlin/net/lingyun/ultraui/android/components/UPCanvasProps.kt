package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPCanvas], mirroring uview-plus `u-canvas`.
 *
 * A drawing surface sized [width] x [height] in [unit] (or filling its parent when
 * [useRootHeightAndWidth]) on a [bgColor] background. [disableScroll] makes the canvas consume touch
 * moves so a scrolling parent does not steal the gesture.
 *
 * `canvasId` (the H5/mini-program canvas element id) has no Android equivalent — drawing happens
 * through the `onDraw` lambda rather than a ref-retrieved context — and is inert; see docs.
 */
public data class UPCanvasProps(
    val canvasId: String = "",
    val width: UPRawValue = 300,
    val height: UPRawValue = 300,
    val unit: String = "px",
    val useRootHeightAndWidth: Boolean = false,
    val bgColor: String = "#ffffff",
    val disableScroll: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
