package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue

/**
 * `u-index-list`'s touch-to-index arithmetic and the A-Z fallback it generates when no
 * `indexList` is supplied. Pure so the boundary handling can be pinned by JVM tests
 * instead of inferred from a dragged rail.
 */

/**
 * `uIndexList()`: an explicit list wins, otherwise upstream generates A-Z from
 * `'A'.charCodeAt(0)`.
 */
internal fun upIndexListEntries(indexList: List<UPRawValue>): List<UPRawValue> =
    indexList.ifEmpty { (0 until 26).map { ('A' + it).toString() } }

/**
 * `getIndexListLetter(pageY)`: the touch offset minus the rail's top, divided by one
 * letter's height. Both ends clamp, because a finger can keep travelling past the rail.
 * Returns `-1` only when there is no letter to land on.
 */
internal fun upIndexListLetterAt(
    touchY: Float,
    railTop: Float,
    railHeight: Float,
    count: Int,
): Int {
    if (count <= 0) return -1
    if (touchY < railTop) return 0
    if (touchY >= railTop + railHeight) return count - 1
    val itemHeight = railHeight / count
    if (itemHeight <= 0f) return 0
    return ((touchY - railTop) / itemHeight).toInt().coerceIn(0, count - 1)
}

/**
 * `setValueForTouch(currentIndex)` returns early when the index has not moved, which is
 * upstream's debounce: a drag inside one letter must not re-emit `select`.
 */
internal fun upIndexListShouldEmit(current: Int, next: Int): Boolean = next >= 0 && next != current

/**
 * `scrollIntoView = `u-index-item-${...charCodeAt(0)}``: the anchor id is the first
 * character's code point, taken from a plain string or from an object's `name`.
 */
internal fun upIndexListAnchorId(entry: UPRawValue): String? {
    val text = when (entry) {
        is Map<*, *> -> (entry["name"] ?: entry["key"])?.toString()
        else -> entry?.toString()
    }?.takeIf { it.isNotEmpty() } ?: return null
    return "u-index-item-${text[0].code}"
}

/** `{{ item.key || item }}` on the rail; objects print their `key`, then their `name`. */
internal fun upIndexListEntryLabel(entry: UPRawValue): String = when (entry) {
    is Map<*, *> -> ((entry["key"] ?: entry["name"])?.toString() ?: entry.toString())
    else -> entry?.toString().orEmpty()
}

/** `sleep(300).then(() => this.touching = false)` hides the magnifier after a release. */
internal const val UPIndexListIndicatorHideDelayMillis: Long = 300L
