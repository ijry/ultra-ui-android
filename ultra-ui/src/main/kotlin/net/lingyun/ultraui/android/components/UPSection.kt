package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

/**
 * Native Compose rendering of uview-plus `u-section` (reconstructed from `section.js`; this snapshot
 * has no upstream `.vue`).
 *
 * Renders a section header row: an optional left accent bar ([`UPSectionProps.showLine`]/[lineColor],
 * empty falls back to the theme primary), the [title] styled by [color]/[fontSize]/[bold], and — when
 * [right] — a trailing area with [subTitle] in [subColor] plus an [arrow] icon that invokes [onClick]
 * (the right-click).
 */
@Composable
public fun UPSection(
    props: UPSectionProps = UPSectionProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSection")
    val titleColor = UPColor.parse(props.color, UPTheme.Main)
    val subColor = UPColor.parse(props.subColor, UPTheme.Tips)
    val lineColor = if (props.lineColor.isEmpty()) UPTheme.Primary else UPColor.parse(props.lineColor, UPTheme.Primary)
    val titleSize = props.fontSize.upTextUnitOr(15.sp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .applyUPResolvedStyle(style)
            .padding(vertical = 8.dp)
            .upTestTag("section"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (props.showLine) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(4.dp)
                    .height(14.dp)
                    .background(lineColor)
                    .upTestTag("section-line"),
            )
        }
        BasicText(
            text = props.title,
            style = TextStyle(color = titleColor, fontSize = titleSize, fontWeight = if (props.bold) FontWeight.Bold else FontWeight.Normal),
            modifier = Modifier.weight(1f),
        )
        if (props.right) {
            Row(
                modifier = Modifier
                    .upClickable(onClick = { onClick?.invoke() })
                    .upTestTag("section-right"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                BasicText(text = props.subTitle, style = TextStyle(color = subColor, fontSize = 13.sp))
                if (props.arrow) {
                    UPIcon(props = UPIconProps(name = "arrow-right", color = props.subColor, size = "14px"))
                }
            }
        }
    }
}
