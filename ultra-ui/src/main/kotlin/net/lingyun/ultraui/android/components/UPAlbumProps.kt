package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPAlbum], mirroring uview-plus `u-album`.
 *
 * [urls] is the image list (strings or objects read via [keyName]). A single image renders at
 * [singleSize] with [singleMode]; multiple images render as a [rowCount]-per-row grid of
 * [multipleSize] squares (or wrapping when [autoWrap]) with [space] gaps and [multipleMode]. At most
 * [maxCount] cells show; the overflow count is overlaid on the last cell when [showMore]. [shape]
 * (`circle`/`square`) and [radius] round cells, [unit] is the size unit and [previewFullImage] opens
 * a tap preview.
 */
public data class UPAlbumProps(
    val urls: List<UPRawValue> = emptyList(),
    val keyName: String = "",
    val singleSize: UPRawValue = 180,
    val multipleSize: UPRawValue = 70,
    val space: UPRawValue = 6,
    val singleMode: String = "scaleToFill",
    val multipleMode: String = "aspectFill",
    val maxCount: UPRawValue = 9,
    val previewFullImage: Boolean = true,
    val rowCount: UPRawValue = 3,
    val showMore: Boolean = true,
    val shape: String = "square",
    val radius: UPRawValue = 0,
    val autoWrap: Boolean = false,
    val unit: String = "px",
    val stop: Boolean = true,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
