package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPPopoverProps(
    val text: UPRawValue = "",
    val color: String = "#333",
    val bgColor: String = "#f7f7f7",
    val popupBgColor: String = "#f7f7f7",
    val placement: String = "top",
    val triggerMode: String = "click",
    val show: Boolean = false,
    val zIndex: UPRawValue = 10070,
    val forcePosition: UPStyleInput = emptyMap<String, UPRawValue>(),
    val direction: String = "top",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPTooltipProps(
    val text: UPRawValue = "",
    val copyText: UPRawValue = "",
    val size: UPRawValue = 14,
    val color: String = "#606266",
    val bgColor: String = "transparent",
    val direction: String = "top",
    val zIndex: UPRawValue = 10071,
    val showCopy: Boolean = true,
    val buttons: List<UPRawValue> = emptyList(),
    val overlay: Boolean = true,
    val showToast: Boolean = true,
    val popupBgColor: String = "",
    val triggerMode: String = "longpress",
    val forcePosition: UPStyleInput = emptyMap<String, UPRawValue>(),
    val show: Boolean = false,
    val singleton: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPStickyProps(
    val offsetTop: UPRawValue = 0,
    val customNavHeight: UPRawValue = 0,
    val disabled: Boolean = false,
    val bgColor: String = "transparent",
    val zIndex: UPRawValue = "",
    val index: UPRawValue = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPSwipeActionProps(
    val autoClose: Boolean = true,
    val opendItem: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPSwipeActionItemProps(
    val show: Boolean = false,
    val closeOnClick: Boolean = true,
    val name: UPRawValue = "",
    val disabled: Boolean = false,
    val autoClose: Boolean = true,
    val scrolling: Boolean = false,
    val threshold: UPRawValue = 20,
    val options: List<UPRawValue> = emptyList(),
    val duration: UPRawValue = 300,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
