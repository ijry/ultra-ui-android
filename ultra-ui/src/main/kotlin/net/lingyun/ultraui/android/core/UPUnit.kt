package net.lingyun.ultraui.android.core

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Converts the dimension values accepted by uview-plus into Compose [Dp] safely. */
public object UPUnit {
    private val dimensionPattern = Regex(
        pattern = "^([+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+))\\s*(rpx|px|dp)?$",
        option = RegexOption.IGNORE_CASE,
    )

    /**
     * Resolves `rpx` against a 750-wide uview design canvas; numeric, `px`, and
     * `dp` values map one-to-one to [Dp]. Invalid input returns [fallback].
     */
    public fun toDp(value: UPRawValue, availableScreenWidth: Dp, fallback: Dp): Dp =
        parseOrNull(value, availableScreenWidth) ?: fallback

    internal fun parseOrNull(value: UPRawValue, availableScreenWidth: Dp): Dp? = when (value) {
        is Dp -> value.takeIf { it.value.isFinite() }
        is Number -> value.toFloat().takeIf(Float::isFinite)?.dp
        is String -> parseString(value, availableScreenWidth)
        else -> null
    }

    private fun parseString(raw: String, availableScreenWidth: Dp): Dp? {
        val match = dimensionPattern.matchEntire(raw.trim()) ?: return null
        val amount = match.groupValues[1].toFloatOrNull()?.takeIf(Float::isFinite) ?: return null
        return when (match.groupValues[2].lowercase()) {
            "rpx" -> (amount * availableScreenWidth.value / 750f)
                .takeIf(Float::isFinite)
                ?.dp
            "", "px", "dp" -> amount.dp
            else -> null
        }
    }
}
