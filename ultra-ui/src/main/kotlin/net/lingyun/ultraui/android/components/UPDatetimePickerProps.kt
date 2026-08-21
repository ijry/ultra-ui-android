package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPDatetimePickerProps(
    val hasInput: Boolean = false,
    val inputProps: UPStyleInput = emptyMap<String, UPRawValue>(),
    val inputBorder: UPRawValue = "surround",
    val disabled: Boolean = false,
    val disabledColor: String = "",
    val placeholder: String = "请选择",
    val format: String = "",
    val show: Boolean = false,
    val popupMode: String = "bottom",
    val showToolbar: Boolean = true,
    val toolbarRightSlot: Boolean = false,
    val value: UPRawValue = "",
    val modelValue: UPRawValue? = null,
    val title: String = "",
    val mode: String = "datetime",
    val maxDate: UPRawValue = defaultDateBound(true),
    val minDate: UPRawValue = defaultDateBound(false),
    val minHour: UPRawValue = 0,
    val maxHour: UPRawValue = 23,
    val minMinute: UPRawValue = 0,
    val maxMinute: UPRawValue = 59,
    val minSecond: UPRawValue = 0,
    val maxSecond: UPRawValue = 59,
    val filter: UPRawValue? = null,
    val formatter: UPRawValue? = null,
    val loading: Boolean = false,
    val itemHeight: UPRawValue = 44,
    val cancelText: String = "取消",
    val confirmText: String = "确认",
    val cancelColor: String = "#909193",
    val confirmColor: String = "#3c9cff",
    val visibleItemCount: UPRawValue = 5,
    val closeOnClickOverlay: Boolean = false,
    val defaultIndex: List<UPRawValue> = emptyList(),
    val pageInline: Boolean = false,
    val maskClass: String = "",
    val maskStyle: String = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public fun defaultDateBound(future: Boolean): Long {
    val calendar = java.util.Calendar.getInstance()
    calendar.add(java.util.Calendar.YEAR, if (future) 10 else -10)
    calendar.set(java.util.Calendar.MONTH, 0)
    calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
    calendar.set(java.util.Calendar.MINUTE, 0)
    calendar.set(java.util.Calendar.SECOND, 0)
    calendar.set(java.util.Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

public data class UPDatetimePickerEvent(
    val value: UPRawValue,
    val selectedValues: List<UPRawValue>,
    val selectedIndexes: List<Int>,
)
