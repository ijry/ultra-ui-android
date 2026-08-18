package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPNativeInteractionPropsTest {
    @Test
    fun alertActionSheetNotifyAndBackTopExposePinnedDefaults() {
        assertEquals("warning", UPAlertProps().type)
        assertEquals("light", UPAlertProps().effect)
        assertFalse(UPAlertProps().closable)
        assertFalse(UPAlertProps().showIcon)
        assertEquals(true, UPAlertProps().modelValue)
        assertEquals(UPConfig.alert.type, UPAlertProps().type)

        assertFalse(UPActionSheetProps().show)
        assertEquals("", UPActionSheetProps().title)
        assertEquals("name", UPActionSheetProps().nameKey)
        assertEquals("subnameKey", UPActionSheetProps().subnameKey)
        assertTrue(UPActionSheetProps().closeOnClickAction)
        assertTrue(UPActionSheetProps().safeAreaInsetBottom)
        assertTrue(UPActionSheetProps().closeOnClickOverlay)
        assertEquals("600px", UPActionSheetProps().wrapMaxHeight)

        assertEquals("primary", UPNotifyProps().type)
        assertEquals("#ffffff", UPNotifyProps().color)
        assertEquals(3000, UPNotifyProps().duration)
        assertEquals(15, UPNotifyProps().fontSize)

        assertEquals("circle", UPBackTopProps().mode)
        assertEquals("arrow-upward", UPBackTopProps().icon)
        assertEquals(100, UPBackTopProps().duration)
        assertEquals(400, UPBackTopProps().top)
        assertEquals(100, UPBackTopProps().bottom)
        assertEquals(20, UPBackTopProps().right)
        assertEquals("#909399", UPBackTopProps().iconStyle["color"])
    }

    @Test
    fun cardCollapseDropdownAndNoticeExposePinnedDefaults() {
        assertFalse(UPCardProps().full)
        assertEquals("#303133", UPCardProps().titleColor)
        assertEquals("15px", UPCardProps().titleSize)
        assertEquals("#909399", UPCardProps().subTitleColor)
        assertEquals("13px", UPCardProps().subTitleSize)
        assertTrue(UPCardProps().border)
        assertEquals("15px", UPCardProps().margin)
        assertEquals("8px", UPCardProps().borderRadius)
        assertTrue(UPCardProps().headBorderBottom)
        assertTrue(UPCardProps().footBorderTop)
        assertTrue(UPCardProps().showHead)
        assertTrue(UPCardProps().showFoot)
        assertEquals("none", UPCardProps().boxShadow)

        assertEquals(null, UPCollapseProps().value)
        assertEquals(null, UPCollapseProps().modelValue)
        assertFalse(UPCollapseProps().accordion)
        assertTrue(UPCollapseProps().border)

        assertEquals("", UPCollapseItemProps().name)
        assertEquals("", UPCollapseItemProps().title)
        assertEquals("", UPCollapseItemProps().value)
        assertEquals("", UPCollapseItemProps().label)
        assertFalse(UPCollapseItemProps().disabled)
        assertTrue(UPCollapseItemProps().isLink)
        assertTrue(UPCollapseItemProps().clickable)
        assertTrue(UPCollapseItemProps().border)
        assertEquals("left", UPCollapseItemProps().align)
        assertEquals(300, UPCollapseItemProps().duration)
        assertTrue(UPCollapseItemProps().showRight)

        assertEquals("#2979ff", UPDropdownProps().activeColor)
        assertEquals("#606266", UPDropdownProps().inactiveColor)
        assertTrue(UPDropdownProps().closeOnClickMask)
        assertTrue(UPDropdownProps().closeOnClickSelf)
        assertEquals(300, UPDropdownProps().duration)
        assertEquals(40, UPDropdownProps().height)
        assertFalse(UPDropdownProps().borderBottom)
        assertEquals(14, UPDropdownProps().titleSize)
        assertEquals(0, UPDropdownProps().borderRadius)
        assertEquals("arrow-down", UPDropdownProps().menuIcon)
        assertEquals(14, UPDropdownProps().menuIconSize)

        assertEquals("", UPDropdownItemProps().modelValue)
        assertEquals("", UPDropdownItemProps().value)
        assertEquals(emptyList<Any?>(), UPDropdownItemProps().options)
        assertFalse(UPDropdownItemProps().disabled)
        assertEquals("auto", UPDropdownItemProps().height)
        assertTrue(UPDropdownItemProps().closeOnClickOverlay)

        assertEquals(emptyList<Any?>(), UPNOTICE_TEXT(UPNoticeBarProps().text))
        assertEquals("row", UPNoticeBarProps().direction)
        assertFalse(UPNoticeBarProps().step)
        assertEquals("volume", UPNoticeBarProps().icon)
        assertEquals("", UPNoticeBarProps().mode)
        assertEquals("#f9ae3d", UPNoticeBarProps().color)
        assertEquals("#fdf6ec", UPNoticeBarProps().bgColor)
        assertEquals(80, UPNoticeBarProps().speed)
        assertEquals(14, UPNoticeBarProps().fontSize)
        assertTrue(UPNoticeBarProps().disableTouch)
        assertEquals("navigateTo", UPNoticeBarProps().linkType)
        assertEquals("flex-start", UPNoticeBarProps().justifyContent)
    }

    @Test
    fun generatedRawValuesAliasesAndCustomStylesArePreserved() {
        assertEquals(false, UPAlertProps(modelValue = false, value = true).modelValue)
        assertEquals(true, UPAlertProps(modelValue = null, value = true).value)
        assertEquals("fade", UPAlertProps(transitionMode = "fade").transitionMode)
        assertEquals(
            "padding: 12px; background-color: #fff;",
            UPAlertProps(customStyle = "padding: 12px; background-color: #fff;").customStyle,
        )

        val actions = listOf(mapOf("name" to "拍照", "subname" to "camera", "disabled" to false))
        assertEquals(actions, UPActionSheetProps(actions = actions, round = "16px").actions)
        assertEquals("tap", UPActionSheetProps(openType = "tap").openType)

        assertEquals("24px", UPNotifyProps(top = "24px", fontSize = "13px").top)
        assertEquals("13px", UPNotifyProps(top = "24px", fontSize = "13px").fontSize)
        assertEquals("#111", UPBackTopProps(iconStyle = mapOf("color" to "#111")).iconStyle["color"])

        assertEquals(7, UPCardProps(index = 7).index)
        assertEquals(mapOf("padding" to "8px"), UPCardProps(bodyStyle = mapOf("padding" to "8px")).bodyStyle)
        assertEquals("16px", UPCardProps(borderRadius = "16px").borderRadius)

        assertEquals("panel-a", UPCollapseProps(modelValue = "panel-a", value = "legacy").modelValue)
        assertEquals(listOf("a", "b"), UPCollapseProps(value = listOf("a", "b")).value)
        assertEquals("left", UPCollapseItemProps(align = "left").align)
        assertEquals(mapOf("color" to "#2979ff"), UPCollapseItemProps(iconStyle = mapOf("color" to "#2979ff")).iconStyle)

        val dropdownOptions = listOf(mapOf("label" to "全部", "value" to "all"))
        assertEquals(listOf("a", "b"), UPDropdownItemProps(modelValue = listOf("a", "b"), multiple = true).modelValue)
        assertEquals(dropdownOptions, UPDropdownItemProps(options = dropdownOptions).options)

        assertEquals(listOf("通知一", "通知二"), UPNOTICE_TEXT(UPNoticeBarProps(text = listOf("通知一", "通知二")).text))
        assertEquals("closable", UPNoticeBarProps(mode = "closable").mode)
        assertEquals("https://example.com", UPNoticeBarProps(url = "https://example.com").url)
    }

    private companion object {
        @Suppress("UNCHECKED_CAST")
        fun UPNOTICE_TEXT(value: Any?): List<Any?> = when (value) {
            is List<*> -> value as List<Any?>
            else -> emptyList()
        }
    }
}
