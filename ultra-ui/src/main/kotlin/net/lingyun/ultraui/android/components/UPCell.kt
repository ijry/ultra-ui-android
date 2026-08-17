package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val CellComponentName = "UPCell"

/** Native Compose counterpart of uview-plus `u-cell`. */
@Composable
public fun UPCell(
    props: UPCellProps = UPCellProps(),
    modifier: Modifier = Modifier,
    onClick: ((net.lingyun.ultraui.android.core.UPRawValue) -> Unit)? = null,
    left: (@Composable RowScope.() -> Unit)? = null,
    right: (@Composable RowScope.() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val arrow = upSafeEnum(
        props.arrowDirection,
        setOf("", "up", "down", "left", "right"),
        "",
        diagnostics,
        CellComponentName,
        "arrowDirection",
    )
    val clickable = !props.disabled && (props.clickable || props.isLink || onClick != null)
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, CellComponentName)
    val titleStyle = rememberUPResolvedStyle(props.titleStyle, diagnostics, "$CellComponentName.titleStyle")
    val iconColor = UPColor.parse("#909399", UPTheme.Tips)
    val title = upRawText(props.title)
    val label = upRawText(props.label)
    val value = upRawText(props.value)
    val root = modifier
        .fillMaxWidth()
        .background(Color.White)
        .applyUPResolvedStyle(style)
        .then(if (props.border) Modifier.border(0.5.dp, UPTheme.Border) else Modifier)
        .upTestTag("cell")
        .upClickable(enabled = clickable, onClick = { onClick?.invoke(props.name) })

    Row(
        modifier = root.padding(horizontal = 16.dp, vertical = if (props.size == "large") 16.dp else 12.dp),
        verticalAlignment = if (props.center) Alignment.CenterVertically else Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (left != null) {
            Row(content = left)
        } else if (props.icon.isNotEmpty()) {
            UPIcon(UPIconProps(name = props.icon, color = iconColor.toHexString(), size = 20))
        }
        if (props.required) {
            BasicText("*", style = TextStyle(color = UPTheme.Error, fontSize = 15.sp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            BasicText(
                title,
                modifier = Modifier.applyUPResolvedStyle(titleStyle),
                style = TextStyle(color = UPTheme.Main, fontSize = 15.sp, fontWeight = FontWeight.Normal),
            )
            if (label.isNotEmpty()) {
                BasicText(label, style = TextStyle(color = UPTheme.Tips, fontSize = 12.sp))
            }
        }
        if (right != null) {
            Row(content = right)
        } else {
            if (value.isNotEmpty()) {
                BasicText(value, style = TextStyle(color = UPTheme.Content, fontSize = 14.sp))
            }
            if (props.isLink || arrow.isNotEmpty()) {
                val iconName = if (arrow.isEmpty()) "arrow-right" else "arrow-$arrow"
                UPIcon(UPIconProps(name = iconName, color = UPTheme.Tips.toHexString(), size = 18))
            } else if (props.rightIcon.isNotEmpty()) {
                UPIcon(UPIconProps(name = props.rightIcon, color = UPTheme.Tips.toHexString(), size = 18))
            }
        }
    }
}

private fun Color.toHexString(): String = "#%08X".format(toArgb())
