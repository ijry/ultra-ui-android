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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
        if (props.showHead && (head != null || props.title.isNotEmpty() || props.subTitle.isNotEmpty() || props.thumb.isNotEmpty())) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (props.headBorderBottom) Modifier.border(0.5.dp, UPTheme.Border) else Modifier)
                    .applyUPResolvedStyle(headStyle)
                    .padding(horizontal = headerPadding, vertical = headerPadding),
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
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        if (props.title.isNotEmpty()) {
                            BasicText(
                                props.title,
                                style = TextStyle(color = titleColor, fontSize = titleSize, fontWeight = FontWeight.Medium),
                            )
                        }
                    }
                    if (props.subTitle.isNotEmpty()) {
                        BasicText(props.subTitle, style = TextStyle(color = subTitleColor, fontSize = subTitleSize))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .applyUPResolvedStyle(bodyStyle)
                .padding(horizontal = bodyPadding, vertical = bodyPadding),
        ) {
            Column(content = content)
        }

        if (props.showFoot && foot != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (props.footBorderTop) Modifier.border(0.5.dp, UPTheme.Border) else Modifier)
                    .applyUPResolvedStyle(footStyle)
                    .padding(horizontal = footerPadding, vertical = footerPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                foot()
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
