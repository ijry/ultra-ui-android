package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

/** True when a CSS background string is a linear/radial gradient (matching upstream detection). */
internal fun upPosterIsGradient(background: String): Boolean =
    background.contains("linear-gradient") || background.contains("radial-gradient")

/** Extracts the ordered colour stops (hex or rgb/rgba) from a CSS background string. */
internal fun upPosterExtractColors(background: String): List<String> =
    Regex("#[0-9a-fA-F]+|rgba?\\([^)]+\\)").findAll(background).map { it.value }.toList()

/** Resolves a poster text `fontSize` string (rpx halved, px/number as-is) to sp; defaults to 14sp. */
internal fun upPosterFontSizeSp(fontSize: String): androidx.compose.ui.unit.TextUnit {
    val trimmed = fontSize.trim()
    val number = Regex("[-+]?[0-9]*\\.?[0-9]+").find(trimmed)?.value?.toFloatOrNull() ?: return 14.sp
    return if (trimmed.endsWith("rpx")) (number / 2f).sp else number.sp
}

/**
 * Native Compose counterpart of uview-plus `u-poster`.
 *
 * Upstream is a headless exporter that paints [`UPPosterProps.json`] onto a hidden canvas and returns
 * a temp-file path via `exportImage()`. The Android port instead renders the same description as a
 * visible, WYSIWYG composition: the container uses `json.css` (`width`/`height`/`background` incl.
 * gradients/`radius`) and each `json.views` element is absolutely positioned by its `css`
 * (`left`/`top`/`width`/`height`) — `view` draws a (rounded) coloured box, `text` draws styled text,
 * `image` renders via [UPImage] and `qrcode` via [UPQrcode].
 *
 * Downgrade: `exportImage()` (rasterise to a temp file) is a host concern (as with `u-signature`);
 * the port renders the poster rather than exporting a bitmap.
 */
@Composable
public fun UPPoster(
    props: UPPosterProps = UPPosterProps(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPPoster")
    val css = remember(props.json) { props.json["css"].upStringKeyMapOrEmpty() }
    val views = remember(props.json) { props.json["views"].upItemsOrEmpty() }

    val width = upDimension(css["width"] ?: "750rpx", 375.dp)
    val height = upDimension(css["height"] ?: "1114rpx", 557.dp)
    val radius = css["radius"]?.let { upDimension(it, 0.dp) } ?: 0.dp
    val bg = css["background"].upStringValueOrEmpty()

    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(radius))
            .then(backgroundModifier(bg))
            .applyUPResolvedStyle(style)
            .upTestTag("poster"),
    ) {
        views.forEachIndexed { index, rawView ->
            UPPosterView(rawView.upStringKeyMapOrEmpty(), index)
        }
    }
}

@Composable
private fun UPPosterView(view: Map<String, UPRawValue>, index: Int) {
    val type = view["type"].upStringValueOrEmpty().ifEmpty { "view" }
    val css = view["css"].upStringKeyMapOrEmpty()
    val left = upDimension(css["left"] ?: "0rpx", 0.dp)
    val top = upDimension(css["top"] ?: "0rpx", 0.dp)
    val width = upDimension(css["width"] ?: "0rpx", 0.dp)
    val height = upDimension(css["height"] ?: "0rpx", 0.dp)
    val radius = css["radius"]?.let { upDimension(it, 0.dp) } ?: 0.dp

    var box = Modifier
        .offset(x = left, y = top)
        .size(width, height)
        .clip(RoundedCornerShape(radius))
        .upTestTag("poster-view-$index")

    when (type) {
        "view" -> {
            val bg = css["background"].upStringValueOrEmpty()
            Box(modifier = box.then(backgroundModifier(bg)))
        }
        "text" -> {
            val align = when (css["textAlign"].upStringValueOrEmpty()) {
                "center" -> TextAlign.Center
                "right" -> TextAlign.Right
                else -> TextAlign.Left
            }
            val weight = if (css["fontWeight"].upStringValueOrEmpty() == "bold") FontWeight.Bold else FontWeight.Normal
            Box(modifier = Modifier.offset(x = left, y = top).upTestTag("poster-view-$index")) {
                BasicText(
                    text = view["text"].upStringValueOrEmpty(),
                    style = TextStyle(
                        color = UPColor.parse(css["color"] ?: "#000000", Color.Black),
                        fontSize = upPosterFontSizeSp(css["fontSize"].upStringValueOrEmpty()),
                        fontWeight = weight,
                        textAlign = align,
                    ),
                )
            }
        }
        "image" -> Box(modifier = box) {
            UPImage(props = UPImageProps(src = view["src"].upStringValueOrEmpty(), width = css["width"] ?: "0rpx", height = css["height"] ?: "0rpx", radius = css["radius"] ?: "0"))
        }
        "qrcode" -> Box(modifier = Modifier.offset(x = left, y = top).size(width, height).upTestTag("poster-view-$index"), contentAlignment = Alignment.Center) {
            val value = view["text"].upStringValueOrEmpty()
            if (value.isNotEmpty()) {
                UPQrcode(props = UPQrcodeProps(`val` = value, size = 200, useRootHeightAndWidth = true))
            } else {
                Box(modifier = Modifier.size(width, height).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                    BasicText(text = "QR", style = TextStyle(color = Color(0xFF999999)))
                }
            }
        }
    }
}

private fun backgroundModifier(background: String): Modifier {
    if (background.isEmpty()) return Modifier
    if (upPosterIsGradient(background)) {
        val colors = upPosterExtractColors(background).map { UPColor.parse(it, Color.Transparent) }
        if (colors.size >= 2) return Modifier.background(Brush.linearGradient(colors))
        if (colors.size == 1) return Modifier.background(colors[0])
        return Modifier
    }
    val color = UPColor.parseOrNull(background) ?: return Modifier
    return Modifier.background(color)
}
