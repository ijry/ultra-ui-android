package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPResolvedStyle
import net.lingyun.ultraui.android.core.UPStyle
import net.lingyun.ultraui.android.core.UPStyleInput

@Composable
internal fun rememberUPResolvedStyle(
    customStyle: UPStyleInput,
    diagnostics: UPCompatibilityDiagnostics,
    component: String,
): UPResolvedStyle {
    val availableScreenWidth = LocalConfiguration.current.screenWidthDp
        .takeIf { it > 0 }
        ?.dp
        ?: 750.dp
    return remember(customStyle, availableScreenWidth, diagnostics, component) {
        UPStyle.resolve(
            input = customStyle,
            availableScreenWidth = availableScreenWidth,
            diagnostics = diagnostics,
            component = component,
        )
    }
}

/** Applies the platform-neutral customStyle subset to a native Compose node. */
internal fun Modifier.applyUPResolvedStyle(style: UPResolvedStyle): Modifier {
    var resolved = this
    if (style.marginTop != 0.dp || style.marginEnd != 0.dp || style.marginBottom != 0.dp || style.marginStart != 0.dp) {
        resolved = resolved.padding(
            start = style.marginStart,
            top = style.marginTop,
            end = style.marginEnd,
            bottom = style.marginBottom,
        )
    }

    resolved = when {
        style.width != null && style.height != null -> resolved.size(style.width, style.height)
        style.width != null -> resolved.width(style.width)
        style.height != null -> resolved.height(style.height)
        else -> resolved
    }

    val shape = RoundedCornerShape(style.borderRadius ?: 0.dp)
    if (style.backgroundGradient != null) {
        resolved = resolved
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(style.backgroundGradient.start, style.backgroundGradient.end)), shape)
    } else if (style.backgroundColor != null) {
        resolved = resolved
            .clip(shape)
            .background(style.backgroundColor, shape)
    }
    if (style.borderWidth != null && style.borderColor != null) {
        resolved = resolved.border(style.borderWidth, style.borderColor, shape)
    }
    if (style.paddingTop != 0.dp || style.paddingEnd != 0.dp || style.paddingBottom != 0.dp || style.paddingStart != 0.dp) {
        resolved = resolved.padding(
            start = style.paddingStart,
            top = style.paddingTop,
            end = style.paddingEnd,
            bottom = style.paddingBottom,
        )
    }
    if (style.opacity != null) {
        resolved = resolved.alpha(style.opacity.coerceIn(0f, 1f))
    }
    return resolved
}
