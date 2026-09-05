package net.lingyun.ultraui.android.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag
import androidx.compose.foundation.combinedClickable

/**
 * Positions a popup at the window origin so its content can span the whole window. The
 * bubble is then placed inside that window by [upTooltipBubbleLeftPx] and friends, which
 * is what lets `zIndex` mean something: the transparent scrim and the bubble live in the
 * same layer, exactly as upstream's `u-overlay` (10070) and bubble (10071) do.
 */
private object UPWindowOriginPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset = IntOffset.Zero
}

@Composable
public fun UPTooltip(
    props: UPTooltipProps = UPTooltipProps(),
    modifier: Modifier = Modifier,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onOpen: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onCopy: ((UPRawValue) -> Unit)? = null,
    onButtonClick: ((UPRawValue, Int) -> Unit)? = null,
    /** `click` carries the button index, with the copy action occupying slot 0. */
    onIndexClick: ((Int) -> Unit)? = null,
    /** `showToast && toast('复制成功' | '复制失败')`; the clipboard itself is the host's. */
    onToast: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable () -> Unit)? = null,
) {
    val trigger = upSafeEnum(props.triggerMode, setOf("longpress", "click", "manual"), "longpress", diagnostics, "UPTooltip", "triggerMode")
    // `getTooltipStyle()` handles four sides, even though the doc comment only lists two.
    val direction = upSafeEnum(props.direction, setOf("top", "bottom", "left", "right"), "top", diagnostics, "UPTooltip", "direction")
    var visible by remember { mutableStateOf(props.show) }
    // In manual mode `show` is the only way in or out; gestures are inert.
    LaunchedEffect(props.show) { visible = props.show }

    // `let activeSingletonTooltip = null` sits at module scope upstream, so the registry has
    // to outlive any single composition. `handle` is this instance's stand-in for `this`:
    // the registry holds it, and closing it flips *that* instance's own state.
    val handle = remember { UPTooltipSingletonHandle() }
    var closedBySingleton by remember { mutableStateOf(false) }

    fun close() {
        UPTooltipSingletonRegistry.release(handle)
        if (!visible) return
        visible = false
        onUpdateShow?.invoke(false)
        onClose?.invoke()
    }

    fun open() {
        // `if (singleton && activeSingletonTooltip !== this) activeSingletonTooltip.close()`.
        if (props.singleton) {
            UPTooltipSingletonRegistry.claim(handle) { previous ->
                (previous as? UPTooltipSingletonHandle)?.requestClose?.invoke()
            }
        }
        if (visible) return
        visible = true
        onUpdateShow?.invoke(true)
        onOpen?.invoke()
    }

    handle.requestClose = { closedBySingleton = true }
    LaunchedEffect(closedBySingleton) {
        if (closedBySingleton) {
            closedBySingleton = false
            close()
        }
    }
    // `beforeUnmount() { this.clearActiveTooltip() }`: a disposed bubble must not keep the
    // singleton slot, or the next `open()` would try to close a gone composition.
    DisposableEffect(handle) { onDispose { UPTooltipSingletonRegistry.release(handle) } }

    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var bubbleWidthPx by remember { mutableFloatStateOf(0f) }
    var bubbleHeightPx by remember { mutableFloatStateOf(0f) }
    val forced = remember(props.forcePosition) { upForcedPosition(props.forcePosition) }
    val density = LocalDensity.current
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val windowWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val windowHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val zIndexValue = props.zIndex.asFiniteFloatOrNull() ?: 10_071f

    val bubble: @Composable () -> Unit = {
        Column(
            Modifier
                .zIndex(zIndexValue)
                .onSizeChanged { bubbleWidthPx = it.width.toFloat(); bubbleHeightPx = it.height.toFloat() }
                .upTestTag("tooltip-bubble"),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // `.u-tooltip__wrapper__popup__indicator`: a 14px square rotated 45 degrees,
            // kept pointing at the trigger even after the bubble is pushed off-centre.
            val indicatorOffset = anchorBounds?.let { anchor ->
                val bubbleLeft = upTooltipBubbleLeftPx(anchor.left, anchor.width, bubbleWidthPx, windowWidthPx)
                with(density) {
                    upTooltipIndicatorLeftPx(
                        bubbleLeftPx = bubbleLeft,
                        triggerLeftPx = anchor.left,
                        triggerWidthPx = anchor.width,
                        bubbleWidthPx = bubbleWidthPx,
                    ).toDp()
                }
            } ?: 0.dp
            val arrow: @Composable () -> Unit = {
                if (props.showCopy || props.buttons.isNotEmpty()) {
                    Box(
                        Modifier
                            .offset(x = indicatorOffset)
                            .size(10.dp)
                            .graphicsLayer { rotationZ = 45f }
                            .background(UPColor.parse(props.popupBgColor.ifEmpty { "#060607" }, Color(0xFF060607)), RoundedCornerShape(2.dp))
                            .upTestTag("tooltip-indicator"),
                    )
                }
            }
            if (direction == "bottom") arrow()
            Row(
                Modifier
                    .background(UPColor.parse(props.popupBgColor.ifEmpty { props.bgColor }, Color.White), RoundedCornerShape(4.dp))
                    .padding(8.dp)
                    .upTestTag("tooltip-content"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BasicText(
                    props.text.toString(),
                    style = TextStyle(color = UPColor.parse(props.color, UPTheme.Content), fontSize = props.size.toString().toFloatOrNull()?.sp ?: 14.sp),
                )
                if (props.showCopy) {
                    // uview copies `copyText` when set and falls back to `text`.
                    val payload = props.copyText.toString().ifEmpty { props.text.toString() }
                    BasicText(
                        "复制",
                        modifier = Modifier.upClickable(onClick = {
                            // `setClipboardData()` closes first, then reports index 0.
                            close()
                            onIndexClick?.invoke(0)
                            onCopy?.invoke(payload)
                            upTooltipCopyToastMessage(props.showToast, success = true)?.let { onToast?.invoke(it) }
                        }).upTestTag("tooltip-copy"),
                    )
                }
                // uview renders `buttons` alongside the copy action as an extension slot.
                props.buttons.forEachIndexed { index, button ->
                    BasicText(
                        actionOrOptionText(button, "text", button.toString()),
                        modifier = Modifier.upClickable(onClick = {
                            // `btnClickHandler` closes the bubble before reporting.
                            close()
                            onButtonClick?.invoke(button, index)
                            onIndexClick?.invoke(upTooltipButtonEventIndex(props.showCopy, index))
                        }).upTestTag("tooltip-button-$index"),
                        style = TextStyle(color = UPColor.parse(props.color, UPTheme.Content)),
                    )
                }
            }
            if (direction == "top") arrow()
        }
    }

    Box(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTooltip")).upTestTag("tooltip")) {
        Box(
            Modifier
                .onGloballyPositioned { anchorBounds = it.boundsInWindow() }
                .upTestTag("tooltip-trigger")
                .then(
                    when (trigger) {
                        "click" -> Modifier.upClickable(onClick = { if (visible) close() else open() })
                        "longpress" -> Modifier.combinedClickable(onClick = {}, onLongClick = { open() })
                        else -> Modifier
                    },
                ),
        ) { content?.invoke() ?: BasicText(props.text.toString()) }
        if (visible) {
            // A window-level `Popup` is what lets the bubble overhang its trigger instead of
            // being clipped by it, and what puts it above the page the way `zIndex` says.
            Popup(
                popupPositionProvider = UPWindowOriginPositionProvider,
                onDismissRequest = { close() },
                properties = PopupProperties(focusable = props.overlay),
            ) {
                Box(Modifier.fillMaxSize()) {
                    // `<u-overlay customStyle="backgroundColor: rgba(0, 0, 0, 0)">`: a fully
                    // transparent scrim whose only jobs are stopping touches from reaching
                    // the page behind ("防止触摸穿透") and closing on tap. Without `overlay`
                    // there is no scrim at all, so touches pass straight through.
                    if (props.overlay) {
                        Box(
                            Modifier
                                .matchParentSize()
                                .zIndex(zIndexValue - 1f)
                                .upClickable(enabled = true, role = null, onClick = { close() })
                                .upTestTag("tooltip-overlay"),
                        )
                    }
                    val anchor = anchorBounds
                    // `tooltipTop: -10000` — upstream parks the bubble off-screen for its
                    // first pass purely to measure it, then positions it. The same two-pass
                    // applies here: until the bubble has a width the placement is unknown,
                    // so it stays invisible rather than flashing at the wrong spot.
                    val placed = anchor != null && bubbleWidthPx > 0f && bubbleHeightPx > 0f
                    val left = if (anchor == null) 0f else if (direction == "left" || direction == "right") {
                        upTooltipSideBubbleLeftPx(direction, anchor.left, anchor.width, bubbleWidthPx)
                    } else {
                        upTooltipBubbleLeftPx(anchor.left, anchor.width, bubbleWidthPx, windowWidthPx)
                    }
                    val top = if (anchor == null) {
                        0f
                    } else {
                        upTooltipBubbleTopPx(direction, anchor.top, anchor.height, bubbleHeightPx)
                    }
                    // `{...style, ...this.forcePosition}`: a named edge overrides the computed
                    // one, and an edge it omits keeps the computed value.
                    val forcedLeft = upForcedEdgePx(forced.left, density)
                        ?: upForcedEdgePx(forced.right, density)?.let { windowWidthPx - it - bubbleWidthPx }
                    val forcedTop = upForcedEdgePx(forced.top, density)
                        ?: upForcedEdgePx(forced.bottom, density)?.let { windowHeightPx - it - bubbleHeightPx }
                    Box(
                        Modifier
                            .offset(
                                x = with(density) { (forcedLeft ?: left).toDp() },
                                y = with(density) { (forcedTop ?: top).toDp() },
                            )
                            .zIndex(zIndexValue)
                            .alpha(if (placed) 1f else 0f),
                    ) { bubble() }
                }
            }
        }
    }
}

/** Registry entry: its identity is the "instance", and `requestClose` closes that one. */
private class UPTooltipSingletonHandle {
    var requestClose: (() -> Unit)? = null
}

@Composable
public fun UPPopover(
    props: UPPopoverProps = UPPopoverProps(),
    modifier: Modifier = Modifier,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onOpen: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onIndexClick: ((Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable () -> Unit)? = null,
) {
    // `u-popover` is `<up-tooltip>` with the content slot filled and no copy button, so it
    // forwards its own props onto the tooltip rather than laying anything out itself.
    // `triggerMode` is hover/click/manual (default click) there; Android has no hover, so
    // uview's hover maps onto long-press.
    val trigger = upSafeEnum(props.triggerMode, setOf("hover", "click", "manual"), "click", diagnostics, "UPPopover", "triggerMode")
    // `:direction="direction" :placement="placement"`: both are forwarded, and `direction`
    // is the one the tooltip reads, so `placement` only matters once `direction` is blank.
    val requestedSide = props.direction.ifBlank { props.placement }
    val placement = upSafeEnum(requestedSide, setOf("top", "bottom", "left", "right"), "top", diagnostics, "UPPopover", "direction")
    UPTooltip(
        props = UPTooltipProps(
            text = props.text,
            color = props.color,
            bgColor = props.bgColor,
            popupBgColor = props.popupBgColor,
            direction = placement,
            triggerMode = if (trigger == "hover") "longpress" else trigger,
            show = props.show,
            zIndex = props.zIndex,
            forcePosition = props.forcePosition,
            // The popover has no copy button; its bubble is the content slot alone.
            showCopy = false,
            customStyle = props.customStyle,
        ),
        modifier = modifier.upTestTag("popover"),
        onUpdateShow = onUpdateShow,
        onOpen = onOpen,
        onClose = onClose,
        onIndexClick = onIndexClick,
        diagnostics = diagnostics,
        content = content,
    )
}

@Composable
public fun UPSticky(props: UPStickyProps = UPStickyProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) {
    // uview offsets the sticky band by `offsetTop + customNavHeight`
    // (`u-sticky.vue`: stickyTop = getPx(offsetTop) + getPx(customNavHeight)).
    // `disabled` skips the whole sticky treatment, so neither the top offset nor the
    // stacking applies (`style()` returns an empty object upstream).
    val stickyTop = if (props.disabled) {
        0.dp
    } else {
        (upRawDp(props.offsetTop, 0.dp) + upRawDp(props.customNavHeight, 0.dp)).coerceAtLeast(0.dp)
    }
    Box(
        modifier.background(UPColor.parse(props.bgColor, Color.Transparent))
            // `uZindex` defaults to `zIndex.sticky` (970) and only a truthy `zIndex` overrides it.
            .then(if (props.disabled) Modifier else Modifier.zIndex(upStickyZIndex(props.zIndex)))
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSticky"))
            .upTestTag("sticky")
            .padding(top = stickyTop),
    ) { content() }
}

/**
 * Coordinates sibling rows. uview closes any other open row when one opens
 * (`autoClose`) and surfaces "something is open" through `opendItem:update`.
 */
private class UPSwipeActionScope(val autoClose: Boolean, val onOpenChanged: (Boolean) -> Unit) {
    private var closers = mutableListOf<() -> Unit>()
    private var openCount = 0

    fun register(close: () -> Unit): () -> Unit {
        closers.add(close)
        return { closers.remove(close) }
    }

    fun notifyOpened(self: () -> Unit, sweep: Boolean) {
        val closeOthers = sweep && autoClose
        if (closeOthers) closers.filter { it !== self }.forEach { it() }
        openCount = if (closeOthers) 1 else openCount + 1
        onOpenChanged(true)
    }

    fun notifyClosed() {
        openCount = (openCount - 1).coerceAtLeast(0)
        if (openCount == 0) onOpenChanged(false)
    }

    /** uview's `closeAll`, reached by setting `opendItem` to false. */
    fun closeAll() {
        if (closers.isEmpty()) return
        closers.toList().forEach { it() }
        openCount = 0
        onOpenChanged(false)
    }
}

private val LocalUPSwipeAction = staticCompositionLocalOf<UPSwipeActionScope?> { null }

@Composable
public fun UPSwipeAction(
    props: UPSwipeActionProps = UPSwipeActionProps(),
    modifier: Modifier = Modifier,
    onUpdateOpendItem: ((Boolean) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    val scope = remember(props.autoClose) {
        UPSwipeActionScope(props.autoClose) { open -> onUpdateOpendItem?.invoke(open) }
    }
    // uview watches `opendItem`: setting it false closes every row (`closeAll`).
    LaunchedEffect(props.opendItem) { if (!props.opendItem) scope.closeAll() }
    Column(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSwipeAction")).upTestTag("swipe-action")) {
        CompositionLocalProvider(LocalUPSwipeAction provides scope) { content() }
    }
}

@Composable
public fun UPSwipeActionItem(
    props: UPSwipeActionItemProps = UPSwipeActionItemProps(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue, Int) -> Unit)? = null,
    onOpen: ((UPRawValue) -> Unit)? = null,
    onClose: ((UPRawValue) -> Unit)? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onUpdateScrolling: ((Boolean) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    // uview's `show` is the open state, not "render the buttons": the row stays closed
    // until a horizontal drag passes `threshold`, then slides the actions into view.
    var opened by remember { mutableStateOf(props.show) }
    LaunchedEffect(props.show) { opened = props.show }
    val threshold = props.threshold.rawFloat(20f).coerceAtLeast(1f)
    val duration = props.duration.rawInt(300).coerceAtLeast(0)
    var dragged by remember { mutableFloatStateOf(0f) }

    // Register with the parent so `autoClose` can collapse this row when a sibling opens.
    // A swept row still reports closing to its caller, but must not touch the parent's
    // open count — the sweep already resets that to 1 for the row being opened.
    val parent = LocalUPSwipeAction.current
    val closeSelf: () -> Unit = remember {
        {
            if (opened) {
                opened = false
                onUpdateShow?.invoke(false)
                onClose?.invoke(props.name)
            }
        }
    }
    DisposableEffect(parent, closeSelf) {
        val unregister = parent?.register(closeSelf)
        onDispose { unregister?.invoke() }
    }

    fun setOpen(next: Boolean) {
        if (opened == next) return
        opened = next
        onUpdateShow?.invoke(next)
        if (next) {
            // `autoClose` on the item decides whether opening it sweeps the siblings.
            parent?.notifyOpened(closeSelf, sweep = props.autoClose)
            onOpen?.invoke(props.name)
        } else {
            parent?.notifyClosed()
            onClose?.invoke(props.name)
        }
    }

    val row = modifier.fillMaxWidth()
        .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSwipeActionItem"))
        .upTestTag("swipe-action-item")
        .then(
            if (props.disabled) {
                Modifier
            } else {
                Modifier.pointerInput(threshold) {
                    detectHorizontalDragGestures(
                        // `scrolling` is uview's v-model flag for pausing outer scroll
                        // while a row is being dragged sideways.
                        onDragStart = { onUpdateScrolling?.invoke(true) },
                        onDragEnd = {
                            // Only a drag beyond the threshold counts as a toggle,
                            // matching uview; anything shorter snaps back.
                            if (-dragged >= threshold) setOpen(true) else if (dragged >= threshold) setOpen(false)
                            dragged = 0f
                            onUpdateScrolling?.invoke(false)
                        },
                        onDragCancel = {
                            dragged = 0f
                            onUpdateScrolling?.invoke(false)
                        },
                    ) { _, delta -> dragged += delta }
                }
            },
        )

    Row(row, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f)) { content() }
        // `duration` drives the reveal so the row does not jump open instantly.
        AnimatedVisibility(
            visible = opened,
            enter = expandHorizontally(animationSpec = tween(duration)),
            exit = shrinkHorizontally(animationSpec = tween(duration)),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                props.options.forEachIndexed { index, option ->
                    Box(
                        Modifier.background(UPTheme.Error)
                            .upClickable(enabled = !props.disabled, onClick = {
                                onClick?.invoke(option, index)
                                // closeOnClick defaults to true upstream.
                                if (props.closeOnClick) setOpen(false)
                            })
                            .padding(14.dp)
                            .upTestTag("swipe-action-option-$index"),
                    ) {
                        BasicText(actionOrOptionText(option, "text", option.toString()), style = TextStyle(color = Color.White))
                    }
                }
            }
        }
    }
}
