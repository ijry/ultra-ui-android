package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPImageLoaders
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val LoadingPageComponentName = "UPLoadingPage"

/** Native Compose counterpart of uview-plus `u-loading-page`. */
@Composable
public fun UPLoadingPage(
    props: UPLoadingPageProps = UPLoadingPageProps(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.loading) return
    val mode = upSafeEnum(
        props.loadingMode,
        setOf("spinner", "circle", "semicircle"),
        "circle",
        diagnostics,
        LoadingPageComponentName,
        "loadingMode",
    )
    val zIndex = props.zIndex.toString().toFloatOrNull() ?: 10f
    val background = if (props.bgColor.isEmpty()) Color.White else UPColor.parse(props.bgColor, Color.White)
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(zIndex)
            .background(background)
            .upTestTag("loading-page"),
        contentAlignment = Alignment.Center,
    ) {
        if (props.image.isNotEmpty()) {
            UPImage(
                props = UPImageProps(src = props.image, width = 120, height = 120),
                loader = UPImageLoaders.Android,
                diagnostics = diagnostics,
            )
        } else {
            UPLoadingIcon(
                props = UPLoadingIconProps(
                    show = true,
                    mode = mode,
                    size = props.iconSize,
                    textSize = props.fontSize,
                    text = props.loadingText,
                    color = props.loadingColor,
                    textColor = props.color,
                    vertical = true,
                ),
                diagnostics = diagnostics,
            )
        }
    }
}
