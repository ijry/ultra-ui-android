package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upIntOrDefault

/**
 * `u-transition`'s eleven animation modes as declared in `vue.ani-style.scss`, plus
 * `u-popup`'s `position()` computed and the bottom-sheet gesture it wires when
 * `touchable` is set. Everything here is pure so the tables and the drag thresholds can be
 * pinned by JVM tests instead of inferred from a running sheet.
 */

/** `$u-zoom-scale: scale(0.95)` from `u-transition`'s stylesheet. */
internal const val UPTransitionZoomScale: Float = 0.95f

/**
 * Every mode `u-transition` ships a stylesheet for, plus the `none` that `pageInline`
 * selects. An unknown mode has no `.u-<name>-enter` rule upstream, so nothing animates.
 */
internal val UPTransitionModes: Set<String> = setOf(
    "fade",
    "zoom",
    "fade-zoom",
    "fade-up",
    "fade-down",
    "fade-left",
    "fade-right",
    "slide-up",
    "slide-down",
    "slide-left",
    "slide-right",
    "none",
)

/** `<u-transition :duration>`; the component's own default is 300ms. */
internal fun upTransitionDuration(duration: UPRawValue): Int =
    duration.upIntOrDefault(300).coerceAtLeast(0)

/** `<u-transition :duration>` as `u-popup` forwards it; the popup default is also 300ms. */
internal fun upPopupTransitionDuration(duration: UPRawValue): Int = upTransitionDuration(duration)

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

/** Every `fade*` rule sets `opacity: 0` on entry; the bare slides and `zoom` do not. */
internal fun upTransitionFades(transition: String): Boolean = transition.startsWith("fade")

/** `zoom` and `fade-zoom` are the two rules carrying `transform: scale(0.95)`. */
internal fun upTransitionScales(transition: String): Boolean =
    transition == "zoom" || transition == "fade-zoom"

/**
 * The entry offset as a fraction of the element, from `translate3d`: `*-up` starts one
 * full height below, `*-down` one above, and the horizontal ones one width aside.
 * Returns `(x, y)`; `fade`, `zoom`, `fade-zoom` and `none` do not translate.
 */
internal fun upTransitionOffsetFraction(transition: String): Pair<Float, Float> = when (transition) {
    "slide-up", "fade-up" -> 0f to 1f
    "slide-down", "fade-down" -> 0f to -1f
    "slide-left", "fade-left" -> -1f to 0f
    "slide-right", "fade-right" -> 1f to 0f
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
