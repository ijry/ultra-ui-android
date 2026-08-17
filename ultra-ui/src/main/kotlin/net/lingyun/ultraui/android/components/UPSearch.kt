package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyle
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val SearchComponentName = "UPSearch"
private val SearchShapes = setOf("round", "square")
private val SearchIconPositions = setOf("left", "right")
private val SearchAlignments = setOf("left", "center", "right")

/**
 * Native Compose counterpart of uview-plus `u-search`.
 *
 * The callback shape follows the platform-neutral generated API: input/change carry the
 * current text, while action callbacks are event notifications. The current text remains
 * available to generated code through its model binding and `onInput`/`onChange`.
 */
@Composable
public fun UPSearch(
    props: UPSearchProps = UPSearchProps(),
    onInput: ((String) -> Unit)? = null,
    onChange: ((String) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    onCustom: (() -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onClickIcon: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val shapeName = upSafeEnum(props.shape, SearchShapes, "round", diagnostics, SearchComponentName, "shape")
    val iconPosition = upSafeEnum(props.iconPosition, SearchIconPositions, "left", diagnostics, SearchComponentName, "iconPosition")
    val alignName = upSafeEnum(props.inputAlign, SearchAlignments, "left", diagnostics, SearchComponentName, "inputAlign")
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, SearchComponentName)
    val inputStyle = rememberUPResolvedStyle(props.inputStyle, diagnostics, "$SearchComponentName.inputStyle")
    val actionStyle = rememberUPResolvedStyle(props.actionStyle, diagnostics, "$SearchComponentName.actionStyle")
    val screenWidth = availableScreenWidth()
    val marginStyle = remember(props.margin, screenWidth, diagnostics) {
        UPStyle.resolve(
            input = mapOf("margin" to props.margin),
            availableScreenWidth = screenWidth,
            diagnostics = diagnostics,
            component = "$SearchComponentName.margin",
        )
    }

    val initialValue = limitUPText(resolveUPModelValue(props.modelValue, props.value).upInputString(), props.maxlength)
    var innerValue by remember(props.modelValue, props.value, props.maxlength) { mutableStateOf(initialValue) }
    var focused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(props.focus) {
        if (props.focus && !props.disabled) runCatching { focusRequester.requestFocus() }
    }

    fun emitChanged() {
        onChange?.invoke(innerValue)
    }

    fun acceptValue(rawValue: String) {
        if (props.disabled) return
        val limited = limitUPText(rawValue, props.maxlength)
        if (limited == innerValue) return
        innerValue = limited
        onInput?.invoke(limited)
        onChange?.invoke(limited)
    }

    fun clearValue() {
        if (innerValue.isEmpty()) return
        innerValue = ""
        onInput?.invoke("")
        onChange?.invoke("")
        onClear?.invoke()
    }

    fun confirmSearch() {
        onSearch?.invoke()
        emitChanged()
    }

    val textColor = when {
        props.disabled -> UPTheme.Disabled
        inputStyle.color != null -> inputStyle.color
        props.color.isNotEmpty() -> UPColor.parse(props.color, UPTheme.Main)
        else -> UPTheme.Main
    }
    val placeholderColor = if (props.placeholderColor.isNotEmpty()) {
        UPColor.parse(props.placeholderColor, UPTheme.Tips)
    } else {
        UPTheme.Tips
    }
    val backgroundColor = if (props.bgColor.isNotEmpty()) {
        UPColor.parse(props.bgColor, UPTheme.Background)
    } else {
        UPTheme.Background
    }
    val borderColor = if (props.borderColor.isNotEmpty()) {
        UPColor.parse(props.borderColor, Color.Transparent)
    } else {
        Color.Transparent
    }
    val fieldHeight = upRawDp(props.height, 32.dp).coerceAtLeast(1.dp)
    val searchIconSize = upRawDp(props.searchIconSize, 22.dp).coerceAtLeast(0.dp)
    val fieldFontSize = (inputStyle.fontSize ?: 14.dp).value.sp
    val actionFontSize = (actionStyle.fontSize ?: 14.dp).value.sp
    val shape = if (shapeName == "round") RoundedCornerShape(percent = 50) else RoundedCornerShape(4.dp)
    val clearVisible = props.clearabled && innerValue.isNotEmpty() && (!props.onlyClearableOnFocused || focused)
    val actionVisible = props.showAction && (!props.animation || focused)
    val fieldTextStyle = TextStyle(
        color = textColor,
        fontSize = fieldFontSize,
        fontWeight = inputStyle.fontWeight ?: FontWeight.Normal,
        textAlign = textAlignForUPInput(alignName),
        lineHeight = fieldFontSize,
    )
    val placeholderTextStyle = TextStyle(
        color = placeholderColor,
        fontSize = fieldFontSize,
        textAlign = textAlignForUPInput(alignName),
        lineHeight = fieldFontSize,
    )

    val rootModifier = modifier
        .fillMaxWidth()
        .padding(
            start = marginStyle.marginStart,
            top = marginStyle.marginTop,
            end = marginStyle.marginEnd,
            bottom = marginStyle.marginBottom,
        )
        .applyUPResolvedStyle(style)
        .upClickable(enabled = props.disabled && onClick != null, role = Role.Button) {
            onClick?.invoke()
        }
        .upTestTag("search")

    Row(
        modifier = rootModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        val contentModifier = Modifier
            .weight(1f)
            .heightIn(min = fieldHeight)
            .clip(shape)
            .background(style.backgroundColor ?: backgroundColor, shape)
            .then(
                if (borderColor.alpha > 0f) Modifier.border(1.dp, borderColor, shape) else Modifier,
            )

        Row(
            modifier = contentModifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            @Composable
            fun SearchIcon() {
                Box(
                    modifier = Modifier
                        .size(searchIconSize.coerceAtLeast(1.dp))
                        .upTestTag("search-icon")
                        .upClickable(role = Role.Button) {
                            onClickIcon?.invoke()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    UPIcon(
                        props = UPIconProps(
                            name = props.searchIcon,
                            size = searchIconSize.value,
                            color = props.searchIconColor.ifEmpty { textColor.toSearchHex() },
                        ),
                        diagnostics = diagnostics,
                    )
                }
            }

            if (props.label != null) {
                BasicText(
                    text = props.label.upInputString(),
                    style = TextStyle(
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    modifier = Modifier.padding(end = 2.dp),
                )
            }

            if (iconPosition == "left") SearchIcon()

            BasicTextField(
                value = innerValue,
                onValueChange = ::acceptValue,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = fieldHeight)
                    .applyUPResolvedStyle(inputStyle)
                    .focusRequester(focusRequester)
                    .onFocusChanged { state ->
                        if (focused == state.isFocused) return@onFocusChanged
                        focused = state.isFocused
                        if (state.isFocused) onFocus?.invoke() else {
                            onBlur?.invoke()
                            emitChanged()
                        }
                    }
                    .upTestTag("search-field"),
                enabled = !props.disabled,
                singleLine = true,
                textStyle = fieldTextStyle,
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardTypeForUPInput("text"),
                    imeAction = imeActionForUPInput("search"),
                ),
                keyboardActions = KeyboardActions(onSearch = { confirmSearch() }, onDone = { confirmSearch() }),
                cursorBrush = SolidColor(UPColor.parse(props.color, UPTheme.Primary)),
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

            if (clearVisible) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(UPTheme.Tips.copy(alpha = 0.75f), CircleShape)
                        .upTestTag("search-clear")
                        .upClickable(role = Role.Button, onClick = ::clearValue),
                    contentAlignment = Alignment.Center,
                ) {
                    UPIcon(
                        props = UPIconProps(name = "close", size = 11, color = "#ffffff"),
                        diagnostics = diagnostics,
                    )
                }
            }

            if (iconPosition == "right") SearchIcon()
        }

        if (actionVisible) {
            BasicText(
                text = props.actionText.upInputString(),
                style = TextStyle(
                    color = actionStyle.color ?: textColor,
                    fontSize = actionFontSize,
                    fontWeight = actionStyle.fontWeight ?: FontWeight.Normal,
                    textAlign = actionStyle.textAlign ?: androidx.compose.ui.text.style.TextAlign.Center,
                ),
                modifier = Modifier
                    .widthIn(min = 40.dp)
                    .padding(horizontal = 2.dp, vertical = 8.dp)
                    .applyUPResolvedStyle(actionStyle)
                    .upTestTag("search-action")
                    .upClickable(role = Role.Button) {
                        onCustom?.invoke()
                    },
            )
        }
    }
}

/** Direct argument form for generated Android source. */
@Composable
@Suppress("LongParameterList")
public fun UPSearch(
    value: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.search.value,
    modelValue: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.search.modelValue,
    shape: String = net.lingyun.ultraui.android.core.UPConfig.search.shape,
    bgColor: String = net.lingyun.ultraui.android.core.UPConfig.search.bgColor,
    placeholder: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.search.placeholder,
    clearabled: Boolean = net.lingyun.ultraui.android.core.UPConfig.search.clearabled,
    onlyClearableOnFocused: Boolean = net.lingyun.ultraui.android.core.UPConfig.search.onlyClearableOnFocused,
    focus: Boolean = net.lingyun.ultraui.android.core.UPConfig.search.focus,
    showAction: Boolean = net.lingyun.ultraui.android.core.UPConfig.search.showAction,
    actionText: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.search.actionText,
    label: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.search.label,
    inputAlign: String = net.lingyun.ultraui.android.core.UPConfig.search.inputAlign,
    disabled: Boolean = net.lingyun.ultraui.android.core.UPConfig.search.disabled,
    animation: Boolean = net.lingyun.ultraui.android.core.UPConfig.search.animation,
    borderColor: String = net.lingyun.ultraui.android.core.UPConfig.search.borderColor,
    searchIconColor: String = net.lingyun.ultraui.android.core.UPConfig.search.searchIconColor,
    searchIconSize: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.search.searchIconSize,
    color: String = net.lingyun.ultraui.android.core.UPConfig.search.color,
    placeholderColor: String = net.lingyun.ultraui.android.core.UPConfig.search.placeholderColor,
    searchIcon: String = net.lingyun.ultraui.android.core.UPConfig.search.searchIcon,
    margin: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.search.margin,
    iconPosition: String = net.lingyun.ultraui.android.core.UPConfig.search.iconPosition,
    maxlength: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.search.maxlength,
    height: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.search.height,
    adjustPosition: Boolean = net.lingyun.ultraui.android.core.UPConfig.search.adjustPosition,
    autoBlur: Boolean = net.lingyun.ultraui.android.core.UPConfig.search.autoBlur,
    inputStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    actionStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    onInput: ((String) -> Unit)? = null,
    onChange: ((String) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    onCustom: (() -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onClickIcon: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPSearch(
        props = UPSearchProps(
            modelValue = modelValue,
            value = value,
            shape = shape,
            bgColor = bgColor,
            placeholder = placeholder,
            clearabled = clearabled,
            onlyClearableOnFocused = onlyClearableOnFocused,
            focus = focus,
            showAction = showAction,
            actionText = actionText,
            label = label,
            inputAlign = inputAlign,
            disabled = disabled,
            animation = animation,
            borderColor = borderColor,
            searchIconColor = searchIconColor,
            searchIconSize = searchIconSize,
            color = color,
            placeholderColor = placeholderColor,
            searchIcon = searchIcon,
            margin = margin,
            iconPosition = iconPosition,
            maxlength = maxlength,
            height = height,
            adjustPosition = adjustPosition,
            autoBlur = autoBlur,
            inputStyle = inputStyle,
            actionStyle = actionStyle,
            customStyle = customStyle,
        ),
        onInput = onInput,
        onChange = onChange,
        onClear = onClear,
        onSearch = onSearch,
        onCustom = onCustom,
        onFocus = onFocus,
        onBlur = onBlur,
        onClick = onClick,
        onClickIcon = onClickIcon,
        diagnostics = diagnostics,
        modifier = modifier,
    )
}

private fun Color.toSearchHex(): String = "#%08X".format(toArgb())
