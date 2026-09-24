package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upTestTag

/** Pull states, matching upstream `refreshStatus`: pull, release, refreshing. */
public enum class UPPullRefreshStatus { Pull, Release, Refreshing }

/** `refreshDistance = min(diff * damping, maxDistance)`, clamped to >= 0. */
internal fun upPullRefreshDistance(rawDiff: Float, damping: Float, maxDistance: Int): Float =
    (rawDiff * damping).coerceIn(0f, maxDistance.toFloat())

/** The status for a pulled distance: refreshing dominates, else release past threshold else pull. */
internal fun upPullRefreshStatus(distance: Float, threshold: Int, refreshing: Boolean): UPPullRefreshStatus = when {
    refreshing -> UPPullRefreshStatus.Refreshing
    distance >= threshold -> UPPullRefreshStatus.Release
    else -> UPPullRefreshStatus.Pull
}

/**
 * Native Compose counterpart of uview-plus `u-pull-refresh`.
 *
 * A pull-to-refresh wrapper: dragging down from the top reveals a refresh area whose height follows
 * `min(diff * damping, maxDistance)`; crossing `threshold` and releasing arms `refresh`
 * ([onRefresh]) and shows the refreshing state until the controlled `refreshing` flag returns false.
 * The header text tracks the pull/release/refreshing status; [pullContent]/[releaseContent]/
 * [refreshingContent] can override the default indicator per state.
 *
 * Difference: upstream only pulls when its inner `scroll-view` is at the top; the port drives the
 * pull from a vertical drag and assumes the caller places scrollable content that starts at top
 * (or gates via [enabled]). The bundled loadmore (`showLoadmore`) is left to a composed `UPLoadmore`.
 */
@Composable
public fun UPPullRefresh(
    props: UPPullRefreshProps = UPPullRefreshProps(),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onRefresh: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    pullContent: (@Composable () -> Unit)? = null,
    releaseContent: (@Composable () -> Unit)? = null,
    refreshingContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPPullRefresh")
    val density = LocalDensity.current
    // Work in dp throughout: `distance`/`accumulated` are dp so they compare directly to `threshold`.
    var distance by remember { mutableFloatStateOf(0f) }
    var accumulated by remember { mutableFloatStateOf(0f) }

    // While the controlled `refreshing` flag is set, the area rests at threshold height; otherwise
    // it settles back to the pulled distance and animates to 0 on release.
    val restingDp = if (props.refreshing) props.threshold.toFloat() else distance
    val animatedDp by animateFloatAsState(targetValue = restingDp, animationSpec = tween(200), label = "up-pull-refresh")
    val status = upPullRefreshStatus(distance, props.threshold, props.refreshing)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .applyUPResolvedStyle(style)
            .upTestTag("pull-refresh")
            .then(
                if (!enabled || props.refreshing) {
                    Modifier
                } else {
                    Modifier.pointerInput(props.threshold, props.damping, props.maxDistance) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (distance >= props.threshold) onRefresh?.invoke()
                                distance = 0f
                                accumulated = 0f
                            },
                            onDragCancel = { distance = 0f; accumulated = 0f },
                            onVerticalDrag = { change, delta ->
                                val deltaDp = with(density) { delta.toDp().value }
                                if (deltaDp > 0f || accumulated > 0f) {
                                    change.consume()
                                    accumulated = (accumulated + deltaDp).coerceAtLeast(0f)
                                    distance = upPullRefreshDistance(accumulated, props.damping, props.maxDistance)
                                }
                            },
                        )
                    }
                },
            ),
    ) {
        val areaDp = (if (props.refreshing) props.threshold.toFloat() else animatedDp).dp
        if (areaDp > 0.dp) {
            Box(
                modifier = Modifier.fillMaxWidth().height(areaDp).upTestTag("pull-refresh-area"),
                contentAlignment = Alignment.Center,
            ) {
                when (status) {
                    UPPullRefreshStatus.Pull -> pullContent?.invoke() ?: RefreshLabel("下拉刷新")
                    UPPullRefreshStatus.Release -> releaseContent?.invoke() ?: RefreshLabel("释放刷新")
                    UPPullRefreshStatus.Refreshing -> refreshingContent?.invoke() ?: RefreshLabel("正在刷新...")
                }
            }
        }
        content()
    }
}

@Composable
private fun RefreshLabel(text: String) {
    BasicText(text, modifier = Modifier.upTestTag("pull-refresh-text"), style = TextStyle(color = UPTheme.Content, fontSize = 13.sp))
}
