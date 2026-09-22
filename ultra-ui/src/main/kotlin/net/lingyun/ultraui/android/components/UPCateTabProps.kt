package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPCateTab], mirroring uview-plus `up-cate-tab`.
 *
 * A category browser: a left menu of `tabList` beside a right content pane. `mode = follow`
 * stacks every category's content (scrolling links to the highlight upstream); `mode = tab`
 * shows only the active category. `tabKeyName`/`itemKeyName` read the menu label and the child
 * name; each `tabList` item may carry a `children` array of `{ icon, <itemKeyName> }`.
 */
public data class UPCateTabProps(
    val mode: String = "follow",
    val height: UPRawValue = "100%",
    val bgColor: String = "#f6f6f6",
    val tabList: List<UPRawValue> = emptyList(),
    val tabKeyName: String = "name",
    val itemKeyName: String = "name",
    val current: Int = 0,
    val activeColor: String = "#000",
    val tabWidth: UPRawValue = 100,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
