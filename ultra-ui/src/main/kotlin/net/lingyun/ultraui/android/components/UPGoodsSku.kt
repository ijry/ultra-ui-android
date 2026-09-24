package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `up-goods-sku`.
 *
 * A bottom-sheet SKU picker: a header (price/stock/selected summary), the `skuTree` attribute
 * groups whose leaf chips toggle a per-group selection (chips that can no longer match any
 * `skuList` row are disabled), a quantity `u-number-box` and a confirm button. Price/stock come
 * from the matched combination (via [upGoodsSkuComb]) or fall back to `goodsInfo`. Confirm is
 * enabled once every group is chosen and stock is positive, emitting `confirm` ([onConfirm]) with
 * the matched sku, quantity and selection summary. Opening/closing emit through [onUpdateShow].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun UPGoodsSku(
    props: UPGoodsSkuProps = UPGoodsSkuProps(),
    modifier: Modifier = Modifier,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onConfirm: ((sku: Map<String, UPRawValue>?, num: Int, selectedText: String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    var selected by remember(props.skuTree) { mutableStateOf(mapOf<String, UPRawValue>()) }
    var buyNum by remember { mutableIntStateOf(1) }

    val comb = upGoodsSkuComb(props.skuTree, props.skuList, selected)
    val price = comb?.get("price") ?: comb?.get("price_fee") ?: props.goodsInfo["price"] ?: props.goodsInfo["price_fee"] ?: 0
    val stock = (comb?.get("stock") ?: comb?.get("quantity") ?: props.goodsInfo["stock"] ?: props.goodsInfo["quantity"] ?: 0).toString().toIntOrNull() ?: 0
    val maxBuyNum = if (stock > props.maxBuy) props.maxBuy else stock
    val allChosen = selected.filterValues { it.toString().isNotEmpty() }.size == props.skuTree.size && props.skuTree.isNotEmpty()
    val canBuy = allChosen && buyNum > 0 && stock > 0

    val selectedText = props.skuTree.mapNotNull { raw ->
        val group = raw.upStringKeyMapOrEmpty()
        val name = group["name"].upStringValueOrEmpty()
        val chosenId = selected[name]
        (group["children"] as? List<*>)
            ?.mapNotNull { it as? Map<*, *> }
            ?.firstOrNull { it["id"]?.toString() == chosenId?.toString() }
            ?.get("name")?.toString()
    }.joinToString(", ")

    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPGoodsSku")
    UPPopup(
        props = UPPopupProps(show = props.show, mode = "bottom", closeable = if (props.pageInline) false else props.closeable, pageInline = props.pageInline, round = 20),
        modifier = modifier,
        onUpdateShow = onUpdateShow,
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp).applyUPResolvedStyle(style).upTestTag("goods-sku")) {
            // Header.
            Column(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                    BasicText("¥", style = TextStyle(color = Color(0xFFFA3534), fontSize = 13.sp))
                    BasicText(price.toString(), modifier = Modifier.upTestTag("goods-sku-price"), style = TextStyle(color = Color(0xFFFA3534), fontSize = 20.sp, fontWeight = FontWeight.Bold))
                }
                BasicText("库存 $stock 件", modifier = Modifier.padding(top = 4.dp), style = TextStyle(color = UPTheme.Tips, fontSize = 12.sp))
                BasicText("已选: $selectedText", modifier = Modifier.padding(top = 4.dp).upTestTag("goods-sku-selected"), style = TextStyle(color = UPTheme.Content, fontSize = 12.sp))
            }

            // Attribute groups.
            props.skuTree.forEach { raw ->
                val group = raw.upStringKeyMapOrEmpty()
                val name = group["name"].upStringValueOrEmpty()
                Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    BasicText(group["label"].upStringValueOrEmpty(), style = TextStyle(color = UPTheme.Main, fontSize = 14.sp, fontWeight = FontWeight.Medium))
                    FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (group["children"] as? List<*>)?.mapNotNull { it as? Map<*, *> }?.forEach { leaf ->
                            val leafId = leaf["id"] as UPRawValue
                            val active = selected[name]?.toString() == leafId?.toString()
                            val disabled = upGoodsSkuLeafDisabled(props.skuTree, props.skuList, selected.filterKeys { it != name }, name, leafId)
                            val bg = if (active) UPTheme.Primary else Color(0xFFF5F6F8)
                            val fg = when { disabled -> UPTheme.Light; active -> Color.White; else -> UPTheme.Main }
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(bg)
                                    .then(if (active) Modifier else Modifier.border(0.5.dp, UPTheme.Border, RoundedCornerShape(4.dp)))
                                    .upTestTag("goods-sku-leaf-${leaf["id"]}")
                                    .upClickable(enabled = !disabled, onClick = {
                                        selected = selected.toMutableMap().apply {
                                            if (this[name]?.toString() == leafId?.toString()) put(name, "") else put(name, leafId)
                                        }
                                    })
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                BasicText(leaf["name"]?.toString() ?: "", style = TextStyle(color = fg, fontSize = 13.sp))
                            }
                        }
                    }
                }
            }

            // Quantity.
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                BasicText("购买数量", style = TextStyle(color = UPTheme.Main, fontSize = 14.sp))
                UPNumberBox(
                    UPNumberBoxProps(value = buyNum, min = 1, max = maxBuyNum.coerceAtLeast(1), disabled = !canBuy),
                    onChange = { buyNum = it.toString().toIntOrNull() ?: buyNum },
                    diagnostics = diagnostics,
                )
            }

            // Confirm.
            UPButton(
                UPButtonProps(text = props.confirmText, type = "primary", disabled = !canBuy),
                onClick = {
                    if (canBuy) onConfirm?.invoke(comb, buyNum, selectedText)
                },
                modifier = Modifier.fillMaxWidth().upTestTag("goods-sku-confirm"),
                diagnostics = diagnostics,
            )
        }
    }
}
