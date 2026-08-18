package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-collapse-item` props. */
public data class UPCollapseItemProps(
    val title: String = UPConfig.collapseItem.title,
    val titleStyle: UPStyleInput = UPConfig.collapseItem.titleStyle,
    val value: UPRawValue = UPConfig.collapseItem.value,
    val label: String = UPConfig.collapseItem.label,
    val disabled: Boolean = UPConfig.collapseItem.disabled,
    val isLink: Boolean = UPConfig.collapseItem.isLink,
    val clickable: Boolean = UPConfig.collapseItem.clickable,
    val border: Boolean = UPConfig.collapseItem.border,
    val align: String = UPConfig.collapseItem.align,
    val name: UPRawValue = UPConfig.collapseItem.name,
    val icon: String = UPConfig.collapseItem.icon,
    val duration: UPRawValue = UPConfig.collapseItem.duration,
    val showRight: Boolean = UPConfig.collapseItem.showRight,
    val iconStyle: UPStyleInput = UPConfig.collapseItem.iconStyle,
    val rightIconStyle: UPStyleInput = UPConfig.collapseItem.rightIconStyle,
    val cellCustomStyle: UPStyleInput = UPConfig.collapseItem.cellCustomStyle,
    val cellCustomClass: String = UPConfig.collapseItem.cellCustomClass,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    /** Compatibility controls for a standalone item renderer. */
    val isOpen: Boolean = false,
    val open: Boolean? = null,
)
