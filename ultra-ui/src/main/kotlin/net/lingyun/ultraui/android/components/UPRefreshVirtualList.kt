package net.lingyun.ultraui.android.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Imperative handle for [UPRefreshVirtualList], mirroring the upstream component's ref methods.
 *
 * `finishRefresh()` clears the refreshing flag (upstream `finishRefresh`); `scrollTo(top)` /
 * `scrollToTop()` jump the list to a pixel offset (upstream mutates `scrollTop`, which the inner
 * virtual list watches). Remember one with [rememberUPRefreshVirtualListController].
 */
public class UPRefreshVirtualListController {
    internal var refreshing by mutableStateOf(false)
    internal var scrollTop by mutableIntStateOf(0)
    internal var remountToken by mutableIntStateOf(0)

    internal fun beginRefresh() {
        refreshing = true
    }

    /** `finishRefresh()`: ends the pull-to-refresh spinner. */
    public fun finishRefresh() {
        refreshing = false
    }

    /** `scrollTo(top)`: seeds the list to [top] pixels from the start. */
    public fun scrollTo(top: Int) {
        scrollTop = top
        remountToken++
    }

    /** `scrollToTop()`: shorthand for `scrollTo(0)`. */
    public fun scrollToTop() {
        scrollTo(0)
    }
}

/** Remembers a [UPRefreshVirtualListController] across recompositions. */
@Composable
public fun rememberUPRefreshVirtualListController(): UPRefreshVirtualListController =
    remember { UPRefreshVirtualListController() }

/**
 * Native Compose counterpart of uview-plus `u-refresh-virtual-list`.
 *
 * Wraps [UPVirtualList] in [UPPullRefresh] exactly as upstream does: a pull past the fixed 50 px
 * threshold flips the internal refreshing flag and invokes [onRefresh]; the host calls
 * [UPRefreshVirtualListController.finishRefresh] when done. Scrolling reports the pixel offset via
 * [onScroll] and updates the controller's `scrollTop`. `content` renders a row from its item and
 * index. Programmatic `scrollTo`/`scrollToTop` re-seed the inner list to that offset.
 */
@Composable
public fun UPRefreshVirtualList(
    props: UPRefreshVirtualListProps = UPRefreshVirtualListProps(),
    controller: UPRefreshVirtualListController = rememberUPRefreshVirtualListController(),
    modifier: Modifier = Modifier,
    onRefresh: (() -> Unit)? = null,
    onScroll: ((Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable (item: Map<String, UPRawValue>, index: Int) -> Unit,
) {
    UPPullRefresh(
        props = UPPullRefreshProps(refreshing = controller.refreshing, threshold = 50),
        modifier = modifier.upTestTag("refresh-virtual-list"),
        onRefresh = {
            controller.beginRefresh()
            onRefresh?.invoke()
        },
        diagnostics = diagnostics,
    ) {
        key(controller.remountToken) {
            UPVirtualList(
                props = UPVirtualListProps(
                    listData = props.listData,
                    itemHeight = props.itemHeight,
                    height = props.height,
                    buffer = props.buffer,
                    keyField = props.keyField,
                    scrollTop = controller.scrollTop,
                    customStyle = props.customStyle,
                ),
                onScroll = {
                    controller.scrollTop = it
                    onScroll?.invoke(it)
                },
                diagnostics = diagnostics,
                content = content,
            )
        }
    }
}
