package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPFloatButton], mirroring uview-plus `up-float-button`.
 *
 * A `position: fixed` round action button. `isMenu` turns the tap into an expand/collapse of
 * `list`, whose items each carry `name` (icon) plus optional `backgroundColor`/`color`/
 * `borderColor` overrides. `right`/`top`/`bottom` are the fixed offsets; Android has no fixed
 * positioning, so the host anchors the button and these become inward offsets (see [UPFloatButton]).
 */
public data class UPFloatButtonProps(
    val backgroundColor: String = "#2979ff",
    val color: String = "#fff",
    val width: UPRawValue = "50px",
    val height: UPRawValue = "50px",
    val borderColor: String = "",
    val right: UPRawValue = "30px",
    val top: UPRawValue = "",
    val bottom: UPRawValue = "",
    val isMenu: Boolean = false,
    val list: List<UPRawValue> = emptyList(),
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
