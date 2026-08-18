package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val NoticeBarComponentName: String = "UPNoticeBar"
private val NoticeBarDirections: Set<String> = setOf("row", "column")
private val NoticeBarModes: Set<String> = setOf("", "link", "closable")
private val NoticeBarJustifyContent: Set<String> = setOf(
    "flex-start",
    "center",
    "flex-end",
    "space-between",
    "space-around",
)

private fun noticeTextItems(value: UPRawValue): List<String> = when (value) {
    is List<*> -> value.map { it.upStringValueOrEmpty() }
    is Array<*> -> value.map { it.upStringValueOrEmpty() }
    null -> emptyList()
    else -> listOf(value.upStringValueOrEmpty())
}.filter(String::isNotEmpty)

private fun noticeContentAlignment(justifyContent: String): Alignment = when (justifyContent) {
    "center" -> Alignment.Center
    "flex-end" -> Alignment.CenterEnd
    else -> Alignment.CenterStart
}

/** Native Compose counterpart of uview-plus `u-notice-bar`. */
@Composable
public fun UPNoticeBar(
    props: UPNoticeBarProps = UPNoticeBarProps(),
    modifier: Modifier = Modifier,
    onClick: ((Int) -> Unit)? = null,
    onItemClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    var visible by remember { mutableStateOf(true) }
    if (!visible) return

    val direction = upSafeEnum(
        props.direction,
        NoticeBarDirections,
        "row",
        diagnostics,
        NoticeBarComponentName,
        "direction",
    )
    val mode = upSafeEnum(
        props.mode,
        NoticeBarModes,
        "",
        diagnostics,
        NoticeBarComponentName,
        "mode",
    )
    val justifyContent = upSafeEnum(
        props.justifyContent,
        NoticeBarJustifyContent,
        "flex-start",
        diagnostics,
        NoticeBarComponentName,
        "justifyContent",
    )
    val messages = noticeTextItems(props.text)
    val usesColumnNotice = direction == "column" || props.step
    val displayText = if (usesColumnNotice) {
        messages.firstOrNull().orEmpty()
    } else {
        messages.joinToString(separator = "  ")
    }
    val textColor = UPColor.parse(props.color, UPTheme.Warning)
    val background = UPColor.parse(props.bgColor, Color(0xFFFDF6EC))
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, NoticeBarComponentName)

    fun clickNotice() {
        onClick?.invoke(0)
        onItemClick?.invoke()
    }

    fun closeNotice() {
        if (!visible) return
        visible = false
        onClose?.invoke()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .applyUPResolvedStyle(style)
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .upTestTag("notice-bar")
            .upClickable(onClick = ::clickNotice),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (props.icon.isNotEmpty()) {
            UPIcon(
                props = UPIconProps(name = props.icon, color = props.color, size = 19),
                diagnostics = diagnostics,
            )
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = noticeContentAlignment(justifyContent),
        ) {
            BasicText(
                text = displayText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(color = textColor, fontSize = props.fontSize.upTextUnitOr(14.sp)),
            )
        }
        when (mode) {
            "link" -> UPIcon(
                props = UPIconProps(name = "arrow-right", color = props.color, size = 17),
                diagnostics = diagnostics,
            )

            "closable" -> UPIcon(
                props = UPIconProps(name = "close", color = props.color, size = 16),
                modifier = Modifier.upTestTag("notice-bar-close"),
                onClick = { closeNotice() },
                diagnostics = diagnostics,
            )
        }
    }
}

/** Direct argument form for generated source. */
@Composable
public fun UPNoticeBar(
    text: UPRawValue,
    direction: String = "row",
    mode: String = "",
    onClick: ((Int) -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPNoticeBar(
        props = UPNoticeBarProps(text = text, direction = direction, mode = mode),
        modifier = modifier,
        onClick = onClick,
        onClose = onClose,
        diagnostics = diagnostics,
    )
}
