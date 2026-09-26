package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

/** The display source for an upload item: prefer `thumb`, then `url`. */
internal fun upUploadItemSource(item: Map<String, UPRawValue>): String =
    item["thumb"].upStringValueOrEmpty().ifEmpty { item["url"].upStringValueOrEmpty() }

/**
 * Native Compose counterpart of uview-plus `u-upload` (visible UI).
 *
 * Renders the [`UPUploadProps.fileList`] thumbnail grid and an add button: each thumbnail sizes to
 * [width] x [height] with [imageMode], shows a delete badge when [deletable], an "uploading"/"failed"
 * status overlay, and (when [previewFullImage]) opens a full-image overlay on tap. The add button
 * (using [uploadIcon]/[uploadIconColor]/[uploadText]) shows until [maxCount] is reached and invokes
 * [onAddClick]; it is greyed and inert when [disabled]. [onDelete] carries the tapped item index.
 *
 * The native picker and upload engine are host concerns: opening the picker happens in [onAddClick]
 * and the accept/capture/size/auto-upload props are inert (see docs).
 */
@Composable
public fun UPUpload(
    props: UPUploadProps = UPUploadProps(),
    modifier: Modifier = Modifier,
    onAddClick: (() -> Unit)? = null,
    onDelete: ((Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPUpload")
    val cellW = upDimension(props.width, 80.dp)
    val cellH = upDimension(props.height, 80.dp)
    val maxCount = props.maxCount.upIntOrDefault(52)
    var previewSrc by remember { mutableStateOf<String?>(null) }

    FlowRow(
        modifier = modifier
            .applyUPResolvedStyle(style)
            .upTestTag("upload"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (props.previewImage) {
            props.fileList.forEachIndexed { index, raw ->
                val item = raw.upStringKeyMapOrEmpty()
                val src = upUploadItemSource(item)
                val status = item["status"].upStringValueOrEmpty()
                Box(
                    modifier = Modifier
                        .size(cellW, cellH)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF4F5F7))
                        .pointerInput(src, props.previewFullImage) {
                            detectTapGestures { if (props.previewFullImage && src.isNotEmpty()) previewSrc = src }
                        }
                        .upTestTag("upload-item-$index"),
                ) {
                    if (src.isNotEmpty()) {
                        UPImage(props = UPImageProps(src = src, width = props.width, height = props.height, mode = props.imageMode))
                    }
                    if (status == "uploading") {
                        Box(Modifier.fillMaxSize().background(Color(0x99000000)), contentAlignment = Alignment.Center) {
                            BasicText(text = "上传中", style = TextStyle(color = Color.White, fontSize = 12.sp))
                        }
                    } else if (status == "failed" || status == "error") {
                        Box(Modifier.fillMaxSize().background(Color(0x99000000)), contentAlignment = Alignment.Center) {
                            BasicText(
                                text = item["message"].upStringValueOrEmpty().ifEmpty { "上传失败" },
                                style = TextStyle(color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center),
                            )
                        }
                    }
                    if (props.deletable) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(18.dp)
                                .background(Color(0xB3303133), RoundedCornerShape(bottomStart = 9.dp))
                                .upClickable(onClick = { onDelete?.invoke(index) })
                                .upTestTag("upload-delete-$index"),
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicText(text = "\u00D7", style = TextStyle(color = Color.White, fontSize = 12.sp))
                        }
                    }
                }
            }
        }

        if (props.fileList.size < maxCount) {
            Box(
                modifier = Modifier
                    .size(cellW, cellH)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF4F5F7))
                    .border(1.dp, UPTheme.Border, RoundedCornerShape(4.dp))
                    .alpha(if (props.disabled) 0.5f else 1f)
                    .upClickable(enabled = !props.disabled, onClick = { onAddClick?.invoke() })
                    .upTestTag("upload-add"),
                contentAlignment = Alignment.Center,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    UPIcon(
                        props = UPIconProps(name = props.uploadIcon, color = props.uploadIconColor, size = "26px"),
                    )
                    if (props.uploadText.isNotEmpty()) {
                        BasicText(
                            text = props.uploadText,
                            style = TextStyle(color = UPColor.parse(props.uploadIconColor, UPTheme.Tips), fontSize = 11.sp),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp),
                        )
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
                Box(Modifier.fillMaxSize().upTestTag("upload-preview"), contentAlignment = Alignment.Center) {
                    UPImage(props = UPImageProps(src = preview, width = "300px", mode = "widthFix"))
                }
            },
        )
    }
}
