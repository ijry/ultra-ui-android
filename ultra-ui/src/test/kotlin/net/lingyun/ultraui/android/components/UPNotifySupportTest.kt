package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** `u-notify`'s icon table, its `1.3 * fontSize` glyph and the ref API it exposes. */
class UPNotifySupportTest {
    @Test
    fun onlyTheThreeStatusThemesCarryAGlyph() {
        // `icon()` leaves `primary` undefined, and the template gates on the other three.
        assertEquals("checkmark-circle", upNotifyIconName("success"))
        assertEquals("close-circle", upNotifyIconName("error"))
        assertEquals("error-circle", upNotifyIconName("warning"))
        assertNull(upNotifyIconName("primary"))
        assertNull(upNotifyIconName("anything-else"))
    }

    @Test
    fun theGlyphIsOnePointThreeTimesTheTextSize() {
        // `:size="1.3 * tmpConfig.fontSize"`; the default font size is 15.
        assertEquals(19.5f, upNotifyIconSize(UPNotifyProps().fontSize), 1e-4f)
        assertEquals(26f, upNotifyIconSize(20), 1e-4f)
        assertEquals(26f, upNotifyIconSize("20"), 1e-4f)
        // An unusable size falls back to the default before scaling.
        assertEquals(19.5f, upNotifyIconSize("large"), 1e-4f)
    }

    @Test
    fun theControllerStartsEmptyAndEachThemeShortcutSetsItsType() {
        val controller = UPNotifyController()
        assertNull(controller.current.value)

        controller.success("已保存")
        assertEquals("success", controller.current.value?.type)
        assertEquals("已保存", controller.current.value?.message)

        controller.error("失败")
        assertEquals("error", controller.current.value?.type)
        controller.warning("注意")
        assertEquals("warning", controller.current.value?.type)
        controller.primary("提示")
        assertEquals("primary", controller.current.value?.type)

        controller.close()
        assertNull(controller.current.value)
    }

    @Test
    fun oneShowNeverInheritsThePreviousCallsOverrides() {
        val controller = UPNotifyController()
        controller.show(UPNotifyProps(message = "第一条", type = "error", duration = 9_000, top = 40))
        assertEquals(9_000, controller.current.value?.duration)
        assertEquals(40, controller.current.value?.top)

        // `deepMerge(this.config, options)` starts from the defaults every time, which is
        // exactly the "避免多次调用后配置造成混乱" comment upstream.
        controller.success("第二条")
        assertEquals(UPNotifyProps().duration, controller.current.value?.duration)
        assertEquals(UPNotifyProps().top, controller.current.value?.top)
        assertEquals("第二条", controller.current.value?.message)
    }
}
