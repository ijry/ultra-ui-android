package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-popup` props. */
public data class UPPopupProps(
    val show: Boolean = UPConfig.popup.show,
    val overlay: Boolean = UPConfig.popup.overlay,
    val mode: String = UPConfig.popup.mode,
    val duration: UPRawValue = UPConfig.popup.duration,
    val closeable: Boolean = UPConfig.popup.closeable,
    val overlayStyle: UPStyleInput = UPConfig.popup.overlayStyle,
    val closeOnClickOverlay: Boolean = UPConfig.popup.closeOnClickOverlay,
    val zIndex: UPRawValue = UPConfig.popup.zIndex,
    val safeAreaInsetBottom: Boolean = UPConfig.popup.safeAreaInsetBottom,
    val safeAreaInsetTop: Boolean = UPConfig.popup.safeAreaInsetTop,
    val closeIconPos: String = UPConfig.popup.closeIconPos,
    val round: UPRawValue = UPConfig.popup.round,
    val zoom: Boolean = UPConfig.popup.zoom,
    val bgColor: String = UPConfig.popup.bgColor,
    val overlayOpacity: UPRawValue = UPConfig.popup.overlayOpacity,
    val pageInline: Boolean = UPConfig.popup.pageInline,
    val touchable: Boolean = UPConfig.popup.touchable,
    val minHeight: UPRawValue = UPConfig.popup.minHeight,
    val maxHeight: UPRawValue = UPConfig.popup.maxHeight,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
