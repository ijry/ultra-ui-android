package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-cell` props. */
public data class UPCellProps(
    val title: UPRawValue = UPConfig.cell.title,
    val label: UPRawValue = UPConfig.cell.label,
    val value: UPRawValue = UPConfig.cell.value,
    val icon: String = UPConfig.cell.icon,
    val disabled: Boolean = UPConfig.cell.disabled,
    val border: Boolean = UPConfig.cell.border,
    val center: Boolean = UPConfig.cell.center,
    val url: String = UPConfig.cell.url,
    val linkType: String = UPConfig.cell.linkType,
    val clickable: Boolean = UPConfig.cell.clickable,
    val isLink: Boolean = UPConfig.cell.isLink,
    val required: Boolean = UPConfig.cell.required,
    val rightIcon: String = UPConfig.cell.rightIcon,
    val arrowDirection: String = UPConfig.cell.arrowDirection,
    val iconStyle: UPStyleInput = UPConfig.cell.iconStyle,
    val rightIconStyle: UPStyleInput = UPConfig.cell.rightIconStyle,
    val titleStyle: UPStyleInput = UPConfig.cell.titleStyle,
    val size: String = UPConfig.cell.size,
    val stop: Boolean = UPConfig.cell.stop,
    val name: UPRawValue = UPConfig.cell.name,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
