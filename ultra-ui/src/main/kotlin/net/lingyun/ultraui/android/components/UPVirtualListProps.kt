package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPVirtualList], mirroring uview-plus `u-virtual-list`.
 *
 * A fixed-row-height virtualized list. `listData` is the data source, `itemHeight` the row height
 * (px), `height` the container height. `keyField` names each item's stable key. `buffer` is the
 * off-screen row overscan; on Compose `LazyColumn` owns virtualization so it is advisory.
 */
public data class UPVirtualListProps(
    val listData: List<UPRawValue> = emptyList(),
    val itemHeight: Int = 50,
    val height: UPRawValue = "100%",
    val buffer: Int = 4,
    val keyField: String = "id",
    val scrollTop: Int = 0,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
