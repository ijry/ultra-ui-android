package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upStringOrDefault
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** `mode` values accepted by the popup that wraps upstream `u-picker`. */
internal val UPPickerPopupModes: Set<String> = setOf("top", "bottom", "left", "right", "center")

/** `border` values accepted by `u-input`, reused by the `hasInput` trigger. */
internal val UPPickerInputBorders: Set<String> = setOf("surround", "bottom", "none")

private val UPPickerInputOverrideKeys: Set<String> = setOf(
    "border",
    "placeholder",
    "disabled",
    "disabledcolor",
    "color",
    "fontsize",
    "inputalign",
    "shape",
    "clearable",
    "readonly",
    "prefixicon",
    "suffixicon",
    "maxlength",
    "type",
    "password",
    "cursorcolor",
    "customstyle",
    "placeholderstyle",
)

internal fun upPickerPopupMode(
    mode: String,
    diagnostics: UPCompatibilityDiagnostics,
    component: String,
): String = upSafeEnum(mode, UPPickerPopupModes, "bottom", diagnostics, component, "popupMode")

/**
 * uview passes `rightSlot` to `u-toolbar`, which then renders the `#right` slot *instead of*
 * the confirm label. The confirm text and the confirm event both disappear with it.
 */
internal fun upPickerToolbarShowsConfirm(toolbarRightSlot: Boolean): Boolean = !toolbarRightSlot

/**
 * uview's `inputBorder` is forwarded straight to `u-input`, whose own default is `surround`.
 * `u-picker/props.js` reads it from `defProps.input.inputBorder`, which upstream never defines,
 * so the Android default keeps the boolean the generator emits and maps it onto the enum.
 */
internal fun upPickerInputBorder(
    value: UPRawValue,
    diagnostics: UPCompatibilityDiagnostics,
    component: String,
): String = when (value) {
    is Boolean -> if (value) "surround" else "none"
    else -> upSafeEnum(
        value.upStringOrDefault("surround"),
        UPPickerInputBorders,
        "surround",
        diagnostics,
        component,
        "inputBorder",
    )
}

/**
 * Mirrors uview's `inputPropsInner`: the picker's own fields build the base props and the
 * caller-supplied `inputProps` map overrides them, key by key.
 */
internal fun upPickerInputProps(
    label: String,
    inputBorder: UPRawValue,
    placeholder: UPRawValue,
    disabled: Boolean,
    disabledColor: String,
    inputProps: UPStyleInput,
    diagnostics: UPCompatibilityDiagnostics,
    component: String,
): UPInputProps {
    var result = UPInputProps(
        modelValue = label,
        value = label,
        // The trigger only mirrors the confirmed value; upstream covers it with a
        // `<cover-view>` so the tap opens the picker instead of focusing the field.
        readonly = true,
        border = upPickerInputBorder(inputBorder, diagnostics, component),
        placeholder = placeholder,
        disabled = disabled,
        disabledColor = disabledColor,
    )
    inputProps.upStringKeyMapOrEmpty().forEach { (rawKey, value) ->
        result = when (val key = rawKey.upPickerInputPropKey()) {
            "border" -> result.copy(border = upPickerInputBorder(value, diagnostics, component))
            "placeholder" -> result.copy(placeholder = value)
            "disabled" -> result.copy(disabled = value.upBooleanValue(result.disabled))
            "disabledcolor" -> result.copy(disabledColor = value.upStringValueOrEmpty())
            "color" -> result.copy(color = value.upStringValueOrEmpty())
            "fontsize" -> result.copy(fontSize = value)
            "inputalign" -> result.copy(inputAlign = value.upStringValueOrEmpty())
            "shape" -> result.copy(shape = value.upStringValueOrEmpty())
            "clearable" -> result.copy(clearable = value.upBooleanValue(result.clearable))
            "readonly" -> result.copy(readonly = value.upBooleanValue(result.readonly))
            "prefixicon" -> result.copy(prefixIcon = value.upStringValueOrEmpty())
            "suffixicon" -> result.copy(suffixIcon = value.upStringValueOrEmpty())
            "maxlength" -> result.copy(maxlength = value)
            "type" -> result.copy(type = value.upStringValueOrEmpty())
            "password" -> result.copy(password = value.upBooleanValue(result.password))
            "cursorcolor" -> result.copy(cursorColor = value.upStringValueOrEmpty())
            "customstyle" -> result.copy(customStyle = value)
            "placeholderstyle" -> result.copy(placeholderStyle = value)
            else -> {
                require(key !in UPPickerInputOverrideKeys)
                diagnostics.report(component, "inputProps.$rawKey", value, "Unsupported u-input override; ignored.")
                result
            }
        }
    }
    return result
}

/**
 * uview keeps a `maskStyleInner` that only overrides the built-in wheel mask when the caller
 * actually passes something. Compose has no "was this argument supplied" signal, so a non-empty
 * value stands in for the explicit prop.
 */
internal fun upPickerMaskStyleDeclared(maskStyle: UPStyleInput): Boolean = when (maskStyle) {
    null -> false
    is String -> maskStyle.isNotBlank()
    is Map<*, *> -> maskStyle.isNotEmpty()
    is Collection<*> -> maskStyle.isNotEmpty()
    else -> true
}

/**
 * Mirrors uview's `inputLabel`: object columns resolve every confirmed value back to its
 * `keyName` label, plain columns join the confirmed values as they are.
 */
internal fun upPickerInputLabel(
    columns: List<UPRawValue>,
    modelValue: List<UPRawValue>,
    keyName: String,
    valueName: String,
): String {
    val firstColumn = columns.firstOrNull().upItemsOrEmpty()
    val objectColumn = firstColumn.firstOrNull().upStringKeyMapOrEmpty().isNotEmpty()
    if (!objectColumn) return modelValue.joinToString("/") { it.upStringValueOrEmpty() }
    return firstColumn
        .filter { option -> modelValue.any { it.upLooseEquals(option.upStringKeyMapOrEmpty()[valueName]) } }
        .joinToString("/") { option -> actionOrOptionText(option, keyName) }
}

/** Mirrors uview's `getInputValue`, including the per-mode fallback formats. */
internal fun upDatetimeInputLabel(mode: String, format: String, value: UPRawValue): String {
    if (value is Number && value.toDouble() == 0.0) return ""
    val text = value.upStringOrDefault().trim()
    if (text.isEmpty()) return ""
    // `time`/`timesecond` carry a `HH:mm[:ss]` string, which upstream shows verbatim.
    if (mode == "time" || mode == "timesecond") return text
    val millis = upDatetimeMillis(value) ?: return text
    val pattern = upJavaDateFormat(format.trim().ifEmpty { upDatetimeDefaultFormat(mode) })
    return runCatching { SimpleDateFormat(pattern, Locale.ROOT).format(Date(millis)) }.getOrDefault(text)
}

internal fun upDatetimeDefaultFormat(mode: String): String = when (mode) {
    "date" -> "YYYY-MM-DD"
    "year-month" -> "YYYY-MM"
    "datehour" -> "YYYY-MM-DD HH"
    "datetimesecond" -> "YYYY-MM-DD HH:mm:ss"
    "time" -> "HH:mm"
    "timesecond" -> "HH:mm:ss"
    else -> "YYYY-MM-DD HH:mm"
}

/**
 * dayjs tokens are mostly SimpleDateFormat tokens already, but `YYYY` is a week-year and `D`
 * a day-of-year in Java, so those two have to be lowered before formatting.
 */
internal fun upJavaDateFormat(pattern: String): String = pattern
    .replace("YYYY", "yyyy")
    .replace("YY", "yy")
    .replace("DD", "dd")
    .replace("D", "d")

private fun upDatetimeMillis(value: UPRawValue): Long? = when (value) {
    is Number -> value.toLong()
    is String -> value.trim().takeIf(String::isNotEmpty)?.let { text ->
        text.toLongOrNull() ?: upParseDatetimeText(text)
    }
    else -> null
}

private fun upParseDatetimeText(value: String): Long? =
    listOf("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH", "yyyy-MM-dd", "yyyy-MM").firstNotNullOfOrNull { pattern ->
        runCatching { SimpleDateFormat(pattern, Locale.ROOT).apply { isLenient = false }.parse(value)?.time }.getOrNull()
    }

private fun String.upPickerInputPropKey(): String = lowercase(Locale.ROOT).replace("-", "").replace("_", "")
