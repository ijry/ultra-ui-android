package net.lingyun.ultraui.android.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPImageLoader
import net.lingyun.ultraui.android.core.UPImageLoaders
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upSize
import net.lingyun.ultraui.android.core.upTestTag

private const val ImageComponentName = "UPImage"
private val ImageShapes = setOf("circle", "square")
private val ImageModes = setOf(
    "scaleToFill", "aspectFit", "aspectFill", "widthFix", "heightFix", "top", "bottom", "center", "left", "right",
)

private data class UPImageLoadState(
    val loading: Boolean,
    val image: ImageBitmap? = null,
    val error: Throwable? = null,
)

/** Native, dependency-free counterpart of uview-plus `u-image`. */
@Composable
public fun UPImage(
    props: UPImageProps = UPImageProps(),
    loader: UPImageLoader = UPImageLoaders.Android,
    onClick: (() -> Unit)? = null,
    onLoad: (() -> Unit)? = null,
    onError: ((Throwable?) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val shapeName = upSafeEnum(props.shape, ImageShapes, "square", diagnostics, ImageComponentName, "shape")
    val mode = upSafeEnum(props.mode, ImageModes, "aspectFill", diagnostics, ImageComponentName, "mode")
    val state by produceState(
        initialValue = UPImageLoadState(loading = props.src.isNotEmpty()),
        key1 = props.src,
        key2 = loader,
    ) {
        if (props.src.isBlank()) {
            value = UPImageLoadState(loading = false, error = IllegalArgumentException("empty image source"))
        } else {
            value = UPImageLoadState(loading = true)
            value = runCatching { loader.load(props.src) }
                .fold(
                    onSuccess = { bitmap ->
                        if (bitmap == null) UPImageLoadState(loading = false, error = null)
                        else UPImageLoadState(loading = false, image = bitmap)
                    },
                    onFailure = { throwable -> UPImageLoadState(loading = false, error = throwable) },
                )
        }
    }
    var callbackDispatched by remember(props.src, loader) { mutableStateOf(false) }
    LaunchedEffect(state, props.src) {
        if (state.loading || callbackDispatched) return@LaunchedEffect
        callbackDispatched = true
        when {
            state.image != null -> onLoad?.invoke()
            state.error != null || props.src.isNotEmpty() -> onError?.invoke(state.error)
        }
    }

    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, ImageComponentName)
    val shape = if (shapeName == "circle") RoundedCornerShape(percent = 50) else {
        RoundedCornerShape(upRawDp(props.radius, 0.dp).coerceAtLeast(0.dp))
    }
    val background = net.lingyun.ultraui.android.core.UPColor.parse(props.bgColor, UPTheme.Background)
    val root = modifier
        .upSize(upRawDp(props.width, 300.dp), upRawDp(props.height, 225.dp))
        .clip(shape)
        .background(background, shape)
        .applyUPResolvedStyle(style)
        .upTestTag("image")
        .upClickable(enabled = onClick != null, onClick = { onClick?.invoke() })

    Box(root, contentAlignment = Alignment.Center) {
        val loadedImage = state.image
        when {
            state.loading && props.showLoading -> {
                UPLoadingIcon(
                    props = UPLoadingIconProps(show = true, mode = "spinner", size = 24, color = "#909399"),
                    modifier = Modifier.upTestTag("image-loading"),
                    diagnostics = diagnostics,
                )
            }
            loadedImage != null -> {
                Image(
                    bitmap = loadedImage,
                    contentDescription = null,
                    contentScale = mode.toContentScale(),
                    modifier = Modifier.fillMaxSize(),
                )
            }
            props.showError -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .upTestTag("image-error"),
                    contentAlignment = Alignment.Center,
                ) {
                    UPIcon(
                        props = UPIconProps(name = props.errorIcon, color = "#909399", size = 26),
                        diagnostics = diagnostics,
                    )
                }
            }
        }
    }
}

private fun String.toContentScale(): ContentScale = when (lowercase()) {
    "scaletofill" -> ContentScale.FillBounds
    "aspectfit", "widthfix", "heightfix" -> ContentScale.Fit
    "center" -> ContentScale.None
    "top", "bottom", "left", "right" -> ContentScale.Inside
    else -> ContentScale.Crop
}
