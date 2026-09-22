package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPChoose], mirroring uview-plus `up-choose`.
 *
 * A tag-based single choice: `options` render as `up-tag`s, the one at `modelValue` (an index)
 * shows as a filled primary tag, the rest as plain info tags. `labelName` is the object key for
 * the tag text. `wrap` lets the tags flow onto multiple lines; `false` keeps them on one
 * horizontally scrollable row. `customClick` makes a tap emit `custom-click(index)` instead of
 * selecting.
 */
public data class UPChooseProps(
    val options: List<UPRawValue> = emptyList(),
    val modelValue: UPRawValue = false,
    val type: String = "radio",
    val itemWidth: String = "auto",
    val itemHeight: String = "50px",
    val itemPadding: String = "8px",
    val labelName: String = "title",
    val valueName: String = "value",
    val customClick: Boolean = false,
    val wrap: Boolean = true,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
