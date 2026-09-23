package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPWaterfall], mirroring uview-plus `u-waterfall`.
 *
 * A masonry layout: `modelValue` holds the item data, packed into `columns` columns by
 * shortest-column height. `columns = "auto"` derives the count from the available width and
 * `minColumnWidth` (clamped to at least `columnsMin`). `idKey` identifies items for removal.
 * `addTime` is the upstream staggered-append interval, which has no Compose counterpart.
 */
public data class UPWaterfallProps(
    val modelValue: List<UPRawValue> = emptyList(),
    val addTime: UPRawValue = 200,
    val idKey: String = "id",
    val columns: UPRawValue = 2,
    val columnsMin: UPRawValue = 2,
    val minColumnWidth: Int = 230,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
