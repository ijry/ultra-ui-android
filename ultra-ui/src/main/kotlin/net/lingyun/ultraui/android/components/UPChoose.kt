package net.lingyun.ultraui.android.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upTestTag

/** `currentIndex = modelValue`: the selected index, or -1 when the model is the `false` default. */
internal fun upChooseCurrentIndex(modelValue: UPRawValue): Int = when (modelValue) {
    is Number -> modelValue.toInt()
    is Boolean -> -1
    is String -> modelValue.toIntOrNull() ?: -1
    else -> -1
}

/**
 * Native Compose counterpart of uview-plus `up-choose`.
 *
 * Renders `options` as `up-tag`s; the tag at `modelValue` is a filled primary tag, the rest are
 * plain info tags. `labelName` reads the tag text off each option object (or the option itself
 * when it is a plain string). `wrap = true` flows the tags across lines; `false` keeps them on a
 * single horizontally scrollable row. A tap emits `update:modelValue(index)` normally, or
 * `custom-click(index)` when `customClick` is set (the selection is then left to the caller).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun UPChoose(
    props: UPChooseProps = UPChooseProps(),
    modifier: Modifier = Modifier,
    onUpdateModelValue: ((Int) -> Unit)? = null,
    onCustomClick: ((Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPChoose")
    val current = upChooseCurrentIndex(props.modelValue)

    @Composable
    fun Tag(index: Int, raw: UPRawValue) {
        val label = raw.upStringKeyMapOrEmpty()[props.labelName]?.upStringValueOrEmpty()
            ?: raw.upStringValueOrEmpty()
        val selected = index == current
        // `:style="{ width: itemWidth, padding: itemPadding }"` on the tag; `auto` width means leave it unset.
        val tagStyle = buildMap<String, Any?> {
            if (props.itemWidth != "auto") put("width", props.itemWidth)
            put("padding", props.itemPadding)
        }
        UPTag(
            props = UPTagProps(
                text = label,
                type = if (selected) "primary" else "info",
                size = "large",
                plain = !selected,
                height = props.itemHeight,
                customStyle = tagStyle,
            ),
            onClick = {
                if (props.customClick) onCustomClick?.invoke(index) else onUpdateModelValue?.invoke(index)
            },
            diagnostics = diagnostics,
        )
    }

    val rootModifier = modifier
        .fillMaxWidth()
        .applyUPResolvedStyle(style)
        .upTestTag("choose")

    if (props.wrap) {
        FlowRow(modifier = rootModifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            props.options.forEachIndexed { index, raw -> Box(Modifier.padding(end = 8.dp, bottom = 8.dp)) { Tag(index, raw) } }
        }
    } else {
        Row(
            modifier = rootModifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            props.options.forEachIndexed { index, raw -> Tag(index, raw) }
        }
    }
}
