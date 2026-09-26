package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPRefreshVirtualList], mirroring uview-plus `u-refresh-virtual-list`.
 *
 * A thin composition of [UPPullRefresh] over [UPVirtualList]: `listData` is the data source,
 * `itemHeight` the fixed row height (px), `height` the container height, `buffer` the advisory
 * off-screen overscan and `keyField` each item's stable key. All are forwarded to [UPVirtualList];
 * the pull threshold is fixed at 50 px as upstream hardcodes.
 */
public data class UPRefreshVirtualListProps(
    val listData: List<UPRawValue> = emptyList(),
    val itemHeight: Int = 50,
    val height: UPRawValue = "100%",
    val buffer: Int = 4,
    val keyField: String = "id",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
