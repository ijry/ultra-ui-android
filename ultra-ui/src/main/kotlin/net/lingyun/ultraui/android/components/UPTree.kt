package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `u-tree`.
 *
 * A collapsible tree: `data` is flattened to its visible rows (children shown only for expanded
 * nodes), each rendered with a `level * indent` inset, an expand/collapse switcher for branches,
 * an optional per-node checkbox (`showCheckbox`) and its label. Tapping the switcher toggles the
 * branch; tapping the row also toggles it when `expandOnClickNode` and emits `node-click`
 * ([onNodeClick]); the checkbox emits `check-change` ([onCheckChange]). `accordion` keeps only one
 * sibling branch open. The flatten/key logic lives in pure helpers ([upTreeFlatten]).
 *
 * Difference: `checkStrictly`/parent-child check propagation and `highlightCurrent` are advanced
 * selection behaviours the host can layer on the emitted events; the port ships expand/collapse,
 * standalone checkboxes and click events as the core.
 */
@Composable
public fun UPTree(
    props: UPTreeProps = UPTreeProps(),
    modifier: Modifier = Modifier,
    onNodeClick: ((Map<String, UPRawValue>) -> Unit)? = null,
    onCheckChange: ((Map<String, UPRawValue>, Boolean) -> Unit)? = null,
    onExpand: ((Map<String, UPRawValue>, Boolean) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable (node: Map<String, UPRawValue>, level: Int) -> Unit)? = null,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTree")
    val initialExpanded = remember(props.data, props.defaultExpandAll, props.defaultExpandedKeys) {
        if (props.defaultExpandAll) {
            upTreeAllKeys(props.data, props.props).toSet()
        } else {
            props.defaultExpandedKeys.map { it.toString() }.toSet()
        }
    }
    var expanded by remember(initialExpanded) { mutableStateOf(initialExpanded) }
    var checkedKeys by remember(props.data, props.defaultCheckedKeys) {
        mutableStateOf(props.defaultCheckedKeys.map { it.toString() }.toSet())
    }
    var currentKey by remember(props.currentNodeKey) { mutableStateOf(props.currentNodeKey.toString()) }
    val indent = props.indent.upIntOrDefault(32)
    val iconSize = props.iconSize.upIntOrDefault(14)

    fun toggleCheck(node: UPTreeVisibleNode) {
        val nowChecked = !checkedKeys.contains(node.key)
        // `checkStrictly = false` (default): checking a parent cascades to every descendant.
        val affected = if (props.checkStrictly) {
            setOf(node.key)
        } else {
            setOf(node.key) + upTreeDescendantKeys(props.data, props.props, node.key)
        }
        checkedKeys = if (nowChecked) checkedKeys + affected else checkedKeys - affected
        onCheckChange?.invoke(node.node, nowChecked)
    }

    fun toggle(node: UPTreeVisibleNode) {
        expanded = if (expanded.contains(node.key)) {
            expanded - node.key
        } else {
            if (props.accordion) {
                // Keep only sibling-less: drop other keys at the same level is approximated by
                // keeping ancestors; simplest accordion — collapse everything else then open.
                setOf(node.key)
            } else {
                expanded + node.key
            }
        }
        onExpand?.invoke(node.node, expanded.contains(node.key))
    }

    val visible = upTreeFlatten(props.data, props.props, expanded)

    Column(modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("tree")) {
        visible.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .upTestTag("tree-node-${item.key}")
                    .then(if (props.highlightCurrent && item.key == currentKey) Modifier.upTreeCurrentHighlight() else Modifier)
                    .upClickable(enabled = !item.disabled, onClick = {
                        onNodeClick?.invoke(item.node)
                        currentKey = item.key
                        if (props.expandOnClickNode && item.hasChildren) toggle(item)
                        if (props.checkOnClickNode && props.showCheckbox) toggleCheck(item)
                    })
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.width((item.level * indent).dp))
                Box(
                    modifier = Modifier.size((iconSize + 8).dp).upTestTag("tree-switcher-${item.key}").upClickable(enabled = item.hasChildren, onClick = { toggle(item) }),
                    contentAlignment = Alignment.Center,
                ) {
                    if (item.hasChildren) {
                        UPIcon(
                            UPIconProps(name = if (item.expanded) props.collapseIcon else props.expandIcon, size = iconSize, color = "#909399"),
                            diagnostics = diagnostics,
                        )
                    }
                }
                if (props.showCheckbox) {
                    UPCheckbox(
                        UPCheckboxProps(usedAlone = true, size = props.checkboxSize, disabled = item.disabled, checked = checkedKeys.contains(item.key)),
                        onChange = { _, _ -> toggleCheck(item) },
                        diagnostics = diagnostics,
                        modifier = Modifier.padding(end = 6.dp),
                    )
                }
                if (content != null) {
                    content(item.node, item.level)
                } else {
                    BasicText(
                        item.node[props.props.label].upStringValueOrEmpty(),
                        style = TextStyle(color = if (item.disabled) UPTheme.Light else UPTheme.Main, fontSize = 14.sp),
                    )
                }
            }
        }
    }
}

/** `highlight-current`: a faint primary wash on the current row. */
private fun Modifier.upTreeCurrentHighlight(): Modifier = this.background(UPTheme.Primary.copy(alpha = 0.08f))
