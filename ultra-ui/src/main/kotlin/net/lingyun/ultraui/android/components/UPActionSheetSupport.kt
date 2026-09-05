package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue

/** The parts of `u-action-sheet` that are decisions rather than layout. */

/**
 * `selectHandler(index)`: `if (item && !item.disabled && !item.loading)`. A loading item is
 * as unreachable as a disabled one, which is easy to miss because only `disabled` is named
 * in the docs.
 */
internal fun upActionSheetItemEnabled(action: UPRawValue): Boolean {
    val map = action.upStringKeyMapOrEmpty()
    return !map["disabled"].upBooleanValue(false) && !map["loading"].upBooleanValue(false)
}

/** `v-else` on the loading icon: a loading item shows a spinner *instead of* its name. */
internal fun upActionSheetItemLoading(action: UPRawValue): Boolean =
    action.upStringKeyMapOrEmpty()["loading"].upBooleanValue(false)

/**
 * `marginTop: ${title && description ? 0 : '18px'}`: the description hugs the title when
 * both are present, and otherwise keeps its own top margin.
 */
internal fun upActionSheetDescriptionTopMarginPx(title: String, description: String): Float =
    if (title.isNotEmpty() && description.isNotEmpty()) 0f else 18f

/**
 * `getItemHoverStyle(index)`: the first item inherits the sheet's rounded top corners, but
 * only when there is no header above it to own them.
 */
internal fun upActionSheetItemRoundsTop(index: Int, round: Float, title: String, description: String): Boolean =
    index == 0 && round > 0f && title.isEmpty() && description.isEmpty()

/**
 * `<u-line v-if="index !== actions.length - 1">`: dividers sit *between* items, so the last
 * one has none.
 */
internal fun upActionSheetItemHasDivider(index: Int, count: Int): Boolean = index < count - 1

/**
 * `closeHandler()` is `u-popup`'s `close`, and it only forwards when `closeOnClickOverlay`
 * is set — so a sheet with the flag off cannot be dismissed by the scrim at all. `cancel()`
 * is unconditional, which is why the cancel button always works.
 */
internal fun upActionSheetOverlayCloses(closeOnClickOverlay: Boolean): Boolean = closeOnClickOverlay

/** `slotClickHandler()`: tapping custom content closes the sheet when `closeOnClickAction`. */
internal fun upActionSheetSlotCloses(closeOnClickAction: Boolean): Boolean = closeOnClickAction
