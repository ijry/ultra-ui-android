package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Resolved animation shape for a `u-transition` [mode]: whether opacity fades, whether it scales
 * from 0.95, and the sign of the initial X/Y translate (as a fraction of the element size).
 */
internal data class UPTransitionSpec(
    val fade: Boolean,
    val scale: Boolean,
    val offsetXSign: Int,
    val offsetYSign: Int,
)

/** Maps a `u-transition` mode name to its [UPTransitionSpec], mirroring the upstream CSS classes. */
internal fun upTransitionSpec(mode: String): UPTransitionSpec = when (mode) {
    "fade" -> UPTransitionSpec(fade = true, scale = false, offsetXSign = 0, offsetYSign = 0)
    "zoom" -> UPTransitionSpec(fade = false, scale = true, offsetXSign = 0, offsetYSign = 0)
    "fade-zoom" -> UPTransitionSpec(fade = true, scale = true, offsetXSign = 0, offsetYSign = 0)
    "fade-up" -> UPTransitionSpec(fade = true, scale = false, offsetXSign = 0, offsetYSign = 1)
    "fade-down" -> UPTransitionSpec(fade = true, scale = false, offsetXSign = 0, offsetYSign = -1)
    "fade-left" -> UPTransitionSpec(fade = true, scale = false, offsetXSign = -1, offsetYSign = 0)
    "fade-right" -> UPTransitionSpec(fade = true, scale = false, offsetXSign = 1, offsetYSign = 0)
    "slide-up" -> UPTransitionSpec(fade = false, scale = false, offsetXSign = 0, offsetYSign = 1)
    "slide-down" -> UPTransitionSpec(fade = false, scale = false, offsetXSign = 0, offsetYSign = -1)
    "slide-left" -> UPTransitionSpec(fade = false, scale = false, offsetXSign = -1, offsetYSign = 0)
    "slide-right" -> UPTransitionSpec(fade = false, scale = false, offsetXSign = 1, offsetYSign = 0)
    else -> UPTransitionSpec(fade = true, scale = false, offsetXSign = 0, offsetYSign = 0)
}

/** Parses the ms duration (`String`|`Number`) exactly like upstream `${duration}ms`. */
internal fun upTransitionDurationMillis(duration: Any?): Int =
    duration?.toString()?.trim()?.toDoubleOrNull()?.toInt()?.coerceAtLeast(0) ?: 300

/** Maps a CSS timing-function name to a Compose [Easing]. */
internal fun upTransitionEasing(name: String): Easing = when (name) {
    "linear" -> LinearEasing
    "ease" -> CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
    "ease-in" -> CubicBezierEasing(0.42f, 0f, 1f, 1f)
    "ease-in-out" -> CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
    else -> CubicBezierEasing(0f, 0f, 0.58f, 1f) // ease-out (upstream default)
}

/**
 * Native Compose counterpart of uview-plus `u-transition`.
 *
 * Toggling [`UPTransitionProps.show`] runs the enter/leave animation for the chosen `mode` over
 * `duration` ms with `timingFunction` easing, following upstream's lifecycle: on enter it mounts,
 * fires [onBeforeEnter] then [onEnter] and, when the animation settles, [onAfterEnter]; on leave it
 * fires [onBeforeLeave] then [onLeave], animates out and, once finished, [onAfterLeave] before
 * unmounting. Tapping the content emits [onClick]. A single progress 0..1 drives opacity, a 0.95
 * scale and an initial size-fraction translate per [upTransitionSpec].
 */
@Composable
public fun UPTransition(
    props: UPTransitionProps = UPTransitionProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onBeforeEnter: (() -> Unit)? = null,
    onEnter: (() -> Unit)? = null,
    onAfterEnter: (() -> Unit)? = null,
    onBeforeLeave: (() -> Unit)? = null,
    onLeave: (() -> Unit)? = null,
    onAfterLeave: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTransition")
    val spec = remember(props.mode) {
        if (props.mode !in RecognizedModes) {
            diagnostics.report("UPTransition", "mode", props.mode, "unknown mode; falling back to fade")
        }
        upTransitionSpec(props.mode)
    }
    val durationMillis = upTransitionDurationMillis(props.duration)
    val easing = remember(props.timingFunction) { upTransitionEasing(props.timingFunction) }
    val progress = remember { Animatable(0f) }
    var rendered by remember { mutableStateOf(false) }

    LaunchedEffect(props.show) {
        if (props.show) {
            rendered = true
            onBeforeEnter?.invoke()
            onEnter?.invoke()
            progress.animateTo(1f, tween(durationMillis, easing = easing))
            onAfterEnter?.invoke()
        } else {
            if (!rendered) return@LaunchedEffect
            onBeforeLeave?.invoke()
            onLeave?.invoke()
            progress.animateTo(0f, tween(durationMillis, easing = easing))
            onAfterLeave?.invoke()
            rendered = false
        }
    }

    if (!rendered) return

    Box(
        modifier = modifier
            .graphicsLayer {
                val p = progress.value
                if (spec.fade) alpha = p
                if (spec.scale) {
                    val s = 0.95f + 0.05f * p
                    scaleX = s
                    scaleY = s
                }
                if (spec.offsetXSign != 0) translationX = (1f - p) * size.width * spec.offsetXSign
                if (spec.offsetYSign != 0) translationY = (1f - p) * size.height * spec.offsetYSign
            }
            .applyUPResolvedStyle(style)
            .upClickable(onClick = { onClick?.invoke() })
            .upTestTag("transition"),
    ) {
        content()
    }
}

private val RecognizedModes = setOf(
    "fade", "zoom", "fade-zoom", "fade-up", "fade-down", "fade-left", "fade-right",
    "slide-up", "slide-down", "slide-left", "slide-right",
)
