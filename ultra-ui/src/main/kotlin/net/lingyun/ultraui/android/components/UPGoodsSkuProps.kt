package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPGoodsSku], mirroring uview-plus `up-goods-sku`.
 *
 * A bottom-sheet SKU picker. `goodsInfo` is the fallback price/stock/image; `skuTree` is the list
 * of attribute groups (`{ label, name, children: [{ id, name }] }`); `skuList` is the flat list of
 * concrete combinations (each carries the per-group `name -> id` keys plus `price`/`stock`).
 * `maxBuy` caps the quantity; `confirmText` labels the button; `closeable`/`pageInline` follow the
 * popup. `show` controls visibility.
 */
public data class UPGoodsSkuProps(
    val goodsInfo: Map<String, UPRawValue> = emptyMap(),
    val skuTree: List<UPRawValue> = emptyList(),
    val skuList: List<UPRawValue> = emptyList(),
    val maxBuy: Int = 999,
    val confirmText: String = "确定",
    val closeable: Boolean = true,
    val pageInline: Boolean = false,
    val show: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
