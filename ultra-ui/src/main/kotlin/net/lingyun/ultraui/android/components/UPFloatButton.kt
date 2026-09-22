package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `up-float-button`.
 *
 * A round action button pinned to a screen corner. With `isMenu = true` the tap toggles a
 * column of [UPFloatButtonProps.list] items rising above the main button, each an icon-only
 * circle that can override `backgroundColor`/`color`/`borderColor`; the main plus glyph rotates
 * 45° while the list is open (`.show-list { transform: rotate(45deg) }`). `click` fires on the
 * main button, `item-click` on a list item (payload `{...item, index}`).
 *
 * Difference: upstream is `position: fixed`; Android has no fixed positioning, so the button
 * anchors to the corner of the space this composable is given (default bottom-right, or top-right
 * when `top` is set) and `right`/`top`/`bottom` become inward offsets from that corner — the same
 * translation `u-back-top` uses.
 */
@Composable
public fun UPFloatButton(
    props: UPFloatButtonProps = UPFloatButtonProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onItemClick: ((item: Map<String, Any?>, index: Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable (showList: Boolean) -> Unit)? = null,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPFloatButton")
    var showList by remember { mutableStateOf(false) }

    val width = upDimension(props.width, 50.dp)
    val height = upDimension(props.height, 50.dp)
    val right = upDimension(props.right, 30.dp)
    val anchorTop = props.top.upStringValueOrEmpty().isNotEmpty()
    val top = upDimension(props.top, 0.dp)
    val bottom = upDimension(props.bottom, 0.dp)
    val background = UPColor.parse(props.backgroundColor, Color(0xFF2979FF))

    fun clickHandler() {
        if (props.isMenu) showList = !showList
        onClick?.invoke()
    }

    Box(modifier = modifier.fillMaxSize()) {
        val alignment = if (anchorTop) Alignment.TopEnd else Alignment.BottomEnd
        // `right` insets from the right edge; `top`/`bottom` from the matching edge. A
        // positive offset toward the anchored corner is negative on that axis.
        val offsetY = if (anchorTop) top else -bottom
        Box(
            modifier = Modifier
                .align(alignment)
                .offset(x = -right, y = offsetY)
                .applyUPResolvedStyle(style)
                .upTestTag("float-button"),
        ) {
            // The expandable list sits above the main button (`bottom: height`).
            if (showList) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = -height)
                        .upTestTag("float-button-list"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    props.list.forEachIndexed { index, raw ->
                        val item = raw.upStringKeyMapOrEmpty()
                        val itemBg = item["backgroundColor"].upStringValueOrEmpty().ifEmpty { props.backgroundColor }
                        val itemColor = item["color"].upStringValueOrEmpty().ifEmpty { props.color }
                        val itemBorder = item["borderColor"].upStringValueOrEmpty().ifEmpty { props.borderColor }
                        Box(
                            modifier = Modifier
                                .size(width = width, height = height)
                                .clip(CircleShape)
                                .background(UPColor.parse(itemBg, background), CircleShape)
                                .then(if (itemBorder.isNotEmpty()) Modifier.border(1.dp, UPColor.parse(itemBorder, Color.Transparent), CircleShape) else Modifier)
                                .upTestTag("float-button-item-$index")
                                .upClickable(onClick = { onItemClick?.invoke(item + ("index" to index), index) }),
                            contentAlignment = Alignment.Center,
                        ) {
                            UPIcon(UPIconProps(name = item["name"].upStringValueOrEmpty(), color = itemColor), diagnostics = diagnostics)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(width = width, height = height)
                    .clip(CircleShape)
                    .background(background, CircleShape)
                    .then(if (props.borderColor.isNotEmpty()) Modifier.border(1.dp, UPColor.parse(props.borderColor, Color.Transparent), CircleShape) else Modifier)
                    .upTestTag("float-button-main")
                    .upClickable(onClick = ::clickHandler),
                contentAlignment = Alignment.Center,
            ) {
                if (content != null) {
                    content(showList)
                } else {
                    UPIcon(
                        UPIconProps(name = "plus", color = props.color),
                        modifier = Modifier.rotate(if (showList) 45f else 0f),
                        diagnostics = diagnostics,
                    )
                }
            }
        }
    }
}
