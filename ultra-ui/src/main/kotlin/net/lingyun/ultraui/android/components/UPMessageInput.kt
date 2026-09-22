package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val MessageInputComponentName = "UPMessageInput"
private val MessageInputModes = setOf("box", "bottomLine", "middleLine")

/**
 * `valueModel` normalisation from the `modelValue` watcher: stringify then clip to `maxlength`.
 */
internal fun upMessageInputValue(raw: String, maxLength: Int): String =
    if (maxLength <= 0) "" else raw.take(maxLength)

/**
 * Native Compose counterpart of uview-plus `u-message-input`.
 *
 * A verification-code field: `maxlength` cells laid out centred, an invisible number field
 * capturing input, and per-`mode` decoration. `box` outlines each cell (the active one takes
 * `activeColor`), `bottomLine`/`middleLine` draw a coloured bar. Filled cells show the digit,
 * or a bullet when `dotFill` is set. Cell width/height come from `width` (rpx), text from
 * `fontSize` (rpx) in `inactiveColor`, matching the upstream inline styles.
 *
 * `change` fires on every edit, `finish` once the length reaches `maxlength`.
 *
 * `breathe` drives the upstream CSS `breathe` keyframe (opacity 0.3 -> 1 -> 0.3 over 2s,
 * infinite, alternating) on the active cell; the port reproduces it with an infinite Compose
 * transition and holds the active cell fully opaque when `breathe` is false.
 */
@Composable
public fun UPMessageInput(
    props: UPMessageInputProps = UPMessageInputProps(),
    onChange: ((String) -> Unit)? = null,
    onFinish: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val mode = upSafeEnum(props.mode, MessageInputModes, "box", diagnostics, MessageInputComponentName, "mode")
    val maxLength = props.maxlength.upIntOrDefault(4).coerceIn(0, 100)
    val initial = upMessageInputValue(props.modelValue.upInputString(), maxLength)
    var value by remember(props.modelValue, props.maxlength) { mutableStateOf(initial) }
    var focused by remember { mutableStateOf(false) }
    var finishEmitted by remember { mutableStateOf(initial.length >= maxLength && maxLength > 0) }
    val focusRequester = remember { FocusRequester() }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, MessageInputComponentName)

    val cell = upDimension(props.width, 40.dp).coerceAtLeast(1.dp)
    val fontSize = (style.fontSize ?: upDimension(props.fontSize, 30.dp)).value.sp
    val activeColor = UPColor.parse(props.activeColor, UPTheme.Primary)
    val inactiveColor = UPColor.parse(props.inactiveColor, UPTheme.Content)
    val fontWeight = if (props.bold) FontWeight.Bold else FontWeight.Normal
    val cellShape = RoundedCornerShape(3.dp)

    // `@keyframes breathe { 0%,100% { opacity: .3 } 50% { opacity: 1 } }`, 2s infinite ease.
    val breatheAlpha = if (props.breathe) {
        val transition = rememberInfiniteTransition(label = "up-message-input-breathe")
        transition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 1_000), RepeatMode.Reverse),
            label = "up-message-input-breathe-alpha",
        ).value
    } else {
        1f
    }

    LaunchedEffect(props.focus) {
        if (props.focus && !props.disabledKeyboard) runCatching { focusRequester.requestFocus() }
    }

    fun accept(raw: String) {
        val next = upMessageInputValue(raw.filter { it.isDigit() }, maxLength)
        if (next == value) return
        value = next
        onChange?.invoke(next)
        if (maxLength > 0 && next.length >= maxLength) {
            if (!finishEmitted) {
                finishEmitted = true
                onFinish?.invoke(next)
            }
        } else {
            finishEmitted = false
        }
    }

    Box(
        modifier = modifier.applyUPResolvedStyle(style).upTestTag("message-input"),
        contentAlignment = Alignment.Center,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(maxLength) { index ->
                val isActive = index == value.length
                val filled = index < value.length
                val boxBorder = if (mode == "box") {
                    Modifier.border(1.dp, if (isActive) activeColor else inactiveColor, cellShape)
                } else {
                    Modifier
                }
                Box(
                    modifier = Modifier
                        .size(cell)
                        .then(boxBorder)
                        .then(if (isActive) Modifier.alpha(breatheAlpha) else Modifier)
                        .upTestTag("message-input-cell-$index"),
                    contentAlignment = Alignment.Center,
                ) {
                    if (filled) {
                        BasicText(
                            text = if (props.dotFill) "●" else value[index].toString(),
                            style = TextStyle(color = inactiveColor, fontSize = fontSize, fontWeight = fontWeight, textAlign = TextAlign.Center),
                        )
                    }
                    // The active-cell placeholder tick (`.u-placeholder-line`), shown for every
                    // mode except `middleLine`.
                    if (isActive && mode != "middleLine") {
                        Box(modifier = Modifier.width(1.dp).height(cell * 0.5f).background(Color(0xFF333333)))
                    }
                    if (mode == "middleLine") {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .width(cell * 0.8f)
                                .height(if (props.bold) 4.dp else 2.dp)
                                .background(if (isActive) activeColor else inactiveColor),
                        )
                    }
                    if (mode == "bottomLine") {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .width(cell * 0.8f)
                                .height(if (props.bold) 4.dp else 2.dp)
                                .background(if (isActive) activeColor else inactiveColor),
                        )
                    }
                }
            }
        }

        BasicTextField(
            value = value,
            onValueChange = ::accept,
            modifier = Modifier
                .fillMaxWidth()
                .height(cell)
                .focusRequester(focusRequester)
                .onFocusChanged { state -> focused = state.isFocused }
                .alpha(0f)
                .upTestTag("message-input-field"),
            enabled = !props.disabledKeyboard,
            singleLine = true,
            textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(Color.Transparent),
        )
    }
}
