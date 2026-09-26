package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPCropper], mirroring uview-plus `u-cropper`.
 *
 * The port renders the interactive crop stage: the image is panned/zoomed ([canScale], clamped by
 * [minScale]/[maxScale]) and rotated ([canRotate], disabled in [inner] mode) under a fixed
 * [areaWidth] x [areaHeight] crop frame on a [fillColor] backdrop; [noTab] toggles the confirm/cancel
 * toolbar. Confirm reports the transform plus [exportWidth]/[exportHeight]/[quality]/[index] so the
 * host can rasterise the crop.
 *
 * Inert (see docs): [lockWidth], [lockHeight], [stretch], [lock] and [canChangeSize] (crop-frame
 * locking/resizing) are not modelled.
 */
public data class UPCropperProps(
    val minScale: UPRawValue = "",
    val maxScale: UPRawValue = "",
    val canScale: Boolean = true,
    val canRotate: Boolean = true,
    val lockWidth: UPRawValue = "",
    val lockHeight: UPRawValue = "",
    val stretch: UPRawValue = "",
    val lock: UPRawValue = "",
    val noTab: Boolean = true,
    val inner: Boolean = false,
    val quality: UPRawValue = "",
    val index: UPRawValue = "",
    val canChangeSize: Boolean = false,
    val areaWidth: UPRawValue = "300rpx",
    val areaHeight: UPRawValue = "300rpx",
    val exportWidth: UPRawValue = "260rpx",
    val exportHeight: UPRawValue = "260rpx",
    val fillColor: String = "transparent",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

/**
 * Result reported by [UPCropper] on confirm: the applied [scale]/[rotation] (degrees) and pan
 * ([offsetX]/[offsetY] px), the target [exportWidth]/[exportHeight] px, plus the passthrough
 * [quality] and [index]. The host uses these to rasterise the cropped image.
 */
public data class UPCropperResult(
    val scale: Float,
    val rotation: Float,
    val offsetX: Float,
    val offsetY: Float,
    val exportWidth: Float,
    val exportHeight: Float,
    val quality: Float,
    val index: UPRawValue,
)
