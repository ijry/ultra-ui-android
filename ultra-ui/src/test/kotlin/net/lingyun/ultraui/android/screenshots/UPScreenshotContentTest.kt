package net.lingyun.ultraui.android.screenshots

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Asserts what the committed reference PNGs actually contain.
 *
 * The progress doc long claimed this renderer "draws neither text nor most fills", which
 * downgraded every reference to mere layout evidence. Decoding the pixels shows the
 * opposite: glyphs, fills and rounded corners are all there — the agent-facing image
 * viewer just downscales a 945px-wide reference until 12sp text vanishes. These tests
 * pin the colours each component is supposed to paint, so a regression that silently
 * stops painting one of them fails here rather than passing a "nothing changed" check.
 */
class UPScreenshotContentTest {
    /** Inclusive pixel bounds, so containment reads the same way as the reference scans. */
    private data class Box(val left: Int, val top: Int, val right: Int, val bottom: Int) {
        val width: Int get() = right - left + 1
        val height: Int get() = bottom - top + 1

        fun contains(other: Box): Boolean =
            other.left >= left && other.right <= right && other.top >= top && other.bottom <= bottom

        fun contains(point: Pair<Int, Int>): Boolean =
            point.first in left..right && point.second in top..bottom

        fun centre(): Pair<Int, Int> = (left + right) / 2 to (top + bottom) / 2

        fun middleThird(): Box = Box(
            left = left + width / 3,
            top = top + height / 3,
            right = right - width / 3,
            bottom = bottom - height / 3,
        )
    }

    private fun IntRange.midpoint(): Int = (first + last) / 2

    /** `Modifier.padding(16.dp)` in the previews, at the 2.625x density they render at. */
    private val PREVIEW_PADDING_PX = 42

    private fun assertRatio(
        label: String,
        measured: Int,
        available: Int,
        expected: Double,
        tolerance: Double = 0.06,
    ) {
        val ratio = measured.toDouble() / available
        assertTrue(
            "$label measured ${measured}px of ${available}px (${"%.2f".format(ratio)}), expected ~$expected",
            Math.abs(ratio - expected) < tolerance,
        )
    }

    @Test
    fun theRendererDrawsTextNotJustLayoutBoxes() {
        val reference = UPScreenshotReference.load("field parity subsection modes")

        // Glyph antialiasing leaves hundreds of one-off blends; flat layout boxes cannot.
        assertTrue(
            "expected antialiased glyph edges, got ${reference.antialiasedFraction()}",
            reference.antialiasedFraction() > 0.001,
        )
        // uview's inactive label colour and the disabled label colour both appear.
        assertTrue("inactive label colour missing", reference.contains("303133"))
        assertTrue("disabled label colour missing", reference.contains("c8c9cc"))
    }

    @Test
    fun subsectionPaintsBothModesWithTheirOwnPalette() {
        val reference = UPScreenshotReference.load("field parity subsection modes")

        // `mode="button"` fills the track with bgColor and floats a white pill in it.
        assertTrue("button track colour missing", reference.contains("eeeeef"))
        // `mode="subsection"` rides on activeColor instead.
        assertTrue("subsection bar colour missing", reference.contains("3c9cff"))
        // The disabled button bar is a lighter grey than the enabled white pill.
        assertTrue("disabled bar colour missing", reference.contains("f5f5f5"))

        // Two button-mode tracks: the enabled one on top, the disabled one at the bottom.
        val trackBands = reference.rowBandsOf("eeeeef")
        assertEquals("expected two button-mode tracks, got $trackBands", 2, trackBands.size)
        // Two subsection-mode rows, each with its own bar position.
        val barBands = reference.rowBandsOf("3c9cff").filter { it.last - it.first > 40 }
        assertEquals("expected two subsection bars, got $barBands", 2, barBands.size)
    }

    @Test
    fun theSubsectionBarSlidesWithTheSelectedIndex() {
        val reference = UPScreenshotReference.load("field parity subsection modes")

        val barBands = reference.rowBandsOf("3c9cff").filter { it.last - it.first > 40 }
        // Sample the middle of each bar so the item borders above and below stay out of it.
        val first = requireNotNull(reference.widestRunInRow("3c9cff", barBands[0].midpoint()))
        val second = requireNotNull(reference.widestRunInRow("3c9cff", barBands[1].midpoint()))

        // current=1 then current=2, so the second bar has to sit further right.
        assertTrue("bar did not advance: $first then $second", second.first > first.first)
        // Both bars are one third of the control wide, give or take rounding and padding.
        val expected = reference.width / 3
        assertTrue(
            "first bar width ${first.last - first.first} is not ~$expected",
            Math.abs((first.last - first.first) - expected) < expected / 4,
        )
        assertTrue(
            "second bar width ${second.last - second.first} is not ~$expected",
            Math.abs((second.last - second.first) - expected) < expected / 4,
        )
        // The two bars advance by exactly one slot.
        assertTrue(
            "expected one slot of travel, got ${second.first - first.first}",
            Math.abs((second.first - first.first) - expected) < expected / 4,
        )
    }

    @Test
    fun tagAutoBgColorPaintsTheDerivedLightBackground() {
        val reference = UPScreenshotReference.load("field parity cell navbar and tag")

        // upTagAutoBackgroundColor("#2979ff", 95) resolves to #e5efff; both the derived
        // background and the text colour it was derived from have to be on screen.
        assertTrue("derived tag background missing", reference.contains("e5efff"))
        assertTrue("tag text colour missing", reference.contains("2979ff"))
        assertTrue(
            "derived background should cover a visible area, got ${reference.countOf("e5efff")}",
            reference.countOf("e5efff") > 1_000,
        )
    }

    @Test
    fun tabsShapeModesEachPaintTheirOwnDecoration() {
        val reference = UPScreenshotReference.load("tabs shapes and pagination")

        // The four shape modes plus the pagination row make this the busiest reference;
        // a flat-fill renderer could not produce this many distinct blends.
        assertTrue(
            "expected a rich palette, got ${reference.colorCounts().size} colours",
            reference.colorCounts().size > 1_000,
        )
        assertTrue(
            "expected antialiased edges, got ${reference.antialiasedFraction()}",
            reference.antialiasedFraction() > 0.005,
        )
        // Four tab strips, so the inactive label colour spans four separate row bands.
        val labelBands = reference.rowBandsOf("606266")
        assertTrue("expected at least four tab rows, got $labelBands", labelBands.size >= 4)
    }

    @Test
    fun theSliderReferenceKeepsTrackAndThumbApart() {
        val reference = UPScreenshotReference.load("motion parity slider geometry")

        // inactiveColor for the track, activeColor for the filled part and the thumb.
        assertTrue("inactive track colour missing", reference.contains("c0c4cc"))
        assertTrue("active track colour missing", reference.contains("2979ff"))
        // Four sliders, so four separate track bands down the image.
        val trackBands = reference.rowBandsOf("c0c4cc")
        assertEquals("expected four slider tracks, got $trackBands", 4, trackBands.size)
        // `height = 8` makes the second track four times thicker than the 2dp default.
        val default = trackBands[0].last - trackBands[0].first
        val thick = trackBands[1].last - trackBands[1].first
        assertTrue("expected a thicker second track: $default then $thick", thick > default * 2)
    }

    @Test
    fun skeletonRowsShrinkExactlyAsRowsWidthAsks() {
        val reference = UPScreenshotReference.load("tail field skeleton select readmore")

        // Every placeholder block shares the same fill, so the bands are avatar + 3 rows.
        val bands = reference.rowBandsOf("e6e8eb")
        assertEquals("expected an avatar row plus three text rows, got $bands", 4, bands.size)

        // rowsWidth = listOf("100%", "80%", "40%") against a 945px-wide preview minus
        // the 16dp padding on both sides.
        val available = reference.width - 2 * PREVIEW_PADDING_PX
        val widths = bands.drop(1).map {
            val run = requireNotNull(reference.widestRunInRow("e6e8eb", it.midpoint()))
            run.last - run.first + 1
        }
        assertRatio("first row", widths[0], available, 1.0)
        assertRatio("second row", widths[1], available, 0.8)
        assertRatio("third row", widths[2], available, 0.4)
    }

    @Test
    fun badgeOffsetPushesTheLabelDownAndIn() {
        val reference = UPScreenshotReference.load("field parity badge offset and number box")

        // Two badges side by side, both painted in the default error colour.
        val columns = reference.columnBandsOf("fa3534", 0 until reference.height)
            .filter { it.last - it.first > 10 }
        assertEquals("expected two badges, got $columns", 2, columns.size)

        // Scan each badge's own column range: a whole-width row scan would merge the two
        // overlapping y ranges into one band and hide the offset entirely.
        val plainRows = reference.rowBandsOf("fa3534", columns[0]).single()
        val offsetRows = reference.rowBandsOf("fa3534", columns[1]).single()

        // offset = listOf(12, 20) is [top, right], so the second badge drops 12dp and
        // pulls 20dp in from the right edge of its own 72dp box.
        val density = reference.densityFor(widthDp = 360)
        val expectedDrop = 12 * density
        assertTrue(
            "expected a ~${expectedDrop.toInt()}px drop, got ${offsetRows.first - plainRows.first}",
            Math.abs((offsetRows.first - plainRows.first) - expectedDrop) < density * 3,
        )
        // Both boxes are 72dp wide with 24dp between them, so an un-offset badge would
        // land exactly 96dp to the right; the shortfall is the 20dp right inset.
        val pitch = 96 * density
        val actualShift = columns[1].first - columns[0].first
        val expectedInset = 20 * density
        assertTrue(
            "expected a ~${expectedInset.toInt()}px right inset, got ${pitch - actualShift}",
            Math.abs((pitch - actualShift) - expectedInset) < density * 3,
        )
    }

    @Test
    fun theNoticeBarAndStickyKeepTheirOwnBackgrounds() {
        val reference = UPScreenshotReference.load("motion parity notice collapse sticky")

        // Two notice bars (row marquee + column carousel) on uview's warning palette.
        val noticeBands = reference.rowBandsOf("fdf6ec")
        assertEquals("expected two notice bars, got $noticeBands", 2, noticeBands.size)
        assertTrue("notice text colour missing", reference.contains("f9ae3d"))
        // The sticky container paints the bgColor the preview passed it.
        val stickyBands = reference.rowBandsOf("f3f4f6")
        assertEquals("expected one sticky band, got $stickyBands", 1, stickyBands.size)
        // Sticky sits below both notice bars, as the preview stacks them.
        assertTrue("sticky should follow the notice bars", stickyBands.single().first > noticeBands.last().last)
    }

    @Test
    fun colSpanAndGridColumnsDivideTheRowEvenly() {
        val reference = UPScreenshotReference.load("u-row col and grid")

        // Three `span = 4` columns, then a `col = 3` grid of six items: the same three
        // theme colours appear twice, the other three only in the grid.
        for (color in listOf("2979ff", "19be6b", "ff9900")) {
            assertEquals(
                "expected $color in both the row and the grid",
                2,
                reference.rowBandsOf(color).size,
            )
        }
        for (color in listOf("fa3534", "6739b6", "909399")) {
            assertTrue("$color missing from the grid", reference.contains(color))
        }

        // Each of the three columns is a third of the content width, minus the 8px gutter.
        val rowBand = reference.rowBandsOf("2979ff").first()
        val widths = listOf("2979ff", "19be6b", "ff9900").map {
            val run = requireNotNull(reference.widestRunInRow(it, rowBand.midpoint(), mergeGap = 8))
            run.last - run.first + 1
        }
        val available = reference.width - 2 * PREVIEW_PADDING_PX
        for ((index, measured) in widths.withIndex()) {
            assertRatio("column $index", measured, available, 1.0 / 3, tolerance = 0.04)
        }
        // ...and they march left to right without overlapping.
        val lefts = listOf("2979ff", "19be6b", "ff9900").map {
            requireNotNull(reference.widestRunInRow(it, rowBand.midpoint(), mergeGap = 8)).first
        }
        assertEquals("columns are out of order: $lefts", lefts.sorted(), lefts)
    }

    @Test
    fun theActionSheetDimsThePageAboveItsPanel() {
        val reference = UPScreenshotReference.load("native action sheet")

        // 45% black over white resolves to #8c8c8c, and it has to stop where the panel
        // starts — a scrim covering the panel too would mean the sheet is behind it.
        val scrim = reference.rowBandsOf("8c8c8c").maxBy { it.last - it.first }
        assertTrue("the scrim should start at the top edge", scrim.first == 0)
        assertTrue(
            "the scrim should cover roughly the upper half, got $scrim",
            scrim.last in (reference.height / 3)..(reference.height * 2 / 3),
        )
        // The panel below it is white and unmistakably tall.
        val panelTop = scrim.last + 1
        assertEquals("the panel should begin right below the scrim", "ffffff", reference.colorAt(reference.width / 2, panelTop + 2))
    }

    @Test
    fun numberBoxDisabledStateRepaintsBothButtonsAndTheField() {
        val reference = UPScreenshotReference.load("u-number-box limits")

        // Three previews stacked: two enabled, one disabled. The enabled rows paint
        // #ebecee behind minus/plus, the disabled row swaps in #f7f8fa.
        val enabled = reference.rowBandsOf("ebecee")
        val disabled = reference.rowBandsOf("f7f8fa")
        assertEquals("expected two enabled number boxes, got $enabled", 2, enabled.size)
        assertEquals("expected the disabled fill in two bands, got $disabled", 2, disabled.size)

        // Both fills split into the same three columns: minus, field, plus.
        for ((color, bands) in listOf("ebecee" to enabled, "f7f8fa" to disabled)) {
            val columns = reference.columnBandsOf(color, bands.last())
                .filter { it.last - it.first > 20 }
            assertEquals("$color should form minus/field/plus, got $columns", 3, columns.size)
        }
        // The disabled preview sits last, below both enabled ones.
        assertTrue("the disabled row should come last", disabled.last().first > enabled.last().first)
    }

    @Test
    fun theSwiperLoadingSlidePaintsItsPlaceholderIcon() {
        val reference = UPScreenshotReference.load("u-swiper loading")

        // `loading = true` fills the slide with bgColor and puts a #909399 glyph in it.
        val slideColumns = reference.columnBandsOf("f3f4f6", 0 until reference.height)
        val slideRows = reference.rowBandsOf("f3f4f6")
        assertTrue("slide background missing", slideColumns.isNotEmpty() && slideRows.isNotEmpty())
        val slide = Box(
            left = slideColumns.first().first,
            top = slideRows.first().first,
            right = slideColumns.last().last,
            bottom = slideRows.last().last,
        )
        // `height = 120` at 2.625 px/dp, so the placeholder slide is a wide band.
        val density = reference.densityFor(widthDp = 320)
        assertRatio("slide height", slide.height, (120 * density).toInt(), 1.0, tolerance = 0.05)

        val glyphColumns = reference.columnBandsOf("909399", 0 until reference.height)
        val glyphRows = reference.rowBandsOf("909399")
        assertTrue("no loading glyph found", glyphColumns.isNotEmpty() && glyphRows.isNotEmpty())
        val glyph = Box(
            left = glyphColumns.first().first,
            top = glyphRows.first().first,
            right = glyphColumns.last().last,
            bottom = glyphRows.last().last,
        )
        // Assert containment rather than an exact centre: a glyph's ink box is offset from
        // its layout box by the font's side bearings, so the ink is never dead centre.
        assertTrue("glyph escapes the slide: $glyph vs $slide", slide.contains(glyph))
        assertTrue(
            "glyph should sit in the middle third of the slide, got $glyph in $slide",
            slide.middleThird().contains(glyph.centre()),
        )
    }

    @Test
    fun everyReferenceIsOpaqueAndNonEmpty() {
        // A blank or transparent reference would still pass validateDebugScreenshotTest,
        // so the floor is checked here instead.
        // Every committed reference, not just this batch's: a blank one anywhere would
        // otherwise sit in the tree indefinitely, quietly passing the "did it change" check.
        val names = UPScreenshotReference.allNames()
        assertTrue("expected the full reference set, found ${names.size}", names.size >= 28)
        for (name in names) {
            val reference = UPScreenshotReference.load(name)
            val counts = reference.colorCounts()
            val white = counts["ffffff"] ?: 0
            val painted = reference.width * reference.height - white
            assertTrue("$name rendered nothing but white", painted > 5_000)
            assertFalse("$name has no colour variety", counts.size < 20)
        }
    }
}
