package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upIntOrDefault
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.max
import kotlin.math.min

/**
 * Pure helpers for the field-parity batch: `u-tag`'s auto background colour,
 * `u-badge`'s absolute offset, and the transition timings of `u-overlay`,
 * `u-modal` and `u-number-box`. Each mirrors one upstream computed property so the
 * composables stay declarative and the arithmetic stays unit-testable on the JVM.
 */

/** Upstream waits this long before a held +/- button starts repeating. */
internal const val UPNumberBoxLongPressDelayMillis: Long = 600L

/** Upstream repeats the held +/- step at this interval. */
internal const val UPNumberBoxLongPressIntervalMillis: Long = 250L

/** `u-navbar-mini`'s `.u-navbar-mini--fixed` offsets the floating pill by `left: 20px`. */
internal const val UPNavbarMiniFixedStartOffsetDp: Int = 20

/** ...and by `top: 10px`. */
internal const val UPNavbarMiniFixedTopOffsetDp: Int = 10

/** Both `.u-navbar--fixed` and `.u-navbar-mini--fixed` raise the bar to `z-index: 11`. */
internal const val UPNavbarFixedZIndex: Float = 11f

/** `<u-transition :duration>` on `u-overlay`; upstream defaults to 300ms. */
internal fun upOverlayFadeDuration(duration: UPRawValue): Int =
    duration.upIntOrDefault(300).coerceAtLeast(0)

/** `<u-popup :duration>` as forwarded by `u-modal`; upstream defaults to 400ms. */
internal fun upModalTransitionDuration(duration: UPRawValue): Int =
    duration.upIntOrDefault(400).coerceAtLeast(0)

/**
 * `badgeStyle` only positions the badge when `absolute` is set, and then reads
 * `top = offset[0]`, `right = offset[1] || top`. Returns `null` when the badge should
 * keep its default corner so callers can skip the offset modifier entirely.
 */
internal fun upBadgeOffsetPair(absolute: Boolean, offset: List<UPRawValue>): Pair<UPRawValue, UPRawValue>? {
    if (!absolute || offset.isEmpty()) return null
    val top = offset[0]
    // JavaScript's `||` also falls through for 0, "" and null, so a second entry of 0
    // deliberately resolves back to `top` exactly like upstream.
    val right = offset.getOrNull(1)?.takeIf { upBadgeOffsetTruthy(it) } ?: top
    return top to right
}

private fun upBadgeOffsetTruthy(value: UPRawValue): Boolean = when (value) {
    null -> false
    is Boolean -> value
    is Number -> value.toDouble() != 0.0
    is String -> value.isNotEmpty() && value != "0"
    else -> true
}

/**
 * `style.backgroundColor = getBagColor(color)` runs only for `autoBgColor > 0 && color`,
 * so a zero or malformed lightness leaves the tag on its type colour.
 */
internal fun upTagAutoBackgroundColor(color: String, autoBgColor: UPRawValue): String? {
    val lightness = autoBgColor.asFiniteFloatOrNull()?.takeIf { it > 0f } ?: return null
    if (color.isBlank()) return null
    return upGenLightColor(color, lightness.toDouble())
}

/**
 * Kotlin port of `genLightColor(textColor, lightness)`: keep the hue and saturation,
 * clamp the lightness to at most 95% and re-encode as `#rrggbb`. Returns `null` for the
 * colour formats upstream throws on (it only parses hex and `rgb()`/`rgba()`).
 *
 * The arithmetic runs in [Double] on purpose. Several channels land exactly on a `.5`
 * rounding boundary (`#2979ff` at 95% lightness resolves to 229.49999999999997), so
 * `Float` precision would round a channel one step away from what the JavaScript
 * original produces.
 */
internal fun upGenLightColor(color: String, lightness: Double = 95.0): String? {
    val rgb = upParseColorChannels(color) ?: return null
    val hsl = upRgbToHsl(rgb[0], rgb[1], rgb[2])
    return upHslToHex(hsl[0], hsl[1], min(lightness, 95.0))
}

/** `parseColorWithoutDOM`: `#rgb`, `#rrggbb` and `rgb()`/`rgba()` only. */
private fun upParseColorChannels(color: String): IntArray? {
    val text = color.trim().lowercase()
    if (text.startsWith("#")) {
        val hex = text.removePrefix("#")
        val full = if (hex.length == 3) hex.map { "$it$it" }.joinToString("") else hex
        if (full.length < 6 || full.take(6).any { it.digitToIntOrNull(16) == null }) return null
        return intArrayOf(
            full.substring(0, 2).toInt(16),
            full.substring(2, 4).toInt(16),
            full.substring(4, 6).toInt(16),
        )
    }
    val match = Regex("""rgba?\((\d+),\s*(\d+),\s*(\d+)""").find(text) ?: return null
    return intArrayOf(
        match.groupValues[1].toInt(),
        match.groupValues[2].toInt(),
        match.groupValues[3].toInt(),
    )
}

/** `rgbToHsl`, including upstream's `toFixed(1)` rounding of every channel. */
private fun upRgbToHsl(red: Int, green: Int, blue: Int): DoubleArray {
    val r = red / 255.0
    val g = green / 255.0
    val b = blue / 255.0
    val maximum = max(r, max(g, b))
    val minimum = min(r, min(g, b))
    val lightness = (maximum + minimum) / 2.0
    if (maximum == minimum) return doubleArrayOf(0.0, 0.0, upToFixedOne(lightness * 100.0))

    val delta = maximum - minimum
    val saturation = if (lightness > 0.5) delta / (2.0 - maximum - minimum) else delta / (maximum + minimum)
    val hue = when (maximum) {
        r -> (g - b) / delta + if (g < b) 6.0 else 0.0
        g -> (b - r) / delta + 2.0
        else -> (r - g) / delta + 4.0
    }
    return doubleArrayOf(
        upToFixedOne(hue * 60.0),
        upToFixedOne(saturation * 100.0),
        upToFixedOne(lightness * 100.0),
    )
}

/** `hslToHex`, keeping upstream's `a = s * min(l, 1 - l) / 100` formulation. */
private fun upHslToHex(hue: Double, saturation: Double, lightnessPercent: Double): String {
    val lightness = lightnessPercent / 100.0
    val amplitude = saturation * min(lightness, 1.0 - lightness) / 100.0
    fun channel(offset: Int): String {
        val k = (offset + hue / 30.0).mod(12.0)
        val value = lightness - amplitude * max(min(min(k - 3.0, 9.0 - k), 1.0), -1.0)
        return Math.round(255.0 * value).toInt().coerceIn(0, 255).toString(16).padStart(2, '0')
    }
    return "#${channel(0)}${channel(8)}${channel(4)}"
}

/**
 * `Number.prototype.toFixed(1)`: pick the one-decimal value closest to the double's exact
 * value, breaking ties upwards. `HALF_UP` on the exact `BigDecimal` expansion does the
 * same for the non-negative inputs `rgbToHsl` produces.
 */
private fun upToFixedOne(value: Double): Double =
    BigDecimal(value).setScale(1, RoundingMode.HALF_UP).toDouble()
