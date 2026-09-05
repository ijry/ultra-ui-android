package net.lingyun.ultraui.android.components

import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UPTabsPaginationImageSupportTest {
    @Test
    fun shapeModeKeepsBlankSilentAndReportsUnknownKeywords() {
        val events = mutableListOf<String>()
        val diagnostics = UPCompatibilityDiagnostics { event -> events += "${event.property}=${event.value}" }
        assertEquals("", upTabsShapeMode("", diagnostics, "UPTabs"))
        assertEquals("", upTabsShapeMode("   ", diagnostics, "UPTabs"))
        assertEquals(emptyList<String>(), events)
        assertEquals("capsule", upTabsShapeMode("capsule", diagnostics, "UPTabs"))
        assertEquals("pill-arrow", upTabsShapeMode("pill-arrow", diagnostics, "UPTabs"))
        assertEquals(emptyList<String>(), events)
        assertEquals("", upTabsShapeMode("ribbon", diagnostics, "UPTabs"))
        assertEquals(listOf("shapeMode=ribbon"), events)
    }

    @Test
    fun onlyCardAndThePlainNavKeepTheSlidingLine() {
        assertTrue(upTabsShowLine(""))
        assertTrue(upTabsShowLine("card"))
        assertFalse(upTabsShowLine("capsule"))
        assertFalse(upTabsShowLine("pill-arrow"))
        assertFalse(upTabsShowLine("tag"))
    }

    @Test
    fun itemStyleCountsAsDeclaredOnlyWhenItDiffersFromTheDefault() {
        assertFalse(upTabsItemStyleDeclared(UPTabsProps().itemStyle))
        assertFalse(upTabsItemStyleDeclared(mapOf("height" to "44px")))
        assertFalse(upTabsItemStyleDeclared(emptyMap<String, UPRawValue>()))
        assertFalse(upTabsItemStyleDeclared(null))
        assertFalse(upTabsItemStyleDeclared("   "))
        assertTrue(upTabsItemStyleDeclared(mapOf("height" to "60px")))
        assertTrue(upTabsItemStyleDeclared("height: 60px"))
    }

    @Test
    fun shapeMetricsMirrorTheUpstreamScss() {
        assertEquals(30.dp, upTabsShapeItemHeight("capsule"))
        assertEquals(34.dp, upTabsShapeItemHeight("card"))
        assertEquals(32.dp, upTabsShapeItemHeight("pill-arrow"))
        assertEquals(28.dp, upTabsShapeItemHeight("tag"))
        assertNull(upTabsShapeItemHeight(""))

        assertEquals(11.dp, upTabsShapeItemPadding(""))
        assertEquals(14.dp, upTabsShapeItemPadding("capsule"))
        assertEquals(0.dp, upTabsShapeItemPadding("card"))
        assertEquals(12.dp, upTabsShapeItemPadding("pill-arrow"))

        assertEquals(0.dp, upTabsShapeItemRadius(""))
        assertEquals(999.dp, upTabsShapeItemRadius("capsule"))
        assertEquals(10.dp, upTabsShapeItemRadius("card"))
        assertEquals(8.dp, upTabsShapeItemRadius("pill-arrow"))

        assertEquals(0.dp, upTabsShapeItemSpacing("capsule"))
        assertEquals(8.dp, upTabsShapeItemSpacing("pill-arrow"))
        assertEquals(8.dp, upTabsShapeItemSpacing("tag"))

        assertEquals(3.dp, upTabsShapeWrapperPadding("capsule"))
        assertEquals(0.dp, upTabsShapeWrapperPadding("card"))

        assertEquals(0.dp to 6.dp, upTabsShapeNavPadding("pill-arrow"))
        assertEquals(2.dp to 2.dp, upTabsShapeNavPadding("tag"))
        assertEquals(0.dp to 0.dp, upTabsShapeNavPadding(""))
    }

    @Test
    fun shapeColorsSplitWrapperItemAndActiveBackgrounds() {
        assertEquals("#edf0f5", upTabsShapeWrapperColor("capsule"))
        assertEquals("#9ccde5", upTabsShapeWrapperColor("card"))
        assertNull(upTabsShapeWrapperColor("tag"))
        assertEquals(999.dp, upTabsShapeWrapperRadius("capsule"))
        assertEquals(10.dp, upTabsShapeWrapperRadius("card"))
        assertEquals(0.dp, upTabsShapeWrapperRadius("pill-arrow"))

        assertEquals("#e8e8e8", upTabsShapeItemColor("pill-arrow"))
        assertEquals("#f3f4f6", upTabsShapeItemColor("tag"))
        assertNull(upTabsShapeItemColor("capsule"))

        assertEquals(listOf("#ffffff"), upTabsShapeActiveColors("capsule"))
        assertEquals(listOf("#f6f8fb"), upTabsShapeActiveColors("card"))
        assertEquals(listOf("#ff6c57", "#ff3b30"), upTabsShapeActiveColors("pill-arrow"))
        assertEquals(listOf("#2a6bf6"), upTabsShapeActiveColors("tag"))
        assertEquals(emptyList<String>(), upTabsShapeActiveColors(""))
    }

    @Test
    fun textColorPrefersDisabledThenTheCallerStyleThenTheShapeDefault() {
        val defaults = UPTabsProps()
        assertEquals(
            "#c8c9cc",
            upTabsTextColorHex("tag", isActive = true, disabled = true, activeStyle = mapOf("color" to "#ff0000"), inactiveStyle = defaults.inactiveStyle),
        )
        assertEquals(
            UPTabsDefaultActiveColor,
            upTabsTextColorHex("", isActive = true, disabled = false, activeStyle = defaults.activeStyle, inactiveStyle = defaults.inactiveStyle),
        )
        assertEquals(
            UPTabsDefaultInactiveColor,
            upTabsTextColorHex("pill-arrow", isActive = false, disabled = false, activeStyle = defaults.activeStyle, inactiveStyle = defaults.inactiveStyle),
        )
        assertEquals(
            "#ffffff",
            upTabsTextColorHex("pill-arrow", isActive = true, disabled = false, activeStyle = defaults.activeStyle, inactiveStyle = defaults.inactiveStyle),
        )
        assertEquals(
            "#ffffff",
            upTabsTextColorHex("tag", isActive = true, disabled = false, activeStyle = defaults.activeStyle, inactiveStyle = defaults.inactiveStyle),
        )
        assertEquals(
            UPTabsDefaultActiveColor,
            upTabsTextColorHex("capsule", isActive = true, disabled = false, activeStyle = defaults.activeStyle, inactiveStyle = defaults.inactiveStyle),
        )
        assertEquals(
            "#00aa00",
            upTabsTextColorHex("tag", isActive = true, disabled = false, activeStyle = mapOf("color" to "#00aa00"), inactiveStyle = defaults.inactiveStyle),
        )
        assertEquals(
            "#123456",
            upTabsTextColorHex("", isActive = false, disabled = false, activeStyle = defaults.activeStyle, inactiveStyle = "color: #123456; font-size: 12px"),
        )
    }

    @Test
    fun styleColorReadsMapsAndDeclarationStrings() {
        assertEquals("#ff0000", upStyleColorOrNull(mapOf("Color" to "#ff0000")))
        assertEquals("#ff0000", upStyleColorOrNull("font-size: 12px; color:#ff0000"))
        assertNull(upStyleColorOrNull(mapOf("height" to "44px")))
        assertNull(upStyleColorOrNull("font-size: 12px"))
        assertNull(upStyleColorOrNull(null))
        assertNull(upStyleColorOrNull(42))
    }

    @Test
    fun lineOffsetCentersTheSliderInsideTheActiveItem() {
        val widths = listOf(80f, 100f, 60f)
        assertEquals(30f, upTabsLineOffset(widths, 0, 20f), 0.001f)
        assertEquals(120f, upTabsLineOffset(widths, 1, 20f), 0.001f)
        assertEquals(200f, upTabsLineOffset(widths, 2, 20f), 0.001f)
        assertEquals(200f, upTabsLineOffset(widths, 9, 20f), 0.001f)
        assertEquals(0f, upTabsLineOffset(emptyList(), 0, 20f), 0.001f)
        assertEquals(0f, upTabsLineOffset(emptyList(), 3, 20f), 0.001f)
        assertEquals(0f, upTabsLineOffset(listOf(0f, 0f), 1, 20f), 0.001f)
    }

    @Test
    fun itemBadgeFallsBackToTheBadgeDefaultsAndKeepsTheTemplateMargin() {
        val defaults = UPBadgeProps()
        val mapped = upTabsItemBadgeProps(mapOf("value" to 5, "isDot" to false, "bgColor" to "#ff0000"))
        assertTrue(mapped.show)
        assertEquals("5", mapped.value)
        assertEquals("#ff0000", mapped.bgColor)
        assertEquals(defaults.max, mapped.max)
        assertEquals(defaults.type, mapped.type)
        assertEquals(defaults.shape, mapped.shape)
        assertEquals(mapOf("marginLeft" to "4px"), mapped.customStyle)
        assertTrue(upTabsItemBadgeProps(mapOf("isDot" to true)).isDot)
        assertEquals(9, upTabsItemBadgeProps(mapOf("max" to 9)).max)
    }

    @Test
    fun badgeVisibilityFollowsShowValueAndDotFlags() {
        assertFalse(upTabsBadgeVisible(emptyMap()))
        assertFalse(upTabsBadgeVisible(mapOf("value" to 0)))
        assertFalse(upTabsBadgeVisible(mapOf("value" to "")))
        assertTrue(upTabsBadgeVisible(mapOf("value" to 3)))
        assertTrue(upTabsBadgeVisible(mapOf("isDot" to true)))
        assertTrue(upTabsBadgeVisible(mapOf("show" to true)))
    }

    @Test
    fun totalPagesRoundsUpAndNeverDropsBelowOne() {
        assertEquals(1, upPaginationTotalPages(0, 10))
        assertEquals(1, upPaginationTotalPages(9, 10))
        assertEquals(5, upPaginationTotalPages(42, 10))
        assertEquals(4, upPaginationTotalPages(40, 10))
        assertEquals(1, upPaginationTotalPages(42, 0))
    }

    @Test
    fun displayedPagesCollapseTheMiddleWithEllipsisMarkers() {
        assertEquals(listOf(1), upPaginationDisplayedPages(0, 1))
        assertEquals(listOf(1, 2, 3, 4), upPaginationDisplayedPages(4, 2))
        assertEquals(listOf<UPRawValue>(1, 2, 3, 4, "...", 10), upPaginationDisplayedPages(10, 1))
        assertEquals(listOf<UPRawValue>(1, 2, 3, 4, "...", 10), upPaginationDisplayedPages(10, 2))
        assertEquals(listOf<UPRawValue>(1, "...", 4, 5, 6, "...", 10), upPaginationDisplayedPages(10, 5))
        assertEquals(listOf<UPRawValue>(1, "...", 7, 8, 9, 10), upPaginationDisplayedPages(10, 9))
        assertEquals(listOf<UPRawValue>(1, "...", 7, 8, 9, 10), upPaginationDisplayedPages(10, 10))
        assertEquals(listOf<UPRawValue>(1, 2, 3, 4, "...", 10), upPaginationDisplayedPages(10, 0))
    }

    @Test
    fun pageSizesAcceptNumbersStringsAndLabeledObjects() {
        val defaults = upNormalizedPageSizes(UPPaginationProps().pageSizes)
        assertEquals(listOf("10条/页", "20条/页", "30条/页", "40条/页", "50条/页"), defaults.map { it.first })
        assertEquals(listOf(10, 20, 30, 40, 50), defaults.map { it.second })

        val mixed = upNormalizedPageSizes(listOf("15", mapOf("label" to "每页 25", "value" to 25), mapOf("value" to 35), null, "abc"))
        assertEquals(listOf("15条/页" to 15, "每页 25" to 25, "35条/页" to 35), mixed)
    }

    @Test
    fun pageSizeLookupsFallBackToTheFirstOptionAndTheRawLabel() {
        val options = upNormalizedPageSizes(listOf(10, 20, 30))
        assertEquals(1, upPageSizeIndex(options, 20))
        assertEquals(0, upPageSizeIndex(options, 99))
        assertEquals("20条/页", upPageSizeLabel(options, 20))
        assertEquals("99", upPageSizeLabel(options, 99))
        assertEquals(30, upPageSizeAt(options, 2))
        assertEquals(10, upPageSizeAt(options, 7))
        assertNull(upPageSizeAt(emptyList(), 0))
        assertNull(upPageSizeAt(listOf("0条/页" to 0), 0))
    }

    @Test
    fun layoutPartsTrimAndLowercaseEachDeclaration() {
        assertEquals(setOf("prev", "pager", "next"), upPaginationLayoutParts(UPPaginationProps().layout))
        assertEquals(setOf("total", "sizes", "prev", "pager", "next"), upPaginationLayoutParts(" Total , SIZES ,prev, pager, next , "))
        assertEquals(emptySet<String>(), upPaginationLayoutParts("  "))
    }

    @Test
    fun fadeDurationHonorsTheDocumentedPropAndZeroesOutWhenFadeIsOff() {
        assertEquals(500, upImageFadeDuration(fade = true, duration = UPImageProps().duration))
        assertEquals(0, upImageFadeDuration(fade = false, duration = 800))
        assertEquals(800, upImageFadeDuration(fade = true, duration = 800))
        assertEquals(800, upImageFadeDuration(fade = true, duration = "800"))
        assertEquals(0, upImageFadeDuration(fade = true, duration = -50))
        assertEquals(500, upImageFadeDuration(fade = true, duration = "abc"))
    }
}
