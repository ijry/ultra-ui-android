package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag
import kotlinx.coroutines.launch

private const val BackTopComponentName: String = "UPBackTop"
private val BackTopModes: Set<String> = setOf("circle", "square")

/** Native Compose counterpart of uview-plus `u-back-top`. */
@Composable
public fun UPBackTop(
    props: UPBackTopProps = UPBackTopProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onBackToTop: (() -> Unit)? = null,
    onScrollToTop: ((Int) -> Unit)? = null,
    /**
     * The scroll container this button belongs to. Supplying it lets the component read the
     * offset and perform the scroll itself, which is what `uni.pageScrollTo` does upstream;
     * leaving it null keeps the `scrollTop` + [onScrollToTop] arrangement for callers whose
     * container is not a Compose `ScrollState`.
     */
    scrollState: ScrollState? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val mode = upSafeEnum(props.mode, BackTopModes, "circle", diagnostics, BackTopComponentName, "mode")
    // `show() { return getPx(scrollTop) > getPx(top) }`. `scrollState` is the ergonomic way
    // in — a caller that already has one need not mirror its offset into `scrollTop`.
    val scrollTop = scrollState?.value?.toFloat() ?: props.scrollTop.asFiniteFloatOrNull() ?: 0f
    val threshold = props.top.asFiniteFloatOrNull() ?: 400f
    val visible = upBackTopVisible(scrollTop, threshold)

    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, BackTopComponentName)
    val iconColor = UPColor.parse(props.iconStyle["color"].upStringValueOrEmpty(), UPTheme.Tips)
    val iconSize = props.iconStyle["fontSize"].upTextUnitOr(19.sp)
    val shape = if (mode == "circle") RoundedCornerShape(percent = 50) else RoundedCornerShape(4.dp)
    val scrollDuration = upBackTopScrollDurationMillis(props.duration)
    val coroutineScope = rememberCoroutineScope()
    val callback: () -> Unit = {
        onClick?.invoke()
        onBackToTop?.invoke()
        // `uni.pageScrollTo({ scrollTop: 0, duration })`. With a `scrollState` in hand this
        // component performs the scroll itself; without one the resolved duration rides
        // along with the callback so the host can do it instead of it being dropped.
        if (scrollState != null) {
            coroutineScope.launch { scrollState.animateScrollTo(0, tween(scrollDuration)) }
        }
        onScrollToTop?.invoke(scrollDuration)
    }

    // `<u-transition mode="fade" :show="show">`: the button fades in and out rather than
    // appearing outright. `entered` has to start false or `animateFloatAsState` would
    // initialise at the end state and nothing would interpolate.
    var entered by remember { mutableStateOf(visible) }
    LaunchedEffect(visible) { entered = visible }
    val opacity by animateFloatAsState(
        targetValue = if (entered && visible) 1f else 0f,
        animationSpec = tween(upTransitionDuration(null)),
        label = "up-back-top-fade",
    )
    // The button leaves the tree only once the fade has finished, so a caller scrolling back
    // up sees it go rather than blink out.
    if (!visible && opacity <= 0f) return

    Box(
        modifier = modifier
            .zIndex(props.zIndex.asFiniteFloatOrNull() ?: 9f)
            // `backTopStyle` fixes the button at 40x40 regardless of its contents.
            .size(UPBackTopSizeDp.dp)
            // `bottom` / `right` are `position: fixed` offsets from the viewport corner
            // upstream. Android has no fixed positioning, so the host anchors the button
            // (typically `Box(contentAlignment = Alignment.BottomEnd)`) and these become an
            // inward offset from that corner. Applying them as padding instead — which is
            // what this did before — inflated the button's own footprint to 140dp and got it
            // clipped, because padding is laid out whereas a fixed offset is not.
            .offset(x = -upRawDp(props.right, 20.dp), y = -upRawDp(props.bottom, 100.dp))
            .alpha(opacity)
            .clip(shape)
            // `.u-back-top { background-color: #E1E1E1 }` — not white, which is what this
            // painted before and what made the button invisible on a white page.
            .background(Color(UPBackTopBackground))
            .applyUPResolvedStyle(style)
            .upTestTag("back-top")
            .upClickable(enabled = true, onClick = callback),
        contentAlignment = Alignment.Center,
    ) {
        // `.u-back-top { flex-direction: column }`: the label sits under the glyph.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            UPIcon(
                props = UPIconProps(
                    name = props.icon,
                    color = "#%08X".format(iconColor.value.toLong()),
                    size = iconSize,
                ),
                diagnostics = diagnostics,
            )
            if (props.text.isNotEmpty()) {
                // `.u-back-top__tips { font-size: 12px; transform: scale(0.8) }`.
                BasicText(
                    props.text,
                    modifier = Modifier.graphicsLayer { scaleX = 0.8f; scaleY = 0.8f }.upTestTag("back-top-text"),
                    style = TextStyle(color = iconColor, fontSize = 12.sp),
                )
            }
        }
    }
}

/** Convenience overload for generated source that supplies a scroll position directly. */
@Composable
public fun UPBackTop(
    scrollTop: Any?,
    top: Any? = 400,
    text: String = "",
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPBackTop(
        props = UPBackTopProps(scrollTop = scrollTop, top = top, text = text),
        modifier = modifier,
        onClick = onClick,
        diagnostics = diagnostics,
    )
}
