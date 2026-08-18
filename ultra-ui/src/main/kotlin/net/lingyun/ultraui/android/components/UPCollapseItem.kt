package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val CollapseItemComponentName = "UPCollapseItem"

/** Native Compose counterpart of uview-plus `u-collapse-item`. */
@Composable
public fun UPCollapseItem(
    props: UPCollapseItemProps = UPCollapseItemProps(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit = {},
) {
    val context = LocalUPCollapseContext.current
    val itemIndex = remember { context?.allocateIndex() ?: 0 }
    val itemName = props.name.takeUnless { it == null || it.toString().isEmpty() } ?: itemIndex
    val itemTagSuffix = if (props.name == null || props.name.toString().isEmpty()) {
        itemIndex.toString()
    } else {
        selectionTagSuffix(itemName)
    }
    val standaloneInitial = props.open ?: props.isOpen
    var standaloneOpen by remember(itemName) { mutableStateOf(standaloneInitial) }

    DisposableEffect(context, itemName) {
        context?.registerName(itemName)
        onDispose { context?.unregisterName(itemName) }
    }

    val open = context?.openNames?.any { it.upLooseEquals(itemName) } ?: standaloneOpen
    val align = upSafeEnum(
        props.align,
        setOf("left", "center", "right"),
        "left",
        diagnostics,
        CollapseItemComponentName,
        "align",
    )
    val clickable = context != null && context.parentClickable && !context.parentDisabled && props.clickable && !props.disabled ||
        context == null && props.clickable && !props.disabled
    val titleStyle = rememberUPResolvedStyle(props.titleStyle, diagnostics, "$CollapseItemComponentName.titleStyle")
    val cellStyle = rememberUPResolvedStyle(props.cellCustomStyle, diagnostics, "$CollapseItemComponentName.cellCustomStyle")
    val headerTextStyle = TextStyle(
        color = if (props.disabled) UPTheme.Light else UPTheme.Main,
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        textAlign = when (align) {
            "center" -> TextAlign.Center
            "right" -> TextAlign.End
            else -> TextAlign.Start
        },
    )

    fun toggle() {
        if (!clickable) return
        if (context != null) {
            context.toggle(itemName, props.disabled, props.clickable, onClick)
        } else {
            standaloneOpen = !standaloneOpen
            onClick?.invoke(itemName)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .applyUPResolvedStyle(cellStyle)
            .upTestTag("collapse-item"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (props.border && context?.border == true) Modifier.border(0.5.dp, UPTheme.Border) else Modifier)
                .upTestTag("collapse-item-$itemTagSuffix-header")
                .upClickable(enabled = clickable, onClick = ::toggle)
                .padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (props.icon.isNotEmpty()) {
                UPIcon(
                    props = UPIconProps(name = props.icon, size = 20, color = "#909399"),
                    modifier = Modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.iconStyle, diagnostics, "$CollapseItemComponentName.iconStyle")),
                    diagnostics = diagnostics,
                )
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = when (align) {
                "center" -> Alignment.Center
                "right" -> Alignment.CenterEnd
                else -> Alignment.CenterStart
            }) {
                if (titleContent != null) {
                    titleContent()
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        if (props.title.isNotEmpty()) {
                            BasicText(props.title, modifier = Modifier.applyUPResolvedStyle(titleStyle), style = headerTextStyle)
                        }
                        if (props.label.isNotEmpty()) {
                            BasicText(props.label, style = TextStyle(color = UPTheme.Tips, fontSize = 12.sp))
                        }
                    }
                }
            }
            if (props.value.toString().isNotEmpty()) {
                BasicText(props.value.toString(), style = TextStyle(color = UPTheme.Content, fontSize = 14.sp))
            }
            if (rightContent != null) {
                rightContent()
            } else if (props.showRight && (props.isLink || context?.arrow == true)) {
                UPIcon(
                    props = UPIconProps(name = if (open) "arrow-up" else "arrow-down", size = 16, color = "#909399"),
                    modifier = Modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.rightIconStyle, diagnostics, "$CollapseItemComponentName.rightIconStyle")),
                    diagnostics = diagnostics,
                )
            }
        }
        if (open) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .upTestTag("collapse-item-$itemTagSuffix-content")
                    .padding(horizontal = 15.dp, vertical = 12.dp),
            ) {
                content()
            }
        }
    }
}

/** Direct argument form for generated source. */
@Composable
public fun UPCollapseItem(
    name: UPRawValue = "",
    title: String = "",
    disabled: Boolean = false,
    isOpen: Boolean = false,
    onClick: ((UPRawValue) -> Unit)? = null,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit = {},
) {
    UPCollapseItem(
        props = UPCollapseItemProps(name = name, title = title, disabled = disabled, isOpen = isOpen),
        modifier = modifier,
        onClick = onClick,
        diagnostics = diagnostics,
        content = content,
    )
}
