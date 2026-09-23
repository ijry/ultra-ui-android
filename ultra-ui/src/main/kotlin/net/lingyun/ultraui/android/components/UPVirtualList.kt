package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

/** `getItemKey(item)`: item[keyField] when present, else the virtual index. */
internal fun upVirtualListKey(item: Map<String, UPRawValue>, keyField: String, index: Int): Any =
    item[keyField] ?: index

/**
 * Native Compose counterpart of uview-plus `u-virtual-list`.
 *
 * A fixed-row-height virtualized list. Upstream hand-rolls windowing (top/bottom spacers around the
 * visible slice); Compose's `LazyColumn` is the native equivalent, so the port renders `listData`
 * into a `LazyColumn` bounded to `height` with each row fixed at `itemHeight`. Scrolling reports
 * the pixel offset through [onScroll] (upstream `scroll` / `update:scrollTop`). `content` renders a
 * row from its item and index.
 *
 * Difference: `buffer` tunes upstream's manual overscan; `LazyColumn` manages its own recycling
 * window, so the value is advisory. `scrollTop` seeds the initial offset.
 */
@Composable
public fun UPVirtualList(
    props: UPVirtualListProps = UPVirtualListProps(),
    modifier: Modifier = Modifier,
    onScroll: ((Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable (item: Map<String, UPRawValue>, index: Int) -> Unit,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPVirtualList")
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (props.itemHeight > 0) props.scrollTop / props.itemHeight else 0,
        initialFirstVisibleItemScrollOffset = if (props.itemHeight > 0) props.scrollTop % props.itemHeight else 0,
    )
    val heightMod = if (props.height.toString() == "100%") Modifier else Modifier.height(upDimension(props.height, 300.dp))

    LaunchedEffect(listState, onScroll) {
        if (onScroll == null) return@LaunchedEffect
        snapshotFlow { listState.firstVisibleItemIndex * props.itemHeight + listState.firstVisibleItemScrollOffset }
            .collect { onScroll(it) }
    }

    Box(modifier.fillMaxWidth().then(heightMod).applyUPResolvedStyle(style).upTestTag("virtual-list")) {
        val data = props.listData
        LazyColumn(state = listState) {
            items(
                count = data.size,
                key = { index -> upVirtualListKey(data[index].upStringKeyMapOrEmpty(), props.keyField, index) },
            ) { index ->
                val item = data[index].upStringKeyMapOrEmpty()
                Box(Modifier.fillMaxWidth().height(props.itemHeight.dp).upTestTag("virtual-list-item-$index")) {
                    content(item, index)
                }
            }
        }
    }
}
