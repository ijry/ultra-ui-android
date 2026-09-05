package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UPFieldParitySupportTest {
    @Test
    fun overlayAndModalDurationsFallBackToTheirUpstreamDefaults() {
        assertEquals(300, upOverlayFadeDuration(UPOverlayProps().duration))
        assertEquals(0, upOverlayFadeDuration(0))
        assertEquals(120, upOverlayFadeDuration("120"))
        assertEquals(300, upOverlayFadeDuration("fast"))
        assertEquals(0, upOverlayFadeDuration(-40))

        assertEquals(400, upModalTransitionDuration(UPModalProps().duration))
        assertEquals(250, upModalTransitionDuration(250))
        assertEquals(400, upModalTransitionDuration(null))
        assertEquals(0, upModalTransitionDuration(-1))
    }

    @Test
    fun badgeOffsetOnlyAppliesWhenAbsoluteAndReusesTheFirstEntry() {
        assertNull(upBadgeOffsetPair(absolute = false, offset = listOf(4, 6)))
        assertNull(upBadgeOffsetPair(absolute = true, offset = emptyList()))
        assertEquals(4 to 6, upBadgeOffsetPair(absolute = true, offset = listOf(4, 6)))
        // `offset[1] || top` in JavaScript also falls through for 0, "" and null.
        assertEquals(4 to 4, upBadgeOffsetPair(absolute = true, offset = listOf(4)))
        assertEquals(4 to 4, upBadgeOffsetPair(absolute = true, offset = listOf<UPRawValue>(4, 0)))
        assertEquals(4 to 4, upBadgeOffsetPair(absolute = true, offset = listOf<UPRawValue>(4, "")))
        assertEquals(4 to 4, upBadgeOffsetPair(absolute = true, offset = listOf<UPRawValue>(4, null)))
        assertEquals("6px" to "10px", upBadgeOffsetPair(absolute = true, offset = listOf("6px", "10px")))
    }

    @Test
    fun autoBackgroundNeedsBothAPositiveLightnessAndAColor() {
        assertNull(upTagAutoBackgroundColor("#2979ff", UPTagProps().autoBgColor))
        assertNull(upTagAutoBackgroundColor("#2979ff", 0))
        assertNull(upTagAutoBackgroundColor("#2979ff", -5))
        assertNull(upTagAutoBackgroundColor("", 95))
        assertNull(upTagAutoBackgroundColor("   ", 95))
        // `hsl(...)` is not one of the two formats parseColorWithoutDOM understands.
        assertNull(upTagAutoBackgroundColor("hsl(210, 100%, 58%)", 95))
        assertEquals("#e5efff", upTagAutoBackgroundColor("#2979ff", 95))
    }

    @Test
    fun genLightColorKeepsTheHueAndClampsLightnessAtNinetyFivePercent() {
        // Same hue and saturation as #2979ff, lifted to 95% lightness. Every expectation
        // here was produced by running upstream's genLightColor on the same input.
        assertEquals("#e5efff", upGenLightColor("#2979ff"))
        // Anything above 95 is clamped back to 95.
        assertEquals("#e5efff", upGenLightColor("#2979ff", 99.0))
        assertEquals("#ccdfff", upGenLightColor("#2979ff", 90.0))
        assertEquals("#3d86ff", upGenLightColor("#2979ff", 62.0))
        // Near-grey stays near-grey: #303133 has almost no saturation to preserve.
        assertEquals("#f2f2f3", upGenLightColor("#303133"))
        // Shorthand hex and rgb()/rgba() resolve to the same channels.
        assertEquals("#ffe5e5", upGenLightColor("#ff0000"))
        assertEquals("#ffe5e5", upGenLightColor("#f00"))
        assertEquals("#ffe5e5", upGenLightColor("rgb(255, 0, 0)"))
        assertEquals("#ffe5e5", upGenLightColor("rgba(255, 0, 0, 0.5)"))
        assertNull(upGenLightColor("red"))
        assertNull(upGenLightColor("#12345"))
    }

    @Test
    fun longPressTimingsMatchUpstreamsSixHundredAndTwoFiftyMilliseconds() {
        assertEquals(600L, UPNumberBoxLongPressDelayMillis)
        assertEquals(250L, UPNumberBoxLongPressIntervalMillis)
    }

    @Test
    fun fixedNavigationOffsetsMatchTheUpstreamStylesheet() {
        assertEquals(11f, UPNavbarFixedZIndex, 0f)
        assertEquals(20, UPNavbarMiniFixedStartOffsetDp)
        assertEquals(10, UPNavbarMiniFixedTopOffsetDp)
    }
}
