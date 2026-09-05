package net.lingyun.ultraui.android.components

/**
 * The geometry of a `picker-view` wheel. Upstream delegates to the platform's own
 * `<picker-view>`, whose contract is: the column is `visibleItemCount * itemHeight` tall,
 * the selection sits in an indicator band of exactly `itemHeight` in the middle, and
 * scrolling snaps so that one option is always centred in that band.
 *
 * Compose has no such widget, so the wheel is built from a snapping list. These helpers
 * carry the arithmetic that makes it behave like the platform one.
 */

/**
 * How much empty space the wheel needs above the first option (and below the last) so that
 * either end can still reach the centre band. With an odd `visibleItemCount` this is a
 * whole number of rows; an even count leaves half a row, which is why the padding is
 * expressed in item-height units rather than rows.
 */
internal fun upPickerWheelPaddingFraction(visibleItemCount: Int): Float =
    ((visibleItemCount.coerceAtLeast(1) - 1) / 2f)

/**
 * Which option sits in the indicator band, given how far the wheel has scrolled. Snapping
 * keeps this whole in practice; rounding makes the mid-drag value follow the nearest row,
 * which is what the platform wheel reports.
 */
internal fun upPickerWheelIndexAt(scrollOffsetPx: Float, itemHeightPx: Float, count: Int): Int {
    if (count <= 0 || itemHeightPx <= 0f) return 0
    return Math.round(scrollOffsetPx / itemHeightPx).coerceIn(0, count - 1)
}

/** The scroll offset that centres [index] in the band — the inverse of the above. */
internal fun upPickerWheelOffsetFor(index: Int, itemHeightPx: Float, count: Int): Float {
    if (count <= 0) return 0f
    return index.coerceIn(0, count - 1) * itemHeightPx
}

/**
 * `changeHandler(e)`: upstream compares the new index array against the previous one and
 * reports the *first* column that moved, because `picker-view` emits the whole array on
 * every change. Returns `null` when nothing moved.
 */
internal fun upPickerChangedColumn(previous: List<Int>, next: List<Int>): Int? {
    for (columnIndex in next.indices) {
        val before = previous.getOrElse(columnIndex) { 0 }
        if (next[columnIndex] != before) return columnIndex
    }
    return null
}

/**
 * `.u-picker__view__column__item--disabled { opacity: 0.35 }`: a column entry may carry its
 * own `disabled` flag, and a disabled option is dimmed and cannot be selected.
 */
internal const val UPPickerDisabledItemAlpha: Float = 0.35f
