package net.lingyun.ultraui.android.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `u-canvas`.
 *
 * Provides a drawing surface sized by [`UPCanvasProps.width`]/[height]/[unit] (or filling its parent
 * when [useRootHeightAndWidth]) on a [bgColor] background. Where upstream hands callers an imperative
 * 2D context via a ref, the Android port exposes the Compose-native [onDraw] `DrawScope` lambda.
 * Touches are reported through [onTouchStart]/[onTouchMove]/[onTouchEnd] (x, y in px), and [disableScroll]
 * consumes the moves so a scrolling parent does not hijack the gesture. [onReady] fires once laid out.
 *
 * Downgrade: `toTempFilePath` (rasterise to a file) is a host concern (as with `u-signature`), and the
 * `canvasId` element id is inert.
 */
@Composable
public fun UPCanvas(
    props: UPCanvasProps = UPCanvasProps(),
    modifier: Modifier = Modifier,
    onReady: (() -> Unit)? = null,
    onTouchStart: ((Float, Float) -> Unit)? = null,
    onTouchMove: ((Float, Float) -> Unit)? = null,
    onTouchEnd: ((Float, Float) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    onDraw: (DrawScope.() -> Unit)? = null,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCanvas")
    val background = UPColor.parse(props.bgColor, Color.White)

    LaunchedEffect(Unit) { onReady?.invoke() }

    val sizeModifier = if (props.useRootHeightAndWidth) {
        Modifier.fillMaxSize()
    } else {
        Modifier.size(
            upDimension("${props.width}${props.unit}", 300.dp),
            upDimension("${props.height}${props.unit}", 300.dp),
        )
    }

    Canvas(
        modifier = modifier
            .then(sizeModifier)
            .background(background)
            .applyUPResolvedStyle(style)
            .pointerInput(props.disableScroll, onTouchStart, onTouchMove, onTouchEnd) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    onTouchStart?.invoke(down.position.x, down.position.y)
                    if (props.disableScroll) down.consume()
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break
                        if (change.pressed) {
                            onTouchMove?.invoke(change.position.x, change.position.y)
                            if (props.disableScroll) change.consume()
                        } else {
                            onTouchEnd?.invoke(change.position.x, change.position.y)
                            break
                        }
                    }
                }
            }
            .upTestTag("canvas"),
    ) {
        onDraw?.invoke(this)
    }
}
