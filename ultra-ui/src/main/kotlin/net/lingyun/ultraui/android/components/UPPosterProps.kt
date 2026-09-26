package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPPoster], mirroring uview-plus `u-poster`.
 *
 * [json] is the poster description: `json.css` styles the container (`width`/`height`/`background`/
 * `radius`) and `json.views` is the list of absolutely-positioned elements, each `{ type, text, src,
 * css }` where `type` is `text`/`image`/`qrcode`/`view` and `css` carries `left`/`top`/`width`/
 * `height` plus per-type styling.
 */
public data class UPPosterProps(
    val json: Map<String, UPRawValue> = emptyMap(),
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
