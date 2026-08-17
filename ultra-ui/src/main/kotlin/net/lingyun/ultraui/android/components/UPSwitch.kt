package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.shape.RoundedCornerShape
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.UPUnit
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val SwitchComponentName = "UPSwitch"

/** Native Compose counterpart of uview-plus `u-switch`. */
@Composable
public fun UPSwitch(
    props: UPSwitchProps = UPSwitchProps(),
    onInput: ((UPRawValue) -> Unit)? = null,
    onChange: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val externalValue = resolveUPModelValue(props.modelValue, props.value)
    var currentValue by remember { mutableStateOf(externalValue) }
    LaunchedEffect(externalValue) { currentValue = externalValue }

    LaunchedEffect(currentValue, props.activeValue, props.inactiveValue, diagnostics) {
        if (!selectionRawEquals(currentValue, props.activeValue) &&
            !selectionRawEquals(currentValue, props.inactiveValue)
        ) {
            diagnostics.report(
                SwitchComponentName,
                "modelValue",
                currentValue,
                "Value must equal activeValue or inactiveValue; rendering the inactive state.",
            )
        }
    }

    val size = upRawDp(props.size, 25.dp).coerceAtLeast(2.dp)
    val space = upRawDp(props.space, 0.dp).coerceAtLeast(0.dp)
    val active = selectionRawEquals(currentValue, props.activeValue)
    val inactiveBackground = selectionColor(props.inactiveColor, Color.White)
    val activeBackground = selectionColor(props.activeColor, UPTheme.Primary)
    val dotColor = selectionColor(
        if (active) props.dotActiveColor else props.dotInactiveColor,
        Color.White,
    )
    val background = if (active) activeBackground else inactiveBackground
    val outerShape = RoundedCornerShape(percent = 50)
    val dotSize = (size - 2.dp - space * 2).coerceAtLeast(2.dp)
    val width = size * 2 + 2.dp
    val enabled = !props.disabled && !props.loading
    val toggle: () -> Unit = {
        val next = if (active) props.inactiveValue else props.activeValue
        if (!props.asyncChange) currentValue = next
        onInput?.invoke(next)
        onChange?.invoke(next)
        Unit
    }
    // Keep the root test/semantics node separate from the visual/clickable layer.
    // Compose clickable merges descendants, which would otherwise hide the dot's
    // stable test tag from the merged semantics tree.
    val root = modifier
        .size(width = width, height = size + 2.dp)
        .upTestTag("switch")
        .semantics {
            if (enabled) {
                role = Role.Switch
                onClick(action = {
                    toggle()
                    true
                })
            }
        }

    Box(modifier = root, contentAlignment = Alignment.CenterStart) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(outerShape)
                .background(background, outerShape)
                .border(1.dp, UPTheme.Border.copy(alpha = 0.35f), outerShape)
                .upClickable(
                    enabled = enabled,
                    role = Role.Switch,
                    onClick = toggle,
                ),
        )
        val left = if (active) width - dotSize - space - 1.dp else space + 1.dp
        Box(
            modifier = Modifier
                .offset(x = left)
                .size(dotSize)
                .clip(RoundedCornerShape(percent = 50))
                .background(dotColor, RoundedCornerShape(percent = 50))
                .upTestTag("switch-dot"),
            contentAlignment = Alignment.Center,
        ) {
            if (props.loading) {
                UPLoadingIcon(
                    props = UPLoadingIconProps(
                        show = true,
                        mode = "circle",
                        color = if (active) props.activeColor else UPTheme.Tips.toString(),
                        size = (size.value * 0.6f).coerceAtLeast(8f),
                    ),
                    diagnostics = diagnostics,
                    modifier = Modifier.upTestTag("switch-loading"),
                )
            }
        }
    }
}

/** Convenience direct-value overload for generated source that uses a Boolean model. */
@Composable
public fun UPSwitch(
    value: Boolean,
    modifier: Modifier = Modifier,
    onChange: ((Boolean) -> Unit)? = null,
) {
    UPSwitch(
        props = UPSwitchProps(modelValue = value, value = value),
        modifier = modifier,
        onChange = { raw -> onChange?.invoke(raw as? Boolean ?: false) },
    )
}
