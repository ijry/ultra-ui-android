package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-modal` props. */
public data class UPModalProps(
    val show: Boolean = UPConfig.modal.show,
    val title: String = UPConfig.modal.title,
    val content: String = UPConfig.modal.content,
    val confirmText: String = UPConfig.modal.confirmText,
    val cancelText: String = UPConfig.modal.cancelText,
    val showConfirmButton: Boolean = UPConfig.modal.showConfirmButton,
    val showCancelButton: Boolean = UPConfig.modal.showCancelButton,
    val confirmColor: String = UPConfig.modal.confirmColor,
    val cancelColor: String = UPConfig.modal.cancelColor,
    val buttonReverse: Boolean = UPConfig.modal.buttonReverse,
    val zoom: Boolean = UPConfig.modal.zoom,
    val asyncClose: Boolean = UPConfig.modal.asyncClose,
    val closeOnClickOverlay: Boolean = UPConfig.modal.closeOnClickOverlay,
    val negativeTop: UPRawValue = UPConfig.modal.negativeTop,
    val width: UPRawValue = UPConfig.modal.width,
    val confirmButtonShape: String = UPConfig.modal.confirmButtonShape,
    val duration: UPRawValue = UPConfig.modal.duration,
    val contentTextAlign: String = UPConfig.modal.contentTextAlign,
    val asyncCloseTip: String = UPConfig.modal.asyncCloseTip,
    val asyncCancelClose: Boolean = UPConfig.modal.asyncCancelClose,
    val contentStyle: UPStyleInput = UPConfig.modal.contentStyle,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
