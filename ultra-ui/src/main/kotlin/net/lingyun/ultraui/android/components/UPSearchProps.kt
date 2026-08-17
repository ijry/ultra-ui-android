package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-search` props. */
public data class UPSearchProps(
    val modelValue: UPRawValue? = UPConfig.search.modelValue,
    val value: UPRawValue = UPConfig.search.value,
    val shape: String = UPConfig.search.shape,
    val bgColor: String = UPConfig.search.bgColor,
    val placeholder: UPRawValue = UPConfig.search.placeholder,
    /** The spelling is intentionally `clearabled`, matching uview-plus. */
    val clearabled: Boolean = UPConfig.search.clearabled,
    val onlyClearableOnFocused: Boolean = UPConfig.search.onlyClearableOnFocused,
    val focus: Boolean = UPConfig.search.focus,
    val showAction: Boolean = UPConfig.search.showAction,
    val actionText: UPRawValue = UPConfig.search.actionText,
    val label: UPRawValue = UPConfig.search.label,
    val inputAlign: String = UPConfig.search.inputAlign,
    val disabled: Boolean = UPConfig.search.disabled,
    val animation: Boolean = UPConfig.search.animation,
    val borderColor: String = UPConfig.search.borderColor,
    val searchIconColor: String = UPConfig.search.searchIconColor,
    val searchIconSize: UPRawValue = UPConfig.search.searchIconSize,
    val color: String = UPConfig.search.color,
    val placeholderColor: String = UPConfig.search.placeholderColor,
    val searchIcon: String = UPConfig.search.searchIcon,
    val margin: UPRawValue = UPConfig.search.margin,
    val iconPosition: String = UPConfig.search.iconPosition,
    val maxlength: UPRawValue = UPConfig.search.maxlength,
    val height: UPRawValue = UPConfig.search.height,
    val adjustPosition: Boolean = UPConfig.search.adjustPosition,
    val autoBlur: Boolean = UPConfig.search.autoBlur,
    val inputStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val actionStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
