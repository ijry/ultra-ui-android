package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-alert` props. */
public data class UPAlertProps(
    val title: String = UPConfig.alert.title,
    val type: String = UPConfig.alert.type,
    val description: String = UPConfig.alert.description,
    val closable: Boolean = UPConfig.alert.closable,
    val showIcon: Boolean = UPConfig.alert.showIcon,
    val effect: String = UPConfig.alert.effect,
    val center: Boolean = UPConfig.alert.center,
    val fontSize: UPRawValue = UPConfig.alert.fontSize,
    val transitionMode: String = UPConfig.alert.transitionMode,
    val duration: UPRawValue = UPConfig.alert.duration,
    val icon: String = UPConfig.alert.icon,
    /** Nullable so generated code can explicitly fall back to the legacy `value` alias. */
    val modelValue: Boolean? = UPConfig.alert.value,
    /** Legacy v-model alias retained for source generators targeting Vue 2. */
    val value: UPRawValue = UPConfig.alert.value,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
