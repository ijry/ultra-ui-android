package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UPSubsectionSupportTest {
    private val activeColor = "#3c9cff"
    private val inactiveColor = "#303133"

    @Test
    fun modeFallsBackToButtonAndReportsUnknownKeywords() {
        val events = mutableListOf<String>()
        val diagnostics = UPCompatibilityDiagnostics { event -> events += "${event.property}=${event.value}" }
        assertEquals("button", upSubsectionMode("button", diagnostics, "UPSubsection"))
        assertEquals("subsection", upSubsectionMode("subsection", diagnostics, "UPSubsection"))
        assertEquals(emptyList<String>(), events)
        assertEquals("button", upSubsectionMode("segmented", diagnostics, "UPSubsection"))
        assertEquals(listOf("mode=segmented"), events)
    }

    @Test
    fun buttonModeIsTallerPaddedAndPaintsItsTrack() {
        assertEquals(34, upSubsectionHeightDp("button"))
        assertEquals(32, upSubsectionHeightDp("subsection"))
        assertEquals(3, upSubsectionWrapperPaddingDp("button"))
        assertEquals(0, upSubsectionWrapperPaddingDp("subsection"))
        assertEquals("#eeeeef", upSubsectionWrapperColorHex("button", "#eeeeef"))
        assertNull(upSubsectionWrapperColorHex("subsection", "#eeeeef"))
    }

    @Test
    fun barColourSplitsByModeAndDisabledState() {
        assertEquals("#ffffff", upSubsectionBarColorHex("button", disabled = false, activeColor = activeColor))
        assertEquals("#f5f5f5", upSubsectionBarColorHex("button", disabled = true, activeColor = activeColor))
        assertEquals(activeColor, upSubsectionBarColorHex("subsection", disabled = false, activeColor = activeColor))
        assertEquals("#d4d4d4", upSubsectionBarColorHex("subsection", disabled = true, activeColor = activeColor))
    }

    @Test
    fun onlyTheOutlinedModeDrawsItemBorders() {
        assertNull(upSubsectionItemBorderColorHex("button", disabled = false, activeColor = activeColor))
        assertNull(upSubsectionItemBorderColorHex("button", disabled = true, activeColor = activeColor))
        assertEquals(activeColor, upSubsectionItemBorderColorHex("subsection", disabled = false, activeColor = activeColor))
        assertEquals("#d4d4d4", upSubsectionItemBorderColorHex("subsection", disabled = true, activeColor = activeColor))
    }

    @Test
    fun textColourPrefersThePerItemOverrideThenTheModeDefault() {
        // subsection mode paints the active label white on top of the coloured bar.
        assertEquals(
            "#FFFFFF",
            upSubsectionTextColorHex("subsection", active = true, disabled = false, activeOverride = null, inactiveOverride = null, activeColor = activeColor, inactiveColor = inactiveColor),
        )
        // button mode paints it in activeColor instead.
        assertEquals(
            activeColor,
            upSubsectionTextColorHex("button", active = true, disabled = false, activeOverride = null, inactiveOverride = null, activeColor = activeColor, inactiveColor = inactiveColor),
        )
        assertEquals(
            inactiveColor,
            upSubsectionTextColorHex("button", active = false, disabled = false, activeOverride = null, inactiveOverride = null, activeColor = activeColor, inactiveColor = inactiveColor),
        )
        // Per-item overrides win in both modes and for both states.
        assertEquals(
            "#ff0000",
            upSubsectionTextColorHex("subsection", active = true, disabled = false, activeOverride = "#ff0000", inactiveOverride = null, activeColor = activeColor, inactiveColor = inactiveColor),
        )
        assertEquals(
            "#00ff00",
            upSubsectionTextColorHex("button", active = false, disabled = false, activeOverride = null, inactiveOverride = "#00ff00", activeColor = activeColor, inactiveColor = inactiveColor),
        )
        // Disabled beats every override.
        assertEquals(
            "#c8c9cc",
            upSubsectionTextColorHex("subsection", active = true, disabled = true, activeOverride = "#ff0000", inactiveOverride = "#00ff00", activeColor = activeColor, inactiveColor = inactiveColor),
        )
    }

    @Test
    fun itemColourOverrideOnlyReadsTruthyObjectEntries() {
        val entry: UPRawValue = mapOf("name" to "日", "activeColorKey" to "#ff0000", "inactiveColorKey" to "")
        assertEquals("#ff0000", upSubsectionItemColorOverride(entry, "activeColorKey"))
        assertNull(upSubsectionItemColorOverride(entry, "inactiveColorKey"))
        assertNull(upSubsectionItemColorOverride(entry, "missingKey"))
        assertNull(upSubsectionItemColorOverride(entry, ""))
        // A plain string entry has no per-item override at all.
        assertNull(upSubsectionItemColorOverride("日", "activeColorKey"))
    }

    @Test
    fun boldOnlyAppliesToTheActiveEnabledItem() {
        assertTrue(upSubsectionBold(bold = true, active = true, disabled = false))
        assertFalse(upSubsectionBold(bold = true, active = false, disabled = false))
        assertFalse(upSubsectionBold(bold = false, active = true, disabled = false))
        assertFalse(upSubsectionBold(bold = true, active = true, disabled = true))
    }

    @Test
    fun barPositionAndIndexStayInsideTheRenderedItems() {
        assertEquals("single", upSubsectionBarPosition(0, 1))
        assertEquals("first", upSubsectionBarPosition(0, 3))
        assertEquals("center", upSubsectionBarPosition(1, 3))
        assertEquals("last", upSubsectionBarPosition(2, 3))

        assertEquals(0, upSubsectionBarIndex(current = -4, count = 3))
        assertEquals(2, upSubsectionBarIndex(current = 9, count = 3))
        assertEquals(1, upSubsectionBarIndex(current = 1, count = 3))
        assertEquals(0, upSubsectionBarIndex(current = 2, count = 0))
    }

    @Test
    fun barAnimationMatchesTheThreeHundredMillisecondTransition() {
        assertEquals(300, UPSubsectionBarDurationMillis)
    }
}
