package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UPIconPropsTest {
    @Test
    fun iconPropsPreserveEveryPinnedUviewDefault() {
        val props = UPIconProps()

        assertEquals("", props.name)
        assertEquals("#606266", props.color)
        assertEquals("16px", props.size)
        assertFalse(props.bold)
        assertEquals("", props.index)
        assertEquals("", props.hoverClass)
        assertEquals("uicon", props.customPrefix)
        assertEquals("", props.label)
        assertEquals("right", props.labelPos)
        assertEquals("15px", props.labelSize)
        assertEquals("#606266", props.labelColor)
        assertEquals("3px", props.space)
        assertEquals("", props.imgMode)
        assertEquals("", props.width)
        assertEquals("", props.height)
        assertEquals(0, props.top)
        assertFalse(props.stop)
        assertEquals(emptyMap<String, UPRawValue>(), props.customStyle)
    }

    @Test
    fun iconPropsKeepJsonCompatibleRawValuesAndCustomStyle() {
        val customStyle: Map<String, UPRawValue> = mapOf("opacity" to 0.6, "padding" to "4rpx")
        val props = UPIconProps(
            name = "map",
            color = "primary",
            size = 30,
            bold = true,
            index = 7,
            hoverClass = "pressed",
            customPrefix = "custom",
            label = 0,
            labelPos = "top",
            labelSize = 12,
            labelColor = "#19be6b",
            space = "6px",
            imgMode = "aspectFill",
            width = 18,
            height = "20px",
            top = "1px",
            stop = true,
            customStyle = customStyle,
        )

        assertEquals("map", props.name)
        assertEquals(30, props.size)
        assertEquals(7, props.index)
        assertEquals(0, props.label)
        assertEquals("1px", props.top)
        assertTrue(props.stop)
        assertEquals(customStyle, props.customStyle)
    }

    @Test
    fun glyphCatalogUsesPinnedUviewNamesAndDoesNotInventUnknownGlyphs() {
        assertEquals(213, UPIconGlyphs.names.size)
        assertEquals('\ue61d', UPIconGlyphs.codePoint("map"))
        assertEquals('\ue61d', UPIconGlyphs.codePoint("uicon-map"))
        assertNull(UPIconGlyphs.codePoint("not-a-uview-glyph"))
    }
}
