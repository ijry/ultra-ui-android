package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

/** Reads an album entry's image URL: a plain string, `item[keyName]`, or a common `url`/`src` key. */
internal fun upAlbumSrc(item: UPRawValue, keyName: String): String = when (item) {
    is String -> item
    is Map<*, *> -> {
        val map = item.upStringKeyMapOrEmpty()
        val byKey = if (keyName.isNotEmpty()) map[keyName].upStringValueOrEmpty() else ""
        byKey.ifEmpty { map["url"].upStringValueOrEmpty().ifEmpty { map["src"].upStringValueOrEmpty() } }
    }
    else -> item.upStringValueOrEmpty()
}

/**
 * Native Compose counterpart of uview-plus `u-album`.
 *
 * Renders [`UPAlbumProps.urls`]: a single image at [singleSize]/[singleMode], otherwise a grid of
 * [multipleSize] squares [rowCount] per row (or wrapping by width when [autoWrap]) spaced by [space]
 * with [multipleMode]. Cells are clipped by [shape]/[radius]. At most [maxCount] cells show and, when
 * [showMore], the overflow count is overlaid on the last cell. Tapping a cell invokes [onClick] and,
 * when [previewFullImage], opens a full-image overlay.
 */
@Composable
public fun UPAlbum(
    props: UPAlbumProps = UPAlbumProps(),
    modifier: Modifier = Modifier,
    onClick: ((Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPAlbum")
    val srcs = remember(props.urls, props.keyName) { props.urls.map { upAlbumSrc(it, props.keyName) } }
    val maxCount = props.maxCount.upIntOrDefault(9).coerceAtLeast(1)
    val rowCount = props.rowCount.upIntOrDefault(3).coerceAtLeast(1)
    val space = upDimension(props.space, 6.dp)
    var previewSrc by remember { mutableStateOf<String?>(null) }
    val cellShape = if (props.shape == "circle") CircleShape else RoundedCornerShape(upDimension(props.radius, 0.dp))

    Box(modifier = modifier.applyUPResolvedStyle(style).upTestTag("album")) {
        if (srcs.isEmpty()) {
            return@Box
        }
        if (srcs.size == 1) {
            val side = upDimension("${props.singleSize}${props.unit}", 180.dp)
            Box(
                modifier = Modifier
                    .size(side)
                    .clip(cellShape)
                    .pointerInput(srcs[0], props.previewFullImage) {
                        detectTapGestures {
                            onClick?.invoke(0)
                            if (props.previewFullImage) previewSrc = srcs[0]
                        }
                    }
                    .upTestTag("album-item-0"),
            ) {
                UPImage(props = UPImageProps(src = srcs[0], width = "${props.singleSize}${props.unit}", mode = props.singleMode))
            }
        } else {
            val cell = upDimension("${props.multipleSize}${props.unit}", 70.dp)
            val visible = srcs.take(maxCount)
            val overflow = srcs.size - maxCount

            @Composable
            fun Cell(index: Int) {
                val src = visible[index]
                Box(
                    modifier = Modifier
                        .size(cell)
                        .clip(cellShape)
                        .pointerInput(src, props.previewFullImage) {
                            detectTapGestures {
                                onClick?.invoke(index)
                                if (props.previewFullImage) previewSrc = src
                            }
                        }
                        .upTestTag("album-item-$index"),
                ) {
                    UPImage(props = UPImageProps(src = src, width = "${props.multipleSize}${props.unit}", height = "${props.multipleSize}${props.unit}", mode = props.multipleMode))
                    if (index == visible.lastIndex && overflow > 0 && props.showMore) {
                        Box(Modifier.fillMaxSize().background(Color(0x99000000)), contentAlignment = Alignment.Center) {
                            BasicText(text = "+$overflow", style = TextStyle(color = Color.White, fontSize = 18.sp))
                        }
                    }
                }
            }

            if (props.autoWrap) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(space),
                    verticalArrangement = Arrangement.spacedBy(space),
                ) {
                    visible.indices.forEach { Cell(it) }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(space)) {
                    visible.indices.chunked(rowCount).forEach { rowIndices ->
                        Row(horizontalArrangement = Arrangement.spacedBy(space)) {
                            rowIndices.forEach { Cell(it) }
                        }
                    }
                }
            }
        }
    }

    val preview = previewSrc
    if (props.previewFullImage && preview != null) {
        UPOverlay(
            props = UPOverlayProps(show = true),
            onClick = { previewSrc = null },
            content = {
                Box(Modifier.fillMaxSize().upTestTag("album-preview"), contentAlignment = Alignment.Center) {
                    UPImage(props = UPImageProps(src = preview, width = "320px", mode = "widthFix"))
                }
            },
        )
    }
}
