package net.lingyun.ultraui.android.components

import kotlin.math.roundToInt

/** HSL (h:0..360, s:0..100, l:0..100) triple. */
internal data class UPHsl(val h: Float, val s: Float, val l: Float)

private fun hue2rgb(p: Float, q: Float, tIn: Float): Float {
    var t = tIn
    if (t < 0) t += 1f
    if (t > 1) t -= 1f
    return when {
        t < 1f / 6f -> p + (q - p) * 6f * t
        t < 1f / 2f -> q
        t < 2f / 3f -> p + (q - p) * (2f / 3f - t) * 6f
        else -> p
    }
}

/** `hslToRgb` then to `#rrggbb`, matching upstream's rounding. */
internal fun upColorHslToHex(h: Float, s: Float, l: Float): String {
    val hn = h / 360f
    val sn = s / 100f
    val ln = l / 100f
    val r: Float
    val g: Float
    val b: Float
    if (sn == 0f) {
        r = ln; g = ln; b = ln
    } else {
        val q = if (ln < 0.5f) ln * (1 + sn) else ln + sn - ln * sn
        val p = 2 * ln - q
        r = hue2rgb(p, q, hn + 1f / 3f)
        g = hue2rgb(p, q, hn)
        b = hue2rgb(p, q, hn - 1f / 3f)
    }
    fun c(v: Float) = (v * 255f).roundToInt().coerceIn(0, 255)
    return "#%02x%02x%02x".format(c(r), c(g), c(b))
}

/** `#rgb`/`#rrggbb` → HSL. Malformed input falls back to red. */
internal fun upColorHexToHsl(hex: String): UPHsl {
    val clean = hex.trim().removePrefix("#")
    val expanded = when (clean.length) {
        3 -> clean.map { "$it$it" }.joinToString("")
        6 -> clean
        else -> "ff0000"
    }
    val r = (expanded.substring(0, 2).toIntOrNull(16) ?: 255) / 255f
    val g = (expanded.substring(2, 4).toIntOrNull(16) ?: 0) / 255f
    val b = (expanded.substring(4, 6).toIntOrNull(16) ?: 0) / 255f
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val l = (max + min) / 2f
    if (max == min) return UPHsl(0f, 0f, l * 100f)
    val d = max - min
    val s = if (l > 0.5f) d / (2f - max - min) else d / (max + min)
    var h = when (max) {
        r -> (g - b) / d + (if (g < b) 6f else 0f)
        g -> (b - r) / d + 2f
        else -> (r - g) / d + 4f
    }
    h /= 6f
    return UPHsl(h * 360f, s * 100f, l * 100f)
}
