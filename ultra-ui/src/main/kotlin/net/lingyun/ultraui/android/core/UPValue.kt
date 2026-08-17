package net.lingyun.ultraui.android.core

/** A raw value emitted by a cross-platform source generator. */
public typealias UPRawValue = Any?

/** The `customStyle` representation accepted by uview-plus props. */
public typealias UPStyleInput = Any?

/** Resolves the modern v-model prop before the legacy `value` alias. */
public fun resolveUPModelValue(modelValue: UPRawValue?, value: UPRawValue): UPRawValue = modelValue ?: value

/** Returns a finite floating point representation, or `null` for malformed values. */
public fun UPRawValue.asFiniteFloatOrNull(): Float? = when (this) {
    is Number -> toFloat().takeIf(Float::isFinite)
    is String -> trim().toFloatOrNull()?.takeIf(Float::isFinite)
    else -> null
}

/** Safely coerces a generated value to an integer. */
public fun UPRawValue.upIntOrDefault(fallback: Int): Int = when (this) {
    is Byte -> toInt()
    is Short -> toInt()
    is Int -> this
    is Long -> toInt()
    is Float -> toInt()
    is Double -> toInt()
    is Number -> toInt()
    is String -> trim().toIntOrNull() ?: trim().toDoubleOrNull()?.toInt() ?: fallback
    else -> fallback
}

/** Safely coerces a generated value to a float. */
public fun UPRawValue.upFloatOrDefault(fallback: Float): Float = when (this) {
    is Number -> toFloat().takeIf(Float::isFinite) ?: fallback
    is String -> trim().toFloatOrNull()?.takeIf(Float::isFinite) ?: fallback
    else -> fallback
}

/** Safely coerces a generated value to a double. */
public fun UPRawValue.upDoubleOrDefault(fallback: Double): Double = when (this) {
    is Number -> toDouble().takeIf(Double::isFinite) ?: fallback
    is String -> trim().toDoubleOrNull()?.takeIf(Double::isFinite) ?: fallback
    else -> fallback
}

/** Safely coerces a generated value to a boolean without throwing on JSON strings. */
public fun UPRawValue.upBooleanOrDefault(fallback: Boolean): Boolean = when (this) {
    is Boolean -> this
    is Number -> toDouble().let { if (it == 1.0) true else if (it == 0.0) false else fallback }
    is String -> when (trim().lowercase()) {
        "true", "1", "yes", "on" -> true
        "false", "0", "no", "off" -> false
        else -> fallback
    }
    else -> fallback
}

/** Safely coerces a generated value to text. */
public fun UPRawValue.upStringOrDefault(fallback: String = ""): String = when (this) {
    null -> fallback
    is String -> this
    else -> toString()
}

/** Returns a list for JSON arrays while preserving each raw item. */
@Suppress("UNCHECKED_CAST")
public fun UPRawValue.upListOrEmpty(): List<UPRawValue> = when (this) {
    is List<*> -> this as List<UPRawValue>
    is Array<*> -> toList() as List<UPRawValue>
    else -> emptyList()
}

/** Clamps a percentage and reports malformed or out-of-range generated values. */
public fun upClampPercentage(
    value: UPRawValue,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    component: String,
): Float {
    val parsed = value.asFiniteFloatOrNull()
    if (parsed == null) {
        diagnostics.report(component, "percentage", value, "Malformed percentage; using 0.")
        return 0f
    }
    if (parsed < 0f || parsed > 100f) {
        diagnostics.report(component, "percentage", value, "Percentage is outside 0..100; clamped.")
    }
    return parsed.coerceIn(0f, 100f)
}
