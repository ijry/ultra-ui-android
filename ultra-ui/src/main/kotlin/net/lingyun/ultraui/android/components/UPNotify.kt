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
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val NotifyComponentName: String = "UPNotify"
private val NotifyTypes: Set<String> = setOf("primary", "success", "warning", "error")

/** Native Compose counterpart of uview-plus `u-notify`. */
@Composable
public fun UPNotify(
    props: UPNotifyProps = UPNotifyProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (props.message.isEmpty()) return

    val type = upSafeEnum(props.type, NotifyTypes, "primary", diagnostics, NotifyComponentName, "type")
    val duration = props.duration.upLongOrDefault(3000L)
    var visible by remember(props.message) { mutableStateOf(true) }

    fun dismiss() {
        if (!visible) return
        visible = false
        onClose?.invoke()
    }

    LaunchedEffect(props.message, props.duration) {
        visible = true
        if (duration > 0L) {
            delay(duration)
            if (visible) {
                visible = false
                onComplete?.invoke()
            }
        }
    }
    if (!visible) return

    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, NotifyComponentName)
    val background = if (props.bgColor.isNotEmpty()) {
        UPColor.parse(props.bgColor, UPTheme.Primary)
    } else {
        upTypeColor(type, UPTheme.Primary)
    }
    val textColor = UPColor.parse(props.color, Color.White)
    val topOffset = upRawDp(props.top, 0.dp)
    val fontSize = props.fontSize.upTextUnitOr(15.sp)
    // `<u-transition mode="slide-down">`: the banner drops in from above the top edge.
    // Like `u-alert`, `entered` starts settled so the screenshot references (frame 0) keep
    // showing the banner; only a later message animates.
    var entered by remember(props.message) { mutableStateOf(true) }
    LaunchedEffect(props.message) { entered = true }
    val progress by animateFloatAsState(
        targetValue = if (entered && visible) 1f else 0f,
        animationSpec = tween(upTransitionDuration(null)),
        label = "up-notify-slide",
    )
    val (_, offsetYFraction) = upTransitionOffsetFraction("slide-down")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = topOffset)
            .graphicsLayer { translationY = offsetYFraction * (1f - progress) * size.height }
            .background(background)
            .applyUPResolvedStyle(style)
            .upTestTag("notify")
            .upClickable(enabled = onClick != null, onClick = { onClick?.invoke() }),
    ) {
        // `<u-status-bar v-if="tmpConfig.safeAreaInsetTop">` sits inside the coloured
        // banner, so the tint reaches behind the status bar rather than starting below it.
        if (props.safeAreaInsetTop) {
            UPStatusBar(diagnostics = diagnostics)
        }
        Row(
            // `.u-notify__warpper` centres its row; `.u-notify` pads 8px 10px.
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // `v-if="['success', 'warning', 'error'].includes(type)"`: `primary` has no glyph.
            upNotifyIconName(type)?.let { iconName ->
                UPIcon(
                    props = UPIconProps(name = iconName, size = upNotifyIconSize(props.fontSize), color = props.color),
                    modifier = Modifier.padding(end = 4.dp).upTestTag("notify-icon"),
                    diagnostics = diagnostics,
                )
            }
            BasicText(
                props.message,
                modifier = Modifier.upTestTag("notify-text"),
                style = TextStyle(color = textColor, fontSize = fontSize, textAlign = TextAlign.Center),
            )
            if (onClose != null) {
                UPIcon(
                    props = UPIconProps(name = "close", size = 16, color = props.color),
                    modifier = Modifier.padding(start = 8.dp).upTestTag("notify-close"),
                    onClick = { dismiss() },
                    diagnostics = diagnostics,
                )
            }
        }
    }
}

/**
 * Host for the imperative `u-notify` API: whatever [controller] currently holds is what
 * renders, so `show()` / `success()` / `close()` drive the banner from anywhere.
 */
@Composable
public fun UPNotifyHost(
    controller: UPNotifyController,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val current by controller.current
    val options = current ?: return
    UPNotify(
        props = options,
        modifier = modifier,
        onClick = onClick,
        onClose = {
            controller.close()
            onClose?.invoke()
        },
        onComplete = {
            controller.close()
            onComplete?.invoke()
        },
        diagnostics = diagnostics,
    )
}

/** Convenience overload for generated source that only supplies a message. */
@Composable
public fun UPNotify(
    message: String,
    type: String = "primary",
    duration: Any? = 3000,
    onComplete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPNotify(
        props = UPNotifyProps(message = message, type = type, duration = duration),
        onComplete = onComplete,
        modifier = modifier,
        diagnostics = diagnostics,
    )
}
