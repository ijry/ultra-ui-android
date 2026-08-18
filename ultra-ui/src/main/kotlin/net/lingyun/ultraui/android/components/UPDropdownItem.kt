package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val DropdownItemComponentName = "UPDropdownItem"

private data class UPDropdownOption(
    val raw: UPRawValue,
    val label: String,
    val value: UPRawValue,
    val disabled: Boolean,
)

private fun resolveOptions(options: List<UPRawValue>): List<UPDropdownOption> = options.map { raw ->
    val map = raw.upStringKeyMapOrEmpty()
    val label = actionOrOptionText(raw, "label").ifEmpty { actionOrOptionText(raw, "name", raw.upStringValueOrEmpty()) }
    UPDropdownOption(
        raw = raw,
        label = label,
        value = map["value"] ?: raw,
        disabled = map["disabled"].upBooleanValue(false),
    )
}

private fun selectedValues(props: UPDropdownItemProps): List<UPRawValue> {
    val value = props.modelValue ?: props.value
    return when (value) {
        is List<*> -> value as List<UPRawValue>
        is Array<*> -> value.toList() as List<UPRawValue>
        null -> emptyList()
        else -> listOf(value)
    }
}

private fun selected(value: UPRawValue, values: List<UPRawValue>): Boolean = values.any { it.upLooseEquals(value) }

/** Native Compose counterpart of uview-plus `u-dropdown-item`. */
@Composable
public fun UPDropdownItem(
    props: UPDropdownItemProps = UPDropdownItemProps(),
    modifier: Modifier = Modifier,
    onUpdateModelValue: ((UPRawValue) -> Unit)? = null,
    onUpdateValue: ((UPRawValue) -> Unit)? = null,
    onChange: ((UPRawValue) -> Unit)? = null,
    onSelect: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable () -> Unit)? = null,
) {
    val context = LocalUPDropdownContext.current
    val index = remember { context?.allocateIndex() ?: 0 }
    val options = remember(props.options) { resolveOptions(props.options) }
    val values = selectedValues(props)
    val isActive = context?.isOpen == true && context.activeIndex == index
    val title = props.title.toString().ifEmpty {
        options.firstOrNull { selected(it.value, values) }?.label.orEmpty()
    }
    val titleColor = if (isActive) {
        net.lingyun.ultraui.android.core.UPColor.parse(context?.activeColor, UPTheme.Primary)
    } else {
        net.lingyun.ultraui.android.core.UPColor.parse(context?.inactiveColor, UPTheme.Content)
    }
    val menuHeight = upRawDp(props.height, 240.dp).coerceAtLeast(48.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .upTestTag("dropdown-item"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .upTestTag("dropdown-title-$index")
                .upClickable(enabled = !props.disabled && context != null, onClick = { context?.toggle(index) })
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            BasicText(
                title,
                modifier = Modifier.weight(1f),
                style = TextStyle(color = if (props.disabled) UPTheme.Light else titleColor, fontSize = 14.sp),
            )
            UPIcon(
                props = UPIconProps(name = if (isActive) "arrow-up" else "arrow-down", color = titleColor.toHex(), size = 14),
                diagnostics = diagnostics,
            )
        }

        if (isActive) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(upRawDp(UPDropdownProps().borderRadius, 0.dp)))
                    .border(0.5.dp, UPTheme.Border)
                    .padding(vertical = 4.dp)
                    .then(if (menuHeight > 0.dp) Modifier else Modifier)
                    .upTestTag("dropdown-menu-$index"),
            ) {
                if (content != null) {
                    content()
                } else {
                    options.forEachIndexed { optionIndex, option ->
                        val isSelected = selected(option.value, values)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .upTestTag("dropdown-option-$index-$optionIndex")
                                .upClickable(enabled = !props.disabled && !option.disabled, onClick = {
                                    val next: UPRawValue = if (props.multiple) {
                                        if (isSelected) values.filterNot { it.upLooseEquals(option.value) }
                                        else values + option.value
                                    } else {
                                        option.value
                                    }
                                    onUpdateModelValue?.invoke(next)
                                    onUpdateValue?.invoke(next)
                                    onChange?.invoke(next)
                                    onSelect?.invoke(option.raw)
                                    context?.close()
                                })
                                .padding(horizontal = 16.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            BasicText(
                                option.label,
                                modifier = Modifier.weight(1f),
                                style = TextStyle(
                                    color = when {
                                        option.disabled -> UPTheme.Light
                                        isSelected -> net.lingyun.ultraui.android.core.UPColor.parse(context?.activeColor, UPTheme.Primary)
                                        else -> UPTheme.Content
                                    },
                                    fontSize = 14.sp,
                                ),
                            )
                            if (isSelected) {
                                UPIcon(UPIconProps(name = "checkmark", size = 16, color = context?.activeColor ?: "#2979ff"), diagnostics = diagnostics)
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Direct argument form for generated source. */
@Composable
public fun UPDropdownItem(
    title: String = "",
    options: List<UPRawValue> = emptyList(),
    value: UPRawValue = "",
    multiple: Boolean = false,
    onUpdateModelValue: ((UPRawValue) -> Unit)? = null,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPDropdownItem(
        props = UPDropdownItemProps(title = title, options = options, value = value, multiple = multiple),
        modifier = modifier,
        onUpdateModelValue = onUpdateModelValue,
        diagnostics = diagnostics,
    )
}

private fun Color.toHex(): String = "#%08X".format(toArgb())
