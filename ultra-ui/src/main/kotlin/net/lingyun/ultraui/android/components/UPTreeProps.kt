package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Field-name mapping for [UPTreeProps.props], mirroring the upstream `props` object.
 */
public data class UPTreeFields(
    val label: String = "label",
    val children: String = "children",
    val nodeKey: String = "id",
    val disabled: String = "disabled",
)

/**
 * Props for [UPTree], mirroring uview-plus `u-tree`.
 *
 * A collapsible tree over `data` (nested maps). `props` maps the label/children/key/disabled field
 * names. `showCheckbox` adds a per-node checkbox; `defaultExpandAll`/`defaultExpandedKeys` seed the
 * expanded set; `expandOnClickNode` toggles a branch when its row (not just the switcher) is tapped;
 * `accordion` keeps only one sibling open. `indent`/`iconSize`/`checkboxSize` size the layout.
 */
public data class UPTreeProps(
    val data: List<UPRawValue> = emptyList(),
    val props: UPTreeFields = UPTreeFields(),
    val showCheckbox: Boolean = false,
    val defaultExpandAll: Boolean = false,
    val defaultExpandedKeys: List<UPRawValue> = emptyList(),
    val defaultCheckedKeys: List<UPRawValue> = emptyList(),
    val expandOnClickNode: Boolean = true,
    val checkOnClickNode: Boolean = false,
    val checkStrictly: Boolean = false,
    val accordion: Boolean = false,
    val highlightCurrent: Boolean = false,
    val currentNodeKey: UPRawValue = "",
    val indent: UPRawValue = 32,
    val iconSize: UPRawValue = 14,
    val checkboxSize: UPRawValue = 16,
    val expandIcon: String = "play-right-fill",
    val collapseIcon: String = "arrow-down-fill",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
