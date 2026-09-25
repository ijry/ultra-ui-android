package net.lingyun.ultraui.android.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.floor
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

/**
 * `true` when module (`qrRow`, `qrCol`) sits inside one of the three finder-pattern 3x3 centres,
 * mirroring upstream `getForeGround`: those cells render with `pdground` instead of `foreground`.
 */
internal fun upQrcodeIsFinderCenter(qrRow: Int, qrCol: Int, count: Int): Boolean {
    val topLeft = qrRow in 2..4 && qrCol in 2..4
    val bottomLeft = qrRow > count - 6 && qrRow < count - 2 && qrCol in 2..4
    val topRight = qrRow in 2..4 && qrCol > count - 6 && qrCol < count - 2
    return topLeft || bottomLeft || topRight
}

/**
 * Native Compose counterpart of uview-plus `u-qrcode`.
 *
 * Encodes [`UPQrcodeProps.val`] with [upQrcodeMatrix] (the faithful port of upstream `qrcode.js`)
 * and paints the module grid on a [Canvas]: dark modules use `foreground`, the three finder-pattern
 * centres use `pdground`, and empty modules plus the `quietZone` margin use `background`. An optional
 * centre [icon] overlays via [UPImage]. When `allowPreview` is set, tapping opens an enlarged overlay
 * and invokes [onPreview]; long-press invokes [onLongpressCallback].
 *
 * Downgrade: upstream's `result` event returns an exported image temp-file path produced by the H5/App
 * canvas; bitmap export is left to the host (as with `u-signature`), so no file path is emitted here.
 */
@Composable
public fun UPQrcode(
    props: UPQrcodeProps = UPQrcodeProps(),
    modifier: Modifier = Modifier,
    onPreview: (() -> Unit)? = null,
    onLongpressCallback: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return

    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPQrcode")
    val matrix = remember(props.`val`, props.lv) {
        if (props.`val`.isEmpty()) emptyArray() else upQrcodeMatrix(props.`val`, props.lv)
    }
    val background = UPColor.parse(props.background, Color.White)
    val foreground = UPColor.parse(props.foreground, Color.Black)
    val pdground = if (props.pdground.isEmpty()) foreground else UPColor.parse(props.pdground, foreground)
    var previewShow by remember { mutableStateOf(false) }

    val interaction = Modifier.pointerInput(props.allowPreview) {
        detectTapGestures(
            onTap = {
                if (props.allowPreview) previewShow = true
                onPreview?.invoke()
            },
            onLongPress = { onLongpressCallback?.invoke() },
        )
    }

    if (props.useRootHeightAndWidth) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .applyUPResolvedStyle(style)
                .then(interaction)
                .upTestTag("qrcode"),
            contentAlignment = Alignment.Center,
        ) {
            val side = if (maxWidth < maxHeight) maxWidth else maxHeight
            QrcodeContent(matrix, background, foreground, pdground, props, Modifier.size(side))
        }
    } else {
        val side = upDimension("${props.size}${props.unit}", 200.dp)
        Box(
            modifier = modifier
                .size(side)
                .applyUPResolvedStyle(style)
                .then(interaction)
                .upTestTag("qrcode"),
            contentAlignment = Alignment.Center,
        ) {
            QrcodeContent(matrix, background, foreground, pdground, props, Modifier.fillMaxSize())
        }
    }

    if (props.allowPreview && previewShow) {
        UPOverlay(
            props = UPOverlayProps(show = true),
            onClick = { previewShow = false },
            content = {
                Box(modifier = Modifier.fillMaxSize().upTestTag("qrcode-preview"), contentAlignment = Alignment.Center) {
                    QrcodeContent(matrix, background, foreground, pdground, props, Modifier.size(260.dp))
                }
            },
        )
    }
}

@Composable
private fun QrcodeContent(
    matrix: Array<BooleanArray>,
    background: Color,
    foreground: Color,
    pdground: Color,
    props: UPQrcodeProps,
    modifier: Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = background, topLeft = Offset.Zero, size = size)
            val count = matrix.size
            if (count == 0) return@Canvas
            val quietZone = if (props.quietZone < 0) 0 else props.quietZone
            val renderCount = count + quietZone * 2
            val tile = size.minDimension / renderCount
            for (row in 0 until count) {
                for (col in 0 until count) {
                    if (!matrix[row][col]) continue
                    val color = if (upQrcodeIsFinderCenter(row, col, count)) pdground else foreground
                    val rc = col + quietZone
                    val rr = row + quietZone
                    val x0 = floor(rc * tile)
                    val y0 = floor(rr * tile)
                    val x1 = ceil((rc + 1) * tile)
                    val y1 = ceil((rr + 1) * tile)
                    drawRect(
                        color = color,
                        topLeft = Offset(x0, y0),
                        size = Size(x1 - x0, y1 - y0),
                    )
                }
            }
        }
        if (props.icon.isNotEmpty()) {
            UPImage(
                props = UPImageProps(
                    src = props.icon,
                    width = "${props.iconSize}${props.unit}",
                    height = "${props.iconSize}${props.unit}",
                ),
            )
        }
    }
}
