package net.lingyun.ultraui.android.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPImageLoader
import net.lingyun.ultraui.android.core.UPImageLoaders
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

/** Resolves the cropper scale clamp; empty values fall back to upstream 0.3 / 4. */
internal fun upCropperScaleBounds(minScale: UPRawValueBoundsInput, maxScale: UPRawValueBoundsInput): Pair<Float, Float> {
    val lo = minScale.value?.toString()?.toFloatOrNull()?.takeIf { it > 0f } ?: 0.3f
    val hi = maxScale.value?.toString()?.toFloatOrNull()?.takeIf { it > 0f } ?: 4f
    return lo to hi
}

/** Wraps a raw value so the pure bounds helper stays JVM-testable without importing core aliases. */
internal data class UPRawValueBoundsInput(val value: Any?)

/** Resolves export quality mirroring upstream `parseInt(quality) || 0.9`. */
internal fun upCropperQuality(quality: Any?): Float {
    val intPart = quality?.toString()?.trim()?.takeWhile { it.isDigit() || it == '-' }?.toIntOrNull()
    return if (intPart == null || intPart == 0) 0.9f else intPart.toFloat()
}

/**
 * Native Compose counterpart of uview-plus `u-cropper`.
 *
 * Renders the interactive crop stage for [src]: the image is panned, zoomed ([canScale], clamped by
 * [minScale]/[maxScale]) and rotated ([canRotate], off in [inner] mode) under a fixed
 * [areaWidth] x [areaHeight] crop frame on a [fillColor] backdrop, with a confirm/cancel toolbar
 * unless [noTab]. Confirm invokes [onConfirm] with the applied transform plus the target export size
 * and [quality]/[index] passthrough so the host can rasterise the crop; [onInit] fires once ready.
 *
 * Downgrade: the actual pixel crop/export to a file URI is a host concern (as with `u-signature`).
 */
@Composable
public fun UPCropper(
    src: String,
    props: UPCropperProps = UPCropperProps(),
    modifier: Modifier = Modifier,
    loader: UPImageLoader = UPImageLoaders.Android,
    onConfirm: ((UPCropperResult) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onInit: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCropper")
    val (minS, maxS) = remember(props.minScale, props.maxScale) {
        upCropperScaleBounds(UPRawValueBoundsInput(props.minScale), UPRawValueBoundsInput(props.maxScale))
    }
    val areaW = upDimension(props.areaWidth, 150.dp)
    val areaH = upDimension(props.areaHeight, 150.dp)
    val exportW = upDimension(props.exportWidth, 130.dp)
    val exportH = upDimension(props.exportHeight, 130.dp)
    val density = androidx.compose.ui.platform.LocalDensity.current
    val fill = UPColor.parse(props.fillColor, Color.Transparent)
    val allowRotate = props.canRotate && !props.inner

    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        if (props.canScale) scale = (scale * zoomChange).coerceIn(minS, maxS)
        if (allowRotate) rotation += rotationChange
        offset += panChange
    }

    val bitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(initialValue = null, src, loader) {
        value = if (src.isEmpty()) null else loader.load(src)
        onInit?.invoke()
    }

    Box(modifier = modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("cropper")) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(areaH + 40.dp)
                .background(if (fill == Color.Transparent) Color(0xFF1A1A1A) else fill)
                .transformable(transformState),
            contentAlignment = Alignment.Center,
        ) {
            val bmp = bitmap
            if (bmp != null) {
                Image(
                    bitmap = bmp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(areaW, areaH)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            rotationZ = rotation
                            translationX = offset.x
                            translationY = offset.y
                        }
                        .upTestTag("cropper-image"),
                )
            }
            // Crop frame overlay.
            Box(
                modifier = Modifier
                    .size(areaW, areaH)
                    .border(1.dp, Color.White)
                    .upTestTag("cropper-frame"),
            )
        }

        if (!props.noTab) {
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                UPButton(props = UPButtonProps(text = "取消", size = "small"), onClick = { onCancel?.invoke() })
                UPButton(
                    props = UPButtonProps(text = "确定", type = "primary", size = "small"),
                    onClick = {
                        onConfirm?.invoke(
                            UPCropperResult(
                                scale = scale,
                                rotation = rotation,
                                offsetX = offset.x,
                                offsetY = offset.y,
                                exportWidth = with(density) { exportW.toPx() },
                                exportHeight = with(density) { exportH.toPx() },
                                quality = upCropperQuality(props.quality),
                                index = props.index,
                            ),
                        )
                    },
                )
            }
        }
    }
}
