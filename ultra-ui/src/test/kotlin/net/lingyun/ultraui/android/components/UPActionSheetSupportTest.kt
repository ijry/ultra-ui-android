package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The decisions inside `u-action-sheet`: which items are reachable, and what closes it. */
class UPActionSheetSupportTest {
    @Test
    fun aLoadingItemIsAsUnreachableAsADisabledOne() {
        // `if (item && !item.disabled && !item.loading)` — only `disabled` is documented,
        // but `loading` blocks selection too.
        assertTrue(upActionSheetItemEnabled(mapOf("name" to "拍照")))
        assertFalse(upActionSheetItemEnabled(mapOf("name" to "拍照", "disabled" to true)))
        assertFalse(upActionSheetItemEnabled(mapOf("name" to "拍照", "loading" to true)))
        // A plain string action has no flags at all, so it is selectable.
        assertTrue(upActionSheetItemEnabled("拍照"))
    }

    @Test
    fun loadingReplacesTheLabelRatherThanDimmingIt() {
        // `<u-loading-icon v-else>`: the spinner takes the label's place.
        assertTrue(upActionSheetItemLoading(mapOf("loading" to true)))
        assertFalse(upActionSheetItemLoading(mapOf("disabled" to true)))
        assertFalse(upActionSheetItemLoading(mapOf("name" to "拍照")))
    }

    @Test
    fun theDescriptionsTopMarginCollapsesUnderATitle() {
        // `marginTop: ${title && description ? 0 : '18px'}`.
        assertEquals(0f, upActionSheetDescriptionTopMarginPx("标题", "描述"), 1e-4f)
        assertEquals(18f, upActionSheetDescriptionTopMarginPx("", "描述"), 1e-4f)
        assertEquals(18f, upActionSheetDescriptionTopMarginPx("标题", ""), 1e-4f)
    }

    @Test
    fun onlyTheFirstItemRoundsAndOnlyWithoutAHeader() {
        // `getItemHoverStyle(index)`: `index === 0 && round && !title && !description`.
        assertTrue(upActionSheetItemRoundsTop(0, round = 16f, title = "", description = ""))
        assertFalse(upActionSheetItemRoundsTop(1, round = 16f, title = "", description = ""))
        assertFalse(upActionSheetItemRoundsTop(0, round = 0f, title = "", description = ""))
        // A header owns the corners instead, so the item must not round them again.
        assertFalse(upActionSheetItemRoundsTop(0, round = 16f, title = "标题", description = ""))
        assertFalse(upActionSheetItemRoundsTop(0, round = 16f, title = "", description = "描述"))
    }

    @Test
    fun dividersSitBetweenItemsSoTheLastOneHasNone() {
        // `<u-line v-if="index !== actions.length - 1">`.
        assertTrue(upActionSheetItemHasDivider(index = 0, count = 3))
        assertTrue(upActionSheetItemHasDivider(index = 1, count = 3))
        assertFalse(upActionSheetItemHasDivider(index = 2, count = 3))
        // A single item has no divider at all.
        assertFalse(upActionSheetItemHasDivider(index = 0, count = 1))
    }

    @Test
    fun theOverlayOnlyClosesWhenItIsAllowedToButCancelAlwaysDoes() {
        // `closeHandler()` guards on the flag; `cancel()` does not, which is why the cancel
        // button still works with `closeOnClickOverlay = false`.
        assertTrue(upActionSheetOverlayCloses(closeOnClickOverlay = true))
        assertFalse(upActionSheetOverlayCloses(closeOnClickOverlay = false))
        // `slotClickHandler()` follows `closeOnClickAction`, like the items do.
        assertTrue(upActionSheetSlotCloses(closeOnClickAction = true))
        assertFalse(upActionSheetSlotCloses(closeOnClickAction = false))
    }
}
