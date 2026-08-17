package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upBooleanOrDefault
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val ToastComponentName = "UPToast"
private val ToastTypes = setOf("default", "primary", "success", "error", "warning", "loading")
private val ToastPositions = setOf("center", "top", "bottom")

/** Renders a controlled toast. The controller/host overload provides imperative show/hide. */
@Composable
public fun UPToast(
    props: UPToastProps = UPToastProps(),
    modifier: Modifier = Modifier,
    onComplete: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val requestedShow = props.show.upBooleanOrDefault(false)
    var visible by remember(props) { mutableStateOf(requestedShow) }
    val type = upSafeEnum(props.type, ToastTypes, "default", diagnostics, ToastComponentName, "type")
    val position = upSafeEnum(props.position, ToastPositions, "center", diagnostics, ToastComponentName, "position")
    val duration = props.duration.asFiniteFloatOrNull()?.toLong() ?: 2000L
    LaunchedEffect(props.show, props.message, props.duration) {
        visible = requestedShow
        if (requestedShow && duration >= 0L) {
            delay(duration.coerceAtLeast(0L))
            visible = false
            invokeUPToastCallback(props.callback)
            onComplete?.invoke()
        }
    }
    if (!visible) return

    val zIndex = props.zIndex.asFiniteFloatOrNull() ?: 10080f
    val panelColor = when (type) {
        "primary" -> UPTheme.Primary
        "success" -> UPTheme.Success
        "warning" -> UPTheme.Warning
        "error" -> UPTheme.Error
        else -> Color(0xFF585858)
    }
    val textColor = if (type == "default" || type == "loading") Color.White else Color.White
    val alignment = when (position) {
        "top" -> Alignment.TopCenter
        "bottom" -> Alignment.BottomCenter
        else -> Alignment.Center
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(zIndex)
            .upTestTag("toast"),
        contentAlignment = alignment,
    ) {
        if (props.overlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .upTestTag("toast-overlay"),
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = if (position == "center") 0.dp else 48.dp)
                .widthIn(max = 320.dp)
                .background(panelColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 20.dp, vertical = if (props.loading) 20.dp else 12.dp)
                .upTestTag("toast-content"),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (props.loading || type == "loading") {
                UPLoadingIcon(
                    props = UPLoadingIconProps(
                        show = true,
                        mode = props.loadingMode,
                        size = 22,
                        color = "#ffffff",
                        text = "",
                    ),
                )
            } else if (props.icon.isNotEmpty()) {
                UPIcon(UPIconProps(name = props.icon, size = 20, color = "#ffffff"))
            }
            if (props.message.isNotEmpty()) {
                BasicText(
                    props.message,
                    style = TextStyle(color = textColor, fontSize = 15.sp),
                )
            }
        }
    }
}

/** Composes the current controller value at the call site's overlay layer. */
@Composable
public fun UPToastHost(
    controller: UPToastController,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val current = controller.current.value ?: return
    UPToast(
        props = current,
        modifier = modifier,
        onComplete = controller::hide,
        diagnostics = diagnostics,
    )
}

private fun invokeUPToastCallback(callback: Any?) {
    when (callback) {
        is Function0<*> -> callback.invoke()
        is Function1<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            (callback as (Any?) -> Any?).invoke(null)
        }
    }
}
