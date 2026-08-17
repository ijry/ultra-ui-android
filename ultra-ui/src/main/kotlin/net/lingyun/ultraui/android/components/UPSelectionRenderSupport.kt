package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.layout.size
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upBooleanOrDefault

internal fun UPRawValue.upSelectionText(): String = when (this) {
    null -> ""
    is String -> this
    else -> toString()
}

internal fun UPRawValue.upSelectionBoolean(fallback: Boolean = false): Boolean =
    upBooleanOrDefault(fallback)

internal fun selectionColor(value: String, fallback: Color): Color =
    UPColor.parse(value.takeIf { it.isNotBlank() }, fallback)

internal fun selectionShape(value: String, fallback: String): String =
    when (value.trim().lowercase()) {
        "circle", "round" -> "circle"
        "square" -> "square"
        else -> fallback
    }

internal fun selectionTagSuffix(value: UPRawValue): String = value
    .upSelectionText()
    .ifBlank { "unnamed" }
    .replace(Regex("[^A-Za-z0-9_-]"), "-")

internal fun selectionRawEquals(left: UPRawValue, right: UPRawValue): Boolean {
    if (left == right) return true
    val leftNumber = left as? Number
    val rightNumber = right as? Number
    return leftNumber != null && rightNumber != null &&
        leftNumber.toDouble().isFinite() && rightNumber.toDouble().isFinite() &&
        leftNumber.toDouble() == rightNumber.toDouble()
}

@Composable
internal fun selectionDimension(value: UPRawValue, fallback: Dp): Dp =
    upRawDp(value, fallback).coerceAtLeast(0.dp)

@Composable
internal fun UPSelectionMark(
    checked: Boolean,
    shape: String,
    size: Dp,
    iconSize: Dp,
    activeColor: Color,
    inactiveColor: Color,
    iconColor: Color,
    disabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val resolvedShape = if (shape == "circle") RoundedCornerShape(percent = 50) else RoundedCornerShape(3.dp)
    val active = if (disabled) inactiveColor else activeColor
    Box(
        modifier = modifier
            .size(size)
            .clip(resolvedShape)
            .background(if (checked) active else Color.Transparent, resolvedShape)
            .border(1.dp, if (checked) active else inactiveColor, resolvedShape)
            .semantics(mergeDescendants = true) {},
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            BasicText(
                text = "✓",
                style = TextStyle(
                    color = iconColor,
                    fontSize = iconSize.value.sp,
                    lineHeight = iconSize.value.sp,
                ),
            )
        }
    }
}

@Immutable
internal data class UPCheckboxGroupContext(
    val selected: List<UPRawValue>,
    val disabled: Boolean,
    val shape: String,
    val activeColor: String,
    val inactiveColor: String,
    val size: UPRawValue,
    val labelSize: UPRawValue,
    val labelColor: String,
    val labelDisabled: Boolean,
    val iconColor: String,
    val iconSize: UPRawValue,
    val iconPlacement: String,
    val borderBottom: Boolean,
    val toggle: (UPRawValue) -> Unit,
)

/**
 * Receiver adapter used when a group is laid out as a row. The public uview
 * contract historically exposes ColumnScope content; row placement must still
 * be able to invoke that content without casting the composable lambda.
 */
internal object UPColumnScopeAdapter : ColumnScope {
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier = this

    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier = this

    override fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine): Modifier = this

    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier = this
}

internal val LocalUPCheckboxGroup = compositionLocalOf<UPCheckboxGroupContext?> { null }

@Immutable
internal data class UPRadioGroupContext(
    val selected: UPRawValue,
    val disabled: Boolean,
    val shape: String,
    val activeColor: String,
    val inactiveColor: String,
    val size: UPRawValue,
    val labelSize: UPRawValue,
    val labelColor: String,
    val labelDisabled: Boolean,
    val iconColor: String,
    val iconSize: UPRawValue,
    val iconPlacement: String,
    val borderBottom: Boolean,
    val toggle: (UPRawValue) -> Unit,
)

internal val LocalUPRadioGroup = compositionLocalOf<UPRadioGroupContext?> { null }

internal fun selectionTextStyle(
    color: Color,
    size: Dp,
    disabled: Boolean,
): TextStyle = TextStyle(
    color = color.copy(alpha = if (disabled) 0.45f else 1f),
    fontSize = size.value.sp,
    lineHeight = (size.value + 2f).sp,
)

internal val SelectionDefaultActiveColor: Color get() = UPTheme.Primary
internal val SelectionDefaultInactiveColor: Color get() = UPTheme.Border
