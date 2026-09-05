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

    /** Sorts and unions overlapping or near-touching ranges, so glyphs count once. */
    private fun List<IntRange>.mergeAdjacent(gap: Int): List<IntRange> {
        if (isEmpty()) return emptyList()
        val sorted = sortedBy { it.first }
        val merged = ArrayList<IntRange>()
        var current = sorted.first()
        for (range in sorted.drop(1)) {
            current = if (range.first - current.last <= gap) {
                current.first..Math.max(current.last, range.last)
            } else {
                merged += current
                range
            }
        }
        merged += current
        return merged
    }

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
    fun lineProgressFillsExactlyItsPercentage() {
        val reference = UPScreenshotReference.load("u-line and circle progress")

        // Four bars at 0 / 50 / 100 / 65-from-right. The unfilled groove keeps #ebeef5,
        // so a 100% bar has no groove left and a 0% bar has no fill.
        val grooves = reference.rowBandsOf("ebeef5")
        val fills = reference.rowBandsOf("2979ff")
        assertEquals("expected a groove on the 0/50/65 bars, got $grooves", 3, grooves.size)
        assertEquals("expected a fill on the 50/100/65 bars, got $fills", 3, fills.size)

        val available = reference.width - 2 * PREVIEW_PADDING_PX
        // Bar 1 (0%): groove spans the full width, no fill on that row.
        val zeroGroove = requireNotNull(reference.widestRunInRow("ebeef5", grooves[0].midpoint()))
        assertRatio("0% groove", zeroGroove.last - zeroGroove.first + 1, available, 1.0, tolerance = 0.02)
        // Bar 2 (50%): fill and groove split the width down the middle.
        val halfFill = requireNotNull(reference.widestRunInRow("2979ff", fills[0].midpoint()))
        assertRatio("50% fill", halfFill.last - halfFill.first + 1, available, 0.5, tolerance = 0.03)
        // Bar 3 (100%): the fill takes everything, so no groove row lines up with it.
        val fullFill = requireNotNull(reference.widestRunInRow("2979ff", fills[1].midpoint()))
        assertRatio("100% fill", fullFill.last - fullFill.first + 1, available, 1.0, tolerance = 0.02)
        // Bar 4 (65%, fromRight): the fill hugs the right edge instead of the left.
        val rightFill = requireNotNull(reference.widestRunInRow("2979ff", fills[2].midpoint()))
        assertRatio("65% fill", rightFill.last - rightFill.first + 1, available, 0.65, tolerance = 0.03)
        assertTrue(
            "fromRight should anchor the fill to the right edge, got $rightFill",
            rightFill.last >= reference.width - PREVIEW_PADDING_PX - 4,
        )
        assertTrue("fromRight should leave a gap on the left", rightFill.first > PREVIEW_PADDING_PX * 4)
    }

    @Test
    fun circleProgressLeavesAnUnfilledArcBelowOneHundred() {
        val reference = UPScreenshotReference.load("u-line and circle progress")

        // Two rings: 30% keeps a #c8c8c8 remainder, 100% is entirely #19be6b.
        val ring = reference.rowBandsOf("19be6b").last()
        val remainder = reference.rowBandsOf("c8c8c8").last()
        assertTrue("the two rings should share a row band", remainder.first in (ring.first - 8)..(ring.last))

        val filledColumns = reference.columnBandsOf("19be6b", ring).filter { it.last - it.first > 20 }
        val remainderColumns = reference.columnBandsOf("c8c8c8", ring).filter { it.last - it.first > 20 }
        assertEquals("expected both rings to show progress, got $filledColumns", 2, filledColumns.size)
        assertEquals("only the 30% ring keeps a remainder, got $remainderColumns", 1, remainderColumns.size)
        // The remainder belongs to the left-hand ring, and the right one is fully filled.
        assertTrue(
            "the remainder should sit on the first ring: $remainderColumns vs $filledColumns",
            remainderColumns.single().first < filledColumns.last().first,
        )
    }

    @Test
    fun switchAndRateSeparateSelectedFromUnselected() {
        val reference = UPScreenshotReference.load("u-selection switch and rate")

        // Row one: `modelValue = false` leaves a white track, `true` paints activeColor.
        // Only one switch is on, so the primary colour forms a single cluster up there.
        val switchRows = 0 until reference.height / 3
        val onTrack = reference.columnBandsOf("2979ff", switchRows).filter { it.last - it.first > 20 }
        assertEquals("expected exactly one switch to be on, got $onTrack", 1, onTrack.size)
        assertTrue("the on switch should be the right-hand one", onTrack.single().first > reference.width / 2)

        // Row two: three rate controls of five stars each — 0, 2.5 with allowHalf, then 5.
        val rateRows = reference.rowBandsOf("ff9f0a").single()
        val lit = reference.columnBandsOf("ff9f0a", rateRows)
        val dim = reference.columnBandsOf("c8c9cc", rateRows)
        // Merge across both palettes so the glyphs are counted, not the colour runs: a
        // half star is one glyph carrying both colours and must not count twice.
        val stars = (lit + dim).mergeAdjacent(gap = 10)
        assertEquals("expected fifteen star glyphs, got ${stars.size}: $stars", 15, stars.size)

        fun litWithin(star: IntRange) = lit.any { it.first >= star.first && it.last <= star.last }
        fun dimWithin(star: IntRange) = dim.any { it.first >= star.first && it.last <= star.last }

        // modelValue = 0: nothing lit.
        assertTrue("the first control should be empty", stars.take(5).none(::litWithin))
        // modelValue = 5: nothing left dim.
        assertTrue("the last control should be full", stars.takeLast(5).none(::dimWithin))
        // modelValue = 2.5 with allowHalf: two solid, one split, two empty.
        val middle = stars.subList(5, 10)
        assertTrue("stars 1-2 of the half control should be lit", middle.take(2).all(::litWithin))
        assertTrue("star 3 should carry both colours", litWithin(middle[2]) && dimWithin(middle[2]))
        assertTrue("stars 4-5 of the half control should be empty", middle.drop(3).none(::litWithin))
        // ...and the lit part of the split glyph really is about half of it.
        val split = middle[2]
        val litPart = requireNotNull(lit.firstOrNull { it.first >= split.first && it.last <= split.last })
        assertRatio("half star fill", litPart.last - litPart.first + 1, split.last - split.first + 1, 0.5, tolerance = 0.08)
    }

    @Test
    fun checkboxAndRadioPaintOnlyTheCheckedMarks() {
        val reference = UPScreenshotReference.load("u-checkbox and radio placements")

        // activeColor fills a checked mark; inactiveColor only outlines an unchecked one,
        // so the checked marks are solid blocks and far fewer than the outlines.
        assertTrue("no checked mark found", reference.countOf("2979ff") > 500)
        assertTrue("no unchecked outline found", reference.countOf("c8c9cc") > 200)
        assertTrue(
            "labels should dominate the ink, got ${reference.countOf("303133")}",
            reference.countOf("303133") > reference.countOf("2979ff"),
        )
    }

    @Test
    fun iconLabelsKeepTheirPerIconColour() {
        val reference = UPScreenshotReference.load("u-icon labels")

        // Three coloured icons plus the default label colour: an icon font that failed to
        // load would leave the label text but drop every glyph colour.
        for (color in listOf("19be6b", "ff9900", "2979ff")) {
            assertTrue("icon colour $color missing", reference.contains(color))
        }
        assertTrue("label colour missing", reference.contains("606266"))
        assertTrue(
            "expected antialiased glyph edges, got ${reference.antialiasedFraction()}",
            reference.antialiasedFraction() > 0.004,
        )
    }

    @Test
    fun pickerMaskStyleTintsTheWholeColumnIncludingTheSelectedRow() {
        val reference = UPScreenshotReference.load("picker toolbar right slot and mask")

        // `maskStyle = rgba(0, 0, 0, 0.06)` covers the column, so every colour underneath
        // it comes out multiplied by 0.94. Both the selected row's #eaf3ff highlight and
        // the plain white background have to show their tinted form — and their untinted
        // form must be gone, which is what proves the mask sits *above* the options.
        assertTrue("tinted highlight #dce4f0 missing", reference.contains("dce4f0"))
        assertTrue("tinted background #f0f0f0 missing", reference.contains("f0f0f0"))
        assertEquals("the untinted highlight should be covered", 0, reference.countOf("eaf3ff"))

        // The tinted highlight is one contiguous band, sitting inside the tinted column.
        val highlight = reference.rowBandsOf("dce4f0").single()
        val column = reference.rowBandsOf("f0f0f0").maxBy { it.last - it.first }
        assertTrue(
            "the highlight should border the tinted column: $highlight vs $column",
            highlight.last + 1 == column.first || column.last + 1 == highlight.first,
        )
    }

    @Test
    fun theStatusBarKeepsItsOwnHeightAndColour() {
        val reference = UPScreenshotReference.load("batch 9b navigation and status")

        // `UPStatusBar(bgColor = "#f3f4f6", height = 8)` is the only #f3f4f6 in the preview.
        val band = reference.rowBandsOf("f3f4f6").single()
        val density = reference.densityFor(widthDp = 360)
        assertRatio("status bar height", band.last - band.first + 1, (8 * density).toInt(), 1.0, tolerance = 0.15)

        // The subsection below it paints its own track, so the two never merge.
        val track = reference.rowBandsOf("eeeeef").maxBy { it.last - it.first }
        assertTrue("the subsection track should follow the status bar", track.first > band.last)
    }

    @Test
    fun calendarMarksTheDefaultDateAndTheSliderFillsBelowIt() {
        val reference = UPScreenshotReference.load("batch 10 calendar and slider")

        // `defaultDate = "2026-08-20"` fills one day cell with activeColor; the slider
        // under it uses the same colour for its filled track, so there are two bands.
        val bands = reference.rowBandsOf("3c9cff")
        assertEquals("expected a selected day and a slider fill, got $bands", 2, bands.size)

        // The day cell is a small square; the slider spans most of the content width.
        val dayRun = requireNotNull(reference.widestRunInRow("3c9cff", bands[0].midpoint()))
        val sliderRun = requireNotNull(reference.widestRunInRow("3c9cff", bands[1].midpoint()))
        assertTrue(
            "the slider fill should be far wider than a day cell: $dayRun vs $sliderRun",
            (sliderRun.last - sliderRun.first) > (dayRun.last - dayRun.first) * 5,
        )
        // `value = 65` of 100, so the fill stops well short of the right edge.
        val available = reference.width - 2 * PREVIEW_PADDING_PX
        assertTrue(
            "a 65% slider should not reach the right edge, got $sliderRun of $available",
            sliderRun.last < reference.width - PREVIEW_PADDING_PX,
        )
    }

    @Test
    fun cascaderHighlightsTheSelectedPathInEveryColumn() {
        val reference = UPScreenshotReference.load("batch 10 cascader and tabbar")

        // `modelValue = ["zhejiang", "hangzhou"]` selects one option per column, and both
        // highlights share a row, so the band splits into two wide clusters.
        val highlight = reference.rowBandsOf("eaf3ff").single()
        // Scan a single row: the two highlights span the same rows, so a band-wide column
        // scan would union them into one 42..902 run and lose the split.
        val clusters = reference.runsInRow("eaf3ff", highlight.midpoint()).filter { it.last - it.first > 100 }
        assertEquals("expected one highlight per column, got $clusters", 2, clusters.size)
        // Two columns of equal share, so the clusters are roughly the same width.
        val widths = clusters.map { it.last - it.first + 1 }
        assertTrue("columns should be even, got $widths", Math.abs(widths[0] - widths[1]) < widths[0] / 3)

        // `dot = true` on the second tabbar item paints a red badge below the cascader.
        val dot = reference.rowBandsOf("fa3534").single()
        assertTrue("the tabbar dot should sit below the cascader", dot.first > highlight.last)
    }

    @Test
    fun alertAndNotifyKeepTheirOwnTypeColours() {
        val reference = UPScreenshotReference.load("native alert notify backtop")

        // `type = "warning"` gives the alert uview's warningLight background and warning
        // foreground; the notify below it defaults to the primary fill.
        val alert = reference.rowBandsOf("fdf6ec").single()
        val notify = reference.rowBandsOf("2979ff").single()
        assertTrue("warning icon/text colour missing", reference.contains("ff9900"))
        assertTrue("the notify should sit below the alert: $alert then $notify", notify.first > alert.last)

        // Both are full-width banners, not inline chips.
        val available = reference.width - 2 * PREVIEW_PADDING_PX
        val alertRun = requireNotNull(reference.widestRunInRow("fdf6ec", alert.midpoint()))
        assertRatio("alert width", alertRun.last - alertRun.first + 1, available, 1.0, tolerance = 0.05)
    }

    @Test
    fun theCollapsedNoticeBarStillPaintsItsBanner() {
        val reference = UPScreenshotReference.load("native card collapse dropdown")

        // The notice bar at the bottom keeps its warning palette even after the card and
        // collapse rows above it, so a layout collapse cannot swallow it silently.
        val notice = reference.rowBandsOf("fdf6ec").single()
        assertTrue("notice text colour missing", reference.contains("f9ae3d"))
        assertTrue("the notice should sit in the lower half", notice.first > reference.height / 2)

        // Card and collapse rows draw uview's border colour, in several separate bands.
        val borders = reference.rowBandsOf("e4e7ed").filter { it.last - it.first > 20 }
        assertTrue("expected several bordered rows, got $borders", borders.size >= 2)
        assertTrue("borders should precede the notice", borders.first().first < notice.first)
    }

    @Test
    fun loadingIconModesEachKeepTheirColourAndOrientation() {
        val reference = UPScreenshotReference.load("u-loading-icon modes")

        // Three spinners in a row: spinner/#2979ff, semicircle/#19be6b, circle/#fa3534.
        // Each colour has to survive on its own — one shared tint would mean `color`
        // stopped reaching the glyph.
        for (color in listOf("2979ff", "19be6b", "fa3534")) {
            assertTrue("loading colour $color missing", reference.contains(color))
        }

        // The two `vertical = true` ones put their label below the glyph, so their glyph
        // colour sits strictly above the topmost default-coloured label pixel.
        val labelTop = reference.rowBandsOf("909399").first().first
        for (color in listOf("19be6b", "fa3534")) {
            val glyph = reference.rowBandsOf(color).single()
            assertTrue("$color glyph should be above its label: $glyph vs $labelTop", glyph.last < labelTop)
        }
        // ...and the three sit side by side, left to right in declaration order.
        val lefts = listOf("2979ff", "19be6b", "fa3534").map {
            reference.columnBandsOf(it, 0 until reference.height).first().first
        }
        assertEquals("loading icons are out of order: $lefts", lefts.sorted(), lefts)
    }

    @Test
    fun swiperPeekingMarginsShrinkTheActiveSlide() {
        val plain = UPScreenshotReference.load("u-swiper text slides")
        val peeking = UPScreenshotReference.load("u-swiper peeking margins")

        // Without margins the slide fills the content width; `previousMargin` and
        // `nextMargin` of 24 each pull it in, so the same 320dp preview yields a narrower
        // slide — and `upSwiperItemScale` shrinks it vertically too.
        val plainRows = plain.rowBandsOf("f3f4f6").single()
        val peekRows = peeking.rowBandsOf("e8eaec").single()
        val plainRun = requireNotNull(plain.widestRunInRow("f3f4f6", plainRows.midpoint()))
        val peekRun = requireNotNull(peeking.widestRunInRow("e8eaec", peekRows.midpoint()))

        // Both previews are 320dp wide with 16dp padding, so the plain slide is full width.
        val available = plain.width - 2 * PREVIEW_PADDING_PX
        assertRatio("plain slide", plainRun.last - plainRun.first + 1, available, 1.0, tolerance = 0.03)
        // The peeking preview's own bgColor proves it is the margin variant, not a re-render.
        assertTrue("expected the peeking bgColor #e8eaec", peeking.contains("e8eaec"))
        assertEquals("the plain preview should not use the peeking bgColor", 0, plain.countOf("e8eaec"))
    }

    @Test
    fun theSwiperTitleBarDarkensTheBottomOfTheSlide() {
        val reference = UPScreenshotReference.load("u-swiper title bar")

        // `showTitle = true` lays a translucent black bar over the slide's lower edge, so
        // the slide's own #f3f4f6 gives way to a darker blend further down.
        // The slide's own fill shows twice: above the bar, and again in the rounded corners
        // below it, so take the tall band rather than expecting a single one.
        val slide = reference.rowBandsOf("f3f4f6").maxBy { it.last - it.first }
        val bar = reference.rowBandsOf("a9aaac").single()
        assertTrue("the title bar should start below the slide's top: $bar vs $slide", bar.first > slide.first)
        assertTrue("the title bar should follow the slide's fill", bar.first >= slide.last - 4)

        // It spans the whole slide, not just the text.
        val barRun = requireNotNull(reference.widestRunInRow("a9aaac", bar.midpoint()))
        val slideRun = requireNotNull(reference.widestRunInRow("f3f4f6", slide.midpoint()))
        assertRatio(
            "title bar width",
            barRun.last - barRun.first + 1,
            slideRun.last - slideRun.first + 1,
            1.0,
            tolerance = 0.03,
        )
    }

    @Test
    fun thePickerInputTriggersRenderAsReadOnlyFields() {
        val trigger = UPScreenshotReference.load("picker has input triggers")
        val datetime = UPScreenshotReference.load("datetime picker has input trigger")

        // `hasInput = true` swaps the inline panel for a bordered field, so uview's border
        // colour is present and the wheel's #eaf3ff highlight is not.
        for (reference in listOf(trigger, datetime)) {
            assertTrue("${reference.name}: no bordered field", reference.contains("dadbde") || reference.contains("e4e7ed"))
            assertEquals("${reference.name}: the wheel should stay closed", 0, reference.countOf("eaf3ff"))
            assertTrue(
                "${reference.name}: expected rendered text, got ${reference.antialiasedFraction()}",
                reference.antialiasedFraction() > 0.002,
            )
        }
    }

    @Test
    fun theInlinePickerHighlightsItsSelectedRow() {
        val reference = UPScreenshotReference.load("batch 9b status and picker")

        // No `maskStyle` here, so the selected row keeps its untinted #eaf3ff highlight —
        // the exact opposite of the masked preview above.
        assertTrue("the selected row highlight is missing", reference.countOf("eaf3ff") > 1_000)
        // A handful of #dce4f0 pixels turn up as glyph antialiasing, so compare magnitudes
        // rather than demanding an exact zero: an actual mask would tint the whole column.
        assertTrue(
            "the highlight should be untinted, got ${reference.countOf("dce4f0")} tinted pixels",
            reference.countOf("dce4f0") * 100 < reference.countOf("eaf3ff"),
        )

        // The skeleton placeholders above it use their own fill, and come first.
        val skeleton = reference.rowBandsOf("e6e8eb")
        val highlight = reference.rowBandsOf("eaf3ff").single()
        assertTrue("expected skeleton rows above the picker, got $skeleton", skeleton.isNotEmpty())
        assertTrue("the picker should follow the skeleton", highlight.first > skeleton.last().last)
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
