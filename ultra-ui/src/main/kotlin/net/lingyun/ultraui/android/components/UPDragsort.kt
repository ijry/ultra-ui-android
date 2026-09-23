package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upTestTag

/** `orderIds.splice(from,1); splice(to,0,moved)`: move one element, returning a new list. */
internal fun <T> upDragsortMove(list: List<T>, from: Int, to: Int): List<T> {
    if (from !in list.indices || to !in list.indices || from == to) return list
    val mutable = list.toMutableList()
    val moved = mutable.removeAt(from)
    mutable.add(to, moved)
    return mutable
}

/** Whether an item participates in dragging: global `draggable` and its own `draggable != false`. */
internal fun upDragsortItemDraggable(globalDraggable: Boolean, item: Map<String, UPRawValue>): Boolean {
    if (!globalDraggable) return false
    val own = item["draggable"]
    return own != false
}

/**
 * Native Compose counterpart of uview-plus `u-dragsort` (vertical mode).
 *
 * A long-press-and-drag reorderable list. Each item renders `content` (or its `label`), and while
 * a draggable row is held it moves with the finger, swapping past neighbours by row height; on
 * release `drag-end` reports the reordered list ([onDragEnd]). A per-item `draggable = false` (or
 * global `draggable = false`) pins the row.
 *
 * Difference: upstream also supports `horizontal`/`all` (grid) reordering via `movable-view`; the
 * port ships the vertical list here and reports the other directions as diagnostics, since a
 * faithful grid drag needs measured cell geometry the host can supply per layout.
 */
@Composable
public fun UPDragsort(
    props: UPDragsortProps = UPDragsortProps(),
    modifier: Modifier = Modifier,
    rowHeight: androidx.compose.ui.unit.Dp = 48.dp,
    onDragEnd: ((List<UPRawValue>) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable (item: Map<String, UPRawValue>, index: Int) -> Unit)? = null,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPDragsort")
    var order by remember(props.initialList) { mutableStateOf(props.initialList) }

    if (props.direction != "vertical") {
        androidx.compose.runtime.LaunchedEffect(props.direction) {
            diagnostics.report("UPDragsort", "direction", props.direction, "Only vertical reordering is modelled; horizontal/all grid drag belongs to a host layout.")
        }
    }

    val density = androidx.compose.ui.platform.LocalDensity.current
    val rowPx = with(density) { rowHeight.toPx() }

    Column(modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("dragsort")) {
        order.forEachIndexed { index, raw ->
            val item = raw.upStringKeyMapOrEmpty()
            val canDrag = upDragsortItemDraggable(props.draggable, item)
            var dragging by remember(order) { mutableStateOf(false) }
            var accumulated by remember(order) { mutableStateOf(0f) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (dragging) Modifier.background(UPTheme.Border.copy(alpha = 0.3f)) else Modifier)
                    .upTestTag("dragsort-item-$index")
                    .then(
                        if (!canDrag) {
                            Modifier
                        } else {
                            Modifier.pointerInput(order, index) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { dragging = true; accumulated = 0f },
                                    onDragEnd = { dragging = false; accumulated = 0f; onDragEnd?.invoke(order) },
                                    onDragCancel = { dragging = false; accumulated = 0f },
                                    onDrag = { change, delta ->
                                        change.consume()
                                        accumulated += delta.y
                                        val steps = (accumulated / rowPx).toInt()
                                        if (steps != 0) {
                                            val target = (index + steps).coerceIn(0, order.size - 1)
                                            if (target != index) {
                                                order = upDragsortMove(order, index, target)
                                                accumulated -= steps * rowPx
                                            }
                                        }
                                    },
                                )
                            }
                        },
                    ),
            ) {
                if (content != null) {
                    content(item, index)
                } else {
                    BasicText(
                        item["label"].upStringValueOrEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        style = TextStyle(color = UPTheme.Main, fontSize = 15.sp),
                    )
                }
            }
        }
    }
}
