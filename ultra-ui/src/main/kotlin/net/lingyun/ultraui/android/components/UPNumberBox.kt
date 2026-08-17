package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upBooleanOrDefault
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag
import java.util.Locale
import kotlin.math.abs
import kotlin.math.round

private const val NumberBoxComponentName = "UPNumberBox"

/** Native Compose counterpart of uview-plus `u-number-box`. */
@Composable
public fun UPNumberBox(
    props: UPNumberBoxProps = UPNumberBoxProps(),
    onInput: ((UPRawValue) -> Unit)? = null,
    onChange: ((UPRawValue) -> Unit)? = null,
    onOverlimit: (() -> Unit)? = null,
    onPlus: (() -> Unit)? = null,
    onMinus: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val bounds = rememberNumberBoxBounds(props, diagnostics)
    val rawExternal = resolveUPModelValue(props.modelValue, props.value)
    val externalValue = formatNumberBoxValue(
        parseNumberBoxValue(rawExternal) ?: bounds.min,
        bounds,
    )
    var currentText by remember(rawExternal, bounds, props.integer, props.decimalLength) {
        mutableStateOf(externalValue)
    }
    LaunchedEffect(rawExternal, bounds, props.integer, props.decimalLength) {
        currentText = externalValue
    }

    val currentNumber = parseNumberBoxValue(currentText) ?: bounds.min
    val buttonSize = selectionDimension(props.buttonSize, 30.dp).coerceAtLeast(1.dp)
    val buttonWidth = selectionDimension(props.buttonWidth, 30.dp).coerceAtLeast(1.dp)
    val inputWidth = selectionDimension(props.inputWidth, 35.dp).coerceAtLeast(1.dp)
    val radius = selectionDimension(props.buttonRadius, 0.dp)
    val resolvedColor = UPColor.parse(props.color.takeIf { it.isNotBlank() }, UPTheme.Main)
    val resolvedDisabledColor = UPTheme.Disabled
    val backgroundColor = UPColor.parse(props.bgColor.takeIf { it.isNotBlank() }, Color(0xFFEBECEE))
    val disabledBackgroundColor = UPColor.parse(
        props.disabledBgColor.takeIf { it.isNotBlank() },
        Color(0xFFF7F8FA),
    )
    val inputBackgroundColor = UPColor.parse(
        props.inputBgColor.takeIf { it.isNotBlank() },
        backgroundColor,
    )
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, NumberBoxComponentName)
    val shape = if (radius > 0.dp) androidx.compose.foundation.shape.RoundedCornerShape(radius) else androidx.compose.foundation.shape.RoundedCornerShape(0.dp)

    fun isDisabled(type: NumberBoxAction): Boolean = when (type) {
        NumberBoxAction.Plus -> props.disabled || props.disablePlus || currentNumber >= bounds.max
        NumberBoxAction.Minus -> props.disabled || props.disableMinus || currentNumber <= bounds.min
    }

    fun emitValue(raw: UPRawValue, action: NumberBoxAction? = null) {
        if (!props.asyncChange) {
            currentText = formatNumberBoxValue(parseNumberBoxValue(raw) ?: bounds.min, bounds)
        }
        onInput?.invoke(raw)
        onChange?.invoke(raw)
        when (action) {
            NumberBoxAction.Plus -> onPlus?.invoke()
            NumberBoxAction.Minus -> onMinus?.invoke()
            null -> Unit
        }
    }

    fun step(action: NumberBoxAction) {
        if (isDisabled(action)) {
            onOverlimit?.invoke()
            return
        }
        val step = parseNumberBoxValue(props.step)?.takeIf { it.isFinite() && it > 0.0 } ?: 1.0
        val signedStep = if (action == NumberBoxAction.Plus) step else -step
        val next = round((currentNumber + signedStep) * 1.0e10) / 1.0e10
        val clamped = clampNumberBoxValue(next, bounds)
        emitValue(numberBoxRawValue(clamped, bounds), action)
    }

    val rootModifier = modifier
        .applyUPResolvedStyle(style)
        .upTestTag("number-box")

    Row(
        modifier = rootModifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (props.showMinus && !(props.miniMode && abs(currentNumber) < 1.0e-9)) {
            NumberBoxButton(
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonSize)
                    .clip(shape)
                    .background(if (isDisabled(NumberBoxAction.Minus)) disabledBackgroundColor else backgroundColor, shape)
                    .upTestTag("number-box-minus"),
                color = if (isDisabled(NumberBoxAction.Minus)) resolvedDisabledColor else resolvedColor,
                icon = "minus",
                disabled = false,
                onClick = { step(NumberBoxAction.Minus) },
                diagnostics = diagnostics,
            )
        }

        BasicTextField(
            value = currentText,
            onValueChange = { rawText ->
                if (props.disabled || props.disabledInput) return@BasicTextField
                val filtered = filterNumberBoxText(rawText, props.integer)
                if (filtered.isEmpty()) {
                    currentText = ""
                    return@BasicTextField
                }
                val limited = limitNumberBoxDecimals(filtered, bounds.decimalLength)
                val parsed = parseNumberBoxValue(limited) ?: return@BasicTextField
                val clamped = clampNumberBoxValue(parsed, bounds)
                val formatted = formatNumberBoxValue(clamped, bounds)
                if (!props.asyncChange) currentText = formatted else currentText = limited
                val emitted = if (props.asyncChange) numberBoxRawValue(parsed, bounds) else numberBoxRawValue(clamped, bounds)
                onInput?.invoke(emitted)
                onChange?.invoke(emitted)
            },
            modifier = Modifier
                .width(inputWidth)
                .height(buttonSize)
                .background(if (props.disabled || props.disabledInput) disabledBackgroundColor else inputBackgroundColor)
                .border(1.dp, UPTheme.Border.copy(alpha = 0.35f))
                .padding(horizontal = 2.dp)
                .upTestTag("number-box-field"),
            enabled = !props.disabled && !props.disabledInput,
            singleLine = true,
            textStyle = TextStyle(
                color = if (props.disabled || props.disabledInput) resolvedDisabledColor else resolvedColor,
                fontSize = 15.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            ),
            cursorBrush = SolidColor(resolvedColor),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (props.integer) KeyboardType.Number else KeyboardType.Decimal,
            ),
        )

        if (props.showPlus) {
            NumberBoxButton(
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonSize)
                    .clip(shape)
                    .background(if (isDisabled(NumberBoxAction.Plus)) disabledBackgroundColor else backgroundColor, shape)
                    .upTestTag("number-box-plus"),
                color = if (isDisabled(NumberBoxAction.Plus)) resolvedDisabledColor else resolvedColor,
                icon = "plus",
                disabled = false,
                onClick = { step(NumberBoxAction.Plus) },
                diagnostics = diagnostics,
            )
        }
    }
}

/** Direct value form for generated Compose source. */
@Composable
public fun UPNumberBox(
    value: UPRawValue,
    modifier: Modifier = Modifier,
    onInput: ((UPRawValue) -> Unit)? = null,
    onChange: ((UPRawValue) -> Unit)? = null,
    onOverlimit: (() -> Unit)? = null,
    onPlus: (() -> Unit)? = null,
    onMinus: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPNumberBox(
        props = UPNumberBoxProps(modelValue = value, value = value),
        onInput = onInput,
        onChange = onChange,
        onOverlimit = onOverlimit,
        onPlus = onPlus,
        onMinus = onMinus,
        diagnostics = diagnostics,
        modifier = modifier,
    )
}

private enum class NumberBoxAction { Plus, Minus }

private data class NumberBoxBounds(
    val min: Double,
    val max: Double,
    val decimalLength: Int?,
)

@Composable
private fun rememberNumberBoxBounds(
    props: UPNumberBoxProps,
    diagnostics: UPCompatibilityDiagnostics,
): NumberBoxBounds {
    val min = parseNumberBoxValue(props.min) ?: run {
        diagnostics.report(NumberBoxComponentName, "min", props.min, "Malformed minimum; using 0.")
        0.0
    }
    val max = parseNumberBoxValue(props.max)?.takeIf { it >= min } ?: run {
        diagnostics.report(NumberBoxComponentName, "max", props.max, "Malformed maximum; using min.")
        min
    }
    val decimalLength = when (val raw = props.decimalLength) {
        null -> null
        else -> raw.upIntOrDefault(-1).takeIf { it >= 0 } ?: run {
            diagnostics.report(NumberBoxComponentName, "decimalLength", raw, "Malformed decimal length; disabling fixed precision.")
            null
        }
    }
    return NumberBoxBounds(min = min, max = max, decimalLength = decimalLength)
}

private fun parseNumberBoxValue(value: UPRawValue): Double? = when (value) {
    is Number -> value.toDouble().takeIf(Double::isFinite)
    is String -> value.trim().toDoubleOrNull()?.takeIf(Double::isFinite)
    else -> null
}

private fun clampNumberBoxValue(value: Double, bounds: NumberBoxBounds): Double =
    value.coerceIn(bounds.min, bounds.max)

private fun filterNumberBoxText(value: String, integer: Boolean): String {
    var filtered = value.filter { it.isDigit() || it == '.' || it == '-' }
    if (integer) filtered = filtered.substringBefore('.')
    val minusIndex = filtered.indexOf('-')
    if (minusIndex > 0) filtered = filtered.replace("-", "")
    if (minusIndex == 0 && filtered.drop(1).contains('-')) {
        filtered = "-" + filtered.drop(1).replace("-", "")
    }
    val dotIndex = filtered.indexOf('.')
    if (dotIndex >= 0 && filtered.substring(dotIndex + 1).contains('.')) {
        filtered = filtered.substring(0, dotIndex + 1) + filtered.substring(dotIndex + 1).replace(".", "")
    }
    return filtered
}

private fun limitNumberBoxDecimals(value: String, decimalLength: Int?): String {
    if (decimalLength == null) return value
    val separator = value.indexOf('.')
    if (separator < 0) return value
    return value.substring(0, separator + 1) + value.substring(separator + 1).take(decimalLength)
}

private fun formatNumberBoxValue(value: Double, bounds: NumberBoxBounds): String {
    val normalized = clampNumberBoxValue(value, bounds)
    if (bounds.decimalLength != null) {
        return String.format(Locale.US, "%.${bounds.decimalLength}f", normalized)
    }
    if (normalized.isFinite() && normalized % 1.0 == 0.0) {
        return normalized.toBigDecimal().toBigIntegerExact().toString()
    }
    return normalized.toBigDecimal().stripTrailingZeros().toPlainString()
}

private fun numberBoxRawValue(value: Double, bounds: NumberBoxBounds): UPRawValue {
    if (bounds.decimalLength != null) return formatNumberBoxValue(value, bounds)
    if (value.isFinite() && value % 1.0 == 0.0) {
        val integral = value.toBigDecimal().toBigIntegerExact()
        return when {
            integral >= Int.MIN_VALUE.toBigInteger() && integral <= Int.MAX_VALUE.toBigInteger() -> integral.toInt()
            integral >= Long.MIN_VALUE.toBigInteger() && integral <= Long.MAX_VALUE.toBigInteger() -> integral.toLong()
            else -> integral.toString()
        }
    }
    return value
}

@Composable
private fun NumberBoxButton(
    modifier: Modifier,
    color: Color,
    icon: String,
    disabled: Boolean,
    onClick: () -> Unit,
    diagnostics: UPCompatibilityDiagnostics,
) {
    Box(
        modifier = modifier.upClickable(
            enabled = !disabled,
            role = Role.Button,
            onClick = onClick,
        ),
        contentAlignment = Alignment.Center,
    ) {
        UPIcon(
            props = UPIconProps(
                name = icon,
                color = "#%08X".format(color.value.toLong()),
                size = 15,
                bold = true,
            ),
            diagnostics = diagnostics,
        )
    }
}
