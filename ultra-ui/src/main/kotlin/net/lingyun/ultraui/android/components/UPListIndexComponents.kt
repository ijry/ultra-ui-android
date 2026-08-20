package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upBooleanOrDefault
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

@Composable
public fun UPList(props: UPListProps = UPListProps(), modifier: Modifier = Modifier, onScroll: (() -> Unit)? = null, onScrollToLower: (() -> Unit)? = null, onScrollToUpper: (() -> Unit)? = null, onRefresherRefresh: (() -> Unit)? = null, onUpdateRefresherTriggered: ((Boolean) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) {
    val root = modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPList")).upTestTag("list")
    if (props.scrollable) Column(root.verticalScroll(rememberScrollState())) { content() } else Column(root) { content() }
}

@Composable
public fun UPListItem(props: UPListItemProps = UPListItemProps(), modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, UPCompatibilityDiagnostics.None, "UPListItem")).upTestTag("list-item")) { content() }
}

@Composable
public fun UPIndexList(props: UPIndexListProps = UPIndexListProps(), modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, UPCompatibilityDiagnostics.None, "UPIndexList")).upTestTag("index-list")) { content() }
}

@Composable
public fun UPIndexItem(props: UPIndexItemProps = UPIndexItemProps(), modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, UPCompatibilityDiagnostics.None, "UPIndexItem")).upTestTag("index-item")) { content() }
}

@Composable
public fun UPIndexAnchor(props: UPIndexAnchorProps = UPIndexAnchorProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    Box(modifier.fillMaxWidth().height(net.lingyun.ultraui.android.core.upDimension(props.height, 32.dp)).background(UPColor.parse(props.bgColor, Color(0xFFF1F1F1))).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPIndexAnchor")).padding(horizontal = 16.dp).upTestTag("index-anchor"), contentAlignment = Alignment.CenterStart) {
        BasicText(props.text.toString(), style = TextStyle(color = UPColor.parse(props.color, UPTheme.Content), fontSize = net.lingyun.ultraui.android.core.upDimension(props.size, 14.dp).value.sp))
    }
}

@Composable
public fun UPScrollList(props: UPScrollListProps = UPScrollListProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) {
    Box(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPScrollList")).upTestTag("scroll-list")) { content() }
}
