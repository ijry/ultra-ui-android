package net.lingyun.ultraui.android.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.report
import java.util.Calendar
import java.util.Date
import kotlin.math.abs
import kotlin.math.floor

internal const val UPFormComponentName: String = "UPForm"
internal const val UPFormItemComponentName: String = "UPFormItem"

/**
 * A single validation failure, mirroring the `complementError` output of the
 * bundled async-validator (`libs/util/async-validator.js`).
 *
 * [field] is the last segment of the property chain, exactly like the upstream
 * `fullField`. [prop] keeps the full `a.b.c` path so callers can map an error
 * back to the owning `UPFormItem`, which the web build does not need because it
 * mutates the child component instance directly.
 */
public data class UPFormError(
    val message: String,
    val field: String,
    val prop: String = field,
)

/**
 * Declarative rule mirroring the object literals accepted by uview-plus
 * `u-form` / `u-form-item`.
 *
 * [pattern] accepts a [Regex] or a [String] just like the upstream `pattern`
 * key, and [validator] mirrors the synchronous return shapes uview-plus
 * documents (`true` / `false` / `Array` / `Error`).
 */
public data class UPFormRule(
    val required: Boolean = false,
    val message: String? = null,
    val trigger: List<String> = emptyList(),
    val type: String? = null,
    val min: Number? = null,
    val max: Number? = null,
    val len: Number? = null,
    val pattern: UPRawValue = null,
    val whitespace: Boolean = false,
    val enum: List<UPRawValue> = emptyList(),
    val transform: ((UPRawValue) -> UPRawValue)? = null,
    val validator: ((UPFormRule, UPRawValue) -> UPRawValue)? = null,
)

/** Default English texts copied verbatim from async-validator `messages`. */
private object UPFormMessages {
    const val Default: String = "Validation error on field %s"
    const val Required: String = "%s is required"
    const val Enum: String = "%s must be one of %s"
    const val Whitespace: String = "%s cannot be empty"
    const val PatternMismatch: String = "%s value %s does not match pattern %s"

    val types: Map<String, String> = mapOf(
        "string" to "%s is not a %s",
        "method" to "%s is not a %s (function)",
        "array" to "%s is not an %s",
        "object" to "%s is not an %s",
        "number" to "%s is not a %s",
        "date" to "%s is not a %s",
        "boolean" to "%s is not a %s",
        "integer" to "%s is not an %s",
        "float" to "%s is not a %s",
        "regexp" to "%s is not a valid %s",
        "email" to "%s is not a valid %s",
        "url" to "%s is not a valid %s",
        "hex" to "%s is not a valid %s",
    )

    val ranges: Map<String, Map<String, String>> = mapOf(
        "string" to mapOf(
            "len" to "%s must be exactly %s characters",
            "min" to "%s must be at least %s characters",
            "max" to "%s cannot be longer than %s characters",
            "range" to "%s must be between %s and %s characters",
        ),
        "number" to mapOf(
            "len" to "%s must equal %s",
            "min" to "%s cannot be less than %s",
            "max" to "%s cannot be greater than %s",
            "range" to "%s must be between %s and %s",
        ),
        "array" to mapOf(
            "len" to "%s must be exactly %s in length",
            "min" to "%s cannot be less than %s in length",
            "max" to "%s cannot be greater than %s in length",
            "range" to "%s must be between %s and %s in length",
        ),
    )
}

private val UPFormPlaceholderPattern = Regex("%[sdj%]")
private val UPFormIntegerPattern = Regex("""^(-)?\d+$""")
private val UPFormFloatPattern = Regex("""^(-)?\d+(\.\d+)?$""")
private val UPFormSurrogatePattern = Regex("""[\uD800-\uDBFF][\uDC00-\uDFFF]""")
private val UPFormEmailPattern =
    Regex("""^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$""")
private val UPFormUrlPattern = Regex(
    """^(?!mailto:)(?:(?:http|https|ftp)://|//)(?:\S+(?::\S*)?@)?(?:(?:(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[0-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]+-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]+-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))|localhost)(?::\d{2,5})?(?:(/|\?|#)[^\s]*)?$""",
    RegexOption.IGNORE_CASE,
)
private val UPFormHexPattern = Regex("""^#?([a-f0-9]{6}|[a-f0-9]{3})$""", RegexOption.IGNORE_CASE)

/** Rule types async-validator resolves to a built-in validator. */
private val UPFormValidatorTypes: Set<String> = setOf(
    "string",
    "method",
    "number",
    "boolean",
    "regexp",
    "integer",
    "float",
    "array",
    "object",
    "enum",
    "pattern",
    "date",
    "url",
    "hex",
    "email",
    "required",
    "any",
)

/** Types whose emptiness check also treats `''` as empty (`isNativeStringType`). */
private val UPFormStringTypes: Set<String> = setOf("string", "url", "hex", "email", "pattern")

/** Types validated through the `types` predicate table instead of `typeof`. */
private val UPFormCustomTypes: List<String> = listOf(
    "integer",
    "float",
    "array",
    "regexp",
    "object",
    "method",
    "email",
    "number",
    "date",
    "url",
    "hex",
)

/** Mirrors JavaScript truthiness, used for `rule.pattern` and `borderBottom`. */
internal fun upFormTruthy(value: UPRawValue): Boolean = when (value) {
    null -> false
    is Boolean -> value
    is String -> value.isNotEmpty()
    is Number -> value.toDouble() != 0.0 && !value.toDouble().isNaN()
    else -> true
}

/** Mirrors `Number(value)` so `isNaN` based checks keep their JavaScript results. */
private fun upFormJsNumber(value: Any?): Double = when (value) {
    null -> 0.0
    is Boolean -> if (value) 1.0 else 0.0
    is Number -> value.toDouble()
    is String -> if (value.trim().isEmpty()) 0.0 else value.trim().toDoubleOrNull() ?: Double.NaN
    is List<*> -> when {
        value.isEmpty() -> 0.0
        value.size == 1 -> upFormJsNumber(value.first())
        else -> Double.NaN
    }
    else -> Double.NaN
}

/** Mirrors `String(Number(value))`, so integral values never render as `1.0`. */
private fun upFormJsNumberText(value: Any?): String {
    val number = upFormJsNumber(value)
    return when {
        number.isNaN() -> "NaN"
        number == Double.POSITIVE_INFINITY -> "Infinity"
        number == Double.NEGATIVE_INFINITY -> "-Infinity"
        floor(number) == number && abs(number) < 1e15 -> number.toLong().toString()
        else -> number.toString()
    }
}

/** Mirrors `String(value)` for the value shapes a rule can carry. */
private fun upFormJsText(value: Any?): String = when (value) {
    null -> "null"
    is Regex -> "/${value.pattern}/" + if (RegexOption.IGNORE_CASE in value.options) "i" else ""
    is Number -> upFormJsNumberText(value)
    is List<*> -> value.joinToString(",") { upFormJsText(it) }
    else -> value.toString()
}

/** Minimal `JSON.stringify` used by the `%j` placeholder. */
private fun upFormJsJson(value: Any?): String = when (value) {
    null -> "null"
    is Boolean -> value.toString()
    is Number -> upFormJsNumberText(value)
    is Map<*, *> -> value.entries.joinToString(",", "{", "}") { (key, item) ->
        upFormJsJson(key.toString()) + ":" + upFormJsJson(item)
    }
    is List<*> -> value.joinToString(",", "[", "]") { upFormJsJson(it) }
    else -> "\"" + value.toString().replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}

/**
 * Kotlin port of the async-validator `format()` helper: `%s` / `%d` / `%j` /
 * `%%` placeholders, untouched placeholders once the arguments run out, and any
 * unconsumed argument appended space separated.
 */
public fun upFormFormat(template: String, vararg args: Any?): String {
    var cursor = 0
    val formatted = UPFormPlaceholderPattern.replace(template) { match ->
        val token = match.value
        when {
            token == "%%" -> "%"
            cursor >= args.size -> token
            else -> {
                val argument = args[cursor]
                cursor += 1
                when (token) {
                    "%s" -> upFormJsText(argument)
                    "%d" -> upFormJsNumberText(argument)
                    "%j" -> upFormJsJson(argument)
                    else -> token
                }
            }
        }
    }
    if (cursor >= args.size) return formatted
    val builder = StringBuilder(formatted)
    while (cursor < args.size) {
        builder.append(' ').append(upFormJsText(args[cursor]))
        cursor += 1
    }
    return builder.toString()
}

/**
 * Reads `a.b.c` style property chains exactly like `getProperty()` in
 * `libs/function/index.js`: a non object receiver or an empty key yields `''`,
 * and a broken chain yields `null` instead of throwing.
 */
public fun upFormGetProperty(obj: UPRawValue, key: String): UPRawValue {
    if (obj !is Map<*, *>) return ""
    if (key.isEmpty()) return ""
    if (!key.contains('.')) return obj[key]
    val keys = key.split(".")
    var current: UPRawValue = obj[keys[0]] ?: emptyMap<String, UPRawValue>()
    for (index in 1 until keys.size) {
        if (!upFormTruthy(current)) break
        current = (current as? Map<*, *>)?.get(keys[index])
    }
    return current
}

/**
 * Immutable counterpart of `setProperty()`: missing or non object intermediate
 * levels are created, and a copy is returned because Compose state cannot be
 * mutated in place the way Vue reactivity allows.
 */
public fun upFormSetProperty(
    obj: Map<String, UPRawValue>,
    key: String,
    value: UPRawValue,
): Map<String, UPRawValue> {
    if (key.isEmpty()) return obj
    return upFormSetPath(obj, key.split("."), value)
}

private fun upFormSetPath(
    source: Map<String, UPRawValue>,
    keys: List<String>,
    value: UPRawValue,
): Map<String, UPRawValue> {
    val head = keys.first()
    val next = source.toMutableMap()
    if (keys.size == 1) {
        next[head] = value
        return next
    }
    val existing = source[head]
    val child: Map<String, UPRawValue> = if (existing is Map<*, *>) {
        existing.entries.associate { (childKey, childValue) -> childKey.toString() to childValue }
    } else {
        emptyMap()
    }
    next[head] = upFormSetPath(child, keys.drop(1), value)
    return next
}

/** Flattens the weakly typed `rules` payload into [UPFormRule] values. */
public fun upFormNormalizeRules(value: UPRawValue): List<UPFormRule> = when (value) {
    null -> emptyList()
    is UPFormRule -> listOf(value)
    is List<*> -> value.flatMap { upFormNormalizeRules(it) }
    is Array<*> -> value.flatMap { upFormNormalizeRules(it) }
    else -> emptyList()
}

/**
 * Runs [rules] against [value] the way `u-form.validateField()` does for one
 * property: trigger filtering, `transform`, one async-validator schema per rule
 * and the `rule.message` override that collapses generated texts.
 */
public fun upFormRunRules(
    field: String,
    value: UPRawValue,
    rules: List<UPFormRule>,
    event: String? = null,
    prop: String = field,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
): List<UPFormError> {
    val errors = mutableListOf<UPFormError>()
    rules.forEach { rule ->
        if (!upFormRuleRunsOn(rule, event)) return@forEach
        val transform = rule.transform
        val transformed = if (transform != null) transform(value) else value
        val messages = upFormApplyRule(rule, field, transformed, diagnostics)
        if (messages.isEmpty()) return@forEach
        val resolved = rule.message?.let { listOf(it) } ?: messages
        resolved.forEach { message ->
            errors += UPFormError(message = message, field = field, prop = prop)
        }
    }
    return errors
}

/**
 * Upstream builds the trigger list with `[].concat(rule.trigger)`, so a rule
 * without `trigger` produces `[undefined]` and is skipped for every named
 * event while still running for a full `validate()`.
 */
internal fun upFormRuleRunsOn(rule: UPFormRule, event: String?): Boolean =
    event.isNullOrEmpty() || rule.trigger.contains(event)

private fun upFormApplyRule(
    rule: UPFormRule,
    field: String,
    value: UPRawValue,
    diagnostics: UPCompatibilityDiagnostics,
): List<String> {
    val custom = rule.validator
    val resolvedType = upFormRuleType(rule, custom != null, diagnostics) ?: return emptyList()
    // `Schema.prototype.validate` normalizes `rule.type` before invoking any
    // validator, so a rule without `type` is seen as `'string'` downstream.
    val normalized = rule.copy(type = resolvedType)
    if (custom != null) return upFormCustomMessages(custom(normalized, value), normalized, field)
    if (upFormIsRequiredOnly(rule)) return upFormValidateRequiredOnly(normalized, value, field)
    return when (resolvedType) {
        "string" -> upFormValidateString(normalized, value, field)
        "number" -> upFormValidateNumber(normalized, value, field)
        "integer", "float" -> upFormValidateNumeric(normalized, value, field)
        "boolean", "method", "object" -> upFormValidateSimple(normalized, value, field)
        "regexp" -> upFormValidateRegexp(normalized, value, field)
        "array" -> upFormValidateArray(normalized, value, field)
        "enum" -> upFormValidateEnum(normalized, value, field)
        "pattern" -> upFormValidatePattern(normalized, value, field)
        "date" -> upFormValidateDate(normalized, value, field)
        "url", "hex", "email" -> upFormValidateNativeString(normalized, value, field)
        "required" -> upFormValidateRequiredOnly(normalized, value, field)
        else -> upFormValidateAny(normalized, value, field)
    }
}

/**
 * Mirrors `getType()`. Upstream throws on an unknown type; Android reports the
 * downgrade and skips the rule so one bad entry cannot crash a screen.
 */
private fun upFormRuleType(
    rule: UPFormRule,
    hasCustomValidator: Boolean,
    diagnostics: UPCompatibilityDiagnostics,
): String? {
    val declared = if (rule.type == null && rule.pattern is Regex) "pattern" else rule.type
    if (declared == null) return "string"
    if (hasCustomValidator) return declared
    if (declared !in UPFormValidatorTypes) {
        diagnostics.report(
            UPFormComponentName,
            "rules.type",
            declared,
            "Unknown async-validator rule type; the rule is skipped instead of throwing.",
        )
        return null
    }
    return declared
}

/**
 * `getValidationMethod()` picks `validators.required` when the only own key
 * besides `message` is `required`. A Kotlin data class always carries every
 * key, so the equivalent test is "every other option left at its default".
 */
private fun upFormIsRequiredOnly(rule: UPFormRule): Boolean =
    rule.required &&
        rule.trigger.isEmpty() &&
        rule.type == null &&
        rule.min == null &&
        rule.max == null &&
        rule.len == null &&
        rule.pattern == null &&
        !rule.whitespace &&
        rule.enum.isEmpty() &&
        rule.transform == null

/**
 * Maps the return value of a user supplied validator. Upstream silently drops a
 * returned string because it expects the async callback; Android treats it as an
 * error message, which is what uview-plus users actually write.
 */
private fun upFormCustomMessages(
    result: UPRawValue,
    rule: UPFormRule,
    field: String,
): List<String> = when (result) {
    true, null, Unit -> emptyList()
    false -> listOf(rule.message ?: "$field fails")
    is List<*> -> result.map { upFormResultMessage(it, field) }
    is Array<*> -> result.map { upFormResultMessage(it, field) }
    is Throwable -> listOf(upFormResultMessage(result, field))
    is String -> listOf(result)
    else -> emptyList()
}

private fun upFormResultMessage(value: Any?, field: String): String = when (value) {
    is String -> value
    is Throwable -> value.message ?: upFormFormat(UPFormMessages.Default, field)
    else -> upFormJsText(value)
}

/** Mirrors `isEmptyValue(value, type)`. */
private fun upFormIsEmptyValue(value: UPRawValue, type: String? = null): Boolean = when {
    value == null -> true
    type == "array" && value is List<*> && value.isEmpty() -> true
    type != null && type in UPFormStringTypes && value is String && value.isEmpty() -> true
    else -> false
}

private fun upFormRequiredRule(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
    type: String?,
    errors: MutableList<String>,
) {
    if (rule.required && upFormIsEmptyValue(value, type ?: rule.type)) {
        errors += upFormFormat(UPFormMessages.Required, field)
    }
}

private fun upFormWhitespaceRule(value: UPRawValue, field: String, errors: MutableList<String>) {
    val text = upFormJsText(value)
    if (Regex("""^\s+$""").containsMatchIn(text) || value == "") {
        errors += upFormFormat(UPFormMessages.Whitespace, field)
    }
}

private fun upFormTypeRule(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
    errors: MutableList<String>,
) {
    if (rule.required && value == null) {
        upFormRequiredRule(rule, value, field, null, errors)
        return
    }
    val ruleType = rule.type ?: return
    val template = UPFormMessages.types[ruleType] ?: UPFormMessages.Default
    if (ruleType in UPFormCustomTypes) {
        if (!upFormMatchesType(ruleType, value)) errors += upFormFormat(template, field, ruleType)
    } else if (upFormJsTypeOf(value) != ruleType) {
        errors += upFormFormat(template, field, ruleType)
    }
}

private fun upFormRangeRule(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
    errors: MutableList<String>,
) {
    val len = rule.len
    val min = rule.min
    val max = rule.max
    val key = when (value) {
        is Number -> "number"
        is String -> "string"
        is List<*> -> "array"
        // Unsupported value shapes are skipped; the rule should add `type`.
        else -> return
    }
    val actual = when (value) {
        is List<*> -> value.size.toDouble()
        // Surrogate pairs are collapsed so astral characters count as one.
        is String -> UPFormSurrogatePattern.replace(value, "_").length.toDouble()
        else -> (value as Number).toDouble()
    }
    val templates = UPFormMessages.ranges.getValue(key)
    when {
        len != null ->
            if (actual != len.toDouble()) errors += upFormFormat(templates.getValue("len"), field, len)
        min != null && max == null && actual < min.toDouble() ->
            errors += upFormFormat(templates.getValue("min"), field, min)
        max != null && min == null && actual > max.toDouble() ->
            errors += upFormFormat(templates.getValue("max"), field, max)
        min != null && max != null && (actual < min.toDouble() || actual > max.toDouble()) ->
            errors += upFormFormat(templates.getValue("range"), field, min, max)
    }
}

private fun upFormEnumRule(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
    errors: MutableList<String>,
) {
    if (rule.enum.indexOf(value) == -1) {
        errors += upFormFormat(
            UPFormMessages.Enum,
            field,
            rule.enum.joinToString(", ") { upFormJsText(it) },
        )
    }
}

private fun upFormPatternRule(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
    errors: MutableList<String>,
) {
    val pattern = rule.pattern
    if (!upFormTruthy(pattern)) return
    // `RegExp.test()` is unanchored, hence `containsMatchIn` instead of `matches`.
    val matched = when (pattern) {
        is Regex -> pattern.containsMatchIn(upFormJsText(value))
        is String -> runCatching { Regex(pattern).containsMatchIn(upFormJsText(value)) }.getOrDefault(true)
        else -> true
    }
    if (!matched) {
        errors += upFormFormat(UPFormMessages.PatternMismatch, field, value, pattern)
    }
}

private fun upFormJsTypeOf(value: UPRawValue): String = when (value) {
    null -> "object"
    is String -> "string"
    is Number -> "number"
    is Boolean -> "boolean"
    is Function<*> -> "function"
    else -> "object"
}

private fun upFormMatchesType(type: String, value: UPRawValue): Boolean = when (type) {
    "integer" -> UPFormIntegerPattern.containsMatchIn(upFormJsText(value))
    "float" -> UPFormFloatPattern.containsMatchIn(upFormJsText(value))
    "array" -> value is List<*>
    "regexp" -> value is Regex || runCatching { Regex(upFormJsText(value)) }.isSuccess
    // JavaScript duck types dates through `getTime`/`getMonth`/`getYear`.
    "date" -> value is Date || value is Calendar
    "number" -> !upFormJsNumber(value).isNaN()
    "object" -> value == null || value is Map<*, *>
    "method" -> value is Function<*>
    "email" -> value is String && UPFormEmailPattern.containsMatchIn(value) && value.length < 255
    "url" -> value is String && UPFormUrlPattern.containsMatchIn(value)
    "hex" -> value is String && UPFormHexPattern.containsMatchIn(value)
    else -> true
}

private fun upFormValidateRequiredOnly(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    // `validators.required` derives the emptiness type from the value itself.
    val derived = if (value is List<*>) "array" else upFormJsTypeOf(value)
    upFormRequiredRule(rule, value, field, derived, errors)
    return errors
}

private fun upFormValidateString(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    if (upFormIsEmptyValue(value, "string") && !rule.required) return errors
    upFormRequiredRule(rule, value, field, "string", errors)
    if (!upFormIsEmptyValue(value, "string")) {
        upFormTypeRule(rule, value, field, errors)
        upFormRangeRule(rule, value, field, errors)
        upFormPatternRule(rule, value, field, errors)
        if (rule.whitespace) upFormWhitespaceRule(value, field, errors)
    }
    return errors
}

private fun upFormValidateNumber(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    // `validators.number` maps `''` to undefined before anything else.
    val effective = if (value == "") null else value
    val errors = mutableListOf<String>()
    if (upFormIsEmptyValue(effective) && !rule.required) return errors
    upFormRequiredRule(rule, effective, field, null, errors)
    if (effective != null) {
        upFormTypeRule(rule, effective, field, errors)
        upFormRangeRule(rule, effective, field, errors)
    }
    return errors
}

private fun upFormValidateNumeric(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    if (upFormIsEmptyValue(value) && !rule.required) return errors
    upFormRequiredRule(rule, value, field, null, errors)
    if (value != null) {
        upFormTypeRule(rule, value, field, errors)
        upFormRangeRule(rule, value, field, errors)
    }
    return errors
}

private fun upFormValidateSimple(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    if (upFormIsEmptyValue(value) && !rule.required) return errors
    upFormRequiredRule(rule, value, field, null, errors)
    if (value != null) upFormTypeRule(rule, value, field, errors)
    return errors
}

private fun upFormValidateRegexp(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    if (upFormIsEmptyValue(value) && !rule.required) return errors
    upFormRequiredRule(rule, value, field, null, errors)
    if (!upFormIsEmptyValue(value)) upFormTypeRule(rule, value, field, errors)
    return errors
}

private fun upFormValidateArray(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    if (upFormIsEmptyValue(value, "array") && !rule.required) return errors
    upFormRequiredRule(rule, value, field, "array", errors)
    if (!upFormIsEmptyValue(value, "array")) {
        upFormTypeRule(rule, value, field, errors)
        upFormRangeRule(rule, value, field, errors)
    }
    return errors
}

private fun upFormValidateEnum(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    if (upFormIsEmptyValue(value) && !rule.required) return errors
    upFormRequiredRule(rule, value, field, null, errors)
    if (value != null) upFormEnumRule(rule, value, field, errors)
    return errors
}

private fun upFormValidatePattern(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    if (upFormIsEmptyValue(value, "string") && !rule.required) return errors
    upFormRequiredRule(rule, value, field, null, errors)
    if (!upFormIsEmptyValue(value, "string")) upFormPatternRule(rule, value, field, errors)
    return errors
}

private fun upFormValidateDate(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    if (upFormIsEmptyValue(value, "date") && !rule.required) return errors
    upFormRequiredRule(rule, value, field, null, errors)
    if (!upFormIsEmptyValue(value, "date")) {
        val dateValue: UPRawValue = if (value is Number) Date(value.toLong()) else value
        upFormTypeRule(rule, dateValue, field, errors)
        // Upstream would throw on `getTime()` of a non date; here range is skipped.
        upFormDateMillis(dateValue)?.let { upFormRangeRule(rule, it, field, errors) }
    }
    return errors
}

private fun upFormValidateNativeString(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    val ruleType = rule.type
    if (upFormIsEmptyValue(value, ruleType) && !rule.required) return errors
    upFormRequiredRule(rule, value, field, ruleType, errors)
    if (!upFormIsEmptyValue(value, ruleType)) upFormTypeRule(rule, value, field, errors)
    return errors
}

private fun upFormValidateAny(
    rule: UPFormRule,
    value: UPRawValue,
    field: String,
): List<String> {
    val errors = mutableListOf<String>()
    upFormRequiredRule(rule, value, field, null, errors)
    return errors
}

private fun upFormDateMillis(value: UPRawValue): Long? = when (value) {
    is Date -> value.time
    is Calendar -> value.timeInMillis
    else -> null
}

/** Bookkeeping for one `UPFormItem` registered against its parent `UPForm`. */
internal class UPFormItemRegistration(
    val prop: String,
    val itemRules: () -> List<UPRawValue>,
    val message: MutableState<String>,
)

/** The subset of `u-form` state that `u-form-item` reads through `provide`. */
internal class UPFormContext(
    val errorType: String,
    val labelPosition: String,
    val labelAlign: String,
    val labelWidth: UPRawValue,
    val labelStyle: UPStyleInput,
    val register: (UPFormItemRegistration) -> Unit,
    val unregister: (UPFormItemRegistration) -> Unit,
    val validateField: (List<String>, String?, Boolean) -> List<UPFormError>,
)

internal val LocalUPFormContext = staticCompositionLocalOf<UPFormContext?> { null }

/**
 * Imperative handle for the methods uview-plus exposes on a `u-form` ref.
 *
 * The controller stays inert until it is passed to a composed [UPForm]; calls
 * made before that resolve to no-ops instead of throwing.
 */
public class UPFormController {
    internal class Binding(
        val validate: (Boolean) -> List<UPFormError>,
        val validateField: (List<String>, String?, Boolean) -> List<UPFormError>,
        val resetFields: () -> Unit,
        val resetField: (String) -> Unit,
        val clearValidate: (List<String>) -> Unit,
        val setRules: (Map<String, UPRawValue>) -> Unit,
    )

    private var binding: Binding? = null

    internal fun attach(binding: Binding) {
        this.binding = binding
    }

    internal fun detach() {
        binding = null
    }

    /** Validates every registered item and returns the accumulated errors. */
    public fun validate(showErrorMsg: Boolean = true): List<UPFormError> =
        binding?.validate?.invoke(showErrorMsg) ?: emptyList()

    /** Validates the given property chains, optionally filtered by [event]. */
    public fun validateField(
        props: List<String>,
        event: String? = null,
        showErrorMsg: Boolean = true,
    ): List<UPFormError> = binding?.validateField?.invoke(props, event, showErrorMsg) ?: emptyList()

    /** Restores every registered property to its first observed model value. */
    public fun resetFields() {
        binding?.resetFields?.invoke()
    }

    /** Restores one property and clears its error message. */
    public fun resetField(prop: String) {
        binding?.resetField?.invoke(prop)
    }

    /** Clears error messages, for all items when [props] is empty. */
    public fun clearValidate(props: List<String> = emptyList()) {
        binding?.clearValidate?.invoke(props)
    }

    /** Replaces the form level rules; an empty map is ignored, like upstream. */
    public fun setRules(rules: Map<String, UPRawValue>) {
        binding?.setRules?.invoke(rules)
    }
}

/** Remembers a [UPFormController] across recompositions. */
@Composable
public fun rememberUPFormController(): UPFormController = remember { UPFormController() }
