package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * JSON-friendly Android contract matching uview-plus `u-form` props.
 *
 * `rules` stays weakly typed so the generated `UPConfig` defaults never need a
 * reverse dependency on the component layer. Every entry may be a single
 * [UPFormRule] or a list of them, exactly like the upstream object literal.
 */
public data class UPFormProps(
    val model: Map<String, UPRawValue> = UPConfig.form.model,
    val rules: Map<String, UPRawValue> = UPConfig.form.rules,
    val errorType: String = UPConfig.form.errorType,
    val borderBottom: Boolean = UPConfig.form.borderBottom,
    val labelPosition: String = UPConfig.form.labelPosition,
    val labelWidth: UPRawValue = UPConfig.form.labelWidth,
    val labelAlign: String = UPConfig.form.labelAlign,
    val labelStyle: UPStyleInput = UPConfig.form.labelStyle,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
