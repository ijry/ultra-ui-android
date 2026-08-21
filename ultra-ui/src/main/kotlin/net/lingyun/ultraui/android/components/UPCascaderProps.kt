package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPCascaderProps(
    val show: Boolean = false,
    val data: List<UPRawValue> = emptyList(),
    val modelValue: List<UPRawValue> = emptyList(),
    val valueKey: String = "value",
    val labelKey: String = "label",
    val childrenKey: String = "children",
    val maskCloseAble: Boolean = true,
    val zIndex: UPRawValue = 0,
    val autoClose: Boolean = false,
    val headerDirection: String = "row",
    val optionsCols: UPRawValue = 2,
    val closeable: Boolean = true,
    val title: String = "",
    val closeOnClickOverlay: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPCascaderEvent(
    val value: List<UPRawValue>,
    val selectedOptions: List<UPRawValue>,
    val index: Int,
)
