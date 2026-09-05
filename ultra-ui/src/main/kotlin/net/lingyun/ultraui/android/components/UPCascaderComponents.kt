package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val CascaderComponentName: String = "UPCascader"

/** `headerDirection`: a row of tabs, or a vertical `u-steps` list for long labels. */
internal val UPCascaderHeaderDirections: Set<String> = setOf("row", "column")

@Composable
public fun UPCascader(
    props: UPCascaderProps = UPCascaderProps(),
    modifier: Modifier = Modifier,
    onUpdateModelValue: ((List<UPRawValue>) -> Unit)? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onChange: ((UPCascaderEvent) -> Unit)? = null,
    onConfirm: ((UPCascaderEvent) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return
    var path by remember(props.modelValue, props.data) { mutableStateOf(props.modelValue) }
    LaunchedEffect(props.modelValue) { path = props.modelValue }
    val columnCount = (path.size + 1)
        .coerceAtMost(props.optionsCols.rawInt(2).coerceAtLeast(1))
        .coerceAtLeast(1)
    // `headerDirection === 'column'` swaps the horizontal tab strip for a vertical
    // `u-steps` list, which suits long labels.
    val headerDirection = upSafeEnum(
        props.headerDirection,
        UPCascaderHeaderDirections,
        "row",
        diagnostics,
        CascaderComponentName,
        "headerDirection",
    )
    // `:maskCloseAble` on the popup; a tap outside dismisses without confirming.
    val dismissOnMask = props.maskCloseAble && props.closeOnClickOverlay

    Column(
        modifier.fillMaxWidth().background(Color.White)
            .then(
                if (!dismissOnMask) {
                    Modifier
                } else {
                    Modifier.upClickable(enabled = true) {
                        onCancel?.invoke()
                        onUpdateShow?.invoke(false)
                    }
                },
            )
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCascader"))
            .upTestTag("cascader"),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText("取消", modifier = Modifier.upClickable { onCancel?.invoke(); onUpdateShow?.invoke(false) }.upTestTag("cascader-cancel"), style = TextStyle(color = UPTheme.Tips))
            BasicText(props.title, style = TextStyle(color = UPTheme.Main, fontWeight = FontWeight.Bold))
            BasicText("确认", modifier = Modifier.upClickable(enabled = path.isNotEmpty()) {
                val event = cascaderEventForPath(props, path)
                onConfirm?.invoke(event)
                onUpdateModelValue?.invoke(path)
                onUpdateShow?.invoke(false)
            }.upTestTag("cascader-confirm"), style = TextStyle(color = UPTheme.Primary))
        }
        // `headerDirection="column"` stacks the levels down the sheet instead of side by side.
        UPCascaderLevels(vertical = headerDirection == "column") {
            repeat(columnCount) { level ->
                val options = cascaderOptionsAt(props, path, level)
                Column(
                    Modifier
                        .then(if (headerDirection == "column") Modifier.fillMaxWidth() else Modifier.weight(1f))
                        .upTestTag("cascader-column-$level"),
                ) {
                    options.forEachIndexed { optionIndex, option ->
                        val map = option.rawMap()
                        val selected = map[props.valueKey].rawEquals(path.getOrNull(level))
                        BasicText(
                            text = map[props.labelKey]?.toString().orEmpty(),
                            modifier = Modifier.fillMaxWidth()
                                .background(if (selected) Color(0xFFEAF3FF) else Color.Transparent)
                                .upClickable {
                                    val event = updateCascaderSelection(props, path, level, option)
                                    path = event.value
                                    onUpdateModelValue?.invoke(path)
                                    onChange?.invoke(event)
                                    val children = option.rawMap()[props.childrenKey].rawList()
                                    if (children.isEmpty() && props.autoClose) {
                                        onConfirm?.invoke(event)
                                        onUpdateShow?.invoke(false)
                                    }
                                }
                                .padding(12.dp)
                                .upTestTag("cascader-option-$level-$optionIndex"),
                            style = TextStyle(color = if (selected) UPTheme.Primary else UPTheme.Main, fontSize = 14.sp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Hosts the level columns on the axis `headerDirection` asks for. `Modifier.weight` only
 * resolves inside the matching scope, so the branch has to wrap the whole list.
 */
@Composable
private fun UPCascaderLevels(vertical: Boolean, content: @Composable UPCascaderLevelScope.() -> Unit) {
    if (vertical) {
        Column(Modifier.fillMaxWidth()) { UPCascaderColumnScope(this).content() }
    } else {
        Row(Modifier.fillMaxWidth()) { UPCascaderRowScope(this).content() }
    }
}

/** Exposes just the `weight` both scopes share, so the level body stays written once. */
internal interface UPCascaderLevelScope {
    fun Modifier.weight(weight: Float): Modifier
}

private class UPCascaderRowScope(private val scope: RowScope) : UPCascaderLevelScope {
    override fun Modifier.weight(weight: Float): Modifier = with(scope) { this@weight.weight(weight) }
}

private class UPCascaderColumnScope(private val scope: ColumnScope) : UPCascaderLevelScope {
    // A vertically stacked level fills the width instead of sharing it.
    override fun Modifier.weight(weight: Float): Modifier = this
}

private fun cascaderEventForPath(props: UPCascaderProps, path: List<UPRawValue>): UPCascaderEvent {
    val options = path.mapIndexedNotNull { level, value ->
        cascaderOptionsAt(props, path, level).firstOrNull { it.rawMap()[props.valueKey].rawEquals(value) }
    }
    return UPCascaderEvent(path, options, (path.size - 1).coerceAtLeast(0))
}
