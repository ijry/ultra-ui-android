package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-loadmore` props. */
public data class UPLoadmoreProps(
    val status: String = UPConfig.loadmore.status,
    val bgColor: String = UPConfig.loadmore.bgColor,
    val icon: Boolean = UPConfig.loadmore.icon,
    val fontSize: UPRawValue = UPConfig.loadmore.fontSize,
    val iconSize: UPRawValue = UPConfig.loadmore.iconSize,
    val color: String = UPConfig.loadmore.color,
    val loadingIcon: String = UPConfig.loadmore.loadingIcon,
    val loadmoreText: String = UPConfig.loadmore.loadmoreText,
    val loadingText: String = UPConfig.loadmore.loadingText,
    val nomoreText: String = UPConfig.loadmore.nomoreText,
    val isDot: Boolean = UPConfig.loadmore.isDot,
    val iconColor: String = UPConfig.loadmore.iconColor,
    val marginTop: UPRawValue = UPConfig.loadmore.marginTop,
    val marginBottom: UPRawValue = UPConfig.loadmore.marginBottom,
    val height: UPRawValue = UPConfig.loadmore.height,
    val line: Boolean = UPConfig.loadmore.line,
    val lineColor: String = UPConfig.loadmore.lineColor,
    val dashed: Boolean = UPConfig.loadmore.dashed,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
