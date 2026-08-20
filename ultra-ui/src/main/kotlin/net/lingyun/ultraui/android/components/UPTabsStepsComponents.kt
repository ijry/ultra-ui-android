package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
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
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upIntOrDefault
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

private val LocalUPStepsIndex = staticCompositionLocalOf { -1 }

@Composable
public fun UPSteps(props: UPStepsProps = UPStepsProps(), modifier: Modifier = Modifier, onClick: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSteps")).upTestTag("steps")) {
        CompositionLocalProvider(LocalUPStepsIndex provides 0) { content() }
    }
}

@Composable
public fun UPStepsItem(props: UPStepsItemProps = UPStepsItemProps(), modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    Row(modifier.fillMaxWidth().upClickable(enabled = onClick != null, onClick = { onClick?.invoke() }).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPStepsItem")).padding(12.dp).upTestTag("steps-item"), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.width(24.dp).height(24.dp).background(if (props.error) UPTheme.Error else UPTheme.Primary), contentAlignment = Alignment.Center) { BasicText("✓", style = TextStyle(color = Color.White)) }
        Column { BasicText(props.title.upStringValueOrEmpty(), style = TextStyle(color = UPTheme.Main)); BasicText(props.desc.upStringValueOrEmpty(), style = TextStyle(color = UPTheme.Content)) }
    }
}
