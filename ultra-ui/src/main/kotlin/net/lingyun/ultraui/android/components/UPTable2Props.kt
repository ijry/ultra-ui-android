package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPTable2], mirroring uview-plus `u-table2`.
 *
 * [data] is the row list and [columns] the column descriptors (each a map with `key`, `title`,
 * `width`, `align`/`headerAlign`, `type` of `default`/`selection`, `sortable`). The port implements
 * the core table: header, cells (`row[col.key]`), [stripe]/[border], [showHeader], [emptyText],
 * [height]/[maxHeight] scroll, [fixedHeader], [rowKey] identity, [rowHeight], row tap +
 * [highlightCurrentRow]/[currentRowKey], a selection column and basic single-column sort driven by
 * [sortable]/[sortOrders].
 *
 * The advanced engines are not modelled: tree/lazy expansion ([lazy], [treeProps], [defaultExpandAll],
 * [expandRowKeys], [mainCol], [expandWidth]), multi/custom sort ([multiSort], [sortBy]), column
 * [filters], hover [showOverflowTooltip] and the upstream style/class/render callbacks (`rowStyle`,
 * `cellStyle`, `cellClassName`, `headerCellClassName`, `rowClassName`, `context`, `load`,
 * `sortMethod`, `spanMethod`) have no Compose equivalent; see the docs downgrade notes.
 */
public data class UPTable2Props(
    val data: List<UPRawValue> = emptyList(),
    val columns: List<UPRawValue> = emptyList(),
    val stripe: Boolean = false,
    val border: Boolean = false,
    val height: UPRawValue = "",
    val maxHeight: UPRawValue = "",
    val showHeader: Boolean = true,
    val highlightCurrentRow: Boolean = false,
    val rowKey: String = "id",
    val currentRowKey: UPRawValue = "",
    val showOverflowTooltip: Boolean = false,
    val lazy: Boolean = false,
    val treeProps: Map<String, UPRawValue> = mapOf("children" to "children", "hasChildren" to "hasChildren"),
    val defaultExpandAll: Boolean = false,
    val expandRowKeys: List<UPRawValue> = emptyList(),
    val sortOrders: List<UPRawValue> = listOf("ascending", "descending"),
    val sortable: Boolean = false,
    val multiSort: Boolean = false,
    val sortBy: String = "",
    val filters: Map<String, UPRawValue> = emptyMap(),
    val fixedHeader: Boolean = true,
    val emptyText: String = "暂无数据",
    val mainCol: String = "",
    val expandWidth: String = "25px",
    val rowHeight: String = "36px",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
