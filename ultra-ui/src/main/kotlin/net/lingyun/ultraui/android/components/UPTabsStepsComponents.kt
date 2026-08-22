package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

@Composable
public fun UPTabs(props: UPTabsProps = UPTabsProps(), modifier: Modifier = Modifier, onChange: ((Int) -> Unit)? = null, onClick: ((Int) -> Unit)? = null, onUpdateCurrent: ((UPRawValue) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    var current by remember { mutableIntStateOf(props.current.upIntOrDefault(0).coerceAtLeast(0)) }
    LaunchedEffect(props.current) { current = props.current.upIntOrDefault(0).coerceAtLeast(0) }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTabs")
    Row(modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("tabs"), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
        props.list.forEachIndexed { index, item ->
            val title = actionOrOptionText(item, props.keyName, item.upStringValueOrEmpty())
            Column(
                modifier = Modifier
                    .then(if (props.scrollable) Modifier else Modifier.weight(1f))
                    .upClickable(onClick = { current = index; onChange?.invoke(index); onClick?.invoke(index); onUpdateCurrent?.invoke(index) })
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .upTestTag("tabs-item-$index"),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BasicText(title, style = TextStyle(color = if (index == current) UPTheme.Main else UPTheme.Content))
                Spacer(Modifier.height(6.dp))
                Box(Modifier.width(upDimension(props.lineWidth, 20.dp)).height(upDimension(props.lineHeight, 3.dp)).background(UPColor.parse(props.lineColor, if (index == current) UPTheme.Primary else Color.Transparent)))
            }
        }
    }
}

@Composable
public fun UPTabs(list: List<UPRawValue>, current: Int = 0, modifier: Modifier = Modifier, onChange: ((Int) -> Unit)? = null) = UPTabs(UPTabsProps(list = list, current = current), modifier, onChange)

@Composable
public fun UPTabsItem(props: UPTabsItemProps = UPTabsItemProps(), modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, UPCompatibilityDiagnostics.None, "UPTabsItem")).upTestTag("tabs-item")) { content() }
}

@Composable
public fun UPSubsection(props: UPSubsectionProps = UPSubsectionProps(), modifier: Modifier = Modifier, onChange: ((Int) -> Unit)? = null, onUpdateCurrent: ((UPRawValue) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    var current by remember { mutableIntStateOf(props.current.upIntOrDefault(0).coerceAtLeast(0)) }
    LaunchedEffect(props.current) { current = props.current.upIntOrDefault(0).coerceAtLeast(0) }
    Row(modifier.fillMaxWidth().background(UPColor.parse(props.bgColor, Color(0xFFEEEEEF))).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSubsection")).upTestTag("subsection")) {
        props.list.forEachIndexed { index, item ->
            val label = actionOrOptionText(item, props.keyName, item.upStringValueOrEmpty())
            Box(Modifier.weight(1f).upClickable(enabled = !props.disabled, onClick = { current = index; onChange?.invoke(index); onUpdateCurrent?.invoke(index) }).padding(8.dp), contentAlignment = Alignment.Center) {
                BasicText(label, style = TextStyle(color = if (current == index) UPColor.parse(props.activeColor, UPTheme.Primary) else UPColor.parse(props.inactiveColor, UPTheme.Main)))
            }
        }
    }
}

/**
 * Parent state each `UPStepsItem` needs. uview passes this down as `parentData`; the
 * items cannot derive their own status without knowing `current` and the sibling count.
 */
@Immutable
private data class UPStepsContext(
    val current: Int,
    val direction: String,
    val activeColor: String,
    val inactiveColor: String,
    val activeIcon: String,
    val inactiveIcon: String,
    val dot: Boolean,
    val nextIndex: () -> Int,
)

private val LocalUPSteps = staticCompositionLocalOf<UPStepsContext?> { null }

/** uview's four step states (`u-steps-item.vue`: statusClass). */
internal fun stepsItemStatus(index: Int, current: Int, error: Boolean): String = when {
    current == index -> if (error) "error" else "process"
    error -> "error"
    current > index -> "finish"
    else -> "wait"
}

@Composable
public fun UPSteps(props: UPStepsProps = UPStepsProps(), modifier: Modifier = Modifier, onClick: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) {
    val direction = upSafeEnum(props.direction, setOf("row", "column"), "row", diagnostics, "UPSteps", "direction")
    var nextIndex = 0
    val context = UPStepsContext(
        current = props.current.upIntOrDefault(0),
        direction = direction,
        activeColor = props.activeColor,
        inactiveColor = props.inactiveColor,
        activeIcon = props.activeIcon,
        inactiveIcon = props.inactiveIcon,
        dot = props.dot,
        nextIndex = { nextIndex++ },
    )
    val root = modifier.fillMaxWidth()
        .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSteps"))
        .upTestTag("steps")
    // `direction` picks the axis the steps advance along, as it does upstream.
    if (direction == "column") {
        Column(root) { CompositionLocalProvider(LocalUPSteps provides context) { content() } }
    } else {
        Row(root) { CompositionLocalProvider(LocalUPSteps provides context) { content() } }
    }
}

@Composable
public fun UPStepsItem(props: UPStepsItemProps = UPStepsItemProps(), modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    val parent = LocalUPSteps.current
    val index = parent?.nextIndex?.invoke() ?: 0
    val status = stepsItemStatus(index, parent?.current ?: 0, props.error)
    val activeColor = UPColor.parse(parent?.activeColor, UPTheme.Primary)
    val inactiveColor = UPColor.parse(parent?.inactiveColor, UPTheme.Tips)
    val markerColor = when (status) {
        "finish", "process" -> activeColor
        "error" -> UPTheme.Error
        else -> inactiveColor
    }
    val markerSize = upRawDp(props.iconSize, 17.dp).coerceAtLeast(0.dp)
    val vertical = parent?.direction == "column"
    val row = modifier
        .then(if (vertical) Modifier.fillMaxWidth() else Modifier)
        .upClickable(enabled = onClick != null, onClick = { onClick?.invoke() })
        .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPStepsItem"))
        .padding(12.dp)
        .upTestTag("steps-item-$index-$status")

    Row(row, verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val marker = Modifier.width(markerSize).height(markerSize)
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.itemStyle, diagnostics, "UPStepsItem.itemStyle"))
        val customIcon = if (index <= (parent?.current ?: 0)) parent?.activeIcon else parent?.inactiveIcon
        if (parent?.dot == true) {
            // Dot mode drops the numbered circle entirely and just tints a disc.
            Box(marker.background(markerColor, RoundedCornerShape(50)).upTestTag("steps-item-$index-dot"))
        } else if (!parent?.activeIcon.isNullOrEmpty() || !parent?.inactiveIcon.isNullOrEmpty()) {
            // uview swaps the circle for `activeIcon`/`inactiveIcon` when either is set,
            // picking by `index <= current` rather than by the four-state status.
            Box(marker.upTestTag("steps-item-$index-icon"), contentAlignment = Alignment.Center) {
                UPIcon(
                    UPIconProps(
                        name = customIcon.orEmpty(),
                        size = props.iconSize,
                        color = if (status == "wait") parent.inactiveColor else parent.activeColor,
                    ),
                )
            }
        } else {
            Box(
                marker
                    .background(if (status == "process") markerColor else Color.Transparent, RoundedCornerShape(50))
                    .border(1.dp, markerColor, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center,
            ) {
                val glyph = when (status) {
                    "finish" -> "✓"
                    "error" -> "✕"
                    else -> "${index + 1}"
                }
                BasicText(
                    glyph,
                    style = TextStyle(
                        color = if (status == "process") Color.White else markerColor,
                        fontSize = (markerSize.value * 0.6f).sp,
                    ),
                )
            }
        }
        Column {
            BasicText(props.title.upStringValueOrEmpty(), style = TextStyle(color = if (status == "wait") inactiveColor else UPTheme.Main))
            BasicText(props.desc.upStringValueOrEmpty(), style = TextStyle(color = UPTheme.Content))
        }
    }
}
