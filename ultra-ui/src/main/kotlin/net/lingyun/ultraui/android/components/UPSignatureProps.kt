package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPSignature], mirroring uview-plus `u-signature`.
 *
 * A signature pad. `width`/`height` size the canvas (px), `bgColor` fills it, `color`/`thickness`
 * style the stroke. `showToolbar` reveals the clear/confirm bar.
 */
public data class UPSignatureProps(
    val width: UPRawValue = 300,
    val height: UPRawValue = 200,
    val bgColor: String = "#ffffff",
    val color: String = "#000000",
    val thickness: UPRawValue = 3,
    val showToolbar: Boolean = true,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
