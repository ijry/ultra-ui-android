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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag
import androidx.compose.foundation.combinedClickable

@Composable
public fun UPPopover(props: UPPopoverProps = UPPopoverProps(), modifier: Modifier = Modifier, onUpdateShow: ((Boolean) -> Unit)? = null, onOpen: (() -> Unit)? = null, onClose: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: (@Composable () -> Unit)? = null) {
    // uview: triggerMode is hover/click/manual (default click). `u-popover` forwards both
    // `direction` and `placement` to its inner tooltip; `direction` is the field that
    // picks the side, and `placement` only matters once it is left blank.
    val trigger = upSafeEnum(props.triggerMode, setOf("hover", "click", "manual"), "click", diagnostics, "UPPopover", "triggerMode")
    val sides = setOf("top", "bottom", "left", "right")
    val requestedSide = props.direction.ifBlank { props.placement }
    val placement = upSafeEnum(requestedSide, sides, "top", diagnostics, "UPPopover", "direction")
    var visible by remember { mutableStateOf(props.show) }
    LaunchedEffect(props.show) { visible = props.show }
    val trigger0 = content ?: { BasicText(props.text.toString()) }

    fun toggle(next: Boolean) {
        if (visible == next) return
        visible = next
        onUpdateShow?.invoke(next)
        if (next) onOpen?.invoke() else onClose?.invoke()
    }

    val panel: @Composable () -> Unit = {
        Box(
            Modifier.background(UPColor.parse(props.popupBgColor.ifEmpty { props.bgColor }, Color(0xFFF7F7F7)), RoundedCornerShape(4.dp))
                .padding(10.dp)
                .upTestTag("popover-content"),
        ) { BasicText(props.text.toString(), style = TextStyle(color = UPColor.parse(props.color, UPTheme.Main))) }
    }
    val triggerBox: @Composable () -> Unit = {
        Box(
            Modifier.upTestTag("popover-trigger").then(
                when (trigger) {
                    "click" -> Modifier.upClickable(onClick = { toggle(!visible) })
                    // Android has no hover, so uview's hover maps onto long-press.
                    "hover" -> Modifier.combinedClickable(onClick = {}, onLongClick = { toggle(true) })
                    else -> Modifier
                },
            ),
        ) { trigger0() }
    }

    val root = modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPPopover")).upTestTag("popover")
    if (placement == "left" || placement == "right") {
        Row(root, verticalAlignment = Alignment.CenterVertically) {
            if (visible && placement == "left") panel()
            triggerBox()
            if (visible && placement == "right") panel()
        }
    } else {
        Column(root) {
            if (visible && placement == "top") panel()
            triggerBox()
            if (visible && placement == "bottom") panel()
        }
    }
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
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable () -> Unit)? = null,
) {
    val trigger = upSafeEnum(props.triggerMode, setOf("longpress", "click", "manual"), "longpress", diagnostics, "UPTooltip", "triggerMode")
    val direction = upSafeEnum(props.direction, setOf("top", "bottom"), "top", diagnostics, "UPTooltip", "direction")
    var visible by remember { mutableStateOf(props.show) }
    // In manual mode `show` is the only way in or out; gestures are inert.
    LaunchedEffect(props.show) { visible = props.show }

    fun toggle(next: Boolean) {
        if (visible == next) return
        visible = next
        onUpdateShow?.invoke(next)
        if (next) onOpen?.invoke() else onClose?.invoke()
    }

    val bubble: @Composable () -> Unit = {
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
                BasicText("复制", modifier = Modifier.upClickable(onClick = { onCopy?.invoke(payload) }).upTestTag("tooltip-copy"))
            }
            // uview renders `buttons` alongside the copy action as an extension slot.
            props.buttons.forEachIndexed { index, button ->
                BasicText(
                    actionOrOptionText(button, "text", button.toString()),
                    modifier = Modifier.upClickable(onClick = { onButtonClick?.invoke(button, index) }).upTestTag("tooltip-button-$index"),
                    style = TextStyle(color = UPColor.parse(props.color, UPTheme.Content)),
                )
            }
        }
    }

    Column(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTooltip")).upTestTag("tooltip")) {
        // `direction` decides which side of the trigger the bubble occupies.
        if (visible && direction == "top") bubble()
        Box(
            Modifier.upTestTag("tooltip-trigger").then(
                when (trigger) {
                    "click" -> Modifier.upClickable(onClick = { toggle(!visible) })
                    "longpress" -> Modifier.combinedClickable(onClick = {}, onLongClick = { toggle(true) })
                    else -> Modifier
                },
            ),
        ) { content?.invoke() ?: BasicText(props.text.toString()) }
        if (visible && direction == "bottom") bubble()
    }
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
