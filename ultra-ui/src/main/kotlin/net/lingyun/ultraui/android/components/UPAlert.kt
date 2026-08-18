package net.lingyun.ultraui.android.components

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
private val AlertTransitions: Set<String> = setOf("fade", "slide-top", "slide-bottom", "zoom-in", "none")

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
    // Compose keeps rendering deterministic; transitionMode is validated and accepted even though
    // native callers may choose to animate the surrounding layout themselves.
    upSafeEnum(props.transitionMode, AlertTransitions, "fade", diagnostics, AlertComponentName, "transitionMode")
    val duration = props.duration.upLongOrDefault(0L)

    fun dismiss() {
        if (!visible) return
        visible = false
        onUpdateModelValue?.invoke(false)
        onUpdateShow?.invoke(false)
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
    if (!visible) return

    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, AlertComponentName)
    val accent = upTypeColor(type, UPTheme.Warning)
    val background = if (effect == "dark") accent else alertLightBackground(type)
    val foreground = if (effect == "dark") Color.White else accent
    val textAlign = if (props.center) TextAlign.Center else TextAlign.Start
    val fontSize = props.fontSize.upTextUnitOr(14.sp)
    val iconName = props.icon.ifEmpty { defaultAlertIcon(type) }

    Row(
        modifier = modifier
            .fillMaxWidth()
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
