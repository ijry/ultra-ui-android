package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.TextStyle
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
    val topPadding = upRawDp(props.top, 0.dp) + if (props.safeAreaInsetTop) 24.dp else 0.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = topPadding)
            .background(background, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
            .applyUPResolvedStyle(style)
            .upTestTag("notify")
            .upClickable(enabled = onClick != null, onClick = { onClick?.invoke() })
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            props.message,
            modifier = Modifier.weight(1f),
            style = TextStyle(color = textColor, fontSize = props.fontSize.upTextUnitOr(15.sp)),
        )
        if (onClose != null) {
            UPIcon(
                props = UPIconProps(name = "close", size = 16, color = props.color),
                modifier = Modifier.upTestTag("notify-close"),
                onClick = { dismiss() },
                diagnostics = diagnostics,
            )
        }
    }
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
