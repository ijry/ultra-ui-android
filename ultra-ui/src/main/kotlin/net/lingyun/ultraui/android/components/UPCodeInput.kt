package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val CodeInputComponentName = "UPCodeInput"
private val CodeInputModes = setOf("box", "line")

/** Native Compose counterpart of uview-plus `u-code-input`. */
@Composable
public fun UPCodeInput(
    props: UPCodeInputProps = UPCodeInputProps(),
    onInput: ((String) -> Unit)? = null,
    onChange: ((String) -> Unit)? = null,
    onFinish: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val mode = upSafeEnum(props.mode, CodeInputModes, "box", diagnostics, CodeInputComponentName, "mode")
    val maxLength = normalizedUPMaxLength(props.maxlength).coerceIn(0, 100)
    val initialValue = sanitizeCodeInputValue(
        resolveUPModelValue(props.modelValue, props.value).upInputString(),
        props.maxlength,
        props.disabledDot,
    )
    var innerValue by remember(props.modelValue, props.value, props.maxlength, props.disabledDot) {
        mutableStateOf(initialValue)
    }
    var focused by remember { mutableStateOf(false) }
    var finishEmitted by remember { mutableStateOf(initialValue.length >= maxLength && maxLength > 0) }
    val focusRequester = remember { FocusRequester() }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, CodeInputComponentName)
    val size = upRawDp(props.size, 35.dp).coerceAtLeast(1.dp)
    val space = upRawDp(props.space, 10.dp).coerceAtLeast(0.dp)
    val fontSize = (style.fontSize ?: upRawDp(props.fontSize, 18.dp)).value.sp
    val textColor = style.color ?: UPColor.parse(props.color, UPTheme.Content)
    val borderColor = style.borderColor ?: UPColor.parse(props.borderColor, UPTheme.Border)
    val fontWeight = style.fontWeight ?: if (props.bold) FontWeight.Bold else FontWeight.Normal
    val cellShape = RoundedCornerShape(3.dp)

    LaunchedEffect(props.focus) {
        if (props.focus && !props.disabledKeyboard) runCatching { focusRequester.requestFocus() }
    }

    fun acceptValue(rawValue: String) {
        if (props.disabledKeyboard) return
        val nextValue = sanitizeCodeInputValue(rawValue, props.maxlength, props.disabledDot)
        if (nextValue == innerValue) return
        innerValue = nextValue
        onInput?.invoke(nextValue)
        onChange?.invoke(nextValue)
        if (maxLength > 0 && nextValue.length >= maxLength) {
            if (!finishEmitted) {
                finishEmitted = true
                onFinish?.invoke(nextValue)
            }
        } else {
            finishEmitted = false
        }
    }

    val rootModifier = modifier
        .fillMaxWidth()
        .applyUPResolvedStyle(style)
        .upTestTag("code-input")

    Box(modifier = rootModifier, contentAlignment = Alignment.CenterStart) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(maxLength) { index ->
                val hasValue = index < innerValue.length
                val cellModifier = Modifier
                    .width(size)
                    .height(size)
                    .then(
                        if (mode == "box") {
                            Modifier.border(
                                width = if (props.hairline) 0.5.dp else 1.dp,
                                color = borderColor,
                                shape = cellShape,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .upTestTag("code-input-cell-$index")
                    .semantics(mergeDescendants = true) {}

                Box(
                    modifier = cellModifier,
                    contentAlignment = Alignment.Center,
                ) {
                    if (hasValue) {
                        if (props.dot) {
                            BasicText(
                                text = "•",
                                style = TextStyle(
                                    color = textColor,
                                    fontSize = fontSize,
                                    fontWeight = fontWeight,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                ),
                            )
                        } else {
                            BasicText(
                                text = innerValue[index].toString(),
                                style = TextStyle(
                                    color = textColor,
                                    fontSize = fontSize,
                                    fontWeight = fontWeight,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                ),
                            )
                        }
                    }
                    if (mode == "line") {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .width(size)
                                .height(if (props.hairline) 2.dp else 4.dp)
                                .clip(RoundedCornerShape(percent = 50))
                                .background(borderColor),
                        )
                    }
                    if (focused && !hasValue && index == innerValue.length) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(textColor),
                        )
                    }
                }
            }
        }

        BasicTextField(
            value = innerValue,
            onValueChange = ::acceptValue,
            modifier = Modifier
                .fillMaxWidth()
                .height(size)
                .focusRequester(focusRequester)
                .onFocusChanged { state -> focused = state.isFocused }
                .alpha(0f)
                .upTestTag("code-input-field"),
            enabled = !props.disabledKeyboard,
            singleLine = true,
            textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(Color.Transparent),
        )
    }
}

/** Direct argument form for generated Android source. */
@Composable
@Suppress("LongParameterList")
public fun UPCodeInput(
    adjustPosition: Boolean = net.lingyun.ultraui.android.core.UPConfig.codeInput.adjustPosition,
    maxlength: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.codeInput.maxlength,
    dot: Boolean = net.lingyun.ultraui.android.core.UPConfig.codeInput.dot,
    mode: String = net.lingyun.ultraui.android.core.UPConfig.codeInput.mode,
    hairline: Boolean = net.lingyun.ultraui.android.core.UPConfig.codeInput.hairline,
    space: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.codeInput.space,
    modelValue: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.codeInput.modelValue,
    value: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.codeInput.value,
    focus: Boolean = net.lingyun.ultraui.android.core.UPConfig.codeInput.focus,
    bold: Boolean = net.lingyun.ultraui.android.core.UPConfig.codeInput.bold,
    color: String = net.lingyun.ultraui.android.core.UPConfig.codeInput.color,
    fontSize: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.codeInput.fontSize,
    size: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.codeInput.size,
    disabledKeyboard: Boolean = net.lingyun.ultraui.android.core.UPConfig.codeInput.disabledKeyboard,
    borderColor: String = net.lingyun.ultraui.android.core.UPConfig.codeInput.borderColor,
    disabledDot: Boolean = net.lingyun.ultraui.android.core.UPConfig.codeInput.disabledDot,
    customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    onInput: ((String) -> Unit)? = null,
    onChange: ((String) -> Unit)? = null,
    onFinish: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPCodeInput(
        props = UPCodeInputProps(
            adjustPosition = adjustPosition,
            maxlength = maxlength,
            dot = dot,
            mode = mode,
            hairline = hairline,
            space = space,
            modelValue = modelValue,
            value = value,
            focus = focus,
            bold = bold,
            color = color,
            fontSize = fontSize,
            size = size,
            disabledKeyboard = disabledKeyboard,
            borderColor = borderColor,
            disabledDot = disabledDot,
            customStyle = customStyle,
        ),
        onInput = onInput,
        onChange = onChange,
        onFinish = onFinish,
        diagnostics = diagnostics,
        modifier = modifier,
    )
}

private fun sanitizeCodeInputValue(value: String, maxlength: UPRawValue, disabledDot: Boolean): String {
    val withoutDots = if (disabledDot) value.filterNot { it == '.' } else value
    return limitUPText(withoutDots, maxlength)
}
