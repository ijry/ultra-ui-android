package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

private const val DatetimePickerComponentName = "UPDatetimePicker"

/** Popup modes an inline panel can still express through its corner rounding. */
private val DatetimePickerInlinePopupModes = setOf("top", "bottom")

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
    toolbarRight: @Composable () -> Unit = {},
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    // uview keeps the `hasInput` trigger mounted and opens the wheel from its own flag, so the
    // trigger stays on screen while the picker itself is hidden.
    var showByClickInput by remember { mutableStateOf(false) }
    val visible = props.show || props.pageInline || (props.hasInput && showByClickInput)
    if (!props.hasInput && !visible) return
    var selection by remember(props.value, props.modelValue, props.mode) { mutableStateOf(resolveDatetimeSelection(props)) }
    LaunchedEffect(props.value, props.modelValue, props.mode) { selection = resolveDatetimeSelection(props) }
    val columns = datetimeColumns(props, selection)
    val popupMode = upPickerPopupMode(props.popupMode, diagnostics, DatetimePickerComponentName)
    LaunchedEffect(props.maskClass, diagnostics) {
        if (props.maskClass.isNotBlank()) diagnostics.report(DatetimePickerComponentName, "maskClass", props.maskClass, "CSS class hooks have no native Android equivalent; the wheel mask is styled through maskStyle instead.")
    }
    LaunchedEffect(popupMode, diagnostics) {
        if (popupMode !in DatetimePickerInlinePopupModes) diagnostics.report(DatetimePickerComponentName, "popupMode", props.popupMode, "Inline render; sliding in from $popupMode needs a window-level overlay.")
    }
    Column(modifier.fillMaxWidth().upTestTag("datetime-picker-wrapper")) {
        if (props.hasInput) Box(Modifier.fillMaxWidth().upTestTag("datetime-picker-input")) {
            UPInput(
                props = upPickerInputProps(
                    label = upDatetimeInputLabel(props.mode, props.format, resolveUPModelValue(props.modelValue, props.value)),
                    inputBorder = props.inputBorder,
                    placeholder = props.placeholder,
                    disabled = props.disabled,
                    disabledColor = props.disabledColor,
                    inputProps = props.inputProps,
                    diagnostics = diagnostics,
                    component = DatetimePickerComponentName,
                ),
                diagnostics = diagnostics,
            )
            // uview lays a <cover-view> over the readonly field so the tap opens the wheel
            // instead of focusing the input.
            Box(Modifier.matchParentSize().upClickable(enabled = !props.disabled) { showByClickInput = !showByClickInput }.upTestTag("datetime-picker-input-cover"))
        }
        if (!visible) return@Column
        Column(modifier = Modifier.fillMaxWidth().background(Color.White).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, DatetimePickerComponentName)).upTestTag("datetime-picker")) {
            if (props.showToolbar) {
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    BasicText(props.cancelText, modifier = Modifier.upClickable(enabled = !props.disabled) { showByClickInput = false; onCancel?.invoke(); onUpdateShow?.invoke(false); onClose?.invoke() }.upTestTag("datetime-picker-cancel"), style = TextStyle(color = UPColor.parse(props.cancelColor, UPTheme.Tips)))
                    BasicText(props.title, style = TextStyle(color = UPTheme.Main))
                    // `toolbarRightSlot` replaces the confirm label upstream, so the confirm tap
                    // disappears with it and the slot owns that corner.
                    if (upPickerToolbarShowsConfirm(props.toolbarRightSlot)) {
                        BasicText(props.confirmText, modifier = Modifier.upClickable(enabled = !props.disabled) {
                            val event = datetimeEvent(props, selection, columns)
                            showByClickInput = false
                            onUpdateValue?.invoke(event.value); onUpdateModelValue?.invoke(event.value); onConfirm?.invoke(event); onUpdateShow?.invoke(false); onClose?.invoke()
                        }.upTestTag("datetime-picker-confirm"), style = TextStyle(color = UPColor.parse(props.confirmColor, UPTheme.Primary)))
                    } else {
                        Box(Modifier.upTestTag("datetime-picker-toolbar-right")) { toolbarRight() }
                    }
                }
            }
            Row(Modifier.fillMaxWidth()) {
                // uview renders each column as a wheel that is `visibleItemCount * itemHeight`
                // tall and scrolls internally. Laying every option out flat instead pushes the
                // off-screen ones to a zero-height box, where they cannot be tapped at all.
                val itemHeight = upRawDp(props.itemHeight, 44.dp).coerceAtLeast(16.dp)
                val visibleCount = props.visibleItemCount.upIntOrDefault(5).coerceIn(1, 20)
                val maskStyle = rememberUPResolvedStyle(props.maskStyle, diagnostics, "$DatetimePickerComponentName.maskStyle")
                val maskDeclared = upPickerMaskStyleDeclared(props.maskStyle)
                columns.forEachIndexed { columnIndex, options ->
                    Box(Modifier.weight(1f).height(itemHeight * visibleCount)) {
                        Column(
                            Modifier.fillMaxSize()
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
                        // uview hands `mask-style` to picker-view, which paints it over the wheel.
                        // A plain Box takes no pointer input, so the options underneath stay tappable.
                        if (maskDeclared) Box(Modifier.matchParentSize().applyUPResolvedStyle(maskStyle).upTestTag("datetime-picker-mask-$columnIndex"))
                    }
                }
            }
        }
    }
}
