package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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

        PopupPanel(
            mode = mode,
            shape = shape,
            backgroundColor = backgroundColor,
            minHeight = minHeight,
            maxHeight = maxHeight,
            safeAreaInsetTop = props.safeAreaInsetTop,
            safeAreaInsetBottom = props.safeAreaInsetBottom,
            modifier = style.toPopupModifier(mode, props.pageInline, onClick),
        ) {
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
