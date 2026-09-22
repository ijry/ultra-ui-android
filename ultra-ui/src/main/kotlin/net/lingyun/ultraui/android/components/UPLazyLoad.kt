package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/** `time / 1000`s fade when `isEffect`, else no transition (0ms). */
internal fun upLazyLoadFadeMillis(isEffect: Boolean, duration: UPRawValue): Int =
    if (!isEffect) 0 else duration.upIntOrDefault(500).coerceAtLeast(0)

/**
 * Native Compose counterpart of uview-plus `u-lazy-load`.
 *
 * Shows `loadingImg` until the element is in view, then swaps to `image` with an opacity fade
 * (`duration/1000`s, disabled when `isEffect = false`); a failed load shows `errorImg`. `imgMode`,
 * `height` (rpx) and `borderRadius` (rpx) style the image. `click`/`load`/`error` echo `index`.
 *
 * Difference: upstream uses IntersectionObserver + `threshold` to decide when the image enters the
 * viewport; Android has no ambient scroll observer here, so visibility is lifted into the [visible]
 * parameter (default true → load immediately). `threshold` is kept for interface parity but is
 * surfaced as a diagnostic since the host owns scroll detection.
 */
@Composable
public fun UPLazyLoad(
    props: UPLazyLoadProps = UPLazyLoadProps(),
    visible: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
    onLoad: ((UPRawValue) -> Unit)? = null,
    onError: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPLazyLoad")
    var isError by remember(props.image) { mutableStateOf(false) }

    LaunchedEffect(props.threshold) {
        diagnostics.report("UPLazyLoad", "threshold", props.threshold, "IntersectionObserver threshold; scroll detection belongs to the host `visible` flag.")
    }

    // `<view :style="{ opacity }" transition>`: the wrapper fades in when the real image shows.
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(visible) { if (visible) entered = true }
    val opacity by animateFloatAsState(
        targetValue = if (visible && entered) 1f else if (props.isEffect) 0.4f else 1f,
        animationSpec = tween(durationMillis = upLazyLoadFadeMillis(props.isEffect, props.duration)),
        label = "up-lazy-load-fade",
    )

    val currentSrc = when {
        isError -> props.errorImg
        visible -> props.image
        else -> props.loadingImg
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(opacity)
            .applyUPResolvedStyle(style)
            .upTestTag("lazy-load")
            .upClickable(onClick = { onClick?.invoke(props.index) }),
    ) {
        UPImage(
            props = UPImageProps(
                src = currentSrc,
                mode = props.imgMode.ifBlank { "widthFix" },
                width = "100%",
                height = props.height,
                radius = props.borderRadius,
                showLoading = false,
                showError = false,
            ),
            onLoad = { if (visible && !isError) onLoad?.invoke(props.index) },
            onError = { isError = true; onError?.invoke(props.index) },
            diagnostics = diagnostics,
            modifier = Modifier.upTestTag("lazy-load-image"),
        )
    }
}
