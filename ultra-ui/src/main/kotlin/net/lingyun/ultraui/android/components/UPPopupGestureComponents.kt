package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag
import androidx.compose.foundation.combinedClickable

@Composable
public fun UPPopover(props: UPPopoverProps = UPPopoverProps(), modifier: Modifier = Modifier, onUpdateShow: ((Boolean) -> Unit)? = null, onOpen: (() -> Unit)? = null, onClose: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: (@Composable () -> Unit)? = null) {
    var visible by remember { mutableStateOf(props.show) }
    val trigger = content ?: { BasicText(props.text.toString()) }
    Column(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPPopover")).upTestTag("popover")) {
        Box(Modifier.upTestTag("popover-trigger").combinedClickable(onClick = { visible = !visible; onUpdateShow?.invoke(visible); if (visible) onOpen?.invoke() else onClose?.invoke() }, onLongClick = { visible = true; onUpdateShow?.invoke(true); onOpen?.invoke() })) { trigger() }
        if (visible) Box(Modifier.fillMaxWidth().background(UPColor.parse(props.popupBgColor, Color(0xFFF7F7F7)), RoundedCornerShape(4.dp)).padding(10.dp).upTestTag("popover-content")) { BasicText(props.text.toString(), style = TextStyle(color = UPColor.parse(props.color, UPTheme.Main))) }
    }
}

@Composable
public fun UPTooltip(props: UPTooltipProps = UPTooltipProps(), modifier: Modifier = Modifier, onUpdateShow: ((Boolean) -> Unit)? = null, onOpen: (() -> Unit)? = null, onClose: (() -> Unit)? = null, onCopy: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: (@Composable () -> Unit)? = null) {
    var visible by remember { mutableStateOf(props.show) }
    Column(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTooltip")).upTestTag("tooltip")) {
        Box(Modifier.upTestTag("tooltip-trigger").combinedClickable(onClick = { visible = !visible; onUpdateShow?.invoke(visible); if (visible) onOpen?.invoke() else onClose?.invoke() }, onLongClick = { visible = true; onUpdateShow?.invoke(true); onOpen?.invoke() })) { content?.invoke() ?: BasicText(props.text.toString()) }
        if (visible) Row(Modifier.background(UPColor.parse(props.popupBgColor, Color.White), RoundedCornerShape(4.dp)).padding(8.dp).upTestTag("tooltip-content"), horizontalArrangement = Arrangement.spacedBy(8.dp)) { BasicText(props.text.toString(), style = TextStyle(color = UPColor.parse(props.color, UPTheme.Content), fontSize = props.size.toString().toFloatOrNull()?.sp ?: 14.sp)); if (props.showCopy) BasicText("复制", modifier = Modifier.upClickable(onClick = { onCopy?.invoke() })) }
    }
}

@Composable
public fun UPSticky(props: UPStickyProps = UPStickyProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) { Box(modifier.background(UPColor.parse(props.bgColor, Color.Transparent)).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSticky")).upTestTag("sticky")) { content() } }

@Composable
public fun UPSwipeAction(props: UPSwipeActionProps = UPSwipeActionProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) { Column(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSwipeAction")).upTestTag("swipe-action")) { content() } }

@Composable
public fun UPSwipeActionItem(props: UPSwipeActionItemProps = UPSwipeActionItemProps(), modifier: Modifier = Modifier, onClick: ((UPRawValue, Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) {
    Row(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSwipeActionItem")).upTestTag("swipe-action-item"), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f)) { content() }
        if (props.show) props.options.forEachIndexed { index, option -> Box(Modifier.background(UPTheme.Error).upClickable(enabled = !props.disabled, onClick = { onClick?.invoke(option, index) }).padding(14.dp).upTestTag("swipe-action-option-$index")) { BasicText(actionOrOptionText(option, "text", option.toString()), style = TextStyle(color = Color.White)) } }
    }
}
