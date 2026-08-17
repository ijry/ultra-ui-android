package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val RadioComponentName = "UPRadio"

/** Native Compose counterpart of uview-plus `u-radio`. */
@Composable
public fun UPRadio(
    props: UPRadioProps = UPRadioProps(),
    onChange: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val group = LocalUPRadioGroup.current
    if (group == null) {
        LaunchedEffect(Unit) {
            diagnostics.report(
                RadioComponentName,
                "group",
                props.name,
                "Radio must be used inside UPRadioGroup; rendering an unchecked standalone item.",
            )
        }
    }

    val checked = group?.let { selectionRawEquals(it.selected, props.name) } ?: false
    val disabled = group?.disabled == true || props.disabled.upSelectionBoolean(false)
    val labelDisabled = group?.labelDisabled == true || props.labelDisabled.upSelectionBoolean(false)
    val shape = selectionShape(
        props.shape.ifBlank { group?.shape.orEmpty() },
        "circle",
    )
    val size = selectionDimension(
        if (props.size.upSelectionText().isBlank()) group?.size else props.size,
        group?.let { selectionDimension(it.size, 18.dp) } ?: 18.dp,
    )
    val iconSize = selectionDimension(
        if (props.iconSize.upSelectionText().isBlank()) group?.iconSize else props.iconSize,
        group?.let { selectionDimension(it.iconSize, 12.dp) } ?: 12.dp,
    )
    val labelSize = selectionDimension(
        if (props.labelSize.upSelectionText().isBlank()) group?.labelSize else props.labelSize,
        group?.let { selectionDimension(it.labelSize, 14.dp) } ?: 14.dp,
    )
    val activeColor = selectionColor(
        props.activeColor.ifBlank { group?.activeColor.orEmpty() },
        SelectionDefaultActiveColor,
    )
    val inactiveColor = selectionColor(
        props.inactiveColor.ifBlank { group?.inactiveColor.orEmpty() },
        SelectionDefaultInactiveColor,
    )
    val labelColor = selectionColor(
        props.labelColor.ifBlank { group?.labelColor.orEmpty() },
        UPTheme.Content,
    )
    val iconColor = selectionColor(
        props.iconColor.ifBlank { group?.iconColor.orEmpty() },
        Color.White,
    )
    val iconPlacement = group?.iconPlacement ?: "left"
    val suffix = selectionTagSuffix(props.name)
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, RadioComponentName)

    fun select() {
        if (disabled) return
        group?.toggle(props.name)
        onChange?.invoke(props.name)
    }

    val rowModifier = modifier
        .then(if (iconPlacement == "right") Modifier.fillMaxWidth() else Modifier)
        .padding(vertical = if (group?.borderBottom == true) 8.dp else 5.dp)
        .applyUPResolvedStyle(style)
        .upTestTag("radio")
        .semantics {
            if (!disabled) {
                role = Role.RadioButton
                onClick(action = {
                    select()
                    true
                })
            }
        }

    Row(
        modifier = rowModifier,
        horizontalArrangement = if (iconPlacement == "right") Arrangement.SpaceBetween else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        @Composable
        fun Label() {
            if (props.label.upSelectionText().isNotEmpty()) {
                BasicText(
                    text = props.label.upSelectionText(),
                    modifier = Modifier
                        .then(
                            Modifier.upClickable(
                                enabled = true,
                                role = Role.RadioButton,
                                onClick = if (labelDisabled || disabled) ({}) else ::select,
                            ),
                        )
                        .upTestTag("radio-label-$suffix"),
                    style = selectionTextStyle(labelColor, labelSize, disabled),
                )
            }
        }

        @Composable
        fun Mark() {
            UPSelectionMark(
                checked = checked,
                shape = shape,
                size = size,
                iconSize = iconSize,
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                iconColor = if (disabled) inactiveColor else iconColor,
                disabled = disabled,
                modifier = Modifier
                    .upClickable(
                        enabled = !disabled,
                        role = Role.RadioButton,
                        onClick = ::select,
                    )
                    .upTestTag("radio-mark-$suffix"),
            )
        }

        if (iconPlacement == "right") {
            Label()
            Mark()
        } else {
            Mark()
            Label()
        }
    }
}

/** Convenience direct-value form for generated source inside a group. */
@Composable
public fun UPRadio(
    name: UPRawValue,
    label: UPRawValue = "",
    modifier: Modifier = Modifier,
    onChange: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPRadio(
        props = UPRadioProps(name = name, label = label),
        onChange = onChange,
        diagnostics = diagnostics,
        modifier = modifier,
    )
}
