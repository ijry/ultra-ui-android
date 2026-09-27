package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPImagePropsTest {
    /**
     * Asserts UPImage's defaults equal the shared contract at
     * contracts/up-image.contract.json (single source of truth).
     *
     * Per-platform override: the contract records
     * platformDefaults.showMenuByLongpress.android = false, so Android
     * intentionally deviates from the canonical `true`. This is an accepted,
     * kept-as-is deviation; the assertion below expects false.
     */
    @Test
    fun defaultsMatchContract() {
        val p = UPImageProps()
        assertEquals("", p.src)
        assertEquals("aspectFill", p.mode)
        assertEquals(300, p.width)
        assertEquals(225, p.height)
        assertEquals("square", p.shape)
        assertEquals(0, p.radius)
        assertTrue(p.lazyLoad)
        assertFalse(p.showMenuByLongpress) // android platform override (contract canonical = true)
        assertEquals("photo", p.loadingIcon)
        assertEquals("error-circle", p.errorIcon)
        assertTrue(p.showLoading)
        assertTrue(p.showError)
        assertTrue(p.fade)
        assertFalse(p.webp)
        assertEquals(500, p.duration)
        assertEquals("#f3f4f6", p.bgColor)
    }
}
