package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-input` props. */
public data class UPInputProps(
    /** Modern v-model alias. A null value means the generated source did not provide it. */
    val modelValue: UPRawValue? = UPConfig.input.modelValue,
    /** Legacy v-model alias retained for Vue 2 generated callers. */
    val value: UPRawValue = UPConfig.input.value,
    val type: String = UPConfig.input.type,
    val fixed: Boolean = UPConfig.input.fixed,
    val disabled: Boolean = UPConfig.input.disabled,
    val disabledColor: String = UPConfig.input.disabledColor,
    val clearable: Boolean = UPConfig.input.clearable,
    val onlyClearableOnFocused: Boolean = UPConfig.input.onlyClearableOnFocused,
    val password: Boolean = UPConfig.input.password,
    val maxlength: UPRawValue = UPConfig.input.maxlength,
    val placeholder: UPRawValue = UPConfig.input.placeholder,
    val placeholderClass: String = UPConfig.input.placeholderClass,
    val placeholderStyle: UPStyleInput = UPConfig.input.placeholderStyle,
    val showWordLimit: Boolean = UPConfig.input.showWordLimit,
    val confirmType: String = UPConfig.input.confirmType,
    val confirmHold: Boolean = UPConfig.input.confirmHold,
    val holdKeyboard: Boolean = UPConfig.input.holdKeyboard,
    val focus: Boolean = UPConfig.input.focus,
    val autoBlur: Boolean = UPConfig.input.autoBlur,
    val disableDefaultPadding: Boolean = UPConfig.input.disableDefaultPadding,
    val cursor: UPRawValue = UPConfig.input.cursor,
    val cursorSpacing: UPRawValue = UPConfig.input.cursorSpacing,
    val selectionStart: UPRawValue = UPConfig.input.selectionStart,
    val selectionEnd: UPRawValue = UPConfig.input.selectionEnd,
    val adjustPosition: Boolean = UPConfig.input.adjustPosition,
    val inputAlign: String = UPConfig.input.inputAlign,
    val fontSize: UPRawValue = UPConfig.input.fontSize,
    val color: String = UPConfig.input.color,
    val prefixIcon: String = UPConfig.input.prefixIcon,
    val prefixIconStyle: UPStyleInput = UPConfig.input.prefixIconStyle,
    val suffixIcon: String = UPConfig.input.suffixIcon,
    val suffixIconStyle: UPStyleInput = UPConfig.input.suffixIconStyle,
    val border: String = UPConfig.input.border,
    val readonly: Boolean = UPConfig.input.readonly,
    val shape: String = UPConfig.input.shape,
    /** A generated `(String) -> String`/`(String) -> Any?` formatter, kept raw for JSON-friendly contracts. */
    val formatter: UPRawValue = UPConfig.input.formatter,
    val ignoreCompositionEvent: Boolean = UPConfig.input.ignoreCompositionEvent,
    val cursorColor: String = UPConfig.input.cursorColor,
    val passwordVisibilityToggle: Boolean = UPConfig.input.passwordVisibilityToggle,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
