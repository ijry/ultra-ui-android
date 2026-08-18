package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-action-sheet` props. */
public data class UPActionSheetProps(
    val show: Boolean = UPConfig.actionSheet.show,
    val title: String = UPConfig.actionSheet.title,
    val description: String = UPConfig.actionSheet.description,
    val actions: List<UPRawValue> = UPConfig.actionSheet.actions,
    val nameKey: String = UPConfig.actionSheet.nameKey,
    val subnameKey: String = UPConfig.actionSheet.subnameKey,
    val cancelText: String = UPConfig.actionSheet.cancelText,
    val closeOnClickAction: Boolean = UPConfig.actionSheet.closeOnClickAction,
    val safeAreaInsetBottom: Boolean = UPConfig.actionSheet.safeAreaInsetBottom,
    val openType: String = UPConfig.actionSheet.openType,
    val closeOnClickOverlay: Boolean = UPConfig.actionSheet.closeOnClickOverlay,
    val round: UPRawValue = UPConfig.actionSheet.round,
    val wrapMaxHeight: String = UPConfig.actionSheet.wrapMaxHeight,
    /** Kept for older generated payloads; uview-plus currently resolves the item by position. */
    val index: UPRawValue = UPConfig.actionSheet.index,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
