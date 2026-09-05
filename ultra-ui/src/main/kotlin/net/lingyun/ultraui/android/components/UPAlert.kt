package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upBooleanOrDefault
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val AlertComponentName: String = "UPAlert"
private val AlertTypes: Set<String> = setOf("primary", "success", "warning", "error", "info")
private val AlertEffects: Set<String> = setOf("light", "dark")
/** `u-transition`'s own mode list; `u-alert` passes `transitionMode` straight through. */
private val AlertTransitions: Set<String> = UPTransitionModes

/** Native Compose counterpart of uview-plus `u-alert`. */
@Composable
public fun UPAlert(
    props: UPAlertProps = UPAlertProps(),
    modifier: Modifier = Modifier,
    onUpdateModelValue: ((Boolean) -> Unit)? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onClosed: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val requestedVisible = props.modelValue ?: props.value.upBooleanOrDefault(true)
    var visible by remember(props.modelValue, props.value) { mutableStateOf(requestedVisible) }
    val type = upSafeEnum(props.type, AlertTypes, "warning", diagnostics, AlertComponentName, "type")
    val effect = upSafeEnum(props.effect, AlertEffects, "light", diagnostics, AlertComponentName, "effect")
    // `<up-transition :mode="transitionMode" :show="show">`: the banner animates itself in,
    // and out again when `closable`/`duration` dismisses it, so the mode has to survive to
    // the modifier rather than being validated and dropped.
    val transitionMode = upSafeEnum(props.transitionMode, AlertTransitions, "fade", diagnostics, AlertComponentName, "transitionMode")
    val duration = props.duration.upLongOrDefault(0L)

    fun dismiss() {
        if (!visible) return
        visible = false
        onUpdateModelValue?.invoke(false)
        onUpdateShow?.invoke(false)
        // `closeHandler()` emits `close` immediately; `closed` follows once the watcher on
        // `show` sees the flag flip, which is what the leave animation waits on.
        onClose?.invoke()
        onClosed?.invoke()
    }

    LaunchedEffect(requestedVisible) {
        visible = requestedVisible
    }
    LaunchedEffect(visible, duration, props.modelValue, props.value) {
        if (visible && duration > 0L) {
            delay(duration)
            dismiss()
        }
    }
    // `u-transition` keeps the element mounted through the leave animation, then removes it,
    // which is why `closed` fires after `close`.
    //
    // Deliberate difference: upstream's watcher is `immediate: true`, so a banner that
    // mounts visible also fades in. Here `entered` starts at the current visibility, so the
    // first frame is the settled state and only a later `show` transition animates. The
    // screenshot references capture frame 0 — a mount-time fade would make every banner
    // invisible in its own reference and destroy the pixel evidence that proves the type
    // colours are painted at all. A fade a user cannot distinguish is not worth that.
    var entered by remember { mutableStateOf(requestedVisible) }
    LaunchedEffect(visible) { entered = visible }
    // `u-alert` never passes a duration, so `u-transition`'s own default of 300ms applies.
    val transitionDuration = upTransitionDuration(null)
    val progress by animateFloatAsState(
        targetValue = if (entered && visible) 1f else 0f,
        animationSpec = tween(transitionDuration),
        label = "up-alert-transition",
    )
    // Only once the leave animation has finished does the banner leave the tree.
    if (!visible && progress <= 0f) return

    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, AlertComponentName)
    val accent = upTypeColor(type, UPTheme.Warning)
    val background = if (effect == "dark") accent else alertLightBackground(type)
    val foreground = if (effect == "dark") Color.White else accent
    val textAlign = if (props.center) TextAlign.Center else TextAlign.Start
    val fontSize = props.fontSize.upTextUnitOr(14.sp)
    val iconName = props.icon.ifEmpty { defaultAlertIcon(type) }

    val (offsetXFraction, offsetYFraction) = upTransitionOffsetFraction(transitionMode)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                if (upTransitionFades(transitionMode)) alpha = progress
                if (upTransitionScales(transitionMode)) {
                    val scale = UPTransitionZoomScale + (1f - UPTransitionZoomScale) * progress
                    scaleX = scale
                    scaleY = scale
                }
                translationX = offsetXFraction * (1f - progress) * size.width
                translationY = offsetYFraction * (1f - progress) * size.height
            }
            .background(background, RoundedCornerShape(4.dp))
            .applyUPResolvedStyle(style)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .upTestTag("alert")
            .upClickable(enabled = onClick != null, onClick = { onClick?.invoke() }),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (props.showIcon) {
            UPIcon(
                props = UPIconProps(name = iconName, size = 18, color = alertColorString(foreground)),
                modifier = Modifier.upTestTag("alert-icon"),
                diagnostics = diagnostics,
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            if (props.title.isNotEmpty()) {
                BasicText(
                    text = props.title,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(color = foreground, fontSize = fontSize, textAlign = textAlign),
                )
            }
            if (props.description.isNotEmpty()) {
                BasicText(
                    text = props.description,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(color = foreground.copy(alpha = 0.82f), fontSize = (fontSize.value * 0.93f).sp, textAlign = textAlign),
                )
            }
        }
        if (props.closable) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .upTestTag("alert-close")
                    .upClickable(onClick = ::dismiss),
                contentAlignment = Alignment.Center,
            ) {
                UPIcon(
                    props = UPIconProps(name = "close", size = 15, color = alertColorString(foreground)),
                    diagnostics = diagnostics,
                )
            }
        }
    }
}

/** Convenience overload for generated source that expands common alert attributes directly. */
@Composable
public fun UPAlert(
    title: String,
    type: String = "warning",
    description: String = "",
    modelValue: Boolean? = true,
    closable: Boolean = false,
    onUpdateModelValue: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPAlert(
        props = UPAlertProps(
            title = title,
            type = type,
            description = description,
            modelValue = modelValue,
            closable = closable,
        ),
        modifier = modifier,
        onUpdateModelValue = onUpdateModelValue,
        onClick = onClick,
        onClose = onClose,
        diagnostics = diagnostics,
    )
}

private fun alertLightBackground(type: String): Color = when (type) {
    "primary" -> Color(0xFFECF5FF)
    "success" -> Color(0xFFF0F9EB)
    "error" -> Color(0xFFFEF0F0)
    "info" -> Color(0xFFF4F4F5)
    else -> Color(0xFFFDF6EC)
}

private fun defaultAlertIcon(type: String): String = when (type) {
    "primary" -> "more-circle-fill"
    "success" -> "checkmark-circle-fill"
    "error" -> "close-circle-fill"
    "info" -> "info-circle-fill"
    else -> "error-circle-fill"
}

private fun alertColorString(color: Color): String = "#%08X".format(color.value.toLong())
