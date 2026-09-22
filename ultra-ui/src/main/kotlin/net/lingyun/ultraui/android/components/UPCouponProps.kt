package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPCoupon], mirroring uview-plus `up-coupon`.
 *
 * A coupon card: a left amount block (`unit` + `amount` + `limit`), a middle info block
 * (`title`/`desc`/`time`) and a right action tag (`actionText`). `shape` is `coupon`/`envelope`/
 * `card`, `size` is `small`/`medium`/`large`. `bgColor`/`color` override the pink defaults;
 * `disabled` dims the card.
 */
public data class UPCouponProps(
    val amount: UPRawValue = "",
    val unit: String = "￥",
    val unitPosition: String = "left",
    val limit: String = "",
    val title: String = "优惠券",
    val desc: String = "",
    val time: String = "",
    val actionText: String = "使用",
    val shape: String = "coupon",
    val size: String = "medium",
    val circle: Boolean = false,
    val disabled: Boolean = false,
    val bgColor: String = "",
    val color: String = "",
    val type: String = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
