package net.lingyun.ultraui.android.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UPStyleTest {
    @Test
    fun resolvesSupportedSizingSpacingColorBorderAndTypographyProperties() {
        val style = UPStyle.resolve(
            input = mapOf(
                "width" to "200rpx",
                "margin" to "4px 8px",
                "paddingTop" to 12,
                "color" to "#369",
                "background" to "linear-gradient(to right, primary, success)",
                "border" to "1px solid #c0c4cc",
                "borderRadius" to "6px",
                "fontSize" to "14px",
                "fontWeight" to "600",
                "textAlign" to "center",
                "opacity" to "0.5",
                "alignItems" to "center",
                "justifyContent" to "space-between",
            ),
            availableScreenWidth = 375.dp,
        )

        assertEquals(100.dp, style.width)
        assertEquals(4.dp, style.marginTop)
        assertEquals(8.dp, style.marginStart)
        assertEquals(8.dp, style.marginEnd)
        assertEquals(4.dp, style.marginBottom)
        assertEquals(12.dp, style.paddingTop)
        assertEquals(Color(0xFF336699), style.color)
        assertEquals(UPLinearGradient(UPTheme.Primary, UPTheme.Success), style.backgroundGradient)
        assertEquals(1.dp, style.borderWidth)
        assertEquals(UPTheme.Light, style.borderColor)
        assertEquals(6.dp, style.borderRadius)
        assertEquals(14.dp, style.fontSize)
        assertEquals(FontWeight.W600, style.fontWeight)
        assertEquals(TextAlign.Center, style.textAlign)
        assertEquals(0.5f, style.opacity)
        assertEquals(UPCrossAxisAlignment.Center, style.alignItems)
        assertEquals(UPMainAxisAlignment.SpaceBetween, style.justifyContent)
    }

    @Test
    fun parsesStringStylesAndReportsOnlyUnsupportedOrMalformedDeclarations() {
        val events = mutableListOf<UPCompatibilityEvent>()
        val style = UPStyle.resolve(
            input = "margin-top: 8px; background-color: rgb(51, 102, 153); box-shadow: 0 1px",
            availableScreenWidth = 375.dp,
            diagnostics = UPCompatibilityDiagnostics { events += it },
        )

        assertEquals(8.dp, style.marginTop)
        assertEquals(Color(0xFF336699), style.backgroundColor)
        assertEquals(listOf("box-shadow"), events.map { it.property })
        assertNull(style.width)
    }
}
