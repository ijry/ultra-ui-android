package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPUpload], mirroring uview-plus `u-upload`.
 *
 * The port renders the visible upload UI: the [fileList] thumbnail grid ([width] x [height],
 * [imageMode], [deletable] delete badges, [previewImage] toggle, [previewFullImage] tap-to-preview)
 * plus the add button ([uploadIcon]/[uploadIconColor]/[uploadText]) shown until [maxCount] is
 * reached and hidden/greyed when [disabled]. [name] tags callbacks.
 *
 * The native media/file picker and the upload engine are host concerns and inert here: [accept],
 * [extension], [capture], [compressed], [camera], [maxDuration], [sizeType], [multiple], [maxSize],
 * [useBeforeRead], [autoDelete], [autoUpload], [autoUploadApi], [autoUploadDriver],
 * [autoUploadAuthUrl], [autoUploadHeader], [customAfterAutoUpload], [getVideoThumb] and
 * [videoPreviewObjectFit]; the `afterRead`/`beforeRead` function hooks are not modelled. See docs.
 */
public data class UPUploadProps(
    val accept: String = "image",
    val extension: List<UPRawValue> = emptyList(),
    val capture: List<UPRawValue> = listOf("album", "camera"),
    val compressed: Boolean = true,
    val camera: String = "back",
    val maxDuration: Int = 60,
    val uploadIcon: String = "camera-fill",
    val uploadIconColor: String = "#D3D4D6",
    val useBeforeRead: Boolean = false,
    val previewFullImage: Boolean = true,
    val maxCount: UPRawValue = 52,
    val disabled: Boolean = false,
    val imageMode: String = "aspectFill",
    val name: String = "",
    val sizeType: List<UPRawValue> = listOf("original", "compressed"),
    val multiple: Boolean = false,
    val deletable: Boolean = true,
    val maxSize: UPRawValue = Long.MAX_VALUE,
    val fileList: List<UPRawValue> = emptyList(),
    val uploadText: String = "",
    val width: UPRawValue = 80,
    val height: UPRawValue = 80,
    val previewImage: Boolean = true,
    val autoDelete: Boolean = false,
    val autoUpload: Boolean = false,
    val autoUploadApi: String = "",
    val autoUploadDriver: String = "",
    val autoUploadAuthUrl: String = "",
    val autoUploadHeader: Map<String, UPRawValue> = emptyMap(),
    val getVideoThumb: Boolean = false,
    val customAfterAutoUpload: Boolean = false,
    val videoPreviewObjectFit: String = "cover",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
