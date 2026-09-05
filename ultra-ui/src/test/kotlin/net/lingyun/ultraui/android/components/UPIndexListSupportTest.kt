package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** `u-index-list`'s A-Z fallback, its touch-to-letter arithmetic and its anchor ids. */
class UPIndexListSupportTest {
    @Test
    fun anEmptyIndexListFallsBackToTheGeneratedAlphabet() {
        val generated = upIndexListEntries(emptyList())
        assertEquals(26, generated.size)
        assertEquals("A", generated.first())
        assertEquals("Z", generated.last())
        // An explicit list wins outright, however short.
        assertEquals(listOf<Any?>("甲", "乙"), upIndexListEntries(listOf("甲", "乙")))
    }

    @Test
    fun theTouchOffsetMapsOntoOneLetterPerSlice() {
        // A 260px rail starting at y=100 gives 26 letters of 10px each.
        assertEquals(0, upIndexListLetterAt(touchY = 100f, railTop = 100f, railHeight = 260f, count = 26))
        assertEquals(0, upIndexListLetterAt(105f, 100f, 260f, 26))
        assertEquals(1, upIndexListLetterAt(110f, 100f, 260f, 26))
        assertEquals(12, upIndexListLetterAt(225f, 100f, 260f, 26))
        assertEquals(25, upIndexListLetterAt(355f, 100f, 260f, 26))
    }

    @Test
    fun bothEndsClampBecauseAFingerCanTravelPastTheRail() {
        // "对第一和最后一个字母做边界处理" — above the rail sticks to the first letter.
        assertEquals(0, upIndexListLetterAt(-500f, 100f, 260f, 26))
        assertEquals(25, upIndexListLetterAt(9_999f, 100f, 260f, 26))
        // A rail with no letters has nothing to land on.
        assertEquals(-1, upIndexListLetterAt(150f, 100f, 260f, 0))
        // A zero-height rail satisfies `pageY >= top + height` at once, so upstream's own
        // branch order lands on the last letter rather than the first.
        assertEquals(25, upIndexListLetterAt(100f, 100f, 0f, 26))
    }

    @Test
    fun anUnchangedLetterIsDebouncedAway() {
        // `if (currentIndex === this.activeIndex) return` is upstream's anti-jitter guard.
        assertFalse(upIndexListShouldEmit(current = 3, next = 3))
        assertTrue(upIndexListShouldEmit(current = 3, next = 4))
        assertTrue(upIndexListShouldEmit(current = -1, next = 0))
        assertFalse(upIndexListShouldEmit(current = -1, next = -1))
    }

    @Test
    fun theAnchorIdIsTheFirstCharactersCodePoint() {
        // `u-index-item-${'A'.charCodeAt(0)}`.
        assertEquals("u-index-item-65", upIndexListAnchorId("A"))
        assertEquals("u-index-item-66", upIndexListAnchorId("B"))
        // An object entry is addressed through its `name`, as upstream does.
        assertEquals("u-index-item-67", upIndexListAnchorId(mapOf("name" to "C")))
        // `'甲'.charCodeAt(0)` is 30002 (U+7532), transcribed from node rather than guessed.
        assertEquals("u-index-item-30002", upIndexListAnchorId("甲"))
        assertNull(upIndexListAnchorId(""))
        assertNull(upIndexListAnchorId(null))
    }

    @Test
    fun theRailPrintsKeyThenNameForAnObjectEntry() {
        // `{{ item.key || item }}`.
        assertEquals("A", upIndexListEntryLabel("A"))
        assertEquals("K", upIndexListEntryLabel(mapOf("key" to "K", "name" to "N")))
        assertEquals("N", upIndexListEntryLabel(mapOf("name" to "N")))
        assertEquals("", upIndexListEntryLabel(null))
        assertEquals(300L, UPIndexListIndicatorHideDelayMillis)
    }
}
