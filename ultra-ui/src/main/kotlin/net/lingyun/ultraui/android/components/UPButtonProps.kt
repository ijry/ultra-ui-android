package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPButtonProps(
    val hairline: Boolean = UPConfig.button.hairline,
    val type: String = UPConfig.button.type,
    val size: String = UPConfig.button.size,
    val shape: String = UPConfig.button.shape,
    val plain: Boolean = UPConfig.button.plain,
    val disabled: Boolean = UPConfig.button.disabled,
    val loading: Boolean = UPConfig.button.loading,
    val loadingText: UPRawValue = UPConfig.button.loadingText,
    val loadingMode: String = UPConfig.button.loadingMode,
    val loadingSize: UPRawValue = UPConfig.button.loadingSize,
    val openType: String = UPConfig.button.openType,
    val formType: String = UPConfig.button.formType,
    val appParameter: String = UPConfig.button.appParameter,
    val hoverStopPropagation: Boolean = UPConfig.button.hoverStopPropagation,
    val lang: String = UPConfig.button.lang,
    val sessionFrom: String = UPConfig.button.sessionFrom,
    val sendMessageTitle: String = UPConfig.button.sendMessageTitle,
    val sendMessagePath: String = UPConfig.button.sendMessagePath,
    val sendMessageImg: String = UPConfig.button.sendMessageImg,
    val showMessageCard: Boolean = UPConfig.button.showMessageCard,
    val dataName: String = UPConfig.button.dataName,
    val throttleTime: UPRawValue = UPConfig.button.throttleTime,
    val hoverStartTime: UPRawValue = UPConfig.button.hoverStartTime,
    val hoverStayTime: UPRawValue = UPConfig.button.hoverStayTime,
    val text: UPRawValue = UPConfig.button.text,
    val icon: String = UPConfig.button.icon,
    val iconColor: String = UPConfig.button.iconColor,
    val color: String = UPConfig.button.color,
    val stop: Boolean = UPConfig.button.stop,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
