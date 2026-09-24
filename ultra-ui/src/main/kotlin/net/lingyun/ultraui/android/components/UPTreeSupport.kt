package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue

/** A flattened visible tree row. `key` is stable per node; `level` is 0-based depth. */
internal data class UPTreeVisibleNode(
    val key: String,
    val node: Map<String, UPRawValue>,
    val level: Int,
    val hasChildren: Boolean,
    val expanded: Boolean,
    val disabled: Boolean,
)

private fun treeChildren(node: Map<String, UPRawValue>, childrenKey: String): List<UPRawValue> =
    (node[childrenKey] as? List<*>)?.map { it as UPRawValue } ?: emptyList()

/** `getNodeKey`: the mapped key field, falling back to a stable path key. */
internal fun upTreeNodeKey(node: Map<String, UPRawValue>, nodeKeyField: String, path: String): String {
    val raw = node[nodeKeyField]
    return if (raw != null && raw.toString().isNotEmpty()) raw.toString() else path
}

/** Collects every node key so `defaultExpandAll` can seed the expanded set. */
internal fun upTreeAllKeys(
    data: List<UPRawValue>,
    fields: UPTreeFields,
    path: String = "root",
): List<String> {
    val out = mutableListOf<String>()
    data.forEachIndexed { index, raw ->
        val node = (raw as? Map<*, *>)?.entries?.mapNotNull { (k, v) -> (k as? String)?.let { it to (v as UPRawValue) } }?.toMap() ?: emptyMap()
        val childPath = "$path-$index"
        val key = upTreeNodeKey(node, fields.nodeKey, childPath)
        out += key
        val children = treeChildren(node, fields.children)
        if (children.isNotEmpty()) out += upTreeAllKeys(children, fields, key)
    }
    return out
}

/**
 * `collectVisibleNodes`: depth-first flatten, descending into a node's children only when its key is
 * in [expandedKeys]. Matches upstream's visible-slice computation.
 */
internal fun upTreeFlatten(
    data: List<UPRawValue>,
    fields: UPTreeFields,
    expandedKeys: Set<String>,
    level: Int = 0,
    path: String = "root",
): List<UPTreeVisibleNode> {
    val out = mutableListOf<UPTreeVisibleNode>()
    data.forEachIndexed { index, raw ->
        val node = (raw as? Map<*, *>)?.entries?.mapNotNull { (k, v) -> (k as? String)?.let { it to (v as UPRawValue) } }?.toMap() ?: emptyMap()
        val childPath = "$path-$index"
        val key = upTreeNodeKey(node, fields.nodeKey, childPath)
        val children = treeChildren(node, fields.children)
        val expanded = expandedKeys.contains(key)
        out += UPTreeVisibleNode(
            key = key,
            node = node,
            level = level,
            hasChildren = children.isNotEmpty(),
            expanded = expanded,
            disabled = node[fields.disabled] == true,
        )
        if (children.isNotEmpty() && expanded) {
            out += upTreeFlatten(children, fields, expandedKeys, level + 1, key)
        }
    }
    return out
}


/** All descendant keys of the node identified by [targetKey] (excluding itself). */
internal fun upTreeDescendantKeys(
    data: List<UPRawValue>,
    fields: UPTreeFields,
    targetKey: String,
    path: String = "root",
): List<String> {
    data.forEachIndexed { index, raw ->
        val node = (raw as? Map<*, *>)?.entries?.mapNotNull { (k, v) -> (k as? String)?.let { it to (v as UPRawValue) } }?.toMap() ?: emptyMap()
        val childPath = "$path-$index"
        val key = upTreeNodeKey(node, fields.nodeKey, childPath)
        val children = treeChildren(node, fields.children)
        if (key == targetKey) {
            return upTreeAllKeys(children, fields, key)
        }
        if (children.isNotEmpty()) {
            val nested = upTreeDescendantKeys(children, fields, targetKey, key)
            if (nested.isNotEmpty()) return nested
        }
    }
    return emptyList()
}
