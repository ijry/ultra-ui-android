package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-toast` props. */
public data class UPToastProps(
    val zIndex: UPRawValue = UPConfig.toast.zIndex,
    val loading: Boolean = UPConfig.toast.loading,
    val message: String = UPConfig.toast.message,
    val icon: String = UPConfig.toast.icon,
    val type: String = UPConfig.toast.type,
    val loadingMode: String = UPConfig.toast.loadingMode,
    val show: UPRawValue = UPConfig.toast.show,
    val overlay: Boolean = UPConfig.toast.overlay,
    val position: String = UPConfig.toast.position,
    val params: Map<String, UPRawValue> = UPConfig.toast.params,
    val duration: UPRawValue = UPConfig.toast.duration,
    val isTab: Boolean = UPConfig.toast.isTab,
    val url: String = UPConfig.toast.url,
    val callback: UPRawValue = UPConfig.toast.callback,
    val back: Boolean = UPConfig.toast.back,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

/** Historical name retained for source generators that used the options spelling. */
public typealias UPToastOptions = UPToastProps
