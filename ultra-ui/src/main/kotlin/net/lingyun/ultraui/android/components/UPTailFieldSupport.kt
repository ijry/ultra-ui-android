package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upListOrEmpty
import kotlin.math.pow

/**
 * Pure helpers for the last batch of declared-but-unread fields: `u-count-to`'s easing,
 * `u-back-top`'s scroll duration, `u-skeleton`'s row/avatar geometry, `u-select`'s options
 * panel and `u-read-more`'s first-line indent. Each mirrors one upstream computed value.
 */

/** `uni.pageScrollTo({ scrollTop: 0, duration })`; upstream defaults to 100ms. */
internal fun upBackTopScrollDurationMillis(duration: UPRawValue): Int =
    duration.upIntOrDefault(100).coerceAtLeast(0)

/**
 * `easingFn(t, b, c, d) = (c * (-2^(-10t/d) + 1) * 1024) / 1023 + b` — upstream's
 * ease-out-expo. Returns the eased value at elapsed time [progress] of [duration].
 */
internal fun upCountToEasedValue(progress: Double, from: Double, delta: Double, duration: Double): Double {
    if (duration <= 0.0) return from + delta
    return (delta * (-(2.0.pow(-10.0 * progress / duration)) + 1.0) * 1024.0) / 1023.0 + from
}

/**
 * `count(timestamp)`: `useEasing` picks the eased curve, otherwise the value moves
 * linearly. Both clamp at `endVal`, counting up or down.
 */
internal fun upCountToValueAt(
    progress: Double,
    duration: Double,
    start: Double,
    end: Double,
    useEasing: Boolean,
): Double {
    if (duration <= 0.0) return end
    val fraction = (progress / duration).coerceIn(0.0, 1.0)
    val countDown = start > end
    val raw = if (useEasing) {
        if (countDown) {
            start - upCountToEasedValue(progress, 0.0, start - end, duration)
        } else {
            upCountToEasedValue(progress, start, end - start, duration)
        }
    } else {
        if (countDown) start - (start - end) * fraction else start + (end - start) * fraction
    }
    return if (countDown) raw.coerceAtLeast(end) else raw.coerceAtMost(end)
}

/** `.u-skeleton__wrapper__avatar--circle|square`. */
internal val UPSkeletonAvatarShapes: Set<String> = setOf("circle", "square")

/**
 * `rowsArray`: an array of widths is read per row, anything else applies to every row —
 * except the last, which falls back to `70%` when the array runs out or is absent.
 * Returns the raw upstream width token so the caller can decide px vs percent.
 */
internal fun upSkeletonRowWidth(rowsWidth: UPRawValue, index: Int, rows: Int): UPRawValue {
    val asList = rowsWidth.upSkeletonWidthListOrNull()
    val isLast = index == rows - 1
    if (asList != null) {
        val declared = asList.getOrNull(index)
        if (declared != null && declared.upStringValueOrEmpty().isNotEmpty()) return declared
        return if (isLast) "70%" else "100%"
    }
    return if (isLast) "70%" else rowsWidth
}

/** Only a real array switches on the per-row branch; a bare string never does. */
private fun UPRawValue.upSkeletonWidthListOrNull(): List<UPRawValue>? = when (this) {
    is List<*> -> upListOrEmpty()
    is Array<*> -> upListOrEmpty()
    else -> null
}

/** `/%$/.test(rowWidth)` — a percentage becomes a fraction of the available width. */
internal fun upSkeletonWidthFractionOrNull(width: UPRawValue): Float? {
    val text = width.upStringValueOrEmpty().trim()
    if (!text.endsWith("%")) return null
    return text.dropLast(1).trim().toFloatOrNull()?.div(100f)?.coerceIn(0f, 1f)
}

/**
 * `normalizedOptionsWidth`: an empty or absent value leaves the panel to size itself,
 * a number becomes `${n}px`, and anything else passes through unchanged.
 */
internal fun upSelectOptionsWidthDp(optionsWidth: UPRawValue): Float? {
    if (optionsWidth == null) return null
    val text = optionsWidth.upStringValueOrEmpty().trim()
    if (text.isEmpty()) return null
    optionsWidth.asFiniteFloatOrNull()?.let { return it.takeIf { value -> value > 0f } }
    if (!text.lowercase().endsWith("px")) return null
    return text.lowercase().removeSuffix("px").trim().toFloatOrNull()?.takeIf { it > 0f }
}

/**
 * The trigger prints the selected option's label only when `showOptionsLabel` is set;
 * otherwise it always prints the static `label`, even after a selection.
 */
internal fun upSelectTriggerText(showOptionsLabel: Boolean, currentLabel: String, label: String): String =
    if (showOptionsLabel) currentLabel.ifEmpty { label } else label

/**
 * `textIndent: '2em'` indents the first line only. `em` resolves against the text size,
 * so the caller passes its own font size in; `px` and bare numbers are absolute.
 */
internal fun upTextIndentPx(textIndent: UPRawValue, fontSizePx: Float): Float? {
    val text = textIndent.upStringValueOrEmpty().trim().lowercase()
    if (text.isEmpty() || text == "0") return null
    if (text.endsWith("em")) {
        return text.removeSuffix("em").trim().toFloatOrNull()?.times(fontSizePx)?.takeIf { it > 0f }
    }
    if (text.endsWith("px")) {
        return text.removeSuffix("px").trim().toFloatOrNull()?.takeIf { it > 0f }
    }
    return text.toFloatOrNull()?.takeIf { it > 0f }
}
