package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val CardComponentName = "UPCard"

/**
 * Native Compose counterpart of uview-plus `u-card`.
 *
 * The trailing slot is the card body. Header and footer slots are optional and
 * are kept as plain Compose lambdas so a generated Kotlin tree can provide
 * arbitrary native content without a JSON or view-runtime bridge.
 */
@Composable
public fun UPCard(
    props: UPCardProps = UPCardProps(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
    /** `head-click` / `body-click` / `foot-click`; each carries `index`, like `click`. */
    onHeadClick: ((UPRawValue) -> Unit)? = null,
    onBodyClick: ((UPRawValue) -> Unit)? = null,
    onFootClick: ((UPRawValue) -> Unit)? = null,
    head: (@Composable () -> Unit)? = null,
    foot: (@Composable () -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, CardComponentName)
    val headStyle = rememberUPResolvedStyle(props.headStyle, diagnostics, "$CardComponentName.headStyle")
    val bodyStyle = rememberUPResolvedStyle(props.bodyStyle, diagnostics, "$CardComponentName.bodyStyle")
    val footStyle = rememberUPResolvedStyle(props.footStyle, diagnostics, "$CardComponentName.footStyle")
    val radius = upRawDp(props.radius ?: props.borderRadius, 8.dp).coerceAtLeast(0.dp)
    val shape = RoundedCornerShape(radius)
    val margin = if (props.full) 0.dp else upRawDp(props.margin, 15.dp).coerceAtLeast(0.dp)
    val basePadding = upRawDp(props.padding, 15.dp).coerceAtLeast(0.dp)
    val headerPadding = upRawDp(props.paddingHead, basePadding).coerceAtLeast(0.dp)
    val bodyPadding = upRawDp(props.paddingBody, basePadding).coerceAtLeast(0.dp)
    val footerPadding = upRawDp(props.paddingFoot, basePadding).coerceAtLeast(0.dp)
    val shadowName = props.shadow ?: props.boxShadow
    val titleSize = props.titleSize.upTextUnitOr(15.sp)
    val subTitleSize = props.subTitleSize.upTextUnitOr(13.sp)
    val titleColor = UPColor.parse(props.titleColor, UPTheme.Main)
    val subTitleColor = UPColor.parse(props.subTitleColor, UPTheme.Tips)
    val clickModifier = if (onClick != null) {
        Modifier.upClickable(onClick = { onClick(props.index) })
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = margin)
            .then(
                if (shadowName.isNotBlank() && !shadowName.equals("none", ignoreCase = true)) {
                    Modifier.shadow(2.dp, shape)
                } else {
                    Modifier
                },
            )
            .background(Color.White, shape)
            .then(if (props.border) Modifier.border(0.5.dp, UPTheme.Border, shape) else Modifier)
            .applyUPResolvedStyle(style)
            .then(clickModifier)
            .upTestTag("card"),
    ) {
        // `v-if="showHead"` alone: the header exists whenever the flag is set, even with
        // nothing in it, and `.u-border-bottom` is a single hairline rather than a box.
        if (props.showHead) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (props.headBorderBottom) Modifier.upCardHairline(top = false) else Modifier)
                    .applyUPResolvedStyle(headStyle)
                    .then(if (onHeadClick == null) Modifier else Modifier.upClickable(onClick = { onHeadClick(props.index) }))
                    .padding(horizontal = headerPadding, vertical = headerPadding)
                    .upTestTag("card-head"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (head != null) {
                    head()
                } else {
                    if (props.thumb.isNotEmpty()) {
                        UPImage(
                            props = UPImageProps(
                                src = props.thumb,
                                width = props.thumbWidth,
                                height = props.thumbWidth,
                                shape = if (props.thumbCircle) "circle" else "square",
                                showError = false,
                            ),
                            diagnostics = diagnostics,
                        )
                    }
                    // `.u-flex-between` splits the row: title on the left, subtitle right.
                    Box(modifier = Modifier.weight(1f)) {
                        if (props.title.isNotEmpty()) {
                            BasicText(
                                props.title,
                                // `.u-line-1`: `white-space: nowrap` + `text-overflow: ellipsis`.
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.upTestTag("card-title"),
                                style = TextStyle(color = titleColor, fontSize = titleSize, fontWeight = FontWeight.Medium),
                            )
                        }
                    }
                    if (props.subTitle.isNotEmpty()) {
                        BasicText(
                            props.subTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.upTestTag("card-subtitle"),
                            style = TextStyle(color = subTitleColor, fontSize = subTitleSize),
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .applyUPResolvedStyle(bodyStyle)
                .then(if (onBodyClick == null) Modifier else Modifier.upClickable(onClick = { onBodyClick(props.index) }))
                .padding(horizontal = bodyPadding, vertical = bodyPadding)
                .upTestTag("card-body"),
        ) {
            Column(content = content)
        }

        // `:style="[{padding: $slots.foot ? addUnit(paddingFoot || padding) : 0}]"`: the
        // footer is present whenever `showFoot` is set, but an empty one takes no padding.
        if (props.showFoot) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (props.footBorderTop) Modifier.upCardHairline(top = true) else Modifier)
                    .applyUPResolvedStyle(footStyle)
                    .then(if (onFootClick == null) Modifier else Modifier.upClickable(onClick = { onFootClick(props.index) }))
                    .then(if (foot == null) Modifier else Modifier.padding(horizontal = footerPadding, vertical = footerPadding))
                    .upTestTag("card-foot"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                foot?.invoke()
            }
        }
    }
}

/** Direct argument form for generated sources that only provide a title and body. */
@Composable
public fun UPCard(
    title: String = "",
    subTitle: String = "",
    index: UPRawValue = "",
    onClick: ((UPRawValue) -> Unit)? = null,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    UPCard(
        props = UPCardProps(title = title, subTitle = subTitle, index = index),
        modifier = modifier,
        onClick = onClick,
        diagnostics = diagnostics,
        content = content,
    )
}

/** `.u-border-top` / `.u-border-bottom`: one 0.5px hairline, not a box outline. */
private fun Modifier.upCardHairline(top: Boolean): Modifier = drawBehind {
    val stroke = 0.5.dp.toPx()
    val y = if (top) stroke / 2f else size.height - stroke / 2f
    drawLine(UPTheme.Border, Offset(0f, y), Offset(size.width, y), strokeWidth = stroke)
}
