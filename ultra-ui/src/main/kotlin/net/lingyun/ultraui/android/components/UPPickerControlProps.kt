package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPPickerProps(
    val modelValue: List<UPRawValue> = emptyList(),
    val hasInput: Boolean = false,
    val inputProps: UPStyleInput = emptyMap<String, UPRawValue>(),
    val inputBorder: UPRawValue = true,
    val disabled: Boolean = false,
    val disabledColor: String = "",
    val placeholder: String = "请选择",
    val show: Boolean = false,
    val popupMode: String = "bottom",
    val showToolbar: Boolean = true,
    val title: String = "",
    val columns: List<UPRawValue> = emptyList(),
    val loading: Boolean = false,
    val itemHeight: UPRawValue = 44,
    val cancelText: String = "取消",
    val confirmText: String = "确认",
    val cancelColor: String = "#909193",
    val confirmColor: String = "",
    val visibleItemCount: UPRawValue = 5,
    val keyName: String = "text",
    val valueName: String = "value",
    val closeOnClickOverlay: Boolean = false,
    val defaultIndex: List<UPRawValue> = emptyList(),
    val immediateChange: Boolean = true,
    val toolbarRightSlot: Boolean = false,
    val zIndex: UPRawValue = 10076,
    val bgColor: String = "",
    val round: UPRawValue = 0,
    val duration: UPRawValue = 300,
    val overlayOpacity: UPRawValue = 0.5,
    val pageInline: Boolean = false,
    val maskClass: String = "",
    val maskStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val value: List<UPRawValue> = emptyList(),
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPPickerColumnProps(
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPPickerEvent(
    val value: List<UPRawValue>,
    val indexs: List<Int>,
    val index: Int = 0,
    val values: List<UPRawValue> = emptyList(),
    val columnIndex: Int = 0,
)

public data class UPPaginationProps(
    val currentPage: UPRawValue = 1,
    val pageSize: UPRawValue = 10,
    val total: UPRawValue = 0,
    val prevText: String = "",
    val nextText: String = "",
    val buttonBgColor: String = "#f5f7fa",
    val buttonBorderColor: String = "#dcdfe6",
    val pageSizes: List<UPRawValue> = listOf(10, 20, 30, 40, 50),
    val layout: String = "prev, pager, next",
    val hideOnSinglePage: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPSelectProps(
    val maxHeight: UPRawValue = "90vh",
    val overlay: Boolean = true,
    val overlayOpacity: UPRawValue = 0.01,
    val overlayStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val duration: UPRawValue = 300,
    val label: String = "选项",
    val options: List<UPRawValue> = emptyList(),
    val keyName: String = "id",
    val labelName: String = "name",
    val showOptionsLabel: Boolean = false,
    val current: UPRawValue = "",
    val zIndex: UPRawValue = 11000,
    val itemColor: String = "",
    val iconColor: String = "",
    val iconSize: UPRawValue = "13px",
    val disabled: Boolean = false,
    val border: Boolean = false,
    val optionsWidth: UPRawValue = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
