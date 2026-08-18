package net.lingyun.ultraui.android.components

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upListOrEmpty

@Suppress("UNCHECKED_CAST")
internal fun UPRawValue.upStringKeyMapOrEmpty(): Map<String, UPRawValue> = when (this) {
    is Map<*, *> -> entries
        .mapNotNull { (key, value) -> (key as? String)?.let { it to value } }
        .toMap()
    else -> emptyMap()
}

internal fun UPRawValue.upStringValueOrEmpty(): String = when (this) {
    null -> ""
    is String -> this
    else -> toString()
}

internal fun UPRawValue.upBooleanValue(default: Boolean = false): Boolean = when (this) {
    is Boolean -> this
    is Number -> when (toInt()) {
        0 -> false
        1 -> true
        else -> default
    }
    is String -> when (trim().lowercase()) {
        "true", "1", "yes", "on" -> true
        "false", "0", "no", "off" -> false
        else -> default
    }
    else -> default
}

internal fun UPRawValue.upTextUnitOr(default: TextUnit): TextUnit =
    asFiniteFloatOrNull()?.takeIf { it >= 0f }?.sp ?: default

internal fun UPRawValue.upLooseEquals(other: UPRawValue): Boolean =
    this == other || (this != null && other != null && toString() == other.toString())

internal fun UPRawValue.upItemsOrEmpty(): List<UPRawValue> = upListOrEmpty()

internal fun actionOrOptionText(item: UPRawValue, key: String, fallback: String = ""): String {
    val map = item.upStringKeyMapOrEmpty()
    return map[key].upStringValueOrEmpty().ifEmpty {
        if (map.isEmpty()) item.upStringValueOrEmpty() else fallback
    }
}
