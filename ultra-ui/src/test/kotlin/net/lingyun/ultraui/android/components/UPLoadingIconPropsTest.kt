package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPLoadingIconPropsTest {
    @Test
    fun loadingIconPropsPreserveEveryPinnedUviewDefault() {
        val props = UPLoadingIconProps()

        assertTrue(props.show)
        assertEquals("#909399", props.color)
        assertEquals("#909399", props.textColor)
        assertFalse(props.vertical)
        assertEquals("spinner", props.mode)
        assertEquals(24, props.size)
        assertEquals(15, props.textSize)
        assertEquals("", props.text)
        assertEquals("ease-in-out", props.timingFunction)
        assertEquals(1200, props.duration)
        assertEquals("", props.inactiveColor)
        assertEquals(emptyMap<String, UPRawValue>(), props.customStyle)
    }

    @Test
    fun loadingIconPropsKeepJsonCompatibleRawValuesAndCustomStyle() {
        val customStyle: Map<String, UPRawValue> = mapOf("marginTop" to "8px", "opacity" to 0.75)
        val props = UPLoadingIconProps(
            show = false,
            color = "success",
            textColor = "#303133",
            vertical = true,
            mode = "circle",
            size = "48rpx",
            textSize = "14px",
            text = 0,
            timingFunction = "linear",
            duration = "800",
            inactiveColor = "#eeeeee",
            customStyle = customStyle,
        )

        assertFalse(props.show)
        assertEquals("48rpx", props.size)
        assertEquals(0, props.text)
        assertEquals("800", props.duration)
        assertEquals(customStyle, props.customStyle)
    }
}
