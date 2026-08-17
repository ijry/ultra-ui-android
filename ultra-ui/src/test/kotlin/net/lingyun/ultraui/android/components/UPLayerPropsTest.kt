package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPLayerPropsTest {
    @Test
    fun layerAndContentPropsExposePinnedDefaults() {
        assertFalse(UPOverlayProps().show)
        assertEquals("bottom", UPPopupProps().mode)
        assertTrue(UPModalProps().showConfirmButton)
        assertFalse(UPModalProps().showCancelButton)
        assertEquals("center", UPToastProps().position)
        assertTrue(UPCellProps().border)
        assertFalse(UPCellProps().isLink)
        assertEquals("aspectFill", UPImageProps().mode)
        assertEquals("circle", UPAvatarProps().shape)
        assertTrue(UPAvatarGroupProps().showMore)
        assertEquals("", UPEmptyProps().icon)
        assertFalse(UPLoadingPageProps().loading)
        assertEquals(19, UPLoadingPageProps().iconSize)
        assertEquals("loadmore", UPLoadmoreProps().status)
        assertEquals(UPConfig.popup.mode, UPPopupProps().mode)
    }

    @Test
    fun rawValuesAndAliasesArePreservedForGeneratedSource() {
        assertEquals("87", UPOverlayProps(zIndex = "87").zIndex)
        assertEquals("42%", UPPopupProps(round = "42%", minHeight = "240rpx").round)
        assertEquals("value", UPToastProps(show = "value").show)
        assertEquals("next", UPCellProps(name = "next").name)
        assertEquals("src", UPImageProps(src = "src").src)
        assertEquals(listOf("a", "b"), UPAvatarGroupProps(urls = listOf("a", "b")).urls)
        assertEquals("busy", UPLoadingPageProps(loadingText = "busy").loadingText)
    }
}
