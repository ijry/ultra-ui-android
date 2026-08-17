package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-code-input` props. */
public data class UPCodeInputProps(
    val adjustPosition: Boolean = UPConfig.codeInput.adjustPosition,
    val maxlength: UPRawValue = UPConfig.codeInput.maxlength,
    val dot: Boolean = UPConfig.codeInput.dot,
    val mode: String = UPConfig.codeInput.mode,
    val hairline: Boolean = UPConfig.codeInput.hairline,
    val space: UPRawValue = UPConfig.codeInput.space,
    val modelValue: UPRawValue? = UPConfig.codeInput.modelValue,
    val value: UPRawValue = UPConfig.codeInput.value,
    val focus: Boolean = UPConfig.codeInput.focus,
    val bold: Boolean = UPConfig.codeInput.bold,
    val color: String = UPConfig.codeInput.color,
    val fontSize: UPRawValue = UPConfig.codeInput.fontSize,
    val size: UPRawValue = UPConfig.codeInput.size,
    val disabledKeyboard: Boolean = UPConfig.codeInput.disabledKeyboard,
    val borderColor: String = UPConfig.codeInput.borderColor,
    val disabledDot: Boolean = UPConfig.codeInput.disabledDot,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
