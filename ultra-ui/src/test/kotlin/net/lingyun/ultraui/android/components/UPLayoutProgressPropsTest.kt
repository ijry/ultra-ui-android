package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UPLayoutProgressPropsTest {
    @Test
    fun layoutAndProgressPropsExposePinnedUviewDefaults() {
        val row = UPRowProps()
        assertEquals(0, row.gutter)
        assertEquals("start", row.justify)
        assertEquals("center", row.align)

        val col = UPColProps()
        assertEquals(12, col.span)
        assertEquals(0, col.offset)
        assertEquals("start", col.justify)
        assertEquals("stretch", col.align)
        assertEquals("left", col.textAlign)

        val grid = UPGridProps()
        assertEquals(3, grid.col)
        assertFalse(grid.border)
        assertEquals("left", grid.align)
        assertEquals("0px", grid.gap)

        val item = UPGridItemProps()
        assertEquals(null, item.name)
        assertEquals("transparent", item.bgColor)

        val line = UPLineProgressProps()
        assertEquals("#19be6b", line.activeColor)
        assertEquals("#ececec", line.inactiveColor)
        assertEquals(0, line.percentage)
        assertTrue(line.showText)
        assertEquals(12, line.height)
        assertFalse(line.fromRight)

        val circle = UPCircleProgressProps()
        assertEquals(30, circle.percentage)
    }

    @Test
    fun propsPreserveRawGeneratedValuesAndCustomStyles() {
        val style: Map<String, UPRawValue> = mapOf(
            "padding" to "4px",
            "backgroundColor" to "#ffffff",
        )
        val row = UPRowProps(gutter = "12rpx", justify = "space-evenly", customStyle = style)
        val col = UPColProps(span = "6", offset = "2", textAlign = "center", customStyle = style)
        val grid = UPGridProps(col = "4", gap = "8px", customStyle = style)
        val item = UPGridItemProps(name = 42, customStyle = style)
        val line = UPLineProgressProps(percentage = "125", height = "20px", customStyle = style)
        val circle = UPCircleProgressProps(percentage = "-20", customStyle = style)

        assertEquals("12rpx", row.gutter)
        assertEquals("6", col.span)
        assertEquals("2", col.offset)
        assertEquals("4", grid.col)
        assertEquals(42, item.name)
        assertEquals("125", line.percentage)
        assertEquals("20px", line.height)
        assertEquals("-20", circle.percentage)
        assertEquals(style, row.customStyle)
        assertEquals(style, col.customStyle)
        assertEquals(style, grid.customStyle)
        assertEquals(style, item.customStyle)
        assertEquals(style, line.customStyle)
        assertEquals(style, circle.customStyle)
    }

    @Test
    fun propsDefaultsTrackCentralConfiguration() {
        val row = UPRowProps()
        assertEquals(UPConfig.row.gutter, row.gutter)
        assertEquals(UPConfig.row.justify, row.justify)
        assertEquals(UPConfig.row.align, row.align)
        assertEquals(UPConfig.col.span, UPColProps().span)
        assertEquals(UPConfig.grid.gap, UPGridProps().gap)
        assertEquals(UPConfig.gridItem.bgColor, UPGridItemProps().bgColor)
        assertEquals(UPConfig.lineProgress.height, UPLineProgressProps().height)
        assertEquals(UPConfig.circleProgress.percentage, UPCircleProgressProps().percentage)
        assertNotNull(UPRowProps().customStyle)
    }

    @Test
    fun publicContentContractsUseGeneratedReceiverScopes() {
        val rowContent: @Composable RowScope.() -> Unit = {}
        val colContent: @Composable ColumnScope.() -> Unit = {}
        assertNotNull(rowContent)
        assertNotNull(colContent)
    }
}

@Composable
private fun compileGeneratedLayoutProgressPropsCalls(
    rowContent: @Composable RowScope.() -> Unit,
    colContent: @Composable ColumnScope.() -> Unit,
) {
    UPRow(
        props = UPRowProps(),
        modifier = Modifier,
        onClick = {},
        diagnostics = UPCompatibilityDiagnostics.None,
        content = rowContent,
    )
    UPCol(
        props = UPColProps(),
        modifier = Modifier,
        onClick = {},
        diagnostics = UPCompatibilityDiagnostics.None,
        content = colContent,
    )
    UPGrid(
        props = UPGridProps(),
        modifier = Modifier,
        diagnostics = UPCompatibilityDiagnostics.None,
        content = rowContent,
    )
    UPGridItem(
        props = UPGridItemProps(name = "item"),
        modifier = Modifier,
        onClick = {},
        diagnostics = UPCompatibilityDiagnostics.None,
        content = colContent,
    )
    UPLineProgress(
        props = UPLineProgressProps(),
        modifier = Modifier,
        diagnostics = UPCompatibilityDiagnostics.None,
    )
    UPCircleProgress(
        props = UPCircleProgressProps(),
        modifier = Modifier,
        diagnostics = UPCompatibilityDiagnostics.None,
    )
}

@Composable
private fun compileGeneratedLayoutProgressDirectCalls(
    rowContent: @Composable RowScope.() -> Unit,
    colContent: @Composable ColumnScope.() -> Unit,
) {
    UPRow(
        gutter = "8px",
        justify = "space-between",
        align = "top",
        modifier = Modifier,
        onClick = {},
        diagnostics = UPCompatibilityDiagnostics.None,
        content = rowContent,
    )
    UPCol(
        span = "6",
        offset = 1,
        justify = "center",
        align = "center",
        textAlign = "center",
        modifier = Modifier,
        onClick = {},
        diagnostics = UPCompatibilityDiagnostics.None,
        content = colContent,
    )
    UPGrid(
        col = 4,
        border = true,
        align = "center",
        gap = "4px",
        modifier = Modifier,
        diagnostics = UPCompatibilityDiagnostics.None,
        content = rowContent,
    )
    UPGridItem(
        name = "payload",
        bgColor = "#fff",
        modifier = Modifier,
        onClick = {},
        diagnostics = UPCompatibilityDiagnostics.None,
        content = colContent,
    )
    UPLineProgress(
        activeColor = "#2979ff",
        inactiveColor = "#eeeeee",
        percentage = "50",
        showText = true,
        height = "12px",
        fromRight = true,
        modifier = Modifier,
        diagnostics = UPCompatibilityDiagnostics.None,
    )
    UPCircleProgress(
        percentage = 75,
        modifier = Modifier,
        diagnostics = UPCompatibilityDiagnostics.None,
    )
}
