package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upIntOrDefault

/**
 * `u-popup`'s `position()` computed plus the bottom-sheet gesture it wires when
 * `touchable` is set. Both are pure so the mapping and the drag thresholds can be
 * pinned by JVM tests instead of inferred from a running sheet.
 */

/** `$u-zoom-scale: scale(0.95)` from `u-transition`'s stylesheet. */
internal const val UPPopupZoomScale: Float = 0.95f

/** `<u-transition :duration>`; upstream's popup defaults to 300ms. */
internal fun upPopupTransitionDuration(duration: UPRawValue): Int =
    duration.upIntOrDefault(300).coerceAtLeast(0)

/**
 * `position()`: only `center` consults `zoom`, and `pageInline` opts out of the
 * transition entirely (`:mode="pageInline ? 'none' : position"`).
 */
internal fun upPopupTransitionMode(mode: String, zoom: Boolean, pageInline: Boolean): String = when {
    pageInline -> "none"
    mode == "center" -> if (zoom) "fade-zoom" else "fade"
    mode == "left" -> "slide-left"
    mode == "right" -> "slide-right"
    mode == "top" -> "slide-down"
    else -> "slide-up"
}

/** Only `fade` and `fade-zoom` interpolate opacity; the slides move at full opacity. */
internal fun upPopupTransitionFades(transition: String): Boolean =
    transition == "fade" || transition == "fade-zoom"

/**
 * The entry offset as a fraction of the panel, from `translate3d`: `slide-up` starts one
 * full height below, `slide-down` one above, and the horizontal slides one width aside.
 * Returns `(x, y)`; `fade`/`fade-zoom`/`none` do not translate.
 */
internal fun upPopupTransitionOffsetFraction(transition: String): Pair<Float, Float> = when (transition) {
    "slide-up" -> 0f to 1f
    "slide-down" -> 0f to -1f
    "slide-left" -> -1f to 0f
    "slide-right" -> 1f to 0f
    else -> 0f to 0f
}

/** `touchable` only arms the drag handle for the bottom sheet. */
internal fun upPopupDragEnabled(touchable: Boolean, mode: String): Boolean = touchable && mode == "bottom"

/**
 * `onTouchMove`: the new height is `touchStartHeight - deltaY`, and it is only committed
 * while it stays inside `[minHeight, maxHeight]` — outside that range upstream keeps the
 * previous height rather than clamping. `maxHeight` falls back to 80% of the window.
 */
internal fun upPopupDragHeightOrNull(
    startHeightPx: Float,
    deltaYPx: Float,
    minHeightPx: Float,
    maxHeightPx: Float,
): Float? {
    if (deltaYPx == 0f) return null
    val next = startHeightPx - deltaYPx
    return next.takeIf { it >= minHeightPx && it <= maxHeightPx }
}

/** `getWindowInfo().windowHeight * 0.8` when `maxHeight` is absent. */
internal fun upPopupDragMaxHeightPx(maxHeightPx: Float?, windowHeightPx: Float): Float =
    maxHeightPx?.takeIf { it > 0f } ?: (windowHeightPx * 0.8f)

/** `minHeight` parses to 200px upstream when absent or unusable. */
internal fun upPopupDragMinHeightPx(minHeightPx: Float?): Float =
    minHeightPx?.takeIf { it > 0f } ?: 200f

/**
 * `onTouchEnd`: a long drag down closes outright, and a shorter one closes when it was
 * fast (`velocity = |deltaY| / elapsed > 0.5`). Everything else snaps back.
 */
internal fun upPopupShouldCloseAfterDrag(deltaYPx: Float, elapsedMillis: Long): Boolean {
    if (deltaYPx > 100f) return true
    if (deltaYPx <= 30f || elapsedMillis <= 0L) return false
    return kotlin.math.abs(deltaYPx) / elapsedMillis > 0.5f
}
