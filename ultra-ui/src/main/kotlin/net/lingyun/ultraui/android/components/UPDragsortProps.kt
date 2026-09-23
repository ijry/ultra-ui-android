package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPDragsort], mirroring uview-plus `u-dragsort`.
 *
 * A drag-to-reorder list. `initialList` seeds the items (each carries an `id` and, by default, a
 * `label`); `draggable` toggles dragging globally, and a per-item `draggable = false` pins an item.
 * `direction` is `vertical`/`horizontal`/`all` (`all` flows into `columns`). `drag-end` reports the
 * reordered list.
 */
public data class UPDragsortProps(
    val initialList: List<UPRawValue> = emptyList(),
    val draggable: Boolean = true,
    val vibrate: Boolean = true,
    val direction: String = "vertical",
    val columns: Int = 3,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
