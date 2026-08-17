package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPCompatibilityEvent
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/** Native clickable text counterpart of uview-plus `u-link`. */
@Composable
public fun UPLink(
    props: UPLinkProps = UPLinkProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onOpen: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPLink")
    LaunchedEffect(props.mpTips, diagnostics) {
        if (props.mpTips.isNotEmpty()) {
            diagnostics.report(
                UPCompatibilityEvent(
                    component = "UPLink",
                    property = "mpTips",
                    value = props.mpTips,
                    reason = "Mini-program clipboard tip is not used by native Android links.",
                ),
            )
        }
    }
    val color = style.color ?: UPColor.parse(props.color, net.lingyun.ultraui.android.core.UPTheme.Primary)
    val lineColor = if (props.lineColor.isEmpty()) color else UPColor.parse(props.lineColor, color)
    val textStyle = TextStyle(
        color = color,
        fontSize = (style.fontSize ?: upRawDp(props.fontSize, 15.dp)).value.let { androidx.compose.ui.unit.TextUnit(it, androidx.compose.ui.unit.TextUnitType.Sp) },
        textDecoration = if (props.underLine) TextDecoration.Underline else TextDecoration.None,
    )
    BasicText(
        text = upRawText(props.text),
        style = textStyle.copy(color = lineColor.takeIf { props.underLine } ?: color),
        modifier = modifier
            .applyUPResolvedStyle(style)
            .upTestTag("link")
            .upClickable(enabled = onClick != null || onOpen != null) {
                onClick?.invoke()
                if (props.href.isNotEmpty()) onOpen?.invoke(props.href)
            },
    )
}

/** Direct argument form for generated Android source. */
@Composable
public fun UPLink(
    text: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.link.text,
    color: String = net.lingyun.ultraui.android.core.UPConfig.link.color,
    fontSize: UPRawValue = net.lingyun.ultraui.android.core.UPConfig.link.fontSize,
    underLine: Boolean = net.lingyun.ultraui.android.core.UPConfig.link.underLine,
    href: String = net.lingyun.ultraui.android.core.UPConfig.link.href,
    mpTips: String = net.lingyun.ultraui.android.core.UPConfig.link.mpTips,
    lineColor: String = net.lingyun.ultraui.android.core.UPConfig.link.lineColor,
    customStyle: net.lingyun.ultraui.android.core.UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onOpen: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPLink(
        props = UPLinkProps(color, fontSize, underLine, href, mpTips, lineColor, text, customStyle),
        modifier = modifier,
        onClick = onClick,
        onOpen = onOpen,
        diagnostics = diagnostics,
    )
}
