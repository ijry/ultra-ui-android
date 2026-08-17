package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-image` props. */
public data class UPImageProps(
    val src: String = UPConfig.image.src,
    val mode: String = UPConfig.image.mode,
    val width: UPRawValue = UPConfig.image.width,
    val height: UPRawValue = UPConfig.image.height,
    val shape: String = UPConfig.image.shape,
    val radius: UPRawValue = UPConfig.image.radius,
    val lazyLoad: Boolean = UPConfig.image.lazyLoad,
    val showMenuByLongpress: Boolean = UPConfig.image.showMenuByLongpress,
    val loadingIcon: String = UPConfig.image.loadingIcon,
    val errorIcon: String = UPConfig.image.errorIcon,
    val showLoading: Boolean = UPConfig.image.showLoading,
    val showError: Boolean = UPConfig.image.showError,
    val fade: Boolean = UPConfig.image.fade,
    val webp: Boolean = UPConfig.image.webp,
    val duration: UPRawValue = UPConfig.image.duration,
    val bgColor: String = UPConfig.image.bgColor,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
