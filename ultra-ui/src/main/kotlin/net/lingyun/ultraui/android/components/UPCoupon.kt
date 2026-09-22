package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/** `.up-coupon--{size}` height map: small 80dp, medium 90dp, large 110dp (rpx/2). */
internal fun upCouponHeight(size: String): Dp = when (size) {
    "small" -> 80.dp
    "large" -> 110.dp
    else -> 90.dp
}

/**
 * Native Compose counterpart of uview-plus `up-coupon`.
 *
 * A coupon card laid out in three columns: the amount block (`unit`+`amount`+optional `limit`,
 * the amount in bold red), a dashed divider, the info block (`title`/`desc`/`time`) and a right
 * `up-tag` action button. `size` sets the height, `shape` chooses coupon/envelope/card styling
 * (envelope adds a top candy-stripe bar), `bgColor`/`color` override the pink defaults, and
 * `disabled` dims the whole card to 50% and blocks the click. A tap emits `click`.
 */
@Composable
public fun UPCoupon(
    props: UPCouponProps = UPCouponProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCoupon")
    val shape = RoundedCornerShape(8.dp)
    val background = if (props.bgColor.isNotEmpty()) UPColor.parse(props.bgColor, Color(0xFFFFEBF0)) else Color(0xFFFFEBF0)
    val textColor = if (props.color.isNotEmpty()) UPColor.parse(props.color, UPTheme.Main) else UPTheme.Main

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(upCouponHeight(props.size))
            .clip(shape)
            .background(background)
            .then(if (props.disabled) Modifier.alpha(0.5f) else Modifier)
            .applyUPResolvedStyle(style)
            .upTestTag("coupon")
            .upClickable(enabled = !props.disabled, onClick = { onClick?.invoke() }),
    ) {
        // `.up-coupon--envelope::before`: a candy-stripe bar across the top.
        if (props.shape == "envelope") {
            Box(Modifier.fillMaxWidth().height(10.dp).background(Color(0xFFFFA000)).upTestTag("coupon-rope"))
        }
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Amount block.
            Column(
                modifier = Modifier.padding(end = 15.dp).upTestTag("coupon-amount"),
                horizontalAlignment = Alignment.Start,
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    if (props.unitPosition == "left") BasicText(props.unit, style = TextStyle(color = Color.Red, fontSize = 12.sp))
                    BasicText(props.amount.upStringValueOrEmpty(), style = TextStyle(color = Color.Red, fontSize = 28.sp, fontWeight = FontWeight.Bold))
                    if (props.unitPosition == "right") BasicText(props.unit, style = TextStyle(color = Color.Red, fontSize = 12.sp))
                }
                if (props.limit.isNotEmpty()) BasicText(props.limit, style = TextStyle(color = textColor, fontSize = 12.sp))
            }

            // Info block.
            Column(
                modifier = Modifier.weight(1f).padding(start = 15.dp).upTestTag("coupon-info"),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                BasicText(props.title, style = TextStyle(color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold))
                if (props.desc.isNotEmpty()) BasicText(props.desc, style = TextStyle(color = textColor, fontSize = 12.sp))
                if (props.time.isNotEmpty()) BasicText(props.time, style = TextStyle(color = textColor, fontSize = 10.sp))
            }

            // Action tag.
            Box(Modifier.upTestTag("coupon-action")) {
                UPTag(
                    props = UPTagProps(
                        text = props.actionText,
                        type = "error",
                        size = "medium",
                        shape = "circle",
                        plain = props.type.isNotEmpty(),
                    ),
                    diagnostics = diagnostics,
                )
            }
        }
    }
}
