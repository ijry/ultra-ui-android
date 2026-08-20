package net.lingyun.ultraui.android.core

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPConfigContractTest {
    @Test
    fun exposesThePinnedUviewPalette() {
        assertEquals(Color(0xFF2979FF), UPTheme.Primary)
        assertEquals(Color(0xFFFF9900), UPTheme.Warning)
        assertEquals(Color(0xFF19BE6B), UPTheme.Success)
        assertEquals(Color(0xFFFA3534), UPTheme.Error)
        assertEquals(Color(0xFF909399), UPTheme.Info)
        assertEquals(Color(0xFF303133), UPTheme.Main)
        assertEquals(Color(0xFF606266), UPTheme.Content)
        assertEquals(Color(0xFF909399), UPTheme.Tips)
        assertEquals(Color(0xFFC0C4CC), UPTheme.Light)
    }

    @Test
    fun exposesImmutableDefaultsForEveryFirstMilestoneComponentFamily() {
        assertEquals("info", UPConfig.button.type)
        assertFalse(UPConfig.button.loading)
        assertEquals("bottom", UPConfig.popup.mode)
        assertTrue(UPConfig.popup.closeOnClickOverlay)
        assertEquals(10090, UPConfig.toast.zIndex)
        assertEquals("primary", UPConfig.tag.type)
        assertEquals("650rpx", UPConfig.modal.width)
        assertEquals("arrow-right", UPConfig.cell.rightIcon)
        assertEquals("spinner", UPConfig.loadingIcon.mode)
        assertTrue(UPConfig.cellGroup.border)
        assertEquals("44px", UPConfig.batch9b.navbarHeight)
        assertEquals("HH:mm:ss", UPConfig.batch9b.countDownFormat)
    }
}
