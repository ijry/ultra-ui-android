package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * JSON-friendly Android contract matching uview-plus `u-form-item` props.
 *
 * `rules` accepts [UPFormRule] values (or nested lists) and wins over the
 * parent `u-form` rules for the same `prop`, mirroring `validateField()`.
 */
public data class UPFormItemProps(
    val label: String = UPConfig.formItem.label,
    val prop: String = UPConfig.formItem.prop,
    val rules: List<UPRawValue> = UPConfig.formItem.rules,
    val borderBottom: UPRawValue = UPConfig.formItem.borderBottom,
    val labelPosition: String = UPConfig.formItem.labelPosition,
    val labelWidth: UPRawValue = UPConfig.formItem.labelWidth,
    val rightIcon: String = UPConfig.formItem.rightIcon,
    val leftIcon: String = UPConfig.formItem.leftIcon,
    val required: Boolean = UPConfig.formItem.required,
    val leftIconStyle: UPStyleInput = UPConfig.formItem.leftIconStyle,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
