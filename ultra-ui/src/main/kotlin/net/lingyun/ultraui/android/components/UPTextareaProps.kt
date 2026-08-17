package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-textarea` props. */
public data class UPTextareaProps(
    val value: UPRawValue = UPConfig.textarea.value,
    val modelValue: UPRawValue? = UPConfig.textarea.modelValue,
    val placeholder: UPRawValue = UPConfig.textarea.placeholder,
    val placeholderClass: String = UPConfig.textarea.placeholderClass,
    val placeholderStyle: UPStyleInput = UPConfig.textarea.placeholderStyle,
    val height: UPRawValue = UPConfig.textarea.height,
    val confirmType: String = UPConfig.textarea.confirmType,
    val disabled: Boolean = UPConfig.textarea.disabled,
    val count: Boolean = UPConfig.textarea.count,
    val focus: Boolean = UPConfig.textarea.focus,
    val autoHeight: Boolean = UPConfig.textarea.autoHeight,
    val fixed: Boolean = UPConfig.textarea.fixed,
    val cursorSpacing: UPRawValue = UPConfig.textarea.cursorSpacing,
    val cursor: UPRawValue = UPConfig.textarea.cursor,
    val showConfirmBar: Boolean = UPConfig.textarea.showConfirmBar,
    val selectionStart: UPRawValue = UPConfig.textarea.selectionStart,
    val selectionEnd: UPRawValue = UPConfig.textarea.selectionEnd,
    val adjustPosition: Boolean = UPConfig.textarea.adjustPosition,
    val disableDefaultPadding: Boolean = UPConfig.textarea.disableDefaultPadding,
    val holdKeyboard: Boolean = UPConfig.textarea.holdKeyboard,
    val maxlength: UPRawValue = UPConfig.textarea.maxlength,
    val border: String = UPConfig.textarea.border,
    val formatter: UPRawValue = UPConfig.textarea.formatter,
    val ignoreCompositionEvent: Boolean = UPConfig.textarea.ignoreCompositionEvent,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
