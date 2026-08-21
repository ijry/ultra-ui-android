package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPSliderProps(
    val value: UPRawValue = 0,
    val modelValue: UPRawValue? = null,
    val isRange: Boolean = false,
    val rangeValue: List<UPRawValue> = listOf(0, 0),
    val min: UPRawValue = 0,
    val max: UPRawValue = 100,
    val step: UPRawValue = 1,
    val activeColor: String = "#2979ff",
    val inactiveColor: String = "#c0c4cc",
    val blockSize: UPRawValue = 18,
    val blockColor: String = "#ffffff",
    val blockStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val showValue: Boolean = false,
    val disabled: Boolean = false,
    val useNative: Boolean = false,
    val height: UPRawValue = "",
    val innerStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val vertical: Boolean = false,
    val size: UPRawValue = "2px",
    val length: UPRawValue = "auto",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPSliderEvent(
    val value: UPRawValue,
    val rangeValue: List<UPRawValue> = emptyList(),
)
