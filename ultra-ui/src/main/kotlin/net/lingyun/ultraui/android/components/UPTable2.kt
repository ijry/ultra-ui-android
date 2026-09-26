package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

/** Cycles a sort order for a column: none -> orders[0] -> orders[1] -> ... -> none. */
internal fun upTable2NextSortOrder(current: String, orders: List<String>): String {
    if (orders.isEmpty()) return ""
    if (current.isEmpty()) return orders.first()
    val idx = orders.indexOf(current)
    return if (idx < 0 || idx == orders.lastIndex) "" else orders[idx + 1]
}

/** Sorts [data] by [key] under [order] ("ascending"/"descending"; anything else keeps input order). */
internal fun upTable2SortData(
    data: List<UPRawValue>,
    key: String,
    order: String,
): List<UPRawValue> {
    if (key.isEmpty() || (order != "ascending" && order != "descending")) return data
    val comparator = Comparator<UPRawValue> { a, b ->
        val av = a.upStringKeyMapOrEmpty()[key]
        val bv = b.upStringKeyMapOrEmpty()[key]
        val an = (av as? Number)?.toDouble() ?: av.upStringValueOrEmpty().toDoubleOrNull()
        val bn = (bv as? Number)?.toDouble() ?: bv.upStringValueOrEmpty().toDoubleOrNull()
        if (an != null && bn != null) an.compareTo(bn)
        else av.upStringValueOrEmpty().compareTo(bv.upStringValueOrEmpty())
    }
    val sorted = data.sortedWith(comparator)
    return if (order == "descending") sorted.reversed() else sorted
}

private data class UPTable2Column(
    val key: String,
    val title: String,
    val type: String,
    val width: UPRawValue,
    val align: String,
    val headerAlign: String,
    val sortable: Boolean,
)

private fun UPRawValue.toColumn(globalSortable: Boolean): UPTable2Column {
    val m = upStringKeyMapOrEmpty()
    return UPTable2Column(
        key = m["key"].upStringValueOrEmpty(),
        title = m["title"].upStringValueOrEmpty(),
        type = m["type"].upStringValueOrEmpty().ifEmpty { "default" },
        width = m["width"] ?: "",
        align = m["align"].upStringValueOrEmpty().ifEmpty { "left" },
        headerAlign = m["headerAlign"].upStringValueOrEmpty(),
        sortable = m["sortable"]?.upBooleanValue(globalSortable) ?: globalSortable,
    )
}

private fun cellAlignment(align: String): TextAlign = when (align) {
    "center" -> TextAlign.Center
    "right" -> TextAlign.Right
    else -> TextAlign.Left
}

/**
 * Native Compose counterpart of uview-plus `u-table2` (core subset).
 *
 * Renders [`UPTable2Props.columns`] over [`UPTable2Props.data`]: an optional sticky [showHeader]
 * header, cell text from `row[col.key]`, [stripe] zebra rows, [border] dividers, [emptyText] when
 * empty, [height]/[maxHeight] vertical scroll and horizontal scroll for wide tables, a `selection`
 * column with per-row + select-all checkboxes ([onSelectionChange]), row tap ([onRowClick]) with
 * [highlightCurrentRow]/[currentRowKey] tracking ([onCurrentChange]) and basic single-column sort via
 * [sortable]/[sortOrders] ([onSortChange]).
 *
 * Downgrades (see docs): tree/lazy expansion, multi/custom sort, column filters, hover tooltips and
 * the upstream style/class/render callbacks are not modelled.
 */
@Composable
public fun UPTable2(
    props: UPTable2Props = UPTable2Props(),
    modifier: Modifier = Modifier,
    onRowClick: ((Map<String, UPRawValue>) -> Unit)? = null,
    onCurrentChange: ((Map<String, UPRawValue>) -> Unit)? = null,
    onSelectionChange: ((List<Map<String, UPRawValue>>) -> Unit)? = null,
    onSortChange: ((String, String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTable2")
    val columns = remember(props.columns, props.sortable) { props.columns.map { it.toColumn(props.sortable) } }
    var sortKey by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf("") }
    var currentKey by remember(props.currentRowKey) { mutableStateOf(props.currentRowKey.upStringValueOrEmpty()) }
    var selectedKeys by remember { mutableStateOf(setOf<String>()) }

    val sortedData = remember(props.data, sortKey, sortOrder) {
        upTable2SortData(props.data, sortKey, sortOrder)
    }
    val orders = remember(props.sortOrders) { props.sortOrders.map { it.upStringValueOrEmpty() } }

    fun rowKeyOf(row: Map<String, UPRawValue>): String = row[props.rowKey].upStringValueOrEmpty()

    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .applyUPResolvedStyle(style)
            .upTestTag("table2"),
    ) {
        val headerContent: @Composable () -> Unit = {
                Row(
                    Modifier
                        .background(Color(0xFFF5F6FA))
                        .then(if (props.border) Modifier.border(1.dp, UPTheme.Border) else Modifier)
                        .upTestTag("table2-header"),
                ) {
                    for (col in columns) {
                        val headerAlign = col.headerAlign.ifEmpty { col.align }
                        Box(
                            Modifier
                                .width(upDimension(col.width, 100.dp))
                                .height(upDimension(props.rowHeight, 36.dp))
                                .then(if (props.border) Modifier.border(0.5.dp, UPTheme.Border) else Modifier)
                                .then(
                                    if (col.sortable && col.type == "default") {
                                        Modifier.upClickable(onClick = {
                                            if (sortKey != col.key) {
                                                sortKey = col.key
                                                sortOrder = upTable2NextSortOrder("", orders)
                                            } else {
                                                sortOrder = upTable2NextSortOrder(sortOrder, orders)
                                                if (sortOrder.isEmpty()) sortKey = ""
                                            }
                                            onSortChange?.invoke(col.key, sortOrder)
                                        })
                                    } else {
                                        Modifier
                                    },
                                )
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (col.type == "selection") {
                                UPTable2Check(
                                    checked = selectedKeys.isNotEmpty() && selectedKeys.size == sortedData.size,
                                    onToggle = {
                                        selectedKeys = if (selectedKeys.size == sortedData.size) {
                                            emptySet()
                                        } else {
                                            sortedData.map { rowKeyOf(it.upStringKeyMapOrEmpty()) }.toSet()
                                        }
                                        onSelectionChange?.invoke(
                                            sortedData.map { it.upStringKeyMapOrEmpty() }
                                                .filter { rowKeyOf(it) in selectedKeys },
                                        )
                                    },
                                )
                            } else {
                                val arrow = if (sortKey == col.key) {
                                    when (sortOrder) { "ascending" -> " \u2191"; "descending" -> " \u2193"; else -> "" }
                                } else {
                                    ""
                                }
                                BasicText(
                                    text = col.title + arrow,
                                    style = TextStyle(color = UPTheme.Main, fontSize = 14.sp, textAlign = cellAlignment(headerAlign)),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
        }

        Column(Modifier.horizontalScroll(hScroll)) {
            if (props.showHeader && props.fixedHeader) headerContent()

            if (sortedData.isEmpty()) {
                Box(
                    Modifier.fillMaxWidth().height(upDimension(props.rowHeight, 36.dp).times(2)).upTestTag("table2-empty"),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(text = props.emptyText, style = TextStyle(color = UPTheme.Tips, fontSize = 14.sp))
                }
            } else {
                val rowsModifier = when {
                    props.height.upStringValueOrEmpty().isNotEmpty() ->
                        Modifier.height(upDimension(props.height, 300.dp)).verticalScroll(vScroll)
                    props.maxHeight.upStringValueOrEmpty().isNotEmpty() ->
                        Modifier.heightIn(max = upDimension(props.maxHeight, 300.dp)).verticalScroll(vScroll)
                    else -> Modifier
                }
                Column(rowsModifier) {
                    if (props.showHeader && !props.fixedHeader) headerContent()
                    sortedData.forEachIndexed { index, rawRow ->
                        val row = rawRow.upStringKeyMapOrEmpty()
                        val key = rowKeyOf(row)
                        val isCurrent = props.highlightCurrentRow && key.isNotEmpty() && key == currentKey
                        val bg = when {
                            isCurrent -> UPTheme.Primary.copy(alpha = 0.12f)
                            props.stripe && index % 2 == 1 -> Color(0xFFFAFAFA)
                            else -> Color.Transparent
                        }
                        Row(
                            Modifier
                                .background(bg)
                                .then(if (props.border) Modifier.border(0.5.dp, UPTheme.Border) else Modifier)
                                .upClickable(onClick = {
                                    if (props.highlightCurrentRow) {
                                        currentKey = key
                                        onCurrentChange?.invoke(row)
                                    }
                                    onRowClick?.invoke(row)
                                })
                                .upTestTag("table2-row-$index"),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            for (col in columns) {
                                Box(
                                    Modifier
                                        .width(upDimension(col.width, 100.dp))
                                        .height(upDimension(props.rowHeight, 36.dp))
                                        .then(if (props.border) Modifier.border(0.5.dp, UPTheme.Border) else Modifier)
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    if (col.type == "selection") {
                                        val checked = key in selectedKeys
                                        UPTable2Check(
                                            checked = checked,
                                            onToggle = {
                                                selectedKeys = if (checked) selectedKeys - key else selectedKeys + key
                                                onSelectionChange?.invoke(
                                                    sortedData.map { it.upStringKeyMapOrEmpty() }
                                                        .filter { rowKeyOf(it) in selectedKeys },
                                                )
                                            },
                                        )
                                    } else {
                                        BasicText(
                                            text = row[col.key].upStringValueOrEmpty(),
                                            style = TextStyle(color = UPTheme.Content, fontSize = 14.sp, textAlign = cellAlignment(col.align)),
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UPTable2Check(checked: Boolean, onToggle: () -> Unit) {
    Box(
        Modifier
            .size(18.dp)
            .border(1.dp, if (checked) UPTheme.Primary else UPTheme.Border)
            .background(if (checked) UPTheme.Primary else Color.Transparent)
            .upClickable(onClick = onToggle)
            .upTestTag("table2-check"),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            BasicText(text = "\u2713", style = TextStyle(color = Color.White, fontSize = 12.sp))
        }
    }
}
