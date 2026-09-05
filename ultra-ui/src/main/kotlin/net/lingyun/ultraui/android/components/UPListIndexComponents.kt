package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upBooleanOrDefault
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag
import kotlin.math.roundToInt

/**
 * Lets a `UPListItem` publish where its `anchor` sits so the parent can honour
 * `scrollIntoView`. uview does the same through `inject: ['uList']` plus a `$uGetRect`
 * measurement of each item.
 */
@Immutable
internal class UPListScope(private val onAnchorPositioned: (UPRawValue, Int) -> Unit) {
    fun publish(anchor: UPRawValue, top: Int) {
        if (anchor.upStringValueOrEmpty().isEmpty()) return
        onAnchorPositioned(anchor, top)
    }
}

internal val LocalUPList = staticCompositionLocalOf<UPListScope?> { null }

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
    // `:scroll-into-view` scrolls to the child whose anchor matches. The items report
    // their own offsets, because only they know where they ended up after layout.
    val anchorOffsets = remember { mutableMapOf<String, Int>() }
    val listScope = remember {
        UPListScope { anchor, top -> anchorOffsets[anchor.upStringValueOrEmpty()] = top }
    }
    val requestedAnchor = props.scrollIntoView.trim()
    LaunchedEffect(requestedAnchor, props.scrollWithAnimation, anchorOffsets[requestedAnchor]) {
        val target = anchorOffsets[requestedAnchor] ?: return@LaunchedEffect
        if (requestedAnchor.isEmpty()) return@LaunchedEffect
        if (props.scrollWithAnimation) scrollState.animateScrollTo(target) else scrollState.scrollTo(target)
    }

    Column(root) {
        if (props.refresherEnabled) {
            UPListRefresher(props, onRefresherRefresh, onUpdateRefresherTriggered)
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val bodyModifier = if (maxHeight == Dp.Infinity) Modifier else Modifier.verticalScroll(scrollState)
            var bodyTop by remember { mutableStateOf(0) }
            Column(
                bodyModifier.onGloballyPositioned { bodyTop = it.positionInRoot().y.roundToInt() },
            ) {
                CompositionLocalProvider(LocalUPList provides listScope) {
                    // The scroll offset of an item is its distance from the column's own
                    // top, which stays stable while the column scrolls under the viewport.
                    CompositionLocalProvider(LocalUPListBodyTop provides bodyTop) { content() }
                }
                // Anchors the bottom edge so callers and tests can scroll straight to it.
                Box(Modifier.fillMaxWidth().height(1.dp).upTestTag("list-sentinel"))
            }
        }
    }
}

/** Root-space top of the scrolling column, so items can convert their own position. */
internal val LocalUPListBodyTop = staticCompositionLocalOf { 0 }

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
    // `:anchor="`u-list-item-${anchor}`"` is what the parent's `scrollIntoView` targets.
    val list = LocalUPList.current
    val bodyTop = LocalUPListBodyTop.current
    val anchor = props.anchor
    Column(
        modifier
            .fillMaxWidth()
            .then(
                if (list == null || anchor.upStringValueOrEmpty().isEmpty()) {
                    Modifier
                } else {
                    Modifier.onGloballyPositioned { coordinates ->
                        list.publish(anchor, (coordinates.positionInRoot().y.roundToInt() - bodyTop).coerceAtLeast(0))
                    }
                },
            )
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, UPCompatibilityDiagnostics.None, "UPListItem"))
            .upTestTag(
                if (anchor.upStringValueOrEmpty().isEmpty()) {
                    "list-item"
                } else {
                    "list-item-${selectionTagSuffix(anchor)}"
                },
            ),
    ) { content() }
}

@Composable
public fun UPIndexList(
    props: UPIndexListProps = UPIndexListProps(),
    modifier: Modifier = Modifier,
    activeIndex: Int = -1,
    onIndexClick: ((UPRawValue, Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    // uview draws a tappable index rail down the right edge from `indexList`; with no
    // characters there is nothing to anchor, so the rail is omitted entirely.
    val itemMargin = upRawDp(props.itemMargin, 0.dp).coerceAtLeast(0.dp)
    Box(
        modifier.fillMaxWidth()
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPIndexList"))
            .upTestTag("index-list"),
    ) {
        Column(Modifier.fillMaxWidth()) { content() }
        if (props.indexList.isNotEmpty()) {
            Column(
                Modifier.align(Alignment.CenterEnd)
                    // `customNavHeight` shifts the rail clear of a custom navbar, and
                    // `safeBottomFix` keeps its tail above the bottom safe area.
                    .padding(top = upRawDp(props.customNavHeight, 0.dp).coerceAtLeast(0.dp), end = 4.dp)
                    .then(if (props.safeBottomFix) Modifier.navigationBarsPadding() else Modifier)
                    .upTestTag("index-list-rail"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(itemMargin),
            ) {
                props.indexList.forEachIndexed { index, entry ->
                    val color = UPColor.parse(
                        if (index == activeIndex) props.activeColor else props.inactiveColor,
                        if (index == activeIndex) UPTheme.Primary else UPTheme.Content,
                    )
                    BasicText(
                        entry.toString(),
                        modifier = Modifier
                            .upClickable(onClick = { onIndexClick?.invoke(entry, index) })
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .upTestTag("index-list-entry-$index"),
                        style = TextStyle(color = color, fontSize = 12.sp),
                    )
                }
            }
        }
    }
}

@Composable
public fun UPIndexItem(props: UPIndexItemProps = UPIndexItemProps(), modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, UPCompatibilityDiagnostics.None, "UPIndexItem")).upTestTag("index-item")) { content() }
}

@Composable
public fun UPIndexAnchor(props: UPIndexAnchorProps = UPIndexAnchorProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    val label = actionOrOptionText(props.text, "name", props.text.upStringValueOrEmpty())
    Box(modifier.fillMaxWidth().height(net.lingyun.ultraui.android.core.upDimension(props.height, 32.dp)).background(UPColor.parse(props.bgColor, Color(0xFFF1F1F1))).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPIndexAnchor")).padding(horizontal = 16.dp).upTestTag("index-anchor"), contentAlignment = Alignment.CenterStart) {
        BasicText(label, style = TextStyle(color = UPColor.parse(props.color, UPTheme.Content), fontSize = net.lingyun.ultraui.android.core.upDimension(props.size, 14.dp).value.sp))
    }
}

@Composable
public fun UPScrollList(props: UPScrollListProps = UPScrollListProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) {
    // uview scrolls its panel horizontally and shows a progress indicator underneath
    // (`indicator` defaults to true), whose bar tracks the scroll position.
    val scrollState = rememberScrollState()
    val trackWidth = upRawDp(props.indicatorWidth, 50.dp).coerceAtLeast(1.dp)
    val barWidth = upRawDp(props.indicatorBarWidth, 20.dp).coerceIn(1.dp, trackWidth)
    Column(
        modifier.fillMaxWidth()
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPScrollList"))
            .upTestTag("scroll-list"),
    ) {
        Box(Modifier.fillMaxWidth().horizontalScroll(scrollState)) { content() }
        if (props.indicator) {
            val progress = if (scrollState.maxValue > 0) {
                scrollState.value.toFloat() / scrollState.maxValue.toFloat()
            } else {
                0f
            }
            Box(
                Modifier.align(Alignment.CenterHorizontally)
                    .padding(top = 6.dp)
                    .width(trackWidth)
                    .height(barWidth / 4f)
                    .applyUPResolvedStyle(rememberUPResolvedStyle(props.indicatorStyle, diagnostics, "UPScrollList.indicatorStyle"))
                    .background(UPColor.parse(props.indicatorColor, Color(0xFFF2F2F2)), RoundedCornerShape(50))
                    .upTestTag("scroll-list-indicator"),
            ) {
                Box(
                    Modifier.offset(x = (trackWidth - barWidth) * progress.coerceIn(0f, 1f))
                        .width(barWidth)
                        .height(barWidth / 4f)
                        .background(UPColor.parse(props.indicatorActiveColor, UPTheme.Primary), RoundedCornerShape(50))
                        .upTestTag("scroll-list-indicator-bar"),
                )
            }
        }
    }
}
