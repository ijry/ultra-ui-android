package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue

/**
 * `getSkuComb(selectedSku)`: once every group in [skuTree] has a non-empty selection, find the
 * [skuList] row whose per-group keys all match. Returns the matched combination map or null.
 */
internal fun upGoodsSkuComb(
    skuTree: List<UPRawValue>,
    skuList: List<UPRawValue>,
    selected: Map<String, UPRawValue>,
): Map<String, UPRawValue>? {
    val filled = selected.filterValues { it != null && it.toString().isNotEmpty() }
    if (filled.size != skuTree.size) return null
    for (raw in skuList) {
        val combo = raw.upStringKeyMapOrEmpty()
        val match = filled.all { (key, value) -> combo[key]?.toString() == value.toString() }
        if (match) return combo
    }
    return null
}

/** Whether choosing [skuValueId] for [skuKey] would still leave a matchable combination. */
internal fun upGoodsSkuLeafDisabled(
    skuTree: List<UPRawValue>,
    skuList: List<UPRawValue>,
    selected: Map<String, UPRawValue>,
    skuKey: String,
    skuValueId: UPRawValue,
): Boolean {
    val probe = selected.toMutableMap().apply { put(skuKey, skuValueId) }
    val filled = probe.filterValues { it != null && it.toString().isNotEmpty() }
    // A leaf is disabled when no skuList row is compatible with the probe selection so far.
    return skuList.none { raw ->
        val combo = raw.upStringKeyMapOrEmpty()
        filled.all { (key, value) -> combo[key]?.toString() == value.toString() }
    }
}
