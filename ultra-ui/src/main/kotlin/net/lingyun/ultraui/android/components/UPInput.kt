package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPUnit
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val InputComponentName = "UPInput"
private val InputBorders = setOf("surround", "bottom", "none")
private val InputShapes = setOf("square", "circle")
private val InputAlignments = setOf("left", "center", "right")

/** Native Compose counterpart of uview-plus `u-input`. */
@Composable
public fun UPInput(
    props: UPInputProps = UPInputProps(),
    onInput: ((String) -> Unit)? = null,
    onChange: ((String) -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val borderName = upSafeEnum(props.border, InputBorders, "surround", diagnostics, InputComponentName, "border")
    val shapeName = upSafeEnum(props.shape, InputShapes, "square", diagnostics, InputComponentName, "shape")
    val alignName = upSafeEnum(props.inputAlign, InputAlignments, "left", diagnostics, InputComponentName, "inputAlign")
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, InputComponentName)
    val placeholderStyle = rememberUPResolvedStyle(props.placeholderStyle, diagnostics, "$InputComponentName.placeholderStyle")
    val initialValue = limitUPText(resolveUPModelValue(props.modelValue, props.value).upInputString(), props.maxlength)
    var innerValue by remember(props.modelValue, props.value, props.maxlength) { mutableStateOf(initialValue) }
    var focused by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(props.focus) {
        if (props.focus) runCatching { focusRequester.requestFocus() }
    }

    fun emitChanged() {
        onChange?.invoke(innerValue)
    }

    fun acceptValue(rawValue: String) {
        if (props.disabled || props.readonly) return
        val formatted = formatUPTextSafely(rawValue, props.formatter, diagnostics, InputComponentName)
        val limited = limitUPText(formatted, props.maxlength)
        if (limited == innerValue) return
        innerValue = limited
        onInput?.invoke(limited)
    }

    fun confirmValue() {
        onConfirm?.invoke()
        emitChanged()
    }

    val textColor = when {
        props.disabled -> UPColor.parse(props.disabledColor, UPTheme.Disabled)
        style.color != null -> style.color
        props.color.isNotEmpty() -> UPColor.parse(props.color, UPTheme.Main)
        else -> UPTheme.Main
    }
    val placeholderColor = placeholderStyle.color ?: UPTheme.Tips.copy(alpha = 0.85f)
    val backgroundColor = when {
        props.disabled && props.disabledColor.isNotEmpty() -> UPColor.parse(props.disabledColor, UPTheme.Background)
        style.backgroundColor != null -> style.backgroundColor
        else -> Color.Transparent
    }
    val fieldFontSize = (style.fontSize ?: upRawDp(props.fontSize, 15.dp)).value.sp
    val placeholderFontSize = (placeholderStyle.fontSize ?: upRawDp(props.fontSize, 15.dp)).value.sp
    val shape = if (shapeName == "circle") RoundedCornerShape(percent = 50) else RoundedCornerShape(4.dp)
    val fieldTextStyle = TextStyle(
        color = textColor,
        fontSize = fieldFontSize,
        textAlign = textAlignForUPInput(alignName),
        lineHeight = fieldFontSize,
    )
    val placeholderTextStyle = TextStyle(
        color = placeholderColor,
        fontSize = placeholderFontSize,
        textAlign = textAlignForUPInput(alignName),
        lineHeight = placeholderFontSize,
    )
    val clearVisible = props.clearable && innerValue.isNotEmpty() && (!props.onlyClearableOnFocused || focused)
    val passwordMode = props.password || props.type.equals("password", ignoreCase = true)

    val rootModifier = modifier
        .fillMaxWidth()
        .then(
            when (borderName) {
                "bottom" -> Modifier.drawBehind {
                    val stroke = 1.dp.toPx()
                    drawLine(
                        color = UPTheme.Border,
                        start = Offset(0f, size.height - stroke / 2f),
                        end = Offset(size.width, size.height - stroke / 2f),
                        strokeWidth = stroke,
                    )
                }
                "none" -> Modifier
                else -> Modifier.border(1.dp, UPTheme.Border, shape)
            },
        )
        .clip(shape)
        .background(backgroundColor, shape)
        .applyUPResolvedStyle(style)
        .upTestTag("input")

    Column(modifier = rootModifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (props.prefixIcon.isNotEmpty()) {
                Box(modifier = Modifier.upTestTag("input-prefix")) {
                    UPIcon(
                        props = UPIconProps(name = props.prefixIcon, customStyle = props.prefixIconStyle),
                        diagnostics = diagnostics,
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = innerValue,
                    onValueChange = ::acceptValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { state ->
                            if (focused == state.isFocused) return@onFocusChanged
                            focused = state.isFocused
                            if (state.isFocused) onFocus?.invoke() else {
                                onBlur?.invoke()
                                emitChanged()
                            }
                        }
                        .upTestTag("input-field"),
                    enabled = !props.disabled,
                    readOnly = props.readonly,
                    textStyle = fieldTextStyle,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardTypeForUPInput(props.type),
                        imeAction = imeActionForUPInput(props.confirmType),
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { confirmValue() },
                        onGo = { confirmValue() },
                        onNext = { confirmValue() },
                        onSearch = { confirmValue() },
                        onSend = { confirmValue() },
                    ),
                    visualTransformation = if (passwordMode && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
                    cursorBrush = SolidColor(UPColor.parse(props.cursorColor, UPTheme.Primary)),
                    decorationBox = { innerTextField ->
                        if (innerValue.isEmpty()) {
                            BasicText(
                                text = props.placeholder.upInputString(),
                                style = placeholderTextStyle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    },
                )
            }

            if (clearVisible) {
                Box(
                    modifier = Modifier
                        .upTestTag("input-clear")
                        .upClickable(role = Role.Button) {
                            innerValue = ""
                            onInput?.invoke("")
                            onChange?.invoke("")
                            onClear?.invoke()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    UPIcon(UPIconProps(name = "close", size = 14, color = "#ffffff"), diagnostics = diagnostics)
                }
            }

            if (passwordMode && props.passwordVisibilityToggle) {
                Box(
                    modifier = Modifier
                        .upTestTag("input-password-toggle")
                        .upClickable(role = Role.Button) { showPassword = !showPassword },
                    contentAlignment = Alignment.Center,
                ) {
                    UPIcon(
                        UPIconProps(name = if (showPassword) "eye-off" else "eye-fill", size = 18),
                        diagnostics = diagnostics,
                    )
                }
            }

            if (props.suffixIcon.isNotEmpty()) {
                Box(modifier = Modifier.upTestTag("input-suffix")) {
                    UPIcon(
                        props = UPIconProps(name = props.suffixIcon, customStyle = props.suffixIconStyle),
                        diagnostics = diagnostics,
                    )
                }
            }
        }
        if (props.showWordLimit) {
            BasicText(
                text = "${innerValue.length}/${normalizedUPMaxLength(props.maxlength).takeUnless { it == Int.MAX_VALUE } ?: "∞"}",
                style = TextStyle(color = UPTheme.Tips, fontSize = 11.sp),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 10.dp, vertical = 2.dp)
                    .upTestTag("input-count"),
            )
        }
    }
}

/** Direct argument form for generated Android source. */
@Composable
@Suppress("LongParameterList")
public fun UPInput(
    value: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.input.value,
    modelValue: UPRawValue? = net.lingyun.ultraui.android.core.UPConfig.input.modelValue,
    type: String = net.lingyun.ultraui.android.core.UPConfig.input.type,
    disabled: Boolean = net.lingyun.ultraui.android.core.UPConfig.input.disabled,
    clearable: Boolean = net.lingyun.ultraui.android.core.UPConfig.input.clearable,
    maxlength: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.input.maxlength,
    placeholder: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.input.placeholder,
    password: Boolean = net.lingyun.ultraui.android.core.UPConfig.input.password,
    readonly: Boolean = net.lingyun.ultraui.android.core.UPConfig.input.readonly,
    passwordVisibilityToggle: Boolean = net.lingyun.ultraui.android.core.UPConfig.input.passwordVisibilityToggle,
    customStyle: net.lingyun.ultraui.android.core.UPStyleInput = emptyMap<String, net.lingyun.ultraui.android.core.UPRawValue>(),
    modifier: Modifier = Modifier,
    onInput: ((String) -> Unit)? = null,
    onChange: ((String) -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPInput(
        props = UPInputProps(
            modelValue = modelValue,
            value = value,
            type = type,
            disabled = disabled,
            clearable = clearable,
            maxlength = maxlength,
            placeholder = placeholder,
            password = password,
            readonly = readonly,
            passwordVisibilityToggle = passwordVisibilityToggle,
            customStyle = customStyle,
        ),
        onInput = onInput,
        onChange = onChange,
        onFocus = onFocus,
        onBlur = onBlur,
        onConfirm = onConfirm,
        onClear = onClear,
        diagnostics = diagnostics,
        modifier = modifier,
    )
}
