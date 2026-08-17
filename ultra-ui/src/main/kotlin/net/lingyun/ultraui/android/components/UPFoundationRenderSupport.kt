package net.lingyun.ultraui.android.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.UPUnit
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upFloatOrDefault
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upStringOrDefault

internal val UPPrimary: Color get() = UPTheme.Primary

internal fun upTypeColor(type: String, fallback: Color = UPTheme.Info): Color = when (type.lowercase()) {
    "primary" -> UPTheme.Primary
    "success" -> UPTheme.Success
    "warning" -> UPTheme.Warning
    "error" -> UPTheme.Error
    "info" -> UPTheme.Info
    else -> fallback
}

internal fun upTextColor(value: String, fallback: Color = UPTheme.Content): Color = UPColor.parse(value, fallback)

internal fun upRawText(value: UPRawValue): String = value?.toString() ?: ""

internal fun upShape(shape: String, radius: Dp = 4.dp): RoundedCornerShape = when (shape.lowercase()) {
    "circle", "round" -> RoundedCornerShape(percent = 50)
    else -> RoundedCornerShape(radius)
}

@Composable
internal fun upRawDp(value: UPRawValue, fallback: Dp): Dp =
    UPUnit.toDp(value, availableScreenWidth(), fallback)

@Composable
internal fun upPercentageWidth(value: UPRawValue, fallback: Float = 1f): Float {
    val text = value as? String
    if (text != null && text.trim().endsWith('%')) {
        return text.trim().dropLast(1).toFloatOrNull()?.div(100f)?.coerceIn(0f, 1f) ?: fallback
    }
    return value.asFiniteFloatOrNull()?.div(100f)?.coerceIn(0f, 1f) ?: fallback
}

@Composable
internal fun availableScreenWidth(): Dp = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp
    .takeIf { it > 0 }
    ?.dp
    ?: 750.dp

internal fun UPRawValue.upLongOrDefault(fallback: Long): Long =
    when (this) {
        is Number -> toLong()
        is String -> trim().toLongOrNull() ?: trim().toDoubleOrNull()?.toLong() ?: fallback
        else -> fallback
    }

internal fun UPRawValue.upBooleanOrDefaultLocal(fallback: Boolean): Boolean =
    when (this) {
        is Boolean -> this
        else -> toString().toBooleanStrictOrNull() ?: fallback
    }
