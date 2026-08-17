package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-loading-page` props. */
public data class UPLoadingPageProps(
    val loadingText: UPRawValue = UPConfig.loadingPage.loadingText,
    val image: String = UPConfig.loadingPage.image,
    val loadingMode: String = UPConfig.loadingPage.loadingMode,
    val loading: Boolean = UPConfig.loadingPage.loading,
    val bgColor: String = UPConfig.loadingPage.bgColor,
    val color: String = UPConfig.loadingPage.color,
    val fontSize: UPRawValue = UPConfig.loadingPage.fontSize,
    val iconSize: UPRawValue = UPConfig.loadingPage.iconSize,
    val loadingColor: String = UPConfig.loadingPage.loadingColor,
    val zIndex: UPRawValue = UPConfig.loadingPage.zIndex,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
