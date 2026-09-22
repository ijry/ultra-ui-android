package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Merges the `up-view` style props into a single style map, dropping empty (unset) entries, then
 * layering `customStyle` on top (customStyle wins) — the shape [UPView] resolves.
 */
internal fun upViewStyleMap(props: UPViewProps): Map<String, UPRawValue> {
    val base = linkedMapOf<String, UPRawValue>()
    fun putIf(key: String, value: String) { if (value.isNotEmpty()) base[key] = value }
    fun putRaw(key: String, value: UPRawValue) {
        val s = if (value is String) value else value?.toString().orEmpty()
        if (s.isNotEmpty()) base[key] = value
    }
    putIf("backgroundColor", props.backgroundColor)
    putIf("color", props.color)
    putIf("justifyContent", props.justifyContent)
    putIf("alignItems", props.alignItems)
    putRaw("width", props.width)
    putRaw("height", props.height)
    putRaw("padding", props.padding)
    putRaw("margin", props.margin)
    putIf("borderColor", props.borderColor)
    val custom = props.customStyle
    if (custom is Map<*, *>) {
        custom.forEach { (k, v) -> if (k is String) base[k] = v }
    }
    return base
}

/**
 * Native Compose counterpart of uview-plus `up-view`.
 *
 * A thin styled container: the CSS-like props ([UPViewProps.backgroundColor], `width`, `padding`,
 * etc.) plus `customStyle` resolve through the shared style engine and apply to a `Box`; a tap
 * emits `click`. Upstream's template is an empty `<view>`, so [content] defaults to empty but is
 * offered for the common case of wrapping children.
 *
 * Difference: `flexDirection`/`flex1` are inline CSS flags the shared style engine does not model
 * (it resolves `justifyContent`/`alignItems` only); they are surfaced as diagnostics rather than
 * silently dropped.
 */
@Composable
public fun UPView(
    props: UPViewProps = UPViewProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable BoxScope.() -> Unit = {},
) {
    if (props.flexDirection.isNotEmpty()) {
        diagnostics.report("UPView", "flexDirection", props.flexDirection, "Inline flex direction is not modelled by the shared style engine.")
    }
    if (props.flex1.isNotEmpty()) {
        diagnostics.report("UPView", "flex1", props.flex1, "Inline flex grow is not modelled by the shared style engine.")
    }
    val style = rememberUPResolvedStyle(upViewStyleMap(props), diagnostics, "UPView")
    Box(
        modifier = modifier
            .applyUPResolvedStyle(style)
            .upTestTag("view")
            .then(if (onClick != null) Modifier.upClickable(onClick = onClick) else Modifier),
        content = content,
    )
}
