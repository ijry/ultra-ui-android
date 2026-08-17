package net.lingyun.ultraui.android.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPComponentSupportTest {
    @Test
    fun nullableModelValueTakesPrecedenceOverLegacyValue() {
        assertEquals("new", resolveUPModelValue("new", "legacy"))
        assertEquals("legacy", resolveUPModelValue(null, "legacy"))
    }

    @Test
    fun malformedRawValuesUseTheRequestedFallback() {
        assertEquals(7, "bad".upIntOrDefault(7))
        assertEquals(3.5f, "bad".upFloatOrDefault(3.5f))
        assertTrue("true".upBooleanOrDefault(false))
        assertFalse("bad".upBooleanOrDefault(false))
    }

    @Test
    fun percentageIsClampedAndReportsMalformedOrOutOfRangeValues() {
        val events = mutableListOf<UPCompatibilityEvent>()
        val diagnostics = UPCompatibilityDiagnostics { events += it }

        assertEquals(0f, upClampPercentage(-4, diagnostics, "u-line-progress"))
        assertEquals(100f, upClampPercentage(140, diagnostics, "u-line-progress"))
        assertEquals(0f, upClampPercentage("bad", diagnostics, "u-circle-progress"))
        assertEquals(3, events.size)
        assertEquals("percentage", events.first().property)
    }

    @Test
    fun allNextBatchDefaultGroupsAreExposed() {
        assertEquals("primary", UPConfig.tag.type)
        assertEquals(999, UPConfig.badge.max)
        assertEquals(14, UPConfig.divider.textSize)
        assertEquals(20, UPConfig.gap.height)
        assertEquals("100%", UPConfig.line.length)
        assertEquals(15, UPConfig.link.fontSize)
        assertEquals(15, UPConfig.text.size)
        assertEquals(10070, UPConfig.overlay.zIndex)
        assertEquals(10075, UPConfig.popup.zIndex)
        assertEquals(10090, UPConfig.toast.zIndex)
        assertEquals("aspectFill", UPConfig.image.mode)
        assertEquals(40, UPConfig.avatar.size)
        assertEquals(5, UPConfig.avatarGroup.maxCount)
        assertEquals("", UPConfig.empty.icon)
        assertEquals("data", UPConfig.empty.mode)
        assertEquals(19, UPConfig.loadingPage.iconSize)
        assertEquals("circle", UPConfig.loadingPage.loadingMode)
        assertEquals("loadmore", UPConfig.loadmore.status)
        assertEquals(140, UPConfig.input.maxlength)
        assertEquals(70, UPConfig.textarea.height)
        assertEquals("round", UPConfig.search.shape)
        assertEquals(6, UPConfig.codeInput.maxlength)
        assertEquals(25, UPConfig.switch.size)
        assertEquals(5, UPConfig.rate.count)
        assertEquals(1, UPConfig.numberBox.min)
        assertFalse(UPConfig.checkbox.checked)
        assertEquals("row", UPConfig.checkboxGroup.placement)
        assertEquals("circle", UPConfig.radioGroup.shape)
        assertEquals(0, UPConfig.row.gutter)
        assertEquals(12, UPConfig.col.span)
        assertEquals(3, UPConfig.grid.col)
        assertEquals(12, UPConfig.lineProgress.height)
        assertEquals(30, UPConfig.circleProgress.percentage)
    }
}
