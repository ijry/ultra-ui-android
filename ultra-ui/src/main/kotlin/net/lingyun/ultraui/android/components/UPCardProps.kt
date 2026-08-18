package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-card` props. */
public data class UPCardProps(
    val full: Boolean = UPConfig.card.full,
    val title: String = UPConfig.card.title,
    val titleColor: String = UPConfig.card.titleColor,
    val titleSize: UPRawValue = UPConfig.card.titleSize,
    val subTitle: String = UPConfig.card.subTitle,
    val subTitleColor: String = UPConfig.card.subTitleColor,
    val subTitleSize: UPRawValue = UPConfig.card.subTitleSize,
    val border: Boolean = UPConfig.card.border,
    val index: UPRawValue = UPConfig.card.index,
    val margin: UPRawValue = UPConfig.card.margin,
    val borderRadius: UPRawValue = UPConfig.card.borderRadius,
    val headStyle: UPStyleInput = UPConfig.card.headStyle,
    val bodyStyle: UPStyleInput = UPConfig.card.bodyStyle,
    val footStyle: UPStyleInput = UPConfig.card.footStyle,
    val headBorderBottom: Boolean = UPConfig.card.headBorderBottom,
    val footBorderTop: Boolean = UPConfig.card.footBorderTop,
    val thumb: String = UPConfig.card.thumb,
    val thumbWidth: UPRawValue = UPConfig.card.thumbWidth,
    val thumbCircle: Boolean = UPConfig.card.thumbCircle,
    val padding: UPRawValue = UPConfig.card.padding,
    val paddingHead: UPRawValue = UPConfig.card.paddingHead,
    val paddingBody: UPRawValue = UPConfig.card.paddingBody,
    val paddingFoot: UPRawValue = UPConfig.card.paddingFoot,
    val showHead: Boolean = UPConfig.card.showHead,
    val showFoot: Boolean = UPConfig.card.showFoot,
    val boxShadow: String = UPConfig.card.boxShadow,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    /** Compatibility aliases used by older generated Android templates. */
    val radius: UPRawValue? = null,
    val shadow: String? = null,
)
