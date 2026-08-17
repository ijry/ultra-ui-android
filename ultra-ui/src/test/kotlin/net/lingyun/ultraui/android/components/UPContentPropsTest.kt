package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPImageLoaders
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPContentPropsTest {
    @Test
    fun contentPropsKeepPinnedDefaultsAndGeneratedValues() {
        assertEquals("aspectFill", UPImageProps().mode)
        assertEquals("circle", UPAvatarProps().shape)
        assertTrue(UPAvatarGroupProps().showMore)
        assertEquals("", UPEmptyProps().icon)
        assertFalse(UPLoadingPageProps().loading)
        assertEquals(19, UPLoadingPageProps().iconSize)
        assertEquals("loadmore", UPLoadmoreProps().status)

        assertEquals("photo.jpg", UPImageProps(src = "photo.jpg").src)
        assertEquals("person", UPAvatarProps(name = "person").name)
        assertEquals("+9", UPAvatarGroupProps(extraValue = "+9").extraValue)
        assertEquals("empty", UPEmptyProps(mode = "empty").mode)
    }

    @Test
    fun emptyLoaderIsAvailableForDeterministicImageFallbacks() {
        assertEquals(null, kotlinx.coroutines.runBlocking { UPImageLoaders.Empty.load("ignored") })
    }
}
