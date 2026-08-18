package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/** JSON-friendly Android contract matching uview-plus `u-collapse` props. */
public data class UPCollapseProps(
    val value: UPRawValue = UPConfig.collapse.value,
    val accordion: Boolean = UPConfig.collapse.accordion,
    val border: Boolean = UPConfig.collapse.border,
    /** Nullable modern alias; when non-null it takes precedence over `value`. */
    val modelValue: UPRawValue? = null,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    /** Android-compatible visual/interaction extensions accepted by older generators. */
    val arrow: Boolean = true,
    val disabled: Boolean = false,
    val clickable: Boolean = true,
)
