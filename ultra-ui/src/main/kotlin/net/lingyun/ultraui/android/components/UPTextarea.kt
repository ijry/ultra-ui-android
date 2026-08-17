package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val TextareaComponentName = "UPTextarea"
private val TextareaBorders = setOf("surround", "bottom", "none")

/** Native Compose counterpart of uview-plus `u-textarea`. */
@Composable
public fun UPTextarea(
    props: UPTextareaProps = UPTextareaProps(),
    onInput: ((String) -> Unit)? = null,
    onChange: ((String) -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val borderName = upSafeEnum(props.border, TextareaBorders, "surround", diagnostics, TextareaComponentName, "border")
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, TextareaComponentName)
    val placeholderStyle = rememberUPResolvedStyle(props.placeholderStyle, diagnostics, "$TextareaComponentName.placeholderStyle")
    val initialValue = limitUPText(resolveUPModelValue(props.modelValue, props.value).upInputString(), props.maxlength)
    var innerValue by remember(props.modelValue, props.value, props.maxlength) { mutableStateOf(initialValue) }
    var focused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(props.focus) {
        if (props.focus) runCatching { focusRequester.requestFocus() }
    }

    fun emitChanged() {
        onChange?.invoke(innerValue)
    }

    fun acceptValue(rawValue: String) {
        if (props.disabled) return
        val formatted = formatUPTextSafely(rawValue, props.formatter, diagnostics, TextareaComponentName)
        val limited = limitUPText(formatted, props.maxlength)
        if (limited == innerValue) return
        innerValue = limited
        onInput?.invoke(limited)
    }

    fun confirmValue() {
        onConfirm?.invoke()
        emitChanged()
    }

    val textColor = style.color ?: UPTheme.Main
    val placeholderColor = placeholderStyle.color ?: UPTheme.Tips.copy(alpha = 0.85f)
    val fieldFontSize = (style.fontSize ?: 15.dp).value.sp
    val placeholderFontSize = (placeholderStyle.fontSize ?: 14.dp).value.sp
    val requestedHeight = upRawDp(props.height, 70.dp).coerceAtLeast(40.dp)
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
    val root = modifier
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
        .background(style.backgroundColor ?: Color.Transparent, shape)
        .applyUPResolvedStyle(style)
        .upTestTag("textarea")

    Column(modifier = root) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (props.autoHeight) Modifier.heightIn(min = requestedHeight) else Modifier.heightIn(min = requestedHeight))
                .padding(horizontal = 10.dp, vertical = 8.dp),
        ) {
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
                    .upTestTag("textarea-field"),
                enabled = !props.disabled,
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = fieldFontSize,
                    lineHeight = 20.sp,
                ),
                singleLine = false,
                maxLines = if (props.autoHeight) Int.MAX_VALUE else (requestedHeight.value / 20f).toInt().coerceAtLeast(1),
                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { confirmValue() }),
                cursorBrush = SolidColor(UPColor.parse("#53c21d", UPTheme.Primary)),
                decorationBox = { innerTextField ->
                    if (innerValue.isEmpty()) {
                        BasicText(
                            text = props.placeholder.upInputString(),
                            style = TextStyle(
                                color = placeholderColor,
                                fontSize = placeholderFontSize,
                                lineHeight = 20.sp,
                            ),
                            maxLines = if (props.autoHeight) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                },
            )
        }
        if (props.count) {
            BasicText(
                text = "${innerValue.length}/${normalizedUPMaxLength(props.maxlength).takeUnless { it == Int.MAX_VALUE } ?: "∞"}",
                style = TextStyle(color = UPTheme.Tips, fontSize = 11.sp),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 10.dp, vertical = 2.dp)
                    .upTestTag("textarea-count"),
            )
        }
    }
}

/** Direct argument form for generated Android source. */
@Composable
@Suppress("LongParameterList")
public fun UPTextarea(
    value: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.textarea.value,
    modelValue: UPRawValue? = net.lingyun.ultraui.android.core.UPConfig.textarea.modelValue,
    placeholder: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.textarea.placeholder,
    height: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.textarea.height,
    disabled: Boolean = net.lingyun.ultraui.android.core.UPConfig.textarea.disabled,
    count: Boolean = net.lingyun.ultraui.android.core.UPConfig.textarea.count,
    autoHeight: Boolean = net.lingyun.ultraui.android.core.UPConfig.textarea.autoHeight,
    maxlength: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.textarea.maxlength,
    border: String = net.lingyun.ultraui.android.core.UPConfig.textarea.border,
    customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    onInput: ((String) -> Unit)? = null,
    onChange: ((String) -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPTextarea(
        props = UPTextareaProps(
            value = value,
            modelValue = modelValue,
            placeholder = placeholder,
            height = height,
            disabled = disabled,
            count = count,
            autoHeight = autoHeight,
            maxlength = maxlength,
            border = border,
            customStyle = customStyle,
        ),
        onInput = onInput,
        onChange = onChange,
        onFocus = onFocus,
        onBlur = onBlur,
        onConfirm = onConfirm,
        diagnostics = diagnostics,
        modifier = modifier,
    )
}
