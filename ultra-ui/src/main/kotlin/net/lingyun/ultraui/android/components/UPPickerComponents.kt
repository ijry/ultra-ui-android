package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
public fun UPPicker(props: UPPickerProps = UPPickerProps(), modifier: Modifier = Modifier, onUpdateModelValue: ((List<UPRawValue>) -> Unit)? = null, onUpdateShow: ((Boolean) -> Unit)? = null, onChange: ((UPPickerEvent) -> Unit)? = null, onConfirm: ((UPPickerEvent) -> Unit)? = null, onCancel: (() -> Unit)? = null, onClose: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    if (!props.show) return
    val selected = remember(props) { mutableStateListOf<Int>().apply { addAll(resolvePickerIndexes(props)) } }
    Column(modifier.fillMaxWidth().background(UPColor.parse(props.bgColor, Color.White)).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPPicker")).upTestTag("picker")) {
        if (props.showToolbar) Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            BasicText(props.cancelText, modifier = Modifier.upClickable(onClick = { onCancel?.invoke(); onUpdateShow?.invoke(false); onClose?.invoke() }), style = TextStyle(color = UPColor.parse(props.cancelColor, UPTheme.Tips)))
            BasicText(props.title, style = TextStyle(color = UPTheme.Main))
            BasicText(props.confirmText, modifier = Modifier.upClickable(onClick = { val event = pickerEvent(props, selected); onConfirm?.invoke(event); onUpdateModelValue?.invoke(pickerModelValues(props, selected)); onUpdateShow?.invoke(false); onClose?.invoke() }), style = TextStyle(color = UPColor.parse(props.confirmColor, UPTheme.Primary)))
        }
        props.columns.forEachIndexed { columnIndex, rawColumn ->
            val column = rawColumn.upItemsOrEmpty()
            Column(Modifier.fillMaxWidth().upTestTag("picker-column-$columnIndex")) {
                column.forEachIndexed { optionIndex, option ->
                    val label = actionOrOptionText(option, props.keyName, option.toString())
                    BasicText(label, modifier = Modifier.fillMaxWidth().background(if (selected.getOrElse(columnIndex) { 0 } == optionIndex) Color(0xFFEAF3FF) else Color.Transparent).upClickable(onClick = { selected[columnIndex] = optionIndex; if (props.immediateChange) onChange?.invoke(pickerEvent(props, selected, columnIndex, optionIndex)) }).padding(12.dp), style = TextStyle(color = UPTheme.Main, fontSize = 14.sp))
                }
            }
        }
    }
}

@Composable
public fun UPPickerColumn(props: UPPickerColumnProps = UPPickerColumnProps(), modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) { Box(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, UPCompatibilityDiagnostics.None, "UPPickerColumn")).upTestTag("picker-column")) { content() } }

internal fun pickerEvent(
    props: UPPickerProps,
    indexes: List<Int>,
    columnIndex: Int = 0,
    index: Int = indexes.getOrElse(columnIndex) { 0 },
): UPPickerEvent {
    val values = props.columns.mapIndexed { columnIndex, rawColumn -> rawColumn.upItemsOrEmpty().getOrNull(indexes.getOrElse(columnIndex) { 0 }) ?: "" }
    return UPPickerEvent(
        value = values,
        indexs = indexes.toList(),
        index = index,
        values = props.columns,
        columnIndex = columnIndex,
    )
}

internal fun resolvePickerIndexes(props: UPPickerProps): List<Int> {
    val controlledValues = props.modelValue.ifEmpty { props.value }
    return props.columns.mapIndexed { columnIndex, rawColumn ->
        val options = rawColumn.upItemsOrEmpty()
        val controlledValue = controlledValues.getOrNull(columnIndex)
        val controlledIndex = controlledValue?.let { value ->
            options.indexOfFirst { option ->
                val comparable = option.upStringKeyMapOrEmpty()[props.valueName] ?: option
                comparable.upLooseEquals(value)
            }.takeIf { it >= 0 }
        }
        val fallbackIndex = props.defaultIndex.getOrNull(columnIndex).upIntOrDefault(0)
        (controlledIndex ?: fallbackIndex).coerceIn(0, (options.size - 1).coerceAtLeast(0))
    }
}

internal fun pickerModelValues(props: UPPickerProps, indexes: List<Int>): List<UPRawValue> =
    props.columns.mapIndexed { columnIndex, rawColumn ->
        val option = rawColumn.upItemsOrEmpty().getOrNull(indexes.getOrElse(columnIndex) { 0 })
        option.upStringKeyMapOrEmpty()[props.valueName] ?: option
    }

@Composable
public fun UPPagination(props: UPPaginationProps = UPPaginationProps(), modifier: Modifier = Modifier, onUpdateCurrentPage: ((Int) -> Unit)? = null, onCurrentChange: ((Int) -> Unit)? = null, onUpdatePageSize: ((Int) -> Unit)? = null, onSizeChange: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    val size = props.pageSize.upIntOrDefault(10).coerceAtLeast(1)
    val total = props.total.upIntOrDefault(0).coerceAtLeast(0)
    val pages = ((total + size - 1) / size).coerceAtLeast(1)
    var current by remember(props) { mutableIntStateOf(props.currentPage.upIntOrDefault(1).coerceIn(1, pages)) }
    Row(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPPagination")).upTestTag("pagination"), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        if (props.layout.contains("prev")) BasicText(props.prevText.ifEmpty { "上一页" }, modifier = Modifier.upClickable(enabled = current > 1, onClick = { current--; onUpdateCurrentPage?.invoke(current); onCurrentChange?.invoke(current) }).padding(8.dp))
        if (props.layout.contains("pager")) (1..pages.coerceAtMost(7)).forEach { page -> BasicText(page.toString(), modifier = Modifier.background(if (page == current) UPTheme.Primary else Color.Transparent).upClickable(onClick = { current = page; onUpdateCurrentPage?.invoke(page); onCurrentChange?.invoke(page) }).padding(8.dp), style = TextStyle(color = if (page == current) Color.White else UPTheme.Main)) }
        if (props.layout.contains("next")) BasicText(props.nextText.ifEmpty { "下一页" }, modifier = Modifier.upClickable(enabled = current < pages, onClick = { current++; onUpdateCurrentPage?.invoke(current); onCurrentChange?.invoke(current) }).padding(8.dp))
    }
}

@Composable
public fun UPSelect(props: UPSelectProps = UPSelectProps(), modifier: Modifier = Modifier, onUpdateCurrent: ((UPRawValue) -> Unit)? = null, onSelect: ((UPRawValue) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    var open by remember { mutableStateOf(false) }
    var current by remember(props) { mutableStateOf(props.current) }
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSelect")).upTestTag("select")) {
        BasicText(actionOrOptionText(props.options.firstOrNull { it.upStringKeyMapOrEmpty()[props.keyName].upLooseEquals(current) }, props.labelName, props.label).ifEmpty { props.label }, modifier = Modifier.fillMaxWidth().upClickable(enabled = !props.disabled, onClick = { open = !open }).padding(12.dp).upTestTag("select-trigger"))
        if (open) props.options.forEach { option -> BasicText(actionOrOptionText(option, props.labelName), modifier = Modifier.fillMaxWidth().upClickable(onClick = { val value = option.upStringKeyMapOrEmpty()[props.keyName]; current = value; open = false; onUpdateCurrent?.invoke(value); onSelect?.invoke(option) }).padding(12.dp).upTestTag("select-option")) }
    }
}
