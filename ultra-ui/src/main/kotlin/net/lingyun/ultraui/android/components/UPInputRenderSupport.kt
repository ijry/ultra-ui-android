package net.lingyun.ultraui.android.components

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upStringOrDefault

internal fun UPRawValue.upInputString(): String = when (this) {
    null -> ""
    is String -> this
    else -> toString()
}

internal fun normalizedUPMaxLength(value: UPRawValue): Int = value.upIntOrDefault(-1).let { length ->
    if (length < 0) Int.MAX_VALUE else length.coerceAtMost(100_000)
}

internal fun limitUPText(value: String, maxlength: UPRawValue): String {
    val limit = normalizedUPMaxLength(maxlength)
    return if (value.length <= limit) value else value.take(limit)
}

/** Applies a generated formatter without allowing an invalid callback to crash rendering. */
@Suppress("UNCHECKED_CAST")
internal fun formatUPTextSafely(
    value: String,
    formatter: UPRawValue,
    diagnostics: UPCompatibilityDiagnostics,
    component: String,
): String {
    if (formatter == null) return value
    val function = formatter as? Function1<*, *> ?: run {
        diagnostics.report(component, "formatter", formatter, "Formatter is not callable; using the unformatted value.")
        return value
    }
    return runCatching {
        (function as (String) -> Any?).invoke(value)?.toString() ?: ""
    }.onFailure { throwable ->
        diagnostics.report(component, "formatter", formatter, "Formatter failed: ${throwable.message ?: "unknown error"}.")
    }.getOrDefault(value)
}

internal fun keyboardTypeForUPInput(type: String): KeyboardType = when (type.trim().lowercase()) {
    "number", "digit" -> KeyboardType.Number
    "tel" -> KeyboardType.Phone
    "email" -> KeyboardType.Email
    "url" -> KeyboardType.Uri
    "decimal" -> KeyboardType.Decimal
    else -> KeyboardType.Text
}

internal fun imeActionForUPInput(confirmType: String, multiline: Boolean = false): ImeAction = when {
    multiline && confirmType.trim().lowercase() in setOf("return", "newline") -> ImeAction.Default
    confirmType.trim().lowercase() == "search" -> ImeAction.Search
    confirmType.trim().lowercase() == "next" -> ImeAction.Next
    confirmType.trim().lowercase() == "go" -> ImeAction.Go
    confirmType.trim().lowercase() == "send" -> ImeAction.Send
    else -> ImeAction.Done
}

internal fun textAlignForUPInput(value: String): TextAlign = when (value.trim().lowercase()) {
    "center" -> TextAlign.Center
    "right", "end" -> TextAlign.End
    else -> TextAlign.Start
}

internal fun numericUPValue(value: UPRawValue, fallback: Float): Float =
    value.asFiniteFloatOrNull() ?: fallback
