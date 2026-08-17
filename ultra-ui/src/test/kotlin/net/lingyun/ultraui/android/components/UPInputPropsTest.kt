package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.resolveUPModelValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class UPInputPropsTest {
    @Test
    fun inputAndTextareaPropsExposePinnedUviewDefaults() {
        val input = UPInputProps()
        val textarea = UPTextareaProps()

        assertEquals(UPConfig.input.type, input.type)
        assertEquals(140, input.maxlength)
        assertEquals("surround", input.border)
        assertEquals("#53c21d", input.cursorColor)
        assertFalse(input.clearable)
        assertFalse(input.onlyClearableOnFocused)
        assertFalse(input.passwordVisibilityToggle)

        assertEquals("", textarea.value)
        assertEquals(70, textarea.height)
        assertEquals("return", textarea.confirmType)
        assertTrue(textarea.showConfirmBar)
        assertEquals(140, textarea.maxlength)
        assertEquals("surround", textarea.border)
    }

    @Test
    fun inputAndTextareaPreserveRawModelAliasesStylesAndFormatter() {
        val formatter: (String) -> String = { it.filter(Char::isDigit) }
        val style: Map<String, UPRawValue> = mapOf("padding" to "4px", "color" to "#2979ff")
        val input = UPInputProps(
            modelValue = "modern",
            value = "legacy",
            placeholderStyle = style,
            prefixIconStyle = "color: #2979ff",
            suffixIconStyle = style,
            formatter = formatter,
            customStyle = "margin: 4px",
        )
        val textarea = UPTextareaProps(
            modelValue = 101,
            value = "legacy",
            placeholderStyle = style,
            formatter = formatter,
            customStyle = style,
        )

        assertEquals("modern", resolveUPModelValue(input.modelValue, input.value))
        assertEquals(101, resolveUPModelValue(textarea.modelValue, textarea.value))
        assertEquals(style, input.placeholderStyle)
        assertEquals(style, input.suffixIconStyle)
        assertSame(formatter, input.formatter)
        assertSame(formatter, textarea.formatter)
        assertEquals(style, textarea.customStyle)
    }

    @Test
    fun searchPropsKeepTheDeliberateClearabledSpellingAndAllStyles() {
        val inputStyle: Map<String, UPRawValue> = mapOf("fontSize" to 13)
        val actionStyle = "color: #2979ff"
        val props = UPSearchProps(
            modelValue = "keyword",
            value = "legacy",
            clearabled = false,
            label = 9,
            inputStyle = inputStyle,
            actionStyle = actionStyle,
            customStyle = inputStyle,
            maxlength = 24,
        )

        assertEquals("round", UPSearchProps().shape)
        assertTrue(UPSearchProps().clearabled)
        assertEquals("搜索", UPSearchProps().actionText)
        assertEquals("keyword", resolveUPModelValue(props.modelValue, props.value))
        assertFalse(props.clearabled)
        assertEquals(9, props.label)
        assertEquals(inputStyle, props.inputStyle)
        assertEquals(actionStyle, props.actionStyle)
        assertEquals(24, props.maxlength)
    }

    @Test
    fun codeInputPropsPreserveAliasAndNativeControlFields() {
        val props = UPCodeInputProps(
            adjustPosition = false,
            maxlength = 4,
            dot = true,
            mode = "line",
            hairline = true,
            space = "12px",
            modelValue = 5678,
            value = "legacy",
            focus = true,
            bold = true,
            disabledKeyboard = true,
            disabledDot = false,
            customStyle = "margin-top: 6px",
        )

        assertTrue(UPCodeInputProps().adjustPosition)
        assertEquals(6, UPCodeInputProps().maxlength)
        assertFalse(UPCodeInputProps().dot)
        assertEquals("box", UPCodeInputProps().mode)
        assertTrue(UPCodeInputProps().disabledDot)
        assertEquals(5678, resolveUPModelValue(props.modelValue, props.value))
        assertEquals("12px", props.space)
        assertTrue(props.focus)
        assertTrue(props.disabledKeyboard)
        assertFalse(props.disabledDot)
        assertNull(UPCodeInputProps().modelValue)
    }
}
