package net.lingyun.ultraui.android.core

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UPColorTest {
    @Test
    fun parsesNamedHexAndRgbaColorsWithoutThrowing() {
        assertEquals(UPTheme.Primary, UPColor.parse("primary", UPTheme.Info))
        assertEquals(UPTheme.Content, UPColor.parse("u-content-color", UPTheme.Info))
        assertEquals(Color(0xFF336699), UPColor.parse("#369", UPTheme.Info))
        assertEquals(Color(0xCC336699), UPColor.parse("#369c", UPTheme.Info))
        assertEquals(Color(0x99803366), UPColor.parse("#80336699", UPTheme.Info))
        assertEquals(Color(0x80336699), UPColor.parse("rgba(51, 102, 153, 0.5)", UPTheme.Info))
        assertEquals(Color(0xFF336699), UPColor.parse("rgb(51,102,153)", UPTheme.Info))
        assertEquals(UPTheme.Info, UPColor.parse("bad-color", UPTheme.Info))
        assertEquals(UPTheme.Info, UPColor.parse("#3g9", UPTheme.Info))
    }

    @Test
    fun parsesOnlyTheSupportedRightFacingTwoColorGradient() {
        assertEquals(
            UPLinearGradient(start = UPTheme.Primary, end = UPTheme.Success),
            UPColor.parseRightLinearGradient("linear-gradient(to right, primary, success)"),
        )
        assertNull(UPColor.parseRightLinearGradient("linear-gradient(to bottom, primary, success)"))
        assertNull(UPColor.parseRightLinearGradient("linear-gradient(to right, primary, success, error)"))
    }
}
