package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upTestTag

private const val OverlayComponentName = "UPOverlay"

/** Native Compose counterpart of uview-plus `u-overlay`. */
@Composable
public fun UPOverlay(
    props: UPOverlayProps = UPOverlayProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {},
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return

    val opacity = remember(props.opacity) { props.opacity.asFiniteFloatOrNull() }
    LaunchedEffect(props.opacity, opacity, diagnostics) {
        if (opacity == null) {
            diagnostics.report(
                OverlayComponentName,
                "opacity",
                props.opacity,
                "Malformed opacity; using 0.5.",
            )
        }
    }
    val zIndex = remember(props.zIndex) { props.zIndex.asFiniteFloatOrNull() ?: 10070f }
    LaunchedEffect(props.zIndex, diagnostics) {
        if (props.zIndex.asFiniteFloatOrNull() == null) {
            diagnostics.report(
                OverlayComponentName,
                "zIndex",
                props.zIndex,
                "Malformed zIndex; using 10070.",
            )
        }
    }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, OverlayComponentName)
    // `<u-transition mode="fade" :duration>` fades the scrim in. The target has to start
    // at zero on the first frame, otherwise `animateFloatAsState` initialises straight to
    // the final opacity and `duration` never has anything to interpolate.
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val fade by animateFloatAsState(
        targetValue = if (entered) (opacity ?: 0.5f).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = upOverlayFadeDuration(props.duration)),
        label = "up-overlay-fade",
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(zIndex)
            .background(Color.Black.copy(alpha = fade))
            .applyUPResolvedStyle(style)
            .upTestTag("overlay")
            // Even without a callback the overlay consumes taps, preventing events from
            // reaching the content underneath it.
            .upClickable(enabled = true, onClick = { onClick?.invoke() }),
        content = content,
    )
}

/** Convenience overload for generated code that keeps the legacy raw opacity value. */
@Composable
public fun UPOverlay(
    show: Boolean,
    opacity: UPRawValue = 0.5,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPOverlay(
        props = UPOverlayProps(show = show, opacity = opacity),
        onClick = onClick,
        diagnostics = diagnostics,
    )
}
