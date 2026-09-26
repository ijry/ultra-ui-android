package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPCrossAxisAlignment
import net.lingyun.ultraui.android.core.UPFlexGlass
import net.lingyun.ultraui.android.core.upGlassBackgroundAlpha
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPFlexPropsTest {
    @Test
    fun defaultsMatchContract() {
        val p = UPFlexProps()
        assertEquals("row", p.direction)
        assertEquals("flex-start", p.justify)
        assertEquals("stretch", p.align)
        assertFalse(p.wrap)
        assertEquals(0, p.gap)
        assertEquals(null, p.glass)
    }

    @Test
    fun glassDefaults() {
        val g = UPFlexGlass()
        assertFalse(g.enabled)
        assertEquals("regular", g.variant)
        assertFalse(g.interactive)
    }

    @Test
    fun directionHelpers() {
        assertTrue(upFlexIsHorizontal("row"))
        assertTrue(upFlexIsHorizontal("row-reverse"))
        assertFalse(upFlexIsHorizontal("column"))
        assertTrue(upFlexIsReverse("row-reverse"))
        assertTrue(upFlexIsReverse("column-reverse"))
        assertFalse(upFlexIsReverse("row"))
    }

    @Test
    fun crossAxisMapsFlexValuesAndDegradesBaseline() {
        val d = UPCompatibilityDiagnostics.None
        assertEquals(UPCrossAxisAlignment.Start, resolveFlexCrossAxis("flex-start", d, "UPFlex"))
        assertEquals(UPCrossAxisAlignment.End, resolveFlexCrossAxis("flex-end", d, "UPFlex"))
        assertEquals(UPCrossAxisAlignment.Center, resolveFlexCrossAxis("center", d, "UPFlex"))
        assertEquals(UPCrossAxisAlignment.Stretch, resolveFlexCrossAxis("stretch", d, "UPFlex"))
        assertEquals(UPCrossAxisAlignment.Start, resolveFlexCrossAxis("baseline", d, "UPFlex"))
    }

    @Test
    fun glassAlphaVariantOrdering() {
        assertTrue(upGlassBackgroundAlpha("clear") < upGlassBackgroundAlpha("regular"))
    }
}
