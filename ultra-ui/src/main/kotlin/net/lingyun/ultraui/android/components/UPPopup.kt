package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upTestTag

private const val PopupComponentName = "UPPopup"
private val PopupModes = setOf("top", "bottom", "left", "right", "center")
private val PopupClosePositions = setOf("top-left", "top-right", "bottom-left", "bottom-right")

/** Native Compose counterpart of uview-plus `u-popup`. */
@Composable
public fun UPPopup(
    props: UPPopupProps = UPPopupProps(),
    modifier: Modifier = Modifier,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onOpen: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return

    val mode = upSafeEnum(props.mode, PopupModes, "bottom", diagnostics, PopupComponentName, "mode")
    val closeIconPos = upSafeEnum(
        props.closeIconPos,
        PopupClosePositions,
        "top-right",
        diagnostics,
        PopupComponentName,
        "closeIconPos",
    )
    val zIndex = props.zIndex.asFiniteFloatOrNull() ?: 10075f
    LaunchedEffect(props.zIndex, diagnostics) {
        if (props.zIndex.asFiniteFloatOrNull() == null) {
            diagnostics.report(PopupComponentName, "zIndex", props.zIndex, "Malformed zIndex; using 10075.")
        }
    }
    LaunchedEffect(Unit) { onOpen?.invoke() }

    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, PopupComponentName)
    val overlayStyle = rememberUPResolvedStyle(props.overlayStyle, diagnostics, "$PopupComponentName.overlayStyle")
    val overlayOpacity = props.overlayOpacity.asFiniteFloatOrNull()?.coerceIn(0f, 1f) ?: 0.5f
    val backgroundColor = UPColor.parse(props.bgColor, Color.White)
    val round = upRawDp(props.round, 0.dp).coerceAtLeast(0.dp)
    val minHeight = upRawDp(props.minHeight, 0.dp).coerceAtLeast(0.dp)
    val maxHeight = upRawDp(props.maxHeight, DpUnspecifiedFallback).takeIf { it != DpUnspecifiedFallback }
    val shape = popupShape(mode, round)

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(zIndex)
            .upTestTag("popup"),
    ) {
        if (props.overlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayOpacity))
                    .applyUPResolvedStyle(overlayStyle)
                    .upTestTag("popup-overlay")
                    .upClickable(enabled = true) {
                        if (props.closeOnClickOverlay) {
                            onUpdateShow?.invoke(false)
                            onClose?.invoke()
                        }
                    },
            )
        }

        // `<u-transition :mode="pageInline ? 'none' : position" :duration>`. `position()`
        // only consults `zoom` in centre mode; every other mode slides in from its own
        // edge. The `entered` flag has to start false or `animateFloatAsState` would
        // initialise at the end state and `duration` would never interpolate.
        val transition = upPopupTransitionMode(mode, props.zoom, props.pageInline)
        val transitionSpec = tween<Float>(durationMillis = upPopupTransitionDuration(props.duration))
        var entered by remember { mutableStateOf(false) }
        LaunchedEffect(transition) { entered = true }
        val progress by animateFloatAsState(
            targetValue = if (entered) 1f else 0f,
            animationSpec = transitionSpec,
            label = "up-popup-transition",
        )
        val (offsetXFraction, offsetYFraction) = upPopupTransitionOffsetFraction(transition)
        // `touchable` adds the grab bar that resizes the bottom sheet and can fling it shut.
        val dragEnabled = upPopupDragEnabled(props.touchable, mode)
        var dragHeight by remember { mutableStateOf<Dp?>(null) }

        PopupPanel(
            mode = mode,
            shape = shape,
            backgroundColor = backgroundColor,
            minHeight = minHeight,
            maxHeight = maxHeight,
            dragHeight = dragHeight,
            safeAreaInsetTop = props.safeAreaInsetTop,
            safeAreaInsetBottom = props.safeAreaInsetBottom,
            modifier = Modifier
                .graphicsLayer {
                    if (upPopupTransitionFades(transition)) alpha = progress
                    if (transition == "fade-zoom") {
                        val zoomScale = UPPopupZoomScale + (1f - UPPopupZoomScale) * progress
                        scaleX = zoomScale
                        scaleY = zoomScale
                    }
                    translationX = offsetXFraction * (1f - progress) * size.width
                    translationY = offsetYFraction * (1f - progress) * size.height
                }
                .then(style.toPopupModifier(mode, props.pageInline, onClick)),
        ) {
            if (props.safeAreaInsetTop) {
                UPStatusBar(diagnostics = diagnostics)
            }
            if (dragEnabled) {
                PopupDragHandle(
                    minHeight = minHeight,
                    maxHeight = maxHeight,
                    currentHeight = dragHeight,
                    onHeightChange = { dragHeight = it },
                    onDismiss = {
                        onUpdateShow?.invoke(false)
                        onClose?.invoke()
                    },
                )
            }
            if (props.closeable) {
                PopupCloseButton(closeIconPos, onUpdateShow, onClose)
            }
            content()
        }
    }
}

@Composable
private fun BoxScope.PopupPanel(
    mode: String,
    shape: RoundedCornerShape,
    backgroundColor: Color,
    minHeight: androidx.compose.ui.unit.Dp,
    maxHeight: androidx.compose.ui.unit.Dp?,
    dragHeight: androidx.compose.ui.unit.Dp?,
    safeAreaInsetTop: Boolean,
    safeAreaInsetBottom: Boolean,
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val alignment = when (mode) {
        "top" -> Alignment.TopCenter
        "left" -> Alignment.CenterStart
        "right" -> Alignment.CenterEnd
        "center" -> Alignment.Center
        else -> Alignment.BottomCenter
    }
    val panelModifier = when (mode) {
        "left", "right" -> modifier
            .fillMaxHeight()
            .widthIn(min = minHeight)
        "center" -> modifier
        else -> modifier
            .fillMaxWidth()
            // A drag sets an explicit height; without one the sheet keeps sizing itself
            // between `minHeight` and `maxHeight`.
            .then(if (dragHeight != null) Modifier.height(dragHeight) else Modifier)
            .heightIn(min = minHeight, max = maxHeight ?: androidx.compose.ui.unit.Dp.Infinity)
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = alignment,
    ) {
        Column(
            modifier = panelModifier
                .background(backgroundColor, shape)
                .border(0.dp, Color.Transparent, shape)
                .padding(
                    top = if (safeAreaInsetTop) 24.dp else 0.dp,
                    bottom = if (safeAreaInsetBottom) 24.dp else 0.dp,
                )
                .upTestTag("popup-panel"),
            verticalArrangement = Arrangement.Top,
        ) {
            content()
        }
    }
}

/**
 * `.u-popup__content__touch-area` with its 100x5 indicator. Dragging resizes the sheet
 * between `minHeight` and `maxHeight`; a long or fast drag downwards closes it.
 */
@Composable
private fun PopupDragHandle(
    minHeight: androidx.compose.ui.unit.Dp,
    maxHeight: androidx.compose.ui.unit.Dp?,
    currentHeight: androidx.compose.ui.unit.Dp?,
    onHeightChange: (androidx.compose.ui.unit.Dp?) -> Unit,
    onDismiss: () -> Unit,
) {
    val density = LocalDensity.current
    val windowHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val minHeightPx = upPopupDragMinHeightPx(with(density) { minHeight.toPx() })
    val maxHeightPx = upPopupDragMaxHeightPx(maxHeight?.let { with(density) { it.toPx() } }, windowHeightPx)
    var panelHeightPx by remember { mutableFloatStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .upTestTag("popup-touch-area")
            .pointerInput(minHeightPx, maxHeightPx) {
                var startHeightPx = 0f
                var startedAt = 0L
                var travelled = 0f
                detectVerticalDragGestures(
                    onDragStart = {
                        startHeightPx = currentHeight?.let { with(density) { it.toPx() } } ?: panelHeightPx
                        startedAt = System.nanoTime()
                        travelled = 0f
                    },
                    onDragEnd = {
                        val elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L
                        if (upPopupShouldCloseAfterDrag(travelled, elapsedMillis)) onDismiss()
                    },
                    onVerticalDrag = { _, dragAmount ->
                        travelled += dragAmount
                        upPopupDragHeightOrNull(startHeightPx, travelled, minHeightPx, maxHeightPx)?.let { next ->
                            onHeightChange(with(density) { next.toDp() })
                        }
                    },
                )
            }
            .onSizeChanged { size -> if (panelHeightPx == 0f) panelHeightPx = size.height.toFloat() },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(5.dp)
                .background(UPTheme.Light, RoundedCornerShape(100.dp))
                .upTestTag("popup-indicator"),
        )
    }
}

@Composable
private fun PopupCloseButton(
    position: String,
    onUpdateShow: ((Boolean) -> Unit)?,
    onClose: (() -> Unit)?,
) {
    val alignment = when (position) {
        "top-left" -> Alignment.TopStart
        "bottom-left" -> Alignment.BottomStart
        "bottom-right" -> Alignment.BottomEnd
        else -> Alignment.TopEnd
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = alignment,
    ) {
        Box(
            modifier = Modifier
                .upTestTag("popup-close")
                .upClickable(enabled = true) {
                    onUpdateShow?.invoke(false)
                    onClose?.invoke()
                },
        ) {
            UPIcon(UPIconProps(name = "close", size = 20, color = UPTheme.Tips.toHexString()))
        }
    }
}

/**
 * Trailing-lambda overload for generated Compose call sites. The diagnostics-aware
 * overload keeps diagnostics as the final optional argument for source
 * compatibility, while this overload preserves the idiomatic `UPPopup(props) {}`
 * form used by sample and test code.
 */
@Composable
public fun UPPopup(
    props: UPPopupProps = UPPopupProps(),
    modifier: Modifier = Modifier,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onOpen: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    UPPopup(
        props = props,
        modifier = modifier,
        onUpdateShow = onUpdateShow,
        onOpen = onOpen,
        onClose = onClose,
        onClick = onClick,
        content = content,
        diagnostics = UPCompatibilityDiagnostics.None,
    )
}

private fun popupShape(mode: String, radius: androidx.compose.ui.unit.Dp): RoundedCornerShape = when {
    radius <= 0.dp -> RoundedCornerShape(0.dp)
    mode == "top" -> RoundedCornerShape(bottomStart = radius, bottomEnd = radius)
    mode == "bottom" -> RoundedCornerShape(topStart = radius, topEnd = radius)
    mode == "left" -> RoundedCornerShape(topEnd = radius, bottomEnd = radius)
    mode == "right" -> RoundedCornerShape(topStart = radius, bottomStart = radius)
    else -> RoundedCornerShape(radius)
}

private const val DpUnspecifiedFallbackValue = -1f
private val DpUnspecifiedFallback = androidx.compose.ui.unit.Dp(DpUnspecifiedFallbackValue)

private fun net.lingyun.ultraui.android.core.UPResolvedStyle.toPopupModifier(
    mode: String,
    pageInline: Boolean,
    onClick: (() -> Unit)?,
): Modifier {
    var result = Modifier.applyUPResolvedStyle(this)
    if (pageInline) result = result.padding(0.dp)
    if (onClick != null) result = result.upClickable(onClick = onClick)
    return result
}

/** Convenience overload for a raw generated mode/show pair. */
@Composable
public fun UPPopup(
    show: Boolean,
    mode: String = "bottom",
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    UPPopup(
        props = UPPopupProps(show = show, mode = mode),
        onUpdateShow = onUpdateShow,
        onClose = onClose,
        content = content,
    )
}

private fun Color.toHexString(): String = "#%08X".format(toArgb())
