package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    // uview treats a `height`/`width` of 0 as "unset" and lets the scroll-view size itself.
    val requestedHeight = upRawDp(props.height, 0.dp).takeIf { it > 0.dp }
    val requestedWidth = upRawDp(props.width, 0.dp).takeIf { it > 0.dp }
    val root = modifier
        .then(if (requestedWidth != null) Modifier.width(requestedWidth) else Modifier.fillMaxWidth())
        .then(if (requestedHeight != null) Modifier.height(requestedHeight) else Modifier)
        .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPList"))
        .upTestTag("list")

    if (!props.scrollable) {
        Column(root) { content() }
        return
    }

    val scrollState = rememberScrollState()
    // scrolltolower/scrolltoupper follow uview's threshold semantics: each fires once
    // per crossing, not on every frame while parked at the edge.
    val lowerThreshold = upRawDp(props.lowerThreshold, 50.dp).value
    val upperThreshold = upRawDp(props.upperThreshold, 0.dp).value
    var wasAtLower by remember { mutableStateOf(false) }
    var wasAtUpper by remember { mutableStateOf(true) }
    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        onScroll?.invoke()
        val remaining = (scrollState.maxValue - scrollState.value).toFloat()
        val atLower = scrollState.maxValue > 0 && remaining <= lowerThreshold
        if (atLower && !wasAtLower) onScrollToLower?.invoke()
        wasAtLower = atLower
        val atUpper = scrollState.value.toFloat() <= upperThreshold
        if (atUpper && !wasAtUpper) onScrollToUpper?.invoke()
        wasAtUpper = atUpper
    }
    LaunchedEffect(props.scrollTop, props.scrollWithAnimation) {
        val target = props.scrollTop.upIntOrDefault(-1)
        if (target >= 0) {
            if (props.scrollWithAnimation) scrollState.animateScrollTo(target) else scrollState.scrollTo(target)
        }
    }

    Column(root) {
        if (props.refresherEnabled) {
            UPListRefresher(props, onRefresherRefresh, onUpdateRefresherTriggered)
        }
        Column(Modifier.verticalScroll(scrollState)) {
            content()
            // Anchors the bottom edge so callers and tests can scroll straight to it.
            Box(Modifier.fillMaxWidth().height(1.dp).upTestTag("list-sentinel"))
        }
    }
}

/**
 * uview hands pulling to the platform `scroll-view`. This library depends only on
 * Compose foundation, so the indicator is drawn here instead of pulling in material3
 * for a single control; `refresherTriggered` remains the source of truth.
 */
@Composable
private fun UPListRefresher(
    props: UPListProps,
    onRefresherRefresh: (() -> Unit)?,
    onUpdateRefresherTriggered: ((Boolean) -> Unit)?,
) {
    var triggered by remember { mutableStateOf(props.refresherTriggered) }
    LaunchedEffect(props.refresherTriggered) { triggered = props.refresherTriggered }
    val style = props.refresherDefaultStyle.trim().lowercase()
    Box(
        Modifier.fillMaxWidth()
            .height(upRawDp(props.refresherThreshold, 45.dp).coerceAtLeast(0.dp))
            .background(UPColor.parse(props.refresherBackground, Color.White))
            .upClickable {
                triggered = true
                onUpdateRefresherTriggered?.invoke(true)
                onRefresherRefresh?.invoke()
            }
            .upTestTag("list-refresher"),
        contentAlignment = Alignment.Center,
    ) {
        if (style != "none") {
            UPLoadingIcon(
                UPLoadingIconProps(
                    show = triggered,
                    color = if (style == "white") "#ffffff" else "#909193",
                    size = 18,
                    text = "",
                ),
            )
        }
    }
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
