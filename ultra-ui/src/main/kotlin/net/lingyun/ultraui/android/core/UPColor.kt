package net.lingyun.ultraui.android.core

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

/** A right-facing two-stop gradient supported by uview button/background styles. */
public data class UPLinearGradient(
    val start: Color,
    val end: Color,
)

/** Safe CSS-like color parsing for uview-plus compatible props. */
public object UPColor {
    /** Returns [fallback] for a malformed or unsupported value rather than throwing. */
    public fun parse(value: UPRawValue, fallback: Color): Color = parseOrNull(value) ?: fallback

    /** Returns a parsed color, or `null` when the value is not a supported color. */
    public fun parseOrNull(value: UPRawValue): Color? = when (value) {
        is Color -> value
        is String -> parseText(value)
        else -> null
    }

    /**
     * Parses only `linear-gradient(to right, colorA, colorB)`. Other CSS gradients
     * deliberately remain unsupported so native rendering is deterministic.
     */
    public fun parseRightLinearGradient(value: UPRawValue): UPLinearGradient? {
        val text = (value as? String)?.trim() ?: return null
        if (!text.startsWith("linear-gradient(", ignoreCase = true) || !text.endsWith(')')) return null

        val inside = text.substringAfter('(').dropLast(1)
        val parts = splitTopLevel(inside, ',').map(String::trim)
        if (parts.size != 3 || !parts[0].equals("to right", ignoreCase = true)) return null

        val start = parseOrNull(parts[1]) ?: return null
        val end = parseOrNull(parts[2]) ?: return null
        return UPLinearGradient(start = start, end = end)
    }

    private fun parseText(raw: String): Color? {
        val text = raw.trim()
        if (text.equals("transparent", ignoreCase = true)) return Color.Transparent
        UPTheme.colorFor(text)?.let { return it }
        if (text.startsWith('#')) return parseHex(text.drop(1))
        return parseRgbFunction(text)
    }

    private fun parseHex(value: String): Color? {
        if (!value.all { it.digitToIntOrNull(16) != null }) return null
        return when (value.length) {
            3 -> colorFromRgba(
                red = duplicateNibble(value[0]),
                green = duplicateNibble(value[1]),
                blue = duplicateNibble(value[2]),
                alpha = 0xFF,
            )
            4 -> colorFromRgba(
                red = duplicateNibble(value[0]),
                green = duplicateNibble(value[1]),
                blue = duplicateNibble(value[2]),
                alpha = duplicateNibble(value[3]),
            )
            6 -> colorFromRgba(
                red = value.substring(0, 2).toInt(16),
                green = value.substring(2, 4).toInt(16),
                blue = value.substring(4, 6).toInt(16),
                alpha = 0xFF,
            )
            8 -> colorFromRgba(
                red = value.substring(0, 2).toInt(16),
                green = value.substring(2, 4).toInt(16),
                blue = value.substring(4, 6).toInt(16),
                alpha = value.substring(6, 8).toInt(16),
            )
            else -> null
        }
    }

    private fun parseRgbFunction(value: String): Color? {
        val function = Regex("^(rgb|rgba)\\((.*)\\)$", RegexOption.IGNORE_CASE).matchEntire(value) ?: return null
        val name = function.groupValues[1].lowercase()
        val values = splitTopLevel(function.groupValues[2], ',').map(String::trim)
        if ((name == "rgb" && values.size != 3) || (name == "rgba" && values.size != 4)) return null

        val red = parseRgbChannel(values[0]) ?: return null
        val green = parseRgbChannel(values[1]) ?: return null
        val blue = parseRgbChannel(values[2]) ?: return null
        val alpha = if (name == "rgba") parseAlpha(values[3]) ?: return null else 0xFF
        return colorFromRgba(red, green, blue, alpha)
    }

    private fun parseRgbChannel(value: String): Int? = when {
        value.endsWith('%') -> value.dropLast(1).trim().toFloatOrNull()
            ?.takeIf { it in 0f..100f }
            ?.let { (it * 2.55f).roundToInt().coerceIn(0, 0xFF) }
        else -> value.toFloatOrNull()
            ?.takeIf { it in 0f..255f }
            ?.roundToInt()
    }

    private fun parseAlpha(value: String): Int? = when {
        value.endsWith('%') -> value.dropLast(1).trim().toFloatOrNull()
            ?.takeIf { it in 0f..100f }
            ?.let { (it * 2.55f).roundToInt().coerceIn(0, 0xFF) }
        else -> value.toFloatOrNull()
            ?.takeIf { it in 0f..1f }
            ?.let { (it * 0xFF).roundToInt().coerceIn(0, 0xFF) }
    }

    private fun duplicateNibble(value: Char): Int = "${value}${value}".toInt(16)

    private fun colorFromRgba(red: Int, green: Int, blue: Int, alpha: Int): Color = Color(
        red = red / 255f,
        green = green / 255f,
        blue = blue / 255f,
        alpha = alpha / 255f,
    )

    internal fun splitTopLevel(value: String, delimiter: Char): List<String> {
        val parts = mutableListOf<String>()
        var depth = 0
        var start = 0
        value.forEachIndexed { index, character ->
            when (character) {
                '(' -> depth += 1
                ')' -> if (depth > 0) depth -= 1
                delimiter -> if (depth == 0) {
                    parts += value.substring(start, index)
                    start = index + 1
                }
            }
        }
        parts += value.substring(start)
        return parts
    }
}
