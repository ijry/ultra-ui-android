package net.lingyun.ultraui.android.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.upTestTag

/** Canvas geometry for a barcode, mirroring upstream `calculateCanvasSize`. */
internal data class UPBarcodeLayout(val canvasWidth: Int, val canvasHeight: Int, val textHeight: Int)

/**
 * Reproduces upstream `calculateCanvasSize`: adds margins and (for top/bottom text) the text band,
 * then floors width at 100 and height at `60 + textHeight`.
 */
internal fun upBarcodeLayout(
    width: Int,
    height: Int,
    displayValue: Boolean,
    fontSize: Int,
    textMargin: Int,
    textPosition: String,
    marginLeft: Int,
    marginRight: Int,
    marginTop: Int,
    marginBottom: Int,
): UPBarcodeLayout {
    val textHeight = if (displayValue) fontSize + textMargin else 0
    var h = height
    if (textPosition == "top" || textPosition == "bottom") h += textHeight
    val w = width + marginLeft + marginRight
    h += marginTop + marginBottom
    return UPBarcodeLayout(max(w, 100), max(h, 60 + textHeight), textHeight)
}

/**
 * Native Compose counterpart of uview-plus `u-barcode`.
 *
 * Encodes [`UPBarcodeProps.value`] with [upBarcodeEncode] and paints the module string on a [Canvas]
 * sized by [upBarcodeLayout]; each '1' becomes a `lineColor` bar `moduleWidth` wide over the full bar
 * height, on a [background] fill. The human-readable [text] (defaulting to `value`) renders per
 * `textPosition`/`textAlign`/`font`/`fontSize` when `displayValue` is set. On an encode error the
 * component shows the upstream error message on a grey panel and invokes [onError].
 *
 * Downgrade: `useCanvas=false` (image-file export) and `fontOptions` are inert (see docs).
 */
@Composable
public fun UPBarcode(
    props: UPBarcodeProps = UPBarcodeProps(),
    modifier: Modifier = Modifier,
    onError: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPBarcode")
    val marginLeft = props.marginLeft ?: props.margin
    val marginRight = props.marginRight ?: props.margin
    val marginTop = props.marginTop ?: props.margin
    val marginBottom = props.marginBottom ?: props.margin
    val layout = remember(props) {
        upBarcodeLayout(
            props.width, props.height, props.displayValue, props.fontSize, props.textMargin,
            props.textPosition, marginLeft, marginRight, marginTop, marginBottom,
        )
    }
    val encoded = remember(props.value, props.format) {
        runCatching { upBarcodeEncode(props.value, props.format) }
    }
    val error = encoded.exceptionOrNull()
    if (error != null) {
        onError?.invoke(error.message ?: "生成条码失败")
    }
    val background = UPColor.parse(props.background, Color.White)
    val lineColor = UPColor.parse(props.lineColor, Color.Black)

    Box(
        modifier = modifier
            .size(layout.canvasWidth.dp, layout.canvasHeight.dp)
            .background(background)
            .applyUPResolvedStyle(style)
            .upTestTag("barcode"),
        contentAlignment = Alignment.Center,
    ) {
        val bits = encoded.getOrNull()
        if (bits == null) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                BasicText(
                    text = "生成条码失败",
                    style = TextStyle(color = Color(0xFFFF0000), fontSize = 14.sp),
                    modifier = Modifier.upTestTag("barcode-error"),
                )
            }
            return@Box
        }

        val barHeightPx = props.height
        val textPosition = props.textPosition
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleX = size.width / layout.canvasWidth
            val scaleY = size.height / layout.canvasHeight
            var barcodeY = marginTop.toFloat()
            if (props.displayValue && textPosition == "top") barcodeY += layout.textHeight
            val moduleWidth = max(1f, (layout.canvasWidth - marginLeft - marginRight).toFloat() / bits.length)
            var x = marginLeft.toFloat()
            for (ch in bits) {
                if (ch == '1') {
                    drawRect(
                        color = lineColor,
                        topLeft = Offset(x * scaleX, barcodeY * scaleY),
                        size = Size(moduleWidth * scaleX, barHeightPx * scaleY),
                    )
                }
                x += moduleWidth
            }
        }

        if (props.displayValue) {
            val label = props.text ?: props.value
            val align = when (props.textAlign) {
                "left" -> TextAlign.Left
                "right" -> TextAlign.Right
                else -> TextAlign.Center
            }
            val boxAlign = when (props.textPosition) {
                "top" -> when (props.textAlign) {
                    "left" -> Alignment.TopStart
                    "right" -> Alignment.TopEnd
                    else -> Alignment.TopCenter
                }
                else -> when (props.textAlign) {
                    "left" -> Alignment.BottomStart
                    "right" -> Alignment.BottomEnd
                    else -> Alignment.BottomCenter
                }
            }
            BasicText(
                text = label,
                style = TextStyle(
                    color = lineColor,
                    fontSize = props.fontSize.sp,
                    fontFamily = if (props.font == "monospace") FontFamily.Monospace else FontFamily.Default,
                    textAlign = align,
                ),
                modifier = Modifier.align(boxAlign).upTestTag("barcode-text"),
            )
        }
    }
}
