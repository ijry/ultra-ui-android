package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-number-box` props. */
public data class UPNumberBoxProps(
    val name: UPRawValue = UPConfig.numberBox.name,
    val value: UPRawValue = UPConfig.numberBox.value,
    val modelValue: UPRawValue = UPConfig.numberBox.modelValue,
    val min: UPRawValue = UPConfig.numberBox.min,
    val max: UPRawValue = UPConfig.numberBox.max,
    val step: UPRawValue = UPConfig.numberBox.step,
    val integer: Boolean = UPConfig.numberBox.integer,
    val disabled: Boolean = UPConfig.numberBox.disabled,
    val disabledInput: Boolean = UPConfig.numberBox.disabledInput,
    val asyncChange: Boolean = UPConfig.numberBox.asyncChange,
    val inputWidth: UPRawValue = UPConfig.numberBox.inputWidth,
    val showMinus: Boolean = UPConfig.numberBox.showMinus,
    val showPlus: Boolean = UPConfig.numberBox.showPlus,
    val decimalLength: UPRawValue = UPConfig.numberBox.decimalLength,
    val longPress: Boolean = UPConfig.numberBox.longPress,
    val color: String = UPConfig.numberBox.color,
    val buttonWidth: UPRawValue = UPConfig.numberBox.buttonWidth,
    val buttonSize: UPRawValue = UPConfig.numberBox.buttonSize,
    val buttonRadius: UPRawValue = UPConfig.numberBox.buttonRadius,
    val bgColor: String = UPConfig.numberBox.bgColor,
    val disabledBgColor: String = UPConfig.numberBox.disabledBgColor,
    val inputBgColor: String = UPConfig.numberBox.inputBgColor,
    val cursorSpacing: UPRawValue = UPConfig.numberBox.cursorSpacing,
    val disablePlus: Boolean = UPConfig.numberBox.disablePlus,
    val disableMinus: Boolean = UPConfig.numberBox.disableMinus,
    val iconStyle: UPStyleInput = UPConfig.numberBox.iconStyle,
    val miniMode: Boolean = UPConfig.numberBox.miniMode,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
