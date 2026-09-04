package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPResolvedStyle
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `u-form-item`.
 *
 * The item owns its error text and registers itself with the surrounding
 * [UPForm], which drives that text from `validate()` / `validateField()`.
 * `label`, `right` and `error` map to [labelContent], [rightContent] and
 * [errorContent]; like upstream, [labelContent] replaces the whole left block,
 * including the required star and [UPFormItemProps.leftIcon].
 *
 * `labelPosition`, `labelWidth`, `labelAlign` and `errorType` fall back to the
 * parent form exactly like the upstream `parentData` mixin, so a standalone
 * item still renders with the global [UPConfig] defaults.
 */
@Composable
public fun UPFormItem(
    props: UPFormItemProps = UPFormItemProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    labelContent: (@Composable () -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null,
    errorContent: (@Composable (String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit = {},
) {
    val context = LocalUPFormContext.current
    val message = remember { mutableStateOf("") }
    // The parent reads the rules lazily so a recomposed `rules` list is picked
    // up without re-registering the item.
    val itemRules = rememberUpdatedState(props.rules)
    val registration = remember(props.prop) {
        UPFormItemRegistration(prop = props.prop, itemRules = { itemRules.value }, message = message)
    }
    DisposableEffect(context, registration) {
        if (context == null) {
            diagnostics.report(
                UPFormItemComponentName,
                "prop",
                props.prop,
                "No parent UPForm, so validation stays inert.",
            )
        }
        context?.register(registration)
        onDispose { context?.unregister(registration) }
    }

    val labelPosition = upSafeEnum(
        props.labelPosition.ifEmpty { context?.labelPosition ?: UPConfig.form.labelPosition },
        setOf("left", "top"),
        "left",
        diagnostics,
        UPFormItemComponentName,
        "labelPosition",
    )
    val labelAlign = upSafeEnum(
        context?.labelAlign ?: UPConfig.form.labelAlign,
        setOf("left", "center", "right"),
        "left",
        diagnostics,
        UPFormItemComponentName,
        "labelAlign",
    )
    val errorType = context?.errorType ?: UPConfig.form.errorType
    // `labelWidth || parentData.labelWidth`: only a truthy own value wins, so
    // the default empty string falls through to the form level width.
    val labelWidth = upRawDp(
        props.labelWidth.takeIf { upFormTruthy(it) } ?: context?.labelWidth ?: UPConfig.form.labelWidth,
        45.dp,
    )
    val labelStyle = rememberUPResolvedStyle(
        context?.labelStyle ?: emptyMap<String, UPRawValue>(),
        diagnostics,
        "$UPFormItemComponentName.labelStyle",
    )
    val customStyle = rememberUPResolvedStyle(props.customStyle, diagnostics, UPFormItemComponentName)

    val bodyModifier = Modifier
        .fillMaxWidth()
        .applyUPResolvedStyle(customStyle)
        .then(if (onClick != null) Modifier.upClickable(onClick = onClick) else Modifier)
        .padding(vertical = 10.dp)
        .upTestTag("form-item-body")
    // `v-if="required || leftIcon != '' || label != ''"` only guards the slot
    // fallback, so a provided label slot always renders the left block.
    val showLabel = labelContent != null ||
        props.required ||
        props.leftIcon.isNotEmpty() ||
        props.label.isNotEmpty()

    Column(modifier = modifier.upTestTag("form-item")) {
        if (labelPosition == "left") {
            Row(modifier = bodyModifier, verticalAlignment = Alignment.CenterVertically) {
                if (showLabel) {
                    UPFormItemLabel(props, labelPosition, labelAlign, labelWidth, labelStyle, labelContent, diagnostics)
                }
                UPFormItemBodyRight(Modifier.weight(1f), rightContent, content)
            }
        } else {
            Column(modifier = bodyModifier) {
                if (showLabel) {
                    UPFormItemLabel(props, labelPosition, labelAlign, labelWidth, labelStyle, labelContent, diagnostics)
                }
                UPFormItemBodyRight(Modifier.fillMaxWidth(), rightContent, content)
            }
        }
        if (errorContent != null) {
            errorContent(message.value)
        } else if (message.value.isNotEmpty() && errorType == "message") {
            BasicText(
                message.value,
                modifier = Modifier
                    .padding(start = if (labelPosition == "top") 0.dp else labelWidth)
                    .upTestTag("form-item-message"),
                style = TextStyle(color = UPTheme.Error, fontSize = 12.sp),
            )
        }
        if (upFormTruthy(props.borderBottom)) {
            UPLine(
                props = UPLineProps(
                    // Upstream picks `color['error']` from `libs/config/color.js`
                    // here, which is #f56c6c and not the #fa3534 of the SCSS
                    // theme that drives every other error color of this port.
                    color = if (message.value.isNotEmpty() && errorType == "border-bottom") {
                        "#f56c6c"
                    } else {
                        UPConfig.line.color
                    },
                    customStyle = mapOf<String, UPRawValue>(
                        "marginTop" to if (message.value.isNotEmpty() && errorType == "message") "5px" else 0,
                    ),
                ),
                diagnostics = diagnostics,
            )
        }
    }
}

@Composable
private fun UPFormItemLabel(
    props: UPFormItemProps,
    labelPosition: String,
    labelAlign: String,
    labelWidth: Dp,
    labelStyle: UPResolvedStyle,
    labelContent: (@Composable () -> Unit)?,
    diagnostics: UPCompatibilityDiagnostics,
) {
    Box(
        modifier = Modifier
            .width(labelWidth)
            .then(if (labelPosition == "left") Modifier else Modifier.padding(bottom = 5.dp))
            .upTestTag("form-item-label"),
    ) {
        if (labelContent != null) {
            labelContent()
        } else {
            if (props.required) {
                // `position: absolute; left: -9px; top: 3px`, so the star never
                // consumes layout space next to the label.
                BasicText(
                    "*",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-9).dp, y = 3.dp)
                        .upTestTag("form-item-required"),
                    style = TextStyle(color = UPTheme.Error, fontSize = 20.sp),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (props.leftIcon.isNotEmpty()) {
                    UPIcon(
                        props = UPIconProps(name = props.leftIcon, customStyle = props.leftIconStyle),
                        modifier = Modifier.padding(end = 4.dp),
                        diagnostics = diagnostics,
                    )
                }
                BasicText(
                    props.label,
                    modifier = Modifier.weight(1f).applyUPResolvedStyle(labelStyle),
                    style = TextStyle(
                        color = labelStyle.color ?: UPTheme.Main,
                        fontSize = (labelStyle.fontSize ?: 15.dp).value.sp,
                        // Upstream appends `justifyContent` after `labelStyle`,
                        // so `labelAlign` wins over a style level text align.
                        textAlign = when (labelAlign) {
                            "center" -> TextAlign.Center
                            "right" -> TextAlign.End
                            else -> TextAlign.Start
                        },
                    ),
                )
            }
        }
    }
}

@Composable
private fun UPFormItemBodyRight(
    modifier: Modifier,
    rightContent: (@Composable () -> Unit)?,
    content: @Composable () -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            content()
        }
        if (rightContent != null) {
            Row(
                modifier = Modifier.padding(start = 5.dp).upTestTag("form-item-right"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                rightContent()
            }
        }
    }
}

/** Direct argument form for generated source. */
@Composable
public fun UPFormItem(
    label: String = "",
    prop: String = "",
    rules: List<UPRawValue> = emptyList(),
    borderBottom: UPRawValue = "",
    labelWidth: UPRawValue = "",
    required: Boolean = false,
    leftIcon: String = "",
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit = {},
) {
    UPFormItem(
        props = UPFormItemProps(
            label = label,
            prop = prop,
            rules = rules,
            borderBottom = borderBottom,
            labelWidth = labelWidth,
            required = required,
            leftIcon = leftIcon,
        ),
        modifier = modifier,
        onClick = onClick,
        diagnostics = diagnostics,
        content = content,
    )
}
