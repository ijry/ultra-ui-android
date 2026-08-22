package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

@Composable
public fun UPDatetimePicker(
    props: UPDatetimePickerProps = UPDatetimePickerProps(),
    modifier: Modifier = Modifier,
    onUpdateValue: ((UPRawValue) -> Unit)? = null,
    onUpdateModelValue: ((UPRawValue) -> Unit)? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onChange: ((UPDatetimePickerEvent) -> Unit)? = null,
    onConfirm: ((UPDatetimePickerEvent) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show && !props.pageInline) return
    var selection by remember(props.value, props.modelValue, props.mode) { mutableStateOf(resolveDatetimeSelection(props)) }
    LaunchedEffect(props.value, props.modelValue, props.mode) { selection = resolveDatetimeSelection(props) }
    val columns = datetimeColumns(props, selection)

    Column(modifier.fillMaxWidth().background(Color.White).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPDatetimePicker")).upTestTag("datetime-picker")) {
        if (props.showToolbar) {
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                BasicText(props.cancelText, modifier = Modifier.upClickable(enabled = !props.disabled) { onCancel?.invoke(); onUpdateShow?.invoke(false); onClose?.invoke() }.upTestTag("datetime-picker-cancel"), style = TextStyle(color = UPColor.parse(props.cancelColor, UPTheme.Tips)))
                BasicText(props.title, style = TextStyle(color = UPTheme.Main))
                BasicText(props.confirmText, modifier = Modifier.upClickable(enabled = !props.disabled) {
                    val event = datetimeEvent(props, selection, columns)
                    onUpdateValue?.invoke(event.value); onUpdateModelValue?.invoke(event.value); onConfirm?.invoke(event); onUpdateShow?.invoke(false); onClose?.invoke()
                }.upTestTag("datetime-picker-confirm"), style = TextStyle(color = UPColor.parse(props.confirmColor, UPTheme.Primary)))
            }
        }
        Row(Modifier.fillMaxWidth()) {
            // uview renders each column as a wheel that is `visibleItemCount * itemHeight`
            // tall and scrolls internally. Laying every option out flat instead pushes the
            // off-screen ones to a zero-height box, where they cannot be tapped at all.
            val itemHeight = upRawDp(props.itemHeight, 44.dp).coerceAtLeast(16.dp)
            val visibleCount = props.visibleItemCount.upIntOrDefault(5).coerceIn(1, 20)
            columns.forEachIndexed { columnIndex, options ->
                Column(
                    Modifier.weight(1f)
                        .height(itemHeight * visibleCount)
                        .verticalScroll(rememberScrollState())
                        .upTestTag("datetime-picker-column-$columnIndex"),
                ) {
                    options.forEach { option ->
                        val selected = selection.values.getOrNull(datetimeValueIndex(props.mode, columnIndex)) == option
                        // uview sets lineHeight = itemHeight so each label sits centred.
                        Box(
                            Modifier.fillMaxWidth().height(itemHeight)
                                .background(if (selected) Color(0xFFEAF3FF) else Color.Transparent)
                                .upClickable(enabled = !props.disabled) {
                                    selection = updateDatetimeSelection(props, selection, columnIndex, option)
                                    onChange?.invoke(datetimeEvent(props, selection, datetimeColumns(props, selection)))
                                }
                                .padding(horizontal = 10.dp)
                                .upTestTag("datetime-picker-option-$columnIndex-$option"),
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicText(
                                option.toString().padStart(2, '0'),
                                style = TextStyle(color = if (selected) UPTheme.Primary else UPTheme.Main, fontSize = 14.sp),
                            )
                        }
                    }
                }
            }
        }
    }
}
