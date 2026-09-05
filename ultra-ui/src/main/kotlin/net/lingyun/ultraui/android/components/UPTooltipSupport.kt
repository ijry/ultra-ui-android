package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue

/**
 * `u-tooltip`'s `getTooltipStyle()` geometry, its `singleton` registry and the copy
 * feedback it emits. Upstream measures the trigger and the bubble with `$uGetRect` and
 * then positions the bubble by hand; Compose hands the same three rectangles to a
 * `PopupPositionProvider`, so the arithmetic ports directly and stays testable.
 */

/** `screenGap: 12` — how close to the screen edge a repositioned bubble may sit. */
internal const val UPTooltipScreenGapPx: Float = 12f

/** `indicatorWidth: 14` — the arrow is a 14px square rotated 45 degrees. */
internal const val UPTooltipIndicatorWidthPx: Float = 14f

/** `marginTop: '-10px'` / `marginBottom: '-10px'` on the vertical placements. */
internal const val UPTooltipVerticalGapPx: Float = 10f

/**
 * The `direction === 'top' || 'bottom'` branch. Upstream compares half the bubble against
 * the room on each side and, when it does not fit, pins the bubble `screenGap` from that
 * edge instead of centring it.
 *
 * All values are window pixels; the returned left edge is also in window pixels.
 */
internal fun upTooltipBubbleLeftPx(
    triggerLeftPx: Float,
    triggerWidthPx: Float,
    bubbleWidthPx: Float,
    windowWidthPx: Float,
    gapPx: Float = UPTooltipScreenGapPx,
): Float {
    val triggerCentre = triggerLeftPx + triggerWidthPx / 2f
    val triggerRight = triggerLeftPx + triggerWidthPx
    return when {
        // `tooltipInfo.width / 2 > triggerInfo.left + triggerInfo.width / 2 - screenGap`
        bubbleWidthPx / 2f > triggerCentre - gapPx -> gapPx
        // `tooltipInfo.width / 2 > windowWidth - triggerInfo.right + triggerInfo.width / 2 - screenGap`
        bubbleWidthPx / 2f > windowWidthPx - triggerRight + triggerWidthPx / 2f - gapPx ->
            windowWidthPx - gapPx - bubbleWidthPx
        else -> triggerCentre - bubbleWidthPx / 2f
    }
}

/**
 * The arrow keeps pointing at the trigger even after the bubble has been pushed aside:
 * upstream recomputes `indicatorStyle.left` from the trigger centre and the bubble's own
 * left edge. Returns the arrow's left edge relative to the bubble.
 */
internal fun upTooltipIndicatorLeftPx(
    bubbleLeftPx: Float,
    triggerLeftPx: Float,
    triggerWidthPx: Float,
    bubbleWidthPx: Float,
    indicatorWidthPx: Float = UPTooltipIndicatorWidthPx,
): Float {
    val triggerCentre = triggerLeftPx + triggerWidthPx / 2f
    val centred = triggerCentre - bubbleLeftPx - indicatorWidthPx / 2f
    // The arrow cannot leave the bubble's own rounded corners.
    return centred.coerceIn(0f, (bubbleWidthPx - indicatorWidthPx).coerceAtLeast(0f))
}

/**
 * `translateY(-100%)` plus `marginTop: -10px` for `top`, the mirror for `bottom`, and a
 * vertical centring for `left`/`right`. Returns the bubble's top edge in window pixels.
 */
internal fun upTooltipBubbleTopPx(
    direction: String,
    triggerTopPx: Float,
    triggerHeightPx: Float,
    bubbleHeightPx: Float,
    gapPx: Float = UPTooltipVerticalGapPx,
): Float = when (direction) {
    "top" -> triggerTopPx - bubbleHeightPx - gapPx
    "bottom" -> triggerTopPx + triggerHeightPx + gapPx
    // `top: '-' + (triggerInfo.height - tooltipInfo.height) / 2` centres it on the trigger.
    else -> triggerTopPx + (triggerHeightPx - bubbleHeightPx) / 2f
}

/**
 * `right: triggerInfo.width + indicatorWidth` for `left`, and the mirror for `right`.
 * Returns the bubble's left edge in window pixels.
 */
internal fun upTooltipSideBubbleLeftPx(
    direction: String,
    triggerLeftPx: Float,
    triggerWidthPx: Float,
    bubbleWidthPx: Float,
    indicatorWidthPx: Float = UPTooltipIndicatorWidthPx,
): Float = when (direction) {
    "left" -> triggerLeftPx - indicatorWidthPx - bubbleWidthPx
    else -> triggerLeftPx + triggerWidthPx + indicatorWidthPx
}

/** Absolute overrides read from `forcePosition`, in the units the caller supplied. */
internal data class UPForcedPosition(
    val top: UPRawValue = null,
    val left: UPRawValue = null,
    val right: UPRawValue = null,
    val bottom: UPRawValue = null,
) {
    val isEmpty: Boolean get() = top == null && left == null && right == null && bottom == null
}

/**
 * `let styleMerge = {...style, ...this.forcePosition}` — whatever `forcePosition` names
 * wins over the computed placement, and anything it omits keeps the computed value.
 */
internal fun upForcedPosition(forcePosition: UPRawValue): UPForcedPosition {
    val map = forcePosition.upStringKeyMapOrEmpty()
    if (map.isEmpty()) return UPForcedPosition()
    return UPForcedPosition(
        top = map["top"],
        left = map["left"],
        right = map["right"],
        bottom = map["bottom"],
    )
}

/**
 * Resolves one `forcePosition` edge into window pixels. The values are CSS lengths
 * upstream (`{top, left, right, bottom}`), so `px`/`rpx`/bare numbers all apply.
 */
internal fun upForcedEdgePx(value: UPRawValue, density: androidx.compose.ui.unit.Density): Float? {
    if (value == null) return null
    val dp = net.lingyun.ultraui.android.core.UPUnit.parseOrNull(value, androidx.compose.ui.unit.Dp(375f))
        ?: return null
    return with(density) { dp.toPx() }
}

/**
 * `showToast && toast('复制成功' | '复制失败')`. Returns the message the host should show,
 * or `null` when `showToast` is off.
 */
internal fun upTooltipCopyToastMessage(showToast: Boolean, success: Boolean): String? = when {
    !showToast -> null
    success -> "复制成功"
    else -> "复制失败"
}

/**
 * `btnClickHandler(index)`: "如果需要展示复制按钮，此处 index 需要加 1，因为复制按钮在第一个位置".
 * The copy action itself reports 0.
 */
internal fun upTooltipButtonEventIndex(showCopy: Boolean, buttonIndex: Int): Int =
    if (showCopy) buttonIndex + 1 else buttonIndex

/**
 * `let activeSingletonTooltip = null` at module scope: one bubble at a time across the
 * whole page. The registry is process-wide for the same reason, and every entry has to
 * unregister on dispose, which is what upstream's `beforeUnmount` does.
 */
internal object UPTooltipSingletonRegistry {
    private var active: Any? = null

    /** `open()`: closes the previous singleton, then claims the slot. */
    @Synchronized
    fun claim(owner: Any, closePrevious: (Any) -> Unit) {
        val previous = active
        if (previous != null && previous !== owner) closePrevious(previous)
        active = owner
    }

    /** `close()` / `clearActiveTooltip()`: only the current holder may release the slot. */
    @Synchronized
    fun release(owner: Any) {
        if (active === owner) active = null
    }

    @Synchronized
    fun activeOwner(): Any? = active
}
