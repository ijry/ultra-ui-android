package net.lingyun.ultraui.android.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

/** A single drawn stroke — an ordered list of points. */
internal class UPSignatureStroke(val points: MutableList<Offset> = mutableStateListOf())

/** `isEmpty`: no strokes recorded yet. */
internal fun upSignatureIsEmpty(strokes: List<UPSignatureStroke>): Boolean =
    strokes.all { it.points.size < 2 }

/**
 * Native Compose counterpart of uview-plus `u-signature`.
 *
 * A finger-drawn signature pad: dragging on the `bgColor` canvas records strokes rendered with
 * `color`/`thickness` (round cap/join). The toolbar (`showToolbar`) offers clear (resets the pad,
 * emits `clear`) and confirm (emits `confirm`
 * once the pad is non-empty; an empty pad reports `error`). Sizes come from `width`/`height` (px→dp).
 *
 * Difference: upstream writes a temp file and emits its path; Android has no ambient temp-file
 * sink here, so [onConfirm] simply signals success and the host captures the pad (e.g. wrapping it
 * in its own graphics layer) to persist as it sees fit.
 */
@Composable
public fun UPSignature(
    props: UPSignatureProps = UPSignatureProps(),
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    onError: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSignature")
    val density = LocalDensity.current
    val widthDp = upDimension(props.width, 300.dp)
    val heightDp = upDimension(props.height, 200.dp)
    val strokeColor = UPColor.parse(props.color, androidx.compose.ui.graphics.Color.Black)
    val strokePx = with(density) { (props.thickness.toString().toFloatOrNull() ?: 3f).dp.toPx() }
    val bg = UPColor.parse(props.bgColor, androidx.compose.ui.graphics.Color.White)

    val strokes = remember { mutableStateListOf<UPSignatureStroke>() }
    var current by remember { mutableStateOf<UPSignatureStroke?>(null) }

    Column(modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("signature")) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp)
                .background(bg)
                .border(0.5.dp, net.lingyun.ultraui.android.core.UPTheme.Border)
                .upTestTag("signature-canvas")
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val s = UPSignatureStroke()
                            s.points.add(offset)
                            current = s
                            strokes.add(s)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            current?.points?.add(change.position)
                        },
                        onDragEnd = { current = null },
                        onDragCancel = { current = null },
                    )
                },
        ) {
            strokes.forEach { stroke ->
                if (stroke.points.size >= 2) {
                    val path = Path().apply {
                        moveTo(stroke.points.first().x, stroke.points.first().y)
                        stroke.points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(path, color = strokeColor, style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
            }
        }

        if (props.showToolbar) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).upTestTag("signature-toolbar"),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                UPButton(
                    UPButtonProps(text = "清空", size = "small"),
                    onClick = { strokes.clear(); current = null; onClear?.invoke() },
                    modifier = Modifier.upTestTag("signature-clear"),
                    diagnostics = diagnostics,
                )
                UPButton(
                    UPButtonProps(text = "确认", type = "primary", size = "small"),
                    onClick = {
                        if (upSignatureIsEmpty(strokes)) onError?.invoke() else onConfirm?.invoke()
                    },
                    modifier = Modifier.upTestTag("signature-confirm"),
                    diagnostics = diagnostics,
                )
            }
        }
    }
}

