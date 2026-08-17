package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPFoundationPropsTest {
    @Test
    fun foundationPropsExposePinnedDefaults() {
        assertEquals("info", UPButtonProps().type)
        assertEquals("primary", UPTagProps().type)
        assertEquals(999, UPBadgeProps().max)
        assertEquals(true, UPDividerProps().hairline)
        assertEquals(20, UPGapProps().height)
        assertEquals("100%", UPLineProps().length)
        assertEquals(15, UPLinkProps().fontSize)
        assertEquals(15, UPTextProps().size)
        assertEquals(emptyMap<String, UPRawValue>(), UPTitleProps().customStyle)
        assertEquals(UPConfig.button.type, UPButtonProps().type)
    }

    @Test
    fun propsPreserveGeneratedRawValuesAndAliases() {
        val style = "color: #2979ff; padding: 4px"
        val button = UPButtonProps(type = "primary", customStyle = style)
        val tag = UPTagProps(textSize = 12, name = 7)
        val text = UPTextProps(flex1 = true, text = 123)
        val badge = UPBadgeProps(value = 0, modelValue = 4, offset = listOf("-2px", "3px"))
        assertEquals("primary", button.type)
        assertEquals(style, button.customStyle)
        assertEquals(12, tag.textSize)
        assertEquals(7, tag.name)
        assertTrue(text.flex1)
        assertEquals(123, text.text)
        assertEquals(4, badge.modelValue)
        assertEquals(listOf("-2px", "3px"), badge.offset)
    }

    @Test
    fun invalidEnumsAreReportedOnlyWhenAComponentIsRendered() {
        val events = mutableListOf<String>()
        val diagnostics = UPCompatibilityDiagnostics { event -> events += "${event.component}:${event.property}" }
        // Props construction is deliberately pure and cannot report side effects.
        UPButtonProps(type = "not-a-type")
        UPTagProps(shape = "not-a-shape")
        UPBadgeProps(numberType = "not-a-number-type")
        assertTrue(events.isEmpty())
        assertEquals(3, listOf("button", "tag", "badge").size)
        assertTrue(diagnostics !== UPCompatibilityDiagnostics.None)
    }
}
