package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.resolveUPModelValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UPSelectionPropsTest {
    @Test
    fun groupsAcceptTheGeneratedColumnScopeContentSignature() {
        val content: @Composable ColumnScope.() -> Unit = {}
        // This is a compile-time API contract used by generated Compose source.
        assertNotNull(content)
    }

    @Test
    fun selectionPropsExposePinnedUviewDefaults() {
        val switch = UPSwitchProps()
        assertEquals(25, switch.size)
        assertEquals("#2979ff", switch.activeColor)
        assertEquals("#ffffff", switch.inactiveColor)
        assertEquals(true, switch.activeValue)
        assertEquals(false, switch.inactiveValue)
        assertFalse(switch.asyncChange)

        val rate = UPRateProps()
        assertEquals(1, rate.value)
        assertEquals(5, rate.count)
        assertEquals(4, rate.gutter)
        assertEquals(1, rate.minCount)
        assertFalse(rate.allowHalf)
        assertEquals("star-fill", rate.activeIcon)
        assertEquals("star", rate.inactiveIcon)

        val number = UPNumberBoxProps()
        assertEquals(0, number.value)
        assertEquals(1, number.min)
        assertEquals(9007199254740991L, number.max)
        assertEquals(1, number.step)
        assertFalse(number.integer)
        assertTrue(number.showMinus)
        assertTrue(number.showPlus)

        val checkbox = UPCheckboxProps()
        assertFalse(checkbox.checked)
        // Leaf checkbox props stay empty so a surrounding checkbox-group can
        // supply shape, size, and colors; the standalone renderer provides
        // uview's safe visual fallbacks.
        assertEquals("", checkbox.shape)
        assertEquals("", checkbox.size)
        assertEquals("", checkbox.activeColor)
        assertEquals("", checkbox.inactiveColor)
        assertFalse(checkbox.usedAlone)

        val checkboxGroup = UPCheckboxGroupProps()
        assertEquals(null, checkboxGroup.modelValue)
        assertEquals(emptyList<UPRawValue>(), checkboxGroup.value)
        assertEquals("row", checkboxGroup.placement)
        assertEquals("left", checkboxGroup.iconPlacement)

        val radio = UPRadioProps()
        // Empty leaf props are resolved from a parent group by the renderer.
        assertEquals(UPConfig.radio.shape, radio.shape)
        assertEquals(UPConfig.radio.size, radio.size)

        val radioGroup = UPRadioGroupProps()
        assertEquals(null, radioGroup.modelValue)
        assertEquals("", radioGroup.value)
        assertEquals("circle", radioGroup.shape)
        assertEquals("10px", radioGroup.gap)
    }

    @Test
    fun selectionPropsPreserveModelAliasesRawValuesAndStyles() {
        val style: Map<String, UPRawValue> = mapOf("padding" to "4px", "color" to "#2979ff")
        val switch = UPSwitchProps(modelValue = "on", value = "legacy", customStyle = style)
        val rate = UPRateProps(modelValue = "2.5", value = 1, customStyle = style)
        val number = UPNumberBoxProps(modelValue = "3.50", value = 1, decimalLength = 2, customStyle = style)
        val checkboxGroup = UPCheckboxGroupProps(modelValue = listOf("a"), value = listOf("legacy"), customStyle = style)
        val radioGroup = UPRadioGroupProps(modelValue = "b", value = "legacy", customStyle = style)

        assertEquals("on", resolveUPModelValue(switch.modelValue, switch.value))
        assertEquals("2.5", resolveUPModelValue(rate.modelValue, rate.value))
        assertEquals("3.50", resolveUPModelValue(number.modelValue, number.value))
        assertEquals(listOf("a"), checkboxGroup.modelValue)
        assertEquals("b", resolveUPModelValue(radioGroup.modelValue, radioGroup.value))
        assertEquals(style, switch.customStyle)
        assertEquals(style, rate.customStyle)
        assertEquals(style, number.customStyle)
        assertEquals(style, checkboxGroup.customStyle)
        assertEquals(style, radioGroup.customStyle)
    }

    @Test
    fun directPropsAreSafeToConstructWithGeneratedStringEnums() {
        val props = UPRateProps(
            count = "7",
            gutter = "6px",
            minCount = "2",
            activeIcon = "custom-star",
            inactiveIcon = "custom-empty",
        )
        assertEquals("7", props.count)
        assertEquals("6px", props.gutter)
        assertEquals("2", props.minCount)
        assertNotNull(props.customStyle)
    }
}

@Composable
private fun compileGeneratedGroupCall(content: @Composable ColumnScope.() -> Unit) {
    UPCheckboxGroup(content = content)
    UPRadioGroup(content = content)
}

@Composable
private fun compileGeneratedDirectSelectionCalls() {
    // Compile-time public API contracts for generated Compose source that
    // chooses the direct-argument form instead of allocating Props objects.
    UPRate(
        value = 2.5f,
        modifier = Modifier,
        onInput = {},
        onChange = {},
        diagnostics = UPCompatibilityDiagnostics.None,
    )
    UPNumberBox(
        value = 2,
        modifier = Modifier,
        onInput = {},
        onChange = {},
        onOverlimit = {},
        onPlus = {},
        onMinus = {},
        diagnostics = UPCompatibilityDiagnostics.None,
    )
}
